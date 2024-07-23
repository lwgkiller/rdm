<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>数据列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">业务方向: </span>
                    <input  name="busTypeId" class="mini-combobox rxc" plugins="mini-combobox"
                            style="width:100%;height:34px"  label="业务方向："
                            length="50"  onvaluechanged="searchFrm()"
                            only_read="false" required="false" allowinput="false" mwidth="100"
                            wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                            textField="text" valueField="value" emptyText="请选择..."
                            url="${ctxPath}/info/busTypeConfig/getDicInfoType.do"
                            nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">情报分类: </span>
                    <input id="infoTypeId" name="infoTypeId" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px"  label="情报分类："
                           length="50"
                           only_read="false" required="false" allowinput="false" mwidth="100" onvaluechanged="searchFrm()"
                           wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/info/type/getDicInfoType.do"
                           nullitemtext="请选择..." emptytext="请选择..."/></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <div style="display: inline-block" class="separator"></div>
            <a  id="addButton" class="mini-button" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="addRow()">新增</a>
            <a  id="delButton" class="mini-button" style="margin-left: 10px"  img="${ctxPath}/scripts/mini/miniui/res/images/cancel.png" onclick="removeRow()">删除</a>
            <div style="display: inline-block" class="separator"></div>

        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="infoUrlConfigGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/info/url/listData.do" idField="id" sortField="UPDATE_TIME_" sortOrder="desc"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div name="action" cellCls="actionIcons" width="40" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="busTypeName" width="60" headerAlign="center" align="center" allowSort="true">业务方向</div>
            <div field="infoTypeName" width="60" headerAlign="center" align="center" allowSort="true">情报分类</div>
            <div field="infoChildType" width="70" headerAlign="center" align="center" allowSort="true" >情报方向</div>
            <div field="url" width="140" headerAlign="center" align="left" allowSort="true" >URL</div>
            <div field="processClass" width="140" headerAlign="center" align="left" allowSort="false">方法路径</div>
            <div field="asyncDate" width="80" headerAlign="center" align="center" allowSort="false">上次同步日期</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var infoUrlConfigGrid = mini.get("infoUrlConfigGrid");
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '<span  title="查看" onclick="viewForm(\'' + id + '\',\'view\')">查看</span>';
        s += '<span  title="编辑" onclick="viewForm(\'' + id + '\',\'edit\')">编辑</span>';
        return s;
    }
    function addRow() {
        let url = jsUseCtxPath + "/info/url/editPage.do?action=add";
        mini.open({
            title: "新增",
            url: url,
            width: 800,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
    //查看
    function viewForm(id,action) {
        var url= jsUseCtxPath +"/info/url/editPage.do?action="+action+"&id="+id;
        var title = "修改";
        if(action=='view'){
            title = "查看";
        }
        mini.open({
            title: title,
            url: url,
            width: 800,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }
    //删除记录
    function removeRow() {
        var rows = [];
        rows = infoUrlConfigGrid.getSelecteds();
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
                        url: jsUseCtxPath + "/info/url/remove.do",
                        method: 'POST',
                        data: {ids: ids.join(',')},
                        success: function (text) {
                            if (infoUrlConfigGrid) {
                                infoUrlConfigGrid.reload();
                            }
                        }
                    });
                }

            }
        });
    }
</script>
<redxun:gridScript gridId="infoUrlConfigGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
