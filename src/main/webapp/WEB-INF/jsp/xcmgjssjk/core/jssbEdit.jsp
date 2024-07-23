<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/xcmgjssjk/jssbEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }
    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formJssb" method="post">
            <input id="jssbId" name="jssbId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 25%;text-align: center">技术名称：</td>
                    <td style="width: 20%">
                        <input id="jsName" name="jsName" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 25% ">申请部门：</td>
                    <td style="width: 20%;min-width:170px ">
                        <input id="sbDepId" name="sbDepId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="sbDep" single="true" initlogindep="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 25% ">申请人：</td>
                    <td style="width: 20%;min-width:170px ">
                        <input id="sbResId" name="sbResId" textname="sbRes"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="width: 25%;text-align: center">申报时间：</td>
                    <td style="width: 20%;text-align:center">
                        <input  id="sbTime" name="sbTime" class="mini-datepicker"  format="yyyy-MM-dd" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 25%;text-align: center">预计开始时间：</td>
                    <td style="width: 20%">
                        <input  id="startTime" name="startTime" class="mini-monthpicker"  format="yyyy-MM" style="width:98%;"/>
                    </td>
                    <td style="  width: 25%;text-align: center">预计完成时间：</td>
                    <td style="width: 20%">
                        <input id="needTime" name="needTime" class="mini-monthpicker"  format="yyyy-MM" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 25%;text-align: center">专业方向：</td>
                    <td style="width: 20%">
                        <input id="direction" name="direction" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="请选择..."
                               data="[
                            {'key' : '液压','value' : '液压'},{'key' : '结构','value' : '结构'}
                           ,{'key' : '动力','value' : '动力'},{'key' : '控制','value' : '控制'}
                           ,{'key' : '电气','value' : '电气'},{'key' : '仿真','value' : '仿真'}
                           ,{'key' : '测试','value' : '测试'},{'key' : '其他','value' : '其他'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%;text-align: center">跳过室主任校对节点：</td>
                    <td style="width: 20%;text-align:center">
                        <input  id="szrCheck" name="szrCheck" class="mini-checkbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 25% ">校对室主任：</td>
                    <td style="width: 20%;min-width:170px ">
                        <input id="szrId" name="szrId" textname="szr"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">技术描述：(3000字以内)</td>
                    <td colspan="3">
						<textarea id="jsms" name="jsms" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="说明" datatype="varchar" allowinput="true"
                                  emptytext="请输入说明..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">技术简介：(3000字以内)</td>
                    <td colspan="3">
						<textarea id="intro" name="intro" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="说明" datatype="varchar" allowinput="true"
                                  emptytext="请输入技术简介..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">创新点：(3000字以内)</td>
                    <td colspan="3">
						<textarea id="innovate" name="innovate" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="说明" datatype="varchar" allowinput="true"
                                  emptytext="请输入创新点..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 25%;height: 300px;text-align: center ">创新点附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 2px;margin-bottom: 2px">
                            <a id="addCnfFile" class="mini-button" onclick="cxupload()">添加附件</a>
                        </div>
                        <div id="cxdFileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/Jssb/getJssbFileList.do?jssbId=${jssbId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="15">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererF">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 25%;height: 350px;text-align: center ">技术指标：</td>
                    <td colspan="3">
                        <div style="margin-top: 20px;margin-bottom: 2px">
                            <a id="editMsgZj" class="mini-button " style="margin-right: 5px" plain="true"
                               onclick="addJszbDetail()">新增</a>
                            <a id="removeZj" style="margin-right: 5px" class="mini-button" onclick="removeJszb()">删除</a>
                            <span style="color: red">注：新增或删除后请点击保存以生效</span>
                        </div>
                        <div id="jszbListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/Jssb/getJszbList.do?jssbId=${jssbId}"
                             allowCellEdit="true" allowCellSelect="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div  field="check" type="checkcolumn" width="15"></div>
                                <div  field="index" type="indexcolumn" align="center" width="30">序号</div>
                                <div name="item" field="item" width="50" headerAlign="center" align="center">项次
                                    <input  property="editor" class="mini-combobox"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true"
                                           nullItemText="请选择..."
                                           data="[
                                            {'key' : '安全性','value' : '安全性'},{'key' : '可靠性','value' : '可靠性'}
                                           ,{'key' : '经济性','value' : '经济性'},{'key' : '环境适应性','value' : '环境适应性'}
                                           ,{'key' : '维修性','value' : '维修性'},{'key' : '操控性','value' : '操控性'}
                                           ,{'key' : '成本','value' : '成本'}]"
                                    /></div>
                                <div field="target" width="80" headerAlign="center" align="center">指标
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="company" width="60" headerAlign="center" align="center">单位
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="nowLevel" width="80" headerAlign="center" align="center">现有水平
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="bestLevel" width="80" headerAlign="center" align="center">标杆水平
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="toLevel" width="80" headerAlign="center" align="center">目标水平
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="note" width="80" headerAlign="center" align="center">备注
                                    <input property="editor" class="mini-textbox" /></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 25%;text-align: center">核心零部件：(2000字以内)</td>
                    <td colspan="3">
						<textarea id="corepart" name="corepart" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="说明" datatype="varchar" allowinput="true"
                                  emptytext="请输入核心零部件..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                <td style="  width: 25%;text-align: center">产权分析：(2000字以内)</td>
                <td colspan="3">
						<textarea id="property" name="property" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="说明" datatype="varchar" allowinput="true"
                                  emptytext="请填写产权分析..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                </td>
                </tr>
                <tr>
                    <td style="   width: 25%;text-align: center">室主任校对驳回原因：</td>
                    <td colspan="3">
						<textarea id="jdView" name="jdView" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="说明" datatype="varchar" allowinput="true"
                                  emptytext="请填写驳回原因..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 25%;text-align: center">部门长驳回原因：</td>
                    <td colspan="3">
						<textarea id="spView" name="spView" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="说明" datatype="varchar" allowinput="true"
                                  emptytext="请填写驳回原因..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var status = "${status}";
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var jssbListGrid = mini.get("jssbListGrid");
    var jssbId = "${jssbId}";
    var formJssb = new mini.Form("#formJssb");
    var cxdFileListGrid = mini.get("cxdFileListGrid");
    var jszbListGrid = mini.get("jszbListGrid");
    var currentUserName = "${currentUserName}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var deptName = "${deptName}";
    var currentUserMainDepId = "${currentUserMainDepId}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";



    function operationRendererF(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnJssbPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadJssbFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')">下载</span>';
        //增加删除按钮
        if (record.CREATE_BY_==currentUserId&&action!='detail') {
            var deleteUrl = "/Jssb/deleteJssbFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteSmFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

function downloadRj() {
    var belongId = mini.get("rjResId").getValue();
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/environment/core/Jssb/jssbDownload.do?action=download');
    var inputFileId = $("<input>");
    inputFileId.attr("type", "hidden");
    inputFileId.attr("name", "belongId");
    inputFileId.attr("value", belongId);
    $("body").append(form);
    form.append(inputFileId);
    form.submit();
    form.remove();
}


</script>
</body>
</html>