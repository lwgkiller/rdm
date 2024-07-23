$(function () {
})

function addForm() {
    let url = jsUseCtxPath + "/rdmZhgl/core/bbs/getEditPage.do?action=add&plate=" + plate+"&model="+model;
    mini.open({
        title: bbsList_fbtz,
        url: url,
        width: 1000,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            listGrid.reload();
        }
    });
}

//修改
function editForm(id) {
    var url = jsUseCtxPath + "/rdmZhgl/core/bbs/getEditPage.do?action=edit&&id=" + id;
    var title = bbsList_bjtz;
    mini.open({
        title: title,
        url: url,
        width: 1000,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}

//删除记录
function delRowByID(id) {
    mini.confirm(bbsList_qdqxxzjl, bbsList_ts, function (action) {
        if (action != 'ok') {
            return;
        } else {
            _SubmitJson({
                url: jsUseCtxPath + "/rdmZhgl/core/bbs/remove.do",
                method: 'POST',
                data: {ids: id},
                success: function (text) {
                    if (listGrid) {
                        listGrid.reload();
                    }
                }
            });
        }
    });
}
function startGjtaApply() {
    var row = listGrid.getSelecteds();
    if(row.length!=1){
        mini.alert(bbsList_qxzytsjjxcz);
        return;
    }
    var bbsType = row[0].bbsType;
    var INST_ID_ = row[0].INST_ID_;
    var id = row[0].id;
    var CREATE_BY_ = row[0].CREATE_BY_;
    if(bbsType!='gjta'){
        mini.alert(bbsList_gjta);
        return;
    }
    if(row[0].closed!='0'){
        mini.alert(bbsList_gbdtz);
        return;
    }
    if(INST_ID_){
        mini.alert(bbsList_lcyjcz);
        return;
    }
    if(CREATE_BY_!=currentUserId){
        mini.alert(bbsList_qcjr);
        return;
    }
    var url = jsUseCtxPath + "/bpm/core/bpmInst/bbsFlow/start.do?plate="+row[0].plate+'&model='+row[0].model+'&bbsType='+bbsType+'&bbsId='+id;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            searchFrm()
        }
    }, 1000);
}
//移帖
function movePost(id) {
    mini.confirm(bbsList_qdjtz, bbsList_ts, function (action) {
        if (action != 'ok') {
            return;
        } else {
            movePostWindow.show();
            mini.get('postId').setValue(id);
        }
    });
}
//关帖
function closed(id) {
    mini.confirm(bbsList_gthj, bbsList_ts, function (action) {
        if (action != 'ok') {
            return;
        } else {
            closeBbsWindow.show();
            mini.get('id').setValue(id);
        }
    });
}
//开贴
function openPost(id) {
    mini.confirm(bbsList_qdcs, bbsList_ts, function (action) {
        if (action != 'ok') {
            return;
        } else {
            _SubmitJson({
                url: jsUseCtxPath + "/rdmZhgl/core/bbs/open.do",
                method: 'POST',
                data: {id: id},
                success: function (text) {
                    searchFrm()
                }
            });
        }
    });
}
function closePost() {
    opinionForm.validate();
    if (!opinionForm.isValid()) {
        return;
    }
    var formData = opinionForm.getData();
    var config = {
        url: jsUseCtxPath+"/rdmZhgl/core/bbs/close.do",
        method: 'POST',
        data: formData,
        success: function (result) {
            //如果存在自定义的函数，则回调
            var result=mini.decode(result);
            if(result.success){
                closeBbsWindow.hide();
                mini.get('id').setValue('');
                searchFrm()
            }else{
            };
        }
    }
    _SubmitJson(config);
}
function removePost() {
    postForm.validate();
    if (!postForm.isValid()) {
        return;
    }
    var formData = postForm.getData();
    var config = {
        url: jsUseCtxPath+"/rdmZhgl/core/bbs/removePost.do",
        method: 'POST',
        data: formData,
        success: function (result) {
            //如果存在自定义的函数，则回调
            var result=mini.decode(result);
            if(result.success){
                movePostWindow.hide();
                mini.get('postId').setValue('');
                searchFrm()
            }else{
            };
        }
    }
    _SubmitJson(config);
}
