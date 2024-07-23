<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/standardManager/jsStandardManagementEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>


    <style type="text/css">
        fieldset {
            border: solid 1px #aaaaaab3;
            min-width: 920px;
        }

        .hideFieldset {
            border-left: 0;
            border-right: 0;
            border-bottom: 0;
        }

        .hideFieldset .fieldset-body {
            display: none;
        }

        .tooltip {
            position: relative;
            display: inline-block;
        }

        .processStage {
            background-color: #ccc !important;
            font-size: 15px !important;
            font-family: '微软雅黑' !important;
            text-align: center !important;
            vertical-align: middle !important;
            color: #201f35 !important;
            height: 30px !important;
            border-right: solid 0.5px #666;
        }

        .rmMem {
            background-color: #848382ad
        }
    </style>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveTemp" class="mini-button" style="display: none" onclick="saveStandardInProcess()"><spring:message
                code="page.jsStandardManagementEdit.name"/></a>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message
                code="page.jsStandardManagementEdit.name1"/></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message
                code="page.jsStandardManagementEdit.name2"/></a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto">
        <form id="standardDemandApply" method="post">
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="finalStandardName" name="finalStandardName" class="mini-hidden"/>
            <input id="id" name="id" class="mini-hidden"/>
            <fieldset id="fdBaseInfo">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="checkboxBaseInfo"
                               onclick="bztoggleFieldSet(this, 'fdBaseInfo')" hideFocus/>
                        <spring:message code="page.jsStandardManagementEdit.name3"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>

                            <td style="width: 20%">任务来源：<span style="color:red">*</span></td>
                            <td style="min-width:170px">
                                <input id="rwly" name="rwly" class="mini-combobox" style="width:98%;"
                                       textField="key" valueField="value"
                                       emptyText="<spring:message code="page.jsStandardManagementEdit.name9" />..."
                                       data="[{key:'技术项目',value:'技术项目'}
                                       ,{key:'标准信息反馈',value:'标准信息反馈'}
                                       ,{key:'技术标准自查',value:'技术标准自查'}
                                       ,{key:'其他',value:'其他'}]"
                                       allowInput="false" showNullItem="false" onvaluechanged="lyChange()"/>

                            </td>
                            <%--<td style="width: 20%"><spring:message code="page.jsStandardManagementEdit.name4"/>：</td>--%>
                            <%--<td style="min-width:170px">--%>
                            <%--<input id="feedbackId" style="width:98%;" class="mini-buttonedit" name="feedbackId"--%>
                            <%--textname="feedbackId"--%>
                            <%--allowInput="false" onbuttonclick="selectStandardFeedback()"/>--%>

                            <%--</td>--%>

                        </tr>
                        <tr id="ly1" style="display: none">
                            <td style="width: 20%">关联技术项目：</td>
                            <td colspan="3" style="min-width:170px;">
                                <input id="jsxmId" name="jsxmId" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr id="ly2" style="display: none">
                            <td style="width: 20%">关联标准信息反馈：</td>
                            <td colspan="3" style="min-width:170px">
                                <input id="feedbackId" style="width:98%;" class="mini-buttonedit" name="feedbackId"
                                       textname="feedbackId"
                                       allowInput="false" onbuttonclick="selectStandardFeedback()"/>

                            </td>
                        </tr>
                        <tr id="ly3" style="display: none">
                            <td style="width: 20%">关联标准自查：</td>
                            <td colspan="3" style="min-width:170px;">
                                <input id="jsbzzcId" name="jsbzzcId" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr id="ly4" style="display: none">
                            <td style="width: 20%">关联其他：</td>
                            <td colspan="3" style="min-width:170px;">
                                <input id="otherId" name="otherId" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <%--<tr>--%>
                        <%--<td style="width: 20%"><spring:message code="page.jsStandardManagementEdit.name6"/>：<span--%>
                        <%--style="color:red">*</span></td>--%>
                        <%--<td colspan="3">--%>
                        <%--<textarea id="rwms" name="rwms" class="mini-textarea rxc"--%>
                        <%--plugins="mini-textarea"--%>
                        <%--style="width:99.1%;height:50px;line-height:25px;"--%>
                        <%--label="<spring:message code="page.jsStandardManagementEdit.name7" />"--%>
                        <%--datatype="varchar" allowinput="true"--%>
                        <%--emptytext="" mwidth="80" wunit="%" mheight="200"--%>
                        <%--hunit="px"></textarea>--%>
                        <%--</td>--%>
                        <%--</tr>--%>


                        <tr>
                            <td style="width: 20%"><spring:message code="page.jsStandardManagementEdit.name8"/>：<span
                                    style="color:red">*</span></td>
                            <td>
                                <input id="rwlb" name="rwlb" class="mini-combobox" style="width:98%;"
                                       textField="key" valueField="value"
                                       emptyText="<spring:message code="page.jsStandardManagementEdit.name9" />..."
                                       data="[{key:'新制定',value:'新制定'},{key:'修订',value:'修订'}]"
                                       allowInput="false" showNullItem="false" onvaluechanged="typeChange2()"/>
                            </td>
                            <td style="width: 20%"><spring:message code="page.jsStandardManagementEdit.name5"/>：<span
                                    style="color:red">*</span></td>
                            <td style="min-width:170px">
                                <input id="standardLeaderId" name="standardLeaderId" textname="standardLeaderName"
                                       property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                            </td>

                        </tr>

                        <tr>
                            <td style="width: 25%"><spring:message code="page.jsStandardManagementEdit.name10"/>：<span
                                    style="color:red">*</span></td>
                            <td style="min-width:170px">
                                <input id="standardPeopleId" name="standardPeopleId" textname="standardPeopleName"
                                       property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                                       enabled="false"/>
                            </td>
                            <td style="width: 20%">模块负责人：<span style="color:red">*</span></td>
                            <td style="min-width:170px">
                                <input id="modelPeopleId" name="modelPeopleId" textname="modelPeopleName"
                                       property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"
                                       enabled="false"/>
                            </td>

                        </tr>

                        <tr id="s1" style="display: none">
                            <td style="width: 20%"><spring:message code="page.jsStandardManagementEdit.name11"/>：<span
                                    style="color:red">*</span></td>
                            <td colspan="3" style="min-width:170px;">
                                <input id="standardName" name="standardName" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr id="s2" style="display: none">
                            <td style="width: 20%"><spring:message code="page.jsStandardManagementEdit.name12"/>：<span
                                    style="color:red">*</span></td>
                            <td colspan="3" style="min-width:170px;">
                                <input id="oldStandardId" style="width:98%" class="mini-buttonedit" showClose="true"
                                       name="oldStandardId" textname="oldStandardName"
                                       oncloseclick="quoteStandardCloseClickOld('old')" allowInput="false"
                                       onbuttonclick="selectQuoteStandardOld('old')"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="width: 20%">标准目的
                                <image src="${ctxPath}/styles/images/question.png"
                                       style="cursor: pointer;vertical-align: middle"
                                       title="应写明现阶段遇到的问题和起草该标准所要解决的困难；"/>
                                ：<span style="color:red">*</span></td>
                            <td colspan="3">
						        <textarea id="bzmd" name="bzmd" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:99%;height:50px;line-height:25px;" required
                                          label="标准目的" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                          allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                            </td>
                        </tr>

                        <tr>
                            <td style="width: 20%">标准范围
                                <image src="${ctxPath}/styles/images/question.png"
                                       style="cursor: pointer;vertical-align: middle" title="应写明标准适用的业务阶段、业务范围等；"/>
                                ：<span style="color:red">*</span>
                            </td>
                            <td colspan="3">
						        <textarea id="bzfw" name="bzfw" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:99%;height:50px;line-height:25px;" required
                                          label="标准范围" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                          allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 20%">标准现状
                                <image src="${ctxPath}/styles/images/question.png"
                                       style="cursor: pointer;vertical-align: middle"
                                       title="应写明国行标现状，集团标准现状，竞品企业标准现状，企业技术标准现状等；"/>
                                ：<span style="color:red">*</span></td>
                            <td colspan="3">
						        <textarea id="bzxz" name="bzxz" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:99%;height:50px;line-height:25px;" required
                                          label="标准现状" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                          allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                            </td>
                        </tr>

                        <tr>
                            <td style="width: 20%">素材来源
                                <image src="${ctxPath}/styles/images/question.png"
                                       style="cursor: pointer;vertical-align: middle" title="主要包括国行标提升、对标研究、经验总结等"/>
                                ：<span style="color:red">*</span></td>
                            <td colspan="3">
						        <textarea id="scly" name="scly" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:99%;height:50px;line-height:25px;" required
                                          label="素材来源" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                          allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                            </td>
                        </tr>


                        <%--<tr>--%>
                        <%--<td style="width: 20%">标准现状：<span style="color:red">*</span></td>--%>
                        <%--<td>--%>
                        <%--<input id="bzxz" name="bzxz" class="mini-textbox" style="width:98%;"/>--%>
                        <%--</td>--%>
                        <%--<td style="width: 20%">素材来源：<span style="color:red">*</span></td>--%>
                        <%--<td>--%>
                        <%--<input id="scly" name="scly" class="mini-textbox" style="width:98%;"/>--%>
                        <%--</td>--%>
                        <%--</tr>--%>
                        <tr>
                            <td style="width: 20%">材料收集
                                <image src="${ctxPath}/styles/images/question.png"
                                       style="cursor: pointer;vertical-align: middle"
                                       title="应写明起草该标准所收集的材料，并将材料上传到附件汇总，附件类别为参考文件"/>
                                ：<span style="color:red">*</span></td>
                            <td colspan="3">
						        <textarea id="clsj" name="clsj" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:99%;height:50px;line-height:25px;" required
                                          label="材料收集" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                          allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                            </td>
                        </tr>

                        <tr>
                            <td style="width: 20%">标准化立项意见：<span style="color:red">*</span></td>
                            <td colspan="3">
						        <textarea id="bzhlxyj" name="bzhlxyj" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:99%;height:75px;line-height:25px;" required
                                          label="标准化立项意见" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                          allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                            </td>
                        </tr>
                        <%--<tr>--%>
                        <%--<td style="width: 20%">标准化立项意见：<span style="color:red">*</span></td>--%>
                        <%--<td colspan="3" style="min-width:170px;">--%>
                        <%--<input id="bzhlxyj" name="bzhlxyj" class="mini-textbox" style="width:98%;"/>--%>
                        <%--</td>--%>
                        <%--</tr>--%>

                        <tr>
                            <td style="width: 20%">是否需要复议：<span style="color:red">*</span></td>
                            <td>
                                <input id="sffy" name="sffy" class="mini-radiobuttonlist" style="width:98%;"
                                       repeatItems="3" repeatLayout="table" repeatDirection="horizontal"
                                       textfield="opinionText" valuefield="opinionValue"
                                       enabled="false"
                                       data="[ {'opinionText' : '需要复议','opinionValue' : 'yes'}
                                       ,{'opinionText' : '无需复议','opinionValue' : 'no'}
                                       ,{'opinionText' : '立项不通过','opinionValue' : 'reject'}
                                       ]"/>
                            </td>
                            <td style="width: 20%">复议是否通过：<span style="color:red">*</span></td>
                            <td>
                                <input id="fyjg" name="fyjg" class="mini-radiobuttonlist" style="width:98%;"
                                       repeatItems="3" repeatLayout="table" repeatDirection="horizontal"
                                       textfield="opinionText" valuefield="opinionValue"
                                       enabled="false"
                                       data="[ {'opinionText' : '通过','opinionValue' : 'yes'},{'opinionText' : '不通过','opinionValue' : 'no'}]"/>
                            </td>

                        </tr>

                        <tr>
                            <td style="width: 20%">是否发送供应商：<span style="color:red">*</span></td>
                            <td>
                                <input id="sffsgys" name="sffsgys" class="mini-radiobuttonlist" style="width:98%;"
                                       repeatItems="3" repeatLayout="table" repeatDirection="horizontal"
                                       textfield="opinionText" valuefield="opinionValue"
                                       enabled="false"
                                       data="[ {'opinionText' : '发送','opinionValue' : 'yes'},{'opinionText' : '不发送','opinionValue' : 'no'}]"/>
                            </td>
                        </tr>



                        <%--<tr>--%>
                        <%--<td style="width: 20%"><spring:message code="page.jsStandardManagementEdit.name13"/>：<span--%>
                        <%--style="color:red">*</span></td>--%>
                        <%--<td colspan="3">--%>
                        <%--<textarea id="standardAndApply" name="standardAndApply" class="mini-textarea rxc"--%>
                        <%--plugins="mini-textarea"--%>
                        <%--style="width:99.1%;height:75px;line-height:25px;"--%>
                        <%--label="<spring:message code="page.jsStandardManagementEdit.name7" />"--%>
                        <%--datatype="varchar" allowinput="true"--%>
                        <%--emptytext="<spring:message code="page.jsStandardManagementEdit.name14" />..."--%>
                        <%--mwidth="80" wunit="%" mheight="200"--%>
                        <%--hunit="px"></textarea>--%>
                        <%--</td>--%>
                        <%--</tr>--%>
                        <tr>
                            <td style="width: 25%"><spring:message code="page.jsStandardManagementEdit.name15"/>：<span
                                    style="color:red">*</span></td>
                            <td style="min-width:170px" colspan="3">
                                <input id="mainDraftIds" name="mainDraftIds" textname="mainDraftNames"
                                       property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: left;width: 14%;height:10px">模块负责人意见：</td>
                            <td colspan="3" height="50px">
                                <div class="mini-toolbar" style="margin-bottom: 5px;display: none"
                                     id="teamDraftToolBar">
                                    <a class="mini-button" id="teamDraftAddRow" plain="true" onclick="addTeamDraftRow">新增行</a>
                                    <a class="mini-button btn-red" id="removeTeamDraftRow" plain="true"
                                       onclick="removeTeamDraftRow">删除</a>
                                </div>
                                <div id="teamDraftGrid" class="mini-datagrid"

                                     allowResize="false"
                                     idField="id"
                                     url=" ${ctxPath}/standardManager/core/standardManagement/getTeamDraftList.do?applyId=${applyId}"
                                     autoload="true"
                                     multiSelect="true" showPager="false" showColumnsMenu="false"
                                     allowAlternating="true"
                                     style="height:180px;"
                                     allowCellSelect="true"
                                     allowCellWrap = "true"
                                     allowCellEdit="false"
                                     oncellbeginedit="OnCellBeginEdit"
                                >
                                    <div property="columns">
                                        <div type="checkcolumn" width="15"></div>
                                        <div field="isAgree" name="isAgree" width="40" headerAlign="center"
                                             align="center"
                                        >是否同意立项
                                            <input property="editor" class="mini-combobox"
                                                   style="width:98%;"
                                                   textField="value" valueField="key"
                                                   emptyText="<spring:message code="page.manualTopicEdit.name7" />..."
                                                   required="false" allowInput="false" showNullItem="true"
                                                   nullItemText="请选择..."
                                                   data="[{'key' : '同意','value' : '同意'}
                                                        ,{'key' : '不同意','value' : '不同意'}
                                                        ]"
                                            />
                                        </div>
                                        <div field="opinion" name="opinion" width="180" headerAlign="center"
                                             align="center"
                                        >负责人详细意见
                                            <input property="editor" class="mini-textarea" />
                                        </div>
                                        <div field="creatorName" name="creatorName" width="60" headerAlign="center"
                                             align="center"
                                        >负责人名称
                                            <input property="editor" class="mini-textbox"/>
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>


                        <tr>
                            <td style="width: 20%"><spring:message code="page.jsStandardManagementEdit.name16"/>：<span
                                    style="color:red">*</span></td>
                            <td style="min-width:170px" colspan="3">
                                <input id="yjUserIds" name="yjUserIds" textname="yjUserNames"
                                       property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%"><spring:message code="page.jsStandardManagementEdit.name17"/>：<span
                                    style="color:red">*</span></td>
                            <td colspan="3">
                                <input id="referStandardIds" name="referStandardIds" style="width:98%;"
                                       class="mini-buttonedit"
                                       showClose="true"
                                       oncloseclick="quoteStandardCloseClick()"
                                       textname="referStandardNames" single="false"
                                       allowInput="false" onbuttonclick="selectQuoteStandard()"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 20%"><spring:message code="page.jsStandardManagementEdit.name18"/>：<span
                                    style="color:red">*</span></td>
                            <td style="min-width:170px" colspan="3">
                                <input id="bzscIds" name="bzscIds" textname="bzscNmaes"
                                       property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:90%;height:34px;" allowinput="false" mainfield="no" single="false"/>
                                <a id="bzscSync" style="color:#44cef6;cursor: pointer;visibility: hidden"
                                   onclick="syncyjUser()"><spring:message
                                        code="page.jsStandardManagementEdit.name19"/></a>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 20%"><spring:message code="page.jsStandardManagementEdit.name20"/>：<span
                                    style="color:red">*</span></td>
                            <td style="min-width:170px" colspan="3">
                                <input id="ssqrIds" name="ssqrIds" textname="ssqrNames"
                                       property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:90%;height:34px;" allowinput="false" mainfield="no" single="false"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="width: 20%"><spring:message code="page.jsStandardManagementEdit.name21"/>：</td>
                            <td colspan="3">
                                <input id="standardId" style="width:90%" class="mini-buttonedit" showClose="true"
                                       name="standardId" textname="finalStandardName"
                                       oncloseclick="quoteStandardCloseClickOld('final')" allowInput="false"
                                       onbuttonclick="selectQuoteStandardOld('final')"/>
                                <span herf="#" style="color:#44cef6;cursor: pointer"
                                      onclick="jumpStandardInfo()"><spring:message
                                        code="page.jsStandardManagementEdit.name22"/></span>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>

            <fieldset id="fdBaseInfojh" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxBaseInfojh" onclick="bztoggleFieldSet(this, 'fdBaseInfojh')"
                               hideFocus/>
                        <spring:message code="page.jsStandardManagementEdit.name23"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 25%"><spring:message code="page.jsStandardManagementEdit.name24"/>
                                <div class="tooltip">
                                    <image src="${ctxPath}/styles/images/warn.png"
                                           style="margin-right:5px;vertical-align: middle;height: 15px"
                                           title="<spring:message code="page.jsStandardManagementEdit.name25" />"/>
                                </div>
                            </td>
                            <td style="min-width:170px">
                                <input id="draftTime" name="draftTime" class="mini-datepicker" format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                            <td style="width: 25%"><spring:message code="page.jsStandardManagementEdit.name26"/></td>
                            <td style="min-width:170px">
                                <input id="sjcaTime" name="sjcaTime" class="mini-datepicker" format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 25%">
                                <spring:message code="page.jsStandardManagementEdit.name27"/>
                                <div class="tooltip">
                                    <image src="${ctxPath}/styles/images/warn.png"
                                           style="margin-right:5px;vertical-align: middle;height: 15px"
                                           title="<spring:message code="page.jsStandardManagementEdit.name28" />"/>
                                </div>
                            </td>
                            <td style="min-width:170px">
                                <input id="solicitopinionsTime" name="solicitopinionsTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                            <td style="width: 25%"><spring:message code="page.jsStandardManagementEdit.name29"/></td>
                            <td style="min-width:170px">
                                <input id="sjzqTime" name="sjzqTime" class="mini-datepicker" format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 25%">
                                <spring:message code="page.jsStandardManagementEdit.name30"/>
                                <div class="tooltip">
                                    <image src="${ctxPath}/styles/images/warn.png"
                                           style="margin-right:5px;vertical-align: middle;height: 15px"
                                           title="<spring:message code="page.jsStandardManagementEdit.name31" />"/>
                                </div>
                            </td>
                            <td style="min-width:170px">
                                <input id="reviewTime" name="reviewTime" class="mini-datepicker" format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                            <td style="width: 25%"><spring:message code="page.jsStandardManagementEdit.name32"/></td>
                            <td style="min-width:170px">
                                <input id="sjpsTime" name="sjpsTime" class="mini-datepicker" format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 25%">
                                <spring:message code="page.jsStandardManagementEdit.name33"/>
                                <div class="tooltip">
                                    <image src="${ctxPath}/styles/images/warn.png"
                                           style="margin-right:5px;vertical-align: middle;height: 15px"
                                           title="<spring:message code="page.jsStandardManagementEdit.name34" />"/>
                                </div>
                            </td>
                            <td style="min-width:170px">
                                <input id="reportforapprovalTime" name="reportforapprovalTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                            <td style="width: 25%"><spring:message code="page.jsStandardManagementEdit.name35"/></td>
                            <td style="min-width:170px">
                                <input id="sjbpTime" name="sjbpTime" class="mini-datepicker" format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 25%">
                                <spring:message code="page.jsStandardManagementEdit.name36"/>
                                <div class="tooltip">
                                    <image src="${ctxPath}/styles/images/warn.png"
                                           style="margin-right:5px;vertical-align: middle;height: 15px"
                                           title="<spring:message code="page.jsStandardManagementEdit.name37" />"/>
                                </div>
                            </td>
                            <td style="min-width:170px">
                                <input id="applicationTime" name="applicationTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                            <td style="width: 25%"><spring:message code="page.jsStandardManagementEdit.name38"/></td>
                            <td style="min-width:170px">
                                <input id="sjyyTime" name="sjyyTime" class="mini-datepicker" format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>

            <fieldset id="fdMemberInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxMemberInfo" onclick="bztoggleFieldSet(this, 'fdMemberInfo')"
                               hideFocus/>
                        <spring:message code="page.jsStandardManagementEdit.name39"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px;width: 98% ;height: auto">
                    <div class="mini-toolbar">
                        <div class="searchBox">
                            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                                <li id="operateStandard" style="margin-left: 10px">
                                    <a id="operateAdd" class="mini-button" style="margin-right: 5px" plain="true"
                                       onclick="openStandardEditWindow('','','add','${applyId}')"><spring:message
                                            code="page.jsStandardManagementEdit.name40"/></a>
                                </li>
                            </form>
                        </div>
                    </div>
                    <div id="messageListGrid" class="mini-datagrid"
                         allowResize="false"
                         url=" ${ctxPath}/standardManager/core/standardManagement/getStandardList.do?applyId=${applyId}&isZqyj=${isZqyj}"
                         idField="applyId" autoload="true" allowCellWrap="true"
                         showPager="false">
                        <div property="columns">
                            <div type="checkcolumn" width="20"></div>
                            <div type="indexcolumn" align="center" width="20"><spring:message
                                    code="page.jsStandardManagementEdit.name41"/></div>
                            <div id="isOperation" name="action" cellCls="actionIcons" width="100" headerAlign="center"
                                 align="center"
                                 renderer="onAction" cellStyle="padding:0;"><spring:message
                                    code="page.jsStandardManagementEdit.name42"/>
                            </div>
                            <div field="chapter" name="chapter" sortField="chapter" width="80"
                                 headerAlign="center"
                                 align="center" allowSort="true" readonly><spring:message
                                    code="page.jsStandardManagementEdit.name43"/> <input readonly property="editor"
                                                                                         class="mini-textbox"/>
                            </div>
                            <div field="opinion" width="180" headerAlign="center" align="left" allowSort="false"
                                 renderer="renderFlag">
                                <spring:message code="page.jsStandardManagementEdit.name44"/>
                            </div>
                            <div field="feedback" align="center" width="180" headerAlign="center" allowSort="false">
                                <spring:message code="page.jsStandardManagementEdit.name45"/>
                            </div>
                            <div field="CREATE_BY_" align="center" width="180" headerAlign="center" allowSort="false">
                                <spring:message code="page.jsStandardManagementEdit.name46"/>
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
            <br>

            <fieldset id="fdAchievementInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxAchievementInfo"
                               onclick="bztoggleFieldSet(this, 'fdAchievementInfo')" hideFocus/>
                        <spring:message code="page.jsStandardManagementEdit.name47"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px;width: 98% ;height: auto">
                    <div class="mini-toolbar" id="ssyjButtons">
                        <a class="mini-button" id="addSsyj" plain="true" onclick="add_ssyjInfoRow"><spring:message
                                code="page.jsStandardManagementEdit.name48"/></a>
                        <a class="mini-button" id="removeSsyj" plain="true" onclick="remove_ssyjInfoRow"><spring:message
                                code="page.jsStandardManagementEdit.name49"/></a>
                    </div>
                    <div id="grid_ssyjInfo" class="mini-datagrid" allowResize="false"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false" showPager="false" allowCellWrap="true"
                         showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div type="indexcolumn" align="center" headerAlign="center" width="15"><spring:message
                                    code="page.jsStandardManagementEdit.name41"/></div>
                            <div field="opinion" align="center" headerAlign="center" width="80" renderer="render">
                                <spring:message code="page.jsStandardManagementEdit.name44"/>
                                <input property="editor" class="mini-textarea"/>
                            </div>
                            <div field="CREATE_BY_NAME" align="center" headerAlign="center" width="80"
                                 renderer="render"><spring:message code="page.jsStandardManagementEdit.name50"/>
                                <input readonly property="editor" class="mini-textarea"/>
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
            <br>

            <fieldset id="fdYanzhengInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxYanzhengInfo"
                               onclick="bztoggleFieldSet(this, 'fdYanzhengInfo')" hideFocus/>
                        <spring:message code="page.jsStandardManagementEdit.name51"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 14%;height: 10px"><spring:message
                                    code="page.jsStandardManagementEdit.name52"/>：
                            </td>
                            <td colspan="3">
                                <div style="margin-top: 10px;margin-bottom: 2px">
                                    <a id="addFile" class="mini-button"
                                       onclick="addStandardFile('${applyId}')"><spring:message
                                            code="page.jsStandardManagementEdit.name53"/></a>
                                    <span style="color: red"><spring:message
                                            code="page.jsStandardManagementEdit.name54"/></span>
                                </div>
                                <div id="fileListGrid" class="mini-datagrid"
                                     allowResize="false"
                                     idField="id"
                                     url="${ctxPath}/standardManager/core/standardManagement/getStandardFileList.do?applyId=${applyId}&stageKey=${stageKey}"
                                     autoload="true"
                                     multiSelect="true" showPager="false" showColumnsMenu="false"
                                     allowAlternating="true">
                                    <div property="columns">
                                        <div type="indexcolumn" align="center" width="20"><spring:message
                                                code="page.jsStandardManagementEdit.name41"/></div>
                                        <div field="fileName" width="140" headerAlign="center" align="center">
                                            <spring:message code="page.jsStandardManagementEdit.name55"/></div>
                                        <div field="typename" width="80" headerAlign="center" align="center">
                                            <spring:message code="page.jsStandardManagementEdit.name56"/>
                                        </div>
                                        <div field="fileSize" width="80" headerAlign="center" align="center">
                                            <spring:message code="page.jsStandardManagementEdit.name57"/></div>
                                        <div field="CREATE_TIME_" width="30" dateFormat="yyyy-MM-dd"
                                             headerAlign='center' align="center"><spring:message
                                                code="page.jsStandardManagementEdit.name58"/></div>
                                        <div field="userName" headerAlign='center' align='center' width="40">
                                            <spring:message code="page.jsStandardManagementEdit.name59"/></div>
                                        <div field="action" width="100" headerAlign='center' align="center"
                                             renderer="operationRenderer"><spring:message
                                                code="page.jsStandardManagementEdit.name42"/>
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
        </form>
    </div>
</div>

<%--引用弹框--%>
<div id="quoteStandardWindow" title="<spring:message code="page.jsStandardManagementEdit.name60" />" class="mini-window"
     style="width:750px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777"><spring:message code="page.jsStandardManagementEdit.name61"/>: </span>
        <input class="mini-textbox" width="130" id="filterStandardNumber" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777"><spring:message code="page.jsStandardManagementEdit.name11"/>: </span>
        <input class="mini-textbox" width="130" id="filterStandardName" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchQuoteStandard()"><spring:message
                code="page.jsStandardManagementEdit.name62"/></a>
    </div>
    <div class="mini-fit">
        <div id="quoteStandardGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="openRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="true"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/standardManager/core/standard/queryList.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="systemName" sortField="systemName" width="90" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardManagementEdit.name63"/>
                </div>
                <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardManagementEdit.name64"/>
                </div>
                <div field="standardNumber" sortField="standardNumber" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true"><spring:message code="page.jsStandardManagementEdit.name65"/>
                </div>
                <div field="standardName" sortField="standardName" width="180" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardManagementEdit.name66"/>
                </div>
                <div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardManagementEdit.name67"/>
                </div>
                <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center" align="center"
                     allowSort="true" renderer="statusRenderer"><spring:message
                        code="page.jsStandardManagementEdit.name68"/>
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px"
                           value="<spring:message code="page.jsStandardManagementEdit.name69" />"
                           onclick="quoteStandardOK()"/>
                    <input type="button" style="height: 25px;width: 70px"
                           value="<spring:message code="page.jsStandardManagementEdit.name70" />"
                           onclick="quoteStandardHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="quoteStandardWindowOld" title="<spring:message code="page.jsStandardManagementEdit.name60" />"
     class="mini-window" style="width:750px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input id="scene" class="mini-textbox" style="display: none"/>
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777"><spring:message code="page.jsStandardManagementEdit.name61"/>: </span>
        <input class="mini-textbox" width="130" id="filterStandardNumberOld" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777"><spring:message code="page.jsStandardManagementEdit.name11"/>: </span>
        <input class="mini-textbox" width="130" id="filterStandardNameOld" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchQuoteStandardOld()"><spring:message
                code="page.jsStandardManagementEdit.name62"/></a>
    </div>
    <div class="mini-fit">
        <div id="quoteStandardGridOld" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="openRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="false"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/standardManager/core/standard/queryList.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="systemName" sortField="systemName" width="90" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardManagementEdit.name63"/>
                </div>
                <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardManagementEdit.name64"/>
                </div>
                <div field="standardNumber" sortField="standardNumber" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true"><spring:message code="page.jsStandardManagementEdit.name65"/>
                </div>
                <div field="standardName" sortField="standardName" width="180" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardManagementEdit.name66"/>
                </div>
                <div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardManagementEdit.name67"/>
                </div>
                <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center" align="center"
                     allowSort="true" renderer="statusRenderer"><spring:message
                        code="page.jsStandardManagementEdit.name68"/>
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px"
                           value="<spring:message code="page.jsStandardManagementEdit.name69" />"
                           onclick="quoteStandardOKold()"/>
                    <input type="button" style="height: 25px;width: 70px"
                           value="<spring:message code="page.jsStandardManagementEdit.name70" />"
                           onclick="quoteStandardHideOld()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<%--标准反馈关联弹窗--%>
<div id="standardFeedbackWindow" title="<spring:message code="page.jsStandardManagementEdit.name71" />"
     class="mini-window" style="width:850px;height:600px;"
     showModal="true" showFooter="true" allowResize="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777"><spring:message code="page.jsStandardManagementEdit.name72"/>: </span>
        <input class="mini-textbox" width="130" id="applyId" textField="applyId" valueField="applyId"
               style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchStandardFeedback()"><spring:message
                code="page.jsStandardManagementEdit.name62"/></a>
    </div>

    <div class="mini-fit">
        <div id="standardFeedbackGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="onRowDblClicks"
             idField="applyId" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/standardManager/core/standardDemand/jsDemandList.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="applyId" sortField="applyId" width="90" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardManagementEdit.name72"/>
                </div>
                <div field="feedbackType" sortField="feedbackType" width="60" headerAlign="center" align="center"
                     renderer="typeRender"
                     allowSort="true"><spring:message code="page.jsStandardManagementEdit.name73"/>
                </div>
                <div field="applyUserName" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true"><spring:message code="page.jsStandardManagementEdit.name74"/>
                </div>
                <div field="applyUserDeptName" sortField="applyUserDeptName" width="140" headerAlign="center"
                     align="center"
                     align="center" allowSort="true"><spring:message code="page.jsStandardManagementEdit.name75"/>
                </div>
                <div field="doDeptNames" width="280" headerAlign="center" align="center" allowSort="false">
                    <spring:message code="page.jsStandardManagementEdit.name79"/></div>
                <div field="instStatus" width="70" headerAlign="center" align="center" renderer="onStatusRenderer"
                     allowSort="false"><spring:message code="page.jsStandardManagementEdit.name76"/>
                </div>
                <div field="currentProcessUser" sortField="currentProcessUser" width="70" align="center"
                     headerAlign="center" allowSort="false"><spring:message
                        code="page.jsStandardManagementEdit.name77"/>
                </div>
                <div field="applyTime" sortField="applyTime" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="100"
                     headerAlign="center" allowSort="true"><spring:message code="page.jsStandardManagementEdit.name78"/>
                </div>
            </div>
        </div>
    </div>

    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px"
                           value="<spring:message code="page.jsStandardManagementEdit.name69" />" onclick="okWindow()"/>
                    <input type="button" style="height: 25px;width: 70px"
                           value="<spring:message code="page.jsStandardManagementEdit.name70" />"
                           onclick="hideWindow()"/>
                </td>
            </tr>
        </table>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var formStatus = "${status}";
    var applyId = "${applyId}";
    var currentUserId = "${currentUserId}";
    var standardDemandObj =${standardDemandObj};
    var standardDemandApply = new mini.Form("#standardDemandApply");
    var currentTime = "${currentTime}";
    var currentUserName = "${currentUserName}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;

    var isBzrzs = ${isBzrzs};
    var isOperation = "${isOperation}";

    var selectStandardWindow = mini.get("selectStandardWindow");
    var standardFeedbackWindow = mini.get("standardFeedbackWindow");
    var quoteStandardWindow = mini.get("quoteStandardWindow");
    var quoteStandardWindowOld = mini.get("quoteStandardWindowOld");


    var messageListGrid = mini.get("messageListGrid");

    //标准列表
    var standardListGrid = mini.get("standardListGrid");
    //标准反馈
    var standardFeedbackGrid = mini.get("standardFeedbackGrid");
    //引用标准
    var quoteStandardGrid = mini.get("quoteStandardGrid");
    var quoteStandardGridOld = mini.get("quoteStandardGridOld");
    //草案附件列表
    var fileListGrid = mini.get("fileListGrid");
    var grid_ssyjInfo = mini.get("#grid_ssyjInfo");
    var teamDraftGrid = mini.get("#teamDraftGrid");
    //最终标准信息
    var finalStandardName = "${standardDemandObj.finalStandardName}"
    var finalStandardNumber = "${standardDemandObj.finalStandardNumber}"

    function renderFlag(e) {
        var record = e.record;
        var html = "<div style='line-height: 20px;' >";
        var opinion = record.opinion;
        if (opinion == null) {
            opinion = "";
        }
        html += '<span style="white-space:pre-wrap" >' + opinion + '</span>';
        html += '</div>'
        return html;
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

    function typeRender(e) {
        var record = e.record;
        var feedbackType = record.feedbackType;
        if (feedbackType == 'need') {
            return "技术标准需求反馈";
        } else if (feedbackType == 'problem') {
            return "标准使用问题反馈";
        }
    }

    /**
     * 表单弹出事件控制
     * @param ck
     * @param id
     */
    function toggleFieldSet(ck, id) {
        var dom = document.getElementById(id);
        if (ck.checked) {
            dom.className = "";
        } else {
            dom.className = "hideFieldset";
        }

    }

    function onStatusRenderer(e) {
        var record = e.record;
        var instStatus = record.instStatus;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, instStatus);
    }

    /**
     * 征求意见_行功能按钮
     */
    function onAction(e) {
        var record = e.record;
        var cellHtml = '';
        //增加修改按钮 所属节点/编制阶段提供修改功能
        // if (action == 'edit' || (action == 'task' && (isZqyj == 'yes'|| isPsg == 'yes'))) {

        if (isPsg == 'yes') {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title= ' + jsStandardManagementEdit_name + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="openStandardEditWindow(\'' + record.id + '\',\'\',\'update\',\'' + e.reviseInfoId + '\')">' + jsStandardManagementEdit_name + '</span>';

        } else if ((isZqyj == 'yes' && record.CREATE_BY_ == currentUserName)) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title= ' + jsStandardManagementEdit_name1 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="openStandardEditWindow(\'' + record.id + '\',\'\',\'update\',\'' + e.reviseInfoId + '\')">' + jsStandardManagementEdit_name1 + '</span>';

        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + jsStandardManagementEdit_name1 + ' style="color: silver" >' + jsStandardManagementEdit_name1 + '</span>';
        }
        //增加删除按钮 所属节点/编制阶段提供删除功能
        if (isZqyj == 'yes' && record.CREATE_BY_ == currentUserName) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + jsStandardManagementEdit_name2 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="removeUser(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + jsStandardManagementEdit_name2 + '</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + jsStandardManagementEdit_name2 + ' style="color: silver" >' + jsStandardManagementEdit_name2 + '</span>';
        }

        return cellHtml;
    }

    /**
     * 用户材料信息_删除用户
     * @param record
     */
    function removeUser(record) {

        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = messageListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(jsStandardManagementEdit_name3);
            return;
        }
        mini.confirm(jsStandardManagementEdit_name4, jsStandardManagementEdit_name5, function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/standardManager/core/standardManagement/deletePublic.do?",
                    method: 'POST',
                    showMsg: false,
                    data: {id: record.id},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            messageListGrid.load();
                        }
                    }
                });
            }
        });
    }

    /**
     * 附件_行功能按钮
     * @param record
     */
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';

        if (isOperation) {
            //增加预览按钮
            cellHtml = returnJsmmPreviewSpan(record.fileName, record.id, applyId, coverContent);
            //增加下载按钮
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + jsStandardManagementEdit_name6 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadJsjlFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + applyId + '\')">' + jsStandardManagementEdit_name6 + '</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + jsStandardManagementEdit_name2 + ' style="color: silver" >' + jsStandardManagementEdit_name2 + '</span>';
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + jsStandardManagementEdit_name2 + ' style="color: silver" >' + jsStandardManagementEdit_name2 + '</span>';
        }

        if ((record.CREATE_BY_ == currentUserId && action == 'task' && isDarft == "yes" && record.spzt == "darft")
            || (record.CREATE_BY_ == currentUserId && action == 'task' && isTdzj == "yes" && record.spzt == "tdzj")
            || (record.CREATE_BY_ == currentUserId && action == 'task' && isPsg == "yes" && record.spzt == "psg")
            || (record.CREATE_BY_ == currentUserId && action == 'task' && isFbg == "yes" && record.spzt == "fbg")
            || (record.CREATE_BY_ == currentUserId && action == 'task' && isYy == "yes" && record.spzt == "yy")
            //@mh 团队草案的时候可以上传附件
            || (record.CREATE_BY_ == currentUserId && action == 'edit' && record.spzt == "start")
            || (record.CREATE_BY_ == currentUserId && action == 'task' && isTeamDraft == "yes" && record.spzt == "team")
            || (record.CREATE_BY_ == currentUserId && action == 'task' && isLx == "yes" && record.spzt == "start")
            //@mh 复议必须上传附件
            || (record.CREATE_BY_ == currentUserId && action == 'task' && isSffy == "yes" && record.spzt == "fy")
            || (record.CREATE_BY_ == currentUserId && action == 'task' && isCash == "yes" && record.spzt == "cash")
            || (record.CREATE_BY_ == currentUserId && action == 'task' && isLxsc == "yes" && record.spzt == "lxsc")) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + jsStandardManagementEdit_name2 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="removeBook(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + jsStandardManagementEdit_name2 + '</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + jsStandardManagementEdit_name2 + ' style="color: silver" >' + jsStandardManagementEdit_name2 + '</span>';
        }
        return cellHtml;
    }

    /**
     * 证书附件维护_删除
     * @param record
     */
    function removeBook(record) {
        console.log("record值", record);
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = fileListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(jsStandardManagementEdit_name3);
            return;
        }
        mini.confirm(jsStandardManagementEdit_name4, jsStandardManagementEdit_name5, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                    instIds.push(r.instId);
                }

                _SubmitJson({
                    url: jsUseCtxPath + "/standardManager/core/standardManagement/deleteBookMeg.do?applyId=" + applyId,
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(','), instIds: instIds.join(','), id: record.id, fileName: record.fileName},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            fileListGrid.load();

                        }
                    }
                });
            }
        });

    }



</script>
</body>
</html>