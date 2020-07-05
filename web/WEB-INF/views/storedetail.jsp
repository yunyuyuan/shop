<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!doctype html>
<html>
<head>
    <title>标题</title>
    <jsp:include page="/WEB-INF/views/plugin/header.jsp" />
    <script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_star.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_turnpage.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/storedetail.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/storedetail.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/vue_plugin/vue_star.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/vue_plugin/vue_turnpage.css"/>
</head>
<body>
<jsp:include page="/WEB-INF/views/plugin/head.jsp"/>
<div id="container">
    <div id="store-detail">
        <div class="head">
            <img :src="path+'/static/img/store/'+info['cover']"/>
            <div class="detail">
                <b class="name">{{ info['name'] }}</b>
                <div class="num-info">
                    <span>总销量<b>{{ info['sell_num'] }}</b></span>
                    <span><b>{{ info['fav_num'] }}</b>人收藏</span>
                </div>
                <div class="star-info">
                    <star ref="store_star" :star="info['star']" :modify="'f'" :f_size="'1.1rem'"></star>
                    <b>{{ info['star'] }}</b>
                    <span>评分人数<b>{{ info['star_num'] }}</b></span>
                </div>
                <button :class="{faved: has_fav}" @click="toggle_fav">{{ has_fav?'取消收藏':'收藏' }}</button>
            </div>
        </div>
        <div class="body">
            <div class="head">
                <select v-model="orient" @change="do_search">
                    <option value="time">新上架</option>
                    <option value="sell">销量高</option>
                    <option value="ev">评价好</option>
                </select>
                <div class="search">
                    <input @keyup.enter="do_search" v-model.lazy="search" placeholder="搜索商品名">
                    <span @click="do_search"></span>
                </div>
                <span><b>{{ (search==''?'全部':search) }}</b>-共<b>{{ search_num }}</b>件商品</span>
            </div>
            <div class="body">
                <table>
                    <thead>
                        <tr>
                            <td>封面</td>
                            <td>标题</td>
                            <td>销量</td>
                            <td>评分</td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in goods_list" :key="item['id']">
                            <td><img :src="path+'/static/img/goods/'+item['cover']"/></td>
                            <td><a :href="path+'/goods/'+item['id']" target="_blank">{{ item['title'] }}</a></td>
                            <td>{{ item['buy_num'] }}</td>
                            <td class="star-td"><star :star="item['star']" :modify="'f'" :f_size="'0.98rem'"></star></td>
                        </tr>
                    </tbody>
                </table>
                <turn-page ref="turn_page" @change_page="change_page" :show_count="15" :now="p" :count="page_count" :ename="change_page"></turn-page>
            </div>
        </div>
    </div>
</div>
</body>
</html>
