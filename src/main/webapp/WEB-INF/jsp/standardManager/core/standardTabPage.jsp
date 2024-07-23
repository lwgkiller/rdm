<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准资源管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/standardManager/standardManagement.js?version=${static_res_version}"
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
<div id="loading" class="loading" style="display:none;text-align:center;"><img src="${ctxPath}/styles/images/loading.gif"></div>
<div class="mini-splitter" style="width:100%;height:100%;" id="content">
    <div size="14%" showCollapseButton="true">
        <div id="systemSearch" class="mini-toolbar" style="padding:2px;border-top:0;border-left:0;border-right:0;">
            <span style="font-size: 14px;color: #777"><spring:message code="page.standardTabPage.txmc" />: </span>
            <input class="mini-textbox" width="100" id="filterNameId" onenter="filterSystemTreeInStandard()"/><br/>
            <span style="font-size: 14px;color: #777"><spring:message code="page.standardTabPage.txbh" />: </span>
            <input class="mini-textbox" width="100" id="filterNumberId" onenter="filterSystemTreeInStandard()"/>
            <a class="mini-button" style="width: 60px;height: 30px" plain="true" onclick="filterSystemTreeInStandard()"><spring:message code="page.standardTabPage.ss" /></a>
        </div>
        <hr color="#ddd"/>
        <div class="mini-fit">
            <ul id="systemTree" class="mini-tree"
                url="${ctxPath}/standardManager/core/standardSystem/treeQuery.do?systemCategoryId=${tabName}"
                style="width:100%;height:98%;padding:5px;"
                showTreeIcon="true" textField="systemName" expandOnLoad="0" idField="id" parentField="parentId"
                resultAsTree="false"
            ></ul>
        </div>
    </div>
    <div showCollapseButton="true">
        <div class="mini-toolbar">
            <div class="searchBox">
                <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                    <ul>
                        <li style="margin-left:15px;margin-right: 10px"><span class="text" style="width:auto"><spring:message code="page.standardTabPage.bh" />: </span><input
                                class="mini-textbox" id="standardNumber" onenter="searchStandard"></li>
                        <li style="margin-right: 10px"><span class="text" style="width:auto"><spring:message code="page.standardTabPage.mc" />: </span><input
                                class="mini-textbox" id="standardName" onenter="searchStandard"></li>
                        <li style="margin-right: 10px"><span class="text" style="width:auto"><spring:message code="page.standardTabPage.lb" />: </span>
                            <input id="category" class="mini-combobox" style="width:150px;"
                                   textField="categoryName" valueField="id" emptyText="<spring:message code="page.standardTabPage.qxz" />..."
                                   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardTabPage.qxz" />..."/>
                        </li>
                        <li style="margin-right: 10px"><span class="text" style="width:auto"><spring:message code="page.standardTabPage.zt" />: </span>
                            <input id="status" class="mini-combobox" style="width:100px;"
                                   textField="value" valueField="key" emptyText="<spring:message code="page.standardTabPage.qxz" />..."
                                   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardTabPage.qxz" />..."
                                   value="enable"
                                   data="[ {'key' : 'enable','value' : '有效'},{'key' : 'disable','value' : '已废止'},{'key' : 'draft','value' : '草稿'}]"
                            />
                        </li>
                        <li class="liBtn">
								<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
									<em><spring:message code="page.standardTabPage.zk" /></em>
									<i class="unfoldIcon"></i>
								</span>
                        </li>

                        <li style="margin-left: 10px">
                            <a class="mini-button" style="margin-right: 5px" plain="true"
                               onclick="searchStandard()"><spring:message code="page.standardTabPage.cx" /></a>
                            <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                               onclick="clearSearchStandard()"><spring:message code="page.standardTabPage.qkcx" /></a>
                            <div style="display: inline-block" class="separator"></div>
                        </li>
                        <li id="operateStandard" style="display: none">
                            <a class="mini-button" style="margin-right: 5px" plain="true"
                               onclick="openStandardEditWindow('','','add')"><spring:message code="page.standardTabPage.xz" /></a>
                            <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                               onclick="removeStandard()"><spring:message code="page.standardTabPage.sc" /></a>
                        </li>
                        <a class="mini-button" id="importId" style="margin-right: 5px;display: none" plain="true"
                           onclick="openImportStandard()"><spring:message code="page.standardTabPage.dr" /></a>
                        <li>
                            <a class="mini-button" style="margin-right: 5px" plain="true" id="exportBtn"
                               onclick="exportStandard()"><spring:message code="page.standardTabPage.dc" /></a>
                        </li>
                        <li>
                            <a id="link" class="mini-button" style="margin-right: 5px;display: none" plain="true"
                               onclick="linkStandard2()"><spring:message code="page.standardTabPage.glbz" /></a>
                        </li>
                        <li>
                            <a class="mini-button" style="margin-right: 5px" plain="true" id="collectBtn"
                               onclick="selectStandard()"><spring:message code="page.standardTabPage.scj" /></a>
                        </li>
                        <li>
                            <a class="mini-button" style="margin-right: 5px;display: none" plain="true" id="doCheckStartFlow"
                               onclick="doCheckStartFlow()"><spring:message code="page.standardTabPage.bzzxzc" /></a>
                        </li>
                        <li>
                            <a class="mini-button" style="margin-right: 5px;display: none" plain="true" id="standardAttachesManagement"
                               onclick="standardAttachesManagement()"><spring:message code="page.standardTabPage.bzfjgl" /></a>
                        </li>
                    </ul>
                    <div id="moreBox">
                        <ul>
                            <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                                  style="width:auto"><spring:message code="page.standardTabPage.tdbzbh" />: </span><input
                                    class="mini-textbox" id="replaceNumber"></li>
                            <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.standardTabPage.btdbzbh" />: </span><input
                                    class="mini-textbox" id="beReplaceNumber"></li>
                            <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.standardTabPage.gkbm" />:</span>
                                <input id="belongDep" class="mini-combobox" style="width:150px;"
                                       textField="belongDepName" valueField="id" emptyText="<spring:message code="page.standardTabPage.qxz" />..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardTabPage.qxz" />..."/>
                            </li>
                            <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                                  style="width:auto"><spring:message code="page.standardTabPage.qcr" />: </span>
                                <input id="publisher" name="publisherId" textname="publisherName"
                                       property="editor" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                                       mainfield="no" single="true"/>
                            </li>
                            <li style="margin-right: 15px">
                                <span class="text" style="width:auto"><spring:message code="page.standardTabPage.fbsj" /> </span>
                                <input id="publishTimeFrom" class="mini-datepicker" format="yyyy-MM-dd"
                                       style="width:120px"/>
                            </li>
                            <li style="margin-right: 15px">
                                <span class="text-to" style="width:auto"><spring:message code="page.standardTabPage.z" /></span>
                                <input id="publishTimeTo" class="mini-datepicker" format="yyyy-MM-dd"
                                       style="width:120px"/>
                            </li>
                            <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.standardTabPage.sfwjybz" />: </span>
                                <input id="whetherIsBorrow" class="mini-combobox" style="width:150px;"
                                       textField="key" valueField="value" emptyText="<spring:message code="page.standardTabPage.qxz" />..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardTabPage.qxz" />..."
                                       data="[ {'key' : '是','value' : 'yes'},{'key' : '否','value' : 'no'}]"
                                />
                            </li>
                            <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.standardTabPage.sffsgys" />: </span>
                                <input id="sendSupplier" class="mini-combobox" style="width:150px;"
                                       textField="key" valueField="value" emptyText="<spring:message code="page.standardTabPage.qxz" />..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardTabPage.qxz" />..."
                                       data="[ {'key' : '是','value' : 'true'},{'key' : '否','value' : 'false'}]"
                                />
                            </li>
                            <li id="banciFilter" style="margin-left:15px;margin-right: 15px;">
                                <span class="text" style="width:auto"><spring:message code="page.standardTabPage.bc" />: </span>
                                <input class="mini-textbox" id="banci" onenter="searchStandard">
                            </li>
                            <li id="yearDoCheckStatusFilter" style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.standardTabPage.ndzczt" />: </span>
                                <input id="yearDoCheckStatus" class="mini-combobox" style="width:150px;"
                                       textField="value" valueField="key" emptyText="<spring:message code="page.standardTabPage.qxz" />..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardTabPage.qxz" />..."
                                       data="[ {'key' : '已完成','value' : '已完成'},{'key' : '进行中','value' : '进行中'},{'key' : '未开始','value' : '未开始'}]"
                                />
                            </li>
                            <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.standardTabPage.zyly" />: </span>
                                <input id="standardField" class="mini-combobox" style="width:150px;"
                                       textField="fieldName" valueField="fieldId" emptyText="<spring:message code="page.standardTabPage.qxz" />..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardTabPage.qxz" />..."/>
                            </li>
                        </ul>
                    </div>
                </form>
            </div>
        </div>
        <div class="mini-fit" style="height: 100%;">
            <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                 idField="id" multiSelect="true" sizeList="[20,50,100,200]" pageSize="20"
                 allowAlternating="true" pagerButtons="#pagerButtons" showColumnsMenu="true"
                 url="${ctxPath}/standardManager/core/standard/queryList.do">
                <div property="columns">
                    <div type="checkcolumn" width="30"></div>
                    <div cellCls="actionIcons" width="120" headerAlign="center" align="center"
                         renderer="operateRenderer" hideable="true"
                         cellStyle="padding:0;"><spring:message code="page.standardTabPage.cz" />
                    </div>
                    <div field="systemName" name="systemName" sortField="systemName" width="100" headerAlign="center" align="center"
                         allowSort="true"><spring:message code="page.standardTabPage.bztx" />
                    </div>
                    <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                         allowSort="true" hideable="true"><spring:message code="page.standardTabPage.bzlb" />
                    </div>
                    <div field="banci" name="banci" sortField="banci" width="40" headerAlign="center"
                         align="center" allowSort="true"><spring:message code="page.standardTabPage.bc" />
                    </div>
                    <div field="standardNumber" sortField="standardNumber" width="130" headerAlign="center"
                         align="center" allowSort="true" hideable="true"><spring:message code="page.standardTabPage.bzbh" />
                    </div>
                    <div field="standardName" sortField="standardName" width="200" headerAlign="center" align="left"
                         allowSort="true" hideable="true"><spring:message code="page.standardTabPage.bzmc" />
                    </div>
                    <div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
                         allowSort="true"><spring:message code="page.standardTabPage.gkbm" />
                    </div>
                    <div field="belongFieldNames" headerAlign="center" width="110" headerAlign="center" align="left"
                         allowSort="false"><spring:message code="page.standardTabPage.zyly" />
                    </div>
                    <div field="cbbh" name="cbbh" headerAlign="center" width="80" headerAlign="center" align="center"
                         allowSort="false"><spring:message code="page.standardTabPage.cbbh" />
                    </div>
                    <div field="yzxcd" name="yzxcd" headerAlign="center" width="120" headerAlign="center" align="center"
                         allowSort="false"><spring:message code="page.standardTabPage.yzxcddh" />
                    </div>
                    <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center"
                         align="center" hideable="true"
                         allowSort="true" renderer="statusRenderer"><spring:message code="page.standardTabPage.zt" />
                    </div>
                    <div field="publisherName" sortField="publisherName" align="center"
                         width="60" headerAlign="center" allowSort="false"><spring:message code="page.standardTabPage.qcr" />
                    </div>
                    <div field="sendSupplier" name="sendSupplier" sortField="sendSupplier" align="center"
                         width="60" headerAlign="center" allowSort="true" renderer="sendSupplierRender"><spring:message code="page.standardTabPage.fsgys" />
                    </div>
                    <div field="creator" name="creator" align="center" width="60" headerAlign="center" allowSort="false"><spring:message code="page.standardTabPage.fbr" />
                    </div>
                    <div field="publishTime" sortField="publishTime" dateFormat="yyyy-MM-dd" align="center"
                         width="80" headerAlign="center" allowSort="true"><spring:message code="page.standardTabPage.fbsj" />
                    </div>
                    <div field="yearDoCheckStatus" name="yearDoCheckStatus" align="center" width="80" headerAlign="center"
                     renderer="doCheckStatusRender"><spring:message code="page.standardTabPage.ndzczt" /></div>
                    <div cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="fileRenderer"
                         cellStyle="padding:0;" hideable="true"><spring:message code="page.standardTabPage.bzqw" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/standardManager/core/standard/exportExcel.do" method="post"
      target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<%--标准导入窗口--%>
<div id="importWindow" title="<spring:message code="page.standardTabPage.bzdrck" />" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importStandard()"><spring:message code="page.standardTabPage.dr" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()"><spring:message code="page.standardTabPage.gb" /></a>
    </div>

    <div class="mini-fit" style="font-size: 14px;margin-top: 20px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%"><spring:message code="page.standardTabPage.scmb" />：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="standardImportTemplate()"><spring:message code="page.standardTabPage.bzpl" />.xls</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%"><spring:message code="page.standardTabPage.cz" />：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile"><spring:message code="page.standardTabPage.xzwj" /></a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile"><spring:message code="page.standardTabPage.qc" /></a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="collectStandardWindow" title="<spring:message code="page.standardTabPage.bzscj" />" class="mini-window" style="width:1050px;height:650px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input id="parentInputScene" style="display: none"/>
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777"><spring:message code="page.standardTabPage.bzbh" />: </span>
        <input class="mini-textbox" width="130" id="filterStandardNumberId" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777"><spring:message code="page.standardTabPage.bzmc" />: </span>
        <input class="mini-textbox" width="130" id="filterStandardNameId" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchStandard1()"><spring:message code="page.standardTabPage.cx" /></a>
    </div>
    <div class="mini-fit">
        <div id="collectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" autoload="true"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/standardManager/core/standard/queryList.do?belongId=${currentUserId}&systemCategoryId=${tabName}"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div name="action" cellCls="actionIcons" width="40" headerAlign="center" align="center" renderer="onMessageActionRenderer"
                     cellStyle="padding:0;"><spring:message code="page.standardTabPage.cz" />
                </div>
                <div field="standardNumber" sortField="standardNumber" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true"><spring:message code="page.standardTabPage.bh" />
                </div>
                <div field="standardName" sortField="standardName" width="180" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.standardTabPage.mc" />
                </div>
                <div field="standardStatus" sortField="standardStatus" width="40" headerAlign="center" align="center"
                     allowSort="true" renderer="statusRenderer"><spring:message code="page.standardTabPage.zt" />
                </div>
                <div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="fileRenderer"
                     cellStyle="padding:0;" hideable="true"><spring:message code="page.standardTabPage.bzqw" />
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardTabPage.gbscj" />" onclick="selectStandardHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var tabName = "${tabName}";
    //当前用户的职级
    var currentUserZJ =${currentUserZJ};
    var currentUserId = "${currentUserId}";
    var currentUserNo= "${currentUserNo}";
    var currentUserRoles =${currentUserRoles};
    var currentUserSubManager =${subManagerObj};
    var standardNumberFilter = "${standardNumber}";
    var standardNameFilter = "${standardName}";
    var standardCategoryFilter = "${standardCategory}";
    var publishTimeFromFilter = "${publishTimeFrom}";
    var publishTimeToFilter = "${publishTimeTo}";
    var standardStatusFilter = "${standardStatus}";
    var fieldId = "${fieldId}";
    var isGlNetwork = "${isGlNetwork}";
    //马天宇:type是某个业务的id
    <%--var type="${type}";--%>
    //@lwgkiller改:将type改为业务标志，然后再传一个businessId代表业务id。
    var type = "${type}";
    var businessId = "${businessId}";
    //@lwgkiller改-end
    var collectStandardWindow = mini.get("collectStandardWindow");
    var collectListGrid = mini.get("collectListGrid");

    var isPointManager = whetherIsPointStandardManager(tabName, currentUserRoles);
    var isSubManager = whetherIsSubManager(tabName, currentUserSubManager);
    var systemTree = mini.get("systemTree");
    var standardListGrid = mini.get("standardListGrid");
    var importWindow = mini.get("importWindow");
    var currentTime = "${currentTime}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    //馬輝
    var standardReverseId = "${dtglid}"
    //马天宇
    var isHw = ${isHw}
    //if (type != "") {
    //    $("#link").show();
    //}
    //@lwgkiller改
    if (type != "" && businessId != "") {
        $("#link").show();
    }
    //@lwgkiller改-end


    //操作栏
    standardListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });

    collectListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });

    function operateRenderer(e) {
        var record = e.record;
        var standardId = record.id;
        var standardStatus = record.standardStatus;
        var borrowFromId = $.trim(record.borrowFromId);
        if (!borrowFromId && (isPointManager || isSubManager)) {
            var s = '<span  title=' + standardTabPage_xq + ' onclick="openStandardEditWindow(\'' + standardId + '\',\'' + record.systemId + '\',\'detail\')">' + standardTabPage_xq + '</span>';
            s += '<span  title=' + standardTabPage_bj + ' onclick="openStandardEditWindow(\'' + standardId + '\',\'' + record.systemId + '\',\'edit\')">' + standardTabPage_bj + '</span>';
            s += '<span title=' + standardTabPage_sc + ' onclick="removeStandard(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + standardTabPage_sc + '</span>';
            s += '<span title=' + standardTabPage_fzqy + ' onclick="stopOldPublishNew(\'' + standardId + '\',\'' + record.systemId + '\')">' + standardTabPage_fzqy + '</span>';
            s += '<span title=' + standardTabPage_bzjy + ' onclick="borrow(\'' + standardId + '\',\'' + record.systemId + '\')">' + standardTabPage_bzjy + '</span>';
            s += '<span title=' + standardTabPage_scang + ' onclick="collect(\'' + currentUserId + '\')">' + standardTabPage_scang + '</span>';
            s += '<span id = "jp2dtgl" title=' + standardTabPage_lcxq + ' onclick="jumpToDynamic(\'' + standardId + '\')">' + standardTabPage_lcxq + '</span>';
            if (tabName == "GL") {
                s += '<span id = "fbgl" title=' + standardTabPage_fbgl + ' onclick="attachFileManage(\'' + standardId +'\',\''+tabName + '\')">' + standardTabPage_fbgl + '</span>';
            }
            return s;
        } else {
            var s = '<span  title=' + standardTabPage_xq + ' onclick="openStandardEditWindow(\'' + standardId + '\',\'' + record.systemId + '\',\'detail\')">' + standardTabPage_xq + '</span>';
            s += '<span title=' + standardTabPage_scang + ' onclick="collect(\'' + currentUserId + '\')">' + standardTabPage_scang + '</span>';
            s += '<span id = "jp2dtgl" title=' + standardTabPage_lcxq + ' onclick="jumpToDynamic(\'' + standardId + '\')">' + standardTabPage_lcxq + '</span>';
            if (tabName == "GL") {
                s += '<span id = "fbgl" title=' + standardTabPage_fbgl + ' onclick="attachFileManage(\'' + standardId +'\',\''+tabName + '\')">' + standardTabPage_fbgl + '</span>';
            }
            return s;
        }
    }

    function fileRenderer(e) {
        var record = e.record;
        var standardId = record.id;
        var existFile = record.existFile;
        var standardName = record.standardName;
        var standardNumber = record.standardNumber;
        var status = record.standardStatus;
        var categoryName = record.categoryName;
        var systemCategoryId = record.systemCategoryId;

        if (existFile) {//本地不存在
            var s = '<span title=' + standardTabPage_yl + '  onclick="previewStandard(\'' + standardId + '\',\'' + standardName + '\',\'' + standardNumber + '\',\'' + categoryName + '\',\'' + status + '\',\'' + coverContent + '\',\'' + systemCategoryId + '\')">' + standardTabPage_yl + '</span>';
            if ((isGlNetwork == "true" && tabName == 'JS')||isHw) {
                s = '<span title=' + standardTabPage_yl + '  style="color: silver">' + standardTabPage_yl + '</span>';
            }
            s += '<span title=' + standardTabPage_xz + ' onclick="downloadStandard(\'' + standardId + '\',\'' + standardName + '\',\'' + standardNumber + '\',\'' + categoryName + '\',\'' + status + '\')">' + standardTabPage_xz + '</span>';
            return s;
        } else {
            var s = '<span title=' + standardTabPage_yl + '  style="color: silver">' + standardTabPage_yl + '</span>';
            s += '<span title=' + standardTabPage_xz + ' style="color: silver">' + standardTabPage_xz + '</span>';
            return s;
        }
    }

    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function doCheckStatusRender(e) {
        var record = e.record;
        var status = record.yearDoCheckStatus;
        var categoryName= record.categoryName;
        if(!status || status =='') {
            return "";
        }
        var arr = [{'key': '已完成', 'value': '已完成', 'css': 'blue'},
            {'key': '进行中', 'value': '进行中', 'css': 'green'},
            {'key': '未开始', 'value': '未开始', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function sendSupplierRender(e) {
        var record = e.record;
        var sendSupplier = record.sendSupplier;
        if ("true" == sendSupplier) {
            return "是";
        } else {
            return "否";
        }
    }

    systemTree.on("nodeselect", function (e) {
        searchStandard();
    });

    function selectStandard(inputScene) {
        $("#parentInputScene").val(inputScene);
        collectStandardWindow.show();
        searchStandard1();
    }
    //查询标准
    function searchStandard1() {
        var queryParam = [];
        //其他筛选条件
        var standardNumber = $.trim(mini.get("filterStandardNumberId").getValue());
        if (standardNumber) {
            queryParam.push({name: "standardNumber", value: standardNumber});
        }
        var standardName = $.trim(mini.get("filterStandardNameId").getValue());
        if (standardName) {
            queryParam.push({name: "standardName", value: standardName});
        }
        var userId = currentUserId;
        if (userId) {
            queryParam.push({name: "belongId", value: userId});
        }
        var tabName = tabName;
        if (tabName) {
            queryParam.push({name: "systemCategoryId", value: standardName});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = collectListGrid.getPageIndex();
        data.pageSize = collectListGrid.getPageSize();
        data.sortField = collectListGrid.getSortField();
        data.sortOrder = collectListGrid.getSortOrder();
        //查询
        collectListGrid.load(data);
    }


    function selectStandardHide() {
        collectStandardWindow.hide();
        mini.get("filterStandardNumberId").setValue('');
        mini.get("filterStandardNameId").setValue('');
    }

    function collect(userId) {
        var rows = standardListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert(standardTabPage_qzsxz);
            return;
        }
        var ids = [];
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            ids.push(r.id);
        }
        _SubmitJson({
            url: jsUseCtxPath + "/standardManager/core/standard/saveCollect.do?userId=" + userId,
            method: 'POST',
            showMsg: false,
            data: {ids: ids.join(',')},
            success: function (data) {
                if (data) {
                    mini.alert(data.message);
                    searchStandard();
                }
            }
        });
    }
    function onMessageActionRenderer(e) {
        var record = e.record;
        var standardId = record.standardId;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
        s += '<span  title=' + standardTabPage_yc + ' onclick="removeCollect(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + standardTabPage_yc + '</span>';
        return s;
    }

    function removeCollect(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = collectListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(standardTabPage_qzsxz);
            return;
        }
        mini.confirm(standardTabPage_qdsc, standardTabPage_ts, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/standardManager/core/standard/deleteCollect.do",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchStandard1();
                        }
                    }
                });
            }
        });
    }

    function showLoading() {
        $("#loading").css('display','');
        $("#content").css('display','none');
    }

    function hideLoading() {
        $("#loading").css('display','none');
        $("#content").css('display','');
    }

    function attachFileManage(standardId,standardType) {
        var canOperateFile = true;
        mini.open({
            title: standardEdit_name13,
            url: jsUseCtxPath + "/standardManager/core/standardFileInfos/attachFileListWindow.do?standardId=" + standardId + "&standardType="+standardType+ "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent,
            width: 1200,
            height: 600,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
            }
        });
    }
</script>
</body>
</html>
