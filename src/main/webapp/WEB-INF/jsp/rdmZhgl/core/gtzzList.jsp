<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>重大项目列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/gtzzList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">创建部门: </span>
                    <input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                           style="width:300px;height:34px" onvaluechanged="searchList()"
                           allowinput="false" textname="deptName" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目名称: </span>
                    <input  name="projectName" class="mini-textbox rxc"   allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">年度: </span>
                    <input id="projectYear" name="projectYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px"  label="年度："
                           length="50"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false" onvaluechanged="searchFrm()"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <a  class="mini-button" plain="true" onclick="addForm()">新增</a>
            <div style="display: inline-block" class="separator"></div>
            <a class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeRow()">删除</a>
            <a class="mini-button" id="importId" style="margin-left: 10px" onclick="openImportWindow()">导入</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="planListGrid" class="mini-datagrid"  allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_" sortOrder="desc"
         url="${ctxPath}/rdmZhgl/core/gtzz/list.do" idField="id" showPager="true" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]"  allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn"  field="mainId" name="mainId" width="20px"></div>
            <div type="indexcolumn"   align="center" headerAlign="center" width="30px">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="projectName" name="projectName" width="200px" headerAlign="center" align="center" allowSort="false">项目名称</div>
            <div field="currentStage" name="currentStage" width="200px" headerAlign="center" align="center" allowSort="false">当前阶段</div>
            <div field="finishProcess" name="finishProcess" width="200px" headerAlign="center" align="center" renderer="onFinishProcess">完成进度</div>
            <div field="creator" name="finishProcess" width="200px" headerAlign="center" align="center" allowSort="false">负责人</div>
            <div field="deptName" name="deptName" width="200px" headerAlign="center" align="center" allowSort="false">负责部门</div>
            <div field="CREATE_TIME_"  width="200px" headerAlign="center" align="center" allowSort="false">创建日期</div>
        </div>
    </div>
</div>
<div id="importWindow" title="挂图作战导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importProduct()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">挂图作战导入模板.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
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
    var planListGrid = mini.get("planListGrid");
    var finishProcessList = getDics("WCJD");
    var importWindow = mini.get("importWindow");
    planListGrid.on("load", function () {
        planListGrid.mergeColumns(["mainId","deptName"]);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '<span  title="编辑" onclick="editForm(\'' + id + '\')">编辑</span>';
        return s;
    }
    function onFinishProcess(e) {
        var record = e.record;
        var resultValue = record.finishProcess;
        var resultText = '';
        for (var i = 0; i < finishProcessList.length; i++) {
            if (finishProcessList[i].key_ == resultValue) {
                resultText = finishProcessList[i].text;
                break
            }
        }
        return resultText;
    }
</script>
<redxun:gridScript gridId="planListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
