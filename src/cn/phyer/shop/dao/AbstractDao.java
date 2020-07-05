package cn.phyer.shop.dao;

import com.alibaba.fastjson.JSONArray;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Part;
import java.util.Arrays;
import java.util.Map;

import static cn.phyer.shop.plugin.Tool.save_img;

public class AbstractDao {
    protected String entity;
    protected Query sql;
    @Autowired
    private SessionFactory sessionFactory;
    AbstractDao(String entity){
        this.entity = entity;
    }

    public Session start(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        return session;
    }

    public void submit(Session session){
        session.getTransaction().commit();
        session.close();
    }

    public boolean check_dup(Session session, String email){
        sql = session.createQuery("select count(*) from "+entity+" where email=?1");
        sql.setParameter(1, email);
        return (Long)sql.uniqueResult()==0;
    }

    public int check_login(Session session, String email, String pwd){
        sql = session.createQuery("select id from "+entity+" where email=?1 and pwd=?2");
        sql.setParameter(1, email);
        sql.setParameter(2, pwd);
        try{
            return (int)sql.uniqueResult();
        }catch (NullPointerException ignore){
            return -1;
        }
    }

    public int add_account(Session session, String name, String email, String pwd){return 0;}

    public void change_name(Session session, int id, String name){
        sql = session.createQuery("update "+entity+" set name=?1 where id=?2");
        sql.setParameter(1, name);
        sql.setParameter(2, id);
        sql.executeUpdate();
    }

    public void change_cover(Session session, Part cover, String str, String path, int id){
        sql = session.createQuery("update "+entity+" set cover=?1 where id=?2");
        sql.setParameter(1, str);
        sql.setParameter(2, id);
        save_img(cover, path);
        sql.executeUpdate();
    }

    protected Map<String, String> get_for_fav(Session sql_session, int i) {return null;}

    public JSONArray get_info_for_i(Session session, int id){
        sql = session.createQuery("select name,cover from "+entity+" where id=?1");
        sql.setParameter(1, id);
        JSONArray result = new JSONArray();
        result.addAll(Arrays.asList((Object[])sql.uniqueResult()));
        return result;
    }
}
