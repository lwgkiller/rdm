function toGetTimeYMDHMS(val) {
    var date=new Date();
    if(val) {
        date=new Date(val);
    }
    var year=date.getFullYear();
    var month=date.getMonth()+1;
    var day=date.getDate();
    var hour=date.getHours();
    var minute=date.getMinutes();
    var seconds=date.getSeconds();
    if(hour<10) {
        hour='0'+hour;
    }
    if(minute<10) {
        minute='0'+minute;
    }
    if(seconds<10) {
        seconds='0'+seconds;
    }
    return year+'-'+month+'-'+day+' '+hour+":"+minute+':'+seconds;
}

//检查某个角色填写的物料必填信息是否完整
function checkOneMaterielRequired(respProperties,oneMateriel,opRoleName) {
    var checkResult={};
    if(!respProperties) {
        checkResult.success=true;
        return checkResult;
    }
    //问题物料不进行必填项检查
    if(oneMateriel.markError=='yes') {
        checkResult.success=true;
        return checkResult;
    }
    for(var index=0;index<respProperties.length;index++) {
        var oneProperty=respProperties[index];
        if(oneProperty.respRoleKey!=opRoleName) {
            continue;
        }
        //非必填项跳过
        if(oneProperty.required!='yes') {
            continue;
        }
        var propertyKey=oneProperty.propertyKey;
        var propertyName=oneProperty.propertyName;
        var requiredPrePropertyKey=oneProperty.requiredPrePropertyKey;
        //无需前置条件的必填
        if(!requiredPrePropertyKey) {
            if(!oneMateriel[propertyKey]) {
                checkResult.success=false;
                checkResult.message=propertyName+'为必填项！';
                return checkResult;
            }
        } else {
            //    前置条件满足时必填
            var requiredPrePropertyValue=oneProperty.requiredPrePropertyValue;
            if(oneMateriel[requiredPrePropertyKey]==requiredPrePropertyValue) {
                if(!oneMateriel[propertyKey]) {
                    var requiredPrePropertyName=oneProperty.requiredPrePropertyName;
                    checkResult.success=false;
                    checkResult.message=propertyName+'在'+requiredPrePropertyName+'等于'+requiredPrePropertyValue+'时为必填项！';
                    return checkResult;
                }
            }
        }
    }
    checkResult.success=true;
    return checkResult;
}

function jumpToPropertyDesc(){
    window.open(jsUseCtxPath + '/materielExtend/apply/materielPropertyDescPage.do','物料主数据扩充说明')
}