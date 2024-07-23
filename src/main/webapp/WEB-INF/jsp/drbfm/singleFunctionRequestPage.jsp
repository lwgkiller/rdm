<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>功能&要求描述管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        .mini-grid-rows-view {
            background: white !important;
        }

        .mini-grid-cell-inner {
            line-height: 25px !important;
            padding: 0;
        }

        .mini-grid-cell-inner {
            font-size: 14px !important;
        }
    </style>
</head>
<body>
<div class="mini-fit" style="width: 100%; height: 100%;background: #fff">
    <p style="font-size: 16px;font-weight: bold;margin-top: 5px">功能描述</p>
    <hr>
    <div id="functionToolBar" class="topToolBar">
        <div style="position: relative!important;">
            <a class="mini-button" id="addFunction" plain="true" style="float: left;display: none"
               onclick="addFunction()">添加</a>
            <a class="mini-button btn-red" id="delFunction" plain="true" style="float: left;display: none"
               onclick="removeFunction()">删除</a>
            <a class="mini-button" style="float: left" plain="true" id="exFunction"
               onclick="exportFunction()">导出</a>
        </div>
    </div>
    <div id="functionListGrid" class="mini-datagrid" allowResize="false" style="height: 30%" autoload="true"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
         allowCellWrap="false" showVGridLines="true"
         url="${ctxPath}/drbfm/single/getFunctionList.do?belongSingleId=${singleId}">
        <div property="columns">
            <div type="checkcolumn" width="30px"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="50px">序号</div>
            <div name="action" cellCls="actionIcons" width="100px" headerAlign="center" align="center"
                 renderer="onFunctionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="functionDesc" headerAlign="center" width="600px" align="left">功能描述<span
                    style="color:red">*</span>
            </div>
            <div field="relDimensionNames" headerAlign="center" width="200px" align="center">所属维度<span
                    style="color:red">*</span>
            </div>
            <div field="demandDesc" headerAlign="center" width="400px" align="left">关联部门需求描述
            </div>
            <div field="structName" headerAlign="center" width="150px" align="left">接口名称
            </div>
        </div>
    </div>
    <p style="font-size: 16px;font-weight: bold;margin-top: 5px">特性要求（必填）</p>
    <hr>

        <div class="mini-toolbar" >

            <div class="searchBox" >
                <form id="searchForm" class="search-form" >
                    <ul >
                        <li id="requestToolBar" >
                                <a class="mini-button" id="addDemand" plain="true" style="margin-right: 5px;display: none"
                                   onclick="addRequest()">添加</a>
                                <a class="mini-button btn-red" id="delDemand" plain="true" style="margin-right: 5px;display: none"
                                   onclick="removeRequest()">删除</a>
                                <a class="mini-button"  plain="true" id="exDemand" style="margin-right: 5px"
                                   onclick="exportRequest()">导出</a>
                            <div style="display: inline-block" class="separator"></div>
                        </li>
                            <span class="text" style="width:auto">关联功能描述: </span>
                            <%--<input id="relFunctionDesc"--%>
                                   <%--style="width:300px"--%>
                                   <%--name="relFunctionDesc" class="mini-textbox"--%>
                                   <%--url="${ctxPath}/drbfm/single/getSingleSxmsListById.do?partId=${singleId}"--%>
                            <%--/>--%>
                            <input id="relFunctionDesc" name="relFunctionDesc" class="mini-combobox rxc" plugins="mini-combobox"
                                   style="width:250px;height:34px" popupWidth="400" label="失效模式："
                                   length="50"
                                   only_read="false" required="true" allowinput="false" mwidth="100"
                                   wunit="%" mheight="34" hunit="px" shownullitem="false" multiSelect="false"
                                   textField="functionDesc" valueField="functionDesc" emptyText="请选择..."
                                   url="${ctxPath}/drbfm/single/getFunctionListByPartId.do?partId=${singleId}"
                                   nullitemtext="请选择..." emptytext="请选择..."/>


                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">特性要求: </span>
                            <input id="requestDescri" style="width:auto" name="requestDescri" class="mini-textbox" style="width:400px"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">是否风险分析: </span>
                            <input id="riskAnalysis" style="width:80px" name="riskAnalysis" class="mini-textbox"/>
                        </li>

                        <li style="margin-left: 10px">
                            <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                            <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                        </li>

                    </ul>

                </form>
            </div>
        </div>
    <div id="requestListGrid" class="mini-datagrid" allowResize="false" style="height: 46%"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
         allowCellWrap="false" showVGridLines="true" autoload="true"
         url="${ctxPath}/drbfm/single/getRequestList.do?belongSingleId=${singleId}">
        <div property="columns">
            <div type="checkcolumn" width="30px"></div>
            <div type="indexcolumn" align="center" width="50px">序号</div>
            <div name="action" cellCls="actionIcons" width="100px" headerAlign="center" align="center"
                 renderer="onRequestRenderer" cellStyle="padding:0;">操作
            </div>
            <div renderer="riskAnalysisRenderer" field="riskAnalysis" headerAlign="center" width="100px" align="center">
                是否风险分析<span style="color:red">*</span>
            </div>
            <div renderer="requestDescRenderer" field="requestDesc" headerAlign="center" width="750px" align="left"
            >特性要求<span style="color:red">*</span>
            </div>
            <div field="compareToJP" headerAlign="center" width="500px" align="left"
            >与竞品对比
            </div>
            <div field="relFunctionDesc" headerAlign="center" width="400px" align="left"
            >关联功能描述
            </div>
            <div field="relDimensionNames" headerAlign="center" width="200px" align="center">所属维度
            </div>
            <div field="structName" headerAlign="center" width="150px" align="left">接口名称
            </div>
        </div>
    </div>
</div>

<div id="functionWindow" title="功能编辑" class="mini-window" style="width:660px;height:380px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar" style="float: right;">
            <div style="position: relative!important;">
                <a id="saveFunction" class="mini-button" onclick="saveFunction()">保存</a>
                <a id="closeFunction" class="mini-button btn-red" onclick="closeFunction()">关闭</a>
            </div>
        </div>
        <input id="functionId" name="id" class="mini-hidden"/>
        <table class="table-detail" cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="width: 15%">功能描述(500字以内)：<span style="color:red">*</span></td>
                <td style="width: 85%;">
                    <input id="functionDesc" name="functionDesc" class="mini-textarea"
                           plugins="mini-textarea" style="width:95%;;height:120px;line-height:25px;" label="功能描述"
                           allowinput="true" emptytext="功能描述了项目/系统元素的预期用途。
例如：传输电力、控制流动、控制速度、传递热量等"/>
                </td>
            </tr>
            <tr>
                <td style="width: 15%;">所属维度：<span style="color:red">*</span></td>
                <td style="width: 85%;">
                    <input id="relDimensionKeys" name="relDimensionKeys" class="mini-combobox" style="width:98%;"
                           textField="text" valueField="key_" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="false" multiSelect="true"
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=SSWD"/>
                </td>
            </tr>
            <tr>
                <td style="width: 15%">关联的部门需求：</td>
                <td style="width: 35%;">
                    <input id="relDeptDemandId" style="width:98%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="onRelDemandCloseClick()"
                           name="relDeptDemandId" textname="relDeptDemandDesc" allowInput="false"
                           onbuttonclick="selectDemandClick()"/>
                </td>
            </tr>
            <tr>
                <td style="width: 15%">接口名称：</td>
                <td style="width: 35%;">
                    <input id="interfaceRequestStructId" style="width:98%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="onRequestStructCloseClick()"
                           name="interfaceRequestStructId" textname="structName" allowInput="true"
                           onbuttonclick="selectRequestStructClick()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="selectDemandWindow" title="选择部门需求" class="mini-window" style="width:750px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">部门需求描述: </span><input
            class="mini-textbox" style="width: 120px" id="demandDesc" name="demandDesc"/>
        <span class="text" style="width:auto">需求部门: </span><input
            class="mini-textbox" style="width: 120px" id="deptNames" name="deptNames"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchDemand()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="selectDemandListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false"
             allowAlternating="true" showPager="false"
             url="${ctxPath}/drbfm/single/getSingleDeptDemandList.do?belongSingleId=${singleId}">
            <div property="columns">
                <div type="checkcolumn" width="10px"></div>
                <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
                <div field="demandDesc" headerAlign="center" width="90px" align="left">部门需求描述
                </div>
                <div field="deptIds" displayField="deptNames" headerAlign="center" align="center" width="50px">需求部门
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectDemandOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectDemandHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="requestWindow" title="要求编辑" class="mini-window" style="width:1440px;height:750px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div style="float: right;margin-right: 20px">
        <span style="color: #ff0000">(如一次填写不完可先保存，但流程提交前会进行完整性校验)</span>
        <a id="saveRequest" class="mini-button" onclick="saveRequest()">保存</a>
        <a id="closeRequest" class="mini-button btn-red" onclick="closeRequest()">关闭</a>
    </div>
    <div style="clear: both"></div>
    <div class="mini-fit">
        <input id="requestId" name="id" class="mini-hidden"/>
        <table class="table-detail" cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="width: 8%">关联的功能：</td>
                <td style="width: 92%;">
                    <input id="relFunctionId" style="width:99%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="onRelFunctionCloseClick()"
                           name="relFunctionId" textname="relFunctionDesc" allowInput="false"
                           onbuttonclick="selectFunctionClick()"/>
                </td>
            </tr>
            <tr>
                <td style="width: 8%;">所属维度：</td>
                <td style="width: 92%;">
                    <input id="relDimensionKeysR" name="relDimensionKeys" class="mini-combobox" style="width:98%;"
                           textField="text" valueField="key_" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="false" multiSelect="true"
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=SSWD"/>
                </td>
            </tr>

            <tr>
                <td style="width: 8%">特性要求(500字以内)：<span style="color:red">*</span></td>
                <td style="width: 92%;" colspan="3">
                    <input id="requestDesc" name="requestDesc" class="mini-textarea"
                           plugins="mini-textarea" style="width:99%;;height:60px;line-height:25px;" label="特性要求"
                           allowinput="true" emptytext="要求是与功能的性能相关的可测量的产品特性，产品特性指产品的显著特征（或可量化的属性）。
要求是功能的量化或细化。"/>
                </td>
            </tr>



            <tr>
                <td style="width: 8%">相较对比产品，与该特性要<br>求（失效模式是否发生）相<br>关的外部/接口/下级件（特<br>性）的变化点：</td>

                <td>
                    <div class="mini-toolbar" style="margin-bottom: 5px" id="riskListToolBar">
                        <a class="mini-button" id="addRiskRow" plain="true"
                           onclick="addRiskRow">新增行</a>
                        <a class="mini-button btn-red" id="removeRiskRow" plain="true"
                           onclick="removeRiskRow">删除</a>
                         （1、风险分析类型为“风险分析件”的必填。2、无变化点填“无”，并选择对比机型。）
                    </div>
                    <div id="riskGrid" class="mini-datagrid" allowResize="false" style="height:200px"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                         allowCellWrap="true" showVGridLines="true" autoload="true"

                    >
                        <div property="columns">
                            <div type="checkcolumn" width="20px"></div>

                            <div field="designModelName" headerAlign="center" width="80px" align="center"
                            >对比机型
                                <input name="designModel"  textname="designModelName" style="width:98%;" property="editor"
                                       class="mini-buttonedit" showClose="true"
                                       allowInput="false"
                                       onbuttonclick="selectDesignModel()"/>
                            </div>
                            <div field="changeDimension" headerAlign="center" width="80px" align="center"
                            >变化维度
                                <input textname="changeDimension" style="width:98%;" property="editor"
                                       class="mini-buttonedit" showClose="true"
                                       allowInput="false"
                                       onbuttonclick="selectChangeDimension()"/>
                            </div>
                            <div field="changeDetail" headerAlign="center" width="170px" align="center"
                            >变化点
                                <%--<input property="editor" class="mini-textbox"/>--%>
                                <input property="editor" id="changeDetail" name="changeDetail" class="mini-combobox" style="width:98%;"
                                       textField="value" valueField="key"
                                       required="false" allowInput="true"
                                       data="[{'key' : '无','value' : '无'}
                                       ]"
                                />
                            </div>

                        </div>
                    </div>
                </td>

            </tr>



            <tr>
                <td style="width: 8%;">与竞品对比(500字以内)：</td>
                <td style="width: 92%;" colspan="3">
                    <input id="compareToJP" name="compareToJP" class="mini-textarea"
                           plugins="mini-textarea" style="width:99%;;height:100px;line-height:25px;" label="与竞品对比"
                           allowinput="true"
                           emptytext="针对该特性要求，与竞品进行优劣势对比，明确竞品产品型号，其他挖机厂家无此产品则填“行业无此竞品”"/>
                </td>
            </tr>



            <tr>
                <td style="width: 15%">失效模式列表：</td>

                <td>
                    <div class="mini-toolbar" style="margin-bottom: 5px" id="tagListToolBar">
                        <a class="mini-button" id="addSxmsRow" plain="true"
                           onclick="addSxmsRow">新增行</a>
                        <a class="mini-button btn-red" id="removeSxmsRow" plain="true"
                           onclick="removeSxmsRow">删除</a>
                    </div>
                    <div id="sxmsGrid" class="mini-datagrid" allowResize="false" style="height:400px"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                         allowCellWrap="true" showVGridLines="true" autoload="true"
                    >
                        <div property="columns">
                            <div type="checkcolumn" width="10px"></div>
                            <div type="indexcolumn" headerAlign="center" width="15px">序号</div>
                            <div field="sxmsName" headerAlign="center" width="200px" align="left"
                            >失效模式
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="needFxfx" headerAlign="center" width="45px" align="center"
                            >是否风险分析
                                <input property="editor" id="needFxfx" name="needFxfx" class="mini-combobox" style="width:98%;"
                                       textField="value" valueField="key"
                                       required="false" allowInput="false"
                                       data="[{'key' : '是','value' : '是'}
                                       ,{'key' : '否','value' : '否'}
                                       ]"
                                />
                            </div>
                        </div>
                    </div>
                </td>

            </tr>
            <tr>
                <td style="text-align: left;height: 200px">附件：</td>
                <td colspan="3">
                    <div class="mini-toolbar" style="margin-bottom: 5px" id="fileListToolBar">
                        <a id="addFile" class="mini-button" onclick="addrequestFile()">添加附件</a>
                        <span style="color: red">注：添加附件前，请先点击“保存”</span>
                    </div>
                    <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                         allowResize="false"
                         idField="id"
                         showPager="false" showColumnsMenu="false" allowAlternating="true">
                        <div property="columns">
                            <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                            <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                            <div field="fileDesc" width="80" headerAlign="center" align="center">附件描述</div>
                            <div field="creator" width="70" headerAlign="center" align="center">创建人</div>
                            <div field="CREATE_TIME_" width="90" headerAlign="center" align="center"
                                 dateFormat="yyyy-MM-dd HH:mm:ss">创建时间
                            </div>
                            <div field="action" width="100" headerAlign='center' align="center"
                                 renderer="operationRenderer">
                                操作
                            </div>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td style="width: 15%">接口名称：</td>
                <td style="width: 35%;">
                    <input id="interfaceRequestStructId_R" style="width:98%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="onRequestStructCloseClick_R()"
                           name="interfaceRequestStructId" textname="structName" allowInput="false"
                           onbuttonclick="selectRequestStructClick_R()"/>
                </td>
            </tr>

        </table>
    </div>
</div>

<div id="selectFunctionWindow" title="选择功能描述" class="mini-window" style="width:1050px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">功能描述: </span><input
            class="mini-textbox" style="width: 120px" id="functionDesc2" name="functionDesc2"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchFunction()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="selectFunctionListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false"
             allowAlternating="true" showPager="false"
             url="${ctxPath}/drbfm/single/getFunctionList.do?belongSingleId=${singleId}">
            <div property="columns">
                <div type="checkcolumn" width="20px"></div>
                <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
                <div field="functionDesc" headerAlign="center" width="290px" align="left">功能描述
                </div>
                <div field="relDimensionNames" headerAlign="center" width="160px" align="left">所属维度
                </div>
                <div field="demandDesc" headerAlign="center" width="180px" align="left">关联部门需求描述
                </div>
                <div field="structName" headerAlign="center" width="140px" align="center">接口名称
                </div>
                <div field="interfaceRequestStructId" visible="false"></div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 40px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectFunctionOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消"
                           onclick="selectFunctionHide    ()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<%--关联变化维度弹窗--%>
<div id="selectDimensionWindow" title="选择变化维度" class="mini-window" style="width:1050px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">维度类型: </span><input
            class="mini-textbox" style="width: 120px" id="dimensionType" name="dimensionType"/>
        <span class="text" style="width:auto">变化维度: </span><input
            class="mini-textbox" style="width: 120px" id="changeDimension" name="changeDimension"/>

        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchChangeDimension()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="dimensionListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false"
             allowAlternating="true" showPager="false"
             autoload="true"
             url="${ctxPath}/drbfm/single/getChangeDimensionList.do">
            <div property="columns">
                <div type="checkcolumn" width="20px"></div>
                <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
                <div field="dimensionType" headerAlign="center" width="200px" align="left">维度类型
                </div>
                <div field="changeDimension" headerAlign="center" width="200px" align="left">变化维度
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 40px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectDimensionOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消"
                           onclick="selectDimensionHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>


<%--关联产品型谱弹窗--%>
<div id="spectrumWindow" title="选择产品型号"
     class="mini-window" style="width:850px;height:600px;"
     showModal="true" showFooter="true" allowResize="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">设计型号: </span>
        <input class="mini-textbox" width="130" id="searchDesignModel" textField="designModel" valueField="designModel"
               style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchDesignModel()">查询</a>
    </div>

    <div class="mini-fit">
        <div id="spectrumListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
             allowResize="false" allowCellWrap="false" showCellTip="true" idField="id"
             allowCellEdit="true" allowCellSelect="true" multiSelect="false" showColumnsMenu="false"
             sizeList="[50,100,200,500]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
             allowSortColumn="true" allowUnselect="true" autoLoad="false"
             url="${ctxPath}/world/core/productSpectrum/applyList.do">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.wlh"/>
                </div>
                <div field="designModel" width="150" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.sjxh"/>
                </div>
                <div field="departName" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.cps"/>
                </div>
                <div field="productManagerName" width="80" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.cpzg"/>
                </div>
                <div field="saleModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.xsxh"/>
                </div>
                <div field="dischargeStage" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.pfjd"/>
                </div>
                <div field="rdStatus" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.yfzt"/>
                </div>
                <div field="yfztqr" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true">研发状态确认时间
                </div>
                <div field="productNotes" width="180" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.cpsm"/>
                </div>
                <div field="tagNames" width="240" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.cpbq"/>
                </div>
                <div field="manuStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.zzzt"/>
                </div>
                <div field="zzztqr" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true">制造状态确认时间
                </div>
                <div field="saleStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.xszt"/>
                </div>
                <div field="xsztqr" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true">销售状态确认时间
                </div>
                <div field="abroad" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.ghnwxs"/>
                </div>
                <div field="region" width="180" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.ghxsqyhgj"/>
                </div>
                <div field="taskName" headerAlign='center' align='center' width="80">当前任务</div>
                <div field="allTaskUserNames" headerAlign='center' align='center' width="120">当前处理人</div>

            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                 allowSort="true">创建时间
            </div>

        </div>

    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px"
                           value="确定" onclick="okWindow()"/>
                    <input type="button" style="height: 25px;width: 70px"
                           value="取消"
                           onclick="hideWindow()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" method="post" target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var singleId = "${singleId}";
    var action = "${action}";
    var stageName = "${stageName}";
    var currentUserId = "${currentUserId}";

    var functionListGrid = mini.get("functionListGrid");
    var functionWindow = mini.get("functionWindow");

    var selectDemandWindow = mini.get("selectDemandWindow");
    var selectDemandListGrid = mini.get("selectDemandListGrid");
    var dimensionListGrid = mini.get("dimensionListGrid");


    var requestListGrid = mini.get("requestListGrid");
    var requestWindow = mini.get("requestWindow");
    var spectrumWindow = mini.get("spectrumWindow");
    var fileListGrid= mini.get("fileListGrid");
    var selectFunctionWindow = mini.get("selectFunctionWindow");
    var selectFunctionListGrid = mini.get("selectFunctionListGrid");
    var selectDimensionWindow = mini.get("selectDimensionWindow");


    var sxmsGrid = mini.get("sxmsGrid");
    var riskGrid = mini.get("riskGrid");
    var spectrumListGrid = mini.get("spectrumListGrid");
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    function riskLevelRenderer(e) {
        var record = e.record;
        var riskLevel = record.riskLevel;

        var arr = [{'key': '高风险', 'value': '高风险', 'css': 'red'},
            {'key': '中风险', 'value': '中风险', 'css': 'orange'},
            {'key': '低风险', 'value': '低风险', 'css': 'green'}
        ];

        return $.formatItemValue(arr, riskLevel);
    }

    function riskAnalysisRenderer(e) {
        var record = e.record;
        var riskAnalysis = record.riskAnalysis;

        var arr = [{'key': '否', 'value': '否', 'css': 'green'},
            // {'key': '中风险', 'value': '中风险', 'css': 'orange'},
            {'key': '是', 'value': '是', 'css': 'red'}
        ];

        return $.formatItemValue(arr, riskAnalysis);
    }

    $(function () {
        if (action=='edit'||(action == 'task' && stageName == 'bjfzrfxfx')) {
            // $("#requestToolBar").show();
            $("#addDemand").show();
            $("#delDemand").show();
            // $("#functionToolBar").show();
            $("#addFunction").show();
            $("#copyFunction").show();
            $("#delFunction").show();
        }
    });

    //行功能按钮
    function onFunctionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var flag = true;
        if (record.hasOwnProperty("belongCollectFlowId") && record.belongCollectFlowId) {
            flag = false;
        }
        var s = '';
        s += '<span  style="color:#2ca9f6;cursor: pointer" title="查看" onclick="detailFunction(\'' + id + '\')">查看</span>';
        if ((action == 'edit'||(action == 'task' && stageName == 'bjfzrfxfx')) && flag) {
            s += '<span style="display: inline-block" class="separator"></span>';
            s += '<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editFunction(\'' + id + '\')">编辑</span>';
        }
        return s;
    }

    //行功能按钮
    function onRequestRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var flag = true;
        if (record.hasOwnProperty("belongCollectFlowId") && record.belongCollectFlowId) {
            flag = false;
        }
        var s = '';
        s += '<span  style="color:#2ca9f6;cursor: pointer" title="查看" onclick="detailRequest(\'' + id + '\')">查看</span>';
        if ((action=="edit"||(action == 'task' && stageName == 'bjfzrfxfx')) && flag) {
            s += '<span style="display: inline-block" class="separator"></span>';
            s += '<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editRequest(\'' + id + '\')">编辑</span>';
        }
        return s;
    }

    function requestDescRenderer(e) {
        var record = e.record;
        var isRelationChange = record.isRelChange;
        var requestDesc = record.requestDesc;
        if (isRelationChange == 'yes') {
            return "<span style='color: red;font-weight: 900;'>" + requestDesc + "</span>";
        } else {
            return "<span style='color: black;'>" + requestDesc + "</span>";
        }
    }

    //功能描述
    function addFunction() {
        mini.get("interfaceRequestStructId").setEnabled(true);
        mini.get("relDimensionKeys").setEnabled(true);
        mini.get("relDeptDemandId").setEnabled(true);
        mini.get("functionDesc").setEnabled(true);
        mini.get("saveFunction").show();
        functionWindow.show();

    }

    function validFunction() {
        var functionDesc = $.trim(mini.get("functionDesc").getValue())
        if (!functionDesc) {
            return {"result": false, "message": "请填写功能描述"};
        }
        var relDimensionKeys = $.trim(mini.get("relDimensionKeys").getValue())
        if (!relDimensionKeys) {
            return {"result": false, "message": "请选择所属维度"};
        }
        return {"result": true};
    }

    function saveFunction() {
        var formValid = validFunction();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var functionDesc = mini.get("functionDesc").getValue();
        var relDimensionKeys = mini.get("relDimensionKeys").getValue();
        var relDimensionNames = mini.get("relDimensionKeys").getText();
        var id = mini.get("functionId").getValue();
        var relDeptDemandId = mini.get("relDeptDemandId").getValue();
        var interfaceRequestStructId = mini.get("interfaceRequestStructId").getValue();
        var data = {
            id: id, functionDesc: functionDesc, relDimensionKeys: relDimensionKeys,
            relDimensionNames: relDimensionNames, belongSingleId: singleId, relDeptDemandId: relDeptDemandId,
            interfaceRequestStructId: interfaceRequestStructId
        };
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/drbfm/single/saveFunction.do',
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            functionListGrid.reload();
                            requestListGrid.reload();
                            closeFunction();
                        }
                    });
                }
            }
        });
    }

    function detailFunction(id) {
        functionWindow.show();
        if (id) {
            var url = jsUseCtxPath + "/drbfm/single/getFunctionDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: id},
                function (json) {
                    mini.get("functionDesc").setValue(json.functionDesc);
                    mini.get("relDeptDemandId").setValue(json.relDeptDemandId);
                    mini.get("relDeptDemandId").setText(json.demandDesc);
                    mini.get("functionId").setValue(json.id);
                    mini.get("relDimensionKeys").setValue(json.relDimensionKeys);
                    mini.get("relDimensionKeys").setText(json.relDimensionNames);
                    mini.get("interfaceRequestStructId").setValue(json.interfaceRequestStructId);
                    mini.get("interfaceRequestStructId").setText(json.structName);
                });
            $.ajaxSettings.async = true;
        }

        mini.get("interfaceRequestStructId").setEnabled(false);
        mini.get("relDimensionKeys").setEnabled(false);
        mini.get("relDeptDemandId").setEnabled(false);
        mini.get("functionDesc").setEnabled(false);
        mini.get("saveFunction").hide();

    }

    function editFunction(id) {
        functionWindow.show();
        if (id) {
            var url = jsUseCtxPath + "/drbfm/single/getFunctionDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: id},
                function (json) {
                    mini.get("functionDesc").setValue(json.functionDesc);
                    mini.get("relDeptDemandId").setValue(json.relDeptDemandId);
                    mini.get("relDeptDemandId").setText(json.demandDesc);
                    mini.get("functionId").setValue(json.id);
                    mini.get("relDimensionKeys").setValue(json.relDimensionKeys);
                    mini.get("relDimensionKeys").setText(json.relDimensionNames);
                    mini.get("interfaceRequestStructId").setValue(json.interfaceRequestStructId);
                    mini.get("interfaceRequestStructId").setText(json.structName);

                });
            $.ajaxSettings.async = true;
        }
        mini.get("interfaceRequestStructId").setEnabled(true);
        mini.get("relDimensionKeys").setEnabled(true);
        mini.get("relDeptDemandId").setEnabled(true);
        mini.get("functionDesc").setEnabled(true);
        mini.get("saveFunction").show();
    }

    function closeFunction() {
        mini.get("functionDesc").setValue('');
        mini.get("functionId").setValue('');
        mini.get("relDimensionKeys").setValue('');
        mini.get("relDimensionKeys").setText('');
        mini.get("relDeptDemandId").setValue('');
        mini.get("relDeptDemandId").setText('');
        mini.get("interfaceRequestStructId").setValue('');
        mini.get("interfaceRequestStructId").setText('');
        functionWindow.hide();
    }

    function removeFunction() {
        var rows = functionListGrid.getSelecteds();
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
                    if (r.hasOwnProperty("belongCollectFlowId") && r.belongCollectFlowId) {
                        mini.alert("接口收集流程传入的数据不能删除！");
                        return;
                    }
                    rowIds.push(r.id);
                }

                _SubmitJson({
                    url: jsUseCtxPath + "/drbfm/single/deleteFunction.do",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);

                            functionListGrid.reload();
                        }
                    }
                });
            }
        });
    }

    //功能分解选择部门需求
    function selectDemandClick() {
        selectDemandWindow.show();
        searchDemand();
    }

    function searchDemand() {
        var queryParam = [];
        //其他筛选条件
        var deptNames = $.trim(mini.get("deptNames").getValue());
        if (deptNames) {
            queryParam.push({name: "deptNames", value: deptNames});
        }
        var demandDesc = $.trim(mini.get("demandDesc").getValue());
        if (demandDesc) {
            queryParam.push({name: "demandDesc", value: demandDesc});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectDemandListGrid.load(data);
    }

    function selectDemandOK() {
        var selectRow = selectDemandListGrid.getSelected();
        mini.get("relDeptDemandId").setValue(selectRow.id);
        mini.get("relDeptDemandId").setText(selectRow.demandDesc);
        selectDemandHide();
    }

    function selectDemandHide() {
        selectDemandWindow.hide();
        mini.get("demandDesc").setValue('');
        mini.get("deptNames").setValue('');
    }

    function onRelDemandCloseClick() {
        mini.get("relDeptDemandId").setValue('');
        mini.get("relDeptDemandId").setText('');
    }

    //功能分解选择部门需求
    function selectRequestStructClick() {
        mini.open({
            title: "部件选择",
            url: jsUseCtxPath + "/drbfm/single/drbfmAllSingleTree.do?singleId=" + singleId,
            width: 1300,
            height: 700,
            showModal: true,
            allowResize: true,
            onload: function () {

            },
            ondestroy: function (returnData) {
                if (returnData && returnData.interfaceRequestStructId && returnData.structName) {
                    mini.get("interfaceRequestStructId").setValue(returnData.interfaceRequestStructId);
                    mini.get("interfaceRequestStructId").setText(returnData.structName);
                } else {
                    mini.alert("未选择部件！");
                }
            }
        });
    }

    function onRequestStructCloseClick() {
        mini.get("interfaceRequestStructId").setValue('');
        mini.get("interfaceRequestStructId").setText('');
    }

    //功能分解选择部门需求
    function selectRequestStructClick_R() {
        var interfaceRequestStructId_R = mini.get("interfaceRequestStructId_R").getText();

        mini.open({
            title: "部件选择",
            url: jsUseCtxPath + "/drbfm/single/drbfmAllSingleTree.do?singleId=" + singleId,
            width: 1300,
            height: 700,
            showModal: true,
            allowResize: true,
            onload: function () {

            },
            ondestroy: function (returnData) {
                if (returnData && returnData.interfaceRequestStructId && returnData.structName) {
                    mini.get("interfaceRequestStructId_R").setValue(returnData.interfaceRequestStructId);
                    mini.get("interfaceRequestStructId_R").setText(returnData.structName);
                } else if (!interfaceRequestStructId_R) {
                    mini.alert("未选择部件！");
                }
            }
        });
    }

    function onRequestStructCloseClick_R() {
        mini.get("interfaceRequestStructId_R").setValue('');
        mini.get("interfaceRequestStructId_R").setText('');
    }

    //要求分解
    function addRequest() {
        mini.get("relFunctionId").setEnabled(true);
        mini.get("relDimensionKeysR").setEnabled(true);
        mini.get("interfaceRequestStructId_R").setEnabled(true);
        mini.get("requestDesc").setEnabled(true);
        mini.get("compareToJP").setEnabled(true);
        mini.get("saveRequest").show();
        //这里要把失效模式列表置空
        url = "${ctxPath}/drbfm/single/getSxmsList.do?";
        sxmsGrid.load(url);
        url2 = "${ctxPath}/drbfm/single/getRiskList.do?";
        riskGrid.load(url2);
        mini.get("addSxmsRow").show();
        mini.get("removeSxmsRow").show();
        requestWindow.show();
        sxmsGrid.setAllowCellEdit(true);
    }

    function validRequest() {
        var requestDesc = $.trim(mini.get("requestDesc").getValue());

        if (requestDesc.length > 500) {
            return {"result": false, "message": "要求描述500字以内"};
        }


        var compareToJP = $.trim(mini.get("compareToJP").getValue())

        if (compareToJP.length > 500) {
            return {"result": false, "message": "与竞品对比500字以内"};
        }

        return {"result": true};
    }

    function saveRequest() {
        var formValid = validRequest();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var requestDesc = mini.get("requestDesc").getValue();
        var relDimensionKeys = mini.get("relDimensionKeysR").getValue();
        var relDimensionNames = mini.get("relDimensionKeysR").getText();
        var id = mini.get("requestId").getValue();
        var compareToJP = mini.get("compareToJP").getValue();

        var relFunctionId = mini.get("relFunctionId").getValue();
        var interfaceRequestStructId = mini.get("interfaceRequestStructId_R").getValue();
        var sxmsGridData = sxmsGrid.getChanges();
        var riskGridData = riskGrid.getChanges();
        //校验失效模式不能为空
        for (var i = 0; i < sxmsGridData.length; i++) {
            if (sxmsGridData[i].sxmsName == undefined || sxmsGridData[i].sxmsName == "") {
                mini.alert("失效模式不能为空！");
                return;
            }
        }

        for (var i = 0; i < riskGridData.length; i++) {
            if (riskGridData[i].changeDetail == undefined || riskGridData[i].changeDetail == "") {
                mini.alert("变化点描述不能为空！");
                return;
            }
        }

        var data = {
            id: id, requestDesc: requestDesc, relDimensionKeys: relDimensionKeys,
            relDimensionNames: relDimensionNames, belongSingleId: singleId,  compareToJP: compareToJP,
            relFunctionId: relFunctionId, interfaceRequestStructId: interfaceRequestStructId
            ,sxmsGridData:sxmsGridData,riskGridData:riskGridData
        };
        var json = mini.encode(data);

        $.ajax({
            url: jsUseCtxPath + '/drbfm/single/saveRequest.do',
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            requestListGrid.reload();
                            mini.get("requestId").setValue(returnData.data);
                            url = "${ctxPath}/drbfm/single/getSxmsList.do?yqId=" + returnData.data;
                            sxmsGrid.load(url);
                            url2 = "${ctxPath}/drbfm/single/getRiskList.do?yqId=" + returnData.data;
                            riskGrid.load(url2);
                        }
                    });
                }
            }
        });
    }

    function detailRequest(id) {
        requestWindow.show();
        if (id) {
            var url = jsUseCtxPath + "/drbfm/single/getRequestDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: id},
                function (json) {
                    mini.get("relDimensionKeysR").setValue(json.relDimensionKeys);
                    mini.get("relDimensionKeysR").setText(json.relDimensionNames);
                    mini.get("interfaceRequestStructId_R").setValue(json.interfaceRequestStructId);
                    mini.get("interfaceRequestStructId_R").setText(json.structName);
                    mini.get("requestDesc").setValue(json.requestDesc);
                    mini.get("requestId").setValue(json.id);
                    mini.get("relFunctionId").setValue(json.relFunctionId);
                    mini.get("relFunctionId").setText(json.functionDesc);
                    mini.get("compareToJP").setValue(json.compareToJP);
                });
            $.ajaxSettings.async = true;
            var url3=jsUseCtxPath+"/drbfm/single/requestFileList.do?requestId="+ id ;
            fileListGrid.setUrl(url3);
            fileListGrid.load();
        }
        mini.get("requestDesc").setEnabled(false);
        mini.get("relDimensionKeysR").setEnabled(false);
        mini.get("interfaceRequestStructId_R").setEnabled(false);
        mini.get("relFunctionId").setEnabled(false);
        mini.get("compareToJP").setEnabled(false);
        mini.get("saveRequest").hide();
        url = "${ctxPath}/drbfm/single/getSxmsList.do?yqId=" + id;
        sxmsGrid.load(url);
        url2 = "${ctxPath}/drbfm/single/getRiskList.do?yqId=" + id;
        riskGrid.load(url2);
        mini.get("addSxmsRow").hide();
        mini.get("removeSxmsRow").hide();
        sxmsGrid.setAllowCellEdit(false);
        $("#fileListToolBar").hide();
        $("#riskListToolBar").hide();
    }

    function editRequest(id) {
        requestWindow.show();
        sxmsGrid.setAllowCellEdit(true);
        if (id) {
            var url = jsUseCtxPath + "/drbfm/single/getRequestDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: id},
                function (json) {
                    mini.get("relDimensionKeysR").setValue(json.relDimensionKeys);
                    mini.get("relDimensionKeysR").setText(json.relDimensionNames);
                    mini.get("interfaceRequestStructId_R").setValue(json.interfaceRequestStructId);
                    mini.get("interfaceRequestStructId_R").setText(json.structName);
                    mini.get("requestDesc").setValue(json.requestDesc);
                    mini.get("requestId").setValue(json.id);
                    mini.get("relFunctionId").setValue(json.relFunctionId);
                    mini.get("relFunctionId").setText(json.functionDesc);
                    mini.get("compareToJP").setValue(json.compareToJP);
                });
            $.ajaxSettings.async = true;


        }
        mini.get("requestDesc").setEnabled(true);
        mini.get("relDimensionKeysR").setEnabled(true);
        mini.get("interfaceRequestStructId_R").setEnabled(true);
        mini.get("relFunctionId").setEnabled(true);
        mini.get("compareToJP").setEnabled(true);
        mini.get("saveRequest").show();
        url = "${ctxPath}/drbfm/single/getSxmsList.do?yqId=" + id;
        sxmsGrid.load(url);
        url2 = "${ctxPath}/drbfm/single/getRiskList.do?yqId=" + id;
        riskGrid.load(url2);
        mini.get("addSxmsRow").show();
        mini.get("removeSxmsRow").show();

        var url3=jsUseCtxPath+"/drbfm/single/requestFileList.do?requestId="+ id ;
        fileListGrid.setUrl(url3);
        fileListGrid.load();

    }

    function closeRequest() {
        mini.get("requestId").setValue('');
        mini.get("requestDesc").setValue('');
        mini.get("relDimensionKeysR").setValue('');
        mini.get("relDimensionKeysR").setText('');
        mini.get("interfaceRequestStructId_R").setValue('');
        mini.get("interfaceRequestStructId_R").setText('');
        mini.get("relFunctionId").setValue('');
        mini.get("relFunctionId").setText('');
        mini.get("compareToJP").setValue('');
        requestWindow.hide();
    }

    function removeRequest() {
        var rows = requestListGrid.getSelecteds();
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
                    if (r.hasOwnProperty("belongCollectFlowId") && r.belongCollectFlowId) {
                        mini.alert("接口收集流程传入的数据不能删除！");
                        return;
                    }
                    rowIds.push(r.id);
                }

                _SubmitJson({
                    url: jsUseCtxPath + "/drbfm/single/deleteRequest.do",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            requestListGrid.reload();
                        }
                    }
                });
            }
        });
    }

    //要求分解选择功能
    function selectFunctionClick() {
        selectFunctionWindow.show();
        searchFunction();
    }

    function searchFunction() {
        var queryParam = [];
        //其他筛选条件
        var functionDesc = $.trim(mini.get("functionDesc2").getValue());
        if (functionDesc) {
            queryParam.push({name: "functionDesc", value: functionDesc});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectFunctionListGrid.load(data);
    }

    function selectFunctionOK() {
        var selectRow = selectFunctionListGrid.getSelected();
        mini.get("relFunctionId").setValue(selectRow.id);
        mini.get("relFunctionId").setText(selectRow.functionDesc);
        var relDimensionKeysR = mini.get("relDimensionKeysR").getValue();
        mini.get("relDimensionKeysR").setValue(selectRow.relDimensionKeys);
        mini.get("relDimensionKeysR").setText(selectRow.relDimensionNames);
        mini.get("interfaceRequestStructId_R").setValue(selectRow.interfaceRequestStructId);
        mini.get("interfaceRequestStructId_R").setText(selectRow.structName);
        selectFunctionHide();
    }

    function selectFunctionHide() {
        selectFunctionWindow.hide();
        mini.get("functionDesc2").setValue('');
    }

    function onRelFunctionCloseClick() {
        mini.get("relFunctionId").setValue('');
        mini.get("relFunctionId").setText('');
    }

    //导出 功能分解 模块
    function exportFunction() {
        var params = [];
        var parent = $(".search-form");
        var inputAry = $("input", parent);
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
        var url = jsUseCtxPath + "/drbfm/single/exportFunction.do?singleId=" + singleId;
        excelForm.attr("action", url);
        excelForm.submit();
    }

    //导出要求分解模块
    //exportRequest
    function exportRequest() {
        var params = [];
        var parent = $(".search-form");
        var inputAry = $("input", parent);
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
        var url = jsUseCtxPath + "/drbfm/single/exportRequest.do?singleId=" + singleId;
        excelForm.attr("action", url);
        excelForm.submit();
    }



    // @mh 2023年7月6日14:52:53 功能失效网部分

    function addSxmsRow() {
        //todo
        var yqId = mini.get("requestId").getValue();
        if (!yqId) {
            mini.alert("请先保存要求基础信息！");
            return;
        }
        var row = {needFxfx: "是"};
        sxmsGrid.addRow(row);
    }


    function removeSxmsRow() {
        var selecteds = sxmsGrid.getSelecteds();
        sxmsGrid.removeRows(selecteds);
    }


    //风险列表
    function addRiskRow() {
        var yqId = mini.get("requestId").getValue();
        if (!yqId) {
            mini.alert("请先保存要求基础信息！");
            return;
        }
        row = {};
        if(isExitsVariable(window.parent.selectRowNames)) {
            var model = window.parent.selectRowNames;
            if (model.indexOf(",") == -1) {
                row = {designModelName: model,designModel:window.parent.selectRowIds};
            }
        } else {
            mini.alert("未找到变量selectRowNames")
        }
        riskGrid.addRow(row);
    }


    function removeRiskRow() {
        var selecteds = riskGrid.getSelecteds();
        riskGrid.removeRows(selecteds);
    }


    /**
     * 关联设计型号弹窗
     */
    function selectDesignModel() {
        spectrumWindow.show();
        searchDesignModel();
    }

    /**
     * 关联设计型号查询
     */
    function searchDesignModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("searchDesignModel").getValue());
        if (designModel) {
            queryParam.push({name: "designModel", value: designModel});
        } else {
            var selectModel = window.parent.selectRowNames;
            if (selectModel) {
                queryParam.push({name: "selectModel", value: selectModel});
            }
        }
        // queryParam.push({name: "instStatus", value: "SUCCESS_END"});

        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = spectrumListGrid.getPageIndex();
        data.pageSize = spectrumListGrid.getPageSize();
        data.sortField = spectrumListGrid.getSortField();
        data.sortOrder = spectrumListGrid.getSortOrder();
        //查询
        spectrumListGrid.load(data);
    }


    /**
     * 产品型号确定按钮
     */
    function okWindow() {
        var selectRow = spectrumListGrid.getSelected();
        if (!selectRow) {
            mini.alert("请选择产品型号！");
            return;
        }
        var objrow = riskGrid.getSelected();
        riskGrid.updateRow(objrow,
            {
                designModel: selectRow.id,
                designModelName: selectRow.designModel

            });
        hideWindow()
    }

    /**
     * 产品型号关闭按钮
     */
    function hideWindow() {
        spectrumWindow.hide();
        mini.get("searchDesignModel").setValue('');
        mini.get("searchDesignModel").setText('');
    }




    function selectChangeDimension() {
        selectDimensionWindow.show();
        searchChangeDimension();
    }

    /**
     * 变化维度查询
     */
    function searchChangeDimension() {
        var queryParam = [];
        //其他筛选条件
        var dimensionType = $.trim(mini.get("dimensionType").getValue());
        if (dimensionType) {
            queryParam.push({name: "dimensionType", value: dimensionType});
        }
        var changeDimension = $.trim(mini.get("changeDimension").getValue());
        if (changeDimension) {
            queryParam.push({name: "changeDimension", value: changeDimension});
        }

        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = dimensionListGrid.getPageIndex();
        data.pageSize = dimensionListGrid.getPageSize();
        data.sortField = dimensionListGrid.getSortField();
        data.sortOrder = dimensionListGrid.getSortOrder();
        //查询
        dimensionListGrid.load(data);
    }


    /**
     * 变化维度确定按钮
     */
    function selectDimensionOK() {
        var selectRow = dimensionListGrid.getSelected();
        if (!selectRow) {
            mini.alert("请选择变化维度！");
            return;
        }
        var objrow = riskGrid.getSelected();
        riskGrid.updateRow(objrow,
            {
                changeDimension: selectRow.changeDimension
            });
        selectDimensionHide()
    }

    /**
     * 变化维度关闭按钮
     */
    function selectDimensionHide() {
        selectDimensionWindow.hide();
        mini.get("dimensionType").setValue('');
        mini.get("dimensionType").setText('');
        mini.get("changeDimension").setValue('');
        mini.get("changeDimension").setText('');
    }

    //附件
    function addrequestFile() {
        var requestId = mini.get("requestId").getValue();
        if (!requestId) {
            mini.alert('请先点击‘保存’进行要求内容的保存！');
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/drbfm/single/requestUploadWindow.do?requestId=" + requestId + "&belongSingleId" + singleId ,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                projectParams.requestId = requestId;
                projectParams.belongSingleId = singleId;
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                fileListGrid.load();
            }
        });
    }
    function operationRenderer(e) {
        var record = e.record;
        if (!record.id) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.requestId, coverContent);
        var downloadUrl = '/drbfm/testTask/fileDownload.do';
        if (record) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.requestId + '\',\'' + downloadUrl + '\')">下载</span>';
        }
        if (record && record.CREATE_BY_ == currentUserId) {
            var deleteUrl = "/drbfm/testTask/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.requestId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    function searchFrm(){
        var relFunctionDesc = $.trim(mini.get("relFunctionDesc").getValue());
        var requestDescri =  $.trim(mini.get("requestDescri").getValue());
        var riskAnalysis = $.trim(mini.get("riskAnalysis").getValue());
        var queryParam = [];
        if (relFunctionDesc) {
            queryParam.push({name: "relFunctionDesc", value: relFunctionDesc});
        }
        if (requestDescri) {
            queryParam.push({name: "requestDesc", value: requestDescri});
        }
        if (riskAnalysis) {
            queryParam.push({name: "riskAnalysis", value: riskAnalysis});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        data.pageIndex = requestListGrid.getPageIndex();
        data.pageSize = requestListGrid.getPageSize();
        data.sortField = requestListGrid.getSortField();
        data.sortOrder = requestListGrid.getSortOrder();
        requestListGrid.load(data);
    }
    function clearForm(){
        var relFunctionDesc = $.trim(mini.get("relFunctionDesc").getValue());
        var requestDescri =  $.trim(mini.get("requestDescri").getValue());
        var riskAnalysis = $.trim(mini.get("riskAnalysis").getValue());
        mini.get("relFunctionDesc").setValue('');
        mini.get("relFunctionDesc").setText('');
        mini.get("requestDescri").setValue('');
        mini.get("requestDescri").setText('');
        mini.get("riskAnalysis").setValue('');
        mini.get("riskAnalysis").setText('');

        var queryParam = [];
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        data.pageIndex = requestListGrid.getPageIndex();
        data.pageSize = requestListGrid.getPageSize();
        data.sortField = requestListGrid.getSortField();
        data.sortOrder = requestListGrid.getSortOrder();
        requestListGrid.load(data);

    }

</script>
</body>
</html>
