<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>机型测试需求下发</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/serviceEngineering/jxbzzbshList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxbzzbshList.name" />：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxbzzbshList.name1" />：</span>
                    <input class="mini-textbox" id="productDepartment" name="productDepartment" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxbzzbshList.name2" />：</span>
                    <input id="productType" name="productType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.jxbzzbshList.name3" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxbzzbshList.name3" />..."
                           data="[{key:'lunWa',value:'轮挖'},{key:'lvWa',value:'履挖'},{key:'teWa',value:'特挖'},{key:'dianWa',value:'电挖'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxbzzbshList.name4" />：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxbzzbshList.name5" />：</span>
                    <input class="mini-textbox" id="materialName" name="materialName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxbzzbshList.name6" />：</span>
                    <input id="versionType" name="versionType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.jxbzzbshList.name3" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxbzzbshList.name3" />..."
                           data="[{key:'cgb',value:'常规版'},{key:'csb',value:'测试版'},{key:'wzb',value:'完整版'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxbzzbshList.name7" />：</span>
                    <input id="taskStatus" name="taskStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.jxbzzbshList.name3" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxbzzbshList.name3" />..."
                           data="[{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},{'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
                                    {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},{'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
                                    {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},{'key': 'PENDING', 'value': '挂起', 'css': 'gray'}]"
                    />
                </li>
                <br/>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.jxbzzbshList.name8" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.jxbzzbshList.name9" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addJxbzzbsh()"><spring:message code="page.jxbzzbshList.name10" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="changeJxbzzbsh()"><spring:message code="page.jxbzzbshList.name11" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="delJxbzzbsh()"><spring:message code="page.jxbzzbshList.name12" /></a>
                    <a id="muban" class="mini-button" style="margin-right: 5px;display: none" plain="true" onclick="moban()"><spring:message code="page.jxbzzbshList.name13" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportJxbzzbsh()">导出</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="jxbzzbshGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/jxbzzbsh/jxbzzbshListQuery.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="40" headerAlign="center" align="center"><spring:message code="page.jxbzzbshList.name14" /></div>
            <div name="action" cellCls="actionIcons" width="170" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.jxbzzbshList.name15" /></div>
            <div field="materialCode" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxbzzbshList.name" /></div>
            <div field="productDepartment" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxbzzbshList.name1" /></div>
            <div field="productType" headerAlign="center" align="center" allowSort="false" renderer="productTypeRenderer"><spring:message code="page.jxbzzbshList.name2" /></div>
            <div field="salesModel" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxbzzbshList.name4" /></div>
            <div field="materialName" name="materialName" width="120" headerAlign="center" align="center"><spring:message code="page.jxbzzbshList.name5" /></div>
            <div field="versionType" headerAlign="center" align="center" allowSort="false" renderer="versionTypeRenderer"><spring:message code="page.jxbzzbshList.name6" /></div>
            <div field="gssNum" headerAlign="center" align="center" allowSort="false">手册编码</div>
            <div field="applicationNumber" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxbzzbshList.name16" /></div>
            <div field="versionNum" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxbzzbshList.name17" /></div>
            <div field="pinFour" headerAlign="center" align="center" allowSort="false"  width="110">适用第四位PIN</div>
            <div field="note" headerAlign="center" align="center" allowSort="false" renderer="noteStatusRenderer"><spring:message code="page.jxbzzbshList.name18" /></div>
            <div field="taskName" headerAlign='center' align='center'><spring:message code="page.jxbzzbshList.name19" /></div>
            <div field="allTaskUserNames" headerAlign='center' align='center'><spring:message code="page.jxbzzbshList.name20" /></div>
            <div field="taskStatus"  headerAlign="center" align="center" renderer="taskStatusRenderer"><spring:message code="page.jxbzzbshList.name7" /></div>
            <div field="creator" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxbzzbshList.name21" /></div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true"><spring:message code="page.jxbzzbshList.name22" /></div>
        </div>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/jxbzzbsh/exportJxbzzbsh.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var currentUserRoles =${currentUserRoles};
    var jxbzzbshGrid=mini.get("jxbzzbshGrid");
    var isJXZY=${isJXZY};
    if(isJXZY){
        $("#muban").show();
    }

    //操作栏
    jxbzzbshGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title=' + jxbzzbshList_name + ' onclick="toJxbzzbshDetail(\'' + record.id + '\')">' + jxbzzbshList_name + '</span>';
        if (record.taskStatus == 'DRAFTED' && record.CREATE_BY_ == currentUserId) {
            s += '<span  title=' + jxbzzbshList_name1 + ' onclick="editJxbzzbsh(\'' + record.id + '\',\'' + record.instId + '\')">' + jxbzzbshList_name1 + '</span>';
        }
        debugger
        if (currentUserRoles.indexOf("jsbzzUpload") > -1) {
            s += '<span  title="上传附件" onclick="uploadJxbzzbsh(\'' + record.id + '\',\'' + record.instId + '\')">上传附件</span>';
        }
        if (record.myTaskId) {
            s += '<span  title=' + jxbzzbshList_name2 + ' onclick="jxbzzbshTask(\'' + record.myTaskId + '\',\'' + record.taskStatus + '\')">' + jxbzzbshList_name2 + '</span>';
        }
        if (currentUserNo != 'admin') {
            if (record.taskStatus == 'DRAFTED' && currentUserId == record.CREATE_BY_) {
                s += '<span  title=' + jxbzzbshList_name3 + ' onclick="delJxbzzbsh(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + jxbzzbshList_name3 + '</span>';
            }
        } else {
            s += '<span  title=' + jxbzzbshList_name3 + ' onclick="delJxbzzbsh(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + jxbzzbshList_name3 + '</span>';
        }
        return s;
    }

    function moban() {
        mini.open({
            title: jxbzzbshList_name4,
            url: jsUseCtxPath + "/serviceEngineering/core/jxbzzbsh/fileList.do",
            width: 850,
            height: 800,
            allowResize: true,
            onload: function () {
                searchFrm();
            }
        });
    }

    function noteStatusRenderer(e) {
        var record = e.record;
        var note = record.note;
        var arr = [{'key': '最新版本', 'value': '最新版本', 'css': 'green'},
            {'key': '历史版本', 'value': '历史版本', 'css': 'red'}];
        return $.formatItemValue(arr, note);
    }

    function exportJxbzzbsh() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
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
        excelForm.submit();
    }
</script>
<redxun:gridScript gridId="jxbzzbshGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

