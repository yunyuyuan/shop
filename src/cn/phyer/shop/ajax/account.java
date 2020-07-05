package cn.phyer.shop.ajax;

import cn.phyer.shop.dao.AccountDao;
import cn.phyer.shop.dao.OrderDao;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

@WebServlet(name = "account")
public class account extends HttpServlet {
    @Autowired
    private AccountDao accountDao;
    private OrderDao orderDao;

    @Override
    public void init() throws ServletException {
        super.init();
        WebApplicationContext app = WebApplicationContextUtils.getWebApplicationContext(getServletConfig().getServletContext());
        accountDao = (AccountDao) app.getBean("accountDao");
        orderDao = (OrderDao) app.getBean("orderDao");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        String key = Objects.requireNonNullElse(request.getParameter("key"), "");
        int para_id = Integer.parseInt(Objects.requireNonNullElse(request.getParameter("id"), "-1"));

        HttpSession session = request.getSession();
        int id = (int) Objects.requireNonNullElse(session.getAttribute("login_id"), -1);
        String tp = (String) Objects.requireNonNullElse(session.getAttribute("login_tp"), "");
        JSONObject print = new JSONObject();
        int p;
        int count;
        if (tp.equals("customer")){
            Session sql_session = accountDao.start();
            String what;
            switch (key){
                case "get_is_fav":
                    try {
                        print.put("state", "success");
                        print.put("data", accountDao.get_is_fav(sql_session, id, String.valueOf(para_id)));
                    }catch (Exception e){
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                    break;
                case "get_is_fav_store":
                    try{
                        print.put("state", "success");
                        print.put("msg", accountDao.get_is_fav_store(sql_session, id, String.valueOf(para_id)));
                    }catch (Exception e){
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                    break;
                case "toggle_fav":
                    try{
                        boolean toggle = Boolean.parseBoolean(Objects.requireNonNullElse(request.getParameter("s"), "true"));
                        print.put("state", "success");
                        print.put("msg", accountDao.toggle_fav(sql_session, id, String.valueOf(para_id), request.getParameter("what"), toggle));
                    }catch (Exception e){
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                    break;
                case "add_cart":
                    try {
                        accountDao.add_cart(sql_session, id, para_id, Objects.requireNonNullElse(request.getParameter("k"), ""), Double.parseDouble(request.getParameter("p")));
                        print.put("state", "success");
                        print.put("msg", "加入购物车成功");
                    }catch (Exception e){
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                    break;
                case "modify_cart":
                    try {
                        accountDao.modify_cart(sql_session, id, Objects.requireNonNullElse(request.getParameter("cart"), "[]"));
                        print.put("state", "success");
                        print.put("msg", "自动更新购物车成功");
                    }catch (Exception e){
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                    break;
                case "get_cart":
                    try {
                        print.put("state", "success");
                        print.put("data", accountDao.get_cart(sql_session, id));
                    }catch (Exception e){
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                    break;
                case "submit_order":
                    try{
                        orderDao.add_order(sql_session, id, request.getParameter("addr"), request.getParameter("cart"));
                        print.put("state", "success");
                        print.put("msg", "提交成功");
                    }catch (Exception e){
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                    break;
                case "get_fav":
                    try {
                        what = request.getParameter("what");
                        p = Integer.parseInt(request.getParameter("p"));
                        count = Integer.parseInt(request.getParameter("count"));
                        print.put("state", "success");
                        print.put("data", accountDao.get_fav(sql_session, id, what, p, count));
                    }catch (Exception e){
                        e.printStackTrace();
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                    break;
                case "del_fav":
                    try{
                        what = request.getParameter("what");
                        String ids = request.getParameter("ids");
                        accountDao.del_fav(sql_session, id, what, ids);
                        print.put("state", "success");
                        print.put("msg", "删除收藏成功");
                    }catch (Exception e){
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                    break;
                case "change_state":
                    try {
                        orderDao.change_state(sql_session, id, "customer", Integer.parseInt(request.getParameter("o_id")), request.getParameter("s"));
                        print.put("state", "success");
                    }catch (Exception e){
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                    break;
                case "get_ev":
                    try {
                        JSONArray data = orderDao.get_some_for_ev(sql_session, id, request.getParameter("state"), Integer.parseInt(request.getParameter("p")), Integer.parseInt(request.getParameter("count")));
                        print.put("state", "success");
                        print.put("data", data);
                    }catch (Exception e){
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                    break;
                case "submit_ev":
                    try {
                        orderDao.set_ev(sql_session, id, JSONArray.parseArray(request.getParameter("ev")), Double.parseDouble(request.getParameter("s")));
                        print.put("state", "success");
                    }catch (Exception e){
                        print.put("state", "error");
                        print.put("msg", e.getMessage());
                    }
                    break;
            }
            accountDao.submit(sql_session);
        }else{
            print.put("state", "error");
            print.put("msg", (tp.equals("merchant"))?" 商户无法操作":"未登录");
        }
        out.print(print.toJSONString());
    }
}
