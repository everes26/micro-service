local http = require "resty.http"
local AuthCustom = {
    VERSION = "0.1",
    PRIORITY = 99999,
}


function verify_token(plugin_conf, token)
    local httpc = http.new()
    local url = plugin_conf.target_url

    local res, err = httpc:request_uri(url, {
      method = "GET",
      headers = {
        ["Authorization"] = token,
      },
    })
  
    if not res then
      return false, err, 500
    end
  
    if res.status ~= 200 then
      return false, res.body, res.status
    end
  
    return true, nil, 200
  end


function AuthCustom:access(plugin_conf)

    local token = kong.request.get_header("Authorization")

    if token == nil then
        kong.log.err("Token is nil")
        return kong.response.exit(401, { message = "Unauthorized" })
    else
        kong.log.err("Token: " .. token) 
    end

    local ok, body, httpStatus = verify_token(plugin_conf, token)

    if not ok then
        kong.log.err("Token verification failed")
        return kong.response.exit(httpStatus, body)
    end

    kong.log.err("Token verification succeeded")

end

return AuthCustom