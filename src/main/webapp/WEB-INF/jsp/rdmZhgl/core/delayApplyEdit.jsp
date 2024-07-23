<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>编辑申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/delayApplyEdit.js?version=${static_res_version}"  type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 80%;">
        <form id="applyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="taskId_" name="taskId_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="mainId" name="mainId" class="mini-hidden"/>
            <input id="deptId" name="deptId" class="mini-hidden"/>
            <input id="mainDepId" name="mainDepId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-size: 18px !important;font-weight: bold">
                    休息日加班申请单
                </caption>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        申请单标题<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="title" name="title" emptyText="请写明xx号~xx号周末加班或如春节、国庆加班"  class="mini-textbox" style="width:99%;height:34px">
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        申请部门：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input name="deptName" readonly class="mini-textbox" style="width:99%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center">备注：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:100%;height:60px;line-height:25px;"
                                  label="" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                  allowinput="true"
                                   mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
        <%--<p style="font-size: 17px;margin-top: 20px;font-weight: bold">加班汇总明细</p>--%>
        <div class="mini-toolbar" style="margin-bottom: 5px">
            <ul class="toolBtnBox">
                <li style="float: left">
                    <span class="text" style="width:auto">加班人员: </span>
                    <input class="mini-textbox" id="userName" style="width:125px"/>
                    <span class="text" style="width:auto">加班日期: </span>
                    <input id="applyTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:125px"/>
                    <a id="searchButton" class="mini-button" plain="true" onclick="loadItemGrid()">查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="addButton" class="mini-button" plain="true" onclick="addRow()">新增</a>
                    <a id="delButton" class="mini-button btn-red" plain="true" onclick="removeRow()">删除</a>
                    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">（注意：新增、删除或编辑后请点击“编辑后保存”。）
                    </p>
                </li>
            </ul>
        </div>
        <div id="itemGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" allowAlternating="false" showPager="false" multiSelect="true" allowCellEdit="true"
             allowCellSelect="true" enableHotTrack="false"
             editNextOnEnterKey="true" editNextRowCell="true">
            <div property="columns">S
                <div type="checkcolumn" width="20"></div>
                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                <div field="userId" displayfield="userName" width="60" headerAlign="center" align="center" allowSort="true">加班人员<span
                        style="color: #ff0000">*</span>
                    <input property="editor" class="mini-user" allowinput="false" style="width:auto;"  single="true"/>
                </div>
                <div field="workDate" dateFormat="yyyy-MM-dd"  width="60" headerAlign="center" align="center"  allowSort="true">加班日期<span
                        style="color: #ff0000">*</span>
                    <input property="editor" allowinput="false"  class="mini-datepicker" style="width:auto;"  />
                </div>
                <div field="workTime" displayField="workTime" width="60" headerAlign="center" align="center">加班时长<span
                        style="color: #ff0000">*</span>
                    <input property="editor" class="mini-combobox" style="width:90%;"
                           textField="text" valueField="value" emptyText="请选择..."
                           allowInput="false" showNullItem="false" nullItemText="请选择..." />
                </div>
                <div field="reason"  align="left" width="250" headerAlign="center">
                    加班工作事项<span  style="color: #ff0000">*</span>
                    <input property="editor" class="mini-textbox" style="width:100%;" />
                </div>
            </div>
        </div>
    </div>

</div>

<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var ApplyObj =${applyObj};
    var status = "${status}";
    var applyForm = new mini.Form("#applyForm");
    var itemGrid = mini.get("itemGrid");
    var permission = ${permission};
    var techAdmin = ${techAdmin};
    var currentUserId="${currentUserId}";
    var workTimeList=[{text:'全天',value:'全天'},{text:'上午',value:'上午'},{text:'下午',value:'下午'}];


    function addRow() {
        var newRow = {workTime: "全天"};
        itemGrid.addRow(newRow, 0);
    }
    function removeRow() {
        var rows = itemGrid.getSelecteds();
        if (rows.length > 0) {
            var finalRows=[];
            //除了人资专员外，其他人只能删除自己创建的
            if(!permission&&!techAdmin) {
                for(var index=0;index<rows.length;index++) {
                    if(!rows[index].CREATE_BY_ || rows[index].CREATE_BY_==currentUserId) {
                        finalRows.push(rows[index]);
                    }
                }
            } else {
                finalRows=rows;
            }
            if(finalRows.length<=0) {
                mini.alert("请至少选中一条自己创建的记录");
                return;
            }
            mini.showMessageBox({
                title: "提示信息！",
                iconCls: "mini-messagebox-info",
                buttons: ["ok", "cancel"],
                message: "是否确定删除！",
                callback: function (action) {
                    if (action == "ok") {
                        itemGrid.removeRows(finalRows, false);
                    }
                }
            });

        } else {
            mini.alert("请至少选中一条记录");
            return;
        }
    }


    itemGrid.on("beforeload", function (e) {
        if (itemGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });

    /*function saveRow() {
        //判断是否已经保存，主表单
        var id = mini.get('id').getValue();
        if(!id){
            mini.alert("请先保存草稿");
        }

        var data = itemGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                data[i].mainId = id;
                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].userId||!data[i].workDate ||!data[i].reason) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
            }
            /!*if(needReload) {
                var countCheck={};
                var dataAll = itemGrid.getData();
                for(var i = 0; i < dataAll.length; i++){
                    var oneRow=dataAll[i];
                    if(!countCheck[oneRow.userId]) {
                        countCheck[oneRow.userId]=[];
                    }
                    countCheck[oneRow.userId].push(oneRow.workDate);
                }
                for(var key in countCheck) {
                    if(countCheck[key]&&countCheck[key].length>1) {
                        message = key+"在"+countCheck[key][0]+"存在多条，请修正！";
                        needReload = false;
                        break;
                    }
                }
            }*!/
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/rdmZhgl/core/applyDetail/dealData.do",
                    data: json,
                    type: "post",
                    contentType: 'application/json',
                    async: false,
                    success: function (data) {
                        if (data && data.message) {
                            message = data.message;
                        }
                        if(data && !data.success){
                            needReload = false;
                        }
                    }
                });
            }
        }
        mini.showMessageBox({
            title: "提示信息",
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok" && needReload) {
                    itemGrid.reload();
                }
            }
        });
    }*/

    itemGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        var createBy=record.CREATE_BY_;
        if(!permission &&!techAdmin) {
            if(!createBy || createBy==currentUserId) {
                e.editor.setEnabled(true);
            } else {
                e.editor.setEnabled(false);
            }
        } else {
            e.editor.setEnabled(true);
        }
        if (e.field == "workTime") {
            e.editor.setData(workTimeList);
        }
    });

    itemGrid.on("drawcell", function (e) {
        var record=e.record;

        if(!record.id&&record.reason=='未申报') {
            e.cellStyle="background-color: #ccc;";
        }
    });


</script>
</body>
</html>
