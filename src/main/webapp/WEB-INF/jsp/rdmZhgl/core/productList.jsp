<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>新品试制列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/productList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">产品重要度: </span>
                    <input id="important" name="important" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="产品重要度："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=ZYD"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">产品主管: </span>
                    <input name="productLeaderName" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">产品类型: </span>
                    <input name="productType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:150px;height:34px" label="产品类型："
                           length="50"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="true"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=XPSZ-CPLX"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目负责人: </span>
                    <input name="responseManName" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">计划管理员: </span>
                    <input name="planAdminName" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属年份: </span>
                    <input id="belongYear" name="belongYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="年度："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">节点: </span>
                    <input id="stage" name="stage" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:150px;height:34px" label="年度："
                           length="50"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="itemName" valueField="itemCode" emptyText="请选择..."
                           url="${ctxPath}/rdmZhgl/core/productConfig/list.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">时间从: </span>
                    <input id="reportStartDate" name="reportStartDate"   class="mini-datepicker"  format="yyyy-MM-dd" style="font-size:14pt;width:120px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">到: </span>
                    <input id="reportEndDate" name="reportEndDate"   class="mini-datepicker"  format="yyyy-MM-dd" style="font-size:14pt;width:120px;"/>
                </li>
                <br>
                <li style="margin-right: 15px"><span class="text" style="width:auto">进度状态: </span>
                    <input id="processStatus" name="processStatus" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="进度状态：" onvaluechanged="searchFrm()"
                           length="50" onvaluechanged="remarkTip"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=XPSZ-JDZT"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">产业化状态: </span>
                    <input id="productStatus" name="productStatus" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="产业化状态："
                           length="50"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="true"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=XPSZ-CYHZT"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">是否查看历史变更记录: </span>
                    <input id="isHistory" name="isHistory" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="是否查看历史变更记录："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">产品设计型号: </span>
                    <input name="productModel" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">是否立项: </span>
                    <input id="isLx" name="isLx" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="是否立项："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/rdmZhgl/core/product/exportExcel.do" method="post"
              target="excelIFrame">
            <input type="hidden" name="pageIndex"/>
            <input type="hidden" name="pageSize"/>
            <input type="hidden" id="filter" name="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
            <a id="addButton" class="mini-button" plain="true" onclick="addForm()">新增</a>
            <div  style="display: inline-block" class="separator"></div>
            <a id="editButton" class="mini-button" onclick="editForm()">编辑</a>
            <div style="display: inline-block" class="separator"></div>
            <a id="delButton" class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeRow()">删除</a>
            <div style="display: inline-block" class="separator"></div>
            <%--<a id="planApply" class="mini-button " style="margin-left: 10px;" plain="true"--%>
               <%--onclick="doMonthApply()">月度提报</a>--%>
            <a id="changeApply" class="mini-button " style="margin-left: 10px;" plain="true" onclick="doChangeApply()">变更申请</a>
            <a class="mini-button" id="importId" style="margin-left: 10px" onclick="openImportWindow()">导入</a>
            <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportBtn()">导出</a>
            <a id="batchEditButton" class="mini-button" style="margin-left: 5px" plain="true"
               onclick="batchEditPlanDate()">批量修改计划时间</a>
            <a id="changeRecordButton" class="mini-button" style="margin-left: 5px" plain="true"
               onclick="changeRecord()">变更记录</a>
            <a id="asyncPdmDeliveryButton" class="mini-button" style="margin-left: 5px" plain="true"
               onclick="asyncPdmDelivery()">同步PDM交付物时间</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="productGrid" class="mini-datagrid" allowResize="true" style="height: 100%"
         url="${ctxPath}/rdmZhgl/core/product/list.do" idField="id" showPager="true" allowCellWrap="true"
         allowCellEdit="true" allowCellSelect="true" ondrawcell="onDrawCell"
         multiSelect="true" showColumnsMenu="false" sizeList="[12,40,50,100,200]" pageSize="12" allowAlternating="true"
         oncellendedit="cellendedit" allowHeaderWrap="true" onlyCheckSelection="true" oncellbeginedit="cellbeginedit"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" field="mainId" name="mainId" width="30px"></div>
            <div field="rowNum" name="rowNum" align="center" headerAlign="center" width="40px">序号</div>
            <div field="productType" name="productType" width="200px" headerAlign="center" align="center">产品类型</div>
            <div field="productModel" name="productModel" width="100px" headerAlign="center" align="center">产品设计型号</div>
            <div field="ysbh" name="ysbh" width="100px" headerAlign="center" align="center">集团项目预算编号</div>
            <div field="taskFrom" name="taskFrom" width="100px" headerAlign="center" align="center">任务来源</div>
            <div field="budgetProductModel" name="budgetProductModel" width="100px" headerAlign="center" align="center">
                预算产品型号
            </div>
            <div field="downLine" name="downLine" width="100px" headerAlign="center" align="center" renderer="onDownLineStatus">上报集团，样机下线</div>
            <div field="isLx" name="isLx" width="100px" headerAlign="center" align="center" renderer="onIsLxStatus">是否立项</div>
            <div field="projectName" name="projectName" width="200px" headerAlign="center" align="center"
                 renderer="onProjectName">项目名称
            </div>
            <div field="isInverted" name="isInverted" width="100px" headerAlign="center" align="center" renderer="onIsInvertedStatus">是否倒排</div>
            <div field="important" name="important" width="100px" headerAlign="center" align="center"
                 renderer="onImportant">重要度
            </div>
            <div field="processStatus" name="processStatus" width="100px" headerAlign="center" align="center"
                 renderer="onProcessStatus">进度状态
            </div>
            <div field="itemType" name="itemType" width="100px" headerAlign="center" align="center"
                 renderer="onTimeType">时间类别
            </div>
            <div field="" width="240px" headerAlign="center" align="center">挖掘机械研究院
                <div property="columns" align="center">
                    <div field="fah_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        方案会
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="tzgd_date" width="100px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        图纸归档
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="tzxf_date" width="100px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        图纸下发
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="sapjl_date" width="100px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        SAP建立
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="czqj_date" width="100px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        长周期件一次性采购通知
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="zkjdh_date" width="100px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        召开交底会
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>
            <div field="" width="240px" headerAlign="center" align="center">工艺技术部
                <div property="columns" align="center">
                    <div field="gyfa_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        工艺方案
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="pbomjl_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        PBOM建立
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>
            <div field="" width="240px" headerAlign="center" align="center">挖掘机械研究院
                <div property="columns" align="center">
                    <div field="fsztzd_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        发试制通知单
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>
            <div field="" width="240px" headerAlign="center" align="center">制造管理部
                <div property="columns" align="center">
                    <div field="scdd_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        生产订单
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>
            <div field="" width="240px" headerAlign="center" align="center">供方发展部
                <div property="columns" align="center">
                    <div field="zybj_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        资源布局
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>

            <div field="" width="240px" headerAlign="center" align="center">采购管理部
                <div property="columns" align="center">
                    <div field="xljdhsj_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        下料件到货时间
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>
            <div field="" width="240px" headerAlign="center" align="center">供方发展部
                <div property="columns" align="center">
                    <div field="mjwcsj_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        模具完成时间
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>
            <div field="" width="240px" headerAlign="center" align="center">采购管理部
                <div property="columns" align="center">
                    <div field="gzjdw_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        长周期件到位
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="qbptjwc_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        全部配套件完成
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>
            <div field="" width="240px" headerAlign="center" align="center">质量保证部
                <div property="columns" align="center">
                    <div field="cwjy_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        初物检验
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>
            <div field="" width="240px" headerAlign="center" align="center">工程中心/各分厂
                <div property="columns" align="center">
                    <div renderer="modelPic" align="center" headerAlign="center">样机照片
                    </div>
                    <div field="yjwczcs_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        样机完成转测试（样机照片必填）
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>
            <div field="" width="240px" headerAlign="center" align="center">工程中心/各分厂
                <div property="columns" align="center">
                    <div field="szgcwtqd_date" width="100px" headeralign="center" align="center"
                         dateFormat="yyyy-MM-dd">
                        试制过程问题上传RDM
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>
            <div field="" width="240px" headerAlign="center" align="center">挖掘机械研究院
                <div property="columns" align="center">
                    <div field="zjtx_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        整机调试
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="cs_date" width="100px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        测试
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="gykhkssj_date" width="100px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        工业考核开始时间
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="yjtzzgwcsj_date" width="100px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        样机图纸整改完成时间
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>

                </div>
            </div>
            <div field="" width="240px" headerAlign="center" align="center">工程中心/各分厂/制造管理部
                <div property="columns" align="center">
                    <div field="xplwcsj_date" width="160px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        小批量完成时间
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>
            <div field="" width="240px" headerAlign="center" align="center">挖掘机械研究院
                <div property="columns" align="center">
                    <div field="sgsywc_date" width="160px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        三高试验完成
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="jswj_date" width="160px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        技术文件
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="xssy_date" width="160px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        型式试验/欧美认证/农机认证
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="kfqrpsh_date" width="160px" headeralign="center" allowsort="true" align="center"
                         dateFormat="yyyy-MM-dd">
                        开发确认评审会
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                    <div field="ssrq_date" width="100px" headeralign="center" align="center" dateFormat="yyyy-MM-dd">
                        上市日期
                        <input property="editor" class="mini-datepicker" style="width:100%;"/>
                    </div>
                </div>
            </div>
            <div field="productLeaderName" name="productLeaderName" width="100px" headerAlign="center" align="center">
                产品主管
            </div>
            <div field="isNewModel" name="isNewModel" width="120px" headerAlign="center" align="center"
                 renderer="onIsNewModel">是否为新销售型号
            </div>
            <div field="productStatus" name="productStatus" width="100px" headerAlign="center" align="center"
                 renderer="onProductStatus">产业化状态
            </div>
            <div field="modelStatus" name="modelStatus" width="100px" headerAlign="center" align="center"
                 renderer="onModelStatus">样机状态
            </div>
            <div field="isNeedModel" name="isNeedModel" width="200px" headerAlign="center" align="center" renderer="onNeedModelStatus">样机或小批量是否需要模具
            </div>
            <div field="responseManName" name="responseManName" width="200px" headerAlign="center" align="center">项目负责人
            </div>
            <div field="mainConfig" name="mainConfig" width="200px" headerAlign="center" align="center">产品主要配置
            </div>
            <div field="planAdminName" name="planAdminName" width="200px" headerAlign="center" align="center">计划管理员
            </div>
            <div field="remark" name="remark" width="100px" headerAlign="center" align="center">备注</div>
            <div field="changeReason" name="changeReason" width="100px" headerAlign="center" align="center">变更原因</div>
            <div field="planChange" name="planChange" width="100px" headerAlign="center" align="center">计划变更</div>
        </div>
    </div>
</div>
<div id="importWindow" title="新品试制导入窗口" class="mini-window" style="width:750px;height:280px;"
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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">新品试制导入模板.xlsx</a>
                    </td>
                </tr>
                <tr >
                    <td  style="white-space: nowrap;">
                        所属年份<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input id="yearSelect" name="yearSelect" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:200px;height:34px" label="年度："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="value" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getYearList.do"
                               nullitemtext="请选择..." emptytext="请选择..."/>
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
    var productGrid = mini.get("productGrid");
    var importantList = getDics("ZYD");
    var modelStatusList = getDics("YJZT");
    var yesOrNoList = getDics("YESORNO");
    var timeTypeList = getDics("timeType");
    var productStatusList = getDics("XPSZ-CYHZT");
    var yellowFlag = false;
    var redFlag = false;
    var coverContent = "徐州徐工挖掘机械有限公司";
    var permission = ${permission};
    var common = ${common};
    var importWindow = mini.get("importWindow");
    var isShow = true;
    var processStatus = "${processStatus}";
    var important = "${important}";
    var belongYear = "${belongYear}";
    var reportType = "${reportType}";
    var dateStart = "${dateStart}";
    var dateEnd = "${dateEnd}";
    productGrid.frozenColumns(0, 3);
    productGrid.on("load", function () {
        productGrid.mergeColumns(["mainId", "rowNum", "ysbh","productType","budgetProductModel","downLine","isInverted","isLx", "taskFrom", "projectName", "processStatus", "productModel", "important", "timeType", "productLeaderName",
            "modelStatus", "isNewModel", "responseManName", "remark", "changeReason", "planChange", "productStatus", "planAdminName", "mainConfig", "isNeedModel"]);
    });
    if (!permission) {
        mini.get("addButton").setEnabled(false);
        mini.get("delButton").setEnabled(false);
        mini.get("planApply").setEnabled(false);
        mini.get("batchEditButton").setEnabled(false);
        mini.get("asyncPdmDeliveryButton").setEnabled(false);
    }
    if (common) {
        mini.get("editButton").setEnabled(false);
        mini.get("changeApply").setEnabled(false);
        mini.get("importId").setEnabled(false);
    }

    function onDrawCell(e) {
        var record = e.record;
        var field = e.field;
        if (record.itemType != '4') {
            if (record.itemType == '3') {
                if (field && (field.endsWith("_date") || field == 'itemType')) {
                    e.cellStyle = "font-weight:bolder";
                }
            }
        } else {
            if (field) {
                if (field.endsWith("_date")) {
                    var mainId = record.mainId;
                    var value = e.cellHtml;
                    if (value) {
                        var result = compareDate(field, value, mainId);
                        if (result) {
                            e.cellStyle = "background-color:" + result;
                        }
                    }
                }
            }
        }
    }

    function compareDate(field, value, mainId) {
        var result = "";
        var param = {mainId: mainId};
        var url = __rootPath + "/rdmZhgl/core/product/getObj.do";
        var resultData = ajaxRequest(url, "POST", false, param);
        if (resultData.success) {
            var planDate = resultData.data[field];
            if (planDate) {
                s2 = new Date(value.replace(/-/g, "/"));
                var time = s2.getTime() - planDate;
                var days = parseInt(time / (1000 * 60 * 60 * 24));
                if (days >= 4 && days <= 8) {
                    result = 'yellow';
                    yellowFlag = true;
                } else if (days > 8) {
                    result = 'red';
                    redFlag = true
                }
            }
        }
        return result;
    }

    function onProcessStatus(e) {
        var processStatus = e.record.processStatus;
        var _html = '';
        if (processStatus == '1') {
            _html = '<span style="color: green">正常</span>'
        } else if (processStatus == '2') {
            _html = '<span style="color: #d9d90a">轻微落后</span>'
        } else if (processStatus == '3') {
            _html = '<span style="color: red">严重滞后</span>'
        }
        return _html;
    }

    function onIsNewModel(e) {
        var record = e.record;
        var value = record.isNewModel;
        var resultText = '';
        for (var i = 0; i < yesOrNoList.length; i++) {
            if (yesOrNoList[i].key_ == value) {
                resultText = yesOrNoList[i].text;
                break
            }
        }
        return resultText;
    }

    function onModelStatus(e) {
        var record = e.record;
        var value = record.modelStatus;
        var resultText = '';
        for (var i = 0; i < modelStatusList.length; i++) {
            if (modelStatusList[i].key_ == value) {
                resultText = modelStatusList[i].text;
                break
            }
        }
        return resultText;
    }

    function onImportant(e) {
        var record = e.record;
        var value = record.important;
        var resultText = '';
        for (var i = 0; i < importantList.length; i++) {
            if (importantList[i].key_ == value) {
                resultText = importantList[i].text;
                break
            }
        }
        return resultText;
    }

    function onTimeType(e) {
        var record = e.record;
        var value = record.itemType;
        var resultText = '';
        for (var i = 0; i < timeTypeList.length; i++) {
            if (timeTypeList[i].key_ == value) {
                resultText = timeTypeList[i].text;
                break
            }
        }
        return resultText;
    }

    function onProductStatus(e) {
        var record = e.record;
        var value = record.productStatus;
        var resultText = '';
        for (var i = 0; i < productStatusList.length; i++) {
            if (productStatusList[i].key_ == value) {
                resultText = productStatusList[i].text;
                break
            }
        }
        return resultText;
    }
    function onDownLineStatus(e) {
        var record = e.record;
        var value = record.downLine;
        var resultText = '';
        for (var i = 0; i < yesOrNoList.length; i++) {
            if (yesOrNoList[i].key_ == value) {
                resultText = yesOrNoList[i].text;
                break
            }
        }
        return resultText;
    }
    function onIsLxStatus(e) {
        var record = e.record;
        var value = record.isLx;
        var resultText = '';
        for (var i = 0; i < yesOrNoList.length; i++) {
            if (yesOrNoList[i].key_ == value) {
                resultText = yesOrNoList[i].text;
                break
            }
        }
        return resultText;
    }
    function onIsInvertedStatus(e) {
        var record = e.record;
        var value = record.isInverted;
        var resultText = '';
        for (var i = 0; i < yesOrNoList.length; i++) {
            if (yesOrNoList[i].key_ == value) {
                resultText = yesOrNoList[i].text;
                break;
            }
        }
        return resultText;
    }

    function onNeedModelStatus(e) {
        var record = e.record;
        var value = record.isNeedModel;
        var resultText = '';
        for (var i = 0; i < yesOrNoList.length; i++) {
            if (yesOrNoList[i].key_ == value) {
                resultText = yesOrNoList[i].text;
                break;
            }
        }
        return resultText;
    }
    function asyncPdmDelivery() {
        var result = "";
        var param = {};
        var url = __rootPath + "/rdmZhgl/core/product/asyncPdmDelivery.do";
        var resultData = ajaxRequest(url, "POST", false, param);
        if (resultData) {
            mini.alert(resultData.message);
        }
        return result;
    }

    productGrid.on("drawcell", function (e) {
        var record = e.record;
        if (!record.productId && e.field=='productModel') {
            e.cellStyle="background-color:red";
        }
    });

</script>
<redxun:gridScript gridId="productGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
