package cn.phyer.shop.ajax;

import cn.phyer.shop.dao.*;
import cn.phyer.shop.plugin.MailSender;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.Session;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static cn.phyer.shop.plugin.Tool.random_code;

@MultipartConfig
@WebServlet(name = "public_")
public class public_ extends HttpServlet {
    private String real_path;
    private AccountDao accountDao;
    private OrderDao orderDao;
    private MerchantDao merchantDao;
    private GoodsDao goodsDao;

    @Override
    public void init() throws ServletException {
        super.init();
        real_path = getServletContext().getRealPath("/").replace("\\", "/").replaceFirst("/$", "");
        WebApplicationContext app = WebApplicationContextUtils.getWebApplicationContext(getServletConfig().getServletContext());
        accountDao = (AccountDao) app.getBean("accountDao");
        orderDao = (OrderDao) app.getBean("orderDao");
        merchantDao = (MerchantDao) app.getBean("merchantDao");
        goodsDao = (GoodsDao) app.getBean("goodsDao");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        String key = Objects.requireNonNullElse(request.getParameter("key"), "");
        String tp = request.getParameter("tp");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String pwd = request.getParameter("pwd");
        String p = request.getParameter("p");

        AbstractDao dao;
        HttpSession session = request.getSession();
        JSONObject print = new JSONObject();
        int login_id;
        int count;
        Session sql_session = accountDao.start();
        switch (key){
            // 账户
            case "login":
                dao = (tp.equals("customer"))? accountDao: merchantDao;
                login_id = dao.check_login(sql_session, email, pwd);
                if (login_id < 0){
                    print.put("state", "error");
                    print.put("msg", "账号或密码错误");
                }else{
                    session.setAttribute("login_tp", tp);
                    session.setAttribute("login_id", login_id);
                    print.put("state", "success");
                }
                break;
            case "logout":
                session.removeAttribute("login_tp");
                session.removeAttribute("login_id");
                print.put("state", "success");
                break;
            case "register":
                String code = request.getParameter("code");
                if (code != null && code.equals(session.getAttribute("gen_code"))){
                    dao = (tp.equals("customer"))? accountDao: merchantDao;
                    try {
                        int save_id = dao.add_account(sql_session, name, email, pwd);
                        session.setAttribute("login_tp", tp);
                        session.setAttribute("login_id", save_id);
                        print.put("state", "success");
                    }catch (Exception e){
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                }else{
                    print.put("state", "error");
                    print.put("msg", "验证码错误");
                }
                break;
            case "send_code":
                dao = (tp.equals("customer")) ? accountDao : merchantDao;
                if (dao.check_dup(sql_session, email)){
                    String gen_code = random_code();
                    new Thread(new MailSender(email, "注册", "验证码: "+gen_code)).start();
                    session.setAttribute("gen_code", gen_code);
                    print.put("state", "success");
                }else{
                    print.put("state", "error");
                    print.put("msg", "邮箱已被注册");
                }
                break;
            case "change_name":
                try {
                    dao = is_customer(session) ? accountDao : merchantDao;
                    dao.change_name(sql_session, (int) session.getAttribute("login_id"), name);
                    print.put("state", "success");
                }catch (Exception e){
                    print.put("state", "error");
                }
                break;
            case "get_account_info":
                try {
                    dao = is_customer(session) ? accountDao : merchantDao;
                    print.put("data", dao.get_info_for_i(sql_session, (int) session.getAttribute("login_id")));
                }catch (Exception e){
                    print.put("data", "[\"unknown\",\"default.jpg\"]");
                }
                break;
            case "change_cover":
                try {
                    dao = is_customer(session) ? accountDao : merchantDao;
                    Part cover_img = request.getPart("f");
                    login_id = (int) session.getAttribute("login_id");
                    String str = login_id+"."+cover_img.getContentType().replaceFirst(".*/", "");
                    dao.change_cover(sql_session, cover_img, str, real_path+"/static/img/"+(is_customer(session)? "customer":"store")+"/"+str, login_id);
                    print.put("state", "success");
                }catch (Exception e){
                    print.put("state", "error");
                }
                break;
            // 商品/店铺
            case "get_list":
                String get_what = request.getParameter("at");
                String get_type = request.getParameter("t");
                String s = URLDecoder.decode(request.getParameter("s"), StandardCharsets.UTF_8);
                String orient = request.getParameter("o");
                count = 10;
                try {
                    String count_para = request.getParameter("count");
                    try{
                        count = Integer.parseInt(count_para);
                    }catch (NumberFormatException ignore){}
                    switch (get_type) {
                        case "cate":
                            if (get_what.equals("goods")) {
                                print.put("data", goodsDao.get_some_good(sql_session, "cate", orient, s, Integer.parseInt(p), count));
                            }else{
                                print.put("data", "[0,[]]");
                            }
                            break;
                        case "search":
                            if (get_what.equals("goods")) {
                                print.put("data", goodsDao.get_some_good(sql_session, "search", orient, "%"+s+"%", Integer.parseInt(p), count));
                            }else{
                                print.put("data", merchantDao.get_some_store(sql_session, "search", orient, "%"+s+"%", Integer.parseInt(p), count));
                            }
                            break;
                    }
                    print.put("state", "success");
                }catch (Exception e){
                    print.put("state", "error");
                    print.put("msg", e.getMessage());
                    print.put("data", "[0,[]]");
                }
                break;
            case "get_goodsdetail":
                String goods_id_para = request.getParameter("id");
                try{
                    int goods_id = Integer.parseInt(goods_id_para);
                    print.put("state", "success");
                    print.put("data", goodsDao.get_goodsdetail(sql_session, goods_id));
                }catch (Exception e){
                    print.put("state", "error");
                    print.put("msg", e.getMessage());
                }
                break;
            case "get_storedetail":
                String m_id_para = request.getParameter("id");
                try{
                    int m_id = Integer.parseInt(m_id_para);
                    print.put("state", "success");
                    print.put("data", merchantDao.get_storedetail(sql_session, m_id));
                }catch (Exception e){
                    print.put("state", "error");
                    print.put("msg", e.getMessage());
                }
                break;
            case "get_evaluate":
                try{
                    int goods_id = Integer.parseInt(request.getParameter("id"));
                    print.put("state", "success");
                    print.put("data", goodsDao.get_evaluate(sql_session, goods_id, Integer.parseInt(p), Integer.parseInt(request.getParameter("count"))));
                }catch (Exception e){
                    print.put("state", "error");
                    print.put("msg", e.getMessage());
                }
                break;
            case "get_rank":
                try {
                    int merchant_id = Integer.parseInt(Objects.requireNonNullElse(request.getParameter("id"), "-1"));
                    count = Integer.parseInt(Objects.requireNonNullElse(request.getParameter("count"), "10"));
                    print.put("state", "success");
                    print.put("data", goodsDao.get_rank_by_sell(sql_session, merchant_id, count));
                }catch (Exception e){
                    print.put("state", "error");
                    print.put("msg", e.getMessage());
                }
                break;
            case "get_store_goods":
                try{
                    JSONArray goods_list = merchantDao.get_some(sql_session, Integer.parseInt(request.getParameter("id")), request.getParameter("s"), request.getParameter("o"), Integer.parseInt(request.getParameter("p")), Integer.parseInt(request.getParameter("count")));
                    print.put("state", "success");
                    print.put("data", goods_list);
                }catch (Exception e){
                    e.printStackTrace();
                    print.put("state", "error");
                    print.put("msg", e.getMessage());
                }
                break;
            case "get_order":
                try {
                    count = Integer.parseInt(request.getParameter("count"));
                    JSONArray orders = orderDao.get_some(sql_session, (int) session.getAttribute("login_id"), request.getParameter("who"), request.getParameter("state"), Integer.parseInt(request.getParameter("p")), count);
                    print.put("state", "success");
                    print.put("data", orders);
                }catch (Exception e){
                    print.put("state", "error");
                    print.put("msg", e.getMessage());
                }
                break;
            case "get_home":
                try{
                    JSONArray lis = goodsDao.get_home(sql_session);
                    print.put("state", "success");
                    print.put("data", lis);
                }catch (Exception e){
                    print.put("state", "error");
                    print.put("msg", e.getMessage());
                }
                break;
        }
        accountDao.submit(sql_session);
        out.print(print.toJSONString());
    }

    private static boolean is_customer(HttpSession session){
        return session.getAttribute("login_tp").equals("customer");
    }
}
