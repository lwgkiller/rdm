(function($){
$.fn.Huploadify = function(opts){
	var defaults = {
		fileTypeExts:'',//允许上传的文件类型，格式'*.jpg;*.doc'
		url:'',//文件提交的地址
		method:'post',//发送请求的方式，get或post
		multi:true,//是否允许选择多个文件
		formData:null,//发送给服务端的参数，格式：{key1:value1,key2:value2}
		fileObjName:'file',//在后端接受文件的参数名称，如PHP中的$_FILES['file']
		fileSizeLimit:100000,//允许上传的文件大小，单位KB
		showUploadedPercent:true,//是否实时显示上传的百分比，如20%
		showUploadedSize:false,//是否实时显示已上传的文件大小，如1M/2M
		buttonText:'选择文件',//上传按钮上的文字
		onUploadStart:null,//上传开始时的动作
		onUploadSuccess:null,//上传成功的动作
		onUploadComplete:null,//上传完成的动作
		onUploadError:null, //上传失败的动作
		onInit:null,//初始化时的动作
		onCancel:null,//删除掉某个文件后的回调函数，可传入参数file
        uploadFileList:null,
        addFileToGrid:null,
        sendFileAttr:null
	};
		
	var option = $.extend(defaults,opts);
	
    //将输入的文件类型字符串转化为数组,原格式为*.jpg;*.png
    var getFileTypes = function(str){
        var result = [];
        if(!str) {
        	return result;
		}
        var arr1 = str.split(";");
        for(var i=0,len=arr1.length;i<len;i++){
            result.push(arr1[i].split(".").pop());
        }
        return result;
    }

	this.each(function(){
		var _this = $(this);

		//创建文件对象
	  var fileObj = {
		  fileInput: _this.find('.file-input'),				//html file控件
		  uploadFileList : option.uploadFileList,		//上传文件表格
		  url: option.url,						//ajax地址
		  fileFilter: [],					//待上传文件数组

		  //文件选择后
		  onSelect: function(files){
				for(var i=0,len=this.fileFilter.length;i<len;i++) {
                    var file = this.fileFilter[i];
                    if(file.status=='准备上传') {
                        this.funUploadFile(file);
                    }
                }
			},
		  onClearAll:function() {
              this.uploadFileList.clearRows();
              this.fileFilter.clear();
          },
		  //更新进度条
		  onProgress: function(file, event) {
		  	if(event.lengthComputable) {
                var percent = (event.loaded *100/ event.total).toFixed(2);
                var row = fileObj.uploadFileList.findRow(function(row){
                    if(row.id == file.id) return true;
                });
                fileObj.uploadFileList.updateRow(row,{complete:percent});
			}
	  	},

		  /* 开发参数和内置方法分界线 */
          filter: function(files) {		//选择文件组的过滤方法
              var arr = [];
              var typeArray = getFileTypes(option.fileTypeExts);
              for(var i=0,len=files.length;i<len;i++){
                  var thisFile = files[i];
                  if(typeArray.length==0) {
                      arr.push(thisFile);
				  }
                  else {
                      if($.inArray(thisFile.name.split('.').pop(),typeArray)>=0){
                          arr.push(thisFile);
                      }
                      else{
                          alert('文件'+thisFile.name+'类型不允许！');
                      }
				  }
              }
              return arr;
          },
		  //获取选择文件，file控件
		  funGetFiles: function(e) {
			  var files = e.target.files;
			  files = this.filter(files);
			  for(var i=0,len=files.length;i<len;i++){
			  	files[i].status='准备上传';
			  	var totalFileCount=this.fileFilter.push(files[i]);
                  files[i].id=totalFileCount-1;
			  }
              // this.addFileToGrid(files);
			  if(option.addFileToGrid) {
                  option.addFileToGrid(files);
			  }
			  return this;
		  },
		  //文件上传
		  funUploadFile: function(file) {
              if(option.onUploadStart) {
              	var startResult=option.onUploadStart(file);
              	if(!startResult.success) {
              		alert(startResult.message);
              		return;
				}
			  }
			  var xhr = false;
			  try{
				 xhr=new XMLHttpRequest();//尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
			  }catch(e){	  
				xhr=ActiveXobject("Msxml12.XMLHTTP");//使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
			  }
			  
			  if (xhr.upload) {
				  // 更新上传数组和列表中的信息
                  file.status='上传中';
                  var row = fileObj.uploadFileList.findRow(function(row){
                      if(row.id == file.id) return true;
                  });
                  fileObj.uploadFileList.updateRow(row,{status:file.status});
                  //更新进度条
				  xhr.upload.addEventListener("progress", function(event) {
					  fileObj.onProgress(file, event);
				  },false);
	  
				  // 文件上传成功或是失败
				  xhr.onreadystatechange = function(e) {
				  	if (xhr.readyState == 4) {
                          if (xhr.status == 200) {
							  // 根据服务器返回的数据更新文件状态
							  var response = JSON.parse(xhr.responseText);
							  if (response.success == true){
								  file.status='上传成功';
								  // 根据更新上传数组和列表中的信息
								  var row = fileObj.uploadFileList.findRow(function(row){
									  if(row.id == file.id) return true;
								  });
								  fileObj.uploadFileList.updateRow(row,{status:file.status,complete:100});
							  }else{
								  file.status = '上传失败';
								  var row = fileObj.uploadFileList.findRow(function(row){
									  if(row.id == file.id) return true;
								  });
								  fileObj.uploadFileList.updateRow(row,{status:file.status,complete:0});
								  mini.alert(response.message);
							  }


                          }
                      }
				  };

				  // 开始上传
				  xhr.open(option.method, this.url, false);
				  xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
				  var fd = new FormData();
				  if(option.formData){
					for(key in option.formData){
						fd.append(key,option.formData[key]);
					}
				  }
				  fd.append(option.fileObjName,file);
				  if(option.sendFileAttr) {
					for(var i=0;i<option.sendFileAttr.length;i++) {
						var key=option.sendFileAttr[i];
						var value=row[key];
						if(!value) {
							value="";
						}
                        fd.append(key,value);
					}
				  }
				  xhr.send(fd);
			  }
		  },

		  init: function() {	  
			  //文件选择控件选择
			  if (this.fileInput.length>0) {
				  this.fileInput.change(function(e) { 
				  	fileObj.funGetFiles(e); 
				  });	
			  }
			  
			  //点击上传按钮时触发file的click事件
			  _this.find('.file-select').on('click',function(){
				  _this.find('.file-input').trigger('click');
				});

              _this.find('.file-upload').on('click',function(e){
                  fileObj.onSelect();
              });
              _this.find('.file-clear').on('click',function(e){
                  fileObj.onClearAll();
              });
              fileObj.uploadFileList.on("drawcell", function (e) {
                  var field = e.field;
                  var record = e.record;
                  var uid = record._uid;
                  if (field == "action") {
                      e.cellHtml = '<a class="upicon-remove" title="删除" name="' + uid + '"><a>';
                  }
              });
              $(document.body).on("click", ".upicon-remove", function () {
                  var uid = $(this).attr("name");
                  var row = fileObj.uploadFileList.getRowByUID(uid);
                  if (row.status!='上传中') {
                      fileObj.uploadFileList.removeRow(row);
                      fileObj.fileFilter[row.id].status='已删除';
                  }
              });

			  option.onInit&&option.onInit();
		  }
  	};

		//初始化文件对象
		fileObj.init();
	}); 
}	

})(jQuery)
