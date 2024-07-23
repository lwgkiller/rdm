$(function () {
    if(flag=='outer'){
        mini.get('closeWindow').hide();
    }
    KindEditor.ready(function(K) {
        editor = K.create('#editor', {
            //这里是指定的文件上传input的的属性名
            filePostName: "uploadFile",
            uploadJson: jsUseCtxPath+"/kindeditor/upload",
            resizeType: 1,
            allowPreviewEmoticons: true,
            allowImageUpload: true,
            afterUpload: function(url, data, name) {
                picName += url.split('=')[1]+",";
            }
        });
        if(action!='add'){
            noticeForm.setData(applyObj);
            setContent(applyObj.content);
        }else{
            noticeForm.setData(applyObj);
        }
    });
    setFormStatus();
})
function setFormStatus(flag) {
    noticeForm.setEnabled(flag);
    mini.get('bbsType').setEnabled(true);
}

function typeChanged(e) {
    var obj = e.selected;
    if(obj == undefined){
        setFormStatus(false)
    }else{
        if(obj.key_!= 'gjta'){
            setFormStatus(true)
        }
    }
    if(obj.key_== 'gjta'){
        doApply()
    }
}
function doApply() {
    mini.confirm(bbsEdit_gjta, bbsEdit_ts, function (action) {
        if (action != 'ok') {
            mini.get('bbsType').setValue('');
            return;
        } else {
            var bbsType =   mini.get('bbsType').getValue();
            var url = jsUseCtxPath + "/bpm/core/bpmInst/bbsFlow/start.do?plate="+plate+'&model='+model+'&bbsType='+bbsType;
            var winObj = window.open(url);
            var loop = setInterval(function () {
                if (winObj.closed) {
                    clearInterval(loop);
                    CloseWindow();
                }
            }, 1000);
        }
    });

}
