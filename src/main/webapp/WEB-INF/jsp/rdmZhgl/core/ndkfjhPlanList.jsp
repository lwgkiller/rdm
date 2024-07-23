<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>年度开发计划</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/ndkfjhPlanList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目编号: </span>
                    <input name="planCode" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目/产品: </span>
                    <input name="productName" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">计划来源: </span>
                    <input id = "planSource"  name="planSource" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="计划来源："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=ndkfjh_source"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">年度: </span>
                    <input id="planYear" name="planYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="年度："
                           length="50"
                           only_read="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           onvaluechanged="searchFrm()"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">负责人: </span>
                    <input name="chargerManName" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">负责人部门: </span>
                    <input name="chargerDeptName" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">管控人: </span>
                    <input name="managerName" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">责任所长: </span>
                    <input name="responsorName" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <a id="yearButton" class="mini-button" plain="true"  onclick="openEditWindow()">同步预算计划</a>
            <a id="doAddApply" style="margin-left: 10px" class="mini-button" plain="true"  onclick="doAddApply()">计划新增流程</a>
            <a id="doMonthApply" style="margin-left: 10px" class="mini-button" plain="true"  onclick="doMonthApply()">月度审批流程</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_"
         sortOrder="desc"
         url="${ctxPath}/rdmZhgl/core/ndkfjh/planDetail/list.do" idField="id" showPager="true" allowCellWrap="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[15,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="30px"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
            <div name="action" cellCls="actionIcons" width="80px" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="planCode" name="planCode" width="150px" headerAlign="center" align="center" allowSort="false">编号</div>
            <div field="productName" width="150px" headerAlign="center" align="center" allowSort="false">项目/产品列表</div>
            <div field="target" name="target" width="200px" headerAlign="center" align="center" allowSort="false">年度目标</div>
            <div field="planSource" width="100px" headerAlign="center"  renderer="onPalnSorce" align="center">计划来源</div>
            <div field="sourceName" width="150px" headerAlign="center" renderer="onSourceName" align="center" allowSort="false">来源名称</div>
            <div field="startEndDate" width="200px" headerAlign="center" align="center" allowSort="false">项目开始结束时间</div>
            <div field="process" name="process" width="240px" headerAlign="center" align="center">当前进度
                <div property="columns" align="center">
                    <div field="currentStage" width="120px" headerAlign="center" align="center" allowSort="false">当前阶段</div>
                    <div field="stageFinishDate" width="160px" headerAlign="center" align="center" allowSort="false">当前阶段要求完成时间</div>
                    <div field="finishRate" width="100px" headerAlign="center" align="center" allowSort="false">完成率</div>
                    <div field="isDelay" width="100px" headerAlign="center" align="center" renderer="onDelay" allowSort="false">是否延期</div>
                    <div field="delayDays" width="100px" headerAlign="center" align="center" allowSort="false">延期天数</div>
                    <div field="remark" width="200px" headerAlign="center" align="center" allowSort="false">备注</div>
                </div>
            </div>
            <div field="chargerManName" width="100px" headerAlign="center" align="center" allowSort="false">负责人</div>
            <div field="chargerDeptName" width="100px" headerAlign="center" align="center" allowSort="false">部门</div>
            <div field="managerName" width="100px" headerAlign="center" align="center" renderer="process" allowSort="false">管控人</div>
            <div field="responsorName" width="100px" headerAlign="center" align="center" renderer="process" allowSort="false">责任所长</div>
        </div>
    </div>
</div>
<div id="yearSelectedWindow" title="选择预算年份" class="mini-window" style="width:500px;height:200px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="topToolBar" style="text-align:right;">
        <a  class="mini-button"  onclick="asyncBudgetPlan()">同步</a>
    </div>
    <div class="mini-fit" style="font-size: 14px">
        <form id="yearForm" method="post">
            <table cellspacing="1" cellpadding="0" class="table-detail column-six grey">
                <tr>
                    <td align="center" style="width: 10%">预算年份：</td>
                    <td align="center" style="width: 20%">
                        <input id="yearSelect" name="reportYear" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px"  label="年度："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                               textField="text" valueField="value" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUser.userId}";
    var listGrid = mini.get("listGrid");
    var permission = ${permission};
    var yearSelectedWindow=mini.get("yearSelectedWindow");
    var yearForm = new mini.Form("#yearForm");
    var yesOrNo = getDics("YESORNO");
    var sourceList = getDics("ndkfjh_source");
    if (!permission) {
        mini.get('yearButton').setEnabled(false);
        mini.get('doMonthApply').setEnabled(false);
    }
    listGrid.on("load", function () {
        listGrid.mergeColumns(["planCode"]);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var manager = record.manager;
        var chargerMan = record.chargerMan;
        var s = '';
        if (currentUserId != manager && !permission && currentUserId != chargerMan) {
            s += '<span  title="编辑" style="color: silver">编辑</span>';
        } else {
            s += '<span  title="编辑" onclick="editForm(\'' + id + '\',\'edit\',\'' + permission + '\')">编辑</span>';
        }
        var planSource = record.planSource;
        if(planSource=='zxxz'&&currentUserId==chargerMan){
            s += '<span  title="删除" style="color: red" onclick="doDelApply(\'' + id + '\')">删除</span>';
        }else if(planSource=='zxxz'&&permission){
            //项目计划管理员可以直接删除
            s += '<span  title="删除" style="color: red" onclick="delPlanDetail(\'' + id + '\')">删除</span>';
        }
        return s;
    }
    function delPlanDetail(mainId) {
        mini.confirm("计划管理员可以直接删除，确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath+"/rdmZhgl/core/ndkfjh/planDetail/remove.do",
                    method: 'POST',
                    data: {ids: mainId},
                    success: function (text) {
                        searchFrm();
                    }
                });
            }
        });
    }
    function doDelApply(mainId) {
        let postData = {"mainId":mainId};
        let _url = jsUseCtxPath + '/rdmZhgl/core/ndkfjh/del/isRunning.do';
        let resultData = ajaxRequest(_url,'POST',false,postData);
        if(resultData&&!resultData.success){
            mini.alert(resultData.message);
            return;
        }
        var url = jsUseCtxPath + "/bpm/core/bpmInst/NDKFJH-DEL/start.do?mainId=" + mainId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
            }
        }, 1000);
    }
    function doAddApply() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/NDKFJH-Add/start.do" ;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
            }
        }, 1000);
    }
    function doMonthApply() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/NDKFJH_MONTH/start.do" ;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
            }
        }, 1000);
    }
    function onSourceName(e) {
        var s = '';
        var record = e.record;
        var planSource = record.planSource;
        var sourceId = record.sourceId;
        if(planSource){
            if(planSource!='zxxz'&&sourceId){
                s = '<span   style="cursor: pointer;color: #0a7ac6" onclick="openSource(\'' + planSource + '\',\'' + sourceId + '\')">'+record.sourceName+'</span>';
            }
        }
        return s;
    }
    function openSource(planSource,sourceId) {
        if(planSource=='lxxm'){
            var action = "detail";
            var url = jsUseCtxPath+"/xcmgProjectManager/core/xcmgProject/edit.do?action="+action+"&projectId=" + sourceId;
            window.open(url);
        }else if(planSource=='xpsz'){
            var url = jsUseCtxPath + "/rdmZhgl/core/product/getEditPage.do?action=view&&mainId=" + sourceId;
            window.open(url);
        }else if(planSource=='tsdd'){
            var action = "detail";
            var url = jsUseCtxPath + "/Ckdd/editPage.do?action=" + action + "&ckddId=" + sourceId;
            window.open(url);
        }
    }
    function onPalnSorce(e) {
        var record = e.record;
        var planSource = record.planSource;
        var resultText = '';
        for (var i = 0; i < sourceList.length; i++) {
            if (sourceList[i].key_ == planSource) {
                resultText = sourceList[i].text;
                break
            }
        }
        return resultText;
    }
    function onDelay(e) {
        var record = e.record;
        var isDelay = record.isDelay;
        var resultText = '';
        for (var i = 0; i < yesOrNo.length; i++) {
            if (yesOrNo[i].key_ == isDelay) {
                resultText = yesOrNo[i].text;
                break
            }
        }
        return resultText;
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
