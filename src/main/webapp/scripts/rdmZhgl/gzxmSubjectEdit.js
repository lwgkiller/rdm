$(function () {
    if(action!='add'){
        planForm.setData(applyObj);
        mini.get('id').setValue(applyObj.id);
    }
    if (action == 'view') {
        planForm.setEnabled(false);
        $('#save').hide();
    }
    if (action == 'edit') {
        planForm.setEnabled(false);
        if(isReporter){
            mini.get('currentStage').setEnabled(true);
            mini.get('processDetail').setEnabled(true);
        }
        if(gzxmAdmin){
            planForm.setEnabled(true);
        }
    }
})
function saveData() {
    planForm.validate();
    if (!planForm.isValid()) {
        return;
    }
    var formData = planForm.getData();
    var config = {
        url: jsUseCtxPath+"/rdmZhgl/core/gzxm/subject/save.do",
        method: 'POST',
        data: formData,
        success: function (result) {
            //如果存在自定义的函数，则回调
            var result=mini.decode(result);
            if(result.success){
                CloseWindow("ok");
            }else{
            };
        }
    }
    _SubmitJson(config);
}
