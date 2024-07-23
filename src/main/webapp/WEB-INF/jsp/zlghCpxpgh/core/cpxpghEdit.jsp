<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <title>产品型谱规划表单</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/zlghCpxpgh/cpxpghEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <style type="text/css">
        fieldset {
            border:solid 1px #aaaaaab3;
            min-width: 920px;
        }
        .hideFieldset {
            border-left:0;
            border-right:0;
            border-bottom:0;
        }
        .hideFieldset .fieldset-body {
            display:none;
        }
    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="cpxpghProcessInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="cpxpghForm" method="post" style="height: 95%;width: 100%">
            <input class="mini-hidden" id="id" name="id" />
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="finalId" name="finalId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="step" name="step" class="mini-hidden"/>
            <input id="changeId" name="changeId" class="mini-hidden"/>
            <fieldset id="cpxh">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="cpxhCheckbox" onclick="toggleCpxpghFieldset(this, 'cpxh')" hideFocus/>
                        产品型号
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 14%">部门：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="departmentId" name="departmentId" class="mini-dep rxc" plugins="mini-dep"
                                       style="width:98%;height:34px"
                                       allowinput="false" textname="department" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
                            </td>
                            <td style="text-align: center;width: 14%">销售型号：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="salesModel" name="salesModel"  class="mini-textbox" style="width:98%;" />
                            </td>
                            <td style="text-align: center;width: 14%">设计型号：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="designModel" name="designModel"  class="mini-textbox" style="width:98%;" />
                            </td>
                        </tr>
                        <tr>

                            <td style="text-align: center;width: 14%">产品状态：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="productStatus" name="productStatus" class="mini-combobox" style="width:98%"
                                       textField="value" valueField="key" emptyText="请选择..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                       data="[{key:'lc',value:'量产'},{key:'kfz',value:'开发中'},{key:'ztkf',value:'暂停开发'},{key:'tc',value:'停产'}]"
                                />
                            </td>
                            <td style="text-align: center;width: 14%">可售年份：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="salsesYear" name="salsesYear"  class="mini-textbox" style="width:98%;" />
                            </td>
                        </tr>
                        <tr>

                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="cpxszt" >
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="cpxsztCheckbox" onclick="toggleCpxpghFieldset(this, 'cpxszt')" hideFocus/>
                        产品销售状态
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <div class="mini-toolbar" id="salesAreaButtons">
                        <a class="mini-button"   plain="true" onclick="addSalesArea">添加</a>
                        <a class="mini-button btn-red"  plain="true" onclick="delSalesArea">删除</a>
                    </div>
                    <div id="cpxsztGrid" class="mini-datagrid"  allowResize="false" allowCellValid="true"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false" oncellvalidation="onCellValidation"
                         multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true" showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" headerAlign="center" align="center"></div>
                            <div type="indexcolumn" headerAlign="center" align="center">序号</div>
                            <div field="childName" vtype="required" displayField="childName" headerAlign="center" align="center">销售区域
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="childValue" vtype="required" name="childValue" type="comboboxcolumn" headerAlign="center" align="center">
                                销售状态
                                <input property="editor" class="mini-combobox" data="comboboxData" required="true"/>
                            </div>
                        </div>

                    </div>
                </div>
            </fieldset>
            <br>
            <fieldset id="cpzypz" >
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="cpzypzCheckbox" onclick="toggleCpxpghFieldset(this, 'cpzypz')" hideFocus/>
                        产品主要配置
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <div class="mini-toolbar" id="cppzButtons">
                        <a class="mini-button"   plain="true" onclick="addCppz">添加</a>
                        <a class="mini-button btn-red"  plain="true" onclick="delCppz">删除</a>
                    </div>
                    <div id="cppzGrid" class="mini-datagrid"  allowResize="false" allowCellValid="true"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false" oncellvalidation="onCellValidation"
                         multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true" showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" headerAlign="center" align="center"></div>
                            <div type="indexcolumn" headerAlign="center" align="center">序号</div>
                            <div field="childName" vtype="required" displayField="childName" headerAlign="center" align="center">
                                配置名称
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="childValue" vtype="required" displayField="childValue" headerAlign="center" align="center">
                                配置型号
                                <input property="editor" class="mini-textbox"/>
                            </div>
                        </div>

                    </div>
                </div>
            </fieldset>
            <br>
            <fieldset id="cpcbxx" >
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="cpcbxxCheckbox" onclick="toggleCpxpghFieldset(this, 'cpcbxx')" hideFocus/>
                        产品成本信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 14%">整机未税成本：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="zjwsCost" name="zjwsCost"  class="mini-textbox" style="width:98%;" />
                            </td>
                            <td style="text-align: center;width: 14%">整机销售价格：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="zjSalePrice" name="zjSalePrice"  class="mini-textbox" style="width:98%;" />
                            </td>
                            <td style="text-align: center;width: 14%">整机边际贡献率：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="zjbjgxRate" name="zjbjgxRate"  class="mini-textbox" style="width:98%;" />
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="cpjszt" >
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="cpjsztCheckbox" onclick="toggleCpxpghFieldset(this, 'cpjszt')" hideFocus/>
                        产品成本信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <%--<tr>--%>
                            <%--<td style="text-align: center;width: 14%;height: 300px">产品文件归档：</td>--%>
                            <%--<td colspan="5">--%>
                                <%--<div style="margin-top: 10px;margin-bottom: 2px">--%>
                                    <%--<a id="addFile" class="mini-button"  onclick="addCpxpghFile()">添加</a>--%>
                                <%--</div>--%>

                                <%--<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"--%>
                                     <%--idField="id"  autoload="true" url="${ctxPath}/strategicplanning/core/cpxpgh/queryCpxpghFileList.do?cpxpghId=${cpxpghId}"--%>
                                     <%--multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true" >--%>
                                    <%--<div property="columns">--%>
                                        <%--<div type="indexcolumn" align="center"  width="20">序号</div>--%>
                                        <%--<div field="fileName"  width="140" headerAlign="center" align="center" >附件名称</div>--%>
                                        <%--<div field="fileSize"  width="80" headerAlign="center" align="center" >附件大小</div>--%>
                                        <%--<div field="creator" width="100" headerAlign="center" align="center">上传者</div>--%>
                                        <%--<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center">上传时间</div>--%>
                                        <%--<div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>--%>
                                    <%--</div>--%>
                                <%--</div>--%>
                            <%--</td>--%>
                        <%--</tr>--%>
                        <tr>
                            <td style="text-align: center;width: 14%">产品归档文件：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="archivedFile" name="archivedFile" class="mini-combobox" style="width:98%"
                                       textField="text" valueField="id" emptyText="请选择..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                       data="[{id:'yes',text: '√'}, {id:'no', text:'×'}]"
                                />
                            </td>
                            <td style="text-align: center;width: 14%">环保信息公开：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="hbxxgk" name="hbxxgk" class="mini-combobox" style="width:98%"
                                       textField="text" valueField="id" emptyText="请选择..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                       data="[{id:'yes',text: '√'}, {id:'no', text:'×'}]"
                                />
                            </td>
                            <td style="text-align: center;width: 14%">产品型式试验状态：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="cpxssyzt" name="cpxssyzt" class="mini-combobox" style="width:98%"
                                       textField="text" valueField="id" emptyText="请选择..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                       data="[{id:'yes',text: '√'}, {id:'no', text:'×'}]"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 14%">产品出口认证状态：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="cpckrzzt" name="cpckrzzt" class="mini-combobox" style="width:98%"
                                       textField="text" valueField="id" emptyText="请选择..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                       data="[{id:'tuv',text: '北美TUV'}, {id:'ce', text:'CE'}, {id:'eac', text:'俄罗斯EAC'}]"
                                />
                            </td>
                            <td style="text-align: center;width: 14%">操保手册/零件图册：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="cbsc" name="cbsc" class="mini-combobox" style="width:98%"
                                       textField="text" valueField="id" emptyText="请选择..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                       data="[{id:'yes',text: '√'}, {id:'no', text:'×'}]"
                                />
                            </td>
                            <td style="text-align: center;width: 14%">产品调试状态：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="cptszt" name="cptszt" class="mini-combobox" style="width:98%"
                                       textField="text" valueField="id" emptyText="请选择..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                       data="[{id:'yes',text: '√'}, {id:'no', text:'×'}]"
                                />
                            </td>

                        </tr>
                        <tr>
                            <td style="text-align: center;width: 14%">产品测试考核状态：</td>
                            <td style="width: 14%;min-width:170px">
                                <input id="cpcskhzt" name="cpcskhzt" class="mini-combobox" style="width:98%"
                                       textField="text" valueField="id" emptyText="请选择..."
                                       required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                       data="[{id:'yes',text: '√'}, {id:'no', text:'×'}]"
                                />
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
    var jsUseCtxPath="${ctxPath}";
    var cpxpghForm = new mini.Form("#cpxpghForm");
    var cpxsztGrid = mini.get("cpxsztGrid");
    var cppzGrid = mini.get("cppzGrid");
    var cpxpghId="${cpxpghId}";
    var action="${action}";
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var mainGroupId="${mainGroupId}";
    var mainGroupName="${mainGroupName}";
    // var fileListGrid=mini.get("fileListGrid");
    var nodeVarsStr='${nodeVars}';
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var jsztqrUser='${jsztqrUser}';
    var comboboxData = [{id:'yes',text: '√'}, {id:'no', text:'×'}];
    var changeId = "${changeId}";
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
</body>
</html>
