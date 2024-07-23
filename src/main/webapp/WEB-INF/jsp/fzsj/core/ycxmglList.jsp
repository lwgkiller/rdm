<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>仿真设计-异常项目管理</title>
    <%@include file="/commons/list.jsp" %>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts-5.1.2/dist/echarts.min.js"></script>
</head>
<body>
<div>

        <span style="float: right;	font-size: 15px;color: #333;vertical-align: middle">
            <input id="startYear" class="mini-combobox" style="width:105px;height: 30px"
                   textField="value" valueField="key" onvaluechanged="queryChartList"
                   required="false" allowInput="false" showNullItem="false"/>
            至
            <input id="endYear" class="mini-combobox" style="width:105px;height: 30px"
                   textField="value" valueField="key" onvaluechanged="queryChartList"
                   required="false" allowInput="false" showNullItem="false"/>
        </span>
    <div id="ndtjChart" style="height:510px;width: 100%;"></div>
</div>
<div style="height: 50%">
    <h1 style="background-color: white;font-weight: bold;">仿真异常管理</h1>
    <div class="mini-toolbar" >
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul >
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">任务名称：</span>
                        <input class="mini-textbox" id="questName" name="questName" />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">应用机型：</span>
                        <input class="mini-textbox" id="applicationType" name="applicationType" />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">任务来源：</span>
                        <input id="taskResource" name="taskResource" class="mini-combobox" style="width:120px;"
                               textField="text" valueField="id" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{id:'xpyf',text:'新品研发'},{id:'zyOrYycp',text:'在研/预研产品'},
                                        {id:'scgj',text:'市场改进'},{id:'hxjsyj',text:'核心技术研究'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">样机状态：</span>
                        <input id="prototypeState" name="prototypeState" class="mini-combobox" style="width:120px;"
                               textField="text" valueField="id" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{id:'yyj',text:'有样机'},{id:'wyj',text:'无样机'}]"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">仿真分析项：</span>
                        <input class="mini-textbox" id="fzlb" name="fzlb" />
                    </li>
                    <%--<li style="margin-right: 15px">--%>
                        <%--<span class="text" style="width:auto">申请时间：</span>--%>
                        <%--<input id="applyStartTime"  name="applyStartTime" format="yyyy-MM-dd"  class="mini-datepicker" allowInput="false"/>--%>
                        <%--<span class="text" style="width:auto">-</span>--%>
                        <%--<input id="applyEndTime"  name="applyEndTime" format="yyyy-MM-dd" class="mini-datepicker"  allowInput="false"/>--%>
                    <%--</li>--%>
                    <li style="margin-right: 15px; display: none;">
                        <span class="text" style="width:auto">任务状态：</span>
                        <input id="taskStatus" name="taskStatus" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..." value="RUNNING"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},{'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
                                        {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},{'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
                                        {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},{'key': 'PENDING', 'value': '挂起', 'css': 'gray'}]"
                        />
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearFormQuery()">清空查询</a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="ycxmglGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
             url="${ctxPath}/fzsj/core/fzsj/ycxmglListQuery.do" idField="id"
             multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
                <div name="action" cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
                <div field="questName" headerAlign="center" align="center" allowSort="false">任务名称</div>
                <div field="taskName" headerAlign='center' align='center' width="80">异常节点</div>
                <div field="ecsx" headerAlign='center' align='center' width="80">异常事项</div>
                <div field="applicationType" headerAlign="center" align="center" allowSort="false">应用机型</div>
                <div field="taskResource" headerAlign="center" align="center" allowSort="false" renderer="taskResourceRenderer" width="80">任务来源</div>
                <div field="prototypeState" headerAlign="center" align="center" allowSort="false" renderer="prototypeStateRenderer" width="60">样机状态</div>
                <div field="fzdx" headerAlign="center" align="center" allowSort="false" width="80">仿真对象</div>
                <div field="fzlb" headerAlign="center" align="center" allowSort="false" width="80">仿真分析项</div>
                <%--<div field="applyTime" dateFormat="yyyy-MM-dd" align="center" headerAlign="center" allowSort="true" width="80">申请时间</div>--%>

                <div field="allTaskUserNames" headerAlign='center' align='center'>当前处理人</div>
            </div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var ycxmglGrid=mini.get("ycxmglGrid");
    var ndtjChart = echarts.init(document.getElementById('ndtjChart'));
    var option = {
        title: {text: '仿真任务完成统计'},
        legend:{},
        tooltip:{
            formatter: function (params) {
                var res = '<p>' + params.name + '年任务量：</p>';
                var total = 0;
                for(var i = 1; i< params.dimensionNames.length; i++) {
                    res += '<p>'+ params.dimensionNames[i]+'：' + params.value[i]+ '</p>';
                    total += params.value[i];
                }
                res += '<p>共计：' + total + '</p>'
                return res;
            }
        },
        dataset:{
            source:[]
        },
        xAxis: {type: 'category'},
        yAxis: {name: '任务量/个'},
        series: [
            {type: 'bar',barGap: '0%'},
            {type: 'bar',barGap: '0%'},
            {type: 'bar',barGap: '0%'},
            {type: 'bar',barGap: '0%'},
            {type: 'bar',barGap: '0%'},
            {type: 'bar',barGap: '0%'}
        ]
    };
    var fzlyList = getDics("FZLY");

    $(function () {
        setFzrwwctjQueryYear();
        queryChartList();
        searchFrm();
    });

    function queryChartList() {
        var source = [];
        var firstSource = fzlyList.map(function(item){
            return item.text;
        });
        firstSource.unshift('field');
        source.push(firstSource);
        var ndtjList = getNdtjList();
        // if (ndtjList.length > 0) {
            var yearList = ndtjList.map(function(item){
                return {yyyy: item.yyyy};
            });
            for (var i = 0; i< yearList.length; i++) {
                var data = [];
                data.push(yearList[i].yyyy);
                var countList =  ndtjList.filter(function(item){
                    return item.yyyy == yearList[i].yyyy;
                });
                for (var j = 0; j< fzlyList.length; j++) {
                    var dataList = countList.filter(function (item) {
                        return fzlyList[j].key_ == item.field;
                    })
                    if (dataList.length > 0) {
                        data.push(dataList[0].count);
                    } else {
                        data.push(0);
                    }
                }
                source.push(data);
            }
        // }
        option.dataset.source = source;
        ndtjChart.setOption(option);
    }

    //操作栏
    ycxmglGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title="明细" onclick="toYcxmglDetail(\'' + record.id + '\')">明细</span>';
        if (record.myTaskId) {
            s += '<span  title="办理" onclick="ycxmglTask(\'' + record.myTaskId + '\')">办理</span>';
        }
        return s;
    }

    //明细
    function toYcxmglDetail(fzsjId) {
        var action = "detail";
        var url = jsUseCtxPath + "/fzsj/core/fzsj/fzsjEditPage.do?action=" + action + "&fzsjId=" + fzsjId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (ycxmglGrid) {
                    ycxmglGrid.reload()
                }
            }
        }, 1000);
    }

    //跳转到任务的处理界面
    function ycxmglTask(taskId) {
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
                            if (ycxmglGrid) {
                                ycxmglGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function taskResourceRenderer(e) {
        var record = e.record;
        var taskResource = record.taskResource;
        var arr = [{key:'xpyf',value:'新品研发'},{key:'zyOrYycp',value:'在研/预研产品'},
            {key:'scgj',value:'市场改进'},{key:'hxjsyj',value:'核心技术研究'}];
        return $.formatItemValue(arr,taskResource);
    }

    function prototypeStateRenderer(e) {
        var record = e.record;
        var prototypeState = record.prototypeState;
        var arr = [{key:'yyj',value:'有样机'},{key:'wyj',value:'无样机'}];
        return $.formatItemValue(arr,prototypeState);
    }

    function taskStatusRenderer(e) {
        var record = e.record;
        var taskStatus = record.taskStatus;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];
        return $.formatItemValue(arr,taskStatus);
    }
    
    function clearFormQuery() {
        mini.get("questName").setValue("");
        mini.get("applicationType").setValue("");
        mini.get("taskResource").setValue("");
        mini.get("prototypeState").setValue("");
        mini.get("fzlb").setValue("");
        mini.get("applyStartTime").setValue("");
        mini.get("applyEndTime").setValue("");
        searchFrm();
    }

    function getYearList() {
        var data = [];
        $.ajax({
            async: false,
            url: jsUseCtxPath + '/fzsj/core/fzsj/getYearList.do',
            type: 'GET',
            contentType: 'application/json',
            success: function (result) {
                data = result;
            }
        });
        return data;
    }

    function getNdtjList() {
        var startYear = mini.get("startYear").getValue();
        var endYear = mini.get("endYear").getValue();
        var data = [];
        $.ajax({
            async: false,
            url: jsUseCtxPath + '/fzsj/core/fzsj/getNdtjList.do?startYear='+ startYear + '&endYear=' + endYear,
            type: 'GET',
            contentType: 'application/json',
            success: function (result) {
                data = result;
            }
        });
        return data;
    }

    function setFzrwwctjQueryYear() {
        var startYearData=generateFzrwwctjYear();
        var endYearData=generateFzrwwctjYear();
        mini.get("startYear").load(startYearData);
        mini.get("endYear").load(endYearData);
        var endYear=new Date().getFullYear();
        var startYear=endYear-4;
        mini.get("startYear").setValue(startYear);
        mini.get("endYear").setValue(endYear);
    }

    function generateFzrwwctjYear() {
        var data=[];
        var nowDate=new Date();
        var startY=nowDate.getFullYear()-10;
        var endY=nowDate.getFullYear()+30;
        for(var i=startY;i<=endY;i++) {
            var oneData={};
            oneData.key=i;
            oneData.value=i+'年';
            data.push(oneData);
        }
        return data;
    }

</script>
<redxun:gridScript gridId="ycxmglGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

