package cn.phyer.shop.ajax;

import cn.phyer.shop.dao.MerchantDao;
import cn.phyer.shop.dao.OrderDao;
import cn.phyer.shop.entity.GoodsEntity;
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
import java.util.Arrays;
import java.util.List;

import static cn.phyer.shop.plugin.Tool.save_img;

@MultipartConfig
@WebServlet(name = "merchant")
public class merchant extends HttpServlet {
    private String real_path, context_path;
    private MerchantDao merchantDao;
    private OrderDao orderDao;

    @Override
    public void init() throws ServletException {
        super.init();
        real_path = getServletContext().getRealPath("/").replace("\\", "/").replaceFirst("/$", "");
        context_path = getServletContext().getContextPath();
        WebApplicationContext app = WebApplicationContextUtils.getWebApplicationContext(getServletConfig().getServletContext());
        merchantDao = (MerchantDao) app.getBean("merchantDao");
        orderDao = (OrderDao) app.getBean("orderDao");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String key = request.getParameter("key");
        String id = request.getParameter("id");

        Session sql_session = orderDao.start();
        HttpSession session = request.getSession();
        int m_id = (int) session.getAttribute("login_id");
        Object login_tp = session.getAttribute("login_tp");
        if (login_tp == null || !session.getAttribute("login_tp").equals("merchant")){
            out.print("{\"state\": \"error\", \"msg\": \"无权限或未登录\"}");
            return;
        }
        int login_id = (int) session.getAttribute("login_id");
        JSONObject print = new JSONObject();
        switch (key){
            case "up_goods":
                try {
                    Part cover = request.getPart("cover");
                    if (cover.getSize()/(1024*1024) > 5){
                        print.put("state", "error");
                        print.put("msg", "图片需小于5M");
                        return;
                    }
                    String title = request.getParameter("title");
                    String dcb = request.getParameter("dcb");
                    String ps = request.getParameter("ps");
                    String cates = request.getParameter("cates");
                    String price_list = request.getParameter("price_list");
                    JSONObject infos = new JSONObject();
                    infos.put("title", title);
                    infos.put("dcb", dcb);
                    infos.put("ps", ps);
                    infos.put("cates", cates);
                    infos.put("price_list", price_list);
                    if (id != null){
                        sql_session = merchantDao.start();
                        String save_path = context_path+"/static/img/goods/";
                        String img_name = "";
                        try{
                            img_name = "%d."+cover.getContentType().replaceFirst(".*?/", "");
                        }catch (NullPointerException ignore){}
                        if (id.equals("")){
                            infos.put("cover", "default.jpg");
                            GoodsEntity goods = merchantDao.upGoods(sql_session, login_id, infos);
                            img_name = String.format(img_name, goods.getId());
                            if (!img_name.equals("") && save_img(cover, real_path+save_path+img_name)){
                                goods.setCover(img_name);
                                sql_session.update(goods);
                            }
                            print.put("state", "success");
                            print.put("msg", "上架成功");
                        }else{
                            int modify_id = Integer.parseInt(id);
                            img_name = String.format(img_name, modify_id);
                            if (!img_name.equals("")){
                                save_img(cover, real_path+save_path+img_name);
                                infos.put("cover", img_name);
                            }else{
                                infos.put("cover", "");
                            }
                            merchantDao.modifyGoods(sql_session, modify_id, login_id, infos);
                            print.put("state", "success");
                            print.put("msg",  "修改成功");
                        }
                    }else{
                        print.put("state", "error");
                        print.put("msg", "缺少id");
                    }
                }catch (Exception e){
                    print.put("state", "error");
                    print.put("msg", e.getMessage());
                }
                break;
            case "get_goods":
                String t = request.getParameter("t");
                String s = request.getParameter("s");
                String p = request.getParameter("p");
                String c = request.getParameter("c");
                try{
                    int page = Integer.parseInt(p);
                    int count = Integer.parseInt(c);
                    List<GoodsEntity> result = merchantDao.get_goods_by_name(sql_session, m_id, s, page, count);
                    JSONArray goods_list = new JSONArray();
                    for (GoodsEntity goods: result){
                        goods_list.add(Arrays.asList(goods.getId(), goods.getCover(), goods.getTitle()));
                    }
                    print.put("state", "success");
                    print.put("data", goods_list);
                }catch (Exception e){
                    print.put("state", "error");
                    print.put("msg", e.getMessage());
                }
                break;
            case "del_goods":
                try{
                    JSONArray ids = JSONArray.parseArray(request.getParameter("ids"));
                    merchantDao.del_goods(sql_session, login_id, ids, real_path);
                    print.put("state", "success");
                    print.put("msg", "删除成功");
                }catch (Exception e){
                    print.put("state", "error");
                    print.put("msg", e.getMessage());
                }
                break;
            case "change_state":
                try {
                    String num = request.getParameter("num");
                    if (num != null){
                        orderDao.send_goods(sql_session, m_id, Integer.parseInt(request.getParameter("o_id")), request.getParameter("num"));
                        print.put("state", "success");
                        print.put("msg", "发货成功");
                    }else {
                        orderDao.change_state(sql_session, m_id, "merchant", Integer.parseInt(request.getParameter("o_id")), request.getParameter("s"));
                        print.put("state", "success");
                        print.put("msg", "处理成功");
                    }
                }catch (Exception e){
                    print.put("state", "error");
                    print.put("msg", e.getMessage());
                }
                break;
        }
        orderDao.submit(sql_session);
        out.print(print.toJSONString());
    }
}
