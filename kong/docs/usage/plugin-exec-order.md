# プラグインの実行順序

- カスタムプラグインは太字
- Kong 標準のプラグインは[公式ドキュメント](https://docs.konghq.com/enterprise/2.5.x/plugin-development/custom-logic/)より抜粋
- ライフサイクル（access, header_filter, body_filter など）で Priority の高いものから実施される

| Plugin                            | Priority |
| --------------------------------- | -------- |
| `pre-function`                    | _+inf_   |
| `correlation-id`                  | _100001_ |
| **`save-business-date`**          | _99999_  |
| **`upstream-log`**                | _99998_  |
| `jwt`                             | _1005_   |
| **`custom-jwt`**                  | _1005_   |
| `request-size-limiting`           | _951_    |
| `acl`                             | _950_    |
| `rate-limiting`                   | _901_    |
| **`datetime-format-change`**      | _803_    |
| **`nestbody-transformer`**        | _802_    |
| `request-transformer`             | _801_    |
| **`custom-request-transformer`**  | _801_    |
| **`regex-change`**                | _800_    |
| **`rest2soap`**                   | _799_    |
| `response-transformer`            | _798_    |
| **`custom-response-transformer`** | _798_    |
| **`redirect-plugin`**             | _797_    |
| `http-log`                        | _12_     |
| `datadog`                         | _10_     |
| `request-termination`             | _2_      |
| `correlation-id`                  | _1_      |
| `post-function`                   | _-1000_  |
