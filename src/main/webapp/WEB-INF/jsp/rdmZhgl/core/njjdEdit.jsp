<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

<head>
    <title>农机鉴定申请书</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/njjdEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/njjdList.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/userManagement.js?version=${static_res_version}"
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

        .conceal {
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

        .class-tr {
            width: 100%;
        }

        .body {
            display: flex;
            align-items: center;
            justify-content: space-around;
            flex-wrap: nowrap;
        }

        .td-radio .radio {
            width: 20px;
            height: 20px;
        }


    </style>

</head>

<body class="body">
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveSubsidy" class="mini-button" style="display: none" onclick="saveSubsidy()">保存信息</a>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formJsmm" method="post">
            <input id="njjdId" name="njjdId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <div style="width: 100% ; height: 30px ; font-size:20px; font-weight: bold; text-align: center">
                农机推广鉴定申请表
            </div>

            <%--申请信息--%>
            <fieldset id="fdBaseInfo">

                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="checkboxBaseInfo"
                               onclick="toggleFieldSet(this, 'fdBaseInfo')" hideFocus/>
                        申请信息
                    </label>
                </legend>

                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0" width="100%">
                        <tr>
                            <td style="text-align: center" colspan="2">产品型号：<span style="color: #ff0000">*</span></td>
                            <td style="text-align: center" style="width: 80%;min-width:170px" colspan="2">
                                <input id="productType" name="productType" class="mini-textbox" style="width:98%;"/>
                            </td>

                            <td style="text-align: center" colspan="2">主销区域：<span style="color: #ff0000">*</span></td>
                            <td style="width: 80%;min-width:170px" colspan="2">
                                <input id="salesArea" name="salesArea" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>


                        <tr class="table-tr">
                            <td colspan="2" style="text-align: center">该型号产品生产量(台)：<span style="color: #ff0000">*</span>
                            </td>
                            <td colspan="2" style="width: 40%;min-width:170px">
                                <input id="njcl" name="njcl" class="mini-textbox" style="width:98%;"/>
                            </td>

                            <td colspan="2" style="text-align: center">该型号产品销售量(台)：<span style="color: #ff0000">*</span>
                            </td>
                            <td colspan="2" style="width: 40%;min-width:170px">
                                <input id="njxs" name="njxs" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr class="table-tr">
                            <td colspan="2" style="text-align: center">所属项目名称：<span style="color: #ff0000">*</span>
                            </td>
                            <td colspan="2" style="width: 40%;min-width:170px">
                                <input id="projectName" name="projectName" class="mini-textbox" style="width:98%;"/>
                            </td>

                            <td colspan="2" style="text-align: center">所属项目编号：<span style="color: #ff0000">*</span>
                            </td>
                            <td colspan="2" style="width: 40%;min-width:170px">
                                <input id="projectNumber" name="projectNumber" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr class="table-tr">
                            <td colspan="2" style="text-align: center">是否补贴：
                            </td>
                            <td colspan="2" style="width: 40%;min-width:170px">
                                <input id="haveSubsidy" name="haveSubsidy" class="mini-combobox" style="width:98%;"
                                       textField="value" valueField="key" emptyText="请选择..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                       data="[ {'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                                />
                            </td>

                            <td colspan="2" style="text-align: center">补贴截止日期：
                            </td>
                            <td colspan="2" style="width: 40%;min-width:170px">
                                <input id="subsidyEndDate" name="subsidyEndDate" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false" style="width:98%;"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>

            <%--产品规格信息--%>
            <fieldset id="fdPlanInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxPlanInfo" onclick="toggleFieldSet(this, 'fdPlanInfo')"
                               hideFocus/>
                        产品规格表
                    </label>
                </legend>

                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <a id="ifDisplay" class="mini-button" style="margin-right: 5px;display: none" plain="true" onclick="exportCpgg()">导出</a>
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center" colspan="4">项目</td>
                            <td style="text-align: center" colspan="1">单位</td>
                            <td style="text-align: center" colspan="3">设计值</td>
                        </tr>
                        <tr>
                            <td style="text-align: center" rowspan="24" colspan="1">整机参数</td>
                            <td style="text-align: center" colspan="3">型号</td>
                            <td style="text-align: center" colspan="1">/</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztXh" name="ztXh" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>

                            <td style="text-align: center" colspan="3">结构型式</td>
                            <td style="text-align: center" colspan="1">/</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztJgxs" name="ztJgxs" class="mini-radiobuttonlist" style="width:100%;"
                                       repeatItems="2" repeatLayout="table" repeatDirection="horizontal" value="0"
                                       textfield="jgxsName" valuefield="jgxsId"
                                       data="[ {'jgxsName' : '专用动力挖掘机','jgxsId' : '0'},{'jgxsName' : '拖拉机挖掘机组','jgxsId' : '1'}]"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">工作质量</td>
                            <td style="text-align: center" colspan="1">kg</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztGzzl" name="ztGzzl" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">铲斗容量</td>
                            <td style="text-align: center" colspan="1">m3</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztCdrl" name="ztCdrl" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="2">外形尺寸（长×宽×高）</td>
                            <td style="text-align: center" colspan="1">运输状态</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztWxcc" name="ztWxcc" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">轴距</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztZj" name="ztZj" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" rowspan="3" colspan="1">轮式</td>
                            <td style="text-align: center" colspan="2">轮距（前/后）</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztLsLj" name="ztLsLj" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="2">轮胎规格</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztLsLtgg" name="ztLsLtgg" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="2">轮胎气压</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztLsLtQy" name="ztLsLtQy" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" rowspan="4" colspan="1">履带式</td>
                            <td style="text-align: center" colspan="2">履带轨距</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztLdGj" name="ztLdGj" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="2">履带接地长度</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztLdCd" name="ztLdCd" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="2">履带板宽度</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztLdKd" name="ztLdKd" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="2">履带材质</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztLdcz" name="ztLdcz" class="mini-radiobuttonlist" style="width:100%;"
                                       repeatItems="3" repeatLayout="table" repeatDirection="horizontal" value="0"
                                       textfield="ztLdczName" valuefield="ztLdczId"
                                       data="[ {'ztLdczName' : '金属','ztLdczId' : '0'},{'ztLdczName' : '橡胶','ztLdczId' : '1'},{'ztLdczName' : '其他','ztLdczId' : '2'}]"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">后端回转半径</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztHdhzBj" name="ztHdhzBj" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">离地间隙</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztLdJx" name="ztLdJx" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">转台离地高度</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztZtldGd" name="ztZtldGd" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">转台总宽度</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztZtKd" name="ztZtKd" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">转台尾端长度</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztZtwdCd" name="ztZtwdCd" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">回转中心至驱动轮中心距离</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztZxj" name="ztZxj" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">平均接地比压</td>
                            <td style="text-align: center" colspan="1">kPa</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztJdby" name="ztJdby" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">档位数（前进/倒退）</td>
                            <td style="text-align: center" colspan="1">/</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztDws" name="ztDws" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" rowspan="2" colspan="1">理论速度</td>
                            <td style="text-align: center" colspan="2">前进</td>
                            <td style="text-align: center" colspan="1">km/h</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztSdQj" name="ztSdQj" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>


                        <tr>
                            <td style="text-align: center" colspan="2">倒退</td>
                            <td style="text-align: center" colspan="1">km/h</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztSdDt" name="ztSdDt" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">爬坡能力</td>
                            <td style="text-align: center" colspan="1">/</td>
                            <td style="text-align: center" colspan="3">
                                <input id="ztPp" name="ztPp" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" rowspan="6" colspan="1">发动机</td>
                            <td style="text-align: center" colspan="3">型号</td>
                            <td style="text-align: center" colspan="1">/</td>
                            <td style="text-align: center" colspan="3">
                                <input id="fdjXh" name="fdjXh" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">型式</td>
                            <td style="text-align: center" colspan="1">/</td>
                            <td style="text-align: center" colspan="3">
                                <input id="fdjXs" name="fdjXs" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">生产厂</td>
                            <td style="text-align: center" colspan="1">/</td>
                            <td style="text-align: center" colspan="3">
                                <input id="fdjFactory" name="fdjFactory" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">标定功率</td>
                            <td style="text-align: center" colspan="1">kW</td>
                            <td style="text-align: center" colspan="3">
                                <input id="fdjBdGl" name="fdjBdGl" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">额定净功率</td>
                            <td style="text-align: center" colspan="1">kW</td>
                            <td style="text-align: center" colspan="3">
                                <input id="fdjEdGl" name="fdjEdGl" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">标定转速</td>
                            <td style="text-align: center" colspan="1">r/min</td>
                            <td style="text-align: center" colspan="3">
                                <input id="fdjBdZs" name="fdjBdZs" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" rowspan="3" colspan="1">驾驶室</td>
                            <td style="text-align: center" colspan="3">型号</td>
                            <td style="text-align: center" colspan="1">/</td>
                            <td style="text-align: center" colspan="3">
                                <input id="jssXh" name="jssXh" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">型式</td>
                            <td style="text-align: center" colspan="1">/</td>
                            <td style="text-align: center" colspan="3">
                                <input id="jssXs" name="jssXs" class="mini-radiobuttonlist" style="width:100%;"
                                       repeatItems="3" repeatLayout="table" repeatDirection="horizontal" value="0"
                                       textfield="jssXsName" valuefield="jssXsId"
                                       data="[ {'jssXsName' : '简易驾驶室','jssXsId' : '0'},{'jssXsName' : '封闭驾驶室','jssXsId' : '1'},{'jssXsName' : '安全框架','jssXsId' : '2'}]"/>

                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">生产厂</td>
                            <td style="text-align: center" colspan="1">/</td>
                            <td style="text-align: center" colspan="3">
                                <input id="jssFactory" name="jssFactory" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" rowspan="7" colspan="1">作业参数</td>
                            <td style="text-align: center" colspan="2" rowspan="2">最大挖掘力</td>
                            <td style="text-align: center" colspan="1">铲斗</td>
                            <td style="text-align: center" colspan="1">kN</td>
                            <td style="text-align: center" colspan="3">
                                <input id="zyccZdWjlCd" name="zyccZdWjlCd" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="1">斗杆</td>
                            <td style="text-align: center" colspan="1">kN</td>
                            <td style="text-align: center" colspan="3">
                                <input id="zycsZdwjlDg" name="zycsZdwjlDg" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">最大挖掘半径</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="zycsZdwjBj" name="zycsZdwjBj" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">最大挖掘深度</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="zycsZdwjSd" name="zycsZdwjSd" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">最大垂直挖掘深度</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="zycsZdCzwjSd" name="zycsZdCzwjSd" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">最大挖掘高度</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="zycsZdwjGd" name="zycsZdwjGd" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">最大卸载高度</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="zycsZdxzGd" name="zycsZdxzGd" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" rowspan="2" colspan="1">工作装置</td>
                            <td style="text-align: center" colspan="3">动臂长度</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="gzzzDbCd" name="gzzzDbCd" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">斗杆长度</td>
                            <td style="text-align: center" colspan="1">mm</td>
                            <td style="text-align: center" colspan="3">
                                <input id="gzzzDgCd" name="gzzzDgCd" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>


                        <tr>
                            <td style="text-align: center" rowspan="3" colspan="1">液压系统</td>
                            <td style="text-align: center" colspan="3">主液压泵型号</td>
                            <td style="text-align: center" colspan="1">/</td>
                            <td style="text-align: center" colspan="3">
                                <input id="yybXh" name="yybXh" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">主液压泵流量</td>
                            <td style="text-align: center" colspan="1">L/min</td>
                            <td style="text-align: center" colspan="3">
                                <input id="yybLl" name="yybLl" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center" colspan="3">设定工作压力</td>
                            <td style="text-align: center" colspan="1">MPa</td>
                            <td style="text-align: center" colspan="3">
                                <input id="gzyl" name="gzyl" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>

                        <%--<tr>
                            <td style="text-align: center" rowspan="1" colspan="1">选装配置</td>
                            <td style="text-align: center" colspan="3">选装配置</td>
                            <td style="text-align: center" colspan="1">MPa</td>
                            <td style="text-align: center" colspan="3">
                                <input id="xpzz" name="xpzz" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>--%>
                        <tr>
                            <td style="text-align: inherit;height: 300px">选装配置</td>
                            <td  colspan="5">
                                <div class="mini-toolbar" id="xzpzButtons" >
                                    <a class="mini-button" id="addNjjdxzpz"  plain="true" onclick="addNjjd_xzpzInfoRow">添加选装配置</a>
                                    <a class="mini-button" id="removeNjjdxzpz" plain="true" onclick="removeNjjd_xzpzInfoRow">删除选装配置</a>
                                </div>
                                <div id="grid_njjd_xzpzInfo" class="mini-datagrid" style="width: 98%; height: 230px" allowResize="false"
                                     idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                                     multiSelect="true" showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
                                    <div property="columns">
                                        <div type="checkcolumn" width="10"></div>
                                        <div field="pzmc"  align="center" headerAlign="center" width="50" renderer="render">选装配置名称<span style="color: #ff0000">*</span>
                                            <input property="editor" class="mini-textarea"/>
                                        </div>
                                        <div field="pzdw"  align="center" headerAlign="center" width="50" renderer="render">选装配置单位
                                            <input property="editor" class="mini-textarea" />
                                        </div>
                                        <div field="pzsjz"  align="center" headerAlign="center" width="50" renderer="render">选装配置设计值<span style="color: #ff0000">*</span>
                                            <input property="editor" class="mini-textarea"/>
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>


            </fieldset>
            <br>

            <%--技术材料附件--%>
            <div id="show" class="conceal">
            <fieldset id="ggPlanInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxMessageInfo" onclick="toggleFieldSet(this, 'ggPlanInfo')"
                               hideFocus/>
                        技术材料附件
                    </label>
                </legend>

                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">

                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <%--<caption style="font-weight: bold">--%>
                        <%--技术材料附件--%>
                        <%--</caption>--%>

                        <tr>
                            <td style="width: 14%;height: 10px">附件列表：</td>
                            <td colspan="3">
                                <div style="margin-top: 10px;margin-bottom: 2px">
                                    <a id="addFile" class="mini-button" onclick="addNjjdFile()">添加附件</a>
                                    <span style="color: red">注：添加附件前，请先进行草稿的保存</span>
                                </div>
                                <div id="fileListGrid" class="mini-datagrid"
                                     allowResize="false"
                                     idField="id"
                                     url="${ctxPath}/zhgl/core/njjd/getNjjdFileList.do?njjdId=${njjdId}&njfjDl=js"
                                     autoload="true"
                                     multiSelect="true" showPager="false" showColumnsMenu="false"
                                     allowAlternating="true">
                                    <div property="columns">
                                        <div type="indexcolumn" align="center" width="20">序号</div>
                                        <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                        <div field="njfjXlName" width="80" headerAlign="center" align="center">附件类别
                                        </div>
                                        <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                        <div field="action" width="100" headerAlign='center' align="center"
                                             renderer="operationJsRenderer">操作
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>

                </div>
            </fieldset>
            <br>
            </div>

            <%--用户信息模块--%>
            <div  id="user" class="conceal">
            <fieldset id="yhPlanInfo" class="hideFieldset">

                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkUserInfo" onclick="toggleFieldSet(this, 'yhPlanInfo')"
                               hideFocus/>
                        用户信息填写
                    </label>
                </legend>

                <div class="fieldset-body" style="margin: 10px 10px 10px 10px;width: 100% ;height: auto">

                    <div class="mini-toolbar">
                        <div class="searchBox">
                            <form id="searchForm" class="search-form" style="margin-bottom: 25px">

                                <li id="operateStandard" style="margin-left: 10px">
                                    <a id="operateAdd" class="mini-button" style="margin-right: 5px" plain="true"
                                       onclick="openStandardEditWindow('','','add','${njjdId}')">新增</a>
                                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                                       onclick="downImportTemplate()">模板下载</a>
                                </li>


                            </form>
                        </div>
                    </div>

                    <div id="njjdListGrid" class="mini-datagrid"
                         allowResize="false" url="${ctxPath}/zhgl/core/njjd/getUserList.do?njjdId=${njjdId}"
                         idField="njjdId"
                         allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true"
                         allowCellSelect="true"
                         editNextOnEnterKey="true" editNextRowCell="true">
                        <div property="columns">
                            <div type="checkcolumn" width="20"></div>
                            <div id="isOperation" name="action" cellCls="actionIcons" width="100" headerAlign="center"
                                 align="center"
                                 renderer="onAction" cellStyle="padding:0;">操作
                            </div>
                            <div field="userName" name="userName" sortField="userName" width="80"
                                 headerAlign="center"
                                 align="center" allowSort="true">用户姓名 <input readonly property="editor"
                                                                             class="mini-textbox"/>
                            </div>

                            <div field="address" width="180" headerAlign="center" align="center" allowSort="false">
                                用户详细地址<input readonly property="editor" class="mini-textbox"/>
                            </div>
                            <div field="phone" width="180" headerAlign="center" align="center" allowSort="false">
                                联系电话<input readonly property="editor" class="mini-textbox"/>
                            </div>
                            <div field="buyTime" width="70" headerAlign="center" align="center" allowSort="false">
                                购买日期<input readonly property="editor" id='buyTime' name="buyTime"
                                           class="mini-datepicker" format="yyyy-MM-dd"/>
                            </div>

                            <div field="fileName" width="70" headerAlign="center" align="center" allowSort="false"
                                 renderer="onActionRenderer">
                                附件
                            </div>

                            <div field="fileId" width="0" headerAlign="center" align="center" allowSort="false">
                                <input id="fileId" name="fileId" class="mini-hidden"/>
                            </div>
                        </div>
                    </div>


                </div>

            </fieldset>
            <br>
            </div>

            <%--证书附件维护--%>
            <fieldset id="zsPlanInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkbookUserInfo" onclick="toggleFieldSet(this, 'zsPlanInfo')"
                               hideFocus/>
                        证书附件维护
                    </label>
                </legend>


                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">

                        <tr>
                            <td style="width: 14%;height: 10px">附件列表：</td>
                            <td colspan="3">
                                <div style="margin-top: 10px;margin-bottom: 2px;">
                                    <a id="addFiles" class="mini-button" onclick="addNjjdFiles()">添加附件</a>
                                    <span style="color: red">注：添加附件前，请先进行草稿的保存</span>
                                </div>
                                <div id="fileListGrids" class="mini-datagrid"
                                     allowResize="false"
                                     idField="id"
                                     url="${ctxPath}/zhgl/core/njjd/getNjjdFileList.do?njjdId=${njjdId}&njfjDl=zs"
                                     autoload="true"
                                     multiSelect="true" showPager="false" showColumnsMenu="false"
                                     allowAlternating="true">
                                    <div property="columns">
                                        <div type="indexcolumn" align="center" width="20">序号</div>
                                        <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                        <div field="njfjXlName" width="80" headerAlign="center" align="center">附件类别
                                        </div>
                                        <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                        <div field="action" width="100" headerAlign='center' align="center"
                                             renderer="operationRenderer">操作
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>

                </div>

            </fieldset>
            <br>

        </form>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/zhgl/core/njjd/exportNjjdcpgg.do?njjdId=${njjdId}" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">

    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var grid = mini.get("njjdListGrid");
    var fileListGrids = mini.get("fileListGrids");
    var fileListGrid = mini.get("fileListGrid");
    var formJsmm = new mini.Form("#formJsmm");
    var njjdId = "${njjdId}";
    var njfjDl = "${njfjDl}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var seeJsfj=${seeJsfj};
    var isJSZX=${isJSZX};
    var isUser=${isUser};
    var isFGLD=${isFGLD};
    var isNjzy=${isNjzy};

    var currentUserId = "${currentUserId}";
    var grid_njjd_xzpzInfo = mini.get("#grid_njjd_xzpzInfo");

    /**
     * 用户材料信息_导入模板下载
     */
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/zhgl/core/njjd/importTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }

    /**
     * 用户材料信息_附件_行功能按钮
     */
    function onActionRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        //预览按钮添加
        cellHtml = returnJsmmPreviewSpan(record.fileName, record.fileId, njjdId, coverContent);
        //增加下载按钮 某些节点/用户提供下载功能 （未做）
        if (action == 'edit' || (action == 'task' && isScUser == 'yes')|| (action == 'task' && isShenHe == 'yes')|| (action == 'task' && isJsWeiHu == 'yes')) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadJsjlFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + njjdId + '\')">下载</span>';
        }
        return cellHtml;
    }

    /**
     * 用户材料信息_操作_行功能按钮
     */
    function onAction(e) {
        var record = e.record;
        var cellHtml = '';
        //增加修改按钮 所属节点/编制阶段提供修改功能
        if (action == 'edit' || (action == 'task' && isScUser == 'yes')) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="修改" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="openStandardEditWindow(\'' + record.id + '\',\'\',\'update\',\'' + njjdId + '\')">修改</span>';
        }else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="修改" style="color: silver" >修改</span>';
        }
        //增加删除按钮 所属节点/编制阶段提供删除功能
        if (action == 'edit' || (action == 'task' && isScUser == 'yes')) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="removeUser(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color: silver" >删除</span>';
        }
        return cellHtml;
    }

    /**
     * 证书附件维护_行功能按钮
     * @param record
     */
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';

        //增加预览按钮
        cellHtml = returnJsmmPreviewSpan(record.fileName, record.Id, njjdId, coverContent);
        //增加下载按钮 所属节点/编制阶段提供删除功能
        if (isFGLD ||  currentUserId == record.CREATE_BY_) {
            //分管领导跟创建人直接下载
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadJsjlFile(\'' + record.fileName + '\',\'' + record.Id + '\',\'' + njjdId + '\')">下载</span>';

        }else {
            //证书下载需要走申请
            if (record.njfjXlName=="农机推广鉴定证书") {
                cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="downLoadZsFile(\'' + record.fileName + '\',\'' + record.Id + '\',\'' + njjdId + '\')">下载</span>';
            }
        }
        //增加删除按钮 所属节点/编制阶段提供删除功能
        if (action == 'edit' || (action == 'task' && isJsWeiHu == 'yes')) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="removeBook(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }

        return cellHtml;
    }

    /**
     * 技术材料附件维护_行功能按钮
     * @param record
     */
    function operationJsRenderer(e) {
        var record = e.record;
        var cellHtml = '';


        if (seeJsfj || record.CREATE_BY_ == currentUserId ) {
            //增加预览按钮
            cellHtml = returnJsmmPreviewSpan(record.fileName, record.Id, njjdId, coverContent);
            //增加下载按钮 所属节点/编制阶段提供删除功能 （未做）
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadJsjlFile(\'' + record.fileName + '\',\'' + record.Id + '\',\'' + njjdId + '\')">下载</span>';
        }else {
            //增加预览按钮
            cellHtml =  '&nbsp;&nbsp;&nbsp;<span title="预览" style="color: silver" >预览</span>';
            //增加下载按钮 所属节点/编制阶段提供删除功能 （未做）
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color: silver" >下载</span>';
        }
        //增加删除按钮 所属节点/编制阶段提供删除功能 （未做）
        if (action == 'edit' || (action == 'task' && isBianzhi == 'yes')) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="removeBook(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
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
            rows = njjdListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.jsmmId);
                    instIds.push(r.instId);
                }

                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/njjd/deletePublic.do?njjdId=" + njjdId,
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(','), instIds: instIds.join(','), fileId: record.fileId, fileName: record.fileName,id:record.id},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            grid.load();
                        }
                    }
                });
            }
        });
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
            rows = njjdListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.jsmmId);
                    instIds.push(r.instId);
                }

                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/njjd/deleteBookMeg.do?njjdId=" + njjdId,
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(','), instIds: instIds.join(','), id: record.Id, fileName: record.fileName},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            fileListGrids.load();
                            fileListGrid.load();

                        }
                    }
                });
            }
        });

    }

    /**
     * 文件上传
     */
    function addUserFile() {
        var njjdId = mini.get("njjdId").getValue();

        // if(!njjdId) {
        //     mini.alert('请先点击‘保存草稿’进行表单的保存！');
        //     return;
        // }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/zhgl/core/njjd/fileUploadWindow.do?njjdId=" + njjdId + "&njfjDl=yh",
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                if (grid) {
                    grid.load();
                }
            }
        });
    }

    /**
     * 用户材料信息_保存（弃）
     */
    function saveData() {

        if (!njjdId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }

        var data = grid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/zhgl/core/njjd/saveUserList.do?njjdId=${njjdId}",
                    data: json,
                    type: "post",
                    contentType: 'application/json',
                    async: false,
                    success: function (result) {
                        if (result && result.message) {
                            message = result.message;
                        }
                    }
                });
            }
        }
        mini.showMessageBox({
            title: "提示信息",
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok" && needReload) {
                    grid.reload();
                }
            }
        });
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

<%--<redxun:gridScript gridId="njjdListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>--%>


</body>
</html>
