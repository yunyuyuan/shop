<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!doctype html>
<html>
<head>
    <title>标题</title>
    <jsp:include page="/WEB-INF/views/plugin/header.jsp" />
    <script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_star.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/vue_plugin/vue_turnpage.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/goodsdetail.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/goodsdetail.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/vue_plugin/vue_star.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/vue_plugin/vue_turnpage.css"/>
</head>
<body>
    <jsp:include page="/WEB-INF/views/plugin/head.jsp"/>
    <div id="container">
        <div id="goods-detail">
            <div class="head">
                <div class="goods-cover white-bg">
                    <img :src="path+'/static/img/goods/'+info['cover']" alt="cover"/>
                </div>
                <div class="goods-info white-bg">
                    <b>{{ info['title'] }}</b>
                    <div class="buy-info">
                        <span>收藏<b>{{ info['fav_num'] }}</b></span>
                        <span>销量<b>{{ info['buy_num'] }}</b></span>
                        <span>评价<b>{{ ev_data[0] }}</b></span>
                    </div>
                    <div class="ps">
                        <div class="p">
                            <span>价格</span>
                            <b class="price-yuan">{{ cal_price[0] }}</b>
                        </div>
                        <div class="s">
                            <span>库存</span>
                            <b>{{ cal_price[1] }}</b>
                        </div>
                    </div>
                    <div class="choose-std">
                        <div v-for="(item,idx1) in info['price']" :key="idx1" :data-idx="idx1">
                            <span>{{ item['name'] }}</span>
                            <div>
                                <span v-for="(std,idx2) in item['stds']" @click.self="change_std" :class="{chosen: std[1]}" :key="idx2" :data-idx="idx2">
                                    {{ std[0] }}
                                    <input v-model="std[1]" type="checkbox"/>
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="submit">
                        <button @click="toggle_fav" class="fav" what="goods" :class="{has: has_fav[0]}">{{ (has_fav[0])?'取消':'' }}收藏</button>
                        <button @click="add_cart" class="cart">加入购物车</button>
                    </div>
                </div>
            </div>
            <div class="body">
                <div class="store-new white-bg">
                    <a :href="path+'/store/'+info['owner_id'][0]" target="_blank">
                        <span>{{ info['owner_id'][1] }}</span>
                    </a>
                    <div class="btns">
                        <button @click="toggle_fav" what="store" :class="{has: has_fav[1]}">{{ (has_fav[1])?'已收藏':'收藏店铺' }}</button>
                    </div>
                    <div class="hot">
                        <b>店铺热销</b>
                        <div>
                            <a v-for="item in rank_goods" :href="path+'/goods/'+item[0]" :key="item[0]" target="_blank">
                                <img :src="path+'/static/img/goods/'+item[2]" alt="cover"/>
                                <span>{{ item[1] }}</span>
                            </a>
                        </div>
                    </div>
                </div>
                <div class="detail white-bg">
                    <div class="head">
                        <span :class="{active: show_what==='dcb'}" @click="change_show('dcb')">详情</span>
                        <span :class="{active: show_what==='evaluate'}" @click="change_show('evaluate')">评价</span>
                    </div>
                    <div class="body">
                        <div class="dcb" :style="{display: (show_what==='dcb')?'flex':'none'}">
                            <span v-html="info['dcb']"></span>
                        </div>
                        <div class="evaluate" :style="{display: (show_what==='evaluate')?'flex':'none'}">
                            <div class="mark">
                                <div class="mark-star">
                                    <b>{{ info['star'] }}</b>
                                    <star ref="goods_star" :star="info['star']" :modify="'f'" :f_size="'1.2rem'"></star>
                                </div>
                            </div>
                            <div class="content">
                                <ul>
                                    <li v-for="(ev,idx) in ev_data[1]" :key="idx">
                                        <div class="head">
                                            <img :src="path+'/static/img/customer/'+ev[1]"/>
                                            <b>{{ ev[0] }}</b>
                                            <star :star="ev[4]" :modify="'f'" :f_size="'0.96rem'"></star>
                                            <span>{{ cal_time(ev[2]) }}</span>
                                        </div>
                                        <div class="body">
                                            <span>{{ (ev[5]==='')?'此用户未填写评价':ev[5] }}</span>
                                            <a>{{ ev[3] }}</a>
                                        </div>
                                    </li>
                                </ul>
                                <turn-page @change_page="change_page" :now="now_p" :count="page_count" :show_count="15" :ename="'change_page'"></turn-page>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
