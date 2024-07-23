<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>总项目分解</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/scripts/jsmind/jsmind.css" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/jsmind/jsmind.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/jsmind/jsmind.draggable-node.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/drbfm/totalDecompose.js?version=${static_res_version}"
            type="text/javascript"></script>

</head>
<body>
<div class="topToolBar">
    <div>
        <a id="savePlan" class="mini-button" onclick="">保存草稿</a>
        <a id="commitPlan" class="mini-button" onclick="">提交</a>
        <a class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="verifyPlanForm" method="post" style="height: 50px;width: 100%">
            <input class="mini-hidden" id="id" name="id" />
            <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 8%">机型<span style="color:red">*</span></td>
                    <td style="width: 150px">
                        <input id="jixing"  name="jixing"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="text-align: center;width: 8%">风险分析名称<span style="color:red">*</span></td>
                    <td style="width: 300px">
                        <input id="analyseName"  name="analyseName"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="text-align: center;width: 8%">关联的科技项目：</td>
                    <td >
                        <input id="projectId"  name="projectId"  class="mini-combobox" style="width:98%;"
                               textField="projectName" valueField="projectId" emptyText="请选择..."
                               required="false" allowInput="false" />
                    </td>
                </tr>
            </table>
        </form>
        <p style="font-size: 16px;font-weight: bold;margin-top: 5px">结构分解</p>
        <hr>
        <a class="mini-button" onclick="getData" >获取数据</a>
        <div id="jsmind_container" style="border: #ccc 1px solid;width: 99%;height:85%;background: #f4f4f4;"></div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var verifyPlanForm = new mini.Form("#verifyPlanForm");

    function editNode() {
        var selected_node = jm.get_selected_node();
        if (!!selected_node) {
            mini.alert("id:"+selected_node.id+",topic:"+selected_node.topic);
        }
    }

    function getData() {
        var mind_data = jm.get_data("node_array");
        var mind_string = jsMind.util.json.json2string(mind_data);
        mini.alert(mind_string)
    }
</script>
</body>
</html>
