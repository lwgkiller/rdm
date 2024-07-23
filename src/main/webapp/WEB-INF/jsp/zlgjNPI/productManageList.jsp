<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>产品管控列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/zlgjNPI/productManageList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
           <%--     <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品管控编号: </span>
                    <input class="mini-textbox" id="id" name="id">
                </li>--%>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="designType" name="designType"/>
                </li>

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
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addProductManage()">新增申请</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="removeProductManage()">删除</a>
                    <%--<a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportJsmm()">导出</a>--%>
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
                </ul>
            </div>
        </form>
    </div>
</div>


<%--产品管控列表--%>
<div class="mini-fit" style="height: 100%;">
    <div id="cpgkListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/xcpdr/core/cpkfgk/getProductManageList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
        <%--    <div field="id" sortField="id" width="80" headerAlign="center" align="center"
                 allowSort="true">产品管控编号
            </div>--%>
            <div field="designType" width="180" headerAlign="center" align="center" allowSort="false">设计型号</div>

            <div field="performznceStartTime" width="70" headerAlign="center" align="center" allowSort="false">
                需求调研开始
            </div>
            <div field="performznceEndTime" width="70" headerAlign="center" align="center" allowSort="false">
                需求调研结束
            </div>
            <div field="productStartTime" width="70" headerAlign="center" align="center" allowSort="false">产品设计开始
            </div>
            <div field="productEndTime" width="70" headerAlign="center" align="center" allowSort="false">产品设计结束</div>
            <div field="assemblyStartTime" width="70" headerAlign="center" align="center" allowSort="false">部件设计开始
            </div>
            <div field="assemblyEndTime" width="70" headerAlign="center" align="center" allowSort="false">部件设计结束</div>
            <div field="verificationStartTime" width="70" headerAlign="center" align="center" allowSort="false">
                首台验证开始
            </div>
            <div field="verificationEndTime" width="70" headerAlign="center" align="center" allowSort="false">首台验证结束
            </div>
            <div field="batchStartTime" width="70" headerAlign="center" align="center" allowSort="false">量产开始</div>
            <div field="batchEndTime" width="70" headerAlign="center" align="center" allowSort="false">量产结束</div>
            <div field="applyUserName" width="70" headerAlign="center" align="center" allowSort="false">申请人</div>
            <div field="CREATE_TIME_" width="70" headerAlign="center" align="center" allowSort="false">提交时间</div>
            <div field="currentProcessTask" width="70" align="center" headerAlign="center">当前阶段</div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="70" align="center"
                 headerAlign="center" allowSort="false">当前处理人
            </div>
            <div field="STATUS" width="70" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">认定状态
            </div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/zhgl/core/njjd/exportNjjdList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var cpgkListGrid = mini.get("cpgkListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";


    var nodeVarsStr = '${nodeVars}';

    var isScUser = '';


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var manageId = record.id;
        var instId = record.instId;
        var status = record.STATUS;
        var s = '';

        s += '<span  title="明细" onclick="productManageDetail(\'' + manageId + '\',\'' + record.STATUS + '\')">明细</span>';

        if (record.STATUS == 'DRAFTED'  && currentUserId == record.CREATE_BY_) {
            s += '<span  title="编辑" onclick="productManageEdit(\'' + manageId + '\',\'' + instId + '\')">编辑</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if (currentProcessUserId && currentProcessUserId.match(currentUserId) ) {
                s += '<span  title="办理" onclick="productManageTask(\'' + record.taskId + '\')">办理</span>';
            }
        }

        if (status == 'DRAFTED' && currentUserId == record.CREATE_BY_) {
            s += '<span  title="删除" onclick="removeProductManage(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }

        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.STATUS;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '认定中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '通过认定', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '未通过认定', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }


</script>
<redxun:gridScript gridId="cpgkListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>