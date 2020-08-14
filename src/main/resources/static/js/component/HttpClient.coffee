define [
    'jQuery'
], (jQuery)->
    {
        get: (url)->
            jQuery.ajax(url)

    }