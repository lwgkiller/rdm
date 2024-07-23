
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>国四环保信息公开新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rap/gsclhbEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }
        .table-detail > tbody > tr > td {
            border: 1px solid #eee;
            background-color: #fff;
            white-space: normal;
            word-break: break-all;
            color: rgb(85, 85, 85);
            font-weight: normal;
            padding: 4px;
            height: 40px;
            min-height: 40px;
            box-sizing: border-box;
            font-size: 15px;
        }
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
            display:none;
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
<div id="changeToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveChange" class="mini-button" onclick="saveChange()">保存变更</a>
        <a class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formCx" method="post">
            <input id="cxId" name="cxId" class="mini-hidden"/>
            <input id="oldCxId" name="oldCxId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="type" name="type" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 15%;text-align: left">机械系族名称(动力工程师提交后自动生成)</td>
                    <td style="width: 15%">
                        <input id="jxxzmc" name="jxxzmc" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="width: 17%">流程编码(流程结束自动生成)</td>
                    <td style="width: 17%">
                        <input id="num" name="num" class="mini-textbox" readonly style="width:98%"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 15% ">电气工程师</td>
                    <td style="width: 15%;min-width:170px ">
                        <input id="dqId" name="dqId" textname="dqName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="width: 15% ">动力工程师</td>
                    <td style="width: 15%;min-width:170px ">
                        <input id="dlId" name="dlId" textname="dlName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 17%">产品主管</td>
                    <td style="width: 17%">
                        <input id="apply" name="applyId" textname="applyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="width: 15% ">申请部门</td>
                    <td style="width: 15%;min-width:170px ">
                        <input id="dept" name="deptId" textname="deptName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
            </table>
            <fieldset id="cpzg" >
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxCpInfo" onclick="bztoggleFieldSet(this, 'cpzg')" hideFocus/>
                        产品主管填写
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table style="table-layout: fixed;color: #555!important;" class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 33%">项目名称(*)号必填</td>
                            <td style="width: 33%">内容填写说明</td>
                            <td style="width: 33%">内容填写</td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">内部编号<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">产品销售型号+三位流水号</td>
                            <td style="width: 15%">
                                <input id="nbbh" name="nbbh" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">整机设计型号<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">只填写当前配置对应的整机设计型号</td>
                            <td style="width: 15%">
                                <input id="zjsjxh" style="width:98%;" class="mini-buttonedit" showClose="true"
                                       oncloseclick="onRelModelCloseClick()"
                                       name="zjsjxhId" textname="zjsjxh" allowInput="false"
                                       onbuttonclick="selectModelClick()"/>
                            </td>
                        </tr>

                        <tr>
                            <td style="width: 15%;text-align: left">整机物料号<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">只填写当前配置对应的整机物料号</td>
                            <td style="width: 15%">
                                <input id="zjwlh" name="zjwlh" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">机械名称<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">与产品标识一致,如液压挖掘机</td>
                            <td style="width: 15%">
                                <input id="jxmc" name="jxmc" readonly class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left;font-weight: bolder">机械型号<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">产品销售型号，与产品标识一致</td>
                            <td style="width: 15%">
                                <input id="cpjxxh" name="cpjxxh" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left;font-weight: bolder">质量名称<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">填写适用的质量名称,如工作质量</td>
                            <td style="width: 15%">
                                <input id="zlname" name="zlname" readonly class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left;font-weight: bolder">质量（KG）<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">与型式试验报告一致</td>
                            <td style="width: 15%">
                                <input id="zl" name="zl" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left;font-weight: bolder">额定功率(kW)<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">发动机额定功率的具体数值</td>
                            <td style="width: 15%">
                                <input id="cyjedgl" name="cyjedgl"  class="mini-textbox" style="  width:98%;"/>
                            </td>
                        <tr>
                        <tr>
                            <td style="width: 15%;text-align: left;font-weight: bolder">车载终端数据是否上传VECC官网<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">选择是或否</td>
                            <td style="width: 15%">
                                <input id="upvecc" name="upvecc" class="mini-combobox" style="width:98%;"
                                       textField="key" valueField="value" nullItemText="请选择" emptyText="请选择..."
                                       data="[{key:'是',value:'是'},{key:'否',value:'否'}]"
                                       allowInput="false" showNullItem="false" />
                            </td>
                        <tr>
                        <tr>
                            <td style="width: 15%;text-align: left;font-weight: bolder">诊断口是否下线访问检查<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">选择是或否</td>
                            <td style="width: 15%">
                                <input id="xxfwjc" name="xxfwjc" class="mini-combobox" style="width:98%;"
                                       textField="key" valueField="value" nullItemText="请选择" emptyText="请选择..."
                                       data="[{key:'是',value:'是'},{key:'否',value:'否'}]"
                                       allowInput="false" showNullItem="false" />
                            </td>
                        <tr>
                        <tr>
                            <td style="width: 15%;text-align: left">外形尺寸<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">长*宽*高，单位为mm</td>
                            <td style="width: 15%">
                                <input id="wxcc" name="wxcc" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">机械的识别方法和位置<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">固定格式,根据产品实际安装位置填写</td>
                            <td style="width: 15%">
                                <input id="jxsbff" name="jxsbff" class="mini-combobox" style="width:98%;"
                                       textField="text" valueField="textValue" nullItemText="请选择" emptyText="请选择..."
                                       url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=bs" showNullItem="true"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">机械环保代码在机体上的打刻位置<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">固定格式</td>
                            <td style="width: 15%">
                                <input id="hbdmwz" name="hbdmwz" readonly class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">环保信息标签位置<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">固定格式,根据产品实际安装位置填写</td>
                            <td style="width: 15%">
                                <input id="hbbqwz" name="hbbqwz" class="mini-combobox" style="width:98%;"
                                       textField="text" valueField="textValue" nullItemText="请选择..."
                                       url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=bq" showNullItem="true"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">最大设计总质量<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">采取不同配置下的最大质量</td>
                            <td style="width: 15%">
                                <input id="zzl" name="zzl" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">最大设计车速（km/h）</td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">此项为选填</td>
                            <td style="width: 15%">
                                <input id="zdcs" name="zdcs" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">额定工作参数及内容</td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">选填，1-2个主要参数[增加填写说明内容]（类工程机械的主参数）根据机械具体适用的参数填写（如牵引力、装载质量、起重质量等）。</td>
                            <td style="width: 15%">
                                <input id="edgzcs" name="edgzcs" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">是否有多种运行模式<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                            <td style="width: 15%">
                                <input id="dzyx" name="dzyx" class="mini-combobox" style="width:98%;"
                                       textField="key" valueField="value" nullItemText="请选择" emptyText="请选择..."
                                       data="[{key:'是',value:'是'},{key:'否',value:'否'}]"
                                       allowInput="false" showNullItem="false" />
                            </td>
                        <tr>
                        <%--<tr>--%>
                            <%--<td style="width: 15%;text-align: left">是否改装<span style="color: #ff0000">*</span></td>--%>
                            <%--<td style="width: 50%;font-size:7pt;color:darkgrey"></td>--%>
                            <%--<td style="width: 15%">--%>
                                <%--<input id="sfgz" name="sfgz" class="mini-combobox" style="width:98%;"--%>
                                       <%--textField="key" valueField="value" nullItemText="请选择" emptyText="请选择..."--%>
                                       <%--data="[{key:'是',value:'是'},{key:'否',value:'否'}]"--%>
                                       <%--allowInput="false" showNullItem="false" />--%>
                            <%--</td>--%>
                        <%--<tr>--%>

                        <tr>
                            <td style="width: 15%;text-align: left">机械分类代码<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">C表示履带式液压挖掘机，W表示轮胎式液压挖掘机，S表示特种液压挖掘机，Q表示其他种类</td>
                            <td style="width: 15%">
                                <input id="fldm" name="fldm" class="mini-combobox" style="width:98%;"
                                       textField="text" valueField="textValue" nullItemText="请选择" emptyText="请选择..."
                                       url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=fl" showNullItem="true"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">商标<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">固定格式</td>
                            <td style="width: 15%">
                                <input id="sb" name="sb" readonly class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">机械分类-大类<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">固定格式</td>
                            <td style="width: 15%">
                                <input id="jxdl" name="jxdl" readonly class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">机械分类-小类<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">固定格式</td>
                            <td style="width: 15%">
                                <input id="jxxl" name="jxxl" readonly class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">制造商名称<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">固定格式</td>
                            <td style="width: 15%">
                                <input id="zzsmc" name="zzsmc" readonly class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">生产地址<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">固定格式</td>
                            <td style="width: 15%">
                                <input id="scdz" name="scdz" readonly class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                            <td style="width: 7%;height: 300px ">产品主管附件上传<br>环保信息标签示意图<span style="color: #ff0000">*</span>
                                <br>外形照片（正面）<span style="color: #ff0000">*</span>
                                <br>外形照片（正侧面45°）<span style="color: #ff0000">*</span>
                                <br>机械环保代码在机体上的打刻示意图<span style="color: #ff0000">*</span>
                                <br>商标示意图<span style="color: #ff0000">*</span>
                                <br>多种运行模式的相关描述<span style="color: #ff0000">*</span>
                                <br>车载终端和精准定位系统防拆除技术措施说明<span style="color: #ff0000">(37kW以下机型，无需上传)</span></td>
                            <td colspan="2">
                                <div style="margin-bottom: 2px">
                                    <a id="addCpFile" class="mini-button" onclick="fileuploadCp()">添加附件</a>
                                    <a id="downImportGsClhbcz" class="mini-button" onclick="downImportGsClhbcz()">下载防拆除技术措施说明模板</a>
                                    <a id="downImportGsClhbwj" class="mini-button" onclick="downImportGsClhbwj()">下载挖机运行模式特点模板</a>
                                    <a id="downloadPic" class="mini-button" onclick="downloadPic()">下载商标示意图</a>
                                </div>
                                <div id="cpFileListGrid" class="mini-datagrid" style="width: 100%; height: 250px"
                                     allowResize="false"
                                     idField="id"  autoload="true"
                                     url="${ctxPath}/environment/core/Cx/getFileList.do?upNode=cp&cxId=${cxId}"
                                     multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                                    <div property="columns">
                                        <div type="indexcolumn" align="center" width="20">序号</div>
                                        <div field="fileName" width="70" headerAlign="center" align="center">附件名称</div>
                                        <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                        <div field="fileType" width="80" headerAlign="center" align="center">附件类型</div>
                                        <div field="action" width="100" headerAlign='center' align="center" renderer="operationRendererCp">操作
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <fieldset id="dqgcs" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox"  id="checkboxDqInfo" onclick="bztoggleFieldSet(this, 'dqgcs')" hideFocus/>
                        电气工程师填写
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table style="table-layout: fixed;color: #555!important;" class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 33%">项目名称(*)号必填</td>
                            <td style="width: 33%">内容填写说明</td>
                            <td style="width: 33%">内容填写</td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">排放控制诊断系统接口位置<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">若产品上安装，需填写，如：驾驶室座椅后方左扶手箱附近</td>
                            <td style="width: 15%">
                                <input id="pfkzwz" name="pfkzwz" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">排放控制诊断系统接口替代位置<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">若产品上安装，需填写，若产品上没有安装，填无</td>
                            <td style="width: 15%">
                                <input id="pfkztdwz" name="pfkztdwz" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">远程车载终端型号<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">若产品上安装，需填写</td>
                            <td style="width: 15%">
                                <input id="czzdxh" name="czzdxh" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">远程车载终端生产厂<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">若产品上安装，需填写</td>
                            <td style="width: 15%">
                                <input id="czzdscc" name="czzdscc" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">远程车载终端安装位置<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">若产品上安装，需填写</td>
                            <td style="width: 15%">
                                <input id="czzdwz" name="czzdwz" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 7%;height: 300px ">电气工程师附件上传<span style="color: #ff0000">*</span><br>远程车载终端安装位置示意图</td>
                            <td colspan="2">
                                <div style="margin-bottom: 2px">
                                    <a id="addDqFile" class="mini-button" onclick="fileuploadDq()">添加附件</a>
                                </div>
                                <div id="dqFileListGrid" class="mini-datagrid" style="width: 100%; height: 250px"
                                     allowResize="false"
                                     idField="id"  autoload="true"
                                     url="${ctxPath}/environment/core/Cx/getFileList.do?upNode=dq&cxId=${cxId}"
                                     multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                                    <div property="columns">
                                        <div type="indexcolumn" align="center" width="20">序号</div>
                                        <div field="fileName" width="70" headerAlign="center" align="center">附件名称</div>
                                        <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                        <div field="fileType" width="80" headerAlign="center" align="center">附件类型</div>
                                        <div field="action" width="100" headerAlign='center' align="center" renderer="operationRendererDq">操作
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <fieldset id="dlgcs" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox"  id="checkboxDlInfo" onclick="bztoggleFieldSet(this, 'dlgcs')" hideFocus/>
                        动力工程师填写
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table style="table-layout: fixed;color: #555!important;" class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 33%">项目名称(*)号必填</td>
                            <td style="width: 33%">内容填写说明</td>
                            <td style="width: 33%">内容填写</td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">燃料规格<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">GB19147车用柴油(国六)</td>
                            <td style="width: 15%">
                                <input id="rlgg" name="rlgg" readonly class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">冷却风扇驱动方式<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                            <td style="width: 15%">
                                <input id="fsqd" name="fsqd" class="mini-combobox" style="width:98%;"
                                       textField="text" valueField="textValue" nullItemText="请选择" emptyText="请选择..."
                                       url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=lq" showNullItem="true"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">燃料类型<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">柴油</td>
                            <td style="width: 15%">
                                <input id="rllx" name="rllx" readonly class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">燃料箱数量<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                            <td style="width: 15%">
                                <input id="rlxsl" name="rlxsl" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">燃油箱各油箱容积（L）<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">燃油箱有效容积(最大容积的95%)</td>
                            <td style="width: 15%">
                                <input id="rlxrj" name="rlxrj" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">燃油箱总容积（L）<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">各燃油箱有效容积之和</td>
                            <td style="width: 15%">
                                <input id="ryxzrj" name="ryxzrj" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">燃油箱材料<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                            <td style="width: 15%">
                                <input id="ryxcl" name="ryxcl" class="mini-combobox" style="width:98%;"
                                       textField="text" valueField="textValue" nullItemText="请选择" emptyText="请选择..."
                                       url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=cl" showNullItem="true"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">选择其他时填写相应内容</td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">选填，燃油箱材料选其他时填写</td>
                            <td style="width: 15%">
                                <input id="ryxqt" name="ryxqt" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">燃油箱安装位置<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">燃油箱在整机上的安装位置</td>
                            <td style="width: 15%">
                                <input id="ryxwz" name="ryxwz" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <%--<tr>--%>
                            <%--<td style="width: 15%;text-align: left">反应剂罐有无<span style="color: #ff0000">*</span></td>--%>
                            <%--<td style="width: 50%;font-size:7pt;color:darkgrey"></td>--%>
                            <%--<td style="width: 15%">--%>
                                <%--<input id="fyjgyw" name="fyjgyw" class="mini-combobox" style="width:98%;"--%>
                                       <%--textField="key" valueField="value" nullItemText="请选择" emptyText="请选择..."--%>
                                       <%--data="[{key:'有',value:'有'},{key:'无',value:'无'}]"--%>
                                       <%--allowInput="false" showNullItem="false" />--%>
                            <%--</td>--%>
                        <%--<tr>--%>
                        <%--<tr>--%>
                            <%--<td style="width: 15%;text-align: left">反应剂罐材料</td>--%>
                            <%--<td style="width: 50%;font-size:7pt;color:darkgrey">填写实际反应剂罐材料</td>--%>
                            <%--<td style="width: 15%">--%>
                                <%--<input id="fyjgcl" name="fyjgcl" class="mini-combobox" style="width:98%;"--%>
                                       <%--textField="text" valueField="textValue" nullItemText="请选择" emptyText="请选择..."--%>
                                       <%--url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=cl" showNullItem="true"/>--%>
                            <%--</td>--%>
                        <%--<tr>--%>
                        <%--<tr>--%>
                            <%--<td style="width: 15%;text-align: left">选其他材料时填写相应内容</td>--%>
                            <%--<td style="width: 50%;font-size:7pt;color:darkgrey">选填，反应剂罐材料选其他时填写</td>--%>
                            <%--<td style="width: 15%">--%>
                                <%--<input id="fyjgqt" name="fyjgqt" class="mini-textbox" style="  width:98%;"/>--%>
                            <%--</td>--%>
                        <%--<tr>--%>
                        <%--<tr>--%>
                            <%--<td style="width: 15%;text-align: left">有无反应剂使用说明标签</td>--%>
                            <%--<td style="width: 50%;font-size:7pt;color:darkgrey"></td>--%>
                            <%--<td style="width: 15%">--%>
                                <%--<input id="fyjsm" name="fyjsm" class="mini-combobox" style="width:98%;"--%>
                                       <%--textField="key" valueField="value" nullItemText="请选择" emptyText="请选择..."--%>
                                       <%--data="[{key:'有',value:'有'},{key:'无',value:'无'}]"--%>
                                       <%--allowInput="false" showNullItem="false" />--%>
                            <%--</td>--%>
                        <%--<tr>--%>
                        <%--<tr>--%>
                            <%--<td style="width: 15%;text-align: left">有无反应剂防冻系统</td>--%>
                            <%--<td style="width: 50%;font-size:7pt;color:darkgrey"></td>--%>
                            <%--<td style="width: 15%">--%>
                                <%--<input id="fyjfd" name="fyjfd" class="mini-combobox" style="width:98%;"--%>
                                       <%--textField="key" valueField="value" nullItemText="请选择" emptyText="请选择..."--%>
                                       <%--data="[{key:'有',value:'有'},{key:'无',value:'无'}]"--%>
                                       <%--allowInput="false" showNullItem="false" />--%>
                            <%--</td>--%>
                        <%--<tr>--%>
                        <%--<tr>--%>
                            <%--<td style="width: 15%;text-align: left">有无反应剂喷射系统防冻系统</td>--%>
                            <%--<td style="width: 50%;font-size:7pt;color:darkgrey"></td>--%>
                            <%--<td style="width: 15%">--%>
                                <%--<input id="fyjps" name="fyjps" class="mini-combobox" style="width:98%;"--%>
                                       <%--textField="key" valueField="value" nullItemText="请选择" emptyText="请选择..."--%>
                                       <%--data="[{key:'有',value:'有'},{key:'无',value:'无'}]"--%>
                                       <%--allowInput="false" showNullItem="false" />--%>
                            <%--</td>--%>
                        <%--<tr>--%>
                        <%--<tr>--%>
                            <%--<td style="width: 15%;text-align: left">尿素箱数量<span style="color: #ff0000">*</span></td>--%>
                            <%--<td style="width: 50%;font-size:7pt;color:darkgrey">若产品上安装，需填写</td>--%>
                            <%--<td style="width: 15%">--%>
                                <%--<input id="nsxsl" name="nsxsl" class="mini-textbox" style="  width:98%;"/>--%>
                            <%--</td>--%>
                        <%--</tr>--%>
                        <%--<tr>--%>
                            <%--<td style="width: 15%;text-align: left">尿素箱容积（L）<span style="color: #ff0000">*</span></td>--%>
                            <%--<td style="width: 50%;font-size:7pt;color:darkgrey">若产品上安装，需填写尿素箱有效容积(最大容积的95%)</td>--%>
                            <%--<td style="width: 15%">--%>
                                <%--<input id="nsxrj" name="nsxrj" class="mini-textbox" style="  width:98%;"/>--%>
                            <%--</td>--%>
                        <%--</tr>--%>
                        <tr>
                            <td style="width: 15%;text-align: left">发动机总成物料号<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                            <td style="width: 15%">
                                <input id="fdjzcwlh" name="fdjzcwlh" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">发动机系族<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">填写14位柴油机系族编码</td>
                            <td style="width: 15%">
                                <input id="cyjxs" name="cyjxs" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">发动机型号<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">填写完整的发动机铭牌型号</td>
                            <td style="width: 15%">
                                <input id="fdjxh" name="fdjxh" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">发动机品牌<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                            <td style="width: 15%">
                                <input id="fdjzzs" name="fdjzzs" class="mini-combobox" style="width:98%;"
                                       textField="text" valueField="textValue" nullItemText="请选择" emptyText="请选择..."
                                       url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=pp" showNullItem="true"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">发动机商标<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">填写发动机环保信息公开中"厂牌"项所示的全部内容</td>
                            <td style="width: 15%">
                                <input id="fdjsb" name="fdjsb" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">发动机生产厂地址<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                            <td style="width: 15%">
                                <input id="fdjscdz" name="fdjscdz" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">发动机环保信息公开编号<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey">需要发动机厂家授权</td>
                            <td style="width: 15%">
                                <input id="fdjgkbh" name="fdjgkbh" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">最大基准扭矩(N.m)<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                            <td style="width: 15%">
                                <input id="zdjznj" name="zdjznj" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 15%;text-align: left">反应剂存储罐容积(L)<span style="color: #ff0000">*</span></td>
                            <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                            <td style="width: 15%">
                                <input id="fyjccgrj" name="fyjccgrj" class="mini-textbox" style="  width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 7%;height: 300px ">动力工程师附件上传
                                <br>燃油箱安装位置示意图<span style="color: #ff0000">*</span>
                                <br>排放控制件位置示意图<span style="color: #ff0000">*</span>
                                <br>(比例示意图、图纸、照片，至少应明确发动机、后处理装置的相对位置)
                                <br>排放质保零部件清单<span style="color: #ff0000">*</span>
                                <br>失效策略合规声明（加盖公章并扫描成PDF）<span style="color: #ff0000">*</span></td>
                            <td colspan="2">
                                <div style="margin-bottom: 2px">
                                    <a id="addDlFile" class="mini-button" onclick="fileuploadDl()">添加附件</a>
                                    <a id="downImportGsClhbpf" class="mini-button" onclick="downImportGsClhbpf()">下载排放质保零部件清单模板</a>
                                    <a id="downImportGsClhbsx" class="mini-button" onclick="downImportGsClhbsx()">下载失效策略合规声明模板</a>
                                </div>
                                <div id="dlFileListGrid" class="mini-datagrid" style="width: 100%; height: 250px"
                                     allowResize="false"
                                     idField="id"  autoload="true"
                                     url="${ctxPath}/environment/core/Cx/getFileList.do?upNode=dl&cxId=${cxId}"
                                     multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                                    <div property="columns">
                                        <div type="indexcolumn" align="center" width="20">序号</div>
                                        <div field="fileName" width="70" headerAlign="center" align="center">附件名称</div>
                                        <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                        <div field="fileType" width="80" headerAlign="center" align="center">附件类型</div>
                                        <div field="action" width="100" headerAlign='center' align="center" renderer="operationRendererDl">操作
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <fieldset id="hbzy" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox"  id="checkboxHbInfo" onclick="bztoggleFieldSet(this, 'hbzy')" hideFocus/>
                        产品主管上传环保信息附件
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table style="table-layout: fixed;color: #555!important;" class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 33%">项目名称(*)号必填</td>
                            <td style="width: 33%">内容填写说明</td>
                            <td style="width: 33%">内容填写</td>
                        </tr>
                        <tr>
                            <td style="width: 33% ">整机环保信息公开号<span style="color: #ff0000">*</span></td>
                            <td>非道路环保信息公开系统生成的29位编码(包含空格)<br>例如：:CN FJ G4 00 0L53000006 000001</td>
                            <td>
                                <input id="zjgkh" name="zjgkh" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 7%;height: 300px ">产品主管上传环保信息附件<br>整机环保信息公开表<span style="color: #ff0000">*</span><br>发动机环保信息公开表<span style="color: #ff0000">*</span>
                                <br>环保信息标签打刻内容模板<span style="color: #ff0000">*</span><br>单机信息上传内容模板<span style="color: #ff0000">*</span></td>
                            <td colspan="2">
                                <div style="margin-bottom: 2px">
                                    <a id="addHbFile" class="mini-button" onclick="fileuploadHb()">添加附件</a>
                                    <a id="downloadGsClhb" class="mini-button" onclick="downImportGsClhb()">下载打刻模板</a>
                                    <a id="downloadGsClhbdj" class="mini-button" onclick="downImportGsClhbdj()">下载单机信息模板</a>
                                </div>
                                <div id="hbFileListGrid" class="mini-datagrid" style="width: 100%; height: 250px"
                                     allowResize="false"
                                     idField="id"  autoload="true"
                                     url="${ctxPath}/environment/core/Cx/getFileList.do?upNode=hb&cxId=${cxId}"
                                     multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                                    <div property="columns">
                                        <div type="indexcolumn" align="center" width="20">序号</div>
                                        <div field="fileName" width="70" headerAlign="center" align="center">附件名称</div>
                                        <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                        <div field="fileType" width="80" headerAlign="center" align="center">附件类型</div>
                                        <div field="action" width="100" headerAlign='center' align="center" renderer="operationRendererHb">操作
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <fieldset id="xxs" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox"  id="checkboxXxInfo" onclick="bztoggleFieldSet(this, 'xxs')" hideFocus/>
                        数字化部填写
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table style="table-layout: fixed;color: #555!important;" class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 33%">项目名称(*)号必填</td>
                            <td style="width: 33%">内容填写说明</td>
                            <td style="width: 33%">内容填写</td>
                        </tr>
                        <tr>
                            <td style="width: 33% ">提交信息公开表填写公司官网地址<span style="color: #ff0000">*</span></td>
                            <td>暂时，待公司该模块建成</td>
                            <td>
                                <input id="gwdz" name="gwdz" emptyText="正在建设中" readonly class="mini-textbox" style="font-size:7pt;width:98%;"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            </table>
        </form>
    </div>
</div>
<div id="selectModelWindow" title="选择设计型号" class="mini-window" style="width:900px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">设计型号: </span><input
            class="mini-textbox" style="width: 120px" id="designModel" name="designModel"/>
        <span class="text" style="width:auto">销售型号: </span><input
            class="mini-textbox" style="width: 120px" id="saleModel" name="saleModel"/>
        <span class="text" style="width:auto">产品所: </span><input
            class="mini-textbox" style="width: 120px" id="departName" name="departName"/>
        <span class="text" style="width:auto">产品主管: </span><input
            class="mini-textbox" style="width: 120px" id="productManagerName" name="productManagerName"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchModel()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="selectModelListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false"
             allowAlternating="true" showPager="true" multiSelect="false"
             url="${ctxPath}/world/core/productSpectrum/dataListQuery.do?">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="materialCode" width="150" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    物料号
                </div>
                <div field="designModel" width="150" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    设计型号
                </div>
                <div field="departName" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    产品所
                </div>
                <div field="productManagerName" width="80" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    产品主管
                </div>
                <div field="saleModel" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    销售型号
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectModelOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectModelHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var cxId = "${cxId}";
    var oldCxId = "${oldCxId}";
    var type = "${type}";
    var formCx = new mini.Form("#formCx");
    var cpFileListGrid=mini.get("cpFileListGrid");
    var dqFileListGrid=mini.get("dqFileListGrid");
    var dlFileListGrid=mini.get("dlFileListGrid");
    var bzhFileListGrid=mini.get("bzhFileListGrid");
    var hbFileListGrid=mini.get("hbFileListGrid");
    var currentUserName="${currentUserName}";
    var currentUserId = "${currentUserId}";
    var deptName = "${deptName}";
    var currentUserMainDepId = "${currentUserMainDepId}";
    var currentTime="${currentTime}";
    var nodeVarsStr = '${nodeVars}';
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var selectModelWindow = mini.get("selectModelWindow");
    var selectModelListGrid = mini.get("selectModelListGrid");

    function bztoggleFieldSet(ck, id) {
        var dom = document.getElementById(id);
        if (ck.checked) {
            dom.className = "";
        } else {
            dom.className = "hideFieldset";
        }
    }
    function deleteFile(fileName,fileId,formId,urlValue) {
        mini.confirm("确定删除？", "提示",
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + urlValue;
                    var data = {
                        fileName: fileName,
                        id: fileId,
                        formId: formId
                    };
                    $.ajax({
                        url:url,
                        method:'post',
                        contentType: 'application/json',
                        data:mini.encode(data),
                        success:function (json) {
                            if(cpFileListGrid) {
                                cpFileListGrid.load();
                            }
                            if(dqFileListGrid) {
                                dqFileListGrid.load();
                            }
                            if(dlFileListGrid) {
                                dlFileListGrid.load();
                            }
                            if(bzhFileListGrid) {
                                bzhFileListGrid.load();
                            }
                            if(hbFileListGrid) {
                                hbFileListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }

    function operationRendererCp(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnCxPreviewSpan(record.fileName,record.fileId,record.cxId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadCxFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.cxId+'\')">下载</span>';
        //增加删除按钮
        if(((action=="edit"||first)&&record.CREATE_BY_==currentUserId && action!='detail')||action=='change') {
            var deleteUrl="/environment/core/Cx/deletecxFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.cxId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }
    function operationRendererDq(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnCxPreviewSpan(record.fileName,record.fileId,record.cxId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadCxFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.cxId+'\')">下载</span>';
        //增加删除按钮
        if((second&&record.CREATE_BY_==currentUserId && action!='detail')||action=='change') {
            var deleteUrl="/environment/core/Cx/deletecxFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.cxId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }function operationRendererDl(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnCxPreviewSpan(record.fileName,record.fileId,record.cxId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadCxFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.cxId+'\')">下载</span>';
        //增加删除按钮
        if((third&&record.CREATE_BY_==currentUserId && action!='detail')||action=='change'){
            var deleteUrl="/environment/core/Cx/deletecxFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.cxId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }
    function operationRendererBzh(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnCxPreviewSpan(record.fileName,record.fileId,record.cxId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadCxFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.cxId+'\')">下载</span>';
        //增加删除按钮
        if(forth&&record.CREATE_BY_==currentUserId && action!='detail') {
            var deleteUrl="/environment/core/Cx/deletecxFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.cxId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }
    function operationRendererHb(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnCxPreviewSpan(record.fileName,record.fileId,record.cxId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadCxFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.cxId+'\')">下载</span>';
        //增加删除按钮
        if((fifth&&record.CREATE_BY_==currentUserId && action!='detail')||action=='change') {
            var deleteUrl="/environment/core/Cx/deletecxFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.cxId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }

    function fileuploadCp() {
        var cxId = mini.get("cxId").getValue();
        if (!cxId) {
            mini.alert("请先保存!");
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/environment/core/Cx/openUploadWindow.do?upNode=cp&cxId=" + cxId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (cpFileListGrid) {
                    cpFileListGrid.load();
                }
            }
        });
    }
    function fileuploadDq() {
        var cxId = mini.get("cxId").getValue();
        if (!cxId) {
            mini.alert("请先保存!");
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/environment/core/Cx/openUploadWindow.do?upNode=dq&cxId=" + cxId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (dqFileListGrid) {
                    dqFileListGrid.load();
                }
            }
        });
    }
    function fileuploadDl() {
        var cxId = mini.get("cxId").getValue();
        if (!cxId) {
            mini.alert("请先保存!");
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/environment/core/Cx/openUploadWindow.do?upNode=dl&cxId=" + cxId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (dlFileListGrid) {
                    dlFileListGrid.load();
                }
            }
        });
    }
    function fileuploadBzh() {
        var cxId = mini.get("cxId").getValue();
        if (!cxId) {
            mini.alert("请先保存!");
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/environment/core/Cx/openUploadWindow.do?upNode=bzh&cxId=" + cxId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (bzhFileListGrid) {
                    bzhFileListGrid.load();
                }
            }
        });
    }
    function fileuploadHb() {
        var cxId = mini.get("cxId").getValue();
        if (!cxId) {
            mini.alert("请先保存!");
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/environment/core/Cx/openUploadWindow.do?upNode=hb&cxId=" + cxId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (hbFileListGrid) {
                    hbFileListGrid.load();
                }
            }
        });
    }

    function selectModelClick() {
        selectModelWindow.show();
        searchModel();
    }

    function searchModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("designModel").getValue());
        if (designModel) {
            queryParam.push({name: "designModel", value: designModel});
        }
        var departName = $.trim(mini.get("departName").getValue());
        if (departName) {
            queryParam.push({name: "departName", value: departName});
        }
        var productManagerName = $.trim(mini.get("productManagerName").getValue());
        if (productManagerName) {
            queryParam.push({name: "productManagerName", value: productManagerName});
        }
        var saleModel = $.trim(mini.get("saleModel").getValue());
        if (saleModel) {
            queryParam.push({name: "saleModel", value: saleModel});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectModelListGrid.load(data);
    }

    function selectModelOK() {
        var rowSelected = selectModelListGrid.getSelected();
        if (rowSelected) {
            mini.get("zjsjxh").setValue(rowSelected.id);
            mini.get("zjsjxh").setText(rowSelected.designModel);
            mini.get("zjwlh").setValue(rowSelected.materialCode);
            selectModelHide();
        }
    }

    function selectModelHide() {
        selectModelWindow.hide();
        mini.get("saleModel").setValue('');
        mini.get("productManagerName").setValue('');
        mini.get("departName").setValue('');
        mini.get("designModel").setValue('');
        selectModelListGrid.clearSelect();
    }

    function onRelModelCloseClick() {
        mini.get("zjsjxh").setValue('');
        mini.get("zjsjxh").setText('');
        mini.get("zjwlh").setValue('');
        selectModelListGrid.clearSelect();
    }
</script>
</body>
</html>