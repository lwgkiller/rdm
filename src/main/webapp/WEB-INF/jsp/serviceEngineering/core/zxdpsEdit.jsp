<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>手册制修订评审</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/embedsoft/cxUtil.js?version=${static_res_version}" type="text/javascript"></script>


</head>
<body>zxdpsList
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.zxdpsEdit.name" /></a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()"><spring:message code="page.zxdpsEdit.name1" /></a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="zxdpsForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="orgId" name="orgId"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="feedbackId" name="feedbackId" class="mini-hidden"/>
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 20px"><spring:message code="page.zxdpsEdit.name2" /></p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.zxdpsEdit.name3" />：</td>
                    <td style="min-width:170px">
                        <input id="applyNumber" name="applyNumber" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>

                    <td style="text-align: center;width: 20%"><spring:message code="page.zxdpsEdit.name4" />：</td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <>
                <td style="text-align: center;width: 20%"><spring:message code="page.zxdpsEdit.name5" />：</td>
                <td style="min-width:170px">
                    <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                           enabled="false"/>
                </td>


                <td style="text-align: center;width: 20%">
                    <spring:message code="page.zxdpsEdit.name6" />：</td>

                <td id="testa" style="min-width:170px" >
                    <%--<input id="feedbackNumber" name="feedbackNumber" class="mini-textbox" style="width:98%;"--%>
                    <%--enabled="true"/>--%>
                    <input id="feedbackNumber" name="feedbackNumber" textname="feedbackNumber" style="width:85%;"
                           property="editor"
                           class="mini-buttonedit" showClose="true"
                           oncloseclick="onSelectXxfkCloseClick" allowInput="true"
                           onbuttonclick="selectXxfk()"/>
                    <span herf = "#" style="color:#44cef6;cursor: pointer" onclick="jumpXxfkInfo()"><spring:message code="page.zxdpsEdit.name7" /></span>

                </td>



                </tr>


                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.zxdpsEdit.name8" />：<span style="color:red">*</span></td>
                    <td>
                        <input id="expert" name="expertId" textname="expertName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"
                        />
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.zxdpsEdit.name9" />：<span style="color:red">*</span></td>
                    <td>
                        <input id="approval" name="approvalId" textname="approvalName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"
                        />
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.zxdpsEdit.name10" />：</td>
                    <td colspan="3">
                        <input id="remark" name="remark" class="mini-textbox" style="width:98%;"
                        />
                    </td>
                </tr>


                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.zxdpsEdit.name11" />：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <input id="leaderResolution" name="leaderResolution" class="mini-textbox" style="width:98%;"
                               enabled="fasle"/>
                    </td>
                </tr>

            </table>

            <p style="font-size: 16px;font-weight: bold;margin-top: 20px"><spring:message code="page.zxdpsEdit.name12" /></p>
            <div class="mini-toolbar" style="margin-bottom: 5px" id="topicToolBar">
                <a class="mini-button" id="choseTopic" plain="true" onclick="selectTopic"><spring:message code="page.zxdpsEdit.name13" /></a>
                <a class="mini-button btn-red" id="removeTopic" plain="true" onclick="removeTopic"><spring:message code="page.zxdpsEdit.name14" /></a>
            </div>

            <div id="topicGrid" class="mini-datagrid" allowResize="false" style="height:340px"
                 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                 url="${ctxPath}/serviceEngineering/core/zxdps/topicList.do?applyId=${applyId}"
                 autoload="true"
            <%--onrowclick="jump2Topic"--%>
            >
                <div property="columns">
                    <div type="checkcolumn" width="50"></div>
                    <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                         renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.zxdpsEdit.name15" />
                    </div>
                    <div field="topicName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name16" /></div>
                    <div field="topicId" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name17" /></div>
                    <div field="topicTextName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name18" /></div>
                    <div field="topicType" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name19" /></div>
                    <div field="region" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name20" /></div>
                    <div field="productLine" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name21" /></div>
                    <div field="productSeries" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name22" /></div>
                    <div field="productSettings" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name23" /></div>
                    <div field="remark" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name10" /></div>
                    <div field="version" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name24" /></div>
                    <div field="versionStatus" headerAlign="center" align="center" allowSort="false"
                         renderer="onHistoryRenderer"><spring:message code="page.zxdpsEdit.name25" />
                    </div>
                </div>
            </div>

            <p style="font-size: 16px;font-weight: bold;margin-top: 20px"><spring:message code="page.zxdpsEdit.name26" /></p>
            <div class="mini-toolbar" style="margin-bottom: 5px" id="opinionListToolBar">
                <a class="mini-button" id="opinionAddRow" plain="true" onclick="addOpinionRow"><spring:message code="page.zxdpsEdit.name27" /></a>
                <a class="mini-button btn-red" id="opinionRemoveRow" plain="true" onclick="removeOpinionRow"><spring:message code="page.zxdpsEdit.name14" /></a>
            </div>

            <div id="opinionGrid" class="mini-datagrid" allowResize="false" style="height:340px"
                 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                 multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false"
                 url="${ctxPath}/serviceEngineering/core/zxdps/opinionList.do?applyId=${applyId}"
                 autoload="true"
                 oncellbeginedit="OnCellBeginEdit"
            >
                <div property="columns">
                    <div type="checkcolumn" width="50"></div>
                    <div field="userName" name="userName" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.zxdpsEdit.name28" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="userDepart" name="userDepart" width="120" headerAlign="center"
                         align="center"
                    ><spring:message code="page.zxdpsEdit.name29" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="hasOpinion" name="hasOpinion" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.zxdpsEdit.name30" />
                        <input property="editor" id="partsType" name="partsType" class="mini-combobox"
                               style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.zxdpsEdit.name31" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.zxdpsEdit.name31" />..."
                               data="[{'key' : '是','value' : '是'}
                                       ,{'key' : '否','value' : '否'}
                                  ]"
                               onvaluechanged="opinionChange"
                        />

                        <%--<input property="editor" class="mini-textbox"/>--%>
                    </div>
                    <div field="opinionDetail" name="opinionDetail" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.zxdpsEdit.name32" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="reviewTime" name="reviewTime" width="120" dateFormat="yyyy-MM-dd HH:mm:ss"
                         headerAlign="center" align="center"
                    ><spring:message code="page.zxdpsEdit.name4" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="opinionFeedback" name="opinionFeedback" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.zxdpsEdit.name33" />
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="confirmFeedback" name="confirmFeedback" width="120" headerAlign="center" align="center"
                    ><spring:message code="page.zxdpsEdit.name34" />
                        <%--<input property="editor" class="mini-textbox"/>--%>
                        <input property="editor" class="mini-combobox"
                               style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.zxdpsEdit.name31" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.zxdpsEdit.name31" />..."
                               data="[{'key' : '同意','value' : '同意'}
                                       ,{'key' : '不同意','value' : '不同意'}
                                  ]"
                        />
                    </div>

                </div>
            </div>

        </form>
    </div>
</div>


<div id="selectTopicWindow" title="<spring:message code="page.zxdpsEdit.name35" />" class="mini-window" style="width:1080px;height:600px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false"
>
    <input id="parentInputScene" style="display: none"/>
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                          style="width:auto"><spring:message code="page.zxdpsEdit.name16" />: </span><input
                            class="mini-textbox" id="topicName" onenter="searchTopic"></li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.zxdpsEdit.name17" />: </span><input
                            class="mini-textbox" id="topicId" onenter="searchTopic"></li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.zxdpsEdit.name18" />: </span><input
                            class="mini-textbox" id="topicTextName" onenter="searchTopic"></li>

                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="searchTopic()"><spring:message code="page.zxdpsEdit.name36" /></a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                           onclick="clearSearchTopic()"><spring:message code="page.zxdpsEdit.name37" /></a>
                        <div style="display: inline-block" class="separator"></div>
                    </li>

                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="topicListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" multiSelect="true" sizeList="[20,50,100,200]" pageSize="20"
             onrowdblclick="onRowDblClick"
             allowAlternating="true" pagerButtons="#pagerButtons" showColumnsMenu="true"
             url="${ctxPath}/serviceEngineering/core/topic/applyList.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="id" headerAlign="center" align="center" allowSort="false" visible="false"><spring:message code="page.zxdpsEdit.name38" /></div>
                <div field="topicName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name16" /></div>
                <div field="topicId" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name17" /></div>
                <div field="topicTextName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name18" /></div>
                <div field="topicType" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name19" /></div>
                <div field="region" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name20" /></div>
                <div field="productLine" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name21" /></div>
                <div field="productSeries" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name22" /></div>
                <div field="productSettings" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name23" /></div>
                <div field="version" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name24" /></div>
                <div field="versionStatus" headerAlign="center" align="center" allowSort="false"
                     renderer="onHistoryRenderer"><spring:message code="page.zxdpsEdit.name25" />
                </div>
                <div field="status" width="50" headerAlign="center" align="center" allowSort="true"
                     renderer="onStatusRenderer"><spring:message code="page.zxdpsEdit.name39" />
                </div>
                <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true"><spring:message code="page.zxdpsEdit.name40" />
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.zxdpsEdit.name41" />" onclick="selectTopicOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.zxdpsEdit.name42" />" onclick="selectTopicHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="selectXxfkWindow" title="<spring:message code="page.zxdpsEdit.name43" />" class="mini-window" style="width:1080px;height:600px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false"
>
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="xxfkSearchForm" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <ul>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto"><spring:message code="page.zxdpsEdit.name44" />: </span>
                            <input class="mini-textbox" id="applyNumberSearch" name="applyNumber"/>
                            <span class="text" style="width:auto"><spring:message code="page.zxdpsEdit.name5" />: </span>
                            <input class="mini-textbox" id="creatorNameSearch" name="creatorName"/>
                            <span class="text" style="width:auto"><spring:message code="page.zxdpsEdit.name45" />: </span>
                            <input property="editor" class="mini-combobox"
                                   style="width:98%;"
                                   id="adoptions" name="adoptions"
                                   textField="key" valueField="value" emptyText="<spring:message code="page.zxdpsEdit.name31" />..."
                                   required="false" allowInput="false" showNullItem="true"
                                   nullItemText="<spring:message code="page.zxdpsEdit.name31" />..."
                                   data="[{'key' : '采纳','value' : '采纳'}
                                       ,{'key' : '部分采纳','value' : '部分采纳'}
                                       ,{'key' : '不采纳','value' : '不采纳'}
                                  ]"
                            />
                        </li>
                        <li style="margin-left: 10px">
                            <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchXxfk()"><spring:message code="page.zxdpsEdit.name36" /></a>
                            <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearXxfk()"><spring:message code="page.zxdpsEdit.name37" /></a>
                        </li>
                    </ul>

                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="scxxfkListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onlyCheckSelection="true"
             onrowdblclick="onRowDblClick2"
             url="${ctxPath}/serviceEngineering/core/scxxfk/applyList.do" idField="id"
             multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
             pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div type="indexcolumn" width="28" headerAlign="center" align="center"><spring:message code="page.zxdpsEdit.name46" /></div>
                <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                     renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.zxdpsEdit.name15" />
                </div>
                <div field="applyNumber" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name44" /></div>
                <div field="creatorName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name5" /></div>
                <div field="departName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name47" /></div>
                <div field="applyTime" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign="center" align="center"
                     allowSort="false"><spring:message code="page.zxdpsEdit.name48" />
                </div>
                <div field="adoptions" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsEdit.name45" /></div>
                <div field="taskName" headerAlign='center' align='center' width="60"><spring:message code="page.zxdpsEdit.name49" /></div>
                <div field="allTaskUserNames" headerAlign='center' align='center' width="40"><spring:message code="page.zxdpsEdit.name50" /></div>
                <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                     renderer="onStatusRenderer"><spring:message code="page.zxdpsEdit.name39" />
                </div>
                <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true"><spring:message code="page.zxdpsEdit.name40" />
                </div>
            </div>
        </div>
    </div>

    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.zxdpsEdit.name41" />" onclick="selectXxfkOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.zxdpsEdit.name42" />" onclick="selectXxfkHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var nodeVarsStr = '${nodeVars}';

    var zxdpsForm = new mini.Form("#zxdpsForm");
    var opinionGrid = mini.get("opinionGrid");
    var topicListGrid = mini.get("topicListGrid");
    var topicGrid = mini.get("topicGrid");
    var selectTopicWindow = mini.get("selectTopicWindow");
    var selectXxfkWindow = mini.get("selectXxfkWindow");
    var scxxfkListGrid = mini.get("scxxfkListGrid");

    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var instId = "${instId}";
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
        var url = jsUseCtxPath + "/serviceEngineering/core/zxdps/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                zxdpsForm.setData(json);
            });
        if (action == 'detail') {
            zxdpsForm.setEnabled(false);


            mini.get("opinionListToolBar").hide();

            opinionGrid.setAllowCellEdit(false);

            $("#detailToolBar").show();
            $("#topicToolBar").hide();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
        } else if (action == "edit") {
            mini.get("opinionListToolBar").hide();
        }
    });

    function getData() {

        //如果是复制，把id置空

        var formData = _GetFormJsonMini("zxdpsForm");


        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_demandGrid) {
            delete formData.SUB_demandGrid;
        }


        if (opinionGrid.getChanges().length > 0) {
            formData.changeOpinionGrid = opinionGrid.getChanges();
        }
        if (topicGrid.getChanges().length > 0) {
            formData.changeTopicGrid = topicGrid.getChanges();
        }

        return formData;

    }

    //保存草稿
    function saveDraft(e) {
        window.parent.saveDraft(e);
    }

    //发起流程
    function startProcess(e) {
        var formValid = validZxdps();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }


        window.parent.startProcess(e);
    }

    //下一步审批
    function projectApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start') {
            var formValid = validZxdps();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        } else {
            var formValid = validNext();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }

        //检查通过
        window.parent.approve();
    }

    function validNext() {
        debugger;

        if (stageName == 'process') {

            var resolution = $.trim(mini.get("resolution").getValue());
            if (!resolution) {
                return {"result": false, "message": zxdpsEdit_name};
            }
        }
        if (stageName == 'confirm') {
            var leaderResolution = $.trim(mini.get("leaderResolution").getValue());
            if (!leaderResolution) {
                return {"result": false, "message": zxdpsEdit_name1};
            }
        }
        if (stageName == "opinion") {
            //如果有意见，需要填写详情，且全部评审人员都需要添加行
            var opinionGridData = opinionGrid.getData();

            //全部评审人员都需要添加行
            var contianFlag = false;

            for (var i = 0; i < opinionGridData.length; i++) {
                if (opinionGridData[i].userId == currentUserId) {
                    contianFlag = true;
                    // 如果有意见，必须填写意见详情
                    if (opinionGridData[i].hasOpinion == "是") {
                        if (opinionGridData[i].opinionDetail == undefined || opinionGridData[i].opinionDetail == "") {
                            return {"result": false, "message": zxdpsEdit_name2};
                        }
                        // 如有意见反馈，必须选择确认结果
                        if (!(opinionGridData[i].opinionFeedback == undefined || opinionGridData[i].opinionFeedback == "")) {
                            if (opinionGridData[i].confirmFeedback == undefined || opinionGridData[i].confirmFeedback == "") {
                                return {"result": false, "message": zxdpsEdit_name3};
                            }
                        }


                    }
                }
            }
            if (currentUserNo != "admin" && (!contianFlag)) {
                return {"result": false, "message": zxdpsEdit_name4};
            }

        }


        return {"result": true};
    }


    function validZxdps() {
        //基础字段验证

        var expert = $.trim(mini.get("expert").getValue());
        if (!expert) {
            return {"result": false, "message": zxdpsEdit_name5};
        }

        var approval = $.trim(mini.get("approval").getValue());
        if (!approval) {
            return {"result": false, "message": zxdpsEdit_name6};
        }


        //驳回后，有意见的反馈信息中要填写意见反馈
        var opinionGridData = opinionGrid.getData();
        for (var i = 0; i < opinionGridData.length; i++) {
            // 如果有意见，必须填写意见反馈
            if (opinionGridData[i].hasOpinion == "是") {
                if (opinionGridData[i].opinionFeedback == undefined || opinionGridData[i].opinionFeedback == "") {
                    return {"result": false, "message": zxdpsEdit_name7};
                }
            }
        }


        return {"result": true};
    }

    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: zxdpsEdit_name8,
            width: 800,
            height: 600
        });
    }


    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName != 'opinion') {
            mini.get("opinionListToolBar").hide();
            opinionGrid.setAllowCellEdit(false);

        }
        if (stageName != 'start') {
            zxdpsForm.setEnabled(false);
            $("#topicToolBar").hide();
        }
        if (stageName == 'confirm') {
            mini.get("leaderResolution").setEnabled("true");
        }
        if (stageName == 'start') {
            // 这个要注意顺序
            opinionGrid.setAllowCellEdit(true);
        }


    }


    function addOpinionRow() {
        //todo 判断权限？
        var row = {
            userId: currentUserId,
            userName: currentUserName,
            userDepart: currentUserDeptName,
            hasOpinion: "否",
            reviewTime: new Date()
        };
        opinionGrid.addRow(row);
    }

    function removeOpinionRow() {
        //单选，只有在办理时，创建者可以移除
        debugger;
        var selected = opinionGrid.getSelected();
        if (currentUserId != selected.userId && currentUserNo != "admin") {
            mini.alert(zxdpsEdit_name9);
            return;
        }
        opinionGrid.removeRow(selected);

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


    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function opinionChange(e) {
        if (e.value == "是") {
            return;
        }
        if (e.value == "否") {
            var selected = opinionGrid.getSelected();
            opinionGrid.updateRow(selected, {"opinionDetail": ""});
        }
    }

    function selectTopic() {
        selectTopicWindow.show();
        searchTopic();
    }

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
        var topicTextName = $.trim(mini.get("topicTextName").getValue());
        if (topicTextName) {
            queryParam.push({name: "topicTextName", value: topicTextName});
        }
        // queryParam.push({name: "instStatus", value: "RUNNING"});
        //这里限制值能搜索到待评审：READY状态的topic
        queryParam.push({name: "status", value: "READY"});
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
        mini.get("topicTextName").setValue("");
        searchTopic();
    }


    function onRowDblClick() {
        selectTopicOK();
    }

    function selectTopicOK() {
        var selectRows = topicListGrid.getSelecteds();
        topicGrid.addRows(selectRows);
        selectTopicHide();

    }

    function selectTopicHide() {
        selectTopicWindow.hide();
    }

    function onSelectTopicCloseClick(e) {
        var objrow = topicGrid.getSelected();
        topicGrid.updateRow(objrow,
            {
                standardIdNumber: ""
            });
    }

    function removeTopic() {
        var selecteds = topicGrid.getSelecteds();
        topicGrid.removeRows(selecteds);
    }

    // function jump2Topic(e) {
    //     var record = e.record;
    //     if (record.jumpId) {
    //         var url = jsUseCtxPath + "/serviceEngineering/core/topic/applyEditPage.do?action=detail&applyId=" + record.jumpId;
    //         var winObj = window.open(url);
    //     }
    // }
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.jumpId;
        if (!businessId) {
            businessId = record.id;
        }

        var s = '<span  title=' + zxdpsEdit_name10 + ' style="color:#409EFF" onclick="topicDetail(\'' + businessId + '\')">' + zxdpsEdit_name10 + '</span>';
        return s;
    }

    function topicDetail(applyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/topic/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
    }

    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
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


    // xxfk选择窗口

    function selectXxfk() {
        selectXxfkWindow.show();
        searchXxfk();
    }

    function searchXxfk() {
        var queryParam = [];
        //其他筛选条件

        var applyNumber = $.trim(mini.get("applyNumberSearch").getValue());
        if (applyNumber) {
            queryParam.push({name: "applyNumber", value: applyNumber});
        }
        var creatorName = $.trim(mini.get("creatorNameSearch").getValue());
        if (creatorName) {
            queryParam.push({name: "creatorName", value: creatorName});
        }
        var adoptions = $.trim(mini.get("adoptions").getValue());
        if (adoptions) {
            queryParam.push({name: "adoptions", value: adoptions});
        }
        // queryParam.push({name: "instStatus", value: "RUNNING"});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = scxxfkListGrid.getPageIndex();
        data.pageSize = scxxfkListGrid.getPageSize();
        data.sortField = scxxfkListGrid.getSortField();
        data.sortOrder = scxxfkListGrid.getSortOrder();
        //查询
        scxxfkListGrid.load(data);
    }

    function clearXxfk() {
        mini.get("applyNumberSearch").setValue("");
        mini.get("creatorNameSearch").setValue("");
        mini.get("adoptions").setValue("");
        searchXxfk();
    }


    function onRowDblClick2() {
        selectXxfkOK();
    }

    function selectXxfkOK() {
        var selectRow = scxxfkListGrid.getSelected();
        mini.get("feedbackNumber").setValue(selectRow.applyNumber);
        mini.get("feedbackNumber").setText(selectRow.applyNumber);
        mini.get("feedbackId").setValue(selectRow.id);
        selectXxfkHide();
    }

    function selectXxfkHide() {
        selectXxfkWindow.hide();
    }

    function onSelectXxfkCloseClick(e) {
        mini.get("feedbackNumber").setValue("");
        mini.get("feedbackNumber").setText("");
        mini.get("feedbackId").setValue("");
    }

    function jumpXxfkInfo() {
        debugger;
        var applyId = mini.get("feedbackId").getValue();
        if(applyId)
        {
            var url = jsUseCtxPath + "/serviceEngineering/core/scxxfk/applyEditPage.do?action=detail&applyId=" + applyId;
            var winObj = window.open(url);
        }

    }


</script>
<redxun:gridScript gridId="topicGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
