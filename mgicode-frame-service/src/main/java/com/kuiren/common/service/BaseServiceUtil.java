package com.kuiren.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kuiren.common.auth.AuthConstants;
import com.kuiren.common.auth.User;
//import com.kuiren.common.easyui.JsonRequestContext;
import com.kuiren.common.util.EnumUtil;
import com.kuiren.common.util.ListUtil;
import com.kuiren.common.util.StringUtil;
import com.kuiren.common.vo.KeyValue;

public class BaseServiceUtil {
    protected static final Log logger = LogFactory
            .getLog(BaseServiceUtil.class);

    public static User getCurrentUser() {
        throw new RuntimeException("取不到用户");
//		Object o = JsonRequestContext.getSession().getAttribute(
//				AuthConstants.SESSION_USER_ATTR_NAME);
//		if (o == null) {
//			if (logger.isDebugEnabled()) {
//				logger.debug("\n用户登录时没有在session中设定AuthConstants.SESSION_USER_ATTR_NAME的User对象，"
//						+ "该User与用户系统的TUser不完全相同，是通用的User。每个用户中心需要设定");
//			}
//			return new User();
//		}
//
//		if (o instanceof User) {
//			return (User) o;
//		}
//		return new User();
    }

    public static int getRandom(int start, int end) {
        if (start > end || start < 0 || end < 0) {
            return -1;
        }
        return (int) ((Math.random() * (end - start + 1)) + start);
    }

    // @Override
    public static List<KeyValue> getEnums(String methodName, Class objClz) {
        Map<Integer, String> map = EnumUtil.getEnumMap(objClz.getName() + "_"
                + methodName);
        List<KeyValue> list = new ArrayList<KeyValue>();
        if (map != null) {
            for (Object s : map.keySet()) {
                KeyValue kv = new KeyValue(String.valueOf(s), map.get(s));
                list.add(kv);
            }
        }
        return list;
    }

    public static String joinBySingleQuote(List<Map<String, Object>> list,
                                           String fieldName) {
        boolean flag = false;
        if (list == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("没有查询到数据！");
            }
            return null;
        }
        if (StringUtil.IsNullOrEmpty(fieldName)) {
            flag = true;
        }
        List<String> retlist = new ArrayList<String>();
        for (Map<String, Object> map : list) {
            if (map == null)
                continue;
            if (map.size() > 1) {
                if (logger.isDebugEnabled()) {
                    logger.debug("\n" + "查询出多个字段，只读取第一个");
                }
            }
            if (StringUtil.IsNullOrEmpty(fieldName)) {
                for (String key : map.keySet()) {
                    retlist.add(map.get(key) + "");
                    break;
                }
            } else {
                for (String key : map.keySet()) {
                    if (key.equals(fieldName)) {
                        retlist.add(map.get(key) + "");
                        flag = true;
                        break;
                    }

                }
            }
        }
        if (flag == false) {
            throw new RuntimeException("没有找到指定的字段名");
        }
        return StringUtil.joinBySingleQuote(retlist, ",");
    }
}
