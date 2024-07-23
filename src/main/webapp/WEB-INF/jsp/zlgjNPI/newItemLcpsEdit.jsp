<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/5/8
  Time: 15:24
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>新品量产评审</title>
    <%@include file="/commons/edit.jsp"%>
    <script src="${ctxPath}/scripts/zlgjNPI/newItemLcpsEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/sys/echarts/echartsFrontCustom.js?t=1.5.137" type="text/javascript"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts.min.js"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts-wordcloud.min.js"></script>
    <script src="${ctxPath}/scripts/sys/echarts/roll.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <link  href="${ctxPath}/scripts/sys/echarts/css/roll.css" rel="stylesheet" type="text/css" />
    <link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css" />
    <style type="text/css">
        fieldset
        {
            border:solid 1px #aaaaaab3;
            min-width: 920px;
        }
        .hideFieldset
        {
            border-left:0;
            border-right:0;
            border-bottom:0;
        }
        .hideFieldset .fieldset-body
        {
            display:none;
        }
        .processStage{
            background-color: #ccc !important;
            font-size:15px !important;
            font-family:'微软雅黑' !important;
            text-align:center !important;
            vertical-align:middle !important;
            color: #201f35 !important;
            height: 30px !important;
            border-right:solid 0.5px #666;
        }
        .rmMem{
            background-color: #848382ad
        }
    </style>
</head>
<body>
    <div id="loading" class="loading" style="display:none;text-align:center;"><img src="${ctxPath}/styles/images/loading.gif"></div>
    <div id="detailToolBar" class="topToolBar" style="display: none">
        <div>
            <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
            <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
        </div>
    </div>
    <div class="mini-fit" id="content">
        <div class="form-container" style="margin: 0 auto">
            <form id="formXplc" method="post">
                <input id="xplcId" name="xplcId" class="mini-hidden"/>
                <input id="instId" name="instId" class="mini-hidden"/>
                <input id="creator" name="creator" class="mini-hidden"/>
                <input id="CREATE_TIME_" name="CREATE_TIME_" class="mini-hidden"/>
                <fieldset id="fdBaseInfo" >
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox" checked id="checkboxBaseInfo" onclick="zltoggleFieldSet(this, 'fdBaseInfo')" hideFocus/>
                            新品量产评审
                        </label>
                    </legend>
                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                        <table class="table-detail" cellspacing="1" cellpadding="0">
                            <tr>
                                <td style="width: 14%">产品主管：</td>
                                <td style="width: 21%">
                                    <input id="cpzgId" name="cpzgId" textname="cpzgName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="项目负责人" length="50" maxlength="50"  mainfield="no"  single="true" />
                                </td>
                                <td style="width: 14%">是否分管领导审批：</td>
                                <td style="width: 21%">
                                    <input id="sffgsp" name="sffgsp" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                           data="[{'key' : 'YES','value' : '是'},{'key' : 'NO','value' : '否'}]"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                </fieldset>
                <hr>
                <fieldset id="fd_file_BaseInfo">
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox" checked id="checkboxFileBaseInfo" onclick="zltoggleFieldSet(this, 'fd_file_BaseInfo')" hideFocus/>
                            新品量产评审-附件
                        </label>
                    </legend>
                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button"  onclick="addXplcFile()">添加附件</a>
                            <span style="color: red">注：添加附件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: auto" allowResize="false"
                             idField="id" url="${ctxPath}/zhgl/core/lcps/getXplcFileList.do?xplcId=${xplcId}" autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true" >
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center"  width="20">序号</div>
                                <div field="fileName"  width="140" headerAlign="center" align="center" >附件名称</div>
                                <div field="fileSize"  width="80" headerAlign="center" align="center" >附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
                            </div>
                        </div>
                    </div>
                </fieldset>
                <hr>
                <fieldset id="fdAchievementInfo" >
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox"  checked id="checkboxAchievementInfo" onclick="zltoggleFieldSet(this, 'fdAchievementInfo')" hideFocus/>
                            新品量产评审-问题改进清单
                        </label>
                    </legend>
                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px;width: 98% ;height: auto">
                        <div class="mini-toolbar" id="projectAchievementButtons">
                            <div class="searchBox">
                                <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                                    <li id="operateStandard" style="margin-left: 10px">
                                        <a id="operateAdd" class="mini-button" style="margin-right: 5px" plain="true"
                                           onclick="openXplcxxEditWindow('','','add','${xplcId}')">新增</a>
                                    </li>
                                </form>
                            </div>
                        </div>
                        <div id="grid_xplcps" class="mini-datagrid"
                             allowResize="false"
                             url=" ${ctxPath}/zhgl/core/lcps/getXplcxxList.do?xplcId=${xplcId}"
                             idField="applyId" autoload="true" allowCellWrap="true"
                             showPager="false">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div id="isOperation" name="action" cellCls="actionIcons" width="60" headerAlign="center"
                                     align="center"
                                     renderer="onAction" cellStyle="padding:0;">操作
                                </div>
                                <div field="product" align="center" width="60" headerAlign="center" allowSort="false">
                                    产品<input readonly property="editor" class="mini-textbox"/>
                                </div>
                                <div field="jixin" align="center" width="80" headerAlign="center" allowSort="false">
                                    机型<input readonly property="editor" class="mini-textbox"/>
                                </div>
                                <div field="lbj" align="center" width="80" headerAlign="center" allowSort="false">
                                    零部件<input readonly property="editor" class="mini-textbox"/>
                                </div>
                                <div field="wtqd" align="center" width="180" headerAlign="center" allowSort="false">
                                    问题清单<input readonly property="editor" class="mini-textbox"/>
                                </div>
                                <div field="zrrName" align="center" width="60" headerAlign="center" allowSort="false">
                                    责任人<input readonly property="editor" class="mini-textbox"/>
                                </div>
                                <div field="bmName" align="center" width="60" headerAlign="center" allowSort="false">
                                    责任部门<input readonly property="editor" class="mini-textbox"/>
                                </div>
                                <div field="yhcs" align="center" width="180" headerAlign="center" allowSort="false">
                                    后续优化措施<input readonly property="editor" class="mini-textbox"/>
                                </div>
                                <div field="wcTime"  headerAlign="center" align="center" width="60" dateFormat="yyyy-MM-dd">
                                    完成日期<input readonly property="editor" class="mini-datepicker" style="width:100%;"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </fieldset>
                <br>
            </form>
        </div>
    </div>
<script type="text/javascript">
    mini.parse();
    var nodeVarsStr='${nodeVars}';
    var nodeId="${nodeId}";
    var status="${status}";
    var xplcId="${xplcId}";
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var formXplc = new mini.Form("#formXplc");
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var grid_xplcps = mini.get("#grid_xplcps");
    var fileListGrid=mini.get("fileListGrid");


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
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnXplcPreviewSpan(record.fileName,record.id,record.xplcId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downXplcLoadFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
        //增加删除按钮
        if(action=='edit' || isFqBianZhi == 'yes') {
            var deleteUrl="/zhgl/core/lcps/delXplcFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.id+'\',\''+record.xplcId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }
    /**
     * 新品量产评审-信息行功能按钮
     */
    function onAction(e) {
        var record = e.record;
        var cellHtml = '';
        //增加修改按钮 所属节点/编制阶段提供修改功能
        // if (action == 'edit' || (action == 'task' && (isZqyj == 'yes'|| isPsg == 'yes'))) {
         if ((action == 'edit' &&  record.CREATE_BY_ == currentUserId )||(isShBianZhi == 'yes')||(isHqBianZhi == 'yes' && record.zrrId == currentUserId)
                ||(isFqBianZhi == 'yes' &&  record.CREATE_BY_ == currentUserId)) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title= "编辑" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="openXplcxxEditWindow(\'' + record.id + '\',\'\',\'update\',\'' + record.xplcId + '\')">编辑</span>';

        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="编辑" style="color: silver" >编辑</span>';
        }
        //增加删除按钮 所属节点/编制阶段提供删除功能
        if ((action == 'edit' && record.CREATE_BY_ == currentUserId) ||(isFqBianZhi == 'yes' &&  record.CREATE_BY_ == currentUserId)) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="removeXplcxx(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color: silver" >删除</span>';
        }

        return cellHtml;
    }
    function removeXplcxx(record) {

        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = grid_xplcps.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/lcps/deleteXplcxx.do?",
                    method: 'POST',
                    showMsg: false,
                    data: {id: record.id},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            grid_xplcps.load();
                        }
                    }
                });
            }
        });
    }

</script>
</body>
</html>
