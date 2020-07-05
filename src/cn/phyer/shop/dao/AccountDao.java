package cn.phyer.shop.dao;

import cn.phyer.shop.entity.CustomerEntity;
import com.alibaba.fastjson.JSONArray;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Arrays;

@Repository("accountDao")
public class AccountDao extends AbstractDao {
    AccountDao(){
        super("CustomerEntity");
    }

    public int add_account(Session session, String name, String email, String pwd){
        CustomerEntity customer = new CustomerEntity();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPwd(pwd);
        customer.setCart("[]");
        customer.setCover("");
        customer.setFavGoods("[]");
        customer.setFavStore("[]");
        session.save(customer);
        return customer.getId();
    }

    public JSONArray get_is_fav(Session session, int id, String goods_id){
        JSONArray re = new JSONArray();
        try {
            re.addAll(Arrays.asList((Object[]) session.createSQLQuery("select json_contains(fav_goods, json_quote(?2), '$')=1,json_contains(fav_store, json_quote(?3), '$')=1 from customer where id=?1")
                    .setParameter(1, id).setParameter(2, goods_id).setParameter(3, String.valueOf((int)session.createQuery("select ownerId from GoodsEntity where id=?1").setParameter(1, Integer.parseInt(goods_id)).uniqueResult())).uniqueResult()));
        }catch (Exception e){
            e.printStackTrace();
            re.add(false);
            re.add(false);
        }
        return re;
    }

    public boolean get_is_fav_store(Session session, int id, String store_id){
        return ((Number)session.createSQLQuery("select json_contains(fav_store, json_quote(?1), '$')=1 from customer where id=?2")
                .setParameter(1, store_id).setParameter(2, id).uniqueResult()).intValue()==1;
    }

    public boolean toggle_fav(Session session, int id, String id_, String what, boolean s){
        if (((BigInteger) session.createSQLQuery("select json_contains(fav_"+what+", json_quote(?2), '$') from customer where id=?1")
                .setParameter(1, id).setParameter(2, id_).uniqueResult()).intValue() == ((s)?0:1)){
            String path="";
            if (!s){
                path = (String) session.createSQLQuery("select json_unquote(json_search(fav_"+what+", 'one', ?1)) from customer where id=?2")
                    .setParameter(1, id_).setParameter(2, id).uniqueResult();
            }
            session.createQuery("update "+(what.equals("goods")?"Goods":"Merchant")+"Entity set favNum=favNum+?1 where id=?2")
                    .setParameter(1, (s?1:-1)).setParameter(2, Integer.parseInt(id_)).executeUpdate();
            String mid_s = (s)? "json_array_append(fav_"+what+", '$', ?1)":"json_remove(fav_"+what+", ?1)";
            return (session.createSQLQuery("update customer set fav_"+what+"=" + mid_s + " where id=?2")
                    .setParameter(1, (s) ? id_ : path).setParameter(2, id).executeUpdate() == 1) == s;
        }
        return false;
    }

    public void add_cart(Session session, int id, int goods_id, String k, double price){
        Object[] info = (Object[]) session.createQuery("select title,cover from GoodsEntity where id=?1").setParameter(1, goods_id).uniqueResult();
        session.createQuery("update CustomerEntity set cart=json_array_append(cart, '$', json_array(?2, ?3, ?4, ?5, 1, ?6, 1)) where id=?1")
                .setParameter(1, id).setParameter(2, goods_id).setParameter(3, info[0]).setParameter(4, k)
                .setParameter(5, price).setParameter(6, info[1]).executeUpdate();
    }

    public void modify_cart(Session session, int id, String cart){
        session.createQuery("update CustomerEntity set cart=?2 where id=?1").setParameter(1, id).setParameter(2, cart).executeUpdate();
    }

    public JSONArray get_cart(Session session, int id){
        return JSONArray.parseArray((String) session.createQuery("select cart from CustomerEntity where id=?1")
                .setParameter(1, id).uniqueResult());
    }

    public JSONArray get_fav(Session session, int id, String what, int p, int count){
        JSONArray re = new JSONArray();
        String column = "fav_"+what;
        String table = what.equals("goods")?"Goods":"Merchant";
        String name = what.equals("goods")?"title":"name";
        re.add(session.createSQLQuery("select json_length("+column+") from customer where id=?1").setParameter(1, id).uniqueResult());
        JSONArray list;
        try {
            list = JSONArray.parseArray((String) session.createSQLQuery("select json_extract(" + column + ", '$[" + p * count + " to " + p * count + count + "]') from customer where id=?1").setParameter(1, id).uniqueResult());
            for (int i = 0; i < list.size(); i++) {
                list.set(i, session.createQuery("select id," + name + ",cover from " + table + "Entity where id=?1").setParameter(1, list.getInteger(i)).uniqueResult());
            }
        }catch (NullPointerException ignore){
            list = new JSONArray();
        }
        re.add(list);
        return re;
    }

    public void del_fav(Session session, int id, String what, String ids) {
        what = (what.substring(0, 1).toUpperCase()+what.substring(1));
        JSONArray old_ids = JSONArray.parseArray((String) session.createQuery("select fav"+what+" from CustomerEntity where id=?1").setParameter(1, id).uniqueResult());
        for (Object i: JSONArray.parseArray(ids)){
            old_ids.remove(i);
        }
        session.createQuery("update CustomerEntity set fav"+what+"=?1 where id=?2").setParameter(1, old_ids.toJSONString()).setParameter(2, id).executeUpdate();
    }
}
