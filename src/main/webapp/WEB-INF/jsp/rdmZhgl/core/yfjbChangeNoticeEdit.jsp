<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>研发降本-切换通知单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/yfjbChangeNoticeEdit.js?version=${static_res_version}" type="text/javascript"></script>
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
            <input id="mainId" name="mainId" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    研发降本-切换通知单
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
                        原供应商<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="orgSupplier" required="true" readonly class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        新供应商<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="newSupplier"  required="true" readonly class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        切换通知号：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="noticeNo"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        切换通知单下发时间<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="noticeDate"  required="true" class="mini-datepicker"  allowinput="false" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        互换性：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="changeable"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        主要差异性、试制要求、</br>竞品使用情况：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="assessment"  class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        通知单中库存</br>及切换车号：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="storageAndCar"  class="mini-textbox rxc" style="width:100%;height:34px">
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
