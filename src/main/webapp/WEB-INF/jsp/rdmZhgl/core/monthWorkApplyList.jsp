<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>月度工作列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/monthWorkApplyList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.monthWorkApplyList.name" />: </span>
                    <input class="mini-textbox" name="applyUserName"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.monthWorkApplyList.name1" />: </span><input
                        id="yearMonth" allowinput="false"  class="mini-monthpicker" onvaluechanged="searchFrm()"
                        style="width: 150px" name="yearMonth"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.monthWorkApplyList.name2" />: </span>
                    <input id="applyType" name="applyType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="<spring:message code="page.monthWorkApplyList.name2" />："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="<spring:message code="page.monthWorkApplyList.name3" />..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YDGZSPLX"
                           nullitemtext="<spring:message code="page.monthWorkApplyList.name3" />..." emptytext="<spring:message code="page.monthWorkApplyList.name3" />..."/>
                </li>
                <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                      style="width:auto"><spring:message code="page.monthWorkApplyList.name4" />: </span>
                    <input id="instStatus" name="instStatus" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.monthWorkApplyList.name3" />..." onvaluechanged="searchFrm()"
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.monthWorkApplyList.name3" />..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                    />
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em><spring:message code="page.monthWorkApplyList.name5" /></em>
						<i class="unfoldIcon"></i>
					</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.monthWorkApplyList.name6" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.monthWorkApplyList.name7" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeApply()"><spring:message code="page.monthWorkApplyList.name8" /></a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.monthWorkApplyList.name9" />: </span>
                        <input class="mini-textbox" name="applyId"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.monthWorkApplyList.name10" /> </span>:<input name="apply_startTime"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:100px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto"><spring:message code="page.monthWorkApplyList.name11" />: </span><input name="apply_endTime"
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
         url="${ctxPath}/rdmZhgl/core/monthWorkApply/queryList.do"
         idField="applyId"  sortOrder="desc" sortField="applyTime"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.monthWorkApplyList.name12" /></div>
            <div field="id" sortField="id" width="100" headerAlign="center" align="left" allowSort="true"><spring:message code="page.monthWorkApplyList.name9" /></div>
            <div field="applyTypeText" width="100" headerAlign="center" align="center" renderer="onApplyType"><spring:message code="page.monthWorkApplyList.name2" /></div>
            <div field="userName" sortField="userName" width="40" headerAlign="center" align="center" allowSort="true"><spring:message code="page.monthWorkApplyList.name" /></div>
            <div field="deptName" width="100" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkApplyList.name13" /></div>
            <div field="yearMonth" width="100" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkApplyList.name14" /></div>
            <div field="instStatus" width="70" headerAlign="center" align="center" renderer="onStatusRenderer" allowSort="false"><spring:message code="page.monthWorkApplyList.name15" /></div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="50" align="center" headerAlign="center" allowSort="false"><spring:message code="page.monthWorkApplyList.name16" /></div>
            <div field="applyTime" sortField="applyTime" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="100" headerAlign="center" allowSort="true"><spring:message code="page.monthWorkApplyList.name17" /></div>
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
    var isAdmin = ${isAdmin};
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.INST_ID_;
        var applyType = record.applyType;
        var s = '<span  title=' + monthWorkApplyList_name + ' onclick="detailApply(\'' + applyId +'\',\''+record.instStatus+ '\',\''+applyType+ '\')">' + monthWorkApplyList_name + '</span>';
        if (record.instStatus == 'DRAFTED') {
            if (record.CREATE_BY_ == currentUserId) {
                s += '<span  title=' + monthWorkApplyList_name1 + ' onclick="editApplyRow(\'' + applyId + '\',\'' + instId + '\',\''+applyType+ '\')">' + monthWorkApplyList_name1 + '</span>';
            } else {
                s += '<span  title=' + monthWorkApplyList_name1 + ' style="color: silver">' + monthWorkApplyList_name1 + '</span>';
            }

        } else {
            if (record.processTask) {
                s += '<span  title=' + monthWorkApplyList_name2 + ' onclick="doApplyTask(\'' + record.taskId + '\',\''+applyType+ '\')">' + monthWorkApplyList_name2 + '</span>';
            } else {
                s += '<span  title=' + monthWorkApplyList_name2 + ' style="color: silver">' + monthWorkApplyList_name2 + '</span>';
            }
        }
        if(record.CREATE_BY_ == currentUserId && (record.instStatus == 'DRAFTED'||record.instStatus == 'DISCARD_END')) {
            s += '<span  title=' + monthWorkApplyList_name3 + ' onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + monthWorkApplyList_name3 + '</span>';
        } else {
            s += '<span  title=' + monthWorkApplyList_name3 + ' style="color: silver">' + monthWorkApplyList_name3 + '</span>';
        }
        if(record.instStatus=='RUNNING'&&isAdmin){
            s += '<span  title=' + monthWorkApplyList_name4 + ' onclick="notice(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + monthWorkApplyList_name4 + '</span>';
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
<redxun:gridScript gridId="applyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
