# Build kong with plugins
FROM kong:3.0.0-ubuntu

USER root

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN apt-get update \
   && apt-get install -y \
     wget \
     gettext \
     tzdata

RUN luarocks install xml2lua
RUN luarocks install base64
RUN luarocks install lua-resty-env

#Library Json
RUN luarocks install dkjson

COPY plugins/maintain-API /usr/local/share/lua/5.1/kong/plugins/maintain-API
COPY plugins/auth-custom /usr/local/share/lua/5.1/kong/plugins/auth-custom

COPY ./config/kong.yml /usr/local/kong/decrelative/kong.yml


# Env variables
ENV KONG_PLUGINS bundled, maintain-API, auth-custom
ENV KONG_UNTRUSTED_LUA on
ENV KONG_DATABASE off
ENV KONG_ADMIN_LISTEN 0.0.0.0:8001
ENV LC_CTYPE en_US.UTF-8
ENV LC_ALL en_US.UTF-8
ENV TZ Asia/Tokyo
ENV KONG_NGINX_HTTP_CLIENT_BODY_BUFFER_SIZE 150m
ENV KONG_NGINX_HTTP_PROXY_BUFFERING on
ENV KONG_NGINX_HTTP_PROXY_BUFFERS 100 8k
ENV KONG_NGINX_HTTP_PROXY_BUFFER_SIZE 8k
ENV KONG_NGINX_HTTP_LOG_FORMAT custom_format '"$remote_addr " - " $remote_user " [$time_local] " $request " "$status " "$body_bytes_sent " "$request_time " "$http_referer " "$http_user_agent " "$http_x_correlation_id " "$http_x_sb_trace_id"'

USER kong