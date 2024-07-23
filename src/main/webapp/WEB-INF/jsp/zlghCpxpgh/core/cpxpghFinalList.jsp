<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">部门：</span>
                    <input class="mini-textbox" id="department" name="department"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品状态：</span>
                    <input id="productStatus" name="productStatus" class="mini-combobox" style="width:120px;"
                       textField="value" valueField="key" emptyText="请选择..."
                       required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                       data="[{key:'lc',value:'量产'},{key:'kfz',value:'开发中'},{key:'ztkf',value:'暂停开发'},{key:'tc',value:'停产'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">变更状态：</span>
                    <input id="changeStatus" name="changeStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{key:'wbg',value: '未变更'},{key:'bgz',value: '变更中'}, {key:'bgwc', value:'变更完成'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">可售年份：</span>
                    <input class="mini-textbox" id="salsesYear" name="salsesYear"/>
                </li>
                <br>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="cpxpghFinalDel()">删除</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="cpxpghFinalGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/strategicplanning/core/cpxpgh/cpxpghFinalListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="140" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="changeStatus" headerAlign="center" align="center" width="100" renderer="changeStatusRenderer">变更状态</div>
            <div field="department" headerAlign="center" align="center" width="100">部门</div>
            <div field="salesModel" headerAlign="center" align="center" width="100">销售型号</div>
            <div field="designModel" headerAlign="center" align="center" width="100">设计型号</div>
            <div field="productStatus" headerAlign="center" align="center" width="70" renderer="productStatusRenderer">产品状态</div>
            <div field="salsesYear" headerAlign="center" align="center" width="60">可售年份</div>
            <div field="zjwsCost" headerAlign="center" align="center" width="90">整机未税成本</div>
            <div field="zjSalePrice" headerAlign="center" align="center" width="90">整机销售价格</div>
            <div field="zjbjgxRate" headerAlign="center" align="center" width="90">整机边际贡献率</div>
            <div field="archivedFile" headerAlign="center" align="center" width="90" renderer="archivedFileRenderer">产品归档文件</div>
            <div field="hbxxgk" headerAlign="center" align="center" width="90" renderer="hbxxgkRenderer">环保信息公开</div>
            <div field="cpxssyzt" headerAlign="center" align="center" width="90" renderer="cpxssyztRenderer">产品型式试验状态</div>
            <div field="cpckrzzt" headerAlign="center" align="center" width="90" renderer="cpckrzztRenderer">产品出口认证状态</div>
            <div field="cbsc" headerAlign="center" align="center" width="90" renderer="cbscRenderer">操保手册/零件图册</div>
            <div field="cptszt" headerAlign="center" align="center" width="90" renderer="cptsztRenderer">产品调试状态</div>
            <div field="cpcskhzt" headerAlign="center" align="center" width="90" renderer="cpcskhztRenderer">产品测试考核状态</div>
            <div field="creator" align="center" headerAlign="center" width="60">创建者</div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" width="150" align="center" headerAlign="center" allowSort="true">创建时间</div>
        </div>
    </div>
</div>

<div id="historyWindow" title="历史数据" class="mini-window" style="width:1500px;height:600px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="height: 100%;">
        <div id="historyGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             allowCellWrap="true" showPager="false"
             showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
             multiSelect="false" showColumnsMenu="false"  allowAlternating="true" />
            <div property="columns">
                <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
                <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="historyActionRenderer" cellStyle="padding:0;">操作</div>
                <div field="department" headerAlign="center" align="center" width="100">部门</div>
                <div field="salesModel" headerAlign="center" align="center" width="100">销售型号</div>
                <div field="designModel" headerAlign="center" align="center" width="100">设计型号</div>
                <div field="productStatus" headerAlign="center" align="center" width="70" renderer="productStatusRenderer">产品状态</div>
                <div field="salsesYear" headerAlign="center" align="center" width="60">可售年份</div>
                <div field="zjwsCost" headerAlign="center" align="center" width="90">整机未税成本</div>
                <div field="zjSalePrice" headerAlign="center" align="center" width="90">整机销售价格</div>
                <div field="zjbjgxRate" headerAlign="center" align="center" width="90">整机边际贡献率</div>
                <div field="archivedFile" headerAlign="center" align="center" width="90" renderer="archivedFileRenderer">产品归档文件</div>
                <div field="hbxxgk" headerAlign="center" align="center" width="90" renderer="hbxxgkRenderer">环保信息公开</div>
                <div field="cpxssyzt" headerAlign="center" align="center" width="90" renderer="cpxssyztRenderer">产品型式试验状态</div>
                <div field="cpckrzzt" headerAlign="center" align="center" width="90" renderer="cpckrzztRenderer">产品出口认证状态</div>
                <div field="cbsc" headerAlign="center" align="center" width="90" renderer="cbscRenderer">操保手册/零件图册</div>
                <div field="cptszt" headerAlign="center" align="center" width="90" renderer="cptsztRenderer">产品调试状态</div>
                <div field="cpcskhzt" headerAlign="center" align="center" width="90" renderer="cpcskhztRenderer">产品测试考核状态</div>
                <div field="creator" align="center" headerAlign="center" width="60">创建者</div>
                <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" width="150" align="center" headerAlign="center">创建时间</div>
            </div>
        </div>
    </div>
<div class="mini-toolbar" style="text-align: center;padding-top: 8px;padding-bottom: 8px;">
    <a class="mini-button" onclick="closeHistoryWindow()">关闭</a>
</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var cpxpghFinalGrid = mini.get("cpxpghFinalGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var isFgld = "${isFgld}";
    var isZlghzy = "${isZlghzy}";
    var mainGroupName = "${mainGroupName}";
    var historyWindow = mini.get("historyWindow");
    var historyGrid = mini.get("historyGrid");
    var rendererData = [{key:'yes',value: '√'}, {key:'no', value:'×'}];
    $(function () {
        searchFrm();
    });
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title="明细" onclick="cpxpghFinalDetail(\'' + record.id + '\')">明细</span>';
        if (record.CREATE_BY_ == currentUserId && record.changeStatus != 'bgz') {
            s += '<span  title="变更" onclick="changeCpxpgh(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">变更</span>';
        }
        if (isZlghzy == 'true') {
            s += '<span  title="删除" onclick="cpxpghFinalDel(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        s+= '<span  title="历史数据" onclick="openHistoryWindow(\'' + record.id + '\')">历史数据</span>';
        return s;
    }

    //行功能按钮
    function historyActionRenderer(e) {
        var record = e.record;
        var s = '<span  title="明细" style="color:#409EFF;" onclick="cpxpghFinalDetail(\'' + record.id + '\')">明细</span>';
        return s;
    }


    function productStatusRenderer(e) {
        var record = e.record;
        var arr = [{key:'lc',value:'量产'},{key:'kfz',value:'开发中'},{key:'ztkf',value:'暂停开发'},{key:'tc',value:'停产'}];
        return $.formatItemValue(arr, record.productStatus);
    }

    function archivedFileRenderer(e) {
        var record = e.record;
        return $.formatItemValue(rendererData, record.archivedFile);
    }

    function hbxxgkRenderer(e) {
        var record = e.record;
        return $.formatItemValue(rendererData, record.hbxxgk);
    }

    function cpxssyztRenderer(e) {
        var record = e.record;
        return $.formatItemValue(rendererData, record.cpxssyzt);
    }

    function cbscRenderer(e) {
        var record = e.record;
        return $.formatItemValue(rendererData, record.cbsc);
    }

    function cptsztRenderer(e) {
        var record = e.record;
        return $.formatItemValue(rendererData, record.cptszt);
    }

    function cpcskhztRenderer(e) {
        var record = e.record;
        return $.formatItemValue(rendererData, record.cpcskhzt);
    }

    function cpckrzztRenderer(e) {
        var record = e.record;
        var arr = [{key:'tuv',value: '北美TUV'}, {key:'ce', value:'CE'}, {key:'eac', value:'俄罗斯EAC'}];
        return $.formatItemValue(arr, record.cpckrzzt);
    }

    //明细（直接跳转到详情的业务controller）
    function cpxpghFinalDetail(cpxpghId, taskStatus) {
        var action = "detail";
        var url = jsUseCtxPath + "/strategicplanning/core/cpxpgh/cpxpghFinalEditPage.do?cpxpghId=" + cpxpghId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cpxpghFinalGrid) {
                    cpxpghFinalGrid.reload()
                }
            }
        }, 1000);
    }

    function changeStatusRenderer(e) {
        var record = e.record;
        var arr = [
            {key:'wbg',value: '未变更','css': 'orange'},
            {key:'bgz',value: '变更中', 'css': 'green'},
            {key:'bgwc', value:'变更完成', 'css': 'blue'}
            ];
        return $.formatItemValue(arr, record.changeStatus);
    }

    function changeCpxpgh(record) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/CPXPGH/start.do?changeId=" + record.id;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cpxpghFinalGrid) {
                    cpxpghFinalGrid.reload()
                }
            }
        }, 1000);
    }

    function cpxpghFinalDel(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = cpxpghFinalGrid.getSelecteds();
        }
        if (isZlghzy == 'false') {
            mini.alert("权限不足,请联系管理员");
            return;
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var ids = [];
                var existStartInst = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.changeStatus != 'bgz') {
                        ids.push(r.id);
                    } else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    alert("非'变更中'状态数据可由本人删除");
                }
                if (ids.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/strategicplanning/core/cpxpgh/deleteCpxpgh.do",
                        method: 'POST',
                        data: {ids: ids.join(','), instIds: ''},
                        showMsg: false,
                        success: function (data) {
                            if (data.success) {
                                mini.alert(data.message,"提示", function () {
                                    searchFrm();
                                })
                            }
                        }
                    });
                }
            }
        });
    }

    function openHistoryWindow(finalId) {
        var url = jsUseCtxPath + "/strategicplanning/core/cpxpgh/queryHistoryData.do?finalId=" + finalId;
        historyWindow.show();
        historyGrid.setUrl(url);
        historyGrid.load()
    }

    function closeHistoryWindow(){
        historyWindow.hide();
        searchFrm();
    }
</script>
<redxun:gridScript gridId="cpxpghFinalGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>