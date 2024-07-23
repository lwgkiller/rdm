<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>年度开发计划-预算</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/ndkfjhBudgetList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目编号: </span>
                    <input name="projectCode" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目名称: </span>
                    <input name="projectName" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">年度: </span>
                    <input id="budgetYear" name="budgetYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="年度："
                           length="50"
                           only_read="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           onvaluechanged="searchFrm()"
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
            <a id="addButton" class="mini-button" plain="true" onclick="addForm()">新增</a>
            <div style="display: inline-block" class="separator"></div>
            <a id="delButton" class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeRow()">删除</a>
            <div style="display: inline-block" class="separator"></div>
            <a class="mini-button" id="importId" style="margin-left: 10px" onclick="openImportWindow()">导入</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_"
         sortOrder="desc"
         url="${ctxPath}/rdmZhgl/core/ndkfjh/budget/list.do" idField="id" showPager="true" allowCellWrap="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[15,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" field="mainId" name="mainId" width="30px"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
            <div field="id" name="id" name="action" cellCls="actionIcons" width="120px" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="projectCode" name="projectCode" width="150px" headerAlign="center" align="center"
                 allowSort="false">项目编号
            </div>
            <div field="projectName" name="projectName"  width="150px" headerAlign="center" align="left" allowSort="false">项目名称</div>
            <div field="target" name="target"  width="350px" headerAlign="center" align="left">工作目标</div>
            <div field="content" name="content"  width="350px" headerAlign="center" align="left" allowSort="false">工作内容</div>
            <div field="coreTechnology" name="coreTechnology"  width="350px" headerAlign="center" align="left" allowSort="false">关键/核心技术
            </div>
            <div field="newProductNum" name="newProductNum"  width="200px" headerAlign="center" align="center" allowSort="false">
                产出新产品合计（台套数）
            </div>
            <div field="firstName" name="firstName"  width="200px" headerAlign="center" align="center" allowSort="false">其中：首台套（名称）</div>
            <div field="upProductName" name="upProductName"  width="200px" headerAlign="center" align="center" allowSort="false">其中：升级产品（名称）
            </div>
            <div field="improveName" name="improveName"  width="200px" headerAlign="center" align="center" allowSort="false">其中：改进产品（名称）
            </div>
            <div field="chiefEngineer" name="chiefEngineer"  width="150px" headerAlign="center" align="center" allowSort="false">首席工程师</div>
            <div field="patentNum" name="patentNum"  width="200px" headerAlign="center" align="center" allowSort="false">发明专利（预算当年数）</div>
            <div field="startDate"  name="startDate" width="150px" headerAlign="center" align="center" allowSort="false">项目开始日期</div>
            <div field="endDate" name="endDate"  width="150px" headerAlign="center" align="center" allowSort="false">项目结束日期</div>
            <div field="products" name="products"  width="150px" headerAlign="center" align="center" allowSort="false">产品列表</div>
            <div field="chargerMan" name="chargerMan"  width="150px" headerAlign="center" align="center" allowSort="false">负责人</div>
            <div field="deptName" name="deptId"  width="150px" headerAlign="center" align="center" allowSort="false">部门</div>
        </div>
    </div>
</div>
<div id="importWindow" title="年度预算导入窗口" class="mini-window" style="width:750px;height:280px;"
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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">年度预算导入模板.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">预算年份<span style="color: red">*</span>：</td>
                    <td style="width: 70%;">
                        <input id="reportYear" name="reportYear" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="年度："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="value" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                               nullitemtext="请选择..." emptytext="请选择..."/>
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
    var importWindow = mini.get("importWindow");
    // listGrid.frozenColumns(0, 4);
    var permission = ${permission};
    if (!permission) {
        mini.get('addButton').setEnabled(false);
        mini.get('delButton').setEnabled(false);
        mini.get('importId').setEnabled(false);
    }
    listGrid.on("load", function () {
        listGrid.mergeColumns(["id","mainId","projectCode","projectName","target","content","coreTechnology","newProductNum",
            "firstName",  "upProductName", "improveName","chiefEngineer","patentNum","startDate","endDate"]);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var creator = record.CREATE_BY_;
        var s = '';
        if (currentUserId != creator && !permission) {
            s += '<span  title="编辑" style="color: silver">编辑</span>';
        } else {
            s = '<span  title="编辑" onclick="editForm(\'' + id + '\',\'edit\')">编辑</span>';
        }
        s += '<span  title="查看" onclick="editForm(\'' + id + '\',\'view\')">查看</span>';
        return s;
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
