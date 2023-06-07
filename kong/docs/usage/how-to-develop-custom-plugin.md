# カスタムプラグインの作成方法

## 参考
- https://konghq.com/blog/custom-lua-plugin-kong-gateway/

## プラグインのtemplateをダウンロード
1. [github](https://github.com/Kong/kong-plugin.git)からクローンorダウンロード
2. プラグインの名前を書き換え
    - kong/plugins/myplugin -> kong/plugins/[plugin name]
    - spec/myplugin -> spec/[plugin name]
    - PLUGIN_NAME を myplugin -> [plugin name]

## カスタムプラグインの作成
1. ファイル構成
    1. handler.lua(必須)
        - プラグインの処理を記載する。ライフサイクルの各フェーズで実行する処理を記載する。
    2. schema.lua（必須）
        - ユーザーが動作を変更するための変数を定義する。
    3. api.lua
        - Admin API用のエンドポイントをて定義する。
    4. daos.lua
        - カスタムのentityを保管する際のDAOを定義する。
    5. migrations/*.lua
        - データベースのマイグレーション用。
2. handler.luaの作成
    - [Implementing Custom Logic](https://docs.konghq.com/gateway-oss/2.5.x/plugin-development/custom-logic/)を参照
3. schema.luaの作成
    - [Plugin Configuration](https://docs.konghq.com/gateway-oss/2.5.x/plugin-development/plugin-configuration/)を参照
2. 関数など
    - [Plugin_Development_Kit]（https://docs.konghq.com/gateway-oss/2.5.x/pdk/）を参照

## カスタムプラグインの有効化
1. luaファイルをコピーする場合
    - Dockerfileでコピーする（例：test-pluginをコピーする場合）

    ```
    COPY ./kong-custom-test-plugin/kong/plugins/test-plugin /usr/local/share/lua/5.1/kong/plugins/test-plugin
    ```
2. luarockでパッケージ化する場合
    - Dockerfileで下記を実施

    ```
    FROM kong as builder

    USER root

    COPY ./kong-custom-test-plugin /plugins/kong-custom-test-plugin

    WORKDIR /plugins/kong-custom-test-plugin

    ENV LUAROCKS_PACKAGE_NAME=kong-custom-test-plugin
    ENV LUAROCKS_PACKAGE_VERSION=1.0.2-1
    RUN luarocks make && \
        luarocks pack ${LUAROCKS_PACKAGE_NAME} ${LUAROCKS_PACKAGE_VERSION}

    FROM kong

    USER root
    COPY --from=builder /plugins/kong-custom-test-plugin/kong-custom-test-plugin*.rock /tmp/plugins/
    RUN luarocks install /tmp/plugins/kong-custom-test-plugin*.rock && \
    rm /tmp/plugins/*
    ```

3. Dockerfileの環境変数`KONG_PLUGINS`に追記する

`ENV KONG_PLUGINS bundled,nestbody-transformer,custom-jwt-user,custom-jwt-admin,redirect-plugin,regex-change,rest2soap,datetime-format-change`

## pongoのインストール（プラグインのテスト用,利用する場合）
1. [github](https://github.com/Kong/kong-pongo)からクローンorダウンロード
2. 下記を実行

```
PATH=$PATH:~/.local/bin
git clone https://github.com/Kong/kong-pongo.git
mkdir -p ~/.local/bin
ln -s $(realpath kong-pongo/pongo.sh) ~/.local/bin/pongo
```
3. ```pongo```で下記が出ればOK

```
               /~\ 
  ______       C oo
  | ___ \      _( ^)
  | |_/ /__  _/__ ~\ __   ___
  |  __/ _ \| '_ \ / _` |/ _ \
  | | | (_) | | | | (_| | (_) |
  \_|  \___/|_| |_|\__, |\___/
                    __/ |
                   |___/
```

## Artifactoryの利用
githubにある[yq（yamlマージ用モジュール）](https://github.com/mikefarah/yq)はArtifactoryで管理している
変更する場合は下記手順で実施

### yqの取得
- [v4.13.4](https://github.com/mikefarah/yq/releases/download/v4.13.4/yq_linux_amd64.tar.gz)にアクセス
- versionはhttps://github.com/mikefarah/yq/releases/download/[version]/yq_linux_amd64.tar.gz で指定する

### Artifactoryへの登録

```
curl -u cn-foundation-user:cn-foundation-user-Password@1 -T yq_linux_amd64.tar.gz "http://artifact-master.mobius.bb.local/artifactory/m00494-local-generic/kong/yq/"
```

### Artifactoryへの登録

```
RUN wget --user cn-foundation-user --password cn-foundation-user-Password@1 http://artifact-master.mobius.bb.local/artifactory/m00494-local-generic/kong/yq/yq_linux_amd64.tar.gz -O - | tar xz && mv yq_linux_amd64 /usr/bin/yq
```