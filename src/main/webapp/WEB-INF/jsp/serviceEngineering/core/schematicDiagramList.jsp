<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>原理图归档</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <input class="mini-hidden" id="isComplex" name="isComplex"/>
            <ul>
                <li style="margin-left:15px;margin-right: 15px">
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">原理图号：</span>
                    <input class="mini-textbox" id="diagramCode" name="diagramCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">原理图类型：</span>
                    <input class="mini-textbox" id="diagramType" name="diagramType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">原理图负责人：</span>
                    <input class="mini-textbox" id="repUserName" name="repUserName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">手册编号：</span>
                    <input class="mini-textbox" id="manualCode" name="manualCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">语言：</span>
                    <input class="mini-textbox" id="manualLanguage" name="manualLanguage"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">版本：</span>
                    <input class="mini-textbox" id="manualVersion" name="manualVersion"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">文件名称：</span>
                    <input class="mini-textbox" id="manualDescription" name="manualDescription"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">归档人：</span>
                    <input class="mini-textbox" id="keyUser" name="keyUser"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">文件状态：</span>
                    <input class="mini-textbox" id="manualStatus" name="manualStatus"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号: </span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品物料编码: </span>
                    <input class="mini-textbox" id="materialCodeProduct" name="materialCodeProduct"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品主管: </span>
                    <input class="mini-textbox" id="productManager" name="productManager"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="schematicDiagram-searchFrm" onclick="searchFrmThis()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormThis()">清空查询</a>
                    <f:a alias="schematicDiagram-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px">新增</f:a>
                    <f:a alias="schematicDiagram-editBusiness" onclick="editBusiness()" showNoRight="false" style="margin-right: 5px">编辑</f:a>
                    <f:a alias="schematicDiagram-removeBusiness" onclick="deleteBusiness()" showNoRight="false"
                         style="margin-right: 5px">删除</f:a>
                    <f:a alias="schematicDiagram-releaseBusiness" onclick="releaseBusiness()" showNoRight="false"
                         style="margin-right: 5px">发布</f:a>
                    <f:a alias="schematicDiagram-copyBusiness" onclick="copyBusiness()" showNoRight="false" style="margin-right: 5px">升版&复制</f:a>
                    <f:a alias="schematicDiagram-exportBusinessThis" onclick="exportBusinessThis()" showNoRight="false"
                         style="margin-right: 5px">导出</f:a>
                    <f:a alias="schematicDiagram-previewBusiness" onclick="previewBusiness2()" showNoRight="false"
                         style="margin-right: 5px">预览</f:a>
                    <f:a alias="schematicDiagram-downloadBusiness" onclick="downloadBusiness2()" showNoRight="false"
                         style="margin-right: 5px">下载</f:a>
                    <f:a alias="schematicDiagram-callBackExiu" onclick="callBackExiu()" showNoRight="false" style="margin-right: 5px">传e修</f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
         allowResize="false" allowCellWrap="false" showCellTip="true" idField="id"
         allowCellEdit="true" allowCellSelect="true" multiSelect="false" showColumnsMenu="false"
         sizeList="[50,100,200,500,1000,5000]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         allowSortColumn="true" allowUnselect="false" autoLoad="false"
         url="${ctxPath}/serviceEngineering/core/schematicDiagram/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true">原理图物料编码</div>
            <div field="diagramCode" width="120" headerAlign="center" align="center" allowSort="true">原理图号</div>
            <div field="diagramType" width="120" headerAlign="center" align="center" allowSort="true">原理图类型</div>
            <div field="repUserName" width="120" headerAlign="center" align="center" allowSort="true">原理图负责人</div>
            <div field="manualCode" width="120" headerAlign="center" align="center" allowSort="true">手册编号</div>
            <div field="manualDescription" width="300" headerAlign="center" align="center" renderer="render" allowSort="true">文件名称</div>
            <div field="manualLanguage" width="80" headerAlign="center" align="center" allowSort="true">语言</div>
            <div field="manualVersion" width="80" headerAlign="center" align="center" allowSort="true">版本</div>
            <div field="keyUser" displayField="keyUser" width="80" headerAlign="center" align="center" allowSort="true">归档人</div>
            <div field="publishTime" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" allowSort="true">归档时间</div>
            <div field="manualStatus" width="120" headerAlign="center" align="center" allowSort="true">文件状态</div>
            <div field="note" width="120" headerAlign="center" align="center" allowSort="true">备注</div>
            <div field="callBackResult" width="120" headerAlign="center" align="center" allowSort="true">是否传e修</div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/schematicDiagram/exportList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<%----%>
<div id="remarkWindow" title="关联产品记录" class="mini-window" style="width:950px;height:400px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div id="remarkListGrid" class="mini-datagrid" style="width: 100%; height: 98%" allowResize="false" allowCellWrap="true"
             showCellTip="true" idField="id" showPager="false" showColumnsMenu="false" allowcelledit="false"
             allowAlternating="true">
            <div property="columns">
                <div field="salesModel_item" width="100" headerAlign="center" align="center">销售型号</div>
                <div field="designModel_item" width="100" headerAlign="center" align="center">设计型号</div>
                <div field="materialCode_item" width="100" headerAlign="center" align="center">物料编码</div>
                <div field="cpzgId_item_name" width="100" headerAlign="center" align="center">产品主管</div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentTime = "${currentTime}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var schematicDiagramAdmin = "${schematicDiagramAdmin}";
    var remarkWindow = mini.get("remarkWindow");
    var remarkListGrid = mini.get("remarkListGrid");
    //..
    $(function () {
        searchFrmThis();
    });
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function fileRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var existFile = record.existFile;
        var s = '<span  title="明细" onclick="detailBusiness(\'' + businessId + '\')">明细</span>';
        s += '<span  title="查看"  onclick="remarkCheck()">关联产品</span>';
        return s;
    }
    //..
    function previewBusiness(id, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/schematicDiagram/preview.do?id=" + id;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) +
            "&file=" + encodeURIComponent(previewUrl));
    }
    function previewBusiness2() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var id = row.id;
        var existFile = row.existFile;
        if (existFile) {
            previewBusiness(id, coverContent);
        } else {
            mini.alert("当前记录没有文件");
            return;
        }
    }
    //..
    function downloadBusiness(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/schematicDiagram/download.do");
        var idAttr = $("<input>");
        idAttr.attr("type", "hidden");
        idAttr.attr("name", "id");
        idAttr.attr("value", id);
        var descriptionAttr = $("<input>");
        descriptionAttr.attr("type", "hidden");
        descriptionAttr.attr("name", "description");
        descriptionAttr.attr("value", description);
        $("body").append(form);
        form.append(idAttr);
        form.append(descriptionAttr);
        form.submit();
        form.remove();
    }
    function downloadBusiness2() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var id = row.id;
        var existFile = row.existFile;
        if (existFile) {
            downloadBusiness(id, row.manualDescription);
        } else {
            mini.alert("当前记录没有文件");
            return;
        }
    }
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/serviceEngineering/core/schematicDiagram/editPage.do?id=&action=add";
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
        if (row == null) {
            mini.alert("请至少选中一条记录");
            return;
        }
        else if (row.manualStatus != "编辑中" && currentUserId != '1' && currentUserNo != schematicDiagramAdmin) {
            mini.alert("只有 编辑中 状态的数据能编辑");
            return;
        }
        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/schematicDiagram/editPage.do?id=" + id + '&action=edit';
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
    function deleteBusiness() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert("请至少选中一条记录");
            return;
        } else if (row.manualStatus != "编辑中" && currentUserId != '1' && currentUserNo != decorationManualAdmin) {
            mini.alert("只有 编辑中 状态的数据能删除");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/schematicDiagram/deleteBusiness.do",
                    type: 'POST',
                    data: mini.encode({id: id}),
                    contentType: 'application/json',
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
    //..
    function releaseBusiness() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert("请至少选中一条记录");
            return;
        } else if (row.manualStatus != "编辑中") {
            mini.alert("只有 编辑中 状态的数据能发布");
            return;
        } else if (row.manualDescription == "" || !row.manualDescription) {
            mini.alert("没有上传文件，不能发布");
            return;
        } else if (row.manualVersion == "" || !row.manualVersion) {
            mini.alert("必须有相应版本才能发布");
            return;
        }
        mini.confirm("确定发布选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/schematicDiagram/releaseBusiness.do",
                    type: 'POST',
                    data: mini.encode({id: id}),
                    contentType: 'application/json',
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
    //..
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/schematicDiagram/editPage.do?id=" + businessId + '&action=detail';
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
    function copyBusiness() {
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert("请至少选中一条记录");
            return;
        } else if (row.manualStatus != "可转出") {
            mini.alert("只有 可转出 状态的数据能升版");
            return;
        }
        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/schematicDiagram/editPage.do?id=" + id + '&action=copy';
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
    function exportBusiness() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
    //由于最后三个条件出现后，会调用不同的查询，来避免join的重复结果干扰主查询的结果
    //所以，加了一个隐藏参数isComplex，有最后三个条件就往后端传true，否则就往后端
    //传false，根据这个值来判断调用哪一个查询
    function searchFrmThis() {
        if (false) {
            mini.get("isComplex").setValue("true");
        } else {
            mini.get("isComplex").setValue("false");
        }
        searchFrm();
    }
    //由于最后三个条件出现后，会调用不同的查询，来避免join的重复结果干扰主查询的结果
    //所以，加了一个隐藏参数isComplex，有最后三个条件就往后端传true，否则就往后端
    //传false，根据这个值来判断调用哪一个查询
    function exportBusinessThis() {
        if (false) {
            mini.get("isComplex").setValue("true");
        } else {
            mini.get("isComplex").setValue("false");
        }
        exportBusiness();
    }
    //由于最后三个条件出现后，会调用不同的查询，来避免join的重复结果干扰主查询的结果
    //所以，加了一个隐藏参数isComplex，有最后三个条件就往后端传true，否则就往后端
    //传false，根据这个值来判断调用哪一个查询。但是原生的清空查询不会真正清空mini组件的值，因此也要改造
    function clearFormThis() {
        mini.get("isComplex").setValue("");
        mini.get("materialCode").setValue("");
        mini.get("diagramCode").setValue("");
        mini.get("diagramType").setValue("");
        mini.get("repUserName").setValue("");
        mini.get("manualCode").setValue("");
        mini.get("manualLanguage").setValue("");
        mini.get("manualVersion").setValue("");
        mini.get("manualDescription").setValue("");
        mini.get("manualStatus").setValue("");
        mini.get("designModel").setValue("");
        mini.get("salesModel").setValue("");
        mini.get("materialCodeProduct").setValue("");
        mini.get("productManager").setValue("");
        searchFrm();
    }
    //..
    function remarkCheck() {
        var record = businessListGrid.getSelected();
        if (record != null) {
            var remarkArray = JSON.parse(record.remark);
            remarkListGrid.setData(remarkArray);
            remarkWindow.show();
        }
    }
    //..
    function callBackExiu() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert("请选中一条记录!");
            return;
        }
        if (rows[0].manualStatus != '可转出') {
            mini.alert("只有'可转出'状态的记录能够回调e修!");
            return;
        }
        if (rows[0].callBackResult == '是') {
            mini.alert("已经回调过的记录不能再次回调e修!");
            return;
        }
        mini.confirm("确认发送吗？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                $.ajax({
                    url: jsUseCtxPath + '/serviceEngineering/core/schematicDiagram/callBackExiu.do?businessId=' + rows[0].id,
                    async: true,
                    success: function (result) {
                        mini.alert(result.message);
                        searchFrm();
                    }
                })
            }
        });
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
