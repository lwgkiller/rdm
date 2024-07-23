<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>重大项目列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/gzxmProjectList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目名称: </span>
                    <input  name="projectName" class="mini-textbox rxc"   allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目负责人: </span>
                    <input  name="responsor" class="mini-textbox rxc"   allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">进度状态: </span>
                    <input id="currentStage" name="currentStage" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px"  label="进度状态："
                           length="50" onValuechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=WCJD"
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
            <a id="doApply" class="mini-button " style="margin-left: 10px;" plain="true" onclick="doApply()">项目审批</a>
            <a class="mini-button" id="importId" style="margin-left: 10px" onclick="openImportWindow()">导入</a>
            <a id="exportButton" class="mini-button" style="margin-left: 5px" plain="true" onclick="exportProjectTask()">导出</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="listGrid" class="mini-datagrid"  allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_" sortOrder="desc"
         url="${ctxPath}/rdmZhgl/core/gzxm/project/list.do" idField="id" showPager="true" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]"  allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn"  field="mainId" name="mainId" width="30px"></div>
            <div type="indexcolumn"   align="center" headerAlign="center" width="40px">序号</div>
            <div name="action" cellCls="actionIcons" width="150px" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="projectName" name="projectName" width="200px" headerAlign="center" align="center" allowSort="false" renderer="onProjectName">项目名称</div>
            <div field="responsor" name="responsor" width="200px" headerAlign="center" align="center" allowSort="false">项目负责人</div>
            <div field="responseDept" name="responseDept" width="200px" headerAlign="center" align="center" allowSort="false">项目承担单位</div>
            <div field="startDate" name="startDate" width="200px" headerAlign="center" align="center" allowSort="false" dateFormat="yyyy-MM-dd">开始日期</div>
            <div field="endDate" name="endDate" width="200px" headerAlign="center" align="center" allowSort="false" dateFormat="yyyy-MM-dd">结束日期</div>
            <div field="currentStage" name="currentStage" width="200px" headerAlign="center" align="center" allowSort="false" renderer="onFinishProcess">进度状态</div>
            <div field="reportUserName" name="reportUserName" width="100px" headerAlign="center" align="center" allowSort="false" >填报人</div>
<%--            <div field="subjectName" name="subjectName" width="150px" headerAlign="center" align="center" renderer="onSubjectName">课题名称</div>--%>
<%--            <div field="subjectResponsor" name="subjectResponsor" width="150px" headerAlign="center" align="center">课题负责人</div>--%>
<%--            <div field="subjectResponseDept" name="subjectResponseDept" width="150px" headerAlign="center" align="center">课题承担单位</div>--%>
        </div>
    </div>
</div>
<form id="excelForm" action=""
      method="post" target="excelIFrame">
    <input type="hidden" name="pageIndex"/>
    <input type="hidden" name="pageSize"/>
    <input type="hidden" name="filter" id="filter">
</form>
<iframe id="excelUnIFrame" name="excelIFrame" style="display: none;"></iframe>
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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">国重项目任务导入模板.xlsx</a>
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
    var listGrid = mini.get("listGrid");
    var finishProcessList = getDics("WCJD");
    var importWindow = mini.get("importWindow");
    var gzxmAdmin = ${gzxmAdmin};
    var isReporter = false;
    listGrid.on("load", function () {
        // listGrid.mergeColumns(["projectName","responsor","responseDept","startDate","endDate","currentStage","reportUserName"]);
    });
    if(!gzxmAdmin){
        mini.get("importId").setEnabled(false);
        mini.get("exportButton").setEnabled(false);
    }
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = "";
        if(record.reportUserId==currentUserId){
            isReporter = true;
        }
        if(currentUserId!=record.reportUserId&&currentUserId!=record.CREATE_BY_&&currentUserId!='1'){
            s += '<span  title="编辑" style="color: silver">编辑</span>';
            s += '<span  title="任务列表" style="color: silver">任务列表</span>';
        }else{
            s += '<span  title="编辑" onclick="editForm(\'' + id + '\')">编辑</span>';
            s += '<span  title="任务列表" onclick="taskPage(\'' + id + '\',\'' + gzxmAdmin + '\',\'' + isReporter + '\')">任务列表</span>';
        }
        return s;
    }
    function onProjectName(e) {
        var record = e.record;
        var id = record.id;
        var s = '';
        s += '<span style="cursor: pointer;color: #0a7ac6"  onclick="overSee(\'' + id + '\')">'+record.projectName+'</span>';
        return s;
    }
    function onFinishProcess(e) {
        var record = e.record;
        var resultValue = record.currentStage;
        var resultText = '';
        for (var i = 0; i < finishProcessList.length; i++) {
            if (finishProcessList[i].key_ == resultValue) {
                resultText = finishProcessList[i].text;
                break
            }
        }
        return resultText;
    }
    //修改
    function overSee(mainId) {
        var url = jsUseCtxPath + "/rdmZhgl/core/gzxm/project/getViewPage.do?action=view&&mainId=" + mainId;
        window.open(url);
    }
    function onSubjectName(e) {
        var record = e.record;
        var id = record.subjectId;
        var s = '';
        if(record.subjectName){
            s += '<span style="cursor: pointer;color: #0a7ac6"  onclick="overSeeSubject(\'' + id + '\')">'+record.subjectName+'</span>';
        }
        return s;
    }
    function overSeeSubject(mainId) {
        var url = jsUseCtxPath + "/rdmZhgl/core/gzxm/subject/getViewPage.do?action=edit&&mainId=" + mainId;
        window.open(url);
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
