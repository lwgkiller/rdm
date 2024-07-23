<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>重大项目列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/yfjbMonthProductionsList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">整机型号: </span>
                    <input  name="productModel" class="mini-textbox rxc"   allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">年度: </span>
                    <input id="productYear" name="productYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px"  label="年度："
                           length="50"
                           only_read="false"  allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false" onvaluechanged="searchFrm()"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <a id="addButton"  class="mini-button" plain="true" onclick="addForm()">新增</a>
            <div style="display: inline-block" class="separator"></div>
            <a id="delButton" class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeRow()">删除</a>
            <div style="display: inline-block" class="separator"></div>
            <a class="mini-button" id="importId" style="margin-left: 10px" onclick="openImportWindow()">导入</a>
            <span style="color: red">（只有降本专员可以维护数据）</span>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="listGrid" class="mini-datagrid"  allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_" sortOrder="desc"
         url="${ctxPath}/rdmZhgl/core/yfjb/monthProducts/list.do" idField="id" showPager="true" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[15,20,50,100,200]" pageSize="15"  allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="30px"></div>
            <div type="indexcolumn"   align="center" headerAlign="center" width="40px">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="productYear" name="productYear" width="150px" headerAlign="center" align="center" allowSort="false">年份</div>
            <div field="productModel" name="productModel" width="150px" headerAlign="center" align="center" allowSort="false">整机型号</div>
            <div field="january"  width="150px" headerAlign="center" align="center" allowSort="false" renderer="onStatus">一月计划产量</div>
            <div field="february"  width="150px" headerAlign="center" align="center" >二月计划产量</div>
            <div field="march"  width="150px" headerAlign="center" align="center" allowSort="false">三月计划产量</div>
            <div field="april" width="150px" headerAlign="center" align="center" allowSort="false">四月计划产量</div>
            <div field="may"  width="150px" headerAlign="center" align="center" allowSort="false" >五月计划产量</div>
            <div field="june"  width="150px" headerAlign="center" align="center" allowSort="false">六月计划产量</div>
            <div field="july"  width="150px" headerAlign="center" align="center" allowSort="false">七月计划产量</div>
            <div field="august"  width="150px" headerAlign="center" align="center" allowSort="false">八月计划产量</div>
            <div field="september"  width="150px" headerAlign="center" align="center" allowSort="false">九月计划产量</div>
            <div field="october"  width="150px" headerAlign="center" align="center" allowSort="false">十月计划产量</div>
            <div field="november"  width="150px" headerAlign="center" align="center" allowSort="false">十一月计划产量</div>
            <div field="december"  width="150px" headerAlign="center" align="center" allowSort="false">十二月计划产量</div>
            <div field="creator"  width="150px" headerAlign="center" align="center" renderer="process"  allowSort="false">创建人</div>
            <div field="CREATE_TIME_"   width="150px" headerAlign="center" align="center" renderer="process"  allowSort="false">创建时间</div>
        </div>
    </div>
</div>
<div id="importWindow" title="月度产量导入窗口" class="mini-window" style="width:750px;height:280px;"
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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">月度产量导入模板.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">年份<span style="color: red">*</span>：</td>
                    <td style="width: 70%;">
                        <input id="reportYear" name="reportYear" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px"  label="年度："
                               length="50"
                               only_read="false" required="true"  allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
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
    listGrid.frozenColumns(0, 4);
    var permission = ${permission};
    if (!permission) {
        mini.get('addButton').setEnabled(false);
        mini.get('delButton').setEnabled(false);
        mini.get('importId').setEnabled(false);
    }
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var response = record.response;
        var creator = record.CREATE_BY_;
        var s = '';
        if(currentUserId!=response&&currentUserId!=creator){
            s += '<span  title="编辑" style="color: silver">编辑</span>';
        }else{
            s = '<span  title="编辑" onclick="editForm(\'' + id + '\')">编辑</span>';
        }
        return s;
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
