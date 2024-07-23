$(function () {
    mini.get("systemCategory").load(systemCategoryArr);
    fieldForm.setData(fieldObj);
});
function saveFieldData() {
    //判断表单内容是否完整
    var formData = fieldForm.getData();
    var checkResult=fieldFormValid(formData);
    if(!checkResult) {
        return;
    }
    var config = {
        url: jsUseCtxPath+"/standardManager/core/standardField/save.do",
        method: 'POST',
        data: formData,
        success: function (result) {
            //如果存在自定义的函数，则回调
            var result=mini.decode(result);
            if(result.success){
                CloseWindow('ok');
            }
        }
    };
    _SubmitJson(config);
}

function fieldFormValid(formData) {
    if(!formData) {
        mini.alert('请填写表单！');
        return false;
    }
    if(!$.trim(formData.fieldName)) {
        mini.alert('请填写专业领域名称！');
        return false;
    }
    if(!$.trim(formData.systemCategoryId)) {
        mini.alert('请选择标准体系类别！');
        return false;
    }
    if(!$.trim(formData.ywRespUserId)) {
        mini.alert('请选择业务责任人！');
        return false;
    }
    if(!$.trim(formData.respUserId)) {
        mini.alert('请选择管理责任人！');
        return false;
    }
    return true;
}
