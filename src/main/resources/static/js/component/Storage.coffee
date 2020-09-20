define [
], ()->{

        USER_TOKEN: "B-TOKEN"

        getUserTokenName: ()->
            return @USER_TOKEN

        getUserToken: ()->
            return @get(@USER_TOKEN)

        setUserToken: (token)->
            @put(@USER_TOKEN, token)

        put: (key, value)->
            window.localStorage.setItem(key, value)

        get: (key)->
            val = window.localStorage.getItem(key)
            if val == '' || val == null || val == undefined
                return ""
            return val

        clear: ()->
            window.localStorage.clear()

        delete: (key)->
            window.localStorage.removeItem(key)

        exists: (key)->
            if window.localStorage.length == 0
                return false
            else
                for i in window.localStorage
                    if window.localStorage.key(i) != null || window.localStorage.key(i) != undefined
                        return true
                return false


    }