<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>预览图片</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <%@include file="/commons/list.jsp"%>
    <script src="imgShow.js"></script>
    <style>
        body,ul,li{
            list-style: none;
            padding: 0;
            margin: 0;
        }
        body{
            height: 100%;
        }
        .div,
        .selectImgBox{
            width: 100%;
            height: 100%;
            padding: 20px;
            box-sizing: border-box;
            border: 1px solid #ddd;
        }
        .imgContent {
            width: 100%;
            height: 100%;
            position: relative;
            border: 1px solid #ddd;
            background: #fff;
        }
        .imgContent .imgMaxBox{
            position: absolute;
            top: 0;
            left: 0;
            right: 150px;
            bottom: 0;
            border-right: 1px solid #ddd;
            text-align: center;

            padding: 10px;
            box-sizing: border-box;
        }
        .imgContent .imgMaxBox:after{
            content: "";
            display: inline-block;
            height: 100%;
            vertical-align: middle;
        }

        .imgContent .imgMaxBox  img{
            max-width: 100%;
           /* height: 100%;*/
            max-height: 100%;
            vertical-align: middle;
            margin-bottom: 2px;
        }
        .imgContent .imgMinBox{
            position: absolute;
            top: 0;
            right: 0;
            width: 150px;
            bottom: 0;
            background: #fff;
            overflow: hidden;
            cursor: pointer;

        }
        .imgContent .imgMinBox #img_div{
            position: absolute;
            top: 0;

            font-size: 0;
            transition: all 0.5s;
        }
        .imgContent .imgMinBox #img_div img{
            border: 1px solid transparent;
            display: block;
            box-sizing: border-box;
        }
        .imgContent .imgMinBox #img_div img.active{
            border-color: #4b5cc4;
        }
        .imgContent .imgMinBox #img_div img{
            width: 100%;
        }

        .imgContent .btnUp,
        .imgContent .btnDown{
            position: absolute;
            background: rgba(0,0,0,0.3);
            text-align: center;
            line-height: 40px;
            color: #fff;
            font-size: 14px;
            /* cursor: pointer; */
            display: none;
        }
        .imgContent .btnUp{
            top: 0;
            left: 0;
            right: 0;

        }
        .imgContent .btnDown{
            bottom: 0;
            left: 0;
            right: 0;
        }
        .imgContent .imgMinBox:hover .btnUp,
        .imgContent .imgMinBox:hover .btnDown{
            display: block;
        }



    </style>
</head>
<body>
    <div class="selectImgBox" id="selectImgBox">
    </div>

</body>
<script type="text/javascript">
    var selectImgBox =$('#selectImgBox');
   function showImg(){
        //第一个;
        $(".selectImgBox").selectimg({imgID:0});
    }

    function  setdata(data) {
        if("file"==data.type){
            var showFileId =data.fileId;
            var fileIds =data.fileIds;
            for(var i=0;i<fileIds.length;i++){
                var imgObj =$('<img src="${ctxPath}/sys/core/file/previewImage.do?fileId='+fileIds[i]+'" alt="">');
                selectImgBox.append(imgObj);
            }
            showImg();
            return;
        }else {
            var imgUrls =data.imgUrls;
            for(var i=0;i<imgUrls.length;i++){
                var imgUrl =imgUrls[i];
                if(imgUrl.startWith("http")){
                    var imgObj =$('<img src="'+imgUrl+'" alt="">');
                    selectImgBox.append(imgObj);
                }else{
                    var imgObj =$('<img src="${ctxPath}'+imgUrl+'" alt="">');
                    selectImgBox.append(imgObj);
                }
            }
            showImg();
            return;
        }
    }
</script>
</html>
