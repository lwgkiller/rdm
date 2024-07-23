<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>报告证书列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/wwrz/wwrzReportFileList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">销售型号: </span><input
                        class="mini-textbox" style="width: 120px" name="productModel"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">设计型号: </span><input
                        class="mini-textbox" style="width: 120px" name="designModel"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">文件类型: </span>
                    <input id="reportType" name="reportType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:120px;height:34px" label="报告类型：" onvaluechanged="searchFrm()"
                           length="50"
                           only_read="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="text" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=BGLX"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">文件编号: </span><input
                        class="mini-textbox" style="width: 120px" name="reportCode"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">文件名称: </span>
                    <input id="reportName" name="reportName" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:300px;height:34px" label="报告类型：" onvaluechanged="searchFrm()"
                           length="50"
                           only_read="false" allowinput="false"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="text" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=WWRZ-WJMC"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">产品主管: </span><input
                        class="mini-textbox" style="width: 120px" name="productManager"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">状态: </span>
                    <input id="valid" name="valid" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:300px;height:34px" label="报告类型：" onvaluechanged="searchFrm()"
                           length="50"
                           only_read="false" allowinput="false"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=WWRZ-ZSZT"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">文件日期从:</span>
                        <input name="reportDate_start" allowInput="false"
                               class="mini-datepicker"
                               format="yyyy-MM-dd"
                               style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">至:</span>
                        <input name="reportDate_end" allowInput="false"
                               class="mini-datepicker"
                               format="yyyy-MM-dd"
                               style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">有效期从: </span>
                        <input name="reportValidity_start" allowInput="false"
                               class="mini-datepicker"
                               format="yyyy-MM-dd"
                               style="width:120px"/></li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">至: </span>
                        <input name="reportValidity_end" allowInput="false"
                               class="mini-datepicker"
                               format="yyyy-MM-dd"
                               style="width:120px"/></li>

                </ul>
                <ul class="toolBtnBox">
<%--                    <a class="mini-button" id="importId" style="margin-left: 10px" img="${ctxPath}/scripts/mini/miniui/res/images/textfield_add.png"--%>
<%--                       onclick="openImportWindow()">历史数据导入</a>--%>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="delButton" class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeRow()">删除</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="invalidButton" class="mini-button" style="margin-left: 10px" plain="true" onclick="batchInvalid()">作废</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="addButton" class="mini-button" style="margin-right: 5px" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="addReport()">新增</a>
                     <a id="exportBtn" class="mini-button" style="margin-left: 5px" plain="true" onclick="exportExcel()">按条件导出</a>
                </ul>
            </div>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/wwrz/core/file/exportReportExcel.do" method="post"
              target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/wwrz/core/file/reportFiles.do?fileType=report" idField="id" sortField="reportDate" sortOrder="desc"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="productModel" name="productModel" width="80" headerAlign="center" align="center"
                 allowSort="true">销售型号
            </div>
            <div field="designModel" name="designModel" width="80" headerAlign="center" align="center"
                 allowSort="true">设计型号
            </div>
            <div field="reportType" width="80" headerAlign="center" align="center" allowSort="true">文件类型</div>
            <div field="reportCode" width="120" headerAlign="center" align="center" allowSort="true">文件编号</div>
            <div field="reportName" width="120" headerAlign="center" align="center" allowSort="true">文件名称</div>
            <div field="reportDate" width="80" headerAlign="center" align="center" allowSort="true">文件日期</div>
            <div field="reportValidity" width="80" headerAlign="center" align="center" allowSort="true">有效期</div>
            <div field="productManager" width="80" headerAlign="center" align="center" allowSort="true">产品主管</div>
            <div field="valid" width="80" headerAlign="center" align="center" allowSort="true" renderer="onValidStatus">状态</div>
            <div field="action" width="100" headerAlign='center' align="center" renderer="operationRendererSq">操作</div>
        </div>
    </div>
</div>
<div id="importWindow" title="认证资料导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importDocument()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">报告证书导入模板.xls</a>
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
    var listGrid = mini.get("listGrid");
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var permission = ${permission};
    var exportPermission = ${exportPermission};
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var importWindow = mini.get("importWindow");
    if(!permission){
        mini.get('importId').setEnabled(false);
        mini.get('delButton').setEnabled(false);
        mini.get('invalidButton').setEnabled(false);
        mini.get('addButton').setEnabled(false);
    }
    if(!exportPermission){
        mini.get('exportBtn').setEnabled(false);
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
