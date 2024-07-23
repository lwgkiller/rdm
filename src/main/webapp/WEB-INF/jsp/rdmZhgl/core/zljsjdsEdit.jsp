<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>专利申请编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/zljsjdsEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
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
<div id="changeToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveChange" class="mini-button" onclick="saveJsjdsInProcess()">保存变更</a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formZljsjds" method="post">
            <input id="jsjdsId" name="jsjdsId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>

            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    发明/实用新型/外观设计专利申请
                </caption>
                <tr>
                    <td style="width: 20%">专业类别：</td>
                    <td style="width: 30%;">
                        <input id="zyType" name="zyType" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="请选择..."
                               data="[
                            {'key' : '液压','value' : '液压'},{'key' : '覆盖件','value' : '覆盖件'}
                           ,{'key' : '动力','value' : '动力'},{'key' : '底盘','value' : '底盘'}
                           ,{'key' : '控制','value' : '控制'},{'key' : '转台','value' : '转台'}
                           ,{'key' : '仿真','value' : '仿真'},{'key' : '工作装置','value' : '工作装置'}
                           ,{'key' : '电气','value' : '电气'},{'key' : '附属机具','value' : '附属机具'}
                           ,{'key' : '电动化','value' : '电动化'},{'key' : '零部件测试','value' : '零部件测试'}
                           ,{'key' : '整机测试','value' : '整机测试'},{'key' : '信息化','value' : '信息化'}
                           ,{'key' : '工艺','value' : '工艺'},{'key' : '其他','value' : '其他'}]"
                        />
                    </td>
                    <td style="width: 17%">技术交底书编号(提交后自动生成)：</td>
                    <td style="width: 30%">
                        <input id="jdsNum" name="jdsNum" readonly class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 17%">提案名称：</td>
                    <td style="width: 30%">
                        <input id="zlName" name="zlName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">专利类型：</td>
                    <td style="width: 30%;" id="ifbianjizl">
                        <input id="zllx" name="zllx" class="mini-radiobuttonlist" style="width:98%;"
                               onvaluechanged="ifycFun()"
                               data="[{id:'FM',text:'发明'},{id:'SYXX',text:'实用新型'},{id:'WGSJ',text:'外观设计'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">是否为国际申请：</td>
                    <td style="width: 30%">
                        <input id="ifgj" name="ifgj" class="mini-combobox" style="width:98%;" onvaluechanged="ifycFun()"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'yes','value' : '是'},{'key' : 'no','value' : '否'}]"/>
                    </td>
                    <td style="width: 14%">发明(设计)人：</td>
                    <td style="width: 30%;">
                        <input id="fmsjr" name="fmsjr" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr id="ifgjxs" style="display: none">
                    <td style="width: 14%">申请国家：</td>
                    <td style="width: 30%">
                        <input id="sqgj" name="sqgj" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">申请该国家原因：</td>
                    <td style="width: 30%;">
                        <input id="sqgjyy" name="sqgjyy" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">我司发明(设计)人：</td>
                    <td style="width: 30%;">
                        <input id="myfmsjId" name="myfmsjId" textname="myfmsjName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="可见范围"
                               length="1000" maxlength="1000" mainfield="no" single="false"/>
                    </td>
                    <td style="width: 14%">专利申请人：</td>
                    <td style="width: 30%">
                        <input id="zlsqr" name="zlsqr" class="mini-textbox" style="width:98%;" emptytext="公司、合作单位"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">新颖性和创造性：</td>
                    <td style="width: 30%;" id="ifbianjixy">
                        <input id="xycz" name="xycz" class="mini-radiobuttonlist" style="width:98%;"
                               repeatLayout="table" repeatDirection="horizontal"
                               data="[{id:'HG',text:'很高'},{id:'H',text:'高'},{id:'YB',text:'一般'}]"/>
                    </td>
                    <td style="width: 14%">专利技术重要度：</td>
                    <td style="width: 30%;" id="ifbianjizyd">
                        <input id="zlzyd" name="zlzyd" class="mini-radiobuttonlist" style="width:98%;"
                               repeatLayout="table" repeatDirection="horizontal"
                               data="[{id:'HX',text:'核心'},{id:'GJ',text:'关键'},{id:'YB',text:'一般'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">是否关联项目：</td>
                    <td style="width: 30%;">
                        <input id="sflx" name="sflx" class="mini-combobox" style="width:98%;" onvaluechanged="sflxFun()"
                               textField="value" valueField="key" emptyText="请选择..." value="是"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"/>
                    </td>
                </tr>
                <tr id="projectTr" style="display: none">
                    <td style="width: 10%">产出该专利的项目名称：</td>
                    <td style="width:auto">
                        <input id="project" name="projectId" textname="projectName" class="mini-buttonedit"
                               style="width:98%;height:34px"
                               allowInput="false" onbuttonclick="addRelatedProject()" showClose="true"
                               oncloseclick="relatedCloseClick()"/>
                    </td>
                    <td style="width: 10%">成果计划：</td>
                    <td style="width:auto">
                        <input id="plan" name="planId" textname="planName" class="mini-buttonedit"
                               style="width:98%;height:34px"
                               allowInput="false" onbuttonclick="addRelatedPlan()" showClose="true"
                               oncloseclick="relatedPlanCloseClick()"/>
                    </td>
                </tr>
                <tr id="hqrTr" style="display: none">
                    <td style="width: 14%">项目负责人(会签)：</td>
                    <td style="width: 30%">
                        <input id="hqrId" name="hqrId" textname="hqrName" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" label="可见范围" length="1000"
                               maxlength="1000" mainfield="no" single="false"/>
                    </td>
                </tr>
                <tr id="ifFmsy1" style="display: none">
                    <td style="width: 14%">现有技术及其存在的问题：</td>
                    <td style="width: 30%;height: 150px" colspan="4">
						<textarea id="jswt" name="jswt" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:100%;height:100%" label="" datatype="varchar" length="1000"
                                  vtype="length:1000" minlen="0" allowinput="true"
                                  emptytext="简述要点"></textarea>
                    </td>
                </tr>
                <tr id="ifFmsy2" style="display: none">
                    <td style="width: 14%">解决技术问题采用的方案：</td>
                    <td style="width: 30%;height: 150px" colspan="4">
						<textarea id="jjwt" name="jjwt" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:100%;height:150px;line-height:50px;" label="" datatype="varchar"
                                  length="1000" vtype="length:1000" minlen="0" allowinput="true"
                                  emptytext="简述要点" mwidth="80" wunit="%" mheight="90" hunit="px"></textarea>
                    </td>
                </tr>
                <tr id="ifFmsy3" style="display: none">
                    <td style="width: 14%">技术效果：</td>
                    <td style="width: 30%;" colspan="4">
                        <input id="jsxg" name="jsxg" class="mini-textarea"
                               style="width:100%;height:150px;overflow: auto;" vtype="length:1000"
                               emptytext="简述要点"></input>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">专利技术保护要点：</td>
                    <td style="width: 30%;" colspan="4">
						<textarea id="zlbhd" name="zlbhd" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:100%;;height:100px;line-height:25px;" label="" datatype="varchar"
                                  length="1000" vtype="length:1000" minlen="0" allowinput="true"
                                  emptytext="简述要点" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr id="ifFmsy4">
                    <td style="width: 14%">技术分支：</td>
                    <td style="width: 30%;">
                        <input id="jsfz" name="jsfz" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onJsfzCloseClick()" textname="选择技术分支" allowInput="false"
                               onbuttonclick="selectJsfz()"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">技术管理部初审意见：</td>
                    <td style="width: 30%;" colspan="4">
						<textarea id="jsyj" name="jsyj" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:100%;;height:50px;line-height:25px;" label="" datatype="varchar"
                                  length="1000" vtype="length:1000" minlen="0" allowinput="true"
                                  emptytext="意见简述" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">技术管理部部长审核意见：</td>
                    <td style="width: 30%;" colspan="4">
						<textarea id="jsbyj" name="jsbyj" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:100%;;height:50px;line-height:25px;" label="" datatype="varchar"
                                  length="1000" vtype="length:1000" minlen="0" allowinput="true"
                                  emptytext="意见简述" mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>


                <tr>
                    <td style="width: 14%;height: 300px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addJsjdsFile()">添加附件</a>
                            <span style="color: red">注：添加附件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id" url="${ctxPath}/zhgl/core/jsjds/getJsjdsFileList.do?jsjdsId=${jsjdsId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileType" width="80" headerAlign="center" align="center">附件类型</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRenderer">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="projectWindow" title="关联项目信息选择窗口" class="mini-window" style="width:1200px;height:700px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchGrid" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">项目名称: </span>
                        <input class="mini-textbox" id="relatedProjectName" name="relatedProjectName"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">项目编号: </span>
                        <input class="mini-textbox" id="projectNumber" name="projectNumber">
                    </li>
                    <li style="margin-right: 15px">
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="searchProcessData()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                           onclick="cleanProcessData()">清空查询</a>
                    </li>
                    <li style="display: inline-block;float: right;">
                        <a id="importBtn" class="mini-button" onclick="choseRelatedProject()">确认</a>
                        <a id="closeProjectWindow" class="mini-button btn-red" onclick="closeProjectWindow()">关闭</a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="projectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onlyCheckSelection="true" idField="projectId" allowCellWrap="true"
             url="${ctxPath}/xcmgProjectManager/core/xcmgProject/getProjectList.do"
             multiSelect="false" showColumnsMenu="true" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
             pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="projectId" visible="false"></div>
                <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
                <div field="projectName" sortField="projectName" width="170" headerAlign="center" align="center"
                     allowSort="true">项目名称
                </div>
                <div field="number" sortField="number" width="110" headerAlign="center" align="center" allowSort="true">
                    项目编号
                </div>
                <div field="respMan" sortField="respMan" width="50" headerAlign="center" align="center"
                     allowSort="false">项目负责人
                </div>
                <div field="status" sortField="status" width="50" allowSort="true" headerAlign="center" align="center"
                     renderer="onStatusRenderer">项目状态
                </div>
            </div>
        </div>
    </div>
</div>
<div id="planWindow" title="成果计划选择窗口" class="mini-window" style="width:1200px;height:700px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-toolbar">
        <div class="searchBox">
            <form class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="display: inline-block;float: right;">
                        <a id="choseRelatedPlan" class="mini-button" onclick="choseRelatedPlan()">确认</a>
                        <a id="closePlanWindow" class="mini-button btn-red" onclick="closePlanWindow()">关闭</a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="planListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onlyCheckSelection="true" idField="id" allowCellWrap="true"
             url="${ctxPath}/zhgl/core/jsjds/selectProjectPlan.do"
             multiSelect="false" showColumnsMenu="true" showPager="false">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="id" visible="false"></div>
                <div field="projectId" visible="false"></div>
                <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
                <div field="description" width="170" headerAlign="center" align="center"
                     allowSort="true">计划描述
                </div>
                <div field="deptName" width="110" headerAlign="center" align="center" allowSort="true">
                    部门
                </div>
                <div field="typeName" width="50" headerAlign="center" align="center"
                     allowSort="false">类别
                </div>
                <div field="output_time" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" width="80">
                    预计产出时间
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var needReport = "${needReport}";
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("fileListGrid");
    var formZljsjds = new mini.Form("#formZljsjds");
    var jsjdsId = "${jsjdsId}";
    var nodeVarsStr = '${nodeVars}';
    var projectWindow = mini.get("projectWindow");
    var projectListGrid = mini.get("projectListGrid");
    var planWindow = mini.get("planWindow");
    var planListGrid = mini.get("planListGrid");
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";


    function ifycFun() {
        var gjData = mini.get("ifgj").getValue();
        if (gjData && 'yes' == gjData) {
            $("#ifgjxs").show();
        } else {
            $("#ifgjxs").css("display", "none");
        }
        var zllxData = mini.get("zllx").getValue();
        if (zllxData && 'WGSJ' != zllxData) {
            $("#ifFmsy1").show();
            $("#ifFmsy2").show();
            $("#ifFmsy3").show();
            $("#jsxg textarea").focus();
            $("#jsxg textarea").blur();
            $("#jswt textarea").focus();
            $("#jswt textarea").blur();
            $("#jjwt textarea").focus();
            $("#jjwt textarea").blur();

        } else {
            $("#ifFmsy1").css("display", "none");
            $("#ifFmsy2").css("display", "none");
            $("#ifFmsy3").css("display", "none");
        }
        mini.get("plan").setValue('');
        mini.get("plan").setText('');
    }

    function sflxFun() {
        var sflx = mini.get("sflx").getValue();
        if (sflx && '是' == sflx) {
            $("#projectTr").show();
            $("#hqrTr").show();
        } else if (sflx && '否' == sflx)  {
            $("#projectTr").hide();
            $("#hqrTr").hide();
            mini.get("project").setValue("");
            mini.get("project").setText("");
            mini.get("plan").setValue("");
            mini.get("plan").setText("");
            mini.get("hqrId").setValue("");
            mini.get("hqrId").setText("");
        }
    }


    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnJsjdsPreviewSpan(record.fileName, record.id, record.jsjdsId, coverContent);
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && isBianzhi == 'yes')) {
            var deleteUrl = "/zhgl/core/jsjds/delJsjdsFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.jsjdsId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downJsjdsLoadFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">下载</span>';
        return cellHtml;
    }


    //添加关联项目信息展开窗口
    function addRelatedProject() {
        projectWindow.show();
        searchProcessData();
    }

    //关闭关联项目选址页面
    function closeProjectWindow() {
        mini.get("relatedProjectName").setValue('');
        mini.get("projectNumber").setValue('');
        projectWindow.hide();
    }

    function cleanProcessData() {
        mini.get("relatedProjectName").setValue('');
        mini.get("projectNumber").setValue('');
        searchProcessData();
    }

    //关联项目查询
    function searchProcessData() {
        var projectName = mini.get("relatedProjectName").getValue();
        var number = mini.get("projectNumber").getValue();
        var paramArray = [{name: "projectName", value: projectName}, {name: "number", value: number}];
        var data = {};
        data.filter = mini.encode(paramArray);
        projectListGrid.load(data);
    }

    //关联项目写入
    function choseRelatedProject(e) {
        var row = projectListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条项目记录");
            return;
        }
        mini.get("project").setValue(row.projectId);
        mini.get("project").setText(row.projectName);
        if (status != 'SUCCESS_END' || isBianzhi == 'yes') {
            mini.get("hqrId").setValue(row.respManId);
            mini.get("hqrId").setText(row.respMan);
        }
        mini.get("plan").setValue('');
        mini.get("plan").setText('');
        closeProjectWindow();
    }

    function relatedCloseClick() {
        mini.get("project").setValue("");
        mini.get("project").setText("");
        mini.get("hqrId").setValue("");
        mini.get("hqrId").setText("");
        mini.get("plan").setValue('');
        mini.get("plan").setText('');
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, status);
    }

    function addRelatedPlan() {
        var zllx = mini.get("zllx").getValue();
        if (!zllx) {
            mini.alert("请先选择专利类型")
            return;
        }
        var projectId = mini.get("project").getValue();
        if (!projectId) {
            mini.alert("请先选择产出该专利的项目名称")
            return;
        }
        planWindow.show();
        searchPlan(projectId,zllx);
    }

    function searchPlan(projectId,zllx) {
        if(zllx=='FM'){
            var zllxName = '发明';
        }else if(zllx=='SYXX'){
            var zllxName = '实用新型';
        }else if(zllx=='WGSJ'){
            var zllxName = '外观';
        }
        var queryParam = [];
        //其他筛选条件
        var data = {};
        queryParam.push({name: "projectId", value: projectId});
        queryParam.push({name: "zllx", value: '国内'+zllxName});
        data.filter = mini.encode(queryParam);
        //查询
        planListGrid.load(data);
    }

    function choseRelatedPlan() {
        var row = planListGrid.getSelected();
        mini.get("plan").setValue(row.id);
        mini.get("plan").setText(row.description);
        closePlanWindow();
    }

    function closePlanWindow() {
        planWindow.hide();
    }

    function relatedPlanCloseClick() {
        mini.get("plan").setValue('');
        mini.get("plan").setText('');
    }

    function onJsfzCloseClick() {
        mini.get("jsfz").setValue('');
        mini.get("jsfz").setText('');
    }

    function selectJsfz() {
        mini.open({
            title: "技术分支选择",
            url: jsUseCtxPath + "/zhgl/core/patentInterpretation/technologyBranch/jszhTree.do",
            width: 1300,
            height: 700,
            showModal: true,
            allowResize: true,
            onload: function () {

            },
            ondestroy: function (returnData) {
                if (returnData) {
                    mini.get("jsfz").setValue(returnData.description);
                    mini.get("jsfz").setText(returnData.description);
                } else {
                    mini.alert("未选择技术分支！");
                }
            }
        });
    }
</script>
</body>
</html>
