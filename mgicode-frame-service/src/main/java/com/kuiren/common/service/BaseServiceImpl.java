package com.kuiren.common.service;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.kuiren.common.dao.GenericHibernateDao;
import com.kuiren.common.dao.JdbcTemplateDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.kuiren.common.auth.User;
import com.kuiren.common.dao.GenericDao;
import com.kuiren.common.dao.JdbcDao;
import com.kuiren.common.dao.hibernate.CriteriaUtil;
import com.kuiren.common.dao.hibernate.PropertySelector;
import com.kuiren.common.dao.hibernate.SqlSelector;
import com.kuiren.common.exception.BizRuntimeException;
import com.kuiren.common.exception.DAOException;
import com.kuiren.common.hibernate.EntityUtil;

import com.kuiren.common.page.Page;
import com.kuiren.common.util.BeanUtil;
import com.kuiren.common.util.ListUtil;
import com.kuiren.common.util.StringUtil;
import com.kuiren.common.vo.KeyValue;
import com.kuiren.common.vo.KeyValuePair;
import com.kuiren.common.vo.PropertyQuery;

/**
 * 版权归作者所有
 * <p>
 * 业务层接口基类
 *
 * @param <T>  实体Model
 * @param <PK> 主键类型，如Integer
 * @author 彭仁夔
 */
@Transactional(readOnly = true)
public class BaseServiceImpl<T, PK> implements BaseService<T, PK>, Serializable {
    protected final Log logger = LogFactory.getLog(getClass());

    protected GenericDao<T, PK> entityDao;
    protected JdbcDao<T, PK> jdbcDao;
    protected Class<?> objClz = null;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        entityDao=new GenericHibernateDao<T,PK>(sessionFactory) ;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
         jdbcDao = new JdbcTemplateDao<T, PK>(jdbcTemplate);
    }

    @Override
    public GenericDao<T, PK> getEntityDao() {
        return entityDao;
    }

    @Override
    public JdbcDao<T, PK> getJdbcDao() {
        return jdbcDao;
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(T entity) throws DAOException {
        entityDao.delete(entity);
        adviceUpdateCb("delete", entity);

    }

    @Transactional(readOnly = false)
    @SuppressWarnings("unchecked")
    @Override
    public void deleteById(PK id) {
        entityDao.deleteById((PK) id);
        adviceUpdateCb("deleteById", id);

    }

    @Transactional(readOnly = false)
    @Override
    public void deleteAll(List<T> entities) {
        entityDao.deleteAll(entities);
        adviceUpdateCb("deleteAll", entities);
    }

    @Override
    public T findByPK(PK id) {
        return entityDao.findByPK(id);
    }

    @Override
    public List<T> findByHql(String hql, Object... values) {
        return entityDao.findByHql(hql, values);
    }

    public Integer findIntByHql(String hql, Object... values) {
        Object o = entityDao.findObjByHql(hql, values);

        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        return null;
    }

    public String findStrByHql(String hql, Object... values) {
        Object o = entityDao.findObjByHql(hql, values);

        if (o instanceof String) {
            return ((String) o);
        }
        return null;

    }

    @Override
    public T findUniqueByHql(String hql, Object... values) {
        List<T> list = findByHql(hql, values);
        if (list != null && list.size() > 1)
            if (logger.isDebugEnabled()) {
                logger.debug("\n" + hql + ",本应该只查询一条数据！现在有多条，请注意！");
            }
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Page<T> find(Page<T> page, String hql, Object... values)
            throws DAOException {
        return entityDao.findByPageHql(page, hql, values);
    }

    @Override
    public List<T> findAll() throws DAOException {
        return entityDao.findAll();
    }

    /**
     * 载入所有实体进行分页
     *
     * @param page
     * @return
     */
    @Override
    public Page<T> findAll(Page<T> page) throws DAOException {
        return entityDao.findAll(page);
    }

    /**
     * 根据指定的属性名及逗号分隔的属性值找到所有实体
     */
    @Override
    public List<T> findByInStr(String name, String dotSepStr) {
        if (StringUtil.IsNullOrEmpty(dotSepStr)) {
            return null;
        }
        String arr[] = dotSepStr.split(",");
        return findByInArr(name, Arrays.asList(arr));
    }

    @Override
    public String findByInStr(String displayname, String name, String dotSepStr) {
        List<T> list = findByInStr(name, dotSepStr);
        return ListUtil.listToStr(list, displayname);
    }

    @Override
    public List<T> findByInArr(String propname, Collection collection) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion c = Restrictions.in(propname, collection);
        criterions.add(c);
        return entityDao.findAllByCriterions(criterions);
    }

    @Override
    public List<T> findByIds(String ids) {
        return findByInStr("id", ids);
    }

    @Override
    public List<KeyValue> getEnums(String methodName) {
        return BaseServiceUtil.getEnums(methodName, this.objClz);
    }

    /**
     * 根据指定属性及值查询实体（集合）
     *
     * @param propertyName 属性名
     * @param value        值
     * @return 实体（集合）
     */
    @Override
    public List<T> findByProperty(String propertyName, Object value)
            throws DAOException {
        return entityDao.findByProperty(propertyName, value);
    }

    /**
     * @author:彭仁夔 于2014年10月23日上午11:21:41创建
     */
    @Deprecated
    @Override
    public List<T> findByPropertyQuery(PropertyQuery... propertyQueries) {
        return findByPropertyQuery(null, null, propertyQueries);
    }

    /**
     * newsService.findByPropertyQuery( "top:desc,createTime:desc,newsId:desc",
     * 7, new PropertyQuery( "newsType", "=", ntNewsType), new PropertyQuery(
     * "state", "=", 2));
     *
     * @param kvs
     * @param top
     * @param propertyQueries
     * @return 彭仁夔 于 2016年3月21日 下午9:00:07 创建
     */
    @Deprecated
    @Override
    public List<T> findByPropertyQuery(String kvs, Integer top,
                                       PropertyQuery... propertyQueries) {
        // return entityDao.findByPropertyQuery(kvs, top, propertyQueries);
        return findByQuery(kvs, top, propertyQueries);
    }

    @Deprecated
    @Override
    public List<T> findByPropertyQueries(PropertyQuery... propertyQueries) {
        // return entityDao.findByPropertyQueryOnly(propertyQueries);
        return findByQuery(propertyQueries);
    }

    @Deprecated
    @Override
    public T findUniqueByPropertyQuery(String kvs,
                                       PropertyQuery... propertyQueries) {
        return findUniqueByQueryOrder(kvs, propertyQueries);

    }

    @Deprecated
    @Override
    public T findUniqueByPropertyQueries(PropertyQuery... propertyQueries) {
        return findUniqueByQuery(propertyQueries);
    }

    @Override
    public List<T> findByQuery(String kvs, Integer top,
                               PropertyQuery... propertyQueries) {
        return entityDao.findByPropertyQuery((String) kvs, (Integer) top,
                propertyQueries);
    }

    @Override
    public List<T> findByQuery(PropertyQuery... propertyQueries) {
        return findByQuery((String) null, (Integer) null, propertyQueries);
        // return entityDao.findByPropertyQueryOnly(propertyQueries);
    }

    @Override
    public List<T> findByQueryOrder(String kvs,
                                    PropertyQuery... propertyQueries) {
        return findByQuery((String) kvs, (Integer) null, propertyQueries);
    }

    @Override
    public T findUniqueByQueryOrder(String kvs,
                                    PropertyQuery... propertyQueries) {
        List<T> list = findByQuery((String) kvs, (Integer) 1, propertyQueries);
        return ListUtil.getFirst(list);

    }

    @Override
    public T findUniqueByQuery(PropertyQuery... propertyQueries) {
        return findUniqueByQueryOrder((String) null, propertyQueries);
        // return findUniqueByPropertyQuery(null, propertyQueries);
    }

    /**
     * 根据指定属性及值查询实体
     *
     * @param propertyName
     * @param value
     * @return 满足条件的第一个记录
     */
    @Override
    public T findUniqueByProperty(String propertyName, Object value)
            throws DAOException {
        return entityDao.findUniqueByProperty(propertyName, value);
    }

    @Override
    public PK findIdByProperty(String propertyName, Object value) {
        T t = findUniqueByProperty(propertyName, value);
        if (t == null)
            return null;
        return EntityUtil.getId(t);

    }

    @Override
    public PK findIdByQueryLike(String propertyName, String value) {
        T t = findUniqueByQuery(new PropertyQuery(propertyName, "like", value));
        if (t == null)
            return null;
        return EntityUtil.getId(t);

    }

    @Override
    public T findByQueryLike(String propertyName, String value) {
        T t = findUniqueByQuery(new PropertyQuery(propertyName, "like", value));
        return t;

    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcDao.getJdbcTemplate();
    }

    @Override
    public Session getSession() {
        return entityDao.getSession();
    }

    /**
     * merge 和saveOrUpdate不一样，当session中某持久化对象有id相同的两个纪录时，
     * 必须用merge，merge会在保存之前来合并记录，不然会报错。 合并记录后的动作和 saveOrUpdate一样
     *
     * @param entity 实体
     */
    @Deprecated
    @Transactional(readOnly = false)
    @Override
    public void merge(T entity) throws DAOException {
        entityDao.merge(entity);
        adviceUpdateCb("merge", entity);
    }

    @Transactional(readOnly = false)
    @Override
    public PK save(T entity) throws DAOException {

        PK pk = entityDao.save(entity);
        adviceUpdateCb("save", entity);
        return pk;
    }

    @Transactional(readOnly = false)
    @Override
    public void saveAll(List<T> entities) {
        entityDao.saveAll(entities);
        adviceUpdateCb("saveAll", entities);
    }

    /**
     * 保存或更新数据: 对象中如果有主键就修改, 如果没有主键就保存. 对于修改的实体，一般先通过find(id)函数找到原有
     * 实体，之后把修改的部分属性更新到原有实体上，然后进行保存
     *
     * @param entity 实体
     */
    @Transactional(readOnly = false)
    @Override
    public void saveOrUpdate(T entity) throws DAOException {
        entityDao.saveOrUpdate(entity);
        adviceUpdateCb("saveOrUpdate", entity);

    }

    @Transactional(readOnly = false)
    @Override
    public void updateSql(String sql) {
        // SQLQuery sqlQuery = getSession().createSQLQuery(sql);
        // sqlQuery.executeUpdate();
        updateBySql(sql);
        adviceUpdateCb("updateSql", sql);
    }

    @Transactional(readOnly = false)
    @Override
    public void updateBySql(String sql) {
        SQLQuery sqlQuery = getSession().createSQLQuery(sql);
        sqlQuery.executeUpdate();
        adviceUpdateCb("updateBySql", sql);
    }

    /**
     * 更新实体
     *
     * @param entity 实体
     */
    @Transactional(readOnly = false)
    @Override
    public void update(T entity) throws DAOException {
        entityDao.update(entity);
        adviceUpdateCb("update", entity);
    }

    /**
     * 更新实体(集合）
     *
     * @param entities
     */
    @Transactional(readOnly = false)
    @Override
    public void updateAll(List<T> entities) throws DAOException {
        entityDao.updateAll(entities);
        adviceUpdateCb("updateAll", entities);

    }

    @Override
    public void updateByProperty(List<KeyValuePair> list,
                                 List<KeyValuePair> where) {
        entityDao.updateByProperty(list, where);
        adviceUpdateCb("updateByProperty", list, where);
    }

    @Override
    public void updateByProperty(List<KeyValuePair> list, String wherename,
                                 Object wherevalue) {
        List<KeyValuePair> where = new ArrayList<KeyValuePair>();
        where.add(new KeyValuePair(wherename, wherevalue));
        updateByProperty(list, where);
        adviceUpdateCb("updateByProperty", list, wherename, wherevalue);

    }

    @Override
    public void updateByProperty(String name, Object value, String wherename,
                                 Object wherevalue) {
        List<KeyValuePair> list = new ArrayList<KeyValuePair>();
        list.add(new KeyValuePair(name, value));
        updateByProperty(list, wherename, wherevalue);
        adviceUpdateCb("updateByProperty", name, value, wherename, wherevalue);
    }

    @Transactional(readOnly = false)
    @SuppressWarnings("unchecked")
    @Override
    public void updateEmit(T entity, boolean emptyStrEmit) {
        updateEmit(entity, emptyStrEmit, false);
        adviceUpdateCb("updateEmit", emptyStrEmit);

    }

    @Transactional(readOnly = false)
    @SuppressWarnings("unchecked")
    @Override
    public void updateEmit(T entity, boolean emptyStrEmit, boolean idnulladd) {

        // PK id = getId(entity);
        PK id = EntityUtil.getId(entity);
        if (id == null || StringUtil.IsNullOrEmpty(id.toString())) {
            if (idnulladd == true) {
                id = save(entity);
                return;
            } else
                throw new RuntimeException("更新必须指定ID值");
        }

        T old = findByPK(id);
        if (old == null) {
            throw new RuntimeException("根据" + id + "找不到对象");
        }

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(objClz);
            PropertyDescriptor[] propertyDescriptors = (beanInfo)
                    .getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; ++i) {
                PropertyDescriptor propertyDescriptor = propertyDescriptors[i];

                Method method = propertyDescriptor.getReadMethod();
                Method write = propertyDescriptor.getWriteMethod();
                if ((method == null) || (write == null))
                    continue;
                try {
                    Object o1 = method.invoke(entity, new Object[0]);
                    if (o1 == null) {
                        continue;
                    }
                    if (o1 instanceof String && ("".equals((String) o1))
                            && (emptyStrEmit == true)) {// 空字符串

                    } else {
                        System.out.println("设定了" + propertyDescriptor.getName()
                                + "值：" + o1);

                        write.invoke(old, new Object[]{o1});
                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

        try {
            String string = new Gson().toJson(old);// new JsonCreator().build(old);
            System.out.println(string);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.update(old);
        // updateEmit(T entity, boolean emptyStrEmit, boolean idnulladd)
        adviceUpdateCb("updateEmit", entity, emptyStrEmit, idnulladd);
    }

    @Override
    public int queryForInt(String sql) {
        return getJdbcTemplate().queryForObject(sql, Integer.class);
        // return jdbcDao.getJdbcTemplate().query.queryForInt(sql);
    }

    @Override
    public Object queryForObject(String sql, Class clz) {
        return jdbcDao.getJdbcTemplate().queryForObject(sql, clz);
    }

    @Override
    public Object queryForObject(String sql) {
        return findObjBySql(sql, null);
        // return new SqlSelector(getSession()).queryForObject(sql,nu);
    }

    @Override
    public Map queryForMap(String sql) {
        try {

            return jdbcDao.getJdbcTemplate().queryForMap(sql);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public void deleteByProperty(String name, Object value) {
        entityDao.deleteByProperty(name, value);
        adviceUpdateCb("deleteByProperty", name, value);

    }

    @Transactional(readOnly = false)
    @Override
    public void deleteByProperties(KeyValuePair... kvs) {
        entityDao.deleteByProperties(kvs);
        adviceUpdateCb("deleteByProperties", kvs);
    }

    @Override
    public List<T> search(Page<T> entityPage, T entity) {
        throw new RuntimeException("没有实现");
    }

    @Override
    public List<T> search(T entity) {
        return search(null, entity);
    }

    @Override
    public List<Map<String, Object>> findMapsBySql(String sql) {
        return findMapsBySql(sql, null);
    }

    @Override
    public List<Map<String, Object>> findMapsBySql(String sql,
                                                   List<Object> parms) {
        return new SqlSelector(getSession()).findMapsBySql(sql, parms);
    }

    @Override
    public <M> List<M> findBySql(String sql, Class<M> clz) {
        return findBySql(sql, clz, (String) null);
    }

    @Override
    public <M> List<M> findBySql(String sql, Class<M> clz, String alias) {
        // String sql = " select {a.*} from t_ast_asset_type a ";
        // return new SqlSelector(getSession()).findBySql(sql, clz, alias);
        return findBySql(sql, clz, (String) alias, (List<Object>) null);
    }

    @Override
    public <M> List<M> findBySql(String sql, Class<M> clz, List<Object> parms) {
        return findBySql(sql, clz, null, (List<Object>) parms);
    }

    @Override
    public <M> List<M> findBySql(String sql, Class<M> clz, String alias,
                                 List<Object> parms) {
        return new SqlSelector(getSession()).findListBySql(sql, clz, alias,
                (List<Object>) parms);
    }

    @Override
    public <M> List<M> findBySql(String sql, Class<M> clz, String alias,
                                 Map<String, Object> parms) {
        return new SqlSelector(getSession()).findListBySql(sql, clz, alias,
                (Map<String, Object>) parms);
    }

    @Override
    public String findInStrBySql(String sql, List<Object> parms) {
        List<Map<String, Object>> list = findMapsBySql(sql, parms);
        return findInStrBySql(sql, parms, null);
    }

    @Override
    public String findInStrBySql(String sql, List<Object> parms,
                                 String fieldName) {
        List<Map<String, Object>> list = findMapsBySql(sql, parms);
        return BaseServiceUtil.joinBySingleQuote(list, fieldName);
    }

    @Override
    public String findJoinBySql(String sql, List<Object> parms) {
        List<Map<String, Object>> list = findMapsBySql(sql, parms);
        return findInStrBySql(sql, parms);
    }

    @Override
    public Object findObjBySql(String sql) {
        return findObjBySql(sql, null);
    }

    @Override
    public Object findObjBySql(String sql, List<Object> parms) {
        return findObjBySql(sql, parms, null);
    }

    @Override
    public Integer findIntBySql(String sql, List<Object> parms) {
        return (Integer) findObjBySql(sql, parms, Integer.class);
    }

    @Override
    public Float findFloatBySql(String sql, List<Object> parms) {
        return (Float) findObjBySql(sql, parms, Float.class);
    }

    @Override
    public String findStringBySql(String sql, List<Object> parms) {
        return (String) findObjBySql(sql, parms, String.class);
    }

    @Override
    public Object findObjBySql(String sql, List<Object> parms, Class clz) {
        return new SqlSelector(getSession()).queryForObject(sql, parms, clz);

    }

    @Override
    public <T1> List<T1> getTreeData(String tableName, String id, String pid,
                                     Class<T1> clz, boolean containSelf, String pidvalue) {

        return new SqlSelector(getSession()).setJdbcTemplate(getJdbcTemplate())
                .findNestedEnities(tableName, id, pid, clz, containSelf,
                        pidvalue);
    }

    @Override
    public List<T> getTreeData(String id, String pid, boolean containSelf,
                               String pidvalue) {
        T entity = findByPK((PK) id);
        Class<T> clz = (Class<T>) entity.getClass();
        String tableName = BeanUtil.getTableName(clz);
        if (StringUtil.IsNullOrEmpty(pidvalue)) {
            return findAll();
        }
        return getTreeData(tableName, id, pid, clz, containSelf, pidvalue);
    }

    public Criterion buildCondition(String dotsepStr, String filedname) {
        return CriteriaUtil.buildArrInCondition(dotsepStr, filedname);
    }

    /**
     * 根据逗号分隔的字符串查询是否在数据库中对应字段是否存在其中指定的字符串<br>
     * 主要用来DICTS这种情况，如A用户有擅长：aa,bb,cc,dd,A用户有擅长：aa,bb,dd<br>
     * 现在查询擅长aa,bb,dd的用户
     *
     * @param cs
     * @param filedname
     * @return 彭仁夔 于 2016年3月29日 下午1:49:54 创建
     */
    public Criterion buildCondition(Collection<String> cs, String filedname) {
        return CriteriaUtil.buildArrInCondition(cs, filedname);
    }

    public Class<?> getObjClz() {
        if (objClz == null) {
            throw new BizRuntimeException("该类对应的实体类找不到");
        }
        return objClz;
    }

    @Override
    public Criteria getCriteria(Class c) {
        Criteria criteria = getSession().createCriteria(c);
        return criteria;
    }

    @Override
    public Criteria getCriteria() {
        return getCriteria(getObjClz());
    }

    @Override
    public PropertySelector getPropertySelector() {
        return new PropertySelector(getSession(), getObjClz());
    }

    @Override
    public String getUserName() {
        return getCurrentUser().getUserName();
    }

    @Override
    public User getCurrentUser() {
        return BaseServiceUtil.getCurrentUser();
    }

    @Override
    public String getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    @Override
    public String getCurrentUserName() {
        return getUserName();
    }

    @Override
    public Object getCurrentUserOrg() {
        User u = getCurrentUser();
        return u.getVal(User.ORG);
    }

    @Override
    public String getCurrentUserOrgCode() {
        Object object = getCurrentUserOrg();
        return (String) BeanUtil.getFieldValue(object, "id");
    }

    @Override
    public List<?> getCurrentUserResources() {
        User u = getCurrentUser();
        return (List<?>) u.getVal(User.RES_LIST);
    }

    public void adviceUpdateCb(String methodname, Object... objects) {
    }

    ;

}
