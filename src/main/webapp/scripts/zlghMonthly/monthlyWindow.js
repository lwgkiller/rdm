$(function () {
    $('#fileUploadDiv').Huploadify({
        sendFileAttr: ["fileName", "fileSize", "description", "ykTime"],
        uploadFileList:monthlyFile,
        url: jsUseCtxPath + '/strategicplanning/core/monthly/monthlyUpload.do?type='+type,
        onUploadStart: function (file) {
            var row = this.uploadFileList.findRow(function (row) {
                if (row.id == file.id) return true;
            });
            if (row.fileName){
                var fileNameSuffix = row.fileName.split('.').pop();
                if (fileNameSuffix != 'pdf') {
                    return {success: false, message: "“" + row.fileName + "”不是pdf文件！"}
                }
            }
            if (!row.ykTime){
                return {success: false, message: "“" + row.fileName + "”请选择月刊时间！"}
            }

            return {success: true, message: ""};
        },
        addFileToGrid: function (files) {
            for (var i = 0; i < files.length; i++) {
                var file = files[i];
                var row = {
                    id: file.id,
                    fileName: file.name,
                    status: file.status,
                    fileSize: bytesToSize(file.size),
                    complete: 0
                };
                this.uploadFileList.addRow(row);
            }
        }
    });
});

