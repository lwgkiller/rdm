x
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>技术分支</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" style="display: none" onclick="saveBusiness()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="businessForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="parentId" name="parentId"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">技术分支</caption>
                <tr>
                    <td style="text-align: center;width: 20%">名称</td>
                    <td style="min-width:170px">
                        <input id="description" name="description" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">责任人</td>
                    <td style="min-width:170px">
                        <input id="liableUserId" name="liableUserId" textname="liableUser" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="1000" maxlength="1000"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessForm = new mini.Form("#businessForm");
    var parentId = "${parentId}";
    var businessId = "${businessId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}"
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

    $(function () {
        if (businessId) {
            var url = jsUseCtxPath + "/zhgl/core/patentInterpretation/technologyBranch" +
                "/queryDataById.do?businessId=" + businessId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    businessForm.setData(json);
                }
            });
        }
        //不同场景的处理
        if (action == 'detail') {
            businessForm.setEnabled(false);
        } else if (action == 'edit' || action == 'add') {
            mini.get("saveBusiness").show();
        }
    });
    //..
    function saveBusiness() {
        var postData = {};
        postData.id = mini.get("id").getValue();
        //新增的时候取传进来的parentId
        postData.parentId = mini.get("parentId").getValue();
        if (!postData.parentId || postData.parentId == '') {
            postData.parentId = parentId;
        }
        postData.description = mini.get("description").getValue();
        postData.liableUserId = mini.get("liableUserId").getValue();
        postData.liableUser = mini.get("liableUserId").getText();
        //检查必填项
        var checkResult = saveValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        $.ajax({
            url: jsUseCtxPath + "/zhgl/core/patentInterpretation/technologyBranch/saveData.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            var url = jsUseCtxPath + "/zhgl/core/patentInterpretation/technologyBranch" +
                                "/editPage.do?businessId=" + returnData.data + "&action=edit";
                            window.location.href = url;
                        }
                    });
                }
            }
        });
    }
    //..
    function saveValidCheck(postData) {
        var checkResult = {};
        if (!postData.description) {
            checkResult.success = false;
            checkResult.reason = '请填写描述！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }
</script>
</body>
</html>
