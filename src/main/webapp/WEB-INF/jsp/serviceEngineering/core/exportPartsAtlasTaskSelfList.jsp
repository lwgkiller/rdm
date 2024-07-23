<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>出口产品零件图册制作个人任务管理</title>
    <%@include file="/commons/list.jsp" %>
    <link href="${ctxPath}/styles/formFile.css?version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/serviceEngineering/exportPartsAtlasTaskSelfList.js?version=${static_res_version}"
            type="text/javascript"></script>

</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">需求通知单编号：</span>
                    <input class="mini-textbox" id="demandNo" name="demandNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="matCode" name="matCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designType" name="designType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号：</span>
                    <input class="mini-textbox" id="saleType" name="saleType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">随机文件语言：</span>
                    <input class="mini-textbox" id="languages" name="languages"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机PIN码：</span>
                    <input class="mini-textbox" id="machineCode" name="machineCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">图册制作人：</span>
                    <input class="mini-textbox" id="taskUserName" name="taskUserName"/>
                </li>
                <li style="margin-right: 15px" id="taskStatus_li">
                    <span class="text" style="width:auto">制作任务状态：</span>
                    <input id="taskStatus" name="taskStatus" class="mini-combobox" style="width:140px;" multiSelect="true"
                           textField="key" valueField="value" emptyText=""
                           required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
                    />
                </li>
                <%--@lwgkiller:以下成品库功能相关查询字段--%>
                <li style="margin-right: 15px">
                    <input id="isFWP" name="isFWP" class="mini-checkboxlist" style="width:auto;"
                           repeatItems="10" repeatLayout="flow" repeatDirection="horizontal"
                           textfield="fieldName" valuefield="field" multiSelect="true"
                           data="[{'fieldName' : '关联成品库实绩','field' : 'isFWP'}]"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">需求单号(成品库)：</span>
                    <input class="mini-textbox" id="demandNoFWP" name="demandNoFWP"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">发运单号(成品库)：</span>
                    <input class="mini-textbox" id="dispatchNoFWP" name="dispatchNoFWP"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" onclick="searchFrm()" style="margin-right: 5px">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="myClearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="taskReleaseBtn" class="mini-button btn-red" tooltip="仅支持“已领取”或“实例制作中”状态的释放" onclick="taskRelease()"
                       style="margin-right: 5px;display:none">任务释放</a>
                    <%--<a id="taskBackBtn" class="mini-button btn-red" onclick="taskBack()" style="margin-right: 5px;display:none">上一步</a>--%>
                    <a id="slzzBtn" class="mini-button" tooltip="仅支持相同物料编码“已领取”任务的推进" onclick="nextSlzz()"
                       style="margin-right: 5px;display:none">实例制作</a>
                    <a id="zzwcBtn" class="mini-button" tooltip="仅支持“实例制作中”/“改制中”任务的推进" onclick="nextZzwc()" style="margin-right: 5px;display:none">制作完成转出</a>
                    <a id="gdqrBtn" class="mini-button" onclick="taskGdqr()" style="margin-right: 5px;display:none">归档确认</a>
                    <a id="createAbnormalBtn" class="mini-button" onclick="createAbnormal()" style="margin-right: 5px;display:none">异常反馈</a>
                    <a id="taskExport" class="mini-button" onclick="taskExport()" style="margin-right: 5px">导出</a>
                </li>
            </ul>
        </form>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="taskListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true" showCellTip="true" idField="id" allowHeaderWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         onshowrowdetail="onShowRowDetail" autoHideRowDetail="true"
         url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/taskListQuery.do?scene=self">
        <div property="columns">
            <div type="checkcolumn" width="30"></div>
            <div width="40" align="center" headerAlign="center" renderer="onFWPRenderer">FPW</div>
            <div field="dispatchTimeFPW" width="100" headerAlign="center" align="center">FPW发车时间</div>
            <div field="languagesFPW" width="100" headerAlign="center" align="center">FPW语言</div>
            <div type="expandcolumn"></div>
            <div field="taskUserName" width="90" headerAlign="center" align="center" visible="false">图册制作人</div>
            <div field="taskStatus" width="110" headerAlign="center" align="center" renderer="taskStatusRenderList">任务状态(点击查看历史)</div>
            <%--<div width="70" headerAlign="center" align="center">制作风险</div>--%>
            <div field="machineCode" width="170" headerAlign="center" align="center">整机PIN码</div>
            <div field="demandNo" width="141" headerAlign="center" align="center" renderer="demandApplyDetailRender">需求通知单号</div>
            <div field="matCode" width="95" headerAlign="center" align="center">整机物料编码</div>
            <div field="designType" width="95" headerAlign="center" align="center">设计型号</div>
            <div field="saleType" width="95" headerAlign="center" align="center">销售型号</div>
            <div field="languages" width="75" headerAlign="center" align="center">随机文件语言</div>
            <div field="needTime" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">交货时间(需求清单)</div>
            <div field="inTime" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">入库时间</div>
            <div field="relDemandTime" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">整机关联需求清单时间(RDM接收)</div>
            <div field="dispatchNo" width="125" headerAlign="center" align="center" renderer="dispatchApplyDetailRender">发运通知单号</div>
            <div field="departTime" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">发车时间(发运清单)</div>
            <div field="relDispatchTime" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">整机关联发运清单时间(RDM接收)</div>
            <div field="dispatchTime" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">发车时间</div>
            <div field="expectTime" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">异常反馈预计完成时间(已发布)</div>
            <div field="reasonDesc" width="300" headerAlign="center" align="center">异常反馈原因(已发布)</div>
            <div field="exchangeScene" width="70" headerAlign="center" align="center">换车号场景</div>
            <div field="reDispatchTime" width="70" headerAlign="center" align="center">补发时间</div>
            <div width="100" headerAlign="center" align="center" renderer="abnormalCheckRenderer">所有关联异常反馈</div>
            <div width="100" headerAlign="center" align="center" renderer="relFlowRenderer">所有关联流程</div>
            <div name="action" cellCls="actionIcons" width="70" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">详情查看
            </div>
        </div>
    </div>
</div>
<div id="statusHisWindow" title="任务状态记录" class="mini-window" style="width:750px;height:400px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div id="statusListGrid" class="mini-datagrid" style="width: 100%; height: 98%" allowResize="false" allowCellWrap="false"
             showCellTip="true" idField="id" showPager="false" showColumnsMenu="false" allowcelledit="false"
             allowAlternating="true">
            <div property="columns">
                <div type="indexcolumn" headerAlign="center" align="center" width="40">序号</div>
                <div field="statusDesc" width="180" headerAlign="center" align="center" renderer="taskStatusRenderHis">状态更改为
                </div>
                <div field="creatorName" width="120" headerAlign="center" align="center">触发人
                </div>
                <div field="CREATE_TIME_" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">触发时间
                </div>
            </div>
        </div>
    </div>
</div>

<div id="fileInfoWindow" title="归档信息填写" class="mini-window" style="width:900px;height:300px;"
     showModal="true" showFooter="false" allowResize="false">
    <div class="mini-fit" style="font-size: 14px">
        <span style="font-weight: bold;font-size: 15px;color:red">提醒：请填写图册在网盘中的归档路径和归档说明。</span>
        <div style="margin-top: 10px">
            图册归档路径：<span style="color:red">*</span><input id="atlasFilePath" class="mini-textarea" style="width:80%;height:40px"/>
        </div>
        <div style="margin-top: 10px">
            图册归档说明：<input id="fileDesc" class="mini-textarea" style="width:80%;height:70px"/>
        </div>
    </div>
    <div class="topToolBar" style="text-align:center;">
        <a id="setFileInfoBtn" class="mini-button" onclick="setGdFileInfo()">确定</a>
        <a id="closeFileInfoBtn" class="mini-button btn-red" onclick="closeGdFileInfoWindow()">关闭</a>
    </div>
</div>

<div id="abnormalSubmitWindow" title="异常反馈编制" class="mini-window" style="width:900px;height:300px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px">
        <div class="form-container" style="margin: 0 auto;height: 99%;min-height: 90%;padding: 0px 10px">
            <div class="mini-toolbar" style="float: right;margin-right: 10px">
                <a class="mini-button" onclick="abnormalSubmit()">提交</a>
                <a class="mini-button btn-red" onclick="closeAbnormalSubmitWindow()">关闭</a>
            </div>
            <form id="formAbnormal" method="post">
                <input class="mini-hidden" name="machinetaskId" id="machinetaskId"/>
                <input class="mini-hidden" name="machineCode" id="machineCodeAbnormal"/>
                <table class="table-detail" cellspacing="1" cellpadding="0">
                    <tr>
                        <td style="width: 15%">预计完成时间：<span style="color:red">*</span></td>
                        <td style="width: 20%;">
                            <input id="expectTime" name="expectTime" class="mini-datepicker" style="width:98%;" class="mini-datepicker"
                                   format="yyyy-MM-dd"/>
                        </td>
                        <td style="width: 15%">原因描述：<span style="color:red">*</span></td>
                        <td style="width: 50%;">
                            <input name="reasonDesc" class="mini-textarea" style="width:98%;height: 120px"/>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/exportPartsAtlas/taskExport.do?scene=self&export=self" method="post"
      target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<%@include file="exportPartsAtlasTaskRelPage.jsp" %>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var taskListGrid = mini.get("taskListGrid");
    var statusListGrid = mini.get("statusListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var statusHisWindow = mini.get("statusHisWindow");
    var archiveUser =${archiveUser};
    var fwgcsUser =${fwgcsUser};

    var fileInfoWindow = mini.get("fileInfoWindow");
    var taskDetailWindow = mini.get("taskDetailWindow");
    var taskDetailForm = new mini.Form("taskDetailForm");
    var taskAbnormalWindow = mini.get("taskAbnormalWindow");
    var taskRelFlowWindow = mini.get("taskRelFlowWindow");
    var taskAbnormalListGrid = mini.get("taskAbnormalListGrid");

    var abnormalSubmitWindow = mini.get("abnormalSubmitWindow");
    var formAbnormal = new mini.Form("formAbnormal");

    function myClearForm() {
        mini.get("taskStatus").setValue("");
        mini.get("isFWP").setValue("");
        clearForm();
    }

    //操作按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '';
        s += '<span  title="查看" onclick="taskInfoCheck(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">任务详情</span>';
        return s;
    }

    //任务详情
    function taskInfoCheck(taskObj) {
        taskDetailForm.setEnabled(false);
        var statusDesc = getStatusDesc(taskObj.taskStatus);
        taskObj.statusDesc = statusDesc;
        taskDetailForm.setData(taskObj);
        taskDetailWindow.show();
    }

    //..@lwgkiller:有demandNo则罢，没有用demandNoFWP
    function demandApplyDetailRender(e) {
        var record = e.record;
        var demandNo = record.demandNo;
        if (!demandNo) {
            demandNo = record.demandNoFWP;
        }
        var demandId = record.demandId;
        if (demandId) {
            return '<span style="text-decoration:underline;cursor: pointer;color:#409EFF" onclick="demandDetail(\'' + demandId + '\')">' + demandNo + '</span>';
        } else {
            return demandNo;
        }

    }

    function demandDetail(demandId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/demandEditPage.do?demandId=" + demandId;
        var winObj = window.open(url);
    }

    function checknumber(String) {
        var reg = /^[0-9]+.?[0-9]*$/
        if (reg.test(String)) {
            return true
        }
        return false
    }

    //..@lwgkiller:有dispatchNo则罢，没有用dispatchNoFWP
    function dispatchApplyDetailRender(e) {
        var record = e.record;
        var dispatchNo = record.dispatchNo;
        if (!dispatchNo) {
            dispatchNo = record.dispatchNoFWP;
        }
        var dispatchId = record.dispatchId;
        if (dispatchId) {
            return '<span style="text-decoration:underline;cursor: pointer;color:#409EFF" onclick="dispatchDetail(\'' + dispatchId + '\')">' + dispatchNo + '</span>';
        } else {
            return dispatchNo;
        }
    }

    function dispatchDetail(dispatchId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/dispatchEditPage.do?dispatchId=" + dispatchId;
        var winObj = window.open(url);
    }

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
        }
        return s;
    }

    function taskStatusRenderList(e) {
        var record = e.record;
        var taskStatus = record.taskStatus;
        var id = record.id;
        var s = '';
        if (taskStatus) {
            s = getStatusDesc(taskStatus);
        }
        return '<span style="text-decoration:underline;cursor: pointer;" onclick="statusHis(\'' + id + '\')">' + s + '</span>';
    }

    //历史制作状态
    function taskStatusRenderHis(e) {
        var record = e.record;
        var taskStatus = record.statusDesc;
        var id = record.id;
        var s = '';
        if (taskStatus) {
            s = getStatusDesc(taskStatus);
        }
        return s;
    }

    //查看异常反馈
    function abnormalCheckRenderer(e) {
        var record = e.record;
        var machinetaskId = record.id;
        var s = '';
        s += '<span  title="查看" style="cursor:pointer;color:#409EFF" onclick="abnormalListCheck(\'' + machinetaskId + '\')">异常反馈</span>';
        return s;
    }

    function abnormalListCheck(machinetaskId) {
        taskAbnormalWindow.show();
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/abnormalListQuery.do?doPage=false";
        var queryParam = [];
        queryParam.push({name: "machinetaskId", value: machinetaskId});
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        taskAbnormalListGrid.setUrl(url);
        taskAbnormalListGrid.load(data);
    }

    //查看关联流程
    function relFlowRenderer(e) {
        var record = e.record;
        var taskId = record.id;
        var s = '';
        s += '<span  title="查看" style="cursor:pointer;color:#409EFF" onclick="relFlowCheck(\'' + taskId + '\')">相关流程</span>';
        return s;
    }

    function relFlowCheck(taskId) {
        taskRelFlowWindow.show();
    }
    function taskExport() {
        var queryParam = [];
        var demandNo = $.trim(mini.get("demandNo").getValue());
        if (demandNo) {
            queryParam.push({name: "demandNo", value: demandNo});
        }
        var matCode = $.trim(mini.get("matCode").getValue());
        if (matCode) {
            queryParam.push({name: "matCode", value: matCode});
        }
        var designType = $.trim(mini.get("designType").getValue());
        if (designType) {
            queryParam.push({name: "designType", value: designType});
        }
        var saleType = $.trim(mini.get("saleType").getValue());
        if (saleType) {
            queryParam.push({name: "saleType", value: saleType});
        }
        var languages = $.trim(mini.get("languages").getValue());
        if (languages) {
            queryParam.push({name: "languages", value: languages});
        }
        var machineCode = $.trim(mini.get("machineCode").getValue());
        if (machineCode) {
            queryParam.push({name: "machineCode", value: machineCode});
        }
        var taskUserName = $.trim(mini.get("taskUserName").getValue());
        if (taskUserName) {
            queryParam.push({name: "taskUserName", value: taskUserName});
        }
        var taskStatus = $.trim(mini.get("taskStatus").getValue());
        if (taskStatus) {
            queryParam.push({name: "taskStatus", value: taskStatus});
        }
        $("#filter").val(mini.encode(queryParam));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }

    //..@lwgkiller:以下成品库功能相关
    //..显示逆向凭证窗口
    function onShowRowDetail(e) {
        var grid = e.sender;
        var row = e.record;
        var td = grid.getRowDetailCellEl(row);
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/getDispatchFWPDetail.do?id=" + row.fwpId,
            success: function (o) {
                debugger;
                //var o = mini.decode(o);
                if (!$.isEmptyObject(o)) {
                    td.innerHTML =
                        "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp整机PIN码：" + "<span style='color: red'>" + o.pin + "</span>" +
                        "&nbsp&nbsp发车日期：" + "<span style='color: red'>" + o.dispatchTimeFPW + "</span>" +
                        "&nbsp&nbsp需求通知单号：" + "<span style='color: red'>" + o.demandNo + "</span>" +
                        "&nbsp&nbsp发运通知单号：" + "<span style='color: red'>" + o.dispatchNo + "</span>" +
                        "&nbsp&nbsp清单号：" + "<span style='color: red'>" + o.detailNo + "</span>" +
                        "&nbsp&nbsp整机物料编码：" + "<span style='color: red'>" + o.materialCode + "</span>" +
                        "&nbsp&nbsp设计型号：" + "<span style='color: red'>" + o.designModel + "</span>" +
                        "&nbsp&nbsp销售型号：" + "<span style='color: red'>" + o.salesModel + "</span><br>" +
                        "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp出口国家：" + "<span style='color: red'>" + o.exportingCountry + "</span>" +
                        "&nbsp&nbsp语言：" + "<span style='color: red'>" + o.languages + "</span>";
                }
            }
        });
    }
    //..
    function onFWPRenderer(e) {
        var record = e.record;
        var hasRisk = record.fwpId;
        var color = '#32CD32';
        if (hasRisk && record.taskStatus != '07YJS' && record.taskStatus != '08ZF') {
            color = '#fb0808';//有成品库信息&档案室未接收&没作废
        } else if (hasRisk && record.taskStatus == '08ZF') {
            color = '#d4c13d';//有成品库信息&作废了
        } else if (!hasRisk) {
            color = '#ffffff';//没有成品库信息
        }
        var s = '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + color + '"></span>';
        return s;
    }
</script>
<redxun:gridScript gridId="taskListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>