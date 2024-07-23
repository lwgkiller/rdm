<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准资源管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/standardManager/fzsjbzTabPage.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-left:15px;margin-right: 15px"><span class="text" style="width:auto">编号: </span><input
                        class="mini-textbox" id="standardNumber" onenter="searchStandard"></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">名称: </span><input
                        class="mini-textbox" id="standardName" onenter="searchStandard"></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">类别: </span>
                    <input id="category" class="mini-combobox" style="width:150px;"
                           textField="categoryName" valueField="id" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                </li>
                <%--<li style="margin-right: 15px"><span class="text" style="width:auto">专业领域: </span>
                    <input id="standardField" class="mini-combobox" style="width:150px;"
                           textField="fieldName" valueField="fieldId" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                </li>--%>
                <li class="liBtn">
								<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
									<em>展开</em>
									<i class="unfoldIcon"></i>
								</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="searchStandard()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="clearSearchStandard()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                </li>
                <li>
                    <a class="mini-button" style="margin-right: 5px" plain="true" id="exportBtn"
                       onclick="exportStandard()">导出</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                          style="width:auto">替代标准编号: </span><input
                            class="mini-textbox" id="replaceNumber"></li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">被替代标准编号: </span><input
                            class="mini-textbox" id="beReplaceNumber"></li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">归口部门:</span>
                        <input id="belongDep" class="mini-combobox" style="width:150px;"
                               textField="belongDepName" valueField="id" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                    </li>
                    <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                          style="width:auto">起草人: </span>
                        <input id="publisher" name="publisherId" textname="publisherName"
                               property="editor" class="mini-user rxc" plugins="mini-user"  style="width:98%;height:34px;" allowinput="false"  mainfield="no"  single="true" />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">发布时间 </span>
                        <input id="publishTimeFrom" class="mini-datepicker" format="yyyy-MM-dd"
                               style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至</span>
                        <input id="publishTimeTo" class="mini-datepicker" format="yyyy-MM-dd"
                               style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">是否为借用标准: </span>
                        <input id="whetherIsBorrow" class="mini-combobox" style="width:150px;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '是','value' : 'yes'},{'key' : '否','value' : 'no'}]"
                        />
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">是否发送供应商: </span>
                        <input id="sendSupplier" class="mini-combobox" style="width:150px;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '是','value' : 'true'},{'key' : '否','value' : 'false'}]"
                        />
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">状态: </span>
                        <input id="status" class="mini-combobox" style="width:150px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               value="enable"
                               data="[ {'key' : 'enable','value' : '有效'},{'key' : 'disable','value' : '已废止'},{'key' : 'draft','value' : '草稿'}]"
                        />
                    </li>
                    <li id="banciFilter" style="margin-left:15px;margin-right: 15px;">
                        <span class="text" style="width:auto">版次: </span>
                        <input class="mini-textbox" id="banci" onenter="searchStandard"></li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         idField="id" multiSelect="true" sizeList="[20,50,100,200]" pageSize="20"
         allowAlternating="true" pagerButtons="#pagerButtons" showColumnsMenu="true"
         url="${ctxPath}/standardManager/core/fzbz/queryFzbzList.do">
        <div property="columns">
            <div type="checkcolumn" width="30"></div>
            <div cellCls="actionIcons" width="110" headerAlign="center" align="center"
                 renderer="operateRenderer" hideable="true"
                 cellStyle="padding:0;">操作
            </div>
            <div field="systemName" name="systemName" sortField="systemName" width="100" headerAlign="center" align="center"
                 allowSort="true">标准体系
            </div>
            <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                 allowSort="true" hideable="true">标准类别
            </div>
            <div field="banci" name="banci" sortField="banci" width="40" headerAlign="center"
                 align="center" allowSort="true">版次
            </div>
            <div field="standardNumber" sortField="standardNumber" width="130" headerAlign="center"
                 align="center" allowSort="true" hideable="true">标准编号
            </div>
            <div field="standardName" sortField="standardName" width="190" headerAlign="center" align="left"
                 allowSort="true" hideable="true">标准名称
            </div>
            <div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
                 allowSort="true">归口部门
            </div>
            <%--                    <div field="replaceNumber" width="120" headerAlign="center" align="left" allowSort="false">替代标准
                                </div>
                                <div field="beReplaceNumber" headerAlign="center" width="120" headerAlign="center" align="left"
                                     allowSort="false">被替代标准
                                </div>--%>
            <div field="belongFieldNames" headerAlign="center" width="120" headerAlign="center" align="left"
                 allowSort="false">专业领域
            </div>
            <div field="cbbh" name="cbbh" headerAlign="center" width="80" headerAlign="center" align="center"
                 allowSort="false">采标编号
            </div>
            <div field="yzxcd" name="yzxcd" headerAlign="center" width="120" headerAlign="center" align="center"
                 allowSort="false">一致性程度代号
            </div>
            <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center"
                 align="center" hideable="true"
                 allowSort="true" renderer="statusRenderer">状态
            </div>
            <div field="publisherName" sortField="publisherName" align="center"
                 width="60" headerAlign="center" allowSort="false">起草人
            </div>
            <div field="sendSupplier" name="sendSupplier" sortField="sendSupplier" align="center"
                 width="60" headerAlign="center" allowSort="true" renderer="sendSupplierRender">发送供应商
            </div>
            <div field="creator" name="creator" align="center" width="60" headerAlign="center" allowSort="false">发布人
            </div>
            <div field="publishTime" sortField="publishTime" dateFormat="yyyy-MM-dd" align="center"
                 width="100" headerAlign="center" allowSort="true">发布时间
            </div>
            <div cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;" hideable="true">标准全文
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



<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var tabName = "${tabName}";
    //当前用户的职级
    var currentUserZJ =${currentUserZJ};
    var currentUserId = "${currentUserId}";
    var currentUserRoles =${currentUserRoles};
    var currentUserSubManager =${subManagerObj};
    var standardNumberFilter = "${standardNumber}";
    var standardNameFilter = "${standardName}";
    var standardCategoryFilter = "${standardCategory}";
    var publishTimeFromFilter = "${publishTimeFrom}";
    var publishTimeToFilter = "${publishTimeTo}";
    var standardStatusFilter = "${standardStatus}";
    var isGlNetwork="${isGlNetwork}";

    var isPointManager = whetherIsPointStandardManager(tabName, currentUserRoles);
    var isSubManager = whetherIsSubManager(tabName, currentUserSubManager);
    var standardListGrid = mini.get("standardListGrid");
    var currentTime = "${currentTime}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    //操作栏
    standardListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });

    function operateRenderer(e) {
        var record = e.record;
        var standardId = record.id;
        var s = '<span  title="详情" onclick="openStandardEditWindow(\'' + standardId + '\',\'' + record.systemId + '\',\'detail\')">详情</span>';
        return s;
    }

    function fileRenderer(e) {
        var record = e.record;
        var standardId = record.id;
        var existFile = record.existFile;
        var standardName = record.standardName;
        var standardNumber = record.standardNumber;
        var status = record.standardStatus;
        var categoryName=record.categoryName;
        var systemCategoryId = record.systemCategoryId;

        if (existFile) {
            var s = '<span title="预览"  onclick="previewStandard(\'' + standardId + '\',\'' + standardName + '\',\'' + standardNumber+ '\',\'' + categoryName + '\',\'' + status + '\',\'' + coverContent + '\',\'' + systemCategoryId + '\')">预览</span>';
            /*if(isGlNetwork=="true" && tabName=='JS') {
                s = '<span title="预览"  style="color: silver">预览</span>';
            }*/
            s += '<span title="下载" onclick="downloadStandard(\'' + standardId + '\',\'' + standardName + '\',\'' + standardNumber + '\',\'' + categoryName + '\',\'' + status + '\')">下载</span>';
            return s;
        } else {
            var s = '<span title="预览"  style="color: silver">预览</span>';
            s += '<span title="下载" style="color: silver">下载</span>';
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

    function sendSupplierRender(e){
        var record = e.record;
        var sendSupplier = record.sendSupplier;
        if("true"==sendSupplier) {
            return "是";
        } else {
            return "否";
        }
    }

</script>
</body>
</html>
