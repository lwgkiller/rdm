<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>项目预估分统计报表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul style="margin-left: 15px">
                <li style="margin-right: 15px"><span class="text" style="width:auto">员工姓名:</span>
                    <input class="mini-textbox" id="personName" name="personName"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">员工部门:</span>
                    <input class="mini-textbox" id="deptName" name="deptName"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目名称: </span>
                    <input class="mini-textbox" name="projectName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">项目录入时间 从 </span>:<input id="projectStartTime"
                                                                                         name="projectStartTime"
                                                                                         class="mini-datepicker"
                                                                                         format="yyyy-MM-dd"
                                                                                         style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至: </span><input id="projectEndTime" name="projectEndTime"
                                                                               class="mini-datepicker"
                                                                               format="yyyy-MM-dd" style="width:120px"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">预估分查询时间 从 </span>:<input
                        id="personEvaluateScoreStartTime" name="personEvaluateScoreStartTime" class="mini-datepicker"
                        format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至: </span><input id="personEvaluateScoreEndTime"
                                                                               name="personEvaluateScoreEndTime"
                                                                               class="mini-datepicker"
                                                                               format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目类别: </span>
                    <input id="projectCategory" name="categoryId" class="mini-combobox" style="width:150px;"
                           textField="categoryName" valueField="categoryId" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目级别: </span>
                    <input id="projectLevel" name="levelId" class="mini-combobox" style="width:150px;"
                           textField="levelName" valueField="levelId" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目状态: </span>
                    <input id="instStatus" name="status" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportPersonEvaluateScore()">导出</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="personEvaluateScoreListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/xcmgProjectManager/report/xcmgProject/personEvaluateScoreList.do" idField="projectId"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true">
        <div property="columns">
            <div type="indexcolumn"></div>
            <div field="personName" name="personName" width="120" headerAlign="center" align="center">员工姓名</div>
            <div field="deptName" name="deptName" width="120" headerAlign="center" align="center">员工部门</div>
            <div field="projectName" width="240" headerAlign="center" align="center" renderer="jumpToDetail">项目名称
            </div>
            <div field="categoryName" name="categoryName" width="160" headerAlign="center" align="center">项目类别</div>
            <div field="levelName" name="levelName" width="70" headerAlign="center" align="center">项目级别</div>

            <div field="number" width="120" headerAlign="center" align="center">项目编号</div>
            <div field="STATUS" width="80" headerAlign="center" align="center" renderer="onStatusRenderer">项目状态
            </div>
            <div field="personEvaluateScore" width="120" headerAlign="center" align="center">预估分</div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/xcmgProjectManager/report/xcmgProject/exportPersonEvaluateScoreList.do"
      method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var clickDeptName = "${clickDeptName}";
    var projectStartTime = "${projectStartTime}";
    var projectEndTime = "${projectEndTime}";
    var personEvaluateScoreListGrid = mini.get("personEvaluateScoreListGrid");


    //行功能按钮
    function jumpToDetail(e) {
        var record = e.record;
        var projectId = record.projectId;
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="detailProjectRow(\'' + projectId + '\',\'' + record.STATUS + '\')">' + record.projectName + '</a>';
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.STATUS;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, status);
    }

    $(function () {
        var nowYear = new Date().getFullYear();
        mini.get("projectStartTime").setValue(nowYear + "-01-01");
        mini.get("projectEndTime").setValue(nowYear + "-12-31");
        mini.get("personEvaluateScoreStartTime").setValue(nowYear + "-01-01");
        mini.get("personEvaluateScoreEndTime").setValue(nowYear + "-12-31");
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getProjectEditRule.do',
            type: 'get',
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    mini.get("#projectCategory").load(data.category);
                    mini.get("#projectLevel").load(data.level);
                }
            }
        });
        searchFrm();
    });

    //明细
    function detailProjectRow(projectId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/edit.do?action=" + action + "&projectId=" + projectId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (progressListGrid) {
                    progressListGrid.reload()
                }
                ;
            }
        }, 1000);
    }

    function exportPersonEvaluateScore() {
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
<redxun:gridScript gridId="personEvaluateScoreListGrid" entityName="" winHeight="" winWidth="" entityTitle=""
                   baseUrl=""/>
</body>
</html>
