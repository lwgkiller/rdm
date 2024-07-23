<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-left: 10px">
                    <%--<a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormDiy()">清空查询</a>--%>
                    <%--<f:a alias="PartsPortrait-searchFrm" onclick="searchFrmDiy()" showNoRight="false" style="margin-right: 5px">查询</f:a>--%>
                    <f:a alias="patentInterpretationTB-addParentBusiness" onclick="addParentBusiness()" showNoRight="false"
                         style="margin-right: 5px"> 新增一级记录</f:a>
                    <f:a alias="patentInterpretationTB-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px">新增子记录</f:a>
                    <f:a alias="patentInterpretationTB-editBusiness" onclick="editBusiness()" showNoRight="false" style="margin-right: 5px">编辑</f:a>
                    <f:a alias="patentInterpretationTB-removeBusiness" onclick="removeBusiness()" showNoRight="false"
                         style="margin-right: 5px">删除</f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-treegrid" style="width: 100%; height: 100%;" showTreeIcon="true"
         treeColumn="description" idField="id" parentField="parentId" resultAsTree="false" virtualScroll="true"
         url="${ctxPath}/zhgl/core/patentInterpretation/technologyBranch/dataListQuery.do"
         expandOnLoad="false" expandOnDblClick="false">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="20" headerAlign="center" align="center">序号</div>
            <div name="description" field="description" width="180" headerAlign="center" align="center">名称</div>
            <div name="liableUser" field="liableUser" width="180" headerAlign="center" align="center">责任人</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentId = ""

    businessListGrid.on("rowdblclick", function (e) {
        var record = e.record;
        var businessId = record.id;
        detailBusiness(businessId);
    });

    businessListGrid.on("load", function (e) {
        gotoCurrentById(currentId);
    })
    //..
    function removeBusiness() {
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/patentInterpretation/technologyBranch/deleteData.do",
                    method: 'POST',
                    data: {id: row.id},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            businessListGrid.reload();
                        }
                    }
                });
            }
        });
    }
    //..
    function addParentBusiness() {
        var url = jsUseCtxPath + "/zhgl/core/patentInterpretation/technologyBranch" +
            "/editPage.do?parentId=&action=add";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function addBusiness() {
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        currentId = row.id;
        var url = jsUseCtxPath + "/zhgl/core/patentInterpretation/technologyBranch" +
            "/editPage.do?parentId=" + currentId + "&action=add";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function editBusiness() {
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        currentId = row.id;
        var url = jsUseCtxPath + "/zhgl/core/patentInterpretation/technologyBranch" +
            "/editPage.do?businessId=" + currentId + "&action=edit";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/zhgl/core/patentInterpretation/technologyBranch" +
            "/editPage.do?businessId=" + businessId + "&action=detail";
        var winObj = window.open(url, '');
    }

    function gotoCurrentById(currentId) {
        var key = currentId;
        if (key == "") {

        } else {
            //查找到节点
            var nodes = businessListGrid.findNodes(function (node) {
                var text = node.id ? node.id : "";
                if (text == key) {
                    return true;
                }
            });
            //展开所有找到的节点
            debugger;
            for (var i = 0, l = nodes.length; i < l; i++) {
                var node = nodes[i];
                //第一层节点
                if (node.children && node.parentId == "") {
                    businessListGrid.expandNode(node);
                }//其他节点
                else {
                    businessListGrid.expandPath(node);
                }
            }
            //第一个节点选中并滚动到视图
            var firstNode = nodes[0];
            if (firstNode) {
                businessListGrid.selectNode(firstNode);
                businessListGrid.scrollIntoView(firstNode);
            }
        }
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>