package com.kuiren.common.easyui;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.kuiren.common.context.Executor;
import com.kuiren.common.exception.BizRuntimeException;
//import com.kuiren.common.json.JsonHelper;
import com.kuiren.common.util.JacksonHelper;
import com.kuiren.common.util.StringUtil;
import com.kuiren.common.vo.AjaxHtmlVo;

public class EasyUIServlet extends HttpServlet {
    //private static JsonHelper jackson = JsonHelper.buildNonDefaultBinder();

    // use JsonRequestContext
    // @Deprecated
    // public static ThreadLocal<HttpServletRequest> servletReqLocal = new
    // ThreadLocal<HttpServletRequest>();

    public EasyUIServlet() {
    }

    public void init() throws ServletException {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    public String convertReqQuery(HttpServletRequest request) {
        String postStr = "{}";
        try {
            // 把查询参数转换为Json,支持点串的方式
            Map map = MapUtil.fetchFromRequestParameterMap(
                    request.getParameterMap(), true, true);
            MapUtil.remove("service,s,serviceMethod,m", map);
            postStr = new Gson().toJson(map);//jackson.toJson(map);
            return postStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postStr;

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object resp = null;
        String result = "";

        // servletReqLocal.set(request);

        final String serviceName = JsonRequestUtil.getParams(request,
                "service,s");
        final String serviceMethod = JsonRequestUtil.getParams(request,
                "serviceMethod,m");

        String jsName = JsonRequestUtil.getParams(request, "_js,_jsName");
        String _text = JsonRequestUtil.getParams(request, "_t,_text");
        // save,search
        final String _type = JsonRequestUtil.getParams(request, "_type");

        if (log.isDebugEnabled()) {
            log.debug("\n请求的服务名和方法名：" + serviceName + "-->" + serviceMethod);
        }

        // String postStr = String.valueOf(getPostData(request, "params"));
        String postStr = String.valueOf(ServletUtil.getPostData(request,
                "params"));

        // 把查询参数转换为Json
        if (StringUtil.IsNullOrEmpty(postStr)) {
            postStr = convertReqQuery(request);
        }

        if (log.isDebugEnabled()) {
            log.debug("\n参数：" + postStr);
        }

        try {
            if (serviceMethod == null)
                throw new BizRuntimeException("9901");
            if (serviceName == null)
                throw new BizRuntimeException("9902");
            if (postStr == null || "".equals(postStr) || "null".equals(postStr)) {
                postStr = "{}";
            }

            Map map = JsonRequestUtil.getMapFromJson(postStr, true);

            if ("save".equals(_type)) {
                map = JsonRequestUtil.removeEmptyObj(map);
            }

            final JsonRequest req = new JsonRequest(map);
            req.setServiceName(serviceName);
            req.setMethodName(serviceMethod);
            JsonRequestContext.setJsonRequest(req);
            JsonRequestContext.setReqestAndSession(request,
                    request.getSession());
            JsonRequestContext.setResponse(response);

            /******** pengrk modefy at 2015.12.23 ***********/
            Object[] clzMethod = JsonRequestUtil.dyncCalClzMethod(serviceName,
                    serviceMethod, JsonRequestUtil.TYPE_EASYUI_JSON);

            final Object service = clzMethod[0];
            // //final String methodName = (String) clzMethod[1];
            final Method method = (Method) clzMethod[2];

            Executor e = new Executor() {
                public Object execute() throws Throwable {
                    return JsonRequestUtil.callMethod(service, method,
                            new Object[]{req});

                }

            };
            resp = JsonRequestUtil.transactionExcute(e);
            // }

            Map<String, Object> tmpres = null;
            if (resp instanceof String) {
                result = (String) resp;
            } else if (resp instanceof AjaxHtmlVo) {
                result = ((AjaxHtmlVo) resp).getContents();
                if (log.isDebugEnabled()) {
                    log.debug(result);
                }

                _text = "1";
                jsName = null;
            } else if (resp != null & resp instanceof JsonResponse) {
                JsonResponse resp1 = (JsonResponse) resp;
                tmpres = resp1.getMap();
                if (resp1.rtnCode != null) {
                    tmpres.put("rtnCode", resp1.rtnCode);
                } else {
                    tmpres.put("rtnCode", "2000");
                }
                if (resp1.rtnMsg != null) {
                    tmpres.put("rtnMsg", resp1.rtnMsg);
                }
                result = new Gson().toJson(tmpres);//fJacksonHelper.getJsonFromMap(tmpres);
            } else if (resp != null) {
                // result = resp.toString();
                result = new Gson().toJson(result);// jackson.toJson(result);
            } else {
                tmpres = new HashMap();
                tmpres.put("rtnCode", "2000");
                tmpres.put("rtnMsg", "\u8C03\u7528\u6210\u529F");
                resp = new JsonResponse(tmpres);
            }

            // 转换为Js
            if (StringUtil.IsNotNullOrEmpty(jsName)) {
                result = "window." + jsName + "=" + result + ";";
            }
        } catch (Throwable e) {
            e.printStackTrace();
            log.error((new StringBuilder("EasyUiServlet在调用"))
                    .append(serviceName).append("类中").append(serviceMethod)
                    .append("方法发生错误").toString());
            log.error(postStr);
            log.error(StringUtil.getFullErrorMessage(e));
            Map error = new HashMap();

            RetData retData = new RetData();
            retData.setSuccess(false);
            if (e instanceof InvocationTargetException) {
                InvocationTargetException ite = (InvocationTargetException) e;
                if (ite.getTargetException() instanceof BizRuntimeException) {
                    retData.setSuccess(true);
                    retData.setMsg(StringUtil.getFullErrorMessage(e));
                    result = new Gson().toJson(result);//jackson.toJson(retData);
                } else if (ite.getTargetException() instanceof DataIntegrityViolationException) {
                    retData.setSuccess(true);
                    retData.setMsg("该项不能删除，该项已经被使用");
                    result = new Gson().toJson(retData);//jackson.toJson(retData);
                } else {
                    retData.setMsg(e.getMessage());
                    result = new Gson().toJson(retData); //jackson.toJson(retData);
                }
            }

        }
        JsonRequestContext.clearContext();
        // System.out.println(result);
        if (log.isDebugEnabled()) {
            log.debug("\n返回的JSON：" + result);
        }
        if (StringUtil.IsNotNullOrEmpty(_text)) {
            response.setContentType("text/plain");
        } else {
            response.setContentType("text/json");
        }
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        out.print(result);
        out.flush();
        out.close();
        return;
    }

    // private StringBuffer getPostData(HttpServletRequest req, String
    // requestName) {
    // StringBuffer s = getData(req, "utf-8");
    // if (s.length() != 0)
    // return s;
    // if (requestName == null)
    // return s;
    // String xml = req.getParameter(requestName);
    // if (xml == null)
    // return s;
    // else
    // return new StringBuffer(xml);
    // }
    //
    // public StringBuffer getData(HttpServletRequest request, String enc) {
    // StringBuffer sb = new StringBuffer();
    // String s = null;
    // try {
    // BufferedReader br = new BufferedReader(new InputStreamReader(
    // request.getInputStream(), enc));
    // while ((s = br.readLine()) != null)
    // sb.append(s).append("\n");
    // br.close();
    // return sb;
    // } catch (IOException e) {
    // throw new RuntimeException((new StringBuilder("getXml error"))
    // .append(e).toString());
    // } catch (Exception e) {
    // throw new RuntimeException((new StringBuilder("getXml error"))
    // .append(e).toString());
    // }
    // }

    private static final Log log = LogFactory.getLog(EasyUIServlet.class);
    private static final long serialVersionUID = 1L;

}
