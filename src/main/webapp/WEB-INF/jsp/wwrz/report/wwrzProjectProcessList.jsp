<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>名称列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-left:15px;margin-right: 15px">
                    <span class="text" style="width:auto">流程编号: </span>
                    <input class="mini-textbox" name="applyId"/>
                </li>
                <li style="margin-left:15px;margin-right: 15px">
                    <span class="text" style="width:auto">产品主管: </span>
                    <input class="mini-textbox" name="productLeaderName"/>
                </li>
                <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                      style="width:auto">申请单流程状态: </span>
                    <input id="instStatus" name="instStatus" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm()"
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
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
<%--                    <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportExcel()">按条件导出</a>--%>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px">
                        <span class="text" style="width:auto">销售型号: </span>
                        <input class="mini-textbox" name="productModel"/>
                    </li>
                    <li style="margin-left:15px;margin-right: 15px">
                        <span class="text" style="width:auto">认证项目: </span>
                        <input class="mini-textbox" name="itemName"/>
                    </li>
                </ul>
            </div>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/wwrz/core/testPlan/exportExcel.do" method="post"
              target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onrowdblclick="onRowDblClick()"
         url="${ctxPath}/wwrz/core/report/queryApplyList.do"  idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
            <div field="id" width="120px" headerAlign="center" align="center"
                 allowSort="true">流程编号
            </div>
            <div field="status" width="80px" headerAlign="center" align="center" renderer="onStatusRenderer" allowSort="true">
                运行状态
            </div>
            <div field="planId" width="100px" headerAlign="center" align="center" allowSort="true" renderer="onPlanRenderer">
                认证计划
            </div>
            <div field="productModel" width="100px" headerAlign="center" align="center"
                 allowSort="true">销售型号
            </div>
            <div field="itemNames" width="120px" headerAlign="center" align="left"
                 allowSort="true">认证项目
            </div>
            <div field="productLeaderName" width="100px" headerAlign="center" align="center"
                 allowSort="true">产品主管
            </div>
            <div field="taskName" width="100px" headerAlign="center" align="center"
                 allowSort="false">当前阶段
            </div>
            <div field="allTaskUserNames" width="100px" headerAlign="center" align="center"
                 allowSort="false">当前处理人
            </div>
            <div  width="80px" headerAlign="center" align="center"  renderer="onProgressRenderer"
                  allowSort="false">项目进度
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    function onStatusRenderer(e) {
        var record = e.record;
        var instStatus = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, instStatus);
    }
    function onProgressRenderer(e) {
        var record = e.record;
        var progressNum = 0;
        if(record.status == 'SUCCESS_END') {
            progressNum = 100;
        }else{
            var currentProcessTask = record.taskName;
            var stage = currentProcessTask.split("");
            var stageInt = '';
            for(var i=0;i<stage.length;i++){
                if(i==0){
                    stageInt = stage[i];
                }
                if(i==1){
                    var flag = isNaN(stage[i])
                    if(!flag){
                        stageInt += stage[i];
                    }
                }
            }
            if(stageInt==''){
                stageInt = '0';
            }
            progressNum = (parseInt(stageInt)/16*100).toFixed(2);
        }
        var s ='<div class="mini-progressbar" id="p1" style="border-width: 0px;width:120px"><div class="mini-progressbar-border"><div class="mini-progressbar-bar" style="width: '+progressNum+'%;"></div><div class="mini-progressbar-text" id="p1$text">'+progressNum+'%</div></div></div>';
        return s;
    }
    function onPlanRenderer(e) {
        var record = e.record;
        var s = '';
        if(record.planId){
            s = '<span style="color: black">计划内</span>'
        }else{
            s = '<span style="color: red">计划外</span>'
        }
        return s;
    }
    function onRowDblClick() {
        rows = listGrid.getSelecteds();
        var id = rows[0].id;
        var action = "detail";
        var url = jsUseCtxPath + "/wwrz/core/test/edit.do?action=" + action + "&id=" + id + "&status=" + status;
        var winObj = window.open(url);
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
