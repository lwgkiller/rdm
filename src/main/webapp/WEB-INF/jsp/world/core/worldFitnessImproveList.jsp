<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>海外适应性改进申请列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/world/worldFitnessImproveList.js?version=${static_res_version}"
            type="text/javascript"></script>

</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.worldFitnessImproveList.sc" />: </span>
                    <input class="mini-textbox" id="applyNumber" name="applyNumber"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.worldFitnessImproveList.fqr" />: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.worldFitnessImproveList.yxzt" />: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:120px;" multiSelect="false"
                           textField="value" valueField="key" emptyText="<spring:message code="page.worldFitnessImproveList.qxz" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.worldFitnessImproveList.qxz" />..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '批准'},{'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.worldFitnessImproveList.cx" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.worldFitnessImproveList.qkcx" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="addTask()"><spring:message code="page.worldFitnessImproveList.xz" /></a>

                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="worldFtinessImproveList" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         autoload="true"
         url="${ctxPath}/world/core/fitnessImprove/applyList.do"
         idField="id"
         multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">

        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="75" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.worldFitnessImproveList.cz" />
            </div>
            <div field="applyNumber" width="100" headerAlign="center" align="center" allowSort="true"><spring:message code="page.worldFitnessImproveList.sc" /></div>
            <div field="creatorName" width="50" headerAlign="center" align="center"><spring:message code="page.worldFitnessImproveList.fqr" /></div>
            <div field="improveSource" width="50" headerAlign="center" align="center" renderer="onFromRenderer"><spring:message code="page.worldFitnessImproveList.ly" /></div>
            <div field="productModel" width="50" headerAlign="center" align="center"><spring:message code="page.worldFitnessImproveList.spxh" /></div>
            <div field="regionKey" width="40" headerAlign="center" align="center"><spring:message code="page.worldFitnessImproveList.qy" /></div>
            <div field="reqDesc" width="80" headerAlign="center" align="center"><spring:message code="page.worldFitnessImproveList.xqms" /></div>
            <div field="productManagerName" width="40" headerAlign="center" align="center"><spring:message code="page.worldFitnessImproveList.cpzg" /></div>
            <div field="improveProjcetDesc" width="80" headerAlign="center" align="center"><spring:message code="page.worldFitnessImproveList.gjfa" /></div>
            <div field="technical" width="50" headerAlign="center" align="center" renderer="onTechnicalRenderer"><spring:message code="page.worldFitnessImproveList.jssfgb" /></div>
            <div field="taskName" headerAlign='center' align='center' width="100"><spring:message code="page.worldFitnessImproveList.dqrw" /></div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="40"><spring:message code="page.worldFitnessImproveList.dqclr" /></div>
            <div field="status" width="40" headerAlign="center" align="center"
                 renderer="onStatusRenderer"><spring:message code="page.worldFitnessImproveList.jd" />
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd" width="55" align="center" headerAlign="center"
                 allowSort="true"><spring:message code="page.worldFitnessImproveList.tjsj" />
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var worldFtinessImproveList = mini.get("worldFtinessImproveList");

    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        debugger
        var technical = record.technical;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title=' + worldFitnessImproveList_mx + ' onclick="clickDetail(\'' + applyId + '\',\'' + status + '\',\'' + instId + '\')"><spring:message code="page.worldFitnessImproveList.mx" /></span>';
        }
        if (status == 'DRAFTED') {
            s += '<span  title=' + worldFitnessImproveList_bj + ' onclick="clickEdit(\'' + applyId + '\',\'' + instId + '\')"><spring:message code="page.worldFitnessImproveList.bj" /></span>';
        }
        if (status == 'RUNNING' && record.myTaskId) {
            s += '<span  title=' + worldFitnessImproveList_bl + ' onclick="clickTask(\'' + record.myTaskId + '\')"><spring:message code="page.worldFitnessImproveList.bl" /></span>';
        }
        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title=' + worldFitnessImproveList_schu + ' onclick="clickRemove(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')"><spring:message code="page.worldFitnessImproveList.schu" /></span>';
        }
        if (currentUserId == record.productManager) {
            s += '<span  title=' + worldFitnessImproveList_bgjszt + ' onclick="statusChange(\'' + applyId + '\',\'' + technical + '\')"><spring:message code="page.worldFitnessImproveList.bgjszt" /></span>';
        }
        return s;
    }

    function onFromRenderer(e) {
        var record = e.record;
        var improveSource = record.improveSource;

        var arr = [{'key': 'zdkh', 'value': '终端客户'},
            {'key': 'dls', 'value': '代理商'},
            {'key': 'jck', 'value': 'I&O进出口',},
            {'key': 'yfjd', 'value': '研发基地'}
        ];

        return $.formatItemValue(arr, improveSource);
    }

    function onTechnicalRenderer(e) {
        var record = e.record;
        var technical = record.technical;

        var arr = [{'key': 'Yes', 'value': '是'},
            {'key': 'No', 'value': '否'}
        ];
        return $.formatItemValue(arr, technical);
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '批准', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function statusChange(applyId,technical) {
        mini.confirm(worldFitnessImproveList_qdbg, worldFitnessImproveList_ts, function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/world/core/fitnessImprove/statusChange.do?technical="+technical,
                    method: 'POST',
                    showMsg: false,
                    data: {applyId: applyId},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
</script>
<redxun:gridScript gridId="worldFtinessImproveList" entityName="" winHeight="" winWidth="" entityTitle=""
                   baseUrl=""/>
</body>
</html>

