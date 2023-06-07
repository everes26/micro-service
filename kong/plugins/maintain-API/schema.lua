local typedefs = require "kong.db.schema.typedefs"


local schema = {
  name = "maintain-API",
  fields = {
    { consumer = typedefs.no_consumer },
    { protocols = typedefs.protocols_http },
    {
      config = {
        type = "record",
        fields = {
            {
              target_url = {
                type = "string",
                required = true
              },
            },
            {
              api_maintain = {
                type = "number",
                required = true
              },
            },
        },
      },
    },
  },
}

return schema
