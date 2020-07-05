$(document).ready(function () {
    let store_id = location.pathname.replace(/.*\/(\d*)/, '$1');
    let vue_ = new Vue({
        el: '#container',
        data: {
            info: {'cover': 'default.jpg','fav_num': 0,'name_': '店名','sell_num': 0,'star_num': 0,'star': 0},
            goods_list: [],
            has_fav: false,
            orient: 'time',
            search: '',
            search_num: 0,
            p: 1,
        },
        computed: {
            page_count: function () {
                return Math.ceil(this.search_num/15);
            }
        },
        methods: {
            toggle_fav: function () {
                ajax_(function (r) {
                    if (r['state']==='success'){
                        vue_.has_fav = r['msg'];
                        vue_.info['fav_num'] += (r['msg']?1:-1);
                        head_vue.remind({'state': 'success', 'msg': (r['msg']?'收藏成功':'取消收藏成功')});
                    }else if (r['msg']==='未登录'){
                        location.href = path+'/login?url='+location.href;
                    }
                }, '/accountajax', {'key': 'toggle_fav', 'id': store_id, 'what': 'store', 's': !vue_.has_fav})
            },
            do_search: function () {
                ajax_(function (r) {
                    if (r['state']==='success'){
                        vue_.search_num = r['data'][0];
                        vue_.goods_list = r['data'][1];
                    }
                }, '/public_', {'key': 'get_store_goods', 'id': store_id, 's': vue_.search, 'o': vue_.orient, 'p': vue_.p-1, 'count': 15})
            },
            change_page: function (p) {
                vue_.p = parseInt(p);
                this.do_search();
            }
        }
    });
    // 获取店铺信息
    ajax_(function(r){
        if (r['state'] === 'success'){
            vue_.info = r['data'];
            vue_.$refs['store_star'].update_percent(r['data']['star']);
        }
    }, '/public_',{'key': 'get_storedetail', 'id':store_id});
    // 获取是否收藏
    ajax_(function (r) {
        if (r['state'] === 'success'){
            vue_.has_fav = r['msg'];
        }
    }, '/accountajax', {'key': 'get_is_fav_store', 'id': store_id});
    vue_.do_search();
});