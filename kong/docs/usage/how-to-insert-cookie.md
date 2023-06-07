# レスポンスをCookieに設定して返却する方法

## 利用方法
### kong.ymlへの追加
1. `pre-function`あるいは`post-function`にて、ヘッダーの`Set-Cookie`に設定する。
2. `pre-function`の場合は他のプラグインの前に実行される。`pre-function`の場合は他のプラグインの後に実行される。

(例)対向先からのレスポンスをクッキーに入れる場合
```
plugins:
- name: post-function
config: 
    access:
    - |2
      kong.service.request.enable_buffering()
    header_filter:
    - |2
      local body = kong.service.response.get_raw_body()
      kong.response.set_header("Set-Cookie","response="..body.."; Max-Age=120; Domain=mb.softbank.jp")
```

(例)リクエストをクッキーに入れる場合
```
plugins:
- name: post-function
config: 
    access:
    - |2
      local body = kong.request.get_raw_body()
      kong.service.request.set_header("Set-Cookie","request="..body.."; Max-Age=120; Domain=mb.softbank.jp")

```

### Set-Cookieヘッダーの属性値
1. [こちら](https://developer.mozilla.org/ja/docs/Web/HTTP/Headers/Set-Cookie)を参照
2. 以下の属性値を指定できる（上記サイト抜粋）

```
Set-Cookie: <cookie-name>=<cookie-value>
Set-Cookie: <cookie-name>=<cookie-value>; Expires=<date>
Set-Cookie: <cookie-name>=<cookie-value>; Max-Age=<number>
Set-Cookie: <cookie-name>=<cookie-value>; Domain=<domain-value>
Set-Cookie: <cookie-name>=<cookie-value>; Path=<path-value>
Set-Cookie: <cookie-name>=<cookie-value>; Secure
Set-Cookie: <cookie-name>=<cookie-value>; HttpOnly

Set-Cookie: <cookie-name>=<cookie-value>; SameSite=Strict
Set-Cookie: <cookie-name>=<cookie-value>; SameSite=Lax
Set-Cookie: <cookie-name>=<cookie-value>; SameSite=None; Secure

// 以下の例のように、複数のディレクティブも利用することができます。
Set-Cookie: <cookie-name>=<cookie-value>; Domain=<domain-value>; Secure; HttpOnly
```

3. luaの文字列結合は`..`でできるため、変数に格納された文字列も使用可能