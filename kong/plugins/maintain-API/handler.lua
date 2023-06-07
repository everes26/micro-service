local lrucache = require "resty.lrucache"
local http = require "resty.http"
local dkjson = require "dkjson"

local cache, err = lrucache.new(100)
if err then
    kong.log.err("Failed to create the cache: " .. err)
end

local MaintainAPI = {
    VERSION = "0.1",
    PRIORITY = 99998,
}

function mainTain(plugin_conf)
    --kong.log.err("mainTain")

    local httpc = http.new()
    local url = plugin_conf.target_url

    local res, err = httpc:request_uri(url, {
        method = "GET",
        body = "",
        headers = {
            -- ["x-user-sub"] = "265B815E2B3EC",
            -- ["x-ozone-service-id"] = "265B815E2B3EC82FE80F293F3285D789C0E17D9B1C0EC0228FEACB1895184E10",
            -- ["Authorization"] = "Bearer TLMhLXCwkZkNpgYdiX-E3w.1668683126847",
            -- ["Content-Type"] = "application/json"
            -- ["Content-Type"] = "application/x-www-form-urlencoded"
        }
    })

    if err then
        kong.log.err("Call maintain error: " .. "err")
    end
    return res
end

function MaintainAPI:access(plugin_conf)
    --kong.log.err("access")

    local path = kong.request.get_path()
    kong.log.err("access-path: " .. path)

    local value, err = cache:get("maintain")
    if err then
        kong.log.err("Failed to get value from cache: " .. "err")
    end

    if value == nil then
        --kong.log.err("Call 1")
        local res = mainTain(plugin_conf)
        value = res.body
        local success, err = cache:set("maintain", value, 30)
        if err then
            kong.log.err("Failed to set value in cache: " .. "err")
        end
    else
        --kong.log.err("Call 2")
    end

    if checkAPIMaintain(path, plugin_conf) then
        kong.response.set_header("KONG-MAINTAIN", "TRUE")
        return kong.response.exit(503,{ message = "API Maintain" })
    else
        kong.response.set_header("KONG-MAINTAIN", "FALSE")
    end
end

function checkAPIMaintain(path, plugin_conf)
    --kong.log.err("checkAPIMaintain")

    local api_maintain = plugin_conf.api_maintain
    --kong.log.err("plugin_conf.apiMaintain: " .. api_maintain)
    local json_string = cache:get("maintain")
    local data = dkjson.decode(json_string)

    for i, infoCache in ipairs(data) do
        if infoCache.name == path then
            --kong.log.err("id: " .. infoCache.id)
            --kong.log.err("name: " .. infoCache.name)
            --kong.log.err("apiMaintain: " .. infoCache.apiMaintain)

            if infoCache.apiMaintain == api_maintain then
                kong.log.err("API Maintain: " .. "true")
                return true
            else
                kong.log.err("API Maintain: " .. "false")
                return false
            end

            break
        end
    end

    return false
end

return MaintainAPI