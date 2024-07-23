<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>研发降本-项目基础信息填写</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/yfjbEdit.js?version=${static_res_version}" type="text/javascript"></script>
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
    <div class="form-container" >
        <form id="infoForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    研发降本-项目基础信息
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
                        销售型号<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="saleModel" required class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        设计型号<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="designModel" emptyText="多个以英文逗号分隔" required class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        原物料编码：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="orgItemCode"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        原物料名称：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="orgItemName"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        原物料价格：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='orgItemPrice' name="orgItemPrice" class="mini-spinner"   minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        基准价格：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="basePrice"  name="basePrice" class="mini-spinner" onblur="autoFillValue"   minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        降本方式：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="costType" name="costType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="降本方式："
                               length="50"   onvaluechanged="autoFillValue"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-JBFS"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        降本措施：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="costMeasure"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        替代物料编码：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="newItemCode"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        替代物料名称：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="newItemName"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        替代物料价格：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="newItemPrice" name="newItemPrice" class="mini-spinner" onblur="autoFillValue"  maxValue="999999"  minValue="0" style="width:100%;height:34px" />
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        差额：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="differentPrice" name="differentPrice" readonly  class="mini-spinner"  maxValue="999999"  minValue="0" style="width:100%;height:34px" />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        原供应商：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="orgSupplier"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        新供应商：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="newSupplier"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        单台用量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id='perSum' name="perSum" class="mini-spinner" onblur="autoFillValue" minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        代替比例（%）：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="replaceRate" name="replaceRate" class="mini-spinner"  onblur="autoFillValue" minValue="0"  style="width:100%;height:34px" />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        单台降本：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="perCost" name="perCost" class="mini-spinner"  readonly  minValue="0"  maxValue="999999" style="width:100%;height:34px" />
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        风险评估：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="risk"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        生产是否切换：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="isReplace" name="isReplace" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="生产是否切换："
                               length="50"  onvaluechanged="autoFillValue"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        已实现单台降本：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="achieveCost" name="achieveCost" class="mini-spinner" readonly   maxValue="999999"  style="width:100%;height:34px" />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        是否需要试制：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="isSz" name="isSz" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="是否需要试制："
                               length="50"    onvaluechanged="isNeedSz"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        计划试制时间：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="jhsz_date" class="mini-monthpicker" onblur="verifyDate(jhsz_date)"  allowinput="true" style="width: 100%" name="jhsz_date"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        计划下发切换通知单时间：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="jhxfqh_date" class="mini-monthpicker"  onblur="verifyDate(jhxfqh_date)"  allowinput="true" style="width: 100%" name="jhxfqh_date"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        实际下发切换通知单时间：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="sjxfqh_date" class="mini-monthpicker"  allowinput="true" onblur="conformChangeForm()"  style="width: 100%" name="sjxfqh_date"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        计划切换时间：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="jhqh_date" class="mini-monthpicker"  onblur="verifyDate(jhqh_date)"  allowinput="true"  style="width: 100%" name="jhqh_date"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        实际切换时间：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="sjqh_date" class="mini-monthpicker"  allowinput="true"  style="width: 100%" name="sjqh_date"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        实际试制时间：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="sjsz_date" class="mini-monthpicker"  onblur="conformProduceForm()"  allowinput="true" style="width: 100%" name="sjsz_date"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        责任人：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="response" textname="responseMan" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="姓名"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        所属部门：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="deptName" single="true" initlogindep="true"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        所属专业：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="major" name="major" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="所属专业："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-SSZY"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
    </div>


</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyObj = ${applyObj};
    var action = '${action}';
    var infoForm = new mini.Form("#infoForm");
    var type = '${type}';
</script>
</body>
</html>
