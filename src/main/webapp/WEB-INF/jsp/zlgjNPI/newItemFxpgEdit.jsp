<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/5/8
  Time: 15:24
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>新品失效风险</title>
    <%@include file="/commons/edit.jsp"%>
    <script src="${ctxPath}/scripts/zlgjNPI/newItemFxpgEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/sys/echarts/echartsFrontCustom.js?t=1.5.137" type="text/javascript"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts.min.js"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts-wordcloud.min.js"></script>
    <script src="${ctxPath}/scripts/sys/echarts/roll.js" type="text/javascript"></script>
    <link  href="${ctxPath}/scripts/sys/echarts/css/roll.css" rel="stylesheet" type="text/css" />
    <link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css" />
    <style type="text/css">
        fieldset
        {
            border:solid 1px #aaaaaab3;
            min-width: 920px;
        }
        .hideFieldset
        {
            border-left:0;
            border-right:0;
            border-bottom:0;
        }
        .hideFieldset .fieldset-body
        {
            display:none;
        }
        .processStage{
            background-color: #ccc !important;
            font-size:15px !important;
            font-family:'微软雅黑' !important;
            text-align:center !important;
            vertical-align:middle !important;
            color: #201f35 !important;
            height: 30px !important;
            border-right:solid 0.5px #666;
        }
        .rmMem{
            background-color: #848382ad
        }
    </style>
</head>
<body>
    <div id="loading" class="loading" style="display:none;text-align:center;"><img src="${ctxPath}/styles/images/loading.gif"></div>
    <div id="detailToolBar" class="topToolBar" style="display: none">
        <div>
            <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
            <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
        </div>
    </div>
    <div class="mini-fit" id="content">
        <div class="form-container" style="margin: 0 auto">
            <form id="formXpsx" method="post">
                <input id="id" name="id" class="mini-hidden"/>
                <input id="instId" name="instId" class="mini-hidden"/>
                <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
                <input id="applyUserName" name="applyUserName" class="mini-hidden"/>
                <fieldset id="fdBaseInfo" >
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox" checked id="checkboxBaseInfo" onclick="zltoggleFieldSet(this, 'fdBaseInfo')" hideFocus/>
                            问题清单填报
                        </label>
                    </legend>
                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                        <table class="table-detail" cellspacing="1" cellpadding="0">
                            <tr>
                                <td style="width: 14%">系统分类：</td>
                                <td style="width: 18%;min-width:140px">
                                    <input id="xtfl" name="xtfl" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                           data="[{'key' : 'DP','value' : '底盘'},{'key' : 'DQ','value' : '电气'},{'key' : 'DL','value' : '动力'},
                                   {'key' : 'FGJ','value' : '覆盖件'},{'key' : 'GZZZ','value' : '工作装置'},{'key' : 'YA','value' : '液压'},
                                   {'key' : 'ZT','value' : '转台'},{'key' : 'SC','value' : '刹车'},{'key' : 'CD','value' : '传动'}]"/>
                                </td>
                                <td style="width: 14%">零部件：</td>
                                <td style="width: 21%">
                                    <input id="lbj" name="lbj" class="mini-textbox" style="width:98%;"/>
                                </td>
                            </tr>
                            <tr>
                                <td>问题清单：</td>
                                <td>
                                    <input id="wtqd" name="wtqd" class="mini-textarea" style="width:98%;overflow: auto" />
                                </td>
                                <td style="width: 14%">改善措施：</td>
                                <td style="width: 21%">
                                    <input id="gscs" name="gscs" class="mini-textarea" style="width:98%;overflow: auto"/>
                                </td>
                            </tr>
                            <tr>
                                <td>改善责任人：</td>
                                <td>
                                    <input id="gsrId" name="gsrId" textname="gsrName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="项目负责人" length="50" maxlength="50"  mainfield="no"  single="true" onvaluechanged="setRespDept()"/>
                                </td>
                                <td>责任人：</td>
                                <td>
                                    <input id="zrrId" name="zrrId" textname="zrrName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="项目负责人" length="50" maxlength="50"   single="false" />
                                </td>
                            </tr>
                            <tr>
                                <td style="width: 14%">是否分管领导审批：</td>
                                <td style="width: 21%">
                                    <input id="sffgsp" name="sffgsp" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                           data="[{'key' : 'YES','value' : '是'},{'key' : 'NO','value' : '否'}]"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                </fieldset>
                <br>
                <fieldset id="fdAchievementInfo"  class="hideFieldset" >
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox"   id="checkboxAchievementInfo" onclick="zltoggleFieldSet(this, 'fdAchievementInfo')" hideFocus/>
                            风险评估及对策
                        </label>
                    </legend>
                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                        <table>
                            <tr>
                                <td style="width: 10%">设计寿命：</td>
                                <td style="width: 21%">
                                    <input id="sjsm" name="sjsm" class="mini-textbox" style="width:98%;"/>
                                </td>
                            </tr>
                        </table>
                        <hr>
                        <div class="mini-toolbar" id="projectAchievementButtons">
                            <a class="mini-button"   plain="true" onclick="addFxpg()">添加</a>
                            <a class="mini-button btn-red"  plain="true" onclick="delFxpg()">删除</a>
                        </div>
                        <div id="grid_xpsx_fxpg" class="mini-datagrid"  allowResize="false"
                             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="true" showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div field="id" align="center"  width="1"  headerAlign="left" visible="false">id</div>
                                <div field="fxd"  headerAlign="center" align="center"   width="100">风险点
                                        <input property="editor" class="mini-textbox" />
                                </div>
                                <div field="gkcs"  headerAlign="center" align="center"   width="100">管控措施
                                        <input property="editor" class="mini-textbox" />
                                </div>
                                <div field="yzfa" displayField="yzfa"  headerAlign="center" align="center"   width="100">验证方法
                                    <input property="editor" class="mini-combobox" textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                           data="[{'key' : 'ZDSY','value' : '震动试验'},{'key' : 'FZFX','value' : '仿真分析'},{'key' : 'CBLSY','value' : '搓板路试验'},
                                           {'key' : 'YJKH','value' : '样机考核'},{'key' : 'TJSY','value' : '台架试验'},{'key' : 'DBFX','value' : '对标分析'}]"/>
                                </div>
                                <div field="wcTime"  headerAlign="center" align="center" width="40" dateFormat="yyyy-MM-dd">完成日期
                                    <input property="editor" class="mini-datepicker" style="width:100%;"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </fieldset>
                <br>
            </form>
        </div>
    </div>
<script type="text/javascript">
    mini.parse();
    var nodeVarsStr='${nodeVars}';
    var nodeId="${nodeId}";
    var status="${status}";
    var xpsxId="${xpsxId}";
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var formXpsx = new mini.Form("#formXpsx");
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var grid_xpsx_fxpg = mini.get("#grid_xpsx_fxpg");


    /**
     * 表单弹出事件控制
     * @param ck
     * @param id
     */
    function zltoggleFieldSet(ck, id) {
        var dom = document.getElementById(id);
        if (ck.checked) {
            dom.className = "";
        } else {
            dom.className = "hideFieldset";
        }
    }

    
    function setRespDept() {
        var userId=mini.get("gsrId").getValue();
        if(!userId) {
            mini.get("zrrId").setValue('');
            mini.get("zrrId").setText('');
            return;
        }
        var userName=mini.get("gsrId").getText();
        mini.get("zrrId").setValue(userId);
        mini.get("zrrId").setText(userName);
    }

</script>
</body>
</html>
