<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>出口产品零件图册更改提醒列表</title>
    <%@include file="/commons/list.jsp" %>
    <link href="${ctxPath}/styles/formFile.css?version=${static_res_version}" rel="stylesheet" type="text/css" />
</head>
<body>

<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">变更类型：</span>
                    <input id="changeType" name="changeType" class="mini-combobox" style="width:140px;"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '需求通知单','value' : '需求通知单'},{'key' : '整机信息','value' : '整机信息'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">变更标题：</span>
                    <input class="mini-textbox" name="changeTitle"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">是否确认：</span>
                    <input id="changeConfirm" name="changeConfirm" class="mini-combobox" style="width:140px;"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '未确认','value' : '未确认'},{'key' : '已确认','value' : '已确认'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" onclick="searchFrm()" style="margin-right: 5px">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="changeNoticeListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true" showCellTip="true" idField="id"  allowHeaderWrap="true"
         multiSelect="false" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/changeNoticeListQuery.do">
        <div property="columns">
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="changeType" width="120" headerAlign="center" align="center" >变更类型</div>
            <div field="changeTitle" width="250" headerAlign="center" align="center">变更标题</div>
            <div field="changeDesc" width="670" headerAlign="center" align="left" renderer="renderMainContent">变更内容</div>
            <div field="CREATE_TIME_" width="110" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">更改创建时间</div>
            <div field="changeConfirm" width="110" headerAlign="center" align="center" renderer="changeConfirmRender">是否确认</div>
            <div field="confirmUserName" width="100" headerAlign="center" align="center" >确认人</div>
            <div field="UPDATE_TIME_" width="110" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">确认时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var changeNoticeListGrid = mini.get("changeNoticeListGrid");
    var fwgcsUser=${fwgcsUser};

    $(function () {
    });

    function renderMainContent(e) {
        var record = e.record;
        var html = "<div style='line-height: 20px;height:100px;overflow: auto' >";
        var changeDesc = record.changeDesc;
        if (!changeDesc) {
            changeDesc = "";
        }
        html += '<span style="white-space:pre-wrap" >' + changeDesc + '</span>';
        html += '</div>'
        return html;
    }

    //操作按钮
    function onActionRenderer(e) {
        var record = e.record;
        var changeConfirm = record.changeConfirm;
        if (changeConfirm == '未确认' && (currentUserNo == 'admin' || fwgcsUser)) {
            return '<span  title="更改确认" onclick="changeConfirm(\''+record.id+'\')">更改确认</span>';
        } else {
            return '<span  title="更改确认" style="color: silver">更改确认</span>';
        }
    }

    function changeConfirm(id) {
        $.ajax({
            url:jsUseCtxPath+"/serviceEngineering/core/exportPartsAtlas/changeConfirm.do?id="+id,
            method:'get',
            success:function (json) {
                changeNoticeListGrid.reload();
            }
        });
    }

    //变更确认
    function changeConfirmRender(e) {
        var record = e.record;
        var s = record.changeConfirm;
        if(s=='已确认') {
            return "<span class='blue'>"+s+"</span>";
        } else if(s=='未确认') {
            return "<span class='red'>"+s+"</span>";
        }

        return s;
    }

</script>
<redxun:gridScript gridId="changeNoticeListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>