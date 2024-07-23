<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>外购件资料收集看板信息采集</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" onclick="saveBusiness()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="purchasedPartsForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">外购件资料收集看板信息采集</caption>
                <tr>
                    <td style="text-align: center;width: 15%">资料类型：</td>
                    <td style="min-width:170px">
                        <input id="dataType" name="dataType"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringPurchasedPartsDataType"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">物料编码：</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">物料描述：</td>
                    <td style="min-width:170px">
                        <input id="materialDescription" name="materialDescription" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">所属部门：</td>
                    <td style="min-width:170px">
                        <input id="departmentId"
                               name="departmentId"
                               class="mini-buttonedit icon-dep-button"
                               required="true" allowInput="false"
                               onbuttonclick="selectMainDep" style="width:98%"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">科室：</td>
                    <td style="min-width:170px">
                        <input id="section" name="section" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">设计型号：</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">供应商：</td>
                    <td style="min-width:170px">
                        <input id="supplier" name="supplier" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">联系人：</td>
                    <td style="min-width:170px">
                        <input id="contact" name="contact" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">联系方式：</td>
                    <td style="min-width:170px">
                        <input id="contactInformation" name="contactInformation" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">产品所责任人：</td>
                    <td style="min-width:170px">
                        <input id="productOwner" name="productOwner" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">收集信息提交日期：</td>
                    <td style="min-width:170px">
                        <input id="collectionInformationSubmissionDate" name="collectionInformationSubmissionDate"
                               class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">服务工程责任人：</td>
                    <td style="min-width:170px">
                        <input id="serviceEngineeringOwner" name="serviceEngineeringOwner" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">一级响应限期提供时间：</td>
                    <td style="min-width:170px">
                        <input id="firstLevelDeadline" name="firstLevelDeadline"
                               class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">一级响应是否提供：</td>
                    <td style="min-width:170px">
                        <input id="firstLevelProvided" name="firstLevelProvided" class="mini-checkbox"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">二级响应限期提供时间：</td>
                    <td style="min-width:170px">
                        <input id="secondLevelDeadline" name="secondLevelDeadline"
                               class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">二级响应是否提供：</td>
                    <td style="min-width:170px">
                        <input id="secondLevelProvided" name="secondLevelProvided" class="mini-checkbox"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">是否已归档：</td>
                    <td style="min-width:170px">
                        <input id="isFiled" name="isFiled" class="mini-checkbox"/>
                    </td>
                    <td style="text-align: center;width: 15%">三级响应是否提供：</td>
                    <td style="min-width:170px">
                        <input id="thirdLevelProvided" name="thirdLevelProvided" class="mini-checkbox"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">是否已制作：</td>
                    <td style="min-width:170px">
                        <input id="isMade" name="isMade" class="mini-checkbox"/>
                    </td>
                    <td style="text-align: center;width: 15%">是否需要下通报：</td>
                    <td style="min-width:170px">
                        <input id="isNeedInform" name="isNeedInform" class="mini-checkbox"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">备注：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px">
                        </textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var purchasedPartsForm = new mini.Form("#purchasedPartsForm");
    var businessId = "${businessId}";
    var action = "${action}";
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //选择主部门
    function selectMainDep(e) {
        var b = e.sender;
        _TenantGroupDlg('1', true, '', '1', function (g) {
            b.setValue(g.groupId);
            b.setText(g.name);
        }, false);

    }
    //..
    $(function () {
        if (businessId) {
            var url = jsUseCtxPath + "/serviceEngineering/core/purchasedParts/queryDataById.do?businessId=" + businessId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    purchasedPartsForm.setData(json);
                }
            });
        }
    });
    //..
    function saveBusiness() {
        var postData = {};
        postData.id = $("#id").val();
        postData.dataType = mini.get("dataType").getValue();
        postData.materialCode = mini.get("materialCode").getValue();
        postData.materialDescription = mini.get("materialDescription").getValue();
        postData.departmentId = mini.get("departmentId").getValue();
        postData.section = mini.get("section").getValue();
        postData.designModel = mini.get("designModel").getValue();
        postData.supplier = mini.get("supplier").getValue();
        postData.contact = mini.get("contact").getValue();
        postData.contactInformation = $.trim(mini.get("contactInformation").getValue());
        postData.productOwner = $.trim(mini.get("productOwner").getValue());
        postData.collectionInformationSubmissionDate = $.trim(mini.get("collectionInformationSubmissionDate").getText());
        postData.serviceEngineeringOwner = $.trim(mini.get("serviceEngineeringOwner").getValue());
        postData.firstLevelDeadline = $.trim(mini.get("firstLevelDeadline").getText());
        postData.firstLevelProvided = $.trim(mini.get("firstLevelProvided").getValue());
        postData.secondLevelDeadline = $.trim(mini.get("secondLevelDeadline").getText());
        postData.secondLevelProvided = $.trim(mini.get("secondLevelProvided").getValue());
        postData.isFiled = $.trim(mini.get("isFiled").getValue());
        postData.thirdLevelProvided = $.trim(mini.get("thirdLevelProvided").getValue());
        postData.isMade = $.trim(mini.get("isMade").getValue());
        postData.isNeedInform = $.trim(mini.get("isNeedInform").getValue());
        postData.remark = $.trim(mini.get("remark").getValue());
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/purchasedParts/saveData.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            window.close();
                        }
                    });
                }
            }
        });
    }


</script>
</body>
</html>
