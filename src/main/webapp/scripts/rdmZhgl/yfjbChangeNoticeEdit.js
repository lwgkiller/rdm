$(function () {
    if(action!='add'){
        infoForm.setData(applyObj);
        mini.get('id').setValue(applyObj.id);
    }
    if (action == 'view') {
        infoForm.setEnabled(false);
        $('#save').hide();
    }
})
function saveData() {
    infoForm.validate();
    if (!infoForm.isValid()) {
        return;
    }
    var formData = infoForm.getData();
    var config = {
        url: jsUseCtxPath+"/rdmZhgl/core/yfjb/changeNotice/save.do",
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
