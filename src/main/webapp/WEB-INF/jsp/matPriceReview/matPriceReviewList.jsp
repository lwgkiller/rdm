<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>物料价格审批</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/matPriceReview/matPriceReviewList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-left:15px;margin-right: 15px">
                    <span class="text" style="width:auto">申请单号: </span>
                    <input class="mini-textbox" name="applyNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" name="applyUserName" style="width: 100px"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">提交状态: </span>
                    <input id="applyStatus" name="applyStatus" class="mini-combobox" style="width:150px;"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '未提交OA','value' : 'no'},{'key' : '已提交OA','value' : 'yes'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请时间 从 </span>:<input id="startTime"  name="startTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至: </span><input  id="endTime" name="endTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px;" plain="true" id="addApply"
                       onclick="editOrViewApply('','edit')">新增申请</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" id="removeApply"
                       onclick="removeApply()">删除</a>
                </li>
            </ul>

        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="applyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/matPriceReview/core/getReviewList.do?reviewCategory=${reviewCategory}"
         idField="id" multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="120" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="applyNo" sortField="applyNo" width="180" headerAlign="center" align="center" allowSort="true">申请单号</div>
            <div field="applyDeptName" sortField="applyDeptName" width="130" headerAlign="center" align="center" allowSort="true">申请人部门</div>
            <div field="applyUserName" sortField="applyUserName" headerAlign="center" align="center" allowSort="true">申请人</div>
            <div field="applyUserMobile" width="130" headerAlign="center" align="center" allowSort="false">联系电话</div>
            <div field="applierCode" width="140" headerAlign="center" align="center" allowSort="false">供应商编码</div>
            <div field="applierName" width="200" sortField="applierName" headerAlign="center" align="center" allowSort="true">供应商名称</div>
            <div field="ptProduct" headerAlign="center" align="center" allowSort="ptProduct" width="150">配套产品</div>
            <div field="CREATE_TIME_" sortField="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="150"
                 headerAlign="center" allowSort="true">申请提交时间</div>
            <div width="100" headerAlign="center" align="center" renderer="onApplyStatusRenderer" allowSort="false">提交状态</div>
        </div>
    </div>
</div>



<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyListGrid = mini.get("applyListGrid");
    var reviewCategory = "${reviewCategory}";
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var oaFormId=record.oaFormId;
        var recordId=record.id;
        if(!recordId) {
            recordId='';
        }
        var s = '<span title="查看" onclick="editOrViewApply(\'' + recordId +'\',\'view\')">查看</span>';
        if((!oaFormId && currentUserId == record.applyUserId) || currentUserNo == 'admin') {
            s+='<span title="编辑" onclick="editOrViewApply(\'' + recordId +'\',\'edit\')">编辑</span>';
            s+='<span  title="删除" onclick="removeApply('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
        }

        return s;
    }

    function onApplyStatusRenderer(e) {
        var record = e.record;
        var oaFormId=record.oaFormId;
        if(!oaFormId) {
            return '<span style="color:orange">未提交OA</span>';
        } else{
            return '<span style="color:#177BFF">已提交OA</span>';
        }

    }

</script>
<redxun:gridScript gridId="applyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
