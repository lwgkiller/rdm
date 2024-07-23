package com.redxun.saweb.menu;

import java.util.List;

import com.redxun.core.util.Tree;
import com.redxun.sys.core.entity.Subsystem;

public class UIMenu implements Tree {
    // 菜单ID
    private String menuId;
    // Key
    private String key;
    // 名称
    private String name;
    // URL
    private String url;

    private String img;
    // 序号
    private Integer sn;
    // 是否为按钮菜单
    private String isBtnMenu;
    // 访问方式
    private String showType;
    // 打开方式
    private String windowOpen;

    private String iconCls = "";

    private String parentId = "0";

    // 类型：外部子系统/内部子系统
    private String type = Subsystem.SYS_INNER;

    private List<Tree> childrens;

    private int childs;

    private int msgNum;

    private String sysId;

    private String sysName;

    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsBtnMenu() {
        return isBtnMenu;
    }

    public void setIsBtnMenu(String isBtnMenu) {
        this.isBtnMenu = isBtnMenu;
    }

    public UIMenu() {

    }

    public UIMenu(String menuId) {
        this.menuId = menuId;
    }

    public UIMenu(String menuId, String key, String name, String url, String img, Integer sn) {
        this.menuId = menuId;
        this.key = key;
        this.name = name;
        this.url = url;
        this.img = img;
        this.sn = sn;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public String getWindowOpen() {
        return windowOpen;
    }

    public void setWindowOpen(String windowOpen) {
        this.windowOpen = windowOpen;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getSn() {
        if (sn == null) {
            return 0;
        }
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    @Override
    public String getId() {
        return this.menuId;
    }

    @Override
    public String getParentId() {

        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String getText() {

        return this.name;
    }

    @Override
    public List<Tree> getChildren() {

        return this.childrens;
    }

    @Override
    public void setChildren(List<Tree> list) {
        this.childrens = list;

    }

    public int getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(int msgNum) {
        this.msgNum = msgNum;
    }

    public int getChilds() {
        return childs;
    }

    public void setChilds(int childs) {
        this.childs = childs;
    }
}
