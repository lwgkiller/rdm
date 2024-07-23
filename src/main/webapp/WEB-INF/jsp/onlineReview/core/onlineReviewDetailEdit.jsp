<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>出口线上评审</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveModel" name="saveModel" class="mini-button" style="display: none" onclick="saveModel()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit" id="content">
    <div class="form-container" style="margin: 0 auto; width: 80%">
        <form id="formModelDetail" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="belongId" name="belongId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <p style="font-size: 16px;font-weight: bold">机型信息</p>
            <hr/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 12%;">研发分管领导意见：</td>
                    <td style="width: 12%" colspan="3">
                        <input property="editor" id="gzOption" name="gzOption" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'同意',text:'同意'},{id:'不同意',text:'不同意'}]"/>
                    </td>
                    <td style="  width: 12%;">生产分管领导意见：</td>
                    <td style="width: 12%" colspan="3">
                        <input property="editor" id="yzOption" name="yzOption" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'同意',text:'同意'},{id:'不同意',text:'不同意'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style=";width: 12%">不同意原因：
                    </td>
                    <td colspan="3">
                        <input id="gzReason" name="gzReason" class="mini-textarea" style="width:99%"/>
                    </td>
                    <td style=";width: 12%">不同意原因：
                    </td>
                    <td colspan="3">
                        <input id="yzReason" name="yzReason" class="mini-textarea" style="width:99%"/>
                    </td>
                </tr>
                <tr>
                    <td style=";width: 12%">设计型号：
                    </td>
                    <td>
                        <input id="designModel" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onRelModelCloseClick()"
                               name="designModelId" textname="designModel" allowInput="false"
                               onbuttonclick="selectModelClick()"/>
                    </td>
                    <td style=";width: 12%">整机物料号：
                    </td>
                    <td>
                        <input id="materielCode" name="materielCode" class="mini-textbox"
                               style="width:99%"/>
                    </td>
                    <td style=";width: 12%">销售型号：
                    </td>
                    <td>
                        <input id="saleModel" name="saleModel" class="mini-textbox" style="width:99%"/>
                    </td>
                    <td style=";width: 12%">数量：
                    </td>
                    <td>
                        <input id="num" name="num" class="mini-textbox" style="width:99%"/>
                    </td>
                </tr>
                <tr>
                    <td style=";width: 12%">需求交货日期：
                    </td>
                    <td>
                        <input id="needTime" name="needTime" class="mini-datepicker"
                               format="yyyy-MM-dd" showTime="false"/>
                    </td>
                    <td style=";width: 12%">技术主管：
                    </td>
                    <td>
                        <input id="jszg" name="jszgId" textname="jszgName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style=";width: 12%">工艺主管：
                    </td>
                    <td>
                        <input id="gyzg" name="gyzgId" textname="gyzgName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style=";width: 12%">制造部计划员：
                    </td>
                    <td>
                        <input id="jhy" name="jhyId" textname="jhyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style=";width: 12%">运输方式：
                    </td>
                    <td>
                        <input id="transportMode" name="transportMode" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="运输方式："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=transportMode"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">运输形式：
                    </td>
                    <td>
                        <input id="transportType" name="transportType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="运输形式："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=transportType"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">包装方式：
                    </td>
                    <td>
                        <input id="packageMode" name="packageMode" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="包装方式："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="true"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=packageMode"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">发运地点：
                    </td>
                    <td>
                        <input id="location" name="location" class="mini-textbox" style="width:99%"/>
                    </td>
                </tr>
                <tr>
                    <td style=";width: 12%">方案需求：
                    </td>
                    <td>
                        <input id="planRequire" name="planRequire" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="方案需求："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=planRequire"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">是否需要改色：
                    </td>
                    <td>
                        <input property="editor" id="colorChange" name="colorChange" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'是',text:'是'},{id:'否',text:'否'}]"/>
                    </td>
                    <td style=";width: 12%">改色色号：
                    </td>
                    <td>
                        <input id="colorNum" name="colorNum" class="mini-textbox" style="width:99%"/>
                    </td>
                    <td style=";width: 12%">改色部位：
                    </td>
                    <td>
                        <input property="editor" id="colorPart" name="colorPart" class="mini-textbox"
                               style="width:99%"/>
                    </td>
                </tr>
                <tr>
                    <td style=";width: 12%">油品要求：
                    </td>
                    <td>
                        <input property="editor" id="oilRequire" name="oilRequire" class="mini-combobox rxc"
                               plugins="mini-combobox"
                               style="width:100%;height:34px" label="油品要求："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=oilRequire"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">机具要求：
                    </td>
                    <td>
                        <input id="jjRequire" name="jjRequire" class="mini-textbox" style="width:99%"/>
                    </td>
                    <td style=";width: 12%">排放要求：
                    </td>
                    <td>
                        <input id="pfRequire" name="pfRequire" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="排放要求："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=pfRequire"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">是否使用库存车改制：
                    </td>
                    <td>
                        <input property="editor" id="inventoryChange" name="inventoryChange" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'库存车改制',text:'库存车改制'},{id:'新排产',text:'新排产'}]"/>
                    </td>
                </tr>
                <tr id="gzTr">
                    <td style=";width: 12%">改制车号：
                    </td>
                    <td colspan="3">
                        <input id="changeNum" name="changeNum" class="mini-textarea" style="width:99%;height: 100px;"/>
                    </td>
                    <td style=";width: 12%">原车号配置：
                    </td>
                    <td colspan="3">
                        <input id="initialConfig" name="initialConfig" class="mini-textarea"
                               style="width:99%;height: 100px;"/>
                    </td>
                </tr>
                <tr>
                    <td style=";width: 12%">认证要求：
                    </td>
                    <td>
                        <input id="rzRequire" name="rzRequire" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="认证要求："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=rzRequire"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">零件图册语言：
                    </td>
                    <td>
                        <input id="ljLanguage" name="ljLanguage" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="零件图册语言："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="true"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=language"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">操保手册语言：
                    </td>
                    <td>
                        <input id="cbLanguage" name="cbLanguage" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="操保手册语言："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="true"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=language"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">装箱单语言：
                    </td>
                    <td>
                        <input id="zxdLanguage" name="zxdLanguage" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="装箱单语言："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="true"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=language"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td style=";width: 12%">仪表语言：
                    </td>
                    <td>
                        <input id="ybLanguage" name="ybLanguage" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="仪表语言："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="true"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=language"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">标识语言：
                    </td>
                    <td>
                        <input id="bsLanguage" name="bsLanguage" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="标识语言："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=language"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">铭牌：
                    </td>
                    <td>
                        <input id="nameplate" name="nameplate" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="铭牌："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="true"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=nameplate"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">GPS加装：
                    </td>
                    <td>
                        <input id="gps" name="gps" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'是',text:'是'},{id:'否',text:'否'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style=";width: 12%">特殊要求：
                    </td>
                    <td colspan="7">
                        <input id="specialRequire" name="specialRequire" class="mini-textarea"
                               style="width:99%;height: 100px;"/>
                    </td>
                </tr>
                <tr>
                    <td style=";width: 12%">随机备件箱规格：
                    </td>
                    <td>
                        <input id="package" name="package" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="铭牌："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="text" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=package"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style=";width: 12%">其它（如锁车程序等）：
                    </td>
                    <td>
                        <input property="editor" id="other" name="other" class="mini-textbox" style="width:99%"/>
                    </td>

                </tr>
                <tr>
                    <td style=";width: 12%">是否有工艺BOM：
                    </td>
                    <td>
                        <input property="editor" id="gybom" name="gybom" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'是',text:'是'},{id:'否',text:'否'}]"/>
                    </td>
                    <td style=";width: 12%">装箱单是否更改：
                    </td>
                    <td>
                        <input property="editor" id="zxdChange" name="zxdChange" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'是',text:'是'},{id:'否',text:'否'}]"/>
                    </td>

                    <td style=";width: 12%">是否下发技术通知：
                    </td>
                    <td>
                        <input property="editor" id="jstz" name="jstz" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'是',text:'是'},{id:'否',text:'否'}]"/>
                    </td>
                    <td style=";width: 12%">技术通知编号：
                    </td>
                    <td>
                        <input property="editor" id="jsNum" name="jsNum" class="mini-textbox"
                               style="width:99%"/>
                    </td>
                </tr>
                <tr>
                    <td style=";width: 12%">是否提交机型制作申请：
                    </td>
                    <td>
                        <input property="editor" id="manualStatus" name="manualStatus" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'是',text:'是'},{id:'否',text:'否'}]"/>
                    </td>
                    <td style=";width: 12%">是否上传长周期件明细：
                    </td>
                    <td>
                        <input property="editor" id="periodFile" name="periodFile" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'是',text:'是'},{id:'否',text:'否'}]"/>
                    </td>
                    <td style=";width: 12%">是否编制/变更手册源文件：
                    </td>
                    <td>
                        <input property="editor" id="sourceFile" name="sourceFile" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'是',text:'是'},{id:'否',text:'否'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style=";width: 12%">配置信息：
                    </td>
                    <td colspan="7">
                        <input id="allConfig" name="allConfig" class="mini-textarea"
                               style="width:99%;height: 100px;"/>
                    </td>
                </tr>
            </table>
        </form>
        <p style="font-size: 16px;font-weight: bold;margin-top: 20px">配置信息</p>
        <hr/>
        <div style="margin-top: 5px;margin-bottom: 2px">
            <a id="addConfigDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="addConfigDetail()">添加</a>
            <a id="removeConfigDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="removeConfigDetail()">删除</a>
<%--            <a class="mini-button" plain="true" id="importConfigDetail" onclick="batchImportOpen">导入（新增/更新）</a>--%>
<%--            <a class="mini-button" plain="true" id="exportConfigDetail" onclick="exportConfigDetail">导出</a>--%>
        </div>
        <div id="configDetailListGrid" class="mini-datagrid" style="width: 100%; height:300px"
             allowResize="false"
             allowCellWrap="true" oncellbeginedit="OnCellBeginEditConfig"
             idField="id" url="${ctxPath}/onlineReview/core/getConfigDetailList.do?belongId=${id}"
             autoload="true" allowCellEdit="true" allowCellSelect="true"
             multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="configMsg" width="60px" headerAlign="center" align="center">需求订单配置信息
                    <input property="editor" class="mini-textbox"/></div>
                <div field="changeCode" width="50px" headerAlign="center" align="center">变化点
                    <input property="editor" class="mini-textbox"/></div>
                <div field="configType" width="50px" headerAlign="center" align="center">类型
                    <input property="editor" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="铭牌："
                           length="50"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="text" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=configType"
                           nullitemtext="请选择..." emptytext="请选择..."/></div>
                <div field="risk" headerAlign="center" align="center" width="70px">风险点
                    <input property="editor" class="mini-textbox"/></div>
                <div field="respId" displayField="respId_name" width="40px" headerAlign="center" align="center">责任人
                    <input property="editor" class="mini-user rxc" plugins="mini-user"
                           allowinput="false" single="true"
                           mainfield="no" name="respId"/>
                </div>
                <div field="control" width="60px" headerAlign="center" align="center">控制措施
                    <input property="editor" class="mini-textbox"/></div>
                <div field="finishTime" dateFormat="yyyy-MM-dd" width="40px" headerAlign="center"
                     align="center">方案完成时间
                    <input property="editor" class="mini-datepicker" style="width:98%;"/></div>
            </div>
        </div>
        <p style="font-size: 16px;font-weight: bold">事项完成时间</p>
        <hr/>
        <div style="margin-top: 5px;margin-bottom: 2px">
            <a id="addTimeDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="addTimeDetail()">添加</a>
            <a id="removeTimeDetail" style="margin-right: 5px;display: none" class="mini-button"
               onclick="removeTimeDetail()">删除</a>
        </div>
        <div id="timeDetailListGrid" class="mini-datagrid" style="width: 100%; height: 600px" allowResize="false"
             allowCellWrap="true" oncellbeginedit="OnCellBeginEditTime"
             idField="id" url="${ctxPath}/onlineReview/core/getTimeDetailList.do?belongId=${id}"
             autoload="true" allowCellEdit="true" allowCellSelect="true"
             multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="timeName" width="60px" headerAlign="center" align="center">事项</div>
                <div field="respId" displayField="respId_name" width="40px" headerAlign="center" align="center">责任人
                    <input property="editor" class="mini-user rxc" plugins="mini-user"
                           allowinput="false" single="true"
                           mainfield="no" name="respId"/></div>
                <div field="finishTime" dateFormat="yyyy-MM-dd" width="40px" headerAlign="center"
                     align="center">完成时间
                    <input property="editor" class="mini-datepicker" style="width:98%;"/></div>
                <div field="note" width="40px" headerAlign="center" align="center">备注
                    <input property="editor" class="mini-textbox"/></div>
            </div>
        </div>
        <p style="margin-top: 5px;font-size: 16px;font-weight: bold">改制文件附件表</p>
        <hr/>
        <div style="margin-top: 10px;margin-bottom: 2px">
            <a style="margin-top: 2px;display: none" id="addgzFile" class="mini-button" onclick="fileupload('gz')">添加附件</a>
        </div>
        <div id="gzFileListGrid" class="mini-datagrid" style="width: 100%; height: 200px" allowResize="false"
             allowCellWrap="true" oncellbeginedit="OnCellBeginEditTime"
             idField="id" url="${ctxPath}/onlineReview/core/getOnlineReviewFileList.do?fileType=gz&id=${id}"
             autoload="true" allowCellEdit="true" allowCellSelect="true"
             multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true">
            <div property="columns">
                <div type="indexcolumn" align="center" width="15">序号</div>
                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                <div field="action" width="100" headerAlign='center' align="center"
                     renderer="operationRendererF">操作
                </div>
            </div>
        </div>
        <p style="font-size: 16px;font-weight: bold">长周期件明细附件表</p>
        <hr/>
        <div style="margin-top: 5px;margin-bottom: 2px">
            <a style="margin-top: 2px;display: none" id="addczqFile" class="mini-button" onclick="fileupload('czq')">添加附件</a>
        </div>
        <div id="czqFileListGrid" class="mini-datagrid" style="width: 100%; height: 200px" allowResize="false"
             allowCellWrap="true" oncellbeginedit="OnCellBeginEditTime"
             idField="id" url="${ctxPath}/onlineReview/core/getOnlineReviewFileList.do?fileType=czq&id=${id}"
             autoload="true" allowCellEdit="true" allowCellSelect="true"
             multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true">
            <div property="columns">
                <div type="indexcolumn" align="center" width="15">序号</div>
                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                <div field="action" width="100" headerAlign='center' align="center"
                     renderer="operationRendererF">操作
                </div>
            </div>
        </div>
    </div>
</div>
<div id="selectModelWindow" title="选择设计型号" class="mini-window" style="width:900px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">设计型号: </span><input
            class="mini-textbox" style="width: 120px" id="selectdesignModel" name="selectdesignModel"/>
        <span class="text" style="width:auto">销售型号: </span><input
            class="mini-textbox" style="width: 120px" id="selectsaleModel" name="selectsaleModel"/>
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
    var action = "${action}";
    var status = "${status}";
    var nodeName = "${nodeName}";
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var deptName = "${deptName}";
    var timeDetailListGrid = mini.get("timeDetailListGrid");
    var configDetailListGrid = mini.get("configDetailListGrid");
    var gzFileListGrid = mini.get("gzFileListGrid");
    var czqFileListGrid = mini.get("czqFileListGrid");
    var formModelDetail = new mini.Form("#formModelDetail");
    var id = "${id}";
    var belongId = "${belongId}";
    var stageName = "${stageName}";
    var currentUserRoles =${currentUserRoles};
    var selectModelWindow = mini.get("selectModelWindow");
    var selectModelListGrid = mini.get("selectModelListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

    function OnCellBeginEditConfig(e) {
        var record = e.record, field = e.field;
        e.cancel = true;
        if (field == "configMsg" && (action == "edit" || stageName == "start")) {
            e.cancel = false;
        }
        if (action == "task" && (stageName == 'onejsfa' || stageName == 'twojsfa' || stageName == 'threejsfa')
            && field != "configMsg") {
            e.cancel = false;
        }
        if (action == "task" && (stageName == 'twoddfx' || stageName == 'oneddfx')
            && (field != "configMsg" && field != "respId" && record.respId == currentUserId)) {
            e.cancel = false;
        }
        if (action == "task" && (stageName == 'threeddfx')
            && (field != "configMsg" && field != "respId")) {
            e.cancel = false;
        }
    }

    function OnCellBeginEditTime(e) {
        var record = e.record, field = e.field;
        e.cancel = true;
        if (stageName == 'oneddfx' || stageName == 'twoddfx') {
            if (record.timeType == 'zz' && field != "timeName" && record.respId == currentUserId) {
                e.cancel = false;
            }
        }
        if (stageName == 'threeddfx') {
            if (record.timeType == 'zz' && field != "timeName") {
                e.cancel = false;
            }
        }
        if (stageName == 'onecpzgtime' || stageName == 'twocpzgtime' || stageName == 'threecpzgtime') {
            if (record.timeType == 'js' && field != "timeName") {
                e.cancel = false;
            }
        }
        if (stageName == 'onefyfa' || stageName == 'twofyfa') {
            if (record.timeType == 'gy' && field != "timeName" && record.respId == currentUserId) {
                e.cancel = false;
            }
        }
        if (stageName == 'threefyfa') {
            if (record.timeType == 'gy' && field != "timeName") {
                e.cancel = false;
            }
        }
        if (stageName == 'oneddxf' || stageName == 'twoddxf') {
            if (record.timeType == 'zz' && field != "timeName" && record.respId == currentUserId) {
                e.cancel = false;
            }
        }
        if (stageName == 'threeddxf') {
            if (record.timeType == 'zz' && field != "timeName") {
                e.cancel = false;
            }
        }
        if (stageName == 'onexlbjgy' || stageName == 'twoxlbjgy') {
            if (record.timeType == 'gf' && field != "timeName" && record.respId == currentUserId) {
                e.cancel = false;
            }
        }
        if (stageName == 'threexlbjgy') {
            if (record.timeType == 'gf' && field != "timeName") {
                e.cancel = false;
            }
        }
        if (stageName == 'onecg' || stageName == 'twocg') {
            if (record.timeType == 'cg' && field != "timeName" && record.respId == currentUserId) {
                e.cancel = false;
            }
        }
        if (stageName == 'threecg') {
            if (record.timeType == 'cg' && field != "timeName") {
                e.cancel = false;
            }
        }
        if (stageName == 'onesxrk' || stageName == 'twosxrk') {
            if (record.timeType == 'zz' && field != "timeName" && record.respId == currentUserId) {
                e.cancel = false;
            }
        }
        if (stageName == 'threesxrk') {
            if (record.timeType == 'zz' && field != "timeName") {
                e.cancel = false;
            }
        }
        if (stageName == 'onecblj' || stageName == 'twocblj') {
            if (record.timeType == 'fw' && field != "timeName" && record.respId == currentUserId) {
                e.cancel = false;
            }
        }
        if (stageName == 'threecblj') {
            if (record.timeType == 'fw' && field != "timeName") {
                e.cancel = false;
            }
        }
        if (stageName == 'oneydbk' || stageName == 'twoydbk') {
            if (record.timeType == 'zl' && field != "timeName" && record.respId == currentUserId) {
                e.cancel = false;
            }
        }
        if (stageName == 'threeydbk') {
            if (record.timeType == 'zl' && field != "timeName") {
                e.cancel = false;
            }
        }
    }

    $(function () {
        if (id) {
            var url = jsUseCtxPath + "/onlineReview/core/getModelDetail.do";
            $.post(
                url,
                {id: id},
                function (json) {
                    formModelDetail.setData(json);
                });
        }
        $("#detailToolBar").show();
        //明细入口
        formModelDetail.setEnabled(false);
        if (action == 'detail') {
            formModelDetail.setEnabled(false);
            timeDetailListGrid.setAllowCellEdit(false);
            configDetailListGrid.setAllowCellEdit(false);
        }
        // else if (action == 'edit' || action == 'add') {
        //     $("#saveModel").show();
        //     $("#addTimeDetail").show();
        //     $("#removeTimeDetail").show();
        //     $("#addConfigDetail").show();
        //     $("#removeConfigDetail").show();
        // }
        else if (action == 'edit' || stageName == 'start' || status == 'DRAFTED' || action == 'add') {
            $("#saveModel").show();
            $("#addConfigDetail").show();
            $("#removeConfigDetail").show();
            timeDetailListGrid.setAllowCellEdit(false);
            mini.get("saleModel").setEnabled(true);
            mini.get("designModel").setEnabled(true);
            mini.get("materielCode").setEnabled(true);
            mini.get("num").setEnabled(true);
            mini.get("jszg").setEnabled(true);
            mini.get("gyzg").setEnabled(true);
            mini.get("jhy").setEnabled(true);
            mini.get("needTime").setEnabled(true);
            mini.get("colorChange").setEnabled(true);
            mini.get("colorNum").setEnabled(true);
            mini.get("colorPart").setEnabled(true);
            mini.get("inventoryChange").setEnabled(true);
            mini.get("changeNum").setEnabled(true);
            mini.get("initialConfig").setEnabled(true);
            mini.get("pfRequire").setEnabled(true);
            mini.get("rzRequire").setEnabled(true);
            mini.get("ljLanguage").setEnabled(true);
            mini.get("cbLanguage").setEnabled(true);
            mini.get("zxdLanguage").setEnabled(true);
            mini.get("ybLanguage").setEnabled(true);
            mini.get("bsLanguage").setEnabled(true);
            mini.get("nameplate").setEnabled(true);
            mini.get("gps").setEnabled(true);
            mini.get("oilRequire").setEnabled(true);
            mini.get("jjRequire").setEnabled(true);
            mini.get("transportMode").setEnabled(true);
            mini.get("transportType").setEnabled(true);
            mini.get("packageMode").setEnabled(true);
            mini.get("location").setEnabled(true);
            mini.get("planRequire").setEnabled(true);
            mini.get("specialRequire").setEnabled(true);
            mini.get("allConfig").setEnabled(true);
            mini.get("package").setEnabled(true);
            mini.get("other").setEnabled(true);
        }
        else if (stageName == 'onejsfa' || stageName == 'twojsfa' || stageName == 'threejsfa') {
            $("#addgzFile").show();
            mini.get("manualStatus").setEnabled(true);
            mini.get("zxdChange").setEnabled(true);
            mini.get("jsNum").setEnabled(true);
            mini.get("jstz").setEnabled(true);
            mini.get("saleModel").setEnabled(true);
            mini.get("designModel").setEnabled(true);
            mini.get("materielCode").setEnabled(true);
            mini.get("periodFile").setEnabled(true);
            mini.get("sourceFile").setEnabled(true);
            timeDetailListGrid.setAllowCellEdit(false);
            $("#saveModel").show();
        }
        else if (stageName == 'onecpzgtime' || stageName == 'twocpzgtime' || stageName == 'threecpzgtime') {
            $("#saveModel").show();
        }
        else if (stageName == 'onefyfa' || stageName == 'twofyfa' || stageName == 'threefyfa') {
            mini.get("gybom").setEnabled(true);
            $("#saveModel").show();
        }
        else if (stageName == 'fgld') {
            if (currentUserRoles.indexOf("FGLD") > -1) {
                mini.get("gzOption").setEnabled(true);
                mini.get("gzReason").setEnabled(true);
            }
            if (currentUserRoles.indexOf("GYFGLD") > -1) {
                mini.get("yzOption").setEnabled(true);
                mini.get("yzReason").setEnabled(true);
            }
            $("#saveModel").show();
        }
        else if (action == 'uploadczq') {
            formModelDetail.setEnabled(false);
            timeDetailListGrid.setAllowCellEdit(false);
            configDetailListGrid.setAllowCellEdit(false);
            $("#addczqFile").show();
        }else {
            $("#saveModel").show();
        }
    });

    function valHwxs() {
        var saleModel = $.trim(mini.get("saleModel").getValue());
        if (!saleModel) {
            return {"result": false, "message": "请填写销售型号"};
        }
        var num = $.trim(mini.get("num").getValue());
        if (!num) {
            return {"result": false, "message": "请填写数量"};
        }
        var needTime = $.trim(mini.get("needTime").getValue());
        if (!needTime) {
            return {"result": false, "message": "请填写需求交货期"};
        }
        var jszg = $.trim(mini.get("jszg").getValue());
        if (!jszg) {
            return {"result": false, "message": "请填写技术主管"};
        }
        var gyzg = $.trim(mini.get("gyzg").getValue());
        if (!gyzg) {
            return {"result": false, "message": "请填写工艺主管"};
        }
        var jhy = $.trim(mini.get("jhy").getValue());
        if (!jhy) {
            return {"result": false, "message": "请填写制造部计划员"};
        }
        var transportType = $.trim(mini.get("transportType").getValue());
        if (!transportType) {
            return {"result": false, "message": "请填写运输形式"};
        }
        var transportMode = $.trim(mini.get("transportMode").getValue());
        if (!transportMode) {
            return {"result": false, "message": "请填写运输方式"};
        }
        var packageMode = $.trim(mini.get("packageMode").getValue());
        if (!packageMode) {
            return {"result": false, "message": "请填写包装方式"};
        }
        var location = $.trim(mini.get("location").getValue());
        if (!location) {
            return {"result": false, "message": "请填写发运地点"};
        }
        var planRequire = $.trim(mini.get("planRequire").getValue());
        if (!planRequire) {
            return {"result": false, "message": "请填写方案需求"};
        }
        var specialRequire = $.trim(mini.get("specialRequire").getValue());
        if (!specialRequire) {
            return {"result": false, "message": "请填写特殊要求"};
        }
        var package = $.trim(mini.get("package").getValue());
        if (!package) {
            return {"result": false, "message": "请填写随机备件箱规格"};
        }
        var colorChange = $.trim(mini.get("colorChange").getValue());
        if (!colorChange) {
            return {"result": false, "message": "请填写是否需要改色"};
        }
        var colorNum = $.trim(mini.get("colorNum").getValue());
        if (!colorNum && colorChange == '是') {
            return {"result": false, "message": "请填写改色色号"};
        }
        var colorPart = $.trim(mini.get("colorPart").getValue());
        if (!colorPart && colorChange == '是') {
            return {"result": false, "message": "请填写改色部位"};
        }
        var inventoryChange = $.trim(mini.get("inventoryChange").getValue());
        if (!inventoryChange) {
            return {"result": false, "message": "请填写是否使用库存车改制"};
        }
        var changeNum = $.trim(mini.get("changeNum").getValue());
        if (!changeNum && inventoryChange == '库存车改制') {
            return {"result": false, "message": "请填写改制车号"};
        }
        var initialConfig = $.trim(mini.get("initialConfig").getValue());
        if (!initialConfig && inventoryChange == '库存车改制') {
            return {"result": false, "message": "请填写原车号配置"};
        }
        var pfRequire = $.trim(mini.get("pfRequire").getValue());
        if (!pfRequire) {
            return {"result": false, "message": "请填写排放要求"};
        }
        var rzRequire = $.trim(mini.get("rzRequire").getValue());
        if (!rzRequire) {
            return {"result": false, "message": "请填写认证要求"};
        }
        var ljLanguage = $.trim(mini.get("ljLanguage").getValue());
        if (!ljLanguage) {
            return {"result": false, "message": "请填写零件图册语言"};
        }
        var cbLanguage = $.trim(mini.get("cbLanguage").getValue());
        if (!cbLanguage) {
            return {"result": false, "message": "请填写操保手册语言"};
        }
        var zxdLanguage = $.trim(mini.get("zxdLanguage").getValue());
        if (!zxdLanguage) {
            return {"result": false, "message": "请填写装箱单语言"};
        }
        var ybLanguage = $.trim(mini.get("ybLanguage").getValue());
        if (!ybLanguage) {
            return {"result": false, "message": "请填写仪表语言"};
        }
        var bsLanguage = $.trim(mini.get("bsLanguage").getValue());
        if (!bsLanguage) {
            return {"result": false, "message": "请填写标识语言"};
        }
        var nameplate = $.trim(mini.get("nameplate").getValue());
        if (!nameplate) {
            return {"result": false, "message": "请填写铭牌"};
        }
        var gps = $.trim(mini.get("gps").getValue());
        if (!gps) {
            return {"result": false, "message": "请填写GPS加装"};
        }
        var oilRequire = $.trim(mini.get("oilRequire").getValue());
        if (!oilRequire) {
            return {"result": false, "message": "请填写油品要求"};
        }
        var jjRequire = $.trim(mini.get("jjRequire").getValue());
        if (!jjRequire) {
            return {"result": false, "message": "请填写机具要求"};
        }
        var other = $.trim(mini.get("other").getValue());
        if (!other) {
            return {"result": false, "message": "请填写其它（如锁车程序等）"};
        }
        var allConfig = $.trim(mini.get("allConfig").getValue());
        if (!allConfig) {
            return {"result": false, "message": "请填写全部配置信息"};
        }
        var detail = configDetailListGrid.getData();
        if (detail.length < 1) {
            return {"result": false, "message": "请填写配置信息"};
        } else {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].configMsg == "" || detail[i].configMsg == undefined) {
                    return {"result": false, "message": "请填写需求订单配置信息"};
                }
            }
        }
        return {"result": true};
    }

    function valYjy1() {
        var materielCode = $.trim(mini.get("materielCode").getValue());
        if (!materielCode) {
            return {"result": false, "message": "请填写整机物料号"};
        }
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": "请填写设计型号"};
        }
        var zxdChange = $.trim(mini.get("zxdChange").getValue());
        if (!zxdChange) {
            return {"result": false, "message": "请填写装箱单是否更改"};
        }
        var jstz = $.trim(mini.get("jstz").getValue());
        if (!jstz) {
            return {"result": false, "message": "请填写是否下发技术通知"};
        }
        var jsNum = $.trim(mini.get("jsNum").getValue());
        if (!jsNum && jstz == '是') {
            return {"result": false, "message": "请填写技术下发通知编号"};
        }
        var manualStatus = $.trim(mini.get("manualStatus").getValue());
        if (!manualStatus) {
            return {"result": false, "message": "请填写是否提交机型制作申请"};
        }
        var periodFile = $.trim(mini.get("periodFile").getValue());
        if (!periodFile) {
            return {"result": false, "message": "请填写是否上传长周期件明细"};
        }
        var sourceFile = $.trim(mini.get("sourceFile").getValue());
        if (!sourceFile) {
            return {"result": false, "message": "请填写是否编制/变更手册源文件"};
        }
        var detail = configDetailListGrid.getData();
        if (detail.length < 1) {
            return {"result": false, "message": "请填写配置信息"};
        } else {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].respId == "" || detail[i].respId == undefined) {
                    if (detail[i].configType == "" || detail[i].configType == undefined) {
                        return {"result": false, "message": "请填写类型"};
                    }
                    if(detail[i].configType=='无方案'){
                        if (detail[i].configMsg == "" || detail[i].configMsg == undefined) {
                            return {"result": false, "message": "请填写需求订单配置信息"};
                        }
                        if (detail[i].risk == "" || detail[i].risk == undefined) {
                            return {"result": false, "message": "请填写风险点"};
                        }
                        if (detail[i].control == "" || detail[i].control == undefined) {
                            return {"result": false, "message": "请填写控制措施"};
                        }
                        if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                            return {"result": false, "message": "请填写完成时间"};
                        }
                        if (detail[i].changeCode == "" || detail[i].changeCode == undefined) {
                            return {"result": false, "message": "请填写变化点"};
                        }
                    }
                }
            }

        }

        return {"result": true};
    }

    function valYjy2() {
        var detail = configDetailListGrid.getData();
        if (detail.length < 1) {
            return {"result": false, "message": "请填写配置信息"};
        } else {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].respId == currentUserId) {
                    if (detail[i].configMsg == "" || detail[i].configMsg == undefined) {
                        return {"result": false, "message": "请填写需求订单配置信息"};
                    }
                    if (detail[i].risk == "" || detail[i].risk == undefined) {
                        return {"result": false, "message": "请填写风险点"};
                    }
                    if (detail[i].control == "" || detail[i].control == undefined) {
                        return {"result": false, "message": "请填写控制措施"};
                    }
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写完成时间"};
                    }
                    if (detail[i].changeCode == "" || detail[i].changeCode == undefined) {
                        return {"result": false, "message": "请填写变化点"};
                    }
                    if (detail[i].configType == "" || detail[i].configType == undefined) {
                        return {"result": false, "message": "请填写类型"};
                    }
                }
            }
        }
        return {"result": true};
    }

    function valYjy3() {
        var detail = timeDetailListGrid.getData();
        {
            var jstz = $.trim(mini.get("jstz").getValue());
            var periodFile = $.trim(mini.get("periodFile").getValue());
            var sourceFile = $.trim(mini.get("periodFile").getValue());
            var zxdChange = $.trim(mini.get("zxdChange").getValue());
            var manualStatus = $.trim(mini.get("manualStatus").getValue());
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].timeId == 'czqjmx') {
                    if ((detail[i].finishTime == "" || detail[i].finishTime == undefined) && periodFile == '是') {
                        return {"result": false, "message": "请填写长周期件明细上传时间"};
                    }
                }
                if (detail[i].timeId == 'tzxf') {
                    if ((detail[i].finishTime == "" || detail[i].finishTime == undefined) && jstz == '是') {
                        return {"result": false, "message": "请填写技术通知下发完成时间"};
                    }
                }
                if (detail[i].timeId == 'scywj') {
                    if ((detail[i].finishTime == "" || detail[i].finishTime == undefined)&& sourceFile == '是') {
                        return {"result": false, "message": "请填写手册源文件编制/变更完成时间"};
                    }
                }
                if (detail[i].timeId == 'jxtc') {
                    if ((detail[i].finishTime == "" || detail[i].finishTime == undefined) && manualStatus == '是') {
                        return {"result": false, "message": "请填写机型图册完成时间"};
                    }
                }
                if (detail[i].timeId == 'zxd') {
                    if ((detail[i].finishTime == "" || detail[i].finishTime == undefined) && zxdChange == '是') {
                        return {"result": false, "message": "请填写装箱单完成时间"};
                    }
                }
            }
        }

        return {"result": true};
    }

    function valGyb() {
        var gybom = $.trim(mini.get("gybom").getValue());
        if (!gybom) {
            return {"result": false, "message": "请填写是否有工艺BOM"};
        }
        var detail = timeDetailListGrid.getData();
        {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].timeId == 'gybom') {
                    if ((detail[i].finishTime == "" || detail[i].finishTime == undefined) && gybom == '否') {
                        return {"result": false, "message": "请填写工艺BOM完成时间"};
                    }
                }
                if (detail[i].timeId == 'fygzsj') {
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写发运工装设计完成时间"};
                    }
                }
                if (detail[i].timeId == 'fygzcg') {
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写发运工装采购技术通知单完成时间"};
                    }
                }
                if (detail[i].timeId == 'fyfhzy') {
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写产品发运防护作业标准书完成时间"};
                    }
                }
                if (detail[i].respId == "" || detail[i].respId == undefined) {
                    return {"result": false, "message": "请填写责任人"};
                }
            }
        }
        return {"result": true};
    }

    function valFws() {
        var detail = timeDetailListGrid.getData();
        {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].timeId == 'ljtc') {
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写零件图册完成时间"};
                    }
                }
                if (detail[i].timeId == 'cbsc') {
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写操保手册完成时间"};
                    }
                }
                if (detail[i].respId == "" || detail[i].respId == undefined) {
                    return {"result": false, "message": "请填写责任人"};
                }
            }
        }
        return {"result": true};
    }

    function valZlb() {
        var detail = timeDetailListGrid.getData();
        {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].timeId == 'bztspz') {
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写编制特殊配置检验表完成时间"};
                    }
                }
                if (detail[i].timeId == 'kzyd') {
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写控制要点完成时间"};
                    }
                }
                if (detail[i].respId == "" || detail[i].respId == undefined) {
                    return {"result": false, "message": "请填写责任人"};
                }
            }
        }
        return {"result": true};
    }

    function valGfb() {
        var detail = timeDetailListGrid.getData();
        {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].timeId == 'xzwxj') {
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写新增外协件的布局完成时间"};
                    }
                }
                if (detail[i].timeId == 'qdgys') {
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写确定供应商完成时间"};
                    }
                }
                if (detail[i].respId == "" || detail[i].respId == undefined) {
                    return {"result": false, "message": "请填写责任人"};
                }
            }
        }
        return {"result": true};
    }

    function valCgb() {
        var detail = timeDetailListGrid.getData();
        {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].timeId == 'wgj') {
                    if ((detail[i].finishTime == "" || detail[i].finishTime == undefined) && detail[i].respId == currentUserId) {
                        return {"result": false, "message": "请填写外购件完成时间"};
                    }
                }
                if (detail[i].timeId == 'wxj') {
                    if ((detail[i].finishTime == "" || detail[i].finishTime == undefined) && detail[i].respId == currentUserId) {
                        return {"result": false, "message": "请填写外协件完成时间"};
                    }
                }
                if (detail[i].timeId == 'jkj') {
                    if ((detail[i].finishTime == "" || detail[i].finishTime == undefined) && detail[i].respId == currentUserId) {
                        return {"result": false, "message": "请填写进口件完成时间"};
                    }
                }
                if (detail[i].respId == "" || detail[i].respId == undefined) {
                    return {"result": false, "message": "请填写责任人"};
                }
            }
        }
        return {"result": true};
    }

    function valZzb1() {
        var detail = timeDetailListGrid.getData();
        {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].timeId == 'ddxf') {
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写订单下发完成时间"};
                    }
                    if (detail[i].respId == "" || detail[i].respId == undefined) {
                        return {"result": false, "message": "请填写责任人"};
                    }
                }
            }
        }
        return {"result": true};
    }

    function valZzb2() {
        var detail = timeDetailListGrid.getData();
        {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].timeId == 'sx') {
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写上线完成时间"};
                    }
                }
                if (detail[i].timeId == 'rk') {
                    if (detail[i].finishTime == "" || detail[i].finishTime == undefined) {
                        return {"result": false, "message": "请填写入库完成时间"};
                    }
                }
                if (detail[i].respId == "" || detail[i].respId == undefined) {
                    return {"result": false, "message": "请填写责任人"};
                }
            }
        }
        return {"result": true};
    }

    function valFgld() {
        if (currentUserRoles.indexOf("FGLD") > -1) {
            var gzOption = $.trim(mini.get("gzOption").getValue());
            if (!gzOption) {
                return {"result": false, "message": "请填写研发领导意见"};
            }
            var gzReason = $.trim(mini.get("gzReason").getValue());
            if (!gzReason && gzOption == '不同意') {
                return {"result": false, "message": "请填写不同意原因"};
            }
        }
        if (currentUserRoles.indexOf("GYFGLD") > -1) {
            var yzOption = $.trim(mini.get("yzOption").getValue());
            if (!yzOption) {
                return {"result": false, "message": "请填写工艺领导意见"};
            }
            var yzReason = $.trim(mini.get("yzReason").getValue());
            if (!yzReason && yzOption == '不同意') {
                return {"result": false, "message": "请填写不同意原因"};
            }
        }
        return {"result": true};
    }

    function saveModel() {
        if (stageName == 'start' || action == 'add' || action == 'edit') {
            var formValfirstId = valHwxs();
            if (!formValfirstId.result) {
                mini.alert(formValfirstId.message);
                return;
            }
        }
        if (stageName == 'onejsfa' || stageName == 'twojsfa' || stageName == 'threejsfa') {
            var formValfirstId = valYjy1();
            if (!formValfirstId.result) {
                mini.alert(formValfirstId.message);
                return;
            }
        }
        if (stageName == 'oneddfx' || stageName == 'twoddfx' || stageName == 'threeddfx') {
            var formValfirstId = valYjy2();
            if (!formValfirstId.result) {
                mini.alert(formValfirstId.message);
                return;
            }
        }
        if (stageName == 'onecpzgtime' || stageName == 'twocpzgtime' || stageName == 'threecpzgtime') {
            var formValfirstId = valYjy3();
            if (!formValfirstId.result) {
                mini.alert(formValfirstId.message);
                return;
            }
        }
        if (stageName == 'onefyfa' || stageName == 'twofyfa' || stageName == 'threefyfa') {
            var formValfirstId = valGyb();
            if (!formValfirstId.result) {
                mini.alert(formValfirstId.message);
                return;
            }
        }
        if (stageName == 'oneddxf' || stageName == 'twoddxf' || stageName == 'threeddxf') {
            var formValfirstId = valZzb1();
            if (!formValfirstId.result) {
                mini.alert(formValfirstId.message);
                return;
            }
        }
        if (stageName == 'onexlbjgy' || stageName == 'twoxlbjgy' || stageName == 'threexlbjgy') {
            var formValfirstId = valGfb();
            if (!formValfirstId.result) {
                mini.alert(formValfirstId.message);
                return;
            }
        }
        if (stageName == 'onecg' || stageName == 'twocg' || stageName == 'threecg') {
            var formValfirstId = valCgb();
            if (!formValfirstId.result) {
                mini.alert(formValfirstId.message);
                return;
            }
        }
        if (stageName == 'onesxrk' || stageName == 'twosxrk' || stageName == 'threesxrk') {
            var formValfirstId = valZzb2();
            if (!formValfirstId.result) {
                mini.alert(formValfirstId.message);
                return;
            }
        }
        if (stageName == 'onecblj' || stageName == 'twocblj' || stageName == 'threecblj') {
            var formValfirstId = valFws();
            if (!formValfirstId.result) {
                mini.alert(formValfirstId.message);
                return;
            }
        }
        if (stageName == 'oneydbk' || stageName == 'twoydbk' || stageName == 'threeydbk') {
            var formValfirstId = valZlb();
            if (!formValfirstId.result) {
                mini.alert(formValfirstId.message);
                return;
            }
        }
        if (stageName == 'fgld') {
            var formValfirstId = valFgld();
            if (!formValfirstId.result) {
                mini.alert(formValfirstId.message);
                return;
            }
        }
        var formData = _GetFormJsonMini("formModelDetail");
        formData.time = timeDetailListGrid.getChanges();
        formData.config = configDetailListGrid.getChanges();
        $.ajax({
            url: jsUseCtxPath + '/onlineReview/core/saveModel.do?belongId=' + belongId,
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnObj) {
                if (returnObj) {
                    var message = "";
                    if (returnObj.success) {
                        message = "数据保存成功";
                        mini.alert(message, "提示信息", function () {
                            window.location.href = jsUseCtxPath + "/onlineReview/core/modelEditPage.do?" +
                                "action=" + action + "&id=" + returnObj.data + "&stageName=" + stageName + "&status=" + status;
                        });
                    } else {
                        message = "数据保存失败，" + returnObj.message;
                        mini.alert(message, "提示信息");
                    }
                }
            }
        });
    }

    function addTimeDetail() {
        var formId = mini.get("id").getValue();
        if (!formId) {
            mini.alert("请先点击‘保存’进行表单创建!");
            return;
        } else {
            var row = {};
            timeDetailListGrid.addRow(row);
        }
    }


    function removeTimeDetail() {
        var selecteds = timeDetailListGrid.getSelecteds();
        if (selecteds.length <= 0) {
            mini.alert("请选择一条记录");
            return;
        }
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        timeDetailListGrid.removeRows(deleteArr);
    }

    function addConfigDetail() {
        var row = {};
        configDetailListGrid.addRow(row);
    }


    function removeConfigDetail() {
        var selecteds = configDetailListGrid.getSelecteds();
        if (selecteds.length <= 0) {
            mini.alert("请选择一条记录");
            return;
        }
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        configDetailListGrid.removeRows(deleteArr);
    }

    // function getModel() {
    //     var materielCode = mini.get("materielCode").getValue();
    //     if (!materielCode) {
    //         mini.get("designModel").setValue('');
    //         mini.get("saleModel").setValue('');
    //         mini.get("jszg").setValue('');
    //         mini.get("jszg").setText('');
    //         return;
    //     }
    //     $.ajax({
    //         url: jsUseCtxPath + '/onlineReview/core/getModel.do?materielCode=' + materielCode,
    //         type: 'get',
    //         async: false,
    //         contentType: 'application/json',
    //         success: function (data) {
    //             mini.get("designModel").setValue(data.designmodel);
    //             mini.get("saleModel").setValue(data.salemodel);
    //             mini.get("jszg").setValue(data.productManagerId);
    //             mini.get("jszg").setText(data.productManagerName);
    //         }
    //     });
    // }

    function selectModelClick() {
        selectModelWindow.show();
        searchModel();
    }

    function searchModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("selectdesignModel").getValue());
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
        var saleModel = $.trim(mini.get("selectsaleModel").getValue());
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
            mini.get("designModel").setValue(rowSelected.id);
            mini.get("designModel").setText(rowSelected.designModel);
            mini.get("materielCode").setValue(rowSelected.materialCode);
            mini.get("saleModel").setValue(rowSelected.saleModel);
            selectModelHide();
        }
    }

    function selectModelHide() {
        selectModelWindow.hide();
        mini.get("selectsaleModel").setValue('');
        mini.get("productManagerName").setValue('');
        mini.get("departName").setValue('');
        mini.get("selectdesignModel").setValue('');
        selectModelListGrid.clearSelect();
    }

    function onRelModelCloseClick() {
        mini.get("designModel").setValue('');
        mini.get("designModel").setText('');
        mini.get("materielCode").setValue('');
        mini.get("saleModel").setValue('');
        selectModelListGrid.clearSelect();
    }

    function fileupload(fileType) {
        var id = mini.get("id").getValue();
        if (!id) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/onlineReview/core/openUploadWindow.do?fileType="+fileType+"&id=" + id,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (gzFileListGrid) {
                    gzFileListGrid.load();
                }
                if (czqFileListGrid) {
                    czqFileListGrid.load();
                }
            }
        });
    }

    function operationRendererF(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnOnlineReviewPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadOnlineReviewFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')">下载</span>';
        //增加删除按钮
        if (record.CREATE_BY_ == currentUserId && action != 'detail') {
            var deleteUrl = "/onlineReview/core/deleteOnlineReviewFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    
    function returnOnlineReviewPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/onlineReview/core/onlineReviewPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/onlineReview/core/onlineReviewOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/onlineReview/core/onlineReviewImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    function downLoadOnlineReviewFile(fileName, fileId, formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/onlineReview/core/onlineReviewPdfPreview.do?action=download');
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputstandardId = $("<input>");
        inputstandardId.attr("type", "hidden");
        inputstandardId.attr("name", "formId");
        inputstandardId.attr("value", formId);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputstandardId);
        form.append(inputFileId);
        form.submit();
        form.remove();
    }


    function deleteFile(fileName, fileId, formId, urlValue) {
        mini.confirm("确定删除？", "确定？",
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
                            if (gzFileListGrid) {
                                gzFileListGrid.load();
                            }
                            if (czqFileListGrid) {
                                czqFileListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }

    function exportConfigDetail() {
        var id=mini.get("id").getValue();
        if(!id) {
            mini.alert('申请单尚未创建！');
            return;
        }
            var form = $("<form>");
            form.attr("style", "display:none");
            form.attr("target", "");
            form.attr("method", "post");
            form.attr("action", jsUseCtxPath + "/materielExtend/apply/exportMateriels.do?id="+id);
            $("body").append(form);
            form.submit();
            form.remove();
    }
</script>
</body>
</html>
