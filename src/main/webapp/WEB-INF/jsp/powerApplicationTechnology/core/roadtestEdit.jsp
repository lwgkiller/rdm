<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>路试项目主信息编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" onclick="saveBusiness()">保存</a>
        <a id="synchGps" class="mini-button" onclick="synchGps()">手工同步GPS</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="businessForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="isClose" name="isClose" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    路试项目主信息
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">整机型号：</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">整机编号：</td>
                    <td style="min-width:170px">
                        <input id="pin" name="pin" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">发动机型号：</td>
                    <td style="min-width:170px">
                        <input id="engineModel" name="engineModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">位置：</td>
                    <td style="min-width:170px">
                        <input id="location" name="location" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">路试状态：</td>
                    <td style="min-width:170px">
                        <input id="roadtestStatus" name="roadtestStatus" class="mini-combobox" align="center"
                               textField="key" valueField="value" style="width:98%;"
                               data="[ {'key' : '已发','value' : '已发'},{'key' : '未发','value' : '未发'}]"/>
                    </td>
                    <td style="text-align: center;width: 15%">路试类型：</td>
                    <td style="min-width:170px">
                        <input id="roadtestType" name="roadtestType"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=roadtestType"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">路试开始日期：</td>
                    <td style="min-width:170px">
                        <input id="roadtestBeginDate" name="roadtestBeginDate" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="true" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">路试结束日期：</td>
                    <td style="min-width:170px">
                        <input id="roadtestEndDate" name="roadtestEndDate" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="true" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">路试内容：</td>
                    <td style="min-width:170px">
                        <input id="roadtestContent" name="roadtestContent" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">日均工作小时(目标)：</td>
                    <td style="min-width:170px">
                        <input id="targetDailyAverageWorkingHours" name="targetDailyAverageWorkingHours"
                               class="mini-spinner" style="width:98%;" minValue="0" maxValue="24"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">剩余天数预计日均工作小时(目标)：</td>
                    <td style="min-width:170px">
                        <input id="targetDailyAverageWorkingHoursRemaining" name="targetDailyAverageWorkingHoursRemaining"
                               class="mini-spinner" style="width:98%;" minValue="0" maxValue="24" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">日均工作小时(实际)：</td>
                    <td style="min-width:170px">
                        <input id="actualDailyAverageWorkingHours" name="actualDailyAverageWorkingHours"
                               class="mini-spinner" style="width:98%;" minValue="0" maxValue="24" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">累计工作时间(h)：</td>
                    <td style="min-width:170px">
                        <input id="cumulativeWorkingHours" name="cumulativeWorkingHours"
                               class="mini-spinner" style="width:98%;" minValue="0" maxValue="100000" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">累计油耗(L)：</td>
                    <td style="min-width:170px">
                        <input id="cumulativeFuelConsumption" name="cumulativeFuelConsumption"
                               class="mini-spinner" style="width:98%;" minValue="0" maxValue="100000" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">累计尿素消耗(L)：</td>
                    <td style="min-width:170px">
                        <input id="cumulativeUreaConsumption" name="cumulativeUreaConsumption"
                               class="mini-spinner" style="width:98%;" minValue="0" maxValue="100000" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">每小时油耗(L)：</td>
                    <td style="min-width:170px">
                        <input id="perhourFuelConsumption" name="perhourFuelConsumption"
                               class="mini-spinner" style="width:98%;" minValue="0" maxValue="100000" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">每小时尿素消耗(L)：</td>
                    <td style="min-width:170px">
                        <input id="perhourUreaConsumption" name="perhourUreaConsumption"
                               class="mini-spinner" style="width:98%;" minValue="0" maxValue="100000" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">计算日期：</td>
                    <td style="min-width:170px">
                        <input id="calculateDate" name="calculateDate" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="true" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 500px">日工作信息：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <span class="text" style="width:auto">开始时间：</span>
                            <input id="beginDate" class="mini-textbox"/>
                            <span class="text" style="width:auto">结束时间：</span>
                            <input id="endDate" class="mini-textbox"/>
                            <a id="searchDaily" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="searchDaily">筛选</a>
                            <a id="saveDaily" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="saveDaily">保存修改</a>
                        </div>
                        <div id="dailyListGrid" class="mini-datagrid" style="width: 100%; height: 92%" allowResize="true" allowCellWrap="true"
                             idField="id"
                             multiSelect="false" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                             allowAlternating="true" autoLoad="false">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div type="indexcolumn" headerAlign="center" align="center" width="40">序号</div>
                                <div field="theDate" align="center" headerAlign="center" width="200" renderer="render">日期</div>
                                <div field="crmStatus" align="center" headerAlign="center" width="200" renderer="render">CRM车辆状态
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="crmStatusNeg" align="center" headerAlign="center" width="200" renderer="render">非CRM车辆状态
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="dailyWorkingHours" align="center" headerAlign="center" width="100" renderer="render">当天工作小时数(h)</div>
                                <div field="dailyFuelConsumption" align="center" headerAlign="center" width="100" renderer="render">当天柴油消耗量(L)</div>
                                <div field="dailyUreaConsumption" align="center" headerAlign="center" width="100" renderer="render">当天尿素消耗量(L)</div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 500px">测试数据收集：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <a id="addTestdata" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addTestdata">添加</a>
                            <a id="editTestdata" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="editTestdata">编辑</a>
                            <a id="detailTestdata" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="detailTestdata">浏览</a>
                            <a id="deleteTestdata" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="deleteTestdata">删除</a>
                        </div>
                        <div id="testdataListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="true" allowCellWrap="true"
                             idField="id" url="${ctxPath}/powerApplicationTechnology/core/roadtest/getTestdataList.do?businessId=${businessId}"
                             multiSelect="false" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                             allowAlternating="true" autoLoad="false">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div type="indexcolumn" headerAlign="center" align="center" width="40">序号</div>
                                <div field="testName" align="center" headerAlign="center" width="200" renderer="render">测试名称</div>
                                <div field="testContent" align="center" headerAlign="center" width="100" renderer="render">测试内容</div>
                                <div field="testTimeSpan" align="center" headerAlign="center" width="120" renderer="render">测试时间区间</div>
                                <div field="remark" align="center" headerAlign="center" width="100" renderer="render">备注</div>
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
    var businessForm = new mini.Form("#businessForm");
    var dailyListGrid = mini.get("dailyListGrid");
    var testdataListGrid = mini.get("testdataListGrid");
    var businessId = "${businessId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date();
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    //..
    $(function () {
        if (businessId) {
            var url = jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/getDetail.do";
            $.post(
                url,
                {businessId: businessId},
                function (json) {
                    businessForm.setData(json);
                    if (action == 'detail') {
                        businessForm.setEnabled(false);
                        mini.get("saveBusiness").setEnabled(false);
                        mini.get("synchGps").setEnabled(false);
                        mini.get("saveDaily").setEnabled(false);
                        mini.get("addTestdata").setEnabled(false);
                        mini.get("editTestdata").setEnabled(false);
                        mini.get("deleteTestdata").setEnabled(false);
                        mini.get("beginDate").setEnabled(true);
                        mini.get("endDate").setEnabled(true);
                        mini.get("addTestdata").setEnabled(true);
                        mini.get("editTestdata").setEnabled(true);
                        mini.get("deleteTestdata").setEnabled(true);
                    }
                });
            var beginDate = getDay(-9);
            mini.get("beginDate").setValue(beginDate);
            var endDate = getDay(0);
            mini.get("endDate").setValue(endDate);
            url = "${ctxPath}/powerApplicationTechnology/core/roadtest/getDailyList.do?businessId=${businessId}" +
                "&beginDate=" + beginDate + "&endDate=" + endDate;
            dailyListGrid.setUrl(url);
            dailyListGrid.load();
            testdataListGrid.load();
        }
    });
    //..
    function saveBusiness() {
        var postData = {};
        postData.id = mini.get("id").getValue();
        postData.isClose = mini.get("isClose").getValue();
        postData.designModel = mini.get("designModel").getValue();
        postData.pin = mini.get("pin").getValue();
        postData.engineModel = mini.get("engineModel").getValue();
        postData.location = mini.get("location").getValue();
        postData.roadtestStatus = mini.get("roadtestStatus").getValue();
        postData.roadtestBeginDate = mini.get("roadtestBeginDate").getText();
        postData.roadtestEndDate = mini.get("roadtestEndDate").getText();
        postData.roadtestType = mini.get("roadtestType").getValue();
        postData.roadtestContent = mini.get("roadtestContent").getValue();
        postData.targetDailyAverageWorkingHours = mini.get("targetDailyAverageWorkingHours").getValue();
        postData.targetDailyAverageWorkingHoursRemaining = mini.get("targetDailyAverageWorkingHoursRemaining").getValue();
        postData.actualDailyAverageWorkingHours = mini.get("actualDailyAverageWorkingHours").getValue();
        postData.cumulativeWorkingHours = mini.get("cumulativeWorkingHours").getValue();
        postData.cumulativeFuelConsumption = mini.get("cumulativeFuelConsumption").getValue();
        postData.cumulativeUreaConsumption = mini.get("cumulativeUreaConsumption").getValue();
        postData.perhourFuelConsumption = mini.get("perhourFuelConsumption").getValue();
        postData.perhourUreaConsumption = mini.get("perhourUreaConsumption").getValue();
        postData.calculateDate = mini.get("calculateDate").getText();
        //检查必填项
        var checkResult = saveValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        $.ajax({
            url: jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/saveBusiness.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            var url = jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/editPage.do?businessId=" +
                                returnData.data + "&action=edit";
                            window.location.href = url;
                        }
                    });
                }
            }
        });
    }
    //..
    function synchGps() {
        if (!businessId) {
            mini.alert("请先点击‘保存’进行路试计划创建！");
            return;
        }
        mini.confirm("确定手工同步记录？如果确定:已有的当天【日工作信息】会被覆盖，请慎重点击!!!", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/synchGPS.do",
                    method: 'POST',
                    showMsg: false,
                    data: {id: businessId},
                    success: function (returnData) {
                        if (returnData) {
                            if (returnData && returnData.message) {
                                mini.alert(returnData.message, '提示', function () {
                                    if (returnData.success) {
                                        var url = jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/editPage.do?businessId=" +
                                            returnData.data + "&action=edit";
                                        window.location.href = url;
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }
    //..
    function searchDaily() {
        var beginDate = mini.get("beginDate").getText();
        var endDate = mini.get("endDate").getText();
        var url = "${ctxPath}/powerApplicationTechnology/core/roadtest/getDailyList.do?businessId=${businessId}" +
            "&beginDate=" + beginDate + "&endDate=" + endDate;
        dailyListGrid.setUrl(url);
        dailyListGrid.load();
    }
    //..
    function saveDaily() {
        if (!businessId) {
            mini.alert("请先点击‘保存’进行路试计划创建！");
            return;
        }
        var postData = dailyListGrid.data;
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/saveDaily.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            dailyListGrid.reload();
                        }
                    });
                }
            }
        });
    }
    //..
    function addTestdata() {
        if (!businessId) {
            mini.alert("请先点击‘保存’进行路试计划创建！");
            return;
        }
        var url = jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/testdataPage.do?mainId=" + businessId + "&action=add";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (testdataListGrid) {
                    testdataListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function editTestdata() {
        var row = testdataListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        if (currentUserId != row.CREATE_BY_) {
            mini.alert("只有自己创建的记录能编辑");
            return;
        }
        var businessId = row.id;
        var url = jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/testdataPage.do?businessId=" + businessId + "&action=edit";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (testdataListGrid) {
                    testdataListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function detailTestdata() {
        var row = testdataListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        var businessId = row.id;
        var url = jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/testdataPage.do?businessId=" + businessId + "&action=detail";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (testdataListGrid) {
                    testdataListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function deleteTestdata() {
        var row = testdataListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        if (row.id == "") {
            delRowGrid("testdataListGrid");
            return;
        }
        if (currentUserId != row.CREATE_BY_) {
            mini.alert("只有自己创建的记录能删除");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                _SubmitJson({
                    url: jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/deleteTestdata.do",
                    method: 'POST',
                    showMsg: false,
                    data: {id: id},
                    success: function (returnData) {
                        if (returnData) {
                            if (returnData && returnData.message) {
                                mini.alert(returnData.message, '提示', function () {
                                    if (returnData.success) {
                                        testdataListGrid.reload();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }
    //..
    function saveValidCheck(postData) {
        var checkResult = {};
        if (!postData.designModel) {
            checkResult.success = false;
            checkResult.reason = '请填写整机型号！';
            return checkResult;
        }
        if (!postData.pin) {
            checkResult.success = false;
            checkResult.reason = '请填写整机编号！';
            return checkResult;
        }
        if (!postData.engineModel) {
            checkResult.success = false;
            checkResult.reason = '请填写发动机型号！';
            return checkResult;
        }
        if (!postData.location) {
            checkResult.success = false;
            checkResult.reason = '请填写位置！';
            return checkResult;
        }
        if (!postData.roadtestStatus) {
            checkResult.success = false;
            checkResult.reason = '请选择路试状态！';
            return checkResult;
        }
        if (!postData.roadtestType) {
            checkResult.success = false;
            checkResult.reason = '请选择路试类型！';
            return checkResult;
        }
        if (!postData.roadtestBeginDate) {
            checkResult.success = false;
            checkResult.reason = '请选择路试开始日期！';
            return checkResult;
        }
        if (!postData.roadtestEndDate) {
            checkResult.success = false;
            checkResult.reason = '请选择路试结束日期！';
            return checkResult;
        }
        if (!postData.roadtestContent) {
            checkResult.success = false;
            checkResult.reason = '请填写路试内容！';
            return checkResult;
        }
        if (!postData.targetDailyAverageWorkingHours) {
            checkResult.success = false;
            checkResult.reason = '请填写日均工作小时(目标)！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }
    //@lwgkiller:js时间处理
    function getDay(day) {
        var today = new Date();
        var targetday_milliseconds = today.getTime() + 1000 * 60 * 60 * 24 * day;
        today.setTime(targetday_milliseconds); //注意，这行是关键代码
        var tYear = today.getFullYear();
        var tMonth = today.getMonth();
        var tDate = today.getDate();
        tMonth = doHandleMonth(tMonth + 1);
        tDate = doHandleMonth(tDate);
        return tYear + "-" + tMonth + "-" + tDate;
    }
    //@lwgkiller:js时间处理
    function doHandleMonth(month) {
        var m = month;
        if (month.toString().length == 1) {
            m = "0" + month;
        }
        return m;
    }
</script>
</body>
</html>
