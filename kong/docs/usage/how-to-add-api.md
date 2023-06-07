# KongへのAPI追加・各種設定

## KongへのAPI追加
__KongへのAPIの追加は設定ファイルに定義することで可能。ファイルは分割して管理しデプロイ時にマージしている__
1. kong_configフォルダにymlファイルを用意する 
2. [kong公式サイト](https://docs.konghq.com/gateway-oss/2.5.x/db-less-and-declarative-config/#the-declarative-configuration-format)を参考に記述する
    - `_format_version: "1.1"` : *必須*
    - `services.name` : 名前
    - `services.url` : バックエンドのURL
    - `services.plugins` : 有効化するプラグインの指定。設定項目は各プラグインの詳細を参照（kong標準であれば[plugin hub](https://docs.konghq.com/hub/)を参照、カスタムプラグインは[カスタムプラグイン仕様書](../plugin-specification)を参照）
    - `services.routes` : KongでのパスとHTTPメソッドの指定。それぞれ複数指定可能。
3. プラグインの定義の場所は**設定範囲に応じて記述**する
    - 全APIの場合 : `global.yml`に追記する
    - 特定のバックエンドの場合 : 各設定ファイルの`services.plugins`に記述する
    - 特定のパスの場合 : 各設定ファイルの`services.routes.plugins`に記述する

(例)
```
_format_version: "1.1"

services:
  - name: jwt-test
    url: http://testapp:8080/returnOK
    plugins:
      - name: custom-jwt-user
        config: 
          secret_is_base64: false
          key_claim_name: iss
          key_name: ${USER_JWT_KEY}
          claims_to_verify: 
            - exp
          header_names: 
            - user-authorization
          claim_identify_prefix: user_claim_
    routes:
    - name: jwt-test
      methods:
        - GET
      paths:
        - /jwt-test
```

## 各種設定
### プラグイン設定
以下を参照し、必要に応じてプラグインを有効化してください。
- [KONG標準プラグインの設定](./how-to-configure-official-plugin.md)

### 環境ごとの変数設定
環境変数としてコンテナに持たせ、デプロイ時に`enbsubst`コマンドでファイルに埋め込むことで、環境ごとの変数設定を行なっている。

1. 環境変数の定義

    **sdi3.0環境の場合**
    - 秘匿情報以外
        - k8sのconfigmapとして保存し、デプロイ時に環境変数化
        - [mobile-api-deployプロジェクト](http://code-dev.ark.sbb-sys.info/cml/mobile-api-deploy)のsdi3.0/k8s-sbm-ols-kong/overlays/dev/[環境]/config.fileに記載してください。

        (例)

        ```
        TEST_URL=http://example.com
        ```

    - 秘匿情報
        - k8sのsecretとして保存し、デプロイ時に環境変数化
        - [mobile-api-deployプロジェクト](http://code-dev.ark.sbb-sys.info/cml/mobile-api-deploy)のsdi3.0/k8s-sbm-ols-kong/overlays/dev/[環境]/secret.fileに記載してください。

        (例)

        ```
        ADMIN_SECRET=secret
        ```

    **ローカルの場合**
    - docker-compose.ymlに定義する

        ```
        environment:
        - ADMIN_JWT_KEY=admin
        - ADMIN_JWT_SECRET=admin_secret
        - USER_JWT_KEY=user
        - USER_JWT_SECRET=user_secret
        ```


2. kong設定ファイル
    - 以下のように変数を`${....}`に入れ、ファイル内で使用する

    (例)

    ```
    jwt_secrets:
    - consumer: user
    key: ${USER_JWT_KEY}
    secret: ${USER_JWT_SECRET}
    ```

### 対向先タイムアウトの設定

#### 設定方法

services.[*].read_timeoutの値を修正することで対向先への通信のタイムアウトを設定することが可能 

```yml
- name: get-business-date
  url: http://10.229.24.71:9090/DateService/S3/GetToday
  routes:
    - name: getBusinessDate
      methods:
        - GET
      paths:
        - /getBusinessDate
  read_timeout: 300000  # ms
```

#### 全service修正
 
全てのserviceに適用したい場合、docker-composeで一括挿入する  
**個別設定は上書きされてしまうので注意!!**  

以下の環境変数を修正する

(例) docker-compose.yml

```yaml
environment:
  - ...
  - UPSTREAM_READ_TIMEOUT=100000
```

以下のコマンドで挿入

```yaml
command: >
  ...
  yq eval -i '.services[].read_timeout += env(UPSTREAM_READ_TIMEOUT)' /usr/local/kong/decrelative/kong.yml &&
  ...
```