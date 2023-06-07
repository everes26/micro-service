# CUSTOM-REQUEST-TRANSFORMER 仕様書

カスタムプラグイン custom-request-transformer の使い方

## 機能概要

- kong 提供の`request-transformer`プラグインではできない、① 値が nil の変数のヘッダー挿入対応、② 環境変数対応　の機能追加。また、ログレベルを一部変更。
- 変更点以外の設定項目の詳細は[request-transformer](https://docs.konghq.com/hub/kong-inc/request-transformer/)を参照してください。

---

## 利用するシチュエーション

- kong 標準の`request-transformer`プラグインではできない以下を行いたい場合
  - 値が nil の変数をヘッダーに挿入するケースがある
  - 環境変数の値をリクエストに挿入したい

---

## 利用方法

[request-transformer](https://docs.konghq.com/hub/kong-inc/request-transformer/)を参照してください。

---

## 公式プラグインからの変更概要

### 値が nil の変数のヘッダー挿入対応

値が`nil`の変数をヘッダーに挿入すると、ヘッダー値が半角スペース（`" "`）になってしまうため対策

**変更箇所**

- `access.lua`に値が半角スペース（`" "`）のヘッダーを削除するコード追加
- [該当コミット](http://code-dev.ark.sbb-sys.info/SBMOLS/sbm-ols-kong/-/commit/86e745e8320e493770652523bc7f356fe07fd361)

### ログレベル変更

テンプレート文字列をレンダリングする際のエラーレベルを変更

**変更箇所**

- `access.lua`で`return error(...)`を`kong.log.debug(...)`に変更
- [該当コミット](http://code-dev.ark.sbb-sys.info/SBMOLS/sbm-ols-kong/-/commit/cfa96d4142e4e60db95926110291a4a36c26e8fb)

### 環境変数対応

テンプレート文字列内で環境変数も使えるように修正  
環境変数を設定したい場合は[環境変数の設定方法](../usage/how-to-set-env-variable.md)を参照

**変更箇所**

- `access.lua`の`__meta_environment`に`env`追加
- [該当コミット](http://code-dev.ark.sbb-sys.info/SBMOLS/sbm-ols-kong/-/commit/e259318ff268ac85c4623a6c4dabfad1713bdbc7)

## ログ一覧

| Message                      | Log level | Description                                          |
| ---------------------------- | --------- | ---------------------------------------------------- |
| `Could not render: [$(...)]` | _warn_    | テンプレート`$(...)`指定でレンダリングに失敗した場合 |
