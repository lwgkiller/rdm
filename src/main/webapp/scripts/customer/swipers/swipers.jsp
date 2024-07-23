<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Swiper demo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1">
    <!-- Link Swiper's CSS -->
    <%@include file="/commons/list.jsp"%>
    <link rel="stylesheet" href="swiper.min.css">

    <!-- Demo styles -->
    <style>
        html, body {
            position: relative;
            height: 100%;
        }
        body {
            background: #fff;
            font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
            font-size: 14px;
            color:#000;
            margin: 0;
            padding: 0;

        }

        .swiper-container {
            width: 100%;
            /* height: 300px; */
            margin-left: auto;
            margin-right: auto;
        }
        .swiper-slide {
            background-size: cover;
            background-position: center;
            text-align: center;
        }
        .gallery-top {
            height: 80%;
            width: 100%;
        }
        .gallery-top .swiper-slide{
            background-color: #fff;
        }
        .gallery-top .swiper-slide  img{
            vertical-align: middle;
            max-height: 100%;
            max-width: 100%;
        }
        .gallery-top  .swiper-slide:before{
            content: "";
            display: inline-block;
            vertical-align: middle;
            height: 100%;
        }


        .gallery-thumbs {
            height: 20%;
            box-sizing: border-box;
            padding: 10px 0;

        }

        .gallery-thumbs .swiper-slide {
            width: 25%;
            height: 100%;
            opacity: 0.4;
            cursor: pointer;
            text-align: center;
            border: 1px solid #ddd;
            background-size: cover;
            background-position: center;
        }
        .gallery-thumbs .swiper-slide:before{
            content: "";
            display: inline-block;
            height: 100%;
            vertical-align: middle;
        }

        .gallery-thumbs .swiper-slide img{
            max-width: 100%;
            max-height: 100%;
            vertical-align: middle;
        }
        .gallery-thumbs .swiper-slide-thumb-active {
            opacity: 1;
        }  /**/
        .swiperBox{
          /*  max-width: 680px;
            max-height: 510px;*/
            top: 20px;
            left: 20px;
            bottom: 20px;
            right: 20px;

            position: absolute;
        }

        .swiper-button-prev.swiper-button-white,
        .swiper-container-rtl .swiper-button-next.swiper-button-white,
        .swiper-button-next.swiper-button-white,
        .swiper-container-rtl .swiper-button-prev.swiper-button-white{
            background-color:rgba(0,0,0,0.3);
            padding:  5px;
            background-size: 20px auto;
            display: none;
        }
        .gallery-thumbs .swiper-slide-thumb-active {
            opacity: 1;
        }  /**/


        .close{
            position: absolute;
            top: 0;
            bottom:20%;
            text-align: center;
            width: 50%;
            left: 0;
            right: 0;
            margin: auto;
            z-index: 99;

        }
        .close:before{
            content: "";
            display: inline-block;
            height: 100%;
            vertical-align: middle;
        }
        .close .btn{
            display: inline-block;
            vertical-align: middle;
            border-radius:60% ;
            height: 60px;
            width: 60px;
            text-align: center;
            line-height: 60px;
            background-color: #000;
            color: #ffffff;
            opacity: 0.3;
            -moz-opacity: 0.3;
            filter: alpha(opacity = 30 );
            display: none;
            cursor: pointer;
        }
        .close .btn:hover{
            background-color:red;
        }
        .close:hover .btn{
            display: inline-block;
        }
    </style>
</head>
<body>
<div class="swiperBox" style="">
    <div class="close" style="" >
        <span class="btn" title="删除图片">
            <i class="iconfont icon-trash" style="font-size: 24px;"></i>
        </span>
    </div>
<!-- Swiper -->
<div class="swiper-container gallery-top">
    <div id="show_1" class="swiper-wrapper">
    </div>
    <!-- Add Arrows -->
    <div class="swiper-button-next swiper-button-white"></div>
    <div class="swiper-button-prev swiper-button-white"></div>

</div>
<div class="swiper-container gallery-thumbs">
    <div id="show_2" class="swiper-wrapper">
    </div>
</div>
</div>
<!-- Swiper JS -->
<script src="swiper.min.js"></script>

<!-- Initialize Swiper -->
<script>

    //是否只读
    var readOnly =${param['readOnly']};
    var show_1 =$('#show_1');
    var show_2 =$('#show_2');
    var initData ={};

    var index_num =0;


    if(!readOnly){
        initDeletFn();
    }
    function initDeletFn() {
        $(".close .btn").click(function () {
            var index_img = $(".gallery-thumbs .swiper-slide-thumb-active").index();
            if (index_img || index_img == 0) {
                if (getInitDataLength() ==1) {
                    mini.confirm("确定删除记录？删除将关闭预览！", "确定？",
                        function (action) {
                            if (action == "ok") {
                                deleteImg(index_img,"close");
                            }
                        }
                    );
                } else {
                    mini.confirm("是否删除当前图片？", "确定？",
                        function (action) {
                            if (action == "ok") {
                                deleteImg(index_img,"continues");
                            }
                        }
                    );
                }
            }
        })
    }

    function deleteImg(indexNum,deleType) {
        debugger;
        var imgType =initData.type;
        if("file"==imgType){
            var fileIds =initData.fileIds;
            deleteDiv(fileIds.length);
            $("#show_1 .swiper-slide").eq(indexNum).remove();
            fileIds.splice(indexNum,1);
        }else {
            var imgUrls =initData.fileIds;
            deleteDiv(imgUrls.length);
            $("#show_1 .swiper-slide").eq(indexNum).remove();
            imgUrls.splice(indexNum,1);
        }
        if("close"==deleType){
            CloseWindow("ok");
        }else {
            var setData=initData;
            index_num =0;
            setdata(setData);
        }
    }

    function getInitDataLength() {
        var imgType =initData.type;
        var dataLength=0;
        if("file"==imgType){
            var fileIds =initData.fileIds;
            if(fileIds){
                dataLength =fileIds.length;
            }
        }else {
            var imgUrls =initData.fileIds;
            if(imgUrls){
                dataLength =imgUrls.length;
            }
        }
        return dataLength;
    }

    function deleteDiv(imgLength) {
        for(var i=0;i<imgLength;i++){
            var showDivObj =$('showImg_'+i);
            showDivObj.remove();
            var listDivObj =$('#listImg_'+i);
            listDivObj.remove();
        }
    }

    function  setdata(data) {
        initData =data;
        var showUrl ="";
        if("file"==data.type){
            var fileIds =data.fileIds;
            for(var i=0;i<fileIds.length;i++){
                showUrl =__rootPath+"/sys/core/file/previewImage.do?fileId="+fileIds[i].fileId;
                show_1.append('<div id="showImg_'+index_num+'" class="swiper-slide" ><img src="'+ showUrl +'"></div>');
                var imgObj =$('<div id="listImg_'+index_num+'" class="swiper-slide" ><img src="'+ showUrl +'"></div>');
                show_2.append(imgObj);
                index_num++;
            }
        }else {
            var imgUrls =data.fileIds;
            for(var i=0;i<imgUrls.length;i++){
                var imgUrl =imgUrls[i].url;
                if(imgUrl.startWith("http")){
                    show_1.append('<div  id="showImg_'+index_num+'" class="swiper-slide" ><img src="'+ imgUrl +'"></div>');
                    var imgObj =$('<div id="listImg_'+index_num+'" class="swiper-slide" ><img src="'+ imgUrl +'"></div>');
                    show_2.append(imgObj);
                }else{
                    showUrl =__rootPath+imgUrl;
                    show_1.append('<div  id="showImg_'+index_num+'"  class="swiper-slide" ><img src="'+ showUrl +'"></div>');
                    var imgObj =$('<div id="listImg_'+index_num+'" class="swiper-slide" ><img src="'+ showUrl +'"></div>');
                    show_2.append(imgObj);
                }
                index_num++;
            }
        }
        initShowImg();
    }

    function initShowImg() {
        var galleryThumbs = new Swiper('.gallery-thumbs', {
            spaceBetween: 10,
            slidesPerView: 5,
            freeMode: true,
            watchSlidesVisibility: true,
            watchSlidesProgress: true,
        });
        var galleryTop = new Swiper('.gallery-top', {
            spaceBetween: 0,

            effect : 'fade',
            navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev',
            },
            thumbs: {
                swiper: galleryThumbs
            }
        });
    }

    function getDatas(){
        return initData;
    }

    $(".gallery-top,.close").hover(function(){
        $(".close .btn").css("display","inline-block");
        $(".swiper-button-white").show();
    },function(){
        $(".close .btn").css("display","none");
        $(".swiper-button-white").hide();
    })
    $(".close .btn").click(function(){
        var index_img = $(".gallery-thumbs .swiper-slide-thumb-active").index();
        console.log(index_img);
    })

</script>
</body>
<script type="text/javascript">

</script>
</html>
