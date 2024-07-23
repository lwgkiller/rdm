<%--这个页面由六个菜单共享，流程一样,分别是：--%>
<%--新产品导入下的(新品试制问题,XPSZ),(新品整机试验问题,XPZDSY),(新品路试问题,XPLS)--%>
<%--分析改进下的(厂内问题改进,CNWT),(市场问题改进,SCWT),(海外问题改进,HWWT)--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>质量改进</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/zlgjNPI/zlgjEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/sys/echarts/echartsFrontCustom.js?t=1.5.137" type="text/javascript"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts.min.js"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts-wordcloud.min.js"></script>
    <script src="${ctxPath}/scripts/sys/echarts/roll.js" type="text/javascript"></script>
    <link href="${ctxPath}/scripts/sys/echarts/css/roll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css"/>
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

        .rmMem {
            background-color: #848382ad
        }
    </style>
</head>
<body>
<div id="loading" class="loading" style="display:none;text-align:center;"><img
        src="${ctxPath}/styles/images/loading.gif"></div>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="changeInfo" class="mini-button" style="display: none" onclick="changeInfo()"><spring:message
                code="page.zlgjEdit.name"/></a>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message
                code="page.zlgjEdit.name1"/></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.zlgjEdit.name2"/></a>
    </div>
</div>
<div class="mini-fit" id="content">
    <div class="form-container" style="margin: 0 auto">
        <form id="formZlgj" method="post">
            <input id="wtId" name="wtId" class="mini-hidden"/>
            <input id="yyId" name="yyId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="applyUserName" name="applyUserName" class="mini-hidden"/>
            <input id="applyUserDeptName" name="applyUserDeptName" class="mini-hidden"/>
            <fieldset id="fdBaseInfo">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="checkboxBaseInfo"
                               onclick="zltoggleFieldSet(this, 'fdBaseInfo')" hideFocus/>
                        <spring:message code="page.zlgjEdit.name3"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 10%"><spring:message code="page.zlgjEdit.name4"/>：<span style="color:red">*</span>
                            </td>
                            <td style="width: 20%;min-width:140px">
                                <input id="wtlx" name="wtlx" class="mini-combobox" style="width:98%;"
                                       textField="value" valueField="key"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       onvaluechanged="wtlxChange()"
                                       required="false" allowInput="false" showNullItem="true"
                                       nullItemText="<spring:message code="page.zlgjEdit.name5" />..."
                                       data="[{'key' : 'XPSZ','value' : '新品试制'},{'key' : 'XPZDSY','value' : '新品整机试验'},{'key' : 'XPLS','value' : '新品路试'},
                                   {'key' : 'CNWT','value' : '厂内问题'},{'key' : 'SCWT','value' : '市场问题'},{'key' : 'HWWT','value' : '海外问题'}
                                   ,{'key' : 'WXBLX','value' : '维修便利性'},{'key' : 'LBJSY','value' : '新品零部件试验'}]"/>
                            </td>
                            <td style="width: 10%"><spring:message code="page.zlgjEdit.name6"/>：</td>
                            <td style="width: 21%">
                                <input id="zlgjNumber" name="zlgjNumber" class="mini-textbox" style="width:98%;"
                                       readonly/>
                            </td>
                            <td style="width: 10%"><spring:message code="page.zlgjEdit.name7"/>：<span style="color:red">*</span>
                                <u style="float: right;"><a href="#" onclick="showJjcd()"><spring:message
                                        code="page.zlgjEdit.name8"/></a></u>
                            </td>
                            <td style="width: 28%">
                                <input id="jjcd" name="jjcd" class="mini-combobox" style="width:98%;"
                                       textField="value" valueField="key"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="false"
                                       nullItemText="<spring:message code="page.zlgjEdit.name5" />..."
                                       data="[{'key' : '特急','value' : '特急'},{'key' : '紧急','value' : '紧急'},{'key' : '一般','value' : '一般'}]"/>
                            </td>
                        </tr>
                        <tr>
                            <td><spring:message code="page.zlgjEdit.name28"/>：</td>
                            <td style="width: 20%;">
                                <a id="uploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;"
                                   onclick="addPicFile"><spring:message code="page.zlgjEdit.name29"/></a>
                            </td>
                            <td style="display:none;"><spring:message code="page.zlgjEdit.name30"/>(<span style="color: red"><spring:message
                                    code="page.zlgjEdit.name31"/></span>)：
                            </td>
                            <td style="display:none;">
                                <input id="gzsplj" name="gzsplj" class="mini-textarea"
                                       style="width:98%;overflow: auto"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name32"/>：<span style="color:red">*</span>
                                <u style="float: right;"><a href="#" onclick="showYzcd()"><spring:message
                                        code="page.zlgjEdit.name8"/></a></u>
                            </td>
                            <td>
                                <input id="yzcd" name="yzcd" class="mini-combobox" style="width:98%;"
                                       textField="value" valueField="key"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="false"
                                       data="[{'key' : 'A','value' : 'A'},{'key' : 'B','value' : 'B'},{'key' : 'C','value' : 'C'},
                                           {'key' : 'W','value' : 'W'}]" onvaluechanged="yzcdChange()"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name14"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="zrcpzgId" name="zrcpzgId" textname="zrcpzgName" class="mini-user rxc"
                                       plugins="mini-user"
                                       style="width:98%;height:34px;" allowinput="false"
                                       label="<spring:message code="page.zlgjEdit.name15" />" length="50" maxlength="50"
                                       mainfield="no"
                                       single="true" onvaluechanged="setRespDept()"/>
                            </td>
                        </tr>
                        <tr>
                            <td><spring:message code="page.zlgjEdit.name16"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="ssbmId" name="ssbmId" textname="ssbmName" class="mini-dep rxc"
                                       plugins="mini-dep"
                                       style="width:98%;height:34px" showclose="false"
                                       allowinput="false" length="500" maxlength="500" minlen="0" single="true"
                                       initlogindep="false"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name17"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="zrrId" name="zrrId" textname="zrrName" class="mini-user rxc"
                                       plugins="mini-user"
                                       style="width:98%;height:34px;" allowinput="false"
                                       label="<spring:message code="page.zlgjEdit.name15" />" length="50" maxlength="50"
                                       mainfield="no"
                                       single="true"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name19"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="lbjgys" name="lbjgys" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr id="zlgjTr1">
                            <td style="width: 10%"><spring:message code="page.zlgjEdit.name9"/>：<span style="color:red">*</span>
                            </td>
                            <td style="width: 19%;min-width:170px">
                                <input id="jiXing" name="jiXing" class="mini-combobox" style="width:98%;"
                                       textField="text" valueField="key_"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="false"
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=CPLB"/>
                                <%--<input id="jiXing" name="jiXing" class="mini-combobox" style="width:98%;"--%>
                                       <%--textField="value" valueField="key"--%>
                                       <%--emptyText="<spring:message code="page.zlgjEdit.name5" />..."--%>
                                       <%--required="false" allowInput="false" showNullItem="true"--%>
                                       <%--nullItemText="<spring:message code="page.zlgjEdit.name5" />..."--%>
                                       <%--data="[{'key' : 'WEIWA','value' : '微挖'},{'key' : 'LUNWA','value' : '轮挖'},{'key' : 'XIAOWA','value' : '小挖'},--%>
                                           <%--{'key' : 'ZHONGWA','value' : '中挖'},{'key' : 'DAWA','value' : '大挖'},{'key' : 'TEWA','value' : '特挖'},--%>
                                           <%--{'key' : 'SHUJU','value' : '属具'},{'key' : 'XINNENGYUAN','value' : '新能源'},{'key' : 'HAIWAI','value' : '海外'}]"/>--%>
                            </td>
                            <td style="width: 14%"><spring:message code="page.zlgjEdit.name10"/>：<span
                                    style="color:red">*</span></td>
                            <td style="width: 21%">
                                <input id="smallJiXing" name="smallJiXing" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name11"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="wtms" name="wtms" class="mini-textarea"
                                       style="width:98%;overflow: auto;height: 80px"/>
                            </td>
                        </tr>
                        <tr id="zlgjTr2">
                            <td style="width: 10%"><spring:message code="page.zlgjEdit.name12"/>：<span
                                    style="color:red">*</span></td>
                            <td style="width: 21%">
                                <input id="zjbh" name="zjbh" class="mini-textarea"
                                       style="width:98%;overflow: auto;height: 80px"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name25"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="wtpcjc" name="wtpcjc" class="mini-textarea"
                                       style="width:98%;overflow: auto;height: 80px"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name26"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="xcczfa" name="xcczfa" class="mini-textarea"
                                       style="width:98%;overflow: auto;height: 80px"/>
                            </td>
                        </tr>
                        <tr id="zlgjTr3">
                            <td><spring:message code="page.zlgjEdit.name18"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="sggk" name="sggk" class="mini-textbox" style="width:98%;"/>
                            </td>

                            <td><spring:message code="page.zlgjEdit.name20"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="jjsl" name="jjsl" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name21"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="gzsl" name="gzsl" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr id="zlgjTr4">
                            <td><spring:message code="page.zlgjEdit.name22"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="gzxt" name="gzxt" class="mini-combobox" style="width:98%;"
                                       textField="text" valueField="key_"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="true"
                                       nullItemText="<spring:message code="page.zlgjEdit.name5" />..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=GZXT"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name23"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="gzbw" name="gzbw" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name24"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="gzlj" name="gzlj" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr id="lbjTr1" style="display: none">
                            <td style="text-align: left;width: 15%">零部件型号：<span style="color:red">*</span></td>
                            <td style="min-width:170px">
                                <input id="componentModel" name="lbjId" textname="componentModel" style="width:98%;" property="editor"
                                       class="mini-buttonedit" showClose="true"
                                       oncloseclick="onSelectLbjCloseClick" allowInput="false"
                                       onbuttonclick="selectLbj()"/>
                            </td>
                            <td style="text-align: left;width: 15%">零部件类别：</td>
                            <td style="min-width:170px">
                                <input id="componentCategory" name="componentCategory"
                                       class="mini-combobox" style="width:98%" readonly
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=componentCategory"
                                       valueField="key" textField="value"/>
                            </td>
                            <td style="text-align: left;width: 15%">零部件名称：</td>
                            <td style="min-width:170px">
                                <input id="componentName" name="componentName" readonly class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr id="lbjTr2" style="display: none">
                            <td style="text-align: left;width: 15%">配套主机型号：</td>
                            <td style="min-width:170px">
                                <input id="machineModel" name="machineModel" readonly class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="text-align: left;width: 15%">物料编码：</td>
                            <td style="min-width:170px">
                                <input id="materialCode" name="materialCode" readonly class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="text-align: left;width: 15%">试验类型：</td>
                            <td style="min-width:170px">
                                <input id="testType" name="testType" readonly
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testType"
                                       valueField="key" textField="value"/>
                            </td>
                        </tr>
                        <tr id="lbjTr3" style="display: none">
                            <td style="text-align: left;width: 15%">试验完成时间：</td>
                            <td style="min-width:170px">
                                <input id="completeTestMonth" readonly name="completeTestMonth" class="mini-monthpicker" style="width:98%;" allowinput="false"/>
                            </td>
                            <td style="text-align: left;width: 15%">试验次数：</td>
                            <td style="min-width:170px">
                                <input id="testRounds" name="testRounds" readonly
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testRounds"
                                       valueField="key" textField="value"/>
                            </td>
                            <td style="text-align: left;width: 15%">试验承担单位：</td>
                            <td style="min-width:170px">
                                <input id="laboratory" name="laboratory" readonly class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr id="lbjTr4" style="display: none">
                            <td style="text-align: left;width: 15%">样品类型：</td>
                            <td style="min-width:170px">
                                <input id="sampleType" name="sampleType" readonly
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleType"
                                       valueField="key" textField="value"/>
                            </td>
                            <td style="text-align: left;width: 15%">试验负责人：</td>
                            <td style="min-width:170px">
                                <input id="testLeaderId" name="testLeaderId" textname="testLeader" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="试验负责人" length="50"
                                       mainfield="no" single="true" readonly/>
                            </td>
                            <td style="text-align: left;width: 15%">改进建议：</td>
                            <td style="min-width:170px">
                                <input id="improvementSuggestions" readonly name="improvementSuggestions" class="mini-textarea" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr id="lbjTr5" style="display: none">
                            <td style="text-align: left;width: 15%">不合格项说明：</td>
                            <td style="min-width:170px">
                                <input id="nonconformingDescription" readonly  name="nonconformingDescription" class="mini-textarea" style="width:98%;"/>
                            </td>
                            <td style="text-align: left;width: 15%">测试结论：</td>
                            <td style="min-width:170px">
                                <input id="testConclusion" readonly name="testConclusion" class="mini-textarea" style="width:98%;"/>
                            </td>
                            <td>测试报告：</td>
                            <td style="width: 20%;">
                                <a id="uploadcsFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;"
                                   onclick="addlbjFile">附件列表</a>
                            </td>
                        </tr>
                        <tr>
                            <td><spring:message code="page.zlgjEdit.name36"/>：</td>
                            <td>
                                <input id="yzTime" name="yzTime" class="mini-datepicker" format="yyyy-MM-dd"
                                       allowInput="false"
                                       showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name37"/>：</td>
                            <td>
                                <input id="yjTime" name="yjTime" class="mini-datepicker" format="yyyy-MM-dd"
                                       allowInput="false"
                                       showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name38"/>：</td>
                            <td>
                                <input id="sffgsp" name="sffgsp" class="mini-combobox" style="width:98%;"
                                       textField="value" valueField="key"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="false"
                                       data="[{'key' : 'YES','value' : '是'},{'key' : 'NO','value' : '否'}]"/>
                            </td>
                        </tr>
                        <tr id="dateData" style="display: none">
                            <td>生产日期：</td>
                            <td>
                                <input id="scDate" name="scDate" class="mini-datepicker" format="yyyy-MM-dd"
                                       allowInput="false"
                                       showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                            </td>
                            <td>交机日期：</td>
                            <td>
                                <input id="jjDate" name="jjDate" class="mini-datepicker" format="yyyy-MM-dd"
                                       allowInput="false"
                                       showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                            </td>
                            <td>故障日期：</td>
                            <td>
                                <input id="gzDate" name="gzDate" class="mini-datepicker" format="yyyy-MM-dd"
                                       allowInput="false"
                                       showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr id="gzData" style="display: none">
                            <td>故障件批次号：</td>
                            <td>
                                <input id="gzNum" name="gzNum" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td>故障现象：<span style="color:red">*(1000字以内)</span></td>
                            <td>
                                <input id="gzProgram" name="gzProgram" class="mini-textarea" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td><spring:message code="page.zlgjEdit.name33"/>：</td>
                            <td id="sfxygj">
                                <input id="ifgj" name="ifgj" class="mini-radiobuttonlist" style="width:100%;"
                                       repeatItems="2" repeatLayout="table" repeatDirection="horizontal" value="yes"
                                       textfield="jgxsName" valuefield="jgxsId" onvaluechanged="ifChange"
                                       data="[ {'jgxsName' : '是','jgxsId' : 'yes'},{'jgxsName' : '否','jgxsId' : 'no'}]"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name34"/>：</td>
                            <td id="bgjly">
                                <input id="noReason" name="noReason" class="mini-textarea"
                                       style="width:98%;overflow: auto"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name35"/>：</td>
                            <td style="width: 20%;">
                                <a id="uploadgjFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;"
                                   onclick="addgjFile"><spring:message code="page.zlgjEdit.name29"/></a>
                            </td>
                        </tr>
                        <tr>
                            <td>是否并案：</td>
                            <td>
                                <input id="ifba" name="ifba" class="mini-radiobuttonlist" style="width:100%;" value="no"
                                       textfield="baName" valuefield="basId" onvaluechanged="clearNum()"
                                       data="[ {'baName' : '是','basId' : 'yes'},{'baName' : '否','basId' : 'no'}]"/>
                            </td>
                            <td>追溯问题编号：</td>
                            <td>
                                <input id="togetherNum" name="togetherId" textname="togetherNum" style="width:98%;" property="editor"
                                       class="mini-buttonedit" showClose="true"
                                       oncloseclick="onSelectZlgjCloseClick" allowInput="false"
                                       onbuttonclick="selectZlgj()"/>
                            </td>
                            <td id="hwxsdqtd1" style="text-align: left;width: 15%;display: none">销售大区：</td>
                            <td id="hwxsdqtd2" style="min-width:170px;display: none">
                                <input id="saleArea" name="saleArea"
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=saleArea"
                                       valueField="key" textField="value"/>
                            </td>
                        </tr>
                        <tr id="zlgjTr5">
                            <td id="tdm_sylx_td1" style="width: 14%;display: none"><spring:message
                                    code="page.zlgjEdit.name39"/>：<span style="color:red">*</span></td>
                            <td id="tdm_sylx_td2" style="width: 19%;min-width:170px;display: none">
                                <input id="tdmSylx" name="tdmSylx" class="mini-combobox" style="width:98%;"
                                       textField="value" valueField="key"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="true"
                                       nullItemText="<spring:message code="page.zlgjEdit.name5" />..."
                                       data="[{'key' : '新品试验','value' : '新品试验'},{'key' : '竞品试验','value' : '竞品试验'},
                                           {'key' : '专项试验','value' : '专项试验'},{'key' : '可靠性试验','value' : '可靠性试验'},
                                           {'key' : '委外认证','value' : '委外认证'},{'key' : '调试试验','value' : '调试试验'},
                                           {'key' : '故障试验','value' : '故障试验'}]"/>
                            </td>
                            <td style="width: 14%;"><spring:message code="page.zlgjEdit.name40"/>：<span
                                    style="color:red">*</span></td>
                            <td style="width: 19%;min-width:170px;">
                                <input id="pfbz" name="pfbz" class="mini-combobox" style="width:98%;"
                                       textField="text" valueField="key_"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="false"
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=PFBZ"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name41"/>：</td>
                            <td>
                                <input id="gzl" name="gzl" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name13"/>：<span style="color:red">*</span></td>
                            </td>
                            <td>
                                <input id="gzxs" name="gzxs" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 14%"><spring:message code="page.zlgjEdit.name42"/>：<span
                                    style="color:red">*</span></td>
                            <td style="width: 19%;min-width:170px">
                                <input id="improvementMethod" name="improvementMethod" class="mini-combobox"
                                       style="width:98%;"
                                       textField="value" valueField="key"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="true"
                                       nullItemText="<spring:message code="page.zlgjEdit.name5" />..."
                                       data="[{'key' : '七步法','value' : '七步法'},
                                           {'key' : '快速改进','value' : '快速改进'}]"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name27"/>：<span style="color:red">*</span></td>
                            <td>
                                <input id="gjyq" name="gjyq" class="mini-textarea"
                                       style="width:98%;overflow: auto;height: 80px"/>
                            </td>
                            <td style="width: 10%"><spring:message code="page.zlgjEdit.name43"/>：</td>
                            <td style="width: 21%">
                                <input id="rounds" name="rounds" class="mini-textbox" style="width:98%;" readonly/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 14%">故障部件物料号：</td>
                            <td style="width: 19%;min-width:170px">
                                <input id="issue_part_no" name="issue_part_no" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <%--问题可能原因--%>
            <fieldset id="fdMemberInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxMemberInfo" onclick="zltoggleFieldSet(this, 'fdMemberInfo')"
                               hideFocus/>
                        <spring:message code="page.zlgjEdit.name44"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <div class="mini-toolbar" id="projectMemberButtons">
                        <a class="mini-button" id="ceshi" plain="true" onclick="addZlgj_reasonInfoRow"><spring:message
                                code="page.zlgjEdit.name45"/></a>
                        <a class="mini-button btn-red" plain="true" onclick="removeZlgj_reasonInfoRow"><spring:message
                                code="page.zlgjEdit.name46"/></a>
                    </div>
                    <div id="grid_zlgj_reasonInfo" class="mini-datagrid" allowResize="false"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false" showPager="false" allowCellWrap="true"
                         showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div field="id" headerAlign="left" visible="false">id</div>
                            <div field="reason" headerAlign="center" align="center"><spring:message
                                    code="page.zlgjEdit.name47"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/></div>
                        </div>
                    </div>
                </div>
            </fieldset>
            <br>
            <%--临时措施--%>
            <fieldset id="fdAchievementInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxAchievementInfo"
                               onclick="zltoggleFieldSet(this, 'fdAchievementInfo')" hideFocus/>
                        <spring:message code="page.zlgjEdit.name48"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <div class="mini-toolbar" id="projectAchievementButtons">
                        <a class="mini-button" plain="true" onclick="addCuoshi()"><spring:message
                                code="page.zlgjEdit.name49"/></a>
                        <a class="mini-button btn-red" plain="true" onclick="delCuoshi()"><spring:message
                                code="page.zlgjEdit.name50"/></a>
                    </div>
                    <div id="grid_zlgj_linshicuoshi" class="mini-datagrid" allowResize="false"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false" showPager="false" allowCellWrap="true"
                         showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div field="id" align="center" width="1" headerAlign="left" visible="false">id</div>
                            <div field="rounds" align="center" width="1" headerAlign="left" visible="false">rounds</div>
                            <div field="yyId" displayField="reason" align="center" headerAlign="center" width="80"
                                 renderer="render"><spring:message code="page.zlgjEdit.name47"/><span
                                    style="color:red">*</span>
                                <input property="editor" class="mini-combobox" style="width:98%;"
                                       textField="reason" valueField="yyId"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="true"
                                       nullItemText="<spring:message code="page.zlgjEdit.name5" />..."/>
                            </div>
                            <div field="dcfa" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name51"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="ddpc" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name52"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <%--<div field="zrr"  displayField="zrrName" headerAlign="center" align="center" width="40" >责任人<span style="color:red">*</span>
                                 <input property="editor" class="mini-user rxc" plugins="mini-user"  style="width:50%;height:34px;" allowinput="false" length="50" maxlength="50"  mainfield="no"  single="true" />
                            </div>--%>
                            <div field="wcTime" headerAlign="center" align="center" width="40" dateFormat="yyyy-MM-dd">
                                <spring:message code="page.zlgjEdit.name53"/><span
                                    style="color:red">*</span>
                                <input property="editor" class="mini-datepicker" style="width:100%;"/>
                            </div>
                            <div field="tzdh" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name54"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="rounds" headerAlign="center" align="center" width="50"><spring:message
                                    code="page.zlgjEdit.name43"/></div>
                            <div field="lszmcl" align="center" headerAlign="center" width="50"
                                 renderer="lszmclAttachFile"><spring:message code="page.zlgjEdit.name55"/>
                                <span style="color:red">*</span>
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
            <br>
            <%--原因验证--%>
            <fieldset id="fdYanzhengInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxYanzhengInfo"
                               onclick="zltoggleFieldSet(this, 'fdYanzhengInfo')" hideFocus/>
                        <spring:message code="page.zlgjEdit.name56"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <div class="mini-toolbar" id="zlgjYanzhengButtons">
                        <a class="mini-button" plain="true" onclick="addZlgj_yanzhengInfoRow"><spring:message
                                code="page.zlgjEdit.name49"/></a>
                        <a class="mini-button btn-red" plain="true" onclick="removeZlgj_yanzhengInfoRow"><spring:message
                                code="page.zlgjEdit.name50"/></a>
                    </div>
                    <div id="grid_zlgj_yanzhengInfo" class="mini-datagrid" allowResize="false" allowCellWrap="true"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showPager="false" showColumnsMenu="false" allowcelledit="true"
                         allowcellselect="true"
                         allowAlternating="true" oncellvalidation="onCellValidation">
                        <div property="columns">
                            <div type="checkcolumn"></div>
                            <div field="id" headerAlign="left" visible="false">id</div>
                            <div field="rounds" headerAlign="left" visible="false">rounds</div>
                            <div field="yyId" displayField="reason" align="center" headerAlign="center" width="80"
                                 renderer="render"><spring:message code="page.zlgjEdit.name47"/><span
                                    style="color:red">*</span>
                                <input property="editor" class="mini-combobox" style="width:98%;"
                                       textField="reason" valueField="yyId"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="true"
                                       nullItemText="<spring:message code="page.zlgjEdit.name5" />..."/>
                            </div>
                            <div field="jcjg" headerAlign="center" align="center" width="80"><spring:message
                                    code="page.zlgjEdit.name57"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/></div>
                            <div field="jielun" headerAlign="center" align="center" width="80"><spring:message
                                    code="page.zlgjEdit.name58"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/></div>
                            <div field="rounds" headerAlign="center" align="center" width="50"><spring:message
                                    code="page.zlgjEdit.name43"/></div>
                            <div field="yyzmcl" align="center" headerAlign="center" width="50"
                                 renderer="yyzmclAttachFile"><spring:message code="page.zlgjEdit.name55"/>
                                <span style="color:red">*</span>
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
            <br>
            <%--方案验证--%>
            <fieldset id="fdFanganInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxFanganInfo" onclick="zltoggleFieldSet(this, 'fdFanganInfo')"
                               hideFocus/>
                        <spring:message code="page.zlgjEdit.name59"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <div class="mini-toolbar" id="zlgjFanganButtons">
                        <a class="mini-button" plain="true" onclick="addyzFangan()"><spring:message
                                code="page.zlgjEdit.name49"/></a>
                        <a class="mini-button btn-red" plain="true" onclick="delyzFangan()"><spring:message
                                code="page.zlgjEdit.name50"/></a>
                    </div>
                    <div id="grid_zlgj_fanganyz" class="mini-datagrid" allowResize="false"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false" showPager="false" allowCellWrap="true"
                         showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div field="id" align="center" width="1" headerAlign="left" visible="false">id</div>
                            <div field="rounds" align="center" width="1" headerAlign="left" visible="false">rounds</div>
                            <div field="yyId" displayField="reason" align="center" headerAlign="center" width="80"
                                 renderer="render"><spring:message code="page.zlgjEdit.name47"/><span
                                    style="color:red">*</span>
                                <input property="editor" class="mini-combobox" style="width:98%;"
                                       textField="reason" valueField="yyId"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="true"
                                       nullItemText="<spring:message code="page.zlgjEdit.name5" />..."/>
                            </div>
                            <div field="gjfa" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name60"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <%--<div field="yzzrr"  displayField="yzzrrName" headerAlign="center" align="center"  width="40">验证责任人<span style="color:red">*</span>
                                <input property="editor" class="mini-user rxc" plugins="mini-user"  style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"  mainfield="no"  single="true" />
                            </div>--%>
                            <div field="sjTime" headerAlign="center" align="center" width="40" dateFormat="yyyy-MM-dd">
                                <spring:message code="page.zlgjEdit.name61"/>
                                <span style="color:red">*</span>
                                <input property="editor" class="mini-datepicker" style="width:100%;"/>
                            </div>
                            <div field="yzjg" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name62"/>
                                <span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="rounds" headerAlign="center" align="center" width="50"><spring:message
                                    code="page.zlgjEdit.name43"/></div>
                            <div field="fazmcl" align="center" headerAlign="center" width="50"
                                 renderer="fazmclAttachFile"><spring:message code="page.zlgjEdit.name55"/>
                                <span style="color:red">*</span>
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
            <br>
            <%--永久解决方案--%>
            <fieldset id="fdFanganjjInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxFanganjjInfo"
                               onclick="zltoggleFieldSet(this, 'fdFanganjjInfo')" hideFocus/>
                        <spring:message code="page.zlgjEdit.name63"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <div class="mini-toolbar" id="zlgjFanganjjButtons">
                        <a class="mini-button" plain="true" onclick="addjjFangan()"><spring:message
                                code="page.zlgjEdit.name49"/></a>
                        <a class="mini-button btn-red" plain="true" onclick="deljjFangan()"><spring:message
                                code="page.zlgjEdit.name50"/></a>
                    </div>
                    <div id="grid_zlgj_fanganjj" class="mini-datagrid" allowResize="false"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false" showPager="false" allowCellWrap="true"
                         showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div field="id" align="center" width="1" headerAlign="left" visible="false">id</div>
                            <div field="rounds" align="center" width="1" headerAlign="left" visible="false">rounds</div>
                            <div field="yyId" displayField="reason" align="center" headerAlign="center" width="80"
                                 renderer="render"><spring:message code="page.zlgjEdit.name47"/><span
                                    style="color:red">*</span>
                                <input property="editor" class="mini-combobox" style="width:98%;"
                                       textField="reason" valueField="yyId"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="true"
                                       nullItemText="<spring:message code="page.zlgjEdit.name5" />..."/>
                            </div>
                            <div field="cqcs" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name64"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <%--<div field="zrr"  displayField="zrrName" headerAlign="center" align="center" width="40">责任人<span style="color:red">*</span>
                                <input property="editor" class="mini-user rxc" plugins="mini-user"  style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"  mainfield="no"  single="true" />
                            </div>--%>
                            <div field="wcTime" headerAlign="center" align="center" width="40" dateFormat="yyyy-MM-dd">
                                <spring:message code="page.zlgjEdit.name61"/><span
                                    style="color:red">*</span>
                                <input property="editor" class="mini-datepicker" style="width:100%;"/>
                            </div>
                            <div field="tzdh" headerAlign="center" align="center" width="80"><spring:message
                                    code="page.zlgjEdit.name54"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="yjqhch" headerAlign="center" align="center" width="80"><spring:message
                                    code="page.zlgjEdit.name65"/><span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="rounds" headerAlign="center" align="center" width="50"><spring:message
                                    code="page.zlgjEdit.name43"/></div>
                            <div field="meetingPlanCompletion" align="center" headerAlign="center" width="50"
                                 renderer="renderAttachFile"><spring:message code="page.zlgjEdit.name55"/>
                                <span style="color:red">*</span>
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
            <br>
            <%--改进效果跟踪--%>
            <fieldset id="fdXGInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxXGInfo" onclick="zltoggleFieldSet(this, 'fdXGInfo')"
                               hideFocus/>
                        <spring:message code="page.zlgjEdit.name66"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width:25%"><spring:message code="page.zlgjEdit.name67"/>：<span style="color:red">*</span>
                            </td>
                            <td style="width:25%">
                                <input id="gjxg" name="gjxg" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="width:25%"><spring:message code="page.zlgjEdit.name68"/>：<span style="color:red">*</span>
                            </td>
                            <td style="width:25%">
                                <input id="gzr" name="gzr" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width:16%"><spring:message code="page.zlgjEdit.name69"/>：
                            </td>
                            <td>
                                <input id="gjhgzl" name="gjhgzl" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td><spring:message code="page.zlgjEdit.name70"/>：<span style="color:red">*</span></td>
                            </td>
                            <td>
                                <input id="sfyx" name="sfyx" class="mini-combobox" style="width:98%;"
                                       textField="value" valueField="key"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="true"
                                       nullItemText="<spring:message code="page.zlgjEdit.name5" />..."
                                       data="[{'key' : 'YES','value' : '是'},{'key' : 'NO','value' : '否'}]"/>
                            </td>
                        </tr>
                        <tr>
                            <td><spring:message code="page.zlgjEdit.name55"/>：</td>
                            <td style="width: 20%;">
                                <a id="uploadxgFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;"
                                   onclick="addxgFile"><spring:message code="page.zlgjEdit.name29"/></a>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <%--风险排查及整改--%>
            <fieldset id="riskInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxRiskInfo" onclick="zltoggleFieldSet(this, 'riskInfo')"
                               hideFocus/>
                        <spring:message code="page.zlgjEdit.name71"/>
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <header style="font-size: 17px;height: 25px">
                        <spring:message code="page.zlgjEdit.name72"/>
                        <span style="color: red">注：产品所长选择负责人须为本部门人员</span>
                    </header>
                    <div class="mini-toolbar" id="zlgjRiskUserButtons">
                        <a class="mini-button" plain="true" onclick="addRiskUser()"><spring:message
                                code="page.zlgjEdit.name49"/></a>
                        <a class="mini-button btn-red" plain="true" onclick="delRiskUser()"><spring:message
                                code="page.zlgjEdit.name50"/></a>
                    </div>
                    <div id="grid_zlgj_riskUser" class="mini-datagrid" allowResize="false"
                         oncellbeginedit="OnCellUserEditDetail"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false" showPager="false" allowCellWrap="true"
                         showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div field="leaderId" displayField="leaderName" width="30" headerAlign="center"
                                 align="center"><spring:message code="page.zlgjEdit.name73"/><span
                                    style="color:red">*</span>
                                <input property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50" onvaluechanged="clearRes()"
                                       mainfield="no" single="true" name="leaderId" textname="leaderName"/>
                            </div>
                            <div field="resId" displayField="resName" width="30" headerAlign="center" align="center">
                                <spring:message code="page.zlgjEdit.name74"/><span style="color:red">*</span>
                                <input property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"
                                       mainfield="no" single="true" name="resId" textname="resName"/>
                            </div>
                            <div field="qualified" width="40" headerAlign="center" align="center"><spring:message
                                    code="page.zlgjEdit.name75"/><span style="color:red">*</span>
                                <input property="editor" class="mini-combobox" style="width:98%;"
                                       textField="value" valueField="key"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="true"
                                       nullItemText="<spring:message code="page.zlgjEdit.name5" />..."
                                       data="[
										   {'key' : '通过','value' : '通过'}
										   ,{'key' : '不通过','value' : '不通过'}]"
                                />
                            </div>
                            <div field="unqualified" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name76"/>
                                <span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <header style="font-size: 17px;height: 25px">
                        <spring:message code="page.zlgjEdit.name77"/>
                    </header>
                    <div class="mini-toolbar" id="zlgjRiskButtons">
                        <a class="mini-button" plain="true" onclick="addRisk()"><spring:message
                                code="page.zlgjEdit.name49"/></a>
                        <a class="mini-button btn-red" plain="true" onclick="delRisk()"><spring:message
                                code="page.zlgjEdit.name50"/></a>
                    </div>
                    <div id="grid_zlgj_risk" class="mini-datagrid" allowResize="false"
                         oncellbeginedit="OnCellRiskEditDetail"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false" showPager="false" allowCellWrap="true"
                         showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div field="riskId" align="center" width="1" headerAlign="left" visible="false">riskId</div>
                            <div field="rounds" align="center" width="1" headerAlign="left" visible="false">rounds</div>
                            <div field="riskContent" align="center" headerAlign="center"
                                 width="80" renderer="render"><spring:message code="page.zlgjEdit.name78"/><span
                                    style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="riskLevel" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name79"/><span style="color:red">*</span>
                                <input property="editor" class="mini-combobox" style="width:98%;"
                                       textField="value" valueField="key"
                                       emptyText="<spring:message code="page.zlgjEdit.name5" />..."
                                       required="false" allowInput="false" showNullItem="true"
                                       nullItemText="<spring:message code="page.zlgjEdit.name5" />..."
                                       data="[
										   {'key' : '无风险','value' : '无风险'}
										   ,{'key' : '高风险','value' : '高风险'}
										   ,{'key' : '中风险','value' : '中风险'}
										   ,{'key' : '低风险','value' : '低风险'}]"
                                />
                            </div>
                            <div field="solution" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name60"/>
                                <span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="market" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name80"/>
                                <span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="handle" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name81"/>
                                <span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="noticeNo" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name54"/>
                                <span style="color:red">*</span>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="changeNo" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name65"/>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="fazmcl" align="center" headerAlign="center" width="50"
                                 renderer="riskAttachFile"><spring:message code="page.zlgjEdit.name55"/>
                                <span style="color:red">*</span>
                            </div>
                            <div field="creator" headerAlign="center" align="center" width="100"><spring:message
                                    code="page.zlgjEdit.name82"/>
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="CREATE_TIME_" width="75" headerAlign="center" align="center"
                                 dateFormat="yyyy-MM-dd HH:mm:ss"><spring:message code="page.zlgjEdit.name83"/></div>
                        </div>
                    </div>
                </div>
            </fieldset>
            <br>
        </form>
    </div>
</div>

<div id="jjcdWindow" title="<spring:message code="page.zlgjEdit.name7" />" class="mini-window"
     style="width:950px;height:340px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px">
        <div id="jjcdListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="roleId" allowAlternating="true" showPager="false" allowCellWrap="true"
             allowCellEdit="false" allowCellSelect="false">
            <div property="columns">
                <div field="xx" width="30" headerAlign="center" align="center"><spring:message
                        code="page.zlgjEdit.name84"/>
                </div>
                <div field="sm" width="100" headerAlign="center" align="left"><spring:message
                        code="page.zlgjEdit.name85"/>
                </div>
                <div field="yq" width="120" headerAlign="center" align="left"><spring:message
                        code="page.zlgjEdit.name86"/>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="yzcdWindow" title="<spring:message code="page.zlgjEdit.name32" />" class="mini-window"
     style="width:1230px;height:550px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px">
        <div id="yzcdListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="roleId" allowAlternating="true" showPager="false" allowCellWrap="true"
             allowCellEdit="false" allowCellSelect="false">
            <div property="columns">
                <div field="xx" width="30" headerAlign="center" align="center"><spring:message
                        code="page.zlgjEdit.name84"/>
                </div>
                <div field="sm" width="130" headerAlign="center" align="left"><spring:message
                        code="page.zlgjEdit.name85"/>
                </div>
                <div field="yq" name="yq" width="100" headerAlign="center" align="left"><spring:message
                        code="page.zlgjEdit.name86"/>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="selectZlWindow" title="选择质量改进流程" class="mini-window" style="width:1080px;height:600px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input id="parentInputScene" style="display: none"/>
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">流程编号: </span>
        <input class="mini-textbox" width="130" id="searchlcNumber" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">机型: </span>
        <input class="mini-textbox" width="130" id="searchSmallJiXing" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">第一责任人: </span>
        <input id="zrcpzg" name="zrcpzgId" textname="zrcpzgName" class="mini-user rxc"
               plugins="mini-user" style="width:130px;height:34px;" allowinput="false" label="" length="50"
               maxlength="50"
               mainfield="no" single="true"/>
        <span style="font-size: 14px;color: #777">问题处理人: </span>
        <input id="zrr" name="zrrId" textname="zrrName" class="mini-user rxc"
               plugins="mini-user" style="width:130px;height:34px;" allowinput="false" label="" length="50"
               maxlength="50"
               mainfield="no" single="true"/>
        <span style="font-size: 14px;color: #777">提报人: </span>
        <input id="applyUser" name="applyUserId" textname="applyUserName" class="mini-user rxc"
               plugins="mini-user" style="width:130px;height:34px;" allowinput="false" label="" length="50"
               maxlength="50"
               mainfield="no" single="true"/>
        <br>
        <span style="font-size: 14px;color: #777">问题描述: </span>
        <input class="mini-textbox" width="130" id="searchWtms" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchZlgj()">查询</a>
        <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>

    </div>
    <div class="mini-fit">
        <div id="zlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="onRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/xjsdr/core/zlgj/getZlgjList.do">
            <div property="columns">
                <div type="checkcolumn" width="25px"></div>
                <div field="zlgjNumber" width="50" headerAlign="center" align="center" allowSort="false" renderer="getDetail">流程编号</div>
                <div field="smallJiXing" width="50" headerAlign="center" align="center" allowSort="false">机型</div>
                <div field="zrrId" width="50" headerAlign="center" align="center" allowSort="false" visible="false">
                    审核责任人id
                </div>
                <div field="zrcpzgId" width="50" headerAlign="center" align="center" allowSort="false" visible="false">
                    第一责任人id
                </div>
                <div field="zrcpzgName" width="30" headerAlign="center" align="center" allowSort="false">第一责任人</div>
                <div field="zrrName" width="30" headerAlign="center" align="center" allowSort="false">问题处理人</div>
                <div field="applyUserName" width="30" headerAlign="center" align="center" allowSort="false">提报人</div>
                <div field="wtms" sortField="zjbh" width="100" headerAlign="center" align="center" allowSort="true">
                    问题描述
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectZlgjOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectZlgjHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<div id="selectLbjWindow" title="选择零部件" class="mini-window" style="width:1080px;height:600px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">试验编号: </span>
        <input class="mini-textbox" width="130" id="selecttestNo" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">零部件名称: </span>
        <input class="mini-textbox" width="130" id="selectcomponentName" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">零部件型号: </span>
        <input class="mini-textbox" width="130" id="selectcomponentModel" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">物料编码: </span>
        <input class="mini-textbox" width="130" id="selectmaterialCode" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchLbj()">查询</a>
        <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
    </div>
    <div class="mini-fit">
        <div id="lbjListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="onRowDblLbjClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/componentTest/core/kanban/dataListQuery.do">
            <div property="columns">
                <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
                <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
                <div field="testNo" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">试验编号</div>
                <div field="componentCategory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">零部件类别</div>
                <div field="componentName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">零部件名称</div>
                <div field="componentModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">零部件型号</div>
                <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">物料编码</div>
                <div field="sampleType" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">样品类型</div>
                <div field="machineModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">配套主机型号</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectLbjOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectLbjHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var nodeId = "${nodeId}";
    var status = "${status}";
    var wtId = "${wtId}";
    var yyId = "${yyId}";
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var formZlgj = new mini.Form("#formZlgj");
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var wtlxtype = "${wtlxtype}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var grid_zlgj_reasonInfo = mini.get("#grid_zlgj_reasonInfo");
    var grid_zlgj_linshicuoshi = mini.get("#grid_zlgj_linshicuoshi");
    var grid_zlgj_yanzhengInfo = mini.get("#grid_zlgj_yanzhengInfo");
    var grid_zlgj_fanganyz = mini.get("#grid_zlgj_fanganyz");
    var grid_zlgj_fanganjj = mini.get("#grid_zlgj_fanganjj");
    var grid_zlgj_riskUser = mini.get("#grid_zlgj_riskUser");
    var grid_zlgj_risk = mini.get("#grid_zlgj_risk");
    var ifCopy = "${ifCopy}";
    var copyZlgjObj = ${copyZlgjObj};
    var selectZlWindow = mini.get("selectZlWindow");
    var zlListGrid = mini.get("#zlListGrid");
    var selectLbjWindow = mini.get("selectLbjWindow");
    var lbjListGrid = mini.get("#lbjListGrid");
    var jjcdWindow = mini.get("#jjcdWindow");
    var jjcdListGrid = mini.get("#jjcdListGrid");
    var yzcdWindow = mini.get("#yzcdWindow");
    var yzcdListGrid = mini.get("#yzcdListGrid");
    yzcdListGrid.on("update", function () {
        yzcdListGrid.mergeColumns(["yq"]);
    });

    function OnCellUserEditDetail(e) {
        var record = e.record, field = e.field;
        e.cancel = true;
        if (action == 'change') {
            e.cancel = false;
        } else {
            if (field == "leaderId" && (action == "task" && selectLeader == "yes")) {
                e.cancel = false;
            }
            if (field == "resId" && (action == "task" && selectRes == "yes") && record.leaderId == currentUserId) {
                e.cancel = false;
            }
            if ((field == "qualified" || field == "unqualified") && (action == "task" && confirmRisk == "yes")) {
                e.cancel = false;
            }
        }

    }

    function OnCellRiskEditDetail(e) {
        var record = e.record, field = e.field;
        var CREATE_BY_ = record.CREATE_BY_;
        if (action == 'change') {
            e.cancel = false;
        } else {
            if (CREATE_BY_ != '' && CREATE_BY_ != undefined) {
                e.cancel = true;
                if ((action == "task" && (checkRisk == "yes" || recheckRisk == "yes")) && record.CREATE_BY_ == currentUserId) {
                    e.cancel = false;
                }
            }
        }

    }

    grid_zlgj_linshicuoshi.on("cellbeginedit", function (e) {
        if (e.field == "yyId" && grid_zlgj_reasonInfo.getChanges().length > 0) {
            mini.alert(zlgjEdit_name);
            e.editor.setEnabled(false);
            return;
        } else {
            if (e.editor) {
                e.editor.setEnabled(true);
            }
            var record = e.record;
            if (e.field == "yyId") {
                e.editor.setData(reasonTypeList);
            }
        }
    });
    grid_zlgj_yanzhengInfo.on("cellbeginedit", function (e) {
        if (e.field == "yyId" && grid_zlgj_reasonInfo.getChanges().length > 0) {
            mini.alert(zlgjEdit_name);
            e.editor.setEnabled(false);
            return;
        } else {
            if (e.editor) {
                e.editor.setEnabled(true);
            }
            var record = e.record;
            if (e.field == "yyId") {
                e.editor.setData(reasonTypeList);
            }
        }
    });
    grid_zlgj_fanganyz.on("cellbeginedit", function (e) {
        if (e.field == "yyId" && grid_zlgj_reasonInfo.getChanges().length > 0) {
            mini.alert(zlgjEdit_name);
            e.editor.setEnabled(false);
            return;
        } else {
            // e.editor.setEnabled(true);
            if (e.editor) {
                e.editor.setEnabled(true);
            }
            var record = e.record;
            if (e.field == "yyId") {
                e.editor.setData(reasonTypeList);
            }
        }
    });
    grid_zlgj_fanganjj.on("cellbeginedit", function (e) {
        if (e.field == "yyId" && grid_zlgj_reasonInfo.getChanges().length > 0) {
            mini.alert(zlgjEdit_name);
            e.editor.setEnabled(false);
            return;
        } else {
            if (e.editor) {
                e.editor.setEnabled(true);
            }
            var record = e.record;
            if (e.field == "yyId") {
                e.editor.setData(reasonTypeList);
            }
        }
    });

    /**
     * 表单弹出事件控制
     * @param ck
     * @param id
     */
    function zltoggleFieldSet(ck, id) {
        var dom = document.getElementById(id);
        if (ck.checked) {
            dom.className = "";
        } else {
            dom.className = "hideFieldset";
        }
    }

    //临时措施
    function lszmclAttachFile(e) {
        var record = e.record;
        var csId = record.csId;
        var wtId = record.wtId;
        var roundsOfMe = record.rounds;
        var roundsMain = mini.get("rounds").getValue();
        if (!csId) {
            csId = '';
        }
        if (!wtId) {
            wtId = '';
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
            'onclick="addlszmclFile(\'' + csId + '\',\'' + wtId + '\',\'' + roundsOfMe + '\',\'' + roundsMain + '\')">附件</a>';
        return s;
    }

    //原因验证
    function yyzmclAttachFile(e) {
        var record = e.record;
        var yzyyId = record.yzyyId;
        var wtId = record.wtId;
        var roundsOfMe = record.rounds;
        var roundsMain = mini.get("rounds").getValue();
        if (!yzyyId) {
            yzyyId = '';
        }
        if (!wtId) {
            wtId = '';
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
            'onclick="addyyzmclFile(\'' + yzyyId + '\',\'' + wtId + '\',\'' + roundsOfMe + '\',\'' + roundsMain + '\')">附件</a>';
        return s;
    }

    //方案验证
    function fazmclAttachFile(e) {
        var record = e.record;
        var faId = record.faId;
        var wtId = record.wtId;
        var roundsOfMe = record.rounds;
        var roundsMain = mini.get("rounds").getValue();
        if (!faId) {
            faId = '';
        }
        if (!wtId) {
            wtId = '';
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
            'onclick="addfazmclFile(\'' + faId + '\',\'' + wtId + '\',\'' + roundsOfMe + '\',\'' + roundsMain + '\')">附件</a>';
        return s;
    }

    //永久解决方案
    function renderAttachFile(e) {
        var record = e.record;
        var faId = record.faId;
        var wtId = record.wtId;
        var roundsOfMe = record.rounds;
        var roundsMain = mini.get("rounds").getValue();
        if (!faId) {
            faId = '';
        }
        if (!wtId) {
            wtId = '';
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
            'onclick="addghPicFile(\'' + faId + '\',\'' + wtId + '\',\'' + roundsOfMe + '\',\'' + roundsMain + '\')">附件</a>';
        return s;
    }

    //风险排查
    function riskAttachFile(e) {
        var record = e.record;
        var riskId = record.riskId;
        var wtId = record.wtId;
        var CREATE_BY_ = record.CREATE_BY_;
        var roundsOfMe = record.rounds;
        var roundsMain = mini.get("rounds").getValue();
        if (!riskId) {
            riskId = '';
        }
        if (!wtId) {
            wtId = '';
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
            'onclick="addriskFile(\'' + riskId + '\',\'' + wtId + '\',\'' + CREATE_BY_ + '\',\'' + roundsMain + '\')">附件</a>';
        return s;
    }

    function setRespDept() {
        mini.get("zrrId").setValue('');
        mini.get("zrrId").setText('');
        var userId = mini.get("zrcpzgId").getValue();
        if (!userId) {
            mini.get("ssbmId").setValue('');
            mini.get("ssbmId").setText('');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.get("ssbmId").setValue(data.mainDepId);
                mini.get("ssbmId").setText(data.mainDepName);
            }
        });
    }

    function ifChange() {
        var ifgj = mini.get("ifgj").getValue();
        if (ifgj == 'no') {
            mini.get("noReason").setEnabled(true);
            $("#sfxygj").css("background-color", "rgb(226 53 53)");
            $("#noReason textarea").css("background-color", "rgb(226 53 53)");
            grid_zlgj_reasonInfo.setAllowCellEdit(false);
            grid_zlgj_linshicuoshi.setAllowCellEdit(false);
            grid_zlgj_yanzhengInfo.setAllowCellEdit(false);
            grid_zlgj_fanganyz.setAllowCellEdit(false);
            grid_zlgj_fanganjj.setAllowCellEdit(false);
            grid_zlgj_riskUser.setAllowCellEdit(false);
            grid_zlgj_risk.setAllowCellEdit(false);
            $("#zlgjRiskUserButtons").hide();
            $("#zlgjRiskButtons").hide();
            $("#projectMemberButtons").hide();
            $("#projectAchievementButtons").hide();
            $("#zlgjYanzhengButtons").hide();
            $("#zlgjFanganButtons").hide();
            $("#zlgjFanganjjButtons").hide();
        } else {
            mini.get("noReason").setEnabled(false);
            mini.get("noReason").setValue("");
            $("#sfxygj").css("background-color", "");
            $("#noReason textarea").css("background-color", "#ffffff");
            grid_zlgj_reasonInfo.setAllowCellEdit(true);
            grid_zlgj_linshicuoshi.setAllowCellEdit(true);
            grid_zlgj_yanzhengInfo.setAllowCellEdit(true);
            grid_zlgj_fanganyz.setAllowCellEdit(true);
            grid_zlgj_fanganjj.setAllowCellEdit(true);
            $("#projectMemberButtons").show();
            $("#projectAchievementButtons").show();
            $("#zlgjYanzhengButtons").show();
            $("#zlgjFanganButtons").show();
            $("#zlgjFanganjjButtons").show();
        }
    }

    function selectZlgj(inputScene) {
        $("#parentInputScene").val(inputScene);
        selectZlWindow.show();
        searchZlgj();
    }

    //清空查询条件
    function clearForm() {
        mini.get("searchWtms").setValue("");
        mini.get("searchlcNumber").setValue("");
        mini.get("zrcpzg").setValue("");
        mini.get("zrcpzg").setText("");
        mini.get("zrr").setValue("");
        mini.get("zrr").setText("");
        mini.get("applyUser").setText("");
        mini.get("applyUser").setText("");
        mini.get("searchSmallJiXing").setValue("");
        searchZlgj();

    }

    //查询标准
    function searchZlgj() {
        var queryParam = [];
        //其他筛选条件
        var wtlx = $.trim(mini.get("wtlx").getValue());
        if (wtlx) {
            queryParam.push({name: "wtlxtype", value: wtlx});
        }
        var wtms = $.trim(mini.get("searchWtms").getValue());
        if (wtms) {
            queryParam.push({name: "wtms", value: wtms});
        }
        var zlgjNumber = $.trim(mini.get("searchlcNumber").getValue());
        if (zlgjNumber) {
            queryParam.push({name: "zlgjNumber", value: zlgjNumber});
        }
        var zrcpzgId = $.trim(mini.get("zrcpzg").getValue());
        if (zrcpzgId) {
            queryParam.push({name: "zrcpzgId", value: zrcpzgId});
        }

        var zrrId = $.trim(mini.get("zrr").getValue());
        if (zrrId) {
            queryParam.push({name: "zrrId", value: zrrId});
        }

        var applyUserId = $.trim(mini.get("applyUser").getValue());
        if (applyUserId) {
            queryParam.push({name: "applyUserId", value: applyUserId});
        }
        var searchSmallJiXing = $.trim(mini.get("searchSmallJiXing").getValue());
        if (searchSmallJiXing) {
            queryParam.push({name: "smallJiXing", value: searchSmallJiXing});
        }
        queryParam.push({name: "jiean", value: "yes"});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = zlListGrid.getPageIndex();
        data.pageSize = zlListGrid.getPageSize();
        data.sortField = zlListGrid.getSortField();
        data.sortOrder = zlListGrid.getSortOrder();
        //查询
        zlListGrid.load(data);
    }

    function onRowDblClick() {
        selectZlgjOK();
    }

    function selectZlgjOK() {
        var selectRow = zlListGrid.getSelected();
            mini.get("togetherNum").setValue(selectRow.wtId);
            mini.get("togetherNum").setText(selectRow.zlgjNumber);
            selectZlgjHide();
    }

    function selectZlgjHide() {
        selectZlWindow.hide();
        mini.get("searchWtms").setValue("");
        mini.get("searchlcNumber").setValue("");
        mini.get("zrcpzg").setValue("");
        mini.get("zrcpzg").setText("");
        mini.get("zrr").setValue("");
        mini.get("zrr").setText("");
        mini.get("applyUser").setText("");
        mini.get("applyUser").setText("");
        mini.get("searchSmallJiXing").setValue("");
    }

    function onSelectZlgjCloseClick(e) {
        mini.get("togetherNum").setValue('');
        mini.get("togetherNum").setText('');
    }

    function clearNum(e) {
        var ifba = $.trim(mini.get("ifba").getValue());
        if (ifba == 'no') {
            mini.get("togetherNum").setValue('');
            mini.get("togetherNum").setText('');
        }
    }

    function clearRes(e) {
        var row=grid_zlgj_riskUser.getSelected();
        grid_zlgj_riskUser.updateRow(row,{resId:'',resName:''});
    }

    function getDetail(e) {
        var record = e.record;
        var zlgjNumber = record.zlgjNumber;
        var wtId = record.wtId;
        var s = '';
        s += '&nbsp;<span  title="明细" style="color:#409EFF" onclick="zlgjDetail(\'' + wtId + '\',\'' + record.status + '\')">'+zlgjNumber+'</span>';
        return s;
    }

    function zlgjDetail(wtId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/editPage.do?action=" + action + "&wtId=" + wtId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zlListGrid) {
                    zlListGrid.reload()
                }
            }
        }, 1000);
    }

    function selectLbj(inputScene) {
        selectLbjWindow.show();
        searchLbj();
    }

    //清空查询条件
    function clearForm() {
        mini.get("selectcomponentModel").setValue("");
        mini.get("selectmaterialCode").setValue("");
        mini.get("selectcomponentModel").setValue("");
        mini.get("selecttestNo").setValue("");
        searchLbj();

    }

    //查询标准
    function searchLbj() {
        var queryParam = [];
        //其他筛选条件
        var selectcomponentModel = $.trim(mini.get("selectcomponentModel").getValue());
        if (selectcomponentModel) {
            queryParam.push({name: "componentModel", value: selectcomponentModel});
        }
        var selectmaterialCode = $.trim(mini.get("selectmaterialCode").getValue());
        if (selectmaterialCode) {
            queryParam.push({name: "materialCode", value: selectmaterialCode});
        }
        var selectcomponentName = $.trim(mini.get("selectcomponentName").getValue());
        if (selectcomponentName) {
            queryParam.push({name: "componentName", value: selectcomponentName});
        }
        var selecttestNo = $.trim(mini.get("selecttestNo").getValue());
        if (selecttestNo) {
            queryParam.push({name: "testNo", value: selecttestNo});
        }

        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = lbjListGrid.getPageIndex();
        data.pageSize = lbjListGrid.getPageSize();
        data.sortField = lbjListGrid.getSortField();
        data.sortOrder = lbjListGrid.getSortOrder();
        //查询
        lbjListGrid.load(data);
    }

    function onRowDblLbjClick() {
        selectLbjOK();
    }

    function selectLbjOK() {
        var selectRow = lbjListGrid.getSelected();
        mini.get("lbjgys").setValue(selectRow.supplierName);
        mini.get("componentCategory").setValue(selectRow.componentCategory);
        mini.get("componentName").setValue(selectRow.componentName);
        mini.get("componentModel").setText(selectRow.componentModel);
        mini.get("componentModel").setValue(selectRow.id);
        mini.get("materialCode").setValue(selectRow.materialCode);
        mini.get("machineModel").setValue(selectRow.machineModel);
        mini.get("laboratory").setValue(selectRow.laboratory);
        mini.get("testRounds").setValue(selectRow.testRounds);
        mini.get("testType").setValue(selectRow.testType);
        mini.get("completeTestMonth").setValue(selectRow.completeTestMonth);
        mini.get("testLeaderId").setValue(selectRow.testLeaderId);
        mini.get("testLeaderId").setText(selectRow.testLeader);
        mini.get("sampleType").setValue(selectRow.sampleType);
        mini.get("nonconformingDescription").setValue(selectRow.nonconformingDescription);
        mini.get("improvementSuggestions").setValue(selectRow.improvementSuggestions);
        mini.get("testConclusion").setValue(selectRow.testConclusion);
        selectLbjHide();
    }

    function onSelectLbjCloseClick(e) {
        mini.get("lbjgys").setValue("");
        mini.get("componentName").setValue("");
        mini.get("componentCategory").setValue("");
        mini.get("componentModel").setValue("");
        mini.get("componentModel").setText("");
        mini.get("materialCode").setValue("");
        mini.get("machineModel").setValue("");
        mini.get("laboratory").setValue("");
        mini.get("testType").setValue("");
        mini.get("completeTestMonth").setValue("");
        mini.get("testLeaderId").setValue("");
        mini.get("testLeaderId").setText("");
        mini.get("sampleType").setValue("");
        mini.get("nonconformingDescription").setValue("");
        mini.get("testRounds").setValue("");
        mini.get("improvementSuggestions").setValue("");
        mini.get("testConclusion").setValue("");
    }

    function selectLbjHide() {
        selectLbjWindow.hide();
        mini.get("selectcomponentModel").setValue("");
        mini.get("selectmaterialCode").setValue("");
        mini.get("selectcomponentModel").setValue("");
        mini.get("selecttestNo").setValue("");
    }
</script>
</body>
</html>
