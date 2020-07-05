<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/i_pages/customer/fav.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/vue_plugin/vue_turnpage.css"/>
<script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_turnpage.js"></script>
<div id="i-fav">
    <div class="head">
        <b class="goods" :class="{active: now_locate=='goods'}" @click="toggle_locate">商品收藏</b>
        <b class="store" :class="{active: now_locate=='store'}" @click="toggle_locate">店铺收藏</b>
    </div>
    <div class="body">
        <div class="head">
            <span>共<b>{{ fav_num }}</b>条数据, <b>{{ page_count }}</b>页</span>
            <div>
                <label>
                    <b :class="{active: select_all}"></b>
                    <span>全选</span>
                    <input @change="change_select_all" type="checkbox"/>
                </label>
                <button @click="del_fav" :disabled="has_select">取消收藏</button>
            </div>
        </div>
        <ul>
            <li v-for="item in fav_data" :key="item[0]">
                <img :src="path+'/static/img/'+now_locate+'/'+item[2]" alt="cover">
                <a target="_blank" :href="path+'/'+now_locate+'/'+item[0]">{{ item[1] }}</a>
                <label>
                    <b :class="{active: item[3]}"></b>
                    <input v-model="item[3]" type="checkbox"/>
                </label>
            </li>
        </ul>
        <turn-page @change_page="change_page" :now="now_p" :count="page_count" :show_count="8" :ename="'change_page'"></turn-page>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        let vue_ = new Vue({
            el: '#i-fav',
            data: {
                now_locate: 'goods',
                fav_num: 0,
                fav_data: [],
                now_p: 1,
                select_all: false
            },
            watch: {
                fav_data: {
                    handler(){
                        let is_select_all = true;
                        if (this.fav_data.length===0) {
                            is_select_all = false;
                        }else{
                            this.fav_data.filter(function (e) {
                                if (!e[3]){
                                    is_select_all = false
                                }
                            });
                        }
                        this.select_all = is_select_all;
                        this.$el.querySelector('.head input[type=checkbox]').checked = is_select_all;
                    },
                    deep: true
                }
            },
            computed: {
                page_count: function () {
                    return Math.ceil(this.fav_num / 10)
                },
                has_select: function () {
                    let has_select = false;
                    this.fav_data.filter(function (e) {
                        if (e[3]) has_select = true;
                    });
                    return !has_select
                }
            },
            methods: {
                toggle_locate: function (e) {
                    let btn = e.target;
                    let s = (btn.classList.contains('goods')? 'goods':'store');
                    search_fav(s, 0, function () {
                        vue_.now_locate = s
                    });
                },
                change_select_all: function(e){
                    let checked = e.target.checked;
                    this.fav_data.filter(function (e) {
                        e[3] = checked;
                    });
                    this.fav_data.splice(0, 0);
                    this.select_all = checked
                },
                change_page: function (p) {
                    search_fav(vue_.now_locate, parseInt(p)-1);
                },
                del_fav: function () {
                    let ids = [];
                    vue_.fav_data.filter(function (e) {
                        if (e[3]){
                            ids.push(e[0].toString())
                        }
                    });
                    ajax_(function (r) {
                        head_vue.remind(r);
                        if (r['state']==='success'){
                            setTimeout(function () {
                                location.reload()
                            }, 500)
                        }
                    }, '/accountajax', {'key': 'del_fav', 'what': vue_.now_locate, 'ids': JSON.stringify(ids)})
                }
            }
        });
        search_fav('goods', 0);
        function search_fav(what, p, callback) {
            ajax_(function (r) {
                if (r['state']==='success'){
                    r['data'][1].filter(function (e) {
                        e.push(false)
                    });
                    [vue_.fav_num, vue_.fav_data] = r['data'];
                    if (typeof callback!='undefined'){callback()}
                }
            }, '/accountajax', {'key': 'get_fav', 'what': what, 'p': p, 'count': 10})
        }
    });
</script>