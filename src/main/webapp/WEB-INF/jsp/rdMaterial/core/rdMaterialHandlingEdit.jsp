<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>物料处理</title>
    <%@include file="/commons/edit.jsp" %>
</head>
<body>
<%--工具栏--%>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" onclick="saveBusiness()">暂存</a>
        <a id="commitBusiness" class="mini-button" onclick="commitBusiness()">提交</a>
    </div>
</div>
<%--表单视图--%>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="businessForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="responsibleDepId" name="responsibleDepId"/>
            <input class="mini-hidden" id="businessStatus" name="businessStatus"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold">
                    物料处理
                </caption>
                <tr>
                    <td style="width: 10%">处理单据号：</td>
                    <td style="width: 23%">
                        <input id="businessNo" name="businessNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 10%">处置人员：</td>
                    <td style="width: 23%">
                        <input id="responsibleUserId" name="responsibleUserId" textname="responsibleUser" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;" allowinput="false" label="责任人员" length="50"
                               mainfield="no" single="true"/>
                    </td>
                    <td style="width: 10%">处置部门：</td>
                    <td style="width: 23%">
                        <input id="responsibleDep" name="responsibleDep" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">处置日期：</td>
                    <td style="width: 23%">
                        <input id="handlingDate" name="handlingDate" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">备注：</td>
                    <td colspan="5">
                        <textarea id="remarks" name="remarks" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px">
                        </textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px">评审单：</td>
                    <td colspan="5">
                        <div id="fileButtons1" class="mini-toolbar">
                            <a id="addFile1" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addFile1">添加附件</a>
                        </div>
                        <div id="fileListGrid1" class="mini-datagrid" style="height: 85%" allowResize="false" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             url="${ctxPath}/rdMaterialCommon/core/file/getFileListInfos.do?businessId=${businessId}&businessType=yanFaWuLiaoChuLiPingShen">
                            <div property="columns">
                                <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="fileRenderer">操作</div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px">出库依据：</td>
                    <td colspan="5">
                        <div id="fileButtons2" class="mini-toolbar">
                            <a id="addFile2" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addFile2">添加附件</a>
                        </div>
                        <div id="fileListGrid2" class="mini-datagrid" style="height: 85%" allowResize="false" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             url="${ctxPath}/rdMaterialCommon/core/file/getFileListInfos.do?businessId=${businessId}&businessType=yanFaWuLiaoChuKuYiJu">
                            <div property="columns">
                                <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="fileRenderer">操作</div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%--物料信息--%>
                <tr>
                    <td style="width: 10%;height: 500px;text-align: center;">物料信息：</td>
                    <td colspan="5">
                        <div id="itemsButtons" class="mini-toolbar">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="addItem">添加处理物料</a>
                            <a id="deleteItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="deleteItem">删除</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="false" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             oncellvalidation="itemListGridCellValidation"
                             url="${ctxPath}/rdMaterial/core/handling/getItemList.do?businessId=${businessId}">
                            <div property="columns">
                                <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
                                <div field="materialCode" width="100" headerAlign="center" align="center">物料号</div>
                                <div field="materialType" width="100" headerAlign="center" align="center">物料类型</div>
                                <div field="businessNo" width="130" headerAlign="center" align="center">入库单据号</div>
                                <div field="responsibleDep" width="130" headerAlign="center" align="center">责任部门</div>
                                <div field="responsibleUser" width="130" headerAlign="center" align="center">责任人</div>
                                <div field="inDate" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">入库日期</div>
                                <div field="reasonForStorage" width="130" headerAlign="center" align="center">入库原因</div>
                                <div field="inQuantity" width="80" headerAlign="center" align="center">入库数量</div>
                                <div field="untreatedQuantity" width="80" headerAlign="center" align="center">未处理数量</div>
                                <div field="handlingQuantity" width="80" headerAlign="center" align="center">处理数量
                                    <input id="handlingQuantity" property="editor" class="mini-spinner" style="width:98%;" minValue="0"
                                           maxValue="9999"/>
                                </div>
                                <div field="handlingMethod" width="100" headerAlign="center" align="center">处理方式
                                    <input id="handlingMethod" property="editor" class="mini-combobox" style="width:98%"
                                           data="[{'key':'回用','value':'回用'},{'key':'转备件','value':'转备件'},
                                           {'key':'返修改制','value':'返修改制'},{'key':'试验教学','value':'试验教学'},
                                           {'key':'报废','value':'报废'},{'key':'账物调整(调减)','value':'账物调整(调减)'},
                                           {'key':'账物调整(调增)','value':'账物调整(调增)'},{'key':'退货','value':'退货'},
                                           {'key':'回购','value':'回购'}]"
                                           valueField="key" textField="value"/>
                                </div>
                                <div field="problemFile" width="80" headerAlign="center" align="center" renderer="itemFileRenderer">附件</div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--选择入库物料明细用窗口--%>
<div id="selectInStorageItemWindow" title="入库物料明细" class="mini-window" style="width:1300px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">入库单号: </span>
        <input id="businessNoInStorage" class="mini-textbox" width="130" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">物料号: </span>
        <input id="materialCodeInStorage" class="mini-textbox" width="130" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">物料描述: </span>
        <input id="materialDescriptionInStorage" class="mini-textbox" width="130" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">责任人: </span>
        <input id="responsibleUserInStorage" class="mini-textbox" width="130" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">责任部门: </span>
        <input id="responsibleDepInStorage" class="mini-textbox" width="130" style="margin-right: 15px"/>
        <br>
        <span style="font-size: 14px;color: #777">入库日期: </span>
        <input id="inDateBeginInStorage" name="inDateBegin" class="mini-datepicker" format="yyyy-MM-dd"
               showTime="false" showOkButton="false" showClearButton="true" width="130"/>
        <span style="font-size: 14px;color: #777">至: </span>
        <input id="inDateEndInStorage" name="inDateEnd" class="mini-datepicker" format="yyyy-MM-dd"
               showTime="false" showOkButton="false" showClearButton="true" width="130"/>
        <a class="mini-button" plain="true" onclick="searchInStorageItem()">查询</a>
        <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormInStorageItem()">清空查询</a>
    </div>
    <div class="mini-fit">
        <div id="inStorageItemListGridGrid" idField="id" class="mini-datagrid" style="width: 100%; height: 100%;"
             allowResize="false" onrowdblclick="onRowDblClick" multiSelect="true"
             showColumnsMenu="false" sizeList="[20,50]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/rdMaterial/core/inStorage/itemListQuery.do?businessStatus=已提交">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="materialCode" width="100" headerAlign="center" align="center">物料号</div>
                <div field="materialDescription" width="200" headerAlign="center" align="center">物料描述</div>
                <div field="materialType" width="100" headerAlign="center" align="center">物料类型</div>
                <div field="businessNo" width="130" headerAlign="center" align="center">入库单据号</div>
                <div field="responsibleDep" width="130" headerAlign="center" align="center">责任部门</div>
                <div field="responsibleUser" width="130" headerAlign="center" align="center">责任人</div>
                <div field="inDate" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">入库日期</div>
                <div field="reasonForStorage" width="130" headerAlign="center" align="center" renderer="render">入库原因</div>
                <div field="inQuantity" width="80" headerAlign="center" align="center">入库数量</div>
                <div field="untreatedQuantity" width="80" headerAlign="center" align="center">未处理数量</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectInStorageItemOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectInStorageItemHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessId = "${businessId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var businessForm = new mini.Form("#businessForm");
    var fileButtons1 = mini.get("fileButtons1");
    var fileListGrid1 = mini.get("fileListGrid1");
    var fileButtons2 = mini.get("fileButtons2");
    var fileListGrid2 = mini.get("fileListGrid2");
    var itemsButtons = mini.get("itemsButtons");
    var itemListGrid = mini.get("itemListGrid");
    var selectInStorageItemWindow = mini.get("selectInStorageItemWindow");
    var inStorageItemListGridGrid = mini.get("inStorageItemListGridGrid");
    //..
    $(function () {
        //锁定所有控件
        lockAll();
        if (businessId) {
            var url = jsUseCtxPath + "/rdMaterial/core/handling/getDataById.do?businessId=" + businessId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    businessForm.setData(json);
                    //不同场景的处理
                    if (action == "detail") {
                        //查看 保持锁定所有控件
                    } else if (action == "edit") {
                        unlockAll();
                    }
                }
            });
        } else {
            unlockAll();
        }
    });
    //..锁定所有控件
    function lockAll() {
        lockForm();
        lockSave();
        lockCommit();
    }
    //..解锁所有控件
    function unlockAll() {
        unlockForm();
        unlockSave();
        unlockCommit();
    }
    //..锁定表单
    function lockForm() {
        businessForm.setEnabled(false);
        fileButtons1.hide();
        fileButtons2.hide();
        itemsButtons.hide();
    }
    //..解锁表单
    function unlockForm() {
        businessForm.setEnabled(true);
        mini.get("responsibleDep").setEnabled(false);
        fileButtons1.show();
        fileButtons2.show();
        itemsButtons.show();
    }
    //..锁定保存
    function lockSave() {
        mini.get("saveBusiness").setEnabled(false);
    }
    //..解锁保存
    function unlockSave() {
        mini.get("saveBusiness").setEnabled(true);
    }
    //..锁定提交
    function lockCommit() {
        mini.get("commitBusiness").setEnabled(false);
    }
    //..解锁提交
    function unlockCommit() {
        mini.get("commitBusiness").setEnabled(true);
    }
    //..
    function saveBusiness() {
        var postData = _GetFormJsonMini("businessForm");
        postData.handlingDate = mini.get("handlingDate").getFormValue();
        var items = itemListGrid.getChanges();
        if (items.length > 0) {
            postData.items = items;
        }
        _SubmitJson({
            url: jsUseCtxPath + "/rdMaterial/core/handling/saveBusiness.do",
            method: 'POST',
            data: postData,
            postJson: true,
            showMsg: false,
            success: function (returnData) {
                if (returnData.success) {
                    mini.alert(returnData.message);
                    var url = jsUseCtxPath + "/rdMaterial/core/handling/editPage.do?businessId=" +
                        returnData.data + "&action=edit";
                    window.location.href = url;
                } else {
                    mini.alert("保存失败:" + returnData.message);
                }
            },
            fail: function (returnData) {
                mini.alert("保存失败:" + returnData.message);
            }
        });
    }
    //..
    function commitBusiness() {
        var postData = _GetFormJsonMini("businessForm");
        postData.handlingDate = mini.get("handlingDate").getFormValue();
        var items = itemListGrid.getChanges();
        if (items.length > 0) {
            postData.items = items;
        }
        //检查必填项
        var checkResult = commitValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        mini.confirm("确定提交？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdMaterial/core/handling/commitBusiness.do",
                    method: 'POST',
                    data: postData,
                    postJson: true,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message);
                            window.close();
                        } else {
                            mini.alert("提交失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("提交失败:" + returnData.message);
                    }
                });
            }
        });
    }
    //..
    function commitValidCheck(postData) {
        var checkResult = {};
        if (!postData.businessNo) {
            checkResult.success = false;
            checkResult.reason = '处理单据号不能为空！';
            return checkResult;
        }
        if (!postData.responsibleUserId) {
            checkResult.success = false;
            checkResult.reason = '处置人员不能为空！';
            return checkResult;
        }
        if (!postData.handlingDate) {
            checkResult.success = false;
            checkResult.reason = '处置日期不能为空！';
            return checkResult;
        }
        var fileList1 = fileListGrid1.getData();
        if (fileList1.length == 0) {
            checkResult.success = false;
            checkResult.reason = '评审单不能为空！';
            return checkResult;
        }
        var fileList2 = fileListGrid2.getData();
        if (fileList2.length == 0) {
            checkResult.success = false;
            checkResult.reason = '出库凭证不能为空！';
            return checkResult;
        }
        itemListGrid.validate();
        if (!itemListGrid.isValid()) {
            var error = itemListGrid.getCellErrors()[0];
            itemListGrid.beginEditCell(error.record, error.column);
            checkResult.success = false;
            checkResult.reason = error.column.header + error.errorText;
            return checkResult;
        }
        var items = itemListGrid.getData();
        var set = new Set();
        for (var i = 0, l = items.length; i < l; i++) {
            set.add(items[i].materialCode + items.businessNo);
        }
        if (items.length != set.size) {
            checkResult.success = false;
            checkResult.reason = '同一个入库信息的同一个物料不能重复！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }
    //..
    function itemListGridCellValidation(e) {
        debugger;
        if (e.field == 'materialCode' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'handlingQuantity' && (!e.value || e.value == '0')) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'handlingMethod' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
    }
    //添加评审单
    function addFile1() {
        if (!businessId) {
            mini.alert("请先点击暂存进行主档的保存");
            return;
        }
        var urlCut = "/rdMaterialCommon/core/file/fileUpload.do";
        url = "/rdMaterialCommon/core/file/openFileUploadWindow.do?businessId=" + businessId +
            "&businessType=yanFaWuLiaoChuLiPingShen&urlCut=" + urlCut + "&fileType=other";
        openFileUploadWindow1(jsUseCtxPath + url);
    }
    //..
    function openFileUploadWindow1(url) {
        mini.open({
            title: "文件上传",
            url: url,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (returnData) {
                fileListGrid1.load();
            }
        });
    }
    //..添加入库依据
    function addFile2() {
        if (!businessId) {
            mini.alert("请先点击暂存进行主档的保存");
            return;
        }
        var urlCut = "/rdMaterialCommon/core/file/fileUpload.do";
        url = "/rdMaterialCommon/core/file/openFileUploadWindow.do?businessId=" + businessId +
            "&businessType=yanFaWuLiaoChuKuYiJu&urlCut=" + urlCut + "&fileType=other";
        openFileUploadWindow2(jsUseCtxPath + url);
    }
    //..
    function openFileUploadWindow2(url) {
        mini.open({
            title: "文件上传",
            url: url,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (returnData) {
                fileListGrid2.load();
            }
        });
    }
    //..文件操作渲染
    function fileRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        var previewUrls = {
            pdf: "/rdMaterialCommon/core/file/pdfPreview.do",
            office: "/rdMaterialCommon/core/file/officePreview.do",
            pic: "/rdMaterialCommon/core/file/imagePreview.do"
        };
        var downLoadUrl = "/rdMaterialCommon/core/file/pdfPreview.do";
        var deleteUrl = "/rdMaterialCommon/core/file/deleteFile.do";
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.businessId, record.businessType, coverContent, previewUrls);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.businessId + '\',\'' +
            record.businessType + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (action == "edit") {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.businessId + '\',\'' +
                record.businessType + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    //..
    function returnPreviewSpan(fileName, fileId, businessId, businessType, coverContent, previewUrls) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId +
                '\',\'' + businessId + '\',\'' + businessType + '\',\'' + coverContent + '\',\'' + previewUrls.pdf + '\')">预览</span>';
        } else if (fileType == 'office') {
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId +
                '\',\'' + businessId + '\',\'' + businessType + '\',\'' + coverContent + '\',\'' + previewUrls.office + '\')">预览</span>';
        } else if (fileType == 'pic') {
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId +
                '\',\'' + businessId + '\',\'' + businessType + '\',\'' + coverContent + '\',\'' + previewUrls.pic + '\')">预览</span>';
        }
        return s;
    }
    //..
    function previewPdf(fileName, fileId, businessId, businessType, coverConent, url) {
        if (!fileName) {
            fileName = '';
        }
        if (!fileId) {
            fileId = '';
        }
        if (!businessId) {
            businessId = '';
        }
        if (!businessType) {
            businessType = '';
        }
        var previewUrl = jsUseCtxPath + url + "?action=preview&fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&businessId=" + businessId + "&businessType=" + businessType;
        var preWindow = window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&file=" + encodeURIComponent(previewUrl));
        // mh 预览title原为pdf的描述，改为文件名或固定字符
        preWindow.onload = function () {
            if (fileName == '') {
                fileName = "文件预览";
            }
            preWindow.document.title = fileName;
        };
        var loop = setInterval(function () {
            if (preWindow.closed) {
                clearInterval(loop);
            }
            else {
                preWindow.document.title = fileName;
            }
        }, 1000);
    }
    //..
    function previewPic(fileName, fileId, businessId, businessType, coverConent, url) {
        if (!fileName) {
            fileName = '';
        }
        if (!fileId) {
            fileId = '';
        }
        if (!businessId) {
            businessId = '';
        }
        var previewUrl = jsUseCtxPath + url + "?fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&businessId=" + businessId + "&businessType=" + businessType;
        var preWindow = window.open(previewUrl);
        preWindow.onload = function () {
            if (fileName == '') {
                fileName = "文件预览";
            }
            preWindow.document.title = fileName;
        };
        var loop = setInterval(function () {
            if (preWindow.closed) {
                clearInterval(loop);
            }
            else {
                preWindow.document.title = fileName;
            }
        }, 1000);
    }
    //..
    function previewDoc(fileName, fileId, businessId, businessType, coverConent, url) {
        if (!fileName) {
            fileName = '';
        }
        if (!fileId) {
            fileId = '';
        }
        if (!businessId) {
            businessId = '';
        }
        var previewUrl = jsUseCtxPath + url + "?fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&businessId=" + businessId + "&businessType=" + businessType;
        var preWindow = window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
        preWindow.onload = function () {
            if (fileName == '') {
                fileName = "文件预览";
            }
            preWindow.document.title = fileName;
        };
        var loop = setInterval(function () {
            if (preWindow.closed) {
                clearInterval(loop);
            }
            else {
                preWindow.document.title = fileName;
            }
        }, 1000);
    }
    //..
    function downLoadFile(fileName, fileId, businessId, businessType, urlValue) {
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
        inputFormId.attr("name", "businessId");
        inputFormId.attr("value", businessId);
        var inputBusinessType = $("<input>");
        inputBusinessType.attr("type", "hidden");
        inputBusinessType.attr("name", "businessType");
        inputBusinessType.attr("value", businessType);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputFileId);
        form.append(inputFormId);
        form.append(inputBusinessType);
        form.submit();
        form.remove();
    }
    //..
    function deleteFile(fileName, fileId, businessId, businessType, urlValue) {
        mini.confirm("确定删除？", "提示",
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + urlValue;
                    var data = {
                        fileName: fileName,
                        id: fileId,
                        businessId: businessId,
                        businessType: businessType
                    };
                    $.ajax({
                        url: url,
                        method: 'post',
                        contentType: 'application/json',
                        data: mini.encode(data),
                        success: function (json) {
                            fileListGrid1.load();
                            fileListGrid2.load();
                        }
                    });
                }
            }
        );
    }
    //..
    function addItem() {
        selectInStorageItemWindow.show();
        clearFormInStorageItem();
    }
    //..
    function searchInStorageItem() {
        var queryParam = [];
        var businessNoInStorage = $.trim(mini.get("businessNoInStorage").getValue());
        if (businessNoInStorage) {
            queryParam.push({name: "businessNo", value: businessNoInStorage});
        }
        var materialCodeInStorage = $.trim(mini.get("materialCodeInStorage").getValue());
        if (materialCodeInStorage) {
            queryParam.push({name: "materialCode", value: materialCodeInStorage});
        }

        var materialDescriptionInStorage = $.trim(mini.get("materialDescriptionInStorage").getValue());
        if (materialDescriptionInStorage) {
            queryParam.push({name: "materialDescription", value: materialDescriptionInStorage});
        }

        var responsibleUserInStorage = $.trim(mini.get("responsibleUserInStorage").getValue());
        if (responsibleUserInStorage) {
            queryParam.push({name: "responsibleUser", value: responsibleUserInStorage});
        }
        var responsibleDepInStorage = $.trim(mini.get("responsibleDepInStorage").getValue());
        if (responsibleDepInStorage) {
            queryParam.push({name: "responsibleDep", value: responsibleDepInStorage});
        }
        var inDateBeginInStorage = $.trim(mini.get("inDateBeginInStorage").getText());
        if (inDateBeginInStorage) {
            queryParam.push({name: "inDateBegin", value: inDateBeginInStorage});
        }
        var inDateEndInStorage = $.trim(mini.get("inDateEndInStorage").getText());
        if (inDateEndInStorage) {
            queryParam.push({name: "inDateEnd", value: inDateEndInStorage});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = inStorageItemListGridGrid.getPageIndex();
        data.pageSize = inStorageItemListGridGrid.getPageSize();
        data.sortField = inStorageItemListGridGrid.getSortField();
        data.sortOrder = inStorageItemListGridGrid.getSortOrder();
        inStorageItemListGridGrid.load(data);
    }
    //..
    function clearFormInStorageItem() {
        mini.get("businessNoInStorage").setValue("");
        mini.get("materialCodeInStorage").setValue("");
        mini.get("responsibleUserInStorage").setValue("");
        mini.get("responsibleDepInStorage").setValue("");
        mini.get("inDateBeginInStorage").setValue("");
        mini.get("inDateEndInStorage").setValue("");
        inStorageItemListGridGrid.load();
    }
    //..
    function selectInStorageItemOK() {
        var selectRows = inStorageItemListGridGrid.getSelecteds();
        if (selectRows.length > 0) {
            for (var i = 0, l = selectRows.length; i < l; i++) {
                var newRow = {}
                newRow.inStorageItemId = selectRows[i].id;
                newRow.materialCode = selectRows[i].materialCode;
                newRow.materialType = selectRows[i].materialType;
                newRow.businessNo = selectRows[i].businessNo;
                newRow.responsibleDep = selectRows[i].responsibleDep;
                newRow.responsibleUser = selectRows[i].responsibleUser;
                newRow.inDate = selectRows[i].inDate;
                newRow.reasonForStorage = selectRows[i].reasonForStorage;
                newRow.inQuantity = selectRows[i].inQuantity;
                newRow.untreatedQuantity = selectRows[i].untreatedQuantity;
                itemListGrid.addRow(newRow, itemListGrid.total - 1);
            }
        } else {
            mini.alert("请选至少选择择一条数据！");
            return;
        }
        selectInStorageItemHide();
    }
    //..
    function selectInStorageItemHide() {
        selectInStorageItemWindow.hide();
    }
    //..
    function deleteItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        delRowGrid("itemListGrid");
    }
    //..
    function itemFileRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        if (record.id && action == "edit") {
            cellHtml = '<span title="编辑" style="color:#409EFF;cursor: pointer;"' +
                'onclick="openFileWindow(\'' + record.id + '\',\'yanFaWuLiaoMingXi\',\'edit\',\'' + coverContent + '\')">编辑</span>';
        } else if (record.id && action == "detail") {
            cellHtml = '<span title="查看" style="color:#409EFF;cursor: pointer;"' +
                'onclick="openFileWindow(\'' + record.id + '\',\'yanFaWuLiaoMingXi\',\'detail\',\'' + coverContent + '\')">查看</span>';
        }
        return cellHtml;
    }
    //..
    function openFileWindow(businessId, businessType, action, coverContent) {
        mini.open({
            title: "文件列表",
            url: jsUseCtxPath + "/rdMaterialCommon/core/file/openFileWindow.do?businessId=" +
            businessId + "&businessType=" + businessType + "&action=" + action + "&coverContent=" + coverContent,
            width: 800,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
            }
        });
    }
    //..常规渲染
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
</body>
</html>
