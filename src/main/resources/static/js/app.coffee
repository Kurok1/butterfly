#入口js
define [
    'js/component/HttpClient',
    'Vue',
    'VueRouter'
], (HttpClient, Vue, VueRouter)->
    {
        start: (ele)->
            #使用VueRouter
            Vue.use(VueRouter)
            #定义组件
            Foo = { template: '<div>foo</div>' }
            Bar = { template: '<div>bar</div>' }

            routes = [
                { path: '/foo', component: Foo },
                { path: '/bar', component: Bar }
            ]

            router = new VueRouter({
                routes
            })
            app = new Vue({
                router
            }).$mount(ele)

        build: ()->
            HttpClient.get()

        getAppName: ()->
            return "hello, world"

    }