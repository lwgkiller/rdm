<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>外发翻译申请列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/serviceEngineering/externalTranslationList.js?version=${static_res_version}"
            type="text/javascript"></script>

</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">外发单号: </span>
                    <input class="mini-textbox" id="applyNumber" name="applyNumber"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">发起人: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">运行状态: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:120px;" multiSelect="false"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '批准'},{'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>


                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="externalTranslationList" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         autoload="true"
         url="${ctxPath}/serviceEngineering/core/externalTranslation/applyList.do"
         idField="id"
         multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">

        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="75" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="applyNumber" width="110" headerAlign="center" align="center" allowSort="true">外发单号</div>
            <div field="transApplyId" width="110" headerAlign="center" align="center" allowSort="true">随机翻译申请单号</div>
            <div field="creatorName" width="50" headerAlign="center" align="center">发起人</div>
            <div field="taskName" headerAlign='center' align='center' width="120">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="80">当前处理人</div>
            <div field="status" width="40" headerAlign="center" align="center"
                 renderer="onStatusRenderer">当前运行状态
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd" width="65" align="center" headerAlign="center"
                 allowSort="true">提交时间
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var externalTranslationList = mini.get("externalTranslationList");

    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="clickDetail(\'' + applyId + '\',\'' + status + '\',\'' + instId + '\')">明细</span>';
        }
        if (status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="clickEdit(\'' + applyId + '\',\'' + instId + '\')">编辑</span>';
        }
        if (status == 'RUNNING' && record.myTaskId) {
            s += '<span  title="办理" onclick="clickTask(\'' + record.myTaskId + '\')">办理</span>';
        }
        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title="删除" onclick="clickRemove(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
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

</script>
<redxun:gridScript gridId="externalTranslationList" entityName="" winHeight="" winWidth="" entityTitle=""
                   baseUrl=""/>
</body>
</html>

