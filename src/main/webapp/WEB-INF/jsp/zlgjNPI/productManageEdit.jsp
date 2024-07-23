<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>产品开发管控编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/zlgjNPI/productManageEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>

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


    </style>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto">
        <form id="formProductManage" method="post">
            <div style="width: 100% ; height: 30px ; text-align: center">
                产品开发管控信息表
            </div>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="id" name="id" class="mini-hidden"/>
            <%--产品开发计划--%>
            <fieldset id="cbkfBaseInfo">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="checkboxBaseInfo"
                               onclick="toggleFieldSet(this, 'cbkfBaseInfo')"/>
                        产品开发计划
                    </label>
                </legend>
                <div class="fieldset-body">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 20%">设计型号：<span style="color:red">*</span></td>
                            <td style="min-width:170px;">
                                <input id="designType" name="designType" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 25%">性能需求调研开始时间：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="performznceStartTime" name="performznceStartTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>


                            <td style="width: 25%">性能需求调研结束时间：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="performznceEndTime" name="performznceEndTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 25%">产品设计开始时间：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="productStartTime" name="productStartTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>


                            <td style="width: 25%">产品设计结束时间：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="productEndTime" name="productEndTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 25%">部件设计开始时间：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="assemblyStartTime" name="assemblyStartTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>

                            <td style="width: 25%">部件设计结束时间：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="assemblyEndTime" name="assemblyEndTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>

                        </tr>
                        <tr>
                            <td style="width: 25%">首台验证开始时间：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="verificationStartTime" name="verificationStartTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>

                            <td style="width: 25%">首台验证结束时间：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="verificationEndTime" name="verificationEndTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>

                        </tr>
                        <tr>
                            <td style="width: 25%">小批量产开始时间：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="batchStartTime" name="batchStartTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>

                            <td style="width: 25%">小批量产结束时间：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="batchEndTime" name="batchEndTime" class="mini-datepicker"
                                       format="yyyy-MM-dd"
                                       style="width:98%"/>
                            </td>
                        </tr>

                    </table>
                </div>
            </fieldset>
            <br>

            <%--参数填写人员指定--%>
            <fieldset id="fzrnInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkFzrInfo" onclick="toggleFieldSet(this, 'fzrnInfo')"
                               hideFocus/>
                        参数填写人员指定
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px;width: 100% ;height: auto">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 20%">技术参数人员：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="productSupervisorId" name="productSupervisorId"
                                       textname="productSupervisorName"
                                       property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                            </td>
                            <td style="width: 20%">整机工艺参数人员：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="tractorParameterId" name="tractorParameterId" textname="tractorParameterName"
                                       property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 20%">零部件技术参数人员：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="partsTechnologyId" name="partsTechnologyId" textname="partsTechnologyName"
                                       property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                            </td>
                            <td style="width: 20%">零部件工艺参数人员：<span
                                    style="color: #ff0000">*</span></td>
                            <td style="min-width:170px">
                                <input id="craftAssemblyId" name="craftAssemblyId" textname="craftAssemblyName"
                                       property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                            </td>

                        </tr>

                    </table>
                </div>

            </fieldset>
            <br>


            <%--整机参数设计--%>
            <fieldset id="cpxnInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkUserInfo" onclick="toggleFieldSet(this, 'cpxnInfo')"
                               hideFocus/>
                        产品性能需求
                    </label>
                </legend>

                <div class="fieldset-body" style="margin: 10px 10px 10px 10px;width: 99% ;height: 300px">
                    <div class="mini-toolbar">
                        <ul class="toolBtnBox">
                            <li style="float: left">
                                <a id="addXnButton" class="mini-button" onclick="addProductMessage('xn')">新增</a>
                                <a id="removeButton" class="mini-button btn-red" plain="true"
                                   onclick="removeProductMessage('xn')">删除</a>
                            </li>
                        </ul>
                    </div>
                    <div class="mini-fit" style="margin-top: 10px;height:90%">
                        <div id="performznceGrid" class="mini-datagrid" style=" height: 100%;" allowResize="false"
                             url="${ctxPath}/xcpdr/core/cpkfgk/items.do?manageId=${manageId}&dicType=1"
                             idField="id" allowAlternating="true" showPager="false" multiSelect="true"
                             allowCellEdit="true"
                             allowCellSelect="true" allowCellWrap="true"
                             editNextOnEnterKey="true" editNextRowCell="true">
                            <div property="columns">
                                <div type="checkcolumn" field="id" name="id" width="30px"></div>
                                <div type="indexcolumn" headerAlign="center" align="center" width="40px">序号</div>
                                <div field="demandSideType" width="60px" headerAlign="center" renderer="onImportant"
                                     align="center">
                                    需求
                                    <input property="editor" class="mini-combobox" textField="text" valueField="key_"
                                           emptyText="请选择..." required
                                           showNullItem="false" nullItemText="请选择..."
                                           url="${ctxPath}/sys/core/commonInfo/getMessage.do?dicType=1&nodeName=${nodeName}"/>
                                </div>
                                <div field="demandElement" displayfield="demandElement" width="200px"
                                     headerAlign="center" align="left">
                                    需求项
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="parameter" displayfield="parameter" width="200px" headerAlign="center"
                                     align="left">
                                    参数
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="standard" displayfield="standard" width="200px" headerAlign="center"
                                     align="left">
                                    标准
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="CREATE_BY_"  name="CREATE_BY_"  displayfield="CREATE_BY_" width="200px" headerAlign="center"
                                     align="left">
                                    创建人
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </fieldset>
            <br>

            <fieldset id="zjInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkzjInfo" onclick="toggleFieldSet(this, 'zjInfo')"
                               hideFocus/>
                        整机参数设计
                    </label>
                </legend>

                <div class="fieldset-body" style="margin: 10px 10px 10px 10px;width: 100% ;height: auto">
                    <div class="mini-toolbar">
                        <ul class="toolBtnBox">
                            <li style="float: left">
                                <a id="addZjButton" class="mini-button" onclick="addProductMessage('zj')">新增</a>
                                <a id="removeZjButton" class="mini-button btn-red" plain="true"
                                   onclick="removeProductMessage('zj')">删除</a>
                            </li>
                        </ul>
                    </div>
                    <div class="mini-fit" style="margin-top: 10px">
                        <div id="productGrid" class="mini-datagrid" style=" height: 100%;" allowResize="false"
                             url="${ctxPath}/xcpdr/core/cpkfgk/items.do?manageId=${manageId}&dicType=2"
                             idField="id" allowAlternating="true" showPager="false" multiSelect="true"
                             allowCellEdit="true"
                             allowCellSelect="true" allowCellWrap="true"
                             editNextOnEnterKey="true" editNextRowCell="true">
                            <div property="columns">
                                <div type="checkcolumn" field="id" name="id" width="30px"></div>
                                <div type="indexcolumn" headerAlign="center" align="center" width="40px">序号</div>
                                <%-- <div field="parentName" displayfield="parentName" width="200px" headerAlign="center" align="left">
                                     项目名称<span
                                         style="color: #ff0000">*</span>
                                     <input property="editor" class="mini-textbox" allowLimitValue="false"
                                            required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                                            decimalPlaces="0" style="width:100%;height:34px"/>
                                 </div>--%>
                                <div field="demandSideType" width="60px" headerAlign="center" renderer="onImportantzj"
                                     align="center">
                                    需求
                                    <input property="editor" class="mini-combobox" textField="text" valueField="key_"
                                           emptyText="请选择..." required
                                           showNullItem="false" nullItemText="请选择..."
                                           url="${ctxPath}/sys/core/commonInfo/getMessage.do?dicType=2&nodeName=${nodeName}"/>

                                </div>
                                <div field="demandElement" displayfield="demandElement" width="200px"
                                     headerAlign="center"
                                     align="left">
                                    需求项
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="parameter" displayfield="parameter" width="200px" headerAlign="center"
                                     align="left">
                                    参数
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="standard" displayfield="standard" width="200px" headerAlign="center"
                                     align="left">
                                    标准
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="CREATE_BY_"  name="CREATE_BY_"  displayfield="CREATE_BY_" width="200px" headerAlign="center"
                                     align="left">
                                    创建人
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </fieldset>
            <br>

            <fieldset id="bjInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkbjInfo" onclick="toggleFieldSet(this, 'bjInfo')"
                               hideFocus/>
                        零部件参数设计
                    </label>
                </legend>

                <div class="fieldset-body" style="margin: 10px 10px 10px 10px;width: 100% ;height: auto">
                    <div class="mini-toolbar">
                        <ul class="toolBtnBox">
                            <li style="float: left">
                                <a id="addBpButton" class="mini-button" onclick="addProductMessage('bp')">新增</a>
                                <a id="removeBpButton" class="mini-button btn-red" plain="true"
                                   onclick="removeProductMessage('bp')">删除</a>
                            </li>
                        </ul>
                    </div>
                    <div class="mini-fit" style="margin-top: 10px">
                        <div id="assemblyGrid" class="mini-datagrid" style=" height: 100%;" allowResize="false"
                             url="${ctxPath}/xcpdr/core/cpkfgk/items.do?manageId=${manageId}&dicType=3"
                             idField="id" allowAlternating="true" showPager="false" multiSelect="true"
                             allowCellEdit="true"
                             allowCellSelect="true" allowCellWrap="true"
                             editNextOnEnterKey="true" editNextRowCell="true">
                            <div property="columns">
                                <%--<div type="checkcolumn" field="mainId" name="mainId" width="30px"></div>--%>
                                <div type="checkcolumn" field="id" name="id" width="30px"></div>
                                <div type="indexcolumn" headerAlign="center" align="center" width="40px">序号</div>

                                <div field="CREATE_BY_"  name="CREATE_BY_"  displayfield="parameter" width="200px" headerAlign="center"
                                     align="left">
                                    创建人
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>

                                <div field="demandSideType" width="60px" headerAlign="center" renderer="onImportantbp"
                                     align="center">
                                    需求
                                    <input property="editor" class="mini-combobox" textField="text" valueField="key_"
                                           emptyText="请选择..." required
                                           showNullItem="false" nullItemText="请选择..."
                                           url="${ctxPath}/sys/core/commonInfo/getMessage.do?dicType=3&nodeName=${nodeName}"/>

                                </div>
                                <div field="demandElement" displayfield="demandElement" width="200px"
                                     headerAlign="center"
                                     align="left">
                                    需求项
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="parameter" displayfield="parameter" width="200px" headerAlign="center"
                                     align="left">
                                    参数
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="standard" displayfield="standard" width="200px" headerAlign="center"
                                     align="left">
                                    标准
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>


                            </div>
                        </div>
                    </div>
                </div>

            </fieldset>
            <br>

            <fieldset id="yzInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkyzInfo" onclick="toggleFieldSet(this, 'yzInfo')"
                               hideFocus/>
                        首台验证
                    </label>
                </legend>

                <div class="fieldset-body" style="margin: 10px 10px 10px 10px;width: 100% ;height: auto">
                    <div class="mini-toolbar">
                        <ul class="toolBtnBox">
                            <li style="float: left">
                                <a id="addYzButton" class="mini-button" onclick="addProductMessage('yz')">新增</a>
                                <a id="removeYzButton" class="mini-button btn-red" plain="true"
                                   onclick="removeProductMessage('yz')">删除</a>
                            </li>
                        </ul>
                    </div>
                    <div class="mini-fit" style="margin-top: 10px">
                        <div id="verificationGrid" class="mini-datagrid" style=" height: 100%;" allowResize="false"
                             url="${ctxPath}/xcpdr/core/cpkfgk/items.do?manageId=${manageId}&dicType=4"
                             idField="id" allowAlternating="true" showPager="false" multiSelect="true"
                             allowCellEdit="true"
                             allowCellSelect="true" allowCellWrap="true"
                             editNextOnEnterKey="true" editNextRowCell="true">
                            <div property="columns">
                                <div type="checkcolumn" field="id" name="id" width="30px"></div>
                                <div type="indexcolumn" headerAlign="center" align="center" width="40px">序号</div>
                                <%-- <div field="parentName" displayfield="parentName" width="200px" headerAlign="center" align="left">
                                     项目名称<span
                                         style="color: #ff0000">*</span>
                                     <input property="editor" class="mini-textbox" allowLimitValue="false"
                                            required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                                            decimalPlaces="0" style="width:100%;height:34px"/>
                                 </div>--%>
                                <div field="demandSideType" width="60px" headerAlign="center" renderer="onImportantyz"
                                     align="center">
                                    需求
                                    <input property="editor" class="mini-combobox" textField="text" valueField="key_"
                                           emptyText="请选择..." required
                                           showNullItem="false" nullItemText="请选择..."
                                           url="${ctxPath}/sys/core/commonInfo/getMessage.do?dicType=4&nodeName=${nodeName}"/>

                                </div>
                                <div field="demandElement" displayfield="demandElement" width="200px"
                                     headerAlign="center"
                                     align="left">
                                    需求项
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="parameter" displayfield="parameter" width="200px" headerAlign="center"
                                     align="left">
                                    参数
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="standard" displayfield="standard" width="200px" headerAlign="center"
                                     align="left">
                                    标准
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="CREATE_BY_"  name="CREATE_BY_"  displayfield="CREATE_BY_" width="200px" headerAlign="center"
                                     align="left">
                                    创建人
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </fieldset>
            <br>

            <fieldset id="xpInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkxpInfo" onclick="toggleFieldSet(this, 'xpInfo')"
                               hideFocus/>
                        小批量产反馈
                    </label>
                </legend>

                <div class="fieldset-body" style="margin: 10px 10px 10px 10px;width: 100% ;height: auto">
                    <div class="mini-toolbar">
                        <ul class="toolBtnBox">
                            <li style="float: left">
                                <a id="addXpButton" class="mini-button" onclick="addProductMessage('xp')">新增</a>
                                <a id="removeXpButton" class="mini-button btn-red" plain="true"
                                   onclick="removeProductMessage('xp')">删除</a>
                            </li>
                        </ul>
                    </div>
                    <div class="mini-fit" style="margin-top: 10px">
                        <div id="batchGrid" class="mini-datagrid" style=" height: 100%;" allowResize="false"
                             url="${ctxPath}/xcpdr/core/cpkfgk/items.do?manageId=${manageId}&dicType=5"
                             idField="id" allowAlternating="true" showPager="false" multiSelect="true"
                             allowCellEdit="true"
                             allowCellSelect="true" allowCellWrap="true"
                             editNextOnEnterKey="true" editNextRowCell="true">
                            <div property="columns">
                                <div type="checkcolumn" field="id" name="id" width="30px"></div>
                                <div type="indexcolumn" headerAlign="center" align="center" width="40px">序号</div>
                                <%-- <div field="parentName" displayfield="parentName" width="200px" headerAlign="center" align="left">
                                     项目名称<span
                                         style="color: #ff0000">*</span>
                                     <input property="editor" class="mini-textbox" allowLimitValue="false"
                                            required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                                            decimalPlaces="0" style="width:100%;height:34px"/>
                                 </div>--%>
                                <div field="demandSideType" width="60px" headerAlign="center" renderer="onImportantxp"
                                     align="center">
                                    需求
                                    <input id="demandSideType" name="demandSideType" property="editor"
                                           class="mini-combobox"
                                           textField="text" valueField="key_"
                                           emptyText="请选择..." required
                                           showNullItem="false" nullItemText="请选择..." allowInput="false"
                                           url="${ctxPath}/sys/core/commonInfo/getMessage.do?dicType=5&nodeName=${nodeName}"/>

                                </div>
                                <div field="demandElement" displayfield="demandElement" width="200px"
                                     headerAlign="center"
                                     align="left">
                                    需求项
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="parameter" displayfield="parameter" width="200px" headerAlign="center"
                                     align="left">
                                    参数
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="standard" displayfield="standard" width="200px" headerAlign="center"
                                     align="left">
                                    标准
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                                <div field="CREATE_BY_"  name="CREATE_BY_"  displayfield="CREATE_BY_" width="200px" headerAlign="center"
                                     align="left">
                                    创建人
                                    <input property="editor" class="mini-textbox" allowLimitValue="false"
                                           required="true" only_read="false" allowinput="true" allowNull="true"
                                           value="null"
                                           decimalPlaces="0" style="width:100%;height:34px"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </fieldset>
            <br>

        </form>

    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var manageId = "${manageId}";
    var nodeName = "${nodeName}";
    var currentUserId = "${currentUserId}";
    var productManageObj =${productManageObj};
    var formProductManage = new mini.Form("#formProductManage");
    var currentTime = "${currentTime}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var formStatus = "${status}";
    //产品开发管控信息
    var performznceGrid = mini.get("performznceGrid");
    //产品(整机)设计
    var productGrid = mini.get("productGrid");
    //部品设计
    var assemblyGrid = mini.get("assemblyGrid");
    //验证/首台
    var verificationGrid = mini.get("verificationGrid");
    //小批/量产
    var batchGrid = mini.get("batchGrid");

    var xnList = getDics("1");
    var zjList = getDics("2");
    var bpList = getDics("3");
    var yzList = getDics("4");
    var xpList = getDics("5");

    productGrid.on("cellbeginedit", function (e) {
        var record = e.record;

        if (record.CREATE_BY_ == null) {
            return;
        }
        if (currentUserId != record.CREATE_BY_) {
            e.cancel = true;
        }
    });

    assemblyGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if (record.CREATE_BY_ == null) {
            return;
        }
        if (currentUserId != record.CREATE_BY_) {
            e.cancel = true;
        }
    });

    verificationGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if (record.CREATE_BY_ == null) {
            return;
        }
        if (currentUserId != record.CREATE_BY_) {
            e.cancel = true;
        }
    });


    function onImportant(e) {
        var record = e.record;
        var value = record.demandSideType;
        var resultText = '';
        for (var i = 0; i < xnList.length; i++) {
            if (xnList[i].key_ == value) {
                resultText = xnList[i].text;
                break
            }
        }
        return resultText;
    }

    function onImportantzj(e) {
        var record = e.record;
        var value = record.demandSideType;
        var resultText = '';
        for (var i = 0; i < zjList.length; i++) {
            if (zjList[i].key_ == value) {
                resultText = zjList[i].text;
                break
            }
        }
        return resultText;
    }

    function onImportantbp(e) {
        var record = e.record;
        var value = record.demandSideType;
        var resultText = '';
        for (var i = 0; i < bpList.length; i++) {
            if (bpList[i].key_ == value) {
                resultText = bpList[i].text;
                break
            }
        }
        return resultText;
    }

    function onImportantyz(e) {
        var record = e.record;
        var value = record.demandSideType;
        var resultText = '';
        for (var i = 0; i < yzList.length; i++) {
            if (yzList[i].key_ == value) {
                resultText = yzList[i].text;
                break
            }
        }
        return resultText;
    }

    function onImportantxp(e) {
        var record = e.record;
        var value = record.demandSideType;
        var resultText = '';
        for (var i = 0; i < xpList.length; i++) {
            if (xpList[i].key_ == value) {
                resultText = xpList[i].text;
                break
            }
        }
        return resultText;
    }

    function getDics(dicKey) {
        var resultDate = [];
        $.ajax({
            async: false,
            url: jsUseCtxPath + '/sys/core/commonInfo/getMessage.do?dicType=' + dicKey + '&nodeName=all',
            type: 'GET',
            contentType: 'application/json',
            success: function (data) {
                if (data.code == 200) {
                    resultDate = data.data;
                }
            }
        });
        return resultDate;
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
</script>
</body>
</html>
