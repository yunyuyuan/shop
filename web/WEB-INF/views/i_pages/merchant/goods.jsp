<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/i_pages/merchant/goods.css"/>
<div id="i-merchant-goods">
    <div class="head">
        <div class="search">
            <input v-model.lazy="search_goods" @keyup.enter="go_search" placeholder="搜索商品名"/>
            <span @click="go_search"></span>
        </div>
        <div class="del">
            <label>
                <b :class="{active: select_all}"></b>
                <span>全选</span>
                <input @change="change_select_all" type="checkbox">
            </label>
            <button @click="del_select" :disabled="has_select">下架</button>
        </div>
    </div>
    <div class="body">
        <ul>
            <li v-for="item in goods_list" :key="item[0]" :class="{select: item[3]}">
                <a :href="path+'/goods/'+item[0]" target="_blank">
                    <img :src="path+'/static/img/goods/'+item[1]" alt="cover"/>
                    <span>{{ item[2] }}</span>
                </a>
                <div>
                    <a :href="path+'/i?locate=up&m_id='+item[0]" target="_blank"></a>
                    <label>
                        <b :class="{active: item[3]}"></b>
                        <input v-model="item[3]" type="checkbox"/>
                    </label>
                </div>
            </li>
        </ul>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        function get_goods(t, s, p, callback) {
            ajax_(function (r) {
                if (r['state'] === 'success'){
                    callback(r['data']);
                }
            },'/merchantajax', {'key': 'get_goods', 't': t, 's': s, 'p': p, 'c': 15});
        }
        let vue_ = new Vue({
            el: "#i-merchant-goods",
            data: {
                search_goods: '',
                select_all: false,
                goods_list: []
            },
            watch: {
                goods_list: {
                    handler() {
                        let is_select_all = true;
                        this.goods_list.filter(function (e) {
                            if (!e[3]) {
                                is_select_all = false;
                            }
                        });
                        this.select_all = is_select_all;
                        this.$el.querySelector('.del input[type=checkbox]').checked = is_select_all;
                    },
                    deep: true
                }
            },
            computed: {
                has_select: function () {
                    for (let e of this.goods_list){
                        if (e[3]){
                            return false
                        }
                    }
                    return true
                }
            },
            methods: {
                go_search: function () {
                    get_goods('name', this.search_goods, 0, function (data) {
                        data.filter(function (e) {
                            e.push(false);
                        });
                        vue_.goods_list = data;
                    });
                },
                change_select_all: function (e) {
                    let checked = e.target.checked;
                    this.select_all = checked;
                    this.goods_list.filter(function (e) {
                        e[3] = checked;
                    });
                    this.goods_list.splice(0, 0);
                },
                del_select: function (e) {
                    let btn = e.target;
                    let down_id = [];
                    this.goods_list.filter(function (e) {
                        if (e[3]){
                            down_id.push(e[0])
                        }
                    });
                    if (confirm('是否下架这些物品?操作不可撤销!')){
                        btn.setAttribute('disabled', '');
                        ajax_(function (r) {
                            head_vue.remind(r);
                            btn.removeAttribute('disabled');
                            if (r['state'] === 'success'){
                                setTimeout(function () {
                                    location.reload()
                                }, 500)
                            }
                        },'/merchantajax',{'key': 'del_goods', 'ids': JSON.stringify(down_id)})
                    }
                }
            }
        });
        vue_.go_search()
    })
</script>