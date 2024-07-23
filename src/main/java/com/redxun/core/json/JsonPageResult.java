package com.redxun.core.json;

import java.util.List;

/**
 *
 * <pre>
 * 描述：显示JSON分页结果
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2014年9月1日-下午6:42:25
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public class JsonPageResult<T> {

    /**
     * 总记录数
     */
    private Integer total = 0;
    /**
     * 一页的数据
     */
    private List<T> data;
    /*
     * 是否成功
     */
    private Boolean success;
    /**
     * 提示消息
     */
    private String message;

    public JsonPageResult() {

    }
    
    public JsonPageResult(Boolean success) {
    	this.success=success;
    }

    public JsonPageResult(List<T> dataList, Integer totalCount) {
        this.data = dataList;
        this.total = totalCount;
    }
    
    public JsonPageResult(Boolean success ,List<T> dataList,Integer totalCount,String message){
    	this.success=success;
    	this.data=dataList;
    	this.total=totalCount;
    	this.message=message;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
