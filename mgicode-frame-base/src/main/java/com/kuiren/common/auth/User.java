package com.kuiren.common.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.tuscany.sca.implementation.java.introspect.JavaClassVisitor;


/**
 * 使用时通过getCurrentUser().adive(key,true);
 */
public class User implements Serializable {
    protected final Log logger = LogFactory.getLog(getClass());

    public final static String ORG = "org";
    public final static String RES_LIST = "res_List";
    public final static String ROLE_LIST = "role_list";
    public final static String USER_INFO = "user_info";

    private String userId;
    private String userCode;
    private String userName;
    private String chineseName;
    private String nickedName;
    private String password;

    private Map<String, Object> expandMap = new HashMap<String, Object>();

    private Map<String, String> sysTokenMap = new HashMap<String, String>();

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getNickedName() {
        return nickedName;
    }

    public void setNickedName(String nickedName) {
        this.nickedName = nickedName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object getExpandVal(String key) {

        Object obj = expandMap.get(key);
        if (obj instanceof UserDetail) {
            UserDetail ud = (UserDetail) obj;
            return ud.get();
        }
        return obj;
    }

    public Object getExpandObj(String key) {

        Object obj = expandMap.get(key);

        return obj;
    }

    public Object getVal(String key) {

        return getExpandVal(key);
    }

    public void setExpandVal(String key, Object v) {
        if (v instanceof UserDetail) {
            UserDetail ud = (UserDetail) v;
            ud.userId = userId;
            ud.name = key;
        }
        expandMap.put(key, v);
    }

    public void setVal(String key, UserDetail v) {
        setExpandVal(key, v);
    }

    public void setVal(String key, String serviceName, String methodName) {

        UserDetail ud = new UserDetail();
        ud.setServiceName(serviceName);
        ud.setMethodName(methodName);
        setVal(key, ud);
    }

    public void advice(String key, boolean modefied) {

        Object o = getExpandObj(key);
        if (o instanceof UserDetail) {
            UserDetail ud = (UserDetail) o;
            ud.setModefied(modefied);
        }
    }

    public void setToken(String sysId, String token) {
        sysTokenMap.put(sysId, token);
    }

    public String getToken(String sysId) {
        return sysTokenMap.get(sysId);
    }

    public class UserDetail implements Serializable {

        private String serviceName;
        private String methodName;

        private boolean modefied = false;

        // user set
        public String userId;
        public String name;

        // return
        private Object data;

        public Object dyncCall() {


            return null;
        }

        public Object get() {
            if ((data == null) || (modefied == true)) {
                data = dyncCall();
                modefied = false;
            }
            return data;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public boolean isModefied() {
            return modefied;
        }

        public void setModefied(boolean modefied) {
            this.modefied = modefied;
        }

    }

}
