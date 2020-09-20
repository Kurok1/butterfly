#入口js
define [
    'js/component/HttpClient',
    'Vue',
    'VueResource',
    'VueRouter'
], (HttpClient, Vue, VueResource,VueRouter )->
    {
        _app: null

        start: (ele)->
            #init vue components...
            Vue.use(VueRouter)
            Vue.use(VueResource)
            #定义组件
            Foo = { template: '<div>foo</div>' }
            Bar = { template: '<div>bar</div>' }

            #定义路由
            routes = [
                { path: '/login'},
                { path: '/bar', component: Bar }
            ]

            router = new VueRouter({
                routes
            })
            app = new Vue({
                router
            }).$mount(ele)
            router.go('login.html')
            @_app = app


        getAppName: ()->
            return "butterfly"

        getAppVersion: ()->
            return "1.0.0"

        http: ()->
            return new HttpClient(@_app)

    }