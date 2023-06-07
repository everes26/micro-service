# Kong kubernetesファイルのメンテナンス方法

## 事前準備
### Kubectlコマンドインストール方法
1. [SDI3.0利用手順](https://docs.google.com/presentation/d/1PWihO_vBzTusTAJEahtxmWxoz8KphI5ZvqTwxSmHH_Q/edit#slide=id.gda47a7eb85_0_548)の`kubectl実行準備`を実施する


## Kong k8sファイルの更新
### プロジェクトのクローン
1. [k8sマニフェストプロジェクト](http://code-dev.ark.sbb-sys.info/cml/mobile-api-deploy)をclone
2. kong向けは`sdi3.0/k8s-sbm-ols-kong`に含まれておりkustomizeで管理している
  - base/kong-deployment.yml
    - deploymentのベースファイル
  - base/kong_service.yml
    - serviceのベースファイル
    - 80ポートを公開し、podの8000ポートに流す設定
  - base/kustomization.yml
    - 共通ラベルの定義（app: kong, owner: m00494）
  - overlays/dev/[各環境]/config.file
    - kongで使用する環境変数（対向先のURLなど）を定義
  - overlays/dev/[各環境]/secret.file
    - kongで使用する秘匿情報（secretなど）を定義
  - overlays/dev/[各環境]/kustomization.yml
    - 各環境のprefix指定やイメージの情報を記載
  - overlays/dev/[各環境]/patch
    - 各環境用に変更必要な部分を記載（現状特になし）

### デプロイメントの概要
1. `initContainer`で以下を実施
  - 環境変数埋め込み
  - nginx.confの設定変更

2. `container`の設定
  - 環境変数を埋め込んだkong.ymlを指定
  - livenessProbe
    - `kong health`を10s間隔で実施
  - readinessProbe:
    - `/healthcheck`に10s間隔でアクセス

## 手動でのデプロイメント更新
基本はパイプラインでデプロイできますが、下記で手動で実施できます。

#### imageの用意
1. harborにイメージpush
  - ```docker build -t kong-custom:latest ./kong```でイメージを作成
  - [参考](http://code-dev.ark.sbb-sys.info/cml/cml-api-deployment-sdi/tree/master/docs/dev/kongapigateway)の「DockerイメージをHarborにプッシュ」を参照
  - `<Username>`と`<CLI secret>`は[harborポータル](http://harbor-portal.m00769-0002-harbor-dev.blue.ot.itdev.sdi/harbor/projects)の画面右上Userprofileから確認([harbor利用マニュアル](https://docs.google.com/presentation/d/1ljmrwzExLAJrY078TDSefLWCl0TDvtCyhlNnqqiyXBI/edit#slide=id.ge342ca4720_0_73)を参照)

  ```
  docker login harbor-portal.m00769-0002-harbor-dev.blue.ot.itdev.sdi -u <Username> -p <CLI secret>
  cd sbm-ols-kong
  docker build . -t harbor-portal.m00769-0002-harbor-dev.blue.ot.itdev.sdi/m00494-0003-cme/sbm-ols-kong:latest
  docker push harbor-portal.m00769-0002-harbor-dev.blue.ot.itdev.sdi/m00494-0003-cme/sbm-ols-kong:latest
  ```

#### Kustomizeでのデプロイ
1. cloneした`mobile-api-deploy`のマニフェストファイルをデプロイする

```
cd sdi3.0/k8s-sbm-ols-kong
kustomize build overlays/dev/review | kubectl --kubeconfig=ves_system_dev-site01.yaml -n m00494-0003-cme-apig-review-l3dev apply -f -
```

(it環境の場合)
```
kustomize build overlays/dev/it | kubectl --kubeconfig=ves_system_dev-site01.yaml -n m00494-0003-cme-apig-it-l3dev apply -f -
```

(qa環境の場合)
```
kustomize build overlays/dev/qa | kubectl --kubeconfig=ves_system_dev-site01.yaml -n m00494-0003-cme-apig-qa-l3dev apply -f -
```
