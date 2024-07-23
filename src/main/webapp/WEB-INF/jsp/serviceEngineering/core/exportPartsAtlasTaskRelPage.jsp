<%@page contentType="text/html" pageEncoding="UTF-8" %>

<div id="taskDetailWindow" title="任务详情" class="mini-window" style="width:960px;height:650px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px;">
        <div class="form-container" style="margin: 0 auto; width: 96%;min-height: 93%;height: 93%">
            <form id="taskDetailForm" method="post">
                <table class="table-detail"  cellspacing="1" cellpadding="0">
                    <tr>
                        <td style="width: 17%">图册制作人：</td>
                        <td style="width: 33%;min-width:170px">
                            <input name="taskUserName"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">当前任务状态：</td>
                        <td style="width: 36%;min-width:170px">
                            <input name="statusDesc"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 14%">整机PIN码：</td>
                        <td style="width: 36%;min-width:170px">
                            <input name="machineCode"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">需求通知单号：</td>
                        <td style="width: 36%;min-width:140px">
                            <input name="demandNo"  class="mini-textbox" style="width:98%;" />
                        </td>

                    </tr>
                    <tr>
                        <td style="width: 14%">整机物料编码：</td>
                        <td style="width: 36%;">
                            <input name="matCode"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">设计型号：</td>
                        <td style="width: 36%;min-width:140px">
                            <input name="designType"  class="mini-textbox" style="width:98%;" />
                        </td>

                    </tr>
                    <tr>
                        <td style="width: 14%">销售型号：</td>
                        <td style="width: 36%;">
                            <input name="saleType"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">随机文件语言：</td>
                        <td style="width: 36%;min-width:140px">
                            <input name="languages"  class="mini-textbox" style="width:98%;" />
                        </td>

                    </tr>
                    <tr>
                        <td style="width: 14%">交货时间(需求清单)：</td>
                        <td style="width: 36%;">
                            <input name="needTime"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">入库时间：</td>
                        <td style="width: 36%;min-width:140px">
                            <input name="inTime"  class="mini-textbox" style="width:98%;" />
                        </td>

                    </tr>
                    <tr>
                        <td style="width: 14%">整机关联需求清单操作人：</td>
                        <td style="width: 36%;">
                            <input name="relDemandUserName"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">整机关联需求清单时间<br>(RDM接收)：</td>
                        <td style="width: 36%;">
                            <input name="relDemandTime"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 14%">发运通知单号：</td>
                        <td style="width: 36%;min-width:140px">
                            <input name="dispatchNo"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">发车时间(发运清单)：</td>
                        <td style="width: 36%;">
                            <input name="departTime"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 14%">整机关联发运清单操作人：</td>
                        <td style="width: 36%;">
                            <input name="relDispatchUserName"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">整机关联发运清单时间<br>(RDM接收)：</td>
                        <td style="width: 36%;min-width:140px">
                            <input name="relDispatchTime"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 14%">异常反馈预计完成时间<br/>(已发布)：</td>
                        <td style="width: 36%;">
                            <input name="expectTime"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">异常反馈原因(已发布)：</td>
                        <td style="width: 36%;min-width:140px">
                            <input name="reasonDesc"  class="mini-textarea" style="width:98%;height: 80px" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 14%">改制订单信息：</td>
                        <td style="width: 36%;">
                            <input name="changeApply"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">图册归档路径：</td>
                        <td style="width: 36%;min-width:140px">
                            <input name="atlasFilePath"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 14%">图册归档说明：</td>
                        <td style="width: 36%;min-width:140px">
                            <input name="fileDesc"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 14%">补发时间：</td>
                        <td style="width: 36%;">
                            <input name="reDispatchTime"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>

<div id="taskAbnormalWindow" title="异常反馈记录" class="mini-window" style="width:950px;height:400px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div id="taskAbnormalListGrid" class="mini-datagrid" style="width: 100%; height: 98%" allowResize="false" allowCellWrap="true"
             showCellTip="true" idField="id" showPager="false" showColumnsMenu="false" allowcelledit="false"
             allowAlternating="true" >
            <div property="columns">
                <div field="id" headerAlign="center" align="center" width="215">反馈编号</div>
                <div field="expectTime" width="100" headerAlign="center" align="center">预计完成时间</div>
                <div field="reasonDesc" width="300" headerAlign="center" align="center" >原因描述</div>
                <div field="abnormalStatus" width="70" headerAlign="center" align="center">审批状态</div>
                <div field="creatorName" width="70" headerAlign="center" align="center">反馈人</div>
                <div field="CREATE_TIME_" width="110" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">创建时间</div>
            </div>
        </div>
    </div>
</div>

<div id="taskRelFlowWindow" title="任务关联流程列表" class="mini-window" style="width:900px;height:400px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div id="relFlowListGrid" class="mini-datagrid" style="width: 100%; height: 98%" allowResize="false" allowCellWrap="false"
             showCellTip="true" idField="id" showPager="false" showColumnsMenu="false" allowcelledit="false"
             allowAlternating="true" >
            <div property="columns">
                <div type="indexcolumn" headerAlign="center" align="center" width="40">序号</div>
                <div field="flowType" width="100" headerAlign="center" align="center">流程类型</div>
                <div field="flowNum" width="200" headerAlign="center" align="center"  renderer="flowNumRenderer">流程编号</div>
                <div field="flowStatus"  width="100" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">流程状态</div>
                <div field="currentProcessUser"  width="100" align="center" headerAlign="center" allowSort="false">当前处理人</div>
                <div field="currentProcessTask" width="150" align="center" headerAlign="center">当前流程任务</div>
                <div field="creator"  width="70" headerAlign="center" align="center" allowSort="false">创建人</div>
                <div field="CREATE_TIME_"  width="100" headerAlign="center" align="center" allowSort="false">创建时间</div>
            </div>
        </div>
    </div>
</div>
