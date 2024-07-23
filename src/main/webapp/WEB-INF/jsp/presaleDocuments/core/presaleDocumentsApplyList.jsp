<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>售前文件申请列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<%----%>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请编号: </span>
                    <input class="mini-textbox" id="businessNo" name="businessNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">关联销售型号：</span>
                    <input class="mini-textbox" id="saleModel" name="saleModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">关联设计型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">关联物料号：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">关联产品主管：</span>
                    <input class="mini-textbox" id="productManagerName" name="productManagerName"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">责任人：</span>
                    <input class="mini-textbox" id="repUserName" name="repUserName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">语种：</span>
                    <input class="mini-textbox" id="docLanguage" name="docLanguage"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售区域：</span>
                    <input class="mini-textbox" id="salesArea" name="salesArea"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请时间：</span>
                    <input id="applyTimeBegin" name="applyTimeBegin" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="applyTimeEnd" name="applyTimeEnd" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">结束时间：</span>
                    <input id="endTimeBegin" name="endTimeBegin" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="endTimeEnd" name="endTimeEnd" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">当前节点：</span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:98%"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="removeBusiness()">删除</a>
                    <%--<a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="testBusiness()">测试直接创建翻译</a>--%>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<%----%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons" sortField="applyTime" sortOrder="desc"
         url="${ctxPath}/presaleDocuments/core/apply/dataListQuery.do?businessType=${businessType}">
        <div property="columns">
            <div type="checkcolumn" width="25"></div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="status" width="70" headerAlign="center" align="center" renderer="onStatusRenderer2">流程状态</div>
            <div field="businessNo" width="130" headerAlign="center" align="center" allowSort="true">单号</div>
            <div field="repUserName" width="120" headerAlign="center" align="center" allowSort="true">负责人</div>
            <div field="salesArea" width="120" headerAlign="center" align="center" allowSort="true">销售区域</div>
            <div field="docLanguage" width="120" headerAlign="center" align="center" allowSort="true">语种</div>
            <div field="systemType" width="120" headerAlign="center" align="center" allowSort="true">系统分类</div>
            <div field="saleModel" width="120" headerAlign="center" align="center" allowSort="false"
                 renderer="productRenderer">销售型号
            </div>
            <div field="designModel" width="120" headerAlign="center" align="center" allowSort="false"
                 renderer="productRenderer">设计型号
            </div>
            <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="false"
                 renderer="productRenderer">物料编码
            </div>
            <div field="productManagerName" width="120" headerAlign="center" align="center" allowSort="false"
                 renderer="productRenderer">产品主管
            </div>
            <div field="applicabilityStatement" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">适用性说明</div>
            <div field="applyUserName" width="120" headerAlign="center" align="center" allowSort="true">申请人</div>
            <div field="applyTime" width="120" headerAlign="center" align="center" allowSort="true">申请时间</div>
            <div field="endTime" width="120" headerAlign="center" align="center" allowSort="true">结束时间</div>
            <div field="businessStatus" width="120" headerAlign="center" align="center" renderer="onStatusRenderer">当前节点</div>
            <div field="allTaskUserNames" sortField="allTaskUserNames" width="100" align="center" headerAlign="center" allowSort="false">当前处理人</div>
        </div>
    </div>
</div>
<%--关联产品小窗口--%>
<div id="productkWindow" title="关联产品" class="mini-window" style="width:950px;height:400px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div id="productListGrid" class="mini-datagrid" style="width: 100%; height: 98%" allowResize="false" allowCellWrap="true"
             showCellTip="true" idField="id" showPager="false" showColumnsMenu="false" allowcelledit="false"
             allowAlternating="true">
            <div property="columns">
                <%--id--%>
                <div field="saleModel_item" width="100" headerAlign="center" align="center">销售型号</div>
                <div field="designModel_item" width="100" headerAlign="center" align="center">设计型号</div>
                <div field="materialCode_item" width="100" headerAlign="center" align="center">物料编码</div>
                <%--productManagerId_item--%>
                <div field="productManagerName_item" width="100" headerAlign="center" align="center">产品主管</div>
            </div>
        </div>
    </div>
</div>
<%----%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var nodeSetListWithName = '${nodeSetListWithName}';
    var businessType = "${businessType}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var businessListGrid = mini.get("businessListGrid");
    var productkWindow = mini.get("productkWindow");
    var productListGrid = mini.get("productListGrid");
    //..
    $(function () {
        mini.get("businessStatus").setData(mini.decode(nodeSetListWithName));
    });
    //..行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var instId = record.instId;
        var s = '';
        s += '<span  title="明细" onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">明细</span>';
        if (record.status == 'DRAFTED' && (currentUserId == record.applyUserId || currentUserNo == 'admin')) {
            s += '<span  title="编辑" onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">编辑</span>';
        } else {
            if (record.myTaskId || currentUserNo == 'admin') {
                s += '<span  title="办理" onclick="businessTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (currentUserNo != 'admin') {
            if (record.status == 'DRAFTED' && currentUserId == record.applyUserId) {
                s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
    //..（后台根据配置的表单进行跳转）
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/PresaleDocumentApply/start.do?businessType_=${businessType}";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }
    //..编辑行数据流程（后台根据配置的表单进行跳转）
    function businessEdit(businessId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }
    //..
    function businessDetail(businessId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/presaleDocuments/core/apply/editPage.do?action=" + action +
            "&businessId=" + businessId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }
    //..跳转到任务的处理界面
    function businessTask(taskId) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            success: function (result) {
                if (!result.success) {
                    top._ShowTips({
                        msg: result.message
                    });
                } else {
                    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                    var winObj = openNewWindow(url, "handTask");
                    var loop = setInterval(function () {
                        if (winObj.closed) {
                            clearInterval(loop);
                            if (businessListGrid) {
                                businessListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }
    //..
    function removeBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                var haveSomeRowsWrong = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.status == 'DRAFTED' && currentUserId == r.applyUserId) {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else if (currentUserNo == 'admin') {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else {
                        haveSomeRowsWrong = true;
                        break;
                    }
                }
                if (haveSomeRowsWrong) {
                    mini.alert("仅草稿状态数据可由本人删除,或者admin可以强制删除");
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/presaleDocuments/core/apply/deleteBusiness.do",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(','), instIds: instIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
    //..
    function onStatusRenderer(e) {
        var record = e.record;
        var businessStatus = record.businessStatus;
        var arr = mini.decode(nodeSetListWithName);
        return $.formatItemValue(arr, businessStatus);
    }
    //..流程状态渲染
    function onStatusRenderer2(e) {
        var record = e.record;
        var status = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常终止', 'css': 'red'}
        ];
        return $.formatItemValue(arr, status);
    }
    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            if (e.record.publishTime != null && e.record.publishTime != "" && e.record.businessStatus != "G-close" &&
                new Date().subtract(e.record.publishTime.parseDate()) > 0 && e.record.status != 'DISCARD_END') {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #ef1b01" >' + e.value + '</span>';
            } else if (e.record.publishTime != null && e.record.publishTime != "" && e.record.businessStatus != "G-close" &&
                new Date().subtract(e.record.publishTime.parseDate()) > -3 && e.record.status != 'DISCARD_END') {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #eecc53" >' + e.value + '</span>';
            } else {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            }
            return html;
        }
    }
    //..
    function productRenderer(e) {
        if (e.value != null && e.value != "") {
            var productSpectrum = e.productSpectrum;
            var html = '<span onclick="detailProduct()" style="color: blue;text-decoration: underline;cursor: pointer;">' + e.value + '</span>';
            return html;
        }
    }
    //..
    function detailProduct(productSpectrum) {
        var record = businessListGrid.getSelected();
        if (record != null) {
            var Array = JSON.parse(record.productSpectrum);
            productListGrid.setData(Array);
            productkWindow.show();
        }
    }
    //..测试直接创建翻译
    function testBusiness(){
        var rows = businessListGrid.getSelecteds();
        var rowIds = [];
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            rowIds.push(r.id);
        }
        _SubmitJson({
            url: jsUseCtxPath + "/presaleDocuments/core/apply/testBusiness.do",
            method: 'POST',
            data: {ids: rowIds.join(',')},
            postJson: false,
            showMsg: false,
            success: function (returnData) {
                if (returnData.success) {
                    mini.alert(returnData.message);
                    searchFrm();
                } else {
                    mini.alert("失败:" + returnData.message);
                }
            },
            fail: function (returnData) {
                mini.alert("失败:" + returnData.message);
            }
        });
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>