# NESTBODY-TRANSFORMER仕様書
カスタムプラグインNESTBODY-TRANSFORMERの使い方

## 機能概要
- kong標準のrequest-transformerでは行えない、ネスト構造のボディに値を挿入,変更する

---
## 利用するシチュエーション
- jwtプラグイン,Custom-jwtプラグインで検証したjwtをネスト構造のBODYにマッピングしたい場合
- jwtのclaimに含まれるMSNやS#をリクエストにマッピングするなど

---
## 利用方法
### kong.ymlへの追加
1. 以下の例とパラメータを指定して、kong.ymlに追加する

(例)
```
plugins:
- name: nestbody-transformer
  config: 
    add:
      - MP_PR202_inputParam.billGroupId:$(shared.jwt-header-Authorization-billGroupId)
```

|Form Parameter           | Required   | Description                                                       |
|-------------------------|------------|-------------------------------------------------------------------|
| `name`                  | *required* |`nestbody-transformer`に固定                                             |
| `route`                 | *optional* |適応するルート.routeもserviceもない場合は全てが対象となる　　　　　　　　　　 |
| `service`               | *optional* |適応するサービス.routeもserviceもない場合は全てが対象となる　　　　　　      |
| `add`                   | *required* |追加対象を指定する.[body-place]:[claim-name] 　　　　　　　　　　　　 　　 |
| `replace`               | *required* |変更対象を指定する.[body-place]:[claim-name] 　　　　　　　　　　　　 　　 |


### 対象の指定方法
### replaceの場合
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

### addの場合
- JsonPathの子要素・配列の表記のみ対応

JsonPath            | Description
--------------------|------------
`.`                 | Child member operator
`[*]`               | Wildcard matching all elements of array regardless their names
`[num]`             | Subscript operator


### 指定可能なパラメータ
- 文字列・数値以外に以下の変数を指定できる
- 環境変数を設定したい場合は[環境変数の設定方法](../usage/how-to-set-env-variable.md)を参照

| Request Param      | Template
| ------------------ | -----------
| header             | `$(headers.<name>)`
| querystring        | `$(query_params.<name>)`
| captured URIs      | `$(uri_captures.<name>)`
| kong.ctx.shared    | `$(shared.<name>)`
| env variable       | `$(env.<name>)`
| request body       | `$(body.<name>)`

(例)
```
add:
  - MP_PR202_inputParam[*].billGroupId:$(shared.jwt-header-Authorization-billGroupId)
```


### 複雑な指定方法
- `or`なども使用できる（[request-transformer](https://docs.konghq.com/hub/kong-inc/request-transformer/)の`Advanced templates`と基本的に同様のため、そちらを参照）

(例)
```
add:
  - MP_PR202_inputParam[*].billGroupId:$(shared.system_id or env.SYSTEM_ID)
```

- 文字列を結合するときは`$()`内であれば可能(`Authorization:Basic $(query_params["auth"])`はできない)

(例)
```
add:
  - Authorization:$("Basic " .. query_params["auth"])
```

---
## 処理内容
### リクエストにマッピングする
1. `add`の情報に従い、ネストされたボデイにマッピングする

### リクエストにパラメータを置換する
1. `replace`の情報に従い、ネストされたボディのパラメータを置換する

---
## ログ一覧

|Message                                            | Log level  | Description                                 |
|---------------------------------------------------|------------|---------------------------------------------|
| `No items match the following key: [key]`         | *info*     |指定したkeyに値が存在しなかった場合               |
| `invalid list number specified in [key]`          | *info*     |keyの内の配列の指定で`*`と数字以外を指定した場合    |
| `Could not render: [$(...)]`                      | *warn*     |テンプレート`$(...)`指定でレンダリングに失敗した場合|