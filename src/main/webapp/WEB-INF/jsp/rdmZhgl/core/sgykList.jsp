<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>业务交流维度配置</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">年份: </span>
                    <input id="signYear" name="signYear"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">月份: </span>
                    <input id="signMonth" name="signMonth"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signMonth"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="create()">生成台账</a>
                    <%--<a class="mini-button" style="margin-right: 5px" plain="true" onclick="calculate()">计算</a>--%>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="sgykGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/zhgl/core/sgyk/sgykListQuery.do"
         idField="id" allowAlternating="true" showPager="false" multiSelect="false" allowCellEdit="true" allowCellSelect="true"
         editNextRowCell="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="signYear" name="signYear" width="60" headerAlign="center" align="center">年份
            </div>
            <div field="signMonth" name="signMonth" width="60" headerAlign="center" align="center">月份
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var sgykGrid = mini.get("sgykGrid");
    var currentYear = "";
    var currentMonth = "";
    //..
    $(function () {
        var date = new Date();
        if (date.getMonth().toString().length == 1) {
            currentMonth = "0" + (date.getMonth() + 1).toString();
        } else {
            currentMonth = (date.getMonth() + 1).toString();
        }
        currentYear = date.getFullYear().toString();
        mini.get("signYear").setValue(currentYear);
        mini.get("signMonth").setValue(currentMonth);
        searchFrm()
    });
    //..
    sgykGrid.on("rowdblclick", function (e) {
        var record = e.record;
        var sgykId = record.id;
        var signYear = record.signYear;
        var signMonth = record.signMonth;
        showDetail(sgykId, signYear, signMonth);
    });
    //..
    function create() {
        if (mini.get("signYear").getText() == "" || mini.get("signMonth").getText() == "") {
            mini.alert("请输入生成台账的年份和月份！");
            return;
        }
        mini.confirm("确定生成台账？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/sgyk/createSgyk.do",
                    method: 'POST',
                    data: {
                        signYear: mini.get("signYear").getText(),
                        signMonth: mini.get("signMonth").getText()
                    },
                    success: function (data) {
                        if (data) {
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
    //..
    function calculate() {
        if (mini.get("signYear").getText() == "" || mini.get("signMonth").getText() == "") {
            mini.alert("请输入生成台账的年份和月份！");
            return;
        }
        mini.confirm("确定进行计算？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/sgyk/calculateSgyk.do",
                    method: 'POST',
                    data: {
                        signYear: mini.get("signYear").getText(),
                        signMonth: mini.get("signMonth").getText()
                    },
                    success: function (data) {
                        if (data) {
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
    //..
    function showDetail(sgykId, signYear, signMonth) {
        var url = jsUseCtxPath + "/zhgl/core/sgyk/editPage.do?sgykId=" + sgykId + "&signYear=" + signYear + "&signMonth=" + signMonth;
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (sgykGrid) {
                    sgykGrid.reload();
                }
            }
        }, 1000);
    }
</script>
<redxun:gridScript gridId="sgykGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>