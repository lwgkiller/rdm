package com.redxun.core.query;

/**
 * 分页信息
 *
 * @author csx
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class Page implements IPage{
	
    /**
     * 最多显示页码数
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    //每页开始的索引号
    //public Integer startIndex = 0;
    //页码大小
    private Integer pageSize = DEFAULT_PAGE_SIZE;
    //总记录数
    private Integer totalItems = 0;
    //开始页码
    private Integer pageIndex=0;
    //忽略计算总记录数
    private boolean skipCountTotal=false;
    

    public Page() {
    }
    
    public Page(boolean skipCountTotal){
        this.skipCountTotal=skipCountTotal;
    }

    /**
     * Page构造函数
     * @param pageIndex 页码索引 从零开始
     * @param pageSize 每页记录数
     */
    public Page(Integer pageIndex, Integer pageSize) {
        this.pageIndex = pageIndex.intValue();
        this.pageSize = pageSize;
    }
    
    /**
     * Page构造函数
     * @param pageIndex 页码索引 从零开始
     * @param pageSize 每页记录数
     * @param skipCountTotal 是否忽略统计总记录数
     */
    public Page(Integer pageIndex,Integer pageSize,boolean skipCountTotal){
        
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

//    public Integer getStart() {
//        return start;
//    }
//
//    public void setStart(Integer start) {
//        this.start = start;
//    }
    
    public Integer getStartIndex(){
        return pageIndex*pageSize;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
        //this.start=pageIndex*pageSize;
    }
    
    public Integer getTotalPages(){
        int page=this.totalItems/this.pageSize;
        return this.totalItems%this.pageSize==0?page:page+1;
    }

    public boolean isSkipCountTotal() {
        return skipCountTotal;
    }

    public void setSkipCountTotal(boolean skipCountTotal) {
        this.skipCountTotal = skipCountTotal;
    }

}
