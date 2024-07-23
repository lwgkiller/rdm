<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>专项测试验证任务编辑页面</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="testTaskForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="belongSingleId" name="belongSingleId"/>
            <input id="applyId" name="applyId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="testType" name="testType" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    专项测试验证任务编辑页面
                    <span style="color: red;font-size: 15px">(注：验证平台部门填写验证结果)</span>
                </caption>
                <tr>
                    <td style="width: 20%">验证任务编号：<br>(提交后自动生成)</td>
                    <td style="width: 30%;" colspan="3">
                        <input id="testNumber" name="testNumber" class="mini-textbox" style="width:98%;" readonly/>
                    </td>
                </tr>
                <tr>

                    <td style="width: 20%">验证方式：<span style="color:red">*</span>
                        <span style="color: red;font-size: 10px"><br>(如没有对应的验证方式，勾选右侧<br>“没有对应的能力”，选择“验证部门”<br>，填写“建议增加验证能力”)</span>
                    </td>
                    <td style="width: 30%">
                        <input id="verifyAbilityId" name="verifyAbilityId" textname="abilityName"
                               class="mini-buttonedit"
                               showClose="true" allowInput="false"
                               oncloseclick="selectAbilityClose()" onbuttonclick="selectAbilityWindowOpen()"
                               style="width:98%;"/>
                    </td>
                    <td style="width: 20%">没有对应的验证能力：</td>
                    <td style="width: 30%;">
                        <input id="verifyTypeNo" name="verifyTypeNo" class="mini-checkbox"
                               onvaluechanged="noNeedValidType"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%">验证责任部门：<span style="color:red">*</span></td>
                    <td style="width:30%">
                        <input id="respDeptId" name="respDeptId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px" allowinput="false" label="部门" textname="respDeptName"
                               single="true" initlogindep="false"
                               enabled="false"/>
                    </td>
                    <td style="width: 20%">建议增加验证能力：</td>
                    <td style="width: 30%;">
                        <input id="suggestAddVerifyType" name="suggestAddVerifyType"
                               class="mini-textbox"
                               style="width:98%;" enabled="false"/>
                    </td>
                </tr>

                <tr>
                    <td style="width: 20%">验证室主任：<span style="color:red">*</span></td>
                    <td style="width: 30%;">
                        <input id="testRespSZRId" name="testRespSZRId" textname="testRespSZRName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                               enabled="false"
                        />
                    </td>
                    <td style="width: 20%">验证责任人：<span style="color:red">*</span></td>
                    <td style="width: 30%;">
                        <input id="testRespUserId" name="testRespUserId" textname="testRespUserName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                               enabled="false"
                        />
                    </td>
                </tr>

                <tr>
                    <td style="width: 20%">计划开始时间 ：<span style="color:red">*</span></td>
                    <td style="width: 30%">
                        <input id="testPlanStartTime" name="testPlanStartTime" class="mini-datepicker"
                               format="yyyy-MM-dd" allowInput="false"
                               showTime="false" valueType="String" showOkButton="false" showClearButton="true"
                               style="width:98%;" enabled="false"/>
                    </td>
                    <td style="width: 20%">计划结束时间 ：<span style="color:red">*</span></td>
                    <td style="width: 30%">
                        <input id="testPlanEndTime" name="testPlanEndTime" class="mini-datepicker" format="yyyy-MM-dd"
                               allowInput="false"
                               showTime="false" valueType="String" showOkButton="false" showClearButton="true"
                               style="width:98%;"
                               enabled="false"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: left;width: 20%">验证内容：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="testContent" name="testContent" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:125px;line-height:25px;"
                                  label="验证内容" datatype="varchar" length="800" vtype="length:800" minlen="0"
                                  allowinput="true"
                                  emptytext="请输入验证内容..." mwidth="80" wunit="%" mheight="150" hunit="px"
                                  enabled="false"> </textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: left;width: 20%;height:300px">指标及验证数据：<span style="color:red">*</span></td>
                    <td colspan="3" >
                        <div class="mini-toolbar" style="margin-bottom: 5px" id="testTaskListToolBar">
                            <a class="mini-button" id="addTestTaskRow" plain="true" onclick="addTestTaskRow">新增行</a>
                            <a class="mini-button btn-red" id="removeTestTaskRow" plain="true"
                               onclick="removeTestTaskRow">删除</a>
                        </div>
                        <div id="testTaskGrid" class="mini-datagrid"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/drbfm/testTask/testTaskDemandList.do?applyId=${applyId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false"
                             allowAlternating="true"
                             style="height: 85%"
                             allowCellSelect="true"
                             allowCellEdit="true"
                             allowCellWrap="true"
                             oncellbeginedit="OnCellBeginEditStandard">
                            <div property="columns">
                                <div type="checkcolumn" width="50"></div>
                                <div id="relQuotaId" field="quotaName" name="quotaName" width="120"
                                     headerAlign="center" displayField="quotaName"
                                     align="center"
                                >指标名称
                                    <input textname="quotaName"
                                           property="editor" class="mini-buttonedit"
                                           showClose="true"
                                           allowInput="false"
                                           oncloseclick="selectQuotaCloseClick(e)"
                                           onbuttonclick="selectQuota()"
                                           style="width:98%;"/>
                                </div>
                                <div id="sjStandardValue" field="sjStandardValue" name="sjStandardValue" width="100"
                                     headerAlign="center"
                                     align="center"
                                >指标设计值
                                </div>
                                <div id="sjStandardIds" field="sjStandardIds" width="200"
                                     headerAlign="center" displayField="sjStandardNames"
                                     align="center"
                                >设计标准<br><span style="color: red">(指标未锁定状态下，可由验证人员协助完善)</span>
                                    <input
                                           property="editor" class="mini-buttonedit"
                                           showClose="true"
                                           allowInput="false"
                                           oncloseclick="sjStandardCloseClick(e)"
                                           onbuttonclick="selectStandardClick('sjStandard')"
                                           style="width:98%;"/>
                                </div>
                                <div id="testStandardIds" field="testStandardIds"  width="200"
                                     headerAlign="center" displayField="testStandardNames"
                                     align="center"
                                >测试标准<br><span style="color: red">(指标未锁定状态下，可由验证人员协助完善)</span>
                                    <input
                                           property="editor" class="mini-buttonedit"
                                           showClose="true"
                                           allowInput="false"
                                           oncloseclick="csStandardCloseClick(e)"
                                           onbuttonclick="selectStandardClick('csStandard')"
                                           style="width:98%;"/>
                                </div>

                                <div id="evaluateStandardIds" field="evaluateStandardIds"
                                     width="200" displayField="evaluateStandardNames"
                                     headerAlign="center"
                                     align="center"
                                >评价标准<br><span style="color: red">(指标未锁定状态下，可由验证人员协助完善)</span>
                                    <input
                                           property="editor" class="mini-buttonedit"
                                           showClose="true"
                                           allowInput="false"
                                           oncloseclick="pjStandardCloseClick(e)"
                                           onbuttonclick="selectStandardClick('pjStandard')"
                                           style="width:98%;"/>
                                </div>

                                <div id="quotaTestValue" field="quotaTestValue" name="quotaTestValue" width="100"
                                     headerAlign="center"
                                     align="center"
                                >指标实测值
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div id="testResult" field="testResult" name="testResult" width="70"
                                     headerAlign="center"
                                     align="center" renderer="testResultRender"
                                >评判结果
                                    <input property="editor" name="testResult" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false"
                                           allowInput="false" showNullItem="false"
                                           data="[{'key' : '合格','value' : '合格'}
                                       ,{'key' : '不合格','value' : '不合格'}
                                       ]"
                                    />
                                </div>
                                <div field="sjStandardLock" visible="false"></div>
                                <div field="testStandardLock" visible="false"></div>
                                <div field="evaluateStandardLock" visible="false"></div>

                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;height: 200px">验证方案及报告：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <div class="mini-toolbar" style="margin-bottom: 5px" id="fileListToolBar">
                            <a id="addFile" class="mini-button" onclick="addTestTaskFile()">添加附件</a>
                            <span style="color: red">注：添加附件前，请先保存草稿</span>
                        </div>
                        <%--<div style="margin-top: 10px;margin-bottom: 2px">--%>
                        <%--</div>--%>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id"
                        <%--url="${ctxPath}/devTask/core/getDevFileList.do?applyId=${applyId}"--%>
                             url="${ctxPath}/drbfm/testTask/demandList.do?applyId=${applyId}"
                             autoload="true"
                             showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div field="fileType" width="140" headerAlign="center" align="center"
                                     renderer="fileTypeRenderer">附件类型
                                </div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="creator" width="70" headerAlign="center" align="center">创建人</div>
                                <div field="CREATE_TIME_" width="90" headerAlign="center" align="center"
                                     dateFormat="yyyy-MM-dd HH:mm:ss">创建时间
                                </div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRenderer">
                                    操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: left;width: 20%;height:200px">下一步工作：</td>
                    <td colspan="3" height="80px">
                        <div class="mini-toolbar" style="margin-bottom: 5px" id="nextWorkListToolBar">
                            <a class="mini-button" plain="true" style="float: left"
                               onclick="addNextWork()">添加</a>
                            <a class="mini-button btn-red" plain="true" style="float: left"
                               onclick="removeNextWork()">删除</a>
                        </div>
                        <div id="nextWorkListGrid" class="mini-datagrid" allowResize="false" style="height: 85%"
                             idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                             allowCellWrap="true" showVGridLines="true" autoload="true"
                             url="${ctxPath}/drbfm/single/queryNextWork.do?testTaskId=${applyId}"
                        >
                            <div property="columns">
                                <div type="checkcolumn" width="60px"></div>
                                <div name="action" cellCls="actionIcons" width="100px" headerAlign="center" align="center"
                                     renderer="nextWorkActionRenderer" cellStyle="padding:0;">操作</div>
                                <div field="nextWorkContent" headerAlign="center" width="500px" align="left">下一步工作
                                </div>
                                <div field="finishFlag" headerAlign="center" width="350px" align="left">完成标志
                                </div>
                                <div field="finishTime" headerAlign="center" width="150px" align="center">完成时间
                                </div>
                                <div field="respDeptNames" headerAlign="center" width="200px" align="center">责任部门
                                </div>
                                <div field="creator" width="70" headerAlign="center" align="center" >创建人</div>
                                <div field="CREATE_TIME_" width="100" headerAlign="center" dateFormat="yyyy-MM-dd" align="center">创建时间</div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<div id="selectQuotaWindow" title="选择指标" class="mini-window" style="width:1340px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <%--<li style="margin-left:15px;margin-right: 15px"><span class="text"--%>
                    <%--style="width:auto">编号: </span><input--%>
                    <%--class="mini-textbox" id="standardNumber" onenter="searchStandard"></li>--%>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">指标名称: </span><input
                            class="mini-textbox" id="quotaName" onenter="searchQuota"></li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">启用状态: </span>
                        <input id="validStatus" name="validStatus" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..." value="有效"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : '有效','value' : '有效'},{'key' : '作废','value' : '作废'}]"/>
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="searchQuota()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                           onclick="clearSearchQuota()">清空查询</a>
                        <div style="display: inline-block" class="separator"></div>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit">
        <div id="quotaListGrid" class="mini-datagrid" allowResize="false" style="height: 100%"
             idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
             multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false"
             allowCellWrap="true" showVGridLines="true">
            <div property="columns">
                <div type="checkcolumn" width="60px"></div>
                <div field="quotaName" align="center" headerAlign="center" width="150px" align="left">指标名称
                </div>
                <div field="sjStandardValue" align="center" headerAlign="center" width="90px" align="left">设计标准值
                </div>
                <div field="requestDesc" headerAlign="center" width="300px" align="left">关联的要求描述
                </div>
                <div field="sjStandardNames" headerAlign="center" width="230px" align="left">设计标准
                </div>
                <div field="testStandardNames" headerAlign="center" width="230px" align="left">测试标准
                </div>
                <div field="evaluateStandardNames" headerAlign="center" width="230px" align="left">评价标准
                </div>
                <div field="validStatus" align="center" headerAlign="center" width="70px" align="left" renderer="validStatusRenderer">启用状态
                </div>
                <div field="replaceQuotaName" align="center" headerAlign="center" width="150px" align="left">代替的旧指标
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectQuotaOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectQuotaHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="selectAbilityWindow" title="选择验证能力" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px">
        <li style="float: left">
            <span class="text" style="width:auto">验证能力: </span>
            <input id="abilityNameFilter" name="abilityName" class="mini-textbox" style="width:140px;"/>
        </li>
        <li style="float: left">
            <span class="text" style="width:auto">归口部门: </span>
            <input id="respDeptIdFilter" name="respDeptId" textname="respDeptName" class="mini-dep rxc"
                   plugins="mini-dep"
                   style="width:150px;height:34px" allowinput="false" length="500" maxlength="500" minlen="0"
                   single="true" initlogindep="false"/>
        </li>
        <li style="float: left">
            <span class="text" style="width:auto">当前是否具备: </span>
            <input id="currentOkFilter" name="currentOk" class="mini-combobox" style="width:90px;margin-right: 5px"
                   textField="value" valueField="key" emptyText="请选择..."
                   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                   data="[ {'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
            />
        </li>
        <li>
            <a class="mini-button" iconCls="icon-reload" onclick="searchAbility()" plain="true">查询</a>
            <a class="mini-button btn-red" style="margin-left:5px;" plain="true" onclick="clearAbility()">清空查询</a>
        </li>
    </div>
    <div class="mini-fit">
        <div id="abilityListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="false"
             allowAlternating="true" pagerButtons="#pagerButtons" onrowdblclick="onRowDblClick"
             url="${ctxPath}/drbfm/ability/getAbilityList.do" autoload="true"
        >
            <div property="columns">
                <div type="checkcolumn" width="30px"></div>
                <div field="abilityName" align="center" width="150px" headerAlign="center">验证能力名称</div>
                <div field="respDeptName" width="100px" headerAlign="center" align="center">归口部门</div>
                <div field="currentOk" width="80px" headerAlign="center" align="center">当前能力是否具备</div>
                <div field="remark" width="100px" headerAlign="center" align="center">备注说明</div>
                <div field="creator" width="50px" headerAlign="center" align="center">创建人</div>
                <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="70px"
                     headerAlign="center">创建时间
                </div>
                <div field="updator" width="50px" headerAlign="center" align="center">更新人</div>
                <div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="70px"
                     headerAlign="center">更新时间
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectAbilityOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectAbilityHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<div id="selectStandardWindow" title="选择标准" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input id="standardType" name="standardType" class="mini-hidden"/>
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">体系类别: </span>
        <input class="mini-combobox" width="130" id="filterSystemCategory" style="margin-right: 15px" textField="text"
               valueField="id" emptyText="请选择..." value="JS"
               data="[{id:'JS',text:'技术标准'},{id:'GL',text:'管理标准'}]"/>
        <span style="font-size: 14px;color: #777">编号: </span>
        <input class="mini-textbox" width="130" id="filterStandardNumberId" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">名称: </span>
        <input class="mini-textbox" width="130" id="filterStandardNameId" style="margin-right: 15px"/>
        <a class="mini-button" plain="true" onclick="searchStandardList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="onRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="true"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/standardManager/core/standard/queryList.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                     allowSort="true">标准类别
                </div>
                <div field="standardNumber" sortField="standardNumber" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true">编号
                </div>
                <div field="standardName" sortField="standardName" width="180" headerAlign="center" align="center"
                     allowSort="true">名称
                </div>
                <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center" align="center"
                     allowSort="true" renderer="standardStatusRenderer">状态
                </div>
                <div field="publisherName" sortField="publisherName" align="center"
                     width="60" headerAlign="center" allowSort="true">起草人
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectOutOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectOutHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<div id="nextWorkWindow" title="下一步工作" class="mini-window" style="width:750px;height:400px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar" style="float: right;">
            <div style="position: relative!important;">
                <a id="saveNextWorkBtn"class="mini-button" onclick="saveNextWork()">保存</a>
                <a id="closeNextWorkBtn" class="mini-button btn-red" onclick="closeNextWork()">关闭</a>
            </div>
        </div>
        <input id="nextWorkId" name="id" class="mini-hidden"/>
        <table class="table-detail"  cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="width: 12%">下一步工作：<span style="color:red">*</span></td>
                <td style="width: 38%;">
                    <input id="nextWorkContent" name="nextWorkContent" class="mini-textarea rxc"
                           plugins="mini-textarea" style="width:95%;;height:90px;line-height:25px;" label="下一步工作"
                           datatype="varchar" length="500"
                           vtype="length:500" minlen="0" allowinput="true" emptytext="请输入下一步工作..." />
                </td>
            </tr>
            <tr>
                <td style="width: 12%">完成标志：</td>
                <td style="width: 38%;">
                    <input id="finishFlag" name="finishFlag" class="mini-textarea rxc"
                           plugins="mini-textarea" style="width:95%;;height:90px;line-height:25px;" label="完成标志"
                           datatype="varchar" length="500"
                           vtype="length:500" minlen="0" allowinput="true" emptytext="请输入完成标志..." />
                </td>
            </tr>
            <tr>
                <td style="width: 12%">完成时间：</td>
                <td style="width: 38%;">
                    <input id="finishTime" name="finishTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                           showTime="false" showOkButton="false" showClearButton="true" style="width:95%;"/>
                </td>
            </tr>
            <tr>
                <td style="width: 12%">责任部门：<span style="color:red">*</span></td>
                <td style="width: 38%;">
                    <input id="respDeptIds" name="respDeptIds" textname="respDeptNames" class="mini-dep rxc" plugins="mini-dep"
                           style="width:95%;height:34px" allowinput="false" length="500" maxlength="500" minlen="0"
                           single="false" initlogindep="false"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("fileListGrid");
    var standardListGrid = mini.get("standardListGrid");
    var testTaskGrid = mini.get("testTaskGrid");
    var quotaListGrid = mini.get("quotaListGrid");
    var testTaskForm = new mini.Form("#testTaskForm");
    var selectQuotaWindow = mini.get("selectQuotaWindow");
    var standardType = "";

    var stageName = "";
    var singleId = "${singleId}";
    var applyId = "${applyId}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var currentDeptId = "${currentDeptId}";
    var currentDeptName = "${currentDeptName}";

    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var selectAbilityWindow = mini.get("selectAbilityWindow");
    var abilityListGrid = mini.get("abilityListGrid");

    var selectStandardWindow = mini.get("selectStandardWindow");
    var standardListGrid = mini.get("standardListGrid");

    var nextWorkListGrid=mini.get("nextWorkListGrid");
    var nextWorkWindow = mini.get("nextWorkWindow");
    var testType="${testType}";

    $(function () {
        if(!applyId) {
            mini.get("CREATE_BY_").setValue(currentUserId);
            mini.get("testType").setValue(testType);
            mini.get("nextWorkListToolBar").hide();
        } else {
            var url = jsUseCtxPath + "/drbfm/testTask/getJson.do";
            $.post(
                url,
                {id: applyId},
                function (json) {
                    testTaskForm.setData(json);

                    if (action == 'detail') {
                        testTaskForm.setEnabled(false);
                        mini.get("testTaskListToolBar").hide();
                        mini.get("fileListToolBar").hide();
                        mini.get("nextWorkListToolBar").hide();
                        testTaskGrid.setAllowCellEdit(false);
                        $("#detailToolBar").show();
                        if (status != 'DRAFTED') {
                            $("#processInfo").show();
                        }
                    } else if (action == 'task') {
                        taskActionProcess();
                    } else if (action == "edit") {
                        mini.get("nextWorkListToolBar").hide();
                        var verifyTypeNo = mini.get("verifyTypeNo").getValue();
                        if (verifyTypeNo == 'true') {
                            mini.get("respDeptId").setEnabled(true);
                            mini.get("suggestAddVerifyType").setEnabled(true);
                            mini.get("verifyAbilityId").setEnabled(false);
                        } else if (verifyTypeNo == 'false') {
                            mini.get("verifyAbilityId").setEnabled(true);
                            mini.get("respDeptId").setEnabled(false);
                            mini.get("suggestAddVerifyType").setEnabled(false);
                            mini.get("suggestAddVerifyType").setValue("");
                        }
                    }
                });
        }
    });


    function OnCellBeginEditStandard(e) {
        var field = e.field;
        // 非申请编制阶段，将部分字段设不可编辑
        if (action == "task") {
            if (stageName == "start") {
                if (field == "quotaName") {
                    e.editor.setEnabled(true);
                } else {
                    e.editor.setEnabled(false);
                }
            }
            if (stageName == "execute") {
                if (field == "quotaName") {
                    e.editor.setEnabled(false);
                } else {
                    var record = e.record, field = e.field;
                    var sjStandardLock = record.sjStandardLock;
                    var testStandardLock = record.testStandardLock;
                    var evaluateStandardLock = record.evaluateStandardLock;
                    if (field == "sjStandardIds" ) {
                        if(sjStandardLock == "true") {
                            mini.alert("标准已由指标创建人锁定，不能修改！");
                            e.editor.setEnabled(false);
                        } else {
                            e.editor.setEnabled(true);
                        }
                    }
                    if (field == "testStandardIds" ) {
                        if(testStandardLock == "true") {
                            mini.alert("标准已由指标创建人锁定，不能修改！");
                            e.editor.setEnabled(false);
                        } else {
                            e.editor.setEnabled(true);
                        }
                    }
                    if (field == "evaluateStandardIds" ) {
                        if(evaluateStandardLock == "true") {
                            mini.alert("标准已由指标创建人锁定，不能修改！");
                            e.editor.setEnabled(false);
                        } else {
                            e.editor.setEnabled(true);
                        }
                    }
                }
            }

        } else if (action == 'edit') {
            if (field == "quotaName") {
                e.editor.setEnabled(true);
            } else {
                e.editor.setEnabled(false);
            }
        }
    }

    function getData() {
        if (singleId) {
            mini.get("belongSingleId").setValue(singleId);
        }
        var formData = _GetFormJsonMini("testTaskForm");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_testTaskGrid) {
            delete formData.SUB_testTaskGrid;
        }
        if (testTaskGrid.getChanges().length > 0) {
            formData.changeDemandGrid = testTaskGrid.getChanges();
        }
        // if (topicGrid.getChanges().length > 0) {
        //     formData.changeTopicGrid = topicGrid.getChanges();
        // }

        return formData;

    }

    //保存草稿
    function saveDraft(e) {
        var demandGridData = testTaskGrid.getData();
        if (demandGridData.length > 0) {
            for (var i = 0; i < demandGridData.length; i++) {
                if (demandGridData[i].relQuotaId == undefined || demandGridData[i].relQuotaId == "") {
                    mini.alert("请填写指标名称");
                }
            }
        }

        window.parent.saveDraft(e);
    }

    // 指标子表
    function addTestTaskRow() {
        var row = {};
        testTaskGrid.addRow(row);
    }

    function removeTestTaskRow() {
        var selecteds = testTaskGrid.getSelecteds();
        testTaskGrid.removeRows(selecteds);
    }

    //指标引用
    function selectQuota() {
        selectQuotaWindow.show();
        if (!singleId) {
            singleId = mini.get("belongSingleId").getValue();
        }
        var url = "${ctxPath}/drbfm/single/getQuotaList.do?belongSingleId=" + singleId;
        quotaListGrid.setUrl(url);
        searchQuota();
    }

    //试验方案及报告上传
    function addTestTaskFile() {

        if (!applyId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        var fileType = "scheme";

        if (stageName == "execute") {
            fileType = "report"
        }
        if (!singleId) {
            singleId = mini.get("belongSingleId").getValue();
        }

        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/drbfm/testTask/openUploadWindow.do?applyId=" + applyId + "&belongSingleId" + singleId + "&fileType=" + fileType,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                projectParams.applyId = applyId;
                projectParams.belongSingleId = singleId;
                projectParams.fileType = fileType;
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                fileListGrid.load();
            }
        });
    }

    function operationRenderer(e) {
        var record = e.record;
        if (!record.id) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, applyId, coverContent);
        var downloadUrl = '/drbfm/testTask/fileDownload.do';
        if (record) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + applyId + '\',\'' + downloadUrl + '\')">下载</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span  title="下载" style="color: silver" >下载</span>';
        }
        if (record && record.CREATE_BY_ == currentUserId
            && (action == "edit" || action == "task" && (stageName == "execute" || stageName == "start"))) {
            var deleteUrl = "/drbfm/testTask/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + applyId + '\',\'' + deleteUrl + '\')">删除</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span  title="删除" style="color: silver" > 删除 </span>';
        }
        return cellHtml;
    }


    //发起流程
    function startProcess(e) {
        var formValid = validTestTask();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    function validTestTask() {
        var verifyAbilityId = $.trim(mini.get("verifyAbilityId").getValue());
        var verifyTypeNo = mini.get("verifyTypeNo").getValue();

        if (!verifyAbilityId && verifyTypeNo == 'false') {
            return {"result": false, "message": "请选择验证方式"};
        }

        var respDeptId = $.trim(mini.get("respDeptId").getValue());
        if (!respDeptId) {
            return {"result": false, "message": "请选择验证责任部门"};
        }

        var suggestAddVerifyType = $.trim(mini.get("suggestAddVerifyType").getValue());
        if (!suggestAddVerifyType && verifyTypeNo == 'true') {
            return {"result": false, "message": "请填写“建议增加验证能力”"};
        }

        var demandGridData = testTaskGrid.getData();
        if (demandGridData.length == 0) {
            mini.alert("请填写指标及验证数据信息");
            return;
        }
        for (var i = 0; i < demandGridData.length; i++) {
            if (demandGridData[i].relQuotaId == undefined || demandGridData[i].relQuotaId == "") {
                return {"result": false, "message": "请填写指标名称"};
            }
        }
        var fileGridData = fileListGrid.getData();
        if (fileGridData.length == 0) {
            mini.alert("请上传验证方案");
            return;
        }
        var yzfaCnt = 0;
        for (var i = 0; i < fileGridData.length; i++) {
            if (fileGridData[i].fileType == "scheme") {
                yzfaCnt += 1;
            }
        }
        if (yzfaCnt == 0) {
            mini.alert("请上传验证方案");
            return
        }


        return {"result": true};

    }

    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName != 'start') {
            mini.get("verifyAbilityId").setEnabled(false);
            mini.get("verifyTypeNo").setEnabled(false);
            mini.get("respDeptId").setEnabled(false);
            mini.get("suggestAddVerifyType").setEnabled(false);
            mini.get("testTaskListToolBar").hide();
            mini.get("fileListToolBar").hide();
            testTaskGrid.setAllowCellEdit(false);
        }
        if (stageName == 'check') {
            mini.get("testRespSZRId").setEnabled(true);
        }
        if (stageName == 'szrProcess') {
            mini.get("testRespUserId").setEnabled(true);
            var verifyTypeNo = mini.get("verifyTypeNo").getValue();
            if (verifyTypeNo == 'true') {
                mini.get("verifyAbilityId").setEnabled(true);
            }
        }
        if (stageName == 'execute') {
            mini.get("testPlanStartTime").setEnabled(true);
            mini.get("testPlanEndTime").setEnabled(true);
            mini.get("testContent").setEnabled(true);
            mini.get("fileListToolBar").show();
            testTaskGrid.setAllowCellEdit(true);
        }
        if (stageName != 'execute') {
            mini.get("nextWorkListToolBar").hide();
        }

    }

    function fileTypeRenderer(e) {
        var record = e.record;
        var fileType = record.fileType;

        var arr = [{'key': 'scheme', 'value': '试验方案'},
            {'key': 'report', 'value': '试验报告',}
        ];

        return $.formatItemValue(arr, fileType);
    }

    function projectApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start') {
            var formValid = validTestTask();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        } else {
            var formValid = validNext();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }

        //检查通过
        window.parent.approve();
    }

    function validNext() {
        if (stageName == "check") {
            var testRespSZRId = $.trim(mini.get("testRespSZRId").getValue());
            if (!testRespSZRId) {
                return {"result": false, "message": "请选择验证室主任"};
            }
        }
        if (stageName == "szrProcess") {
            var verifyAbilityId = $.trim(mini.get("verifyAbilityId").getValue());
            if (!verifyAbilityId) {
                return {"result": false, "message": "验证方式为空，请在“验证能力清单列表”页面中根据提报人“建议增加验证能力”创建对应能力项，然后补充此页面的“验证方式”"};
            }
            var testRespUserId = $.trim(mini.get("testRespUserId").getValue());
            if (!testRespUserId) {
                return {"result": false, "message": "请选择验证责任人"};
            }
        }

        if (stageName == "execute") {
            var testPlanStartTime = $.trim(mini.get("testPlanStartTime").getValue());
            if (!testPlanStartTime) {
                return {"result": false, "message": "请选择计划开始时间 "};
            }
            var testPlanEndTime = $.trim(mini.get("testPlanEndTime").getValue());
            if (!testPlanEndTime) {
                return {"result": false, "message": "请选择计划结束时间 "};
            }
            var testContent = $.trim(mini.get("testContent").getValue());
            if (!testContent) {
                return {"result": false, "message": "请填写验证内容"};
            }

            var demandGridData = testTaskGrid.getData();
            for (var i = 0; i < demandGridData.length; i++) {
                if (demandGridData[i].quotaTestValue == undefined || demandGridData[i].quotaTestValue == "") {
                    return {"result": false, "message": "请填写指标实测值"};
                }
                if (demandGridData[i].testResult == undefined || demandGridData[i].testResult == "") {
                    return {"result": false, "message": "请填写评判结果"};
                }
            }
            var fileGridData = fileListGrid.getData();
            var reportCnt = 0;
            for (var i = 0; i < fileGridData.length; i++) {
                if (fileGridData[i].fileType == "report") {
                    reportCnt += 1;
                }
            }
            if (reportCnt == 0) {
                mini.alert("请上传试验报告");
                return
            }

        }
        return {"result": true};
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {

        var s = '';
        if (fileName == "") {
            return s;
        }
        var fileType = getFileType(fileName);

        if (fileType == 'other') {
            s = '&nbsp;&nbsp;&nbsp;<span  title="预览" style="color: silver" >预览</span>';
        } else {
            var url = '/drbfm/testTask/preview.do?fileType=' + fileType;
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">预览</span>';
        }
        return s;
    }

    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }

    function searchQuota() {
        var queryParam = [];
        //其他筛选条件

        var quotaName = $.trim(mini.get("quotaName").getValue());
        if (quotaName) {
            queryParam.push({name: "quotaName", value: quotaName});
        }
        var validStatus = $.trim(mini.get("validStatus").getValue());
        if (validStatus) {
            queryParam.push({name: "validStatus", value: validStatus});
        }
        // queryParam.push({name: "instStatus", value: "RUNNING"});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = quotaListGrid.getPageIndex();
        data.pageSize = quotaListGrid.getPageSize();
        data.sortField = quotaListGrid.getSortField();
        data.sortOrder = quotaListGrid.getSortOrder();
        //查询
        quotaListGrid.load(data);
    }


    function clearSearchQuota() {
        mini.get("quotaName").setValue("");
        searchQuota();
    }

    function selectQuotaOK() {
        var selectRow = quotaListGrid.getSelected();
        //判断指标是否已选择过
        var existData = testTaskGrid.getData();
        if(existData.length>0) {
            for(var index = 0;index<existData.length;index++) {
                if(existData[index].relQuotaId==selectRow.id) {
                    mini.alert("指标当前已选择过，不能重复添加！");
                    return;
                }
            }
        }
        var objrow = testTaskGrid.getSelected();
        testTaskGrid.updateRow(objrow,
            {
                relQuotaId: selectRow.id,
                quotaName: selectRow.quotaName,
                sjStandardValue: selectRow.sjStandardValue,
                sjStandardIds: selectRow.sjStandardIds,
                testStandardIds: selectRow.testStandardIds,
                evaluateStandardIds: selectRow.evaluateStandardIds,
                sjStandardNames: selectRow.sjStandardNames,
                testStandardNames: selectRow.testStandardNames,
                evaluateStandardNames: selectRow.evaluateStandardNames,
                sjStandardLock: selectRow.sjStandardLock,
                testStandardLock: selectRow.testStandardLock,
                evaluateStandardLock: selectRow.evaluateStandardLock

            });

        selectQuotaHide();

    }

    function selectQuotaHide() {
        selectQuotaWindow.hide();
        quotaListGrid.deselectAll();
    }

    function selectQuotaCloseClick(e) {
        var obj=e.sender;
        obj.setValue("");
        obj.setText("");
        var objrow = testTaskGrid.getSelected();
        testTaskGrid.updateRow(objrow,
            {
                relQuotaId: "",
                quotaName: "",
                sjStandardValue: "",
                sjStandardIds: "",
                sjStandardNames: "",
                testStandardIds: "",
                testStandardNames: "",
                evaluateStandardIds: "",
                evaluateStandardNames: ""
            });
    }

    function selectAbilityWindowOpen() {
        selectAbilityWindow.show();
        searchAbility();
        abilityListGrid.deselectAll();
    }

    function selectAbilityClose() {
        mini.get("verifyAbilityId").setValue("");
        mini.get("verifyAbilityId").setText("");
        mini.get("respDeptId").setValue("");
        mini.get("respDeptId").setText("");
    }

    function selectAbilityOK() {
        var selectRow = abilityListGrid.getSelected();
        if (selectRow) {
            mini.get("verifyAbilityId").setValue(selectRow.id);
            mini.get("verifyAbilityId").setText(selectRow.abilityName);
            mini.get("respDeptId").setValue(selectRow.respDeptId);
            mini.get("respDeptId").setText(selectRow.respDeptName);
            selectAbilityHide();
        } else {
            mini.alert("请选择一条数据！");
        }
    }

    function selectAbilityHide() {
        selectAbilityWindow.hide();
        mini.get("abilityNameFilter").setValue("");
        mini.get("respDeptIdFilter").setValue("");
        mini.get("respDeptIdFilter").setText("");
        mini.get("currentOkFilter").setValue("");
    }


    function onRowDblClick() {
        selectAbilityOK();
    }

    function searchAbility() {
        var params = [];
        var abilityNameFilter = mini.get("abilityNameFilter").getValue();
        if (abilityNameFilter) {
            params.push({name: 'abilityName', value: abilityNameFilter});
        }
        var respDeptIdFilter = mini.get("respDeptIdFilter").getValue();
        if (respDeptIdFilter) {
            params.push({name: 'respDeptId', value: respDeptIdFilter});
        }
        var currentOkFilter = mini.get("currentOkFilter").getValue();
        if (currentOkFilter) {
            params.push({name: 'currentOk', value: currentOkFilter});
        }
        var data = {};
        data.filter = mini.encode(params);
        abilityListGrid.load(data);
    }

    function clearAbility() {
        mini.get("abilityNameFilter").setValue("");
        mini.get("respDeptIdFilter").setValue("");
        mini.get("respDeptIdFilter").setText("");
        mini.get("currentOkFilter").setValue("");
        abilityListGrid.load();
    }

    function noNeedValidType() {
        var verifyTypeNo = mini.get("verifyTypeNo").getValue();
        if (verifyTypeNo == 'true') {
            mini.get("respDeptId").setEnabled(true);
            mini.get("suggestAddVerifyType").setEnabled(true);
            mini.get("verifyAbilityId").setEnabled(false);
            mini.get("verifyAbilityId").setValue("");
            mini.get("verifyAbilityId").setText("");
        } else if (verifyTypeNo == 'false') {
            mini.get("verifyAbilityId").setEnabled(true);
            mini.get("respDeptId").setEnabled(false);
            mini.get("suggestAddVerifyType").setEnabled(false);
            mini.get("suggestAddVerifyType").setValue("");
        }
    }

    abilityListGrid.on("drawcell", function (e) {
        var record = e.record;

        if (record.currentOk && record.currentOk == '否') {
            e.cellStyle += "background-color: #a59d9da6;";
        }
    });

    function sjStandardCloseClick(e) {
        var obj=e.sender;
        obj.setValue("");
        obj.setText("");
        var objrow = testTaskGrid.getSelected();
        testTaskGrid.updateRow(objrow,
            {
                sjStandardIds: "",
                sjStandardNames: "",
            });
    }

    function csStandardCloseClick(e) {
        var obj=e.sender;
        obj.setValue("");
        obj.setText("");
        var objrow = testTaskGrid.getSelected();
        testTaskGrid.updateRow(objrow,
            {
                testStandardIds: "",
                testStandardNames: "",
            });
    }

    function pjStandardCloseClick(e) {
        var obj=e.sender;
        obj.setValue("");
        obj.setText("");
        var objrow = testTaskGrid.getSelected();
        testTaskGrid.updateRow(objrow,
            {
                evaluateStandardIds: "",
                evaluateStandardNames: "",
            });
    }

    //标准引用
    function selectStandardClick(standardType) {
        selectStandardWindow.show();
        mini.get("standardType").setValue(standardType);
        searchStandardList();
    }

    //查询标准
    function searchStandardList() {
        var queryParam = [];
        var systemCategoryId = $.trim(mini.get("filterSystemCategory").getValue());
        if (systemCategoryId) {
            queryParam.push({name: "systemCategoryId", value: systemCategoryId});
        }
        var standardNumber = $.trim(mini.get("filterStandardNumberId").getValue());
        if (standardNumber) {
            queryParam.push({name: "standardNumber", value: standardNumber});
        }
        var standardName = $.trim(mini.get("filterStandardNameId").getValue());
        if (standardName) {
            queryParam.push({name: "standardName", value: standardName});
        }
        //默认搜索有效的
        queryParam.push({name: "standardStatus", value: "enable"});
        var inputList = '';
        inputList = standardListGrid;
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = inputList.getPageIndex();
        data.pageSize = inputList.getPageSize();
        data.sortField = inputList.getSortField();
        data.sortOrder = inputList.getSortOrder();
        //查询
        inputList.load(data);
    }

    function onRowDblClick() {
        selectOutOK();
    }

    function selectOutOK() {
        var standardType = mini.get("standardType").getValue();
        var inputList = '';
        inputList = standardListGrid;
        // 这里要改成多选，只返回id，用逗号拼接
        var selectRows = inputList.getSelecteds();
        if (selectRows) {
            var ids = [];
            var names = [];
            for (var i = 0; i < selectRows.length; i++) {
                var row = selectRows[i];
                ids.push(row.id);
                names.push(row.standardName);
            }
            var idsStr = ids.join(',');
            var namesStr = names.join(',');
            if (standardType == "sjStandard") {
                var objrow = testTaskGrid.getSelected();
                testTaskGrid.updateRow(objrow,
                    {
                        sjStandardIds: idsStr,
                        sjStandardNames: namesStr
                    });
            } else if (standardType == "csStandard") {
                var objrow = testTaskGrid.getSelected();
                testTaskGrid.updateRow(objrow,
                    {
                        testStandardIds: idsStr,
                        testStandardNames: namesStr
                    });
            } else if (standardType == "pjStandard") {
                var objrow = testTaskGrid.getSelected();
                testTaskGrid.updateRow(objrow,
                    {
                        evaluateStandardIds: idsStr,
                        evaluateStandardNames: namesStr
                    });
            } else {
                mini.alert("标准选择错误！");
                return;
            }


        } else {
            mini.alert("请选择一条数据！");
            return;
        }
        selectOutHide();
    }

    function selectOutHide() {
        selectStandardWindow.hide();
        mini.get("standardType").setValue('');
        mini.get("filterSystemCategory").setValue('JS');
        mini.get("filterStandardNumberId").setValue('');
        mini.get("filterStandardNameId").setValue('');
        standardListGrid.deselectAll(true);
    }

    function standardStatusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function testResultRender(e) {
        var record = e.record;
        var testResult = record.testResult;
        if(!testResult) {
            return "";
        }
        if (testResult=='合格') {
            return '<span style="color:green">'+testResult+'</span>'
        }  else if (testResult=='不合格') {
            return '<span style="color:red">'+testResult+'</span>'
        }
    }

    function validStatusRenderer(e) {
        var record = e.record;
        var validStatus = record.validStatus;
        if(!validStatus) {
            return "";
        }
        if (validStatus=='有效') {
            return '<span style="color:green">有效</span>'
        }  else if (validStatus=='作废') {
            return '<span style="color:red">作废</span>'
        }
    }

    function nextWorkActionRenderer(e) {
        var record = e.record;
        var s = '';
        if(action!='task' || stageName !='execute') {
            s+='<span   style="color: silver" >编辑</span>';
            return s;
        }
        s+='<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editNextWork(' + JSON.stringify(record).replace(/"/g, '&quot;')+ ')">编辑</span>';
        return s;
    }

    function addNextWork() {
        nextWorkWindow.show();
        mini.get("respDeptIds").setValue(currentDeptId);
        mini.get("respDeptIds").setText(currentDeptName);
        mini.get("respDeptIds").setEnabled(false);
    }

    function editNextWork(record) {
        nextWorkWindow.show();
        mini.get("nextWorkId").setValue(record.id);
        mini.get("nextWorkContent").setValue(record.nextWorkContent);
        mini.get("respDeptIds").setValue(record.respDeptIds);
        mini.get("respDeptIds").setText(record.respDeptNames);
        mini.get("finishFlag").setValue(record.finishFlag);
        mini.get("finishTime").setValue(record.finishTime);
        mini.get("respDeptIds").setEnabled(false);
    }

    function saveNextWork() {
        var formData = {};
        formData.id= mini.get("nextWorkId").getValue();
        if (!singleId) {
            singleId = mini.get("belongSingleId").getValue();
        }
        formData.belongSingleId=singleId;
        formData.nextWorkContent=mini.get("nextWorkContent").getValue();
        formData.respDeptIds=mini.get("respDeptIds").getValue();
        formData.respDeptNames=mini.get("respDeptIds").getText();
        formData.finishFlag=mini.get("finishFlag").getValue();
        formData.finishTime=mini.get("finishTime").getText();
        formData.relTestTaskId=applyId;

        if(!formData.nextWorkContent) {
            mini.alert("请填写下一步工作！");
            return;
        }
        if(!formData.respDeptIds) {
            mini.alert("请选择责任部门！");
            return;
        }
        var json = mini.encode(formData);
        $.ajax({
            url: jsUseCtxPath + "/drbfm/single/saveNextWork.do",
            type: 'post',
            async: false,
            data: json,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    if (data.success) {
                        closeNextWork();
                    } else {
                        mini.alert("数据保存失败，" + data.message);
                    }
                }
            }
        });
    }

    function closeNextWork() {
        nextWorkWindow.hide();
        mini.get("nextWorkId").setValue("");
        mini.get("nextWorkContent").setValue("");
        mini.get("respDeptIds").setValue("");
        mini.get("respDeptIds").setText("");
        mini.get("finishFlag").setValue("");
        mini.get("finishTime").setValue("");
        nextWorkListGrid.reload();
    }

    function removeNextWork() {
        var rows = nextWorkListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }

                _SubmitJson({
                    url: jsUseCtxPath + "/drbfm/single/deleteNextWork.do",
                    method: 'POST',
                    showMsg:false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            nextWorkListGrid.reload();
                        }
                    }
                });
            }
        });
    }
</script>
</body>
</html>
