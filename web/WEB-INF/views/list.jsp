<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!doctype html>
<html>
<head>
    <title>商品列表</title>
    <jsp:include page="/WEB-INF/views/plugin/header.jsp" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/list.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/vue_plugin/vue_star.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/vue_plugin/vue_turnpage.css"/>
    <script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_star.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_turnpage.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/list.js"></script>
</head>
<body>
    <jsp:include page="/WEB-INF/views/plugin/head.jsp"/>
    <div id="container">
        <div id="list">
            <div class="head">
                <select v-model="order" @change="go_search">
                    <option value="none">最新</option>
                    <option value="sell">销量高</option>
                    <option value="evaluate">评价好</option>
                </select>
            </div>
            <div class="body">
                <div class="head">
                    <div class="detail">
                        <a>{{ search_detail }}</a>
                        <span>--</span>
                        <b>{{ s }}</b>
                    </div>
                    <div class="count">
                        <b>{{ info_count }}</b>条数据，<b>{{ page_count }}</b>页
                    </div>
                </div>
                <div class="body">
                    <template v-if="is_goods">
                        <div class='goods' v-for="item in info_list" :key="item['id']">
                           <span>销量&nbsp;&nbsp;{{ item['buy_num'] }}</span>
                           <a :href="path+'/goods/'+item['id']">
                               <img :src="path+'/static/img/goods/'+item['cover']" alt='封面'/>
                               <span>{{ item['title'] }}</span>
                           </a>
                           <div class='mid'>
                               <span class='price-yuan'>{{ JSON.parse(item['ps'])[0] }}</span>
                               <star :star="item['star']" :modify="'f'" :f_size="'1rem'"></star>
                           </div>
                           <div class='bottom'>
                               <a :href="path+'/store/'+item['owner_id'][0]">{{ item['owner_id'][1] }}</a>
                           </div>
                        </div>
                    </template>
                    <template v-else>
                        <div class='store' v-for="item in info_list" :key="item['id']">
                           <a :href="path+'/store/'+item['id']"><img :src="path+'/static/img/store/'+item['cover']" alt='cover'/></a>
                           <div>
                               <a :href="path+'/store/'+item['id']">{{ item['name_'] }}</a>
                               <span>销量:<span>{{ item['sell_num'] }}</span></span>
                               <star :star="item['star']" :modify="'f'" :f_size="'1rem'"></star>
                           </div>
                        </div>
                    </template>
                </div>
            </div>
            <div class="bottom">
                <turn-page @page_event="change_page" :ename="'page_event'" :ref="'turn_page'" :now="page_now" :count="page_count" :show_count="8"></turn-page>
            </div>
        </div>
    </div>
</body>
</html>