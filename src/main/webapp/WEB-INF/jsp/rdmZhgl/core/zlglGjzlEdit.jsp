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
    <title>创建国际专利</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/rdmZhgl/zlglGjzlEdit.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
    <div id="toolBar" class="topToolBar">
        <div>
            <a id="saveNewGjzl" class="mini-button" style="" onclick="saveNewGjzl()">保存</a>
            <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
        </div>
    </div>
    <div class="mini-fit">
        <div class="form-container" style="margin: 0 auto;width: 100%;">
            <form id="formGjzl" method="post" >
                <input id="gjzlId" name="gjzlId" class="mini-hidden"/>
                <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                    <tr>
                        <td style="width: 12%">PCT名称：</td>
                        <td style="width: 20%">
                            <input id="pctName" name="pctName"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 12%" >英文名称：</td>
                        <td style="width: 20%">
                            <input id="englishName" name="englishName"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 12%">国际申请号：</td>
                        <td style="width: 20%;">
                            <input id="applictonNumber" name="applictonNumber"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 12%">当前状态：</td>
                        <td style="width: 20%;">
                            <input id="gjztId" name="gjztId" class="mini-combobox" style="width:98%;"
                                   textField="enumName" valueField="id" emptyText="请选择..."
                                   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..." />
                        </td>
                        <td style="width: 12%">国际申请日：</td>
                        <td style="width: 20%;min-width:140px">
                        <input id="applictonDay" name="applictonDay"  class="mini-datepicker" dateFormat="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" />
                        </td>
                        <td style="width: 12%">国际公开日：</td>
                        <td style="width: 20%;">
                            <input id="openDay" name="openDay"  class="mini-datepicker" dateFormat="yyyy-MM-dd"
                                   showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 12%">国际公开号：</td>
                        <td style="width: 20%;">
                            <input id="openNumber" name="openNumber"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 12%">指定国家：</td>
                        <td style="width: 20%;">
                            <input id="theCountry" name="theCountry"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 12%">国家局申请号：</td>
                        <td style="width: 20%;">
                            <input id="nationalNamber" name="nationalNamber"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 12%">国家局公开号：</td>
                        <td style="width: 20%;min-width:140px">
                            <input id="nationOpenDay" name="nationOpenDay"  class="mini-textbox" style="width:98%;"/>
                        </td>
                        <td style="width: 12%">国家局公开日：</td>
                        <td style="width: 20%;">
                            <input id="nationOpenNumbei" name="nationOpenNumbei"  class="mini-datepicker" dateFormat="yyyy-MM-dd"
                                    showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" />
                        </td>
                        <td style="width: 12%">国家授权号：</td>
                        <td style="width: 20%;min-width:140px">
                            <input id="authorizedNumber" name="authorizedNumber"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 12%">国家授权日：</td>
                        <td style="width: 20%;">
                            <input id="authorizedDay" name="authorizedDay"  class="mini-datepicker" dateFormat="yyyy-MM-dd"
                                    showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" />
                        </td>
                        <td style="width: 12%">发明人：</td>
                        <td style="width: 20%;min-width:140px">
                            <input id="theInventor" name="theInventor"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 12%">专利权人/申请人：</td>
                        <td style="width: 20%;min-width:140px">
                            <input id="thePatentee_theApplicane" name="thePatentee_theApplicane"  class="mini-textbox" style="width:98%;"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 12%">代理机构：</td>
                        <td style="width: 20%;">
                            <input id="theAgency" name="theAgency"  class="mini-textbox" style="width:98%;" />
                        </td>
                        <td style="width: 12%">是否已申请国家专利：</td>
                        <td style="width: 20%;">
                        <input id="appliedNational" name="appliedNational" class="mini-combobox" style="width:98%;"
                        textField="enumName" valueField="id" emptyText="请选择..."
                        required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                        </td>
                        <td style="width: 12%">对应的国内专利号：</td>
                        <td style="width: 20%;min-width:140px">
                            <input id="correspondingNumber" name="correspondingNumber"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 12%">专利名称：</td>
                        <td style="width: 20%;min-width:140px">
                            <input id="patentName" name="patentName"  class="mini-textbox" style="width:98%;"/>
                        </td>
                        <td style="width: 12%">是否已进国家阶段：</td>
                        <td style="width: 20%;">
                            <input id="nationalStage" name="nationalStage"  class="mini-combobox" style="width:98%;"
                                   textField="enumName" valueField="id" emptyText="请选择..."
                                   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..." />
                        </td>
                        <td style="width: 12%">授权奖励金额：</td>
                        <td style="width: 20%;min-width:140px">
                            <input id="rewardAmount" name="rewardAmount"  class="mini-textbox" style="width:98%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 12%">授权奖励时间：</td>
                        <td style="width: 20%;min-width:140px">
                            <input id="rewardTime" name="rewardTime"  class="mini-datepicker" dateFormat="yyyy-MM-dd"
                                   showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" />
                        </td>
                        <td style="width: 12%">我司发明人：</td>
                        <td style="width: 20%;min-width:140px">
                            <input id="myCompanyUserIds" name="myCompanyUserIds" textname="myCompanyUserNames" class="mini-user rxc" plugins="mini-user"  style="width:98%;"
                                   allowinput="false" label="可见范围" length="1000" maxlength="1000"  mainfield="no"  single="false"/>
                        </td>
                        <td style="width: 12%">专利证书附件：</td>
                        <td style="width: 20%;" colspan="3">
                            <a id="yyaluploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;" onclick="addZhengShuFile">添加附件</a>
                        </td>
                    </tr>
                    <tr id="jiaofeiTr" style="display: none">
                        <td style="text-align: center;height: 300px">缴费部分：</td>
                        <td  colspan="5">
                            <div class="mini-toolbar" id="planButtons" >
                                <a id="addJiaoFei" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addGJJiaoFei">添加缴费</a>
                                <a id="saveJiaoFei" class="mini-button btn-yellow" style="margin-bottom: 5px;margin-top: 5px" onclick="saveGJJiaoFei">保存缴费</a>
                                <a id="deleteMJiaoFei" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="deleteMJiaoFei">删除缴费</a>
                            </div>
                            <div id="jiaoFeiListGrid" class="mini-datagrid" style="width: 98%; height: 85%" allowResize="false" allowCellWrap="true"
                                 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                                 multiSelect="false" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                                 allowAlternating="true" oncellvalidation="onCellValidation">
                                <div property="columns">
                                    <div type="checkcolumn" width="10"></div>
                                    <div type="indexcolumn" headerAlign="center" align="center" width="10">序号</div>
                                    <div field="jflbId" displayField="enumName" align="center" headerAlign="center" width="50" renderer="render">缴纳费用类别
                                        <input property="editor" class="mini-combobox" style="width:98%;"
                                               textField="enumName" valueField="id" emptyText="请选择..."
                                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                                    </div>
                                    <div field="paymenAmount"  align="center" headerAlign="center" width="50" renderer="render">缴费金额
                                        <input property="editor" class="mini-textarea"/>
                                    </div>
                                    <div field="paymentTime" align="center" headerAlign="center" width="50"
                                         dateFormat="yyyy-MM-dd">缴费时间
                                        <input property="editor" class="mini-datepicker" dateFormat="yyyy-MM-dd"
                                               showTime="false" showOkButton="true" showClearButton="false"/>
                                    </div>
                                    <div field="meetingPlanCompletion" align="center" headerAlign="center" width="50" renderer="renderAttachFile">缴费票据
                                        <%--<input property="editor"  class="mini-button" style="margin-bottom: 5px;margin-top: 5px;" onclick="addJiaoFeiFile" />--%>
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
        var status="${status}";
        var action="${action}";
        var zlUseCtxPath="${ctxPath}";
        var formGjzl = new mini.Form("#formGjzl");
        var gjzlId="${gjzlId}";
        var nodeVarsStr='${nodeVars}';
        var currentUserName="${currentUserName}";
        var currentTime="${currentTime}";
        var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
        var gjzlObj=${gjzlObj};
        var currentUserId="${currentUserId}";
        var currentUserName="${currentUserName}";
        var currentUserMainGroupId="${currentUserMainGroupId}";
        var currentUserMainGroupName="${currentUserMainGroupName}";
        var jiaoFeiListGrid = mini.get("jiaoFeiListGrid");
        var showJiaofei=${showJiaofei};

        jiaoFeiListGrid.on("cellbeginedit", function (e) {
            var record = e.record;
            if(action=='edit' ||action=='') {
                if (e.field == "jflbId") {
                    e.editor.setData(achieveTypeList);
                }
            }else {
                e.editor.setEnabled(false);
            }
        });
        function renderAttachFile(e) {
            var record = e.record;
            var fyid = record.fyId;
            var gjzlId=record.gjzlId;
            if(!fyid) {
                return '';
            }
            var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
                'onclick="addJiaoFeiFile(\'' + fyid +'\',\''+gjzlId+ '\')">附件</a>';
            return s;
        }

    </script>
</body>
</html>
