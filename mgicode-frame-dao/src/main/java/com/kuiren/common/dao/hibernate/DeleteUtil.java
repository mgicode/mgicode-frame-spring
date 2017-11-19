package com.kuiren.common.dao.hibernate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.kuiren.common.annotation.LogicalDelete;
import com.kuiren.common.util.AppconfigUtil;
import com.kuiren.common.util.BeanUtil;

public class DeleteUtil {

	protected final static Log logger = LogFactory.getLog(DeleteUtil.class);
	public static int LOGICALDELETE_DELETE = 1;
	public static int LOGICALDELETE_NO_DELETE = 0;

	// 返回null表示没有找到逻辑删除的标识字段
	public static PropertyDescriptor canLogicalDelete(Class entityClass) {
		try {
			// 总开关
			if (AppconfigUtil.allowLogicalDelete()) {
				if (entityClass.isAnnotationPresent(LogicalDelete.class)) {
					PropertyDescriptor pd = BeanUtil
							.getLogicDeleteProperty(entityClass);
					if (pd == null) {
						logger.error("在实体中找到逻辑删除标识，但是在其属性字段中的get方法中没有找到");
						return null;
					}
					if (pd.getPropertyType() == Integer.TYPE
							|| (pd.getPropertyType() == Integer.class)) {
						return pd;
					} else {
						logger.error("逻辑删除只能设定为int或Integer类别");
						return null;
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static <T> T setLogicalDeleteState(T entity, PropertyDescriptor pd) {
		try {
			pd.getWriteMethod().invoke(entity, DeleteUtil.LOGICALDELETE_DELETE);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return entity;
	}

	public static void addLogicalDeleteCriteria(Criteria c, Class entityClass) {
		PropertyDescriptor pDescriptor = DeleteUtil
				.canLogicalDelete(entityClass);
		if (pDescriptor != null) {
			Criterion lgc = Restrictions.eq(pDescriptor.getName(),
					DeleteUtil.LOGICALDELETE_NO_DELETE);
			Criterion lgc1 = Restrictions.isNull(pDescriptor.getName());

			Criterion lCriterion = Restrictions.or(lgc, lgc1);
			c.add(lCriterion);

		}
	}
}
