<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>项目变更申请列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectChangeApplyList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.projectChangeApplyList.name" />: </span>
                    <input class="mini-textbox" name="applyUserName"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.projectChangeApplyList.name1" />: </span>
                    <input class="mini-textbox" name="projectName"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.projectChangeApplyList.name2" />: </span>
                    <input id="projectCategory" name="categoryId" class="mini-combobox" style="width:300px;"
                           onvaluechanged="searchFrm()"
                           textField="categoryName" valueField="categoryId" emptyText="<spring:message code="page.projectChangeApplyList.name3" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.projectChangeApplyList.name3" />..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.projectChangeApplyList.name4" />: </span>
                    <input id="projectLevel" name="levelId" class="mini-combobox" style="width:150px;"
                           onvaluechanged="searchFrm()"
                           textField="levelName" valueField="levelId" emptyText="<spring:message code="page.projectChangeApplyList.name3" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.projectChangeApplyList.name3" />..."/>
                </li>
                <li style="margin-left:15px;margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.projectChangeApplyList.name5" />: </span>
                    <input id="instStatus" name="instStatus" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.projectChangeApplyList.name3" />..." value="RUNNING"
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.projectChangeApplyList.name3" />..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                    />
                </li>
                <li style="margin-left:15px;margin-right: 15px"><span class="text" style="width:auto">是否重大变更: </span>
                    <input id="isBigChange" name="isBigChange" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="请选择"
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择"
                           data="[ {'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                    />
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em><spring:message code="page.projectChangeApplyList.name6" /></em>
						<i class="unfoldIcon"></i>
					</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.projectChangeApplyList.name7" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.projectChangeApplyList.name8" /></a>
                    <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportBtn()">导出</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeApply()"><spring:message code="page.projectChangeApplyList.name9" /></a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.projectChangeApplyList.name10" />: </span>
                        <input class="mini-textbox" name="applyId"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.projectChangeApplyList.name11" /> </span>:<input name="apply_startTime"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:100px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto"><spring:message code="page.projectChangeApplyList.name12" />: </span><input name="apply_endTime"
                                                                                  class="mini-datepicker"
                                                                                  format="yyyy-MM-dd"
                                                                                  style="width:100px"/>
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="applyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/xcmgProjectManager/core/xcmgProjectChange/queryList.do"
         idField="applyId"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="90" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.projectChangeApplyList.name13" />
            </div>
            <div field="id" sortField="id" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.projectChangeApplyList.name10" /></div>
            <div field="applyUserName" sortField="applyUserName" width="40" headerAlign="center" align="center"
                 allowSort="true"><spring:message code="page.projectChangeApplyList.name" />
            </div>
            <div field="projectName" width="150" headerAlign="center" align="center" allowSort="false" renderer="jumpToDetail"><spring:message code="page.projectChangeApplyList.name1" /></div>
            <div field="reason" width="150" headerAlign="center" align="center" allowSort="false"><spring:message code="page.projectChangeApplyList.name14" /></div>
            <div field="instStatus" width="50" headerAlign="center" align="center" renderer="onStatusRenderer"
                 allowSort="false"><spring:message code="page.projectChangeApplyList.name15" />
            </div>
            <div field="currentProcessTask" sortField="currentProcessTask" width="50" align="center"
                 headerAlign="center" allowSort="false"><spring:message code="page.projectChangeApplyList.name16" />
            </div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="50" align="center"
                       headerAlign="center" allowSort="false"><spring:message code="page.projectChangeApplyList.name17" />
            </div>
            <div field="currentStage" width="70" headerAlign="center" align="center" allowSort="false"><spring:message code="page.projectChangeApplyList.name18" /></div>
            <div field="projectType" width="40" headerAlign="center" align="center" allowSort="false"><spring:message code="page.projectChangeApplyList.name19" /></div>
            <div field="projectLevel" width="40" headerAlign="center" align="center" allowSort="false"><spring:message code="page.projectChangeApplyList.name4" /></div>
            <div field="mainDeptName" width="70" headerAlign="center" align="center" allowSort="false"><spring:message code="page.projectChangeApplyList.name20" /></div>
            <div field="applyTime" sortField="applyTime" dateFormat="yyyy-MM-dd" align="center" width="70"
                 headerAlign="center" allowSort="true"><spring:message code="page.projectChangeApplyList.name21" />
            </div>
            <div field="INST_ID_" visible="false"></div>
            <div field="taskId" visible="false"></div>
            <!--判断当前登录人是否是流程处理人!-->
            <div field="processTask" visible="false"></div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" method="post" target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyListGrid = mini.get("applyListGrid");
    var currentUserId = "${currentUser.userId}";
    var applyCategoryId = "${applyCategoryId}";

    function exportBtn(){
        var params = [];
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        var url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProjectChange/exportProjectChange.do?";
        excelForm.attr("action", url);
        excelForm.submit();
    }

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.INST_ID_;
        var s = '<span  title=' + projectChangeApplyList_name + ' onclick="detailChangeApply(\'' + applyId + '\',\'' + record.instStatus + '\')">' + projectChangeApplyList_name + '</span>';
        if (record.instStatus == 'DRAFTED') {
            if (record.CREATE_BY_ == currentUserId) {
                s += '<span  title=' + projectChangeApplyList_name1 + ' onclick="editApplyRow(\'' + applyId + '\',\'' + instId + '\')">' + projectChangeApplyList_name1 + '</span>';
            } else {
                s += '<span  title=' + projectChangeApplyList_name1 + ' style="color: silver">' + projectChangeApplyList_name1 + '</span>';
            }

        } else {
            if (record.processTask) {
                s += '<span  title=' + projectChangeApplyList_name2 + ' onclick="doApplyTask(\'' + record.taskId + '\')">' + projectChangeApplyList_name2 + '</span>';
            } else {
                s += '<span  title=' + projectChangeApplyList_name2 + ' style="color: silver">' + projectChangeApplyList_name + '</span>';
            }
        }
        if (record.CREATE_BY_ == currentUserId && (record.instStatus == 'DRAFTED' || record.instStatus == 'DISCARD_END')) {
            s += '<span  title=' + projectChangeApplyList_name3 + ' onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + projectChangeApplyList_name3 + '</span>';
        } else {
            s += '<span  title=' + projectChangeApplyList_name3 + ' style="color: silver">' + projectChangeApplyList_name3 + '</span>';
        }
        return s;
    }

    function onUseStatusRenderer(e) {
        var record = e.record;
        var useStatus = record.useStatus;
        if (useStatus == 'yes') {
            return '<span>已使用</span>';
        } else {
            return '<span>未使用</span>';
        }
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var instStatus = record.instStatus;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, instStatus);
    }

    function jumpToDetail(e) {
        var record = e.record;
        var projectId = record.projectId;
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="detailProjectRow(\'' + projectId +'\',\''+'RUNNING'+ '\')">'+record.projectName+'</a>';
        return s;
    }
</script>
<redxun:gridScript gridId="applyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
