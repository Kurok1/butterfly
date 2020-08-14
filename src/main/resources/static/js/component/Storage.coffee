define [

], ()->{

        put: (key, value)->
            window.localStorage.setItem(key, value)

        get: (key)->
            window.localStorage.getItem(key)

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