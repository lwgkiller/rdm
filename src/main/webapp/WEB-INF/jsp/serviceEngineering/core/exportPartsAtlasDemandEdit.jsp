<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>出口产品零件图册需求通知单表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>

<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    出口产品需求通知单
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">需求通知单编号：</td>
                    <td style="min-width:170px">
                        <input id="demandNo" name="demandNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">出口国家：</td>
                    <td style="min-width:170px">
                        <input id="exportCountryName" name="exportCountryName" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">编制人：</td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="text-align: center;width: 15%">编制日期：</td>
                    <td style="min-width:170px">
                        <input name="CREATE_TIME_" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 500px">需求明细：</td>
                    <td colspan="3">
                        <div id="demandDetailListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" idField="id" url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/getDemandDetailList.do?demandId=${demandId}"
                             showPager="false" showColumnsMenu="false" allowAlternating="true" autoload="true">
                            <div property="columns">
                                <div field="detailNo" headerAlign="center" align="center" width="60">清单号</div>
                                <div field="matCode" width="90" headerAlign="center" align="center">物料编码
                                </div>
                                <div field="designType" width="70" headerAlign="center" align="center">设计型号
                                </div>
                                <div field="saleType" width="70" headerAlign="center" align="center">销售型号
                                </div>
                                <div field="needNumber" width="80" headerAlign="center" align="center" renderer="detailMachineNumRender">数量（已分配）
                                </div>
                                <div field="languages" width="80" headerAlign="center" align="center">随机文件语言
                                </div>
                                <div field="needTime" width="70" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">交货日期
                                </div>
                                <div width="70" headerAlign="center" align="center" renderer="configListRender">配置清单
                                </div>
                                <div field="field_l1i12__c" width="70" headerAlign="center" align="center" >是否需要EC声明
                                </div>
                                <div field="field_8M8qH__c" width="70" headerAlign="center" align="center" >EC声明语言种类
                                </div>
                                <div field="field_7p01H__c" width="70" headerAlign="center" align="center" >EC声明配置识别码
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>

            </table>
        </form>
    </div>
</div>
<div id="machineWindow" title="整机信息" class="mini-window" style="width:750px;height:400px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div id="machineListGrid" class="mini-datagrid" style="width: 100%; height: 98%" allowResize="false" allowCellWrap="false"
             showCellTip="true" idField="id" showPager="false" showColumnsMenu="false" allowcelledit="false"
             allowAlternating="true" >
            <div property="columns">
                <div type="indexcolumn" headerAlign="center" align="center" width="40">序号</div>
                <div field="machineCode" width="180" headerAlign="center" align="center" >整机PIN码
                </div>
                <div field="inTime" width="150" headerAlign="center" align="center">入库时间
                </div>
                <div field="relDemandUserName" width="120" headerAlign="center" align="center">操作人
                </div>
                <div field="relDemandTime" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">操作时间
                </div>
            </div>
        </div>
    </div>
</div>

<div id="configListWindow" title="配置清单" class="mini-window" style="width:700px;height:350px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div id="configListGrid" class="mini-datagrid" style="width: 100%; height: 98%" allowResize="false" allowCellWrap="true"
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

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var demandDetailListGrid = mini.get("demandDetailListGrid");
    var machineListGrid = mini.get("machineListGrid");
    var formBusiness = new mini.Form("#formBusiness");
    var demandId = "${demandId}";
    var machineWindow = mini.get("machineWindow");
    var configListWindow = mini.get("configListWindow");
    var configListGrid = mini.get("configListGrid");

    $(function () {
        formBusiness.setEnabled(false);
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/getDemandInfo.do";
        $.post(
            url,
            {demandId: demandId},
            function (json) {
                formBusiness.setData(json);
            }
        );
    });

    function detailMachineNumRender(e) {
        var record = e.record;
        var needNumber = record.needNumber;
        var machineNum = record.machineNum;
        var demandDetailId = record.id;
        var demandId = record.demandId;

        var cellStr=needNumber+"("+machineNum+")";
        if(needNumber<=machineNum) {
            return '<span style="color: #409EFF;text-decoration:underline;cursor: pointer;" onclick="machineCheck(\''+demandId+'\',\''+demandDetailId+'\')">'+cellStr+'</span>';
        } else {
            return '<span style="color: red;text-decoration:underline;cursor: pointer;" onclick="machineCheck(\''+demandId+'\',\''+demandDetailId+'\')">'+cellStr+'</span>';
        }
    }

    function machineCheck(demandId,demandDetailId) {
        machineWindow.show();
        var url=jsUseCtxPath+"/serviceEngineering/core/exportPartsAtlas/getMachineList.do";
        url+="?demandId="+demandId+"&demandDetailId="+demandDetailId;
        machineListGrid.setUrl(url);
        machineListGrid.load();
    }

    function configListRender(e) {
        var record = e.record;
        var demandDetailId = record.id;
        var demandId = record.demandId;
        return '<span style="color: #409EFF;text-decoration:underline;cursor: pointer;" onclick="configListCheck(\''+demandId+'\',\''+demandDetailId+'\')">查看</span>';
    }

    function configListCheck(demandId,demandDetailId) {
        configListWindow.show();
        var url=jsUseCtxPath+"/serviceEngineering/core/exportPartsAtlas/getDetailConfigList.do";
        url+="?demandId="+demandId+"&demandDetailId="+demandDetailId;
        configListGrid.setUrl(url);
        configListGrid.load();
    }
</script>
</body>
</html>
