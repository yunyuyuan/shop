﻿<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!doctype html>
<html>
<head>
    <title>登录/注册</title>
    <jsp:include page="/WEB-INF/views/plugin/header.jsp" />
    <script src="${pageContext.request.contextPath}/static/js/plugin/md5.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_login.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/login.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css"/>
</head>
<body>
    <jsp:include page="/WEB-INF/views/plugin/head.jsp"/>
    <div id="container">
        <div id="login-div">
            <div id="login-head">
                <button @click='switch_' class='active'>我是顾客</button>
                <button @click='switch_'>我是商家</button>
            </div>
            <div id="login-body">
                <div :style="switch_left">
                    <login class="login-input" tp="customer"></login>
                    <login class="login-input" tp="merchant"></login>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
