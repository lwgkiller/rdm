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
                    <span class="text" style="width:auto">生产通知单号: </span>
                    <input class="mini-textbox" id="noticeNo" name="noticeNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">内部订单号: </span>
                    <input class="mini-textbox" id="saleNum" name="saleNum"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">出口区域: </span>
                    <input id="saleArea" name="saleArea" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="出口区域："
                           length="50"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="text" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=reviewSaleArea"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">出口国家: </span>
                    <input id="saleCountry" name="saleCountry" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="出口国家："
                           length="50" valueFromSelect="true"
                           only_read="false" required="true" allowinput="true" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="text" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=saleCountry"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addOnlineReview()">新增</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="onlineReviewListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/onlineReview/core/queryOnlineReviewBase.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="40" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="noticeNo" headerAlign='center' align='center' width="30">生产通知单号</div>
            <div field="company"  width="60" headerAlign="center" align="center" allowSort="true">订单需求单位名称</div>
            <div field="respName"  width="20" headerAlign="center" align="center" allowSort="true">业务员</div>
            <div field="saleArea"  width="40" headerAlign="center" align="center" allowSort="true">出口区域</div>
            <div field="saleCountry" headerAlign='center' width="40" align='center'>出口国家</div>
            <div field="saleNum"  width="40" headerAlign="center" align="center" allowSort="true">内部订单号</div>
            <div field="taskName" headerAlign='center' align='center' width="30">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="30">当前处理人</div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="CREATE_TIME_" width="30" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">申请时间</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var onlineReviewListGrid = mini.get("onlineReviewListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";


    function onMessageActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="onlineReviewDetail(\'' + id + '\',\'' + status + '\')">明细</span>';
            s += '<span  title="上传长周期件" onclick="onlineReviewUpload(\'' + id + '\',\'' + status + '\')">上传长周期件</span>';
        }

        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="onlineReviewEdit(\'' + id + '\',\'' + instId + '\',\'' + status + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="onlineReviewTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title="删除" onclick="removeOnlineReview(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == 'DISCARD_END' &&(currentUserId ==record.CREATE_BY_)) {
            s += '<span  title="删除" onclick="removeOnlineReview(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
    

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '批准','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }

    $(function () {
        searchFrm();
    });


    function addOnlineReview() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/CKCPXSPS/start.do?";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (onlineReviewListGrid) {
                    onlineReviewListGrid.reload()
                }
            }
        }, 1000);
    }
    function onlineReviewEdit(id,instId,status) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&id=" + id+"&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (onlineReviewListGrid) {
                    onlineReviewListGrid.reload()
                }
            }
        }, 1000);
    }

    function onlineReviewUpload(id,status) {
        var action = "uploadczq";
        var url = jsUseCtxPath + "/onlineReview/core/baseEditPage.do?action=" + action + "&id=" + id+ "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (onlineReviewListGrid) {
                    onlineReviewListGrid.reload()
                }
            }
        }, 1000);
    }

    function onlineReviewDetail(id,status) {
        var action = "detail";
        var url = jsUseCtxPath + "/onlineReview/core/baseEditPage.do?action=" + action + "&id=" + id+ "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (onlineReviewListGrid) {
                    onlineReviewListGrid.reload()
                }
            }
        }, 1000);
    }

    function onlineReviewTask(taskId) {
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
                            if (onlineReviewListGrid) {
                                onlineReviewListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeOnlineReview(record) {
        var rows = onlineReviewListGrid.getSelected();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/onlineReview/core/deleteOnlineReview.do",
                    method: 'POST',
                    showMsg:false,
                    data: {id: rows.id},
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


</script>
<redxun:gridScript gridId="onlineReviewListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
