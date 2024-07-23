<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>年度开发计划</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/ndkfjhPlanDetailEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
        <a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"
           onclick="saveData()">保存</a>
        <a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif"
           onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container">
        <form id="infoForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="sourceId" name="sourceId" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    年度开发计划详情
                </caption>
                <tbody>
                <tr class="firstRow displayTr">
                    <td align="center"></td>
                    <td align="left"></td>
                    <td align="center"></td>
                    <td align="left"></td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        编号：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="planCode" readonly class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        项目/产品：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="productName" readonly class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        年度目标：
                    </td>
                    <td colspan="3">
						<textarea id="target" name="target" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:80px;line-height:25px;"
                                  label="年度目标" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="true" emptytext="年度目标..." hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        计划来源：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id = "planSource"  name="planSource" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="计划来源："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=ndkfjh_source"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        来源名称：
                    </td>
                    <td colspan="1">
                        <input id="sourceName" style="width:98%;" class="mini-buttonedit" showClose="true"
                                name="sourceName" textname="sourceName" allowInput="false" onbuttonclick="selectSourceName()"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                        项目开始日期：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="startDate" name="startDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
                    </td>
                    <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                        项目结束日期：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="endDate" name="endDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        进度年月<span style="color: red">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="yearMonth" allowinput="false" class="mini-monthpicker" required="true"
                               style="width:100%;height:34px" name="yearMonth"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        当前阶段：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="currentStage" name="currentStage"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        当前阶段要求完成时间：
                    </td>
                    <td colspan="1">
                        <input id="stageFinishDate" name="stageFinishDate" class="mini-datepicker" allowInput="false"  required="false" style="width:100%;height:34px" >
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        完成率：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="finishRate"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        是否延期：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="isDelay" name="isDelay" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="是否延期："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        延期天数：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="delayDays" name="delayDays"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        备注：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="remark"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        负责人：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="chargerMan" name="chargerMan" textname="chargerManName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="负责人" length="50" mainfield="no" single="true" enabled="true"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        部门：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="chargerDept" name="chargerDept" class="mini-dep rxc" plugins="mini-dep"
                               style="width:100%;height:34px" readonly emptytext="后台自动带入..."
                               allowinput="false" textname="chargerDeptName" length="200" maxlength="200" minlen="0"
                               single="true" initlogindep="false"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        管控人：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="manager" name="manager" textname="managerName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="管控人" length="50" mainfield="no" single="true" enabled="true"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        责任所长：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="responsor" name="responsor" textname="responsorName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="责任所长" length="50" mainfield="no" single="true" enabled="true"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
    </div>
    <div id="selectProjectWindow" title="选择立项项目" class="mini-window" style="width:1000px;height:450px;"
         showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
        <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
             borderStyle="border-left:0;border-top:0;border-right:0;">
            <span style="font-size: 14px;color: #777">项目名称: </span>
            <input class="mini-textbox" width="130" id="projectName" style="margin-right: 15px"/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchProject()">查询</a>
        </div>
        <div class="mini-fit">
            <div id="projectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                 idField="id"  showColumnsMenu="false" sizeList="[5,10,20,50,100,200]" pageSize="5"
                 allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/rdmZhgl/core/ndkfjh/planDetail/listProject.do">
                <div property="columns">
                    <div type="checkcolumn" width="30"></div>
                    <div field="projectName" width="120" headerAlign="center" align="left" allowSort="false">项目名称</div>
                    <div field="startDate" width="80" headerAlign="center" align="center" allowSort="false">项目开始时间</div>
                    <div field="endDate" width="80" headerAlign="center" align="center" allowSort="false">项目结束时间</div>
                    <div field="currentStage" width="80" headerAlign="center" align="center" allowSort="false">当前阶段</div>
                    <div field="stageFinishDate" width="120" headerAlign="center" align="center" allowSort="false">当前阶段要求完成时间</div>
                    <div field="isDelay" renderer="onDelay" width="60" headerAlign="center" align="center" allowSort="false">是否延期</div>
                    <div field="delayDays" width="60" headerAlign="center" align="center" allowSort="false">延期天数</div>
                </div>
            </div>
        </div>
        <div property="footer" style="padding:5px;height: 35px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectProjectOK()"/>
                        <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectProjectHide()"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div id="selectNewProductWindow" title="选择新品项目" class="mini-window" style="width:1000px;height:450px;"
         showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
        <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
             borderStyle="border-left:0;border-top:0;border-right:0;">
            <span style="font-size: 14px;color: #777">产品设计型号: </span>
            <input class="mini-textbox" width="130" id="productModel" style="margin-right: 15px"/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchNewProduct()">查询</a>
        </div>
        <div class="mini-fit">
            <div id="newProductListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                 idField="id"  showColumnsMenu="false" sizeList="[5,10,20,50,100,200]" pageSize="5"
                 allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/rdmZhgl/core/ndkfjh/planDetail/listNewProduct.do">
                <div property="columns">
                    <div type="checkcolumn" width="30"></div>
                    <div field="productType" width="100" headerAlign="center" align="center" allowSort="false">产品类型</div>
                    <div field="productModel" width="100" headerAlign="center" align="center" allowSort="false">产品设计型号</div>
                    <div field="startDate" width="70" headerAlign="center" align="center" allowSort="false">项目开始时间</div>
                    <div field="endDate" width="80" headerAlign="center" align="center" allowSort="false">项目结束时间</div>
                    <div field="currentStage" width="120" headerAlign="center" align="center" allowSort="false">当前阶段</div>
                    <div field="stageFinishDate" width="120" headerAlign="center" align="center" allowSort="false">当前阶段要求完成时间</div>
                    <div field="isDelay" renderer="onDelay" width="60" headerAlign="center" align="center" allowSort="false">是否延期</div>
                    <div field="delayDays" width="60" headerAlign="center" align="center" allowSort="false">延期天数</div>
                </div>
            </div>
        </div>
        <div property="footer" style="padding:5px;height: 35px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectNewProductOK()"/>
                        <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectNewProductHide()"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div id="selectSpecialOrderWindow" title="选择特殊订单" class="mini-window" style="width:800px;height:450px;"
         showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
        <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
             borderStyle="border-left:0;border-top:0;border-right:0;">
            <span style="font-size: 14px;color: #777">产品型号: </span>
            <input class="mini-textbox" width="130" id="model" style="margin-right: 15px"/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchSpecialOrder()">查询</a>
        </div>
        <div class="mini-fit">
            <div id="specialOrderListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
                 idField="id"  showColumnsMenu="false" sizeList="[5,10,20,50,100,200]" pageSize="5"
                 allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/rdmZhgl/core/ndkfjh/planDetail/listSpecialOrder.do">
                <div property="columns">
                    <div type="checkcolumn" width="30"></div>
                    <div field="ckMonth" width="100" headerAlign="center" align="center" allowSort="false">出口日期</div>
                    <div field="model" width="100" headerAlign="center" align="center" allowSort="false">产品型号</div>
                    <div field="country" width="80" headerAlign="center" align="center" allowSort="false">出口国家</div>
                    <div field="ddNumber" width="70" headerAlign="center" align="center" allowSort="false">需求数量</div>
                    <div field="cpzg" width="80" headerAlign="center" align="center" allowSort="false">产品主管</div>
                </div>
            </div>
        </div>
        <div property="footer" style="padding:5px;height: 35px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectSpecialOrderOK()"/>
                        <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectSpecialOrderHide()"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyObj = ${applyObj};
    var action = '${action}';
    var infoForm = new mini.Form("#infoForm");
    var selectProjectWindow=mini.get("selectProjectWindow");
    var projectListGrid=mini.get("projectListGrid");
    var selectNewProductWindow=mini.get("selectNewProductWindow");
    var newProductListGrid=mini.get("newProductListGrid");
    var selectSpecialOrderWindow=mini.get("selectSpecialOrderWindow");
    var specialOrderListGrid=mini.get("specialOrderListGrid");
    var yesOrNo = getDics("YESORNO");
    var permission = "${permission}";
    function onDelay(e) {
        var record = e.record;
        var isDelay = record.isDelay;
        var resultText = '';
        for (var i = 0; i < yesOrNo.length; i++) {
            if (yesOrNo[i].key_ == isDelay) {
                resultText = yesOrNo[i].text;
                break
            }
        }
        return resultText;
    }
</script>
</body>
</html>
