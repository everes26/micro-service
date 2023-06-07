# CUSTOM-RESPONSE-TRANSFORMER 仕様書

カスタムプラグイン custom-response-transformer の使い方

## 機能概要

- kong 提供の`response-transformer`プラグインではできない、テンプレート文字列の利用機能追加。
- 変更点以外の設定項目の詳細は[response-transformer](https://docs.konghq.com/hub/kong-inc/response-transformer/)を参照してください

---

## 利用するシチュエーション

- kong 標準の`response-transformer`プラグインではできない以下を行いたい場合
  - テンプレート文字列`$(...)`を使いたい

---

## 利用方法

[response-transformer](https://docs.konghq.com/hub/kong-inc/response-transformer/)を参照してください

---

## 公式プラグインからの変更概要

### テンプレート文字列対応

- テンプレート文字列`$(...)`で`kong.ctx.shared`と環境変数`env`が使用できるように変更
- `or`なども使用できる（[](https://docs.konghq.com/hub/kong-inc/request-transformer/)の`Advanced templates`と基本的に同様のため、そちらを参照）
- 環境変数`env`を設定したい場合は[環境変数の設定方法](../usage/how-to-set-env-variable.md)を参照

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

**変更箇所**

- テンプレート文字列を処理する`template_operation.lua`を追加
- `header_transformer.lua`,`body_transformer.lua`にて上記を呼び出し
- [該当コミット](http://code-dev.ark.sbb-sys.info/SBMOLS/sbm-ols-kong/-/commits/develop/plugins/response-transformer)

## ログ一覧

| Message                      | Log level | Description                                          |
| ---------------------------- | --------- | ---------------------------------------------------- |
| `Could not render: [$(...)]` | _warn_    | テンプレート`$(...)`指定でレンダリングに失敗した場合 |
