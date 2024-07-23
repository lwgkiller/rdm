<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>合规性文件资料</title>
    <%--<%@include file="/commons/list.jsp" %>--%>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveStandard" class="mini-button" onclick="saveBusiness()"><spring:message code="page.topicStandardEdit.name" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.topicStandardEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="standardId" name="standardId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;"><spring:message code="page.topicStandardEdit.name2" /></caption>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.topicStandardEdit.name3" />:</td>
                    <td>
                        <input id="type" name = "type"
                               property="editor" class="mini-combobox"
                               style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.topicStandardEdit.name4" />..."
                               required="false" allowInput="false"
                               nullItemText="<spring:message code="page.topicStandardEdit.name4" />..."
                               onvaluechanged="typeChange"
                               data="[{'key' : '标准','value' : '标准'}
                                       ,{'key' : '其他','value' : '其他'}
                                  ]"
                        />
                    </td>

                    <td style="text-align: center;width: 20%"><spring:message code="page.topicStandardEdit.name5" />：</td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>

                </tr>

                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.topicStandardEdit.name6" /></td>
                    <td style="min-width:170px">
                        <input id="region" name="region" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.topicStandardEdit.name7" />:</td>
                    <td>
                        <input id = "standardIdNumber" name = "standardIdNumber"
                               textname="standardIdNumber"
                               style="width:98%;" property="editor"
                               class="mini-buttonedit" showClose="true"
                               onvaluechanged="numberChange"
                               oncloseclick="onSelectStandardCloseClick" allowInput="true"
                               onbuttonclick="selectStandard()"/>
                    </td>
                </tr>


                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.topicStandardEdit.name8" />：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="topicStandardName" name="standardName" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.topicStandardEdit.name9" />：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="year" name="year" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.topicStandardEdit.name10" />：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="categoryName" name="categoryName" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>

                </tr>


                <tr id="txxytm">
                    <td style="text-align: center;width: 14%;height:10px"><spring:message code="page.topicStandardEdit.name11" />：</td>
                    <td colspan="3" height="60px">
                        <div id="picToolBar" style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addPic" class="mini-button" onclick="addFile('${applyId}')"><spring:message code="page.topicStandardEdit.name12" /></a>
                            <span style="font-size: 14px;color:red"><spring:message code="page.topicStandardEdit.name13" /></span>
                        </div>

                        <div id="fileListGrid" class="mini-datagrid"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/topicStandard/fileList.do?applyId=${applyId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false"
                             allowAlternating="true"
                             style="height:150px;"
                        >
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.topicStandardEdit.name14" /></div>
                                <div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.topicStandardEdit.name15" /></div>
                                <div field="fileDesc" width="80" headerAlign="center" align="center"><spring:message code="page.topicStandardEdit.name16" />
                                </div>
                                <div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.topicStandardEdit.name17" /></div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRenderer"><spring:message code="page.topicStandardEdit.name18" />
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>

        <p style="font-size: 16px;font-weight: bold;margin-top: 20px"><spring:message code="page.topicStandardEdit.name19" /></p>


        <div id="topicStandardGrid" class="mini-datagrid" allowResize="false" style="height:340px"
             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
        >
            <div property="columns">
                <div type="checkcolumn" width="50"></div>
                <div type="indexcolumn" width="45" headerAlign="center" align="center"><spring:message code="page.topicStandardEdit.name14" /></div>
                <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                     renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.topicStandardEdit.name18" />
                </div>
                <div field="topicName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.topicStandardEdit.name20" /></div>
                <div field="topicId" headerAlign="center" align="center" allowSort="false"><spring:message code="page.topicStandardEdit.name21" /></div>
                <div field="topicTextName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.topicStandardEdit.name22" /></div>
                <div field="topicType" headerAlign="center" align="center" allowSort="false"><spring:message code="page.topicStandardEdit.name23" /></div>
                <div field="region" headerAlign="center" align="center" allowSort="false"><spring:message code="page.topicStandardEdit.name24" /></div>
                <div field="productLine" headerAlign="center" align="center" allowSort="false"><spring:message code="page.topicStandardEdit.name25" /></div>
                <div field="productSeries" headerAlign="center" align="center" allowSort="false"><spring:message code="page.topicStandardEdit.name26" /></div>
                <div field="productSettings" headerAlign="center" align="center" allowSort="false"><spring:message code="page.topicStandardEdit.name27" /></div>
                <div field="remark" headerAlign="center" align="center" allowSort="false"><spring:message code="page.topicStandardEdit.name28" /></div>
                <div field="version" headerAlign="center" align="center" allowSort="false"><spring:message code="page.topicStandardEdit.name29" /></div>
                <div field="versionStatus" headerAlign="center" align="center" allowSort="false"
                    renderer="onHistoryRenderer"><spring:message code="page.topicStandardEdit.name30" /></div>
                <div field="status" width="50" headerAlign="center" align="center" allowSort="true"
                     renderer="onStatusRenderer"><spring:message code="page.topicStandardEdit.name31" />
                </div>
                <div field="creatorName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.topicStandardEdit.name32" /></div>
                <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true"><spring:message code="page.topicStandardEdit.name33" />
                </div>

            </div>
        </div>

    </div>

</div>

<div id="selectStandardWindow" title="<spring:message code="page.topicStandardEdit.name34" />" class="mini-window" style="width:1080px;height:600px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false"
>
    <input id="parentInputScene" style="display: none"/>
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                          style="width:auto"><spring:message code="page.topicStandardEdit.name35" />: </span><input
                            class="mini-textbox" id="standardNumber" onenter="searchStandard"></li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.topicStandardEdit.name36" />: </span><input
                            class="mini-textbox" id="standardName" onenter="searchStandard"></li>


                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="searchStandard()"><spring:message code="page.topicStandardEdit.name37" /></a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                           onclick="clearSearchStandard()"><spring:message code="page.topicStandardEdit.name38" /></a>
                        <div style="display: inline-block" class="separator"></div>
                    </li>

                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" multiSelect="true" sizeList="[20,50,100,200]" pageSize="20"
             onrowdblclick="onRowDblClick"
             allowAlternating="true" pagerButtons="#pagerButtons" showColumnsMenu="true"
             url="${ctxPath}/standardManager/core/standard/queryList.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="id" name="id" width="100" headerAlign="center" align="center"
                     allowSort="true" visible="false"><spring:message code="page.topicStandardEdit.name39" />
                </div>
                <div field="systemName" name="systemName" sortField="systemName" width="100" headerAlign="center"
                     align="center"
                     allowSort="true"><spring:message code="page.topicStandardEdit.name40" />
                </div>
                <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                     allowSort="true" hideable="true"><spring:message code="page.topicStandardEdit.name10" />
                </div>
                <div field="banci" name="banci" sortField="banci" width="40" headerAlign="center"
                     align="center" allowSort="true"><spring:message code="page.topicStandardEdit.name29" />
                </div>
                <div field="standardNumber" sortField="standardNumber" width="130" headerAlign="center"
                     align="center" allowSort="true" hideable="true"><spring:message code="page.topicStandardEdit.name42" />
                </div>
                <div field="standardName" sortField="standardName" width="200" headerAlign="center" align="left"
                     allowSort="true" hideable="true"><spring:message code="page.topicStandardEdit.name43" />
                </div>
                <div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.topicStandardEdit.name44" />
                </div>
                <div field="belongFieldNames" headerAlign="center" width="110" headerAlign="center" align="left"
                     allowSort="false"><spring:message code="page.topicStandardEdit.name45" />
                </div>
                <div field="cbbh" name="cbbh" headerAlign="center" width="80" headerAlign="center" align="center"
                     allowSort="false"><spring:message code="page.topicStandardEdit.name46" />
                </div>
                <div field="yzxcd" name="yzxcd" headerAlign="center" width="120" headerAlign="center" align="center"
                     allowSort="false"><spring:message code="page.topicStandardEdit.name47" />
                </div>
                <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center"
                     align="center" hideable="true"
                     allowSort="true" renderer="statusRenderer"><spring:message code="page.topicStandardEdit.name31" />
                </div>
                <div field="publisherName" sortField="publisherName" align="center"
                     width="60" headerAlign="center" allowSort="true"><spring:message code="page.topicStandardEdit.name48" />
                </div>
                <div field="sendSupplier" name="sendSupplier" sortField="sendSupplier" align="center"
                     width="60" headerAlign="center" allowSort="true" renderer="sendSupplierRender"><spring:message code="page.topicStandardEdit.name49" />
                </div>
                <div field="creator" name="creator" align="center" width="60" headerAlign="center" allowSort="false"><spring:message code="page.topicStandardEdit.name50" />
                </div>
                <div field="publishTime" sortField="publishTime" dateFormat="yyyy-MM-dd" align="center"
                     width="100" headerAlign="center" allowSort="true"><spring:message code="page.topicStandardEdit.name51" />
                </div>
                <div field="yearDoCheckStatus" name="yearDoCheckStatus" align="center" width="80" headerAlign="center"
                     renderer="doCheckStatusRender"><spring:message code="page.topicStandardEdit.name52" />
                </div>

            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.topicStandardEdit.name53" />" onclick="selectStandardOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.topicStandardEdit.name54" />" onclick="selectStandardHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var applyId = "${applyId}";
    var formBusiness = new mini.Form("#formBusiness");
    var selectStandardWindow = mini.get("selectStandardWindow");
    var standardListGrid = mini.get("standardListGrid");
    var topicStandardGrid = mini.get("topicStandardGrid");
    var fileListGrid = mini.get("fileListGrid");

    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    //..
    $(function () {

        // if (applyId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/topicStandard/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                formBusiness.setData(json);
                loadStandardInfo();
            });

        // }
        if (action == 'detail') {
            formBusiness.setEnabled(false);
            mini.get("saveStandard").setEnabled(false);
            fileListGrid.load();
        }

    });



    function saveBusiness() {
        // var formValid = validBusiness();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }

        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }

        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/topicStandard/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = topicStandardEdit_name;
                    } else {
                        message = topicStandardEdit_name1 + data.message;
                    }

                    mini.alert(message, topicStandardEdit_name2, function () {
                        var url = jsUseCtxPath + "/serviceEngineering/core/topicStandard/EditPage.do?applyId=" + data.data + '&action=edit';
                        window.location.href = url;
                    });
                }
            }
        });
    }
    //..
    function checkEditRequired(formData) {
        if (!formData) {
            mini.alert(topicStandardEdit_name3);
            return false;
        }
        if (!$.trim(formData.partsType)) {
            mini.alert(topicStandardEdit_name4);
            return false;
        }
        if (!$.trim(formData.partsModel)) {
            mini.alert(topicStandardEdit_name11);
            return false;
        }
        // if (!$.trim(formData.fileName)) {
        //     mini.alert('文件不能为空！');
        //     return false;
        // }


        return true;
    }

    //..
    function uploadFile() {
        $("#inputFile").click();
    }

    //..
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            mini.get("fileName").setValue(fileList[0].name);

        }
    }

    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }



    // 标准相关
    function selectStandard() {
        // $("#parentInputScene").val(inputScene);
        debugger;
        var type = mini.get("type").getValue();
        if (type != "标准") {
            mini.alert(topicStandardEdit_name5);
            return;
        }
        selectStandardWindow.show();
        searchStandard();
    }

    //查询标准
    function searchStandard() {
        var queryParam = [];
        //其他筛选条件

        var standardNumber = $.trim(mini.get("standardNumber").getValue());
        if (standardNumber) {
            queryParam.push({name: "standardNumber", value: standardNumber});
        }
        var standardName = $.trim(mini.get("standardName").getValue());
        if (standardName) {
            queryParam.push({name: "standardName", value: standardName});
        }
        queryParam.push({name: "instStatus", value: "RUNNING"});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = standardListGrid.getPageIndex();
        data.pageSize = standardListGrid.getPageSize();
        data.sortField = standardListGrid.getSortField();
        data.sortOrder = standardListGrid.getSortOrder();
        //查询
        standardListGrid.load(data);
    }

    function clearSearchStandard() {
        mini.get("standardName").setValue("");
        mini.get("standardNumber").setValue("");
        searchStandard();
    }


    function onRowDblClick() {
        selectStandardOK();
    }

    function selectStandardOK() {
        var selectRow = standardListGrid.getSelected();

        mini.get("topicStandardName").setValue(selectRow.standardName);
        mini.get("year").setValue(selectRow.banci);
        mini.get("standardId").setValue(selectRow.id);
        mini.get("standardIdNumber").setValue(selectRow.standardNumber);
        mini.get("standardIdNumber").setText(selectRow.standardNumber);
        mini.get("categoryName").setValue(selectRow.categoryName);
        selectStandardHide();
        loadStandardInfo();

    }

    function selectStandardHide() {
        selectStandardWindow.hide();
    }

    function onSelectStandardCloseClick(e) {
        var objrow = standardGrid.getSelected();
        standardGrid.updateRow(objrow,
            {
                standardIdNumber: ""
            });
    }

    function typeChange(e) {
        if (e.value == "标准") {
            mini.get("topicStandardName").setEnabled(false);
            mini.get("standardIdNumber").setAllowInput(false);
            mini.get("year").setEnabled(false);
            mini.get("categoryName").setEnabled(false);
        }
        if (e.value == "其他") {
            mini.get("topicStandardName").setEnabled(true);
            mini.get("standardIdNumber").setAllowInput(true);
            mini.get("year").setEnabled(true);
            mini.get("categoryName").setEnabled(true);
        }

    }

    function numberChange(e) {
        if (e.value == "") {
            return;
        }

        loadStandardInfo()
    }

    function loadStandardInfo() {
        debugger;
        var standardIdNumber = mini.get("standardIdNumber").getValue();
        var url="${ctxPath}/serviceEngineering/core/topicStandard/getStandardTopicInfo.do?standardIdNumber="+standardIdNumber
        topicStandardGrid.setUrl(url);
        topicStandardGrid.load();
    }


    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;

        var s = '<span  title=' + topicStandardEdit_name6 + ' style="color:#409EFF" onclick="topicDetail(\'' + businessId + '\')">' + topicStandardEdit_name6 + '</span>';
        return s;
    }

    function topicDetail(applyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/topic/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
    }

    function addFile(applyId) {
        debugger;
        mini.open({
            title: topicStandardEdit_name7,
            url: jsUseCtxPath + "/serviceEngineering/core/topicStandard/openUploadWindow.do",
            width: 900,
            height: 350,
            showModal: true,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var passParams = {};
                passParams.applyId = applyId;
                var data = {passParams: passParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                fileListGrid.load();
            }
        });
    }

    function operationRenderer(e) {
        var record = e.record;
        //预览、下载和删除
        if (!record.id) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.applyId, coverContent);
        var downloadUrl = '/serviceEngineering/core/topicStandard/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + topicStandardEdit_name8 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">' + topicStandardEdit_name8 + '</span>';

        if (record && action == "edit" && currentUserId == record.CREATE_BY_) {
            var deleteUrl = "/serviceEngineering/core/topicStandard/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + topicStandardEdit_name9 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">' + topicStandardEdit_name9 + '</span>';
        }
        return cellHtml;
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var s = '';
        if (fileName == "") {
            return s;
        }
        var fileType = getFileType(fileName);

        if (fileType == 'other') {
            s = '<span  title=' + topicStandardEdit_name10 + ' style="color: silver" >' + topicStandardEdit_name10 + '</span>';
        } else {
            var url = '/serviceEngineering/core/topicStandard/preview.do?fileType=' + fileType;
            s = '<span  title=' + topicStandardEdit_name10 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">' + topicStandardEdit_name10 + '</span>';
        }

        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFT', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '评审中', 'css': 'green'},
            {'key': 'READY', 'value': '待评审', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '已评审', 'css': 'blue'},
        ];

        return $.formatItemValue(arr, status);
    }

    function onHistoryRenderer(e) {
        var record = e.record;
        var versionStatus = record.versionStatus;

        var arr = [{'key': 'current', 'value': '有效', 'css': 'green'},
            {'key': 'history', 'value': '历史版本', 'css': 'red'},

        ];

        return $.formatItemValue(arr, versionStatus);
    }
    // function render(e) {
    //     if (e.value != null && e.value != "") {
    //         var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
    //         return html;
    //     }
    // }


</script>
<redxun:gridScript gridId="topicStandardGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
