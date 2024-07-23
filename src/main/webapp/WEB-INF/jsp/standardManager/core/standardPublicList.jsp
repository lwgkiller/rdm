<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>产品标准公开管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/standardManager/standardPublicList.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        #systemSearch .mini-textbox {
            height: 27px;
        }

        #systemSearch .mini-textbox-border {
            height: 25px;
        }

        #systemSearch .mini-textbox-input {
            height: 25px;
            line-height: 23px;
        }
    </style>
</head>
<body>
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.standardPublicList.name1" />: </span>
                        <input class="mini-textbox" id="standardName" onenter="searchStandard">
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.standardPublicList.name2" />: </span>
                        <input class="mini-textbox" id="standardNumber" onenter="searchStandard">
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.standardPublicList.name3" />: </span>
                        <input class="mini-textbox" id="companyName" onenter="searchStandard">
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.standardPublicList.name4" />: </span>
                        <input id="standardStatus" class="mini-combobox" style="width:150px;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.standardPublicList.name5" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardPublicList.name5" />..."
                               value="enable" data="[ {'key' : 'enable','value' : '有效'},{'key' : 'disable','value' : '废止'}]"
                        />
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="searchStandard()"><spring:message code="page.standardPublicList.name6" /></a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                           onclick="clearSearchStandard()"><spring:message code="page.standardPublicList.name7" /></a>
                        <div style="display: inline-block" class="separator"></div>
                    </li>
                    <li id="operateStandard" style="display: none">
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="openStandardEditWindow('','add')"><spring:message code="page.standardPublicList.name8" /></a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                           onclick="removeStandard()"><spring:message code="page.standardPublicList.name9" /></a>
                    </li>
                    <li>
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="exportStandard()"><spring:message code="page.standardPublicList.name10" /></a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" multiSelect="true" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" showColumnsMenu="true"
             url="${ctxPath}/standardManager/core/standard/queryPublicList.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div cellCls="actionIcons" width="80" headerAlign="center" align="center"
                     renderer="operateRenderer" hideable="true"
                     cellStyle="padding:0;"><spring:message code="page.standardPublicList.name11" />
                </div>
                <div field="companyName" sortField="companyName" width="100" headerAlign="center" align="left"
                     allowSort="true" hideable="true"><spring:message code="page.standardPublicList.name3" />
                </div>
                <div field="standardNumber" sortField="standardNumber" width="130" headerAlign="center"
                     align="left" allowSort="true" hideable="true"><spring:message code="page.standardPublicList.name2" />
                </div>
                <div field="standardName" sortField="standardName" width="170" headerAlign="center" align="left"
                     allowSort="true" hideable="true"><spring:message code="page.standardPublicList.name1" />
                </div>
                <div field="standardStatus" sortField="standardStatus" width="30" headerAlign="center"
                     align="center" hideable="true"
                     allowSort="true" renderer="statusRenderer"><spring:message code="page.standardPublicList.name4" />
                </div>
                <div field="publishTime" sortField="publishTime" dateFormat="yyyy-MM-dd" align="center"
                     width="50" headerAlign="center" allowSort="true"><spring:message code="page.standardPublicList.name12" />
                </div>
                <div field="creator" align="center" width="30" headerAlign="center" allowSort="false"><spring:message code="page.standardPublicList.name13" />
                </div>
                <div cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="fileRenderer"
                     cellStyle="padding:0;" hideable="true"><spring:message code="page.standardPublicList.name14" />
                </div>
            </div>
        </div>
    </div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/standardManager/core/standard/exportPublicExcel.do" method="post"
      target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserRoles =${currentUserRoles};
    var isPointManager = whetherIsPointStandardManager('JS', currentUserRoles);
    var standardListGrid = mini.get("standardListGrid");
    var currentTime = "${currentTime}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;

    var standardNumber="${standardNumber}";
    var standardName="${standardName}";
    var companyName="${companyName}";
    //操作栏
    standardListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });

    function operateRenderer(e) {
        var record = e.record;
        var standardId = record.id;
        var s = '<span  title=' + standardPublicList_mx + ' onclick="openStandardEditWindow(\'' + standardId + '\',\'detail\')">' + standardPublicList_mx + '</span>';
        if (isPointManager) {
            s += '<span  title=' + standardPublicList_bj + ' onclick="openStandardEditWindow(\'' + standardId + '\',\'edit\')">' + standardPublicList_bj + '</span>';
            s += '<span title=' + standardPublicList_sc + ' onclick="removeStandard(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + standardPublicList_sc + '</span>';
        }
        return s;
    }

    function fileRenderer(e) {
        var record = e.record;
        var standardId = record.id;
        var existFile = record.existFile;
        var standardName = record.standardName;
        var standardNumber = record.standardNumber;
        var status = record.standardStatus;

        if (existFile) {
            var s = '<span title=' + standardPublicList_yl + '  onclick="previewStandard(\'' + standardId +'\',\''+ coverContent + '\')">' + standardPublicList_yl + '</span>';
            s += '<span title=' + standardPublicList_xz + ' onclick="downloadStandard(\'' + standardId + '\',\'' + standardName + '\',\'' + standardNumber + '\')">' + standardPublicList_xz + '</span>';
            return s;
        } else {
            var s = '<span title=' + standardPublicList_yl + '  style="color: silver">' + standardPublicList_yl + '</span>';
            s += '<span title=' + standardPublicList_xz + ' style="color: silver">' + standardPublicList_xz + '</span>';
            return s;
        }
    }

    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
</script>
</body>
</html>
