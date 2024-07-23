<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>审批列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/gkgf/gkgfApplyList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.gkgfApplyList.name" />: </span>
                    <input class="mini-textbox" name="applyUserName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.gkgfApplyList.name1" />: </span>
                    <input class="mini-textbox" name="model"/>
                </li>
                <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                      style="width:auto"><spring:message code="page.gkgfApplyList.name2" />: </span>
                    <input id="instStatus" name="instStatus" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.gkgfApplyList.name3" />..." onvaluechanged="searchFrm()"
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.gkgfApplyList.name3" />..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                    />
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em><spring:message code="page.gkgfApplyList.name4" /></em>
						<i class="unfoldIcon"></i>
					</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.gkgfApplyList.name5" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.gkgfApplyList.name6" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeApply()"><spring:message code="page.gkgfApplyList.name7" /></a>
                    <a id="deptApply" class="mini-button " style="margin-left: 10px;" plain="true"
                       onclick="doApply()"><spring:message code="page.gkgfApplyList.name8" /></a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.gkgfApplyList.name9" />: </span>
                        <input class="mini-textbox" name="applyId"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.gkgfApplyList.name10" /> </span>:<input name="apply_startTime"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:100px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto"><spring:message code="page.gkgfApplyList.name11" />: </span><input name="apply_endTime"
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
         url="${ctxPath}/gkgf/core/apply/queryList.do?applyType=${applyType}"
         idField="applyId" sortOrder="desc" sortField="applyTime"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.gkgfApplyList.name12" />
            </div>
            <div field="id" sortField="id" width="100" headerAlign="center" align="center" allowSort="true"><spring:message code="page.gkgfApplyList.name9" /></div>
            <div field="model" sortField="model" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.gkgfApplyList.name1" /></div>
            <div field="userName" sortField="userName" width="40" headerAlign="center" align="center" allowSort="true">
                <spring:message code="page.gkgfApplyList.name" />
            </div>
            <div field="instStatus" width="70" headerAlign="center" align="center" renderer="onStatusRenderer"
                 allowSort="false"><spring:message code="page.gkgfApplyList.name13" />
            </div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="50" align="center"
                 headerAlign="center" allowSort="false"><spring:message code="page.gkgfApplyList.name14" />
            </div>
            <div field="currentProcessTask" sortField="currentProcessTask" width="80" align="center"
                 headerAlign="center" allowSort="false"><spring:message code="page.gkgfApplyList.name15" />
            </div>
            <div field="applyTime" sortField="applyTime" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="100"
                 headerAlign="center" allowSort="true"><spring:message code="page.gkgfApplyList.name16" />
            </div>
            <div field="reason" width="60" headerAlign="center" align="center" allowSort="false"><spring:message code="page.gkgfApplyList.name17" /></div>
            <div field="INST_ID_" visible="false"></div>
            <div field="taskId" visible="false"></div>
            <div field="processTask" visible="false"></div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyListGrid = mini.get("applyListGrid");
    var currentUserId = "${currentUser.userId}";
    var applyTypeList = getDics("YDGZSPLX");
    var applyType = "${applyType}";

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.INST_ID_;
        var applyType = record.applyType;
        var s = '<span  title=' + gkgfApplyList_name + ' onclick="detailApply(\'' + applyId + '\',\'' + record.instStatus + '\',\'' + applyType + '\')">' + gkgfApplyList_name + '</span>';
        if (record.instStatus == 'DRAFTED') {
            if (record.CREATE_BY_ == currentUserId) {
                s += '<span  title=' + gkgfApplyList_name1 + ' onclick="editApplyRow(\'' + applyId + '\',\'' + instId + '\',\'' + applyType + '\')">' + gkgfApplyList_name1 + '</span>';
            } else {
                s += '<span  title=' + gkgfApplyList_name1 + ' style="color: silver">' + gkgfApplyList_name1 + '</span>';
            }

        } else {
            if (record.processTask) {
                s += '<span  title=' + gkgfApplyList_name2 + ' onclick="doApplyTask(\'' + record.taskId + '\',\'' + applyType + '\')">' + gkgfApplyList_name2 + '</span>';
            } else {
                s += '<span  title=' + gkgfApplyList_name2 + ' style="color: silver">' + gkgfApplyList_name2 + '</span>';
            }
        }
        if (record.CREATE_BY_ == currentUserId && (record.instStatus == 'DRAFTED' || record.instStatus == 'DISCARD_END')) {
            s += '<span  title=' + gkgfApplyList_name3 + ' onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + gkgfApplyList_name3 + '</span>';
        } else {
            s += '<span  title=' + gkgfApplyList_name3 + ' style="color: silver">' + gkgfApplyList_name3 + '</span>';
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

    function onApplyType(e) {
        var record = e.record;
        var applyType = record.applyType;
        var resultText = '';
        for (var i = 0; i < applyTypeList.length; i++) {
            if (applyTypeList[i].key_ == applyType) {
                resultText = applyTypeList[i].text;
                break
            }
        }
        return resultText;
    }
</script>
<redxun:gridScript gridId="applyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
