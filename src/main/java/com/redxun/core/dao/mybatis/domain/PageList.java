package com.redxun.core.dao.mybatis.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
/**
 * 
 * @author mansan
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 * @param <E>
 */
public class PageList<E> extends ArrayList<E> implements Serializable {

    private static final long serialVersionUID = 1412759446332294208L;
    
    private PageResult pageResult;

    public PageList() {}
    
	public PageList(Collection<? extends E> c) {
		super(c);
	}

	
	public PageList(Collection<? extends E> c,PageResult p) {
        super(c);
        this.pageResult = p;
    }

    public PageList(PageResult p) {
        this.pageResult = p;
    }


	/**
	 * 得到分页器，通过Paginator可以得到总页数等值
	 * @return
	 */
	public PageResult getPageResult() {
		return pageResult;
	}

	/**
	 * @param pageResult the pageResult to set
	 */
	public void setPageResult(PageResult pageResult) {
		this.pageResult = pageResult;
	}

	
}
