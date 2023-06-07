# DATADOGとの連携
pluginを利用する方法とコンテナでdatadogを有効化する方法があり

## 1.kong datadog pluginの利用の場合
### kong.ymlへの追加

```
plugins:
- name: datadog
  config: 
    host: 127.0.0.1
    port: 8125
    metrics: 
    prefix: kong
```

### メトリック
1. request_count
    - リクエスト数
2. request_size
    - リクエストサイズ
3. response_size
    - レスポンスサイズ
4. latency
    - レイテンシー
5. upstream_latency
    - backend側のレイテンシー
6. kong_latency
    - kong側のレイテンシー

### datadogのクエリ

```
avg:kong.latency.avg{name:sample-service}
```

### 変更可能性
- メトリックの量を絞るのみ
- datadogプラグインをカスタムすることで変更は可能

## 2.コンテナでdatadogを有効化する場合
### podへの設定

```
apiVersion: v1
kind: Pod
metadata:
  name: '<POD_NAME>'
  annotations:
    ad.datadoghq.com/<CONTAINER_IDENTIFIER>.check_names: '["kong"]'
    ad.datadoghq.com/<CONTAINER_IDENTIFIER>.init_configs: '[{}]'
    ad.datadoghq.com/<CONTAINER_IDENTIFIER>.instances: '[{"kong_status_url": "http://%%host%%:8001/status/"}
]'
spec:
  containers:
    - name: '<CONTAINER_IDENTIFIER>'
```

### メトリック
[こちらを参照](https://docs.datadoghq.com/ja/integrations/kong/?tab=host#%E3%83%A1%E3%83%88%E3%83%AA%E3%82%AF%E3%82%B9)

### 変更可能性
- 不可能