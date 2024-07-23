$(function () {
    initUpload();
});
function initUpload(){
    $('#fileUploadDiv').Huploadify({
        formData:{kjlwId:kjlwId},
        sendFileAttr:["fileName","fileSize"],
        uploadFileList:grid,
        url:jsUseCtxPath+'/zhgl/core/kjlw/fileUpload.do',
        onUploadStart:function (file) {
            var row = this.uploadFileList.findRow(function(row){
                if(row.id == file.id) return true;
            });
            return {success:true,message:""};
        },
        addFileToGrid: function (files) {
            for(var i=0;i<files.length;i++) {
                var file=files[i];
                var row = {id:file.id,fileName:file.name,status:file.status,fileSize:bytesToSize(file.size),complete:0};
                this.uploadFileList.addRow(row);
            }
        }
    });
}




