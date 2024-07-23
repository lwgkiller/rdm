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
    <title>中国专利</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/rdmZhgl/zlglZgzlList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
    <div class="mini-toolbar" >
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul >
                    <li style="margin-right: 15px"><span class="text" style="width:auto">部门:</span>
                        <input id="departmentId" name="departmentId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:130px;height:34px"
                               allowinput="false" label="部门" textname="bm_name" length="500" maxlength="500" minlen="0" single="true" required="false" initlogindep="false"
                               level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </li>

                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">专利类型: </span>
                        <input id="zllxId" name="zllxId" class="mini-combobox" style="width:100px;"
                               textField="enumName" valueField="id" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..." />
                    </li>
                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">专利名称: </span>
                        <input class="mini-textbox" id="patentName" style="width:120px" name="patentName">
                    </li>
                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">提案名称: </span>
                        <input class="mini-textbox" id="reportName" style="width:90px" name="reportName">
                    </li>
                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">发明人: </span>
                        <input class="mini-textbox" id="theInventors" style="width:90px" name="theInventors">
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
                        <a class="mini-button" id="exportZgzl" style="margin-right: 5px" plain="true" onclick="exportZgzl()">导出</a>
                        <a id="addZgzl" class="mini-button" style="margin-right: 5px" plain="true" onclick="addZgzl()">新增</a>
                        <a id="removeZgzl" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeZgzl()">删除</a>
                        <a class="mini-button" id="importId" style="margin-left: 5px" onclick="openImportWindow()">导入</a>
                        <a style="margin-left: 15px">
                            <span class="text" style="width:auto;font-weight: bold;font-size:15px">更新时间：</span>
                            <input id="sysLastUpdateTime"  name="sysLastUpdateTime"  class="mini-datepicker" format="yyyy-MM-dd"  allowInput="false" style="width:120px" value="${lastUpdateTime}"/>
                        </a>
                        <a class="mini-button" id="saveTime" style="margin-left: 5px" onclick="saveTime()">保存</a>

                    </li>
                </ul>
                <div id="moreBox">
                    <ul>
                        <li style="margin-right: 14px">
                            <span class="text" style="width:auto">专业: </span>
                            <input class="mini-textbox" id="zhuanYe" style="width:90px" name="zhuanYe">
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">审批日 从 </span>:<input id="examinationApprovalStartTime"  name="examinationApprovalStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text-to" style="width:auto">至: </span><input  id="examinationApprovalEndTime" name="examinationApprovalEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">申请日 从 </span>:<input id="filingdateStartTime"  name="filingdateStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text-to" style="width:auto">至: </span><input  id="filingdateEndTime" name="filingdateEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">授权日 从 </span>:<input id="authionizationStartTime"  name="authionizationStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text-to" style="width:auto">至: </span><input  id="authionizationEndTime" name="authionizationEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                        </li>
                        <li style="margin-right: 14px">
                            <span class="text" style="width:auto">案件状态: </span>
                            <input id="gnztId" name="gnztId"  class="mini-combobox" style="width:98%;"
                                   textField="enumName" valueField="id" emptyText="请选择..."
                                   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">专利权丧失发文日 从 </span>:<input id="patentDateStartTime"  name="patentDateStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text-to" style="width:auto">至: </span><input  id="patentDateEndTime" name="patentDateEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">失效日期 从 </span>:<input id="expiryDateStartTime"  name="expiryDateStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text-to" style="width:auto">至: </span><input  id="expiryDateEndTime" name="expiryDateEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                        </li>
                        <li style="margin-right: 14px">
                            <span class="text" style="width:auto">失效原因: </span>
                            <input class="mini-textbox" id="failureReason" name="failureReason">
                        </li>
                        <li style="margin-right: 14px">
                            <span class="text" style="width:auto">代理机构名称: </span>
                            <input class="mini-textbox" id="agencyName" name="agencyName">
                        </li>
                        <li style="margin-right: 14px">
                            <span class="text" style="width:auto">奖励类型: </span>
                            <input class="mini-textbox" id="jllb" name="jllb">
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">授权奖励时间 从 </span>:<input id="authirizedTimeStartTime"  name="authirizedTimeStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text-to" style="width:auto">至: </span><input  id="authirizedTimeEndTime" name="authirizedTimeEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                        </li>
                    </ul>
                </div>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="zgzlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
             url="${ctxPath}/zhgl/core/zlgl/queryListData.do" idField="zgzlId" allowCellWrap="true"
             multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" width="25px"></div>
                <div type="indexcolumn" width="50px" headerAlign="center" align="center">序号</div>
                <div name="action" cellCls="actionIcons" width="90px" headerAlign="center" align="center" renderer="onActionRenderer" >操作</div>
                <div field="deptName"  sortField="deptName"  width="80px" headerAlign="center" align="center" allowSort="true">部门</div>
                <div field="zhuanYe" align="center" width="80px" headerAlign="center" allowSort="false">专业</div>
                <div field="billNo" sortField="XMMC"  width="150px" headerAlign="center" align="center" allowSort="false">提案号</div>
                <div field="reportName" align="center" width="130px" headerAlign="center" allowSort="false">提案名称</div>
                <div field="patentName" sortField="patentName" width="130px" headerAlign="center" align="center" allowSort="false">专利名称</div>
                <div field="zllxName" displayField="zllxName" width="80px" headerAlign="center" align="center" allowSort="false">专利类型</div>
                <div field="applicationNumber"  width="130px" headerAlign="center" align="center" allowSort="false">专利（申请）号</div>
                <div field="theInventors"  width="150px" headerAlign="center" align="center" allowSort="false">发明人</div>
                <div field="thepatentee"  width="90px" headerAlign="center" align="center" allowSort="false" renderer="onStatusRenderer">专利权人/申请人</div>
                <div field="projectName"  width="120px" headerAlign="center" align="center" allowSort="false">项目名称</div>
                <div field="examinationApproval"  width="90px" headerAlign="center" align="center" allowSort="false">审批日</div>
                <div field="byCase"  width="90px" headerAlign="center" align="center" allowSort="false">委案日</div>
                <div field="filingdate" sortField="filingdate" width="90px" headerAlign="center" align="center" allowSort="true">申请日</div>
                <div field="authorizationDate"  width="90px" headerAlign="center" align="center" allowSort="false">授权通知发文日</div>
                <div field="authionization"  width="90px" headerAlign="center" align="center" allowSort="false">授权日期</div>
                <div field="specifyCountry"  width="80px" headerAlign="center" align="center" allowSort="false">专利证书号</div>
                <div field="ifName" displayField="ifName" width="90px" headerAlign="center" align="center" allowSort="false">是否存在许可他人使用</div>
                <div field="personPermitted"  width="110px" headerAlign="center" align="center" allowSort="false">被许可使用的单位或人员名称</div>
                <div field="ifzyName" displayField="ifzyName" width="60px" headerAlign="center" align="center" allowSort="false">是否质押</div>
                <div field="transferredCompany"  width="90px" headerAlign="center" align="center" allowSort="false">被转让公司名称</div>
                <div field="claimsNumber"  width="90px" headerAlign="center" align="center" allowSort="false">权利要求项数</div>
                <div field="gnztName" sortField="gnztId" displayField="gnztName" width="70px" headerAlign="center" align="center" allowSort="true">案件状态</div>
                <div field="patentDate"  width="110px" headerAlign="center" align="center" allowSort="false">专利权丧失发文日</div>
                <div field="expiryDate"  width="90px" headerAlign="center" align="center" allowSort="false">失效日期</div>
                <div field="failureReason"  width="80px" headerAlign="center" align="center" allowSort="false">失效原因</div>
                <div field="agencyName"  width="80px" headerAlign="center" align="center" allowSort="false">代理机构名称</div>
                <div field="agentThe"  width="80px" headerAlign="center" align="center" allowSort="false">代理人</div>
                <div field="productName"  width="110px" headerAlign="center" align="center" allowSort="false">专利所属公司项目或产品名称</div>
                <div field="companyBelongs"  width="130px" headerAlign="center" align="center" allowSort="false">专利所属公司项目或产品所处阶段</div>
                <div field="ipcFlh"  width="80px" headerAlign="center" align="center" allowSort="false">IPC分类号</div>
                <div field="ipcZflh"  width="80px" headerAlign="center" align="center" allowSort="false">IPC主分类号</div>
                <%--<div field="myCompanyUserNames"  width="80" headerAlign="center" align="center" allowSort="false">我司发明人</div>--%>
                <div field="jllb"  width="80px" headerAlign="center" align="center" allowSort="false">奖励类别</div>
                <div field="authorizedReward"  width="80px" headerAlign="center" align="center" allowSort="false">奖励金额</div>
                <div field="authirizedTime"  width="90px" headerAlign="center" align="center" allowSort="false">奖励时间</div>
                <div field="jsfz"  width="90px" headerAlign="center" align="center" allowSort="false">技术分支</div>
                <div field="beiZhu"  width="80px" headerAlign="center" align="center" allowSort="false">备注</div>
            </div>
        </div>
    </div>
    <div id="importWindow" title="专利导入窗口" class="mini-window" style="width:750px;height:280px;"
         showModal="true" showFooter="false" allowResize="true">
        <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
        </p>
        <div style="display: inline-block;float: right;">
            <a id="importBtn" class="mini-button" onclick="importProduct()">导入</a>
            <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
        </div>
        <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
            <form id="formImport" method="post">
                <table class="table-detail" cellspacing="1" cellpadding="0">
                    <tr>
                        <td style="width: 30%">上传模板：</td>
                        <td style="width: 70%;">
                            <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">专利台账（模板）.xls</a>
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
    <!--导出Excel相关HTML-->
    <form id="excelForm" action="${ctxPath}/zhgl/core/zlgl/exportZgzlList.do" method="post" target="excelIFrame">
        <input type="hidden" name="filter" id="filter"/>
    </form>
    <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
    <script type="text/javascript">
        mini.parse();
        var zgzlPath="${ctxPath}";
        var zgzlListGrid=mini.get("zgzlListGrid");
        var currentUserId="${currentUserId}";
        var currentUserNo="${currentUserNo}";
        var currentUserRoles=${currentUserRoles};
        var importWindow = mini.get("importWindow");
        var lastUpdateTime = "${lastUpdateTime}";
        var lastSaveTime = mini.get("sysLastUpdateTime").getFormValue();
        // zgzlListGrid.frozenColumns(0, 10);

        function onActionRenderer(e) {
            var record = e.record;
            var zgzlId = record.zgzlId;
            var s = '';
            s+= '<span  title="明细" onclick="zgzlDetail(\'' + zgzlId +'\''+ ')">明细</span>';
            s+= '<span  title="变更产品" onclick="zgzlChange(\'' + zgzlId +'\''+ ')">变更产品</span>';
            if((currentUserId == record.CREATE_BY_) ||(whetherZgzl(currentUserRoles))){
                s+= '<span  title="编辑" onclick="zgzlkEdit(\'' + zgzlId +'\''+ ')">编辑</span>';
            }

            return s;
        }
        zgzlListGrid.on("drawcell",function(e){
            var record=e.record;
            if(record.enumName&&record.enumName.indexOf("驳回")!=-1) {
                e.cellStyle="background-color: coral !important;";
            }
            if(record.enumName&&record.enumName.indexOf("撤回")!=-1) {
                e.cellStyle="background-color: coral !important;";
            }
            if(record.enumName&&record.enumName.indexOf("放弃")!=-1) {
                e.cellStyle="background-color: coral !important;";
            }
            if(record.enumName&&record.enumName.indexOf("暂缓申请")!=-1) {
                e.cellStyle="background-color: coral !important;";
            }
            if(record.enumName&&record.enumName.indexOf("转让")!=-1) {
                e.cellStyle="background-color: grey_40_percent !important;";
            }
        });
    </script>
    <redxun:gridScript gridId="zgzlListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
