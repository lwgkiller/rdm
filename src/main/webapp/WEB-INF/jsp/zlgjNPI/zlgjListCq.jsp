<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>质量改进列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/zlgjNPI/zlgjListCq.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <input id="type" name="type" class="mini-hidden" value="${type}"/>
            <input id="deptName" name="deptName" class="mini-hidden"/>
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">问题类型: </span>
                    <input id="wtlx" name="wtlx" class="mini-combobox" style="width:98%;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{'key' : 'XPSZ','value' : '新品试制'},{'key' : 'XPZDSY','value' : '新品整机试验'}
                           ,{'key' : 'XPLS','value' : '新品路试'},{'key' : 'CNWT','value' : '厂内问题'}
                           ,{'key' : 'SCWT','value' : '市场问题'},{'key' : 'HWWT','value' : '海外问题'}
                           ,{'key' : 'WXBLX','value' : '维修便利性'},{'key' : 'LBJSY','value' : '新品零部件试验'}]"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">问题编号: </span>
                    <input class="mini-textbox" id="zlgjNumber" name="zlgjNumber">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">机型类别: </span>
                    <input id="jiXing" name="jiXing" class="mini-combobox" style="width:100px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{'key' : 'WEIWA','value' : '微挖'},{'key' : 'LUNWA','value' : '轮挖'},{'key' : 'XIAOWA','value' : '小挖'},
                                   {'key' : 'ZHONGWA','value' : '中挖'},{'key' : 'DAWA','value' : '大挖'},{'key' : 'TEWA','value' : '特挖'},
                                   {'key' : 'SHUJU','value' : '属具'},{'key' : 'XINNENGYUAN','value' : '新能源'}
                                   ,{'key' : 'HAIWAI','value' : '海外'}]"
                    />N
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">机型: </span>
                    <input class="mini-textbox" id="smallJiXing" name="smallJiXing">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机编号: </span>
                    <input class="mini-textbox" id="zjbh" name="zjbh">
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
                    <a id="addZlgj" class="mini-button" style="margin-right: 5px" plain="true" onclick="addZlgj()">新增</a>
                    <a id="removeZlgj" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeZlgj()">删除</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportWtsb()">导出</a>
                </li>
            </ul>

            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">第一责任人: </span>
                        <input id="zrcpzgId" name="zrcpzgId" textname="zrcpzgName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                               mainfield="no" single="true"/>
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">责任部门:</span>
                        <input id="ssbmId" name="ssbmId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:160px;height:34px" allowinput="false" label="部门" textname="ssbmName" length="500" maxlength="500"
                               minlen="0" single="true" required="false" initlogindep="false"
                               level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">问题处理人部门:</span>
                        <input id="zrrDeptId" name="zrrDeptId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:160px;height:34px" allowinput="false" label="部门" textname="zrrDeptName" length="500" maxlength="500"
                               minlen="0" single="true" required="false" initlogindep="false"
                               level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">流程处理人部门:</span>
                        <input id="currentProcessUserDeptId" name="currentProcessUserDeptId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:160px;height:34px" allowinput="false" label="部门" textname="currentProcessUserDeptName" length="500" maxlength="500"
                               minlen="0" single="true" required="false" initlogindep="false"
                               level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">故障零件: </span>
                        <input class="mini-textbox" id="gzlj" name="gzlj">
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">问题描述: </span>
                        <input class="mini-textbox" id="wtms" name="wtms">
                    </li>
                    <li style="margin-right: 15px;display: none" id="tdmSylx_li">
                        <span class="text" style="width:auto">试验类型: </span>
                        <input id="tdmSylx" name="tdmSylx" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : '新品试验','value' : '新品试验'},{'key' : '竞品试验','value' : '竞品试验'},
                                           {'key' : '专项试验','value' : '专项试验'},{'key' : '可靠性试验','value' : '可靠性试验'},
                                           {'key' : '委外认证','value' : '委外认证'},{'key' : '调试试验','value' : '调试试验'},
                                           {'key' : '故障试验','value' : '故障试验'}]"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">创建时间 从 </span>:<input name="create_startTime" id="create_startTime"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至: </span><input name="create_endTime" id="create_endTime"
                                                                                  class="mini-datepicker"
                                                                                  format="yyyy-MM-dd"
                                                                                  style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">提报人: </span>
                        <input id="applyUserId" name="applyUserId" textname="applyUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                               mainfield="no" single="true"/>
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">提报部门:</span>
                        <input id="applyDepId" name="applyDepId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:160px;height:34px" allowinput="false" label="部门" textname="applyUserDeptName" length="500" maxlength="500"
                               minlen="0" single="true" required="false" initlogindep="false"
                               level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">问题响应要求: </span>
                        <input id="jjcd" name="jjcd" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : '特急','value' : '特急'},{'key' : '紧急','value' : '紧急'},{'key' : '一般','value' : '一般'}]"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">问题严重程度: </span>
                        <input id="yzcd" name="yzcd" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..." nullItemText="请选择..."
                               required="false" allowInput="false" showNullItem="true" multiSelect="true"
                               data="[{'key' : 'A','value' : 'A'},{'key' : 'B','value' : 'B'},{'key' : 'C','value' : 'C'},
                                           {'key' : 'W','value' : 'W'}]"/>
                    </li>
                    <li style="margin-right: 15px;">
                        <span class="text" style="min-width:250px;">结案状态（责任部门负责人审核完成）: </span>
                        <input id="jiean" name="jiean" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : 'no','value' : '进行中'},{'key' : 'yes','value' : '已完成'}]"
                        />
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="zlgjListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         allowHeaderWrap="true"
         url="${ctxPath}/xjsdr/core/zlgj/getZlgjListCq.do?type=${type}&chartType=${chartType}&czxpj=${czxpj}&zrType=${zrType}&deptName=${deptName}" idField="wtId" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" width="30"></div>
            <div type="checkcolumn" width="30"></div>
            <div name="action" cellCls="actionIcons" width="110" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="wtlx" width="100" headerAlign='center' align='center' width="20" renderer="wtlxRenderer">问题类型</div>
            <div field="zlgjNumber" width="150" headerAlign="center" align="center">问题编号</div>
            <div field="yzcd" width="60" headerAlign="center" align="center">问题严重度</div>
            <div field="tdmSylx" name="tdmSylx" width="70" headerAlign="center" align="center">试验类型</div>
            <div field="jiXing" width="65" headerAlign="center" align="center" renderer="onJXRenderer">机型类别</div>
            <div field="smallJiXing" width="100" headerAlign="center" align="center">机型</div>
            <div field="zjbh" sortField="zjbh" width="140" headerAlign="center" align="center">整机编号</div>
            <div field="gzlj" sortField="gzlj" width="110" headerAlign="center" align="center">故障零件</div>
            <div field="wtms" width="400" headerAlign="center" align="center">问题描述</div>
            <div field="zrcpzgName" width="80" headerAlign="center" align="center">第一责任人</div>
            <div field="ssbmName" width="100" headerAlign="center" align="center">责任部门</div>
            <div field="zrrName" width="80" headerAlign="center" align="center">问题处理人</div>
            <div field="zrrDeptName" width="80" headerAlign="center" align="center">问题处理人部门</div>
            <div field="jiean" width="70" headerAlign="center" align="center" renderer="jieanRenderer">结案状态</div>
            <div field="status" width="70" headerAlign="center" align="center" renderer="onStatusRenderer">流程状态</div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="80" align="center" headerAlign="center">流程处理人</div>
            <div field="currentProcessUserDeptName"  width="80" align="center" headerAlign="center">流程处理人部门</div>
            <div field="currentProcessTask" width="100" align="center" headerAlign="center">流程任务</div>
            <div field="applyUserName" width="80" headerAlign="center" align="center">提报人</div>
            <div field="applyUserDeptName" width="100" headerAlign="center" align="center">提报部门</div>
            <div field="CREATE_TIME" width="90" headerAlign="center" align="center">提报时间</div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/xjsdr/core/zlgj/exportWtsbCqExcel.do?type=${type}&chartType=${chartType}&czxpj=${czxpj}" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var zlgjListGrid = mini.get("zlgjListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserRoles =${currentUserRoles};
    var currentUserNo = "${currentUserNo}";
    var isJsglbRespUser =${isJsglbRespUser};
    var isBgry =${isBgry};
    var type = "${wtlxtype}";
    var isScwtUser =${isScwtUser};
    var isHwwtUser =${isHwwtUser};
    var create_startTimeStr = "${create_startTime}";
    var create_endTimeStr = "${create_endTime}";
    var deptNameStr = "${deptName}";
    var chartType = "${chartType}"
    var czxpj = "${czxpj}";
    var yzcd = "${yzcd}";
    var zrType = "${zrType}";
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var wtId = record.wtId;
        var instId = record.instId;
        var s = '';
        if (isBgry) {
            s += '<span  title="变更" onclick="zlgjChange(\'' + wtId + '\',\'' + record.status + '\')">变更</span>';
        }
        if (record.status == 'SUCCESS_END') {
            //技术管理部负责人不能看，不是自己且不是技术管理部其他人的且知悉范围中没有自己的，已成功的秘密内容
            if (isJsglbRespUser && (currentUserId != record.CREATE_BY_) && record.applyUserDeptName != '技术管理部' && record.readUserIds.indexOf(currentUserId) == -1) {
                s += '<span  title="明细" style="color: silver">明细</span>';
            }
            else {
                s += '<span  title="明细" onclick="zlgjDetail(\'' + wtId + '\',\'' + record.status + '\')">明细</span>';
            }
        } else {
            s += '<span  title="明细" onclick="zlgjDetail(\'' + wtId + '\',\'' + record.status + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="zlgjEdit(\'' + wtId + '\',\'' + instId + '\')">编辑</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if (currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) {
                s += '<span  title="办理" onclick="zlgjTask(\'' + record.taskId + '\')">办理</span>';
            }
        }

        var status = record.status;
        if (currentUserNo != 'admin') {
            if ((status == 'DRAFTED' && currentUserId == record.CREATE_BY_) || (status == 'DISCARD_END' && currentUserId == record.CREATE_BY_)) {
                s += '<span  title="删除" onclick="removeZlgj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="removeZlgj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        s += '<span  title="复制" onclick="copyZlgj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">复制</span>';

        if (record.status == 'SUCCESS_END' && (type == "XPZDSY" || type == "XPSZ") && (record.CREATE_BY_ == currentUserId || currentUserId == "1")) {
            s += '<span  title="回调" onclick="callBackOut(\'' + wtId + '\')">回调</span>';
        }
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '审批完成', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];
        return $.formatItemValue(arr, status);
    }

    function jieanRenderer(e) {
        var record = e.record;
        var status = record.jiean;

        var arr = [
            {'key': '进行中', 'value': '进行中', 'css': 'orange'},
            {'key': '已完成', 'value': '已完成', 'css': 'blue'}
        ];
        return $.formatItemValue(arr, status);
    }

    function onJXRenderer(e) {
        var record = e.record;
        var jiXing = record.jiXing;

        var arr = [{'key': 'WEIWA', 'value': '微挖'},
            {'key': 'LUNWA', 'value': '轮挖'}, {'key': 'XIAOWA', 'value': '小挖'},
            {'key': 'ZHONGWA', 'value': '中挖'}, {'key': 'DAWA', 'value': '大挖'},
            {'key': 'TEWA', 'value': '特挖'}, {'key': 'SHUJU', 'value': '属具'},
            {'key': 'XINNENGYUAN', 'value': '新能源'}, {'key': 'HAIWAI', 'value': '海外'}
        ];
        return $.formatItemValue(arr, jiXing);
    }

    function wtlxRenderer(e) {
        var record = e.record;
        var wtlx = record.wtlx;
        var wtlxName = '';
        switch (wtlx) {
            case 'XPSZ':
                wtlxName = '新品试制';
                break;
            case 'XPZDSY':
                wtlxName = '新品整机试验';
                break;
            case 'XPLS':
                wtlxName = '新品路试';
                break;
            case 'CNWT':
                wtlxName = '厂内问题';
                break;
            case 'SCWT':
                wtlxName = '市场问题';
                break;
            case 'HWWT':
                wtlxName = '海外问题';
                break;
            case 'WXBLX':
                wtlxName = '维修便利性';
                break;
            case 'LBJSY':
                wtlxName = '新品零部件试验';
                break;
        }
        return wtlxName;
    }

</script>
<redxun:gridScript gridId="zlgjListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
