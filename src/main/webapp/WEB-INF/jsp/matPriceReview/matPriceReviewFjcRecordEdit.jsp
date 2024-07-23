
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>非集采信息记录编辑</title>
    <%@include file="/commons/edit.jsp"%>
    <script src="${ctxPath}/scripts/matPriceReview/matPriceReviewRecord.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css" />

    <style>
        body .mini-textbox-disabled .mini-textbox-border
        {
            background: #dedbdb;
            color:#6D6D6D;
            cursor:default;
        }
        body .mini-buttonedit-disabled .mini-buttonedit-border,
        body .mini-buttonedit-disabled .mini-buttonedit-input
        {
            background:#dedbdb;color:#6D6D6D;cursor:default;
        }
    </style>
</head>


<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="commitForm" class="mini-button" onclick="saveRecord()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto">
        <form id="recordForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="reviewId" name="reviewId" class="mini-hidden"/>
            <input id="jclx" name="jclx" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 200px">
                <tr>
                    <td>物料号：<span style="color:red">*</span></td>
                    <td  >
                        <input id="matCode" name="matCode"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td>物料描述：<span style="color:red">*</span></td>
                    <td  >
                        <input id="matName" name="matName"  class="mini-textbox" style="width:85%;" />
                        <image id="syncRecordMatName" src="${ctxPath}/styles/images/同步.png" style="cursor: pointer;vertical-align: middle" title="自动获取" onclick="syncRecordInfo('matName')"/>
                    </td>
                    <td>采购组织：<span style="color:red">*</span></td>
                    <td>
                        <input id="cgzz" name="cgzz" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td  >工厂代码：<span style="color:red">*</span></td>
                    <td  >
                        <input id="gc" name="gc"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td  >供应商代码(SAP)：<span style="color:red">*</span></td>
                    <td  >
                        <input id="applierCode" name="applierCode"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td  >供应商名称：<span style="color:red">*</span></td>
                    <td  >
                        <input name="applierName" id="applierName"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td  >MRP控制者：<span style="color:red">*</span></td>
                    <td  >
                        <input name="mrpkzz" id="mrpkzz" class="mini-textbox" style="width:85%;" onBlur="mrpkzzBlur()"/>
                        <image id="syncRecordMrp" src="${ctxPath}/styles/images/同步.png" style="cursor: pointer;vertical-align: middle" title="自动获取" onclick="syncRecordInfo('mrpkzz')"/>
                    </td>
                    <td  >采购信息记录类型：<span style="color:red">*</span></td>
                    <td  >
                        <input id="recordType" name="recordType" id="recordType" class="mini-textbox" style="width:98%;" />
                    </td>
                    <td  >计划交货时间：<span style="color:red">*</span></td>
                    <td  >
                        <input id="planDeliveryTime" name="planDeliveryTime"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td  >采购组：<span style="color:red">*</span></td>
                    <td  >
                        <input id="cgz" name="cgz"  class="mini-textbox" style="width:85%;" />
                        <image id="syncRecordCgz" src="${ctxPath}/styles/images/同步.png" style="cursor: pointer;vertical-align: middle" title="自动获取" onclick="syncRecordInfo('cgz')"/>
                    </td>
                    <td  >净价：<span style="color:red">*</span></td>
                    <td  >
                        <input id="jingjia" name="jingjia"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td  >币种：<span style="color:red">*</span></td>
                    <td  >
                        <input id="bizhong" name="bizhong"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td  >价格单位：<span style="color:red">*</span></td>
                    <td  >
                        <input name="jgdw" id="jgdw" class="mini-textbox" style="width:98%;" />
                    </td>
                    <td  >单位：<span style="color:red">*</span></td>
                    <td  >
                        <input id="jldw" name="jldw"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td  >税码：<span style="color:red">*</span></td>
                    <td  >
                        <input id="shuima" name="shuima"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td  >是否估计价格：<span style="color:red">*</span></td>
                    <td  >
                        <input name="sfGjPrice" id="sfGjPrice" class="mini-textbox" style="width:98%;" />
                    </td>

                </tr>
            </table>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var recordObj=${recordObj};
    var recordForm = new mini.Form("#recordForm");

    $(function () {
        recordForm.setData(recordObj);
        if(action=='view') {
            recordForm.setEnabled(false);
            mini.get("commitForm").hide();
            $("#syncRecordMatName").hide();
            $("#syncRecordMrp").hide();
            $("#syncRecordCgz").hide();
        } else {
            recordForm.setEnabled(true);
            mini.get("commitForm").show();
            $("#syncRecordMatName").show();
            $("#syncRecordMrp").show();
            $("#syncRecordCgz").show();
        }
        mini.get("recordType").setEnabled(false);
    });

</script>
</body>
</html>