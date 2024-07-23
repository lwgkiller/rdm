<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>产品机型图册制作申请表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
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

        .rmMem .mini-grid-cell-inner {
            color: red !important;
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
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formData" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>

            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    产品机型图册制作申请
                </caption>
                <tr>
                    <td style="width: 17%">编制人：</td>
                    <td style="width: 33%;min-width:170px">
                        <input id="creator" name="creator" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                    <td style="width: 17%">流程编号（自动生成）：</td>
                    <td style="width: 33%;min-width:170px">
                        <input id="modelMadeNum" name="modelMadeNum" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">整机物料编码<span style="color:red">*</span>：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="matCode" name="matCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">设计型号<span style="color:red">*</span>：</td>
                    <td style="width: 36%;min-width:140px">
                        <input id="designType" name="designType" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">设计产品主管<span style="color:red">*</span>：</td>
                    <td style="width: 36%;">
                        <input id="modelOwnerId" name="modelOwnerId" textname="modelOwnerName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="设计产品主管" length="1000" maxlength="1000" mainfield="no" single="true"/>
                    </td>
                    <td style="width: 14%">机型制作人<span style="color:red">*</span>：</td>
                    <td style="width: 36%;">
                        <input id="jxzzrId" name="jxzzrId" textname="jxzzrName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="机型制作人" length="1000" maxlength="1000" mainfield="no" single="true"/>
                    </td>

                </tr>

                <tr>
                    <td style="width: 14%">工艺负责人<span style="color:red">*</span>：</td>
                    <td style="width: 36%;">
                        <input id="gyfzrId" name="gyfzrId" textname="gyfzrName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="工艺负责人" length="1000" maxlength="1000" mainfield="no" single="true"/>
                    </td>
                    <td style="width: 14%">服务工程外购件负责人<span style="color:red">*</span>：</td>
                    <td style="width: 36%;">
                        <input id="fwgcPersonId" name="fwgcPersonId" textname="fwgcPersonName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="服务工程外购件负责人" length="1000" maxlength="1000" mainfield="no" single="true"/>
                    </td>


                </tr>
                <td style="width: 14%">销售型号<span style="color:red">*</span>：</td>
                <td style="width: 36%;min-width:170px">
                    <input id="saleModel" name="saleModel" class="mini-textbox" style="width:98%;"/>
                </td>
                <td style="width: 14%">是否出口机型<span style="color:red">*</span>：</td>
                <td style="width: 36%;min-width:170px">
                    <input id="sfckjx" name="sfckjx" class="mini-combobox" style="width:98%;"
                           textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm()"
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                    />
                </td>
                <tr>


                </tr>
                <tr>

                    <td style="width: 14%">预计出口区域<span style="color:red">*</span>：</td>
                    <td style="width: 36%;min-width:140px">
                        <%--<input id="yjckqy" name="yjckqy"  class="mini-textbox" style="width:98%;" />--%>
                        <input id="yjckqy" name="yjckqy" class="mini-combobox" style="width:98%"
                               multiSelect="true"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=CPXPGHXSQYGJ"
                               valueField="name" textField="name"/>
                    </td>
                    <td style="width: 14%">产品预计上市时间<span style="color:red">*</span>：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="cpyjsssj" name="cpyjsssj" class="mini-datepicker" format="yyyy-MM-dd"
                               allowInput="false" style="width:98%"/>
                    </td>
                </tr>

                <tr>

                    <td style="width: 14%">小语种需求<span style="color:red">*</span>：</td>
                    <td style="width: 36%;min-width:140px">
                        <input id="xyzxq" name="xyzxq" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>

                <tr>
                    <td style="width: 14%">机型图册制作开始时间<span style="color:red">*</span>：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="zzStartTime" name="zzStartTime" class="mini-datepicker" format="yyyy-MM-dd"
                               allowInput="false" style="width:98%"/>
                    </td>
                    <td style="width: 14%">机型图册制作开始时间<span style="color:red">*</span>：</td>
                    <td style="width: 36%;min-width:140px">
                        <input id="zzEndTime" name="zzEndTime" class="mini-datepicker" format="yyyy-MM-dd"
                               allowInput="false" style="width:98%"/></td>
                </tr>

            </table>

            <br>
            <fieldset id="bdgxsl"
            <%--class="hideFieldset" --%>
            >
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="bdgxslInfo" onclick="toggleFieldSet(this, 'bdgxsl')" hideFocus checked="true"/>
                        变动关系梳理
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                        <div class="mini-toolbar" style="margin-bottom: 5px" id="demandGridToolBar">
                            <a class="mini-button" id="addChangeRelRow" plain="true"
                               onclick="addChangeRelRow">新增行</a>
                            <a class="mini-button btn-red" id="removeChangeRelRow" plain="true"
                               onclick="removeChangeRelRow">删除</a>
                            <a id="openImportWindow" class="mini-button" style="margin-right: 5px" plain="true"
                               onclick="openImportWindow('bdgxsl')">导入</a>
                            <a id="exportItem" class="mini-button" style="margin-right: 5px" plain="true"
                               onclick="exportItem('bdgxsl')">导出</a>
                        </div>
                        <div id="changeRelGrid" class="mini-datagrid"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/changeRelList.do?applyId=${id}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false"
                             allowAlternating="true"
                             style="height:600px;"
                             allowCellSelect="true"
                             allowCellEdit="true"
                             allowCellWrap="true"
                        >
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>

                                <div field="sourcelCode" name="sourcelCode" width="50" headerAlign="center"
                                     align="center"
                                >参考原型物料编码
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="partDesc" name="partDesc" width="50" headerAlign="center"
                                     align="center"
                                >零部件物料描述
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <%--<div field="num" name="num" width="50" headerAlign="center"--%>
                                <%--align="center"--%>
                                <%-->零部件数量--%>
                                <%--<input property="editor" class="mini-textbox"/>--%>
                                <%--</div>--%>
                                <div field="newCode" name="newCode" width="50" headerAlign="center"
                                     align="center"
                                >变动后物料编码
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <%--<div field="num2" name="num2" width="50" headerAlign="center"--%>
                                <%--align="center"--%>
                                <%-->变动后数量--%>
                                <%--<input property="editor" class="mini-textbox"/>--%>
                                <%--</div>--%>
                                <div field="mainChangeDesc" name="mainChangeDesc" width="50" headerAlign="center"
                                     align="center"
                                >主要变动点说明
                                    <input property="editor" class="mini-textbox"/>
                                </div>

                                <div field="partPersonId" name="partPersonId" displayField="partPerson" width="50"
                                     headerAlign="center"
                                     align="center"
                                >部件负责人
                                    <input property="editor" id="partPersonId" name="partPersonId" textname="partPerson"
                                           class="mini-user rxc"
                                           plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                                           label="部件负责人" length="1000" maxlength="1000" mainfield="no" single="true"/>
                                </div>
                                <div field="remark" name="remark" width="50" headerAlign="center"
                                     align="center"
                                >备注
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                            </div>
                        </div>
                    </table>
                </div>
            </fieldset>

            <br>
            <fieldset id="wgjtczl"
            <%--class="hideFieldset" --%>
            >
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="wgjtczlInfo" onclick="toggleFieldSet(this, 'wgjtczl')" hideFocus checked="true"/>
                        外购件图册资料
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                        <div class="mini-toolbar" style="margin-bottom: 5px" id="wgjtczlGridToolBar">
                            <a class="mini-button" id="addWgjtczlRow" plain="true"
                               onclick="addWgjtczlRow">新增行</a>
                            <a class="mini-button btn-red" id="removeWgjtczlRow" plain="true"
                               onclick="removeWgjtczlRow">删除</a>
                            <a id="openImportWindow" class="mini-button" style="margin-right: 5px" plain="true"
                               onclick="openImportWindow('wgjtczl')">导入</a>
                            <a id="exportItem" class="mini-button" style="margin-right: 5px" plain="true"
                               onclick="exportItem('wgjtczl')">导出</a>
                        </div>
                        <div id="wgjtczlGrid" class="mini-datagrid"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/wgjtczlList.do?applyId=${id}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false"
                             allowAlternating="true"
                             style="height:600px;"
                             allowCellSelect="true"
                             allowCellEdit="true"
                             allowCellWrap="true"
                        >
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>

                                <div field="belongParts" name="belongParts" width="50" headerAlign="center"
                                     align="center"
                                >所属部件<span style="color:red">*</span>
                                    <input property="editor" id="belongParts" name="belongParts" class="mini-combobox"
                                           style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           onvaluechanged="searchFrm()"
                                           required="false" allowInput="false"
                                           data="[ {'key' : '液压','value' : '液压'}
                                           ,{'key' : '动力','value' : '动力'}
                                           ,{'key' : '覆盖件','value' : '覆盖件'}
                                           ,{'key' : '底盘','value' : '底盘'}
                                           ,{'key' : '电气','value' : '电气'}
                                           ,{'key' : '转台','value' : '转台'}
                                           ,{'key' : '传动','value' : '传动'}
                                           ,{'key' : '其他','value' : '其他'}
                                           ]"
                                    />

                                    <%--<input property="editor" class="mini-textbox"/>--%>
                                </div>
                                <div field="materialName" name="materialName" width="50" headerAlign="center"
                                     align="center"
                                >物料名称<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="wgjCode" name="wgjCode" width="50" headerAlign="center"
                                     align="center"
                                >外购件编码
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="materialDesc" name="materialDesc" width="50" headerAlign="center"
                                     align="center"
                                >物料描述
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="xxyfPersonId" name="xxyfPersonId" displayField="xxyfPersonName" width="50"
                                     headerAlign="center"
                                     align="center"
                                >外购件选型研发负责人
                                    <input property="editor" id="xxyfPersonId" name="xxyfPersonId"
                                           textname="xxyfPersonName" class="mini-user rxc"
                                           plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                                           label="外购件选型研发负责人" length="1000" maxlength="1000" mainfield="no"
                                           single="true"/>

                                </div>
                                <%--<div field="fwgcPersonId" name="fwgcPersonId" displayField="fwgcPersonName" width="50" headerAlign="center"--%>
                                <%--align="center"--%>
                                <%-->服务工程外购件负责人--%>
                                <%--<input property="editor" id="fwgcPersonId" name="fwgcPersonId" textname="fwgcPersonName" class="mini-user rxc"--%>
                                <%--plugins="mini-user" style="width:98%;height:34px;" allowinput="false"--%>
                                <%--label="服务工程外购件负责人" length="1000" maxlength="1000"  mainfield="no"  single="true" />--%>
                                <%--</div>--%>
                                <div field="remark" name="remark" width="50" headerAlign="center"
                                     align="center"
                                >备注
                                    <input property="editor" class="mini-textbox"/>
                                </div>


                            </div>
                        </div>
                    </table>
                </div>
            </fieldset
            >

            <br>
            <fieldset id="wxzyjsl"
            <%--class="hideFieldset"--%>
            >
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="wxzyjslInfo" onclick="toggleFieldSet(this, 'wxzyjsl')" hideFocus checked="true"/>
                        维修专用件梳理
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                        <div class="mini-toolbar" style="margin-bottom: 5px" id="wxzyjGridToolBar">
                            <a class="mini-button" id="addWxzyjRow" plain="true"
                               onclick="addWxzyjRow">新增行</a>
                            <a class="mini-button btn-red" id="removeWxzyjRow" plain="true"
                               onclick="removeWxzyjRow">删除</a>
                            <a id="openImportWindow" class="mini-button" style="margin-right: 5px" plain="true"
                               onclick="openImportWindow('wxzyjsl')">导入</a>
                            <a id="exportItem" class="mini-button" style="margin-right: 5px" plain="true"
                               onclick="exportItem('wxzyjsl')">导出</a>
                        </div>
                        <div id="wxzyjGrid" class="mini-datagrid"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/wxzyjList.do?applyId=${id}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false"
                             allowAlternating="true"
                             style="height:600px;"
                             allowCellSelect="true"
                             allowCellEdit="true"
                             allowCellWrap="true"
                        >
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>

                                <div field="zzjName" name="zzjName" width="50" headerAlign="center"
                                     align="center"
                                >自制件<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="zzjCode" name="zzjCode" width="50" headerAlign="center"
                                     align="center"
                                >自制件物料号<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="zzjDesc" name="zzjDesc" width="50" headerAlign="center"
                                     align="center"
                                >物料描述及尺寸信息<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="wxjName" name="wxjName" width="50" headerAlign="center"
                                     align="center"
                                >维修专用件<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="wxjCode" name="wxjCode" width="50" headerAlign="center"
                                     align="center"
                                >维修专用件物料号<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="wxjDesc" name="wxjDesc" width="50" headerAlign="center"
                                     align="center"
                                >物料描述<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="zcBom" name="zcBom" width="50" headerAlign="center"
                                     align="center"
                                >总成BOM<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="remark" name="remark" width="50" headerAlign="center"
                                     align="center"
                                >备注
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                            </div>
                        </div>
                    </table>
                </div>
            </fieldset>

            <br>
            <fieldset id="gyhjmx"
            <%--class="hideFieldset"--%>
            >
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="gyhjmxInfo" onclick="toggleFieldSet(this, 'gyhjmx')" hideFocus checked="true"/>
                        工艺合件明细
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                        <div class="mini-toolbar" style="margin-bottom: 5px" id="gyhjmxGridToolBar">
                            <a class="mini-button" id="addGyhjmxRow" plain="true"
                               onclick="addGyhjmxRow">新增行</a>
                            <a class="mini-button btn-red" id="removeGyhjmxRow" plain="true"
                               onclick="removeGyhjmxRow">删除</a>
                            <a id="openImportWindow" class="mini-button" style="margin-right: 5px" plain="true"
                               onclick="openImportWindow('gyhjmx')">导入</a>
                            <a id="exportItem" class="mini-button" style="margin-right: 5px" plain="true"
                               onclick="exportItem('gyhjmx')">导出</a>
                        </div>
                        <div id="gyhjmxGrid" class="mini-datagrid"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/gyhjmxList.do?applyId=${id}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false"
                             allowAlternating="true"
                             style="height:600px;"
                             allowCellSelect="true"
                             allowCellEdit="true"
                             allowCellWrap="true"
                        >
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>

                                <div field="fxCode" name="fxCode" width="50" headerAlign="center"
                                     align="center"
                                >父项物料号
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="fxDesc" name="fxDesc" width="50" headerAlign="center"
                                     align="center"
                                >父项物料描述
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="gyhjCode" name="gyhjCode" width="50" headerAlign="center"
                                     align="center"
                                >工艺合件物料号
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="gyhjDesc" name="gyhjDesc" width="50" headerAlign="center"
                                     align="center"
                                >工艺合件描述
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="zxCode" name="zxCode" width="50" headerAlign="center"
                                     align="center"
                                >子项物料号
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="zxDesc" name="zxDesc" width="50" headerAlign="center"
                                     align="center"
                                >子项物料描述
                                    <input property="editor" class="mini-textbox"/>
                                </div>


                            </div>
                        </div>
                    </table>
                </div>
            </fieldset>
            <br>
        </form>
        <br>

    </div>
</div>
<%--导入窗口--%>
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;" showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importItem()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">模板下载.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName" readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="uploadFile()">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="clearUploadFile()">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var formData = new mini.Form("#formData");
    var id = "${id}";
    var applyId = "${id}";
    var objDetail = ${objDetail};
    var nodeVarsStr = '${nodeVars}';
    var stageVar = "";
    var changeRelGrid = mini.get("changeRelGrid");
    var wgjtczlGrid = mini.get("wgjtczlGrid");
    var wxzyjGrid = mini.get("wxzyjGrid");
    var gyhjmxGrid = mini.get("gyhjmxGrid");
    var currentTime = "${currentTime}";
    var importWindow = mini.get("importWindow");
    var importType = "";//当前导入类型

    $(function () {
        formData.setData(objDetail);

        subGridEditFalse();
        if (action == 'detail') {
            formData.setEnabled(false);
            $("#detailToolBar").show();
            //非草稿放开流程信息查看按钮
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'edit') {
            // mini.get("gssApplyNo").setEnabled(false);
            bianzhiEdit();
        } else if (action == 'task') {
            taskActionProcess();
        }
        $.ajaxSettings.async = false;
        wxzyjGrid.load();
        $.ajaxSettings.async = true;
        // addDefaultWxzyj();


    });

    //流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("formData");
        if (changeRelGrid.getChanges().length > 0) {
            formData.changeRelGrid = changeRelGrid.getChanges();
        }

        if (wgjtczlGrid.getChanges().length > 0) {
            formData.wgjtczlGrid = wgjtczlGrid.getChanges();
        }

        if (wxzyjGrid.getChanges().length > 0) {
            formData.wxzyjGrid = wxzyjGrid.getChanges();
        }
        //
        if (gyhjmxGrid.getChanges().length > 0) {
            formData.gyhjmxGrid = gyhjmxGrid.getChanges();
        }
        return formData;
    }

    //启动流程
    function startModelProcess(e) {
        var matCode = $.trim(mini.get("matCode").getValue());
        if (!matCode) {
            mini.alert("请填写整机物料编码");
            return;
        }
        var designType = $.trim(mini.get("designType").getValue());
        if (!designType) {
            mini.alert("请填写设计型号");
            return;
        }
        var modelOwnerId = $.trim(mini.get("modelOwnerId").getValue());
        if (!modelOwnerId) {
            mini.alert("请选择设计产品主管");
            return;
        }
        var jxzzrId = $.trim(mini.get("jxzzrId").getValue());
        if (!jxzzrId) {
            mini.alert("请选择机型制作人");
            return;
        }
        var gyfzrId = $.trim(mini.get("gyfzrId").getValue());
        if (!gyfzrId) {
            mini.alert("请选择工艺负责人");
            return;
        }
        var fwgcPersonId = $.trim(mini.get("fwgcPersonId").getValue());
        if (!fwgcPersonId) {
            mini.alert("请选择服务工程外购件负责人");
            return;
        }

        window.parent.startProcess(e);
    }

    //流程中的审批或者下一步
    function applyApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageVar == 'bianzhi') {
            var modelOwnerId = $.trim(mini.get("modelOwnerId").getValue());
            if (!modelOwnerId) {
                mini.alert("请选择设计产品主管");
                return;
            }
            var jxzzrId = $.trim(mini.get("jxzzrId").getValue());
            if (!jxzzrId) {
                mini.alert("请选择机型制作人");
                return;
            }
            var gyfzrId = $.trim(mini.get("gyfzrId").getValue());
            if (!gyfzrId) {
                mini.alert("请选择工艺负责人");
                return;
            }
            var matCode = $.trim(mini.get("matCode").getValue());
            if (!matCode) {
                mini.alert("请填写整机物料编码");
                return;
            }
            var designType = $.trim(mini.get("designType").getValue());
            if (!designType) {
                mini.alert("请填写设计型号");
                return;
            }

        }
        if (stageVar == 'shenqing') {
            //需要填三个子表，及主表的一堆数据

            var saleModel = $.trim(mini.get("saleModel").getValue());
            if (!saleModel) {
                mini.alert("请填写销售型号");
                return;
            }

            var sfckjx = $.trim(mini.get("sfckjx").getValue());
            if (!sfckjx) {
                mini.alert("请填写是否出口机型");
                return;
            }
            var yjckqy = $.trim(mini.get("yjckqy").getValue());
            if (sfckjx == "是") {
                if (!yjckqy) {
                    mini.alert("请填写预计出口区域");
                    return;
                }
            }
            var cpyjsssj = $.trim(mini.get("cpyjsssj").getValue());
            if (!cpyjsssj) {
                mini.alert("请填写产品预计上市时间");
                return;
            }
            var xyzxq = $.trim(mini.get("xyzxq").getValue());
            if (!xyzxq) {
                mini.alert("请填写小语种需求");
                return;
            }

            //外购件图册资料必填项
            var wgjtczlGridData = wgjtczlGrid.getData();
            if (wgjtczlGridData.length < 1) {
                mini.alert("请添加外购件图册资料信息！");
                return false;
            }
            for (var i = 0; i < wgjtczlGridData.length; i++) {
                if (wgjtczlGridData[i].belongParts == undefined || wgjtczlGridData[i].belongParts == "") {
                    mini.alert("请选择所属部件！");
                    return false;
                }
                if (wgjtczlGridData[i].materialName == undefined || wgjtczlGridData[i].materialName == "") {
                    mini.alert("请填写物料名称！");
                    return false;
                }
            }


            //维修专用件梳理必填项
            var wxzyjGridData = wxzyjGrid.getData();
            for (var i = 0; i < wxzyjGridData.length; i++) {
                if (wxzyjGridData[i].zzjCode == undefined || wxzyjGridData[i].zzjCode == "") {
                    mini.alert("请填写自制件物料号！");
                    return false;
                }
                if (wxzyjGridData[i].zzjName == undefined || wxzyjGridData[i].zzjName == "") {
                    mini.alert("请填写自制件！");
                    return false;
                }
                if (wxzyjGridData[i].zzjDesc == undefined || wxzyjGridData[i].zzjDesc == "") {
                    mini.alert("请填写物料描述及尺寸信息！");
                    return false;
                }
                if (wxzyjGridData[i].wxjName == undefined || wxzyjGridData[i].wxjName == "") {
                    mini.alert("请填写维修专用件！");
                    return false;
                }
                if (wxzyjGridData[i].wxjCode == undefined || wxzyjGridData[i].wxjCode == "") {
                    mini.alert("请填写维修专用件物料号！");
                    return false;
                }
                if (wxzyjGridData[i].wxjDesc == undefined || wxzyjGridData[i].wxjDesc == "") {
                    mini.alert("请填写物料描述！");
                    return false;
                }
                if (wxzyjGridData[i].zcBom == undefined || wxzyjGridData[i].zcBom == "") {
                    mini.alert("请填写总成BOM！");
                    return false;
                }
            }


        }
        //检查通过
        if (stageVar == 'gongyi') {
            var gyhjmxGridData = gyhjmxGrid.getData();
            if (gyhjmxGridData.length < 1) {
                mini.alert("请添加工艺合件明细！");
                return false;
            }
            for (var i = 0; i < gyhjmxGridData.length; i++) {
                if (gyhjmxGridData[i].fxCode == undefined || gyhjmxGridData[i].fxCode == "") {
                    mini.alert("请填写父项物料号！");
                    return false;
                }

            }
        }
        window.parent.approve();
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
            if (nodeVars[i].KEY_ == 'stageVar') {
                stageVar = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageVar == 'bianzhi') {
            // mini.get("gssApplyNo").setEnabled(false);
            bianzhiEdit();

        } else if (stageVar == 'shenqing') {
            shenqingEdit();
        } else if (stageVar == 'gongyi') {
            formData.setEnabled(false);
            gongyiEdit();
        } else if (stageVar == 'fgld') {
            formData.setEnabled(false);
            subGridEditFalse();
            mini.get("zzStartTime").setValue(currentTime);
        } else if (stageVar == 'finish') {
            formData.setEnabled(false);
            subGridEditFalse();
            mini.get("zzEndTime").setValue(currentTime);
        } else {
            formData.setEnabled(false);
            subGridEditFalse();
        }

    }

    function saveBusinessInProcess() {
        var formData = getData();

        //这里要校验外购件图册资料，每一行都要有变动不然getChange拿不到
//        //维修专用件梳理至少校验一个必填项
//        var wxzyjGridData = wxzyjGrid.getData();
//        for (var i = 0; i < wxzyjGridData.length; i++) {
//            if (wxzyjGridData[i].zzjCode == undefined || wxzyjGridData[i].zzjCode == "") {
//                mini.alert("请填写自制件物料号！");
//                return false;
//            }
//            if (wxzyjGridData[i].zzjName == undefined || wxzyjGridData[i].zzjName == "") {
//                mini.alert("请填写自制件！");
//                return false;
//            }
//            if (wxzyjGridData[i].wxjName == undefined || wxzyjGridData[i].wxjName == "") {
//                mini.alert("请填写维修专用件！");
//                return false;
//            }
//        }

        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/exportPartsAtlas/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "数据保存成功";
                    } else {
                        message = "数据保存失败" + data.message;
                    }
                    mini.alert(message, "提示信息", function () {
                        window.location.reload();
                    });
                }
            }
        });
    }

    //保存草稿
    function saveDraft(e) {

        window.parent.saveDraft(e);
    }

    function toggleFieldSet(ck, id) {
        var dom = document.getElementById(id);
        if (ck.checked) {
            dom.className = "";
        } else {
            dom.className = "hideFieldset";
        }

    }

    function addChangeRelRow() {
        var row = {}
        changeRelGrid.addRow(row);
    }

    function removeChangeRelRow() {
        var selecteds = changeRelGrid.getSelecteds();
        changeRelGrid.removeRows(selecteds);
    }

    function addWgjtczlRow() {
        var row = {}
        wgjtczlGrid.addRow(row);
    }

    function removeWgjtczlRow() {
        var selecteds = wgjtczlGrid.getSelecteds();
        wgjtczlGrid.removeRows(selecteds);
    }

    function addWxzyjRow() {
        var row = {}
        wxzyjGrid.addRow(row);
    }

    function removeWxzyjRow() {
        var selecteds = wxzyjGrid.getSelecteds();
        wxzyjGrid.removeRows(selecteds);
    }

    function addGyhjmxRow() {
        var row = {}
        gyhjmxGrid.addRow(row);
    }

    function removeGyhjmxRow() {
        var selecteds = gyhjmxGrid.getSelecteds();
        gyhjmxGrid.removeRows(selecteds);
    }

    function bianzhiEdit() {
        formData.setEnabled(false);
        mini.get("matCode").setEnabled(true);
        mini.get("designType").setEnabled(true);
        mini.get("modelOwnerId").setEnabled(true);
        mini.get("jxzzrId").setEnabled(true);
        mini.get("gyfzrId").setEnabled(true);
        mini.get("fwgcPersonId").setEnabled(true);
        mini.get("demandGridToolBar").hide();
        mini.get("wgjtczlGridToolBar").hide();
        mini.get("wxzyjGridToolBar").hide();
        mini.get("gyhjmxGridToolBar").hide();
        changeRelGrid.setAllowCellEdit(false);
        wgjtczlGrid.setAllowCellEdit(false);
        wxzyjGrid.setAllowCellEdit(false);
        gyhjmxGrid.setAllowCellEdit(false);
    }

    function shenqingEdit() {
        formData.setEnabled(false);
        mini.get("saleModel").setEnabled(true);
        mini.get("sfckjx").setEnabled(true);
        mini.get("yjckqy").setEnabled(true);
        mini.get("cpyjsssj").setEnabled(true);
        mini.get("xyzxq").setEnabled(true);
        mini.get("demandGridToolBar").show();
        mini.get("wgjtczlGridToolBar").show();
        mini.get("wxzyjGridToolBar").show();
        changeRelGrid.setAllowCellEdit(true);
        wgjtczlGrid.setAllowCellEdit(true);
        wxzyjGrid.setAllowCellEdit(true);
    }

    function gongyiEdit() {
        mini.get("gyhjmxGridToolBar").show();
        gyhjmxGrid.setAllowCellEdit(true);
    }

    function subGridEditFalse() {
        mini.get("demandGridToolBar").hide();
        mini.get("wgjtczlGridToolBar").hide();
        mini.get("wxzyjGridToolBar").hide();
        mini.get("gyhjmxGridToolBar").hide();
        changeRelGrid.setAllowCellEdit(false);
        wgjtczlGrid.setAllowCellEdit(false);
        wxzyjGrid.setAllowCellEdit(false);
        gyhjmxGrid.setAllowCellEdit(false);
    }
    //..
    function openImportWindow(type) {
        if (!applyId) {
            mini.alert("请先点击暂存进行表单的保存");
            return;
        }
        importType = type;
        importWindow.show();
    }
    //..
    function importItem() {
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
            //开始上传
            xhr.open('POST', jsUseCtxPath +
                '/serviceEngineering/core/exportPartsAtlas/importItem.do?businessId=' + applyId + '&importType=' + importType, false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }
    //..
    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        window.location.reload();
    }
    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/importItemTDownload.do?importType=" + importType);
        $("body").append(form);
        form.submit();
        form.remove();
    }
    //..
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            } else {
                clearUploadFile();
                mini.alert('请上传excel文件！');
            }
        }
    }
    //..
    function uploadFile() {
        $("#inputFile").click();
    }
    //..
    function exportItem(type) {
        var params = [];
        var obj = {};
        obj.name = "applyId";
        obj.value = applyId;
        params.push(obj);
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.attr('action', jsUseCtxPath + '/serviceEngineering/core/exportPartsAtlas/exportItem.do?type=' + type);
        excelForm.submit();
    }
</script>
</body>
</html>
