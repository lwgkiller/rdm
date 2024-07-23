<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">标准方向: </span><input class="mini-textbox" style="width: 150px" name="type"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">标准名称: </span><input class="mini-textbox" style="width: 150px" name="standardName"/></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addRow()">新增</a>
            <a id="delButton" class="mini-button btn-red" style="margin-left: 10px"   onclick="removeRow()">删除</a>
            <a class="mini-button" style="margin-left: 10px" plain="true" onclick="spiderData()">数据爬取</a>
            <span style="color: red">注：点击数据爬取后，5秒后点击查询，查看新数据</span>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/info/standard/listData.do" idField="id" sortField="publishDate" sortOrder="desc"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="10">序号</div>
            <div name="action" cellCls="actionIcons" width="40" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="standardCode" width="60" headerAlign="center" align="left" allowSort="true">标准编号</div>
            <div field="standardName" width="120" headerAlign="center" align="left" allowSort="true">标准名称</div>
            <div field="standardStatus" width="40" headerAlign="center" align="center" allowSort="true">标准状态</div>
            <div field="infoChildType" width="60" headerAlign="center" align="center" allowSort="true" renderer="typeRenderer">标准方向</div>
            <div field="publishDate" width="60" headerAlign="center" align="center" allowSort="true" >发布日期</div>
            <div field="async" width="40" headerAlign="center" align="center" allowSort="true" renderer="addTypeRenderer">新增方式</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false">同步日期</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var standardListGrid = mini.get("standardListGrid");
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '<span  title="详情" onclick="viewForm(\'' + id + '\',\'view\')">详情</span>';
        if(record.async=='1'){
            s += '<span  title="编辑" onclick="viewForm(\'' + id + '\',\'edit\')">编辑</span>';
        }
        return s;
    }
    //查看
    function viewForm(id,action) {
        if(action=='view'){
            var url= jsUseCtxPath +"/info/standard/detailViewPage.do?id="+id;
            window.open(url);
        }else{
            var url= jsUseCtxPath +"/info/standard/editPage.do?id="+id+"&action="+action;
            var title = "修改";
            mini.open({
                title: title,
                url: url,
                width: 1000,
                height: 800,
                showModal: true,
                allowResize: true,
                onload: function () {
                },
                ondestroy: function (action) {
                    searchFrm();
                }
            });
        }
    }
    function spiderData() {
        $.ajax({
            url:jsUseCtxPath+"/info/standard/doSpider.do?infoTypeName=标准",
            success:function (data) {
                searchFrm()
            },
            fail:function () {
                searchFrm()
            }
        });
    }

    function addRow() {
        let url = jsUseCtxPath + "/info/standard/editPage.do?action=add";
        mini.open({
            title: "新增",
            url: url,
            width: 1000,
            height: 800,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
    function typeRenderer(e) {
        var record = e.record;
        var type = "";
        if(record.async=='1'){
            type = record.standardType;
        }else{
            type = record.infoChildType;
        }
        return type;
    }
    function addTypeRenderer(e) {
        var record = e.record;
        var type = "";
        if(record.async=='1'){
            type = '自增';
        }else{
            type = '同步';
        }
        return type;
    }
    function removeRow() {
        var rows = [];
        rows = standardListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定取消选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var ids = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    ids.push(r.id);
                }
                if (ids.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/info/standard/remove.do",
                        method: 'POST',
                        data: {ids: ids.join(',')},
                        success: function (text) {
                            if (standardListGrid) {
                                standardListGrid.reload();
                            }
                        }
                    });
                }

            }
        });
    }
</script>
<redxun:gridScript gridId="standardListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
