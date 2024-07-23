<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>安全信息库归档</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveTopic" class="mini-button" onclick="saveBusiness()"><spring:message code="page.hgxSafeArchiveEdit.name" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.hgxSafeArchiveEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;"><spring:message code="page.hgxSafeArchiveEdit.name2" /></caption>
                <tr>
                    <%--<td style="text-align: center;width: 20%">零部件类型：<span style="color:red">*</span></td>--%>

                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxSafeArchiveEdit.name3" />：</td>
                    <td style="min-width:170px">
                        <input id="safeCode" name="safeCode" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>

                    <td style="text-align: center;width: 15%"><spring:message code="page.hgxSafeArchiveEdit.name4" />：</td>
                    <td colspan="2">
                        <input class="mini-textbox" style="width:70%;float: left" id="fileName" name="fileName"
                               enabled="false"/>
                        <%--<input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/pdf"/>--%>
                        <input id="inputFile"
                               style="display:none;"
                               type="file" onchange="getSelectFile()"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile"><spring:message code="page.hgxSafeArchiveEdit.name5" /></a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile"><spring:message code="page.hgxSafeArchiveEdit.name6" /></a>
                    </td>

                </tr>


                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxSafeArchiveEdit.name7" />：</td>
                    <td style="min-width:170px">
                        <input id="safeType" name="safeType" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.hgxSafeArchiveEdit.name8" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.hgxSafeArchiveEdit.name8" />..."
                               data="[{'key' : '警告信息','value' : '警告信息'}
                                    ,{'key' : '安全标志','value' : '安全标志'}
                                    ,{'key' : '安全注意事项','value' : '安全注意事项'}
                                    ,{'key' : '其他','value' : '其他'}]"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.hgxSafeArchiveEdit.name9" />:</td>
                    <td>
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxSafeArchiveEdit.name10" />：</td>
                    <td style="min-width:170px">
                        <input id="region" name="region" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.hgxSafeArchiveEdit.name8" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.hgxSafeArchiveEdit.name8" />..."
                               data="[{'key' : '履挖','value' : '履挖'}
                                    ,{'key' : '轮挖','value' : '轮挖'}
                                    ,{'key' : '通用','value' : '通用'}
                                    ]"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.hgxSafeArchiveEdit.name11" />:</td>
                    <td>
                        <input id="version" name="version" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>

                </tr>



                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.hgxSafeArchiveEdit.name12" /></td>
                    <td colspan="3">
                        <input id="remark" name="remark" class="mini-textbox" style="width:98%;" enabled="true"/>
                </tr>
            </table>
        </form>

        <p style="font-size: 16px;font-weight: bold;margin-top: 20px"><spring:message code="page.hgxSafeArchiveEdit.name13" /></p>

        <div class="mini-toolbar" style="margin-bottom: 5px" id="topicListToolBar">
            <a class="mini-button" id="topicAddRow" plain="true" onclick="addTopicRow"><spring:message code="page.hgxSafeArchiveEdit.name14" /></a>
            <a class="mini-button btn-red" id="topicRemoveRow" plain="true" onclick="removeTopicRow"><spring:message code="page.hgxSafeArchiveEdit.name15" /></a>
        </div>


        <div id="topicGrid" class="mini-datagrid" allowResize="false" style="height:340px"
             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
             url="${ctxPath}/serviceEngineering/core/safeArchive/topicList.do?applyId=${businessId}"
             autoload = "true"
        >
            <div property="columns">
                <div type="checkcolumn" width="50"></div>
                <div type="indexcolumn" width="45" headerAlign="center" align="center"><spring:message code="page.hgxSafeArchiveEdit.name16" /></div>
                <div field="topicName" name="relTopicId" width="120" headerAlign="center"
                     align="center"
                ><spring:message code="page.hgxSafeArchiveEdit.name17" />
                    <%--<input property="editor" class="mini-textbox"/>--%>

                    <input textname="topicName" style="width:98%;" property="editor"
                           class="mini-buttonedit" showClose="true"
                           oncloseclick="onSelectTopicCloseClick" allowInput="false"
                           onbuttonclick="selectTopic()"/>

                </div>
                <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                     renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.hgxSafeArchiveEdit.name18" />
                </div>
                <div field="topicId" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name19" /></div>
                <div field="topicTextName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name20" /></div>
                <div field="topicType" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name21" /></div>
                <div field="region" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name22" /></div>
                <div field="productLine" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name23" /></div>
                <div field="productSeries" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name24" /></div>
                <div field="productSettings" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name25" /></div>
                <div field="remark" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name12" /></div>
                <div field="version" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name11" /></div>
                <div field="versionStatus" headerAlign="center" align="center" allowSort="false"
                     renderer="onHistoryRenderer"><spring:message code="page.hgxSafeArchiveEdit.name26" /></div>
                <div field="status" width="50" headerAlign="center" align="center" allowSort="true"
                     renderer="onStatusRenderer"><spring:message code="page.hgxSafeArchiveEdit.name27" />
                </div>
                <div field="creatorName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name28" /></div>
                <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true"><spring:message code="page.hgxSafeArchiveEdit.name29" />
                </div>

            </div>
        </div>


        <p style="font-size: 16px;font-weight: bold;margin-top: 20px"><spring:message code="page.hgxSafeArchiveEdit.name30" /></p>

        <div class="mini-toolbar" style="margin-bottom: 5px" id="standardListToolBar">
            <a class="mini-button" id="standardAddRow" plain="true" onclick="addStandardRow"><spring:message code="page.hgxSafeArchiveEdit.name14" /></a>
            <a class="mini-button btn-red" id="standardRemoveRow" plain="true" onclick="removeStandardRow"><spring:message code="page.hgxSafeArchiveEdit.name15" /></a>
        </div>


        <div id="standardGrid" class="mini-datagrid" allowResize="false" style="height:340px"
             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
             url="${ctxPath}/serviceEngineering/core/safeArchive/standardList.do?applyId=${businessId}"
             autoload = "true"
        >
            <div property="columns">
                <div type="checkcolumn" width="50"></div>
                <div type="indexcolumn" width="45" headerAlign="center" align="center"><spring:message code="page.hgxSafeArchiveEdit.name16" /></div>
                <div field="standardIdNumber" name="relStandardId" width="120" headerAlign="center"
                     align="center"
                ><spring:message code="page.hgxSafeArchiveEdit.name31" />
                    <%--<input property="editor" class="mini-textbox"/>--%>

                    <input textname="topicName" style="width:98%;" property="editor"
                           class="mini-buttonedit" showClose="true"
                           oncloseclick="onSelectStandardCloseClick" allowInput="false"
                           onbuttonclick="selectStandard()"/>

                </div>
                <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                     renderer="onActionRenderer2" cellStyle="padding:0;"><spring:message code="page.hgxSafeArchiveEdit.name18" />
                </div>
                <div field="categoryName" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name32" />
                </div>

                <%--<div field="standardIdNumber" width="120" headerAlign="center" align="center" renderer="render"--%>
                     <%--allowSort="true">--%>
                    <%--文件编号--%>
                <%--</div>--%>
                <div field="standardName" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name4" />
                </div>
                <div field="year" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name33" />
                </div>
                <div field="region" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name10" />
                </div>
                <div field="isApplied" width="80" headerAlign="center" align="center" renderer="onGuanbiaoRenderer"
                     allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name34" />
                </div>
                <div field="creatorName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name35" />
                </div>
                <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name29" />
                </div>

            </div>
        </div>

    </div>

</div>

<div id="selectTopicWindow" title="<spring:message code="page.hgxSafeArchiveEdit.name36" />" class="mini-window" style="width:1080px;height:600px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false"
>
    <input id="parentInputScene" style="display: none"/>
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                          style="width:auto"><spring:message code="page.hgxSafeArchiveEdit.name19" />: </span><input
                            class="mini-textbox" id="topicId" onenter="topicId"></li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.hgxSafeArchiveEdit.name37" />: </span><input
                            class="mini-textbox" id="topicName" onenter="searchTopic"></li>


                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="searchTopic()"><spring:message code="page.hgxSafeArchiveEdit.name38" /></a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                           onclick="clearSearchTopic()"><spring:message code="page.hgxSafeArchiveEdit.name39" /></a>
                        <div style="display: inline-block" class="separator"></div>
                    </li>

                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="topicListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onlyCheckSelection="true"
             url="${ctxPath}/serviceEngineering/core/topic/applyList.do" idField="id"
             multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
             pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div type="indexcolumn" width="45" headerAlign="center" align="center"><spring:message code="page.hgxSafeArchiveEdit.name16" /></div>
                <div name="action" cellCls="actionIcons" width="100" headerA="center" align="center"
                     renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.hgxSafeArchiveEdit.name18" />
                </div>
                <%--<div field="creatorName" headerAlign="center" align="center" allowSort="false">创建人</div>--%>
                <div field="topicName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name40" /></div>
                <div field="topicId" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name19" /></div>
                <div field="topicTextName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name20" /></div>
                <div field="topicType" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name21" /></div>
                <div field="region" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name22" /></div>
                <div field="productLine" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name23" /></div>
                <div field="productSeries" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name24" /></div>
                <div field="productSettings" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name25" /></div>
                <div field="remark" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name12" /></div>
                <div field="version" headerAlign="center" align="center" allowSort="true"><spring:message code="page.hgxSafeArchiveEdit.name11" /></div>
                <div field="versionStatus" headerAlign="center" align="center" allowSort="true"
                     renderer="onHistoryRenderer"><spring:message code="page.hgxSafeArchiveEdit.name26" />
                </div>
                <div field="status" width="50" headerAlign="center" align="center" allowSort="true"
                     renderer="onStatusRenderer"><spring:message code="page.hgxSafeArchiveEdit.name27" />
                </div>
                <div field="creatorName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.hgxSafeArchiveEdit.name28" /></div>
                <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true"><spring:message code="page.hgxSafeArchiveEdit.name29" />
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.hgxSafeArchiveEdit.name41" />" onclick="selectTopicOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.hgxSafeArchiveEdit.name42" />" onclick="selectTopicHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>


<div id="selectStandardWindow" title="<spring:message code="page.hgxSafeArchiveEdit.name36" />" class="mini-window" style="width:1080px;height:600px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false"
>
    <input id="parentInputScene2" style="display: none"/>
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchForm2" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                          style="width:auto"><spring:message code="page.hgxSafeArchiveEdit.name43" />: </span><input
                            class="mini-textbox" id="standardIdNumber" onenter="standardIdNumber"></li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.hgxSafeArchiveEdit.name4" />: </span><input
                            class="mini-textbox" id="standardName" onenter="standardName"></li>


                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="searchStandard()"><spring:message code="page.hgxSafeArchiveEdit.name38" /></a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                           onclick="clearSearchStandard()"><spring:message code="page.hgxSafeArchiveEdit.name39" /></a>
                        <div style="display: inline-block" class="separator"></div>
                    </li>

                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onlyCheckSelection="true"
             url="${ctxPath}/serviceEngineering/core/topicStandard/applyList.do">
             idField="id"
             multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
             pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div type="indexcolumn" width="45" headerAlign="center" align="center"><spring:message code="page.hgxSafeArchiveEdit.name16" /></div>
                <div field="categoryName" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name32" />
                </div>

                <div field="standardIdNumber" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name43" />
                </div>
                <div field="standardName" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name4" />
                </div>
                <div field="year" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name33" />
                </div>
                <div field="region" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name10" />
                </div>
                <div field="isApplied" width="80" headerAlign="center" align="center" renderer="onGuanbiaoRenderer"
                     allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name34" />
                </div>
                <div field="creatorName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name35" />
                </div>
                <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.hgxSafeArchiveEdit.name29" />
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.hgxSafeArchiveEdit.name41" />" onclick="selectStandardOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.hgxSafeArchiveEdit.name42" />" onclick="selectStandardHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>




<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var obj =${obj};
    var formBusiness = new mini.Form("#formBusiness");
    var selectTopicWindow = mini.get("selectTopicWindow");
    var selectStandardWindow = mini.get("selectStandardWindow");
    var topicGrid = mini.get("topicGrid");
    var topicListGrid = mini.get("topicListGrid");
    var standardGrid = mini.get("standardGrid");
    var standardListGrid = mini.get("standardListGrid");

    var businessId = "${businessId}";
    //..
    $(function () {

        formBusiness.setData(obj);
        if (action == 'detail') {
            formBusiness.setEnabled(false);
            mini.get("saveTopic").setEnabled(false);
            mini.get("uploadFileBtn").setEnabled(false);
            mini.get("clearFileBtn").setEnabled(false);
            mini.get("topicListToolBar").hide();
            mini.get("standardListToolBar").hide();
        }
    });

    //..
    function saveBusiness() {
        if (action == "copy") {
            mini.get("id").setValue("");
        }
        var formData = formBusiness.getData();
        // var checkResult = checkEditRequired(formData);
        // if (!checkResult) {
        //     return;
        // }

        if (action == "copy") {
            var standardGridData = standardGrid.getData();
            for (var index = 0; index < standardGridData.length; index++) {
                standardGridData[index].id = "";
            }
            formData.standardGrid = standardGridData;

            var topicGridData = topicGrid.getData();
            for (var index = 0; index < topicGridData.length; index++) {
                topicGridData[index].id = "";
            }
            formData.topicGrid = topicGridData;
        }
        else {
            if (standardGrid.getChanges().length > 0) {
                formData.standardGrid = standardGrid.getChanges();
            }

            if (topicGrid.getChanges().length > 0) {
                formData.topicGrid = topicGrid.getChanges();
            }
        }

        // formData.topicGrid =topicGrid.getChanges()
        // formData.standardGrid =standardGrid.getChanges()
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        //XMLHttpRequest方式上传表单
        var xhr = false;
        try {
            //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
            xhr = new XMLHttpRequest();
        } catch (e) {
            //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
            xhr = ActiveXobject("Msxml12.XMLHTTP");
        }

        if (xhr.upload) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        if (xhr.responseText) {
                            var returnObj = JSON.parse(xhr.responseText);
                            var message = '';
                            if (returnObj.message) {
                                message = returnObj.message;
                            }
                            mini.alert(message, hgxSafeArchiveEdit_name, function (action) {
                                if (returnObj.success) {
                                    mini.get("id").setValue(returnObj.id);
                                }
                                var url = jsUseCtxPath + "/serviceEngineering/core/safeArchive/EditPage.do?id=" + returnObj.id + '&action=edit';
                                window.location.href = url;
                                // CloseWindow();
                            });
                        }
                    }
                }
            };

            //开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/safeArchive/saveBusiness.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            if (formData) {
                debugger;
                for (key in formData) {
                    if (key == "topicGrid"||key == "standardGrid") {
                        fd.append(key, mini.encode(formData[key]));
                    } else {
                        fd.append(key, formData[key]);
                    }
                }
            }
            fd.append('businessFile', file);
            xhr.send(fd);
        }
    }

    //..
    function checkEditRequired(formData) {
        if (!formData) {
            mini.alert(hgxSafeArchiveEdit_name1);
            return false;
        }
        if (!$.trim(formData.partsType)) {
            mini.alert(hgxSafeArchiveEdit_name2);
            return false;
        }
        if (!$.trim(formData.partsModel)) {
            mini.alert(hgxSafeArchiveEdit_name3);
            return false;
        }



        return true;
    }

    //..
    function uploadFile() {
        $("#inputFile").click();
    }

    //..
    function getSelectFile() {
        debugger;
        //todo 上传的文件名格式验证需要是xxx-xxx.xxx
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            mini.get("fileName").setValue(fileList[0].name);
            var code = fileList[0].name.split('.');
            mini.get("picCode").setValue(code[0]);
            // if (fileNameSuffix == 'pdf') {
            //     mini.get("fileName").setValue(fileList[0].name);
            // }
            // else {
            //     clearUploadFile();
            //     mini.alert('请上传pdf文件！');
            // }
        }
    }

    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }


    function addTopicRow() {
        var row = {}
        topicGrid.addRow(row);
    }


    function removeTopicRow() {
        var selecteds = topicGrid.getSelecteds();
        topicGrid.removeRows(selecteds);
    }


    // 标准相关
    function selectTopic() {
        selectTopicWindow.show();
        searchTopic();
    }

    //查询标准
    function searchTopic() {
        var queryParam = [];
        //其他筛选条件

        var topicId = $.trim(mini.get("topicId").getValue());
        if (topicId) {
            queryParam.push({name: "topicId", value: topicId});
        }
        var topicName = $.trim(mini.get("topicName").getValue());
        if (topicName) {
            queryParam.push({name: "topicName", value: topicName});
        }
        // queryParam.push({name: "instStatus", value: "RUNNING"});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = topicListGrid.getPageIndex();
        data.pageSize = topicListGrid.getPageSize();
        data.sortField = topicListGrid.getSortField();
        data.sortOrder = topicListGrid.getSortOrder();
        //查询
        topicListGrid.load(data);
    }

    function clearSearchTopic() {
        mini.get("topicId").setValue("");
        mini.get("topicName").setValue("");
        searchTopic();
    }


    function onRowDblClick() {
        selectTopicOK();
    }

    function selectTopicOK() {
        var selectRow = topicListGrid.getSelected();
        // 这里要updateRow 保存 只保存id和name


        var objrow = topicGrid.getSelected();
        topicGrid.updateRow(objrow,
            {
                topicName: selectRow.topicName,
                relTopicId: selectRow.id,
            });
        selectTopicHide();
        // loadTopicInfo();

    }

    function selectTopicHide() {
        selectTopicWindow.hide();
    }

    function onSelectTopicCloseClick(e) {
        var objrow = standardGrid.getSelected();
        standardGrid.updateRow(objrow,
            {
                standardIdNumber: ""
            });
    }


    //关联合规性文件


    function addStandardRow() {
        var row = {}
        standardGrid.addRow(row);
    }


    function removeStandardRow() {
        var selecteds = standardGrid.getSelecteds();
        standardGrid.removeRows(selecteds);
    }

    function selectStandard() {

        selectStandardWindow.show();
        searchStandard();
    }

    //查询标准
    function searchStandard() {
        var queryParam = [];
        //其他筛选条件

        var standardIdNumber = $.trim(mini.get("standardIdNumber").getValue());
        if (standardIdNumber) {
            queryParam.push({name: "standardIdNumber", value: standardIdNumber});
        }
        var standardName = $.trim(mini.get("standardName").getValue());
        if (standardName) {
            queryParam.push({name: "standardName", value: standardName});
        }
        // queryParam.push({name: "instStatus", value: "RUNNING"});
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
        mini.get("standardIdNumber").setValue("");
        searchStandard();
    }


    function onRowDblClick() {
        selectStandardOK();
    }

    function selectStandardOK() {
        var selectRow = standardListGrid.getSelected();

        var objrow = standardGrid.getSelected();
        standardGrid.updateRow(objrow,
            {
                standardIdNumber: selectRow.standardIdNumber,
                relStandardId: selectRow.id,

            });

        selectStandardHide();

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


    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.relTopicId;

        var s = '<span  title=' + hgxSafeArchiveEdit_name4 + ' style="color:#409EFF" onclick="topicDetail(\'' + businessId + '\')">' + hgxSafeArchiveEdit_name4 + '</span>';
        return s;
    }
    function onActionRenderer2(e) {
        var record = e.record;
        var businessId = record.relStandardId;

        var s = '<span  title=' + hgxSafeArchiveEdit_name4 + ' style="color:#409EFF" onclick="standardDetail(\'' + businessId + '\')">' + hgxSafeArchiveEdit_name4 + '</span>';
        return s;
    }

    function topicDetail(applyId) {
        debugger;
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/topic/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
    }

    function standardDetail(applyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/topicStandard/EditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
    }

    function onHistoryRenderer(e) {
        var record = e.record;
        var versionStatus = record.versionStatus;

        var arr = [{'key': 'current', 'value': '有效', 'css': 'green'},
            {'key': 'history', 'value': '历史版本', 'css': 'red'},

        ];

        return $.formatItemValue(arr, versionStatus);
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

    function onGuanbiaoRenderer(e) {
        var record = e.record;
        var isApplied = record.isApplied;
        var arr = [{'key': '贯标中', 'value': '贯标中', 'css': 'green'},
            {'key': '未贯标', 'value': '未贯标', 'css': 'red'},

        ];
        if (record.isComplete == '1') {
            arr = [{'key': '贯标中', 'value': '已贯标', 'css': 'blue'},]
        }


        return $.formatItemValue(arr, isApplied);
    }

</script>
</body>
</html>
