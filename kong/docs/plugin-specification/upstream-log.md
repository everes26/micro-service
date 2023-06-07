# UPSTREAM-LOG仕様書
カスタムプラグインUPSTREAM-LOGの使い方

## 機能概要
- アップストリームのログを出力する

---
## 利用するシチュエーション
- アップストリームのログを出力したい場合

---
## 利用方法
### kong.ymlへの追加
1. 以下の例とパラメータを指定して、kong.ymlに追加する

(例)
```
plugins:
- name: upstream-log
  config:
    except_paths
    - /healthcheck
    - /redirectCasis
    log_display_res_size:
      when_status_error: 100
      when_status_ok: 100
```


|Form Parameter                           | Required   | Description                                         |
|-----------------------------------------|------------|-----------------------------------------------------|
| `name`                                  | *required* |`save-business-date`に固定                            |
| `route`                                 | *optional* |適応するルート.routeもserviceもない場合は全てが対象となる　　|
| `service`                               | *optional* |適応するサービス.routeもserviceもない場合は全てが対象となる |
| `except_paths`                          | *optional* |ログ出力対象から除外するkongのAPIのパス              　　　|
| `log_display_res_size.when_status_error`| *optional* |4xx,5xxの時の表示するレスポンスの文字数。デフォルトは100    |
| `log_display_res_size.when_status_ok`   | *optional* |4xx,5xx以外の時の表示するレスポンスの文字数。デフォルトは50  |


---
## 処理内容
### アップストリームログを出力する
1. 以下の形式でアップストリームとの通信ログを`log`phaseに出力する(ログレベルは`info`)

```
[upstream-log] date: Fri, 03 Dec 2021 01:33:26 GMT, x-correlation-id: 715adda5-928f-406d-8c99-422188aea25e, X-SB-Trace-ID: 715adda5, upstream_uri: /info/api/v1/addresses, status: 400, latency: 378, response: {"result":"failure","error":{"code":"E-00002"},"detail":[]}
```

2. その他仕様
  - `except_paths`に設定したkong APIのパスの場合は処理を行わない
  - 取得できなかった項目は空文字となる
  - ログ出力においてエラーが発生した場合はログ一覧のwarningメッセージが出力される

---
## ログ一覧

|Message                                                                          | Log level  | Description                              |
|---------------------------------------------------------------------------------|------------|------------------------------------------|
| `date: [date], ...`                                                             | *info*     |アップストリームログ　　　　　　　　            |
| `Could not get upstream response, path: , x-sb-request-id: , x-correlation-id: `| *warn*     |レスポンスボディの取得で失敗した場合            |
| `Could not get http status, path: , x-sb-request-id: , x-correlation-id: `      | *warn*     |レスポンスのステータス取得で失敗した場合         |
| `Could not display upstream log, path: [path]`                                  | *warn*     |ログの出力で失敗した場合                      |

