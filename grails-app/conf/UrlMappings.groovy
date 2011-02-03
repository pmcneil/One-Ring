class UrlMappings {

    static mappings = {
        "/rest/applyRules"(controller: "rulesEngine") {
            action = [GET: "fire", POST: "fire"]
        }
        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }

        "/"(view: "/index")
        "500"(view: '/error')
    }
}
