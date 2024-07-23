<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectList.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.xcmgProjectList.name"/>: </span>
                    <input class="mini-textbox" id="projectName" name="projectName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.xcmgProjectList.name1"/>: </span>
                    <input class="mini-textbox" id="number" name="number">
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message
                        code="page.xcmgProjectList.name32"/>: </span>
                    <input id="projectCategory" name="categoryId" class="mini-combobox" style="width:150px;"
                           textField="categoryName" valueField="categoryId"
                           emptyText="<spring:message code="page.xcmgProjectList.name3" />..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="<spring:message code="page.xcmgProjectList.name3" />..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message
                        code="page.xcmgProjectList.name4"/>: </span>
                    <input id="projectLevel" name="levelId" class="mini-combobox" style="width:80px;"
                           textField="levelName" valueField="levelId"
                           emptyText="<spring:message code="page.xcmgProjectList.name3" />..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="<spring:message code="page.xcmgProjectList.name3" />..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message
                        code="page.xcmgProjectList.name5"/>: </span>
                    <input id="instStatus" name="status" class="mini-combobox" style="width:120px;"
                           oncloseclick="onCloseClick" multiSelect="true" showClose="true"
                           textField="value" valueField="key"
                           emptyText="<spring:message code="page.xcmgProjectList.name3" />..." value="RUNNING"
                           required="false" allowInput="false" showNullItem="false"
                           nullItemText="<spring:message code="page.xcmgProjectList.name3" />..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                    />
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em><spring:message code="page.xcmgProjectList.name6"/></em>
						<i class="unfoldIcon"></i>
					</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message
                            code="page.xcmgProjectList.name7"/></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="myClearForm()"><spring:message code="page.xcmgProjectList.name8"/></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="openCreateInstWindow()"><spring:message code="page.xcmgProjectList.name9"/></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="removeProject()"><spring:message code="page.xcmgProjectList.name10"/></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="exportProject()"><spring:message code="page.xcmgProjectList.name11"/></a>
                    <a id="setProgressRunBtn" class="mini-button" style="margin-right: 5px;display: none" plain="true"
                       onclick="setProgressRunWindow()"><spring:message code="page.xcmgProjectList.name12"/></a>
                    <a id="unSetProgressRunBtn" class="mini-button" style="margin-right: 5px;display: none" plain="true"
                       onclick="undoSetProgressRun()"><spring:message code="page.xcmgProjectList.name13"/></a>
                    <a id="splitBtn" class="mini-button" style="margin-right: 1px;;display: none" plain="true"
                       onclick="splitUserDelivery()"><spring:message code="page.xcmgProjectList.name14"/></a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message
                                code="page.xcmgProjectList.name15"/>: </span>
                        <input id="projectSource" name="sourceId" class="mini-combobox" style="width:150px;"
                               textField="sourceName" valueField="sourceId"
                               emptyText="<spring:message code="page.xcmgProjectList.name3" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.xcmgProjectList.name3" />..."/>
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message
                            code="page.xcmgProjectList.name16"/>:</span>
                        <input id="mainDepId" name="mainDepId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:160px;height:34px"
                               allowinput="false" label="<spring:message code="page.xcmgProjectList.name26" />"
                               textname="bm_name" length="500" maxlength="500" minlen="0" single="false"
                               required="false" initlogindep="false"
                               level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34"
                               hunit="px"/>
                    </li>
                    <li id="progressRunStatusFilter" style="margin-right: 15px;display: none"><span class="text"
                                                                                                    style="width:auto"><spring:message
                            code="page.xcmgProjectList.name17"/>: </span>
                        <input id="progressRunStatus" name="progressRunStatus" class="mini-combobox"
                               style="width:150px;"
                               textField="value" valueField="key"
                               emptyText="<spring:message code="page.xcmgProjectList.name3" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.xcmgProjectList.name3" />..."
                               data="[ {'key' : '1','value' : '是'},{'key' : '0','value' : '否'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message
                                code="page.xcmgProjectList.name18"/> </span>:<input id="projectStartTime"
                                                                                    name="projectStartTime"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto"><spring:message
                                code="page.xcmgProjectList.name19"/>: </span><input id="projectEndTime"
                                                                                    name="projectEndTime"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message
                                code="page.xcmgProjectList.name20"/> </span>:<input id="knotTimeStart"
                                                                                    name="knotTimeStart"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto"><spring:message
                                code="page.xcmgProjectList.name19"/>: </span><input id="knotTimeEnd" name="knotTimeEnd"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message
                                code="page.xcmgProjectList.name21"/> </span>:<input id="planEndTimeStart"
                                                                                    name="planEndTimeStart"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto"><spring:message
                                code="page.xcmgProjectList.name19"/>: </span><input id="planEndTimeEnd"
                                                                                    name="planEndTimeEnd"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message
                                code="page.xcmgProjectList.name22"/>: </span>
                        <input class="mini-textbox" id="respMan" name="respMan">
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message
                                code="page.xcmgProjectList.name23"/>: </span>
                        <input id="hasRisk" name="hasRisk" class="mini-combobox" style="width:150px;"
                               textField="key" valueField="value"
                               emptyText="<spring:message code="page.xcmgProjectList.name3" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.xcmgProjectList.name3" />..."
                               data="[ {'key' : '延误超5天','value' : 2},{'key' : '延误未超5天','value' : 1},{'key' : '正常','value' : 0},{'key' : '项目未启动或已废止','value' : 3},{'key' : '超30天未填项目计划','value':4}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message
                                code="page.xcmgProjectList.name24"/>: </span>
                        <input class="mini-textbox" id="taskName" name="taskName"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message
                                code="page.xcmgProjectList.name25"/>: </span>
                        <input class="mini-textbox" name="productName"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message
                                code="page.xcmgProjectEdit.name18"/>: </span>
                        <input class="mini-textbox" name="cwddh"/>
                    </li>
                </ul>
            </div>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="projectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/xcmgProjectManager/core/xcmgProject/getProjectList.do" idField="projectId" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="left"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message
                    code="page.xcmgProjectList.name27"/></div>
            <div field="projectName" sortField="projectName" width="215" headerAlign="center" align="left"
                 allowSort="true"><spring:message code="page.xcmgProjectList.name28"/></div>
            <div field="number" sortField="number" width="125" headerAlign="center" align="center" allowSort="true">
                <spring:message code="page.xcmgProjectList.name29"/></div>
            <div field="firstPlanStartTime" dateFormat="yyyy-MM-dd" align="center" width="90" headerAlign="center"
                 allowSort="false"><spring:message code="page.xcmgProjectList.name30"/></div>
            <div field="lastPlanEndTime" dateFormat="yyyy-MM-dd" align="center" width="90" headerAlign="center"
                 allowSort="false"><spring:message code="page.xcmgProjectList.name43"/></div>
            <div field="cwddh" sortField="projectName" width="110" headerAlign="center" align="center" allowSort="true">
                <spring:message code="page.xcmgProjectList.name44"/></div>
            <div field="mainDepName" sortField="mainDepName" width="110" headerAlign="center" align="center"
                 allowSort="false"><spring:message code="page.xcmgProjectList.name16"/></div>
            <div field="sourceName" sortField="sourceName" width="80" headerAlign="center" align="center"
                 allowSort="true"><spring:message code="page.xcmgProjectList.name31"/></div>
            <div field="categoryName" sortField="categoryName" width="120" headerAlign="center" align="center"
                 allowSort="true"><spring:message code="page.xcmgProjectList.name32"/></div>
            <%-- 立项级别--%>
            <div field="initLevelName" sortField="initLevelName" width="60" headerAlign="center" align="center"
                 allowSort="true"><spring:message code="page.xcmgProjectList.name46"/></div>
            <%-- 结项级别--%>
            <div field="levelName" sortField="levelName" width="60" headerAlign="center" align="center"
                 allowSort="true"><spring:message code="page.xcmgProjectList.name2"/></div>
            <div field="respMan" sortField="respMan" width="80" headerAlign="center" align="center" allowSort="false">
                <spring:message code="page.xcmgProjectList.name22"/></div>
            <div field="status" sortField="status" width="60" allowSort="true" headerAlign="center" align="center"
                 renderer="onStatusRenderer"><spring:message code="page.xcmgProjectList.name5"/></div>
            <div field="currentStageName" sortField="currentStageName" width="100" headerAlign="center" align="center"
                 allowSort="false"><spring:message code="page.xcmgProjectList.name33"/></div>
            <div field="allTaskUserNames" width="80" align="center" headerAlign="center" allowSort="false">
                <spring:message code="page.xcmgProjectList.name34"/></div>
            <div field="taskName" width="140" align="center" headerAlign="center" allowSort="false"><spring:message
                    code="page.xcmgProjectList.name24"/></div>
            <div field="changeNumber" width="65" align="center" headerAlign="center" allowSort="false"><spring:message
                    code="page.xcmgProjectList.name45"/></div>
            <div width="60" align="center" headerAlign="center" renderer="onRiskRenderer"><spring:message
                    code="page.xcmgProjectList.name35"/></div>
            <div field="INST_ID_" visible="false"></div>
            <div field="myTaskId" visible="false"></div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/xcmgProjectManager/core/xcmgProject/exportProjectExcel.do" method="post"
      target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<%--进度追赶设置窗口--%>
<div id="progressRunWindow" title="<spring:message code="page.xcmgProjectList.name36" />" class="mini-window"
     style="width:650px;height:280px;"
     showModal="true" showFooter="false" allowResize="false">
    <div class="mini-fit" style="font-size: 14px">
        <span style="font-weight: bold;font-size: 15px"><spring:message code="page.xcmgProjectList.name37"/></span>
        <br/><span style="font-size: 15px"><spring:message code="page.xcmgProjectList.name38"/></span><br/>
        <div style="margin-top: 60px">
            积分发放时间：<input id="scoreGetTime" class="mini-combobox" style="width:150px;"
                                textField="value" valueField="key" onvaluechanged="scoreGetTimeChange()"
                                required="false" allowInput="false" showNullItem="false"
                                data="[ {'key' : 'actualEndTime','value' : '阶段实际结束时间'},{'key' : 'planEndTime','value' : '阶段计划结束时间'},{'key' : 'defineTime','value' : '自定义时间'}]"
        />
            <input id="defineTimeId" style="display: none;margin-left: 20px" class="mini-datepicker" format="yyyy-MM-dd"
                   style="width:100px"/>
        </div>
    </div>
    <div class="topToolBar" style="text-align:center;">
        <a id="setOldBtn" class="mini-button" onclick="setProgressRun()"><spring:message
                code="page.xcmgProjectList.name39"/></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeSetProgressRunWindow()"><spring:message
                code="page.xcmgProjectList.name40"/></a>
    </div>
</div>

<div id="createInstWindow" title="<spring:message code="page.xcmgProjectList.name41" />" class="mini-window"
     style="width:650px;height:200px;"
     showModal="true" showFooter="false" allowResize="false">
    <div class="mini-fit" style="font-size: 14px">
        <span style="font-weight: bold;font-size: 15px;color: red"><spring:message
                code="page.xcmgProjectList.name42"/></span>
        <br/>
        <div style="margin-top: 10px">
            <spring:message code="page.xcmgProjectList.name32"/>：<input id="selectProjectCategory" class="mini-combobox"
                                                                        style="width:98%;"
                                                                        textField="categoryName" valueField="categoryId"
                                                                        emptyText="<spring:message code="page.xcmgProjectList.name3" />..."
                                                                        allowInput="false" showNullItem="false"/>
        </div>
    </div>
    <div class="topToolBar" style="text-align:center;">
        <a id="createInstOk" class="mini-button" onclick="createInstOkWindow()"><spring:message
                code="page.xcmgProjectList.name39"/></a>
        <a id="createInstClose" class="mini-button btn-red" onclick="createInstCloseWindow()"><spring:message
                code="page.xcmgProjectList.name40"/></a>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var progressRunWindow = mini.get("progressRunWindow");
    var currentUserRoles =${currentUserRoles};
    var currentUserNo = "${currentUserNo}";
    var isProjectManager = whetherIsProjectManagerAll(currentUserRoles);
    var projectListGrid = mini.get("projectListGrid");
    var pointCategoryName = "${pointCategoryName}";
    var projectStartTime = "${projectStartTime}";
    var projectEndTime = "${projectEndTime}";
    var createInstWindow = mini.get("createInstWindow");


    projectListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var projectId = record.projectId;
        var instId = record.INST_ID_;

        //明细场景下是否隐藏成员系数
        var detailHideRoleRatio = false;
        if (record.processTask && record.taskName && record.taskName.indexOf("所长指定成员") != -1) {
            detailHideRoleRatio = true;
        }
        var s = '<span  title=' + xcmgProjectList_name + ' onclick="detailProjectRow(\'' + projectId + '\',\'' + record.status + '\',\'' + detailHideRoleRatio + '\',\'' + record.categoryId + '\')">' + xcmgProjectList_name + '</span>';
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + xcmgProjectList_name1 + ' onclick="editProjectRow(\'' + projectId + '\',\'' + instId + '\')">' + xcmgProjectList_name1 + '</span>';
        } else {
            if (record.processTask) {
                s += '<span  title=' + xcmgProjectList_name2 + ' onclick="doProjectTask(\'' + record.myTaskId + '\')">' + xcmgProjectList_name2 + '</span>';
            }
            if (currentUserNo == 'admin' || isProjectManager) {
                s += '<span  title=' + xcmgProjectList_name3 + ' onclick="managerChange(\'' + projectId + '\')">' + xcmgProjectList_name3 + '</span>';
            }
        }

        //无删除权限的按钮变灰色
        var status = record.status;
        if (status == 'DRAFTED' || status == 'DISCARD_END') {
            s += '<span  title=' + xcmgProjectList_name4 + ' onclick="removeProject(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + xcmgProjectList_name4 + '</span>';
        } else {
            s += '<span  title=' + xcmgProjectList_name4 + ' style="color: silver">' + xcmgProjectList_name4 + '</span>';
        }
        s += '<span  title=' + xcmgProjectList_name5 + ' onclick="editProjectDelivery(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + xcmgProjectList_name5 + '</span>';
        return s;
    }


    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, status);
    }

    projectListGrid.on("rowdblclick", function (e) {
        var record = e.record;
        var projectId = record.projectId;
        var instId = record.INST_ID_;
        if (record.status == 'DRAFTED') {
            editProjectRow(projectId, instId);
        } else if (record.processTask) {
            doProjectTask(record.myTaskId);
        } else {
            detailProjectRow(projectId, record.status);
        }
    });

    function onRiskRenderer(e) {
        var record = e.record;
        var hasRisk = record.hasRisk;
        var color = '#32CD32';
        var title = '项目未延误';
        if (hasRisk == 1) {
            color = '#EEEE00';
            title = '项目延误时间未超过5天';
        } else if (hasRisk == 2) {
            color = '#fb0808';
            title = '项目延误时间超过5天';
        } else if (hasRisk == 3) {
            color = '#cccccc';
            title = '项目未启动或已停止';
        } else if (hasRisk == 4) {
            color = '#9B00FCFF';
            title = '项目没有计划时间且延误超过30天';
        }
        var s = '<span title= "' + title + '" style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + color + '"></span>';
        return s;
    }

    function managerChange(projectId) {
        var url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/edit.do?action=change&projectId=" + projectId
        var winObj = window.open(jsUseCtxPath + "/rdm/core/noFlowFormIframe?url=" + encodeURIComponent(url));
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (projectListGrid) {
                    projectListGrid.reload()
                }
                ;
            }
        }, 1000);
    }

    function onCloseClick(e) {
        var obj = e.sender;
        obj.setText("");
        obj.setValue("");
    }

    function myClearForm() {
        mini.get("instStatus").setValue("");
        clearForm();
    }


    function testScore() {
        var data = {};
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/testScore.do',
            type: 'post',
            data: mini.encode(data),
            contentType: 'application/json',
            success: function (data) {

            }
        });
    }

</script>
<redxun:gridScript gridId="projectListGrid" entityName="" winHeight=""
                   winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
