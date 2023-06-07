# SAVE-BUSINESS-DATE仕様書
カスタムプラグインSAVE-BUSINESS-DATEの使い方

## 機能概要
- 業務日付APIから日付を取得し、kong.ctx.sharedに格納する

---
## 利用するシチュエーション
- 業務日付をリクエストスコープに格納したい場合

---
## 利用方法
### kong.ymlへの追加
1. 以下の例とパラメータを指定して、kong.ymlに追加する

(例)
```
plugins:
- name: save-business-date
  config:
    target_url: http://10.229.24.71:9090/DateService/S3/GetToday
    variable_name: business_date
```

### キャッシュする場合
1. 対象URLに対し、APIを用意しキャッシュを有効にする
2. `proxy-cache`に関しては[KONG公式プラグインの設定方法](../usage/how-to-configure-official-plugin.md)の3を参照

(例)
```
- name: get-business-date
  url: http://10.229.24.71:9090/DateService/S3/GetToday
  plugins:
  - name: proxy-cache
    config: 
      response_code:
        - 200
        - 301
      request_method:
        - GET
      content_type:
        - text/plain
        - application/json
        - application/json; charset=UTF-8
      cache_ttl: 300 #seconds
      strategy: memory
  routes:
  - name: getBusinessDate
    methods:
      - GET
    paths:
      - /getBusinessDate
```

3. `target_url`にAPIを指定する

(例)
```
plugins:
- name: save-business-date
  config:
    target_url: http://localhost:8000/getBusinessDate
    variable_name: business_date
```

|Form Parameter           | Required   | Description                                                       |
|-------------------------|------------|-------------------------------------------------------------------|
| `name`                  | *required* |`save-business-date`に固定                                          |
| `route`                 | *optional* |適応するルート.routeもserviceもない場合は全てが対象となる　　　　　　　　　　 |
| `service`               | *optional* |適応するサービス.routeもserviceもない場合は全てが対象となる　　　　　　      |
| `target_url`            | *required* |APIのURLを指定する.　　　　　　　　　　　　　　　　　　　　　　　　　　　 　　 |
| `variable_name`         | *required* |`kong.ctx.shared`に格納する変数名を指定する.　 　　　　　　　　　　　　 　　 |


---
## 処理内容
### 業務日付を取得し、変数に格納する
1. 環境変数`ENV`が`prod`の場合
  - 現在日付を`yyyymmdd`形式で取得する
  - `kong.ctx.shared.[variable_name]`に格納する
2. 環境変数`ENV`が`prod`以外の場合
  - `target_url`に対しリクエストし、業務日付を取得する
  - `yyyymmdd`形式に変換する
  - `kong.ctx.shared.[variable_name]`に格納する

---
## ログ一覧

|Message                                            | Log level  | Description                              |
|---------------------------------------------------|------------|------------------------------------------|
| `Could not get business date`                     | *error*    |業務日付をAPIから取得できなかった場合           |