<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>应用创新分配信息编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" style="display: none" onclick="saveBusiness()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="businessForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="mainId" name="mainId"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">应用创新分配信息编辑</caption>
                <tr>
                    <td style="text-align: center;width: 15%">主管所长：</td>
                    <td style="min-width:170px">
                        <input id="suoZhangId" name="suoZhangId" textname="suoZhang" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="1000" maxlength="1000"
                               mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 15%">创新人：</td>
                    <td style="min-width:170px">
                        <input id="chuangXinRenId" name="chuangXinRenId" textname="chuangXinRen" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="1000" maxlength="1000"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" style="text-align: center;width: 15%;color:red">
                        是否和本所无关(请谨慎选择！如果一旦勾选！则不用选择创新人！相应创新任务将不会产生待办！)：
                    </td>
                    <td colspan="2">
                        <input id="isNothing" name="isNothing" class="mini-checkbox" style="width:98%;"
                               trueValue="是" falseValue="否"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">创新方案情况</td>
                    <td style="min-width:170px">
                        <input id="chuangXinFangAn" name="chuangXinFangAn" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=chuangXinFangAn"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 20%">创新技术交底书时间</td>
                    <td style="min-width:170px">
                        <input id="chuangXinJiaoDiDate" name="chuangXinJiaoDiDate" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">创新情况说明：</td>
                    <td colspan="3">
                        <textarea id="chuangXinQingKuang" name="chuangXinQingKuang" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">创新分析完成日期</td>
                    <td style="min-width:170px">
                        <input id="chuangXinWanChengDate" name="chuangXinWanChengDate" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">创新评价</td>
                    <td style="min-width:170px">
                        <input id="chuangXinPingFen" name="chuangXinPingFen"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=interpretationEvaluate"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">不合格或驳回时填写意见：</td>
                    <td colspan="3">
                        <textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">技术交底书编号</td>
                    <td style="min-width:170px">
                        <input id="chuangXinJiaoDiShuNo" name="chuangXinJiaoDiShuNo" textname="chuangXinJiaoDiShuNo"
                               style="width:99.5%;height:34px;"
                               class="mini-buttonedit" allowInput="false" showClose="true" onbuttonclick="SelectPatentNameMine"
                               oncloseclick="ColsePatentNameMine" enabled="fales"/>
                    </td>
                    <td style="text-align: center;width: 20%">交底书所属IPC主分类号(审批通过后由IPR填写)</td>
                    <td style="min-width:170px">
                        <input id="chuangXinJiaoDiShuIPCMainNo" name="chuangXinJiaoDiShuIPCMainNo" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessForm = new mini.Form("#businessForm");
    var mainId = "${mainId}";
    var businessId = "${businessId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}"
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var isIPR = "${isIPR}";
    //..
    $(function () {
        if (businessId || mainId) {
            var url = jsUseCtxPath + "/zhgl/core/patentInterpretation" +
                "/getItemDetail.do?businessId=" + businessId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    businessForm.setData(json);
                    //不同场景的处理
                    if (action == 'detail') {
                        businessForm.setEnabled(false);
                    } else if (action == 'add') {
                        mini.get("saveBusiness").show();
                        businessForm.setEnabled(false);
                        mini.get("suoZhangId").setEnabled(true);
                    } else if (action == 'edit') {
                        mini.get("saveBusiness").show();
                        businessForm.setEnabled(false);
                        if (isIPR == "true") {
                            mini.get("chuangXinJiaoDiShuIPCMainNo").setEnabled(true);
                        }
                        if (mini.get("suoZhangId").getValue() == currentUserId) {
                            mini.get("chuangXinRenId").setEnabled(true)
                            mini.get("chuangXinWanChengDate").setEnabled(true)
                            mini.get("chuangXinPingFen").setEnabled(true);
                            mini.get("remark").setEnabled(true);
                            mini.get("isNothing").setEnabled(true);
                        } else if (mini.get("chuangXinRenId").getValue() == currentUserId) {
                            mini.get("chuangXinFangAn").setEnabled(true);
                            mini.get("chuangXinQingKuang").setEnabled(true);
                            mini.get("chuangXinJiaoDiDate").setEnabled(true);
                            mini.get("chuangXinJiaoDiShuNo").setEnabled(true);
                        } else {
                            mini.get("suoZhangId").setEnabled(true);
                            mini.get("isNothing").setEnabled(true);
                        }
                    }
                }
            });
        }
    });

    //..
    function saveBusiness() {
        var postData = businessForm.getData();
        //新增的时候取传进来的mainId
        if (!postData.mainId || postData.mainId == '') {
            postData.mainId = mainId;
        }
        postData.chuangXinJiaoDiDate = mini.get("chuangXinJiaoDiDate").getText();
        postData.chuangXinWanChengDate = mini.get("chuangXinWanChengDate").getText();
        $.ajax({
            url: jsUseCtxPath + "/zhgl/core/patentInterpretation/saveItem.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
//                        if (returnData.success) {
//                            var url = jsUseCtxPath + "/zhgl/core/patentInterpretation" +
//                                "/itemPage.do?businessId=" + returnData.data + "&action=edit";
//                            window.location.href = url;
//                        }
                        window.close();
                    });
                }
            }
        });
    }

    //..需要定制因此不能直接用_OnMiniDialogShow
    // function SelectPatentNameMine() {
    //     _CommonDialog({
    //         title: "选择我司对应专利",
    //         height: 600,
    //         width: 1200,
    //         dialogKey: "我司专利名称-申请号-提案号-IPC主分类号",
    //         ondestroy: function (action) {
    //             var billNo = "";
    //             var ipcZflh = "";
    //             var win = this.getIFrameEl().contentWindow;
    //             var data = win.getData();
    //             var rows = data.rows;
    //             if (rows != null && rows.length > 0) {
    //                 for (i = 0; i < rows.length; i++) {
    //                     billNo += rows[i].billNo + ";"
    //                     ipcZflh += rows[i].ipcZflh + ";"
    //                 }
    //                 billNo = billNo.substring(0, billNo.length - 1);
    //                 ipcZflh = ipcZflh.substring(0, ipcZflh.length - 1);
    //                 mini.get("chuangXinJiaoDiShuNo").setValue(billNo);
    //                 mini.get("chuangXinJiaoDiShuNo").setText(billNo);
    //                 mini.get("chuangXinJiaoDiShuIPCMainNo").setValue(ipcZflh);
    //             }
    //         }
    //     });
    // }
    function SelectPatentNameMine() {
        _CommonDialog({
            title: "选择技术交底书",
            height: 600,
            width: 1200,
            dialogKey: "专利申请技术交底书",
            ondestroy: function (action) {
                debugger;
                var win = this.getIFrameEl().contentWindow;
                var data = win.getData();
                var row = data.rows[0];
                mini.get("chuangXinJiaoDiShuNo").setValue(row.jdsNum);
                mini.get("chuangXinJiaoDiShuNo").setText(row.jdsNum);
            }
        });
    }

    function ColsePatentNameMine() {
        mini.get("chuangXinJiaoDiShuNo").setValue("");
        mini.get("chuangXinJiaoDiShuNo").setText("");
        mini.get("chuangXinJiaoDiShuIPCMainNo").setValue("");
    }
</script>
</body>
</html>
