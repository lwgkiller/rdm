<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/4/13
  Time: 19:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>创建关键技术</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/rdmZhgl/gjjsEdit.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
    <div id="toolBar" class="topToolBar">
        <div>
            <a id="saveNewGjjs" class="mini-button" style="" onclick="saveNewGjjs()">保存</a>
            <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
        </div>
    </div>
    <div class="mini-fit">
        <div class="form-container" style="margin: 0 auto;width: 100%;">
            <form id="formGjjs" method="post" >
                <input id="pId" name="pId" class="mini-hidden"/>
                <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                    <tr>
                        <td style="width: 12%">鉴定时间：<span style="color: #ff0000">*</span></td>
                        <td style="width: 20%;">
                            <input id="jdTime" name="jdTime"  class="mini-monthpicker"
                                   onfocus="this.blur()" style="width:98%;" />
                        </td>
                        <td style="width: 12%">项目名称：<span style="color: #ff0000">*</span></td>
                        <td style="width: 20%;min-width:140px">
                            <input id="proName" name="proName"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 12%">项目经理：</td>
                        <td style="width: 20%;min-width:140px">
                            <input id="proRespUserId" name="proRespUserId" textname="proRespUserName" class="mini-user rxc"
                                   plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                                   mainfield="no"  single="true" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 12%">查新报告：</td>
                        <td style="width: 20%;" colspan="5">
                            <a id="luploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;" onclick="addBaoGaoFile">附件</a>
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 12%">鉴定证书：</td>
                        <td style="width: 20%;" colspan="5">
                            <a id="yyaluploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;" onclick="addZhengShuFile">附件</a>
                        </td>

                    </tr>
                    <tr>
                        <td style="text-align: center;height: 300px">技术部分：</td>
                        <td  colspan="5">
                            <div class="mini-toolbar" id="planButtons" >
                                <a id="addJishu" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addJishu">添加技术</a>
                                <a id="saveJishu" class="mini-button btn-yellow" style="margin-bottom: 5px;margin-top: 5px" onclick="saveJishu">保存技术</a>
                                <a id="deleteJishu" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="deleteJishu">删除技术</a>
                            </div>
                            <div id="jiShuListGrid" class="mini-datagrid" style="width: 98%; height: 85%" allowResize="false" allowCellWrap="true"
                                 idField="id" url="${ctxPath}/zhgl/core/gjjs/getJiShuList.do?pId=${pId}"
                                 multiSelect="true" showPager="false" showColumnsMenu="false" allowcelledit="true" allowCellSelect="true"
                                 allowAlternating="true" oncellvalidation="onCellValidation">
                                <div property="columns">
                                    <div type="checkcolumn" width="10"></div>
                                    <div type="indexcolumn" headerAlign="center" align="center" width="10">序号</div>
                                    <div field="jsName"  align="center" headerAlign="center" width="50" renderer="render">技术名称<span style="color: #ff0000">*</span>
                                        <input property="editor" class="mini-textarea"/>
                                    </div>
                                    <div field="jssp" displayField="jssp" align="center" headerAlign="center" width="50" renderer="render">技术水平<span style="color: #ff0000">*</span>
                                        <input property="editor" class="mini-combobox" style="width:120px;"
                                               textField="value" valueField="key" emptyText="请选择..."
                                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                               data="[{'key' : 'GJXJ','value' : '国际先进'},{'key' : 'GJLX','value' : '国际领先'},{'key' : 'GNLX','value' : '国内领先'}]"
                                        />
                                    </div>
                                    <div field="beizhu"  align="center" headerAlign="center" width="50" renderer="render">备注
                                        <input property="editor" class="mini-textarea"/>
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
    <script type="text/javascript">
        mini.parse();
        var action="${action}";
        var zlUseCtxPath="${ctxPath}";
        var formGjjs = new mini.Form("#formGjjs");
        var pId="${pId}";
        var nodeVarsStr='${nodeVars}';
        var currentUserName="${currentUserName}";
        var currentTime="${currentTime}";
        var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
        var gjjsObj=${gjjsObj};
        var currentUserId="${currentUserId}";
        var currentUserName="${currentUserName}";
        var jiShuListGrid = mini.get("jiShuListGrid");
        jiShuListGrid.on("cellbeginedit", function (e) {
            var record = e.record;
            if(action =='detail') {
                e.editor.setEnabled(false);
            }
        });


    </script>
</body>
</html>
