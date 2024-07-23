<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>机型测试需求下发回传列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/serviceEngineering/jxxqxfList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxxqxfList.name" />：</span>
                    <input class="mini-textbox" id="issueDepartment" name="issueDepartment" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxxqxfList.name1" />：</span>
                    <input class="mini-textbox" id="productDepartment" name="productDepartment" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxxqxfList.name2" />：</span>
                    <input id="versionType" name="versionType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.jxxqxfList.name3" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxxqxfList.name3" />..."
                           data="[{key:'cgb',value:'常规版'},{key:'wzb',value:'完整版'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxxqxfList.name4" />：</span>
                    <input id="productType" name="productType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.jxxqxfList.name3" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxxqxfList.name3" />..."
                           data="[{key:'lunWa',value:'轮挖'},{key:'lvWa',value:'履挖'},{key:'teWa',value:'特挖'},{key:'dianWa',value:'电挖'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxxqxfList.name5" />：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jxxqxfList.name6" />：</span>
                    <div id="passBacks" name="passBacks" class="mini-combobox" popoupWith="400" textField="value" valueField="key" value="hcz,whc"
                    multiSelect="true" showClose="true" oncloseClick="oncloseClick" style="width: 180px;" emptyText="<spring:message code="page.jxxqxfList.name3" />..."
                    data="[{key:'hcwc',value:'回传完成'},{key:'hcz',value:'回传中'},{key:'whc',value:'未回传'}]">
                        <div property="columns">
                            <div header="回传状态" field="value"></div>
                        </div>
                    </div>
                </li>
                <li>
                    <span class="text" style="width:auto"><spring:message code="page.jxxqxfList.name7" />：</span>
                    <input id="priority" name="priority" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="<spring:message code="page.jxxqxfList.name7" />："
                           length="50"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="<spring:message code="page.jxxqxfList.name3" />..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=ZYD"
                           nullitemtext="<spring:message code="page.jxxqxfList.name3" />..." emptytext="<spring:message code="page.jxxqxfList.name3" />..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.jxxqxfList.name8" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearQueryForm()"><spring:message code="page.jxxqxfList.name9" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addJxxqxf()"><spring:message code="page.jxxqxfList.name10" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="delJxxqxf()"><spring:message code="page.jxxqxfList.name11" /></a>
                    <a class="mini-button" id="importId" style="margin-left: 5px" plain="true" onclick="openImportWindow()"><spring:message code="page.jxxqxfList.name12" /></a>
                    <a class="mini-button" style="margin-left: 10px" plain="true" onclick="exportJxxqxf()"><spring:message code="page.jxxqxfList.name13" /></a>
                </li>
            </ul>
        </form>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/jxxqxf/exportJxxqxf.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<div class="mini-fit" style="height: 100%;">
    <div id="jxxqxfGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/jxxqxf/jxxqxfListQuery.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center"><spring:message code="page.jxxqxfList.name14" /></div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.jxxqxfList.name15" /></div>
            <div field="issueDepartment" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxxqxfList.name" /></div>
            <div field="productDepartment" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxxqxfList.name1" /></div>
            <div field="versionType" headerAlign="center" align="center" allowSort="false" renderer="versionTypeRenderer"><spring:message code="page.jxxqxfList.name2" /></div>
            <div field="productType" headerAlign="center" align="center" allowSort="false" renderer="productTypeRenderer"><spring:message code="page.jxxqxfList.name4" /></div>
            <div field="materialCode" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxxqxfList.name5" /></div>
            <div field="salesModel" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxxqxfList.name16" /></div>
            <div field="designModel" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxxqxfList.name17" /></div>
            <div field="pinCode" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxxqxfList.name18" /></div>
            <div field="priority" headerAlign="center" align="center" allowSort="false" renderer="priorityRenderer"><spring:message code="page.jxxqxfList.name7" /></div>
            <div field="passBack" headerAlign="center" align="center" allowSort="false" renderer="passBackRenderer"><spring:message code="page.jxxqxfList.name6" /></div>
            <div field="passBackNum" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxxqxfList.name19" /></div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true"><spring:message code="page.jxxqxfList.name20" /></div>
            <div field="creator" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxxqxfList.name21" /></div>
            <div field="jxhcl" name ="jxhcl" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxxqxfList.name22" /></div>
            <div field="zshcl" name ="zshcl" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxxqxfList.name23" /></div>
        </div>
    </div>
</div>

<div id="importWindow" title="<spring:message code="page.jxxqxfList.name24" />" class="mini-window" style="width:750px;height:300px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importJxxqxf()"><spring:message code="page.jxxqxfList.name12" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeWindow()"><spring:message code="page.jxxqxfList.name25" /></a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%"><spring:message code="page.jxxqxfList.name" />：</td>
                    <td style="width: 70%;">
                        <input id="issueDepartmentId" name="issueDepartmentId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="issueDepartment" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%"><spring:message code="page.jxxqxfList.name2" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="importVersionType" name="importVersionType" class="mini-combobox" style="width:98%"
                               textField="value" valueField="key" emptyText="<spring:message code="page.jxxqxfList.name3" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxxqxfList.name3" />..."
                               data="[{key:'cgb',value:'常规版'},{key:'wzb',value:'完整版'}]"
                        />
                    </td>
                </tr>

                <tr>
                    <td style="width: 30%"><spring:message code="page.jxxqxfList.name26" />：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downloadTemplate()"><spring:message code="page.jxxqxfList.name27" />.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%"><spring:message code="page.jxxqxfList.name15" />：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="validFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile"><spring:message code="page.jxxqxfList.name28" /></a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile"><spring:message code="page.jxxqxfList.name29" /></a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var jxxqxfGrid=mini.get("jxxqxfGrid");
    var importWindow = mini.get("importWindow");
    var priorityList = getDics("ZYD");
    var mainGroupName = "${mainGroupName}";
    if (mainGroupName == '质量保证部' || mainGroupName == '测试研究所') {
        mini.get("issueDepartment").setEnabled(false);
        mini.get("issueDepartment").setValue(mainGroupName);
    }
    //必须为要合并的列增加name属性
    jxxqxfGrid.on("load", function () {
        jxxqxfGrid.mergeColumns(["jxhcl","zshcl"]);
    })
    //操作栏
    jxxqxfGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title=' + jxxqxfList_name + ' onclick="toJxxqxfDetail(\'' + record.id + '\')">' + jxxqxfList_name + '</span>';
        if(currentUserId == record.CREATE_BY_ || currentUserNo=='admin') {
            s +='<span  title=' + jxxqxfList_name1 + ' onclick="editJxxqxf(\'' + record.id + '\')">' + jxxqxfList_name1 + '</span>'
                + '<span  title=' + jxxqxfList_name2 + ' onclick="delJxxqxf('+JSON.stringify(record).replace(/"/g, '&quot;')+')">' + jxxqxfList_name2 + '</span>'
        }
        return s;
    }
</script>
<redxun:gridScript gridId="jxxqxfGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

