<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>零件图册下载申请表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/serviceEngineering/partsAtlasFileDownloadApplyEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveInfo" class="mini-button" style="display: none" onclick="saveInDetail()">保存</a>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfoM()">流程信息</a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="partsAtlasFileDownloadForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">申请编号：</td>
                    <td style="min-width:170px">
                        <input id="applyNumber" name="applyNumber" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="CREATE_BY_" name="CREATE_BY_" class="mini-textbox" style="display: none"/>
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">申请部门：</td>
                    <td style="min-width:170px">
                        <input id="departName" name="departName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">是否完备：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="complete" name="complete" class="mini-combobox" style="width:120px;" multiSelect="false"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false"
                               data="[ {'key' : 'yes','value' : '是'},{'key' : 'no','value' : '否'}]"
                               enabled="false"

                        />
                    </td>

                </tr>
            </table>

            <p style="font-size: 16px;font-weight: bold;margin-top: 20px">申请下载的零件图册清单<span
                    style="font-size: 14px;color:red">（仅允许申请人在流程结束后下载）</span></p>
            <div class="mini-toolbar" style="margin-bottom: 5px" id="demandListToolBar">
                <a class="mini-button" id="selfAddRow" plain="true" onclick="addDataRow">新增行</a>
                <a class="mini-button btn-red" id="delDemand" plain="true" onclick="delDataRow">移除</a>

            </div>
            <div id="demandGrid" class="mini-datagrid" allowResize="false" style="height:340px"
            <%--autoload="true"--%>
                 autoload="true"

                 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                 url="${ctxPath}/serviceEngineering/core/partsAtlasFileDownloadApply/demandList.do?applyId=${applyId}"
                 oncellbeginedit="OnCellBeginEdit"
            >
                <div property="columns">
                    <div type="checkcolumn" width="50"></div>
                    <div type="indexcolumn" headerAlign="center" width="50">序号</div>
                    <div field="vinCode" width="120" headerAlign="center" align="center"
                         allowSort="true">整机编号<span style="color: #ff0000">*</span>
                        <input property="editor" class="mini-textbox"/>
                    </div>

                    <div field="languageType" width="80" headerAlign="center" align="center">图册语言
                        <span style="color: #ff0000">*</span>
                        <input property="editor" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                               valueField="key" textField="key"/>
                    </div>
                    <div field="salesModel" width="120" headerAlign="center" align="center"
                         allowSort="true">销售型号
                        <input property="editor" class="mini-textbox">
                    </div>
                    <div field="fileType"type="comboboxcolumn" width="120" headerAlign="center" align="center">文件类型<span style="color: #ff0000">*</span>
                        <input property="editor" class="mini-combobox"
                               textField="name" valueField="name" emptyText="请选择..."
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=LJSCWJLX"
                               allowInput="false" showNullItem="false" nullItemText="请选择..."/>
                    </div>
                    <div field="customerInfo" width="250" headerAlign="center" align="center"
                         allowSort="true">客户信息
                        <input property="editor" class="mini-textbox">
                    </div>
                    <div field="applyReason" width="250" headerAlign="center" align="center"
                         allowSort="true">申请原因
                        <input property="editor" class="mini-textbox">
                    </div>

                    <div field="partsAtlasId" name="selectid" width="110" headerAlign="center" align="center"
                         displayField="partsAtlasName" allowSort="true" allowInput="false" allowCellEdit="false"
                         visible="false">选择关联图册
                        <span style="color: #ff0000">*</span>
                        <input allowInput="false" property="editor" class="mini-buttonedit" showClose="true" oncloseclick="clearButtonEdit(e)"
                               onbuttonclick="getPartsAtlasId()" >

                    </div>
                    <div name="op" cellCls="actionIcons" width="110" headerAlign="center" align="center"
                         renderer="partsAtlasRenderer" cellStyle="padding:0;"
                         visible="false">操作
                    </div>
                </div>
            </div>

            <div id="selectPartsAtlasFileWindow" title="选择零件图册" class="mini-window" style="width:1450px;height:700px;"
                 showModal="true" showFooter="true" allowResize="true" showCloseButton="false" >
                <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
                     borderStyle="border-left:0;border-top:0;border-right:0;">
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">整机编号：</span>
                        <input class="mini-textbox" id="vinCode" name="vinCode" style="width: 160px"/>
                        <span class="text" style="width:auto">图册语言：</span>
                        <input class="mini-textbox" id="languageType" name="languageType" style="width: 60px"/>
                        <span class="text" style="width:auto">文件类型：</span>
                        <input class="mini-combobox" id="fileType" name="fileType" style="width: 120px"
                               textField="name" valueField="name" emptyText="请选择..."
                               allowInput="false" showNullItem="false" nullItemText="请选择..."
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=LJSCWJLX"/>
                        <span class="text" style="width:auto">销售型号：</span>
                        <input class="mini-textbox" id="salesModel" name="salesModel" style="width: 120px"/>
                        <span class="text" style="width:auto">设计型号：</span>
                        <input class="mini-textbox" id="designModel" name="designModel" style="width: 120px"/>
                        <span class="text" style="width:auto">文件名称：</span>
                        <input class="mini-textbox" id="partsAtlasName" name="partsAtlasName"
                               style="width: 120px"/>
                        <a class="mini-button" iconCls="icon-search" plain="true" style="left: 20px"
                           onclick="searchPartsAtlas()">查询</a>
                        <a class="mini-button btn-red" id="clearSearch" plain="true" style="left: 20px"
                           onclick="clearSearch()">清空查询</a>
                    </li>

                </div>
                <div class="mini-fit">
                    <div id="partsAtlasGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
                         allowResize="false"
                         idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
                         multiSelect="false"
                         allowAlternating="true" pagerButtons="#pagerButtons"
                         url="${ctxPath}/serviceEngineering/core/patsAtlasFileCollection/dataListQuery.do">
                        <div property="columns">
                            <div type="checkcolumn" width="30"></div>
                            <div field="vinCode" width="120" align="center" headerAlign="center" allowSort="true">整机编号
                            </div>
                            <div field="partsAtlasName" width="120" align="center" headerAlign="center"
                                 allowSort="true">零件图册名称
                            </div>
                            <div field="salesModel" width="120" align="center" headerAlign="center" allowSort="true">
                                销售型号
                            </div>
                            <div field="designModel" width="120" align="center" headerAlign="center" allowSort="true">
                                设计型号
                            </div>
                            <div field="languageType" width="120" align="center" headerAlign="center" allowSort="true">
                                语言类型
                            </div>
                            <div field="fileType" width="120" align="center" headerAlign="center" allowSort="true">
                                文件类型
                            </div>
                            <div field="CREATE_TIME_" width="120" align="center" headerAlign="center" allowSort="true">
                                创建时间
                            </div>
                            <div field="UPDATE_TIME_" width="120" align="center" headerAlign="center" allowSort="true">
                                更新时间
                            </div>
                        </div>
                    </div>
                </div>
                <div property="footer" style="padding:5px;height: 35px">
                    <table style="width:100%;height: 100%">
                        <tr>
                            <td style="width:120px;text-align:center;">
                                <input type="button" style="height: 25px;width: 70px" value="确定"
                                       onclick="selectAtlasOK()"/>
                                <input type="button" style="height: 25px;width: 70px" value="取消"
                                       onclick="selectAtlasCancel()"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </form>
        >

    </div>

</div>


<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var jsUseCtxPath = "${ctxPath}";
    var partsAtlasFileDownloadForm = new mini.Form("#partsAtlasFileDownloadForm");
    var demandGrid = mini.get("demandGrid");
    var partsAtlasGrid = mini.get("partsAtlasGrid");
    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var instId = "${instId}";
    var isTCGL = ${isTCGL};
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var selectPartsAtlasFileWindow = mini.get("selectPartsAtlasFileWindow");

    function partsAtlasRenderer(e) {
        var record = e.record;
        //需都可以预览，只有申请人在流程结束后，通过detail查看才能下载
        var cellHtml = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPartsAtlas(\'' + record.partsAtlasId + '\',\'' + coverContent + '\',\'' + record.fileName + '\')">预览</span>';
        var creatorId = mini.get("CREATE_BY_").getValue();
        if (action == 'detail' && status == 'SUCCESS_END' && currentUserId == creatorId) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downloadPartsAtlas(\'' + record.partsAtlasId + '\',\'' + record.fileName + '\')">下载</span>';
        }
        return cellHtml;
    }

    function previewPartsAtlas(id, coverContent,fileNameStr) {
        // 参数传过来的是字符串
        if (id == "undefined" || id == "") {
            mini.alert("请关联图册后预览");
            return;
        }
        if (fileNameStr) {
            var fileType = getFileType(fileNameStr);
            if (fileType == "pdf") {
                var previewUrl = jsUseCtxPath + "/serviceEngineering/core/patsAtlasFileCollection/Preview.do?id=" + id+"&fileName="+fileNameStr;
                window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
            }
            if (fileType == "office") {
                var previewUrl = jsUseCtxPath + "/serviceEngineering/core/patsAtlasFileCollection/previewOffice.do?id=" + id+"&fileName="+fileNameStr;
                var preWindow=window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
                preWindow.onload = function () {
                    if (fileNameStr == '') {
                        fileNameStr = "文件预览";
                    }
                    preWindow.document.title = fileNameStr;
                };
            }
        }
        var loop = setInterval(function () {
            if (preWindow.closed) {
                clearInterval(loop);
            }
            else {
                preWindow.document.title = fileNameStr;
            }
        }, 1000);
    }


    demandGrid.on("cellendedit", function (e) {
        var record = e.record;
        //编辑三者中任意一个时判断是否与其他行存在三个字段完全相同的情况
        if (e.field == "vinCode" || e.field == "languageType" || e.field == "fileType") {
            console.log("...");
            var hadRows = demandGrid.findRows();
            var checkRepeatSet = new Set();
            var checkFileTypeSet = new Set();
            for (var i = 0; i < hadRows.length; i++) {
                var tempRow = hadRows[i]
                //整机编号、图册语言、文件类型都填写完成才判断
                if (tempRow.vinCode && tempRow.languageType && tempRow.fileType) {
                    var tempStr = tempRow.vinCode + tempRow.languageType + tempRow.fileType;
                    if (checkRepeatSet.has(tempStr)) {
                        mini.alert(tempRow.vinCode + "的" + tempRow.languageType + "版本的" + tempRow.fileType + "已经存在");
                        demandGrid.updateRow(record, {fileType: ""});
                        break;
                    } else {
                        checkRepeatSet.add(tempStr);
                    }
                }
                //控制零件图册和保养、易损件清单不能同时申请
                if (tempRow.fileType) {
                    if (checkFileTypeSet.size != 0) {
                        if ((checkFileTypeSet.has("零件图册") && tempRow.fileType!="零件图册")||(!checkFileTypeSet.has("零件图册") && tempRow.fileType=="零件图册")) {
                            mini.alert("零件图册与其它文件类型不能同时申请！");
                            demandGrid.updateRow(record, {fileType: ""});
                            break;
                        }
                    } else {
                        checkFileTypeSet.add(tempRow.fileType);
                    }
                }
            }
        }
    });
    // demandGrid.on("cellbeginedit", function (e) {
    //     var record = e.record;
    //     if (action == 'detail' ) {
    //         e.editor.setEnabled(false);
    //         debugger;
    //         mini.alert(isTCGL);
    //         if( isTCGL && e.field == "selectid")
    //         {
    //             e.editor.setEnabled(true);
    //         }
    //     }
    //
    // });

    function downloadPartsAtlas(id, description) {
        if (id == "undefined" || id == "") {
            mini.alert("请关联图册后下载");
            return;
        }
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "input>post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/patsAtlasFileCollection/Download.do");
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

    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
</body>
</html>
