$(function(){
    $('#fileUploadDiv').Huploadify({
        sendFileAttr:["fileName","fileSize","description"],
        uploadFileList:multiuploadGrid,
        url:jsUseCtxPath+'/standardManager/core/standardConfig/templateUpload.do',
        onUploadStart:function (file) {
            var row = this.uploadFileList.findRow(function(row){
                if(row.id == file.id) return true;
            });
            if(!row.description) {
                return {success:false,message:"“"+row.fileName+"”请填写文件说明！"};
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
});