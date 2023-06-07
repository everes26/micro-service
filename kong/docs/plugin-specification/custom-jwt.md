# CUSTOM-JWT仕様書
カスタムプラグインcustom-jwtの使い方

## 機能概要
- kong提供のJWTプラグインではできない、JWTが付与されていた時のみ検証、検証済みpayloadを変数に格納する機能を提供

---
## 利用するシチュエーション
- kong標準のJWTプラグインではできない以下を行いたい場合
  - 常にJWT検証するのではなく、付与されていた時のみ検証したい
  - JWT検証後のpayloadを変数に格納したい

---
## 利用方法
### kong.ymlへの追加
1. 以下の例とパラメータを指定して、kong.ymlに追加する

(例)
```
- name: custom-jwt
  config: 
    secret_is_base64: false
    key_claim_name: iss
    key_name: user
    claims_to_verify: 
      - exp
    cookie_names: 
      - token
    verification: IfExist
    claim_identify_prefix: user_claim_
```

2. Consumer,Secretを追加する
- kongでのconsumerはAPIを使用する個人を指し、トラッキングやアクセス管理などに使用することができる。

(例)
```
consumers:
  - username: user
    custom_id: userid

jwt_secrets:
  - consumer: admin
    key: JWT_KEY
    secret: JWT_SECRET
    
```

### 複数のJWTを扱いたい場合
1. Dockerfileにてcustom-jwtを別名でコンテナにコピーする

(例)　
Dockerfile

```
COPY ./plugins/custom-jwt /usr/local/share/lua/5.1/kong/plugins/custom-jwt
COPY ./plugins/custom-jwt /usr/local/share/lua/5.1/kong/plugins/custom-jwt2
```

2. コンテナの環境変数に追加する

(例) 
docker-compose.yml（ローカルの場合）

```
KONG_PLUGINS=bundled,insert-nestbody,custom-jwt,custom-jwt2,redirect-plugin,regex-change
```

3. 別名(この場合はcustom-jwt2)でpluginを追加する

(例)
```
- name: custom-jwt2
  config: 
    secret_is_base64: false
    ...
```


|Form Parameter           | Required   | Description                                                       |
|-------------------------|------------|-------------------------------------------------------------------|
| `name`                  | *required* |コンテナにコピーした時のフォルダ名を指定する(`custom-jwt2`など)            |
| `route`                 | *optional* |適応するルート.routeもserviceもない場合は全てが対象となる　　　　　　　　　　 |
| `service`               | *optional* |適応するサービス.routeもserviceもない場合は全てが対象となる　　　　　　      |
| `secret_is_base64`      | *required* |secretをbase64エンコードするか.defaultは`false`                        |
| `key_claim_name`        | *optional* |keyを入れるclaimの指定.defaultは`iss`　　　　　　　　　　                |
| `key_name`              | *required* |secretのkey指定.`jwt_secrets`のkeyを指定する　   　　　                |
| `claims_to_verify`      | *optional* |検証するclaimの指定.`exp`と`nbf`を検証可能                             |
| `cookie_names`          | *required* |tokenの場所の名前　　　　　　　　　　　                                  |
| `uri_param_names`       | *required* |tokenの場所の名前　　　　　　　　　　　                                  |
| `header_names`          | *required* |tokenの場所の名前                                                    |
| `verification`          | *optional* |常に検証`Always`, あれば検証`IfExist`。デフォルトは`Always`　　       　 |
| `claim_identify_prefix` | *optional* |kong.ctx.sharedに格納する時の変数のprefix。デフォルトは`""`              |

その他パラメータは[jwtプラグイン](https://docs.konghq.com/hub/kong-inc/jwt/)を参照

---
## 処理内容（kong標準JWTプラグインからの変更部分のみ）
### jwtの検証
1. `verification`の値を確認する。
2. `IfExist`の場合、指定されたkeyがあれば検証を行う。なければ処理を終了する。
3. `Always`の場合、常にtokenの検証を行う。

### Payloadの変数格納
1. 検証が済んだ後、プラグイン間で共有可能な`kong.ctx.shared`に格納する
2. その際、`claim_identify_prefix`で指定したprefixをつける。指定がない場合は何もつけない。

### 格納された変数のスコープ
1. [kong.ctx.shared](https://docs.konghq.com/gateway-oss/2.5.x/pdk/kong.ctx/)を参照
2. スコープはリクエストで、特定のリクエストでプラグイン間で共有される

---
## 格納した変数の使い方

(例)`claim_identify_prefix`で`user_claim_`を指定して以下のJWTのPAYLOADを送った場合

```
{
  "sub": "1234567890",
  "name": "John Doe",
  "iat": 1516239022,
  "iss": "admin" 
}
```

### ヘッダー、クッキーにセットしたい場合
1. [kong標準のrequest-transformerプラグイン](https://docs.konghq.com/hub/kong-inc/request-transformer/)を使用する

(例) ヘッダーの`name`に`user_claim_name`を挿入する
(`claim_identify_prefix`で`user_claim_`を指定したjwtの内の`name`を挿入する)

```
plugins:
  - name: request-transformer
    config: 
      add:
        headers:
        - name:$(shared.user_claim_name)
```

### ボディにセットしたい場合
1. `nestbody-transformerプラグイン`を使用する

(例) ボディの`info[*].place.test`に`user_claim_name`を挿入する
(`claim_identify_prefix`で`user_claim_`を指定したjwtの内の`name`を挿入する)

```
plugins:
  - name: nestbody-transformer
    config: 
      add:
        - info[*].place.test:$(shared.user_claim_name)
```

### 前処理、後処理で使用する場合
1. [kong標準のserverlessプラグイン](https://docs.konghq.com/hub/kong-inc/serverless-functions/)を使用する
2. `kong.ctx.shared.・・・`で指定する

(例) ログに`user_claim_name`を出力する
(`claim_identify_prefix`で`user_claim_`を指定したjwtの内の`name`を出力する)

```
plugins:
  - name: post-function
    config: 
      access:
        - |2
          kong.log.debug(kong.ctx.shared.user_claim_name)
```


---
## ログ一覧

|Message                                            | Log level  | Description                                |
|---------------------------------------------------|------------|--------------------------------------------|
|`Something wrong in re_gmatch. Description: ~`    | *info*     |Bearer...のパターンマッチ時にエラーが発生した場合　|
|`Something wrong in iterator. Description:  ~`    | *info*     |ヘッダーのイテレートでエラーが発生した場合         |
