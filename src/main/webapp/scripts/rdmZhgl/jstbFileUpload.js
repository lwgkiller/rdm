$(function () {
    initUpload();
});
function initUpload(){
    $('#fileUploadDiv').Huploadify({
        formData:{jstbId:jstbId},
        sendFileAttr:["fileName","fileSize","typeId"],
        uploadFileList:grid,
        url:jsUseCtxPath+'/zhgl/core/jsmm/fileUpload.do',
        onUploadStart:function (file) {
            var row = this.uploadFileList.findRow(function(row){
                if(row.id == file.id) return true;
            });

            return {success:true,message:""};
        },
        addFileToGrid: function (files) {
            for(var i=0;i<files.length;i++) {
                var file=files[i];
                var row = {id:file.id,fileName:file.name,status:file.status,fileSize:bytesToSize(file.size),complete:0,typeId:typeInfos[0].typeId,typeName:typeInfos[0].typeName};
                this.uploadFileList.addRow(row);
            }
        }
    });
}

function onProgressRenderer(e) {
    var record = e.record;
    var value = e.value;
    var uid = record._uid;
    var s='<div class="progressbar">'
        + '<div class="progressbar-percent" style="width:' + value + '%;"></div>'
        + '<div class="progressbar-label">' + value + '%</div>'
        + '</div>';
    return s;
}


