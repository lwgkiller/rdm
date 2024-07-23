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
                    <span class="text" style="width:auto">出口国家：</span>
                    <input class="mini-textbox" id="exportingCountry" name="exportingCountry"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机PIN码：</span>
                    <input class="mini-textbox" id="pin" name="pin"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">需求通知单号：</span>
                    <input class="mini-textbox" id="demandNo" name="demandNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">发运通知单号：</span>
                    <input class="mini-textbox" id="dispatchNo" name="dispatchNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">语言：</span>
                    <input class="mini-textbox" id="languages" name="languages"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">发车日期：</span>
                    <input id="dispatchTimeFPW" name="dispatchTimeFPW" class="mini-datepicker"
                           format="yyyy-MM-dd" showTime="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">创建日期(起)：</span>
                    <input id="startTime" name="startTime" class="mini-datepicker"
                           format="yyyy-MM-dd" showTime="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">创建日期(止)：</span>
                    <input id="endTime" name="endTime" class="mini-datepicker"
                           format="yyyy-MM-dd" showTime="false"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="removeBusiness" class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="removeBusiness()">删除</a>
                    <a id="saveStatusDesc" class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="saveStatusDesc()">保存</a>
                    <a id="openImportWindow" class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="openImportWindow()">导入</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportList()"><spring:message
                            code="page.manualTopicList.name20"/></a>

                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<%-------------------------------%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" allowHeaderWrap="true"
         oncellbeginedit="onCellBeginEdit"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/dispatchFPWListQuery.do" virtualScroll="false">
        <div property="columns">
            <div type="checkcolumn" width="40"></div>
            <div field="taskStatus" width="120" headerAlign="center" align="center" renderer="taskStatusRenderList">
                任务状态
            </div>
            <div field="manualStatus" width="540" headerAlign="center" align="center"
                 <%--renderer="manualStatusRenderList"--%>
            >
                操保手册状态
            </div>
            <div field="statusDesc" width="200" headerAlign="center" align="center">操保手册状态描述
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="exportingCountry" width="120" headerAlign="center" align="center" allowSort="true"
                 renderer="render">出口国家
            </div>
            <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true"
                 renderer="render">整机物料编码
            </div>
            <div field="designModel" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">
                设计型号
            </div>
            <div field="salesModel" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">
                销售型号
            </div>
            <div field="pin" width="180" headerAlign="center" align="center" allowSort="true" renderer="render">整机PIN码
            </div>
            <div field="demandNo" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">
                需求通知单号
            </div>
            <div field="dispatchNo" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">
                发运通知单号
            </div>
            <div field="detailNo" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">
                明细号
            </div>
            <div field="languages" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">
                语言
            </div>
            <div field="dispatchTimeFPW" width="160" headerAlign="center" align="center" allowSort="true"
                 dateFormat="yyyy-MM-dd">发车日期
            </div>
            <div field="CREATE_TIME_" width="160" headerAlign="center" align="center" allowSort="true"
                 dateFormat="yyyy-MM-dd">创建日期
            </div>
        </div>
    </div>
</div>
<%-------------------------------%>
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importBusiness()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">成品库发运产品明细导入模板.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<%--mh导出--%>
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/exportPartsAtlas/exportData.do" method="post"
      target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<%-------------------------------%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var whatIsTheRole = "${whatIsTheRole}";//sa,apply,browse
    var manualAdmin = "${manualAdmin}";
    var importWindow = mini.get("importWindow");
    //..
    $(function () {
        if (whatIsTheRole == 'apply') {
            mini.get("removeBusiness").setEnabled(false);
        } else if (whatIsTheRole == 'browse') {
            mini.get("removeBusiness").setEnabled(false);
            mini.get("openImportWindow").setEnabled(false);
        }
        //非操保手册管理员保存按钮隐藏
        if (manualAdmin != "true" && currentUserId != 1) {
            mini.get("saveStatusDesc").hide();
        }
    });

    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    //..
    function removeBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/deleteDispatchFPW.do",
                    method: 'POST',
                    data: {ids: rowIds.join(',')},
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
    function openImportWindow() {
        importWindow.show();
    }

    //..
    function importBusiness() {
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        if (!file) {
            mini.alert('请选择文件！');
            return;
        }
        //XMLHttpRequest方式上传表单
        var xhr = false;
        try {
            //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
            xhr = new XMLHttpRequest();
        } catch (e) {
            //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
            xhr = ActiveXobject("Msxml12.XMLHTTP");
        }

        if (xhr.upload) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        if (xhr.responseText) {
                            var returnObj = JSON.parse(xhr.responseText);
                            var message = '';
                            if (returnObj.message) {
                                message = returnObj.message;
                            }
                            mini.alert(message);
                        }
                    }
                }
            };
            //开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/exportPartsAtlas/importDispatchFPW.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }

    //..
    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        searchFrm();
    }

    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/importDispatchFPWTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }

    //..
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传excel文件！');
            }
        }
    }

    //..
    function uploadFile() {
        $("#inputFile").click();
    }

    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }

    //..
    function taskStatusRenderList(e) {
        var record = e.record;
        var taskStatus = record.taskStatus;
        var id = record.id;
        var s = getStatusDesc(taskStatus);
        if (!s) {
            return;
        }
        return s;
    }

    function manualStatusRenderList(e) {
        // 根据五个筛选条件后台查数据
        // todo mh
        var record = e.record;
        var taskStatus = record.taskStatus;
        var id = record.id;
        var exportingCountry = record.exportingCountry;
        var materialCode = record.materialCode;
        var designModel = record.designModel;
        var salesModel = record.salesModel;
        var demandNo = record.demandNo;
        var s = ""

        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/exportPartsAtlas/checkManaulStatus.do?materialCode=' + materialCode
                + '&exportingCountry=' + exportingCountry
                + '&designModel=' + designModel
                + '&salesModel=' + salesModel
                + '&demandNo=' + demandNo,
            type: 'post',
            async: false,
            // data: mini.encode(param),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    s = data;
                }
            }
        });

        if (!s) {
            return;
        }
        return s;
    }

    //..
    function getStatusDesc(taskStatus) {
        var s = '';
        if (taskStatus) {
            switch (taskStatus) {
                case "00WLQ":
                    s = '未领取';
                    break;
                case "01YLQ":
                    s = '已领取';
                    break;
                case "02JXZZSQ":
                    s = '机型制作申请中';
                    break;
                case "03JXZZ":
                    s = '机型制作中';
                    break;
                case "04SLZZ":
                    s = '实例制作中';
                    break;
                case "05GZ":
                    s = '改制中';
                    break;
                case "06ZZWCYZC":
                    s = '制作完成已转出';
                    break;
                case "07YJS":
                    s = '档案室已接收';
                    break;
                case "08ZF":
                    s = '作废';
                    break;
            }
        } else {
            s = '作废';
        }
        return s;
    }

    function exportList() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
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

    //@mh 操保手册状态描述列非管理员不可编辑
    function onCellBeginEdit(e) {
        var field = e.field;
        if (field == "statusDesc") {
            if (manualAdmin != "true" && currentUserId != 1) {
                e.cancel = true;
            }
        }
    }

    // 保存操保手册状态描述
    function saveStatusDesc() {
        if (businessListGrid.getChanges().length > 0) {
            var changeGrid = businessListGrid.getChanges();
        } else {
            mini.alert("数据无变动！");
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/exportPartsAtlas/saveStatusDesc.do',
            type: 'post',
            async: false,
            data: mini.encode(changeGrid),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "保存成功！";
                    } else {
                        message = "保存失败！" + data.message;
                    }

                    mini.alert(message, "提示信息", function () {
                        businessListGrid.reload();
                    });
                }
            }
        });

    }

</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>