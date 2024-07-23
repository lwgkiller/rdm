$(function () {
    initUpload();
});
function initUpload(){
    $('#fileUploadDiv').Huploadify({
        formData:{jsjdsId:jsjdsId},
        sendFileAttr:["fileName","fileSize","fileType"],
        uploadFileList:grid,
        url:jsUseCtxPath+'/zhgl/core/jsjds/fileUpload.do',
        onUploadStart:function (file) {
            var row = this.uploadFileList.findRow(function(row){
                if(row.id == file.id) return true;
            });
            if(!row.fileType) {
                return {success:false,message:"“"+row.fileName+"”没有选择文件类别，请点击单元格处选择！"};
            }
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




