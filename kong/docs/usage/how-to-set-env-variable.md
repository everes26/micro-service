# 環境変数の設定方法

## kong設定ファイルへの環境変数の埋め込み
環境ごとの変数設定を行いたい場合、[環境変数埋め込み](./how-to-add-api.md#各種設定)を参照

## kongプラグイン内で環境変数を使用する場合
kongプラグインでコンテナ定義の環境変数を利用する際はnginxを介する必要あり

`request-transformer`・`response-transformer`・`nestbody-transformer`で$(env.~)で環境変数を使用する場合は以下を実施する

### docker-composeで指定する場合（ローカル）
(例)`ENV`に`dev`を設定して、kong内で利用したい場合
- `ENV=dev`を追加する
- `KONG_NGINX_MAIN_ENV`に環境変数名を指定する
- 複数指定したい場合は`KONG_NGINX_MAIN_ENV=FOO; env BAR;`という風に記述する(FOOとBARを指定したい場合)

docker-compose.yml
```
environment:
- KONG_NGINX_MAIN_ENV=ENV
- ENV=dev
```

### kubernetesで指定する場合
(例)`ENV`に`dev`を設定して、kong内で利用したい場合
- 上記同様の項目をdeploymentの`env`で定義する

```
env:
- name: ENV
  value: dev
- name: KONG_NGINX_MAIN_EN
  value: ENV
```



