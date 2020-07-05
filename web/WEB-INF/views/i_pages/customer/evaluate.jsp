<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/i_pages/customer/ev.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/vue_plugin/vue_turnpage.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/vue_plugin/vue_star.css"/>
<script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_turnpage.js"></script>
<script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_star.js"></script>
<div id="i-ev">
    <div class="head">
        <select v-model="state" @change="change_page(1)">
            <option value="all">全部</option>
            <option value="3">待评价</option>
            <option value="4">已评价</option>
        </select>
    </div>
    <div class="body">
        <span>共<b>{{ ev_num }}</b>条数据, <b>{{ page_count }}</b>页</span>
        <ul>
            <li v-for="(item,idx) in ev_data" :key="item[0]">
                <div class="head">
                    <a target="_blank" :href="path+'/store/'+item['m_id']">{{ item['m_n'] }}</a>
                    <button @click="function(e){show_ev(e,idx)}" :state="item['state']">{{ calc_btn(item['state']) }}</button>
                </div>
                <div class="body">
                    <div class="goods">
                        <a v-for="goods in item['goods']" target="_blank" :href="path+'/goods/'+goods[0]">
                            <img :src="path+'/static/img/goods/'+goods[5]">
                            <span>{{ goods[1] }}</span>
                        </a>
                    </div>
                </div>
            </li>
        </ul>
        <transition name="fade">
            <div v-show="has_show_ev" class="fixed-div">
                <div class="wrap" @click.self="hide_ev">
                    <div class="main">
                        <div class="head">
                            <b>评价</b>
                        </div>
                        <div class="body">
                            <div class="store">
                                <span>店铺评分</span>
                                <star @change_star="change_s_star" :star="s_star" :modify="'t'" :f_size="'1.1rem'" :event="'change_star'"></star>
                                <b>{{ s_star }}</b>
                            </div>
                            <div v-for="(item,idx) in ev_info[1]" class="goods">
                                <div class="info">
                                    <img :src="path+'/static/img/goods/'+item[3]">
                                    <div>
                                        <span>{{ item[1] }}</span>
                                        <span>{{ item[2] }}</span>
                                    </div>
                                </div>
                                <div class="-star">
                                    <span>评分</span>
                                    <star @change_star="change_star" :idx="idx" :star="item[4]" :modify="'t'" :f_size="'1.2rem'" :event="'change_star'"></star>
                                    <b>{{ item[4] }}</b>
                                </div>
                                <div class="txt">
                                    <textarea v-model="item[5]" placeholder="书写评价" maxlength="1024"></textarea>
                                </div>
                            </div>
                        </div>
                        <div class="submit">
                            <button @click="submit_ev">提交</button>
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
            el: '#i-ev',
            data: {
                ev_num: 0,
                ev_data: [],
                s_star: 2.5,
                ev_info: [0,[]],
                now_p: 1,
                state: 'all',
                has_show_ev: false
            },
            watch: {
            },
            computed: {
                page_count: function () {
                    return Math.ceil(this.ev_num/10)
                },
            },
            methods: {
                calc_btn: function(s){
                    return (s==='3')? '评价':'已评价';
                },
                show_ev: function(e,idx){
                    if (e.target.innerText==='评价'){
                        vue_.has_show_ev = true;
                        vue_.ev_info = [vue_.ev_data[idx]['id'], []];
                        for(let info of vue_.ev_data[idx]['goods']){
                            vue_.ev_info[1].push([info[0], info[1], info[2], info[5], 2.5, ''])
                        }
                    }
                },
                hide_ev: function(){
                    vue_.has_show_ev = false;
                },
                change_star: function(l){
                    this.ev_info[1][parseInt(l[0].getAttribute('idx'))].splice(4, 1, parseFloat((l[1]*5).toString().replace(/(\.\d)\d*/, '$1')));
                },
                change_s_star: function(l){
                    this.s_star = parseFloat((l[1]*5).toString().replace(/(\.\d)\d*/, '$1'));
                },
                submit_ev: function(e){
                    let btn = e.target;
                    btn.setAttribute('disabled', '');
                    let lis = [vue_.ev_info[0], []];
                    vue_.ev_info[1].filter(function (e) {
                        lis[1].push([e[0], e[2], e[4], e[5]])
                    });
                    ajax_(function (r) {
                        if (r['state']==='success'){
                            location.reload()
                        }else{
                            head_vue.remind(r);
                            btn.removeAttribute('disabled');
                        }
                    }, '/accountajax', {'key': 'submit_ev', 'ev': JSON.stringify(lis), 's': vue_.s_star})
                },
                change_page: function (p) {
                    search_ev(parseInt(p)-1);
                }
            }
        });
        search_ev(0);
        function search_ev(p, callback) {
            ajax_(function (r) {
                if (r['state']==='success'){
                    r['data'][1].filter(function (e) {
                        e['goods'] = JSON.parse(e['goods'])
                    });
                    [vue_.ev_num, vue_.ev_data] = r['data'];
                    if (typeof callback!='undefined'){callback()}
                }
            }, '/accountajax', {'key': 'get_ev', 'state': vue_.state, 'p': p, 'count': 10})
        }
    });
</script>