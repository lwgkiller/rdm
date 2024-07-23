<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准预览申请列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/standardManager/standardApplyList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-left:15px;margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardApplyList.name1" />: </span>
                    <input class="mini-textbox" name="applyId"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardApplyList.name2" />: </span>
                    <input class="mini-textbox" name="applyUserName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardApplyList.name3" />: </span>
                    <input class="mini-textbox" name="applyStandardNumber">
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.standardApplyList.name4" />: </span>
                    <input class="mini-textbox" name="applyStandardName"/>
                </li>

                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em><spring:message code="page.standardApplyList.name5" /></em>
						<i class="unfoldIcon"></i>
					</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.standardApplyList.name6" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.standardApplyList.name7" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="addApply()"><spring:message code="page.standardApplyList.name8" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeApply()"><spring:message code="page.standardApplyList.name9" /></a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                          style="width:auto"><spring:message code="page.standardApplyList.name10" />: </span>
                        <input id="instStatus" name="instStatus" class="mini-combobox" style="width:150px;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.standardApplyList.name11" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardApplyList.name11" />..."
                               data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                        />
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.standardApplyList.name12" />:</span>
                        <input id="useStatus" name="useStatus" class="mini-combobox" style="width:150px;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.standardApplyList.name11" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardApplyList.name11" />..."
                               data="[ {'key' : 'yes','value' : '已使用'},{'key' : 'no','value' : '未使用'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.standardApplyList.name13" /> </span>:<input name="applyTimeStart"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:100px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto"><spring:message code="page.standardApplyList.name14" />: </span><input name="applyTimeEnd"
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
         url="${ctxPath}/standardManager/core/standardApply/applyQuery.do?applyCategoryId=${applyCategoryId}"
         idField="applyId"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.standardApplyList.name15" />
            </div>
            <div field="applyId" sortField="applyId" width="130" headerAlign="center" align="left" allowSort="true">
                <spring:message code="page.standardApplyList.name1" />
            </div>
            <div field="applyUserName" sortField="applyUserName" width="70" headerAlign="center" align="center"
                 allowSort="true"><spring:message code="page.standardApplyList.name2" />
            </div>
            <div field="standardNumber" sortField="standardNumber" width="110" headerAlign="center" align="center"
                 allowSort="true"><spring:message code="page.standardApplyList.name3" />
            </div>
            <div field="standardName" width="180" headerAlign="center" align="center" allowSort="false"><spring:message code="page.standardApplyList.name4" /></div>
            <div field="systemName" width="90" headerAlign="center" align="center" allowSort="false"><spring:message code="page.standardApplyList.name16" /></div>
            <div field="applyReason" width="160" headerAlign="center" align="center" allowSort="false"><spring:message code="page.standardApplyList.name17" /></div>
            <div field="instStatus" width="70" headerAlign="center" align="center" renderer="onStatusRenderer"
                 allowSort="false"><spring:message code="page.standardApplyList.name18" />
            </div>
            <div field="useStatus" width="70" renderer="onUseStatusRenderer" headerAlign="center" align="center"
                 allowSort="false"><spring:message code="page.standardApplyList.name19" />
            </div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="70" align="center"
                 headerAlign="center" allowSort="false"><spring:message code="page.standardApplyList.name20" />
            </div>
            <div field="applyTime" sortField="applyTime" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="100"
                 headerAlign="center" allowSort="true"><spring:message code="page.standardApplyList.name21" />
            </div>
            <div field="INST_ID_" visible="false"></div>
            <div field="taskId" visible="false"></div>
            <!--判断当前登录人是否是流程处理人!-->
            <div field="processTask" visible="false"></div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyListGrid = mini.get("applyListGrid");
    var currentUserId = "${currentUser.userId}";
    var applyCategoryId = "${applyCategoryId}";
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.applyId;
        var instId = record.INST_ID_;
        var s = '';
        if (record.instStatus == 'DRAFTED') {
            if (record.CREATE_BY_ == currentUserId) {
                s += '<span  title=' + standardApplyList_bj + ' onclick="editApplyRow(\'' + applyId + '\',\'' + instId + '\')">' + standardApplyList_bj + '</span>';
            } else {
                s += '<span  title=' + standardApplyList_bj + ' style="color: silver">' + standardApplyList_bj + '</span>';
            }

        } else {
            if (record.processTask) {
                s += '<span  title=' + standardApplyList_bl + ' onclick="doApplyTask(\'' + record.taskId + '\')">' + standardApplyList_bl + '</span>';
            } else {
                s += '<span  title=' + standardApplyList_bl + ' style="color: silver">' + standardApplyList_bl + '</span>';
            }
        }
        if(record.CREATE_BY_ == currentUserId && (record.instStatus == 'DRAFTED'||record.instStatus == 'DISCARD_END')) {
            s += '<span  title=' + standardApplyList_sc + ' onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + standardApplyList_sc + '</span>';
        } else {
            s += '<span  title=' + standardApplyList_sc + ' style="color: silver">' + standardApplyList_sc + '</span>';
        }
        if(record.CREATE_BY_ == currentUserId && record.instStatus == 'SUCCESS_END' && record.useStatus == 'no'){
            s += '<span title=' + standardApplyList_xz + ' onclick="downloadStandard(\'' + record.standardId +'\',\''+record.standardName+'\',\''+record.standardNumber+'\',\''+record.standardStatus+ '\',\''+record.applyId+ '\')">' + standardApplyList_xz + '</span>';
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
    function downloadStandard(standardId, standardName, standardNumber, status,applyId) {
        if(status=='disable') {
            mini.confirm(standardApplyList_gbzyfz,standardApplyList_ts,function (action) {
                if(action=="ok") {
                    downloadStandardDo(standardId, standardName, standardNumber, applyId);
                }
            });
        }
        else {
            downloadStandardDo(standardId, standardName, standardNumber, applyId);
        }
    }
    function downloadStandardDo(standardId, standardName, standardNumber,applyId) {
        changeApplyUseStatus(applyId, 'yes');
        //记录下载情况
        recordStandardOperate('download', standardId);

        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/standardManager/core/standard/download.do");
        var standardIdAttr = $("<input>");
        standardIdAttr.attr("type", "hidden");
        standardIdAttr.attr("name", "standardId");
        standardIdAttr.attr("value", standardId);
        var standardNameAttr = $("<input>");
        standardNameAttr.attr("type", "hidden");
        standardNameAttr.attr("name", "standardName");
        standardNameAttr.attr("value", standardName);
        var standardNumberAttr = $("<input>");
        standardNumberAttr.attr("type", "hidden");
        standardNumberAttr.attr("name", "standardNumber");
        standardNumberAttr.attr("value", standardNumber);
        $("body").append(form);
        form.append(standardIdAttr);
        form.append(standardNameAttr);
        form.append(standardNumberAttr);
        form.submit();
        form.remove();
        searchFrm();
    }
    function changeApplyUseStatus(applyId, useStatus) {
        $.ajax({
            url: jsUseCtxPath + "/standardManager/core/standardApply/changeUseStatus.do?applyId=" + applyId + "&useStatus=" + useStatus,
            method: 'GET',
            success: function () {

            }
        });
    }

    function recordStandardOperate(action, standardId) {
        $.ajax({
            url: jsUseCtxPath + "/standardManager/core/standard/record.do?standardId=" + standardId + "&action=" + action,
            method: 'GET',
            success: function (data) {

            }
        });
    }

</script>
<redxun:gridScript gridId="applyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
