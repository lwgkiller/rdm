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
    <script src="${ctxPath}/scripts/standardManager/standardChange.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px;display: none">
                    <span class="text" style="width:auto"><spring:message code="page.nationStandardChange.name1" />: </span>
                    <input class="mini-textbox" id="companyName" name="companyName"/>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.nationStandardChange.name2" />: </span>
                    <input class="mini-textbox" id="standardName" name="standardName"/>
                <li style="margin-left: 10px">
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.nationStandardChange.name3" />: </span>
                    <input class="mini-textbox" id="spNumber" name="spNumber"/>
                <li style="margin-left: 10px">
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.nationStandardChange.name4" />: </span>
                    <input id="standardLv" name="standardLv" class="mini-combobox" style="width:150px;" textField="text"
                           valueField="id" emptyText="<spring:message code="page.nationStandardChange.name5" />..."
                           data="[{id:'国际',text:'国际'},{id:'国家',text:'国家'},{id:'集团',text:'集团'},
                         {id:'地方',text:'地方'},{id:'行业',text:'行业'},{id:'团体',text:'团体'}]"
                           required="true" allowInput="true" showNullItem="true" nullItemText="<spring:message code="page.nationStandardChange.name5" />..."/>
                <li style="margin-left: 10px">
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.nationStandardChange.name6" />: </span>
                    <input id="joinDegree" name="joinDegree" class="mini-combobox" style="width:150px;" textField="text"
                           valueField="id" emptyText="<spring:message code="page.nationStandardChange.name5" />..."
                           data="[{id:'参与',text:'参与'},{id:'主持',text:'主持'}]"
                           required="true" allowInput="true" showNullItem="true" nullItemText="<spring:message code="page.nationStandardChange.name5" />..."/>
                <li style="margin-left: 10px">
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.nationStandardChange.name7" />: </span>
                    <input id="enactoralter" name="enactoralter" class="mini-combobox" style="width:150px;"
                           textField="text" valueField="id" emptyText="<spring:message code="page.nationStandardChange.name5" />..."
                           data="[{id:'制定',text:'制定'},{id:'修订',text:'修订'}]"
                           allowInput="true" showNullItem="true" nullItemText="<spring:message code="page.nationStandardChange.name5" />..."/>
                <li style="margin-left: 10px">
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.nationStandardChange.name8" />: </span>
                    <input id="standaStauts" name="standaStauts" class="mini-combobox" style="width:150px;"
                           textField="text" valueField="id" emptyText="<spring:message code="page.nationStandardChange.name5" />..."
                           data="[{id:'申请立项',text:'申请立项'},{id:'已立项 ',text:'已立项'},
                               {id:'工作组讨论稿',text:'工作组讨论稿'},{id:'征求意见稿',text:'征求意见稿'},
                               {id:'送审稿',text:'送审稿'},{id:'报批稿',text:'报批稿'},
                               {id:'已报批',text:'已报批'},{id:'已发布',text:'已发布'}]"
                           required="true" allowInput="true" showNullItem="true" nullItemText="<spring:message code="page.nationStandardChange.name5" />..."/>
                <li style="margin-left: 10px">
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.nationStandardChange.name9" />: </span>
                    <input class="mini-monthpicker" id="createtime1" name="createtime1"  showTime="true"/>
                </li>
                <li>
                    <span class="text-to"><spring:message code="page.nationStandardChange.name10" />：</span>
                    <input class="mini-monthpicker" id="createtime2" name="createtime2"  showTime="true"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.nationStandardChange.name11" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.nationStandardChange.name12" /></a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addNew()"><spring:message code="page.nationStandardChange.name13" /></a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="nationStandardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/standardManager/core/NationStandardChangeController/queryList.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="35" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;"><spring:message code="page.nationStandardChange.name14" />
            </div>
            <div field="companyName" headerAlign='center' align='center' visible="false" renderer="onMessageTitleRenderer" width="25"><spring:message code="page.nationStandardChange.name1" /></div>
            <div field="standardName" headerAlign='center' align='center' width="60"><spring:message code="page.nationStandardChange.name2" /></div>
            <div field="spNumber" sortField="numInfo" width="40" headerAlign="center" align="center" allowSort="true">
                <spring:message code="page.nationStandardChange.name3" />
            </div>
            <div field="standardLv" width="25" headerAlign="center" align="center" allowSort="true"><spring:message code="page.nationStandardChange.name4" /></div>
            <div field="joinDegree" width="25" headerAlign="center" align="center" allowSort="true"><spring:message code="page.nationStandardChange.name6" /></div>
            <div field="enactoralter" width="25" headerAlign="center" align="center" allowSort="true"><spring:message code="page.nationStandardChange.name7" /></div>
            <div field="standaStauts" width="25" headerAlign="center" align="center" allowSort="true"><spring:message code="page.nationStandardChange.name8" /></div>
            <div field="releaseTime" width="30" dateFormat="yyyy-MM-dd" headerAlign='center' align="center"><spring:message code="page.nationStandardChange.name9" /></div>
            <div field="belongGroup" width="25" headerAlign="center" align="center" allowSort="true"><spring:message code="page.nationStandardChange.name15" /></div>
            <div field="note" width="25" headerAlign="center" align="center" allowSort="true"><spring:message code="page.nationStandardChange.name16" /></div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var nationStandardListGrid = mini.get("nationStandardListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var isJSBZGL =${isJSBZGL};
    var currentUserNo = "${currentUserNo}";
    if(!isJSBZGL){
        mini.get("editMsg").setEnabled(false);
    }
    function onMessageActionRenderer(e) {
        var record = e.record;
        var standardId = record.standardId;
        var instId = record.instId;
        var s = '';
        s += '<span  title=' + nationStandardChange_ck + ' onclick="StandardChangeDetail(\'' + standardId + '\')">' + nationStandardChange_ck + '</span>';
        if(isJSBZGL||currentUserNo=='admin'){
            s += '<span  title=' + nationStandardChange_bj + ' onclick="addNew(\'' + standardId + '\')">' + nationStandardChange_bj + '</span>';
            s += '<span  title=' + nationStandardChange_sc + ' onclick="removestandardChange(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + nationStandardChange_sc + '</span>';
        }
        return s;
    }


</script>
<redxun:gridScript gridId="nationStandardListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
