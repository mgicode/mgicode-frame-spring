package com.kuiren.common.vo;

import java.io.Serializable;


/**
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */
public class PropertyQuery implements Serializable {

    private String name;
    private Object value;
    private String relative;

    // private Strinbg
    private String type = "and";
    //
    // public final static String TYPE_STRING = "string";
    // public final static String TYPE_INTEGER = "integer";
    // public final static String TYPE_BOOLEAN = "boolean";
    // public final static String TYPE_FLOAT = "float";
    // public final static String TYPE_DATETIME = "datetime";
    public final static String LIKE = "like";
    public final static String GE = ">";
    public final static String LE = "<";
    public final static String NOTEQUAL = "<>";

    //isNull  isNotNull
    // }
    public PropertyQuery(String name, String relative, Object value) {
        this.name = name;
        this.value = value;
        this.relative = relative;
    }

    public PropertyQuery(String name, String relative, Object value, String type) {
        this.name = name;
        this.value = value;
        this.relative = relative;
        if ("or".equalsIgnoreCase(type)) {
            this.type = "or";
        } else {
            this.type = "and";
        }
    }

    public PropertyQuery() {

    }

    // public PropertyQuery setNullEqu(Object... obj) {
    //
    // }
    // (news.getHit() != null) && (news.getHit().intValue() > 0)
    // pengrk add 20160701
    public boolean isNull() {
        if (value == null)
            return true;
        if (value instanceof String) {
            return isNullOrEmpty((String) value);
        }

        return false;
    }


    public static boolean isNullOrEmpty(String str) {

        if (str == null) {
            return true;
        }
        if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getRelative() {
        return relative;
    }

    public void setRelative(String relative) {
        this.relative = relative;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
