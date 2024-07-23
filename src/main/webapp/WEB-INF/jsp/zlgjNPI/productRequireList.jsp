<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>审批列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" name="applyName"/>
                </li>
                <li style="margin-left:15px;margin-right: 15px"><span class="text"style="width:auto">申请单流程状态: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm()"
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},
                           {'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>
                <li style="margin-left:15px;margin-right: 15px">
                    <span class="text" style="width:auto">申请单号: </span>
                    <input class="mini-textbox" name="lcNum"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="deptApply" class="mini-button " style="margin-left: 10px;" plain="true"
                       onclick="doApply()">新增申请</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="cpkfListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/rdm/core/ProductRequire/queryProductRequire.do?"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true"
         sizeList="[50,100,200]" pageSize="50">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="lcNum" sortField="lcNum" width="100" headerAlign="center" align="center" allowSort="true">申请单号</div>
            <div field="productName" width="70" headerAlign="center" align="center" allowSort="false">设计型号</div>
            <div field="cpzgName" width="70" headerAlign="center" align="center" allowSort="false">产品主管</div>
            <div field="modelDeptName" width="70" headerAlign="center" align="center" allowSort="false">产品所</div>
            <div field="fileType" width="70" headerAlign="center" align="center" allowSort="false">文件类型</div>
            <div field="version" width="70" headerAlign="center" align="center" allowSort="false">文件版本</div>
            <div field="taskName" headerAlign='center' align='center' width="40">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="50">当前处理人</div>
            <div field="status" width="35" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="userName" sortField="userName" width="40" headerAlign="center" align="center" allowSort="true">
                申请人
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="50" headerAlign="center" allowSort="true">申请时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var cpkfListGrid = mini.get("cpkfListGrid");
    var currentUserId = "${currentUser.userId}";
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var cpkfId = record.cpkfId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="cpkfDetail(\'' + cpkfId + '\',\'' + instId + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="cpkfEdit(\'' + cpkfId + '\',\'' + instId + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="cpkfTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED') {
            s += '<span  title="删除" onclick="removeCpkf(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }


    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }



    $(function () {
        searchFrm();
    });
    //编辑行数据流程（后台根据配置的表单进行跳转）
    function cpkfEdit(cpkfId,instId) {
        var action = "detail";
        var url=jsUseCtxPath+"/bpm/core/bpmInst/start.do?instId="+instId+ "&cpkfId=" + cpkfId;
        var winObj = window.open(url);
        var loop = setInterval(function() {
            if(winObj.closed) {
                clearInterval(loop);
                if(cpkfListGrid){
                    cpkfListGrid.reload()
                };
            }
        }, 1000);
    }

    //删除记录
    function removeCpkf(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = cpkfListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            }else {
                var rowIds = [];
                var instIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.cpkfId);
                    instIds.push(r.instId);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdm/core/ProductRequire/deleteProductRequire.do",
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
            }
        });
    }
    //跳转到任务的处理界面
    function cpkfTask(taskId,applyType) {
        $.ajax({
            url:jsUseCtxPath+'/bpm/core/bpmTask/checkTaskLockStatus.do?taskId='+taskId,
            success:function (result) {
                if(!result.success){
                    top._ShowTips({
                        msg:result.message
                    });
                }else{
                    var url=jsUseCtxPath+'/bpm/core/bpmTask/toStart.do?taskId='+taskId;
                    var winObj = window.open(url);
                    var loop = setInterval(function() {
                        if(winObj.closed) {
                            clearInterval(loop);
                            if(cpkfListGrid){
                                cpkfListGrid.reload()
                            };
                        }
                    }, 1000);
                }
            }
        })
    }
    //明细 的点击查看方法
    function cpkfDetail(cpkfId,status) {
        var action = "detail";
        var url=jsUseCtxPath+"/rdm/core/ProductRequire/editPage.do?action="+action+"&cpkfId=" + cpkfId+"&status="+status;
        var winObj = window.open(url);
        var loop = setInterval(function() {
            if(winObj.closed) {
                clearInterval(loop);
                if(cpkfListGrid){
                    cpkfListGrid.reload()
                };
            }
        }, 1000);
    }
    //新增提报审批流程
    function doApply() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/CPKFXQ/start.do?";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cpkfListGrid) {
                    cpkfListGrid.reload();
                }
            }
        }, 1000);
    }

</script>
<redxun:gridScript gridId="cpkfListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
