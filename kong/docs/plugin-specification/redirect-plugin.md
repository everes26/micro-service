# REDIRECT-PLUGIN仕様書
カスタムプラグインredirect-pluginの使い方

## 機能概要
- 対象の通信先に対し、リダイレクトを行う

---
## 利用するシチュエーション
- リダイレクト行いたい場合
- 認証など（認証画面へのリダイレクト（UI→BFF→CASIS）認証後のリダイレクト（CASIS→BFF→UI））

---
## 利用方法
### kong.ymlへの追加
1. 以下の例とパラメータを指定して、kong.ymlに追加する

(例)
```
- name: redirect-plugin
  config:
    redirect_url: "https://www.google.com/search"
```

|Form Parameter           | Required   | Description                                                       |
|-------------------------|------------|-------------------------------------------------------------------|
| `name`                  | *required* |`redirect-plugin`に固定                                             |
| `route`                 | *optional* |適応するルート.routeもserviceもない場合は全てが対象となる                  |
| `service`               | *optional* |適応するサービス.routeもserviceもない場合は全てが対象となる　　　　　　      |
| `redirect_url`    　　　 | *optional* |リダイレクト先のurl                                                   |

---
## 処理内容
### リクエストの変換
1. `redirect_url`に301でリダイレクトする
2. その際、クエリパラメータは全て引き継ぐ
3. なお、serviceのurlの指定は無視されるが、url形式出ないとエラーになるため注意

kong.yml
```
- name: redirect
  url: http://stg-python-service:8080/returnOK2
  routes:
    ...
```


---
## ログ一覧

|Message                                            | Log level  | Description                              |
|---------------------------------------------------|------------|------------------------------------------|


---
## Tips
### CASIS(STG)の確認
1. kong.ymlに追加

```
  # CASISログイン
  - name: redirect-to-casis
    url: https://st.id.softbank.jp/sbid_auth/type1/2.0/authorization.php
    routes:
      - name: redirectCasis
        methods:
          - GET
        paths:
          - /redirectCasis
    plugins:
      - name: redirect-plugin
        config:
          redirect_url: "https://st.id.softbank.jp/sbid_auth/type1/2.0/authorization.php"

  # CASISコールバック
  - name: callback-from-casis
    url: http://localhost
    routes:
      - name: callbackCasis
        methods:
          - GET
        paths:
          - /sbols/api/casis/callback
    plugins:
      - name: redirect-plugin
        config:
          redirect_url: "http://localhost"
```

2. クエリパラメータとともに`/redirectCasis`にブラウザでアクセス
```http://localhost:8000/redirectCasis?response_type=code&display=touch&scope=openid&ui_locales=ja&acr_value=2&nonce=20211005121813723cc2E9xzKzFgTimR&client_id=TstNZeniPtFn3JhgmnkNUK8Zpffnr6Fj&redirect_uri=http%3A%2F%2Flocalhost%3A8000%2Fsbols%2Fapi%2Fcasis%2Fcallback&prompt=login%20consent```

3. ログイン画面に遷移するので下記を入力
- 電話番号: 08030018587
- パスワード: pass08030018587

4. `http://localhost`にcodeとともにリダイレクトされる
