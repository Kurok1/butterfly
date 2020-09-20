define [
    'js/component/Storage'
], (Storage)->
    (app)-> {
        _headers : {}
        _DEFAULT_HEADERS : {"Content-Type": "application/json; charset=utf-8", "B-DEVICE": "browser"} # 默认都使用json
        _responseType : 'json'
        _config : {}
        _requestObject : null
        _error: (response)->
            console.log(response)

        _reset: ()->
            @_headers = {}
            @_config = {}
            @_requestObject = null

        _addHeaders: (headers = {})->
            for key of headers
                try
                    if headers[key] != null || headers[key] != undefined
                        @_headers[key] = headers[key]

        get: (url, headers = {})->
            me = @
            @_reset()
            @_addHeaders(@_DEFAULT_HEADERS)
            @_addHeaders({})
            config = {
                method : 'get'
                headers : @_headers
                responseType : @_responseType
                before : (request)->
                    request.headers.set(Storage.getUserTokenName(), Storage.getUserToken())
            }
            @_config = config

            @_requestObject = app.$http.get(url, config)
            return @


        post: (url, body = null, headers = {})->
            me = @
            @_reset()
            @_addHeaders(@_DEFAULT_HEADERS)
            @_addHeaders({})
            config = {
                method : 'post'
                headers : @_headers
                responseType : @_responseType
                before : (request)->
                    request.headers.set(Storage.getUserTokenName(), Storage.getUserToken())
            }
            @_config = config

            @_requestObject = app.$http.post(url, body,config)
            return @

        put: (url, body = null, headers = {})->
            me = @
            @_reset()
            @_addHeaders(@_DEFAULT_HEADERS)
            @_addHeaders({})
            config = {
                method : 'put'
                headers : @_headers
                responseType : @_responseType
                before : (request)->
                    request.headers.set(Storage.getUserTokenName(), Storage.getUserToken())
            }
            @_config = config

            @_requestObject = app.$http.put(url, body,config)
            return @

        delete: (url, body = null, headers = {})->
            me = @
            @_reset()
            @_addHeaders(@_DEFAULT_HEADERS)
            @_addHeaders({})
            config = {
                method : 'delete'
                headers : @_headers
                responseType : @_responseType
                before : (request)->
                    request.headers.set(Storage.getUserTokenName(), Storage.getUserToken())
            }
            @_config = config

            @_requestObject = app.$http.delete(url, body,config)
            return @


        login: (username, password)->
            #预留

        execute: (Callback)->
            @_requestObject.then(Callback, @_error)



    }