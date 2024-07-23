<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>月度重点工作明细</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/monthUnPlanTaskList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="excelForm" action="${ctxPath}/rdmZhgl/core/monthUnPlanTask/exportProjectPlanExcel.do"
              method="post" target="excelIFrame">
            <input type="hidden" name="pageIndex"/>
            <input type="hidden" name="pageSize"/>
            <input type="hidden" name="filter" id="filter">
        </form>
        <iframe id="excelUnIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
            <a class="mini-button" style="margin-right: 10px" plain="true" onclick="addUnPlanTask()"><spring:message code="page.monthUnPlanTaskList.name1" /></a>
            <a class="mini-button" onclick="editProjectPlan()"><spring:message code="page.monthUnPlanTaskList.name2" /></a>
            <a class="mini-button" style="margin-left: 10px" onclick="openEditWindow()"><spring:message code="page.monthUnPlanTaskList.name3" /></a>
            <a class="mini-button" style="margin-left: 10px" onclick="editProjectPlan('finish')"><spring:message code="page.monthUnPlanTaskList.name4" /></a>
            <a class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeRow()"><spring:message code="page.monthUnPlanTaskList.name5" /></a>
            <div style="display: inline-block" class="separator"></div>
            <a id="delayApply" class="mini-button " style="margin-left: 10px;" plain="true" onclick="doDelayApply()"><spring:message code="page.monthUnPlanTaskList.name6" /></a>
            <div style="display: inline-block" class="separator"></div>
            <a class="mini-button" style="margin-left: 5px" plain="true" onclick="searchList()"><spring:message code="page.monthUnPlanTaskList.name7" /></a>
            <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportBtn()"><spring:message code="page.monthUnPlanTaskList.name8" /></a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="unPlanTaskGrid" class="mini-datagrid" style="min-height: 660px;max-height: 750px" allowResize="true"
         url="${ctxPath}/rdmZhgl/core/monthUnPlanTask/plans.do" idField="id" showPager="false" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" field="mainId" name="mainId" width="30px"></div>
            <div field="rowNum" name="rowNum" align="center" headerAlign="center" width="40px"><spring:message code="page.monthUnPlanTaskList.name9" /></div>
            <div field="taskName" name="taskName" width="200px" headerAlign="center" align="center" allowSort="false">
                <spring:message code="page.monthUnPlanTaskList.name10" />
            </div>
            <div field="projectCode" name="projectCode" width="150px" headerAlign="center" align="center" allowSort="false">
                <spring:message code="page.monthUnPlanTaskList.name11" />
            </div>
            <div field="yearMonth" name="yearMonth" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthUnPlanTaskList.name12" /></div>
            <div field="taskFrom" name="taskFrom" width="150px" headerAlign="center" align="center" allowSort="false">
                <spring:message code="page.monthUnPlanTaskList.name13" />
            </div>
            <div field="startEndDate" name="startEndDate" width="200px" headerAlign="center" align="center"
                 allowSort="false"><spring:message code="page.monthUnPlanTaskList.name14" />
            </div>
            <div field="responseMan" name="responseMan" width="100px" headerAlign="center" align="center"
                 allowSort="false"><spring:message code="page.monthUnPlanTaskList.name15" />
            </div>
            <div field="deptName" name="responseMan" width="100px" headerAlign="center" align="center"
                 allowSort="false">负责部门
            </div>
            <div field="workContent" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthUnPlanTaskList.name16" /></div>
            <div field="finishFlag" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthUnPlanTaskList.name17" /></div>
            <div field="planResponseMan" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthUnPlanTaskList.name18" /></div>
            <div field="responseDeptId" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthUnPlanTaskList.name19" /></div>
            <div field="isCompanyLevel" name="isCompanyLevel" width="100px" headerAlign="center" align="center"
                 renderer="onCompany" allowSort="false"><spring:message code="page.monthUnPlanTaskList.name20" />
            </div>
            <div field="finishStatus" name="finishStatus" width="100px" headerAlign="center" align="center"
                 allowSort="false" renderer="onFinish"><spring:message code="page.monthUnPlanTaskList.name21" />
            </div>
            <div field="remark" name="remark" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthUnPlanTaskList.name22" />
            </div>
            <div field="isDelayApply" name="isDelayApply" width="150px" headerAlign="center" align="center"
                 allowSort="false" renderer="onDelayApplyRate"><spring:message code="page.monthUnPlanTaskList.name23" />
            </div>
        </div>
    </div>
</div>
<div id="yearMonthSelectedWindow" title="<spring:message code="page.monthUnPlanTaskList.name24" />" class="mini-window" style="width:500px;height:200px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="topToolBar" style="text-align:right;">
        <a class="mini-button" onclick="copyPlan()"><spring:message code="page.monthUnPlanTaskList.name25" /></a>
    </div>
    <div class="mini-fit" style="font-size: 14px">
        <form id="yearForm" method="post">
            <table cellspacing="1" cellpadding="0" class="table-detail column-six grey">
                <tr>
                    <td align="center" style="width: 10%"><spring:message code="page.monthUnPlanTaskList.name26" /><span style="color: red">(<spring:message code="page.monthUnPlanTaskList.name27" />)</span>：</td>
                    <td align="center" style="width: 20%">
                        <input id="yearSelect" class="mini-monthpicker" required allowinput="false" style="width: 150px"
                               name="yearSelect"/>
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
    var unPlanTaskGrid = mini.get("unPlanTaskGrid");
    var yesOrNo = getDics("YESORNO");
    var jhjbList = getDics("YDJH-JHJB");
    var finishStatusList = getDics("WCQK");
    var importWindow = mini.get("importWindow");
    var permission = ${permission};
    var isAdmin = ${isAdmin};
    var isLeader = ${isLeader};
    var yearMonthSelectedWindow = mini.get("yearMonthSelectedWindow");
    var yearForm = new mini.Form("#yearForm");
    if (!permission) {
    }
    unPlanTaskGrid.frozenColumns(0, 3);
    unPlanTaskGrid.on("load", function () {
        unPlanTaskGrid.mergeColumns(["mainId","rowNum","taskName","yearMonth","projectCode","taskFrom", "startEndDate",  "responseMan", "isCompanyLevel","finishStatus","remark"]);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '<span  title=' + monthUnPlanTaskList_name + ' onclick="viewForm(\'' + id + '\',\'view\')">' + monthUnPlanTaskList_name + '</span>';
        s += '<span  title=' + monthUnPlanTaskList_name1 + ' onclick="viewForm(\'' + id + '\',\'edit\')">' + monthUnPlanTaskList_name1 + '</span>';
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
<redxun:gridScript gridId="unPlanTaskGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
