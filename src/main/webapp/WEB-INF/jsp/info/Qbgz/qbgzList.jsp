<%--
  Created by IntelliJ IDEA.
  User: matianyu
  Date: 2021/2/23
  Time: 14:57
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/qbgz/qbgzList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.qbgzList.name" />: </span>
                    <input class="mini-textbox" id="qbNum" name="qbNum"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.qbgzList.name1" />: </span>
                    <input id="companyName" name="companyName" class="mini-combobox" style="width:150px;" textField="text"
                           valueField="id" emptyText="<spring:message code="page.qbgzList.name2" />..."
                           data="[{id:'三一',text:'三一'},{id:'卡特',text:'卡特'},{id:'小松',text:'小松'},
                         {id:'柳工',text:'柳工'},{id:'临工',text:'临工'},{id:'其他',text:'其他'}]"
                           allowInput="true" showNullItem="true" nullItemText="<spring:message code="page.qbgzList.name2" />..."/>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.qbgzList.name3" />: </span>
                    <input id="projectName" name="projectName" class="mini-combobox" style="width:150px;" textField="text"
                           valueField="id" emptyText="<spring:message code="page.qbgzList.name2" />..."
                           data="[{id:'成本价格',text:'成本价格'},{id:'产品技术',text:'产品技术'},{id:'研发体系',text:'研发体系'},
                         {id:'产权创新',text:'产权创新'}]"
                            allowInput="true" showNullItem="true" nullItemText="<spring:message code="page.qbgzList.name2" />..."
                           onvaluechanged="valuechanged()"/>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.qbgzList.name4" />: </span>
                    <input id="qbgzType" name="qbgzType" class="mini-combobox" style="width:150px;" textField="text"
                           valueField="id" emptyText="<spring:message code="page.qbgzList.name2" />..."
                            allowInput="true" showNullItem="true" nullItemText="<spring:message code="page.qbgzList.name2" />..."/>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.qbgzList.name5" /></a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px;display: none" plain="true"
                       onclick="addQbgz()"><spring:message code="page.qbgzList.name6" /></a>
                <a class="mini-button" style="margin-right: 5px;display: none" plain="true" onclick="moban()"><spring:message code="page.qbgzList.name7" /></a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="qbgzListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/Info/Qbgz/queryQbgz.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;"><spring:message code="page.qbgzList.name8" />
            </div>
            <div field="qbNum" headerAlign='center' width="40" align='center'><spring:message code="page.qbgzList.name" /></div>
            <div field="companyName" headerAlign='center' width="40" align='center'><spring:message code="page.qbgzList.name1" /></div>
            <div field="projectName" sortField="projectName" width="40" headerAlign="center" align="center" allowSort="true">
                <spring:message code="page.qbgzList.name3" />
            </div>
            <div field="qbgzType" sortField="qbgzType" width="40" headerAlign="center" align="center" allowSort="true">
                <spring:message code="page.qbgzList.name4" />
            </div>
            <div field="qbName" sortField="qbName" width="120" headerAlign="center" align="center" allowSort="false">
                <spring:message code="page.qbgzList.name9" />
            </div>
            <div field="deptName" headerAlign='center' align='center' width="40"><spring:message code="page.qbgzList.name10" /></div>
            <div field="userName" headerAlign='center' align='center' width="40"><spring:message code="page.qbgzList.name11" /></div>
            <div field="currentProcessTask" headerAlign='center' align='center' width="40"><spring:message code="page.qbgzList.name12" /></div>
            <div field="currentProcessUser" headerAlign='center' align='center' width="40"><spring:message code="page.qbgzList.name13" /></div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer"><spring:message code="page.qbgzList.name14" />
            </div>
            <div field="CREATE_TIME_" width="30" dateFormat="yyyy-MM-dd" headerAlign='center' align="center"><spring:message code="page.qbgzList.name15" /></div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var qbgzListGrid = mini.get("qbgzListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";
    var isQbzy =${isQbzy};

    qbgzListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });

    function onMessageActionRenderer(e) {
        var record = e.record;
        var qbgzId = record.qbgzId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title=' + qbgzList_name + ' onclick="qbgzDetail(\'' + qbgzId + '\',\'' + instId + '\')">' + qbgzList_name + '</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + qbgzList_name1 + ' onclick="qbgzEdit(\'' + qbgzId + '\',\'' + instId + '\')">' + qbgzList_name1 + '</span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1) {
                s+='<span  title=' + qbgzList_name2 + ' onclick="qbgzTask(\'' + record.taskId + '\')">' + qbgzList_name2 + '</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title=' + qbgzList_name3 + ' onclick="removeQbgz(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + qbgzList_name3 + '</span>';
        }
        if (status == 'SUCCESS_END' &&(currentUserNo=='admin'||isQbzy)) {
            s += '<span  title=' + qbgzList_name3 + ' onclick="removeQbgz(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + qbgzList_name3 + '</span>';
            s += '<span  title=' + qbgzList_name4 + ' onclick="qbgzChange(\'' + qbgzId + '\')">' + qbgzList_name4 + '</span>';
        }
        return s;
    }
    function valuechanged() {
        var projectName = mini.get("projectName").getValue();
        if ("成本价格" == projectName) {
            mini.get("qbgzType").setData("[{id:'物料成本',text:'物料成本'},{id:'挖机售价',text:'挖机售价'},{id:'供应商情况',text:'供应商情况'}]");
        } else if ("产品技术" == projectName) {
            mini.get("qbgzType").setData("[{id:'新品信息',text:'新品信息'},{id:'新品计划',text:'新品计划'},{id:'创新规划',text:'创新规划'}]");
        } else if ("研发体系" == projectName) {
            mini.get("qbgzType").setData("[{id:'研发体系文件',text:'研发体系文件'},{id:'机构及人员信息',text:'机构及人员信息'}" +
                ",{id:'海外研发情况',text:'海外研发情况'},{id:'奖项申报',text:'奖项申报'}]");
        } else if ("产权创新" == projectName) {
            mini.get("qbgzType").setData("[{id:'专利',text:'专利'},{id:'论文',text:'论文'},{id:'标准',text:'标准'}]");
        }
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '批准','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }

</script>
<redxun:gridScript gridId="qbgzListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
