package com.kuiren.common.hibernate;

import java.beans.PropertyDescriptor;

import com.kuiren.common.util.BeanUtil;

public class EntityUtil {
	public  static <PK>  PK getId(Object entity) {
		PropertyDescriptor pd = BeanUtil.getIdProperty(entity.getClass());
		if (pd != null) {
			PK pk = (PK) BeanUtil.invoke(entity, pd.getReadMethod());
			return pk;
			// try {
			// PK pk = (PK) pd.getReadMethod().invoke(entity);
			// return pk;
			// } catch (IllegalArgumentException e) {
			// e.printStackTrace();
			// } catch (IllegalAccessException e) {
			// e.printStackTrace();
			// } catch (InvocationTargetException e) {
			// e.printStackTrace();
			// }
		}

		return null;

	}
}
