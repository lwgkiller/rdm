//物料属性默认值字典项
var materielDic;
//页面进来时markError的值
var originalMarkErrorVal='no';
$(function () {
    //查询页面上的下拉和默认值
    queryMaterielDic();
    //赋值下拉列表
    processSelect();
    //使用表单数据初始化
    if(action=='view' && sqUserId != currentUserId) {
        materielObj.jg='***';
    }
    materielForm.setData(materielObj);
    originalMarkErrorVal=materielObj.markError;
    //按照action和登录人角色处理页面的可编辑字段
    if(action=='view') {
        materielForm.setEnabled(false);
        mini.get("commitForm").hide();
    } else {
        var materielId=mini.get("materielId").getValue();
        if(!materielId) {
            mini.get("markError").setEnabled(false);
            mini.get("markErrorReason").setEnabled(false);
        }
        //当前允许"申请人"填写所有字段
        if(opRoleName!='SQRKC') {
            materielForm.setEnabled(false);
            //除了申请人和工艺环节外，其他环节如果物料已经标注有问题，则不允许修改为否
            if(opRoleName=='GYKC'||originalMarkErrorVal=='no') {
                mini.get("markError").setEnabled(true);
                mini.get("markErrorReason").setEnabled(true);
            }
            if(respProperties) {
                for(var index=0;index<respProperties.length;index++) {
                    mini.get(respProperties[index].propertyKey).setEnabled(true);
                }
            }
            // 允许财务编辑重量
            if(opRoleName=='CWKC') {
                mini.get("zl").setEnabled(true);
            }
            // 允许采购编辑供应商
            if(opRoleName=='CGKC') {
                mini.get("gys").setEnabled(true);
            }
        }
    }
    //对默认值处理
    processDefault();
});

function queryMaterielDic() {
    $.ajax({
        url:jsUseCtxPath+"/materielExtend/apply/materielDic.do",
        method:'GET',
        async:false,
        success:function (data) {
            materielDic=data;
        }
    });
}

function processSelect() {
    if(!materielDic) {
        return;
    }
    var selectData=materielDic.select;
    if(selectData) {
        for(var [propertyKey,propertyObj] of Object.entries(selectData)) {
            var comboboxData=[];
            for(var val of propertyObj.value) {
                comboboxData.push({key:val,value:val});
            }
            mini.get(propertyKey).load(comboboxData);
        }
    }
}

function processDefault() {
    if(!materielDic) {
        return;
    }
    //对于default、preDefault设置为readonly，对于default设置默认值
    var defaultData=materielDic.default;
    if(defaultData) {
        for(var [propertyKey,propertyObj] of Object.entries(defaultData)) {
            mini.get(propertyKey).setEnabled(false);
            mini.get(propertyKey).setValue(propertyObj.value[0]);
        }
    }
    var preDefaultData=materielDic.preDefault;
    if(preDefaultData) {
        for(var [propertyKey,propertyObj] of Object.entries(preDefaultData)) {
            mini.get(propertyKey).setEnabled(false);
        }
    }

}

//问题物料的标注（并行的几个节点，不允许从是变为否）
function markErrorChanged() {
    var markErrorVal=mini.get("markError").getValue();
    if(markErrorVal=='no') {
        if(originalMarkErrorVal=='yes'&&opRoleName!='SQRKC'&&opRoleName!='GYKC') {
            mini.alert('供方、采购、财务、物流、制造等并行环节不允许将问题物料状态由“是”改为“否”！');
            mini.get("markError").setValue('yes');
            return;
        }
        mini.get("markErrorReason").setValue('');
        mini.get("markErrorUserName").setValue('');
        mini.get("markErrorUserId").setValue('');
        mini.get("markErrorTime").setValue('');
    } else if(markErrorVal=='yes') {
        mini.confirm("标注为“是”后，该条物料信息不需要继续维护，也不会进行必填项检查，最终不会被扩充到SAP。" +
            "<br>供方、采购、财务、物流、制造等并行环节也无法将问题物料状态由“是”改为“否”。确定继续么？", "提示",
            function (action) {
                if (action == "ok") {
                    mini.get("markErrorUserName").setValue(currentUserName);
                    mini.get("markErrorUserId").setValue(currentUserId);
                    mini.get("markErrorTime").setValue(toGetTimeYMDHMS());
                } else {
                    mini.get("markError").setValue('no');
                }
            }
        );
    }
}

function cglxChanged() {
    var cglxVal=mini.get("cglx").getValue();
    var prePropertyConditions=onePrePropertyConditions('cglx');
    changeEffectPropertyVal(prePropertyConditions,cglxVal);
}
function dwChanged(){
    var dwVal=mini.get("dw").getValue();
    var prePropertyConditions=onePrePropertyConditions('dw');
    changeEffectPropertyVal(prePropertyConditions,dwVal);
}


function wllxChanged() {
    var wllxVal=mini.get("wllx").getValue();
    var prePropertyConditions=onePrePropertyConditions('wllx');
    changeEffectPropertyVal(prePropertyConditions,wllxVal);
}

function changeEffectPropertyVal(prePropertyConditions,prePropertyVal) {
    if(!prePropertyConditions) {
        return;
    }
    for(var [key,val] of Object.entries(prePropertyConditions)) {
        if(key=='yes') {
            for(var [innerKey,innerVal] of Object.entries(val)) {
                if(prePropertyVal==innerKey) {
                    for(var effectProperty of Object.values(innerVal)) {
                        mini.get(effectProperty.propertyKey).setValue(effectProperty.propertyValue);
                    }
                }
            }

        } else if(key=='no') {
            for(var [innerKey,innerVal] of Object.entries(val)) {
                if(prePropertyVal!=innerKey) {
                    for(var effectProperty of Object.values(innerVal)) {
                        mini.get(effectProperty.propertyKey).setValue(effectProperty.propertyValue);
                    }
                }
            }
        }
    }
}

//抽取一个前置条件对应的所有影响的属性：
// op--yes--prePropertyVal--影响的属性key和val
// op--no--prePropertyVal--影响的属性key和val
// {
//     "yes":{
//     "FERT":[
//         {
//             "propertyKey": "kmszz",
//             "propertyName": "科目设置组",
//             "propertyValue": "01"
//         },
//         {
//             "propertyKey": "xlhcswj",
//             "propertyName": "序列号参数文件",
//             "propertyValue": ""
//         }
//     ]
// },
//     "no":{
//     "FERT":[
//         {
//             "propertyKey": "kmszz",
//             "propertyName": "科目设置组",
//             "propertyValue": "01"
//         },
//         {
//             "propertyKey": "xlhcswj",
//             "propertyName": "序列号参数文件",
//             "propertyValue": ""
//         }
//     ]
// }
// }
function onePrePropertyConditions(prePropertyKey) {
    var result={};
    if(!materielDic) {
        return result;
    }
    var preDefaultData=materielDic.preDefault;
    if(preDefaultData) {
        for(var propertyObj of Object.values(preDefaultData)) {
            for(var oneVal of Object.values(propertyObj.value)) {
                if(oneVal.enumPrePropertyKey==prePropertyKey) {
                    var enumPrePropertyOpVal=oneVal.enumPrePropertyOp;
                    if(!result[enumPrePropertyOpVal]) {
                        result[enumPrePropertyOpVal]={};
                    }
                    var enumPrePropertyVal=oneVal.enumPrePropertyValue;
                    var oneOpObj=result[enumPrePropertyOpVal];
                    if(!oneOpObj[enumPrePropertyVal]) {
                        oneOpObj[enumPrePropertyVal]=[];
                    }
                    oneOpObj[enumPrePropertyVal].push({propertyKey:oneVal.propertyKey,propertyName:oneVal.propertyName,propertyValue:oneVal.propertyValue});
                }
            }
        }
    }
    return result;
}

function saveMateriel() {
    materielForm.validate();
    if (materielForm.isValid() == false) {
        return;
    }
    var formData=materielForm.getData();
    //去除空格
    for(var key in formData) {
        formData[key]=$.trim(formData[key]);
    }
    //采购存储地点字母默认转化为大写
    if (formData.cgccdd) {
        formData.cgccdd = formData.cgccdd.toUpperCase();
    }
    var r = new RegExp("^(-?\\d+)(\\.\\d+)?$");
    if(formData.jg&&!r.test(formData.jg)) {
        mini.alert('价格请填写数字！');
        return;
    }
    //如果物料标注为问题物料，则原因必填
    if(formData.markError=='yes') {
        var markErrorReason=formData.markErrorReason;
        if(!markErrorReason) {
            mini.alert('请填写问题物料原因！');
            return;
        }
    }

    //按照登录人的角色不同进行物料必填项的校验
    var requiredCheck=checkOneMaterielRequired(respProperties,formData,opRoleName);
    if(!requiredCheck.success) {
        mini.alert(requiredCheck.message);
        return;
    }
    //保存或更新
    var postData=mini.encode(formData);
    $.ajax({
        url:jsUseCtxPath+"/materielExtend/apply/materielEditSave.do",
        data:postData,
        type:"POST",
        contentType: 'application/json',
        success:function (data) {
            if(data) {
                mini.alert(data.message,'提示',function (action) {
                    if(data.success){
                        CloseWindow();
                    }
                });
            }
        }
    });
}
