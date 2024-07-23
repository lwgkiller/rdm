<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>NPI文件资源管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgNpi/npiFileList.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        .mini-grid-cell-inner,
        .mini-grid-headerCell-inner {
            line-height: 30px;
            padding: 0;
        }
    </style>
</head>
<body>
<div class="mini-splitter" style="width:100%;height:100%;" id="content">
    <div size="27%" showCollapseButton="true">
        <div id="systemSearch" class="mini-toolbar" style="padding:2px;border-top:0;border-left:0;border-right:0;">
            <span style="font-size: 14px;color: #777">名称: </span>
            <input class="mini-textbox" width="200" id="filterNameId" onenter="filterSystemTree()"/>
            <a class="mini-button"plain="true" onclick="filterSystemTree()">搜索</a>
            <a class="mini-button"  onclick="expandFileAll()">展开</a>
        </div>
        <hr color="#ddd"/>
        <div class="mini-fit">
            <ul id="systemTree" class="mini-tree"
                url="${ctxPath}/xcmgNpi/core/npiFile/treeQuery.do"
                style="width:100%;height:98%;padding:5px;" ondrawnode="onDrawNode"
                showTreeIcon="true" textField="systemName" expandOnLoad="1" idField="id" parentField="parentId"
                resultAsTree="false">
            </ul>
        </div>
    </div>
    <div showCollapseButton="true">
        <div class="mini-toolbar">
            <div class="searchBox">
                <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                    <ul>
                        <li style="margin-left:15px;margin-right: 10px"><span class="text" style="width:auto">流程阶段: </span>
                            <input id="stageId" name="stageDicId" class="mini-combobox" style="width:220px;max-width:400px"
                                   textField="name" valueField="dicId" emptyText="请选择..."
                                   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                            />
                        </li>
                        <li style="margin-right: 10px"><span class="text" style="width:auto">活动名称: </span>
                            <input class="mini-textbox" id="fileName"  name="fileName"/>
                        </li>

                        <li class="liBtn">
								<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
									<em>展开</em>
									<i class="unfoldIcon"></i>
								</span>
                        </li>
                        <li style="margin-left: 10px">
                            <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchNpiFiles()">查询</a>
                            <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearSearchNpiFiles()">清空查询</a>
                        </li>
                        <li id="opBtns" style="display: none">
                            <div style="display: inline-block" class="separator"></div>
                            <a class="mini-button" style="margin-right: 5px" plain="true" onclick="openEditWindow('','add')">新增</a>
                            <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeNpiFile()">删除</a>
                            <a class="mini-button" style="margin-right: 5px" plain="true" id="exportBtn" onclick="exportNpiFile()">导出</a>
                        </li>
                    </ul>
                    <div id="moreBox">
                    </div>
                </form>
            </div>
        </div>
        <div class="mini-fit" style="height: 100%;">
            <div id="npiFileListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                 idField="id" multiSelect="true" sizeList="[20,50,100,200]" pageSize="20"
                 allowAlternating="true" pagerButtons="#pagerButtons" showColumnsMenu="true" allowCellWrap="true"
                 url="${ctxPath}/xcmgNpi/core/npiFile/queryList.do">
                <div property="columns">
                    <div type="checkcolumn" width="30"></div>
                    <div cellCls="actionIcons" width="50" headerAlign="center" align="center"
                         renderer="operateRenderer" hideable="true"
                         cellStyle="padding:0;">操作</div>
                    <div field="fileName" name="fileName" width="120" headerAlign="center"
                         align="center" allowSort="false" renderer="fileNameRender">活动名称</div>
                    <div field="stageName" sortField="stageName" width="80" headerAlign="center" align="center"
                         allowSort="true" renderer="stageNameRender">流程阶段</div>
                    <div field="systemName" name="systemName" sortField="systemName" width="150" headerAlign="center" align="center"
                    allowSort="true" renderer="systemNameRender">活动类型</div>
                    <div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="fileRenderer"
                         cellStyle="padding:0;" hideable="true">附件</div>
                </div>
            </div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/xcmgNpi/core/npiFile/exportExcel.do" method="post"
      target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var systemTree = mini.get("systemTree");
    var npiFileListGrid = mini.get("npiFileListGrid");
    var isOperator = "${isOperator}";
    var currentTime = "${currentTime}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;

    //操作栏
    npiFileListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });

    function operateRenderer(e) {
        var s='';
        if (isOperator=="true") {
            var record = e.record;
            var recordId = record.id;
            s='<span title="编辑" onclick="openEditWindow(\'' + recordId + '\',\'edit\')">编辑</span>';
            s+='<span title="删除" onclick="removeNpiFile(\'' + recordId + '\')">删除</span>';
        } else {
            s='<span  title="编辑" style="color: silver" >编辑</span>';
            s+='<span  title="删除" style="color: silver" >删除</span>';
        }
        return s;
    }
;
    function fileRenderer(e) {
        var record = e.record;
        var cellHtml = returnNpiFilePreview(record.fileObjName, record.id, coverContent);
        if (isOperator=="true") {
            cellHtml += '<span title="下载" style="color:#409EFF;cursor: pointer;" onclick="downNpiFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">下载</span>';
        }
        return cellHtml;
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

    function fileNameRender(e) {
        var record = e.record;
        var fileName = record.fileName;
        var fileNameEn = record.fileNameEn;
        var result = fileName;
        if (fileNameEn) {
            result+="<br>【"+fileNameEn+"】";
        }
        return result;
    }

    function stageNameRender(e) {
        var record = e.record;
        var stageName = record.stageName;
        var stageNameEn = record.stageNameEn;
        var result = stageName;
        if (stageNameEn) {
            result+="<br>【"+stageNameEn+"】";
        }
        return result;
    }

    function systemNameRender(e) {
        var record = e.record;
        var systemName = record.systemName;
        var systemNameEn = record.systemNameEn;
        var result = systemName;
        if (systemNameEn) {
            result+="<br>【"+systemNameEn+"】";
        }
        return result;
    }

    systemTree.on("nodeselect", function (e) {
        searchNpiFiles();
    });

    function onDrawNode(e) {
        var node = e.node;
        e.nodeHtml = node.systemName;
        if (node.systemNameEn) {
            e.nodeHtml+="【"+node.systemNameEn+"】";
        }
    }

    function expandFileAll() {
        systemTree.expandAll();
    }

</script>
</body>
</html>
