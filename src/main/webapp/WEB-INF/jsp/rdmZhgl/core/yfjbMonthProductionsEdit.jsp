<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>研发降本-月度产量维护</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/yfjbMonthProductionsEdit.js?version=${static_res_version}" type="text/javascript"></script>
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
                    研发降本-月度产量维护
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
                        所属年份<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="productYear" name="productYear" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px"  label="年度："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                               textField="text" valueField="value" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        整机型号<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="productModel"   required class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        一月计划产量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='january' name="january" class="mini-spinner"  minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        二月计划产量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='february' name="february" class="mini-spinner"  minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        三月计划产量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='march' name="march" class="mini-spinner"  minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        四月计划产量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='april' name="april" class="mini-spinner"  minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        五月计划产量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='may' name="may" class="mini-spinner"  minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        六月计划产量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='june' name="june" class="mini-spinner"  minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        七月计划产量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='july' name="july" class="mini-spinner"  minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        八月计划产量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='august' name="august" class="mini-spinner"  minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        九月计划产量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='september' name="september" class="mini-spinner"  minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        十月计划产量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='october' name="october" class="mini-spinner"  minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        十一月计划产量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='november' name="november" class="mini-spinner"  minValue="0" maxValue="999999" style="width:100%;height:34px" />
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        十二月计划产量：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input  id='december' name="december" class="mini-spinner"  minValue="0" maxValue="999999" style="width:100%;height:34px" />
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
</script>
</body>
</html>
