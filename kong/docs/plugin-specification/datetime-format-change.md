# REGEX-CHANGE仕様書
カスタムプラグインdatetime-format-changeの使い方

## 機能概要
- 対象のパラメータに対し、時刻フォーマットの置換を行う

---
## 利用するシチュエーション
- リクエスト時にパラメータに含まれる時刻（UNIX時間）を特定のフォーマットに変換したい場合

---
## 利用方法
### リクエストボディ内の時刻のフォーマットを変更する場合
1. 以下のリクエストにおいて時刻フォーマットを変更したい場合
- リクエスト時
  - `nowDatetime`を`yyyymmdd`形式に変更
  - `serviceFlagTypeList[*].operationTime`をRFC 3339（ISO 8601）形式 `yyyy-MM-ddTHH:mm:ssZ`に変更

(例)
requestボディ
```
{
  "nowDatetime": "1634716720",
  "serviceFlagTypeList": [
    {
      "itemCode": "APSJFB",
      "serviceFlagTypeCode": "APSJFB",
      "serviceFlagTypeName": "APSJFB",
      "serviceFlagValue": "APSJFB",
      "operationTime": "1634716720"
    }
  ]
}
```

2. kong.ymlで以下のように指定

```
plugins:
- name: datetime-format-change
  config:
    req_targets: 
      - key: "nowDatetime"
        format: "%Y%m%d"
      - key: "serviceFlagTypeList[*].operationTime"
        format: "%Y-%m-%dT%TZ"
        timezone: UTC
```

3. 以下のようにリクエストが変換される

requestボディ
```
{
  "nowDatetime": "20211020",
  "serviceFlagTypeList": [
    {
        
        "itemCode": "APSJFB",
        "serviceFlagTypeCode": "APSJFB",
        "serviceFlagTypeName": "APSJFB",
        "serviceFlagValue": "APSJFB"
        "operationTime": "2021-10-20T07:58:40Z",
    }
  ]
}
```

### ヘッダー、グローバル変数、クエリ文字列の時刻のフォーマットを変更する場合
1. 指定可能なパラメータ
- 文字列・数値以外に以下の変数を指定し、変換できる

| Request Param      | Template
| ------------------ | -----------
| header             | `$(headers.<name>)`
| querystring        | `$(query_params.<name>)`
| kong.ctx.shared    | `$(shared.<name>)`


2. kong.ymlで以下のように指定
- グローバル変数`shared`の`unixtime`、ヘッダーの`unixtime`、クエリ文字列の`unixtime`のフォーマットを変換したい場合

(例)
```
plugins:
- name: datetime-format-change
  config:
    req_targets: 
      - key: "$(shared.unixtime)"
        format: "%Y-%m-%dT%TZ"
        timezone: UTC
      - key: "$(headers.unixtime)"
        format: "%Y%m%d"
        timezone: UTC
      - key: "$(query_params.unixtime)"
        format: "%Y-%m-%dT%T+09:00"
        timezone: JST
```


|Form Parameter           | Required   | Description                                                       |
|-------------------------|------------|-------------------------------------------------------------------|
| `name`                  | *required* |`datetime-format-change`に固定                                      |
| `route`                 | *optional* |適応するルート.routeもserviceもない場合は全てが対象となる                  |
| `service`               | *optional* |適応するサービス.routeもserviceもない場合は全てが対象となる　　　　　　      |
| `req_change_targets`    | *optional* |リクエスト時に変換するパラメータの情報のリスト                             |
| `key`                   | *optional* |変換する時刻の識別子（指定方法は後述）。`UNIX時間`を指定する                 |
| `format`　　　           | *optional* |時刻変換のフォーマット　　　　　　　　　　　　　　　　　　　　　　　　　　　　　 |
| `timezone`　　　         | *optional* |時刻変換時のtimezone. `JST`か`UTC`を指定可能. デフォルトは`JST`　　　　　　 |
| `if_key_not_exist`　　　 | *optional* |keyがnilの場合の挙動. `None`(何もしない)か`Current`(現在時刻)を指定可能. デフォルトは`None`|


### リクエストボディのkeyの指定方法
- JsonPathの書き方に則っており、[lua-jsonpath](https://github.com/hy05190134/lua-jsonpath)を参照

JsonPath            | Description
--------------------|------------
`$`                 | The root object/element
`@`                 | The current object/element
`.`                 | Child member operator
`..`                | Recursive descendant operator; JsonPath borrows this syntax from E4X
`*`                 | Wildcard matching all objects/elements regardless their names
`[]`                | Subscript operator
`[,]`               | Union operator for alternate names or array indices as a set
`[start:end:step]`  | Array slice operator borrowed from ES4 / Python
`?()`               | Applies a filter (script) expression via static evaluation
`()`                | Script expression via static evaluation


### 変換対象の時刻の指定方法
- `UNIX時間`を指定する。テスト等で現在時刻から生成する場合は[変換サイト](https://keisan.casio.jp/exec/system/1526003938)を利用してください


### 時間フォーマットの指定方法
- 代表的なフォーマットは下記の表を参照してください。詳細は[こちら](https://www.lua.org/pil/22.1.html)を参考にしてください。

(例)
- `mm/dd/yyyy` -> `%m/%d/%Y`
- `yyyymmdd` -> `%Y%m%d`
- `yyyy-MM-ddTHH:mm:ssZ` -> `%Y-%m-%dT%TZ`

Charactor   | Description
------------|------------
`%d`	      | day of the month [01-31]
`%H`	      | hour, using a 24-hour clock [00-23]
`%M`	      | minute [00-59]
`%m`	      | month [01-12]
`%S`	      | second [00-61]
`%x`	      | date (e.g., 09/16/98)
`%X`	      | time (e.g., 23:48:10)
`%T`	      | time (e.g., 23:48:10)
`%Y`	      | full year (1998)
`%%`	      | the character `%´

---
## 処理内容
### リクエストの変換
1. `req_change_targets`の各要素に関して以下を実施
    - `target`がbodyにあるかを確認
    - ない場合、ログに`No items match the following key: [指定したkey]`が出力され、次に進む。
    - 存在したら、`format`で変換。
    - 指定されたkeyのvalueに数字以外が指定されているなど変換できない場合は、ログに`Invalid value in specified key: [description]`が出力され、変換しない。

---
## ログ一覧

|Message                                            | Log level  | Description                                              |
|---------------------------------------------------|------------|----------------------------------------------------------|
| `No items match the following key: [key]`         | *info*     |指定したkeyに値が存在しなかった場合　　　　　　　 　　　　　　　　　 |
| `Invalid value in specified key: [description]`   | *info*     |指定されたkeyのvalueに数字以外が指定されているなど変換できない場合　|
| `Invalid value in specified template literal`     | *info*     |指定されたテンプレート文字列から変換ができなかった場合             |
| `Could not render: [$(...)]`                      | *warn*     |テンプレート`$(...)`指定でレンダリングに失敗した場合             |