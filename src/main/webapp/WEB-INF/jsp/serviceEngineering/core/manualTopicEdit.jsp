<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>手册Topic</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/embedsoft/cxUtil.js?version=${static_res_version}" type="text/javascript"></script>


</head>
<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" onclick="saveBusiness()"><spring:message code="page.manualTopicEdit.name" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.manualTopicEdit.name1" /></a>
    </div>
</div>


<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="topicForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="orgId" name="orgId"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 20px"><spring:message code="page.manualTopicEdit.name2" /></p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">


                <td style="text-align: center;width: 20%"><spring:message code="page.manualTopicEdit.name3" />：<span style="color:red">*</span></td>
                <td style="min-width:170px">
                    <input id="topicName" name="topicName" class="mini-textbox" style="width:98%;"
                    />
                </td>


                <td style="text-align: center;width: 20%"><spring:message code="page.manualTopicEdit.name4" />：<span style="color:red">*</span></td>
                <td style="min-width:170px">
                    <input id="topicId" name="topicId" class="mini-textbox" style="width:98%;"/>
                </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.manualTopicEdit.name5" />：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="topicTextName" name="topicTextName" class="mini-textbox" style="width:98%;"
                        />
                    </td>


                    <td style="text-align: center;width: 20%"><spring:message code="page.manualTopicEdit.name6" />：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="topicType" name="topicType"
                               property="editor" class="mini-combobox"
                               style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.manualTopicEdit.name7" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.manualTopicEdit.name7" />..."
                               data="[{'key' : '模块','value' : '模块'}
                                       ,{'key' : '模板','value' : '模板'}
                                  ]"
                        />
                    </td>
                </tr>




                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.manualTopicEdit.name8" />：</td>
                    <td style="min-width:170px">
                        <input id="productLine" name="productLine" class="mini-combobox" style="width:98%"
                               emptyText="<spring:message code="page.manualTopicEdit.name7" />..."
                               showNullItem="true"
                               multiSelect="true"
                               nullItemText="<spring:message code="page.manualTopicEdit.name7" />..."
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringProductLine"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.manualTopicEdit.name9" />：</td>
                    <td style="min-width:170px">
                        <input id="productSeries" name="productSeries" class="mini-combobox" style="width:98%"
                               emptyText="<spring:message code="page.manualTopicEdit.name7" />..."
                               showNullItem="true"
                               multiSelect="true"
                               nullItemText="<spring:message code="page.manualTopicEdit.name7" />..."
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringProductSerie"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.manualTopicEdit.name10" />：</td>
                    <td style="min-width:170px">
                        <input id="region" name="region" class="mini-combobox" style="width:98%"
                               emptyText="<spring:message code="page.manualTopicEdit.name7" />..."
                               showNullItem="true"
                               multiSelect="true"
                               nullItemText="<spring:message code="page.manualTopicEdit.name7" />..."
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringRegion"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.manualTopicEdit.name11" />：</td>
                    <td style="min-width:170px">
                        <%--<input id="productSettings" name="productSettings" class="mini-textbox" style="width:98%;"/>--%>
                        <input id="productSettings" name="productSettings" class="mini-combobox" style="width:98%"
                               emptyText="<spring:message code="page.manualTopicEdit.name7" />..."
                               showNullItem="true"
                               multiSelect="true"
                               nullItemText="<spring:message code="page.manualTopicEdit.name7" />..."
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringProductSettings"
                               valueField="key" textField="value"/>
                    </td>
                </tr>



                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.manualTopicEdit.name12" />：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="version" name="version" class="mini-textbox" style="width:98%;"
                        />

                    </td>


                    <td style="text-align: center;width: 20%"><spring:message code="page.manualTopicEdit.name13" />：</td>
                    <td style="min-width:170px">
                        <%--<input id="versionStatus" name="versionStatus" class="mini-textbox" style="width:98%;"/>--%>
                        <input id="versionStatus" name="versionStatus"
                               class="mini-combobox"
                               style="width:98%;"
                               textField="text" valueField="id" value="current"
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.manualTopicEdit.name7" />..."
                               data="[{'id' : 'history','text' : '历史版本'}
                                       ,{'id' : 'current','text' : '有效'}
                                  ]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.manualTopicEdit.name14" />：</td>

                    <td colspan="3">
						<textarea id="topicDetail" name="topicDetail" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:125px;line-height:25px;"
                                  label="<spring:message code="page.manualTopicEdit.name14" />" datatype="varchar" length="200" vtype="length:200" minlen="0"
                                  allowinput="true"
                                  emptytext="<spring:message code="page.manualTopicEdit.name15" />..." mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>


                </tr>
                <tr>
                    <td style="text-align: center;width: 14%;height:10px"><spring:message code="page.manualTopicEdit.name16" />：</td>
                    <td colspan="3" height="60px">
                        <div class="mini-toolbar" id="fileToolBar" style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addTopicFile('${applyId}')">添加附件</a>
                            <span style="font-size: 14px;color:red"><spring:message code="page.manualTopicEdit.name17" /></span>
                        </div>
                        <div id="topicFileListGrid" class="mini-datagrid"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/topic/demandList.do?applyId=${applyId}&detailId=${applyId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false"
                             allowAlternating="true"
                             style="height:150px;"
                        >
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.manualTopicEdit.name18" /></div>
                                <div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.manualTopicEdit.name19" /></div>
                                <div field="fileDesc" width="80" headerAlign="center" align="center"><spring:message code="page.manualTopicEdit.name20" />
                                </div>
                                <div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.manualTopicEdit.name21" /></div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRenderer"><spring:message code="page.manualTopicEdit.name22" />
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>


            </table>

            <p style="font-size: 16px;font-weight: bold;margin-top: 20px"><spring:message code="page.manualTopicEdit.name23" /></p>
            <div class="mini-toolbar" style="margin-bottom: 5px" id="standardListToolBar">
                <a class="mini-button" id="standardAddRow" plain="true" onclick="addStandardRow"><spring:message code="page.manualTopicEdit.name24" /></a>
                <a class="mini-button btn-red" id="standardRemoveRow" plain="true" onclick="removeStandardRow"><spring:message code="page.manualTopicEdit.name25" /></a>
            </div>

            <div id="standardGrid" class="mini-datagrid" allowResize="false" style="height:340px"
                 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                 url="${ctxPath}/serviceEngineering/core/topic/standardList.do?applyId=${applyId}"
                 autoload="true"
                 oncellbeginedit="standardOnCellBeginEdit"
            >
                <div property="columns">
                    <div type="checkcolumn" width="50"></div>
                    <div field="type" name="type" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.manualTopicEdit.name26" />
                        <input property="editor" class="mini-combobox"
                               style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.manualTopicEdit.name7" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.manualTopicEdit.name7" />..."
                               data="[{'key' : '标准','value' : '标准'}
                                       ,{'key' : '其他','value' : '其他'}
                                  ]"
                        /></div>
                    <div field="categoryName" name="categoryName" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.manualTopicEdit.name27" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="standardIdNumber" name="standardIdNumber" width="120" headerAlign="center"
                         align="center"
                    ><spring:message code="page.manualTopicEdit.name28" />
                        <%--<input property="editor" class="mini-textbox"/>--%>

                        <input textname="standardIdNumber" style="width:98%;" property="editor"
                               class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectStandardCloseClick" allowInput="true"
                               onbuttonclick="selectStandard()"/>

                    </div>
                    <div field="standardName" name="standardName" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.manualTopicEdit.name29" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="year" name="year" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.manualTopicEdit.name30" />
                        <input property="editor" class="mini-textbox"/>
                    </div>

                    <div field="standardStatus" name="standardStatus" width="120" headerAlign="center" align="center"
                         renderer="statusRenderer"><spring:message code="page.manualTopicEdit.name31" />
                        <%--<input property="editor" class="mini-textbox"/>--%>

                        <input property="editor" class="mini-combobox"
                               style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.manualTopicEdit.name7" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.manualTopicEdit.name7" />..."
                               data="[{'key' : 'enable','value' : '有效'}
                                       ,{'key' : 'disable','value' : '已废止'}
                                  ]"
                        />
                        <%--</div>--%>
                    </div>
                    <div field="standardRegion" name="standardRegion" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.manualTopicEdit.name32" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="standardRemark" name="standardRemark" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.manualTopicEdit.name14" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="options" width="120" headerAlign="center" align="center"
                         renderer="fileRenderer"><spring:message code="page.manualTopicEdit.name33" />
                    </div>

                </div>
            </div>

            <p style="font-size: 16px;font-weight: bold;margin-top: 20px"><spring:message code="page.manualTopicEdit.name34" /></p>
            <div class="mini-toolbar" style="margin-bottom: 5px" id="benchmarkingListToolBar">
                <a class="mini-button" id="benchmarkingAddRow" plain="true" onclick="addBenchmarkingRow"><spring:message code="page.manualTopicEdit.name24" /></a>
                <a class="mini-button btn-red" id="benchmarkingRemoveRow" plain="true" onclick="removeBenchmarkingRow"><spring:message code="page.manualTopicEdit.name25" /></a>
            </div>

            <div id="benchmarkingGrid" class="mini-datagrid" allowResize="false" style="height:340px"
                 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                 url="${ctxPath}/serviceEngineering/core/topic/benchmarkingList.do?applyId=${applyId}"
                 autoload="true"
            <%--url="${ctxPath}/embedsoft/core/confirmFunction/demandList.do?applyId=${applyId}"--%>
            >
                <div property="columns">
                    <div type="checkcolumn" width="50"></div>
                    <div field="aimCompany" name="aimCompany" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.manualTopicEdit.name35" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="fileName" name="fileName" width="120" headerAlign="center"
                         align="center"
                    ><spring:message code="page.manualTopicEdit.name36" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="year" name="year" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.manualTopicEdit.name30" />
                        <input property="editor" class="mini-textbox"/>
                    </div>


                    <div field="aimRemark" name="aimRemark" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.manualTopicEdit.name14" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="options" width="120" headerAlign="center" align="center"
                         renderer="fileRenderer"><spring:message code="page.manualTopicEdit.name33" />
                    </div>
                </div>
            </div>


        </form>
    </div>
</div>

<div id="fileWindow" title="<spring:message code="page.manualTopicEdit.name33" />" class="mini-window" style="width:1000px;height:400px;"
     showModal="true" showFooter="false" allowResize="true" showCloseButton="true">
    <div class="mini-toolbar">
        <%--<a id="cxFileUploadBtn" class="mini-button" style="margin-bottom: 5px;display: none"--%>
        <a id="fileUploadBtn" class="mini-button" style="margin-bottom: 5px;"
           onclick="uploadFile()"><spring:message code="page.manualTopicEdit.name37" /></a>
            <span style="font-size: 14px;color:red"><spring:message code="page.manualTopicEdit.name17" /></span>

    </div>
    <input class="mini-hidden" id="detailId"/>
    <div class="mini-fit">
        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height:100%"
             allowResize="false" allowCellWrap="true" idField="id"
             autoload="false" showPager="false" showColumnsMenu="false" allowAlternating="true">
            <div property="columns">
                <div field="fileName" name="name" width="160" headerAlign='center' align='center'><spring:message code="page.manualTopicEdit.name36" /></div>
                <div field="fileSize" headerAlign='center' align='center' width="60"><spring:message code="page.manualTopicEdit.name61" /></div>
                <div field="fileDesc" width="80" headerAlign='center' align="center"><spring:message code="page.manualTopicEdit.name38" /></div>
                <div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center'
                     align="center"><spring:message code="page.manualTopicEdit.name39" />
                </div>
                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.manualTopicEdit.name22" />
                </div>
            </div>
        </div>
    </div>
</div>


<div id="selectStandardWindow" title="<spring:message code="page.manualTopicEdit.name40" />" class="mini-window" style="width:1080px;height:600px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false"
>
    <input id="parentInputScene" style="display: none"/>
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                          style="width:auto"><spring:message code="page.manualTopicEdit.name28" />: </span><input
                            class="mini-textbox" id="standardNumber" onenter="searchStandard"></li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.manualTopicEdit.name29" />: </span><input
                            class="mini-textbox" id="standardName" onenter="searchStandard"></li>

                    <li class="liBtn">
								<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
									<em><spring:message code="page.manualTopicEdit.name41" /></em>
									<i class="unfoldIcon"></i>
								</span>
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="searchStandard()"><spring:message code="page.manualTopicEdit.name42" /></a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                           onclick="clearSearchStandard()"><spring:message code="page.manualTopicEdit.name43" /></a>
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
                     allowSort="true" visible="false"><spring:message code="page.manualTopicEdit.name44" />
                </div>
                <div field="systemName" name="systemName" sortField="systemName" width="100" headerAlign="center"
                     align="center"
                     allowSort="true"><spring:message code="page.manualTopicEdit.name45" />
                </div>
                <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                     allowSort="true" hideable="true"><spring:message code="page.manualTopicEdit.name46" />
                </div>
                <div field="banci" name="banci" sortField="banci" width="40" headerAlign="center"
                     align="center" allowSort="true"><spring:message code="page.manualTopicEdit.name47" />
                </div>
                <div field="standardNumber" sortField="standardNumber" width="130" headerAlign="center"
                     align="center" allowSort="true" hideable="true"><spring:message code="page.manualTopicEdit.name48" />
                </div>
                <div field="standardName" sortField="standardName" width="200" headerAlign="center" align="left"
                     allowSort="true" hideable="true"><spring:message code="page.manualTopicEdit.name49" />
                </div>
                <div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.manualTopicEdit.name50" />
                </div>
                <div field="belongFieldNames" headerAlign="center" width="110" headerAlign="center" align="left"
                     allowSort="false"><spring:message code="page.manualTopicEdit.name51" />
                </div>
                <div field="cbbh" name="cbbh" headerAlign="center" width="80" headerAlign="center" align="center"
                     allowSort="false"><spring:message code="page.manualTopicEdit.name52" />
                </div>
                <div field="yzxcd" name="yzxcd" headerAlign="center" width="120" headerAlign="center" align="center"
                     allowSort="false"><spring:message code="page.manualTopicEdit.name53" />
                </div>
                <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center"
                     align="center" hideable="true"
                     allowSort="true" renderer="statusRenderer"><spring:message code="page.manualTopicEdit.name31" />
                </div>
                <div field="publisherName" sortField="publisherName" align="center"
                     width="60" headerAlign="center" allowSort="true"><spring:message code="page.manualTopicEdit.name54" />
                </div>
                <div field="sendSupplier" name="sendSupplier" sortField="sendSupplier" align="center"
                     width="60" headerAlign="center" allowSort="true" renderer="sendSupplierRender"><spring:message code="page.manualTopicEdit.name55" />
                </div>
                <div field="creator" name="creator" align="center" width="60" headerAlign="center" allowSort="false"><spring:message code="page.manualTopicEdit.name56" />
                </div>
                <div field="publishTime" sortField="publishTime" dateFormat="yyyy-MM-dd" align="center"
                     width="100" headerAlign="center" allowSort="true"><spring:message code="page.manualTopicEdit.name57" />
                </div>
                <div field="yearDoCheckStatus" name="yearDoCheckStatus" align="center" width="80" headerAlign="center"
                     renderer="doCheckStatusRender"><spring:message code="page.manualTopicEdit.name58" />
                </div>

            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.manualTopicEdit.name59" />" onclick="selectStandardOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.manualTopicEdit.name60" />" onclick="selectStandardHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var nodeVarsStr = '${nodeVars}';

    var topicForm = new mini.Form("#topicForm");
    var fileWindow = mini.get("fileWindow");
    var selectStandardWindow = mini.get("selectStandardWindow");


    var fileListGrid = mini.get("fileListGrid");
    var topicFileListGrid = mini.get("topicFileListGrid");
    var standardGrid = mini.get("standardGrid");
    var benchmarkingGrid = mini.get("benchmarkingGrid");
    var standardListGrid = mini.get("standardListGrid");


    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var instId = "${instId}";
    var isAdmin = "${isAdmin}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentUserDeptName = "${currentUserDeptName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;


    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }


    var stageName = "";
    $(function () {

        if (applyId) {
            var url = jsUseCtxPath + "/serviceEngineering/core/topic/getJson.do";
            $.post(
                url,
                {id: applyId},
                function (json) {
                    topicForm.setData(json);
                });
        }

        if (action == 'detail') {
            topicForm.setEnabled(false);
            mini.get("standardListToolBar").hide();
            mini.get("benchmarkingListToolBar").hide();
            mini.get("fileToolBar").hide();
            standardGrid.setAllowCellEdit(false);
            benchmarkingGrid.setAllowCellEdit(false);
            //todo 明细不能保存
            mini.get("saveBusiness").setEnabled(false);

            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
        } else if (action == "edit") {
        }
    });

    function getData() {

        //如果是复制，把id置空
        if (action == "copy") {
            mini.get("id").setValue("");
            mini.get("orgId").setValue(applyId);
        }
        var formData = _GetFormJsonMini("topicForm");


        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_demandGrid) {
            delete formData.SUB_demandGrid;
        }

        // todo
        if (action == "copy") {
            debugger;
            var standardGridData = standardGrid.getData();
            for (var index = 0; index < standardGridData.length; index++) {
                standardGridData[index].id = "";
            }
            formData.changeStandardGrid = standardGridData;

            var benchmarkingGridData = benchmarkingGrid.getData();
            for (var index = 0; index < benchmarkingGridData.length; index++) {
                benchmarkingGridData[index].id = "";
            }
            formData.changeBenchmarkingGrid = benchmarkingGridData;
        }
        else {
            if (standardGrid.getChanges().length > 0) {
                formData.changeStandardGrid = standardGrid.getChanges();
            }

            if (benchmarkingGrid.getChanges().length > 0) {
                formData.changeBenchmarkingGrid = benchmarkingGrid.getChanges();
            }
        }




        return formData;

    }







    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: manualTopicEdit_name,
            width: 800,
            height: 600
        });
    }




    function addZxdpsFile(applyId) {

        var stageKey = "";
        if (!applyId) {
            mini.alert(manualTopicEdit_name1);
            return;
        }


        mini.open({
            title: manualTopicEdit_name2,
            url: jsUseCtxPath + "/serviceEngineering/core/topic/openUploadWindow.do?applyId=" + applyId,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                projectParams.applyId = applyId;
                var data = {projectParams: projectParams};  //传递上传参数
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
        var downloadUrl = '/serviceEngineering/core/topic/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + manualTopicEdit_name3 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">' + manualTopicEdit_name3 + '</span>';

        if (record && (action == "edit" || stageName == "start")) {
            var deleteUrl = "/serviceEngineering/core/topic/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + manualTopicEdit_name4 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">' + manualTopicEdit_name4 + '</span>';
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
            s = '<span  title=' + manualTopicEdit_name5 + ' style="color: silver" >' + manualTopicEdit_name5 + '</span>';
        } else {
            var url = '/serviceEngineering/core/topic/preview.do?fileType=' + fileType;
            s = '<span  title=' + manualTopicEdit_name5 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">' + manualTopicEdit_name5 + '</span>';
        }

        return s;
    }

    function fileRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        var detailId = record.id;
        if (detailId) {
            cellHtml = '<span  title=' + manualTopicEdit_name6 + ' style="color:#409EFF;cursor: pointer;" onclick="clickFileList(\'' + detailId + '\',\'' + record.judge + '\')">' + manualTopicEdit_name6 + '</span>';
        }
        return cellHtml;

    }

    function addStandardRow() {
        var row = {}
        standardGrid.addRow(row);
    }


    function removeStandardRow() {
        var selecteds = standardGrid.getSelecteds();
        standardGrid.removeRows(selecteds);
    }

    function addBenchmarkingRow() {
        var row = {}
        benchmarkingGrid.addRow(row);
    }

    function removeBenchmarkingRow() {
        var selecteds = benchmarkingGrid.getSelecteds();
        benchmarkingGrid.removeRows(selecteds);
    }


    function clickFileList(detailId) {
        mini.get("detailId").setValue(detailId);
        var url = jsUseCtxPath + "/serviceEngineering/core/topic/checkFileList.do?detailId=" + detailId;
        fileListGrid.setUrl(url);
        fileListGrid.load();
        fileWindow.show();
        if (action == 'detail' || (action == "task" && stageName != "start")) {
            mini.get("fileUploadBtn").hide();
        }
    }

    function uploadFile() {

        debugger;
        var detailId = mini.get("detailId").getValue();

        mini.open({
            title: manualTopicEdit_name2,
            url: jsUseCtxPath + "/serviceEngineering/core/topic/openUploadWindow.do",
            width: 900,
            height: 350,
            showModal: true,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var passParams = {};
                passParams.applyId = applyId;
                passParams.detailId = detailId;
                var data = {passParams: passParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                fileListGrid.load();
            }
        });
    }

    //控制反馈信息编辑权限
    function OnCellBeginEdit(e) {
        var field = e.field;
        var record = e.record;
        // 只有编制阶段且有意见可以编辑意见反馈字段
        if (action == "task" && stageName == 'start') {
            if (field == "userName" || field == "userDepart" || field == "reviewTime" || field == "hasOpinion" || field == "opinionDetail" || field == "confirmFeedback") {
                e.cancel = true;
            }
            // 无意见不可编辑
            if (record.hasOpinion != "是") {
                e.cancel = true;
            }
        }
        if (action == "task" && stageName == "opinion") {
            //这步只可以编辑自己的内容
            if (record.userId != currentUserId) {
                e.cancel = true;
            }

            if (field == "userName" || field == "userDepart" || field == "reviewTime" || field == "opinionFeedback") {
                e.cancel = true;
            }
            // 无意见不允许编辑评审意见
            if (record.hasOpinion != "是") {
                if (field == "opinionDetail") {
                    e.cancel = true;
                }
            }

            // 已有意见反馈的不允许修改是否有意见和评审详情
            if (record.opinionFeedback) {
                if (field == "hasOpinion" || field == "opinionDetail") {
                    e.cancel = true;
                }
            } else {
                //没意见反馈不允许确认反馈意见
                if (field == "confirmFeedback") {
                    e.cancel = true;
                }
            }


        }
    }

    //控制反馈信息编辑权限
    function standardOnCellBeginEdit(e) {
        var field = e.field;
        var record = e.record;
        if (record.type == "标准") {
            if (field == "year" || field == "standardName" || field == "standardStatus" || field == "standardType") {
                e.cancel = true;
            }
        }
    }

    //关联标准

    function selectStandard() {
        // $("#parentInputScene").val(inputScene);
        debugger;
        var selected = standardGrid.getSelected();
        if (selected.type != "标准") {
            mini.alert(manualTopicEdit_name7);
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

        var objrow = standardGrid.getSelected();
        standardGrid.updateRow(objrow,
            {
                type: "标准",
                standardId: selectRow.id,
                standardName: selectRow.standardName,
                year: selectRow.banci,
                standardIdNumber: selectRow.standardNumber,
                standardStatus: selectRow.standardStatus,
                categoryName: selectRow.categoryName,
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

    function saveBusiness() {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        if (action == "copy") {
            mini.get("id").setValue("");
            mini.get("orgId").setValue(applyId);
        }

        var formData = _GetFormJsonMini("topicForm");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }

        if (action == "copy") {
            var standardGridData = standardGrid.getData();
            for (var index = 0; index < standardGridData.length; index++) {
                standardGridData[index].id = "";
            }
            formData.changeStandardGrid = standardGridData;

            var benchmarkingGridData = benchmarkingGrid.getData();
            for (var index = 0; index < benchmarkingGridData.length; index++) {
                benchmarkingGridData[index].id = "";
            }
            formData.changeBenchmarkingGrid = benchmarkingGridData;
        }
        else {
            if (standardGrid.getChanges().length > 0) {
                formData.changeStandardGrid = standardGrid.getChanges();
            }

            if (benchmarkingGrid.getChanges().length > 0) {
                formData.changeBenchmarkingGrid = benchmarkingGrid.getChanges();
            }
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/topic/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = manualTopicEdit_name16;
                    } else {
                        message = manualTopicEdit_name17 + data.message;
                    }

                    mini.alert(message, manualTopicEdit_name18, function () {
                        var url = jsUseCtxPath + "/serviceEngineering/core/topic/applyEditPage.do?applyId=" + data.data + '&action=edit';
                        window.location.href = url;
                        // CloseWindow();
                    });
                }
            }
        });
    }

    //..检验表单是否必填
    function validBusiness() {

        var topicName = $.trim(mini.get("topicName").getValue());
        if (!topicName) {
            return {"result": false, "message": manualTopicEdit_name8};
        }

        var topicId = $.trim(mini.get("topicId").getValue());
        if (!topicId) {
            return {"result": false, "message": manualTopicEdit_name9};

        }
        var topicType = $.trim(mini.get("topicType").getValue());
        if (!topicType) {
            return {"result": false, "message": manualTopicEdit_name10};
        }

        var topicTextName = $.trim(mini.get("topicTextName").getValue());
        if (!topicTextName) {
            return {"result": false, "message": manualTopicEdit_name11};
        }

        var version = $.trim(mini.get("version").getValue());
        if (!version) {
            return {"result": false, "message": manualTopicEdit_name12};
        }

        return {"result": true};
    }

    function addTopicFile(applyId) {
        if (!applyId) {
            mini.alert(manualTopicEdit_name13);
            return;
        }
        mini.open({
            title: manualTopicEdit_name2,
            url: jsUseCtxPath + "/serviceEngineering/core/topic/openUploadWindow.do",
            width: 900,
            height: 350,
            showModal: true,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var passParams = {};
                passParams.applyId = applyId;
                passParams.detailId = applyId;
                var data = {passParams: passParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                topicFileListGrid.load();
            }
        });
    }

    //删除文档
    function deleteFile(fileName, fileId, formId, urlValue) {
        mini.confirm(manualTopicEdit_name14, manualTopicEdit_name15,
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + urlValue;
                    var data = {
                        fileName: fileName,
                        id: fileId,
                        formId: formId
                    };
                    $.ajax({
                        url: url,
                        method: 'post',
                        contentType: 'application/json',
                        data: mini.encode(data),
                        success: function (json) {
                            debugger;
                            if (topicFileListGrid) {
                                topicFileListGrid.load();
                            }

                            if (fileListGrid) {
                                fileListGrid.load();
                            }

                        }
                    });
                }
            }
        );
    }


</script>
</body>
</html>
