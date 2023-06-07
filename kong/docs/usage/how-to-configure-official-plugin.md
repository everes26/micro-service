# KONG 公式プラグインの設定方法

以下の設定が必要な場合はプラグインを有効化する

1. 流量制限
2. リクエストサイズ制限
3. レスポンスキャッシュ
4. バージョニング
5. ID の付与
6. in/out マッピング
7. 監視設定
8. 前処理・後処理
9. API 仕様の表示の設定

### 1.流量制御

1. 設定ファイルの plugins に`rate-limiting`を追加
2. 設定項目の詳細は[rate-limiting](https://docs.konghq.com/hub/kong-inc/rate-limiting/)を参照
3. レスポンス
   - HTTPcode 429 で`API rate limit exceeded`が返却される
   - 挙動の変更不可（拒否することのみ）
   - 返却コード変更不可
   - コネクションでの流量制限なし

(例)

```
plugins:
- name: rate-limiting
  config:
    second: 5
    hour: 10000
    policy: local
```

### 2.リクエストサイズ制限

1. 設定ファイルの plugins に`request-size-limiting`を追加
2. 設定項目の詳細は[request-size-limiting](https://docs.konghq.com/hub/kong-inc/request-size-limiting/)を参照
3. レスポンス
   - HTTPcode 417 で`Request size limit exceeded`が返却される
   - 挙動の変更不可（拒否することのみ）
   - 返却コード変更不可

(例)

```
plugins:
- name: request-size-limiting
  config:
    allowed_payload_size: 128
    size_unit: megabytes
    require_content_length: false
```

### 3.レスポンスキャッシュ

1. 設定ファイルの plugins に`proxy-cache`を追加
2. 設定項目の詳細は[proxy-cache](https://docs.konghq.com/hub/kong-inc/proxy-cache/)を参照
   - `content_type` : ヘッダの content_type と完全一致の必要あり。
   - `strategy` : キャッシュの保存先,無料版ではメモリのみ。
   - `vary_headers` : キャッシュキーに考慮されるヘッダー
   - `vary_query_params` : キャッシュキーに考慮されるクエリパラメータ
3. キャッシュの挙動
   - 複数ポッドの場合、各ポッドへの最初のアクセス以降キャッシュされる。ポッド間では共有されない。

(例)

```
plugins:
- name: proxy-cache
　config:
    response_code:
    - 200
    request_method:
    - GET
    content_type:
    - application/json; charset=UTF-8
    cache_ttl: 300
    strategy: memory
```

### 4.バージョニング

1. [バージョニング](https://konghq.com/blog/service-design-guidelines-api-versioning/)を参照

2. route の paths を追加する

(例)

```
routes:
- name: test-api
    methods:
    - GET
    paths:
    - /returnOK
    - /v1.1/returnOK
```

### 5.ID の付与

1. 設定ファイルの plugins に`correlation-id`を追加
2. 設定項目の詳細は[Correlation-id](https://docs.konghq.com/hub/kong-inc/correlation-id/)を参照
3. 各設定項目について
   - header_name : ヘッダ名
   - generator : id の生成方法の指定。以下を指定できる。
     - uuid : リクエストごとに生成。
     - uuid#counter : 初めに作成された uuid をインクリメントする。処理が早い。
     - tracker : ip や pid を用いて作成する。
   - echo_downstream : リクエスト元に id を返すかどうか

(例)

```
- name: correlation-id
  config:
    header_name: Kong-Correlation-ID
    generator: uuid
    echo_downstream: true
```

### 6.in/out マッピング

1. 設定ファイルの plugins に`request-transformer`または`response-transformer`を追加
   - `request-transformer` : リクエストのマッピング
   - `response-transformer` : レスポンスのマッピング
2. 設定項目の詳細は[request-transformer](https://docs.konghq.com/hub/kong-inc/request-transformer/),[response-transformer](https://docs.konghq.com/hub/kong-inc/response-transformer/)を参照。
   - `request-transformer`の場合は追加`add`・置換`replace`・削除`remove`・名前変更`rename`が可能
   - `request-transformer`の場合はヘッダやクエリ文字列からもマッピングできる
     - header を利用する : `$(headers.<header_name>)`
     - クエリ文字列を利用する : `$(query_params.<query-param-name>)`
   - `response-transformer`の場合は追加（すでにあれば何もしない）`add`・追加（すでにあっても追加）`append`・削除`remove`・置換`replace`が可能
3. **!!注意!!**
   - `request-transformer`・`response-transformer`ともにネスト構造のボディにはマッピングできない。リクエスト時にネスト構造のボディにマッピングする際は[nestbody-transformer](../plugin-specification/nestbody-transformer.md)を利用してください。

(例)リクエストボディの code にクエリパラメータの code をマッピングする

```
- name: request-transformer
  config:
    add:
        body:
        - code:$(query_params.code)
```

(例)レスポンスボディの`demo`ヘッダーに`injected-by-kong`という文字列をマッピングする

```
- name: response-transformer #backendからresponseの変換
  config:
    add:
      headers:
      - demo:injected-by-kong
```

4. **!!一部仕様変更あり!!**

kong 標準の`request-transformer`と`response-transformer`を機能拡張したカスタムプラグインも用意している。詳しくは以下を参照。

- [custom-request-transformer](../plugin-specification/custom-request-transformer.md)を参照
- [custom-response-transformer](../plugin-specification/custom-response-transformer.md)を参照

### 7. 監視設定

[datadog](./how-to-configure-datadog.md)を参照

### 8. 前処理・後処理

1. 設定ファイルの plugins に`pre-function`または`post-function`を追加
   - `pre-function` : 他のプラグインよりも**前**に実行される
   - `post-function` : 他のプラグインより**後**に実行される
2. [各コンテキスト](https://docs.konghq.com/gateway-oss/2.5.x/plugin-development/custom-logic/#available-contexts)ごとに設定可能。代表的なものは以下。

   - `access` : バックエンドへのアクセスフェーズ。**前処理**となる。
   - `header_filter` : レスポンスのヘッダー解析フェーズ。**後処理**となる。
   - `config.body_filter` : レスポンスのボディ解析フェーズ。**後処理**となる。
   - `config.log` : ログフェーズ。**後処理**となる。

3. 設定項目の詳細は[serverless-functions](https://docs.konghq.com/hub/kong-inc/serverless-functions/)を参照)

(例)

```
- name: post-function
  config:
    access:
    - |
        local cjson = require "cjson.safe"
        local http = require "socket.http"
        kong.service.request.enable_buffering()
        local url = "http://host.docker.internal:30080/returnDate"
        local body, code = http.request(url)
        local tableBody = cjson.decode(body)
        local request_body = kong.request.get_body("application/json")
        request_body["date"] = tableBody["date"]
        kong.service.request.set_body(request_body,"application/json")
    header_filter:
    - |
        local cjson = require "cjson.safe"
        local body = kong.service.response.get_raw_body()
        local tableBody = cjson.decode(body)
        kong.response.set_header("Set-Cookie","date="..tableBody["date"].."; Max-Age=120")
    body_filter:
    - |
        kong.log('body_filter!!')
    log:
    - |
        kong.log('log fase!!')
```

### 9. API 仕様の表示の設定

1. dockerfile に下記を追加

```
RUN luarocks install kong-spec-expose
```

2. mobile-api-deploy プロジェクトの sdi3.0/k8s-sbm-ols-kong/base/kong_deployment.yml に下記を追加

```
- name: KONG_PLUGINS
  value: "bundled,kong-spec-expose, ....."
```

3. 設定ファイルの plugins に`kong-spec-expose`を追加。設定項目の詳細は[Correlation-id](https://docs.konghq.com/hub/optum/kong-spec-expose/)を参照

4. [kong の url]/specz にアクセスするとリダイレクトされる。`localhost:30080/returnOK/specz`などとしてアクセス

(例)

```
- name: kong-spec-expose
  config:
    spec_url: https://github.com/OAI/OpenAPI-Specification/blob/master/examples/v2.0/json/petstore.json
```
