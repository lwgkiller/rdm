<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>农机鉴定列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/njjdList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请编号: </span>
                    <input class="mini-textbox" id="applyNumber" name="applyNumber">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品型号: </span>
                    <input class="mini-textbox" id="productType" name="productType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">分管领导: </span>
                    <input id="applyUserId" name="applyUserId" textname="applyUserName" class="mini-user rxc"
                           plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50"
                           maxlength="50"
                           mainfield="no" single="true"/>
                </li>
                <%--<li style="margin-right: 15px"><span class="text" style="width:auto">申请部门:</span>
                    <input id="applyDepId" name="applyDepId" class="mini-dep rxc" plugins="mini-dep"
                           data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                           style="width:160px;height:34px"
                           allowinput="false" label="部门" textname="bm_name" length="500" maxlength="500" minlen="0"
                           single="true" required="false" initlogindep="false"
                           level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34"
                           hunit="px"/>
                </li>--%>
                <li style="margin-right: 15px"><span class="text" style="width:auto">认定状态: </span>
                    <input id="instStatus" name="status" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '认定中'},{'key' : 'SUCCESS_END','value' : '通过认定'},
							   {'key' : 'DISCARD_END','value' : '未通过认定'}]"
                    />
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addNjjd()">新增认定</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeNjjd()">删除</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportJsmm()">导出</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">提交时间 从 </span>:<input id="rdTimeStart" name="rdTimeStart"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至: </span><input id="rdTimeEnd" name="rdTimeEnd"
                                                                                  class="mini-datepicker"
                                                                                  format="yyyy-MM-dd"
                                                                                  style="width:120px"/>
                    </li>
                    <%--<li style="margin-right: 15px"><span class="text" style="width:auto">奖励状态: </span>--%>
                    <%--<input id="whetherJLFilter" name="whetherJL" class="mini-combobox" style="width:120px;"--%>
                    <%--textField="key" valueField="value" emptyText="请选择..."--%>
                    <%--required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."--%>
                    <%--data="[ {'key' : '已奖励','value' : 'yes'},{'key' : '未奖励','value' : 'no'}]"--%>
                    <%--/>--%>
                    <%--</li>--%>
                </ul>
            </div>
        </form>
    </div>
</div>

<%--农机鉴定列表--%>
<div class="mini-fit" style="height: 100%;">
    <div id="jsmmListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/zhgl/core/njjd/getNjjdList.do" idField="jsmmId"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="applyNumber" sortField="applyNumber" width="80" headerAlign="center" align="center"
                 allowSort="true">申请编号
            </div>
            <div field="productType" width="180" headerAlign="center" align="center" allowSort="false">产品型号</div>
            <div field="applyUserName"  width="70" headerAlign="center" align="center" allowSort="false">产品主管</div>
            <div field="CREATE_TIME_" width="70" headerAlign="center" align="center" allowSort="false">提交时间</div>
            <div field="currentProcessTask" width="70" align="center" headerAlign="center">当前阶段</div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="70" align="center"
                 headerAlign="center" allowSort="false">当前处理人
            </div>
            <div field="STATUS" width="70" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">认定状态
            </div>
            <div field="saleStatus" width="70" headerAlign="center" align="center" allowSort="true"
                 renderer="onSaleStatus">销售状态
            </div>
            <div field="haveSubsidy" align="center" width="80" headerAlign="center" allowSort="false">是否补贴</div>
            <div field="subsidyEndDate" width="100" headerAlign="center" align="center" allowSort="false" dateFormat="yyyy-MM-dd">补贴截止日期</div>
            <%--<div field="njxs" width="70" headerAlign="center" align="center" allowSort="false">农机销量</div>
            <div field="projectName" width="70" headerAlign="center" align="center" allowSort="false">项目名称</div>
            <div field="projectNumber" sortField="applyNumber" width="80" headerAlign="center" align="center"
                 allowSort="true">项目编号
            </div>--%>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/zhgl/core/njjd/exportNjjdList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<div id="updateJlWindow" title="奖励状态" class="mini-window" style="width:400px;height:185px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <input id="jsmmId" name="jsmmId" class="mini-hidden"/>
        <table class="table-detail" cellspacing="1" cellpadding="0">
            <tr>
                <td style="width: 17%">是否已奖励：</td>
                <td style="width: 33%;min-width:170px">
                    <div id="whetherJL" name="whetherJL" class="mini-checkbox" readOnly="false" text=""></div>
                </td>
            </tr>
            <tr>
                <td style="width: 14%">奖励时间：</td>
                <td style="width: 36%;min-width:170px">
                    <input id="jlTime" name="jlTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </td>
            </tr>
        </table>
    </div>
    <div property="footer" style="padding:5px;height: 40px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="updateJlSave()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="updateJlCancle()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var jsmmListGrid = mini.get("jsmmListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserRoles =${currentUserRoles};
    var currentUserNo = "${currentUserNo}";
    var updateJlWindow = mini.get("updateJlWindow");
    var isJsglbRespUser =${isJsglbRespUser};
    var isNjzy =${isNjzy};

    var nodeVarsStr = '${nodeVars}';


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var njjdId = record.id;
        var instId = record.instId;
        var s = '' ;
        s += '<span  title="明细" onclick="njjdDetail(\'' + njjdId + '\',\'' + record.STATUS + '\')">明细</span>';

        if (record.STATUS == 'DRAFTED') {
            s += '<span  title="编辑" onclick="jsmmEdit(\'' + njjdId + '\',\'' + instId + '\')">编辑</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if (currentProcessUserId && currentProcessUserId ==  currentUserId) {
                s += '<span  title="办理" onclick="jsmmTask(\'' + record.taskId + '\')">办理</span>';
            }
        }

        var status = record.STATUS;
        if (currentUserNo != 'admin') {
            if (status == 'DRAFTED' && currentUserId == record.CREATE_BY_) {
                s += '<span  title="删除" onclick="removeNjjd(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="removeNjjd(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }

        if (isNjzy == true ) {
            s += '<span  title="更改销售状态" onclick="changeStatus(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">更改销售状态</span>';
        }

        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.STATUS;
        if(!status){
            var status = 'SUCCESS_END';
        }
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '认定中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '通过认定', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '未通过认定', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function onSaleStatus(e) {
        var record = e.record;
        var status = record.saleStatus;

        var arr = [{'key': '0', 'value': '停售', 'css': 'red'},
            {'key': '1', 'value': '在售', 'css': 'green'}
        ];


        return $.formatItemValue(arr, status);
    }

    function changeStatus(record) {

        var saleStatus = record.saleStatus;
        var id = record.id;
        console.log("测试")
        mini.confirm("确定修改销售状态？", "提示",
            function () {
                var postData = {saleStatus: saleStatus, id: id};
                $.ajax({
                    url: '${ctxPath}/zhgl/core/njjd/updateSaleButton.do',
                    type: 'post',
                    data: mini.encode(postData),
                    contentType: 'application/json',
                    success: function (data) {
                        jsmmListGrid.load();
                    }
                });
            }
        );
    }

    function onJlRenderer(e) {
        var record = e.record;
        var whetherJL = record.whetherJL;
        if (whetherJL == 'yes') {
            return "已奖励";
        } else {
            return "未奖励";
        }
    }
</script>
<redxun:gridScript gridId="jsmmListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>