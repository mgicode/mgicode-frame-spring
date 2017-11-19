package com.kuiren.common.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.kuiren.common.exception.BaseException;
import com.kuiren.common.spring.SpringInit;

public class BaseController {
	protected final Log logger = LogFactory.getLog(getClass());

	public Object getAction(String beanName) {

		ApplicationContext ac = SpringInit.getApplicationContext();
		if (ac.containsBean(beanName)) {
			return ac.getBean(beanName);
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("\n没有找到名称为" + beanName + "的action");
			}
			throw new BaseException("\n没有找到名称为" + beanName + "的action");

		}

	}
}
