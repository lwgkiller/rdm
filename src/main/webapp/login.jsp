<%@ page pageEncoding="UTF-8" language="java" import="java.util.*" %>
<%@ page import="com.redxun.saweb.util.WebAppUtil" %>
<%
    //用于ajax请求时，其响应时进行回写标识，以使前台可以跳到登录页
    response.setHeader("timeout", "true");
    String webappName = WebAppUtil.getProperty("webappName");
    request.setAttribute("webappName", webappName);
    String userName = request.getParameter("userName");
    String passWord = request.getParameter("passWord");
    if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(passWord)) {
        request.setAttribute("userName", userName);
        request.setAttribute("passWord", passWord);
    }
%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/commons/edit.jsp" %>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1">
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport">
    <link rel="shortcut icon" href="${ctxPath}/styles/images/index/icon4.ico">
    <script src="${ctxPath}/scripts/mini/boot.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/jquery-1.11.3.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/jquery/jquery.cookie.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/jquery/plugins/jquery-cookie.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/common/sha256.js" type="text/javascript"></script>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/styles/login-h01.css"/>
    <link href="${ctxPath}/styles/commons.css?static_res_version=${static_res_version}" rel="stylesheet"
          type="text/css"/>
    <c:choose>
        <c:when test="${localeLanguage=='zh_CN'}">
            <title>用户登录</title>
        </c:when>
        <c:when test="${localeLanguage=='en_US'}">
            <title>Login</title>
        </c:when>
    </c:choose>

    <script type="text/javascript">
        if (top != this) {//当这个窗口出现在iframe里，表示其目前已经timeout，需要把外面的框架窗口也重定向登录页面
            top.location = '${ctxPath}/login.jsp';
        }
    </script>
    <style>
        .mini-listbox td {
            font-size: 12px;
        }

        #language, #language .mini-buttonedit {
            height: 19px;
        }

        #language .mini-buttonedit-border {
            height: 17px;
        }

        #language .mini-buttonedit-input {
            height: 17px;
            line-height: 15px;
        }

        #language .mini-buttonedit-button {
            height: 13px;
            width: 18px
        }

        #language .mini-buttonedit-icon {

            width: 15px;
            height: 14px;
        }
    </style>

</head>
<body>
<canvas id="canvas" width="1500" height="1000"></canvas>
<div id="bgBanner"></div>
<div class="login-box">
    <div class="logo">
        <c:choose>
            <c:when test="${webappName=='rdm'}">
                <c:choose>
                    <c:when test="${localeLanguage=='zh_CN'}">
                        <span class="rdmLogoSpan"></span>
                    </c:when>
                    <c:when test="${localeLanguage=='en_US'}">
                        <span class="gecLogoSpan"></span>
                    </c:when>
                </c:choose>
            </c:when>
            <c:when test="${webappName=='sim'}">
                <span class="simLogoSpan"></span>
            </c:when>
        </c:choose>
    </div>
    <div class="content_login">
        <div class="Myinput">
            <span class="span1"></span>
            <input type="text" id="u1" onkeydown="onKeyDown()">
        </div>
        <div class="Myinput">
            <span class="span2"></span>
            <input type="password" id="p1" value="" onkeydown="onKeyDown()">
        </div>
        <div id="mainPage">
            <input type="checkbox" checked id="rememberMe">
            <span class="rember-me">
                <c:choose>
                    <c:when test="${localeLanguage=='zh_CN'}">
                        记住登录
                    </c:when>
                    <c:when test="${localeLanguage=='en_US'}">
                        Remember
                    </c:when>
                </c:choose>
            </span>
            <input id="language" class="mini-combobox"
                   style="width:80px;margin-right: 45px;margin-top:2px;float: right;"
                   textField="languageText" valueField="languageValue"
                   required="false" allowInput="false" showNullItem="false"
                   value=${localeLanguage} onvaluechanged="languageChange()"
                   data="[{'languageText':'中文','languageValue':'zh_CN'},{'languageText':'English','languageValue':'en_US'}]"
            />

            <a href="#" onclick="newUser()"
               style="font-size: 13px;margin-right: 40px;float: right;text-decoration:underline;">
                <c:choose>
                    <c:when test="${localeLanguage=='zh_CN'}">
                        用户注册
                    </c:when>
                    <c:when test="${localeLanguage=='en_US'}">
                        Register
                    </c:when>
                </c:choose>
            </a>

            <div style="margin-top: 10px;margin-left: 40px;margin-right: 40px;">
                <c:choose>
                    <c:when test="${localeLanguage=='zh_CN'}">
                        <span style="font-weight: bold;color: red">请使用谷歌浏览器：</span>
                        <a href="#" onclick="downloadExplorer()" style="text-decoration:underline;">浏览器下载</a>
                        <span style="color: red">（首次使用浏览器，如按钮无反应请在地址栏右侧点击允许弹框）</span>
                    </c:when>
                    <c:when test="${localeLanguage=='en_US'}">
                        <span style="font-weight: bold;color: red">Please use Chrome browser：</span>
                        <a href="#" onclick="downloadExplorer()" style="text-decoration:underline;">Browser Download</a>
                        <span style="color: red">（If you first use browser，please allow the pop up window in the address bar）</span>
                    </c:when>
                </c:choose>
            </div>
        </div>
        <div style="text-align:center;">
            <c:choose>
                <c:when test="${localeLanguage=='zh_CN'}">
                    <input type="button" value="登   录" class="login-btn" onclick="onLoginClick()" id="Login"/>
                </c:when>
                <c:when test="${localeLanguage=='en_US'}">
                    <input type="button" value="Login" class="login-btn" onclick="onLoginClick()" id="Login"/>
                </c:when>
            </c:choose>
        </div>
    </div>
</div>

<div class="loadingBox">
    <div class="mini-mask-msg mini-mask-loading">
    </div>
</div>

<div id="newUserWindow" class="mini-window" style="width:800px;height:400px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="true">
    <div style="height: 88%;width: 98%">
        <span style="color: red;font-size: 16px">提交后系统会通知管理员审核，请稍后登录，或联系数字化部（61501）。</span>
        <div class="form-container" style="margin: 0 auto;width: 98%;height: 100%">
            <form id="formNewUser" method="post">
                <table class="table-detail" cellspacing="1" cellpadding="0">
                    <tr>
                        <td style="width: 10%">姓名<span style="color:red">(汉字)：*</span></td>
                        <td style="width: 30%;">
                            <input name="fullname" class="mini-textbox" style="width: 98%"
                                   emptyText="请输入姓名"/>
                        </td>
                        <td style="width: 25%">OA账号<span style="color:red">(登录用,非汉字)：*</span></td>
                        <td style="width: 35%;">
                            <input name="userNo" class="mini-textbox" emptyText="请输入账号" style="width: 98%"/>
                        </td>
                    </tr>
                    <tr>
                        <td>密码：<span style="color:red">(至少8位密码<br>且包含数字与字母):*</span></td>
                        <td><input class="mini-password" name="pwd" id="pwd" required="true" style="width: 98%"/></td>
                        <td>部门<span style="color:red">(自己的所部,<br/>不是“徐工挖掘机械事业部”)：*</span></td>
                        <td>
                            <input id="mainDepId"
                                   name="mainDepId" textName="mainDepName"
                                   class="mini-buttonedit icon-dep-button"
                                   allowInput="false"
                                   onbuttonclick="selectMainDep" style="width:98%"/>
                        </td>
                    </tr>
                    <tr>
                        <td>岗位：<span style="color:red">*</span></td>
                        <td>
                            <input id="gwId"
                                   name="gwId" textName="gwName"
                                   class="mini-buttonedit icon-dep-button"
                                   allowInput="false"
                                   onbuttonclick="selectGw" style="width:98%"/>
                        </td>
                        <td>职级<span style="color:red">(默认师一级，如使用<br>管理网则选择“管理x级”)：</span></td>
                        <td>
                            <input id="zjId"
                                   name="zjId" textName="zjName"
                                   class="mini-buttonedit icon-dep-button"
                                   allowInput="false"
                                   onbuttonclick="selectZj" style="width:98%"/>
                        </td>
                    </tr>
                    <tr>
                        <td>身份证号<span style="color:red">*</span>：</td>
                        <td><input name="certNo" class="mini-textbox" style="width: 98%"/></td>
                        <td>联系电话<span style="color:red">*</span>：</td>
                        <td><input name="mobile" class="mini-textbox" style="width: 98%"/></td>
                    </tr>
                    <tr>
                        <td>Windchill账号<span style="color:red">(非汉字)</span>：</td>
                        <td><input name="windchillPDM" class="mini-textbox" style="width: 98%"/></td>
                        <td>所属室主任：</td>
                        <td>
                            <input id="szrUserId" name="szrUserId" textName="szrUserName"
                                   class="mini-buttonedit icon-user-button" allowInput="false"
                                   onbuttonclick="selectToUser" style="width: 98%"/>
                        </td>
                    </tr>

                </table>
            </form>
        </div>
    </div>
    <div property="footer" style="height: 50px">
        <table style="width:100%;height: 90%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" id="saveNewUser" style="height: 25px;width: 70px" value="确定"
                           onclick="saveNewUser()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="newUserHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script src="${ctxPath}/scripts/common/login-01.js"></script>
<script type="text/javascript">
    mini.parse();
    var newUserWindow = mini.get("newUserWindow");
    var formNewUser = new mini.Form("formNewUser");
    var ctxPath = "${ctxPath}";
    var userName = "${userName}";
    var passWord = "${passWord}";
    $(function () {
        var username = $.getCookie('username');
        if (username) {
            $("#username").val(unescape(username));
        }
        if(userName && passWord) {
            $.ajax({
                url: "${ctxPath}/login.do",
                type: "post",
                data: {acc: userName, pd: passWord, rememberMe: '1'},
                success: function (result) {
                    if (result && result.success) {
                        window.location = "${ctxPath}/index.do";
                    } else {
                        $('.loadingBox').hide();
                        $('.login-box').show();
                        clearTimeout(loginTime);
                        mini.alert("登录失败！" + result.message);
                    }
                }, failture: function () {
                    mini.alert("登录失败！");
                    clearTimeout(loginTime);
                    $('.loadingBox').hide();
                    $('.login-box').show();
                }
            });
        }

    });

    function onLoginClick(e) {
        var loginTime = setTimeout(function () {
            $('.loadingBox').show();
            $('.login-box').hide();
        }, 100);

        var u1 = $("#u1").val(),
            p1 = $("#p1").val(),
            rememberMe = $("#rememberMe").is(':checked') ? "1" : "0";
        p1 = p1.replace(/\s*/g, "");
        if (u1 == '' || p1 == '') {
            clearTimeout(loginTime);
            $('.loadingBox').hide();
            return mini.alert(locale_loginTip);
        }
        var weakPwd="no";
        var reg = new RegExp(/^(?![^a-zA-Z]+$)(?!\D+$)/);
        if (p1.length < 8 || !reg.test(p1)) {
            weakPwd = "yes";
        }
        p1 = sha256_digest(p1);
        $.ajax({
            url: "${ctxPath}/login.do",
            type: "post",
            data: {acc: u1, pd: p1, rememberMe: rememberMe, language: mini.get("language").getValue(),weakPwd: weakPwd},
            success: function (result) {
                if (result && result.success) {
                    window.location = "${ctxPath}/index.do";
                } else {
                    $('.loadingBox').hide();
                    $('.login-box').show();
                    clearTimeout(loginTime);
                    mini.alert("登录失败！" + result.message);
                }
            }, failture: function () {
                mini.alert("登录失败！");
                clearTimeout(loginTime);
                $('.loadingBox').hide();
                $('.login-box').show();
            }
        });
    }

    function reset() {
        $("#u1").val('');
        $("#p1").val('');
    }

    $(window).resize(
        marginTop()
    );

    function marginTop() {
        var window_h = $(window).height(),
            window_w = $(window).width(),
            content_h = $(".content_bg").height(),
            content_margin = (window_h - content_h) / 2 - 30;

        $("body").height(window_h)
        $(".content_bg").css("marginTop", content_margin);

        if (window_w < 1200) {
            $("body").removeClass("minWidth");
        } else {
            $("body").addClass("minWidth");
        }
    }

    marginTop();
    $(".content_r p input").mouseenter(function () {
        $(this).stop(true, true).animate({top: -1}, 100);
    }).mouseleave(function () {
        $(this).stop(true, true).animate({top: 0}, 100);
    });

    function onKeyDown(ev) {
        var ev = ev || event;
        if (ev.keyCode == 13) {
            onLoginClick()
        }
    }

    function downloadExplorer() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", "${ctxPath}/rdmCommon/download/downloadExplorer.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }

    //选择主部门
    function selectMainDep(e) {
        var b = e.sender;

        _TenantGroupDlg('1', true, '', '1', function (g) {
            b.setValue(g.groupId);
            b.setText(g.name);
        }, false);

    }

    //选择岗位
    function selectGw(e) {
        var b = e.sender;

        _TenantGroupDlg('1', true, '', '107902686716190730', function (g) {
            b.setValue(g.groupId);
            b.setText(g.name);
        }, false);

    }

    //选择职级
    function selectZj(e) {
        var b = e.sender;

        _TenantGroupDlg('1', true, '', '87212403321741322', function (g) {
            b.setValue(g.groupId);
            b.setText(g.name);
        }, false);

    }

    function selectToUser(e) {
        var buttonEdit = e.sender;
        _UserDlg(true, function (user) {
            buttonEdit.setValue(user.userId);
            buttonEdit.setText(user.fullname);
        });
    }

    function newUser() {
        newUserWindow.show();
    }

    function saveNewUser() {
        var data = formNewUser.getData();
        var checkResult = validateForm(data);
        if (!checkResult.success) {
            mini.alert(checkResult.message);
            return;
        }
        data.pwd = sha256_digest(data.pwd);
        $("#saveNewUser").attr('disabled', true);
        $.ajax({
            url: '${ctxPath}/rdm/core/newUserSave.do',
            type: 'POST',
            data: mini.encode(data),
            contentType: 'application/json',
            success: function (returnData) {
                $("#saveNewUser").attr('disabled', false);
                if (returnData && !returnData.success) {
                    mini.alert(returnData.message);
                } else {
                    mini.alert("提交成功，待管理员审批后使用账号登录！");
                    newUserHide();
                }
            }
        });

    }

    function validateForm(data) {
        var result = {success: true, message: ""};
        if (!data.fullname) {
            result.success = false;
            result.message = "请输入姓名";
            return result;
        }
        if (!data.userNo) {
            result.success = false;
            result.message = "请输入OA账号";
            return result;
        }
        if (!data.pwd) {
            result.success = false;
            result.message = "请输入密码";
            return result;
        }
        data.pwd = data.pwd.replace(/\s*/g, "");
        if (data.pwd.length < 8) {
            result.success = false;
            result.message = "密码长度至少为8位！";
            return result;
        }
        var reg = new RegExp(/^(?![^a-zA-Z]+$)(?!\D+$)/);
        if (!reg.test(data.pwd)) {
            result.success = false;
            result.message = "密码须包含数字与字母！";
            return result;
        }
        if (!data.mainDepId) {
            result.success = false;
            result.message = "请选择部门";
            return result;
        }
        if (!data.gwId) {
            result.success = false;
            result.message = "请选择岗位";
            return result;
        }
        if (!data.certNo) {
            result.success = false;
            result.message = "请填写身份证号";
            return result;
        }
        if (!data.mobile) {
            result.success = false;
            result.message = "请填写联系电话";
            return result;
        }
        // 默认师一级
        if (!data.zjId) {
            mini.get("zjId").setValue("87212403321741351");
            mini.get("zjId").setText("师一级");
        }

        return result;
    }

    function newUserHide() {
        formNewUser.setData({});
        mini.get("mainDepId").setText("");
        mini.get("gwId").setText("");
        mini.get("zjId").setText("");
        mini.get("szrUserId").setText("");
        newUserWindow.hide();
    }

    function languageChange() {
        var url = ctxPath + "/login.jsp?locale=" + mini.get("language").getValue();
        window.location.href = url;
    }
</script>
</body>
</html>
