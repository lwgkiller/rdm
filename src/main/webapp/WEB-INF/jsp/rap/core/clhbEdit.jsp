
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>国三环保信息公开新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rap/clhbEdit.js?version=${static_res_version}" type="text/javascript"></script>
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
        <form id="formWj" method="post">
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="wjId" name="wjId" class="mini-hidden"/>
            <input id="oldWjId" name="oldWjId" class="mini-hidden"/>
            <input id="type" name="type" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table style="table-layout: fixed" class="table-detail" cellspacing="1" cellpadding="0"  >
                <tr>
                    <td style="width: 20%">流程编码(流程结束后自动生成)</td>
                    <td style="width: 25%">
                        <input id="num" name="num" class="mini-textbox" readonly style="width:98%"/>
                    </td>
                    <td style="width: 25%">申请时间(保存后自动生成)</td>
                    <td style="width: 25%">
                        <input id="CREATE_TIME_" name="CREATE_TIME_" class="mini-datepicker" readonly style="width:98%"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 25%">产品主管</td>
                    <td style="width: 25%">
                        <input id="apply" name="applyId" textname="applyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="width: 25% ">申请部门</td>
                    <td style="width: 25%;min-width:170px ">
                        <input id="dept" name="deptId" textname="deptName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr id="dl">
                    <td style="width: 25% ">动力工程师</td>
                    <td style="width: 25%;min-width:170px ">
                        <input id="dlId" name="dlId" textname="dlName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td rowspan="25" style="width: 25%">产品主管汇总</td>
                    <td style="width: 25%;font-size:14pt;color: black">项目名称(*)号必填</td>
                    <td style="width: 25%;font-size:14pt;color: black">内容填写说明</td>
                    <td style="width: 25%;font-size:14pt;color: black">内容填写</td>
                </tr>
                <tr>
                    <td style="width: 20%;font-size:7pt">内部编号<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">产品销售型号+三位流水号</td>
                    
                    <td style="width: 33%;min-width:170px;font-size:7pt">
                        <input id="wjNo" name="wjNo"  class="mini-textbox"  style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">排放标准<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">排放阶段</td>
                    <td>
                        <input id="wjEmission" name="wjEmission" readonly class="mini-textbox"  style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">车辆类别<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                    <td>
                        <input id="carType" name="carType" readonly class="mini-textbox"  style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">国家标准<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                    <td>
                        <input id="nationStandard" name="nationStandard" readonly class="mini-textbox"  style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">整机设计型号<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                    <td>
                        <input id="designXh" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onRelModelCloseClick()"
                               name="designXhId" textname="designXh" allowInput="false"
                               onbuttonclick="selectModelClick()"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">整机物料号<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                    <td>
                        <input id="zjwlh" name="zjwlh" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">机械名称<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">与产品标牌一致</td>
                    <td>
                        <input id="wjName" name="wjName" readonly class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt;font-weight: bolder">机械型号<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">产品销售型号，与产品标牌一致</td>

                    <td>
                        <input id="wjModel" name="wjModel" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt;font-weight: bolder">质量名称(填写适用的质量名称，比如：工作质量)<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">工作质量</td>
                    <td>
                        <input id="zlName" name="zlName" readonly class="mini-textbox"  style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt;font-weight: bolder">质量<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">与型式试验报告一致</td>
                    
                    <td>
                        <input id="wjWeight" name="wjWeight" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt;font-weight: bolder">额定功率(kW)<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">发动机额定功率具体数值</td>
                    <td>
                        <input id="ratedPower" name="ratedPower" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">外形尺寸<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">长*宽*高，单位为mm，可填写多个，在相应尺寸后加注说明是什么尺寸</td>
                    
                    <td>
                        <input id="wjSize" name="wjSize" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">机械环保代码在机体上的打刻位置<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">PIN码在整机铭牌上</td>
                    <td>
                        <input id="dmLocation" name="dmLocation" readonly class="mini-textbox"  style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">机械的标识方法和位置<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">根据产品实际安装位置填写</td>

                    <td>
                        <input id="bsLocation" name="bsLocation" class="mini-combobox" style="width:98%;"
                               textField="text" valueField="textValue" nullItemText="请选择..."
                               url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=bs" showNullItem="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">环保信息标签的位置<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">根据大小挖不同情况具体化</td>
                    <td>
                        <input id="bqLocation" name="bqLocation" class="mini-combobox" style="width:98%;"
                               textField="text" valueField="textValue" nullItemText="请选择..."
                               url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=bq" showNullItem="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">额定工作参数</td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">系统压力</td>
                    <td>
                        <input id="parameter" name="parameter" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">商标<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">徐工牌XCMG</td>

                    <td>
                        <input id="wjBrand" name="wjBrand" readonly class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">机械分类-大类<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">工程机械</td>

                    <td>
                        <input id="classA" name="classA"  readonly class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">机械分类-小类<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">挖掘机械</td>

                    <td>
                        <input id="classB" name="classB" readonly class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">制造商名称<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">徐州徐工挖掘机械有限公司</td>

                    <td>
                        <input id="producter" name="producter" readonly class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">生产厂地址<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">江苏省徐州经济技术开发区高新路39号</td>
                    <td>
                        <input id="adress" name="adress" readonly class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%;height: 300px;font-size:7pt">外形照片附件列表<br>(与型式试验报告一致，如：正侧面45°角照片)：</td>
                    <td colspan="2">
                        <div style="margin-bottom: 2px;margin-top: 1px;">
                            <a id="addFile" class="mini-button" onclick="fileuploadtupian()">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 250px"
                             allowResize="false"
                             idField="id"  autoload="true"
                             url="${ctxPath}/environment/core/Wj/getFileList.do?fileType=tupian&wjId=${wjId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" width="70" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">整机环保信息公开号<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">非道路环保信息公开系统生成的29位编码(包含空格)</td>
                    <td>
                        <input id="zjgkh" name="zjgkh" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%;height: 300px;font-size:7pt"><br>整机环保信息公开表<br>发动机环保信息公开表<br>环保信息标签打刻内容模板<br>单机信息上传内容模板附件列表：</td>
                    <td colspan="2">
                        <div style="margin-bottom: 2px">
                            <a id="addFile2" class="mini-button" onclick="fileuploadbiaoge()">添加附件</a>
                            <a id="downloadClhb" class="mini-button" onclick="downImportClhb()">下载打刻模板</a>
                            <a id="downloadGsClhbdj" class="mini-button" onclick="downImportGsClhbdj()">下载单机信息模板</a>
                        </div>
                        <div id="fileListGrid2" class="mini-datagrid" style="width: 100%; height: 250px"
                             allowResize="false"
                             idField="id"  autoload="true"
                             url="${ctxPath}/environment/core/Wj/getFileList.do?fileType=biaoge&wjId=${wjId}"
                             multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" width="70" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td rowspan="15">动力工程师汇总</td>
                    <td style="width: 50%;font-size:7pt">燃料类型<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">柴油</td>
                    
                    <td>
                        <input id="fuelType" name="fuelType" readonly class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">燃料规格<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">GB19147 车用柴油</td>
                    
                    <td>
                        <input id="fuelSpecies" name="fuelSpecies" readonly class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">隔热措施<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">有/无</td>
                    
                    <td>
                        <input id="grMeasures" name="grMeasures" class="mini-combobox" style="width:98%;"
                               textField="text" valueField="textValue" nullItemText="请选择..."
                               url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=gr" showNullItem="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">隔热措施描述<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">如有隔热措施，则进行描述，或无隔热措施，则填无</td>
                    
                    <td>
                        <input id="grMeasuresMs" name="grMeasuresMs" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">后处理具体安装位置<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">填写后处理前端到发动机的距离，没有后处理则填无后处理</td>
                    
                    <td>
                        <input id="hclLocation" name="hclLocation" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">进气系统特征<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                    
                    <td>
                        <input id="jqSystem" name="jqSystem" class="mini-combobox" style="width:98%;"
                               textField="text" valueField="textValue" nullItemText="请选择..."
                               url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=jq" showNullItem="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">机械进气系统阻力<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">填限值</td>
                    
                    <td>
                        <input id="resistance" name="resistance" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">中冷器形式<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">空空中冷或无</td>
                    
                    <td>
                        <input id="coldType" name="coldType" class="mini-combobox" style="width:98%;"
                               textField="text" valueField="textValue" nullItemText="请选择..."
                               url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=zlq" showNullItem="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">发动机额定转速和100%负荷下,排气系统背压（kPa）<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">填写限值</td>
                    <td>
                        <input id="pqPressure" name="pqPressure" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%;font-size:7pt">发动机品牌<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt"></td>
                    
                    <td>
                        <input id="fdjzzs" name="fdjzzs" class="mini-combobox" style="width:98%;"
                               textField="text" valueField="textValue" nullItemText="请选择..."
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
                    <td style="width: 50%;font-size:7pt">发动机型号<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">填写完整的发动机铭牌型号</td>
                    <td>
                        <input id="fdjxh" name="fdjxh" class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 15%;text-align: left;font-size:7pt">发动机总成物料号<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                    <td style="width: 15%">
                        <input id="fdjzcwlh" name="fdjzcwlh" class="mini-textbox" style="  width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 15%;text-align: left;font-size:7pt">发动机生产厂地址<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey"></td>
                    <td style="width: 15%">
                        <input id="fdjscdz" name="fdjscdz" class="mini-textbox" style="  width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 15%;text-align: left;font-size:7pt">发动机环保信息公开编号<span style="color: #ff0000">*</span></td>
                    <td style="width: 50%;font-size:7pt;color:darkgrey">需要发动机厂家授权</td>
                    <td style="width: 15%">
                        <input id="fdjgkbh" name="fdjgkbh" class="mini-textbox" style="  width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 50%">数字化部汇总</td>
                    <td style="width: 33% ">提交信息公开表填写公司官网地址<span style="color: #ff0000">*</span></td>
                    <td>暂时，待公司该模块建成</td>
                    <td>
                        <input id="gwdz" name="gwdz" emptyText="正在建设中" readonly class="mini-textbox" style="font-size:7pt;width:98%;"/>
                    </td>
                </tr>
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
    var wjListGrid = mini.get("wjListGrid");
    var wjId = "${wjId}";
    var oldWjId = "${oldWjId}";
    var type = "${type}";
    var formWj = new mini.Form("#formWj");
    var fileListGrid=mini.get("fileListGrid");
    var fileListGrid2=mini.get("fileListGrid2");
    var currentUserName="${currentUserName}";
    var currentUserId = "${currentUserId}";
    var deptName = "${deptName}";
    var currentUserMainDepId = "${currentUserMainDepId}";
    var currentTime="${currentTime}";
    var nodeVarsStr = '${nodeVars}';
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var selectModelWindow = mini.get("selectModelWindow");
    var selectModelListGrid = mini.get("selectModelListGrid");


    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnWjPreviewSpan(record.fileName,record.fileId,record.wjId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadWjFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.wjId+'\')">下载</span>';
        //增加删除按钮
        if((record.CREATE_BY_==currentUserId && action!='detail')||action=='change') {
            var deleteUrl="/environment/core/Wj/deletewjFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.wjId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
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
                            if(fileListGrid) {
                                fileListGrid.load();
                            }
                            if(fileListGrid2) {
                                fileListGrid2.load();
                            }
                        }
                    });
                }
            }
        );
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
            mini.get("designXh").setValue(rowSelected.id);
            mini.get("designXh").setText(rowSelected.designModel);
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
        mini.get("designXh").setValue('');
        mini.get("designXh").setText('');
        mini.get("zjwlh").setValue('');
        selectModelListGrid.clearSelect();
    }




</script>
</body>
</html>