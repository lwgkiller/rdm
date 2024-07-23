//
// function findBigType() {
//     $.ajax({
//         url: jsUseCtxPath + "/xcmgBudget/core/budgetMonth/queryBigTypeList.do",
//         contentType: 'application/json',
//         async: false,
//         success: function (data) {
//             if (data) {
//                 mini.get("bigTypeId").setData(data);
//             }
//         }
//     });
// }
//
// function findDeptByBigType() {
//     var bigTypeId=mini.get("bigTypeId").getValue();
//     if(!bigTypeId) {
//         return;
//     }
//     $.ajax({
//         url: jsUseCtxPath + "/xcmgBudget/core/budgetMonth/queryDeptListByTypeId.do?bigTypeId="+bigTypeId,
//         contentType: 'application/json',
//         async: false,
//         success: function (data) {
//             if (data) {
//                 mini.get("deptId").setData(data);
//             }
//         }
//     });
// }

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


function convert2Number(value) {
    if(!value) {
        return value;
    }
    var value=$.trim(value);
    if(value.indexOf(",")!=-1) {
        value=value.replace(/,/g,"");
    }
    return value;
}