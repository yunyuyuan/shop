<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
    String tp = (String) request.getSession().getAttribute("login_tp");
    if (tp == null){
        response.sendRedirect("/login?url="+request.getRequestURL());
        return;
    }
%>
<!doctype html>
<html>
<head>
    <title>个人信息</title>
    <jsp:include page="/WEB-INF/views/plugin/header.jsp" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/i.css"/>
    <script src="${pageContext.request.contextPath}/static/js/i.js"></script>
</head>
<body>
    <jsp:include page="/WEB-INF/views/plugin/head.jsp"/>
    <div id="container">
        <div id="i-container">
            <div id="i-head">
                <img src="${pageContext.request.contextPath}/static/img/constant/ibg.jpg" alt="ibg"/>
                <div class="avatar" onclick="this.querySelector('input').click()" title="更换头像">
                    <img :src="cover" alt="cover"/>
                    <input @change="change_cover" type="file" accept="image/*"/>
                    <span :style="{top: (1-upload_percent)*100+'%'}"></span>
                    <a v-show="uploading">上传中</a>
                </div>
                <div class="name">
                    <input @keyup.enter="change_name" maxlength="16" v-model="user_name" />
                    <button @click="change_name">修改</button>
                </div>
                <button title="退出" class="logout" @click="logout"></button>
            </div>
            <% if (tp.equals("customer")){ %>
            <jsp:include page="/WEB-INF/views/i_pages/customer.jsp"/>
            <% }else{ %>
            <jsp:include page="/WEB-INF/views/i_pages/merchant.jsp"/>
            <% } %>
        </div>
    </div>
</body>
</html>
