$(function () {
    searchFrm();
});

//物料扩充申请的窗口
function editOrViewApply(applyNo,action) {
    var title="物料扩充申请";
    var url=jsUseCtxPath+"/materielExtend/apply/editPage.do?applyNo="+applyNo+"&action="+action;
    var winObj=window.open(url,title);
    var loop = setInterval(function() {
        if(winObj.closed) {
            clearInterval(loop);
            if(applyListGrid){
                applyListGrid.reload();
            };
        }
    }, 1000);
}

//删除记录
function removeApply(record) {
    var rows =[];
    if(record) {
        rows.push(record);
    } else {
        rows = applyListGrid.getSelecteds();
    }

    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            //对于非草稿状态的申请不能删除
            var ids = [];
            var existCannotDelete=false;
            for (var i = 0; i < rows.length; i++) {
                var r = rows[i];
                if(r.applyStatus=='draft') {
                    ids.push(r.applyNo);
                } else {
                    existCannotDelete=true;
                }
            }
            if(existCannotDelete) {
                mini.alert("非草稿状态的数据不会被删除！");
            }
            if(ids.length>0) {
                _SubmitJson({
                    url: jsUseCtxPath+"/materielExtend/apply/applyDel.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    contentType:'application/json',
                    success: function (text) {
                        if(applyListGrid) {
                            applyListGrid.reload();
                        }
                    }
                });
            }
        }
    });
}

//查询申请单各个节点的处理状态
function processStatusQuery(applyNo){
    $.ajax({
        url:jsUseCtxPath+"/materielExtend/apply/applyDetail.do?applyNo="+applyNo,
        success:function (data) {
            if(data) {
                //赋值
                processStatusForm.setData(data);
                processStatusForm.setEnabled(false);
                processStatus();
            }
            processStatusWindow.show();
        }
    });
}

//处理几个处理节点状态的中文
function processStatus() {
    toProcessNameColor("gyStatus",'gyCommitUserName','gyCommitTime');
    toProcessNameColor("gfStatus",'gfCommitUserName','gfCommitTime');
    toProcessNameColor("cgStatus",'cgCommitUserName','cgCommitTime');
    toProcessNameColor("cwStatus",'cwCommitUserName','cwCommitTime');
    toProcessNameColor("wlStatus",'wlCommitUserName','wlCommitTime');
    toProcessNameColor("zzStatus",'zzCommitUserName','zzCommitTime');
}

function toProcessNameColor(statusInputName,userInputName,timeInputName) {
    var statusCode=mini.get(statusInputName).getValue();
    var nameAndColor={};
    switch (statusCode) {
        case '00':
            nameAndColor.name='未开始';
            nameAndColor.color='#8080809e';
            break;
        case '01':
            nameAndColor.name='进行中';
            nameAndColor.color='#ddf3149e';
            break;
        case '02':
            nameAndColor.name='已完成';
            nameAndColor.color='#5ede1a9e';
            break;
    }
    mini.get(statusInputName).setValue(nameAndColor.name)
    $("input[name="+statusInputName+"]").parent().css("background-color",nameAndColor.color);
    $("input[name="+userInputName+"]").parent().css("background-color",nameAndColor.color);
    $("input[name="+timeInputName+"]").parent().css("background-color",nameAndColor.color);
}

function exportBtn(){
    var params=[];
    var parent=$(".search-form");
    var inputAry=$("input",parent);
    inputAry.each(function(i){
        var el=$(this);
        var obj={};
        obj.name=el.attr("name");
        if(!obj.name) return true;
        obj.value=el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}


