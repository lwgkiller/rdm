;(function($){
    $.fn.selectimg = function(options){
        var defaults = {
            //各种参数，各种属性
			imgID:1,
			readOnly:false
        }

		console.log(this);
        var options = $.extend(defaults,options);
        this.each(function(){
            //各种功能  //可以理解成功能代码
            var _this = $(this);
			var htmls = _this.html();
			var selectImgBox ="";
			if(options.readOnly){//只读，是这样吗嗯   你调用的时候传个参数，好，我试试
				selectImgBox =$('<div class="imgContent"><div class="imgMaxBox">'
					+'<img id="maxImg" src="" alt="">'
					+'</div>'
					+'<div class="imgMinBox">'
					+'<div id="img_div">'

					+'</div>'
					+'<div class="btnUp">上</div>'
					+'<div class="btnDown">下</div>'
					+'</div><div>')
			}else {//编辑
				selectImgBox =$('<div class="imgContent"><div class="imgMaxBox">'
					+'<span class="closeBtn">X</span>'
					+'<img id="maxImg" src="" alt="">'
					+'</div>'
					+'<div class="imgMinBox">'
					+'<div id="img_div">'

					+'</div>'
					+'<div class="btnUp">上</div>'
					+'<div class="btnDown">下</div>'
					+'</div><div>')
			}
			_this.empty();
			_this.html(selectImgBox);
			var img_div = _this.find("#img_div");
			img_div.html(htmls);

			var img_src = img_div.find("img").eq(options.imgID - 1 ).attr("src");
			var img_id = img_div.find("img").eq(options.imgID - 1 ).attr("id");
			_this.find("#maxImg").attr("src",img_src);
			_this.find(".closeBtn").attr("id",img_id);
			_this.on("click","#img_div img",function(){
				var _this_src = $(this).attr("src");
				var _this_id = $(this).attr("id");
				_this.find("#maxImg").attr("src",_this_src);
				_this.find(".closeBtn").attr("id",_this_id);
			})
			
			var btnUp =  _this.find(".btnUp");
			var btnDown =  _this.find(".btnDown");
			
			var imgMinBox_height = parseInt( $(".imgMinBox").height() );
			var img_ui_height =parseInt( img_div.height());	
			 /* 上移动*/
			 btnUp.click(function(){
				 imgMinBox_height = parseInt( $(".imgMinBox").height() );
				 img_ui_height =parseInt( img_div.height());
				if(img_ui_height>imgMinBox_height){
					var _top = img_div.css("top");
					var  tops = parseInt(_top);
					if(img_ui_height + tops >= imgMinBox_height){
						img_div.css({"top":tops - 100 });
						var t =	setTimeout(function(){	
							var _top2 =img_div.css("top");
							var s = img_ui_height + parseInt(_top2) ;
							var h = imgMinBox_height;	
							if(s < h ){
								img_div.stop().animate({"top":- (img_ui_height - imgMinBox_height) },500);
							}
						},300)
					}
				}
			 })
			 /* 下移动*/
			 btnDown.click(function(){
				 imgMinBox_height = parseInt( $(".imgMinBox").height() );
				 img_ui_height =parseInt( img_div.height());
				  if(img_ui_height>imgMinBox_height){
					var _top3 =img_div.css("top");
					var tops = parseInt(_top3) ;
					console.log(tops);
					if(tops <= 0){
						img_div.stop().css("top",tops + 100 );
						var t2 = setTimeout(function(){	
							var _top4 = parseInt( img_div.css("top") );
							if(_top4 > 0 ){
								img_div.stop().animate({"top":0},500);
							}
						},300)
					}
				}	
			 })
        });
        return this;
    }
})(jQuery);