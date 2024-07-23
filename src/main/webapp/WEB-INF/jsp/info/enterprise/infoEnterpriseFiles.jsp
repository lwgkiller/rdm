<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>企业供应商文件样本</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li>
            <span class="text" style="width:auto">文件名称: </span>
            <input id="key" class="mini-textbox" style="width: 150px" onenter="onHanlerEnter"/>
            <input id="test" class="mini-textbox" style="width: 150px;display: none"/>
            <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchByName()">查询</a>
            <a class="mini-button btn-red" onclick="refreshAchievementType()" plain="true">清空查询</a>
            <%--<input id="loadTableHeader" class="mini-textbox" style="width: 150px;display: none" name="loadTableHeader" />--%>
        </li>
        <span id="firstSeparator" class="separator"></span>
        <li>
            <%--<a id="addKpi" class="mini-button" iconCls="icon-add" onclick="openEditorWindow()">新增</a>--%>
            <a id="addKpi" class="mini-button" iconCls="icon-add" onclick="addAchievementType()">上传文件</a>
            <%--<a id="removeKpi" class="mini-button btn-red" plain="true" onclick="removeAchievementType()">删除</a>--%>
        </li>
        <%--<span id="secondSeparator" class="separator"></span>--%>
        <%--<li>--%>
            <%--<a id="saveKpi" class="mini-button" iconCls="icon-save" onclick="saveAchievementType()">保存</a>--%>
            <%--<p style="display: inline-block;color: #f6253e;font-size:15px;vertical-align: middle">（--%>
                <%--<image src="${ctxPath}/styles/images/warn.png"--%>
                       <%--style="margin-right:5px;vertical-align: middle;height: 15px"/>--%>
                <%--删除后需要进行保存操作）--%>
            <%--</p>--%>
        <%--</li>--%>
    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/info/core/enterprise/enterpriseFile/list.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons" allowCellEdit="false" allowCellSelect="true" editNextOnEnterKey="false"
         editNextRowCell="false" oncellvalidation="onCellValidation">
        <div property="columns">
            <div name="action" cellCls="actionIcons" width="40" headerAlign="center" align="center" renderer="renderer">
                操作
            </div>
            <div name="fileName" field="fileName" headerAlign="center" align="center">
                文件名称<span style="color: #ff0000">*</span>
            </div>
            <div name="fileSize" field="fileSize" width="30" headerAlign="center" align="center">
                文件大小<span style="color: #ff0000">*</span>
            </div>
            <div field="creator" width="30" headerAlign='center' align="center">创建人</div>
            <div field="createTime" width="40" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center">
                创建时间
            </div>
        </div>
    </div>
</div>
<%--<div class="mini-toolbar" style="text-align:center;padding-top:8px;padding-bottom:8px;" borderStyle="border:0;">--%>
    <%--<a class="mini-button" style="width:60px;" onclick="onOk()">确定</a>--%>
    <%--<span style="display:inline-block;width:25px;"></span>--%>
    <%--<a class="mini-button btn-red" plain="true" style="width:60px;" onclick="onCancel()">取消</a>--%>
<%--</div>--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var isManager = whetherIsProjectManager(${currentUserRoles});
    var url = "${ctxPath}/info/core/enterprise/enterpriseFile/list.do";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var isZLGHZY =${isZLGHZY};
    var listGrid = mini.get("listGrid");
    var key = mini.get("#key");
    var belongId = "${belongId}";
    searchByName();
    qxkz();

    // 权限控制
    function qxkz() {
        if (!isZLGHZY) {
            $("p").hide();
            $("#firstSeparator").hide();
            $("#secondSeparator").hide();
            mini.get("addKpi").hide();
            // mini.get("removeKpi").hide();
            // mini.get("saveKpi").hide();
            return;
        }
    }

    function renderer(e) {
        let record = e.record;
        var fileId = record.id;
        var fileName = record.fileName;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(fileId,fileName,belongId,coverContent);
        var s = cellHtml + '<span title="下载" onclick="downLoadFile(\'' + record._uid + '\')">下载</span>';
        if (currentUserId === record.CREATE_BY_ || isZLGHZY) {
            s += '<span title="删除" onclick="removeFile(\''+ record._uid +'\')">删除</span>';
        }
        return s;
    }

    function onHanlerEnter() {
        searchByName();
    }

    function searchByName() {
        var queryParam = [];
        //其他筛选条件
        var cxName = $.trim(key.getValue());
        if (cxName) {
            queryParam.push({name: "fileName", value: cxName});
        }
        if (belongId) {
            queryParam.push({name: "belongId", value: belongId});
        }
        var data = {};
        data.filter = JSON.stringify(queryParam);
        data.pageIndex = listGrid.getPageIndex();
        data.pageSize = listGrid.getPageSize();
        data.sortField = listGrid.getSortField();
        data.sortOrder = listGrid.getSortOrder();
        listGrid.load(data);
    }

    function refreshAchievementType() {
        key.setValue("");
        searchByName();
    }

    // 新增行
    function addAchievementType() {
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/info/core/enterprise/filesUpload/page.do",
            width: 750,
            height: 450,
            showModal:false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                iframe.contentWindow.SetData(belongId);
            },
            ondestroy: function (action) {
                searchByName();
            }
        });
    }

    /**
     * 获取附件类型
     * */
    function getFileType(fileName) {
        var suffix = "";
        var suffixIndex = fileName.lastIndexOf('.');
        if (suffixIndex != -1) {
            suffix = fileName.substring(suffixIndex + 1).toLowerCase();
        }
        var pdfArray = ['pdf'];
        if (pdfArray.indexOf(suffix) != -1) {
            return 'pdf';
        }
        var officeArray = ['doc', 'docx', 'ppt', 'txt', 'xlsx', 'xls', 'pptx'];
        if (officeArray.indexOf(suffix) != -1) {
            return 'office';
        }
        var picArray = ['jpg', 'jpeg', 'jif', 'bmp', 'png', 'tif', 'gif'];
        if (picArray.indexOf(suffix) != -1) {
            return 'pic';
        }
        return 'other';
    }

    function returnPreviewSpan(fileId, fileName, belongId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileId + '\',\'' + fileName + '\',\'' + belongId + '\',\'' + coverContent + '\')">预览</span>';
        } else if (fileType == 'office') {
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileId + '\',\'' + fileName + '\',\'' + belongId + '\',\'' + coverContent + '\')">预览</span>';
        } else if (fileType == 'pic') {
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileId + '\',\'' + fileName + '\',\'' + belongId + '\',\'' + coverContent + '\')">预览</span>';
        }
        return s;
    }

    function previewPdf(field, fileName, belongId, coverConent) {
        if (!fileName) {
            fileName = '';
        }
        if (!belongId) {
            belongId = '';
        }
        var previewUrl = jsUseCtxPath + "/info/core/enterprise/downOrPdfPreview.do?action=preview&fileName=" + encodeURIComponent(fileName) + "&belongId=" + belongId + "&fileId=" + field;
        let open = window.open('_blank');
        open.location = jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&file=" + encodeURIComponent(previewUrl);
        open.document.title = fileName;
    }

    function previewPic(field, fileName, belongId) {
        if (!fileName) {
            fileName = '';
        }
        if (!belongId) {
            belongId = '';
        }
        var previewUrl = jsUseCtxPath + "/info/core/enterprise/imagePreview.do?fileName=" + encodeURIComponent(fileName) + "&belongId=" + belongId + "&fileId=" + field;
        let open = window.open('_blank');
        open.location = previewUrl;
        // TODO 不起作用
        open.document.title = fileName;
    }

    function previewDoc(field, fileName, belongId, coverContent) {
        if (!fileName) {
            fileName = '';
        }
        if (!belongId) {
            belongId = '';
        }
        var previewUrl = jsUseCtxPath + "/info/core/enterprise/officePreview.do?fileName=" + encodeURIComponent(fileName) + "&belongId=" + belongId + "&fileId=" + field;
        let open = window.open('_blank');
        open.location = jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl);
        // TODO 不起作用
        open.document.title = fileName;
    }


    // 删除行
    // function removeAchievementType() {
    //     if (!isZLGHZY) {
    //         mini.alert("没有操作权限!");
    //         return;
    //     }
    //     var rows = listGrid.getSelecteds();
    //     if (rows.length > 0) {
    //         mini.showMessageBox({
    //             title: "提示信息！",
    //             iconCls: "mini-messagebox-info",
    //             buttons: ["ok", "cancel"],
    //             message: "是否确定删除？",
    //             callback: function (action) {
    //                 if (action == "ok") {
    //                     listGrid.removeRows(rows, false);
    //                 }
    //             }
    //         });
    //
    //     } else {
    //         mini.alert("请至少选中一条记录");
    //         return;
    //     }
    // }

    // 下载文件
    function downLoadFile (uid) {
        let rowData = listGrid.getRowByUID(uid);
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/info/core/enterprise/file/download.do");
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", rowData.fileName);
        var id = $("<input>");
        id.attr("type", "hidden");
        id.attr("name", "id");
        id.attr("value", rowData.id);
        var belongIdInput = $("<input>");
        belongIdInput.attr("type", "hidden");
        belongIdInput.attr("name", "belongId");
        belongIdInput.attr("value", belongId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(id);
        form.append(belongIdInput);
        form.submit();
        form.remove();
    }

    // 删除文件
    function removeFile (uid) {
        let rowData = listGrid.getRowByUID(uid);
        mini.confirm("确定删除？", "确定？",
            function (action) {
                if (action == "ok") {
                    $.ajax({
                        url:jsUseCtxPath + "/info/core/enterprise/file/delete.do?id="+rowData.id+"&belongId="+belongId+"&fileName="+rowData.fileName,
                        success:function (data) {
                            searchByName();
                        }
                    });
                }
            }
        );
    }

    // 自定义验证
    function onCellValidation(e) {
        switch (e.field) {
            case "fileName":
                verifyValue(e, "文件名称");
                break;
            default:
                break;
        }
    }

    // 验证函数
    function verifyValue(e, headerName) {
        let value = e.value;
        if (value === undefined || value === null || value === "") {
            e.isValid = false;
            e.errorText = headerName + "必填";
        } else {
            e.isValid = true
        }
    }

    // 保存数据
    function saveAchievementType() {
        // 验证表格
        listGrid.validate();
        // 验证是否通过
        if (listGrid.isValid() === false) {
            var error = listGrid.getCellErrors()[0];
            listGrid.beginEditCell(error.record, error.column);
            showTips(error.errorText);
            return;
        }
        // 获取变化数据
        var data = listGrid.getChanges();
        var message = "数据保存成功";
        if (data.length > 0) {
            var json = mini.encode(data);
            $.ajax({
                url: jsUseCtxPath + "/info/core/enterprise/enterpriseFile/batchOptions.do",
                data: json,
                type: "post",
                contentType: 'application/json',
                async: false,
                success: function (text) {
                    if (text && text.message) {
                        message = text.message;
                    }
                }
            });
        }
        mini.showMessageBox({
            title: "提示信息",
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok") {
                    listGrid.reload();
                }
            }
        });
    }

    // 展示提示
    function showTips(message) {
        mini.showMessageBox({
            showModal: false,
            width: 250,
            title: "提示",
            iconCls: "mini-messagebox-warning",
            message: message,
            timeout: 2000,
            x: "right",
            y: "bottom"
        });
    }

    // 双击行事件
    function onRowDblClick(e) {
        onOk();
    }

    // 操作列样式渲染
    listGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });

    //////////////////////////////////
    function CloseWindow(action) {
        if (window.CloseOwnerWindow) return window.CloseOwnerWindow(action);
        else window.close();
    }

    function onOk() {
        CloseWindow("ok");
    }

    function onCancel() {
        CloseWindow("cancel");
    }

</script>
<%--<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>--%>
</body>
</html>
