/**
 * tab控件。
 * @param divContainerId
 * @param total
 * @param conf
 * @returns {PageTab}
 */
PageTab = function(divContainerId,total,conf){
	{
		var addCallBack=function(){
			alert("addCallBack");
		};
		var beforeActive=function(prePage){
			alert("beforeActive:" +prePage);
		};
		var activeCallBack=function(prePage,currentPage){
			alert("activeCallBack:" +prePage +"," + currentPage);
		};
		var beforeDell=function(curPage){
			alert("beforeDell:curPage:" + curPage);
		};
		var delCallBack=function(currentPage){
			alert("delCallBack:" + currentPage);
		};		
		var txtCallBack=function(){
			alert("txtCallBack");
		};
		this.conf= $.extend({},{addCallBack:addCallBack,beforeActive:beforeActive,activeCallBack:activeCallBack,beforeDell:beforeDell,delCallBack:delCallBack,txtCallBack:txtCallBack}, conf);
		//当前tab。
		this.currentTab=null;
		
		/*var imgPlus=__rootPath +"/scripts/form/tab/img/icon_plus.gif";
		span
		var imgDelete=__rootPath +"/scripts/form/tab/img/icon_minus.gif";*/
		
		var str='<div class="pagetab" ><ul id="pageList"></ul><span class="add" >'+
				'<span id="addPage"  class="iconfont icon-zengjia1" title="增加" ></span><span id="delPage" class="iconfont icon-reduce" title="删除" />'+
				'<span id="leftPage" class="iconfont icon-houtui" title="左移"></span><span id="rightPage" class="iconfont icon-qianjin" title="右移"></span>'+
				'</span></div>';
		
		this.totalPage=(total==undefined) ?1:total;
		
		//前一页
		this.prePage=1;
		
		$("#" + divContainerId).append(str);
		
		var _self=this;
		
		//在tab上双击进行编辑。
		$("#pageList").delegate("li>b", "dblclick", function() {
				var obj=$(this);
				var hasInput=obj.has("input").length==1;
				//是否有输入框。
				if(hasInput) return;
				var txtObj=$("<input type='text' maxlength='20' value='"+obj.text()+"' style='border-style:none;height:16px;' size='5' />");
				txtObj.blur(function(){
					var tmpObj=$(this);
					var val=tmpObj.val();
					tmpObj.parent().text(val);
					tmpObj.remove();
					var curPage=_self.getCurrentIndex();
					_self.conf.txtCallBack(curPage);
				});
				//回车处理
				txtObj.keydown(function(event) {
					 if (event.keyCode == '13') {
						 txtObj.parent().text(txtObj.val());
						 txtObj.remove();
						 var curPage=_self.getCurrentIndex();
						 _self.conf.txtCallBack(curPage);
					 }
				});
				obj.empty();
				obj.append(txtObj);
				txtObj.focus();
			});
	}
	/**
	 * 控件初始化。
	 */
	this.init=function(aryTitle,aryData){
		this.initPages(aryTitle);
		var _self=this;
		$("#addPage").click(function(){
			_self.addPage();
		});
		$("#delPage").click(function(){
			_self.deletePage();
		});
		$("#leftPage").click(function(){
			_self.leftPage();
		});
		$("#rightPage").click(function(){
			_self.rightPage();
		});
	},
	/**
	 * 初始化tab选项卡。
	 */
	this.initPages=function(aryTitle){
		var pageList = $("#pageList");
		pageList.empty();
		this.totalPage=aryTitle.length;
		//添加tab
		for(var i = 1;i <= this.totalPage;i++){
			var title=aryTitle[i-1];
			if(title=="")
				title="页 " + i ;
			var li=$("<li><b>"+title+"</b></li>");
			//设置点击事件。
			this.attachTabEvent(li);
			pageList.append(li);
		}
		this.currentTab=pageList.children().first();
		
		this.setActivePage();
	},
	/**
	 * 设置点击选项卡点击事件。
	 */
	this.attachTabEvent =function (obj){
		var self=this;
		obj.click(function(){
			var tabObj=$(this);
			var prePage=self.getCurrentIndex();
			var result=self.conf.beforeActive(prePage);
			if(!result)return;
			self.currentTab=tabObj;
			//前一页的号码
			var curPage=self.getCurrentIndex();			
			self.setActivePage();
			self.conf.activeCallBack(prePage,curPage);
		});	
	},
	/**
	 * 添加tab
	 */
	this.addPage =function(){
		this.totalPage ++;
		var li=$("<li><b>新页面</b></li>");
		this.attachTabEvent(li);
		this.currentTab.after(li);
		this.currentTab=li;
	  	this.setActivePage();
	  	//添加回调函数
	  	this.conf.addCallBack();
	},
	/**
	 * 左移tab
	 */
	this.leftPage =function(){
		var idx = this.getCurrentIndex()-1;
		//左边第一个
		if(idx==0)return;
		var li = $("#pageList li").eq(idx);
		var html = $(li.prop("outerHTML"));
		$("#pageList li").eq(idx-1).before(html);
		this.attachTabEvent(html);
		this.currentTab=html;
	  	this.setActivePage();
		li.remove();
		//获取要迁移的tab表单内容
		var form = formContainer.getForm(idx-1);
		//设置迁移后的表单内容
		formContainer.setForm(form.title,form.form, idx);
		//设置被迁移的表单内容
		formContainer.setFormTitle(html.text(), idx-1);
	},
	/**
	 * 右移tab
	 */
	this.rightPage =function(){
		var idx = this.getCurrentIndex()-1;
		var len = $("#pageList").children().length-1;
		//右边最后一个
		if(idx==len)return;
		var li = $("#pageList li").eq(idx);
		var html = $(li.prop("outerHTML"));
		$("#pageList li").eq(idx+1).after(html);
		this.attachTabEvent(html);
		this.currentTab=html;
	  	this.setActivePage();
		li.remove();
		//获取要迁移的tab表单内容
		var form = formContainer.getForm(idx+1);
		//设置迁移后的表单内容
		formContainer.setForm(form.title,form.form, idx);
		//设置被迁移的表单内容
		formContainer.setFormTitle(html.text(), idx+1);
	},
	/**
	 * 设置激活页
	 */
	this.setActivePage=function (){
		var curTab =this.currentTab;
		if(curTab.siblings()){
			curTab.siblings().removeClass("current");
		}
		curTab.addClass("current");
	},
	/**
	 * 取得当前tab的索引。
	 * @returns
	 */
	this.getCurrentIndex=function(){
		return  $("#pageList").children().index(this.currentTab) +1;
	},
	/**
	 * 删除tab选项。
	 */
	this.deletePage=function(){
		if (this.totalPage==1) return;
		var curPage=this.getCurrentIndex();
		var next=this.currentTab.next();
		var prev=this.currentTab.prev();
		var result=this.conf.beforeDell(curPage);
		if(!result)return;
		this.currentTab.remove();
		if(next.length>0){
			this.currentTab=next;
		}
		else{
			this.currentTab=prev;
		}
		this.totalPage--;
		this.setActivePage();
		//回调删除事件处理
		this.conf.delCallBack(curPage);
	};
};