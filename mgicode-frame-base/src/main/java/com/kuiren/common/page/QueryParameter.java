package com.kuiren.common.page;

/**
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */
public class QueryParameter implements java.io.Serializable {
    /**
     * 升序
     */
    public static final String ASC = "asc";
    /**
     * 降序
     */
    public static final String DESC = "desc";

    /**
     * 当前页的序号
     */
    protected int pageNo = 1;

    /**
     * 页面大小
     */
    protected int pageSize = -1;
    /**
     * 按asc或desc来进行排序
     */
    protected String orderBy = null;
    /**
     * 排序的方式 ，默认是ASC
     */
    @Deprecated
    protected String order = ASC;
    /**
     * 是否自动计数
     */
    protected boolean autoCount = false;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isPageSizeSetted() {
        return pageSize > -1;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getFirst() {
        if (pageNo < 1 || pageSize < 1)
            return 0;
        else
            return ((pageNo - 1) * pageSize);
    }

    public boolean isFirstSetted() {
        return (pageNo > 0 && pageSize > 0);
    }

    @Deprecated
    public String getOrderBy() {
        return orderBy;
    }

    @Deprecated
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Deprecated
    public boolean isOrderBySetted() {

//		return StringUtils.isNotBlank(orderBy);
        return !isNullOrEmpty(orderBy);
    }

    @Deprecated
    public String getOrder() {
        return order;
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

    @Deprecated
    public void setOrder(String order) {
        if (ASC.equalsIgnoreCase(order) || DESC.equalsIgnoreCase(order)) {
            this.order = order.toLowerCase();
        } else
            throw new IllegalArgumentException(
                    "order should be 'desc' or 'asc'");
    }

    public boolean isAutoCount() {
        return autoCount;
    }

    public void setAutoCount(boolean autoCount) {
        this.autoCount = autoCount;
    }
}
