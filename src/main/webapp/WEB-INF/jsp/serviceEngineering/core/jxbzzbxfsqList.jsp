<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>检修标准值表下发申请</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/serviceEngineering/jxbzzbxfsqList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxbzzbxfsqList.name" />：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxbzzbxfsqList.name1" />：</span>
                    <input class="mini-textbox" id="creator" name="creator" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxbzzbxfsqList.name2" />：</span>
                    <input class="mini-textbox" id="applyDept" name="applyDept" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxbzzbxfsqList.name3" />：</span>
                    <input id="taskStatus" name="taskStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.jxbzzbxfsqList.name4" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxbzzbxfsqList.name4" />..."
                           data="[{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},{'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
                                    {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},{'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
                                    {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},{'key': 'PENDING', 'value': '挂起', 'css': 'gray'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.jxbzzbxfsqList.name5" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.jxbzzbxfsqList.name6" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addJxbzzbxfsq()"><spring:message code="page.jxbzzbxfsqList.name7" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="delJxbzzbxfsq()"><spring:message code="page.jxbzzbxfsqList.name8" /></a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="jxbzzbxfsqGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/jxbzzbxfsq/jxbzzbxfsqListQuery.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center"><spring:message code="page.jxbzzbxfsqList.name9" /></div>
            <div name="action" cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.jxbzzbxfsqList.name10" /></div>
            <div field="materialCode" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxbzzbxfsqList.name" /></div>
            <div field="creator" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxbzzbxfsqList.name1" /></div>
            <div field="applyDept" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxbzzbxfsqList.name2" /></div>
            <div field="taskName" headerAlign='center' align='center'><spring:message code="page.jxbzzbxfsqList.name11" /></div>
            <div field="allTaskUserNames" headerAlign='center' align='center'><spring:message code="page.jxbzzbxfsqList.name12" /></div>
            <div field="taskStatus"  headerAlign="center" align="center" renderer="taskStatusRenderer"><spring:message code="page.jxbzzbxfsqList.name3" /></div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true"><spring:message code="page.jxbzzbxfsqList.name13" /></div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var jxbzzbxfsqGrid=mini.get("jxbzzbxfsqGrid");
    //操作栏
    jxbzzbxfsqGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title=' + jxbzzbxfsqList_name + ' onclick="jxbzzbxfsqDetail(\'' + record.id + '\')">' + jxbzzbxfsqList_name + '</span>';
        if (record.taskStatus == 'DRAFTED' && record.CREATE_BY_ == currentUserId) {
            s += '<span  title=' + jxbzzbxfsqList_name1 + ' onclick="editJxbzzbxfsq(\'' + record.id + '\',\'' + record.instId + '\')">' + jxbzzbxfsqList_name1 + '</span>';
        }
        if (record.myTaskId) {
            s += '<span  title=' + jxbzzbxfsqList_name2 + ' onclick="jxbzzbxfsqTask(\'' + record.myTaskId + '\',\'' + record.taskStatus + '\')">' + jxbzzbxfsqList_name2 + '</span>';
        }
        if (currentUserNo != 'admin') {
            if (record.taskStatus == 'DRAFTED' && currentUserId == record.CREATE_BY_) {
                s += '<span  title=' + jxbzzbxfsqList_name3 + ' onclick="delJxbzzbxfsq(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + jxbzzbxfsqList_name3 + '</span>';
            }
        } else {
            s += '<span  title=' + jxbzzbxfsqList_name3 + ' onclick="delJxbzzbxfsq(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + jxbzzbxfsqList_name3 + '</span>';
        }
        return s;
    }
</script>
<redxun:gridScript gridId="jxbzzbxfsqGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

