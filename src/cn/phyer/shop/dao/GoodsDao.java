package cn.phyer.shop.dao;

import cn.phyer.shop.entity.GoodsEntity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.Session;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("goodsDao")
public class GoodsDao extends AbstractDao {
    @Autowired
    private MerchantDao merchantDao;

    GoodsDao(){
        super("");
    }

    public JSONObject get_goodsdetail(Session session, int id){
        JSONObject re = new JSONObject();
        re.fluentPutAll((Map) session.createNativeQuery("select id,owner_id,title,price,ps,dcb,buy_num,fav_num,star,cover,cate,evaluate from goods where id=?1")
                .setParameter(1, id)
                .unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .uniqueResult());
        for (String s: new String[]{"cate", "price", "evaluate"}) {
            re.put(s, JSONArray.parseArray((String) re.get(s)));
        }
        re.put("ps", JSONObject.parseObject((String) re.get("ps")));
        int owner_id = (int) re.get("owner_id");
        re.put("owner_id", Arrays.asList(owner_id, merchantDao.get_name(session, owner_id), merchantDao.get_star(session, owner_id)));
        return re;
    }

    public GoodsEntity get_goods_for_merchant(Session session, int id, int merchant_id){
        return (GoodsEntity) session.createQuery("from GoodsEntity where id=?1 and ownerId=?2")
                .setParameter(1, id)
                .setParameter(2, merchant_id)
                .uniqueResult();
    }

    public JSONArray get_some_good(Session session, String what, String o, String s, int page, int count){
        JSONArray result = new JSONArray();
        String sql_s = "";
        String orient = "id";
        if (o.equals("sell")){
            orient = "buy_num";
        }else if (o.equals("evaluate")){
            orient = "star";
        }
        if (!s.equals("")) {
            if (what.equals("cate")) {
                sql_s = "where json_contains(cate, json_quote(?1), '$')=1 order by "+orient+" desc";
            } else {
                sql_s = "where title like ?1 order by "+orient+" desc";
            }
        }
        sql = session.createSQLQuery("select count(*) from goods "+sql_s);
        if (!s.equals("")){
            sql.setParameter(1, s);
        }
        result.add(sql.uniqueResult());
        sql = session.createNativeQuery("select id,owner_id,title,buy_num,star,cover,json_extract(ps, '$.*[0]') as ps from goods "+sql_s)
                .unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        if (!s.equals("")){
            sql.setParameter(1, s);
        }
        sql.setFirstResult(page*count);
        sql.setMaxResults(count);
        JSONArray goods_array = new JSONArray();
        for (Object goods: sql.list()){
            Map<String, Object> goods_info = (Map) goods;
            int m_id = (int)goods_info.get("owner_id");
            goods_info.put("owner_id", Arrays.asList(m_id, merchantDao.get_name(session, m_id)));
            goods_array.add(goods_info);
        }
        result.add(goods_array);
        return result;
    }

    public Map<String, String> get_for_fav(Session session, int i) {
        GoodsEntity goods = session.get(GoodsEntity.class, i);
        Map<String, String> re = new HashMap<>();
        re.put("title", goods.getTitle());
        re.put("cover", goods.getCover());
        return re;
    }

    public JSONArray get_rank_by_sell(Session session, int id, int count){
        JSONArray re = new JSONArray();
        sql = session.createQuery("select id,title,cover from GoodsEntity where ownerId=?1 order by buyNum desc ");
        sql.setParameter(1, id);
        sql.setMaxResults(count);
        re.addAll(sql.list());
        return re;
    }

    public JSONArray get_evaluate(Session session, int goods_id, int p, int count) {
        JSONArray re = new JSONArray();
        Number num = (Number) session.createSQLQuery("select JSON_LENGTH(evaluate) from goods where id=?1").setParameter(1, goods_id).uniqueResult();
        re.add(num);
        if (num.intValue() == 0){
            re.add(new JSONArray());
            return re;
        }
        JSONArray data = JSONArray.parseArray((String) session.createSQLQuery("select json_extract(evaluate, '$["+p+" to "+(p+1)*count+"]') from goods where id=?1").setParameter(1, goods_id).uniqueResult());
        Map<Integer, Object[]> ids = new HashMap<>();
        for (Object item: data){
            int id = (int) ((JSONArray) item).get(0);
            if (!ids.containsKey(id)){
                ids.put(id, (Object[]) session.createQuery("select name,cover from CustomerEntity where id=?1").setParameter(1, id).uniqueResult());
            }
        }
        for (int i=0;i<data.size();i++){
            JSONArray d = ((JSONArray) data.get(i));
            int id = (int) d.remove(0);
            Object[] info = ids.get(id);
            d.add(0, info[1]);
            d.add(0, info[0]);
        }
        re.add(data);
        return re;
    }

    public JSONArray get_home(Session session){
        JSONArray re = new JSONArray();
        for (String s:Arrays.asList("id", "buy_num")) {
            JSONArray new_ = new JSONArray();
            new_.addAll(session.createNativeQuery("select id,owner_id,title,buy_num,star,cover,json_extract(ps, '$.*[0]') as ps from goods order by "+s+" desc limit 0,6")
                    .unwrap(NativeQueryImpl.class)
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
            for (Object i : new_) {
                Map r = (Map) i;
                int owner_id = (int) r.get("owner_id");
                r.put("owner_id", Arrays.asList(owner_id, merchantDao.get_name(session, owner_id), merchantDao.get_star(session, owner_id)));
            }
            re.add(new_);
        }
        return re;
    }
}
