package com.kuiren.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.transaction.annotation.Transactional;

import com.kuiren.common.easyui.EasyUIServlet;

import com.kuiren.common.easyui.EuiPage;
import com.kuiren.common.easyui.JsonRequest;
import com.kuiren.common.easyui.JsonRequestContext;
import com.kuiren.common.easyui.RetData;
import com.kuiren.common.json.JsonCreator;

import com.kuiren.common.page.Page;
import com.kuiren.common.service.BaseServiceImpl;
import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.EnumUtil;
import com.kuiren.common.util.ListUtil;
import com.kuiren.common.util.StringUtil;
import com.kuiren.common.vo.KeyValue;

/**
 * @param <T>
 * @param <PK>
 * @author 彭仁夔 于2014年10月23日上午10:29:29创建
 */
public class BasicServiceImpl<T, PK> extends BaseServiceImpl<T, PK> implements
        BasicService<T, PK> {

    //public static JsonHelper jackson = JsonHelper.buildNonDefaultBinder();
    final static String IS_FIRST = "null";
    public static final int PAGE_SIZE = 10;

    // protected DaoHelperDW daoHelperDW = null;

    public String toStr(Collection<?> list) {
        return ListUtil.listToStr(list);
    }

    public String toStr(Object[] list) {
        return ListUtil.listToStr(list);
    }

    public int getInt(String s) {
        if (StringUtil.isNullOrEmpty(s))
            return 0;
        if (StringUtil.isNum(s)) {

            return Integer.parseInt(s);
        }
        return 0;
    }

    public float getFloat(Float f) {

        if (f == null) {
            return 0f;
        } else
            return f.floatValue();
    }

    public float getFloat(String f) {
        if (StringUtil.isNullOrEmpty(f))
            return 0f;
        if (StringUtil.isNum(f)) {
            return getFloat(Float.parseFloat(f));
        }
        return 0f;
    }

    @Transactional(readOnly = false)
    @Override
    public String add(JsonRequest req) {
        RetData retMsg = new RetData();
        try {
            String json = req.getJson();

            T obj = (T) new Gson().fromJson(json, objClz);//jackson.fromJson(json, objClz);
            PK id = this.save(obj);

            retMsg.setSuccess(true);
            retMsg.setMsg(id + "");

            // return jackson.toJson(retMsg);
            String ret = new JsonCreator().build(retMsg);
            System.out.println(ret);
            adviceUpdateCb("add", req);
            return ret;
        } catch (Exception e) {
            retMsg.setSuccess(false);
            retMsg.setMsg(e.getMessage());
            e.printStackTrace();
            String ret = new JsonCreator().build(retMsg);
            return ret;
        }
    }

    @Override
    public String get(JsonRequest req) {
        return getById(req);

    }

    @Transactional(readOnly = false)
    @Override
    public String modefy(JsonRequest req) {

        RetData retMsg = new RetData();
        try {
            String json = req.getJson();
            T obj = (T) new Gson().fromJson(json, objClz);//jackson.fromJson(json, objClz);
            // this.saveOrUpdate(obj);
            this.update(obj);

            retMsg.setSuccess(true);
            retMsg.setMsg("修改成功");

            String ret = new JsonCreator().build(retMsg);
            System.out.println(ret);
            adviceUpdateCb("modefy", req);
            return ret;
        } catch (Exception e) {
            retMsg.setSuccess(false);
            retMsg.setMsg(e.getMessage());
            e.printStackTrace();
            String ret = new JsonCreator().build(retMsg);
            return ret;
        }

    }

    @Transactional(readOnly = false)
    @Override
    public String modefyEmit(JsonRequest req) {

        RetData retMsg = new RetData();
        try {
            String json = req.getJson();

            T obj = (T) new Gson().fromJson(json, objClz);//jackson.fromJson(json, objClz);
            this.updateEmit(obj, false, false);

            retMsg.setSuccess(true);
            retMsg.setMsg("修改成功");

            String ret = new JsonCreator().build(retMsg);
            System.out.println(ret);
            adviceUpdateCb("modefyEmit", req);
            return ret;
        } catch (Exception e) {
            retMsg.setSuccess(false);
            retMsg.setMsg(e.getMessage());
            e.printStackTrace();
            String ret = new JsonCreator().build(retMsg);
            return ret;
        }

        // return jackson.toJson(retMsg);

    }

    @Transactional(readOnly = false)
    @Override
    public void saveOrUpdateEmit(T obj) {
        this.updateEmit(obj, false, false);
        adviceUpdateCb("saveOrUpdateEmit", obj);

    }

    @Transactional(readOnly = false)
    @Override
    public String del(JsonRequest req) {
        RetData retMsg = new RetData();

        try {
            PK id = (PK) req.paramAsStr("id");
            deleteById((PK) id);
            retMsg.setSuccess(true);
            retMsg.setMsg(id + "");
            adviceUpdateCb("del", req);
        } catch (Exception e) {
            retMsg.setSuccess(false);
            retMsg.setMsg(e.getMessage());
            e.printStackTrace();
        }
        String ret = new JsonCreator().build(retMsg);
        System.out.println(ret);
        return ret;

        // return jackson.toJson(retMsg);
    }

    @Override
    public String getById(JsonRequest req) {
        RetData retMsg = new RetData();
        try {
            PK id = (PK) req.paramAsStr("id");
            T e = findByPK(id);

            retMsg.setSuccess(true);
            retMsg.setData(e);
            String ret = new JsonCreator().build(e);
            System.out.println(ret);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            retMsg.setSuccess(false);
            retMsg.setMsg(StringUtil.exception(e));
            String ret = new JsonCreator().build(e);
            return ret;
        }
    }

    @Override
    public String load(JsonRequest req) {
        // RetData retMsg = new RetData();

        PK id = (PK) req.paramAsStr("id");
        T e = findByPK(id);

        // retMsg.setSuccess(true);
        // retMsg.setData(e);
        String ret = new JsonCreator().build(e);
        System.out.println(ret);
        return ret;
        // String str = jackson.toJson(e);
        // return str;

    }

    /**
     * @author:彭仁夔 于2014年10月28日下午10:03:12创建
     * @see com.kuiren.common.BasicService#fetch(com.kuiren.common.easyui.JsonRequest)
     */
    @Override
    public String fetch(JsonRequest req) {

        String json = req.getJson();
        @SuppressWarnings("unchecked")
        T e = (T) new Gson().fromJson(json, objClz);//jackson.fromJson(json, objClz);
        List list = this.search(null, e);
        if (list == null || list.size() < 1) {
            return "{}";

        } else {
            String ret = new JsonCreator().build(list.get(0));
            System.out.println(ret);
            return ret;
            // String str = jackson.toJson(list.get(0));
            // return str;
        }
    }

    @Override
    public String list(JsonRequest req) {
        // RetData retMsg = new RetData();
        String json = req.getJson();
        T e = (T) new Gson().fromJson(json, objClz);//jackson.fromJson(json, objClz);
        List list = this.search(null, e);
        String ret = new JsonCreator().build(list);
        System.out.println(ret);
        return ret;
        // String str = jackson.toJson(list);
        // return str;

    }

    public Page<T> buildSearchPage(JsonRequest req) {

        // 获取每页大小
        int pageSize = req.param("pageSize,pagesize,psize").toInt(PAGE_SIZE);

        // 获取当前页数
        int pageNumber = req.param("pageNumber,pagenumber,pageNo,pageno,pno")
                .toInt(1);

        Page<T> page = new Page<T>(pageSize);

        page.setAutoCount(true);
        page.setPageNo(pageNumber);
        return page;
    }

    @Override
    public String pagelist(JsonRequest req) {
        // EuiPage up = new EuiPage();
        //
        //
        // try {
        // String json = req.getJson();
        // //
        // // int pageSize = req.param("pageSize,pagesize,psize")
        // // .toInt(PAGE_SIZE);
        // // int pageNumber = req.param(
        // // "pageNumber,pagenumber,pageNo,pageno,pno").toInt(1);
        // //
        // // Page<T> page = new Page<T>(pageSize);
        // //
        // // page.setAutoCount(true);
        // // page.setPageNo(pageNumber);
        //
        // Page<T> page = buildSearchPage(req);
        //
        // T e = (T) jackson.fromJson(json, objClz);
        //
        //
        // this.search(page, e);
        //
        // if (page != null) {
        // up.setTotal(page.getTotalCount());
        // up.setRows(page.getResult());
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        // String str = jackson.toJson(up);

        EuiPage up = euiPageList(req);
        if (up == null || up.getRows() == null || up.getRows().size() < 1) {
            return " {\"total\":0,\"rows\":[]}";
        }
        String str = new JsonCreator().build(up);

        return str;

    }

    @Override
    public EuiPage euiPageList(JsonRequest req) {
        EuiPage up = new EuiPage();
        try {
            String json = req.getJson();
            Page<T> page = buildSearchPage(req);
            T e = (T) new Gson().fromJson(json, objClz);//jackson.fromJson(json, objClz);
            this.search(page, e);

            if (page != null) {
                up.setTotal(page.getTotalCount());
                up.setRows(page.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return up;
    }

    @Override
    public String enums(JsonRequest req) {

        String ret = "{}";
        String str = (String) req.getMap().get("fname");
        if (StringUtil.IsNotNullOrEmpty(str)) {
            List<KeyValue> list = getEnums(str);
            ret = new JsonCreator().build(list);
            System.out.println(ret);
            // return ret;
            // ret = jackson.toJson(list);
        }
        return ret;
    }

    @Override
    public String enumMap(JsonRequest req) {

        String ret = "{}";
        String str = (String) req.getMap().get("fname");
        if (StringUtil.isNotNullOrEmpty(str)) {

            Map<Integer, String> map = EnumUtil.getEnumMap(this.objClz
                    .getName() + "_" + str);
            ret = new JsonCreator().build(map);
            System.out.println(ret);
            // ret = jackson.toJson(map);
        }
        return ret;// "window.enum" + str + "=" + ret;
    }

    @Override
    public String enumMapJs(JsonRequest req) {

        String ret = "{}";
        String str = (String) req.getMap().get("fname");
        if (StringUtil.isNotNullOrEmpty(str)) {

            Map<Integer, String> map = EnumUtil.getEnumMap(this.objClz
                    .getName() + "_" + str);
            ret = new JsonCreator().build(map);
            System.out.println(ret);
            // ret = jackson.toJson(map);
            return "window.enum_" + str + "=" + ret;
        }
        return "";
    }

    /**
     * 根据逗号分隔的Ids找到所有实体
     */
    @Override
    public List<T> findByIds(String ids) {
        return findByInStr("id", ids);
    }

    // public String getUser() {
    // return (String) ApplicationContextUtils.getSession().getAttribute(
    // "user");
    //
    // }
    //
    // public ctais.services.authenticate.User getCurrentUser() {
    // // UserHolder.getCurrentUser().getUserInfo().getRealmName()
    // // UserHolder.getCurrentUser().getUserInfo();
    // return UserHolder.getCurrentUser();
    //
    // }

    // public String get

    /**
     * 批量更新map中id:value的记录，其更新的属性名由updateProp指定 该类只用于当前的实体
     *
     * @param map
     * @param updateProp
     * @param idProp
     * @author:彭仁夔 于2014年10月23日下午11:06:10创建
     */
    @Override
    public void batchUpdate(Map<String, Object> map, String updateProp,
                            String idProp) {
        for (Object o : map.keySet()) {
            updateByProperty(updateProp, map.get(o), idProp, String.valueOf(o));
        }
        adviceUpdateCb("batchUpdate", map, updateProp, idProp);
    }

    @Override
    public void batchUpdate(Map<String, Object> map, String updateProp,
                            String idProp, String type) {
        for (Object o : map.keySet()) {

            if ("string".equals(type)) {
                updateByProperty(updateProp, String.valueOf(map.get(o)),
                        idProp, o);
            } else if ("float".equals(type)) {
                updateByProperty(updateProp,
                        Float.parseFloat(String.valueOf(map.get(o))), idProp,
                        String.valueOf(o));
            } else {
                updateByProperty(updateProp, map.get(o), idProp, o);
            }
            adviceUpdateCb("batchUpdate", map, updateProp, idProp, type);
        }
    }

    @Transactional(readOnly = false)
    @Override
    public String addOrModefy(JsonRequest req) {

        RetData retMsg = new RetData();
        String json = req.getJson();

        T obj = (T) new Gson().fromJson(json, objClz);// jackson.fromJson(json, objClz);
        this.updateEmit(obj, false, true);

        retMsg.setSuccess(true);
        retMsg.setData(obj);
        retMsg.setMsg("修改成功");
        adviceUpdateCb("addOrModefy", req);
        return new Gson().toJson(retMsg);//jackson.toJson(retMsg);

    }


    public String errorMsg(String msg) {

        RetData retMsg = new RetData();
        retMsg.setSuccess(false);
        retMsg.setMsg(msg);
        return new Gson().toJson(retMsg);//jackson.toJson(retMsg);
    }

    @Transactional(readOnly = false)
    @Override
    public String adjustOrder(JsonRequest req) {
        // String jsonString = req.paramAsStr("m");
        String sortedField = req.paramAsStr("fname");
        String pidField = req.paramAsStr("pidField");
        List list = (List) req.getMap().get("m");
        for (Object object : list) {
            if (object instanceof Map) {
                Map map = (Map) object;
                String id = (String) map.get("id");
                if (id.equals("all_total")) {// 修改父亲节点
                    String sourceid = (String) map.get("sourceid");
                    String pid = (String) map.get("pid");
                    updateByProperty(pidField, pid, "id", sourceid);
                } else {// 调整兄弟节点的所有排序
                    String orderid = (String) map.get("orderid");
                    // TAssetType
                    updateByProperty("sortedField", getInt(orderid), "id", id);
                }

            }
        }

        RetData retData = new RetData();
        retData.setSuccess(true);
        return new JsonCreator().build(retData);
    }

    /**
     * 执行Sql查询，查询一条数据，把该数据转换为Map 主要用于只需要几个字段的的Sql查询 (non-Javadoc)
     *
     * @author 彭仁夔于2015年12月28日下午3:58:12创建
     * @see com.kuiren.common.BasicService#queryUniqueToMap(java.lang.String)
     */
    @Override
    public Map<String, Object> queryUniqueToMap(String sql) {
        Query query = getSession().createSQLQuery(sql);
        return (Map) query.setResultTransformer(
                Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult(); // 返回值为map集合
        // 且为唯一值（只能返回一条数据）
    }

    /**
     * 将sql中的全部数据查出来，返回值为一个List<Map<String, Object>>
     *
     * @param sql
     * @return
     * @author 彭仁夔于2015年12月28日下午4:00:53创建
     */
    @Override
    public List<Map<String, Object>> queryToListMap(String sql) {
        Query query = getSession().createSQLQuery(sql);
        return (List) query.setResultTransformer(
                Transformers.ALIAS_TO_ENTITY_MAP).list();

    }

    /**
     * 将sql中的全部数据查出来，返回值为一个List<Map<String, Object>> ,加上分页
     *
     * @param sql
     * @param pageSize
     * @param pageNumber
     * @return
     * @author 彭仁夔于2015年12月28日下午4:37:19创建
     */
    @Override
    public List<Map<String, Object>> queryToListMap(String sql, int pageSize,
                                                    int pageNumber) {
        Query query = getSession().createSQLQuery(sql);
        // new Object[] {
        // pageNumber * pageSize, (pageNumber - 1) * pageSize
        return (List<Map<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize).list();

    }

    @Override
    public Page<Map<String, Object>> queryToPageMap(String sql, int pageSize,
                                                    int pageNumber) {
        Page<Map<String, Object>> page = new Page<Map<String, Object>>();
        page.setTotalCount(getTotal(sql));
        page.setPageNo(pageNumber);
        page.setPageSize(pageSize);
        Query query = getSession().createSQLQuery(sql);
        List<Map<String, Object>> list = (List<Map<String, Object>>) query
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize).list();
        page.setResult(list);
        return page;
    }

    /**
     * 取得查询的总数
     *
     * @param sql
     * @return
     * @author 彭仁夔于2015年12月28日下午4:29:40创建
     */
    @Override
    public int getTotal(String sql) {
        String total = "SELECT count(1) as total from (" + sql + ") s ";
        return queryForInt(total);
    }


}
