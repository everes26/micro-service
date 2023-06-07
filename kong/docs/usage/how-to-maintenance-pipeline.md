# Kongパイプラインのメンテナンス方法

## 事前準備
### Flyコマンドインストール方法
1. [Concourse](http://app-ci.dev.ithqdevops.net/)にアクセスし、AzureADでログイン
2. ページ下部にカーソルを当てると「version: v5.7.1　cli:...」という表示があるので、自分の環境に合わせてダウンロードする（[参考資料](https://docs.google.com/presentation/d/1GjfqYDmHuJ8hbK1LaloQve90e6ZsutiTiRMGiTtNG7Q/edit#slide=id.gaf24c9ab40_0_75)）
3. 任意のディレクトリに配置し、パスを通す。

（macの場合の例）
```
mv <ダウンロードしたファイル>　/usr/local/bin/fly
chmod +x /usr/local/bin/fly
# 動作確認
fly --version
```

## Kongパイプラインの更新
### パイプラインのクローン
1. [パイプラインプロジェクト](http://code-dev.ark.sbb-sys.info/cml/mobile-pipeline)をclone
2. kongパイプラインは`/sdi3.0/kong`に含まれており
  - sbm-ols-kong-pipeline.yml
    - パイプラインの処理内容
  - sbm-ols-kong-settings.yml
    - パイプラインで使用する変数の定義
  - version
    - パイプラインで参照するリリース時のタグの定義

### パイプラインの概要
kong向けのパイプラインは以下の流れで実行される

1. イメージのビルド
  - 本リポジトリから最新資材を取得する
  - wsdl2swagger.shを実施し、swaggerを生成する
  - イメージをビルドし、harborへpushする
2. review環境にデプロイ
  - harborから１でpushしたイメージをpullする
  - [k8sプロジェクト](http://code-dev.ark.sbb-sys.info/cml/mobile-api-deploy)を取得し、review環境にデプロイ
3. it環境のリリースのバージョンを更新する
  - version/sbm-ols-kong-it-versionを更新する
4. it環境にデプロイ
  - 2と同様にして、it環境にデプロイ
5. qa環境のリリースのバージョンを更新する
  - version/sbm-ols-kong-qa-versionを更新する
6. qa環境にデプロイ
  - 2と同様にして、qa環境にデプロイ

### パイプラインの更新
1. 修正後、以下のコマンドを打ち、差分が表示されるので`Y/N`でyを選択し反映する

```
fly -t ci set-pipeline -c sdi3.0/kong/sbm-ols-kong-pipeline.yml -p sbm-ols-kong-pipeline-sdi3 -l sdi3.0/kong/sbm-ols-kong-settings.yml
```

2. コマンドでパイプラインを動かす場合は以下
- パイプラインのpause解除（パイプラインが稼働状態の場合は不要）
```
fly -t ci unpause-pipeline -p sbm-ols-kong-pipeline-sdi3
```

- イメージビルドのジョブ実行
```
fly -t ci trigger-job -j sbm-ols-kong-pipeline-sdi3/build-container-image -w 
```

3. パイプラインのデバッグ
以下でジョブ実行中のコンテナに入ることができる

- build-container-imageのジョブのデバッグ
```
fly -t ci hijack -j sbm-ols-kong-pipeline-sdi3/build-container-image
```

- deploy-ui-app-to-reviewのジョブのデバッグ
```
fly -t ci hijack -j sbm-ols-kong-pipeline-sdi3/deploy-ui-app-to-review
```