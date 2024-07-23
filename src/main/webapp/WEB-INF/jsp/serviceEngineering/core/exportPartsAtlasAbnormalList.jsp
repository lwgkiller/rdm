<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>出口产品零件图册制作异常反馈列表</title>
    <%@include file="/commons/list.jsp" %>
    <link href="${ctxPath}/styles/formFile.css?version=${static_res_version}" rel="stylesheet" type="text/css" />
</head>
<body>

<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">反馈编号：</span>
                    <input class="mini-textbox" name="abnormalNum"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机PIN码：</span>
                    <input class="mini-textbox" name="machineCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">预计完成时间：</span>
                    <input class="mini-datepicker" name="expectTime" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">原因描述：</span>
                    <input class="mini-textbox" name="reasonDesc"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">反馈人：</span>
                    <input class="mini-textbox" name="creatorName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">状态：</span>
                    <input id="abnormalStatus" name="abnormalStatus" class="mini-combobox" style="width:140px;"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '编制','value' : '编制'},{'key' : '审核','value' : '审核'},
                           {'key' : '发布','value' : '发布'}
                           ]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" onclick="searchFrm()" style="margin-right: 5px">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="myClearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="abnormalNextBtn" class="mini-button" onclick="abnormalNext()" style="margin-right: 5px;display:none">审核通过</a>
                    <a id="abnormalPreBtn" class="mini-button btn-red" onclick="abnormalPreClick()" style="margin-right: 5px;display:none">审核驳回</a>
                </li>
            </ul>
        </form>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="abnormalListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true" showCellTip="true" idField="id"  allowHeaderWrap="true"
         multiSelect="false" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/abnormalListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="30"></div>
            <div name="action" cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="abnormalNum" width="240" headerAlign="center" align="center" >异常反馈编号</div>
            <div field="machineCode" width="200" headerAlign="center" align="center">整机PIN码</div>
            <div field="expectTime" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">预计完成时间</div>
            <div field="reasonDesc" width="300" headerAlign="center" align="center" >异常反馈原因描述</div>
            <div field= "taskStatus" width="150" headerAlign="center" align="center" renderer="abnormalStatusRenderList">流程状态(点击查看历史)</div>
            <div field="creatorName" width="100" headerAlign="center" align="center" >反馈人</div>
            <div field="CREATE_TIME_" width="110" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">反馈创建时间</div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="taskCheckRenderer" cellStyle="padding:0;">制作任务详情</div>
        </div>
    </div>
</div>
<div id="statusHisWindow" title="状态记录" class="mini-window" style="width:750px;height:400px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div id="statusListGrid" class="mini-datagrid" style="width: 100%; height: 98%" allowResize="false" allowCellWrap="false"
             showCellTip="true" idField="id" showPager="false" showColumnsMenu="false" allowcelledit="false"
             allowAlternating="true" >
            <div property="columns">
                <div type="indexcolumn" headerAlign="center" align="center" width="40">序号</div>
                <div field="statusDesc" width="120" headerAlign="center" align="center">状态更改为
                </div>
                <div field="creatorName" width="110" headerAlign="center" align="center">触发人
                </div>
                <div field="CREATE_TIME_" width="110" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">触发时间
                </div>
                <div field="optionDesc" width="250" headerAlign="center" align="center">更改意见
                </div>
            </div>
        </div>
    </div>
</div>
<div id="abnormalPreWindow" title="驳回" class="mini-window" style="width:750px;height:400px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div class="form-container" style="margin: 0 auto;height: 99%;min-height: 90%;padding: 0px 10px">
            <div class="mini-toolbar" style="float: right;margin-right: 10px">
                <a class="mini-button" onclick="abnormalPre()">确定</a>
                <a class="mini-button btn-red" onclick="closeAbnormalPreWindow()">关闭</a>
            </div>
            <form method="post" style="height: 99%">
                <table class="table-detail" cellspacing="1" cellpadding="0">
                    <tr>
                        <td style="width: 14%">驳回意见：<span style="color:red">*</span></td>
                        <td style="width: 36%;min-width:140px">
                            <input id="optionDesc"  class="mini-textarea" style="width:98%;height: 100px" />
                        </td>

                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div id="abnormalEditWindow" title="编制" class="mini-window" style="width:900px;height:300px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px">
        <div class="form-container" style="margin: 0 auto;height: 99%;min-height: 90%;padding: 0px 10px">
            <div class="mini-toolbar" style="float: right;margin-right: 10px">
                <a class="mini-button" onclick="abnormalEdit()">确定</a>
                <a class="mini-button btn-red" onclick="closeAbnormalEditWindow()">关闭</a>
            </div>
            <form method="post" style="height: 99%">
                <table class="table-detail" cellspacing="1" cellpadding="0">
                    <tr>
                        <td style="width: 15%">预计完成时间：<span style="color:red">*</span></td>
                        <td style="width: 20%;">
                            <input id="expectTime" name="expectTime" class="mini-datepicker" style="width:98%;" class="mini-datepicker" format="yyyy-MM-dd"/>
                        </td>
                        <td style="width: 15%">原因描述：<span style="color:red">*</span></td>
                        <td style="width: 50%;">
                            <input name="reasonDesc" id="reasonDesc" class="mini-textarea" style="width:98%;height: 120px" />
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<%@include file="exportPartsAtlasTaskRelPage.jsp" %>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var statusHisWindow = mini.get("statusHisWindow");
    var statusListGrid = mini.get("statusListGrid");
    var taskDetailWindow= mini.get("taskDetailWindow");
    var taskDetailForm = new mini.Form("taskDetailForm");
    var abnormalListGrid = mini.get("abnormalListGrid");
    var fwgcsUser = ${fwgcsUser};
    var fwgcsLeader = ${fwgcsLeader};
    var abnormalPreWindow = mini.get("abnormalPreWindow");
    var abnormalEditWindow = mini.get("abnormalEditWindow");

    $(function () {
        if(fwgcsLeader || currentUserNo == 'admin') {
            mini.get("abnormalPreBtn").show();
            mini.get("abnormalNextBtn").show();
        }
    });

    function myClearForm() {
        mini.get("abnormalStatus").setValue("");
        clearForm();
    }

    //操作按钮
    function onActionRenderer(e) {
        var record = e.record;
        var createById=record.CREATE_BY_;
        var abnormalStatus = record.abnormalStatus;
        var s = '';
        if (abnormalStatus == '编制' && createById == currentUserId) {
            s+='<span  title="编辑" onclick="abnormalEditClick()">编辑</span>';
        } else {
            s+='<span  title="编辑" style="color: silver">编辑</span>';
        }
        if (abnormalStatus == '编制' && createById == currentUserId) {
            s+='<span  title="删除" onclick="abnormalDel('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
        } else {
            s+='<span  title="删除" style="color: silver">删除</span>';
        }

        return s;
    }

    //制作任务查看
    function taskCheckRenderer(e) {
        var record = e.record;
        var s = '';
        s += '<span  title="查看" onclick="taskInfoCheck(\'' +record.machinetaskId + '\')">查看</span>';
        return s;
    }

    //任务详情
    function taskInfoCheck(machinetaskId) {
        taskDetailForm.setEnabled(false);
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/taskDetailInfo.do?id="+machinetaskId;
        $.ajax({
            url:url,
            method:'get',
            success:function (taskObj) {
                var statusDesc = getStatusDesc(taskObj.taskStatus);
                taskObj.statusDesc=statusDesc;
                taskDetailForm.setData(taskObj);
                taskDetailWindow.show();
            }
        });
    }

    function abnormalStatusRenderList(e) {
        var record = e.record;
        var abnormalStatus = record.abnormalStatus;
        if(!abnormalStatus) {
            return;
        }
        var id = record.id;
        return '<span style="text-decoration:underline;cursor: pointer;" onclick="statusHis(\''+id+'\')">'+abnormalStatus+'</span>';
    }

    function getStatusDesc(taskStatus) {
        var s = '';
        if(taskStatus) {
            switch (taskStatus) {
                case "00WLQ":
                    s='未领取';
                    break;
                case "01YLQ":
                    s='已领取';
                    break;
                case "02JXZZSQ":
                    s='机型制作申请中';
                    break;
                case "03JXZZ":
                    s='机型制作中';
                    break;
                case "04SLZZ":
                    s='实例制作中';
                    break;
                case "05GZ":
                    s='改制中';
                    break;
                case "06ZZWCYZC":
                    s='制作完成已转出';
                    break;
                case "07YJS":
                    s='档案室已接收';
                    break;
                case "08ZF":
                    s='作废';
                    break;
            }
        }
        return s;
    }

    function statusHis(busKeyId) {
        statusHisWindow.show();
        var url=jsUseCtxPath+"/serviceEngineering/core/exportPartsAtlas/statusHisQuery.do";
        url+="?busKeyId="+busKeyId;
        url+="&scene=abnormal";
        statusListGrid.setUrl(url);
        statusListGrid.load();
    }

    function abnormalNext() {
        var selectRow=abnormalListGrid.getSelected();
        if (!selectRow) {
            mini.alert("请选择一条数据！");
            return;
        }
        var status=selectRow.abnormalStatus;
        if(status=='发布') {
            mini.alert("流程已发布，无法操作！");
            return;
        }
        if(status=='审核') {
            if(fwgcsLeader) {
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/abnormalNext.do",
                    method: 'POST',
                    data: {abnormalObj: mini.encode(selectRow),currentStatus:status},
                    success: function (returnData) {
                        mini.alert(returnData.message,'提示',function () {
                            if(returnData.success) {
                                abnormalListGrid.reload();
                            }
                        });
                    }
                });
            } else {
                mini.alert("无操作权限！");
                return;
            }
        }
        if(status=='编制') {
            mini.alert("流程处于编制状态，无法操作！");
            return;
        }
    }

    function abnormalPreClick() {
        var selectRow=abnormalListGrid.getSelected();
        if (!selectRow) {
            mini.alert("请选择一条数据！");
            return;
        }
        var status=selectRow.abnormalStatus;
        if(status=='发布') {
            mini.alert("流程已发布，无法操作！");
            return;
        }
        if(status=='审核') {
            if(fwgcsLeader) {
                abnormalPreWindow.show();
            } else {
                mini.alert("无操作权限！");
                return;
            }
        }
        if(status=='编制') {
            mini.alert("流程处于编制状态，无法操作！");
            return;
        }
    }

    function abnormalPre() {
        var optionDesc =mini.get("optionDesc").getValue();
        if(!optionDesc) {
            mini.alert("请填写驳回意见！");
            return;
        }
        var selectRow=abnormalListGrid.getSelected();
        _SubmitJson({
            url: jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/abnormalPre.do",
            method: 'POST',
            data: {abnormalObj: mini.encode(selectRow),optionDesc:optionDesc},
            success: function (returnData) {
                mini.alert(returnData.message,'提示',function () {
                    if(returnData.success) {
                        closeAbnormalPreWindow();
                        abnormalListGrid.reload();
                    }
                });
            }
        });
    }

    function closeAbnormalPreWindow() {
        abnormalPreWindow.hide();
        mini.get("optionDesc").setValue("");
    }

    function abnormalDel(selectRow) {
        if (!selectRow) {
            mini.alert("请选择一条数据！");
            return;
        }
        if(selectRow.CREATE_BY_!=currentUserId || selectRow.abnormalStatus!='编制') {
            mini.alert("仅“编制”状态的数据可由本人删除！");
            return;
        }
        _SubmitJson({
            url: jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/delAbnormal.do",
            method: 'POST',
            data: {ids: selectRow.id},
            success: function (returnData) {
                mini.alert(returnData.message,'提示',function () {
                    if(returnData.success) {
                        abnormalListGrid.reload();
                    }
                });
            }
        });
    }

    function abnormalEditClick() {
        var selectRow=abnormalListGrid.getSelected();
        mini.get("expectTime").setValue(selectRow.expectTime);
        mini.get("reasonDesc").setValue(selectRow.reasonDesc);
        abnormalEditWindow.show();
    }

    function abnormalEdit() {
        var expectTime = mini.get("expectTime").getText();
        var reasonDesc = mini.get("reasonDesc").getValue();
        if(!expectTime) {
            mini.alert("请填写预计完成时间！");
            return;
        }
        if(!reasonDesc) {
            mini.alert("请填写原因描述！");
            return;
        }
        var selectRow=abnormalListGrid.getSelected();
        selectRow.expectTime=expectTime;
        selectRow.reasonDesc=reasonDesc;
        _SubmitJson({
            url: jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/abnormalNext.do",
            method: 'POST',
            data: {abnormalObj: mini.encode(selectRow),currentStatus:'编制'},
            success: function (returnData) {
                mini.alert(returnData.message,'提示',function () {
                    if(returnData.success) {
                        closeAbnormalEditWindow();
                        abnormalListGrid.reload();
                    }
                });
            }
        });
    }

    function closeAbnormalEditWindow() {
        abnormalEditWindow.hide();
        mini.get("expectTime").setValue("");
        mini.get("reasonDesc").setValue("");
    }
</script>
<redxun:gridScript gridId="abnormalListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>