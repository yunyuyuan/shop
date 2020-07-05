<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/i_pages/customer/order.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/vue_plugin/vue_turnpage.css"/>
<script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_turnpage.js"></script>
<div id="i-order">
    <div class="head">
        <select v-model="state" @change="change_page(1)">
            <option value="all">全部</option>
            <option value="0">待付款</option>
            <option value="1">已付款</option>
            <option value="2">已发货</option>
            <option value="3">已完成</option>
            <option value="4">已评价</option>
            <option value="-1">已取消</option>
        </select>
    </div>
    <div class="body">
        <span class="count">共<b>{{ order_num }}</b>条数据,<b>{{ page_count }}</b>页</span>
        <ul>
            <li v-for="(item,idx) in order_data" :key="item['id']">
                <div @click.self="show_detail(idx)" class="head">
                    <a class="customer" target="_blank" :href="path+'/customer/'+item['c_id']">{{ item['c_n'] }}</a>
                    <b :s="item['state']" class="state-color">{{ calc_state(item['state']) }}</b>
                    <button :o_id="item['id']" @click="handle_order" v-for="btn_txt in calc_btn(item['state'])">{{ btn_txt }}</button>
                </div>
                <div class="goods" v-for="row in item['goods']" :class="{invalid: row[6]!=1}">
                    <img :src="path+'/static/img/goods/'+row[5]">
                    <div class="name">
                        <a target="_blank" :href="path+'/goods/'+row[0]">{{ row[1] }}</a>
                        <span>{{ row[2] }}</span>
                    </div>
                    <div class="price">
                        <b>{{ row[3]*row[4] }}</b>
                        <span><span>{{ row[4] }}</span>x<span>{{ row[3] }}</span></span>
                    </div>
                </div>
                <div class="foot">
                    <span class="price">总价格:<b>{{ item['price'] }}</b></span>
                </div>
            </li>
        </ul>
        <transition name="fade">
            <div v-for="detail in order_detail" v-show="detail_visible" class="order-detail fixed-div">
                <div class="wrap" @click.self="hide_check">
                    <div class="main">
                        <div class="head">
                            <b>订单信息</b>
                        </div>
                        <div class="body">
                            <div>
                                <b>状态</b>
                                <span style="font-weight: bold" class="state-color" :s="detail['state']">{{ calc_state(detail['state']) }}</span>
                            </div>
                            <div>
                                <b>收获信息</b>
                                <div class="addr">
                                    <div>
                                        <b>收货人</b>
                                        <span>{{ detail['addr'][0] }}</span>
                                    </div>
                                    <div>
                                        <b>收货电话</b>
                                        <span>{{ detail['addr'][2] }}</span>
                                    </div>
                                    <div>
                                        <b>收货地址</b>
                                        <span>{{ detail['addr'][1] }}</span>
                                    </div>
                                    <div>
                                        <b>运单号</b>
                                        <span>{{ detail['addr'][3] }}</span>
                                    </div>
                                </div>
                            </div>
                            <div>
                                <b>订单号</b>
                                <div>{{ detail['id'] }}</div>
                            </div>
                            <div>
                                <b>创建时间</b>
                                <div>{{ new Date(parseInt(detail['crt_tm'])).toLocaleString() }}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </transition>
        <turn-page @change_page="change_page" :now="now_p" :count="page_count" :show_count="8" :ename="'change_page'"></turn-page>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        let vue_ = new Vue({
            el: '#i-order',
            data: {
                order_num: 0,
                order_data: [],
                now_p: 1,
                state: 'all',
                order_detail: [],
                detail_visible: false,
            },
            watch: {
            },
            computed: {
                page_count: function () {
                    return Math.ceil(this.order_num/10)
                },
            },
            methods: {
                calc_state: function(i){
                    i = parseInt(i);
                    if (i===0){
                        return '待付款'
                    }else if (i===1){
                        return '待发货'
                    }else if (i===2){
                        return '已发货'
                    }else if (i===3){
                        return '已完成'
                    }else if (i===4){
                        return '已评价'
                    }else if (i===-1){
                        return '已取消'
                    }
                },
                calc_btn: function(i){
                    i = parseInt(i);
                    if (i===0){
                        return ['取消']
                    }else if (i===1){
                        return ['退单','发货']
                    }else if (i===2){
                        return []
                    }else{
                        return []
                    }
                },
                calc_time: function(i){
                    return new Date(parseInt(i)).toLocaleString();
                },
                show_detail: function(idx){
                    this.order_detail = [this.order_data[idx]];
                    this.detail_visible = true;
                },
                hide_check: function(){
                    this.detail_visible = false;
                },
                handle_order: function(e){
                    let t = e.target.innerText;
                    let form = {'key': 'change_state', 'o_id': e.target.getAttribute('o_id')};
                    let s = '-1';
                    if(t==='取消'&&!confirm('确认取消订单?')){
                        return
                    }else if (t==='退单'&&!confirm('确认退单并退款?')){
                        return
                    }else if (t==='发货'){
                        let ipt = prompt('填写快递单号');
                        if (ipt==='' || ipt==null){
                            return;
                        }
                        form['num'] = ipt;
                    }
                    form['s'] = s;
                    ajax_(function (r) {
                        head_vue.remind(r);
                        if (r['state']==='success'){
                            location.reload()
                        }
                    }, '/merchantajax', form)
                },
                change_page: function (p) {
                    search_order(parseInt(p)-1);
                }
            }
        });
        search_order(0);
        function search_order(p, callback) {
            ajax_(function (r) {
                if (r['state']==='success'){
                    r['data'][1].filter(function (e){
                        e['addr'] = JSON.parse(e['addr']);
                        e['goods'] = JSON.parse(e['goods']);
                    });
                    [vue_.order_num, vue_.order_data] = r['data'];
                    if (typeof callback!='undefined'){callback()}
                }
            }, '/public_', {'key': 'get_order', 'who': 'merchant', 'state': vue_.state, p: p, 'count': 10})
        }
    });
</script>