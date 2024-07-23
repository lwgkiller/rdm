<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>质量改进</title>
    <%@include file="/commons/edit.jsp"%>
    <script src="${ctxPath}/scripts/zlgjNPI/gysWtEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
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
    </style>
</head>
<body>
    <div id="detailToolBar" class="topToolBar" style="display: none">
        <div>
            <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
            <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
        </div>
    </div>
    <div class="mini-fit" id="content">
        <div class="form-container" style="margin: 0 auto">
            <form id="formZlgj" method="post">
                <input id="wtId" name="wtId" class="mini-hidden"/>
                <input id="instId" name="instId" class="mini-hidden"/>
                <fieldset id="fdBaseInfo" >
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox" checked id="checkboxBaseInfo" onclick="zltoggleFieldSet(this, 'fdBaseInfo')" hideFocus/>
                            问题识别信息
                        </label>
                    </legend>
                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                        <table class="table-detail" cellspacing="1" cellpadding="0">
                            <tr>
                                <td style="width: 14%">物料大类：</td>
                                <td style="width: 18%;min-width:140px">
                                    <input id="bigTypeName" name="bigTypeName" class="mini-textbox" style="width:98%;" readonly value="${type}"/>
                                </td>
                                <td style="width: 14%">物料分类：<span style="color:red">*</span></td>
                                <td style="width: 19%;min-width:170px">
                                    <input id="smallTypeId" name="smallTypeId" class="mini-combobox" style="width:98%;"
                                           textField="smallTypeName" valueField="smallTypeId" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="false"
                                    />
                                </td>
                                <td style="width: 14%">不良零部件 ：<span style="color:red">*</span></td></td>
                                <td style="width: 21%">
                                    <input id="bllbj" name="bllbj" class="mini-textbox" style="width:98%;"/>
                                </td>
                            </tr>
                            <tr>
                                <td style="width: 14%">零部件图号/型号：<span style="color:red">*</span></td>
                                <td style="width: 21%">
                                    <input id="lbjThXh" name="lbjThXh" class="mini-textbox" style="width:98%;"/>
                                </td>
                                <td>零部件供应商：<span style="color:red">*</span></td></td>
                                <td>
                                    <input id="lbjgys" name="lbjgys"  class="mini-textbox" style="width:98%;" />
                                </td>
                                <td>不良数量：<span style="color:red">*</span></td></td>
                                <td>
                                    <input id="blsl" name="blsl"  class="mini-textbox" style="width:98%;" />
                                </td>
                            </tr>
                            <tr>
                                <td>问题发生区域：<span style="color:red">*</span></td>
                                <td>
                                    <input id="fsqy" name="fsqy" class="mini-combobox" style="width:98%;"
                                           textField="key" valueField="value" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="false"
                                           data="[ {'key' : '进货','value' : '进货'},{'key' : '结构厂','value' : '结构厂'},{'key' : '涂装厂','value' : '涂装厂'},
							   {'key' : '装配厂','value' : '装配厂'},{'key' : '调试','value' : '调试'},{'key' : '成品库','value' : '成品库'}]"
                                    />
                                </td>
                                <td>问题详细描述：<span style="color:red">*</span></td>
                                <td>
                                    <input id="wtms" name="wtms" class="mini-textarea" style="width:98%;overflow: auto;height: 80px" />
                                </td>
                                <td>现场处置方法：<span style="color:red">*</span></td>
                                <td>
                                    <input id="xcczfa" name="xcczfa" class="mini-textarea" style="width:98%;overflow: auto;height: 80px" />
                                </td>
                            </tr>
                            <tr>
                                <td style="width: 14%">涉及机型：</td></td>
                                <td style="width: 21%">
                                    <input id="jiXing" name="jiXing" class="mini-textbox" style="width:98%;"/>
                                </td>
                                <td style="width: 14%">本订单采购数：</td></td>
                                <td style="width: 21%">
                                    <input id="cgs" name="cgs" class="mini-textbox" style="width:98%;"/>
                                </td>
                                <td>问题排查过程及检测方法：</td>
                                <td>
                                    <input id="wtpcjc" name="wtpcjc" class="mini-textarea" style="width:98%;overflow: auto;height: 80px" />
                                </td>
                            </tr>
                            <tr>
                                <td>改进要求（改进后达到效果）：</td>
                                <td>
                                    <input id="gjyq" name="gjyq" class="mini-textbox" style="width:98%;" />
                                </td>
                                <td>不良图片：<span style="color:red">*（先保存草稿）</span></td>
                                <td style="width: 20%;" >
                                    <a id="uploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;" onclick="addPicFile">附件列表</a>
                                </td>
                                <td>不良视频链接：</td>
                                <td >
                                    <input id="gzsplj" name="gzsplj" class="mini-textarea" style="width:98%;overflow: auto"  />
                                </td>
                            </tr>
                            <tr>
                                <td>供方管理工程师：</td>
                                <td>
                                    <input id="gfglgcsNames" name="gfglgcsNames" class="mini-textbox" style="width:98%;height:34px;" readonly/>
                                </td>
                                <td>供应商有无故障：</td>
                                <td>
                                    <input id="sfgz" name="sfgz" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="false"
                                           data="[{'key' : 'YES','value' : '有故障'},{'key' : 'NO','value' : '无故障'}]"/>
                                </td>
                                <td>是否分管领导审批：</td>
                                <td>
                                    <input id="sffgsp" name="sffgsp" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="false"
                                           data="[{'key' : 'YES','value' : '是'},{'key' : 'NO','value' : '否'}]"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                </fieldset>
                <br>

                <fieldset id="fdMemberInfo"  class="hideFieldset"  >
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox"  id="checkboxMemberInfo" onclick="zltoggleFieldSet(this, 'fdMemberInfo')" hideFocus/>
                            问题可能原因
                        </label>
                    </legend>
                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                        <div class="mini-toolbar" id="reasonButtons">
                            <a class="mini-button" id="ceshi"  plain="true" onclick="addZlgj_reasonInfoRow">添加</a>
                            <a class="mini-button btn-red"  plain="true" onclick="removeZlgj_reasonInfoRow">删除</a>
                        </div>
                        <div id="grid_zlgj_reasonInfo" class="mini-datagrid"  allowResize="false"
                             idField="yyId" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="true" showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div field="yyId" headerAlign="left" visible="false">id</div>
                                <div field="reason"  headerAlign="center" align="center" >原因<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox" /></div>
                            </div>
                        </div>
                    </div>
                </fieldset>
                <br>

                <fieldset id="fdAchievementInfo"  class="hideFieldset" >
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox"   id="checkboxAchievementInfo" onclick="zltoggleFieldSet(this, 'fdAchievementInfo')" hideFocus/>
                            临时措施
                        </label>
                    </legend>
                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                        <div class="mini-toolbar" id="lscsButtons">
                            <a class="mini-button"   plain="true" onclick="addCuoshi()">添加</a>
                            <a class="mini-button btn-red"  plain="true" onclick="delCuoshi()">删除</a>
                        </div>
                        <div id="grid_zlgj_linshicuoshi" class="mini-datagrid"  allowResize="false"
                             idField="csId" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="true" showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div field="csId" align="center"  width="1"  headerAlign="left" visible="false">id</div>
                                <div field="yyId" displayField="reason" align="center" headerAlign="center" width="80" renderer="render">原因<span style="color:red">*</span>
                                    <input property="editor" class="mini-combobox" style="width:98%;"
                                           textField="reason" valueField="yyId" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                                </div>
                                <div field="dcfa"  headerAlign="center" align="center"   width="100">对策方案<span style="color:red">*</span>
                                        <input property="editor" class="mini-textbox" />
                                </div>
                                <div field="ddpc"  headerAlign="center" align="center"   width="100">断点批次<span style="color:red">*</span>
                                        <input property="editor" class="mini-textbox" />
                                </div>
                                <div field="dhTime"  headerAlign="center" align="center" width="40" dateFormat="yyyy-MM-dd">到货时间
                                    <input property="editor" class="mini-datepicker" style="width:100%;"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </fieldset>
                <br>

                <fieldset id="fdYanzhengInfo" class="hideFieldset" >
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox" id="checkboxYanzhengInfo" onclick="zltoggleFieldSet(this, 'fdYanzhengInfo')" hideFocus/>
                            根本原因
                        </label>
                    </legend>
                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                        <div class="mini-toolbar" id="gbyyButtons">
                            <a class="mini-button"   plain="true" onclick="addZlgj_yanzhengInfoRow">添加</a>
                            <a class="mini-button btn-red"  plain="true" onclick="removeZlgj_yanzhengInfoRow">删除</a>
                        </div>
                        <div id="grid_zlgj_yanzhengInfo" class="mini-datagrid"  allowResize="false" allowCellWrap="true"
                             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                             allowAlternating="true" oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn"></div>
                                <div field="id" headerAlign="left" visible="false">id</div>
                                <div field="yyId" displayField="reason" align="center" headerAlign="center" width="80" renderer="render">原因<span style="color:red">*</span>
                                    <input property="editor" class="mini-combobox" style="width:98%;"
                                           textField="reason" valueField="yyId" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                                </div>
                                <div field="jcjg"  headerAlign="center" align="center" width="80">检测结果<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox" />
                                </div>
                                <div field="jielun"  headerAlign="center" align="center" width="80">结论
                                    <input property="editor" class="mini-textbox" />
                                </div>
                            </div>
                        </div>
                    </div>
                </fieldset>
                <br>

                <fieldset id="fdFanganInfo" class="hideFieldset" >
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox"   id="checkboxFanganInfo" onclick="zltoggleFieldSet(this, 'fdFanganInfo')" hideFocus/>
                            方案验证
                        </label>
                    </legend>
                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                        <div class="mini-toolbar" id="zlgjFanganButtons">
                            <a class="mini-button"   plain="true" onclick="addyzFangan()">添加</a>
                            <a class="mini-button btn-red"  plain="true" onclick="delyzFangan()">删除</a>
                        </div>
                        <div id="grid_zlgj_fanganyz" class="mini-datagrid"  allowResize="false"
                             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="true" showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div field="id" align="center"  width="1"  headerAlign="left" visible="false">id</div>
                                <div field="yyId" displayField="reason" align="center" headerAlign="center" width="80" renderer="render">原因<span style="color:red">*</span>
                                    <input property="editor" class="mini-combobox" style="width:98%;"
                                           textField="reason" valueField="yyId" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                                </div>
                                <div field="gjfa"  headerAlign="center" align="center"   width="100">改进方案<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox" />
                                </div>
                                <div field="sjTime"  headerAlign="center" align="center" width="40"  dateFormat="yyyy-MM-dd">实际完成时间
                                    <input property="editor" class="mini-datepicker" style="width:100%;"/>
                                </div>
                                <div field="yzjg"  headerAlign="center" align="center"   width="100">验证结果
                                    <input property="editor" class="mini-textbox" />
                                </div>
                            </div>
                        </div>
                    </div>
                </fieldset>
                <br>

                <fieldset id="fdFanganjjInfo" class="hideFieldset" >
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox"   id="checkboxFanganjjInfo" onclick="zltoggleFieldSet(this, 'fdFanganjjInfo')" hideFocus/>
                            永久解决方案
                        </label>
                    </legend>
                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                        <div class="mini-toolbar" id="zlgjFanganjjButtons">
                            <a class="mini-button"   plain="true" onclick="addjjFangan()">添加</a>
                            <a class="mini-button btn-red"  plain="true" onclick="deljjFangan()">删除</a>
                        </div>
                        <div id="grid_zlgj_fanganjj" class="mini-datagrid"  allowResize="false"
                             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="true" showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div field="id" align="center"  width="1"  headerAlign="left" visible="false">id</div>
                                <div field="yyId" displayField="reason" align="center" headerAlign="center" width="80" renderer="render">原因<span style="color:red">*</span>
                                    <input property="editor" class="mini-combobox" style="width:98%;"
                                           textField="reason" valueField="yyId" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                                </div>
                                <div field="cqcs"  headerAlign="center" align="center"   width="100">长期措施<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox" />
                                </div>
                                <div field="ddpch"  headerAlign="center" align="center"   width="80">断点批次号<span style="color:red">*</span>
                                    <input property="editor" class="mini-textbox" />
                                </div>
                                <div field="wcTime"  headerAlign="center" align="center" width="40" dateFormat="yyyy-MM-dd">实际完成时间<span style="color:red">*</span>
                                    <input property="editor" class="mini-datepicker" style="width:100%;"/>
                                </div>
                                <div align="center" headerAlign="center" width="50" renderer="renderAttachFile">供应商分析报告
                                </div>
                            </div>
                        </div>
                    </div>
                </fieldset>
                <br>
                <fieldset id="fdXGInfo"  class="hideFieldset" >
                    <legend>
                        <label style="font-size:17px">
                            <input type="checkbox"  id="checkboxXGInfo" onclick="zltoggleFieldSet(this, 'fdXGInfo')" hideFocus/>
                            改进效果跟踪
                        </label>
                    </legend>
                    <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                        <table class="table-detail" cellspacing="1" cellpadding="0">
                            <tr>
                                <td>改进效果验证：<span style="color:red">*</span></td>
                                <td>
                                    <input id="gjxg" name="gjxg" class="mini-textbox" style="width:98%;" />
                                </td>
                                <td>跟踪人：<span style="color:red">*</span></td>
                                <td>
                                    <input id="gzr" name="gzr" class="mini-textbox" style="width:98%;" />
                                </td>
                                <td>是否有效：<span style="color:red">*</span></td>
                                <td>
                                    <input id="sfyx" name="sfyx" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="false"
                                           data="[{'key' : 'YES','value' : '是'},{'key' : 'NO','value' : '否'}]"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                </fieldset>
                <br>
            </form>
        </div>
    </div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var nodeVarsStr='${nodeVars}';
    var status="${status}";
    var wtId="${wtId}";
    var action="${action}";
    var formZlgj = new mini.Form("#formZlgj");
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var type="${type}";
    var smallTypeList = ${smallTypeList};
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var grid_zlgj_reasonInfo = mini.get("#grid_zlgj_reasonInfo");
    var grid_zlgj_linshicuoshi = mini.get("#grid_zlgj_linshicuoshi");
    var grid_zlgj_yanzhengInfo = mini.get("#grid_zlgj_yanzhengInfo");
    var grid_zlgj_fanganyz = mini.get("#grid_zlgj_fanganyz");
    var grid_zlgj_fanganjj = mini.get("#grid_zlgj_fanganjj");


    grid_zlgj_linshicuoshi.on("cellbeginedit", function (e) {
        if(e.field == "yyId" && grid_zlgj_reasonInfo.getChanges().length > 0){
            mini.alert("“问题可能原因”数据有变动，请先点击'暂存信息'进行保存！");
            e.editor.setEnabled(false);
            return;
        }else {
            e.editor.setEnabled(true);
            var record = e.record;
            if (e.field == "yyId") {
                e.editor.setData(grid_zlgj_reasonInfo.getData());
            }
        }
    });
    grid_zlgj_yanzhengInfo.on("cellbeginedit", function (e) {
        if(e.field == "yyId" &&grid_zlgj_reasonInfo.getChanges().length > 0){
            mini.alert("“问题可能原因”数据有变动，请先点击'暂存信息'进行保存！");
            e.editor.setEnabled(false);
            return;
        }else {
            e.editor.setEnabled(true);
            var record = e.record;
            if (e.field == "yyId") {
                e.editor.setData(grid_zlgj_reasonInfo.getData());
            }
        }
    });
    grid_zlgj_fanganyz.on("cellbeginedit", function (e) {
        if(e.field == "yyId" &&grid_zlgj_reasonInfo.getChanges().length > 0){
            mini.alert("“问题可能原因”数据有变动，请先点击'暂存信息'进行保存！");
            e.editor.setEnabled(false);
            return;
        }else {
            e.editor.setEnabled(true);
            var record = e.record;
            if (e.field == "yyId") {
                e.editor.setData(grid_zlgj_reasonInfo.getData());
            }
        }
    });
    grid_zlgj_fanganjj.on("cellbeginedit", function (e) {
        if(e.field == "yyId" &&grid_zlgj_reasonInfo.getChanges().length > 0){
            mini.alert("“问题可能原因”数据有变动，请先点击'暂存信息'进行保存！");
            e.editor.setEnabled(false);
            return;
        }else {
            if(e.editor){
                e.editor.setEnabled(true);
            }
            var record = e.record;
            if (e.field == "yyId") {
                e.editor.setData(grid_zlgj_reasonInfo.getData());
            }
        }
    });

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


    function renderAttachFile(e) {
        var record = e.record;
        var faId = record.faId;
        var wtId=record.wtId;
        if(!faId) {
            faId='';
        }
        if(!wtId) {
            wtId='';
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
            'onclick="addGysfxFile(\'' + faId +'\',\''+wtId+ '\')">附件</a>';
        return s;
    }
    
    function setRespDept() {
        var userId=mini.get("zrrId").getValue();
        if(!userId) {
            mini.get("ssbmId").setValue('');
            mini.get("ssbmId").setText('');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.get("ssbmId").setValue(data.mainDepId);
                mini.get("ssbmId").setText(data.mainDepName);
            }
        });
    }
    function ifChange() {
        var ifgj=mini.get("ifgj").getValue();
        if (ifgj == 'no'){
            mini.get("noReason").setEnabled(true);
            $("#sfxygj").css("background-color","rgb(226 53 53)");
            $("#noReason textarea").css("background-color","rgb(226 53 53)");
        }else {
            mini.get("noReason").setEnabled(false);
            mini.get("noReason").setValue("");
            $("#sfxygj").css("background-color","");
            $("#noReason textarea").css("background-color","#ffffff");
        }
    }

</script>
</body>
</html>
