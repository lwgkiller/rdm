package com.redxun.core.elastic;

public class PageResult<T> {

	private T t;
	
	private int page=1;
	
	private int pageSize=20;
	
	private int total=0;
	
	

	public T getData() {
		return t;
	}

	public void setData(T t) {
		this.t = t;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalPage() {
		int page= this.getTotal() / this.pageSize ;
		if(this.getTotal() % this.pageSize!=0){
			page++;
		}
		return page;
	}

	
	
}
