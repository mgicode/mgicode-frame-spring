package com.kuiren.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
//import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.transaction.annotation.Transactional;

import com.kuiren.common.auth.AuthConstants;
import com.kuiren.common.auth.User;
import com.kuiren.common.code.ICodeBuilder;
import com.kuiren.common.dao.hibernate.PropertySelector;
import com.kuiren.common.easyui.EasyUIServlet;

import com.kuiren.common.easyui.EuiPage;
import com.kuiren.common.easyui.JsonRequest;
import com.kuiren.common.easyui.JsonRequestContext;
import com.kuiren.common.easyui.RetData;
import com.kuiren.common.hibernate.BaseEntity;
import com.kuiren.common.json.JsonCreator;

import com.kuiren.common.page.Page;
import com.kuiren.common.service.BaseService;
import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.EnumUtil;
import com.kuiren.common.util.StringUtil;
import com.kuiren.common.vo.KeyValue;

/**
 * @param <T>
 * @param <PK>
 * @author 彭仁夔 于2014年10月23日上午10:29:29创建
 */
public abstract class JsonRequestBaseAction<T, PK> {

    //public static JsonHelper jackson = JsonHelper.buildNonDefaultBinder();
    public static final int PAGE_SIZE = 10;

    protected BaseService<T, PK> entityService;

    protected ICodeBuilder<T, PK> codeBuilder;

    public abstract void setEntityService(BaseService<T, PK> entityService);// {
    // this.entityService = entityService;
    // }

    @Transactional(readOnly = false)
    public String add(JsonRequest req) {
        RetData retMsg = new RetData();
        try {
            String json = req.getJson();

            T obj = (T) new Gson().fromJson(json, entityService.getObjClz());//jackson.fromJson(json, entityService.getObjClz());
            setDateAndSortedNum(obj);
            PK id = entityService.save(obj);

            retMsg.setSuccess(true);
            retMsg.setMsg(id + "");

            String ret = new JsonCreator().build(retMsg);
            System.out.println(ret);
            return ret;
        } catch (Exception e) {
            retMsg.setSuccess(false);
            retMsg.setMsg(e.getMessage());
            e.printStackTrace();
            String ret = new JsonCreator().build(retMsg);
            return ret;
        }
    }

    public String get(JsonRequest req) {
        return getById(req);
    }

    @Transactional(readOnly = false)
    public String modefy(JsonRequest req) {

        RetData retMsg = new RetData();
        try {
            String json = req.getJson();
            T obj = (T) new Gson().fromJson(json, entityService.getObjClz());//jackson.fromJson(json, entityService.getObjClz());
            // this.saveOrUpdate(obj);
            entityService.update(obj);

            retMsg.setSuccess(true);
            retMsg.setMsg("修改成功");

            String ret = new JsonCreator().build(retMsg);
            System.out.println(ret);
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
    public String modefyEmit(JsonRequest req) {

        RetData retMsg = new RetData();
        try {
            String json = req.getJson();
            T obj = (T) new Gson().fromJson(json, entityService.getObjClz());//jackson.fromJson(json, entityService.getObjClz());
            entityService.updateEmit(obj, false, false);

            retMsg.setSuccess(true);
            retMsg.setMsg("修改成功");

            String ret = new JsonCreator().build(retMsg);
            System.out.println(ret);
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
    public void saveOrUpdateEmit(T obj) {

        setDateAndSortedNum(obj);

        entityService.updateEmit(obj, false, false);

    }

    @Transactional(readOnly = false)
    public String del(JsonRequest req) {
        RetData retMsg = new RetData();
        try {
            PK id = (PK) req.paramAsStr("id");
            entityService.deleteById((PK) id);
            retMsg.setSuccess(true);
            retMsg.setMsg(id + "");
        } catch (Exception e) {
            retMsg.setSuccess(false);
            retMsg.setMsg(e.getMessage());
            e.printStackTrace();
        }
        String ret = new JsonCreator().build(retMsg);
        System.out.println(ret);
        return ret;
    }

    public String getById(JsonRequest req) {
        RetData retMsg = new RetData();
        try {
            PK id = (PK) req.paramAsStr("id");
            T e = entityService.findByPK(id);

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

    public String load(JsonRequest req) {
        PK id = (PK) req.paramAsStr("id");
        T e = entityService.findByPK(id);

        String ret = new JsonCreator().build(e);
        System.out.println(ret);
        return ret;

    }

    /**
     * @author:彭仁夔 于2014年10月28日下午10:03:12创建
     * @see com.kuiren.common.BasicService#fetch(com.kuiren.common.easyui.JsonRequest)
     */

    public String fetch(JsonRequest req) {

        String json = req.getJson();
        @SuppressWarnings("unchecked")
        T e = (T) new Gson().fromJson(json, entityService.getObjClz());//jackson.fromJson(json, entityService.getObjClz());
        List list = entityService.search(null, e);
        if (list == null || list.size() < 1) {
            return "{}";

        } else {
            String ret = new JsonCreator().build(list.get(0));
            System.out.println(ret);
            return ret;
        }
    }

    public String datalist(JsonRequest req) {
        String json = req.getJson();
        T e = (T) new Gson().fromJson(json, entityService.getObjClz());//jackson.fromJson(json, entityService.getObjClz());
        List list = entityService.search(null, e);
        String ret = new JsonCreator().build(list);
        System.out.println(ret);
        return ret;
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

    public String pagedatalist(JsonRequest req) {

        EuiPage up = euiPageList(req);
        if (up == null || up.getRows() == null || up.getRows().size() < 1) {
            return " {\"total\":0,\"rows\":[]}";
        }
        String str = new JsonCreator().build(up);

        return str;

    }

    public EuiPage euiPageList(JsonRequest req) {
        EuiPage up = new EuiPage();
        try {
            String json = req.getJson();
            Page<T> page = buildSearchPage(req);
            T e = (T) new Gson().fromJson(json, entityService.getObjClz()); //jackson.fromJson(json, entityService.getObjClz());
            entityService.search(page, e);

            if (page != null) {
                up.setTotal(page.getTotalCount());
                up.setRows(page.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return up;
    }

    public String enums(JsonRequest req) {

        String ret = "{}";
        String str = (String) req.getMap().get("fname");
        if (StringUtil.IsNotNullOrEmpty(str)) {
            List<KeyValue> list = entityService.getEnums(str);
            ret = new JsonCreator().build(list);
            System.out.println(ret);
        }
        return ret;
    }

    public String enumMap(JsonRequest req) {

        String ret = "{}";
        String str = (String) req.getMap().get("fname");
        if (StringUtil.IsNotNullOrEmpty(str)) {

            Map<Integer, String> map = EnumUtil.getEnumMap(entityService
                    .getObjClz().getName() + "_" + str);
            ret = new JsonCreator().build(map);
            System.out.println(ret);
        }
        return ret;// "window.enum" + str + "=" + ret;
    }

    public String enumMapJs(JsonRequest req) {

        String ret = "{}";
        String str = (String) req.getMap().get("fname");
        if (StringUtil.IsNotNullOrEmpty(str)) {

            Map<Integer, String> map = EnumUtil.getEnumMap(entityService
                    .getObjClz().getName() + "_" + str);
            ret = new JsonCreator().build(map);
            System.out.println(ret);
            return "window.enum_" + str + "=" + ret;
        }
        return "";
    }

    @Transactional(readOnly = false)
    public String addOrModefy(JsonRequest req) {

        RetData retMsg = new RetData();
        String json = req.getJson();

        T obj = (T) new Gson().fromJson(json, entityService.getObjClz());//jackson.fromJson(json, entityService.getObjClz());

        setDateAndSortedNum(obj);

        entityService.updateEmit(obj, false, true);

        retMsg.setSuccess(true);
        retMsg.setData(obj);
        retMsg.setMsg("修改成功");
        return new Gson().toJson(retMsg);// jackson.toJson(retMsg);

    }

    public String errorMsg(String msg) {

        RetData retMsg = new RetData();
        retMsg.setSuccess(false);
        retMsg.setMsg(msg);
        return new Gson().toJson(retMsg);//jackson.toJson(retMsg);
    }

//
//    public void writeExcelWorkbook(String name, Workbook wb) {
//
//        try {
//            HttpServletResponse resp = JsonRequestContext.getResponse();
//            resp.setContentType("application/octet-stream");
//            resp.setHeader("Content-disposition", "attachment;filename="
//                    + new String((name).getBytes(), "iso-8859-1"));
//            OutputStream out = resp.getOutputStream();
//
//            wb.write(out);
//            out.close();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void downFile(File file) throws FileNotFoundException {

        HttpServletResponse response = JsonRequestContext.getResponse();

        InputStream fileInputStream = new FileInputStream(file);

        String fileName = null;
        try {
            fileName = URLDecoder.decode(file.getName(), "UTF-8");
        } catch (UnsupportedEncodingException e4) {
            e4.printStackTrace();
        }
        try {
            fileName = new String(fileName.getBytes(), "iso8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename="
                + fileName);
        BufferedInputStream bis = new BufferedInputStream(fileInputStream);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            bos.flush();

        } catch (Exception e1) {
            if ("ClientAbortException".equals(e1.getClass().getSimpleName())) {
                System.out.println("----->Socket异常，可能原因是客户端中断了附件下载。");
            } else {

            }
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e2) {
                // log.error(e.getMessage(), e);
            }

            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e3) {
                e3.printStackTrace();
            }

        }

    }

    public int getInt(String s) {
        if (StringUtil.IsNullOrEmpty(s))
            return 0;
        if (StringUtil.isNum(s)) {

            return Integer.parseInt(s);
        }
        return 0;
    }

    @Transactional(readOnly = false)
    // @Override
    public String adjustOrder(JsonRequest req) {
        // String jsonString = req.paramAsStr("m");
        String sortedField = req.paramAsStr("fname");
        String pidField = req.paramAsStr("pidField");
        List list = (List) req.getMap().get("nodes");
        if (list != null) {
            for (Object object : list) {
                if (object instanceof Map) {
                    Map map = (Map) object;
                    String id = (String) map.get("id");
                    if (id.equals("all_total")) {// 修改父亲节点
                        String sourceid = (String) map.get("sourceid");
                        String pid = (String) map.get("pid");
                        // String pid = (String) map.get(pidField);
                        entityService.updateByProperty(pidField, pid, "id",
                                sourceid);
                        // try {
                        // if (codeBuilder != null) {
                        // T p = entityService.findByPK((PK) pid);
                        // List<T> data = entityService.getTreeData(
                        // sourceid, pidField, true, pid);
                        // codeBuilder.buildLevel(data, p, true);
                        // }
                        // } catch (Exception e) {
                        // e.printStackTrace();
                        // }

                    } else {// 调整兄弟节点的所有排序
                        Integer orderid = (Integer) map.get("ordernum");
                        // TAssetType
                        entityService.updateByProperty(sortedField, orderid,
                                "id", id);
                    }

                }
            }
        }

        RetData retData = new RetData();
        retData.setSuccess(true);
        return new JsonCreator().build(retData);
    }

    public PropertySelector getPropertySelector() {
        return entityService.getPropertySelector();
    }

    public String getUserName() {
        return getCurrentUser().getUserName();
    }

    public User getCurrentUser() {
        return entityService.getCurrentUser();
    }

    public String getCurrentUserName() {
        return getUserName();
    }

    public Object getCurrentUserOrg() {

        User u = getCurrentUser();
        return u.getVal(User.ORG);

    }

    public List<?> getCurrentUserResources() {
        User u = getCurrentUser();
        return (List<?>) u.getVal(User.RES_LIST);
    }

    public void setDateAndSortedNum(T obj) {
        if (obj == null)
            return;
        try {
            if (BeanUtil.readFieldValue(obj, "id") == null) {
                if (obj instanceof BaseEntity) {
                    BaseEntity be = (BaseEntity) obj;
                    be.setCreateTime(new Date());
                    be.setCreateUser(getUserName());

                    // if (codeBuilder != null) {
                    // be.setSortedNum(Integer.parseInt(codeBuilder
                    // .buildSortedNum(obj.getClass().getSimpleName())));
                    // }
                }
            }
        } catch (Exception e) {
        }

    }
}
