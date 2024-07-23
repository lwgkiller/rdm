<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" onclick="saveBusiness">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="businessForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="businessStatus" name="businessStatus"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">客户拜访记录信息编辑</caption>
                <tr>
                    <td style="text-align: center;width: 15%">被拜访公司：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="companyVisited" name="companyVisited" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">被访人：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="personVisited" name="personVisited" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">拜访目的：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <input id="purposeVisited" name="purposeVisited" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">访问开始时间：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="dateVisitedBegin" name="dateVisitedBegin" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">访问结束时间：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="dateVisitedEnd" name="dateVisitedEnd" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">达成建议：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <input id="proposal" name="proposal" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">任务分解：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="task" name="task" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="400" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">编制：</td>
                    <td style="min-width:170px">
                        <input id="creator" name="creator" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">任务执行人：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="taskExecutorId" name="taskExecutorId" textname="taskExecutor" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="任务执行人" length="50"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
                <%--itemListGrid--------------------------------------------------------------------%>
                <tr>
                    <td style="text-align: center;height: 400px">任务完成情况跟踪记录：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons" style="display: none">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem">添加记录</a>
                            <a id="removeItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="removeItem">删除记录</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="false" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
                                <div field="trackingDate_item" width="50" headerAlign="center" align="center" renderer="render">跟踪日期
                                    <input id="trackingDate_item" property="editor" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                                           valueType="string" showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                                </div>
                                <div field="completion_item" width="150" headerAlign="center" align="center" renderer="render">完成情况
                                    <input id="completion_item" property="editor" class="mini-textbox" style="width:98%;"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessId = "${businessId}";
    var action = "${action}";
    var businessForm = new mini.Form("#businessForm");
    var itemListGrid = mini.get("itemListGrid");
    var currentUserId = "${currentUserId}"
    var currentUserName = "${currentUserName}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    //..
    $(function () {
        if (businessId) {
            var url = jsUseCtxPath + "/world/core/customerVisitRecord/queryDataById.do?businessId=" + businessId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    businessForm.setData(json);
                    var recordItems = JSON.parse(json.recordItems);
                    itemListGrid.setData(recordItems);
                    if (action == 'detail') {
                        businessForm.setEnabled(false);
                        mini.get("saveBusiness").setEnabled(false);
                    } else if (action == 'edit' && json.businessStatus == "执行中") {
                        businessForm.setEnabled(false);
                        mini.get("itemButtons").show();
                    }
                }
            });
        } else if (action == 'add') {

        }
    });
    //..
    function saveBusiness() {
        var postData = _GetFormJsonMini("businessForm");
        var recordItems = itemListGrid.getData();
        if (recordItems.length > 0) {
            postData.recordItems = recordItems;
        }
        //检查必填项
        var checkResult = saveValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        $.ajax({
            url: jsUseCtxPath + "/world/core/customerVisitRecord/saveBusiness.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            var url = jsUseCtxPath + "/world/core/customerVisitRecord/editPage.do?businessId=" +
                                returnData.data + "&action=edit";
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
        if (!postData.companyVisited) {
            checkResult.success = false;
            checkResult.reason = '被拜访公司不能为空';
            return checkResult;
        }
        if (!postData.personVisited) {
            checkResult.success = false;
            checkResult.reason = '被访人不能为空！';
            return checkResult;
        }
        if (!postData.purposeVisited) {
            checkResult.success = false;
            checkResult.reason = '拜访目的不能为空！';
            return checkResult;
        }
        if (!postData.dateVisitedBegin) {
            checkResult.success = false;
            checkResult.reason = '访问开始时间不能为空！';
            return checkResult;
        }
        if (!postData.dateVisitedEnd) {
            checkResult.success = false;
            checkResult.reason = '访问结束时间不能为空！';
            return checkResult;
        }
        if (!postData.proposal) {
            checkResult.success = false;
            checkResult.reason = '达成建议不能为空！';
            return checkResult;
        }
        if (!postData.task) {
            checkResult.success = false;
            checkResult.reason = '任务分解不能为空！';
            return checkResult;
        }
        if (!postData.taskExecutorId) {
            checkResult.success = false;
            checkResult.reason = '任务执行人不能为空！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }
    //..
    function addItem() {
        var newRow = {}
        itemListGrid.addRow(newRow, 0);
    }
    //..
    function removeItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        delRowGrid("itemListGrid");
    }
</script>
</body>
</html>
