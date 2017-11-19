/**
 * 
 */
package com.kuiren.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * author:pengrk
 * email:sjkjs155@126.com
 *  @wetsite:www.mgicode.com
 * @license:GPL
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MappingEntity {

}
