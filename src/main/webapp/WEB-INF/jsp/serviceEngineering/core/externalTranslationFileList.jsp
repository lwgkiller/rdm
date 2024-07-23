<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">翻译申请单号：</span>
                    <input class="mini-textbox" id="transApplyId" name="transApplyId" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">手册类型：</span>
                    <input id="manualType" name="manualType" class="mini-combobox" style="width:98%;" enabled="true"
                           textField="value" valueField="key" emptyText="请选择..."
                           allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{'key' : '操保手册','value' : '操保手册'},
							   {'key' : '装修手册','value' : '装修手册'},
							   {'key' : '装箱单','value' : '装箱单'},
							   {'key' : '零件图册','value' : '零件图册'},
							   {'key' : '其他','value' : '其他'}]"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">销售型号：</span>
                    <input class="mini-textbox" id="saleType" name="saleType" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designType" name="designType" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">手册编码：</span>
                    <input class="mini-textbox" id="manualCode" name="manualCode" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">申请时间：</span>
                    <input id="applyTime" name="applyTime" class="mini-datepicker" format="yyyy-MM-dd"
                           allowInput="false" valueType="string" showTime="false" showOkButton="false" showClearButton="false"
                           style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">源手册语言：</span>
                    <input id="sourceManualLan" name="sourceManualLan" class="mini-combobox" style="width:98%;"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">翻译语言：</span>
                    <input id="targetManualLan" name="targetManualLan" class="mini-combobox" style="width:98%;"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                           valueField="key" textField="value" multiSelect="false"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">翻译人员：</span>
                    <input class="mini-textbox" id="translator" name="translator" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">外发文件名称：</span>
                    <input class="mini-textbox" id="outFileName" name="outFileName" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">文件类型：</span>
                    <input class="mini-textbox" id="outFileType" name="outFileType" style="width:100%;"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportList()">导出</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportFile()">批量下载文件</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="saveBusiness()">保存</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true" showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/externalTranslation/externalTranslationFileListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
            <div field="transApplyId" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">翻译申请单号</div>
            <div field="manualType" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">手册类型</div>
            <div field="saleType" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">销售型号</div>
            <div field="designType" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">设计型号</div>
            <div field="materialCode" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">物料编码</div>
            <div field="manualCode" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">手册编码</div>
            <div field="outFileName" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">外发文件名称</div>
            <div width="100" headerAlign='center' align="center" renderer="operationRendererOutFile">操作</div>
            <div field="returnFileName" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">回传文件名称</div>
            <div width="100" headerAlign='center' align="center" renderer="operationRendererReturnFile">操作</div>
            <div field="outFileType" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">文件类型
                <input property="editor" class="mini-textbox">
            </div>
            <div field="outFileTotal" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">文件数量
                <input property="editor" class="mini-textbox">
            </div>
            <div field="outFileNum" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">外发字符数
                <input property="editor" class="mini-textbox">
            </div>
            <div field="outRate" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">重复率
                <input property="editor" class="mini-textbox">
            </div>
            <div field="outDesc" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">备注说明</div>
            <div field="applyTime" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">申请时间</div>
            <div field="sourceManualLan" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">源手册语言</div>
            <div field="targetManualLan" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">翻译语言</div>
            <div field="translator" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">翻译人员</div>
        </div>
    </div>
</div>
<%--导出用--%>
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/externalTranslation/exportFileListInfo.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + new Date().format("yyyy-MM-dd hh:mm:ss") + "<br/>徐州徐工挖掘机械有限公司";
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function operationRendererOutFile(e) {
        var record = e.record;
        var cellHtml = returnPreviewSpanOutFile(record.outFileName, record.outFileId, record.applyId, coverContent);
        var downloadUrl = '/serviceEngineering/core/externalTranslation/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.outFileName + '\',\'' + record.outFileId + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">下载</span>';
        return cellHtml;
    }
    //..
    function operationRendererReturnFile(e) {

        var record = e.record;
        if(record.returnFileName){
            var cellHtml = returnPreviewSpanOutFile(record.returnFileName, record.returnFileId, record.applyId, coverContent);
            var downloadUrl = '/serviceEngineering/core/externalTranslation/fileDownload.do';
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.returnFileName + '\',\'' + record.returnFileId + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">下载</span>';
            return cellHtml;
        }
    }
    //..
    function returnPreviewSpanOutFile(fileName, fileId, formId, coverContent) {
        if(fileName){
            var fileType = getFileType(fileName);
            var s = '';
            if (fileType == 'other') {
                s = '<span  title="预览" style="color: silver" >预览</span>';
            } else if (fileType == 'pdf') {
                var url = '/serviceEngineering/core/attachedDocTranslate/PdfPreviewAndAllDownload.do';
                s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
            } else if (fileType == 'office') {
                var url = '/serviceEngineering/core/attachedDocTranslate/OfficePreview.do';
                s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
            } else if (fileType == 'pic') {
                var url = '/serviceEngineering/core/attachedDocTranslate/ImagePreview.do';
                s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
            }
            return s;
        }
    }
    //..
    function exportList() {
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
    //..
    function exportFile() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var cnt = 0;
        var fileIds = "";
        var fileNames = "";
        var ids = "";
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            if (r.outFileId) {
                //用form的方式去提交
                if (cnt > 0) {
                    ids += ',';
                    fileIds += ",";
                    fileNames += ",";
                }
                ids += r.applyId;
                fileIds += r.outFileId;
                fileNames += r.outFileName;
                cnt = cnt + 1;
            }
            //..外发
            if (r.returnFileId) {
                //用form的方式去提交
                if (cnt > 0) {
                    ids += ',';
                    fileIds += ",";
                    fileNames += ",";
                }
                ids += r.applyId;
                fileIds += r.returnFileId;
                fileNames += r.returnFileName;
                cnt = cnt + 1;
            }
        }
        var url = "/serviceEngineering/core/externalTranslation/zipFileDownload.do";
        downLoadFile(fileNames, fileIds, ids, url);
    }
    //..
    function downLoadFile(fileName, fileId, formId, urlValue) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + urlValue);
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        var inputFormId = $("<input>");
        inputFormId.attr("type", "hidden");
        inputFormId.attr("name", "formId");
        inputFormId.attr("value", formId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputFileId);
        form.append(inputFormId);
        form.submit();
        form.remove();
    }
    //..
    function saveBusiness() {
        var postData = businessListGrid.getChanges("modified");
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/externalTranslation/update4Attribute.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            businessListGrid.reload();
                        }
                    });
                }
            }
        });
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>