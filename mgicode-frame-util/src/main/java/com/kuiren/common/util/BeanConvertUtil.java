package com.kuiren.common.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import com.kuiren.common.annotation.Desc;


public class BeanConvertUtil {
    protected final static Log logger = LogFactory
            .getLog(BeanConvertUtil.class);

    public static Set<Class<?>> getClasses(String pack, Class<?> intefaceclz) {
        Set<Class<?>> clzSet = BeanUtil.getClasses(pack);
        Set<Class<?>> retSet = new HashSet<Class<?>>();
        for (Class<?> clz : clzSet) {
            if (intefaceclz.isAssignableFrom(clz) && !intefaceclz.equals(clz)) {
                retSet.add(clz);
            }
        }

        return retSet;
    }

    public static Object newInstance(Class cls) {
        try {
            Object pNum = cls.newInstance();
            return pNum;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

//	public static void buildInstanceDescMap(Map map, String packageName,
//			Class interClz) {
//		// 不为空说明其已经加载了，那么就不需要再次加载
//		if (!map.isEmpty())
//			return;
//		Set<Class<?>> clses = BeanConvertUtil.getClasses(packageName, interClz);
//
//		for (Class<?> cClass : clses) {
//			if (!cClass.isAnnotationPresent(Desc.class)) {
//				continue;
//			}
//			Desc desc = cClass.getAnnotation(Desc.class);
//			Object pNum = BeanConvertUtil.newInstance(cClass);
//
//			if (pNum != null && StringUtil.isNotNullOrEmpty(desc.value())) {
//				map.put(desc.value(), pNum);
//				continue;
//			}
//			if (logger.isDebugEnabled()) {
//				if (pNum == null)
//					logger.debug(cClass.getName() + "的类在实例化时失败！");
//				else if (StringUtil.isNullOrEmpty(desc.value())) {
//					logger.debug(cClass.getName() + "的类没有取到其@Desc的value值");
//				}
//			}
//
//		}
//
//	}

    public static void buildInstanceMap(Map map, String packageName,
                                        Class interClz) {

        if (!map.isEmpty()) {
            return;
        }
        List<String> pns = StringUtil.split(packageName, "[,]");
        for (String s : pns) {
            Set<Class<?>> clses = BeanConvertUtil.getClasses(s, interClz);
            if (logger.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("根据" + s + "的名称找到");
                if (clses != null) {
                    for (Class<?> class1 : clses) {
                        sb.append(class1.getName() + ",");
                    }
                }
                logger.debug(sb.toString());
            }

            for (Class<?> clz : clses) {
                Object m = BeanConvertUtil.newInstance(clz);
                if (m == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(clz.getName() + "的类在实例化时失败！");
                    }
                    continue;
                }
                try {
                    String clzName = clz.getSimpleName();
                    if (map.containsKey(clzName)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(clz.getSimpleName() + "已经存在！");
                        }
                    } else {
                        map.put(clzName, m);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (logger.isErrorEnabled()) {
                        logger.error(e);
                    }

                }

            }
        }

    }

    public static void invoke(Map<String, Object> map, String s, String msg) {
        try {
            if (StringUtil.isNullOrEmpty(s))
                return;
            String arr[] = s.split("[.]");
            if (arr == null || arr.length != 2) {
                if (logger.isDebugEnabled()) {
                    logger.debug(msg);
                }
                return;
            }
            List<Object> list = new ArrayList<Object>();
            list.add((Map<String, Object>) map);
            BeanUtil.invokeBySpring(arr[0], arr[1], list);

        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e);
            }

        }

    }

    public static void populateProperty(Object o, Map<String, Object> map) {

        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(o.getClass());
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

        PropertyDescriptor pds[] = beanInfo.getPropertyDescriptors();

        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            String name = StringUtils.capitalize(pd.getName());
            if (!map.containsKey(name)) {
                continue;
            }
            Object value = map.get(name);
            if (value == null) {
                continue;
            }
            Class type = pd.getPropertyType();
            ConvertUtilsBean cub = BeanUtilsBean.getInstance()
                    .getConvertUtils();

            Converter converter = cub.lookup(type);
            if (converter != null)
                value = converter.convert(type, value);
            try {
                pd.getWriteMethod().invoke(o, new Object[]{value});
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }

    }
}
