<%--这个页面由六个菜单共享，流程一样,分别是：--%>
<%--新产品导入下的(新品试制问题,XPSZ),(新品整机试验问题,XPZDSY),(新品路试问题,XPLS)--%>
<%--分析改进下的(厂内问题改进,CNWT),(市场问题改进,SCWT),(海外问题改进,HWWT)--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>质量改进列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/zlgjNPI/zlgjList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <input id="wtlxtype" name="wtlxtype" class="mini-hidden" value="${wtlxtype}"/>
            <input id="deptName" name="deptName" class="mini-hidden"/>
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.zlgjList.name"/>: </span>
                    <input class="mini-textbox" id="zlgjNumber" name="zlgjNumber">
                </li>
                <li style="margin-right: 15px;">
                    <span class="text" style="min-width:250px;"><spring:message code="page.zlgjList.name5"/>: </span>
                    <input id="jiean" name="jiean" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.zlgjList.name2" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.zlgjList.name2" />..."
                           data="[ {'key' : 'no','value' : '进行中'},{'key' : 'yes','value' : '已完成'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.zlgjList.name21"/>: </span>
                    <input id="jjcd" name="jjcd" class="mini-combobox" style="width:98%;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.zlgjList.name2" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.zlgjList.name2" />..."
                           data="[{'key' : '特急','value' : '特急'},{'key' : '紧急','value' : '紧急'},{'key' : '一般','value' : '一般'}]"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.zlgjList.name35"/>: </span>
                    <input id="yzcd" name="yzcd" class="mini-combobox" style="width:98%;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.zlgjList.name2" />..."
                           nullItemText="<spring:message code="page.zlgjList.name2" />..."
                           required="false" allowInput="false" showNullItem="true" multiSelect="true"
                           data="[{'key' : 'A','value' : 'A'},{'key' : 'B','value' : 'B'},{'key' : 'C','value' : 'C'},
                                           {'key' : 'W','value' : 'W'}]"/>
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em><spring:message code="page.zlgjList.name6"/></em>
						<i class="unfoldIcon"></i>
					</span>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message
                            code="page.zlgjList.name7"/></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message
                            code="page.zlgjList.name8"/></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="addZlgj" class="mini-button" style="margin-right: 5px" plain="true" onclick="addZlgj()"><spring:message
                            code="page.zlgjList.name9"/></a>
                    <a id="removeZlgj" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeZlgj()"><spring:message
                            code="page.zlgjList.name10"/></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportWtsb()"><spring:message
                            code="page.zlgjList.name11"/></a>
                </li>
            </ul>

            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.zlgjList.name12"/>: </span>
                        <input id="zrcpzgId" name="zrcpzgId" textname="zrcpzgName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                               mainfield="no" single="true"/>
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.zlgjList.name13"/>:</span>
                        <input id="ssbmId" name="ssbmId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:160px;height:34px" allowinput="false" label="<spring:message code="page.zlgjList.name33" />"
                               textname="ssbmName" length="500" maxlength="500"
                               minlen="0" single="true" required="false" initlogindep="false"
                               level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </li>

                    <li style="margin-right: 15px;display: none" id="tdmSylx_li">
                        <span class="text" style="width:auto"><spring:message code="page.zlgjList.name16"/>: </span>
                        <input id="tdmSylx" name="tdmSylx" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.zlgjList.name2" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.zlgjList.name2" />..."
                               data="[{'key' : '新品试验','value' : '新品试验'},{'key' : '竞品试验','value' : '竞品试验'},
                                           {'key' : '专项试验','value' : '专项试验'},{'key' : '可靠性试验','value' : '可靠性试验'},
                                           {'key' : '委外认证','value' : '委外认证'},{'key' : '调试试验','value' : '调试试验'},
                                           {'key' : '故障试验','value' : '故障试验'}]"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.zlgjList.name17"/> </span>:<input name="create_startTime"
                                                                                                                           id="create_startTime"
                                                                                                                           class="mini-datepicker"
                                                                                                                           format="yyyy-MM-dd"
                                                                                                                           style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto"><spring:message code="page.zlgjList.name18"/>: </span><input name="create_endTime"
                                                                                                                              id="create_endTime"
                                                                                                                              class="mini-datepicker"
                                                                                                                              format="yyyy-MM-dd"
                                                                                                                              style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.zlgjList.name19"/>: </span>
                        <input id="applyUserId" name="applyUserId" textname="applyUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                               mainfield="no" single="true"/>
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.zlgjList.name20"/>:</span>
                        <input id="applyDepId" name="applyDepId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:160px;height:34px" allowinput="false" label="<spring:message code="page.zlgjList.name33" />"
                               textname="applyUserDeptName" length="500" maxlength="500"
                               minlen="0" single="true" required="false" initlogindep="false"
                               level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.zlgjList.name22"/>: </span>
                        <input id="ifgj" name="ifgj" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" emptyText="<spring:message code="page.zlgjList.name2" />..."
                               nullItemText="<spring:message code="page.zlgjList.name2" />..."
                               required="false" allowInput="false" showNullItem="true"
                               data="[ {'key' : '是','value' : 'yes'},{'key' : '否','value' : 'no'}]"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.zlgjList.name24"/>: </span>
                        <input id="currentProcessUserId" name="currentProcessUserId" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                               mainfield="no" single="true"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.zlgjList.name25"/>: </span>
                        <input id="zrrName" name="zrrName" class="mini-textbox" style="width:98%;"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">流程状态: </span>
                        <input id="status" name="status" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '审批完成'},
							   {'key' : 'DISCARD_END','value' : '作废'}]"
                        />
                    </li>
                </ul>
                <ul id="lbjUl">
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">零部件名称: </span>
                        <input id="componentName" name="componentName" class="mini-textbox" style="width:98%;"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">零部件型号: </span>
                        <input id="componentModel" name="componentModel" class="mini-textbox" style="width:98%;"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">物料编码: </span>
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">配套主机型号: </span>
                        <input id="machineModel" name="machineModel" class="mini-textbox" style="width:98%;"/>
                    </li>
                </ul>
                <ul id="zlgjUl">
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.zlgjList.name3"/>: </span>
                        <input class="mini-textbox" id="smallJiXing" name="smallJiXing">
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.zlgjList.name4"/>: </span>
                        <input class="mini-textbox" id="zjbh" name="zjbh">
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.zlgjList.name1"/>: </span>
                        <input id="jiXing" name="jiXing" class="mini-combobox" style="width:100px;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.zlgjList.name2" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.zlgjList.name2" />..."
                               data="[{'key' : 'WEIWA','value' : '微挖'},{'key' : 'LUNWA','value' : '轮挖'},{'key' : 'XIAOWA','value' : '小挖'},
                                   {'key' : 'ZHONGWA','value' : '中挖'},{'key' : 'DAWA','value' : '大挖'},{'key' : 'TEWA','value' : '特挖'},
                                   {'key' : 'SHUJU','value' : '属具'},{'key' : 'XINNENGYUAN','value' : '新能源'},{'key' : 'HAIWAI','value' : '海外'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.zlgjList.name23"/>: </span>
                        <input id="sggk" name="sggk" class="mini-textbox" style="width:98%;"/>
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.zlgjList.name14"/>: </span>
                        <input class="mini-textbox" id="gzlj" name="gzlj">
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.zlgjList.name15"/>: </span>
                        <input class="mini-textbox" id="wtms" name="wtms">
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.zlgjList.name26"/>: </span>
                        <input id="gzxt" name="gzxt" class="mini-combobox" style="width:98%;"
                               textField="text" valueField="key_" emptyText="<spring:message code="page.zlgjList.name2" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.zlgjList.name2" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=GZXT"/>
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="zlgjListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         allowHeaderWrap="true"
         url="${ctxPath}/xjsdr/core/zlgj/getZlgjList.do?czxpj=${czxpj}&wtlxtype=${wtlxtype}&report=${report}" idField="wtId" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="30"></div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;"><spring:message code="page.zlgjList.name27"/>
            </div>
            <div field="zlgjNumber" width="150" headerAlign="center" align="center"><spring:message code="page.zlgjList.name"/></div>
            <div field="yzcd" width="60" headerAlign="center" align="center"><spring:message code="page.zlgjList.name28"/></div>
            <div field="componentCategory" name="componentCategory" width="70" headerAlign="center" align="center">零部件类型</div>
            <div field="componentName" name="componentName" width="70" headerAlign="center" align="center">零部件名称</div>
            <div field="componentModel" name="componentModel" width="70" headerAlign="center" align="center">零部件型号</div>
            <div field="materialCode" name="materialCode" width="70" headerAlign="center" align="center">物料编码</div>
            <div field="testType" name="testType" width="70" headerAlign="center" align="center">试验类型</div>
            <div field="sampleType" name="sampleType" width="70" headerAlign="center" align="center">样品类型</div>
            <div field="machineModel" name="machineModel" width="70" headerAlign="center" align="center">配套主机型号</div>
            <div field="lbjgys" name="lbjgys" width="70" headerAlign="center" align="center">零部件供应商</div>
            <div field="testRounds" name="testRounds" width="70" headerAlign="center" align="center">试验次数</div>
            <div field="laboratory" name="laboratory" width="70" headerAlign="center" align="center">试验承担单位</div>
            <div field="testLeader" name="testLeader" width="70" headerAlign="center" align="center">试验负责人</div>
            <div field="testConclusion" name="testConclusion" width="250" headerAlign="center" align="center">测试结论</div>
            <div field="nonconformingDescription" name="nonconformingDescription" width="250" headerAlign="center" align="center">不合格项说明</div>
            <div field="improvementSuggestions" name="improvementSuggestions" width="250" headerAlign="center" align="center">改进建议</div>
            <div field="tdmSylx" name="tdmSylx" width="70" headerAlign="center" align="center"><spring:message code="page.zlgjList.name16"/></div>
            <div field="jiXing" name="jiXing" width="65" headerAlign="center" align="center" renderer="onJXRenderer"><spring:message
                    code="page.zlgjList.name1"/></div>
            <div field="smallJiXing" name="smallJiXing" width="100" headerAlign="center" align="center"><spring:message code="page.zlgjList.name3"/></div>
            <div field="zjbh" name="zjbh" sortField="zjbh" width="140" headerAlign="center" align="center"><spring:message code="page.zlgjList.name4"/></div>
            <div field="gzlj" name="gzlj" sortField="gzlj" width="110" headerAlign="center" align="center"><spring:message code="page.zlgjList.name14"/></div>
            <div field="wtms" name="wtms" width="400" headerAlign="center" align="center"><spring:message code="page.zlgjList.name15"/></div>
            <div field="zrcpzgName" width="80" headerAlign="center" align="center"><spring:message code="page.zlgjList.name12"/></div>
            <div field="ssbmName" width="100" headerAlign="center" align="center"><spring:message code="page.zlgjList.name13"/></div>
            <div field="zrrName" width="80" headerAlign="center" align="center"><spring:message code="page.zlgjList.name25"/></div>
            <div field="zrrDeptName" width="80" headerAlign="center" align="center">问题处理人部门</div>
            <div field="jiean" width="70" headerAlign="center" align="center" renderer="jieanRenderer"><spring:message
                    code="page.zlgjList.name29"/></div>
            <div field="ifgj" width="70" headerAlign="center" align="center" renderer="ifgjRenderer"><spring:message
                    code="page.zlgjList.name22"/></div>
            <div field="sggk" width="70" headerAlign="center" align="center"><spring:message code="page.zlgjList.name23"/></div>
            <div field="status" width="70" headerAlign="center" align="center" renderer="onStatusRenderer"><spring:message
                    code="page.zlgjList.name30"/></div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="80" align="center" headerAlign="center"><spring:message
                    code="page.zlgjList.name31"/></div>
            <div field="currentProcessUserDeptName"  width="80" align="center" headerAlign="center">流程处理人部门</div>
            <div field="currentProcessTask" width="100" align="center" headerAlign="center"><spring:message code="page.zlgjList.name32"/></div>
            <div field="applyUserName" width="80" headerAlign="center" align="center"><spring:message code="page.zlgjList.name19"/></div>
            <div field="applyUserDeptName" width="100" headerAlign="center" align="center"><spring:message code="page.zlgjList.name20"/></div>
            <div field="CREATE_TIME" width="90" headerAlign="center" align="center"><spring:message code="page.zlgjList.name34"/></div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/xjsdr/core/zlgj/exportWtsbExcel.do?wtlxtype=${wtlxtype}&czxpj=${czxpj}" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var zlgjListGrid = mini.get("zlgjListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserRoles =${currentUserRoles};
    var currentUserNo = "${currentUserNo}";
    var isJsglbRespUser =${isJsglbRespUser};
    var isBgry =${isBgry};
    var wtlxtype = "${wtlxtype}";
    var isScwtUser =${isScwtUser};
    var isHwwtUser =${isHwwtUser};
    var create_startTime = "${create_startTime}";
    var create_endTime = "${create_endTime}";
    var deptName = "${deptName}";
    var czxpj = "${czxpj}";
    var report = "${report}";
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var wtId = record.wtId;
        var instId = record.instId;
        var s = '';
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + zlgjList_name + ' onclick="zlgjEdit(\'' + wtId + '\',\'' + instId + '\')">' + zlgjList_name + '</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
                s += '<span  title=' + zlgjList_name1 + ' onclick="zlgjTask(\'' + record.taskId + '\')">' + zlgjList_name1 + '</span>';
            }
        }
        if (isBgry) {
            s += '<span  title=' + zlgjList_name2 + ' onclick="zlgjChange(\'' + wtId + '\',\'' + record.status + '\')">' + zlgjList_name2 + '</span>';
        }
        s += '<span  title=' + zlgjList_name3 + ' onclick="zlgjDetail(\'' + wtId + '\',\'' + record.status + '\')">' + zlgjList_name3 + '</span>';


        var status = record.status;
        if (currentUserNo != 'admin') {
            if ((status == 'DRAFTED' && currentUserId == record.CREATE_BY_) || (status == 'DISCARD_END' && currentUserId == record.CREATE_BY_)) {
                s += '<span  title=' + zlgjList_name4 + ' onclick="removeZlgj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + zlgjList_name4 + '</span>';
            }
        } else {
            s += '<span  title=' + zlgjList_name4 + ' onclick="removeZlgj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + zlgjList_name4 + '</span>';
        }
        s += '<span  title=' + zlgjList_name10 + ' onclick="copyZlgj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + zlgjList_name10 + '</span>';

        if (record.status == 'SUCCESS_END' && (wtlxtype == "XPZDSY" || wtlxtype == "XPSZ") && (record.CREATE_BY_ == currentUserId || currentUserId == "1")) {
            s += '<span  title=' + zlgjList_name5 + ' onclick="callBackOut(\'' + wtId + '\')">' + zlgjList_name5 + '</span>';
        }
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '审批完成', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];
        return $.formatItemValue(arr, status);
    }

    function ifgjRenderer(e) {
        var record = e.record;
        var ifgj = record.ifgj;
        if (!ifgj) {
            ifgj = 'yes';
        }
        var arr = [
            {'key': 'yes', 'value': '是', 'css': 'green'},
            {'key': 'no', 'value': '否', 'css': 'red'}
        ];
        return $.formatItemValue(arr, ifgj);
    }

    function jieanRenderer(e) {
        var record = e.record;
        var status = record.jiean;

        var arr = [
            {'key': '进行中', 'value': '进行中', 'css': 'orange'},
            {'key': '已完成', 'value': '已完成', 'css': 'blue'}
        ];
        return $.formatItemValue(arr, status);
    }

    function onJXRenderer(e) {
        var record = e.record;
        var jiXing = record.jiXing;

        var arr = [{'key': 'WEIWA', 'value': '微挖'},
            {'key': 'LUNWA', 'value': '轮挖'}, {'key': 'XIAOWA', 'value': '小挖'},
            {'key': 'ZHONGWA', 'value': '中挖'}, {'key': 'DAWA', 'value': '大挖'},
            {'key': 'TEWA', 'value': '特挖'}, {'key': 'SHUJU', 'value': '属具'},
            {'key': 'XINNENGYUAN', 'value': '新能源'}, {'key': 'HAIWAI', 'value': '海外'}
        ];
        return $.formatItemValue(arr, jiXing);
    }

</script>
<redxun:gridScript gridId="zlgjListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
