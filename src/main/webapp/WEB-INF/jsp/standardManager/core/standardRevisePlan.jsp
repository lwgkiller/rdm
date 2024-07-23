<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准制修订计划</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectConfig.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style type="text/css">
        .custom-hidden{
            visibility: hidden;
        }
    </style>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li>
            <span class="text" style="width:auto"><spring:message code="page.standardRevisePlan.name1" />: </span>
            <input id="belongYear" style="width: 80px;" class="mini-spinner" minValue="2000" maxValue="3000" allowInput="false" value=""/>
            <span class="text" style="width:auto"><spring:message code="page.standardRevisePlan.name2" />: </span>
            <input id="deptId" name="deptId" required="true" class="mini-dep rxc" plugins="mini-dep" showclose="false"
            style="width:200px;height:34px" allowinput="false" textname="respDeptNames" length="40" maxlength="40" minlen="0" single="true" required="false" initlogindep="false"/>
            <a class="mini-button" style="margin-right: 5px" plain="true" id="searchButton"
               onclick="searchName()"><spring:message code="page.standardRevisePlan.name3" /></a>
            <a class="mini-button btn-red" plain="true" onclick="refreshAchievementType()" plain="true"><spring:message code="page.standardRevisePlan.name4" /></a>
        </li>
        <span class="separator"></span>
        <li>
            <a id="addKpi" class="mini-button" iconCls="icon-add" onclick="addAchievementType()"><spring:message code="page.standardRevisePlan.name5" /></a>
            <a id="saveKpi" class="mini-button" iconCls="icon-save" onclick="saveAchievementType()"><spring:message code="page.standardRevisePlan.name6" /></a>
            <p style="display: inline-block;color: #f6253e;font-size:15px;vertical-align: middle">（<image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;vertical-align: middle;height: 15px"/><spring:message code="page.standardRevisePlan.name7" />）
            </p>
        </li>

    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" idField="id"
         url="${ctxPath}/standardManager/core/standardManagement/revisePlanList.do"
         showColumnsMenu="false" allowAlternating="true"
         showPager="false" allowCellEdit="true" allowCellSelect="true"
         editNextOnEnterKey="true" editNextRowCell="true" oncellvalidation="onCellValidation">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="15"><spring:message code="page.standardRevisePlan.name8" /></div>
            <div name="deptId" field="deptId" visible="false"></div>
            <div name="deptName" field="deptName" width="45" headerAlign="center" align="center">
                <spring:message code="page.standardRevisePlan.name2" />
            </div>
            <div field="belongYear" width="30" headerAlign="center" align="center">
                <spring:message code="page.standardRevisePlan.name1" /></div>
            <div field="belongJd" width="30" headerAlign="center" align="center">
                <spring:message code="page.standardRevisePlan.name9" /></div>
            <div name="planNum" field="planNum" width="30" headerAlign="center" align="center">
                <spring:message code="page.standardRevisePlan.name10" /><span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox"/>
            </div>
            <div name="jdStartYearMonth" field="jdStartYearMonth" visible="false"></div>
            <div name="jdEndYearMonth" field="jdEndYearMonth" visible="false"></div>
            <div field="creator" width="30" headerAlign='center' align="center"><spring:message code="page.standardRevisePlan.name11" /></div>
            <div field="CREATE_TIME_" width="50" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">
                <spring:message code="page.standardRevisePlan.name12" />
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var isOperator =${isOperator};
    var listGrid = mini.get("listGrid");
    $(function () {
        refreshAchievementType();
        if(!isOperator) {
            mini.get("addKpi").setEnabled(false);
            mini.get("saveKpi").setEnabled(false);
        }
    });

    function searchName() {
        var queryParam = [];
        //其他筛选条件
        let belongYear = $.trim(mini.get("belongYear").getValue());
        let deptId = $.trim(mini.get("deptId").getValue());
        if(!belongYear) {
            mini.alert(standardRevisePlan_name1);
            return;
        }
        if(!deptId) {
            mini.alert(standardRevisePlan_name2);
            return;
        }
        queryParam.push({name: "deptId", value: deptId});
        queryParam.push({name: "belongYear", value: belongYear});
        var data = {};
        data.filter = JSON.stringify(queryParam);
        listGrid.load(data, function (e) {
            isShowEmpty(e.data);
        });
    }

    // 清空查询
    function refreshAchievementType() {
        mini.get("belongYear").setValue(new Date().getFullYear());
        mini.get("deptId").setValue('161416982793248776');
        mini.get("deptId").setText('小挖研究所');
        searchName();
    }

    // 新增
    function addAchievementType() {
        var queryData=listGrid.getData();
        if(queryData && queryData.length >0) {
            mini.alert(standardRevisePlan_name3);
            return;
        }
        let deptId =  mini.get("deptId").getValue();
        let deptName =  mini.get("deptId").getText();
        var belongYear=  mini.get("belongYear").getValue();
        let newRows = [];
        for (let i = 1; i <= 4; i++) {
            var jdStartYearMonth=belongYear+"-";
            var jdEndYearMonth=belongYear+"-";
            if(i==1) {
                jdStartYearMonth+='01';
                jdEndYearMonth+='03';
            }
            if(i==2) {
                jdStartYearMonth+='04';
                jdEndYearMonth+='06';
            }
            if(i==3) {
                jdStartYearMonth+='07';
                jdEndYearMonth+='09';
            }
            if(i==4) {
                jdStartYearMonth+='10';
                jdEndYearMonth+='12';
            }
            let data = {
                deptId: deptId,
                deptName: deptName,
                belongYear: belongYear,
                belongJd: i+'季度',
                jdStartYearMonth:jdStartYearMonth,
                jdEndYearMonth:jdEndYearMonth,
                planNum: 0
            };
            newRows.push(data);
        }
        listGrid.addRows(newRows);
    }

    // 刷新
    listGrid.on("beforeload", function (e) {
        if (listGrid.getChanges().length > 0) {
            if (confirm(standardRevisePlan_name4)) {
                e.cancel = true;
            }
        }
    });

    // 单元格编辑事件
    listGrid.on("cellbeginedit", function (e) {
        let record = e.record;
        if (isOperator) {
            // 专利工程师或超级管理员 ===> 允许
            e.cancel = false;
        } else {
            mini.alert(standardRevisePlan_name5);
            e.cancel = true;
        }
    });

    // 单元格数字验证
    function verifyValue(e) {
        let value = e.value;
        let isNotEmpty = !(value === undefined || value === null || value === "");
        let isRange = value >= 0;
        if (isNotEmpty && !isRange ) {
            e.isValid = false;
            e.errorText = standardRevisePlan_name6;
        } else {
            e.isValid = true
        }
    }

    // 自定义验证
    function onCellValidation(e) {
        if(e.field=='planNum') {
            verifyValue(e);
        }
    }

    // 保存
    function saveAchievementType() {
        // 验证表格
        listGrid.validate();
        // 验证是否通过
        if (listGrid.isValid() === false) {
            var error = listGrid.getCellErrors()[0];
            listGrid.beginEditCell(error.record, error.column);
            mini.alert(error.errorText);
            return;
        }
        // 获取变化数据
        var data = listGrid.getChanges();
        var message = standardRevisePlan_name7;
        if (data.length > 0) {
            var json = mini.encode(data);
            $.ajax({
                url: jsUseCtxPath + "/standardManager/core/standardManagement/revisePlanSave.do",
                data: json,
                type: "post",
                contentType: 'application/json',
                async: false,
                success: function (text) {
                    if (text && text.message) {
                        message = text.message;
                    }
                }
            });
        }
        mini.alert(message,standardRevisePlan_name8,function(action){
            listGrid.reload();
        });
    }

    // 暂无数据
    function isShowEmpty(data){
        if (data.length === 0 && !$('.listEmpty').length) {
            $('#' + listGrid.id + ' .mini-panel-body .mini-grid-rows-view').append("<div class='listEmpty'><img src='" + __rootPath + "/styles/images/index/empty2.png'/><span>暂无数据</span></div>");
        } else if (data.length > 0 && $('.listEmpty').length) {
            $('.listEmpty').remove();
        }
    }

</script>
</body>
</html>
