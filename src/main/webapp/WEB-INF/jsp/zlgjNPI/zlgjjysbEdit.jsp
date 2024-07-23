<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>质量改进建议申报表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/zlgjNPI/zlgjjysbEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
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
    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="zlgjjysbProcessInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="zlgjjysbForm" method="post" style="height: 95%;width: 100%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="step" name="step" class="mini-hidden"/>
            <fieldset id="jbxx">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="cpxhCheckbox" onclick="toggleZlgjjysbFieldset(this, 'jbxx')" hideFocus/>
                        基本信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 17%">项目名称：</td>
                            <td style="width: 33%;">
                                <input id="projectName" name="projectName" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="text-align: center;width: 17%">申报人：</td>
                            <td style="width: 33%;">
                                <input id="CREATE_BY_" name="CREATE_BY_" textname="creator" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="申报人" length="50"
                                       mainfield="no" single="true"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">电话：</td>
                            <td style="width: 33%;">
                                <input id="phoneNumber" name="phoneNumber" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="text-align: center;width: 17%">申报单位：</td>
                            <td style="width: 33%;">
                                <input id="applicationUnitId" name="applicationUnitId" class="mini-dep rxc" plugins="mini-dep"
                                       style="width:98%;height:34px"
                                       allowinput="false" textname="applicationUnit" length="200" maxlength="200" minlen="0" single="true"
                                       initlogindep="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">设计/工艺室主任：</td>
                            <td style="width: 33%;">
                                <input id="sectionChiefId" name="sectionChiefId" textname="sectionChief" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="设计/工艺室主任" length="50"
                                       single="false"
                                       mainfield="no"/>
                            </td>
                            <td style="text-align: center;width: 17%">申报日期(点击'提交'后自动生成)：</td>
                            <td style="width: 33%">
                                <input id="declareTime" name="declareTime" class="mini-datepicker" allowInput="false" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <%--@mh 增加质量审核人员--%>
                            <td style="text-align: center;width: 17%">质量审核人员：</td>
                            <td style="width: 33%;">
                                <input id="checkId" name="checkId" textname="checkName" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                                       label="质量审核人员" length="50" single="true"
                                       mainfield="no" enable="false"/>
                            </td>

                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="wtms">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="wtmsCheckbox" onclick="toggleZlgjjysbFieldset(this, 'wtms')" hideFocus/>
                        问题描述
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 17%">问题大类：</td>
                            <td style="width: 33%">
                                <input id="wtdl" name="wtdl" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                                       textField="value" valueField="key" emptyText="请选择..."
                                       data="[{key:'部品检验',value:'部品检验'}
                                       ,{key:'结构检验',value:'结构检验'}
                                       ,{key:'装配',value:'装配'}
                                       ,{key:'焊接',value:'焊接'}
                                       ,{key:'涂装',value:'涂装'}
                                       ,{key:'产品建议',value:'产品建议'}]"
                                />
                            </td>
                            <td style="text-align: center;width: 17%">问题小类：</td>
                            <td style="width: 33%">
                                <input id="wtxl" name="wtxl" class="mini-checkboxlist" multiSelect="true" style="width:98%"
                                       textField="value" valueField="key" emptyText="请选择..."
                                       data="[{key:'图纸错误',value:'图纸错误'},{key:'设计不合理',value:'设计不合理'},
                                   {key:'工艺不合理',value: '工艺不合理'},{key:'其他',value: '其他'}]"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">机型类别：</td>
                            <td style="width: 33%">
                                <input id="jxlb" name="jxlb" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                                       textField="value" valueField="key" emptyText="请选择..."
                                       data="[{key:'大挖',value:'大挖'}
                                       ,{key:'中挖',value:'中挖'}
                                       ,{key:'小挖',value:'小挖'}
                                       ,{key:'微挖',value:'微挖'}
                                       ,{key:'轮挖',value:'轮挖'}
                                       ,{key:'特挖',value:'特挖'}]"
                                />
                            </td>
                            <td style="text-align: center;width: 17%">涉及机型：</td>
                            <td>
                                <input id="involvingModel" name="involvingModel" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">问题简述：</td>
                            <td style="width: 33%;" colspan="3">
                                <textarea id="problemDescription" name="problemDescription" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                          vtype="length:1000" minlen="0" allowinput="true"
                                          emptytext="请输入问题简述" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%;height: 300px">问题附件：</td>
                            <td style="width: 33%;" colspan="3">
                                <div style="margin-top: 10px;margin-bottom: 2px">
                                    <a id="addFile" class="mini-button" onclick="addZlgjjysbFile()">添加附件</a>
                                    <span style="color: red">注：添加附件前，请先进行草稿的保存</span>
                                </div>
                                <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                                     idField="id" url="${ctxPath}/zlgjNPI/core/zlgjjysb/queryZlgjjysbFileList.do?zlgjjysbId=${zlgjjysbId}"
                                     autoload="true"
                                     multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                                    <div property="columns">
                                        <div type="indexcolumn" align="center" width="20">序号</div>
                                        <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                        <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                        <div field="creator" width="100" headerAlign="center" align="center">上传者</div>
                                        <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center">上传时间</div>
                                        <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="yyfx">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="yyfxCheckbox" onclick="toggleZlgjjysbFieldset(this, 'yyfx')" hideFocus/>
                        原因分析
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 17%">原因分析：</td>
                            <td>
                                <textarea id="reasonAnalysis" name="reasonAnalysis" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                          vtype="length:1000" minlen="0" allowinput="true"
                                          emptytext="请输入原因分析" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="suggestion">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="suggestionCheckbox" onclick="toggleZlgjjysbFieldset(this, 'suggestion')" hideFocus/>
                        对策建议方案
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 17%">对策建议方案：</td>
                            <td>
                                <textarea id="dcjyfa" name="dcjyfa" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                          vtype="length:1000" minlen="0" allowinput="true"
                                          emptytext="请输入对策建议方案" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>

                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="cbsh">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="cbshCheckbox" onclick="toggleZlgjjysbFieldset(this, 'cbsh')" hideFocus/>
                        初步审核
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 17%">建议对策部门：</td>
                            <td>
                                <input id="suggestDepartmentId" name="suggestDepartmentId" class="mini-dep rxc" plugins="mini-dep"
                                       style="width:98%;height:34px"
                                       allowinput="false" textname="suggestDepartment" length="200" maxlength="200" minlen="0" single="false"
                                       initlogindep="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">审核意见：</td>
                            <td>
                                 <textarea id="preliminaryAudit" name="preliminaryAudit" class="mini-textarea rxc" plugins="mini-textarea"
                                           style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                           vtype="length:1000" minlen="0" allowinput="true"
                                           emptytext="" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="dcbmsh">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="dcbmshCheckbox" onclick="toggleZlgjjysbFieldset(this, 'dcbmsh')" hideFocus/>
                        对策部门审核
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 17%;height: 300px;">实施状态：</td>
                            <td>
                                <div class="mini-toolbar" id="dcryButtons">
                                    <a id="addDcry" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addDcry">添加对策人员</a>
                                    <a id="delDcry" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px"
                                       onclick="deleteDcry">移除对策人员</a>
                                </div>
                                <div id="dcryGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                                     idField="id" url="${ctxPath}/zlgjNPI/core/zlgjjysb/queryDcry.do?zlgjjysbId=${zlgjjysbId}"
                                     allowcelledit="false" allowCellWrap="true" allowcellselect="true"
                                     multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true"
                                     oncellvalidation="dcryGridCellValidation" oncellbeginedit="dcryGridCellBeginEdit">
                                    <div property="columns">
                                        <div type="checkcolumn" headerAlign="center" align="center" width="10"></div>
                                        <div type="indexcolumn" headerAlign="center" align="center" width="10">序号</div>
                                        <div field="creator" width="80" headerAlign="center" align="center">设计/室主任</div>
                                        <div field="dcryId" displayField="dcryId_name"
                                             align="center" headerAlign="center">
                                            对策人员
                                            <input property="editor" class="mini-user rxc" plugins="mini-user" allowinput="false"
                                                   mainfield="no" name="dcryId"/>
                                        </div>
                                        <div type="comboboxcolumn" field="implementationStatus" headerAlign="center" align="center">实施状态<span
                                                style="color: #ff0000">*</span>
                                            <input property="editor" class="mini-combobox"
                                                   data="[{id:'ljss',text:'立即实施'},{id:'xydjxss',text:'下一代机型实施'},{id:'yss',text:'已实施'}]"/>
                                        </div>
                                        <div field="ssContent" headerAlign="center" align="center">实施内容描述
                                            <input property="editor" class="mini-textarea"/>
                                        </div>
                                        <div field="auditSuggest" headerAlign="center" align="center">审核意见（更改通知编号）
                                            <input property="editor" class="mini-textarea"/>
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="zlsh">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="zlshCheckbox" onclick="toggleZlgjjysbFieldset(this, 'zlsh')" hideFocus/>
                        质量审核评定
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 17%">等级评定：</td>
                            <td>
                                <input id="rating" name="rating" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                                       textField="value" valueField="key" emptyText="请选择..."
                                       data="[{key:'一级',value:'一级'},{key:'二级',value:'二级'},{key:'三级',value:'三级'},{key:'四级',value:'四级'},{key:'五级',value:'五级'}]"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">审核意见：</td>
                            <td>
                                 <textarea id="zlshshyj" name="zlshshyj" class="mini-textarea rxc" plugins="mini-textarea"
                                           style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                           vtype="length:1000" minlen="0" allowinput="true"
                                           emptytext="" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="ssqr">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="ssqrCheckbox" onclick="toggleZlgjjysbFieldset(this, 'ssqr')" hideFocus/>
                        改进实施确认
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 17%">实施情况确认：</td>
                            <td>
                                <input id="ssqk" name="ssqk" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                                       textField="value" valueField="key" emptyText="请选择..."
                                       data="[{key:'已实施',value:'已实施'},{key:'下一代实施',value:'下一代实施'}
                                       ,{key:'超过三个月未实施',value:'超过三个月未实施'}]"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">已实施完成时间：</td>
                            <td>
                                <input id="sswcTime" name="sswcTime" class="mini-datepicker" allowInput="false" style="width:10%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">改进实施确认意见：</td>
                            <td>
                                 <textarea id="gjssqryj" name="gjssqryj" class="mini-textarea rxc" plugins="mini-textarea"
                                           style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="500"
                                           vtype="length:500" minlen="0" allowinput="true"
                                           emptytext="" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var zlgjjysbForm = new mini.Form("#zlgjjysbForm");
    var zlgjjysbId = "${zlgjjysbId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var mainGroupId = "${mainGroupId}";
    var mainGroupName = "${mainGroupName}";
    var mobile = "${mobile}";
    var fileListGrid = mini.get("fileListGrid");
    var nodeVarsStr = '${nodeVars}';
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var dcryGrid = mini.get("dcryGrid");


    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    //作废
    function discardInst() {
        if (!confirm('流程作废后不可恢复，确定要继续吗?')) {
            return;
        }
        //处理质量改进建议上报中对CRM系统的闭环
        $.ajax({
            url: __rootPath + '/zlgjNPI/core/zlgjjysb/callBackOutSystem.do?instId=' +
            mini.get("instId").getValue() + '&okOrNot=2&zlshshyjMock=' + mini.get("zlshshyj").getValue(),
            type: 'post',
            async: true,
            contentType: 'application/json',
            success: function (result) {
                var isAopSuccess = result.success;
                var isAopSuccessMessage = result.message;
                if (isAopSuccess) {
//                    alert("模拟作废");
                    _SubmitJson({
                        url: __rootPath + '/bpm/core/bpmInst/discardInst.do',
                        data: {
                            instId: mini.get("instId").getValue()
                        },
                        method: 'POST',
                        success: function () {
                            CloseWindow();
                            window.opener.grid.reload();
                            window.opener.mini.showTips({
                                content: "<b>成功</b> <br/>流程实例已作废",
                                state: 'warning',
                                x: 'center',
                                y: 'center',
                                timeout: 3000
                            });
                        }
                    })
                } else {
                    mini.alert(isAopSuccessMessage);
                }
            }
        });
    }
</script>
</body>
</html>
