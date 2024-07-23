<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>物料编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/materielExtend/materielExtendMaterielEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/materielExtend/CommonUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css"/>

    <style>
        body .mini-textbox-disabled .mini-textbox-border {
            background: #dedbdb;
            color: #6D6D6D;
            cursor: default;
        }

        body .mini-buttonedit-disabled .mini-buttonedit-border,
        body .mini-buttonedit-disabled .mini-buttonedit-input {
            background: #dedbdb;
            color: #6D6D6D;
            cursor: default;
        }
    </style>
</head>


<body>
<div id="toolBar" class="topToolBar">
    <div>
        <span style="cursor:pointer;color:#44cef6;text-decoration:underline;vertical-align: middle;margin-right: 20px"
              onclick="jumpToPropertyDesc()">物料各字段填写说明</span>
        <a id="commitForm" class="mini-button" onclick="saveMateriel()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto">
        <form id="materielForm" method="post">
            <input id="materielId" name="id" class="mini-hidden"/>
            <input name="applyNo" class="mini-hidden"/>
            <input name="wlzt" class="mini-hidden" value="D"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 200px">
                <tr>
                    <td style="background-color: yellow">是否为问题物料：
                        <image src="${ctxPath}/styles/images/question2.png"
                               style="cursor: pointer;vertical-align: middle"
                               title="标注为“是”后，该条物料将不需要继续扩充，最终也不会被更新到SAP"/>
                    </td>
                    <td>
                        <input id="markError" name="markError" class="mini-combobox" textField="key" valueField="value"
                               allowInput="false" style="width:98%;"
                               data="[{'key' : '否','value' : 'no'},{'key' : '是','value' : 'yes'}]"
                               onvaluechanged="markErrorChanged"/>
                    </td>
                    <td style="background-color: yellow">问题原因：</td>
                    <td>
                        <input id="markErrorReason" name="markErrorReason" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="background-color: yellow">问题标注人：</td>
                    <td>
                        <input type="mini-textbox" id="markErrorUserId" name="markErrorUserId" class="mini-hidden"/>
                        <input type="mini-textbox" id="markErrorTime" name="markErrorTime" class="mini-hidden"/>
                        <input id="markErrorUserName" name="markErrorUserName" class="mini-textbox" style="width:98%;"
                               readonly/>
                    </td>
                </tr>
                <tr>
                    <td>物料号码：</td>
                    <td>
                        <input id="wlhm" name="wlhm" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>物料类型：</td>
                    <td>
                        <input id="wllx" name="wllx" class="mini-textbox" style="width:98%;"
                               onvaluechanged="wllxChanged()"/>
                    </td>
                    <td>物料描述：</td>
                    <td>
                        <input name="wlms" id="wlms" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>单位：</td>
                    <td>
                        <input id="dw" name="dw" class="mini-combobox" textField="key" valueField="value"
                               allowInput="false" style="width:98%;" emptytext="请选择...(详情可查右上角说明)" onvaluechanged="dwChanged()"/>
                    </td>
                    <td>物料组：</td>
                    <td>
                        <input name="wlz" id="wlz" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>进口国产：</td>
                    <td>
                        <input name="jkgc" id="jkgc" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>工厂：</td>
                    <td>
                        <input id="gc" name="gc" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>销售组织：</td>
                    <td>
                        <input id="xszz" name="xszz" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>分销渠道：</td>
                    <td>
                        <input id="fxqd" name="fxqd" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>产品组：</td>
                    <td>
                        <input id="cpz" name="cpz" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>科目设置组：</td>
                    <td>
                        <input id="kmszz" name="kmszz" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>交货工厂：</td>
                    <td>
                        <input id="jhgc" name="jhgc" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>重量：</td>
                    <td>
                        <input name="zl" id="zl" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>供应商：</td>
                    <td>
                        <input name="gys" id="gys" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>备注（材质）：</td>
                    <td>
                        <input name="bzcz" id="bzcz" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>价格（元）：
                        <image src="${ctxPath}/styles/images/tip.png" style="cursor: pointer;vertical-align: middle"
                               title='申请人填写“外购件”价格，供方填写“标准的胶管接头和标准件”价格，财务填写“外协件”价格。采购类型为F且特殊采购类不为30时必填！'/>
                    </td>
                    <td>
                        <input name="jg" id="jg" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>MRP控制者：</td>
                    <td>
                        <input name="mrpkzz" id="mrpkzz" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>集采类型：</td>
                    <td>
                        <input id="jclx" name="jclx" class="mini-combobox" textField="key" valueField="value"
                               allowInput="false" style="width:98%;" emptytext="请选择..."
                               data="[{'key' : '非集采','value' : 'fjc'},{'key' : '保税集采','value' : 'bsjc'},{'key' : '供应集采','value' : 'gyjc'}]"
                        />
                    </td>

                </tr>
                <tr>
                    <td>采购类型：</td>
                    <td>
                        <input id="cglx" name="cglx" class="mini-combobox" textField="key" valueField="value"
                               allowInput="false" style="width:98%;" emptytext="请选择..." onvaluechanged="cglxChanged()"/>
                    </td>
                    <td>特殊采购类：</td>
                    <td>
                        <input name="tscgl" id="tscgl" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>采购组：</td>
                    <td>
                        <input name="cgz" id="cgz" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>配送类型：</td>
                    <td>
                        <input name="pslx" id="pslx" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>批量大小：</td>
                    <td>
                        <input id="pldx" name="pldx" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>独立集中：</td>
                    <td>
                        <input id="dljz" name="dljz" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>采购存储地点：</td>
                    <td colspan="2" >
                        <input name="cgccdd" id="cgccdd" class="mini-textbox" vtype="maxLength:4" maxLengthErrorText="长度不能超过四个字符" style="width:98%;"/>
                    </td>
                    <td>计划交货时间：</td>
                    <td colspan="2" >
                        <input name="jhjhsj" id="jhjhsj" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>评估类：</td>
                    <td colspan="2" >
                        <input name="pgl" id="pgl" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>利润中心：</td>
                    <td colspan="2" >
                        <input id="lrzx" name="lrzx" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>价格单位：</td>
                    <td>
                        <input id="jgdw" name="jgdw" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>序列号参数文件：</td>
                    <td>
                        <input id="xlhcswj" name="xlhcswj" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>反冲：</td>
                    <td>
                        <input name="fc" id="fc" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>自制生产时间：</td>
                    <td>
                        <input name="zzscsj" id="zzscsj" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>生产仓储地点：</td>
                    <td>
                        <input name="scccdd" id="scccdd" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>可用性检查：</td>
                    <td>
                        <input id="kyxjc" name="kyxjc" class="mini-textbox" style="width:98%;"/>
                    </td>

                </tr>
                <tr>
                    <td>MRP类型：</td>
                    <td>
                        <input id="mrplx" name="mrplx" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>库存地点1：</td>
                    <td>
                        <input name="kcdd1" id="kcdd1" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>库存地点2：</td>
                    <td>
                        <input name="kcdd2" id="kcdd2" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>库存地点3：</td>
                    <td>
                        <input name="kcdd3" id="kcdd3" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td>关重件名称选择</td>
                    <td>
                        <input id="codeId" name="codeId" textname="codeName" style="width:98%;" property="editor"
                               class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectCodeClick" allowInput="false"
                               onbuttonclick="selectCode()"/>
                    </td>
                    <td>是否创建信息记录：</td>
                    <td>
                        <input id="sfwg" name="sfwg" class="mini-combobox" textField="key" valueField="value"
                               allowInput="false" style="width:98%;" emptytext="请选择..."
                               data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                        />
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="selectCodeWindow" title="选择关重件" class="mini-window" style="width:750px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input id="parentInputScene" style="display: none"/>
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">关重件名称: </span>
        <input class="mini-textbox" width="130" id="codeName" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchCode()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="codeListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="onRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/materielExtend/apply/queryCodeList.do?">
            <div property="columns">
                <div type="checkcolumn" width="25px"></div>
                <div field="belongSystem" sortField="id" width="60" headerAlign="center" align="center"
                     allowSort="true">所属系统
                </div>
                <div field="codeName" width="50" headerAlign="center" align="center" allowSort="false">关重件名称</div>
                <div field="collectType" width="50" headerAlign="center" align="center" allowSort="false">采集类型</div>
                <div field="productType" width="50" headerAlign="center" align="center" allowSort="false"
                     renderer="onWSwitchType">产品种类
                </div>
                <div field="note" width="80" headerAlign="center" align="center" allowSort="false">备注</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectCodeOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectCodeHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var codeListGrid = mini.get("codeListGrid");
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var sqUserId = "${sqUserId}";
    var opRoleName = "${opRoleName}";
    var materielObj =${materielObj};
    var respProperties =${respProperties};
    var selectCodeWindow = mini.get("selectCodeWindow");

    var materielForm = new mini.Form("#materielForm");

    function selectCode(inputScene) {
        $("#parentInputScene").val(inputScene);
        selectCodeWindow.show();
        searchCode();
    }

    //查询标准
    function searchCode() {
        var queryParam = [];
        //其他筛选条件
        var codeName = $.trim(mini.get("codeName").getValue());
        if (codeName) {
            queryParam.push({name: "codeName", value: codeName});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = codeListGrid.getPageIndex();
        data.pageSize = codeListGrid.getPageSize();
        data.sortField = codeListGrid.getSortField();
        data.sortOrder = codeListGrid.getSortOrder();
        //查询
        codeListGrid.load(data);
    }

    function onRowDblClick() {
        selectCodeOK();
    }

    function selectCodeOK() {
        var selectRow = codeListGrid.getSelected();
        mini.get("codeId").setValue(selectRow.codeId);
        mini.get("codeId").setText(selectRow.codeName);
        selectCodeHide();
    }


    function selectCodeHide() {
        selectCodeWindow.hide();
        mini.get("codeName").setValue('');
    }

    function onSelectCodeClick(e) {
        mini.get("codeId").setValue('');
        mini.get("codeId").setText('');
    }

</script>
</body>
</html>