# Kong/Nginxの設定方法

## Kong設定方法

kongの設定は`KONG_`の接頭辞の環境変数か`/etc/kong/kong.conf`ファイルで指定することができる。   
環境ごとに変更する場合は環境変数での指定を用いる。  
詳しくは[Configuration Reference for Kong Gateway](https://docs.konghq.com/gateway/latest/reference/configuration/)を参照。

**設定ファイルを用いる場合**

`/etc/kong/kong.conf`をデフォルトで読み込む設定になっている。  
変更する場合は`kong start --conf /path/to/kong.conf`で指定する
設定例は`/etc/kong/kong.conf.default`に記載がある。

(例) `kong.conf`での設定定義

```
plugins = bundled,nestbody-transformer,...
untrusted_lua = on
database = off
```

イメージにコピーする

```
COPY kong.conf /etc/kong/kong.conf
```

**環境変数を用いる場合**

`KONG_`の接頭辞がついた環境変数をkongの設定値として認識する。

(例) Dockerfileでの環境変数定義

```
ENV KONG_PLUGINS bundled,nestbody-transformer,...
ENV KONG_UNTRUSTED_LUA on
ENV KONG_DATABASE off
```

## Nginxの設定方法

KongはNginxをベースに動いており、Nginxのパラメータ設定も環境変数または設定ファイルで行うことができる。

**設定ファイルを用いる場合**

`nginx_<namespace>_<directive>`を指定することで`[prefix (default to /usr/local/kong/)]/nginx.conf`に挿入される。

（例）httpブロックの設定を挿入したい場合

  1. `kong.conf`に記載する

      ```
      nginx_http_client_body_buffer_size=150m
      nginx_http_proxy_buffering=on
      nginx_http_proxy_buffers=8k
      nginx_http_proxy_buffer_size=8k
      nginx_http_log_format custom_format '"$remote_addr " - " $remote_user " [$time_local] " $request " "$status " "$body_bytes_sent " "$request_time " "$http_referer " "$http_user_agent " "$http_x_correlation_id " "$http_x_sb_trace_id"'
      ```

  2. httpブロックに挿入される

      ```
      http {
        ...
        # injected nginx_http_* directives
        client_body_buffer_size 150m;
        ...
      }
      ```

他も同様の仕組みで挿入される

| prefix                       | Target              | 
|------------------------------|---------------------|
| nginx_main_<directive>       | root in nginx.conf  |
| nginx_events_<directive>     | events {} block     |
| nginx_http_<directive>       | http {} block       |
| nginx_proxy_<directive>      | server {} block     |
| nginx_upstream_<directive>   | upstream {} block   |


**設定ファイルを用いる場合**

上記に`KONG_`の接頭辞をつけた環境変数を設定する。  
その他の仕組みは【設定ファイルを用いる場合】と同様。

(例) Dockerfileでの環境変数定義

```
ENV KONG_NGINX_HTTP_CLIENT_BODY_BUFFER_SIZE 150m
ENV KONG_NGINX_HTTP_PROXY_BUFFERING on
ENV KONG_NGINX_HTTP_PROXY_BUFFERS 100 8k
ENV KONG_NGINX_HTTP_PROXY_BUFFER_SIZE 8k
ENV KONG_NGINX_HTTP_LOG_FORMAT custom_format '"$remote_addr " - " $remote_user " [$time_local] " $request " "$status " "$body_bytes_sent " "$request_time " "$http_referer " "$http_user_agent " "$http_x_correlation_id " "$http_x_sb_trace_id"'
```
