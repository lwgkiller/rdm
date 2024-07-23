<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>月度重点工作明细</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/monthUnWorkList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="excelUnForm" action="${ctxPath}/rdmZhgl/core/monthUnProjectPlan/exportProjectPlanExcel.do"
              method="post" target="excelIFrame">
            <input type="hidden" name="pageIndex"/>
            <input type="hidden" name="pageSize"/>
            <input type="hidden" name="filter" id="filter">
        </form>
        <iframe id="excelUnIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
<%--            <a class="mini-button" style="margin-left: 10px" plain="true" onclick="addUnProjectPlan()">新增非项目计划</a>--%>
            <a class="mini-button" onclick="editProjectPlan()"><spring:message code="page.monthUnWorkList.name" /></a>
            <a class="mini-button" style="margin-left: 10px" onclick="openEditWindow()"><spring:message code="page.monthUnWorkList.name1" /></a>
            <a class="mini-button" style="margin-left: 10px" onclick="editProjectPlan('finish')"><spring:message code="page.monthUnWorkList.name2" /></a>
            <a class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeRow()"><spring:message code="page.monthUnWorkList.name3" /></a>
            <div style="display: inline-block" class="separator"></div>
            <a id="delayApply" class="mini-button " style="margin-left: 10px;" plain="true" onclick="doDelayApply()"><spring:message code="page.monthUnWorkList.name4" /></a>
            <a id="openBudget" class="mini-button " style="margin-left: 10px;" plain="true" onclick="openBudget()"><spring:message code="page.monthUnWorkList.name5" /></a>
            <a id="openStrategy" class="mini-button " style="margin-left: 10px;" plain="true" onclick="openStrategy()"><spring:message code="page.monthUnWorkList.name6" /></a>
            <div style="display: inline-block" class="separator"></div>
            <a class="mini-button" style="margin-left: 5px" plain="true" onclick="searchList()"><spring:message code="page.monthUnWorkList.name7" /></a>
            <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportBtn()"><spring:message code="page.monthUnWorkList.name8" /></a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="unPlanListGrid" class="mini-datagrid" style="min-height: 660px;max-height: 750px" allowResize="true"
         url="${ctxPath}/rdmZhgl/core/monthUnProjectPlan/plans.do" idField="id" showPager="false" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" field="mainId" name="mainId" width="30px"></div>
            <div field="rowNum" name="rowNum" align="center" headerAlign="center" width="40px"><spring:message code="page.monthUnWorkList.name9" /></div>
            <div field="taskName" name="taskName" width="200px" headerAlign="center" align="center" allowSort="false">
                <spring:message code="page.monthUnWorkList.name10" />
            </div>
            <div field="projectCode" name="projectCode" width="150px" headerAlign="center" align="center" allowSort="false">
                <spring:message code="page.monthUnWorkList.name11" />
            </div>
            <div field="yearMonth" name="yearMonth" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthUnWorkList.name12" /></div>
            <div field="processRate" name="processRate" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthUnWorkList.name13" /></div>
            <div field="processStatusText" name="processStatusText" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthUnWorkList.name14" /></div>
            <div field="taskFrom" name="taskFrom" width="150px" headerAlign="center" align="center" allowSort="false">
                <spring:message code="page.monthUnWorkList.name15" />
            </div>
            <div field="startEndDate" name="startEndDate" width="200px" headerAlign="center" align="center"
                 allowSort="false"><spring:message code="page.monthUnWorkList.name16" />
            </div>
            <div field="stageId" name="stageId" width="100px" headerAlign="center" align="center" allowSort="false">
                <spring:message code="page.monthUnWorkList.name17" />
            </div>
            <div field="responseMan" name="responseMan" width="100px" headerAlign="center" align="center"
                 allowSort="false"><spring:message code="page.monthUnWorkList.name18" />
            </div>
            <div field="deptName" name="responseMan" width="100px" headerAlign="center" align="center"
                 allowSort="false">负责部门
            </div>
            <div field="workContent" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthUnWorkList.name19" /></div>
            <div field="finishFlag" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthUnWorkList.name20" /></div>
            <div field="planResponseMan" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthUnWorkList.name21" /></div>
            <div field="responseDeptId" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthUnWorkList.name22" /></div>
            <div field="isCompanyLevel" name="isCompanyLevel" width="100px" headerAlign="center" align="center"
                 renderer="onCompany" allowSort="false"><spring:message code="page.monthUnWorkList.name23" />
            </div>
            <div field="finishStatus" name="finishStatus" width="100px" headerAlign="center" align="center"
                 allowSort="false" renderer="onFinish"><spring:message code="page.monthUnWorkList.name24" />
            </div>
            <div field="remark" name="remark" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthUnWorkList.name25" />
            </div>
            <div field="isDelayApply" name="isDelayApply" width="150px" headerAlign="center" align="center"
                 allowSort="false" renderer="onDelayApplyRate"><spring:message code="page.monthUnWorkList.name26" />
            </div>
        </div>
    </div>
</div>
<div id="yearMonthSelectedWindow" title="<spring:message code="page.monthUnWorkList.name27" />" class="mini-window" style="width:500px;height:200px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="topToolBar" style="text-align:right;">
        <a class="mini-button" onclick="copyPlan()"><spring:message code="page.monthUnWorkList.name28" /></a>
    </div>
    <div class="mini-fit" style="font-size: 14px">
        <form id="yearForm" method="post">
            <table cellspacing="1" cellpadding="0" class="table-detail column-six grey">
                <tr>
                    <span style="color: red"><spring:message code="page.monthUnWorkList.name29" /></span>
                    <td align="center" style="width: 10%"><spring:message code="page.monthUnWorkList.name30" /><span style="color: red">(<spring:message code="page.monthUnWorkList.name31" />)</span>：</td>
                    <td align="center" style="width: 20%">
                        <input id="yearSelect" class="mini-monthpicker" required allowinput="false" style="width: 150px"
                               name="yearSelect"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<div id="importBudgetWindow" title="<spring:message code="page.monthUnWorkList.name32" />" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBudget" class="mini-button" onclick="importBudget()"><spring:message code="page.monthUnWorkList.name33" /></a>
        <a id="closeBudget" class="mini-button btn-red" onclick="closeBudgetWindow()"><spring:message code="page.monthUnWorkList.name34" /></a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td align="center" style="width: 10%"><spring:message code="page.monthUnWorkList.name35" /><span style="color: red">(<spring:message code="page.monthUnWorkList.name31" />)</span>：</td>
                    <td align="center" style="width: 20%">
                        <input id="yearMonthSelect" class="mini-monthpicker" required allowinput="false" style="width: 150px"
                               name="yearMonthSelect"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%" align="center"><spring:message code="page.monthUnWorkList.name36" />：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downBudgetTemplate()"><spring:message code="page.monthUnWorkList.name37" />.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%" align="center"><spring:message code="page.monthUnWorkList.name38" />：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="budgetFileName" name="budgetFileName"
                               readonly/>
                        <input id="inputBudgetFile" style="display:none;" type="file" onchange="getBudgetFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadBudgetFile"><spring:message code="page.monthUnWorkList.name39" /></a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearBudgetFile"><spring:message code="page.monthUnWorkList.name40" /></a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="importStrategyWindow" title="<spring:message code="page.monthUnWorkList.name41" />" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importStrategy" class="mini-button" onclick="importStrategy()"><spring:message code="page.monthUnWorkList.name33" /></a>
        <a id="closeStrategy" class="mini-button btn-red" onclick="closeStrategyWindow()"><spring:message code="page.monthUnWorkList.name34" /></a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImportStrategy" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td align="center" style="width: 10%"><spring:message code="page.monthUnWorkList.name35" /><span style="color: red">(<spring:message code="page.monthUnWorkList.name31" />)</span>：</td>
                    <td align="center" style="width: 20%">
                        <input id="yearMonthSelectStrategy" class="mini-monthpicker" required allowinput="false" style="width: 150px"
                               name="yearMonthSelectStrategy"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%" align="center"><spring:message code="page.monthUnWorkList.name36" />：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downStrategyTemplate()"><spring:message code="page.monthUnWorkList.name42" />.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%" align="center"><spring:message code="page.monthUnWorkList.name38" />：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="strategyFileName" name="strategyFileName"
                               readonly/>
                        <input id="inputStrategyFile" style="display:none;" type="file" onchange="getStrategyFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtnStrategy" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadStrategyFile"><spring:message code="page.monthUnWorkList.name39" /></a>
                        <a id="clearFileBtnStrategy" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearStrategyFile"><spring:message code="page.monthUnWorkList.name40" /></a>
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
    var currentUserName = "${currentUser.fullname}";
    var unPlanListGrid = mini.get("unPlanListGrid");
    var yesOrNo = getDics("YESORNO");
    var jhjbList = getDics("YDJH-JHJB");
    var finishStatusList = getDics("WCQK");
    var importWindow = mini.get("importWindow");
    var permission = ${permission};
    var isAdmin = ${isAdmin};
    var yearMonthSelectedWindow = mini.get("yearMonthSelectedWindow");
    var yearForm = new mini.Form("#yearForm");
    var importBudgetWindow = mini.get("importBudgetWindow");
    var importStrategyWindow = mini.get("importStrategyWindow");
    if (!permission) {
        // mini.get("delayApply").setEnabled(false);
    }
    if (!isAdmin) {
        mini.get("openBudget").setEnabled(false);
        mini.get("openStrategy").setEnabled(false);
    }
    unPlanListGrid.frozenColumns(0, 3);
    unPlanListGrid.on("load", function () {
        unPlanListGrid.mergeColumns(["mainId","rowNum","taskName","yearMonth","projectCode","processRate","processStatusText", "taskFrom", "startEndDate", "stageId", "responseMan", "isCompanyLevel","finishStatus","remark"]);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '<span  title=' + monthUnWorkList_name + ' onclick="viewForm(\'' + id + '\',\'view\')">' + monthUnWorkList_name + '</span>';
        s += '<span  title=' + monthUnWorkList_name1 + ' onclick="viewForm(\'' + id + '\',\'edit\')">' + monthUnWorkList_name1 + '</span>';
        return s;
    }

    function onCompany(e) {
        var record = e.record;
        var resultValue = record.isCompanyLevel;
        var resultText = '';
        for (var i = 0; i < jhjbList.length; i++) {
            if (jhjbList[i].key_ == resultValue) {
                resultText = jhjbList[i].text;
                break
            }
        }
        return resultText;
    }

    function onFinish(e) {
        var record = e.record;
        var resultValue = record.finishStatus;
        var resultText = '';
        for (var i = 0; i < finishStatusList.length; i++) {
            if (finishStatusList[i].key_ == resultValue) {
                resultText = finishStatusList[i].text;
                break
            }
        }
        var _html = '';
        if (resultValue == '1' && record.isDelayApply != '1') {
            _html = '<span style="color: red">' + resultText + '</span>'
        } else if (resultValue == '0') {
            _html = '<span style="color: green">' + resultText + '</span>'
        } else if (resultValue == '1' && record.isDelayApply == '1') {
            _html = '<span style="color: green">' + resultText + '</span>'
        }
        return _html;
    }

    function onDelayApplyRate(e) {
        var record = e.record;
        var delayApply = record.isDelayApply;
        var resultText = '';
        for (var i = 0; i < yesOrNo.length; i++) {
            if (yesOrNo[i].key_ == delayApply) {
                resultText = yesOrNo[i].text;
                break
            }
        }
        return resultText;
    }

    function getDics(dicKey) {
        let resultDate = [];
        $.ajax({
            async: false,
            url: __rootPath + '/sys/core/commonInfo/getDicItem.do?dicType=' + dicKey,
            type: 'GET',
            contentType: 'application/json',
            success: function (data) {
                if (data.code == 200) {
                    resultDate = data.data;
                }
            }
        });
        return resultDate;
    }
</script>
<redxun:gridScript gridId="unPlanListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
