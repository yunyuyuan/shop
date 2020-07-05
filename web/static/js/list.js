$(document).ready(function () {
    let paras = para_to_map();
    let para_map = {
        'at': get_para_default(paras, 'at', 'goods'),
        't': get_para_default(paras, 't', 'search'),
        's': get_para_default(paras, 's', '')};
    let s = ((para_map['s']==='')? "全部":decodeURI(para_map['s']));
    let t = ((para_map['t'] === "cate")? "分类":"搜索");
    document.title = t + '--' + s;
    let vue_ = new Vue({
        el: "#container",
        data: {
            at: para_map['at'],
            page_now: 1,
            s: (para_map['s']==='')? "全部":decodeURI(para_map['s']),
            order: 'none',
            info_list: [],
            info_count: 0,
            is_goods: (para_map['at'] === "goods"),
        },
        computed: {
            search_detail: function(){
                return (this.at==='goods'?'商品':'店铺')+t;
            },
            page_count: function () {
                return Math.ceil(this.info_count / 10)
            }
        },
        methods: {
            go_search: function () {
                get_info(vue_.page_now)
            },
            change_page: get_info
        },
    });
    get_info(1);
    function get_info(p) {
        ajax_(function (r) {
                if (r['state']==='error'){
                    head_vue.remind(r);
                }
                vue_.info_count = r['data'][0];
                vue_.page_now = p;
                vue_.info_list = r['data'][1];
            }, '/public_', {
                'key': 'get_list',
                'at': vue_.at,      // goods或store
                't': para_map['t'], // cate或search
                's': para_map['s'], // 搜索关键词
                'p': p - 1,         // 页码
                'o': vue_.order,    // 排序，销量或评价
                'count': 10         // 一页的数量
            });
    }
});