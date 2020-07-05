$(document).ready(function () {
    let goods_id = location.pathname.replace(/.*\/(\d*)/, '$1');
    let vue_ = new Vue({
        el: '#container',
        data: {
            info: {'id': '','owner_id': [0, '', 0], 'title': '', 'price': [{'name': '', 'stds': [['',true]]}], 'ps': {'0': [88, 1]},'dcb': '', 'buy_num': 0, 'fav_num': 0, 'star': 0, 'cover': 'default.jpg', 'cate': []},
            choose_key: ['',''],
            rank_goods: [],
            has_fav: [false,false],
            show_what: 'dcb',
            now_p: 1,
            ev_data: [0,[]],
        },
        computed: {
            cal_price: function () {
                let key = '';
                let sk = '';
                for (let e of this.info['price']) {
                    e['stds'].filter(function (s, idx) {
                        key += (s[1])? idx.toString():'';
                        sk += (s[1])? (s[0]+'+'):'';
                    })
                }
                this.choose_key = [key, sk.substring(0, sk.length-1)];
                return this.info['ps'][key];
            },
            page_count: function () {
                return Math.ceil(this.ev_data[0]/15)
            }
        },
        methods: {
            change_std: function (e) {
                let span = e.target;
                if(!span.classList.contains('chosen')){
                    span.parentElement.querySelector('.chosen input').click();
                    span.querySelector('input').click();
                }
            },
            toggle_fav: function (e) {
                let btn = e.target;
                btn.setAttribute('disabled', '');
                let idx = (btn.getAttribute('what')==='goods')?0:1;
                ajax_(function (r) {
                        btn.removeAttribute('disabled');
                        if (r['state'] === 'success') {
                            vue_.has_fav.splice(idx, 1, r['msg']);
                            vue_.info['fav_num'] += (r['msg']?1:-1);
                            r['msg'] = ((r['msg'])? '':'取消')+'收藏成功';
                        }else{
                            if (r['msg'] === '未登录'){
                                location.href = path+'/login?url='+location.href;
                            }
                        }
                        head_vue.remind(r);
                    }, '/accountajax', {'key': 'toggle_fav', 's': !vue_.has_fav[idx], 'id': goods_id, 'what': btn.getAttribute('what')})
            },
            add_cart: function(){
                ajax_(function (r) {
                        if (r['msg'] === '未登录'){
                            location.href = path+'/login?url='+location.href;
                        }
                        head_vue.remind(r);
                    }, '/accountajax', {'key': 'add_cart', 'id': goods_id, 'k': vue_.choose_key[1], 'p': vue_.info['ps'][vue_.choose_key[0]][0]})
            },
            change_show: function (s) {
                this.show_what = s;
            },
            cal_time: function (t) {
                return (new Date(t)).toLocaleDateString().replace(/\//g, '-')
            },
            change_page: function (p) {
                get_evaluate(parseInt(p)-1)
            }
        }
    });
    // 获取信息
    ajax_(function(r){
        if (r['state'] === 'success'){
            let data = r['data'];
            data['price'].filter(function (e) {
                for (let i=0;i<e['stds'].length;i++){
                    e['stds'][i] = [e['stds'][i], i===0];
                }
            });
            vue_.info = data;
            document.title = data['title'];
            vue_.$refs['goods_star'].update_percent(data['star']);
            // 获取热销
            ajax_(function (r) {
                if (r['state']==='success'){
                    vue_.rank_goods = r['data']
                }
            }, '/public_', {'key': 'get_rank', 'id': data['owner_id'][0], 'count': 10})
        }
    }, '/public_',{'key': 'get_goodsdetail', 'id':goods_id});
    // 获取是否收藏
    ajax_(function (r) {
            if (r['state'] === 'success'){
                vue_.has_fav = r['data'];
            }
        }, '/accountajax', {'key': 'get_is_fav', 'id': goods_id});
    // 获取评价
    get_evaluate(0);
    function get_evaluate(p) {
        ajax_(function (r) {
            if (r['state']==='success'){
                vue_.ev_data = r['data'];
            }else{
                head_vue.remind(r);
            }
        }, '/public_', {'key': 'get_evaluate', 'id': goods_id, 'p': p, 'count': 15})
    }
});