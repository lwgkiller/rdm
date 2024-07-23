<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>KPI编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
    <div>
        <a id="saveKpi" class="mini-button" onclick="saveKpiYear()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto">
        <form id="formKpiYear" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 14%">年度：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="year" name="year" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" required="false" allowInput="false"
                               showNullItem="false"/>
                    </td>
                    <td style="width: 14%">执考人员：</td>
                    <td style="width: 36%;min-width:140px">
                        <input id="zkUserId" name="zkUserId" textname="zkUserName" class="mini-user rxc"
                            plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                            mainfield="no"  single="true" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">考核指标：</td>
                    <td style="width: 36%;min-width:140px">
                        <input id="metricDesc" name="metricDesc" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">被考核人员：</td>
                    <td style="width: 36%;">
                        <input id="bkhUserIds" name="bkhUserIds" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px" allowinput="false" textname="bkhUserNames" length="2000"
                               maxlength="2000" minlen="0" single="false" initlogindep="false"/>
                    </td>

                </tr>
                <tr>
                    <td style="width: 14%">周期：</td>
                    <td style="width: 36%;">
                        <input id="period" name="period" class="mini-combobox" style="width:98%"
                               textField="key" valueField="value" required="false" allowInput="false"
                               showNullItem="false"
                               data="[{value:'月度',key:'月度'}, {value:'季度', key: '季度'}]"/>
                    </td>
                    <td style="width: 14%">权重(不带%)：</td>
                    <td style="width: 36%;">
                        <input id="weight" name="weight" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">目标值(比率需带%)：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="targetValue" name="targetValue" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">目标值描述：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="targetFormula" name="targetFormula" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">目标下限值(比率需带%)：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="targetLowerValue" name="targetLowerValue" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">目标下限描述：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="targetLowerFormula" name="targetLowerFormula" class="mini-textbox"
                               style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">目标上限值(比率需带%)：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="targetUpperValue" name="targetUpperValue" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">目标上限描述：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="targetUpperFormula" name="targetUpperFormula" class="mini-textbox"
                               style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">计算方法及考核规则：</td>
                    <td colspan="3" style="width: 36%;height: 150px">
                        <input id="computeDesc" name="computeDesc" class="mini-textarea rxc" plugins="mini-textarea"
                               style="width:100%;height: 100%; line-height:25px;" datatype="varchar" length="1500"
                               vtype="length:1500" minlen="0" allowinput="true" mwidth="80" wunit="%" mheight="1500"
                               hunit="px" />
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var scene = "${scene}";
    var selectYear = "${selectYear}";
    var formKpiYear = new mini.Form("#formKpiYear");
    var jsUseCtxPath = "${ctxPath}";
    var fixDeptId = "${fixDeptId}";
    var fixDeptName = "${fixDeptName}";
    var kpiId="${kpiId}";

    $(function (){
        setCreateYear();
        if (action=='detail') {
            formKpiYear.setEnabled(false);
            queryAndAssignKpiInfos(kpiId);
            mini.get("saveKpi").hide();
        } else if(action =='add') {
            mini.get("year").setValue(selectYear);
        } else if(action =='edit') {
            queryAndAssignKpiInfos(kpiId);
        }
    });

    function queryAndAssignKpiInfos(kpiId) {
        $.ajax({
            url: jsUseCtxPath + '/kpiLeader/core/getKpiYearJson.do?kpiId='+kpiId,
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    formKpiYear.setData(data);
                }
            }
        });
    }

    function setCreateYear() {
        var createYearData=generateYearSelect();
        mini.get("year").load(createYearData);
        var nowY=new Date().getFullYear();
        mini.get("year").setValue(nowY);
    }

    function generateYearSelect() {
        var data=[];
        var nowDate=new Date();
        var startY=nowDate.getFullYear()-10;
        var endY=nowDate.getFullYear()+1;
        for(var i=endY;i>=startY;i--) {
            var oneData={};
            oneData.key=i+'年';
            oneData.value=i;
            data.push(oneData);
        }
        return data;
    }

    function saveKpiYear() {
        var kpiYearFormData=formKpiYear.getData();
        var checkResult=kpiYearValidCheck(kpiYearFormData);
        if(!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/kpiLeader/core/saveKpi.do',
            type: 'post',
            async: false,
            data:mini.encode(kpiYearFormData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message="";
                    if(data.success) {
                        message="数据保存成功";
                    } else {
                        message="数据保存失败，"+data.message;
                    }

                    mini.alert(message,"提示信息",function () {
                        if(data.success) {
                            CloseWindow();
                        }
                    });
                }
            }
        });
    }


    //检查年度kpi表单是否有效，返回result和message
    function kpiYearValidCheck(kpiYearFormData) {
        var checkResult={result:false};
        //先检查参数的必填性
        if(!kpiYearFormData.year) {
            checkResult.message="请选择'年度'！";
            return checkResult;
        }
        if(!kpiYearFormData.metricDesc) {
            checkResult.message="请填写'考核指标'！";
            return checkResult;
        }
        if(!kpiYearFormData.zkUserId) {
            checkResult.message="请选择'执考人员'！";
            return checkResult;
        }
        if(!kpiYearFormData.targetValue) {
            checkResult.message="请填写'目标值'！";
            return checkResult;
        }
        if(!kpiYearFormData.period) {
            checkResult.message="请选择'考核周期'！";
            return checkResult;
        }
        if(!kpiYearFormData.weight) {
            checkResult.message="请填写'权重'！";
            return checkResult;
        }
        if(!kpiYearFormData.computeDesc) {
            checkResult.message="请填写'计算方法及考核规则'！";
            return checkResult;
        }
        //再检查已填写参数的合法性
        var targetValue=kpiYearFormData.targetValue;
        if(targetValue) {
            if(targetValue.indexOf('%')==targetValue.length-1) {
                targetValue=targetValue.substr(0,targetValue.length-1);
            }
            if(!checkIsNumber(targetValue)) {
                checkResult.message="'目标值'请填写数字或百分比！";
                return checkResult;
            }
        }
        var targetLowerValue=kpiYearFormData.targetLowerValue;
        if(targetLowerValue) {
            if(targetLowerValue.indexOf('%')==targetLowerValue.length-1) {
                targetLowerValue=targetLowerValue.substr(0,targetLowerValue.length-1);
            }
            if(!checkIsNumber(targetLowerValue)) {
                checkResult.message="'目标下限'请填写数字或百分比！";
                return checkResult;
            }
        }
        var targetUpperValue=kpiYearFormData.targetUpperValue;
        if(targetUpperValue) {
            if(targetUpperValue.indexOf('%')==targetUpperValue.length-1) {
                targetUpperValue=targetUpperValue.substr(0,targetUpperValue.length-1);
            }
            if(!checkIsNumber(targetUpperValue)) {
                checkResult.message="'目标上限'请填写数字或百分比！";
                return checkResult;
            }
        }
        var weight=kpiYearFormData.weight;
        if(weight) {
            if(!checkIsNumber(weight)) {
                checkResult.message="'权重'请填写数字！";
                return checkResult;
            }
            var parseWeightValue=parseFloat(weight);
            if(parseWeightValue<=0 || parseWeightValue>100) {
                checkResult.message="'权重'超出范围0~100！";
                return checkResult;
            }
        }
        checkResult.result=true;
        return checkResult;
    }

    function checkIsNumber(value) {
        if(!value) {
            return false;
        }
        var parseValue=parseFloat(value);
        if(parseValue!=value) {
            return false;
        }
        return true;
    }

</script>
</body>
</html>
