<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>仿真设计表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/fzsj/fzsjEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <style type="text/css">
        fieldset {
            border: solid 1px #aaaaaab3;
            min-width: 920px;
        }

        .hideFieldset {
            border-left: 0;
            border-right: 0;
            border-bottom: 0;
        }

        .hideFieldset .fieldset-body {
            display: none;
        }

        .processStage {
            background-color: #ccc !important;
            font-size: 15px !important;
            font-family: '微软雅黑' !important;
            text-align: center !important;
            vertical-align: middle !important;
            color: #201f35 !important;
            height: 30px !important;
            border-right: solid 0.5px #666;
        }

        .rmMem {
            background-color: #848382ad
        }
    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveProjectInfo" class="mini-button" style="display: none" onclick="saveFzsjInProcess()">保存</a>
        <a id="processInfo" class="mini-button" style="display: none" onclick="fzsjProcessInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="fzsjForm" method="post" style="height: 95%;width: 100%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="step" name="step" class="mini-hidden"/>
            <input id="sqlcsfjs" name="sqlcsfjs" class="mini-hidden"/>
            <input id="ldjsspsj" name="ldjsspsj" class="mini-hidden"/>
            <fieldset id="xxBaseInfo">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="checkboxBaseInfo" onclick="zltoggleFieldSet(this, 'xxBaseInfo')" hideFocus/>
                        基本信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 17%">申请人：</td>
                            <td style="width: 33%;">
                                <input id="CREATE_BY_" name="CREATE_BY_" textname="creator" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="申请人" length="50"
                                       mainfield="no" single="true"/>
                            </td>
                            <td style="text-align: center;width: 14%">部门：</td>
                            <td style="width: 33%;min-width:170px">
                                <input id="departmentId" name="departmentId" class="mini-dep rxc" plugins="mini-dep"
                                       style="width:98%;height:34px"
                                       allowinput="false" textname="department" length="200" maxlength="200" minlen="0" single="true"
                                       initlogindep="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">申请时间(点击'提交'后自动生成)：</td>
                            <td style="width: 33%">
                                <input id="applyTime" name="applyTime" class="mini-datepicker" allowInput="false" style="width:98%;"/>
                            </td>
                            <td style="text-align: center;width: 14%">仿真编号(点击'提交'后自动生成)：</td>
                            <td style="width: 36%;min-width:170px">
                                <input id="fzNumber" name="fzNumber" class="mini-textbox" readonly style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 14%">任务名称：<span style="color:red">*</span></td>
                            <td style="width: 36%;min-width:170px">
                                <input id="questName" name="questName" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="text-align: center;width: 14%">应用机型：</td>
                            <td style="width: 36%;min-width:170px">
                                <input
                                        id="applicationType"
                                        name="applicationType"
                                        class="mini-treeselect"
                                        url="${ctxPath}/fzsj/core/sdm/listSdmProduct.do"
                                        multiSelect="false"
                                        textField="name"
                                        valueField="id"
                                        parentField="parentId"
                                        required="true"
                                        value="" onnodeclick="setTreeKey(e)" onbeforenodeselect="onBeforeNodeSelect"
                                        showFolderCheckBox="false"
                                        expandOnLoad="true"
                                        popupWidth="100%"
                                        style="width:100%"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 14%">任务类型：</td>
                            <td style="width: 36%;min-width:170px">
                                <input id="taskType" name="taskType" class="mini-combobox rxc" plugins="mini-combobox"
                                       style="width:100%;height:34px" label="任务类型："
                                       length="50"
                                       only_read="false" required="true" allowinput="false" mwidth="100"
                                       wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                                       textField="text" valueField="key_" emptyText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=taskType"
                                       nullitemtext="请选择..." emptytext="请选择..."/>
                            </td>
                            <td style="text-align: center;width: 14%">是否推送SDM：</td>
                            <td style="width: 36%;min-width:170px">
                                <input id="isSendToSDM" name="isSendToSDM" class="mini-combobox rxc" plugins="mini-combobox"
                                       style="width:100%;height:34px" label="是否推送SDM："
                                       length="50"
                                       only_read="false" required="true" allowinput="false" mwidth="100"
                                       wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                                       textField="text" valueField="key_" emptyText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=FZSJ_YESORNO"
                                       nullitemtext="请选择..." emptytext="请选择..."/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 14%">任务来源：<span style="color:red">*</span></td>
                            <td style="width: 36%;min-width:170px">
                                <input id="taskResource" name="taskResource" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                                       textField="text" valueField="id"
                                       data="[{id:'xpyf',text:'新品研发'},{id:'zyOrYycp',text:'在研/预研产品'},{id:'scgj',text:'市场改进'},{id:'hxjsyj',text:'核心技术研究'}]"
                                />
                            </td>
                            <td style="text-align: center;width: 14%">样机状态：<span style="color:red">*</span></td>
                            <td style="width: 36%;min-width:170px">
                                <input id="prototypeState" name="prototypeState" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                                       textField="text" valueField="id"
                                       data="[{id:'yyj',text:'有样机'},{id:'wyj',text:'无样机'}]"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 14%">是否紧急：</td>
                            <td style="width: 20%;text-align:left">
                                <input id="idUrgent" name="idUrgent" class="mini-checkbox" style="width:98%;"
                                       trueValue="是" falseValue="否"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">产品主管或室主任：<span style="color:red">*</span></td>
                            <td style="width: 33%;">
                                <input id="cpzgOrSzrId" name="cpzgOrSzrId" textname="cpzgOrSzr" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="产品主管或室主任" length="50"
                                       mainfield="no" single="false"/>
                            </td>
                            <td style="text-align: center;width: 14%">仿真室主任：<span style="color:red">*</span></td>
                            <td style="width: 33%;min-width:170px">
                                <input id="fzszrId" name="fzszrId" textname="fzszr" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="仿真室主任" length="50"
                                       mainfield="no" single="true"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 14%">仿真分析项：<span style="color:red">*</span></td>
                            <td style="width: 36%;min-width:170px">
                                <input id="fzlbId" name="fzlbId" textName="fzlb" style="width:98%;" class="mini-buttonedit"
                                       onbuttonclick="openFzlbWindow" allowInput="false"/>
                            </td>
                            <td style="text-align: center;width: 14%">仿真对象：</td>
                            <td style="width: 36%;">
                                <input id="fzdx" name="fzdx" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 14%">需求资料：</td>
                            <td style="width: 36%;min-width:170px;">
                                <textarea id="demandData" name="demandData" class="mini-textarea rxc"
                                          plugins="mini-textarea" style="width:98%;height:100px;line-height:25px;" label="" datatype="varchar"
                                          length="500"
                                          vtype="length:500" minlen="0" allowinput="true"
                                          wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                            <td style="text-align: center;width: 14%">关联的科技项目：<br>(<span style="color: red">负责或参与的，运行中的项目；<br>申请批准后不能修改，<br>若修改联系项目管理人员</span>)
                            </td>
                            <td style="width: 36%;">
                                <input id="projectId" name="projectId" class="mini-combobox" style="width:98%;"
                                       textField="projectName" valueField="projectId" emptyText="请选择..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                       onvaluechanged="projectChange()"/>
                                <input id="projectName" name="projectName" readonly class="mini-textbox" style="width:98%;display: none"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 14%">仿真目的：(不超过1000字)<span style="color:red">*</span></td>
                            <td colspan="3">
						<textarea id="fzmd" name="fzmd" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                  vtype="length:1000" minlen="0" allowinput="true"
                                  emptytext="请输入仿真目的" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="fzzxBaseInfo">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="checkboxMemberInfo" onclick="zltoggleFieldSet(this, 'fzzxBaseInfo')" hideFocus/>
                        仿真执行
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px;width: 98% ;height: auto">
                    <div>
                        <a id="addFzzx" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addFzzx">新增</a>
                    </div>
                    <div id="fzzxGrid" class="mini-datagrid" style="width: 100%; height: auto" allowResize="false"
                         idField="id" url="${ctxPath}/fzsj/core/fzsj/queryFzzx.do?fzsjId=${fzsjId}"
                         allowcelledit="false" allowCellWrap="true" allowcellselect="true" showCellTip="true"
                         multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true">
                        <div property="columns">
                            <div type="checkcolumn" headerAlign="center" align="center" width="10"></div>
                            <div type="indexcolumn" headerAlign="center" align="center" width="15">序号</div>
                            <div headerAlign="center" name="action" align="center" width="30" renderer="fzzxGridOptionRender" cellCls="actionIcons"
                                 cellStyle="padding:0;">操作
                            </div>
                            <div field="zxry" headerAlign="center" align="center" width="40">执行人员</div>
                            <div field="timeNode" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" width="25">
                                时间节点
                            </div>
                            <div field="fzjgjjy" headerAlign="center" width="100" align="center">
                                仿真结果及建议
                            </div>
                            <div field="confirmResult" headerAlign="center" align="center" width="30" renderer="confirmResultRenderer">
                                确认结果
                            </div>
                            <div field="confirmReason" headerAlign="center" width="100" align="center">
                                确认原因
                            </div>
                            <div headerAlign="center" name="fileAction" align="center" width="20" renderer="fzzxGridFileOptionRender"
                                 cellCls="actionIcons" cellStyle="padding:0;">附件
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
            <br>
            <fieldset id="gjxxBaseInfo">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="checkboxXGInfo" onclick="zltoggleFieldSet(this, 'gjxxBaseInfo')" hideFocus/>
                        改进信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 14%">改进意见：</td>
                            <td style="width: 36%;min-width:170px">
                                <input id="gjyj" name="gjyj" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                                       textField="text" valueField="id"
                                       data="[{id:'tygj',text:'同意改进'},{id:'bfgj',text:'部分改进'},{id:'btygj',text:'不同意改进'}]"
                                />
                            </td>
                            <td style="text-align: center;width: 14%">预计完成改进时间：</td>
                            <td style="width: 36%;min-width:170px">
                                <input id="predictFinishTime" name="predictFinishTime" class="mini-datepicker" allowInput="false" format="yyyy-MM-dd"
                                       style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 14%">部分改进/不改进理由：</td>
                            <td colspan="3">
						<textarea id="gjyy" name="gjyy" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="请输部分改进/不改进理由" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 14%">改进后反馈：</td>
                            <td colspan="3">
						<textarea id="gjhxntsfk" name="gjhxntsfk" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="请输入改进后反馈" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="fjBaseInfo">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="checkboxYanzhengInfo" onclick="zltoggleFieldSet(this, 'fjBaseInfo')" hideFocus/>
                        改进后附件
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px;width: 98% ;height: auto">
                    <div style="margin-top: 10px;margin-bottom: 2px">
                        <a id="addFile" class="mini-button" onclick="addFzsjFile()">添加附件</a>
                    </div>
                    <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: auto" allowResize="false"
                         idField="id" url="${ctxPath}/fzsj/core/fzsj/queryFzsjFileList.do?belongDetailId=${fzsjId}" autoload="true"
                         multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                        <div property="columns">
                            <div type="indexcolumn" align="center" width="20">序号</div>
                            <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                            <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center">上传时间</div>
                            <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
                        </div>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var fzsjForm = new mini.Form("#fzsjForm");
    var fzsjId = "${fzsjId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var mainGroupId = "${mainGroupId}";
    var mainGroupName = "${mainGroupName}";
    var fileListGrid = mini.get("fileListGrid");
    var nodeVarsStr = '${nodeVars}';
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var fzzxGrid = mini.get("fzzxGrid");
    var downloadPermission = "${downloadPermission}"
    var currentUserRoles =${currentUserRoles};

    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    //操作栏
    fzzxGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });
    /**
     * 表单弹出事件控制
     * @param ck
     * @param id
     */
    function zltoggleFieldSet(ck, id) {
        var dom = document.getElementById(id);
        if (ck.checked) {
            dom.className = "";
        } else {
            dom.className = "hideFieldset";
        }
    }
</script>
</body>
</html>
