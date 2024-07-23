$(function () {
    initUpload();
});
function initUpload(){
    $('#fileUploadDiv').Huploadify({
        formData:{rjzzId:rjzzId},
        sendFileAttr:["fileName","fileSize","fjlx"],
        uploadFileList:grid,
        url:jsUseCtxPath+'/zhgl/core/rjzz/fileUpload.do',
        onUploadStart:function (file) {
            var row = this.uploadFileList.findRow(function(row){
                if(row.id == file.id) return true;
            });
            if(!row.fjlx) {
                return {success:false,message:"请选择附件类型"};
            }
            return {success:true,message:""};
        },
        addFileToGrid: function (files) {
            for(var i=0;i<files.length;i++) {
                var file=files[i];
                var row = {id:file.id,fileName:file.name,status:file.status,fileSize:bytesToSize(file.size),complete:0,fjlx:file.fjlx};
                this.uploadFileList.addRow(row);
            }
        }
    });
}




