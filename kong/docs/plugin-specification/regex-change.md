# REGEX-CHANGE 仕様書

カスタムプラグイン regex-change の使い方

## 機能概要

- 対象のパラメータに対し、正規表現での置換を行う

---

## 利用するシチュエーション

- リクエスト・レスポンス時にパラメータの特定の文字を置換したい場合
- 商品コードの変換など（リクエスト時：APSJFB→APAJFB、レスポンス時：APAJFB→APSJFB）

---

## 利用方法

### kong.yml への追加

1. 以下のリクエストにおいて正規表現に従い変更したい場合

- リクエスト時
  - `itemCodeList`を SJ -> MJ
  - `serviceFlagTypeList[*].itemCode`の三文字目を S -> A
- レスポンス時
  - `itemFlagTypeList[*].itemCode`の三文字目を A -> S

(例)
request ボディ

```
{
  "itemCodeList": [
      "SJAAA1",
      "SJAAB1",
      "SJAAB2"
  ]
  "serviceFlagTypeList": [
    {
      "itemCode": "APSJFB",
      "serviceFlagTypeCode": "APSJFB",
      "serviceFlagTypeName": "APSJFB",
      "serviceFlagValue": "APSJFB"
    }
  ]
}
```

response ボディ

```
{
  "itemFlagTypeList": [
    {
      "itemCode": "APAJFB",
      "itemFlagTypeCode": "APAJFB",
      "itemFlagTypeName": "APAJFB",
      "itemFlagValue": "APAJFB"
    }
  ]
}
```

2. kong.yml で以下のように指定

```
plugins:
- name: regex-change
  config:
    req_targets:
      - key: "itemCodeList"
        regex: "SJ(....)"
        repl: "MJ%1"
      - key: "serviceFlagTypeList[*].itemCode"
        regex: "(..)S(...)"
        repl: "%1A%2"
    res_targets:
      - key: "itemFlagTypeList[*].itemCode"
        regex: "(..)A(...)"
        repl: "%1S%2"
```

| Form Parameter       | Required   | Description                                                                      |
| -------------------- | ---------- | -------------------------------------------------------------------------------- |
| `name`               | _required_ | `regex-change`に固定                                                             |
| `route`              | _optional_ | 適応するルート.route も service もない場合は全てが対象となる                     |
| `service`            | _optional_ | 適応するサービス.route も service もない場合は全てが対象となる　　　　　　       |
| `req_change_targets` | _optional_ | リクエスト時に変換するパラメータの情報のリスト                                   |
| `res_change_targets` | _optional_ | レスポンス時に変換するパラメータの情報のリスト                                   |
| `key`                | _optional_ | 変換するパラメータの識別子（指定方法は後述） 　 　　　　　                       |
| `regex`　　　        | _optional_ | 検索する正規表現　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　 |
| `repl`　　　         | _optional_ | 置換する文字列　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　 |

### key の指定方法

- JsonPath の書き方に則っており、[lua-jsonpath](https://github.com/hy05190134/lua-jsonpath)を参照

| JsonPath           | Description                                                          |
| ------------------ | -------------------------------------------------------------------- |
| `$`                | The root object/element                                              |
| `@`                | The current object/element                                           |
| `.`                | Child member operator                                                |
| `..`               | Recursive descendant operator; JsonPath borrows this syntax from E4X |
| `*`                | Wildcard matching all objects/elements regardless their names        |
| `[]`               | Subscript operator                                                   |
| `[,]`              | Union operator for alternate names or array indices as a set         |
| `[start:end:step]` | Array slice operator borrowed from ES4 / Python                      |
| `?()`              | Applies a filter (script) expression via static evaluation           |
| `()`               | Script expression via static evaluation                              |

### Lua での正規表現について

- LUA の正規表現は他のメジャーな言語とは記述方法が異なるため注意が必要
  - LUA では、`%`でエスケープ
  - `%`を文字として扱う場合は`%%`とする
  - `+`は 1 回以上、`*`は 0 回以上の最長マッチ、`-`は 0 回以上の最短マッチ
  - [その他はこちらを参照](https://xn--pckzexbx21r8q9b.net/lua_tips/?lua_reference_lib_regex)

---

## 処理内容

### リクエストの変換

1. `req_change_targets`の各要素に関して以下を実施
   - `target`が body にあるかを確認
   - ない場合、ログに`No items match the following key: [指定したkey]`が出力され、次に進む。
   - 存在したら、`regex`と`repl`で置換。パターンマッチしない場合は置換されない

### レスポンスの変換

1. `res_change_targets`の各要素に関して以下を実施
   - `target`が body にあるかを確認
   - ない場合、ログに`No items match the following key: [指定したkey]`が出力され、次に進む。
   - 存在したら、`regex`と`repl`で置換。パターンマッチしない場合は置換されない

---

## ログ一覧

| Message                                   | Log level | Description                                         |
| ----------------------------------------- | --------- | --------------------------------------------------- |
| `No items match the following key: [key]` | _debug_   | 指定した key に値が存在しなかった場合　　　　　　　 |
