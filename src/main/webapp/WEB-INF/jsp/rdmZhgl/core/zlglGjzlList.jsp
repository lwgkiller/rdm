<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/4/12
  Time: 16:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>国际专利</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/rdmZhgl/zlglGjzlList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
    <div class="mini-toolbar" >
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul >
                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">PCT名称: </span>
                        <input class="mini-textbox" id="pctName" name="pctName">
                    </li>
                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">国际申请号: </span>
                        <input class="mini-textbox" id="applictonNumber" name="applictonNumber">
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">国际申请日 从 </span>:<input id="applictonDayStartTime"  name="applictonDayStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至: </span><input  id="applictonDayEndTime" name="applictonDayEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                    </li>
                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">指定国家: </span>
                        <input class="mini-textbox" id="theCountry" name="theCountry">
                    </li>
                    <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                    </li>

                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                        <div style="display: inline-block" class="separator"></div>
                        <a id="addGjzl" class="mini-button" style="margin-right: 5px" plain="true" onclick="addGjzl()">新增</a>
                        <a id="removeGjzl" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeGjzl()">删除</a>
                        <a class="mini-button" id="exportGjzl" style="margin-right: 5px" plain="true" onclick="exportGjzl()">导出</a>
                    </li>
                </ul>
                <div id="moreBox">
                    <ul>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">国家授权日 从 </span>:<input id="authorizedDayStartTime"  name="authorizedDayStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text-to" style="width:auto">至: </span><input  id="authorizedDayEndTime" name="authorizedDayEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                        </li>
                        <li style="margin-right: 14px">
                            <span class="text" style="width:auto">发明人: </span>
                            <input class="mini-textbox" id="theInventor" name="theInventor">
                        </li>
                        <li style="margin-right: 14px">
                            <span class="text" style="width:auto">代理机构: </span>
                            <input class="mini-textbox" id="theAgency" name="theAgency">
                        </li>
                        <li style="margin-right: 14px">
                            <span class="text" style="width:auto">当前状态: </span>
                            <input id="gjztId" name="gjztId" class="mini-combobox" style="width:98%;"
                                   textField="enumName" valueField="id" emptyText="请选择..."
                                   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..." />
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">授权奖励时间 从 </span>:<input id="rewardTimeStartTime"  name="rewardTimeStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text-to" style="width:auto">至: </span><input  id="rewardTimeEndTime" name="rewardTimeEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                        </li>
                    </ul>
                </div>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="gjzlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
             url="${ctxPath}/zhgl/core/zlgl/queryGjListData.do" idField="gjzlId" showPager="true"
             multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
                <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
                <div field="pctName"   width="120" headerAlign="center" align="center" allowSort="true">pct名称</div>
                <div field="englishName"   width="150" headerAlign="center" align="center" allowSort="false">英文名称</div>
                <div field="applictonNumber" align="center" width="150" headerAlign="center" allowSort="false">国际申请号</div>
                <div field="gjztName" displayField="gjztName" width="100" headerAlign="center" align="center" allowSort="false">当前状态</div>
                <div field="applictonDay"  width="70" headerAlign="center" align="center" allowSort="false">国际申请日</div>
                <div field="openDay"  width="70" headerAlign="center" align="center" allowSort="false">国际公开日</div>
                <div field="openNumber"  width="150" headerAlign="center" align="center" allowSort="false">国际公开号</div>
                <div field="theCountry"  width="60" headerAlign="center" align="center" allowSort="false">指定国家</div>
                <div field="nationalNamber"  width="150" headerAlign="center" align="center" allowSort="false">国家局申请号</div>
                <div field="nationOpenNumbei"  width="60" headerAlign="center" align="center" allowSort="false">国家局公开日</div>
                <div field="authorizedNumber"  width="60" headerAlign="center" align="center" allowSort="false">国家授权号</div>
                <div field="authorizedDay"  width="60" headerAlign="center" align="center" allowSort="false">国家授权日</div>
                <div field="theInventor"  width="60" headerAlign="center" align="center" allowSort="false">发明人</div>
                <div field="thePatentee_theApplicane"  width="90" headerAlign="center" align="center" allowSort="false" renderer="onStatusRenderer">专利权人/申请人</div>
                <div field="theAgency"  width="60" headerAlign="center" align="center" allowSort="false">代理机构</div>
                <div field="ifAppliedNational" displayField="ifAppliedNational" width="110" headerAlign="center" align="center" allowSort="false">是否已申请国家专利</div>
                <div field="correspondingNumber"  width="100" headerAlign="center" align="center" allowSort="false">对应的国内专利号</div>
                <div field="patentName" sortField="patentName" width="150" headerAlign="center" align="center" allowSort="false">专利名称</div>
                <div field="ifNationalStage" displayField="ifNationalStage" width="90" headerAlign="center" align="center" allowSort="false">是否已进国家阶段</div>
                <div field="rewardAmount"  width="60" headerAlign="center" align="center" allowSort="false">授权奖励金额</div>
                <div field="rewardTime"  width="60" headerAlign="center" align="center" allowSort="false">授权奖励时间</div>
                <%--<div field="myCompanyUserNames"  width="60" headerAlign="center" align="center" allowSort="false">我司发明人</div>--%>
            </div>
        </div>
    </div>
    <!--导出Excel相关HTML-->
    <form id="excelForm" action="${ctxPath}/zhgl/core/zlgl/exportGjzlList.do" method="post" target="excelIFrame">
        <input type="hidden" name="filter" id="filter"/>
    </form>
    <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
    <script type="text/javascript">
        mini.parse();
        var gjzlPath="${ctxPath}";
        var gjzlListGrid=mini.get("gjzlListGrid");
        var currentUserId="${currentUserId}";
        var currentUserNo="${currentUserNo}";
        var currentUserRoles=${currentUserRoles};

        function onActionRenderer(e) {
            var record = e.record;
            var gjzlId = record.gjzlId;
            var s = '';
            s+= '<span  title="明细" onclick="zgzlDetail(\'' + gjzlId +'\''+ ')">明细</span>';
            if((currentUserId == record.CREATE_BY_) ||(whetherGjzl(currentUserRoles))){
                s+= '<span  title="编辑" onclick="zgzlkEdit(\'' + gjzlId +'\''+ ')">编辑</span>';
            }

            return s;
        }
    </script>
    <redxun:gridScript gridId="gjzlListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
