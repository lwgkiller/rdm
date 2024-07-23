<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>测试计划</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
        <a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"
           onclick="saveData()">保存</a>
        <a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif"
           onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;">
        <form id="planForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    委外整机认证测试试验计划
                </caption>
                <tbody>
                <tr class="firstRow displayTr">
                    <td align="center"></td>
                    <td align="left"></td>
                    <td align="center"></td>
                    <td align="left"></td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        认证计划编号：
                    </td>
                    <td align="left" colspan="3">
                        <input name="planCode" class="mini-textbox rxc" plugins="mini-textbox"
                                only_read="false" allowinput="true" value="" readonly emptytext="后台自动生成..."
                               style="width:100%;height:34px"/>
                    </td>
                <tr>
                    <td style="text-align: center;width: 20%">部门：</td>
                    <td style="min-width:170px">
                        <input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="部门" textname="deptName"
                               length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false"
                               showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px" enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">产品主管：</td>
                    <td style="min-width:170px">
                        <input id="charger" name="charger" textname="chargerName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;"
                               allowinput="false"
                               label="编制人" length="50" mainfield="no" single="true" enabled="true"/>
                    </td>
                <tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        型号<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="left">
                        <input name="productModel" class="mini-textbox rxc" plugins="mini-textbox"
                               required="true" only_read="false" allowinput="true" value=""
                               style="width:100%;height:34px"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        认证类别<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="certType" name="certType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="认证类别："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=RZLB"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        全新/补测<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="testType" name="testType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="测试类别："
                               length="50"  onvaluechanged="remarkTip"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=CSLB"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style="text-align: center">
                        预计时间<span style="color: #ff0000">*</span>：
					</td>
                    <td >
                        <input id="yearMonth" class="mini-monthpicker"  style="width: 100%" allowinput="false"
                               name="yearMonth"    onvaluechanged="yearMonthChange"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        产品描述：
                    </td>
                    <td align="left" colspan="3">
                        <input id="remark" name="remark" class="mini-textbox rxc" plugins="mini-textbox"
                                only_read="false" allowinput="true" value=""
                               style="width:100%;height:34px"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyObj = ${applyObj};
    var action = '${action}';
    var planForm = new mini.Form("#planForm");
    $(function () {
        planForm.setData(applyObj);
    })

    function saveData() {
        planForm.validate();
        if (!planForm.isValid()) {
            return;
        }
        var remark = mini.get('remark').getValue();
        if(!remark){
            var testType = mini.get('testType').getValue();
            if(testType=='bc'){
                mini.alert('请备注写明参考机型和更改内容！农机型式试验，需填写与同型号液压挖掘机之间是否有区别，若有区别需填写更改内容');
                return;
            }
        }
        var formData = planForm.getData();
        var config = {
            url: jsUseCtxPath + "/wwrz/core/testPlan/save.do",
            method: 'POST',
            data: formData,
            success: function (result) {
                //如果存在自定义的函数，则回调
                var result = mini.decode(result);
                if (result.success) {
                    CloseWindow('ok');
                } else {
                }
                ;
            }
        }
        _SubmitJson(config);
    }

    function remarkTip(e) {
        var obj = e.selected;
        if(obj.key_== 'bc'){
            mini.alert("请写明参考机型和更改内容！农机型式试验，需填写与同型号液压挖掘机之间是否有区别，若有区别需填写更改内容");
        }
    }
    function yearMonthChange(e) {
        var obj = e.selected;
        var yearMonth = e.sender.text;
        var currentYearMonth = getCurrentYearMonth();
        var afterYearMonth = addMonths(currentYearMonth,3);
        if(yearMonth<=currentYearMonth||yearMonth>afterYearMonth){
            mini.alert("只允许选择未来三个月数据，有疑问请联系标准技术所！");
            mini.get('yearMonth').setValue("");
            return
        }
    }
</script>
</body>
</html>
