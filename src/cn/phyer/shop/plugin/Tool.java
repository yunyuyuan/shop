package cn.phyer.shop.plugin;

import com.alibaba.fastjson.JSONArray;

import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;

public class Tool {
    public static JSONArray all_cate = JSONArray.parseArray("[" +
            "{'服装': ['T恤', '外套', '裤子', '毛衣', '卫衣', '鞋子', '西装', '裙子']}," +
            "{'数码家电': ['手机', '电脑', '相机', '台式机', '笔记本', '电饭煲', '冰箱', '洗衣机', '耳机', '键盘']}," +
            "{'食品': ['零食', '水果', '生鲜', '奶粉', '糖果', '西餐', '牛奶', '饮料']}," +
            "{'图书': ['名著', '外国小说', '推理', '言情', '科幻', '文学', '古典', '杂志', '学术', '教材']}," +
            "{'居家': ['餐具', '衣架', '锅碗', '拖把', '杯子', '桌子', '相册', '伞']}," +
            "]");
    private static Random rand = new Random();

    public static String random_code(){
        StringBuilder re = new StringBuilder();
        for (int i=0;i<4;i++){
            re.append(rand.nextInt(10));
        }
        return re.toString();
    }

    public static boolean save_img(Part part, String path) {
        try (FileOutputStream outputStream = new FileOutputStream(new File(path));
             InputStream reader = part.getInputStream()) {
            byte[] b = new byte[1024];
            while (reader.read(b, 0, 1024) > 0) {
                outputStream.write(b);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void del_img(String path){
        if (!path.matches(".*?default\\.jpg$")){
            new File(path).delete();
        }
    }
}
