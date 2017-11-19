package com.kuiren.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AppconfigUtil {

    protected static final Log logger =
            LogFactory.getLog(AppconfigUtil.class);
    private static final ResourceBundle CONFIG_BUNDLE = ResourceBundle
            .getBundle("config");
    private static final String LOGICAL_DELETE = "logicalDelete";
//	public static Map<String, ConfigItem> allConfigMap = new HashMap<String,
//			ConfigItem>();

    // public static ConfigItem getConfigItem(String key) {
    // if (allConfigMap.containsKey(key)) {
    // return allConfigMap.get(key);
    // }
    // return null;
    // }

    public static String val(String key) {
        if (CONFIG_BUNDLE == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("config.properties的配置文件不存在！");
            }
            return null;
        }
        if (CONFIG_BUNDLE.containsKey(key)) {
            return CONFIG_BUNDLE.getString(key);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("config.properties的配置文件中的" + key + "不存在，请在配置文件中增加");
            }
            return null;
        }
        //return ConfigUtil.val(key);
        // return val(key, null);
    }

    public static String val(String key, String dv) {
        if (CONFIG_BUNDLE == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("config.properties的配置文件不存在！");
            }
            return dv;
        }
        if (CONFIG_BUNDLE.containsKey(key)) {
            String v = CONFIG_BUNDLE.getString(key);
            if (v != null)
                return v.trim();
            return v;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("config.properties的配置文件中的" + key
                        + "不存在，请在config.properites配置文件中增加" + key
                        + ",如果没有指定，采用默认值" + dv);
            }
            return dv;
        }
        // return ConfigUtil.val(key, dv);
    }

    public static String valPath(String key, String dv) {

        String path = val(key, dv);
        return StringUtil.buildPath(path);
    }


    public static boolean exist(String key) {
        if (CONFIG_BUNDLE.containsKey(key)) {
            String v = val(key);
            if (!StringUtil.IsNullOrEmpty(v)) {
                return true;
            }
        }
        return false;

        //return ConfigUtil.exist(key);
    }

    public static boolean allowLogicalDelete() {
        if ("true".equalsIgnoreCase(val(LOGICAL_DELETE))) {
            return true;
        }
        return false;
        //return ConfigUtil.allowLogicalDelete();
    }

}
