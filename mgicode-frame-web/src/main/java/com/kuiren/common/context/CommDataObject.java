package com.kuiren.common.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.kuiren.common.util.JacksonHelper;

/**
 * 通用数据对象
 *
 * @author pushi
 */
public class CommDataObject implements Serializable {
    private static final String XML_ROOT = "ROOT";
    /**
     * data对象可能传入的类型有：String (XML),XMLDataObject,Map 3种
     */
    private Object data = null;
    /**
     * 0:HashMap; 1:XML_String; 2:XMLDataObject;
     */
    // private Object[] cache = new Object[3];

    private Map cacheMap;

    public CommDataObject() {
        this.data = new HashMap();
    }

    public CommDataObject(Object data) {
        this.data = data;
    }

    public Map getMap() {

        if (cacheMap != null) {
            return cacheMap;
        }
        if (data instanceof String) {
            // 默认是Json数据
            cacheMap = new Gson().fromJson((String) data, Map.class);//JacksonHelper.getMapFromJson((String) data);
            return cacheMap;

        } else if (data instanceof Map) {
            cacheMap = (Map) data;
        }

        return cacheMap;
    }

    public String toString() {
        return getJson();
    }

    public String getJson() {
        return new Gson().toJson(this.getMap());//JacksonHelper.getJsonFromMap(this.getMap());
    }

    // public static void main(String[] args) {
    // StringBuilder builder = new StringBuilder();
    // long i = 0;
    // try {
    // while (true) {
    // builder.append("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
    // i++;
    // System.out.println("i:" + i);
    // }
    // } catch (Throwable e) {
    // System.out.println("i:" + i);
    // }
    // }
}
