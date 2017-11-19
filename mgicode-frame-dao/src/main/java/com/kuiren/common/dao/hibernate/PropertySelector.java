package com.kuiren.common.dao.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.util.Assert;

import com.kuiren.common.exception.DAOException;
import com.kuiren.common.hibernate.Order;
import com.kuiren.common.page.Page;
import com.kuiren.common.util.StringUtil;
import com.kuiren.common.vo.PropertyQuery;

public class PropertySelector {

    // Projections提供了分组函数的查询方法：
    // rowCount() 查询记录总数量；
    // count(String propertyName) 统计某列数量；
    // countDistinct(String propertyName) 统计某列数量（排除重复）；
    // avg(String propertyName) 统计某列平均值；
    // sum(String propertyName) 对某列值求和；
    // max(String propertyName) 求某列最大值；
    // min(String propertyName) 求某列最小值。
    public final static int ROWCOUNT = 1;
    public final static int COUNT = 2;
    public final static int COUNTDISTINCT = 3;
    public final static int AVG = 4;
    public final static int SUM = 5;
    public final static int MAX = 6;
    public final static int MIN = 7;

    protected final Log logger = LogFactory.getLog(getClass());

    private Criteria criteria;

    private Page page;

    public PropertySelector(Session session, Class entityClass) {
        criteria = CriteriaUtil.createCriteria(session, entityClass);
    }

    public Criteria getCriteria() {

        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;

    }

    private List<Criterion> criterions = new ArrayList<Criterion>();

    private Map<String, String> aliasNameMap = new HashMap<String, String>();

    private List<Order> orderList = new ArrayList<Order>();

    private List<Projection> projectionList = new ArrayList<Projection>();

    private Integer firstResult = null;

    private Integer maxResults = null;

    /**
     * 对于
     *
     * @param propertyName
     * @param aliasName
     * @return 彭仁夔 于 2016年4月27日 下午3:55:20 创建
     */
    public PropertySelector addAliasName(String propertyName, String aliasName) {

        if (aliasNameMap.containsKey(propertyName)) {
            if (logger.isDebugEnabled()) {
                logger.debug("\n已经存在" + propertyName + "的别名"
                        + aliasNameMap.get(propertyName));
            }
        }
        aliasNameMap.put(propertyName, aliasName);

        return this;
    }

    public PropertySelector addOrder(Order o) {
        // PropertyQueryUtil.addOrder(o, criteria);
        orderList.add(o);
        return this;
    }

    public PropertySelector addOrders(List<Order> olist) {
        // PropertyQueryUtil.addOrder(o, criteria);
        orderList.addAll(olist);
        return this;
    }

    public PropertySelector addOrders(Map<String, String> map) {
        // PropertyQueryUtil.addOrder(o, criteria);
        List<Order> list = OrderUtil.getOrders(map);
        orderList.addAll(list);
        return this;
    }

    public PropertySelector addOrders(String kvs) {
        List<Order> list = OrderUtil.getOrders(kvs);
        orderList.addAll(list);
        return this;
    }

    public PropertySelector addQuery(PropertyQuery pq, boolean nullAdded) {
        if (pq == null) {
            return this;
        }
        if (nullAdded == false) {
            Object v = pq.getValue();
            if (v == null) {
                return this;
            }
            // ""字符串不处理
            if ((v instanceof String) && StringUtil.isNullOrEmpty(v + "")) {
                return this;
            }

        }
        if (logger.isDebugEnabled()) {

            logger.debug("\n创建Criterion：" + pq.getName() + "" + pq.getRelative()
                    + "" + pq.getValue());
        }
        criterions.add(PropertyQueryUtil.createCriterion(pq));
        return this;
    }

    public PropertySelector addQuery(PropertyQuery pq) {
        addQuery(pq, false);
        return this;
    }

    public PropertySelector addQueryIncludeNull(PropertyQuery pq) {
        addQuery(pq, true);
        return this;
    }

    public PropertySelector addQuery(String name, Object v) {
        PropertyQuery pq = new PropertyQuery(name, "=", v);
        addQuery(pq, false);
        return this;
    }

    public PropertySelector addQueryIncludeNull(String name, Object v) {
        PropertyQuery pq = new PropertyQuery(name, "=", v);
        addQuery(pq, true);
        return this;
    }

    public PropertySelector addQuery(String name, String relative, Object v) {
        PropertyQuery pq = new PropertyQuery(name, relative, v);
        addQuery(pq, false);
        return this;
    }

    public PropertySelector addQueryIncludeNull(String name, String relative,
                                                Object v) {
        PropertyQuery pq = new PropertyQuery(name, relative, v);
        addQuery(pq, true);
        return this;
    }

    public PropertySelector addCriterion(Criterion c) {
        criterions.add(c);
        return this;
    }

    public PropertySelector add(Object obj, Object propertyValue, Criterion c) {

        if (obj == null || propertyValue == null) {
            return this;
        }
        // 空字符串也不加入
        if (propertyValue != null
                && ("".equalsIgnoreCase((propertyValue + "").trim()))) {
            return this;
        }

        return addCriterion(c);
    }

    /**
     * // Projections提供了分组函数的查询方法：<br>
     * // rowCount() 查询记录总数量；<br>
     * // count(String propertyName) 统计某列数量；<br>
     * // countDistinct(String propertyName) 统计某列数量（排除重复）；<br>
     * // avg(String propertyName) 统计某列平均值；<br>
     * // sum(String propertyName) 对某列值求和；<br>
     * // max(String propertyName) 求某列最大值；<br>
     * // min(String propertyName) 求某列最小值。<br>
     *
     * @param type
     * @param propetyName
     * @return 彭仁夔 于 2016年4月27日 下午3:19:53 创建
     */
    public PropertySelector addCol(int type, String propetyName) {
        Projection projection = null;
        if (type == PropertySelector.AVG) {
            projection = Projections.avg(propetyName);
        } else if (type == PropertySelector.COUNT) {
            projection = Projections.count(propetyName);

        } else if (type == PropertySelector.COUNTDISTINCT) {
            projection = Projections.countDistinct(propetyName);

        } else if (type == PropertySelector.MAX) {
            projection = Projections.max(propetyName);

        } else if (type == PropertySelector.MIN) {
            projection = Projections.min(propetyName);

        } else if (type == PropertySelector.ROWCOUNT) {
            projection = Projections.rowCount();

        } else if (type == PropertySelector.SUM) {
            projection = Projections.sum(propetyName);
        }

        if (projection != null) {
            projectionList.add(projection);
        }
        return this;
    }

    public PropertySelector addCol(Projection projection) {
        if (projection != null) {
            projectionList.add(projection);
        }
        return this;

    }

    public PropertySelector addCol(String name, String aliasName) {
        if (StringUtil.isNullOrEmpty(name)) {
            return this;
        }
        Projection p = Property.forName(name);
        if (StringUtil.isNullOrEmpty(aliasName)) {
            // projectionList.add(p);
            addCol(p);
        } else {
            // projectionList.add(Projections.alias(p, aliasName));
            addCol(Projections.alias(p, aliasName));
        }
        return this;
    }

    public PropertySelector addCol(String name) {
        addCol(name, null);
        return this;
    }

    public PropertySelector addCols(String... names) {
        if (names == null)
            return this;
        for (String name : names) {
            addCol(name);
        }

        return this;
    }

    public PropertySelector addAliasCols(String... names) {
        if (names == null)
            return this;
        for (String name : names) {
            if (StringUtil.isNullOrEmpty(name)) {
                continue;
            }
            List<String> list = StringUtil.split(name, "[: ：#!]");
            if (list == null) {
                continue;
            }
            if (list.size() == 1) {
                addCol(list.get(0));
            } else if (list.size() == 2) {
                addCol(list.get(0), list.get(1));
            }
        }

        return this;
    }

    // // @Override
    // public Page<T> findByCriterions(Page<T> page, List<Criterion> criterions,
    // Map<String, List<Criterion>>... children) throws DAOException {
    // Assert.notNull(page);
    //
    // Criteria c = CriteriaUtil.createCriteria(getSession(), entityClass,
    // criterions);
    //
    // if (children != null && children.length > 0) {
    // CriteriaUtil.createChildCriterions(c, children);
    // }
    //
    // PageUtil.calPageCount(page, c);
    //
    // OrderUtil.calOrder(page, c);
    //
    // List result = c.list();
    // if (result != null && result.size() > 0
    // && !(result.get(0) instanceof Integer)) {
    // page.setResult(result);
    // }
    //
    // return page;
    //
    // }

    public PropertySelector setPage(Page page) {

        this.page = page;

        return this;
    }

    private void build() {

        // propertyName, String aliasName
        for (String propertyName : aliasNameMap.keySet()) {
            String aliasName = aliasNameMap.get(propertyName);
            getCriteria().createAlias(propertyName, aliasName);
        }
        // 多表查询采用别名的形式进行
        // 如： Restrictions.eq("alias.name", "v");
        for (Criterion c : criterions) {
            getCriteria().add(c);
        }

        for (Order c : orderList) {
            getCriteria().addOrder(c);
        }
        // 兼容原来的分页
        if (this.page != null) {
            PageUtil.calPageCount(page, getCriteria());
            OrderUtil.calOrder(page, getCriteria());
        }

        if (projectionList != null && projectionList.size() > 0) {

            ProjectionList pl = Projections.projectionList();
            for (Projection p : projectionList) {

                pl.add(p);
            }
            getCriteria().setProjection(pl);
        }

        if (getFirstResult() != null) {
            getCriteria().setFirstResult(getFirstResult());
        }

        if (getMaxResults() != null) {
            getCriteria().setMaxResults(getMaxResults());
        }

    }

    public List list() {
        build();
        // getCriteria().uniqueResult();
        return getCriteria().list();
    }

    public Page pagelist() {
        build();
        List result = getCriteria().list();
        if (result != null && result.size() > 0
                && !(result.get(0) instanceof Integer)) {
            page.setResult(result);
        }
        return page;

    }

    public Number queryForNumber() {
        build();
        return ((Number) getCriteria().uniqueResult());
    }

    public Float queryForFloat() {
        Number n = queryForNumber();
        if (n != null) {
            return n.floatValue();
        }
        return null;
    }

    public Integer queryForInteger() {
        Number n = queryForNumber();
        if (n != null) {
            return n.intValue();
        }
        return null;
    }

    ;

    public <T> T queryForObj(Class<T> clz) {
        build();
        return (T) getCriteria().setResultTransformer(
                Transformers.aliasToBean(clz)).uniqueResult();
    }

    public <T> List<T> queryForObjList(Class<T> clz) {
        build();
        return (List) getCriteria().setResultTransformer(
                Transformers.aliasToBean(clz)).list();

    }

    public Map<String, Object> queryUniqueToMap() {
        build();
        return (Map) getCriteria().setResultTransformer(
                Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult();
        // 返回值为map集合
        // 且为唯一值（只能返回一条数据）
    }

    /**
     * 将sql中的全部数据查出来，返回值为一个List<Map<String, Object>>
     *
     * @return
     * @author 彭仁夔于2015年12月28日下午4:00:53创建
     */

    public List<Map<String, Object>> queryToListMap() {
        build();
        return (List) getCriteria().setResultTransformer(
                Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    public Integer getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

}
