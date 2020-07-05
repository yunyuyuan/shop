<%@ page import="com.alibaba.fastjson.JSONArray" %>
<%@ page import="com.alibaba.fastjson.JSONObject" %>
<%@ page import="static cn.phyer.shop.plugin.Tool.all_cate" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_remind.js"></script>
<script src="${pageContext.request.contextPath}/static/js/plugin/head.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/plugin/head.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/vue_plugin/vue_remind.css"/>
<nav id="-head">
    <a id="-head-cate"></a>
    <div class="search">
        <select v-model="choose">
            <option value="goods" selected>商品</option>
            <option value="store">店铺</option>
        </select>
        <input @keyup.enter="go_search" :placeholder="'搜索'+cal_choose_what+'名'" maxlength="32"/>
        <span id="-head-search" @click="go_search"></span>
    </div>
    <% if (!"merchant".equals(session.getAttribute("login_tp"))){ %>
    <a :href="path+'/cart'" id="-head-cart"></a>
    <% } %>
    <a :href="path+'/i'" id="-head-account"></a>
    <ul>
        <% for (Object row: all_cate){
            JSONObject r = (JSONObject) row;
            String key = (String) r.keySet().toArray()[0];
            JSONArray cates = (JSONArray) r.get(key); %>
        <li>
            <b><%= key %></b>
            <div>
                <% for(Object cate: cates){ %>
                <a href="${pageContext.request.contextPath}/list?at=goods&t=cate&s=<%= cate %>"><%= cate %></a>
                <% } %>
            </div>
        </li>
        <% } %>
    </ul>
    <div class="hide-remind"></div>
    <remind ref="head_remind" :shown="0.3" :duration="5" :start_css="{'top': '0'}" :end_css="{'top': '4rem'}"></remind>
</nav>
