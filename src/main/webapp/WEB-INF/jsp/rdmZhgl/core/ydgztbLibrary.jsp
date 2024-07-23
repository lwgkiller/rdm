<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>月度工作提报知识库</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">提报人: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                    <span class="text" style="width:auto">部门名称: </span>
                    <input class="mini-textbox" id="departName" name="departName"/>
                    <span class="text " style="width:auto">月份: </span>
                    <input class="mini-monthpicker" id="yearMonth" name="yearMonth"/>
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
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeBusiness()">删除</a>
                    <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportExcel()">按条件导出</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li>
                        <span class="text" style="width:auto">工作报告名称: </span>
                        <input class="mini-textbox" id="workReportName" name="workReportName"/>
                        <span class="text" style="width:auto">是否优秀案例: </span>
                        <input id="isYxal" name="isYxal" class="mini-combobox" style="width:150px;"
                               textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm()"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : 'yes','value' : '是'},{'key' : 'no','value' : '否'}]"
                        />
                        <span class="text" style="width:auto">分类: </span>
                        <input class="mini-combobox" id="category" name="category"
                               textField="value" valueField="key" required="false" allowInput="false" showNullItem="false"/>
                        <span class="text" style="width:auto">关键词: </span>
                        <input class="mini-textbox" id="keyWords" name="keyWords"/>
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="ydgztbLibraryGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/zhgl/core/ydgztb/getTBLibraryList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div type="indexcolumn" width="20" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="20" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="workReportName" headerAlign="center" align="center" allowSort="false" width="50">工作报告名称</div>
            <div field="category" headerAlign="center" align="center" allowSort="false" width="35" renderer="categoryRenderer">分类</div>
            <div field="keyWords" headerAlign="center" align="center" allowSort="false" width="50">关键词</div>
            <div field="isYxal" headerAlign="center" align="center" allowSort="false" width="20" renderer="yxalRender">优秀案例</div>
            <div field="creatorName" headerAlign="center" align="center" allowSort="false" width="35">提报人</div>
            <div field="departName" headerAlign="center" align="center" allowSort="false" width="45">部门</div>
            <div field="yearMonth" dateFormat="yyyy-MM" headerAlign="center" align="center"
                 allowSort="false" width="35">年月
            </div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/zhgl/core/ydgztb/exportExecl.do" method="post"
      target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var ydgztbLibraryGrid = mini.get("ydgztbLibraryGrid");
    const category = [
        {'key': '001', 'value': '控制'},
        {'key': '002', 'value': '液压'},
        {'key': '003', 'value': '动力'},
        {'key': '004', 'value': '工作装置'},
        {'key': '005', 'value': '底盘'},
        {'key': '006', 'value': '覆盖件'},
        {'key': '007', 'value': '转台'},
        {'key': '008', 'value': '电气'},
        {'key': '009', 'value': '仿真'},
        {'key': '010', 'value': '零部件测试'},
        {'key': '011', 'value': '整机测试'},
        {'key': '012', 'value': '电动化'},
        {'key': '013', 'value': '附属机具'},
        {'key': '014', 'value': '整机'},
        {'key': '015', 'value': '标准'},
        {'key': '016', 'value': '其他'}];
    function categoryRenderer(e) {
        var key = e.value;
        for (var x = 0; x < category.length; x++) {
            if (category[x].key == key) {
                return category[x].value;
            }
        }
    }
    $(function () {
        mini.get("category").setData(category);
        searchFrm();
    });

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        s = '<span  title="查看" onclick="ydgztbBrowse(\'' + applyId + '\',\'' + instId + '\')">查看</span>';
        return s;
    }

    function yxalRender(e) {
        var record = e.record;
        var yxal = record.isYxal;
        if (yxal == "yes") {
            return "√";
        } else {
            return "";
        }
    }

    function ydgztbBrowse(applyId) {
        var url = jsUseCtxPath + "/zhgl/core/ydgztb/applyEditPage.do?action=browse&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (ydgztbLibraryGrid) {
                    ydgztbLibraryGrid.reload()
                }
            }
        }, 1000);
    }

    function exportExcel() {
        var params = [];
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }

</script>
<redxun:gridScript gridId="ydgztbLibraryGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

