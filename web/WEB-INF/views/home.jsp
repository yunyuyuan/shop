<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>主页</title>
    <jsp:include page="/WEB-INF/views/plugin/header.jsp" />
    <script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_star.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/home.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/vue_plugin/vue_star.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/home.css"/>
</head>
<body>
    <jsp:include page="/WEB-INF/views/plugin/head.jsp"/>
    <div id="container">
        <div id="home">
            <div class="head">
                <img :src="path+'/static/img/constant/home.jpg'"/>
            </div>
            <div class="body">
                <div v-for="d in data_lis">
                    <span>{{ d[0] }}</span>
                    <div>
                        <div class='goods' v-for="item in d[1]" :key="item['id']">
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
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
