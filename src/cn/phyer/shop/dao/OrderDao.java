package cn.phyer.shop.dao;

import cn.phyer.shop.entity.OrderEntity;
import com.alibaba.fastjson.JSONArray;
import org.hibernate.Session;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("orderDao")
public class OrderDao extends AbstractDao {
    OrderDao(){
        super("");
    }

    public JSONArray get_some(Session session, int id, String who, String state, int p, int count){
        JSONArray re = new JSONArray();
        JSONArray d = new JSONArray();
        List<Map> list;
        if (Arrays.asList("-1", "0", "1", "2", "3", "4").contains(state)){
            re.add(session.createQuery("select count(*) from OrderEntity where "+who+"Id=?1 and state=?2").setParameter(1, id).setParameter(2, state).uniqueResult());
            list = session.createSQLQuery("select id,customer_id as c_id,merchant_id as m_id,crt_tm,addr,price,goods,state from order_ where "+who+"_id=?1 and state=?2 order by id desc limit ?3,?4").setParameter(1, id).setParameter(2, state).setParameter(3, p*count).setParameter(4, count)
                    .unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        }else{
            re.add(session.createQuery("select count(*) from OrderEntity where "+who+"Id=?1").setParameter(1, id).uniqueResult());
            list = session.createSQLQuery("select id,customer_id as c_id,merchant_id as m_id,crt_tm,addr,price,goods,state from order_ where "+who+"_id=?1 order by id desc  limit ?2,?3").setParameter(1, id).setParameter(2, p * count).setParameter(3, count)
                    .unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        }
        if (who.equals("customer")) {
            for (Map o : list) {
                o.put("m_n", session.createQuery("select name from MerchantEntity where id=?1").setParameter(1, o.get("m_id")).uniqueResult());
            }
        }else{
            for (Map o : list) {
                o.put("c_n", session.createQuery("select name from CustomerEntity where id=?1").setParameter(1, o.get("c_id")).uniqueResult());
            }
        }
        d.addAll(list);
        re.add(d);
        return re;
    }

    public JSONArray get_some_for_ev(Session session, int id, String state, int p, int count){
        JSONArray re = new JSONArray();
        JSONArray d = new JSONArray();
        List<Map> list;
        if (Arrays.asList("3", "4").contains(state)){
            re.add(session.createQuery("select count(*) from OrderEntity where customerId=?1 and state=?2").setParameter(1, id).setParameter(2, state).uniqueResult());
            list = session.createSQLQuery("select id,merchant_id as m_id,crt_tm,goods,state from order_ where customer_id=?1 and state=?2 order by id desc limit ?3,?4").setParameter(1, id).setParameter(2, state).setParameter(3, p*count).setParameter(4, count)
                    .unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        }else{
            re.add(session.createQuery("select count(*) from OrderEntity where customerId=?1 and (state='3' or state='4')").setParameter(1, id).uniqueResult());
            list = session.createSQLQuery("select id,merchant_id as m_id,crt_tm,goods,state from order_ where customer_id=?1 and (state='3' or state='4') order by id desc  limit ?2,?3").setParameter(1, id).setParameter(2, p * count).setParameter(3, count)
                    .unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        }
        for (Map o : list) {
            o.put("m_n", session.createQuery("select name from MerchantEntity where id=?1").setParameter(1, o.get("m_id")).uniqueResult());
        }
        d.addAll(list);
        re.add(d);
        return re;
    }
    public void add_order(Session session, int id, String addr, String goods){
        Map<Integer, JSONArray> re = new HashMap<>();
        for (Object i: JSONArray.parseArray(goods)){
            JSONArray item = (JSONArray) i;
            int m_id = (int) session.createQuery("select ownerId from GoodsEntity where id=?1").setParameter(1, item.get(0)).uniqueResult();
            JSONArray old_list;
            if (re.containsKey(m_id)){
                old_list = re.get(m_id);
            }else{
                old_list = new JSONArray();
            }
            old_list.add(item);
            re.put(m_id, old_list);
        }
        for (int m_id: re.keySet()){
            double p = 0;
            for (Object arr: re.get(m_id)){
                JSONArray a = (JSONArray) arr;
                p += Double.parseDouble(String.valueOf(a.get(3)))*(int)a.get(4);
            }
            OrderEntity order = new OrderEntity();
            order.setCustomerId(id);
            order.setMerchantId(m_id);
            order.setCrtTm(String.valueOf(new Date().getTime()));
            order.setAddr(addr);
            order.setGoods(re.get(m_id).toJSONString());
            order.setPrice(p);
            order.setState("0");
            session.save(order);
        }
    }

    public void set_ev(Session session, int c_id, JSONArray ev, double s){
        long time = new Date().getTime();
        if (session.createQuery("update OrderEntity set state='4' where id=?1 and customerId=?2 and state='3'").setParameter(1, ev.get(0)).setParameter(2, c_id).executeUpdate()==1) {
            session.createQuery("update MerchantEntity set star=round((star*starNum+?1)/(starNum+1), 1),starNum=starNum+1 where id=?2").setParameter(1, s).setParameter(2, session.createQuery("select merchantId from OrderEntity where id=?1").setParameter(1, ev.get(0)).uniqueResult()).executeUpdate();
            for (Object o : (JSONArray) ev.get(1)) {
                JSONArray lis = (JSONArray) o;
                Object[] old_info = (Object[]) session.createSQLQuery("select star,json_length(evaluate) from goods where id=?1").setParameter(1, lis.get(0)).uniqueResult();
                double new_star = Double.parseDouble(String.format("%1f", ((double)old_info[0]*((Number)old_info[1]).intValue()+((Number)lis.get(2)).doubleValue())/(((Number)old_info[1]).intValue()+1)));
                session.createQuery("update GoodsEntity set evaluate=json_array_append(evaluate, '$', json_array(?1, ?2, ?3, ?4, ?5)),star=?6 where id=?7")
                        .setParameter(1, c_id).setParameter(2, time).setParameter(3, lis.get(1)).setParameter(4, lis.get(2)).setParameter(5, lis.get(3))
                        .setParameter(6, new_star).setParameter(7, lis.get(0)).executeUpdate();
            }
        }
    }

    public void change_state(Session session, int id, String who, int o_id, String state){
        session.createQuery("update OrderEntity set state=?1 where id=?2 and "+who+"Id=?3").setParameter(1, state).setParameter(2, o_id).setParameter(3, id).executeUpdate();
        // 更新销量
        if (state.equals("3")){
            JSONArray goods_lis = JSONArray.parseArray((String) session.createQuery("select goods from OrderEntity where id=?1").setParameter(1,o_id).uniqueResult());
            for (Object i: goods_lis){
                int g_id = (int) ((JSONArray) i).get(0);
                session.createQuery("update GoodsEntity set buyNum=buyNum+1 where id=?1").setParameter(1, g_id).executeUpdate();
                session.createQuery("update MerchantEntity set sellNum=sellNum+1 where id=?1").setParameter(1, session.createQuery("select ownerId from GoodsEntity where id=?1").setParameter(1, g_id).uniqueResult()).executeUpdate();
            }
        }
    }

    public void send_goods(Session session, int id, int o_id, String num){
        session.createQuery("update OrderEntity set addr=json_replace(addr, '$[3]', ?1),state='2' where id=?2 and merchantId=?3").setParameter(1, num).setParameter(2, o_id).setParameter(3, id).executeUpdate();
    }
}
