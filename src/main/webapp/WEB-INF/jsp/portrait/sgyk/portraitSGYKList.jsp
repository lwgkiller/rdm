<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>三高一可列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/portrait/sgyk/portraitSGYKList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属部门: </span><input class="mini-textbox" style="width: 150px" name="deptName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">姓名: </span><input class="mini-textbox" style="width: 150px" name="userName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">年月: </span><input class="mini-monthpicker" style="width: 150px" name="yearMonth"/></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <div style="display: inline-block" class="separator"></div>
            <a id="addButton" class="mini-button" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="addRow()">新增</a>
            <a id="delButton" class="mini-button" style="margin-left: 10px"  img="${ctxPath}/scripts/mini/miniui/res/images/cancel.png" onclick="removeRow()">删除</a>
            <a class="mini-button" id="importId" style="margin-left: 10px" img="${ctxPath}/scripts/mini/miniui/res/images/textfield_add.png"
               onclick="openImportWindow()">导入</a>
            <div style="display: inline-block" class="separator"></div>

        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="sgykListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/portrait/sgyk/sgykList.do" idField="id" sortField="UPDATE_TIME_" sortOrder="desc"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="deptName" width="80" headerAlign="center" align="center" allowSort="true">所属部门</div>
            <div field="userName" width="80" headerAlign="center" align="center" allowSort="true">姓名</div>
            <div field="yearMonth" width="80" headerAlign="center" align="center" allowSort="true" >年月</div>
            <div field="indexName" width="120" headerAlign="center" align="center" allowSort="true" >指标名称</div>
            <div field="finishRate" width="80" headerAlign="center" align="center" allowSort="true" >完成情况</div>
            <div field="weight" width="80" headerAlign="center" align="center" allowSort="true" >权重</div>
            <div field="score" width="80" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false">添加日期</div>
        </div>
    </div>
</div>
<div id="importWindow" title="三高一可贡献导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
        <%--        <image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;vertical-align: middle;height: 15px"/>--%>
        <%--        注意：公司领导不计入排名，无需将领导加入作者中排名！--%>
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importSGYK()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">三高一可贡献导入模板.xls</a>
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
    var sgykListGrid = mini.get("sgykListGrid");
    var importWindow = mini.get("importWindow");
    var permission = ${permission};
    if(true){
        mini.get("addButton").setEnabled(false);
        mini.get("delButton").setEnabled(false);
        mini.get("importId").setEnabled(false);
    }
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '<span  title="查看" onclick="viewForm(\'' + id + '\',\'view\')">查看</span>';
        if(permission){
            s += '<span  title="编辑" onclick="viewForm(\'' + id + '\',\'edit\')">编辑</span>';
        }
        return s;
    }
</script>
<redxun:gridScript gridId="sgykListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
