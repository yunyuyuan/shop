<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
    String locate = request.getParameter("locate");
    locate = (locate != null)? locate:"up";
%>
<div id="i-body" data-who="store">
    <ul id="i-menu">
        <li><a class="menu-up <%= (locate.equals("up")?"active":"") %>" href="?locate=up"><span>上架/修改</span></a></li>
        <li><a class="menu-goods-menu <%= (locate.equals("goods")?"active":"") %>" href="?locate=goods"><span>商品列表</span></a></li>
        <li><a class="menu-order <%= (locate.equals("order")?"active":"") %>" href="?locate=order"><span>客户订单</span></a></li>
    </ul>
    <div id="i-main">
        <% switch (locate){
            case "up": %>
        <jsp:include page="/WEB-INF/views/i_pages/merchant/up.jsp"/>
        <% break;
            case "order": %>
        <jsp:include page="/WEB-INF/views/i_pages/merchant/order.jsp"/>
        <% break;
            case "msg": %>
        <jsp:include page="/WEB-INF/views/i_pages/merchant/msg.jsp"/>
        <% break;
            case "goods": %>
        <jsp:include page="/WEB-INF/views/i_pages/merchant/goods.jsp"/>
        <% break;
        }
        %>
    </div>
</div>