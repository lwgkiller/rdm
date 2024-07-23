<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>零件图册下载申请列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/serviceEngineering/partsAtlasDownloadApplyList.js?version=${static_res_version}" type="text/javascript"></script>

</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请编号: </span>
                    <input class="mini-textbox" id="applyNumber" name="applyNumber"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请部门:</span>
                    <input id="creatorDeptId" name="creatorDeptId" class="mini-dep rxc" plugins="mini-dep"
                           data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                           style="width:160px;height:34px" allowinput="false" label="部门" textname="bm_name" length="500"
                           maxlength="500" minlen="0" single="true" required="false" initlogindep="false"
                           mwidth="160" wunit="px" mheight="34" hunit="px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">流程状态: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:120px;" multiSelect="false"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '批准'},{'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">是否完备: </span>
                    <input id="complete" name="complete" class="mini-combobox" style="width:120px;" multiSelect="false"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'yes','value' : '是'},{'key' : 'no','value' : '否'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机编号: </span>
                    <input class="mini-textbox" id="vinCode" name="vinCode"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="addPartsAtlasFileDownload()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="removePartsAtlasFileDownload()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="partsAtlasDownloadApplyList" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         autoload = "true"
         url="${ctxPath}/serviceEngineering/core/partsAtlasFileDownloadApply/applyList.do"
         idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="75" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="applyNumber" width="110" headerAlign="center" align="center" allowSort="false">申请编号</div>
            <div field="departName" width="70" headerAlign="center" align="center" allowSort="false">申请部门</div>
            <div field="creatorName" width="50" headerAlign="center" align="center" allowSort="false">申请人</div>
            <div field="taskName" headerAlign='center' align='center' width="120">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="80">当前处理人</div>
            <div field="status" width="40" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="complete" width="50" headerAlign="center" align="center" allowSort="true"
                 renderer="onCompleteRenderer">是否完备
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd" width="65" align="center" headerAlign="center"
                 allowSort="true">创建时间
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var partsAtlasDownloadApplyList = mini.get("partsAtlasDownloadApplyList");

    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="partsAtlasFileDownloadDetail(\'' + applyId + '\',\'' + status + '\',\'' + instId + '\')">明细</span>';
        }
        if (status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="partsAtlasFileDownloadEdit(\'' + applyId + '\',\'' + instId + '\')">编辑</span>';
        }
        if (status == 'RUNNING' && record.myTaskId) {
            s += '<span  title="办理" onclick="partsAtlasFileDownloadTask(\'' + record.myTaskId + '\')">办理</span>';
        }
        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title="删除" onclick="removePartsAtlasFileDownload(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '批准', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
    //..
    function onCompleteRenderer(e) {
        var record = e.record;
        var complete = record.complete;
        var arr = [{'key': 'yes', 'value': '是', 'css': 'green'},
            {'key': 'no', 'value': '否', 'css': 'red'}
        ];

        return $.formatItemValue(arr, complete);
    }

</script>
<redxun:gridScript gridId="partsAtlasDownloadApplyList" entityName="" winHeight="" winWidth="" entityTitle=""
                   baseUrl=""/>
</body>
</html>

