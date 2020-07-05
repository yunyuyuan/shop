package cn.phyer.shop.dao;

import cn.phyer.shop.entity.GoodsEntity;
import cn.phyer.shop.entity.MerchantEntity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.Session;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.phyer.shop.plugin.Tool.del_img;

@Repository("merchantDao")
public class MerchantDao extends AbstractDao {
    MerchantDao(){
        super("MerchantEntity");
    }

    public int add_account(Session session, String name, String email, String pwd) {
        MerchantEntity merchant = new MerchantEntity();
        merchant.setName(name);
        merchant.setEmail(email);
        merchant.setPwd(pwd);
        merchant.setStar(0d);
        merchant.setStarNum(0);
        merchant.setFavNum(0);
        merchant.setSellNum(0);
        merchant.setCover("");
        session.save(merchant);
        return merchant.getId();
    }

    public MerchantEntity get_merchant(Session session, int id){
        return session.get(MerchantEntity.class, id);
    }

    public String get_name(Session session, int id){
        return (String) session.createQuery("select name from MerchantEntity where id="+id).uniqueResult();
    }

    public Double get_star(Session session, int id){
        return (Double) session.createQuery("select star from MerchantEntity where id="+id).uniqueResult();
    }

    public GoodsEntity upGoods(Session session, int owner_id, JSONObject info){
        GoodsEntity goods = new GoodsEntity();
        goods.setOwnerId(owner_id);
        goods.setFavNum(0);
        goods.setBuyNum(0);
        goods.setStar(5d);
        goods.setEvaluate("[]");
        set_info(goods, info);
        session.save(goods);
        return goods;
    }

    public void modifyGoods(Session session, int goods_id, int owner_id, JSONObject info){
        sql = session.createQuery("from GoodsEntity where id=?1 and ownerId=?2");
        sql.setParameter(1, goods_id);
        sql.setParameter(2, owner_id);
        try {
            GoodsEntity goods = (GoodsEntity) sql.uniqueResult();
            set_info(goods, info);
            session.update(goods);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void set_info(GoodsEntity goods, JSONObject info){
        goods.setTitle((String) info.get("title"));
        goods.setPrice((String) info.get("price_list"));
        goods.setDcb((String) info.get("dcb"));
        goods.setPs((String) info.get("ps"));
        if (!info.get("cover").equals("")){
            goods.setCover((String) info.get("cover"));
        }
        goods.setCate((String) info.get("cates"));
    }

    public List<GoodsEntity> get_goods_by_name(Session session, int m_id, String name, int p, int count){
        if (name.equals("")){
            sql = session.createQuery("from GoodsEntity where ownerId=?1").setParameter(1, m_id);
        }else{
            sql = session.createQuery("from GoodsEntity where title like ?1 and ownerId=?2")
            .setParameter(1, "%"+name+"%").setParameter(2, m_id);
        }
        sql.setFirstResult(p*count);
        sql.setMaxResults(count);
        return sql.list();
    }

    public void del_goods(Session session, int owner_id, JSONArray goods_ids, String real_path){
        sql = session.createQuery("from GoodsEntity where ownerId=?1 and id=?2");
        for (Object goods_id: goods_ids){
            sql.setParameter(1, owner_id);
            sql.setParameter(2, goods_id);
            Object r = sql.uniqueResult();
            if (r != null) {
                GoodsEntity goods = (GoodsEntity) r;
                del_img(real_path+"/static/img/goods/"+goods.getCover());
                session.delete(goods);
            }
        }
    }

    // 用户收藏本店
    public Map<String, String> get_for_fav(Session session, int id){
        MerchantEntity merchant = session.get(MerchantEntity.class, id);
        Map<String, String> re = new HashMap<>();
        re.put("name", merchant.getName());
        re.put("cover", merchant.getCover());
        return re;
    }

    // 搜索商店
    public JSONArray get_some_store(Session session, String what, String o, String s, int p, int count) {
        JSONArray result = new JSONArray();
        String sql_s = "";
        String orient = "id";
        if (o.equals("sell")){
            orient = "sell_num";
        }else if (o.equals("evaluate")){
            orient = "star";
        }
        if (!s.equals("") && what.equals("search")) {
            sql_s = "where name_ like ?1 order by "+orient+" desc ";
        }
        sql = session.createNativeQuery("select count(*) from merchant " + sql_s);
        if (!s.equals("")){
            sql.setParameter(1, s);
        }
        result.add(sql.uniqueResult());
        sql = session.createNativeQuery("select id,name_,star,sell_num,cover from merchant " + sql_s)
                .unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        if (!s.equals("")) {
            sql.setParameter(1, s);
        }
        sql.setFirstResult(p*count);
        sql.setMaxResults(count);
        List r = sql.list();
        result.add(r);
        return result;
    }

    public JSONObject get_storedetail(Session session, int id){
        JSONObject re = new JSONObject();
        re.fluentPutAll((Map)session.createNativeQuery("select name_ as name,star,star_num,fav_num,sell_num,cover from merchant where id=?1").setParameter(1, id)
                .unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult());
        return re;
    }

    public JSONArray get_some(Session session, int id, String s, String o, int p, int count){
        JSONArray re = new JSONArray();
        String orient = "id";
        if (o.equals("sell")){
            orient = "buy_num";
        }else if (o.equals("ev")){
            orient = "star";
        }
        if (s.equals("")){
            re.add(session.createNativeQuery("select count(*) from goods where owner_id=?1")
                    .setParameter(1, id).uniqueResult());
            re.add(session.createNativeQuery("select id,title,cover,star,buy_num from goods where owner_id=?1 order by "+orient+" desc limit ?2,?3")
                    .setParameter(1, id).setParameter(2, p*count).setParameter(3, count).unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
        }else{
            re.add(session.createNativeQuery("select count(*) from goods where owner_id=?1 and title like ?2")
                    .setParameter(1, id).setParameter(2, "%"+s+"%").uniqueResult());
            re.add(session.createNativeQuery("select id,title,cover,star,buy_num from goods where owner_id=?1 and title like ?4 order by "+orient+" desc limit ?2,?3")
                    .setParameter(1, id).setParameter(2, p*count).setParameter(3, count).setParameter(4, "%"+s+"%")
                    .unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list());
        }
        return re;
    }
}
