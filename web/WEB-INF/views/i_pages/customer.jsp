<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
    String locate = request.getParameter("locate");
    locate = (locate != null)? locate:"fav";
%>
<div id="i-body" data-who="customer">
    <ul id="i-menu">
        <li><a class="menu-fav <%= (locate.equals("fav")?"active":"") %>" href="?locate=fav"><span>收藏</span></a></li>
        <li><a class="menu-order <%= (locate.equals("order")?"active":"") %>" href="?locate=order"><span>订单</span></a></li>
        <li><a class="menu-evaluate <%= (locate.equals("evaluate")?"active":"") %>" href="?locate=evaluate"><span>评价</span></a></li>
    </ul>
    <div id="i-main">
        <% switch (locate){
            case "fav": %>
        <jsp:include page="/WEB-INF/views/i_pages/customer/fav.jsp"/>
        <% break;
            case "order": %>
        <jsp:include page="/WEB-INF/views/i_pages/customer/order.jsp"/>
        <% break;
            case "msg": %>
        <jsp:include page="/WEB-INF/views/i_pages/customer/msg.jsp"/>
        <% break;
            case "evaluate": %>
        <jsp:include page="/WEB-INF/views/i_pages/customer/evaluate.jsp"/>
        <% break;
            }
        %>
    </div>
</div>