# REST2SOAP仕様書
カスタムプラグインrest2soapの使い方

## 機能概要
- jsonでのリクエストをsoapxmlに変換しリクエスト、返却されたsoapxmlをjsonに変換し返却する

---
## 利用するシチュエーション
- COPやSCM、MGSなどSOAPでの通信が必要なシステムへのリクエストに対し、RESTで通信したい場合

---
## 利用方法
### swaggerの生成
__!! パイプラインでswagger生成を実施しているので実施不要です。swaggerのファイルが欲しい際は下記を実行してください__
1. （参考）wsdl2swaggerのインストール・実施

```
cd ./tools/wsdl2swagger
bash wsdl2swagger.sh
```

### kong.ymlへの追加
1. 以下の例とパラメータを指定して、kong.ymlに追加する

(例)
```
- name: SLFIWS010
  url: ...
  plugins:
    - name: rest2soap
      config:
        wsdl_path: "/usr/local/kong/soap/wsdl/SLFIWS010.wsdl"
        swagger_path: "/usr/local/kong/soap/swagger/SLFIWS010.json"
        request_body_tag: "ns2:SLFIWS010Request"
        response_body_tag: "slf:SLFIWS010Response"
  routes: 
    ...
```

|Form Parameter      | Required   | Description                                                       |
|--------------------|------------|-------------------------------------------------------------------|
| `name`             | *required* |`rest2soap`に固定                                                   |
| `route`            | *optional* |適応するルート.routeもserviceもない場合は全てが対象となる                  |
| `service`          | *optional* |適応するサービス.routeもserviceもない場合は全てが対象となる　　　　　　       |
| `wsdl_path`        | *required* |wsdlのコンテナ内でのパス                                               |
| `request_body_tag` | *required* |soapリクエスト時のbodyのタグ                                           |
| `response_body_tag`| *required* |soapレスポンスのbodyのタグ                                    　       |
| `xml_header`　　　  | *optional* |xml送信時のcontent-type.defaultは`application/soap+xml; charset=utf-8`|

---
## 処理内容
### wsdlの読み込み
1. wsdlのschemaタグのnamespace, targetNamespaceを変数に格納する

### swaggerの読み込み
1. swaggerのレスポンスのexampleをjsonに変換して変数に格納する
2. swaggerのレスポンスのexampleから配列対象を取得して変数に格納する

### SOAPリクエスト電文の作成,リクエスト
1. soap:Envelopeの枠を作る
2. リクエストのjsonをxmlに変換する
3. 1で作成したsoap:Envelopeのbodyタグに2を入れる
4. soapでリクエストを行う

### SOAPレスポンスの変換
1. soapでレスポンスをもらう
2. レスポンスのbodyタグの中身をjsonに変換する
3. swaggerのレスポンスのexampleを正として、レスポンスにないものをnullで埋める
4. swaggerで`type: array`のelementに対して、配列でない場合は配列にする
5. jsonをnginxのbodyに入れて返却する

### エラーが返却された場合
1. soapでレスポンスをもらう
2. レスポンスのbodyタグの中身をjsonに変換する
3. jsonをnginxのbodyに入れて返却する


---
## ログ一覧

|Message                                         | Log level  | Description                                |
|------------------------------------------------|------------|--------------------------------------------|
| `Unable to read WSDL file: [wsdl file]`        | *error*    |wsdlファイルの読み込みに失敗した場合  　　　　　   |
| `Unable to parse WSDL namespaces`              | *error*    |wsdlからnamespaceの読み込みに失敗した場合        |
| `Unable to create response example`            | *error*    |swaggerからレスポンスのexampleの生成に失敗した場合|
| `Failed parse API spec files: [description]`   | *error*    |wsdl,swaggerからの情報読み込みに失敗した場合      |
| `Empty request found`                          | *info*     |リクエストボディが空の場合                　     |
| `Unable to build XML body`                     | *error*    |リクエストボディのXML変換で失敗した場合           |
| `Unable to build SOAP request`                 | *error*    |soapリクエストの電文作成に失敗した場合            |
| `Unable to parse soap response body`           | *error*    |soapレスポンスのJSON変換に失敗した場合           |
| `Unable to complement soap response body`      | *error*    |soapレスポンスのNull補完に失敗した場合           |
| `Unable to convert soap item to single item array` | *error*    |soapレスポンスの配列変換に失敗した場合            |
| `Failed convert response:  [description]`      | *error*    |レスポンスの変換に失敗した場合                   |


---
## Tips
### soap mock コンテナの立ち上げ
1. 参考
    - https://github.com/castlemock/castlemock#docker
    - https://github.com/castlemock/castlemock/wiki/Use-Case:-SOAP

2. 下記を実行

```
docker run -d -p 8080:8080 castlemock/castlemock
```
ホスト側のポートは適宜変更

3. mockの設定
    - 次にブラウザでアクセス　http://localhost:8080/castlemock
    - username: admin, password: adminでログイン
    - [new project]からsoapでプロジェクト作成し、[　upload　]からwsdlをuploadする
    - 各Operationが酒制されるので、[Mock responses]から適宜レスポンスを修正する
