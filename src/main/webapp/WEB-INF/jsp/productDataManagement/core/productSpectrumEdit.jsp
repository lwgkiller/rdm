<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>产品型谱</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style type="text/css">
        fieldset
        {
            border:solid 1px #aaaaaab3;
            min-width: 920px;
        }
        .hideFieldset
        {
            border-left:0;
            border-right:0;
            border-bottom:0;
        }
        .hideFieldset .fieldset-body
        {
            display:none;
        }
        .processStage{
            background-color: #ccc !important;
            font-size:15px !important;
            font-family:'微软雅黑' !important;
            text-align:center !important;
            vertical-align:middle !important;
            color: #201f35 !important;
            height: 30px !important;
            border-right:solid 0.5px #666;
        }
        .rmMem .mini-grid-cell-inner{
            color: red !important;
        }
    </style>
</head>

<body>
<div id="spEditToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveTopic" class="mini-button" onclick="saveBusinessInProcess()"><spring:message
                code="page.productSpectrumEdit.bc"/></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message
                code="page.productSpectrumEdit.gb"/></a>
    </div>
</div>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>


<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="tagIds" name="tagIds" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;"><spring:message
                        code="page.productSpectrumEdit.cpxp"/></caption>
            </table>
                <fieldset id="spBaseInfo" >
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox" checked id="checkboxBaseInfo" onclick="toggleFieldSet(this, 'spBaseInfo')" hideFocus/>
                            基本信息
                        </label>
                    </legend>

                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                        <table class="table-detail" cellspacing="1" cellpadding="0"style="height: 100%;width: 100%">
                            <tr>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.wlh"/>：<span style="color:red">*</span>
                                </td>
                                <td style="min-width:170px">
                                    <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"
                                           enabled="true"/>
                                </td>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.sjxh"/>：<span style="color:red">*</span>
                                </td>
                                <td style="min-width:170px">
                                    <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"
                                           enabled="false"/>
                                </td>
                            </tr>
                            <tr>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.cps"/>：<span style="color:red">*</span>
                                </td>
                                <td style="min-width:170px">
                                    <input id="departName" name="departId" class="mini-dep rxc" plugins="mini-dep"
                                           style="width:98%;height:34px"
                                           allowinput="false" textname="departName" single="true" initlogindep="false"/>
                                </td>

                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.cpzg"/>：<span style="color:red">*</span>
                                </td>
                                <td style="min-width:170px">
                                    <input id="productManager" name="productManagerId" textname="productManagerName"
                                           property="editor" class="mini-user rxc" plugins="mini-user"
                                           style="width:98%;height:34px;" allowinput="false" mainfield="no"
                                           single="true"
                                    />
                                </td>
                            </tr>
                            <tr>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.xsxh"/>：
                                </td>
                                <td style="min-width:170px">
                                    <input id="saleModel" name="saleModel" class="mini-textbox" style="width:98%;"
                                           enabled="true" emptyText="请填写正确的销售型号，否则保持为空"/>
                                </td>
                                <td style="text-align: center;width: 20%">立项时间：<span style="color:red">*</span>
                                <td style="min-width:170px">
                                    <input id="lxsj" name="lxsj" class="mini-datepicker" format="yyyy-MM-dd"
                                           showTime="false" showOkButton="true" showClearButton="false"
                                           allowInput="false"
                                           style="width:98%;"/>
                                </td>
                            </tr>
                            <tr>
                                <td style="text-align: center;width: 20%">研发状态确认时间：<span style="color:red">*</span>
                                </td>
                                <td style="min-width:170px">
                                    <input id="yfztqr" name="yfztqr" class="mini-datepicker" format="yyyy-MM-dd"
                                           showTime="false" showOkButton="true" showClearButton="false"
                                           allowInput="false"
                                           style="width:98%;"/>
                                </td>
                                <td style="text-align: center;width: 20%">制造状态确认时间：
                                <td style="min-width:170px">
                                    <input id="zzztqr" name="zzztqr" class="mini-datepicker" format="yyyy-MM-dd"
                                           showTime="false" showOkButton="true" showClearButton="false"
                                           allowInput="false"
                                           style="width:98%;"/>
                                </td>
                            </tr>
                            <tr>
                                <td style="text-align: center;width: 20%">销售状态确认时间：
                                </td>
                                <td style="min-width:170px">
                                    <input id="xsztqr" name="xsztqr" class="mini-datepicker" format="yyyy-MM-dd"
                                           showTime="false" showOkButton="true" showClearButton="false"
                                           allowInput="false"
                                           style="width:98%;"/>
                                </td>
                                <td style="text-align: center;width: 20%">项目名称：
                                </td>
                                <td style="min-width:170px">
                                    <input id="projectName" name="projectName" class="mini-textbox" style="width:98%;"
                                           enabled="true"/>
                                </td>
                            </tr>
                            <tr>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.pfjd"/>：<span style="color:red">*</span>
                                </td>
                                <td style="min-width:170px">
                                    <input id="dischargeStage" name="dischargeStage" class="mini-combobox"
                                           style="width:98%;"
                                           textField="text" valueField="key_" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="false"
                                           multiSelect="true"
                                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=PFBZ"/>


                                </td>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.yfzt"/>：
                                    <image src="${ctxPath}/styles/images/question.png"
                                           style="cursor: pointer;vertical-align: middle"
                                           title="在研：正在研发的产品
定型：已定型的产品
作废：整套图纸作废的产品"/>
                                    <span style="color:red">*</span>
                                </td>
                                <td style="min-width:170px">
                                    <input id="rdStatus" name="rdStatus" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key"
                                           emptyText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                           required="false" allowInput="false" showNullItem="true"
                                           nullItemText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                           data="[{'key' : '在研','value' : '在研'}
                                       ,{'key' : '定型','value' : '定型'}
                                       ,{'key' : '作废','value' : '作废'}
                                       ]"
                                    />
                                </td>

                            </tr>
                            <tr>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.cpsm"/>：
                                </td>
                                <td style="min-width:170px">
                                    <input id="productNotes" name="productNotes" class="mini-textbox" style="width:98%;"
                                           enabled="true"/>
                                </td>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.zzzt"/>：
                                    <image src="${ctxPath}/styles/images/question.png"
                                           style="cursor: pointer;vertical-align: middle"
                                           title="未生产：未产生任何实际整机产品
样机：只生产了样机
量产：批量生产
停产：当前不生产"/>
                                </td>
                                <td style="min-width:170px">
                                    <input id="manuStatus" name="manuStatus" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key"
                                           emptyText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                           required="false" allowInput="false" showNullItem="true"
                                           nullItemText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                           data="[{'key' : '未生产','value' : '未生产'}
                                       ,{'key' : '量产','value' : '量产'}
                                       ,{'key' : '样机','value' : '样机'}
                                       ,{'key' : '停产','value' : '停产'}
                                       ]"
                                    />

                            </tr>
                            <tr>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.xszt"/>：
                                    <image src="${ctxPath}/styles/images/question.png"
                                           style="cursor: pointer;vertical-align: middle"
                                           title="在售：正在销售
停售：当前已停售
不可售：不允许销售"/>
                                </td>
                                <td style="min-width:170px">
                                    <input id="saleStatus" name="saleStatus" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key"
                                           emptyText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                           required="false" allowInput="false" showNullItem="true"
                                           nullItemText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                           data="[{'key' : '在售','value' : '在售'}
                                       ,{'key' : '停售','value' : '停售'}
                                       ,{'key' : '不可售','value' : '不可售'}
                                       ]"
                                    />
                                </td>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.ghnwxs"/>：<span style="color:red">*</span>
                                </td>
                                <td style="min-width:170px">
                                    <input id="abroad" name="abroad" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key"
                                           emptyText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                           required="false" allowInput="false" showNullItem="true"
                                           nullItemText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                           multiSelect="true"
                                           data="[{'key' : '国内','value' : '国内'}
                                       ,{'key' : '出口','value' : '出口'}
                                       ]"
                                    />

                            </tr>
                            <tr>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.ghxsqy"/>：<span style="color:red">*</span>
                                </td>
                                <td style="min-width:170px">
                                    <input id="region" name="region" class="mini-combobox" style="width:98%"
                                           multiSelect="true"
                                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=CPXPGHXSQYGJ"
                                           valueField="name" textField="name"/>
                                </td>
                                <td style="text-align: center;width: 20%">销售认证：
                                </td>
                                <td style="min-width:170px">
                                    <input id="xsrz" name="xsrz" class="mini-combobox" style="width:98%"
                                           multiSelect="true"
                                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=CPXPXSRZ"
                                           valueField="name" textField="name"/>
                                </td>
                            </tr>
                            <tr>

                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.dsw"/>：
                                </td>
                                <td style="min-width:170px">
                                    <input id="pin4" name="pin4" class="mini-textbox" style="width:98%;"
                                           enabled="true"/>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.dbw"/>：
                                </td>
                                <td style="min-width:170px">
                                    <input id="pin8" name="pin8" class="mini-textbox" style="width:98%;"
                                           enabled="true"/>
                            </tr>
                            <tr>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.cpbq"/>:(底盘形式、动力形式、应用领域为必填)
                                </td>
                                <td colspan="3">
                                    <div class="mini-toolbar" style="margin-bottom: 5px" id="tagListToolBar">
                                        <a class="mini-button" id="tagAddRow" plain="true"
                                           onclick="addTagRow"><spring:message
                                                code="page.productSpectrumEdit.xzh"/></a>
                                        <a class="mini-button btn-red" id="tagRemoveRow" plain="true"
                                           onclick="removeTagRow"><spring:message
                                                code="page.productSpectrumEdit.sc"/></a>
                                    </div>
                                    <div id="tagGrid" class="mini-datagrid" allowResize="false" style="height:340px"
                                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="true"
                                         multiSelect="true" showColumnsMenu="false" allowAlternating="true"
                                         showPager="false"
                                         allowresize="true" sortmode="client"
                                         oncellbeginedit="gridInputCellBeginEdit"
                                         autoload="true"
                                    >
                                        <div property="columns">
                                            <div type="checkcolumn" width="50"></div>
                                            <%--<div type="tagId" width="45" headerAlign="center" align="center" visible="false">标签Id</div>--%>
                                            <div field="tagType" name="tagType" width="80" headerAlign="center"
                                                 align="center"
                                                 renderer="render"><spring:message code="page.productSpectrumEdit.lb"/>
                                                <input property="editor" class="mini-combobox" style="width:98%"
                                                       url="${ctxPath}/world/core/productSpectrum/tagType.do"
                                                       valueField="tagType" textField="tagType"
                                                />
                                            </div>
                                            <div field="tagId" displayField="tagName" name="settingMaterialCode"
                                                 width="120"
                                                 headerAlign="center"
                                                 align="center"
                                            ><spring:message code="page.productSpectrumEdit.bqmc"/>
                                                <input id="tagComb" property="editor" class="mini-combobox"
                                                       style="width:98%"
                                                       valueField="tagId" textField="tagName" multiSelect="true"
                                                />
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td style="text-align: center;width: 20%"><spring:message
                                        code="page.productSpectrumEdit.bz"/></td>
                                <td colspan="3">
                                    <input id="remark" name="remark" class="mini-textbox" style="width:98%;"
                                           enabled="true"/>
                            </tr>
                        </table>
                    </div>

                </fieldset>
<%--            </table>--%>
        </form>
        <br>

        <fieldset id="zypzb" class="hideFieldset" >
            <legend>
                <label style="font-size:17px">
                    <input type="checkbox" id="zypzbInfo" onclick="toggleFieldSet(this, 'zypzb')" hideFocus/>
                    <spring:message code="page.productSpectrumEdit.zypzb"/>
                </label>
            </legend>
            <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                <table class="table-detail" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                    <div class="mini-toolbar" style="margin-bottom: 5px" id="mainSettingsListToolBar">
                        <a class="mini-button" id="settingAddRow" plain="true" onclick="addSettingRow"><spring:message
                                code="page.productSpectrumEdit.xzh"/></a>
                        <a class="mini-button btn-red" id="settingRemoveRow" plain="true"
                           onclick="removeSettingRow"><spring:message
                                code="page.productSpectrumEdit.sc"/></a>
                        <a class="mini-button" id="addDefaultSetting" plain="true"
                           onclick="addDefaultSetting">添加默认配置</a>
                        <a class="mini-button" id="exportSetting" plain="true" onclick="exportSetting()">导出主要配置表</a>
                        <a class="mini-button" id="importSetting" plain="true" onclick="importSetting()">导入</a>
                    </div>
                    <div id="mainSettingGrid" class="mini-datagrid" allowResize="false" style="height:680px"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="true"
                         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                         allowresize="true" sortmode="client"
                         oncellbeginedit="settingsGridInputCellBeginEdit"
                         url="${ctxPath}/world/core/productSpectrum/mainSettingList.do?applyId=${businessId}"
                         autoload="true"
                    >
                        <div property="columns">
                            <div type="checkcolumn" width="50"></div>
                            <div field="settingTypeClass" allowSort="true" name="settingTypeClass" width="80"
                                 headerAlign="center"
                                 align="center"
                                 renderer="render">一级类别<span style="color:red">*</span>
                                <input property="editor" class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/world/core/productSpectrum/distinctType.do?key=mainSettingInit"
                                       valueField="tagType" textField="tagType"
                                />
                            </div>
                            <div field="settingType" width="80" headerAlign="center" align="center" renderer="render">
                                <spring:message code="page.productSpectrumEdit.lb"/><span style="color:red">*</span>
                                <input id="settingComb" property="editor" class="mini-combobox"
                                       style="width:98%"
                                       valueField="tagName" textField="tagName" multiSelect="true"
                                />
                            </div>
                            <div field="settingMaterialCode" name="settingMaterialCode" width="120" headerAlign="center"
                                 align="center"
                            ><spring:message code="page.productSpectrumEdit.wlbm"/>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="settingModel" name="settingModel" width="120" headerAlign="center"
                                 align="center"
                            ><spring:message code="page.productSpectrumEdit.xingh"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="supplyName" name="supplyName" width="120" headerAlign="center" align="center"
                            ><spring:message code="page.productSpectrumEdit.gysmc"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="sfxp" name="sfxp" width="40" headerAlign="center" align="center"
                            >是否选配
                                <input property="editor" id="sfxp" name="sfxp" class="mini-combobox" style="width:98%;"
                                       textField="value" valueField="key"
                                       required="false" allowInput="false"
                                       data="[{'key' : '是','value' : '是'}
                                       ,{'key' : '否','value' : '否'}
                                       ]"
                                />
                            </div>
                            <div field="settingRemark" name="settingRemark" width="120" headerAlign="center"
                                 align="center"
                            ><spring:message code="page.productSpectrumEdit.bz"/>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                        </div>
                    </div>
                </table>
            </div>
        </fieldset>
        <br>
        <fieldset id="zycsb" class="hideFieldset">
            <legend>
                <label style="font-size:17px">
                    <input type="checkbox" id="zycsbInfo" onclick="toggleFieldSet(this, 'zycsb')" hideFocus/>
                    <spring:message code="page.productSpectrumEdit.zycsb"/>
                </label>
            </legend>
            <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                <table class="table-detail" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                    <div class="mini-toolbar" style="margin-bottom: 5px" id="mainParamListToolBar">
                        <a class="mini-button" id="mainParamAddRow" plain="true" onclick="addParamRow"><spring:message
                                code="page.productSpectrumEdit.xzh"/></a>
                        <a class="mini-button btn-red" id="mainParamRemoveRow" plain="true"
                           onclick="removeParamRow"><spring:message
                                code="page.productSpectrumEdit.sc"/></a>
                        <a class="mini-button" id="addZDWParam" plain="true" onclick="addDefaultParam('mainParamZDW')">添加中大挖参数</a>
                        <a class="mini-button" id="addWXWParam" plain="true" onclick="addDefaultParam('mainParamWXW')">添加微小挖参数</a>
                        <a class="mini-button" id="addLWParam" plain="true" onclick="addDefaultParam('mainParamLW')">添加轮挖参数</a>
                        <a class="mini-button" id="exportParam" plain="true" onclick="exportParam()">导出主要参数表</a>
                        <a class="mini-button" id="importParam" plain="true" onclick="importParam()">导入</a>

                    </div>
                    <div id="mainParamGrid" class="mini-datagrid" allowResize="false" style="height:800px"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="true"
                         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                         allowresize="true" sortmode="client"
                         oncellbeginedit="paramGridInputCellBeginEdit"
                         url="${ctxPath}/world/core/productSpectrum/mainParamList.do?applyId=${businessId}"
                         autoload="true"
                    >
                        <div property="columns">
                            <div type="checkcolumn" width="50"></div>

                            <div field="paramTypeClass" name="paramTypeClass" allowSort="true" width="80"
                                 headerAlign="center"
                                 align="center"
                                 renderer="render">一级类别<span style="color:red">*</span>
                                <input property="editor" class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/world/core/productSpectrum/distinctType.do?key=${jsggType}"
                                <%--url="${ctxPath}/world/core/productSpectrum/distinctType.do?key=mainParamZDW"--%>
                                       valueField="tagType" textField="tagType"/>
                            </div>
                            <div field="paramType" width="80" headerAlign="center" align="center" renderer="render">
                                <spring:message
                                        code="page.productSpectrumEdit.cslx"/><span style="color:red">*</span>
                                <input id="paramComb" property="editor" class="mini-combobox"
                                       style="width:98%"
                                       valueField="tagName" textField="tagName" multiSelect="true"
                                />
                            </div>
                            <div field="paramUnit" name="paramUnit" width="120" allowSort="true" headerAlign="center"
                                 align="center"
                            >参数单位<span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="paramValue" name="paramValue" width="120" allowSort="true" headerAlign="center"
                                 align="center"
                            ><spring:message code="page.productSpectrumEdit.csz"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="remark" name="remark" width="120" headerAlign="center" align="center"
                            ><spring:message code="page.productSpectrumEdit.bz"/>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                        </div>
                    </div>
                </table>
            </div>
        </fieldset>
        <br>

        <fieldset id="workDevice" class="hideFieldset">
            <legend>
                <label style="font-size:17px">
                    <input type="checkbox" id="workDeviceInfo" onclick="toggleFieldSet(this, 'workDevice')"
                           hideFocus/>
                    后市场专用工装数据库
                </label>
            </legend>
            <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                <table class="table-detail" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                    <span class="mini-toolbar" style="margin-bottom: 5px" id="deviceListToolBar">
                        <a class="mini-button" id="deviceAddRow" plain="true" onclick="addDeviceRow"><spring:message
                                code="page.productSpectrumEdit.xzh"/></a>
                        <a class="mini-button btn-red" id="deviceRemoveRow" plain="true" onclick="removeDeviceRow"><spring:message
                                code="page.productSpectrumEdit.sc"/></a>
                    </span>
                    <span>
                        <a class="mini-button" id="showBoom" plain="true" onclick="showBoom">显示动臂信息</a>
                        <a class="mini-button" id="showStick" plain="true" onclick="showStick">显示斗杆信息</a>
                        <a class="mini-button" id="showPipe" plain="true" onclick="showPipe">显示破碎管路信息</a>
                        <a class="mini-button" id="showBasket" plain="true" onclick="showBasket">显示铲斗信息</a>
                        <a class="mini-button" id="showAll" plain="true" onclick="showAll">显示全部</a>
                        <a class="mini-button" id="hideAll" plain="true" onclick="hideAll">隐藏全部</a>
                    </span>
                    <div id="deviceGrid" class="mini-datagrid" allowResize="false" style="height:340px"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="true"
                         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                         allowresize="true" sortmode="client"
                         url="${ctxPath}/world/core/productSpectrum/workDeviceList.do?applyId=${businessId}"
                         autoload="true"
                    >
                        <div property="columns">
                            <div type="checkcolumn" width="25"></div>
                            <div field="saleModel" name="saleModel" width="60" headerAlign="center"
                                 align="center">销售型号
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="designModel" name="designModel" width="60" headerAlign="center"
                                 align="center">设计型号
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="boom" name="boom" width="60" headerAlign="center"
                                 align="center">动臂
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="boomLength" name="boomLength" width="60" headerAlign="center"
                                 align="center">动臂长度
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="boomMaterial" name="boomMaterial" width="60" headerAlign="center"
                                 align="center">动臂物料
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="boomAsse" name="boomAsse" width="60" headerAlign="center"
                                 align="center">动臂备件用总成
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="boomFrontAsse" name="boomFrontAsse" width="60" headerAlign="center"
                                 align="center">动臂备件用前叉总成
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="boomWeigth" name="boomWeigth" width="60" headerAlign="center"
                                 align="center">动臂重量
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="boomRemark" name="boomRemark" width="60" headerAlign="center"
                                 align="center">动臂备注
                                <input property="editor" class="mini-textbox"/>
                            </div>

                            <div field="stick" name="stick" width="60" headerAlign="center"
                                 align="center">斗杆
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="stickLength" name="stickLength" width="60" headerAlign="center"
                                 align="center">斗杆长度
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="stickMaterial" name="stickMaterial" width="60" headerAlign="center"
                                 align="center">斗杆物料
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="stickAsse" name="stickAsse" width="60" headerAlign="center"
                                 align="center">斗杆备件用总成
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="stickFrontAsse" name="stickFrontAsse" width="60" headerAlign="center"
                                 align="center">斗杆备件用马拉头总成
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="stickWeigth" name="stickWeigth" width="60" headerAlign="center"
                                 align="center">斗杆重量
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="stickRemark" name="stickRemark" width="60" headerAlign="center"
                                 align="center">斗杆备注
                                <input property="editor" class="mini-textbox"/>
                            </div>

                            <div field="pipe" name="pipe" width="60" headerAlign="center"
                                 align="center">破碎管路
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="pipeMaterial" name="pipeMaterial" width="60" headerAlign="center"
                                 align="center">破碎管路物料
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="pipeSize" name="pipeSize" width="60" headerAlign="center"
                                 align="center">破碎管路规格尺寸
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="pipeConnect" name="pipeConnect" width="60" headerAlign="center"
                                 align="center">破碎管路接头连接方式
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="pipeVehicle" name="pipeVehicle" width="60" headerAlign="center"
                                 align="center">破碎管路适用车辆范围
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="pipeRemark" name="pipeRemark" width="60" headerAlign="center"
                                 align="center">破碎管路备注
                                <input property="editor" class="mini-textbox"/>
                            </div>


                            <div field="basket" name="basket" width="60" headerAlign="center"
                                 align="center">铲斗
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="basketMaterial" name="basketMaterial" width="60" headerAlign="center"
                                 align="center">铲斗物料
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="basketCap" name="basketCap" width="60" headerAlign="center"
                                 align="center">铲斗斗容
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="basketType" name="basketType" width="60" headerAlign="center"
                                 align="center">铲斗斗型
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="basketWidth" name="basketWidth" width="60" headerAlign="center"
                                 align="center">铲斗斗宽
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="basketHeight" name="basketHeight" width="60" headerAlign="center"
                                 align="center">铲斗斗高
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="basketWeigth" name="basketWeigth" width="60" headerAlign="center"
                                 align="center">铲斗重量
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="basketRemark" name="basketRemark" width="60" headerAlign="center"
                                 align="center">铲斗备注
                                <input property="editor" class="mini-textbox"/>
                            </div>

                            <div field="openSize" name="openSize" width="60" headerAlign="center"
                                 align="center">属具开档尺寸
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="breaker" name="breaker" width="60" headerAlign="center"
                                 align="center">破碎锤
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="others" name="others" width="60" headerAlign="center"
                                 align="center">其他属具
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="remark" name="remark" width="60" headerAlign="center"
                                 align="center">备注
                                <input property="editor" class="mini-textbox"/>
                            </div>
                        </div>
                    </div>
                </table>
            </div>
        </fieldset>
        <br>
        <fieldset id="manualChange" class="hideFieldset" >
            <legend>
                <label style="font-size:17px">
                    <input type="checkbox" id="manualChangeInfo" onclick="toggleFieldSet(this, 'manualChange')" hideFocus/>
                    产品更改履历
                </label>
            </legend>
            <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                <table class="table-detail" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                    <div id="changeGrid" class="mini-datagrid" allowResize="false" style="height:340px"
                         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="true"
                         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                         allowresize="true" sortmode="client"
                         url="${ctxPath}/world/core/productSpectrum/getPdmInfo.do?productModel=${productModel}"
                         autoload="true"
                    >
                        <div property="columns">

                            <div field="docInfoUrl" name="docInfoUrl" width="150" headerAlign="center" align="center"
                                 allowSort="false" visible="false">跳转链接
                            </div>
                            <div field="number" name="number" width="120" headerAlign="center"
                                 align="center" renderer="jumpToPdmDetail"
                            >通知单号
                            </div>
                            <div field="docName" name="docName" width="120" headerAlign="center"
                                 align="center"
                            >通知单名称
                            </div>
                            <div field="zgProgress" name="zgProgress" width="120" headerAlign="center"
                                 align="center" renderer="zgProgressRender"
                            >主动整改完成情况
                            </div>
                            <div field="docCreator" name="docCreator" width="120" headerAlign="center"
                                 align="center"
                            >创建人
                            </div>
                            <div field="docCreateTime" name="docCreateTime" width="120" headerAlign="center"
                                 align="center"
                            >创建时间
                            </div>
                        </div>
                    </div>

                    <div class="mini-toolbar" style="margin-bottom: 5px" id="changeListToolBar">
                        <a class="mini-button" id="changeAddRow" plain="true" onclick="addChangeRow"><spring:message
                                code="page.productSpectrumEdit.xzh"/></a>
                        <a class="mini-button btn-red" id="changeRemoveRow" plain="true" onclick="removeChangeRow"><spring:message
                                code="page.productSpectrumEdit.sc"/></a>
                    </div>
                    <div id="manualChangeGrid" class="mini-datagrid" allowResize="false" style="height:340px"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="true"
                         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                         allowresize="true" sortmode="client"
                         url="${ctxPath}/world/core/productSpectrum/manualChangeList.do?applyId=${businessId}"
                         autoload="true"
                    >
                        <div property="columns">
                            <div type="checkcolumn" width="25"></div>
                            <div field="changeName" name="changeName" width="120" headerAlign="center"
                                 align="center">更改名称
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="changeDesc" name="changeDesc" width="180" headerAlign="center"
                                 align="center">更改概述
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="creatorName" name="creatorName" width="60" headerAlign="center"
                                 align="center"
                            >编制人员
                                <%--<input property="editor" class="mini-textbox"/>--%>
                            </div>
                            <div field="createTime" name="createTime" width="60" headerAlign="center"
                                 align="center"
                            >发布时间
                                <input property="editor" valueType="string" format="yyyy-MM-dd" allowInput="false"
                                       class="mini-datepicker"/>
                            </div>
                            <div field="remark" name="remark" width="120" headerAlign="center"
                                 align="center"
                            >备注
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <%--<div field="remark" name="remark" width="120" headerAlign="center"--%>
                            <%--align="center"--%>
                            <%-->附件上传--%>
                            <%--</div>--%>
                        </div>
                    </div>
                </table>
            </div>
        </fieldset>
        <br>

        <fieldset id="monthStatus" class="hideFieldset" >
            <legend>
                <label style="font-size:17px;">
                    <input type="checkbox" id="monthStatusInfo" onclick="toggleFieldSet(this, 'monthStatus')" hideFocus/>
                    状态变更记录
                </label>
            </legend>
            <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                <table class="table-detail" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                    <div class="mini-toolbar" style="margin-bottom: 5px" id="monthStatusListToolBar">
                        <a class="mini-button" id="monthAddRow" plain="true" onclick="addMonthRow"><spring:message
                                code="page.productSpectrumEdit.xzh"/></a>
                        <a class="mini-button btn-red" id="monthRemoveRow" plain="true" onclick="removeMonthRow"><spring:message
                                code="page.productSpectrumEdit.sc"/></a>
                    </div>
                    <div id="monthGrid" class="mini-datagrid" allowResize="false" style="height:340px"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="true"
                         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                         allowresize="true" sortmode="client"
                         url="${ctxPath}/world/core/productSpectrum/monthList.do?applyId=${businessId}"
                         autoload="true"
                    >
                        <div property="columns">
                            <div type="checkcolumn" width="50"></div>
                            <div field="monthTime" name="monthTime" width="120" allowSort="true" headerAlign="center" align="center"
                            ><spring:message code="page.productSpectrumEdit.sj"/>
                                <input property="editor" id="monthTime" name="monthTime" class="mini-datepicker"
                                       format="yyyy-MM-dd" valueType="String" allowInput="false" dataType="String"
                                       showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                            </div>
                            <div field="changeNumber" name="changeNumber" width="120" allowSort="true" headerAlign="center"
                                 align="center"
                            >变更单号
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="changeTarget" name="changeTarget" width="120" allowSort="true" headerAlign="center"
                                 align="center"
                            >调整字段
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="changeDesc" name="changeDesc" width="120" allowSort="true" headerAlign="center"
                                 align="center"
                            >变更内容
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="changePersonName" name="changePersonName" width="120" allowSort="true" headerAlign="center"
                                 align="center"
                            >变更人员
                            </div>
                            <div field="remark" name="remark" width="120" headerAlign="center" align="center"
                            ><spring:message code="page.productSpectrumEdit.bz"/>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                        </div>
                    </div>
                </table>
            </div>
        </fieldset>
    </div>
</div>

<div id="importWindow" title="导入" class="mini-window" style="width:750px;height:300px;"
     showModal="true" showFooter="false" allowResize="true">
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="doImport()">导入</a>
        <a id="closeImportWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>

    <div class="mini-fit" style="font-size: 14px;margin-top: 50px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr id="paramsTypeDisplay">
                    <td style="width: 30%">导入类型：<span style="color:red">*</span></td>
                    <td style="width: 70%;">
                        <input id="paramType" name="paramType" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key"
                               emptyText="请选择模板类型..."
                               required="false" allowInput="false"
                               multiSelect="false"
                               data="[{'key' : '中大挖','value' : '中大挖'}
                                       ,{'key' : '微小挖','value' : '微小挖'}
                                       ,{'key' : '轮挖','value' : '轮挖'}
                                       ]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>


<form id="excelForm" action="${ctxPath}/world/core/productSpectrum/exportParam.do?applyId=${businessId}" method="post"
      target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<form id="settingForm" action="${ctxPath}/world/core/productSpectrum/exportSetting.do?applyId=${businessId}"
      method="post"
      target="excelIFrame">
    <input type="hidden" name="filter" id="settingFilter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var nodeVarsStr = '${nodeVars}';
    var obj =${obj};
    var instId = "${instId}";
    var menuType = "${menuType}";
    var jsggType = "${jsggType}";
    var changeType = "${changeType}";
    var formBusiness = new mini.Form("#formBusiness");
    var monthGrid = mini.get("monthGrid");
    var changeGrid = mini.get("changeGrid");
    var manualChangeGrid = mini.get("manualChangeGrid");
    var mainParamGrid = mini.get("mainParamGrid");
    var tagGrid = mini.get("tagGrid");
    var tagListGrid = mini.get("tagListGrid");
    var mainSettingGrid = mini.get("mainSettingGrid");
    var deviceGrid = mini.get("deviceGrid");
    var selectTagWindow = mini.get("selectTagWindow");
    var tagTypeSearch = "";
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var importWindow = mini.get("importWindow");
    var businessId = "${businessId}";
    var importType = "";
    var paramType = "";
    //..
    var stageName = "";
    $(function () {
        formBusiness.setData(obj);
        var url = "${ctxPath}/world/core/productSpectrum/tagListGrid.do?applyId=${businessId}&tagIds=" + mini.get(tagIds).getValue();
        tagGrid.load(url);
        if (action == 'detail') {
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
            detailModel();
        } else if (action == "spEdit") {
            $("#spEditToolBar").show();
            detailModel();
        } else if (action == 'task') {
            taskActionProcess();
        }
        if (action=='edit') {
            mini.get("designModel").setEnabled(true);
        }
        //这里定义一下不同编辑类型的显示
        if (changeType == "销售状态") {
            // 销售状态可以更改
            mini.get("xsztqr").setEnabled(true);
            mini.get("saleStatus").setEnabled(true);
            mini.get("abroad").setEnabled(true);
            mini.get("region").setEnabled(true);
            mini.get("saleModel").setEnabled(true);
            mini.get("xsztqr").setEnabled(true);
        } else if (changeType == "研发状态") {
            mini.get("rdStatus").setEnabled(true);
            mini.get("yfztqr").setEnabled(true);
        } else if (changeType == "其他内容") {
            editModel();
        } else if (changeType == "制造状态") {
            mini.get("manuStatus").setEnabled(true);
            mini.get("zzztqr").setEnabled(true);
        }
        //隐藏部分工装属性
        deviceGrid.hideColumns(["boomMaterial","boomAsse","boomFrontAsse","boomWeigth","boomRemark"]);
        deviceGrid.hideColumns(["stickMaterial","stickAsse","stickFrontAsse","stickWeigth","stickRemark"]);
        deviceGrid.hideColumns(["pipeMaterial","pipeSize","pipeConnect","pipeVehicle","pipeRemark"]);
        deviceGrid.hideColumns(["basketMaterial","basketCap","basketType","basketWidth","basketHeight","basketWeigth","basketRemark"]);
        //自动合并
        deviceGrid.on("load", function (e) {deviceGrid.mergeColumns(["saleModel","designModel","basket","openSize"]);});

    });


    function detailModel() {
        formBusiness.setEnabled(false);
        monthGrid.setAllowCellEdit(false);
        mainParamGrid.setAllowCellEdit(false);
        mainSettingGrid.setAllowCellEdit(false);
        tagGrid.setAllowCellEdit(false);
        manualChangeGrid.setAllowCellEdit(false);
        deviceGrid.setAllowCellEdit(false);
        mini.get("monthStatusListToolBar").hide();
        mini.get("mainParamListToolBar").hide();
        mini.get("mainSettingsListToolBar").hide();
        mini.get("tagListToolBar").hide();
        mini.get("changeListToolBar").hide();
        mini.get("deviceListToolBar").hide();
    }

    function editModel() {
        formBusiness.setEnabled(true);
        mini.get("materialCode").setEnabled(false);
        mini.get("designModel").setEnabled(false);
        monthGrid.setAllowCellEdit(true);
        mainParamGrid.setAllowCellEdit(true);
        mainSettingGrid.setAllowCellEdit(true);
        tagGrid.setAllowCellEdit(true);
        manualChangeGrid.setAllowCellEdit(true);
        deviceGrid.setAllowCellEdit(true);
        mini.get("monthStatusListToolBar").show();
        mini.get("mainParamListToolBar").show();
        mini.get("mainSettingsListToolBar").show();
        mini.get("tagListToolBar").show();
        mini.get("changeListToolBar").show();
        mini.get("deviceListToolBar").show();
    }


    function saveBusinessInProcess() {
        var formData = getData();
        var materialCode = $.trim(formData.materialCode);
        if (!materialCode) {
            mini.alert("请填写物料号！");
            return;
        }
        var designModel = $.trim(formData.designModel);
        if (!designModel) {
            mini.alert("请填写设计型号！");
            return;
        }
        if (formData.saleModel && !formData.saleModel.startsWith("X")) {
            mini.alert("请填写正确的销售型号！");
            return;
        }
        var checkResult = checkDesignModelValid();
        if (!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        }

        $.ajax({
            url: jsUseCtxPath + '/world/core/productSpectrum/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        mini.alert("数据保存成功", "提示信息", function () {
                            window.location.reload();
                        });
                    } else {
                        mini.alert("数据保存失败：" + data.message);
                    }
                }
            }
        });
    }


    function addMonthRow() {
        var row = {"changePersonName": currentUserName};
        monthGrid.addRow(row);
    }


    function removeMonthRow() {
        var selecteds = monthGrid.getSelecteds();
        monthGrid.removeRows(selecteds);
    }

    function addParamRow() {
        var row = {}
        mainParamGrid.addRow(row);
    }


    function removeParamRow() {
        var selecteds = mainParamGrid.getSelecteds();
        mainParamGrid.removeRows(selecteds);
    }

    function addChangeRow() {
        var row = {"creatorName": currentUserName}
        manualChangeGrid.addRow(row);
    }


    function removeChangeRow() {
        var selecteds = manualChangeGrid.getSelecteds();
        manualChangeGrid.removeRows(selecteds);
    }

    function addDeviceRow() {
        var row = {}
        deviceGrid.addRow(row);
    }


    function removeDeviceRow() {
        var selecteds = deviceGrid.getSelecteds();
        deviceGrid.removeRows(selecteds);
    }

    function addSettingRow() {
        var row = {}
        mainSettingGrid.addRow(row);
    }

    function addDefaultSetting() {
        //后台查配置,前端加到表格中
        var rows = {};
        $.ajax({
            url: jsUseCtxPath + '/world/core/productSpectrum/getDefaultSettingList.do',
            type: 'post',
            async: false,
            // data: mini.encode(param),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    rows = data;
                }
            }
        });
        mainSettingGrid.addRows(rows);
    }

    function addDefaultParam(type) {
        //后台查配置,前端加到表格中
        var rows = {};
        $.ajax({
            url: jsUseCtxPath + '/world/core/productSpectrum/getDefaultParamList.do?type=' + type,
            type: 'post',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    rows = data;
                }
            }
        });
        mainParamGrid.addRows(rows);
        paramType = type;
    }


    function exportSetting() {
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
        $("#settingFilter").val(mini.encode(params));
        var settingForm = $("#settingForm");
        settingForm.submit();
    }


    function exportParam() {
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


    function removeSettingRow() {
        var selecteds = mainSettingGrid.getSelecteds();
        mainSettingGrid.removeRows(selecteds);
    }

    function addTagRow() {
        var row = {}
        tagGrid.addRow(row);
    }


    function removeTagRow() {
        var selecteds = tagGrid.getSelecteds();
        tagGrid.removeRows(selecteds);
    }


    function selectTagClick() {
        selectTagWindow.show();
        searchTag();
    }

    //查询标签
    function searchTag() {
        var queryParam = [];
        var tagName = $.trim(mini.get("tagName").getValue());
        if (tagName) {
            queryParam.push({name: "tagName", value: tagName});
        }
        var tagStatus = $.trim(mini.get("tagStatus").getValue());
        if (tagStatus) {
            queryParam.push({name: "tagStatus", value: tagStatus});
        }

        var inputList = '';
        inputList = tagListGrid;
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = inputList.getPageIndex();
        data.pageSize = inputList.getPageSize();
        data.sortField = inputList.getSortField();
        data.sortOrder = inputList.getSortOrder();
        //查询
        inputList.load(data);
    }

    function clearSearchTag() {
        mini.get("tagName").setValue("");
        mini.get("tagStatus").setValue("");
        searchTag();
    }


    function selectTagOK() {
        var inputList = '';
        inputList = tagListGrid;
        // 这里要改成多选，只返回id，用逗号拼接
        var selectRows = inputList.getSelecteds();
        if (selectRows) {
            var checkReq = [];
            var names = [];
            for (var i = 0; i < selectRows.length; i++) {
                var row = selectRows[i];
                if (row.key_ == "是") {
                    checkReq.push(row.text);

                }
                if (row.text != "无") {
                    names.push(row.text);
                }
            }
            var idsStr = ids.join(',');
            var namesStr = names.join(',');
            mini.get("productTags").setValue(namesStr);
            mini.get("productTags").setText(namesStr);
        } else {
            mini.alert(productSpectrumEdit_qxzytsj);
            return;
        }
        selectTagHide();
    }

    function selectTagHide() {
        selectTagWindow.hide();
        tagListGrid.deselectAll(true);
    }

    function setTagType(e) {
        var record = e.record;
        tagTypeSearch = e.selected.tagType;
    }

    function gridInputCellBeginEdit(e) {
        var field = e.field;
        var record = e.record;
        //当点击绑定值字段时处理。
        if (field == 'tagId') {
            if (!record.tagType) {
                mini.alert(productSpectrumEdit_qxzbqlb)
                return;
            }
            var url = "${ctxPath}/world/core/productSpectrum/tagName.do?tagType=" + record.tagType;
            mini.get("tagComb").load(url);
        }
    }

    function settingsGridInputCellBeginEdit(e) {
        var field = e.field;
        var record = e.record;
        //当点击绑定值字段时处理。
        var key = "mainSettingInit";
        if (field == 'settingType') {
            if (!record.settingTypeClass) {
                mini.alert("请先选择配置表一级类别！")
                return;
            }
            var url = "${ctxPath}/world/core/productSpectrum/getSubClass.do?className=" + record.settingTypeClass + "&key=" + key;
            mini.get("settingComb").load(url);
        }
    }

    function paramGridInputCellBeginEdit(e) {
        var field = e.field;
        var record = e.record;
        // 这个要加判断的
        var key = "mainParamZDW";
        if (jsggType == "微小挖") {
            key = "mainParamWXW"
        } else if (jsggType == "轮挖") {
            key = "mainParamLW"
        }

        //当点击绑定值字段时处理。
        if (field == 'paramType') {
            if (!record.paramTypeClass) {
                mini.alert("请先选择主要参数表一级类别")
                return;
            }
            var url = "${ctxPath}/world/core/productSpectrum/getSubClass.do?className=" + record.paramTypeClass + "&key=" + key;
            mini.get("paramComb").load(url);
        }
    }

    function checkEditRequired(formData) {
        if (!formData) {
            mini.alert(productSpectrumEdit_btx);
            return false;
        }
        if (!$.trim(formData.designModel)) {
            mini.alert(productSpectrumEdit_sjxhbnwk);
            return false;
        }
        if (!$.trim(formData.departName)) {
            mini.alert(productSpectrumEdit_cpsbnwk);
            return false;
        }
        if (!$.trim(formData.productManagerId)) {
            mini.alert(productSpectrumEdit_cpzgbnwk);
            return false;
        }
        return true;
    }

    function checkTagRequired() {
        //检查标签三个必填项
        var tagGridData = tagGrid.getData();
        if (tagGridData.length < 3) {
            mini.alert(productSpectrumEdit_qxzdlxsdpxs);
            return false;
        }
        var dl_cnt = 0;
        var dp_cnt = 0;
        var yy_cnt = 0;
        for (var i = 0; i < tagGridData.length; i++) {
            if (tagGridData[i].tagType == "动力形式" && tagGridData[i].tagName != undefined && tagGridData[i].tagName != "") {
                dl_cnt += 1;
            } else if (tagGridData[i].tagType == "底盘形式" && tagGridData[i].tagName != undefined && tagGridData[i].tagName != "") {
                dp_cnt += 1;
            } else if (tagGridData[i].tagType == "应用领域" && tagGridData[i].tagName != undefined && tagGridData[i].tagName != "") {
                yy_cnt += 1;
            }
        }
        if (dl_cnt < 1) {
            mini.alert(productSpectrumEdit_qxzdlxs);
            return false;
        } else if (dp_cnt < 1) {
            mini.alert(productSpectrumEdit_qxzdpxs);
            return false;
        } else if (yy_cnt < 1) {
            mini.alert(productSpectrumEdit_qxzyyly);
            return false;
        }

        return true;
    }

    function checkSettingRequired() {

        //检查主要配置表必填
        var mainSettingGridData = mainSettingGrid.getData();
        if (mainSettingGridData.length < 3) {
            mini.alert("请添加默认配置项！");
            return false;
        }
        for (var i = 0; i < mainSettingGridData.length; i++) {
            if (mainSettingGridData[i].settingType == undefined || mainSettingGridData[i].settingType == "") {
                mini.alert(productSpectrumEdit_pzblb);
                return false;
            }
            else {
                if (mainSettingGridData[i].supplyName == undefined || mainSettingGridData[i].supplyName == "") {
                    mini.alert(productSpectrumEdit_qtx + mainSettingGridData[i].settingType + "供应商名称");
                    return false;
                }
                if (mainSettingGridData[i].settingModel == undefined || mainSettingGridData[i].settingModel == "") {
                    mini.alert(productSpectrumEdit_qtx + mainSettingGridData[i].settingType + productSpectrumEdit_xh);
                    return false;
                }
            }
        }
        return true;
    }

    function checkParamRequired() {

        //检查主要配置表必填
        var paramGridData = mainParamGrid.getData();
        if (paramGridData.length < 3) {
            mini.alert("请添加默认参数项！");
            return false;
        }
        for (var i = 0; i < paramGridData.length; i++) {
            if (paramGridData[i].paramTypeClass == undefined || paramGridData[i].paramTypeClass == "") {
                mini.alert("请选择参数表一级类别！");
                return false;
            }
            if (paramGridData[i].paramType == undefined || paramGridData[i].paramType == "") {
                mini.alert("请选择参数类别！");
                return false;
            }

            if (paramGridData[i].paramValue == undefined || paramGridData[i].paramValue == "") {
                mini.alert("请填写" + paramGridData[i].paramType + "参数值");
                return false;
            }


        }
        return true;
    }


    function jumpToPdmDetail(e) {
        //显示更改单号，点击跳转
        var record = e.record;
        var number = record.number;
        if (!number) {
            number = '';
        }
        var url = record.docInfoUrl;
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="jumpPdmRow(\'' + url + '\')">' + number + '</a>';
        return s;
    }

    function jumpPdmRow(url) {
        var winObj = window.open(url);
    }

    function zgProgressRender(e) {
        //显示整改进度,从CRM ESB接口获取数据
        var record = e.record;
        var number = record.number;
        if (!number) {
            return "更改单号为空"
        }
        var s = "";
        if (number.startWith("JSTZ")) {
            $.ajax({
                url: jsUseCtxPath + '/world/core/productSpectrum/getZgProcessFromCrm?jstzNo=' + number,
                type: 'post',
                async: false,
                // data: mini.encode(param),
                contentType: 'application/json',
                success: function (data) {
                    if (data) {
                        if (data.success) {
                            s += data.data;

                        } else {
                            s += data.message;
                        }
                    }
                }
            });
        }
        return s;
    }


    function getData() {
        var formData = _GetFormJsonMini("formBusiness");
        formData.materialCode=$.trim(formData.materialCode);
        formData.saleModel=$.trim(formData.saleModel);
        formData.pin4=$.trim(formData.pin4);
        formData.pin8=$.trim(formData.pin8);
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_demandGrid) {
            delete formData.SUB_demandGrid;
        }
        if (tagGrid.getData().length > 0) {
            var tagGridData = tagGrid.getData();

            var tagList = [];
            var tagIdList = [];

            for (var i = 0; i < tagGridData.length; i++) {
                tagList.push(tagGridData[i].tagName);
                tagIdList.push(tagGridData[i].tagId);
            }
            var tagStr = tagList.join(",");
            var tagIdStr = tagIdList.join(",");
            formData.tagNames = tagStr;
            formData.tagIds = tagIdStr;

        }

        if (mainSettingGrid.getChanges().length > 0) {
            formData.mainSettingGrid = mainSettingGrid.getChanges();
        }
        if (mainParamGrid.getChanges().length > 0) {
            formData.mainParamGrid = mainParamGrid.getChanges();
        }
        if (manualChangeGrid.getChanges().length > 0) {
            formData.manualChangeGrid = manualChangeGrid.getChanges();
        }
        if (monthGrid.getChanges().length > 0) {
            formData.monthGrid = monthGrid.getChanges();
        }
        if (deviceGrid.getChanges().length > 0) {
            formData.deviceGrid = deviceGrid.getChanges();
        }
        return formData;
    }

    //保存草稿
    function saveDraft(e) {
        var formData = getData();
        var materialCode = $.trim(formData.materialCode);
        if (!materialCode) {
            mini.alert("请填写物料号！");
            return;
        }
        var designModel = $.trim(formData.designModel);
        if (!designModel) {
            mini.alert("请填写设计型号！");
            return;
        }
        if (formData.saleModel && !formData.saleModel.startsWith("X")) {
            mini.alert("请填写正确的销售型号！");
            return;
        }
        var tagGridData = tagGrid.getData();

        for (var i = 0; i < tagGridData.length; i++) {
            if (tagGridData[i].tagType == undefined || tagGridData[i].tagType == "") {
                mini.alert("产品标签类别及名称不能为空,请删除空白行")
                return;
            }
            if (tagGridData[i].tagName == undefined || tagGridData[i].tagName == "") {
                mini.alert("产品标签类别及名称不能为空！")
                return;
            }
        }
        var checkResult = checkDesignModelValid();
        if (!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        }

        window.parent.saveDraft(e);
    }

    //检查型谱表中是否存在不是本id的materialCode和designModel
    function checkDesignModelValid() {
        var designModel = mini.get("designModel").getValue();
        var materialCode = mini.get("materialCode").getValue();
        var id = mini.get("id").getValue();
        if (!designModel) {
            return {"result": false, "message": "请填写设计型号！"};
        }
        if (!materialCode) {
            return {"result": false, "message": "请填写物料号！"};
        }
        var result={"result": true, "message": ""};
        $.ajax({
            url: jsUseCtxPath + '/world/core/productSpectrum/checkExsit.do?designModel='
                + designModel+"&materialCode="+materialCode+"&id="+id ,
            async: false,
            success: function (returnData) {
                if (returnData.success) {
                    //已经存在
                    result= {"result": false, "message": returnData.message};
                }
                else {
                    result=  {"result": true, "message": returnData.message};
                }
            }
        });
        return result;
    }


    //发起流程
    function startProcess(e) {
        //必填字段检查
        var formValid = validSpectrum();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        //型号是否已存在的检查
        var checkResult = checkDesignModelValid();
        if (!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        }
        window.parent.startProcess(e);
    }

    function validSpectrum() {
        var productManager = $.trim(mini.get("productManager").getValue());
        if (!productManager) {
            return {"result": false, "message": "请选择产品主管！"};
        }
        var designModel = mini.get("designModel").getValue();
        if (!designModel) {
            return {"result": false, "message": "请填写设计型号"};
        }
        //这里要要争罗马字符
        if (hasRomanCharacters(designModel)) {
            return {"result": false, "message": "设计型号中不能包含罗马字符及空格"};
        }
        var departName = $.trim(mini.get("departName").getValue());
        if (!departName) {
            return {"result": false, "message": "请选择产品所！"};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": "请填写物料号！"};
        }
        var lxsj = $.trim(mini.get("lxsj").getValue());
        if (!lxsj) {
            return {"result": false, "message": "请选择立项时间！"};
        }

        var rdStatus = $.trim(mini.get("rdStatus").getValue());
        if (!rdStatus) {
            return {"result": false, "message": "请选择研发状态！"};
        }
        var yfztqr = $.trim(mini.get("yfztqr").getValue());
        if (!yfztqr) {
            return {"result": false, "message": "请选择研发状态确认时间！"};
        }
        var abroad = $.trim(mini.get("abroad").getValue());
        if (!abroad) {
            return {"result": false, "message": "请选择规划内外销售！"};
        }
        var region = $.trim(mini.get("region").getValue());
        if (!region) {
            return {"result": false, "message": "请选择规划销售区域或国家！"};
        }
        var saleModel = $.trim(mini.get("saleModel").getValue());
        if (saleModel && !saleModel.startsWith("X")) {
            return {"result": false, "message": "请填写正确的销售型号！"};
        }

        var checkResult = checkTagRequired();
        if (!checkResult) {
            return;
        }
        var checkResult = checkSettingRequired();
        if (!checkResult) {
            return;
        }
        var checkResult = checkParamRequired();
        if (!checkResult) {
            return;
        }

        return {"result": true};


    }

    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
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
        if (stageName != 'start') {
            detailModel();
        }

    }

    function projectApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start') {
            //必填字段检查
            var formValid = validSpectrum();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
            //型号是否已存在的检查
            var checkResult = checkDesignModelValid();
            if (!checkResult.result) {
                mini.alert(checkResult.message);
                return;
            }
        }

        //检查通过
        window.parent.approve();
    }

    function hasRomanCharacters(str) {
        //这里加了两个空格 一个中文一个英文
        var romanRegex = /[ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩⅪⅫ  ]/i;
        return romanRegex.test(str);
    }

    //@mh 2023年5月17日15:34:56 添加导入

    function importParam() {
        if (!businessId) {
            mini.alert("请先保存草稿！");
            return
        }
        importType = "param";
        document.getElementById("paramsTypeDisplay").style.display = "";
        importWindow.show();
    }
    function importSetting() {
        if (!businessId) {
            mini.alert("请先保存草稿！");
            return
        }

        importType = "setting";
        document.getElementById("paramsTypeDisplay").style.display = "none";
        importWindow.show();
    }


    //清空文件
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }

    //触发文件选择
    function uploadFile() {
        $("#inputFile").click();
    }

    //文件类型判断及文件名显示
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix =='xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传xls文件！');
            }
        }
    }
    //关闭导入
    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        if (importType == "param") {
            mainParamGrid.load();
        } else {
            mainSettingGrid.load();
        }
    }


    function doImport() {
        debugger;
        if (importType == "param") {
            var paramType = $.trim(mini.get("paramType").getValue());
            if (!paramType) {
                mini.alert("请选择导入模板类型");
                return;
            }
        }

        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        if (!file) {
            mini.alert('请选择文件！');
            return;
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

        // var id = currentUserId;

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
                            mini.alert(message);
                        }
                    }
                }
            };

            // 开始上传
            var fd = new FormData();
            if (importType == "param") {
                xhr.open('POST', jsUseCtxPath + '/world/core/productSpectrum/importMainParam.do', false);
                fd.append("paramType",paramType);
            }else if (importType == "setting") {
                xhr.open('POST', jsUseCtxPath + '/world/core/productSpectrum/importMainSettings.do', false);
            } else {
                mini.alert("导入类型mis,导入失败")
            }
            // xhr.open('POST', jsUseCtxPath + '/devAccountingImport/core/importXljcb.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            fd.append('importFile', file);
            fd.append('applyId', businessId);
            xhr.send(fd);
        }
    }

    function showBoom(){
        deviceGrid.showColumns(["boomMaterial","boomAsse","boomFrontAsse","boomWeigth","boomRemark"]);
        deviceGrid.hideColumns(["stickMaterial","stickAsse","stickFrontAsse","stickWeigth","stickRemark"]);
        deviceGrid.hideColumns(["pipeMaterial","pipeSize","pipeConnect","pipeVehicle","pipeRemark"]);
        deviceGrid.hideColumns(["basketMaterial","basketCap","basketType","basketWidth","basketHeight","basketWeigth","basketRemark"]);
    }
    function showStick(){
        deviceGrid.hideColumns(["boomMaterial","boomAsse","boomFrontAsse","boomWeigth","boomRemark"]);
        deviceGrid.showColumns(["stickMaterial","stickAsse","stickFrontAsse","stickWeigth","stickRemark"]);
        deviceGrid.hideColumns(["pipeMaterial","pipeSize","pipeConnect","pipeVehicle","pipeRemark"]);
        deviceGrid.hideColumns(["basketMaterial","basketCap","basketType","basketWidth","basketHeight","basketWeigth","basketRemark"]);
    }
    function showPipe(){
        deviceGrid.hideColumns(["boomMaterial","boomAsse","boomFrontAsse","boomWeigth","boomRemark"]);
        deviceGrid.hideColumns(["stickMaterial","stickAsse","stickFrontAsse","stickWeigth","stickRemark"]);
        deviceGrid.showColumns(["pipeMaterial","pipeSize","pipeConnect","pipeVehicle","pipeRemark"]);
        deviceGrid.hideColumns(["basketMaterial","basketCap","basketType","basketWidth","basketHeight","basketWeigth","basketRemark"]);
    }
    function showBasket(){
        deviceGrid.hideColumns(["boomMaterial","boomAsse","boomFrontAsse","boomWeigth","boomRemark"]);
        deviceGrid.hideColumns(["stickMaterial","stickAsse","stickFrontAsse","stickWeigth","stickRemark"]);
        deviceGrid.hideColumns(["pipeMaterial","pipeSize","pipeConnect","pipeVehicle","pipeRemark"]);
        deviceGrid.showColumns(["basketMaterial","basketCap","basketType","basketWidth","basketHeight","basketWeigth","basketRemark"]);
    }

    function showAll(){
        deviceGrid.showColumns(["boomMaterial","boomAsse","boomFrontAsse","boomWeigth","boomRemark"]);
        deviceGrid.showColumns(["stickMaterial","stickAsse","stickFrontAsse","stickWeigth","stickRemark"]);
        deviceGrid.showColumns(["pipeMaterial","pipeSize","pipeConnect","pipeVehicle","pipeRemark"]);
        deviceGrid.showColumns(["basketMaterial","basketCap","basketType","basketWidth","basketHeight","basketWeigth","basketRemark"]);
    }

    function hideAll(){
        deviceGrid.hideColumns(["boomMaterial","boomAsse","boomFrontAsse","boomWeigth","boomRemark"]);
        deviceGrid.hideColumns(["stickMaterial","stickAsse","stickFrontAsse","stickWeigth","stickRemark"]);
        deviceGrid.hideColumns(["pipeMaterial","pipeSize","pipeConnect","pipeVehicle","pipeRemark"]);
        deviceGrid.hideColumns(["basketMaterial","basketCap","basketType","basketWidth","basketHeight","basketWeigth","basketRemark"]);
    }
    function toggleFieldSet(ck, id) {
        var dom = document.getElementById(id);
        if (ck.checked) {
            dom.className = "";
        } else {
            dom.className = "hideFieldset";
        }

    }
</script>
</body>
</html>
