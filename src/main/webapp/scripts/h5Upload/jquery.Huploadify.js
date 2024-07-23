/*
Huploadify
version : 2.1.1
author : 吕大豹
publish date : 2014/01/27
site : http://www.cnblogs.com/lvdabao
description : 修正了第一次将文件完整上传后，再次上传相同文件仍然会发送请求的bug
*/
(function($){
$.fn.Huploadify = function(opts){
	var itemTemp = '<div id="${fileID}" class="uploadify-queue-item"><div class="uploadify-progress"><div class="uploadify-progress-bar"></div></div><span class="up_filename">${fileName}</span><span class="uploadbtn">上传</span><span class="delfilebtn">删除</span></div>';
	var defaults = {
		fileTypeExts:'*.*',//允许上传的文件类型，格式'*.jpg;*.doc'
		url:'',//文件提交的地址
		auto:false,//是否开启自动上传
		method:'post',//发送请求的方式，get或post
		multi:true,//是否允许选择多个文件
		formData:{},//发送给服务端的参数，格式：{key1:value1,key2:value2}
		fileObjName:'file',//在后端接受文件的参数名称，如PHP中的$_FILES['file']
		fileSizeLimit:99999999,//允许上传的文件大小，单位KB
		showUploadedPercent:true,//是否实时显示上传的百分比，如20%
		showUploadedSize:false,//是否实时显示已上传的文件大小，如1M/2M
		buttonText:'选择文件',//上传按钮上的文字
		removeTimeout: 9999999,//上传完成后进度条的消失时间
		itemTemplate:itemTemp,//上传队列显示的模板
		breakPoints:true,//是否开启断点续传
		fileSplitSize:2*1024*1024,//断点续传的文件块大小，单位Byte，默认2M
		getUploadedSize:null,//类型：function，自定义获取已上传文件的大小函数，用于开启断点续传模式，可传入一个参数file，即当前上传的文件对象，需返回number类型
		saveUploadedSize:null,//类型：function，自定义保存已上传文件的大小函数，用于开启断点续传模式，可传入两个参数：file：当前上传的文件对象，value：已上传文件的大小，单位Byte
		saveInfoLocal:false,//用于开启断点续传模式，是否使用localStorage存储已上传文件大小
		onUploadStart:null,//上传开始时的动作
		onUploadSuccess:null,//上传成功的动作
		onUploadComplete:null,//上传完成的动作
		onUploadError:null, //上传失败的动作
		onInit:null,//初始化时的动作
		onCancel:null,//删除掉某个文件后的回调函数，可传入参数file
		onSelect:null,//选择文件后执行的动作，可传入参数files，文件列表
        uploadFileList:null,//文件上传表格list
        addFileToGrid:null,
        sendFileAttr:null
	}

	var option = $.extend(defaults,opts);	

	//将文件的单位由bytes转换为KB或MB，若第二个参数指定为true，则永远转换为KB
	var formatFileSize = function(size,byKB){
		if (size> 1024 * 1024&&!byKB){
			size = (Math.round(size * 100 / (1024 * 1024)) / 100).toString() + 'MB';
		}
		else{
			size = (Math.round(size * 100 / 1024) / 100).toString() + 'KB';
		}
		return size;
	}

    function bytesToSize(bytes) {
        if (bytes === 0) return '0 B';
        var k = 1024,
            sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
            i = Math.floor(Math.log(bytes) / Math.log(k));

        return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
    }

	//根据文件序号获取文件
	var getFile = function(index,files){
		for(var i=0;i<files.length;i++){	   
		  if(files[i].index == index){
			  return files[i];
			}
		}
		return false;
	}
	
	//将输入的文件类型字符串转化为数组,原格式为*.jpg;*.png
	var getFileTypes = function(str){
		var result = [];
        if(!str) {
            return result;
        }
		var arr1 = str.split(";");
		for(var i=0,len=arr1.length;i<len;i++){
		    if(arr1[i]) {
                result.push(arr1[i].split(".").pop());
            }
		}
		return result;
	}

	var mimetypeMap = {
		zip : ['application/x-zip-compressed'],
		jpg : ['image/jpeg'],
		png : ['image/png'],
		gif : ['image/gif'],
		doc : ['application/msword'],
		xls : ['application/msexcel'],
		docx : ['application/vnd.openxmlformats-officedocument.wordprocessingml.document'],
		xlsx : ['application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'],
		ppt : ['application/vnd.ms-powerpoint '],
		pptx : ['application/vnd.openxmlformats-officedocument.presentationml.presentation'],
		mp3 : ['audio/mp3'],
		mp4 : ['video/mp4'],
		pdf : ['application/pdf']
	};

	//根据后缀名获得文件的mime类型
	var getMimetype = function(name){
		return mimetypeMap[name];
	}

	//根据配置的字符串，获得上传组件accept的值
	var getAcceptString = function(str){
		var types = getFileTypes(str);
		var result = [];
		for(var i=0,len=types.length;i<len;i++){
			var mime = getMimetype(types[i]);
			if(mime){
				result.push(mime);				
			}
		}
		return result.join(',');
	}

	//发送文件块函数
	var sendBlob = function(url,xhr,file,formdata,otherData){
	    if(!url) {
	        alert("上传url为空！");
	        return;
        }
        if(!url.startWith("/")) {
            url="/"+url;
        }
        url=jsUseCtxPath+url;
	 	xhr.open(option.method, url, true);
		xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
		var fd = new FormData();
		fd.append(option.fileObjName,file);
		if(formdata){
            for(key in formdata){
                fd.append(key,formdata[key]);
            }
	    }
        if(otherData){
            for(key in otherData){
                fd.append(key,otherData[key]);
            }
        }
		xhr.send(fd);
	}

	var fileObj = null;

	this.each(function(){
		var _this = $(this);

		//创建文件对象
	  fileObj = {
	      uploadAllowed: true,
		  fileInput: _this.find('.file-input'),				//html file控件
		  uploadFileList : option.uploadFileList,
		  container : _this, //上传控件的外层div引用
		  url: option.url,						//ajax地址
		  fileFilter: [],					//过滤后的文件数组
		  uploadOver:false, //一次上传是否真正结束，用于断点续传的情况
		  filter: function(files) {		//选择文件组的过滤方法
			  var arr = [];
			  var typeArray = getFileTypes(option.fileTypeExts);
			  if(typeArray.length>0){
				  for(var i=0,len=files.length;i<len;i++){
				  	var thisFile = files[i];
			  		if(parseInt(formatFileSize(thisFile.size,true))>option.fileSizeLimit){
			  			alert('文件'+thisFile.name+'大小超出限制！');
			  			continue;
			  		}
                    if($.inArray(thisFile.name.split('.').pop().toLowerCase(),typeArray)>=0 || $.inArray('*', typeArray)>=0){
                        arr.push(thisFile);
                    }
                    else{
                        alert('文件'+thisFile.name+'类型不允许！');
                    }
				  }
				}
			  return arr;  	
		  },
		  //文件选择后生成对应的每一条显示
          addSelectFiles: function(files){
              if(option.addFileToGrid) {
                  option.addFileToGrid(files);
              }
			},
		  onProgress: function(file, loaded, total) {
				var percent = (loaded * 100/ total).toFixed(2);
                  var row = fileObj.uploadFileList.findRow(function(row){
                      if(row.id == file.id) return true;
                  });
                var thisTimePercent = parseFloat(percent)+parseFloat(row.complete);
                if(thisTimePercent>100) {
                    thisTimePercent=100.00;
                }
              fileObj.uploadFileList.updateRow(row,{complete:thisTimePercent.toFixed(2)});
	  	},


		  //获取已上传的文件片大小，当开启断点续传模式
		  funGetUploadedSize: function(file){
		  	if(option.getUploadedSize){
		  		return option.getUploadedSize(file);
		  	}
		  	else{
		  		if(option.saveInfoLocal){
		  			return parseInt(localStorage.getItem(file.name)) || 0;	
		  		} else {
		  		    return 0;
                }
		  	}
		  },

		  //获取选择文件，file控件
		  funGetFiles: function(e) {	  
			  // 获取文件列表对象
			  var files = e.target.files;
			  //继续添加文件
			  files = this.filter(files);
			  for(var i=0,len=files.length;i<len;i++){
                  var totalFileCount=this.fileFilter.push(files[i]);
                  files[i].id=totalFileCount-1;
                  files[i].status='准备上传';
                  files[i].uuid = Math.uuidFast();
			  }
              this.addSelectFiles(files);
			  return this;
		  },
          onClearAll:function() {
              this.uploadFileList.clearRows();
              this.fileFilter.clear();
              this.fileInput.val('');
              this.uploadAllowed = false;
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
			  var originalFile = file;//保存原始文件
              //校正进度条和上传比例的误差
              var row = fileObj.uploadFileList.findRow(function(row){
                  if(row.id == file.id) return true;
              });
              var filterFileArr = fileObj.fileFilter.filter(function(item){
                  return item.id==file.id;
              });
              if(!filterFileArr || filterFileArr.length==0) {
                  return;
              }
              var filterFile = filterFileArr[0];
			  var regulateView = function(){
			  	if(fileObj.uploadOver){
                    filterFile.status='上传成功';
                    fileObj.uploadFileList.updateRow(row,{status:'上传成功',complete:100.00});
			  	}
			  }

			  try{
				 xhr=new XMLHttpRequest();//尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
			  }catch(e){	  
				xhr=ActiveXobject("Msxml12.XMLHTTP");//使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
			  }
			  if(option.breakPoints){
			  	var fileName = file.name;
			  	var fileId = file.id;
			  	var fileIndex = file.index;
			  	var fileSize = file.size;//先保存原来的文件名称
			  	var uploadedSize = parseInt(this.funGetUploadedSize(originalFile));
			  	//如果已完全上传文件，退出函数	
			  	if(uploadedSize>=fileSize){
			  		return;
			  	}
			  	var sliceIndex=0;
			  	//对文件进行切割，并保留原来的信息			  	
			  	file = originalFile.slice(uploadedSize,uploadedSize + option.fileSplitSize);
			  	file.name = fileName;
			  	file.id = fileId;
			  	file.index = fileIndex;
			  	file.size = fileSize;
			  }
			  			  
			  if (xhr.upload) {
                  // 更新上传数组和列表中的信息
                  filterFile.status='上传中';
                  fileObj.uploadFileList.updateRow(row,{status:'上传中'});

				  // 上传中
				  xhr.upload.addEventListener("progress", function(e) {
					  fileObj.onProgress(file, e.loaded, originalFile.size);
				  }, false);
	  
				  // 文件上传成功或是失败
				  xhr.onreadystatechange = function(e) {
					  if (xhr.readyState == 4) {
					  	fileObj.uploadOver = true;
						  if (xhr.status == 200) {
								var returnData = xhr.responseText ? JSON.parse(xhr.responseText) : {} ;
								if(!returnData.success) {
								    alert(returnData.message);
                                }
								//将文件块数据更新到本地记录
								if(option.breakPoints){
									//更新已上传文件大小，保存到本地
									uploadedSize += option.fileSplitSize;
                                    sliceIndex++;
									//继续上传其他片段
									if(uploadedSize<fileSize){
										fileObj.uploadOver = false;
										if(fileObj.uploadAllowed){
											file = originalFile.slice(uploadedSize,uploadedSize + option.fileSplitSize);
									  	    file.name = fileName;
									  	    file.id = fileId;
									  	    file.index = fileIndex;
									  	    file.size = fileSize;

                                            otherData.finish=(uploadedSize + option.fileSplitSize)>=fileSize?"yes":"no";
                                            otherData.sliceIndex=sliceIndex;
											sendBlob(fileObj.url,xhr,file,option.formData,otherData);
										}
									}
									else{
										regulateView();
									}
								}
								else{
									regulateView();
								}
									
								if(fileObj.uploadOver){
									option.onUploadSuccess&&option.onUploadSuccess(originalFile, xhr.responseText);
								}
							  
						  } else {
							  fileObj.uploadOver&&option.onUploadError&&option.onUploadError(originalFile, xhr.responseText);		
						  }

						  if (fileObj.uploadOver) {
						  	option.onUploadComplete&&option.onUploadComplete(originalFile,xhr.responseText);
							  //清除文件选择框中的已有值
							  fileObj.fileInput.val('');	
						  };
						  
					  }
				  };
				  // 开始上传
                  var otherData={};
                  otherData.fileName = originalFile.name;
                  otherData.lastModifiedDate = originalFile.lastModifiedDate.getTime();
                  otherData.uuid=originalFile.uuid;
                  otherData.finish=(uploadedSize + option.fileSplitSize)>=fileSize?"yes":"no";
                  otherData.sliceIndex=sliceIndex;
                  otherData.fileSize = bytesToSize(originalFile.size);
				  fileObj.uploadAllowed = true;//重置允许上传为true
                  if(option.sendFileAttr) {
                      for(var i=0;i<option.sendFileAttr.length;i++) {
                          var key=option.sendFileAttr[i];
                          var value=row[key];
                          if(!value) {
                              value="";
                          }
                          otherData[key]=value;
                      }
                  }
				  sendBlob(this.url,xhr,file,option.formData,otherData);
			  }	
		  },
		  
		  init: function() {	  
			  //文件选择控件选择
			  if (this.fileInput.length>0) {
				  this.fileInput.change(function(e) { 
				  	fileObj.funGetFiles(e); 
				  });	
			  }
			  
			  //点击浏览按钮时触发file的click事件
			  _this.find('.file-select').on('click',function(){
				  _this.find('.file-input').trigger('click');
				});
              _this.find('.file-upload').on('click',function(e){
                  for(var i=0,len=fileObj.fileFilter.length;i<len;i++){
                      if(fileObj.fileFilter[i].status=='准备上传') {
                          fileObj.funUploadFile(fileObj.fileFilter[i]);
                      }
                  }
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

	var returnObj =  {
		settings : function(name,value){
			if(arguments.length==1){
				return option[name];
			}
			else{
				if(name=='formData'){
					option.formData = $.extend(option.formData, value);
				}
				else{
					option[name] = value;
				}
			}
		},
		Huploadify : function(){
			var method = arguments[0];
			if(method in this){
				Array.prototype.splice.call(arguments, 0, 1);
				this[method].apply(this[method], arguments);
			}
		}
	};
	
	return returnObj;
}	

})(jQuery)