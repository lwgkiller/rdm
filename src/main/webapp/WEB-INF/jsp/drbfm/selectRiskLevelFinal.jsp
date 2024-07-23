<%--
  Created by IntelliJ IDEA.
  User: zhangwentao
  Date: 2023/3/13
  Time: 14:47
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="ToolBar" class="topToolBar" style="display: block" >
    <div>
        <span style="color: red;font-size: 8px;float: left;margin-left: 10px">注：保存可暂存分数但是全部有分才会返回风险评级！</span>
        <a id="save" class="mini-button"
           onclick="saveData()">保存</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto" >
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">严重度打分：<span style="color: #ff0000">*</span></td>
                    <td style="min-width:170px">
                        <input id="yanzhongduLevel" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onYanzhongduCloseClick()"
                               name="yanzhongduLevel" textname="yanzhongduLevel" allowInput="false"
                               onbuttonclick="selectYanzhongduLevel()"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">发生度打分：<span style="color: #ff0000">*</span></td>
                    <td style="min-width:170px">
                        <input id="fashengduLevel" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onFashengduCloseClick()"
                               name="fashengduLevel" textname="fashengduLevel" allowInput="false"
                               onbuttonclick="selectFashengduLevel()"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">探测度打分：<span style="color: #ff0000">*</span></td>
                    <td style="min-width:170px">
                        <input id="tanceduLevel" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onTanceduCloseClick()"
                               name="tanceduLevel" textname="tanceduLevel" allowInput="false"
                               onbuttonclick="selectTanceduLevel()"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var coverContent = "<br/>徐州徐工挖掘机械有限公司";
    var id ="${id}";
    var yanzhongduLevel ="${yanzhongduLevel}";
    var fashengduLevel ="${fashengduLevel}";
    var tanceduLevel ="${tanceduLevel}";
    var formBusiness = new mini.Form("#formBusiness");
    $(function () {
        var url = jsUseCtxPath + "/drbfm/single/getRiskLevelFinalSOD.do";
        $.post(
            url,
            {requestId: id},
            function (json) {
                mini.get("yanzhongduLevel").setValue(json.riskLevelFinalyzd);
                mini.get("yanzhongduLevel").setText(json.riskLevelFinalyzd);
                mini.get("fashengduLevel").setValue(json.riskLevelFinalfsd);
                mini.get("fashengduLevel").setText(json.riskLevelFinalfsd);
                mini.get("tanceduLevel").setValue(json.riskLevelFinaltcd);
                mini.get("tanceduLevel").setText(json.riskLevelFinaltcd);
            });
    });


    function selectYanzhongduLevel() {
        var yanzhongduLevel =mini.get("yanzhongduLevel").value;
        mini.open({
            title: "SOD严重度打分",
            url: jsUseCtxPath + "/drbfm/single/yanzhongdu.do?yanzhongduLevel="+yanzhongduLevel,
            width: 1000,
            height: 700,
            showModal: true,
            allowResize: true,
            showCloseButton:true,
            onload: function () {

            },
            ondestroy: function (returnData) {
                if (returnData && returnData.yanzhongId) {
                    mini.get("yanzhongduLevel").setValue(returnData.yanzhongId);
                    mini.get("yanzhongduLevel").setText(returnData.yanzhongId);
                } else if(!yanzhongduLevel){
                    mini.alert("未选择严重度分数！");
                }
            }

        });
    }

    function selectFashengduLevel() {
        var fashengduLevel =mini.get("fashengduLevel").value;
        mini.open({
            title: "SOD发生度打分",
            url: jsUseCtxPath + "/drbfm/single/fashengdu.do",
            width: 1000,
            height: 700,
            showModal: true,
            allowResize: true,
            showCloseButton:true,
            onload: function () {

            },
            ondestroy: function (returnData) {
                if (returnData && returnData.fashengId) {
                    mini.get("fashengduLevel").setValue(returnData.fashengId);
                    mini.get("fashengduLevel").setText(returnData.fashengId);
                } else if(!fashengduLevel){
                    mini.alert("未选择发生度分数！");
                }
            }

        });
    }

    function selectTanceduLevel() {
        var tanceduLevel =mini.get("tanceduLevel").value;
        mini.open({
            title: "SOD探测度打分",
            url: jsUseCtxPath + "/drbfm/single/tancedu.do",
            width: 1000,
            height: 700,
            showModal: true,
            allowResize: true,
            showCloseButton:true,
            onload: function () {

            },
            ondestroy: function (returnData) {
                if (returnData && returnData.tanceId) {
                    mini.get("tanceduLevel").setValue(returnData.tanceId);
                    mini.get("tanceduLevel").setText(returnData.tanceId);
                } else if(!tanceduLevel){
                    mini.alert("未选择探测度分数！");
                }
            }

        });
    }

    function onYanzhongduCloseClick() {
        mini.get("yanzhongduLevel").setValue('');
        mini.get("yanzhongduLevel").setText('');
    }

    function onFashengduCloseClick() {
        mini.get("fashengduLevel").setValue('');
        mini.get("fashengduLevel").setText('');
    }

    function onTanceduCloseClick() {
        mini.get("tanceduLevel").setValue('');
        mini.get("tanceduLevel").setText('');
    }

    function saveData() {

        var formData = new mini.Form("formBusiness");
        var data = formData.getData();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/drbfm/single/saveSODFinal?id='+id,
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (!data.success) {
                    mini.alert(data.message, "提示消息");

                }
            }
        });
        var S = mini.get("yanzhongduLevel").getValue();
        if (!S) {
            mini.alert('还未选择严重度分数！');
            return;
        }
        var O = mini.get("fashengduLevel").getValue();
        if (!O) {
            mini.alert('还未选择发生度分数！');
            return;
        }
        var D = mini.get("tanceduLevel").getValue();
        if (!D) {
            mini.alert('还未选择探测度分数！');
            return;
        }

        var H = 'H';
        var M = 'M';
        var L = 'L';
        var returnData = {};
        if (S >= 9 && S <= 10) {
            if (O >= 8 && O <= 10) {
                returnData = {riskLevel: H};
                window.CloseOwnerWindow(returnData);

            }

            if (O >= 6 && O <= 7) {
                returnData = {riskLevel: H};
                window.CloseOwnerWindow(returnData);
            }
            if (O >= 4 && O <= 5) {
                if(D >= 2 && D <= 10){
                    returnData = {riskLevel: H};
                    window.CloseOwnerWindow(returnData);
                }
                if(D <= 1){
                    returnData = {riskLevel: M};
                    window.CloseOwnerWindow(returnData);
                }

            }
            if (O >= 2 && O <= 3) {
                if(D >= 7 && D <= 10){
                    returnData = {riskLevel: H};
                    window.CloseOwnerWindow(returnData);
                }
                if(D >= 5 && D <= 6){
                    returnData = {riskLevel: M};
                    window.CloseOwnerWindow(returnData);
                }
                if(D >= 2 && D <= 4){
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }
                if( D <= 1){
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }
            }
            if (O == 1) {
                returnData = {riskLevel: L};
                window.CloseOwnerWindow(returnData);
            }

        }

        if (S >= 7 && S <= 8) {
            if (O >= 8 && O <= 10) {
                returnData = {riskLevel: H};
                window.CloseOwnerWindow(returnData);
            }
            if (O >= 6 && O <= 7) {
                if (D >= 2 && D <= 10) {
                    returnData = {riskLevel: H};
                    window.CloseOwnerWindow(returnData);
                }
                if(D <= 1){
                    returnData = {riskLevel: M};
                    window.CloseOwnerWindow(returnData);
                }
            }
            if (O >= 4 && O <= 5) {
                if(D >=7 && D <= 10) {
                    returnData = {riskLevel: H};
                    window.CloseOwnerWindow(returnData);
                }
                if(D >=1 && D <= 6){
                    returnData = {riskLevel: M};
                    window.CloseOwnerWindow(returnData);
                }
            }
            if (O >= 2 && O <= 3) {
                if(D >=5 && D <= 10){
                    returnData = {riskLevel: M};
                    window.CloseOwnerWindow(returnData);
                }
                if(D >=1 && D <= 4){
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }
            }
            if (O == 1) {
                returnData = {riskLevel: L};
                window.CloseOwnerWindow(returnData);
            }
        }

        if (S >= 4 && S <= 6) {
            if (O >= 8 && O <= 10) {
                if (D >= 5 && D <= 10) {
                    returnData = {riskLevel: H};
                    window.CloseOwnerWindow(returnData);
                }
                if (D >= 1 && D <= 4) {
                    returnData = {riskLevel: M};
                    window.CloseOwnerWindow(returnData);
                }

            }
            if (O >= 6 && O <= 7) {
                if (D >= 2 && D <= 10) {
                    returnData = {riskLevel: M};
                    window.CloseOwnerWindow(returnData);
                }
                if ( D <= 1) {
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }
            }
            if (O >= 4 && O <= 5) {
                if (D >= 7 && D <= 10) {
                    returnData = {riskLevel: M};
                    window.CloseOwnerWindow(returnData);
                }
                if (D >= 1 && D <= 6) {
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }
            }
            if (O >= 2 && O <= 3) {
                returnData = {riskLevel: L};
                window.CloseOwnerWindow(returnData);
            }
            if (O == 1) {
                returnData = {riskLevel: L};
                window.CloseOwnerWindow(returnData);
            }
        }

        if (S >= 2 && S <= 3) {
            if (O >= 8 && O <= 10) {
                if (D >= 5 && D <= 10) {
                    returnData = {riskLevel: M};
                    window.CloseOwnerWindow(returnData);
                }
                if (D >= 1 && D <= 4) {
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }

            }
            if (O >= 6 && O <= 7) {
                returnData = {riskLevel: L};
                window.CloseOwnerWindow(returnData);
            }
            if (O >= 4 && O <= 5) {
                returnData = {riskLevel: L};
                window.CloseOwnerWindow(returnData);
            }
            if (O >= 2 && O <= 3) {
                returnData = {riskLevel: L};
                window.CloseOwnerWindow(returnData);
            }
            if (O == 1) {
                returnData = {riskLevel: L};
                window.CloseOwnerWindow(returnData);
            }
        }

        if (S == 1) {
            returnData = {riskLevel: L};
            window.CloseOwnerWindow(returnData);
        }


    }
</script>
</body>
</html>
