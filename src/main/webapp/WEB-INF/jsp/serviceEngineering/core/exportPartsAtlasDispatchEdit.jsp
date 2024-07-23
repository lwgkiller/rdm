<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>出口产品零件图册发运通知单表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" style="display: none;height: 40px;width: 100%;background: #fff";vertical-align:middle;>
    <div style="display:table-cell;vertical-align:middle;float: right; margin: 5px 15px 0px 0px;">
        <a id="saveDispatchBtn" class="mini-button" onclick="saveDispatch()">保存草稿</a>
        <a id="commitDispatchBtn" class="mini-button" onclick="commitDispatch()">提交</a>
        <span style="color: red;font-size: 15px">（提交后不允许修改）</span>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    出口产品发运通知单
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">发运通知单编号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="dispatchNo" name="dispatchNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">编制人：</td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" readonly style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">编制日期：</td>
                    <td style="min-width:170px">
                        <input name="CREATE_TIME_" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">出口国家：</td>
                    <td style="min-width:170px">
                        <input name="exportCountryName" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">区域：</td>
                    <td style="min-width:170px">
                        <input name="region" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">业务员：</td>
                    <td style="min-width:170px">
                        <input name="salesMan" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%;height: 150px">发运要求：</td>
                    <td colspan="3">
                        <input name="dispatchDesc" class="mini-textarea" readonly style="width:100%;height: 150px"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 500px">发运明细：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="detailBtn">
                            <a class="mini-button" style="margin-bottom: 5px;" onclick="dispatchDetailProcess('','add')" >添加</a>
                            <span style="color: red;font-size: 15px">（添加之前请先“保存草稿”）</span>
                            <a class="mini-button btn-red" style="margin-bottom: 5px;" onclick="delDispatchDetail()" >删除</a>
                        </div>
                        <div id="dispatchDetailListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="true" allowCellWrap="false" multiselect="true"
                             showCellTip="true" idField="id" url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/getDispatchDetailList.do?dispatchId=${dispatchId}"
                             showPager="false" showColumnsMenu="false" allowAlternating="true" autoload="true">
                            <div property="columns">
                                <div name="checkcolumn" type="checkcolumn" width="35"></div>
                                <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="detailActionRenderer" cellStyle="padding:0;">操作</div>
                                <div field="detailNo" headerAlign="center" align="center" width="60">编号</div>
                                <div field="demandNo" width="110" headerAlign="center" align="center">需求通知单号
                                </div>
                                <div field="matCode" width="100" headerAlign="center" align="center">物料编码
                                </div>
                                <div field="designType" width="100" headerAlign="center" align="center">设计型号
                                </div>
                                <div field="saleType" width="70" headerAlign="center" align="center">销售型号
                                </div>
                                <div field="languages" width="80" headerAlign="center" align="center">随机文件语言
                                </div>
                                <div field="departurestartTime" width="80" headerAlign="center" align="center">发车开始时间
                                </div>
                                <div field="departureendTime" width="80" headerAlign="center" align="center">发车结束时间
                                </div>
                                <div field="vehicleNumber" width="80" headerAlign="center" align="center">整车编号
                                </div>
                                <div field="cardisPose" width="80" headerAlign="center" align="center">车辆配置
                                </div>
                                <div field="destination" width="80" headerAlign="center" align="center">目的地
                                </div>
                                <div field="actualdepartureCountry" width="80" headerAlign="center" align="center">实际发车国家
                                </div>
                                <div field="needNumber" width="80" headerAlign="center" align="center" renderer="detailMachineNumRender">数量（已分配）
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>

            </table>
        </form>
    </div>
</div>
<div id="machineWindow" title="整机信息查看" class="mini-window" style="width:750px;height:400px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div id="machineListGrid" class="mini-datagrid" style="width: 100%; height: 98%" allowResize="false" allowCellWrap="true"
             showCellTip="true" idField="id" showPager="false" showColumnsMenu="false" allowcelledit="false"
             allowAlternating="true" >
            <div property="columns">
                <div type="indexcolumn" headerAlign="center" align="center" width="40">序号</div>
                <div field="machineCode" width="150" headerAlign="center" align="center" >整机PIN码
                </div>
                <div field="changeApply" width="180" headerAlign="center" align="center">改制订单信息
                </div>
                <div field="relDispatchUserName" width="120" headerAlign="center" align="center">操作人
                </div>
                <div field="relDispatchTime" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">操作时间
                </div>
            </div>
        </div>
    </div>
</div>
<div id="configListWindow" title="配置清单查看" class="mini-window" style="width:700px;height:350px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div id="configListGrid" class="mini-datagrid" style="width: 100%; height: 98%" allowResize="false" allowCellWrap="false"
             showCellTip="true" idField="id" showPager="false" showColumnsMenu="false" allowcelledit="false"
             allowAlternating="true" >
            <div property="columns">
                <div field="configNo" headerAlign="center" align="center" width="20">序号</div>
                <div field="configDesc" width="120" headerAlign="center" align="left">配置描述
                </div>
            </div>
        </div>
    </div>
</div>
<div id="dispatchDetailWindow" title="发运明细编辑" class="mini-window" style="width:960px;height:870px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" >
        <div class="form-container" style="margin: 0 auto;height: 99%;min-height: 90%;padding: 0px 10px">
            <div class="mini-toolbar" style="float: right;margin-right: 10px">
                <a class="mini-button" onclick="saveDispatchDetail()">保存</a>
                <a class="mini-button btn-red" onclick="closeDispatchDetailWindow()">关闭</a>
            </div>
            <form id="dispatchDetailForm" method="post" style="height: 99%">
                <input id="action" class="mini-hidden"/>
                <input name="id" class="mini-hidden"/>
                <table class="table-detail" cellspacing="1" cellpadding="0">
                    <tr>
                        <td style="width: 14%">编号：<span style="color:red">*</span></td>
                        <td style="width: 36%;min-width:140px">
                            <input name="detailNo"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">需求通知单号：<span style="color:red">*</span></td>
                        <td style="width: 36%;min-width:170px">
                            <input name="demandNo" textname="demandNo"  id="demandNo" style="width:98%;" class="mini-buttonedit" showClose="true" oncloseclick="clearSelectDemandNo()" allowInput="false" onbuttonclick="selectDemandNo()"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 14%">物料编码：</td>
                        <td style="width: 36%;min-width:140px">
                            <input name="matCode" id="matCode" class="mini-textbox" readonly style="width:98%;" />
                        </td>
                        <td style="width: 14%">设计型号：</td>
                        <td style="width: 36%;">
                            <input name="designType" id="designType" class="mini-textbox" readonly style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 14%">销售型号：</td>
                        <td style="width: 36%;">
                            <input name="saleType" id="saleType" class="mini-textbox" readonly style="width:98%;" />
                        </td>
                        <td style="width: 14%">随机文件语言：</td>
                        <td style="width: 36%;">
                            <input name="languages" id="languages" class="mini-textbox" readonly style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 14%">数量：<span style="color:red">*</span></td>
                        <td style="width: 36%;">
                            <input name="needNumber"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">整车编号：<span style="color:red">*</span></td>
                        <td style="width: 36%;">
                            <input name="vehicleNumber"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 14%">发车开始时间：<span style="color:red">*</span></td>
                        <td style="width: 36%;">
                            <input name="departurestartTime"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">发车结束时间：<span style="color:red">*</span></td>
                        <td style="width: 36%;">
                            <input name="departureendTime"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 14%">目的地：<span style="color:red">*</span></td>
                        <td style="width: 36%;">
                            <input name="destination"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">实际发车国家：<span style="color:red">*</span></td>
                        <td style="width: 36%;">
                            <input name="actualdepartureCountry"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 14%">车辆配置：<span style="color:red">*</span></td>
                        <td style="width: 36%;">
                            <input name="cardisPose"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr >
                        <td style="width: 14%;height: 200px">配置清单：</td>
                        <td colspan="3">
                            <div class="mini-toolbar" id="detailConfigBtn" >
                                <a class="mini-button" style="margin-bottom: 5px;" onclick="detailConfigAdd()" >添加</a>
                                <a class="mini-button btn-red" style="margin-bottom: 5px;" onclick="detailConfigDel()" >删除</a>
                            </div>
                            <div id="detailConfigListGrid" class="mini-datagrid" style="width: 100%; height: 84%" allowResize="false" allowCellWrap="true"
                                 showCellTip="true" idField="id" showPager="false" showColumnsMenu="false" allowCellEdit="true" allowCellSelect="true"
                                 allowAlternating="true" multiSelect="true" >
                                <div property="columns">
                                    <div type="checkcolumn" width="10"></div>
                                    <div field="configNo" headerAlign="center" align="center" width="20">序号
                                        <input property="editor" class="mini-textarea" style="overflow: auto" />
                                    </div>
                                    <div field="configDesc" width="140" headerAlign="center" align="left">配置描述
                                        <input property="editor" class="mini-textarea" style="overflow: auto" />
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                    <tr >
                        <td style="width: 14%;height: 350px">整机信息：<span style="color:red">*</span></td>
                        <td colspan="3">
                            <div class="mini-toolbar" id="detailMachineBtn">
                                <a class="mini-button" style="margin-bottom: 5px;" onclick="detailMachineAdd()" >添加</a>
                                <a class="mini-button btn-red" style="margin-bottom: 5px;" onclick="detailMachineDel()" >删除</a>
                            </div>
                            <div id="machineRelListGrid" class="mini-datagrid" style="width: 100%; height: 84%" allowResize="false" allowCellWrap="true"
                                 showCellTip="true" idField="id" showPager="false" showColumnsMenu="false"
                                 allowCellEdit="true" allowCellSelect="true" multiSelect="true" allowAlternating="true" >
                                <div property="columns">
                                    <div type="checkcolumn" width="30"></div>
                                    <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
                                    <div field="machineCode" width="120" headerAlign="center" align="center">整机PIN码
                                    </div>
                                    <div field="changeApply" width="180" headerAlign="center" align="center">改制订单信息
                                        <input property="editor" class="mini-textarea" style="overflow: auto" />
                                    </div>
                                    <div field="relDispatchUserName" width="90" headerAlign="center" align="center">操作人
                                    </div>
                                    <div field="relDispatchTime" width="90" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">操作时间
                                    </div>
                                    <div field="taskStatus" visible="false">整机任务状态</div>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>

<div id="selectDemandNoWindow" title="选择需求通知单" class="mini-window" style="width:1140px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">需求通知单号: </span>
        <input class="mini-textbox" width="130" id="demandNoFilter" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">物料编码: </span>
        <input class="mini-textbox" width="130" id="matCodeFilter" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">设计型号: </span>
        <input class="mini-textbox" width="130" id="designTypeFilter" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">销售型号: </span>
        <input class="mini-textbox" width="130" id="saleTypeFilter" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchDemandDetail()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="demandDetailListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="id"  showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/getAllDemandDetailList.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="demandNo"  width="120" headerAlign="center" align="center">需求通知单号</div>
                <div field="exportCountryName"  width="90" headerAlign="center" align="center">出口国家</div>
                <div field="detailNo" headerAlign="center" align="center" width="60">清单号</div>
                <div field="matCode" width="90" headerAlign="center" align="center">物料编码
                </div>
                <div field="designType" width="120" headerAlign="center" align="center">设计型号
                </div>
                <div field="saleType" width="120" headerAlign="center" align="center">销售型号
                </div>
                <div field="needNumber" width="80" headerAlign="center" align="center" >数量
                </div>
                <div field="languages" width="80" headerAlign="center" align="center">随机文件语言
                </div>
                <div field="needTime" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">交货日期
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectDemandNoOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectDemandNoHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="selectMachineWindow" title="关联整机" class="mini-window" style="width:1140px;height:690px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">需求通知单号: </span>
        <input class="mini-textbox" width="130" id="demandNoMachineFilter" readonly style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">物料编码: </span>
        <input class="mini-textbox" width="130" id="matCodeMachineFilter" readonly style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">设计型号: </span>
        <input class="mini-textbox" width="130" id="designTypeMachineFilter" readonly style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">销售型号: </span>
        <input class="mini-textbox" width="130" id="saleTypeMachineFilter" readonly style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">随机文件语言: </span>
        <input class="mini-textbox" width="130" id="languagesMachineFilter" readonly style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">整机PIN码: </span>
        <input class="mini-textbox" width="130" id="machineCodeMachineFilter" style="margin-right: 15px"/>

        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchMachine()">查询</a>
        <span style="color: red;font-size: 15px">（已关联发运单整机会过滤掉）</span>

    </div>
    <div class="mini-fit">
        <div id="machineSelectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onMachineRowDblClick"
             idField="id"  showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiselect="true"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/taskListQuery.do?scene=all"
        >
            <div property="columns">
                <div type="checkcolumn" width="40"></div>
                <div field="machineCode" width="170" headerAlign="center" align="center">整机PIN码</div>
                <div field="demandNo" width="125" headerAlign="center" align="center">需求通知单号</div>
                <div field="matCode" width="95" headerAlign="center" align="center">整机物料编码</div>
                <div field="designType" width="95" headerAlign="center" align="center">设计型号</div>
                <div field="saleType" width="95" headerAlign="center" align="center">销售型号</div>
                <div field="languages" width="75" headerAlign="center" align="center">随机文件语言</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectMachineOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectMachineHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var dispatchDetailListGrid = mini.get("dispatchDetailListGrid");
    var machineListGrid = mini.get("machineListGrid");
    var formBusiness = new mini.Form("#formBusiness");
    var dispatchId = "${dispatchId}";
    var machineWindow = mini.get("machineWindow");
    var configListWindow = mini.get("configListWindow");
    var configListGrid = mini.get("configListGrid");
    var action="${action}";
    var dispatchDetailWindow = mini.get("dispatchDetailWindow");
    var dispatchDetailForm = new mini.Form("#dispatchDetailForm");
    var detailConfigListGrid = mini.get("detailConfigListGrid");
    var machineRelListGrid = mini.get("machineRelListGrid");
    var selectDemandNoWindow = mini.get("selectDemandNoWindow");
    var demandDetailListGrid = mini.get("demandDetailListGrid");

    var machineSelectListGrid = mini.get("machineSelectListGrid");
    var selectMachineWindow = mini.get("selectMachineWindow");

    dispatchDetailListGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    $(function () {
        if(action=='detail') {
            formBusiness.setEnabled(false);
            $("#detailBtn").hide();
            dispatchDetailListGrid.hideColumn('action');
            dispatchDetailListGrid.hideColumn('checkcolumn');
        } else if(action=='add' || action=='edit') {
            $("#detailToolBar").show();
        } else if (action=='relMachine') {
            formBusiness.setEnabled(false);
            $("#detailBtn").hide();
            dispatchDetailListGrid.hideColumn('checkcolumn');
        }
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/getDispatchInfo.do";
        $.post(
            url,
            {dispatchId: dispatchId},
            function (json) {
                formBusiness.setData(json);
            }
        );
    });

    function detailMachineNumRender(e) {
        var record = e.record;
        var needNumber = record.needNumber;
        var machineNum = record.machineNum;
        var dispatchDetailId = record.id;
        var dispatchId = record.dispatchId;

        var cellStr=needNumber+"("+machineNum+")";
        if(needNumber<=machineNum) {
            return '<span style="color: #409EFF;text-decoration:underline;cursor: pointer;" onclick="machineCheck(\''+dispatchId+'\',\''+dispatchDetailId+'\')">'+cellStr+'</span>';
        } else {
            return '<span style="color: red;text-decoration:underline;cursor: pointer;" onclick="machineCheck(\''+dispatchId+'\',\''+dispatchDetailId+'\')">'+cellStr+'</span>';
        }
    }

    function machineCheck(dispatchId,dispatchDetailId) {
        machineWindow.show();
        var url=jsUseCtxPath+"/serviceEngineering/core/exportPartsAtlas/getMachineList.do";
        url+="?dispatchId="+dispatchId+"&dispatchDetailId="+dispatchDetailId;
        machineListGrid.setUrl(url);
        machineListGrid.load();
    }
    function configListRender(e) {
        var record = e.record;
        var dispatchDetailId = record.id;
        var dispatchId = record.dispatchId;
        return '<span style="color: #409EFF;text-decoration:underline;cursor: pointer;" onclick="configListCheck(\''+dispatchId+'\',\''+dispatchDetailId+'\')">查看</span>';
    }

    function configListCheck(dispatchId,dispatchDetailId) {
        configListWindow.show();
        var url=jsUseCtxPath+"/serviceEngineering/core/exportPartsAtlas/getDetailConfigList.do";
        url+="?dispatchId="+dispatchId+"&dispatchDetailId="+dispatchDetailId;
        configListGrid.setUrl(url);
        configListGrid.load();
    }
    
    function saveDispatch() {
        var dispatchNo = mini.get("dispatchNo").getValue();
        if(!dispatchNo) {
            mini.alert("请填写发运通知单编号！");
            return;
        }
        var formData = formBusiness.getData();
        var json = mini.encode(formData);
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/exportPartsAtlas/saveOrCommitDispatch.do?scene=save',
            type: 'post',
            async: false,
            data: json,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData) {
                    if (returnData.success) {
                        mini.alert("保存成功","提示",function(){
                        window.location.href = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/dispatchEditPage.do?dispatchId=" + returnData.data+"&action=edit";
                        });
                    } else {
                        mini.alert("保存草稿失败" + returnData.message);
                    }
                }
            }
        });
    }

    function commitDispatch() {
        var dispatchNo = mini.get("dispatchNo").getValue();
        if(!dispatchNo) {
            mini.alert("请填写发运通知单编号！");
            return;
        }
        var detailData=dispatchDetailListGrid.getData();
        if(!detailData || detailData.length==0) {
            mini.alert("请添加发运明细！");
            return;
        }

        var formData = formBusiness.getData();
        var json = mini.encode(formData);
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/exportPartsAtlas/saveOrCommitDispatch.do?scene=commit',
            type: 'post',
            async: false,
            data: json,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    if (data.success) {
                        CloseWindow();
                    } else {
                        mini.alert("提交失败" + data.message);
                    }
                }
            }
        });
    }
    
    function detailActionRenderer(e) {
        var record = e.record;
        var dispatchDetailId = record.id;
        var s = '';
        if(action=='edit') {
            s += '<span  title="编辑" onclick="dispatchDetailProcess(\'' + dispatchDetailId +'\',\''+action+ '\')">编辑</span>';
        } else if(action=='relMachine') {
            s += '<span  title="整机录入" onclick="dispatchDetailProcess(\'' + dispatchDetailId +'\',\''+action+ '\')">整机录入</span>';
        }
        return s;
    }

    function dispatchDetailProcess(dispatchDetailId,action) {
        if(!dispatchId) {
            mini.alert("请先“保存草稿”！");
            return;
        }
        //通过dispatchDetailId查询信息赋值或者新增场景清空
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/getDispatchDetail.do?dispatchDetailId="+dispatchDetailId;
        $.get(
            url,
            function (json) {
                dispatchDetailForm.setData(json);
                mini.get("action").setValue(action);
                //查询配置清单
                var url=jsUseCtxPath+"/serviceEngineering/core/exportPartsAtlas/getDetailConfigList.do";
                url+="?dispatchId="+dispatchId+"&dispatchDetailId="+dispatchDetailId;
                detailConfigListGrid.setUrl(url);
                detailConfigListGrid.load();
                //查询整机清单
                var url=jsUseCtxPath+"/serviceEngineering/core/exportPartsAtlas/getMachineList.do";
                url+="?dispatchId="+dispatchId+"&dispatchDetailId="+dispatchDetailId;
                machineRelListGrid.setUrl(url);
                machineRelListGrid.load();

                if(action == 'edit' || action == 'add') {
                    $("#detailMachineBtn").hide();
                } else {
                    dispatchDetailForm.setEnabled(false);
                    detailConfigListGrid.setAllowCellEdit(false);
                    $("#detailConfigBtn").hide();
                }
                dispatchDetailWindow.show();
            }
        );
    }

    function delDispatchDetail() {
        var selecteds = dispatchDetailListGrid.getSelecteds();
        if (!selecteds || selecteds.length <= 0) {
            mini.alert('请至少选择一条数据');
            return;
        }
        mini.confirm("确定删除？", "提示",
            function (action) {
                if (action == "ok") {
                    var ids = [];
                    for(var index=0;index<selecteds.length;index++) {
                        ids.push(selecteds[index].id)
                    }
                    var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/delDispatchDetail.do";
                    _SubmitJson({
                        url: url,
                        method: 'POST',
                        data: {ids: ids.join(',')},
                        success: function (text) {
                            dispatchDetailListGrid.reload();
                        }
                    });

                }
            }
        );
    }
    
    function saveDispatchDetail() {
        var action = mini.get("action").getValue();
        var dispatchDetailFormData =dispatchDetailForm.getData();
        if(action == 'edit' || action == 'add') {
            if(!dispatchDetailFormData.detailNo) {
                mini.alert("请填写编号！");
                return;
            }
            if(!dispatchDetailFormData.demandNo) {
                mini.alert("请选择需求通知单号！");
                return;
            }
            if(!dispatchDetailFormData.needNumber) {
                mini.alert("请填写数量！");
                return;
            }
            if(!dispatchDetailFormData.departurestartTime) {
                mini.alert("请填写发车开始时间！");
                return;
            }
            if(!dispatchDetailFormData.vehicleNumber) {
                mini.alert("请填写整车编号！");
                return;
            }
            if(!dispatchDetailFormData.cardisPose) {
                mini.alert("请填写车辆配置！");
                return;
            }
            if(!dispatchDetailFormData.departureendTime) {
                mini.alert("请填写发车结束时间！");
                return;
            }
            if(!dispatchDetailFormData.destination) {
                mini.alert("请填写目的地！");
                return;
            }
            if(!dispatchDetailFormData.actualdepartureCountry) {
                mini.alert("请填写实际发车国家！");
                return;
            }
            var detailConfigChangeData = detailConfigListGrid.getChanges();
            dispatchDetailFormData.detailConfigChangeData=detailConfigChangeData;
        } else if(action == 'relMachine') {
            var detailMachineData = machineRelListGrid.getData();
            if(!detailMachineData || detailMachineData.length==0) {
                mini.alert("请添加整机信息！");
                return;
            }
            var detailMachineChangeData = machineRelListGrid.getChanges();
            dispatchDetailFormData.detailMachineChangeData=detailMachineChangeData;
        }
        dispatchDetailFormData.dispatchId=dispatchId;
        var json =mini.encode(dispatchDetailFormData);
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/exportPartsAtlas/saveDispatchDetail.do?scene='+action,
            type: 'post',
            async: false,
            data:json,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message="";
                    if(data.success) {
                        mini.alert('保存成功','提示',function(){
                            closeDispatchDetailWindow();
                        });
                    } else {
                        mini.alert("保存失败："+data.message);
                    }
                }
            }
        });
    }
    
    function closeDispatchDetailWindow() {
        dispatchDetailWindow.hide();
        dispatchDetailListGrid.reload();
    }

    function detailConfigAdd() {
        detailConfigListGrid.addRow({})
    }
    
    function detailConfigDel() {
        var selecteds = detailConfigListGrid.getSelecteds();
        if (!selecteds || selecteds.length <= 0) {
            mini.alert('请至少选择一条数据');
            return;
        }
        detailConfigListGrid.removeRows(selecteds);
    }


    function selectDemandNo() {
        selectDemandNoWindow.show();
        searchDemandDetail();
    }

    function searchDemandDetail() {
        var queryParam = [];
        //其他筛选条件
        var demandNoFilter = $.trim(mini.get("demandNoFilter").getValue());
        if (demandNoFilter) {
            queryParam.push({name: "demandNo", value: demandNoFilter});
        }
        var matCodeFilter = $.trim(mini.get("matCodeFilter").getValue());
        if (matCodeFilter) {
            queryParam.push({name: "matCode", value: matCodeFilter});
        }
        var designTypeFilter = $.trim(mini.get("designTypeFilter").getValue());
        if (designTypeFilter) {
            queryParam.push({name: "designType", value: designTypeFilter});
        }
        var saleTypeFilter = $.trim(mini.get("saleTypeFilter").getValue());
        if (saleTypeFilter) {
            queryParam.push({name: "saleType", value: saleTypeFilter});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = demandDetailListGrid.getPageIndex();
        data.pageSize = demandDetailListGrid.getPageSize();
        data.sortField = demandDetailListGrid.getSortField();
        data.sortOrder = demandDetailListGrid.getSortOrder();
        //查询
        demandDetailListGrid.load(data);
    }

    function onRowDblClick() {
        selectDemandNoOK();
    }

    function clearSelectDemandNo() {
        mini.get("demandNo").setValue("");
        mini.get("demandNo").setText("");
        mini.get("exportCountryName").setValue("");
        mini.get("matCode").setValue("");
        mini.get("designType").setValue("");
        mini.get("saleType").setValue("");
        mini.get("languages").setValue("");
    }

    function selectDemandNoOK() {
        var row=demandDetailListGrid.getSelected();
        if(!row || row.length==0) {
            mini.alert("请选择一条数据");
            return;
        }
        mini.get("demandNo").setValue(row.demandNo);
        mini.get("demandNo").setText(row.demandNo);
        mini.get("exportCountryName").setValue(row.exportCountryName);
        mini.get("matCode").setValue(row.matCode);
        mini.get("designType").setValue(row.designType);
        mini.get("saleType").setValue(row.saleType);
        mini.get("languages").setValue(row.languages);

        selectDemandNoHide();
    }

    function selectDemandNoHide() {
        mini.get("demandNoFilter").setValue("");
        mini.get("matCodeFilter").setValue("");
        mini.get("designTypeFilter").setValue("");
        mini.get("saleTypeFilter").setValue("");
        selectDemandNoWindow.hide();
    }







    function detailMachineAdd() {
        selectMachineWindow.show();
        //限定筛选条件
        mini.get("demandNoMachineFilter").setValue(mini.get("demandNo").getValue());
        mini.get("matCodeMachineFilter").setValue(mini.get("matCode").getValue());
        mini.get("designTypeMachineFilter").setValue(mini.get("designType").getValue());
        mini.get("saleTypeMachineFilter").setValue(mini.get("saleType").getValue());
        mini.get("languagesMachineFilter").setValue(mini.get("languages").getValue());
        searchMachine();
        machineSelectListGrid.deselectAll();
    }

    function detailMachineDel() {
        var selecteds = machineRelListGrid.getSelecteds();
        if (!selecteds || selecteds.length <= 0) {
            mini.alert('请至少选择一条数据');
            return;
        }
        machineRelListGrid.removeRows(selecteds);
    }

    function searchMachine() {
        var queryParam = [];
        //其他筛选条件
        var demandNoFilter = $.trim(mini.get("demandNoMachineFilter").getValue());
        if (demandNoFilter) {
            queryParam.push({name: "verifyDemandNo", value: demandNoFilter});
        }
        var matCodeFilter = $.trim(mini.get("matCodeMachineFilter").getValue());
        if (matCodeFilter) {
            queryParam.push({name: "verifyMatCode", value: matCodeFilter});
        }
        var designTypeFilter = $.trim(mini.get("designTypeMachineFilter").getValue());
        if (designTypeFilter) {
            queryParam.push({name: "verifyDesignType", value: designTypeFilter});
        }
        var saleTypeFilter = $.trim(mini.get("saleTypeMachineFilter").getValue());
        if (saleTypeFilter) {
            queryParam.push({name: "verifySaleType", value: saleTypeFilter});
        }
        var languagesFilter = $.trim(mini.get("languagesMachineFilter").getValue());
        if (languagesFilter) {
            queryParam.push({name: "languages", value: languagesFilter});
        }
        var machineCodeFilter = $.trim(mini.get("machineCodeMachineFilter").getValue());
        if (machineCodeFilter) {
            queryParam.push({name: "machineCode", value: machineCodeFilter});
        }
        //过滤已经关联过的数据
        queryParam.push({name: "filterHasRel", value: "yes"});

        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = machineSelectListGrid.getPageIndex();
        data.pageSize = machineSelectListGrid.getPageSize();
        data.sortField = machineSelectListGrid.getSortField();
        data.sortOrder = machineSelectListGrid.getSortOrder();
        //查询
        machineSelectListGrid.load(data);
    }

    function onMachineRowDblClick() {
        selectMachineOK();
    }

    function selectMachineOK() {
        var rows=machineSelectListGrid.getSelecteds();
        if(!rows || rows.length==0) {
            mini.alert("请至少选择一条数据");
            return;
        }
        //检查是否表中已有
        var existData=machineRelListGrid.getData();
        var existMachineCode=[];
        if(existData && existData.length>0) {
            for(var index=0;index<existData.length;index++) {
                existMachineCode.push(existData[index].machineCode);
            }
        }
        for(var index=0;index<rows.length;index++) {
            if(existMachineCode.indexOf(rows[index].machineCode)==-1) {
                machineRelListGrid.addRow({id:rows[index].id,machineCode:rows[index].machineCode,taskStatus:rows[index].taskStatus});
            }
        }
        selectMachineHide();
    }

    function selectMachineHide() {
        selectMachineWindow.hide();
    }
</script>
</body>
</html>
