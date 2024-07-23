<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>三高一可指标填写</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <style type="text/css">
        .myrow {
            background: #eecc53;
        }
    </style>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveContract" class="mini-button" onclick="saveSgykDetail()">保存</a>
        <a id="calculate" class="mini-button" onclick="calculate()">计算</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div id="sgykDetailGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/zhgl/core/sgyk/sgykDetailListQuery.do"
         idField="id" allowAlternating="true" showPager="false" multiSelect="false" allowCellEdit="true" allowCellSelect="true"
         editNextRowCell="true" oncellbeginedit="cellBeginEdit" ondrawcell="drawcell" showColumnsMenu="true">
        <div property="columns" hideable="true">
            <div type="checkcolumn" width="20"></div>
            <div field="signYear" name="signYear" width="30" headerAlign="center" align="center">年份
            </div>
            <div field="signMonth" name="signMonth" width="30" headerAlign="center" align="center">月份
            </div>
            <div field="normClass" name="normClass" width="30" headerAlign="center" align="center">指标类别
            </div>
            <div field="normDesp" name="normDesp" width="60" headerAlign="center" align="center">指标描述
            </div>
            <div field="normKey" name="normKey" width="60" headerAlign="center" align="center"
                 visible="false">指标编码
            </div>
            <div field="normValue" name="normValue" width="30" headerAlign="center" align="center">指标值
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="999999999"/>
            </div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var sgykId = "${sgykId}";
    var signYear = "${signYear}";
    var signMonth = "${signMonth}";
    var currentUserNo = "${currentUserNo}";
    var sgykDetailGrid = mini.get("sgykDetailGrid");
    sgykDetailGrid.load();
    sgykDetailGrid.on("beforeload", function (sender) {
        sender.data = {"sgykId": sgykId};
    });
    //由于后端模板没有设置负责人字段，因此只能出此下策，将负责人ids配置到数据字典的描述里面
    var sgykNormMap = new Map();
    var url = "${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=SGYKZB";
    $.getJSON(url, function callback(json) {
        json.forEach(function (e) {
                sgykNormMap.set(e.key, e.descp.split(","))
            }
        );
    });

    //每次渲染行的时候看看对应的指标key的字典描述里面的用户有没有当前用户，有就特殊渲染
    function drawcell(e) {
        debugger;
        var userNos = sgykNormMap.get(e.record.normKey);
        userNos.forEach(function (e2) {
            if (e2 == currentUserNo) {
                e.rowCls = "myrow";
            }
        })
    }

    function cellBeginEdit(sender) {
        debugger;
        if (sender.record.calculateClass == "neg") {
            sender.editor.setEnabled(true);
        } else {
            sender.editor.setEnabled(false);
        }
    }

    function saveSgykDetail() {
        var postData = sgykDetailGrid.getChanges("modified");
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/zhgl/core/sgyk/saveSgykDetail.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            sgykDetailGrid.reload();
                        }
                    });
                }
            }
        });
    }

    function calculate() {
        mini.confirm("确定进行计算？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/sgyk/calculateSgyk.do",
                    method: 'POST',
                    data: {
                        signYear: signYear,
                        signMonth: signMonth
                    },
                    success: function (data) {
                        if (data) {
                            sgykDetailGrid.load();
                        }
                    }
                });
            }
        });
    }

</script>
</body>
</html>
