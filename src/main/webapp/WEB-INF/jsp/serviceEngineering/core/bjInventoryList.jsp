<%--
  Created by IntelliJ IDEA.
  User: matianyu
  Date: 2021/2/23
  Time: 14:57
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机物料编码: </span>
                    <input class="mini-textbox" id="materielCode" name="materielCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="designModelName" name="designModelName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号: </span>
                    <input class="mini-textbox" id="saleModel" name="saleModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">编制人: </span>
                    <input class="mini-textbox" id="applyName" name="applyName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">流程状态: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '审批完成'},
							   {'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addBjInventory()">新增</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="bjInventoryListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/serviceEngineer/core/BjInventory/queryBjInventory.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="false">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="40" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="processNum" headerAlign='center' align='center' width="50">单据编号</div>
            <div field="saleModel"  width="40" headerAlign="center" align="center" allowSort="true">销售型号</div>
            <div field="materielCode"  width="30" headerAlign="center" align="center" allowSort="true">整机物料编码</div>
            <div field="designModelName"  width="40" headerAlign="center" align="center" allowSort="true">设计型号</div>
            <div field="machineNum"  width="40" headerAlign="center" align="center" allowSort="true">整机编号</div>
            <div field="needTime" headerAlign='center' width="30" align='center' dateFormat="yyyy-MM-dd">需求时间</div>
            <div field="needLanguage"  width="30" headerAlign="center" align="center" allowSort="true">需求翻译语言</div>
            <div field="applyName" headerAlign='center' align='center' width="30">申请人</div>
            <div field="CREATE_TIME_" width="40" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">申请时间</div>
            <div field="taskName" headerAlign='center' align='center' width="60">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="30">当前处理人</div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
<%--            <div field="UPDATE_TIME_" width="40" dateFormat="yyyy-MM-dd" headerAlign='center' align="center" renderer="finishTime">流程结束时间</div>--%>
        </div>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/serviceEngineer/core/BjInventory/exportBjInventoryList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var bjInventoryListGrid = mini.get("bjInventoryListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";

    function finishTime(e) {
        var record = e.record;
        var status = record.status;
        if(status != 'SUCCESS_END'){
            var s = '<span></span>';
        }else {
            var s = '<span>'+record.UPDATE_TIME_+'</span>';
        }
        return s;
    }

    function onMessageActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="bjInventoryDetail(\'' + id + '\',\'' + status + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="bjInventoryEdit(\'' + id + '\',\'' + instId + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="bjInventoryTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title="删除" onclick="removeBjInventory(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == 'DISCARD_END' &&(currentUserId ==record.CREATE_BY_)) {
            s += '<span  title="删除" onclick="removeBjInventory(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
    

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '审批完成','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }

    $(function () {
        searchFrm();
    });


    function addBjInventory() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/BJTJQD/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (bjInventoryListGrid) {
                    bjInventoryListGrid.reload()
                }
            }
        }, 1000);
    }

    function copyBjInventory() {
        row = bjInventoryListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        var id = row.id;
        var url = jsUseCtxPath + "/bpm/core/bpmInst/BJTJQD/start.do?oldId="+id;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (bjInventoryListGrid) {
                    bjInventoryListGrid.reload()
                }
            }
        }, 1000);
    }

    function bjInventoryEdit(id,instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&id=" + id;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (bjInventoryListGrid) {
                    bjInventoryListGrid.reload()
                }
            }
        }, 1000);
    }


    function bjInventoryDetail(id,status) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineer/core/BjInventory/editPage.do?action=" + action + "&id=" + id+ "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (bjInventoryListGrid) {
                    bjInventoryListGrid.reload()
                }
            }
        }, 1000);
    }

    function bjInventoryTask(taskId) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            async:false,
            success: function (result) {
                if (!result.success) {
                    top._ShowTips({
                        msg: result.message
                    });
                } else {
                    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                    var winObj = openNewWindow(url, "handTask");
                    var loop = setInterval(function () {
                        if(!winObj) {
                            clearInterval(loop);
                        } else if (winObj.closed) {
                            clearInterval(loop);
                            if (bjInventoryListGrid) {
                                bjInventoryListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeBjInventory(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = bjInventoryListGrid.getSelecteds();
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
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                    instIds.push(r.instId);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineer/core/BjInventory/deleteBjInventory.do",
                    method: 'POST',
                    showMsg:false,
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

    function exportBjInventory() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
</script>
<redxun:gridScript gridId="bjInventoryListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
