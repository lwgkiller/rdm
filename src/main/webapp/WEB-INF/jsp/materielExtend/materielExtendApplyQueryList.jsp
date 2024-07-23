<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>物料扩充申请查询</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/materielExtend/materielExtendApplyQueryList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-left:15px;margin-right: 15px">
                    <span class="text" style="width:auto">申请单号: </span>
                    <input class="mini-textbox" name="applyNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" name="applyUserName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料号码: </span>
                    <input class="mini-textbox" name="wlhm">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">是否紧急: </span>
                    <input name="urgent" class="mini-combobox" style="width:150px;"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '是','value' : 'yes'},{'key' : '否','value' : 'no'}]"
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
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="exportBtn()">导出物料</a>
                    <p style="display: inline-block;color: #f6253e;font-size:15px;vertical-align: middle">（导出时请增加筛选条件，防止数据量过大）</p>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">状态: </span>
                        <input id="applyStatus" name="applyStatus" class="mini-combobox" style="width:150px;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '草稿','value' : 'draft'},{'key' : '处理中','value' : 'running'},{'key' : '流程结束（无错误物料）','value' : 'successEnd'},{'key' : '流程结束（有错误物料）','value' : 'failEnd'},{'key' : '待更新至SAP','value' : 'needSync2Sap'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">申请时间 从 </span>:<input id="startTime"  name="startTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至: </span><input  id="endTime" name="endTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                    </li>
                </ul>
            </div>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/materielExtend/apply/exportMaterielsByApply.do?scene=queryPage" method="post" target="excelIFrame">
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="applyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/materielExtend/apply/getApplyList.do?scene=queryPage"
         idField="applyId" multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="urgent" sortField="urgent" width="70" headerAlign="center" align="center" allowSort="true" renderer="urgentRender">
                是否紧急
            </div>
            <div field="applyNo" sortField="applyNo" width="210" headerAlign="center" align="center" allowSort="true">
                申请单号
            </div>
            <div field="applyUserName" sortField="applyUserName" headerAlign="center" align="center"
                 allowSort="false">申请人
            </div>
            <div field="applyUserDepName" sortField="applyUserDepName" headerAlign="center" align="center"
                 allowSort="false">申请人部门
            </div>
            <div field="applyUserMobile" width="100" headerAlign="center" align="center" allowSort="false">联系电话</div>
            <div field="sqCommitTime" sortField="sqCommitTime" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="160"
                 headerAlign="center" allowSort="true">申请提交时间</div>
            <div headerAlign="center" align="center" allowSort="false" renderer="processStatusRenderer">处理进度查询</div>
            <div field="applyStatus" width="170" sortField="applyStatus" headerAlign="center" align="center" renderer="onApplyStatusRenderer"
                 allowSort="true">申请单状态
                <image src="${ctxPath}/styles/images/question2.png" style="cursor: pointer;vertical-align: middle" title="状态为“流程结束（有错误物料）”时，请进入申请单，点击“导出问题物料”，然后重新发起新的扩充申请！&#10;状态为“待更新至SAP”时，请联系管理员处理！"/>
            </div>
        </div>
    </div>
</div>

<div id="processStatusWindow" title="处理进度窗口" class="mini-window" style="width:900px;height:350px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 10px">
        <form id="processStatusForm">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 10%">工艺扩充状态：</td>
                    <td style="width: 15%;">
                        <input id="gyStatus" name="gyStatus"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 10%">工艺处理人：</td>
                    <td style="width: 30%;">
                        <input name="gyCommitUserName"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 10%">工艺完成时间：</td>
                    <td style="width: 25%">
                        <input name="gyCommitTime"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">供方扩充状态：</td>
                    <td style="width: 20%;">
                        <input id="gfStatus" name="gfStatus"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 10%">供方处理人：</td>
                    <td style="width: 22%">
                        <input name="gfCommitUserName"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 10%">供方完成时间：</td>
                    <td style="width: 28%">
                        <input name="gfCommitTime"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">采购扩充状态：</td>
                    <td style="width: 20%;">
                        <input id="cgStatus" name="cgStatus"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 10%">采购处理人：</td>
                    <td style="width: 22%;">
                        <input name="cgCommitUserName"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 10%">采购完成时间：</td>
                    <td style="width: 28%">
                        <input name="cgCommitTime"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">财务扩充状态：</td>
                    <td style="width: 20%;">
                        <input id="cwStatus" name="cwStatus"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 10%">财务处理人：</td>
                    <td style="width: 22%;">
                        <input name="cwCommitUserName"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 10%">财务完成时间：</td>
                    <td style="width: 28%">
                        <input name="cwCommitTime"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">物流扩充状态：</td>
                    <td style="width: 20%;">
                        <input id="wlStatus" name="wlStatus"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 10%">物流处理人：</td>
                    <td style="width: 22%;">
                        <input name="wlCommitUserName"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 10%">物流完成时间：</td>
                    <td style="width: 28%">
                        <input name="wlCommitTime"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">制造扩充状态：</td>
                    <td style="width: 20%;">
                        <input id="zzStatus" name="zzStatus"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 10%">制造处理人：</td>
                    <td style="width: 22%">
                        <input name="zzCommitUserName"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 10%">制造完成时间：</td>
                    <td style="width: 28%">
                        <input name="zzCommitTime"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyListGrid = mini.get("applyListGrid");
    var opRoleName = "${opRoleName}";
    var processStatusWindow=mini.get("processStatusWindow");
    var processStatusForm=new mini.Form("#processStatusForm");
    var currentUserNo="${currentUserNo}";

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyStatus=record.applyStatus;
        var s = '<span title="查看" onclick="editOrViewApply(\'' + record.applyNo +'\',\'view\')">查看</span>';

        return s;
    }

    function onApplyStatusRenderer(e) {
        var record = e.record;
        var applyStatus = record.applyStatus;
        switch(applyStatus) {
            case 'draft':
                return '<span style="color:orange">草稿</span>';
            case 'running':
                return '<span style="color:#4BFF19">处理中</span>';
            case 'successEnd':
                return '<span style="color:#177BFF">流程结束（无错误物料）</span>';
            case 'failEnd':
                return '<span style="color:red">流程结束（有错误物料）</span>';
            case 'needSync2Sap':
                return '<span style="color:red">待更新至SAP</span>';
            case 'successConfirm':
                return '<span style="color:#177BFF">流程结束（错误已处理）</span>';
        }
    }

    function urgentRender(e) {
        var record = e.record;
        var urgent = record.urgent;
        if(urgent=='yes') {
            return '<span style="color:red">是</span>';
        } else {
            return '<span>否</span>';
        }
    }

    function processStatusRenderer(e) {
        var record = e.record;
        return '<span  title="查看详情" style="color:#44cef6;text-decoration:underline;" onclick="processStatusQuery(\''+record.applyNo+'\')">查看进度</span>';
    }
</script>
<redxun:gridScript gridId="applyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>