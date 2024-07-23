<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/serviceEngineering/standardvalueShipmentnotmadeList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardvalueShipmentnotmadeList.name" />：</span>
                    <input class="mini-textbox" id="department" name="department"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardvalueShipmentnotmadeList.name1" />：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardvalueShipmentnotmadeList.name2" />：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardvalueShipmentnotmadeList.name3" />：</span>
                    <input class="mini-textbox" id="materialName" name="materialName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardvalueShipmentnotmadeList.name4" />：</span>
                    <input id="versionType" name="versionType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.standardvalueShipmentnotmadeList.name5" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardvalueShipmentnotmadeList.name5" />..."
                           data="[{key:'cgb',value:'常规版'},{key:'csb',value:'测试版'},{key:'wzb',value:'完整版'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardvalueShipmentnotmadeList.name6" />：</span>
                    <input class="mini-textbox" id="principal" name="principal"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardvalueShipmentnotmadeList.name7" />：</span>
                    <input id="betaCompletion" name="betaCompletion" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.standardvalueShipmentnotmadeList.name5" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardvalueShipmentnotmadeList.name5" />..."
                           data="[{key: 'dzz',value:'待制作'},{key: 'zzing',value:'制作中'},{key: 'zzwc',value:'制作完成'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardvalueShipmentnotmadeList.name8" />：</span>
                    <input id="responseStatus" name="responseStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.standardvalueShipmentnotmadeList.name5" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardvalueShipmentnotmadeList.name5" />..."
                           data="[{key: '正常',value:'正常'},{key: '一级',value:'一级'},{key: '二级',value:'二级'},{key: '三级',value:'三级'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.standardvalueShipmentnotmadeList.name9" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.standardvalueShipmentnotmadeList.name10" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addStandardvalueShipmentnotmade()"><spring:message code="page.standardvalueShipmentnotmadeList.name11" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="delStandardvalueShipmentnotmade()"><spring:message code="page.standardvalueShipmentnotmadeList.name12" /></a>
                    <a class="mini-button" id="importId" style="margin-left: 5px" plain="true" onclick="openImportWindow()"><spring:message code="page.standardvalueShipmentnotmadeList.name13" /></a>
                </li>
            </ul>
        </form>
        <%--<span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">--%>
				<%--<i class="icon-sc-lower"></i>--%>
        <%--</span>--%>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="standardvalueShipmentnotmadeListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
         showCellTip="true" idField="id" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/standardvalue/shipmentnotmade/dataListQuery.do" >
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center"><spring:message code="page.standardvalueShipmentnotmadeList.name14" /></div>
            <div name="action" cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.standardvalueShipmentnotmadeList.name15" /></div>
            <div field="department" width="80" headerAlign="center" align="center"><spring:message code="page.standardvalueShipmentnotmadeList.name" /></div>
            <div field="salesModel" name="salesModel" width="120" headerAlign="center" align="center"><spring:message code="page.standardvalueShipmentnotmadeList.name1" /></div>
            <div field="materialCode" name="materialCode" width="120" headerAlign="center" align="center"><spring:message code="page.standardvalueShipmentnotmadeList.name2" /></div>
            <div field="materialName" name="materialName" width="120" headerAlign="center" align="center"><spring:message code="page.standardvalueShipmentnotmadeList.name3" /></div>
            <div field="versionType" headerAlign="center" align="center" allowSort="false" renderer="versionTypeRenderer"><spring:message code="page.standardvalueShipmentnotmadeList.name4" /></div>
            <div field="pinFour" name="pinFour" width="120" headerAlign="center" align="center"><spring:message code="page.standardvalueShipmentnotmadeList.name16" /></div>
            <div field="principal" name="principal" width="120" headerAlign="center" align="center"><spring:message code="page.standardvalueShipmentnotmadeList.name6" /></div>
            <div field="betaCompletion" name="betaCompletion" width="120" headerAlign="center" align="center" renderer="completionRenderer"><spring:message code="page.standardvalueShipmentnotmadeList.name7" /></div>
            <div field="responseTime" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true"><spring:message code="page.standardvalueShipmentnotmadeList.name17" /></div>
            <div field="responseStatus" align="center" headerAlign="center"><spring:message code="page.standardvalueShipmentnotmadeList.name8" /></div>
            <div field="creator" name="betaCompletion" width="80" headerAlign="center" align="center"><spring:message code="page.standardvalueShipmentnotmadeList.name18" /></div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true"><spring:message code="page.standardvalueShipmentnotmadeList.name19" /></div>
        </div>
    </div>
</div>

<div id="importWindow" title="<spring:message code="page.standardvalueShipmentnotmadeList.name20" />" class="mini-window" style="width:750px;height:300px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importStandardvalueShipmentnotmade()"><spring:message code="page.standardvalueShipmentnotmadeList.name13" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeWindow()"><spring:message code="page.standardvalueShipmentnotmadeList.name21" /></a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">

                <tr>
                    <td style="width: 30%"><spring:message code="page.standardvalueShipmentnotmadeList.name22" />：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downloadTemplate()"><spring:message code="page.standardvalueShipmentnotmadeList.name23" />.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%"><spring:message code="page.standardvalueShipmentnotmadeList.name15" />：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="validFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile"><spring:message code="page.standardvalueShipmentnotmadeList.name24" /></a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile"><spring:message code="page.standardvalueShipmentnotmadeList.name25" /></a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var standardvalueShipmentnotmadeListGrid = mini.get("standardvalueShipmentnotmadeListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var mainGroupName='${mainGroupName}';
    var importWindow = mini.get("importWindow");
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title=' + standardvalueShipmentnotmadeList_name + ' onclick="toStandardvalueShipmentnotmadeDetail(\'' + record.id + '\')">' + standardvalueShipmentnotmadeList_name + '</span>';
        if(record.betaCompletion == 'dzz' && (currentUserId == record.CREATE_BY_ || currentUserNo=='admin' || mainGroupName == '服务工程技术研究所')) {
            s +='<span  title=' + standardvalueShipmentnotmadeList_name1 + ' onclick="editStandardvalueShipmentnotmade(\'' + record.id + '\')">' + standardvalueShipmentnotmadeList_name1 + '</span>'
                + '<span  title=' + standardvalueShipmentnotmadeList_name2 + ' onclick="delStandardvalueShipmentnotmade('+JSON.stringify(record).replace(/"/g, '&quot;')+')">' + standardvalueShipmentnotmadeList_name2 + '</span>'
        }
        return s;
    }
</script>
<redxun:gridScript gridId="standardvalueShipmentnotmadeListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>