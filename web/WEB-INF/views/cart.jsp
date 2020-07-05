<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<html>
<head>
    <title>购物车</title>
    <jsp:include page="/WEB-INF/views/plugin/header.jsp" />
    <script src="${pageContext.request.contextPath}/static/js/cart.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/cart.css"/>
</head>
<body>
    <jsp:include page="/WEB-INF/views/plugin/head.jsp"/>
    <div id="container">
        <div id="cart">
            <ul id="cart-ul">
                <li class="head">
                    <a class="cover">封面</a>
                    <span class="name-std">商品</span>
                    <div class="num">数量</div>
                    <span class="price">价格</span>
                    <label class="choose">选择</label>
                </li>
                <li v-for="(item,idx1) in cart_goods" :key="idx1" :class="{active: choose_cart[idx1]}">
                    <img class="cover" :src="path+'/static/img/goods/'+item[5]" alt="cover">
                    <div class="name-std">
                        <a target="_blank" :href="path+'/goods/'+item[0]">{{ item[1] }}</a>
                        <span>{{ item[2] }}</span>
                    </div>
                    <div class="num">
                        <input v-model.number="item[4]" type="number">
                    </div>
                    <span class="price price-yuan">
                        {{ item[3]*item[4] }}
                    </span>
                    <label class="choose">
                        <b :class="{active: choose_cart[idx1]}"></b>
                        <input v-model="choose_cart[idx1]" type="checkbox"/>
                    </label>
                </li>
            </ul>
            <div id="cart-submit">
                <label>
                    <a>全选</a>
                    <b :class="{active: choose_cart.indexOf(false)==-1}"></b>
                    <input @change="change_select_all" type="checkbox"/>
                </label>
                <button class="del" @click="del" :disabled="has_select">删除</button>
                <b class="price-yuan">{{ cal_price }}</b>
                <button class="submit" @click="check_" :disabled="has_select">结算</button>
            </div>
        </div>
        <transition name="fade">
            <div v-show="go_check" id="submit-order" class="fixed-div">
                <div class="wrap" @click.self="hide_check">
                    <div>
                        <div class="head">确认订单信息</div>
                        <div class="body">
                            <div class="addr">
                                <div class="place">
                                    <div>
                                        <span>收货地址:</span>
                                        <div class="underline-input">
                                            <input v-model="addr[0]"/>
                                            <span></span>
                                        </div>
                                    </div>
                                </div>
                                <div class="people">
                                    <div>
                                        <span>收货人 :</span>
                                        <div class="underline-input">
                                            <input v-model="addr[1]"/>
                                            <span></span>
                                        </div>
                                    </div>
                                    <div>
                                        <span>收货电话:</span>
                                        <div class="underline-input">
                                            <input v-model.number="addr[2]" type="number"/>
                                            <span></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="goods">
                                <span>共<b>{{ select_count }}</b>件商品</span>
                                <ul>
                                    <li v-for="(item,idx) in cart_goods" v-if="choose_cart[idx]" :key="idx">
                                        <img :src="path+'/static/img/goods/'+item[5]" alt="cover"/>
                                        <div class="info">
                                            <a class="link" target="_blank" :href="path+'/goods/'+item[0]">{{ item[1] }}</a>
                                            <span>{{ item[2] }}</span>
                                        </div>
                                        <div class="price">
                                            <b class="price-yuan">{{ item[3]*item[4] }}</b>
                                            <span>
                                                <span>{{ item[4] }}</span>
                                                x
                                                <b class="price-yuan">{{ item[3] }}</b>
                                            </span>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                            <div class="submit">
                                <span class="price-yuan">{{ cal_price }}</span>
                                <button @click="submit_order">提交订单</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </transition>
    </div>
</body>
</html>
