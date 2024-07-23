
<%-- 
    Document   : [流程沟通留言板]编辑页
    Created on : 2017-10-27 16:51:41
    Author     : mansan
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>[流程沟通留言板]编辑</title>
<%@include file="/commons/edit.jsp"%>



<style type="text/css">
.messageBox{
	height: 100%;
}

.input-container,
.chat-container{
	padding:0;
	margin:0;
}

.form-outer{
	height: calc(100% - 130px);
}

.messageInput{
	width: calc(100% - 74px);	
}

form{
	height: 100%
}

.chat-container{
	height: 100%;
	overflow:auto;
}
.chat-container ul{
	padding: 0 10px;
}
.input-container{
	height: 10%
}


body{
	overflow: hidden;
	background-color: white;
}

.no_message{
	text-align: center;
	padding-top:5px;
	padding-bottom: 5px;
}

.no_message h1{
	color:red;
	font-size:18px;
	padding:5px;
}

.othersMessage span:nth-of-type(3){
	display:block;
	padding: 5px;
	margin:5px;
	background-color: #fffbf0;
	border-radius: 5px;
	max-width: 60%
}

.myMessage span:nth-of-type(3){
	display:block;
	padding: 5px;
	margin:5px;
	background-color: #c0ebd7;
	border-radius: 5px;
	text-align: left !important;
	

}

.myMessage{
	text-align: right;
}

.messageContent{
	float: right;
}

.clearfix{
	clear: both;
}
.form-inner{
	padding:10px ;
}
</style>

<!--[if lte IE 8]>
	<style>
		.form-outer{
			height: 90%;
		}
		.messageInput {
		    width: 86%;
		}
	</style>
<![endif]-->

</head>
<body>
	<div class="messageBox">
		<form id="form1" method="post" style="height: 100%;">
			<div class="form-inner">
				<input id="pkId" name="pkId" class="mini-hidden"
					value="${bpmMessageBoard.id}" />
				<input id="instId" name="instId" class="mini-hidden"
					value="${bpmInst.instId}" />
				<table class="table-detail table-align" cellspacing="1" cellpadding="0">
					<tr>
						<td>流程名称:</td>
						<td><a
							href="javascript:myDetailRow('${bpmInst.instId}')">
								${bpmInst.subject}</a>
						</td>
					</tr>
				</table>
			</div>

			<div class="mini-fit" style="padding: 10px 0;box-sizing: border-box">
				<div class="chat-container" id="chat-container">
				<c:choose>
					<c:when test="${bpmMessageBoards.size()==0}">
						<div class="no_message">
							<h1>暂无留言</h1>
						</div>

					</c:when>

					<c:otherwise>
						<ul>
							<c:forEach items="${bpmMessageBoards}" var="instMessage">
								<c:choose>
									<c:when test="${!(instMessage.messageAuthorId eq currentUserId)}">
										<li class="othersMessage" >
											<span>  ${instMessage.messageAuthor}</span>
											<span><fmt:formatDate value="${instMessage.updateTime}" type="both"/> </span></br>
											<span > ${instMessage.messageContent} </span>
											<div class="clearfix"></div>
										</li>
									</c:when>
									<c:otherwise>
										<li class="myMessage">
											<span><fmt:formatDate value="${instMessage.updateTime}" type="both"/> </span>
											<span>自己</span></br>
											<span class="messageContent"> ${instMessage.messageContent} </span>
											<div class="clearfix"></div>
										</li>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</ul>
					</c:otherwise>
				</c:choose>
			</div>
			</div>
			<div style="padding: 0 10px" >
				<textarea id="messageContent" name="messageContent"
						  class="mini-textarea messageInput"
						  emptyText="请输入留言内容"
						  required="true" style="width: 100%;height: 100px;"
				>
				</textarea>
			</div>
			<div style="text-align: right;padding: 10px;"><a class="mini-button" onclick="leaveMessage">留言</a></div>
		</form>
	</div>
	<script>
		addBody();
		$(function(){
			 $(".upload-file").on('click',function(){
				 var img=this;
				_UserImageDlg(true,
					function(imgs){
						if(imgs.length==0) return;
						$(img).attr('src','${ctxPath}/sys/core/file/imageView.do?thumb=true&fileId='+imgs[0].fileId);
						$(img).siblings('input[type="hidden"]').val(imgs[0].fileId);
						
					}
				);
			 });
			 
			 //将滚动条设置到底部
			/* $(".chat-container")[0].scrollTop = $(".chat-container")[0].scrollHeight;*/
			$("#chat-container").scrollTop($("#chat-container").height());
			 
		});	
	
		mini.parse();	
		function myDetailRow(instId) {			
			_OpenWindow({
				title : '流程明细',
				height : 600,
				width : 800,
				openType : 'NewWin',
				url : __rootPath + '/bpm/core/bpmInst/inform.do?instId=' + instId,
				ondestroy : function(action) {
				}
			});
		}
		
		//留言
		function leaveMessage(){
			//var form = mini.getForm("form1");
			var url = __rootPath + '/bpm/core/bpmMessageBoard/leaveMessage.do';
			_SaveData("form1",url,refresh);
			/*CloseWindow();*/
		}
		
		function refresh(){
			window.location.reload();
		}
		
		
		
		
		
		
	</script>

</body>


</html>