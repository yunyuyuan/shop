$(document).ready(function () {
    let vue_ = new Vue({
        el: '#container',
        data: {
            cart_goods: [],
            choose_cart: [],
            go_check: null,
            price: 0,
            addr: ['','','','暂无'],
        },
        watch: {
            cart_goods: {
                handler(){
                    for (let i of this.cart_goods){
                        if (i[4] < 1){
                            head_vue.remind({'state': 'error', 'msg': '商品数量不能小于1'});
                            return
                        }
                    }
                    if (this.submit_change!=null){
                        clearTimeout(this.submit_change);
                        this.submit_change=setTimeout(function () {
                            ajax_(function (r) {
                                head_vue.remind(r)
                            }, '/accountajax',{'key': 'modify_cart', 'cart': JSON.stringify(vue_.cart_goods)})
                        }, 1000);
                    }else{
                        this.submit_change=setTimeout(function () {}, 0);
                    }
                },
                deep: true
            },
            choose_cart: function () {
                this.$el.querySelector('#cart-submit>label>input').checked = this.choose_cart.indexOf(false)===-1;
            }
        },
        computed: {
            cal_price: function () {
                let p = 0;
                for (let i in this.choose_cart){
                    if (this.choose_cart[i]){
                        p += this.cart_goods[i][3]*this.cart_goods[i][4]
                    }
                }
                this.price = p;
                return p;
            },
            has_select: function () {
                return this.choose_cart.indexOf(true)===-1;
            },
            select_count: function () {
                let c = 0;
                for (let i in this.choose_cart){
                    if (this.choose_cart[i]){
                        c += this.cart_goods[i][4]
                    }
                }
                return c;
            }
        },
        methods: {
            change_select_all: function (e) {
                let t = e.target.checked;
                for (let i in vue_.choose_cart){
                    vue_.choose_cart[i]=t;
                }
                vue_.choose_cart.splice(0, 0);
            },
            del: function () {
                if (confirm('确认删除？')){
                    let new_cart = [];
                    for (let i in vue_.choose_cart){
                        if (!vue_.choose_cart[i]){
                            new_cart.push(vue_.cart_goods[i])
                        }
                    }
                    ajax_(function (r) {
                        location.reload()
                    }, '/accountajax', {'key': 'modify_cart', 'cart': JSON.stringify(new_cart)})
                }
            },
            check_: function () {
                this.go_check = true;
            },
            hide_check: function () {
                this.go_check = false;
            },
            submit_order: function (e) {
                let btn = e.target;
                if (vue_.addr.indexOf('')!==-1){
                    head_vue.remind({'state': 'error','msg': '收货信息为空！'})
                }else{
                    btn.setAttribute('disabled', '');
                    let new_cart = [];
                    for (let i in vue_.choose_cart){
                        if (vue_.choose_cart[i]){
                            new_cart.push(vue_.cart_goods[i])
                        }
                    }
                    ajax_(function (r) {
                        head_vue.remind(r);
                        if (r['state']==='success'){
                            setTimeout(function () {
                                location.href=path+'/i?locate=order'
                            }, 500)
                        }else{
                            head_vue.remind(r);
                        }
                    }, '/accountajax', {'key': 'submit_order', 'cart': JSON.stringify(new_cart), 'addr': JSON.stringify(vue_.addr)})
                }
            }
        }
    });
    // 获取购物车
    ajax_(function (r) {
        if (r['state']==='success'){
            vue_.cart_goods = r['data'];
            vue_.choose_cart = [];
            for (let i=0;i<r['data'].length;i++){
                vue_.choose_cart.push(false);
            }
        }else if(r['msg']==='未登录'){
            location.href=path+'/login?url='+location.href;
        }
    }, '/accountajax', {'key': 'get_cart'});

});