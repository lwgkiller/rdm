<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html >
<html>
<head>
<title>日期计算</title>
<%@include file="/commons/mini.jsp"%>

<style type="text/css">
fieldset {
	height: 100%;
	border: 1px solid silver;
}

fieldset legend {
	font-weight: bold;
}

.droppable {
	width: 150px;
	cursor: pointer;
	height: 30px;
	padding: 0.5em;
	margin: 5px;
	border: 1px dashed black;
}

.droppable.highlight {
	background: orange;
}
div.operatorContainer{
	padding: 5px;
	text-align: left;
	overflow: auto;
	border: 1px solid #ddd;
}
textarea{
	border-color: #ddd;
	resize: none;
	outline: none;
}
div.operatorContainer > div {
    border-radius: 5px;
    border:1px solid #409EFF;
    display:inline-block;
    background: #fff;
	color: #409EFF;
    padding:6px;
    text-align:center;
    margin:4px;
    min-width: 32px;
    font-size:14px;
	cursor: pointer;
}

div.operatorContainer > div:hover{
  	background: #409EFF;
	color: #fff;
}
	.title-p{
		padding: 6px;
		font-size: 14px;
	}
</style>
</head>
<body>
	<script type="text/javascript">
		var isMain = true;
		var curEditEl = editor.editdom;
		window.onload = function() {
			//加载树
			loadTree(editor, curEditEl);
			//初始化公式
			init();
		}

		function init() {
			var curEl = $(curEditEl);
			var options = curEl.attr("data-options") || "{}";
			var json = mini.decode(options);
			var setting = json.datecalc;
			var obj = $("#condition");
			obj.val(setting);
		}

		function loadTree(editor, el) {
			var data = getMetaData(editor, el);
			var treeObj = mini.get("fieldTree");
			treeObj.loadList(data.aryData);
		}

		function getMetaData(editor, el) {
			var container = $(editor.getContent());
			var elObj = $(el);
			var grid = elObj.closest(".rx-grid");
			isMain = grid.length == 0;
			var aryJson = [];
			var root = {
				id : "root_",
				text : "根节点",
				table_ : false,
				main : false,
				expanded : true
			};

			aryJson.push(root);
			if (isMain) {
				var els = $(
						"[plugins]:not(div.rx-grid [plugins])",
						container);
				els.each(function() {
					var obj = $(this);
					var label = obj.attr("label");
					var name = obj.attr("name");
					var fieldObj = {
						id : name,
						text : label,
						table_ : false,
						main : true,
						pid : "root_"
					};
					aryJson.push(fieldObj);
				});
			} else {
				$("[plugins]", grid).each(function() {
					var obj = $(this);
					var label = obj.attr("label");
					var name = obj.attr("name");
					var fieldObj = {
						id : name,
						text : label,
						table_ : false,
						main : false,
						pid : "root_"
					};
					aryJson.push(fieldObj);
				});
			}
			var obj = {
				aryData : aryJson,
				el : el
			};
			return obj;

		}

		function fieldClick(e) {
			var node = e.node;
			if (node.id == "root_")
				return;

			var obj = $("#condition");
			obj.val(obj.val()+"{"+node.id+"}");
		}

		dialog.onok = function() {
			var curEl = $(curEditEl);
			var options = curEl.attr("data-options") || "{}";
			var json = mini.decode(options);
			json.datecalc = $("#condition").val();
			curEl.attr("data-options", mini.encode(json));
		}

		dialog.oncancel = function(e) {
			/* var curEl = $(curEditEl);//清空脚本
			curEl.removeAttr("data-options"); */
		}
	</script>
	<div class="mini-layout" style="width: 100%;height: 100%;">
		<div region="west" title="表单字段">
			<div class="mini-fit">
				<ul id="fieldTree" class="mini-tree" onnodeclick="fieldClick"
					style="width: 100%; padding: 5px;"
					showTreeIcon="true" resultAsTree="false" allowDrag="true">
				</ul>
			</div>
		</div>
		<div region="center">
			<div>
				<p class="title-p">日期计算设置</p>
				<div class="operatorContainer">
					<div operator="+" title="加">+</div>
					<div operator="-" title="减">-</div>
					<div operator="*" title="乘">*</div>
					<div operator="/" title="除">/</div>
					<div operator="()" title="括号">( )</div>
					<div operator=".subtract(,)">两个日期相减</div>
					<div operator=".addDate(,)">两个日期相加</div>
					<div operator=".sub(,)">减日期时间</div>
					<div operator=".add(,)">加日期时间</div>
				</div>
			</div>
			<p class="title-p">脚本运算</p>
			<div class="mini-fit" style="box-sizing: border-box;">
				<textarea id="condition" style="width:100%;height:98%;box-sizing: border-box;"></textarea>
			</div>
			<div style="display: none" id="formulaHelp"></div>
		</div>
	</div>

	<script type="text/javascript">
		mini.parse();
		$(function(){
			handOperator("condition");
		})
		function handOperator(targetId){
			$("div.operatorContainer div").click(function(e){
				var obj=$(this);
				insert(obj.attr("operator"),targetId)
			})
		}
		function insert(content,targetId){
			var formulaObj=document.getElementById(targetId);
			$.insertText(formulaObj,content);
		}
	</script>
</body>
</html>