<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="static cn.phyer.shop.plugin.Tool.all_cate" %>
<%@ page import="com.alibaba.fastjson.JSONObject" %>
<%@ page import="com.alibaba.fastjson.JSONArray" %>
<%@ page import="cn.phyer.shop.entity.GoodsEntity" %>
<%@ page import="cn.phyer.shop.dao.GoodsDao" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.hibernate.Session" %>
<%!
    private GoodsDao goodsDao;
    public void jspInit(){
        goodsDao = (GoodsDao) WebApplicationContextUtils.getWebApplicationContext(getServletConfig().getServletContext()).getBean("goodsDao");
    }
%>
<%
    String m_id_para = request.getParameter("m_id");
    boolean is_modify = m_id_para != null;
    GoodsEntity goods = null;
    try{
        int m_id = Integer.parseInt(m_id_para);
        Session sql_session = goodsDao.start();
        goods = goodsDao.get_goods_for_merchant(sql_session, m_id, (int) request.getSession().getAttribute("login_id"));
        if (goods == null){
            out.print("<b>商品不存在或您无权访问!</b>");
            return;
        };
        goodsDao.submit(sql_session);
    }catch (NumberFormatException e){
        if (is_modify){
            out.print("<b>商品ID是数字!</b>");
            return;
        }
    }catch (Exception e){
        e.printStackTrace();
        is_modify = false;
    }
%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/i_pages/merchant/up.css"/>
<div id="i-merchant-up">
    <div class="head">
        <div>
            <input v-model.lazy="search_mid" @keyup.enter="go_search" placeholder="搜索商品id">
            <span @click="go_search"></span>
        </div>
    </div>
    <div class="body">
        <div class="cover">
            <b>封面</b>
            <input @change="choose_img" type="file" accept="image/*"/>
            <span @drop.prevent="drop_in" @dragover.prevent @dragenter="draged=true" @dragleave="draged=false" :class="{showed: chosen,dragin: draged}" onclick="this.previousElementSibling.click()">
                <img src="<%= (is_modify)? (request.getContextPath()+"/static/img/goods/"+goods.getCover()):"" %>" alt="cover"/>
                <span>{{ (draged)?'松开鼠标':'选择封面' }}</span>
            </span>
        </div>
        <div class="title">
            <b>标题</b>
            <textarea v-model="title" maxlength="32" placeholder="32字以内"></textarea>
        </div>
        <div class="cate">
            <b>分类</b>
            <div>
                <span v-for="cate in cates">{{ cate }}</span>
                <span @click="slide_cate" class="modify"></span>
                <div class="choose-cate">
                    <% for (Object row: all_cate){
                        JSONObject r = (JSONObject) row;
                        String key = (String) r.keySet().toArray()[0];
                        JSONArray cates = (JSONArray) r.get(key); %>
                    <div>
                        <b><%= key %></b>
                        <div>
                            <% for(Object cate: cates){ %>
                            <a @click="toggle_cate" :class="{chosen: cal_at_cates('<%= cate %>')}"><%= cate %></a>
                            <% } %>
                        </div>
                    </div>
                    <% } %>
                </div>
            </div>
        </div>
        <div class="price">
            <b>价格</b>
            <div>
                <ul>
                    <li v-for="(item,idx1) in price_list" :key="idx1" :data-idx="idx1">
                        <div class="std">
                            <textarea v-model="item['name']"></textarea>
                            <span class="close" @click="del_price"></span>
                        </div>
                        <div class="move">
                            <button @click="to_up"></button>
                            <button @click="to_bottom"></button>
                        </div>
                        <div class="label">
                            <label v-for="(std,idx2) in item['stds']" :key="idx2" :data-idx="idx2">
                                <input v-model="item['stds'][idx2]"/>
                                <span class="close" @click="del_standard"></span>
                            </label>
                            <button @click="add_standard"></button>
                        </div>
                    </li>
                </ul>
                <div class="btn">
                    <button @click="add_price">增加规格</button>
                    <button @click="show_set_ps">设置库存和价格</button>
                </div>
                <transition name="fade">
                    <div v-show="set_ps" class="set-ps fixed-div">
                        <div class="wrap" @click.self="hide_set_ps">
                            <div class="main">
                                <div class="body">
                                    <table>
                                        <thead>
                                            <tr>
                                                <td>套餐</td>
                                                <td>价格</td>
                                                <td>库存</td>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr onclick="if(event.target.tagName.toLowerCase()!=='input')$(this).toggleClass('active')" v-for="item in Object.keys(ps_list)" :key="item" :data-key="item">
                                                <td class="name" v-html="cal_ps_name(item)"></td>
                                                <td><input v-model.number="ps_list[item][0]" type="number"/></td>
                                                <td><input v-model.number="ps_list[item][1]" type="number"/></td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <div class="foot">
                                    <div>
                                        <span>价格:</span>
                                        <input v-model.number="set_ps_price" type="number">
                                    </div>
                                    <div>
                                        <span>库存:</span>
                                        <input v-model.number="set_ps_stock" type="number">
                                    </div>
                                    <button @click="set_choose_ps">一键设置</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </transition>
            </div>
        </div>
        <div class="dcb">
            <b>描述</b>
            <textarea v-model="dcb"></textarea>
        </div>
    </div>
    <div class="submit">
        <button @click="submit_"><span :style="{left: cal_upload_percent}"></span><a data-o="<%= (is_modify)? "修改":"上架" %>"><%= (is_modify)? "修改":"上架" %></a></button>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        let cover_blob = "";
        let vue_ = new Vue({
            el: "#i-merchant-up",
            data: {
                draged: false,
                chosen: <%= is_modify %>,
                search_mid: '',
                title: '<%= (is_modify)? goods.getTitle():"" %>',
                cates: <%= (is_modify)? goods.getCate():"[]" %>,
                dcb: '<%= (is_modify)? goods.getDcb():"" %>',
                price_list: <%= (is_modify)? goods.getPrice():"[{'name': '规格名', 'stds': ['选项名']}]" %>,
                ps_list: <%= (is_modify)? goods.getPs():"{'0': [0, 0]}" %>,
                set_ps: false,
                set_ps_price: 0,
                set_ps_stock: 0,
                upload_percent: 0,
            },
            watch: {
                price_list: {
                    handler() {
                        vue_.ps_list = {};
                        for (let i of vue_.price_list){
                            vue_.ps_list = cal_ps_list(vue_.ps_list, i['stds'].length)
                        }
                    },
                    deep: true
                }
            },
            computed: {
                cal_upload_percent: function () {
                    return parseInt((this.upload_percent-1)*100)+'%';
                }
            },
            methods: {
                go_search: function(){
                    if (this.search_mid !== ''){
                        location.href = '?locate=up&m_id='+this.search_mid;
                    }
                },
                cal_at_cates: function (e) {
                    return this.cates.indexOf(e)!==-1;
                },
                drop_in: function(e){
                    this.draged = false;
                    let file = e.dataTransfer.files[0];
                    if (file.type.search('image/')===0) {
                        this.handle_img(file)
                    }
                },
                choose_img: function(){
                    let file = this.$el.querySelector('.body > .cover > input').files[0];
                    this.handle_img(file)
                },
                handle_img: function(file){
                    if (typeof file!=='undefined') {
                        if (file.size / (1024 ** 2) < 5) {
                            let vue_ = this;
                            cover_blob = file;
                            let img_span = $(this.$el.querySelector('.body > .cover > span'));
                            let img = img_span.find('img');
                            let reader = new FileReader();
                            reader.readAsDataURL(file);
                            reader.onload = function (r) {
                                vue_.chosen = true;
                                img.attr('src', r.target.result);
                            }
                        } else {
                            head_vue.remind({'state': 'error', 'msg': '图片大小限制5M以内!'});
                        }
                    }
                },
                slide_cate: function(e){
                    let span = $(e.target);
                    let cate_div = span.siblings('.choose-cate');
                    if (span.hasClass('active')) {
                        cate_div.stop().slideUp(100, 'linear', function () {
                            span.removeClass('active');
                        });
                    }else{
                        cate_div.stop().slideDown(100, 'linear', function () {
                            span.addClass('active');
                            win_blur(cate_div[0], function (el) {
                                $(el).stop().slideUp(100, 'linear', function () {
                                    $(el).siblings('span').removeClass('active');
                                });
                            })
                        });
                    }
                },
                toggle_cate: function (e) {
                    let a = $(e.target);
                    if (a.hasClass('chosen')){
                        this.cates.splice(this.cates.indexOf(a.text()), 1);
                    }else{
                        if (this.cates.length === 3){
                            head_vue.remind({'state': 'error', 'msg': '分类--最多选择3个分类!'});
                        }else{
                            this.cates.push(a.text());
                        }
                    }
                },
                add_price: function () {
                    this.price_list.push({
                        'name': '规格名',
                        'stds': ['选项名']
                    });
                },
                del_price: function(e){
                    if (this.price_list.length < 2){
                        head_vue.remind({'state': 'error', 'msg': '价格--至少一个规格!'});
                        return
                    }
                    this.price_list.splice(parseInt($(e.target).parents('li').attr('data-idx')), 1);
                },
                to_up: function(e){
                    let btn = e.target;
                    let idx = parseInt($(btn).parents('li').attr('data-idx'));
                    if (idx-1 >= 0){
                        let pre = this.price_list[idx];
                        let next = this.price_list[idx-1];
                        this.price_list.splice(idx-1, 2, pre, next);
                    }
                },
                to_bottom: function(e){
                    let btn = e.target;
                    let idx = parseInt($(btn).parents('li').attr('data-idx'));
                    if (idx+1 < this.price_list.length){
                        let pre = this.price_list[idx+1];
                        let next = this.price_list[idx];
                        this.price_list.splice(idx, 2, pre, next);
                    }
                },
                add_standard: function (e) {
                    let btn = e.target;
                    this.price_list[parseInt($(btn).parents('li').attr('data-idx'))]['stds'].push('选项名');
                },
                del_standard: function (e) {
                    let lis = this.price_list[parseInt($(e.target).parents('li').attr('data-idx'))]['stds'];
                    if (lis.length < 2){
                        head_vue.remind({'state': 'error', 'msg': '价格--至少一个选项!'});
                        return
                    }
                    lis.splice(parseInt($(e.target).parents('label').attr('data-idx')), 1);
                },
                show_set_ps: function(){
                    this.set_ps = true;
                },
                hide_set_ps: function(){
                    this.set_ps = false;
                },
                set_choose_ps: function(e){
                    $(e.target).parents('.main').find('tr.active').each(function () {
                        vue_.ps_list[this.getAttribute('data-key')] = [vue_.set_ps_price, vue_.set_ps_stock];
                    })
                },
                cal_ps_name: function(num){
                    let s='';
                    for (let i=0;i<num.length;i++){
                        s += '<span>'+this.price_list[i]['stds'][num[i]]+'</span>';
                    }
                    return s;
                },
                submit_: function () {
                    let vue_ = this;
                    if (this.title.length===0){
                        head_vue.remind({'state': 'error', 'msg': '标题不能为空!'})
                    }else if (this.cates.length===0){
                        head_vue.remind({'state': 'error', 'msg': '分类不能为空!'})
                    }else if (this.dcb.length===0){
                        head_vue.remind({'state': 'error', 'msg': '描述不能为空!'})
                    }else if(this.check_std_dup()){
                        let btn = vue_.$el.querySelector('.submit > button');
                        let btn_a = btn.querySelector('a');
                        let data = new FormData();
                        data.append('key', 'up_goods');
                        data.append('id', '<%= (is_modify)?m_id_para:"" %>');
                        data.append('cover', cover_blob);
                        data.append('title', this.title);
                        data.append('cates', JSON.stringify(vue_.cates));
                        data.append('dcb', this.dcb);
                        data.append('price_list', JSON.stringify(vue_.price_list));
                        data.append('ps', JSON.stringify(vue_.ps_list));
                        btn.setAttribute('disabled', '');
                        btn_a.innerText = '上传中...';
                        ajax_(function (r) {
                                head_vue.remind(r);
                            }, '/merchantajax', data, {xhr: function (){return listen_upload_progress( function (e) {
                                let percent = e.loaded/e.total;
                                vue_.upload_percent = percent;
                                if (percent === 1){
                                    btn_a.innerText = '处理中...';
                                }
                            })}, complete: function(){
                                btn.removeAttribute('disabled');
                                btn_a.innerText = btn_a.getAttribute('data-o');
                                vue_.upload_percent = 0;
                            }, processData: false, contentType: false}
                        )
                    }
                },
                check_std_dup: function () {
                    let std_list=[];
                    for (let std of this.price_list) {
                        if (std_list.indexOf(std['name']) !== -1) {
                            head_vue.remind({'state': 'error', 'msg': '价格--不能有重复的规格名!'});
                            return false
                        }
                        std_list.push(std['name']);
                        let stds = std['stds'];
                        if (stds.length !== new Set(stds).size){
                            head_vue.remind({'state': 'error', 'msg': '价格--不能有重复的选项名!'});
                        }
                    }
                    return true
                }
            }
        });
        // 求规格排列
        function cal_ps_list(list, stds) {
            let new_list = {};
            if (Object.keys(list).length === 0){
                for (let i=0;i<stds;i++){
                    new_list[i.toString()] = [0, 0];
                }
            }else{
                for (let i of Object.keys(list)){
                    for (let j=0;j<stds;j++){
                        new_list[i+j.toString()] = [0, 0];
                    }
                }
            }
            return new_list
        }
    });
</script>