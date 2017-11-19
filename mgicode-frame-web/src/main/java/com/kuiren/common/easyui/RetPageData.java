package com.kuiren.common.easyui;

import com.kuiren.common.page.Page;

public class RetPageData extends RetData {

	private int totalCount = -1;

	private int totalPages = -1;

	protected int pageNo = 1;

	protected int pageSize = -1;
	
	

	public static RetPageData convertPage(Page<?> pages) {

		RetPageData rpd = new RetPageData();
		return convertPage(pages, rpd);
	}

	public static RetPageData convertPage(Page<?> pages, RetPageData rpd) {
		if (rpd == null) {
			rpd = new RetPageData();
		}
		if (pages == null) {
			return rpd;
		} 

		rpd.setData(pages.getResult());
		rpd.setPageNo(pages.getPageNo());
		rpd.setPageSize(pages.getPageSize());
		rpd.setTotalCount(pages.getTotalCount());
		rpd.setTotalPages(pages.getTotalPages());

		return rpd;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
