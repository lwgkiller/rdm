package com.redxun.core.entity;


/**
 * 
 * <pre> 
 * 描述：树型基础节点类
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:keith@mitom.cn
 * 日期:2014年5月5日-上午9:19:28
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public class BaseTreeNode {
	/**
	 * 节点ID
	 */
	protected String id = null;
	/**
	 * 父节点Id
	 */
	protected String parentId = null;
	/**
	 * 旧父节点ID
	 */
	protected String oldParentId = null;
	/**
	 * 是否为父节点
	 */
	protected Boolean isParent = null;
	/**
	 * 节点名
	 */
	protected String name = null;
	/**
	 * 节点Key
	 */
	protected String key=null;
	/**
	 * 是否展开
	 */
	protected Boolean open = Boolean.valueOf(false);
	/**
	 * url
	 */
	protected String url = null;
	/**
	 * 目标窗口ID
	 */
	protected String target = null;
	/**
	 * 关闭图标
	 */
	protected String iconClose = null;
	/**
	 * 打开图标
	 */
	protected String iconOpen = null;
	/**
	 * 图标
	 */
	protected String icon = null;
	
//	private String menuType = null;
	
	protected Boolean existed = null;
	/**
	 * 描述
	 */
	protected String descp;
	
	protected Boolean checked;
	protected Boolean nocheck;
	protected Boolean chkDisabled;
	protected Boolean drag;
	protected Boolean drop;
	protected Boolean clickExpand;
	protected String click;
	
	/**
	 * 序号
	 */
	protected Integer sn;
	
	public BaseTreeNode() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getOldParentId() {
		return oldParentId;
	}

	public void setOldParentId(String oldParentId) {
		this.oldParentId = oldParentId;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getIconClose() {
		return iconClose;
	}

	public void setIconClose(String iconClose) {
		this.iconClose = iconClose;
	}

	public String getIconOpen() {
		return iconOpen;
	}

	public void setIconOpen(String iconOpen) {
		this.iconOpen = iconOpen;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Boolean getExisted() {
		return existed;
	}

	public void setExisted(Boolean existed) {
		this.existed = existed;
	}

	public String getDescp() {
		return descp;
	}

	public void setDescp(String descp) {
		this.descp = descp;
	}

	public Integer getSn() {
		return sn;
	}

	public void setSn(Integer sn) {
		this.sn = sn;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public Boolean getNocheck() {
		return nocheck;
	}

	public void setNocheck(Boolean nocheck) {
		this.nocheck = nocheck;
	}

	public Boolean getChkDisabled() {
		return chkDisabled;
	}

	public void setChkDisabled(Boolean chkDisabled) {
		this.chkDisabled = chkDisabled;
	}

	public Boolean getDrag() {
		return drag;
	}

	public void setDrag(Boolean drag) {
		this.drag = drag;
	}

	public Boolean getDrop() {
		return drop;
	}

	public void setDrop(Boolean drop) {
		this.drop = drop;
	}

	public Boolean getClickExpand() {
		return clickExpand;
	}

	public void setClickExpand(Boolean clickExpand) {
		this.clickExpand = clickExpand;
	}

	public String getClick() {
		return click;
	}

	public void setClick(String click) {
		this.click = click;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	

//	private Map<Object, Object> infoMap = null;
	
	
}
