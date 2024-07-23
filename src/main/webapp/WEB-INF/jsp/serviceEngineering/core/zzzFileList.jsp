<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>再制造文档列表</title>
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
                    <span class="text" style="width:auto">零部件类型：</span>
                    <input id="partsType" name="partsType" class="mini-combobox" style="width:98%;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="请选择..."
                           data="[{'key' : '发动机','value' : '发动机'}
                                               ,{'key' : '主泵','value' : '主泵'}
                                               ,{'key' : '液压马达','value' : '液压马达'}
                                               ,{'key' : '油缸','value' : '油缸'}
                                               ,{'key' : '阀','value' : '阀'}
                                               ,{'key' : '电气件','value' : '电气件'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">零部件型号：</span>
                    <input class="mini-textbox" id="partsModel" name="partsModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">文件名称：</span>
                    <input class="mini-textbox" id="fileName" name="fileName"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">归档人：</span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>


                <li style="margin-left: 10px">
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="editBusiness()">编辑</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="deleteBusiness()">删除</a>
                    <%--<a class="mini-button" style="margin-right: 5px" plain="true" onclick="previewBusiness2()">预览</a>--%>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="downloadBusiness2()">下载</a>
                    <%--<a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportFile()">批量下载文件</a>--%>
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
         sizeList="[50,100,200,500]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         allowSortColumn="true" allowUnselect="true" autoLoad="false"
         url="${ctxPath}/serviceEngineering/core/zzzFile/dataListQuery.do?menuType=${menuType}">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="partsType" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                零部件类型
            </div>
            <div field="partsModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                零部件型号
            </div>
            <div field="fileName" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                文件名称
            </div>
            <div field="creatorName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                归档人
            </div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                归档时间
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentUserNo = "${currentUserNo}";
    var currentTime = "${currentTime}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var salesModel = "${salesModel}";
    var menuType = "${menuType}";
    var designModel = "${designModel}";
    var vinCode = "${vinCode}";
    var launguageType = "${launguageType}";


    $(function () {
        mini.get("salesModel").setValue(salesModel);
        mini.get("designModel").setValue(designModel);
        mini.get("vinCode").setValue(vinCode);
        mini.get("launguageType").setValue(launguageType);
        searchFrm();
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

        var s = '<span  title="明细" onclick="detailBusiness(\'' + businessId + '\')">明细</span>';
        return s;
    }

    //..
    function previewBusiness(id, fileName, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/zzzFile/Preview.do?id=" + id + "&fileName=" + fileName;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }

    function previewBusiness2() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var id = row.id;
        var fileName = row.fileName;
        if (fileName) {
            previewBusiness(id, fileName, coverContent);
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
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/zzzFile/Download.do");
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
        var fileName = row.fileName;
        if (fileName) {
            // downloadBusiness(id, row.partsAtlasName);
            downloadBusiness(id, row.fileName);
        } else {
            mini.alert("当前记录没有文件");
            return;
        }

    }

    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/serviceEngineering/core/zzzFile/EditPage.do?id=&action=add&menuType=" + menuType;
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
        if (row.CREATE_BY_ != currentUserId && currentUserNo != 'admin') {
            mini.alert("只有归档人能编辑");
            return;
        }

        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/zzzFile/EditPage.do?id=" + id + '&action=edit&menuType=' + menuType;
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
        }
        if (row.CREATE_BY_ != currentUserId && currentUserNo != 'admin') {
            mini.alert("只有归档人才能删除");
            return;
        }

        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                var fileName = row.fileName;
                $.ajax({
                    url: jsUseCtxPath + "/serviceEngineering/core/zzzFile/deleteBusiness.do",
                    type: 'POST',
                    data: mini.encode({id: id, fileName: fileName}),
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
    //..
    //..
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/zzzFile/EditPage.do?id=" + businessId + '&action=detail&menuType=' + menuType;
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
    //..


    function clearFormThis() {
        mini.get("salesModel").setValue("");
        mini.get("designModel").setValue("");
        mini.get("vinCode").setValue("");
        mini.get("partsAtlasName").setValue("");
        mini.get("languageType").setValue("");
        searchFrm();
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
