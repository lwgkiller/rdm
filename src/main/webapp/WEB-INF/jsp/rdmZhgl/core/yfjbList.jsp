<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>研发降本项目列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/yfjbList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<style type="text/css">
</style>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">创建年份: </span>
                    <input id="createYear" name="createYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px"  label="年度："
                           length="50"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false" onvaluechanged="searchFrm()"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">销售型号: </span>
                    <input name="saleModel" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">设计型号: </span>
                    <input name="designModel" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">责任人: </span>
                    <input name="responseMan" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属部门: </span>
                    <input id="deptName" name="deptName" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">原物料编码: </span>
                    <input name="orgItemCode" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">替代物料编码: </span>
                    <input name="newItemCode" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">代替比例: </span>
                    <input name="replaceRate" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">降本方式: </span>
                    <input id="costType" name="costType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="降本方式："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-JBFS"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <br>
                <li style="margin-right: 15px"><span class="text" style="width:auto">生产是否切换: </span>
                    <input id="isReplace" name="isReplace" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="生产是否切换："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属专业: </span>
                    <input id="major" name="major" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="所属专业："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-SSZY"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">进度状态: </span>
                    <input id="processStatus" name="processStatus" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="进度状态："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-JDZT"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">是否填写最新进度: </span>
                    <input id="isNewProcess" name="isNewProcess" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="是否填写最新进度："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目状态: </span>
                    <input id="infoStatus" name="infoStatus" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="项目状态："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-XMZT"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">是否需要试制: </span>
                    <input id="isSz" name="isSz" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="是否需要试制："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">实际切换时间 从 </span>: <input id="actChange_startDate"
                                                                                     class="mini-monthpicker"
                                                                                     style="width: 150px"
                                                                                     name="actChange_startDate"
                                                                                     onvaluechanged="searchFrm"
                                                                                     allowinput="false"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至: </span> <input id="actChange_endDate"
                                                                                   class="mini-monthpicker"
                                                                                   style="width: 150px"
                                                                                   name="actChange_endDate"
                                                                                   onvaluechanged="searchFrm"
                                                                                   allowinput="false"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">计划切换时间 从 </span>: <input id="planChange_startDate"
                                                                                       class="mini-monthpicker"
                                                                                       style="width: 150px"
                                                                                       name="planChange_startDate"
                                                                                       onvaluechanged="searchFrm"
                                                                                       allowinput="false"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至: </span> <input id="planChange_endDate"
                                                                                   class="mini-monthpicker"
                                                                                   style="width: 150px"
                                                                                   name="planChange_endDate"
                                                                                   onvaluechanged="searchFrm"
                                                                                   allowinput="false"/>
                    </li>
                </ul>
            </div>
        </form>
        <ul class="toolBtnBox">
            <a class="mini-button" plain="true" onclick="addForm()">新增</a>
            <a class="mini-button" id="importId" style="margin-left: 10px" onclick="openImportWindow()">导入</a>
            <a class="mini-button" style="margin-left: 5px" plain="true" onclick="submitInfo()">提交</a>
            <div style="display: inline-block" class="separator"></div>
            <a class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeRow()">删除</a>
            <a class="mini-button" style="margin-left: 10px" onclick="copyInfo()">复制</a>
            <a class="mini-button" style="margin-left: 10px" onclick="changeNotice()">切换通知单</a>
            <a class="mini-button" style="margin-left: 10px" onclick="produceNotice()">试制通知单</a>
            <div style="display: inline-block" class="separator"></div>
            <a id="abolishApply" class="mini-button " style="margin-left: 10px;" plain="true"
               onclick="doAbolishApply()">作废申请</a>
            <a id="editApply" class="mini-button " style="margin-left: 10px;" plain="true" onclick="doEditApply()">信息修改申请</a>
            <div style="display: inline-block" class="separator"></div>
            <a id="reportExport" class="mini-button " style="margin-left: 10px;" plain="true"
               onclick="openExportWindow()">报表导出</a>
            <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportExcel()">按条件导出</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_"
         sortOrder="desc"
         url="${ctxPath}/rdmZhgl/core/yfjb/list.do" idField="id" showPager="true" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="30px"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="saleModel" name="saleModel" width="200px" headerAlign="center" align="center" allowSort="false">
                销售型号
            </div>
            <div field="designModel" name="designModel" width="200px" headerAlign="center" align="left"
                 allowSort="false">设计型号
            </div>
            <div field="infoStatus" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onStatus">项目状态
            </div>
            <div field="isNewProcess" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onNewProcess">是否填写最新进度
            </div>
            <div width="150px" headerAlign="center" align="center" renderer="renderMember" allowSort="false">项目成员</div>
            <div field="orgItemCode" width="150px" headerAlign="center" align="center">原物料编码</div>
            <div field="orgItemName" width="200px" headerAlign="center" align="center" allowSort="false">原物料名称</div>
            <div field="orgItemPrice" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onHidePrice">原物料价格
            </div>
            <div field="orgSupplier" width="200px" headerAlign="center" align="center" allowSort="false">原供应商</div>
            <div field="basePrice" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onHidePrice">基准价格
            </div>
            <div field="costType" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onJbfs">
                降本方式
            </div>
            <div field="costMeasure" width="200px" headerAlign="center" align="center" allowSort="false">降本措施</div>
            <div field="newItemCode" width="150px" headerAlign="center" align="center" allowSort="false">替代物料编码</div>
            <div field="newItemName" width="200px" headerAlign="center" align="center" allowSort="false">替代物料名称</div>
            <div field="newItemPrice" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onHidePrice">替代物料价格
            </div>
            <div field="newSupplier" width="200px" headerAlign="center" align="center" allowSort="false">新供应商</div>
            <div field="differentPrice" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="toFixNum">差额
            </div>
            <div field="perSum" width="150px" headerAlign="center" align="center" allowSort="false">单台用量</div>
            <div field="replaceRate" width="150px" headerAlign="center" align="center" allowSort="false">代替比例(%)</div>
            <div field="perCost" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="toFixNum">单台降本
            </div>
            <div field="achieveCost" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="toFixNum">已实现单台降本
            </div>
            <div field="risk" width="200px" headerAlign="center" align="center" allowSort="false">风险评估</div>
            <div field="isReplace" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onReplace">生产是否切换
            </div>
            <div field="isSz" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onIsSz">
                是否需要试制
            </div>
            <div field="jhsz_date" width="150px" headerAlign="center" align="center" allowSort="false">计划试制时间</div>
            <div field="sjsz_date" width="150px" headerAlign="center" align="center" allowSort="false">实际试制时间</div>
            <div field="jhxfqh_date" width="180px" headerAlign="center" align="center" allowSort="false">计划下发切换通知单时间
            </div>
            <div field="sjxfqh_date" width="180px" headerAlign="center" align="center" allowSort="false">实际下发切换通知单时间
            </div>
            <div field="jhqh_date" width="150px" headerAlign="center" align="center" allowSort="false">计划切换时间</div>
            <div field="sjqh_date" width="150px" headerAlign="center" align="center" allowSort="false">实际切换时间</div>
            <div field="deptName" width="150px" headerAlign="center" align="center" allowSort="false">所属部门</div>
            <div field="major" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onMajor">
                所属专业
            </div>
            <div field="responseMan" width="150px" headerAlign="center" align="center" allowSort="false">责任人</div>
            <div field="yearMonth" width="150px" headerAlign="center" align="center" allowSort="false">进度年月</div>
            <div field="type" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onProcessType">进度类型
            </div>
            <div field="processStatus" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onProcessStatus">进度状态
            </div>
            <div field="processContent" width="150px" headerAlign="center" align="center" allowSort="false">进度内容</div>
            <div width="150px" headerAlign="center" align="center" renderer="process" allowSort="false">降本项目进度跟踪</div>
        </div>
    </div>
</div>
<div id="exportWindow" title="报表导出窗口" class="mini-window" style="width:650px;height:370px;overflow: hidden"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a class="mini-button" onclick="exportBtn()">导出</a>
        <a class="mini-button btn-red" onclick="closeExportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px;overflow: hidden">
        <form id="infoForm" method="post">
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <tr class="firstRow displayTr">
                    <td align="center"></td>
                    <td align="left"></td>
                    <td align="center"></td>
                    <td align="left"></td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        报表类型<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input id="reportType" name="reportType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="报表类型：" onvaluechanged="verifyInfo"
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-BBLX"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr id="reportModelType" style="display: none">
                    <td align="center" style="white-space: nowrap;">
                        机型报表分类<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input id="modelType" name="modelType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="机型报表分类：" onvaluechanged="showModelType"
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-JXBBFL"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr id="modelInfo" style="display: none">
                    <td align="center" style="white-space: nowrap;">
                        销售机型：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input id="saleModel" name="saleModel" class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr id="yearMonthTr" style="display: none">
                    <td align="center" style="white-space: nowrap;">
                        统计年月：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input id="yearMonth" name="yearMonth" allowinput="false" class="mini-monthpicker"
                               style="width:100%;height:34px"/>
                    </td>
                </tr>
                <tr id="costTypeTr" style="display: none">
                    <td align="center" style="white-space: nowrap;">
                        项目维度<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input id="costCategory" name="costCategory" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="项目维度："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-XMWD"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr id="ruleTr" style="display: none">
                    <td align="center" style="white-space: nowrap;">
                        统计规则<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input id="reportRule" name="reportRule" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="统计规则："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-TJGZ"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr id="reportYearTr" style="display: none">
                    <td align="center" style="white-space: nowrap;">
                        统计年份<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input id="reportYear" name="reportYear" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="年度："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="value" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr id="reportYearMonthTr" style="display: none">
                    <td align="center" style="white-space: nowrap;">
                        统计月份<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input id="reportYearMonth" class="mini-monthpicker" allowinput="false" style="width: 100%"
                               name="reportYearMonth"/>
                    </td>
                </tr>
                <tr id="reportDeptTr" style="display: none">
                    <td align="center" style="white-space: nowrap;">
                        统计部门：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input id="reportDept" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:100%;height:34px" emptyText="如果是导出整个挖掘机械研究院数据，则不需要选择部门"
                               allowinput="false" textname="deptName" length="200" maxlength="200" minlen="0"
                               single="true" initlogindep="false"/>
                    </td>
                </tr>
                </tr>
                </tbody>
            </table>
        </form>
        <form id="excelForm" action="${ctxPath}/rdmZhgl/core/yfjb/exportExcel.do" method="post" target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

        <form id="excelSearchForm" action="${ctxPath}/rdmZhgl/core/yfjb/exportBaseInfoExcel.do" method="post"
              target="excelSearchIFrame">
            <input type="hidden" name="filterSearch" id="filterSearch"/>
        </form>
        <iframe id="excelSearchIFrame" name="excelSearchIFrame" style="display: none;"></iframe>
    </div>
</div>
<div id="importWindow" title="研发降本导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importProduct()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">研发降本项目导入模板.xlsx</a>
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
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUser.userId}";
    var listGrid = mini.get("listGrid");
    var jbfsList = getDics("YFJB-JBFS");
    var replaceList = getDics("YESORNO");
    var majorList = getDics("YFJB-SSZY");
    var statusList = getDics("YFJB-XMZT");
    var processTypeList = getDics("YFJB-JDLB");
    var processStatusList = getDics("YFJB-JDZT");
    var exportWindow = mini.get("exportWindow");
    listGrid.frozenColumns(0, 5);
    var importWindow = mini.get("importWindow");
    var permission = ${permission};
    var YFJBAdmin = ${YFJBAdmin};
    var JbzyAdmin = ${JbzyAdmin};
    var currentUserDeptId = ${deptId};
    var paramJson = ${paramJson};
    if (!permission) {
        mini.get('reportExport').setEnabled(false);
    }

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var response = record.response;
        var creator = record.CREATE_BY_;
        var deptId = record.deptId;
        var s = '';
        if (currentUserId == response || currentUserId == creator || YFJBAdmin) {
            s = '<span  title="编辑" onclick="editForm(\'' + id + '\')">编辑</span>';
        } else if (JbzyAdmin && currentUserDeptId == deptId) {
            s = '<span  title="编辑" onclick="editForm(\'' + id + '\')">编辑</span>';
        } else {
            s += '<span  title="编辑" style="color: silver">编辑</span>';
        }
        var processStatus = e.record.processStatus;
        if (processStatus == 'tq') {
            e.rowStyle = 'background-color:green';
        } else if (processStatus == 'yh') {
            e.rowStyle = 'background-color:red';
        }

        return s;
    }

    function toFixNum(e) {
        if (e.value) {
            return parseFloat(e.value).toFixed(2);
        }
    }

    function onProcessStatus(e) {
        var processStatus = e.record.processStatus;
        if (processStatus == 'tq') {
            e.rowStyle = 'background-color:green';
        } else if (processStatus == 'yh') {
            e.rowStyle = 'background-color:red';
        }
    }

    function onJbfs(e) {
        var record = e.record;
        var resultValue = record.costType;
        var resultText = '';
        for (var i = 0; i < jbfsList.length; i++) {
            if (jbfsList[i].key_ == resultValue) {
                resultText = jbfsList[i].text;
                break
            }
        }
        return resultText;
    }

    function onReplace(e) {
        var record = e.record;
        var resultValue = record.isReplace;
        var resultText = '';
        for (var i = 0; i < replaceList.length; i++) {
            if (replaceList[i].key_ == resultValue) {
                resultText = replaceList[i].text;
                break
            }
        }
        return resultText;
    }

    function onIsSz(e) {
        var record = e.record;
        var resultValue = record.isSz;
        var resultText = '';
        for (var i = 0; i < replaceList.length; i++) {
            if (replaceList[i].key_ == resultValue) {
                resultText = replaceList[i].text;
                break
            }
        }
        return resultText;
    }

    function onMajor(e) {
        var record = e.record;
        var resultValue = record.major;
        var resultText = '';
        for (var i = 0; i < majorList.length; i++) {
            if (majorList[i].key_ == resultValue) {
                resultText = majorList[i].text;
                break
            }
        }
        return resultText;
    }

    function onProcessType(e) {
        var record = e.record;
        var resultValue = record.type;
        var resultText = '';
        for (var i = 0; i < processTypeList.length; i++) {
            if (processTypeList[i].key_ == resultValue) {
                resultText = processTypeList[i].text;
                break
            }
        }
        return resultText;
    }

    function onProcessStatus(e) {
        var record = e.record;
        var resultValue = record.processStatus;
        var resultText = '';
        for (var i = 0; i < processStatusList.length; i++) {
            if (processStatusList[i].key_ == resultValue) {
                resultText = processStatusList[i].text;
                break
            }
        }
        return resultText;
    }

    function onNewProcess(e) {
        var record = e.record;
        var resultValue = record.isNewProcess;
        var resultText = '';
        for (var i = 0; i < replaceList.length; i++) {
            if (replaceList[i].key_ == resultValue) {
                resultText = replaceList[i].text;
                break
            }
        }
        return resultText;
    }

    function onStatus(e) {
        var record = e.record;
        var resultValue = record.infoStatus;
        var resultText = '';
        for (var i = 0; i < statusList.length; i++) {
            if (statusList[i].key_ == resultValue) {
                resultText = statusList[i].text;
                break
            }
        }
        var _html = '';
        var color = '';
        if (resultValue == '1') {
            color = '#cdcd62'
        } else if (resultValue == '2') {
            color = 'green';
        } else if (resultValue == '3') {
            color = 'red';
        } else if (resultValue == '4') {
            color = '#3b0df0';
        }
        _html = '<span style="color: ' + color + '">' + resultText + '</span>'
        return _html;
    }

    function onHidePrice() {
        return '***';
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
