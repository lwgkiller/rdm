<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <title>意见审批控件-mini-nodeopinion</title>
    <%@include file="/commons/mini.jsp"%>
    <style type="text/css">

    </style>
</head>
<body>
<div class="form-outer">
    <form id="miniForm">
        <table class="table-detail column-four table-align" cellspacing="0" cellpadding="1">
            <tr>
                <td>此控件<span style="color: red">仅</span>在<span style="color: red">&nbsp;表单模板&nbsp;</span>中，<span style="color: red">&nbsp;模板二&nbsp;</span>“新增行”的时使用,点击“确定”既可添加；</td>
            </tr>
        </table>
    </form>
</div>
<script type="text/javascript">
    var pre="div_";
    mini.parse();
    var form=new mini.Form('miniForm');
    //编辑的控件的值
    var oNode = null,thePlugins = 'mini-grid-div';

    window.onload = function() {
        //若控件已经存在，则设置回调其值
        if( UE.plugins[thePlugins].editdom ){
            //
            oNode = UE.plugins[thePlugins].editdom;
            //获得字段名称
            var formData={};
            var attrs=oNode.attributes;

            for(var i=0;i<attrs.length;i++){
                var obj=attrs[i];
                var name=obj.name;
                if(name=="name"){
                    formData[name]=obj.value.replace(pre,"");
                }
                else{
                    formData[name]=obj.value;
                }
            }
            form.setData(formData);
        }
        else{
            var data=_GetFormJson("miniForm");
            var array=getFormData(data);
            initPluginSetting(array);
        }
    };
    //取消按钮
    dialog.oncancel = function () {
        if( UE.plugins[thePlugins].editdom ) {
            delete UE.plugins[thePlugins].editdom;
        }
    };

    //确认
    dialog.onok = function (){

        var isCreate=false;
        //控件尚未存在，则创建新的控件，否则进行更新
        if( !oNode ) {
            try {
                oNode= document.createElement("div");
                oNode.setAttribute("class","gridSpan");
                oNode.setAttribute("plugins","mini-grid-div");
                oNode.innerHTML = '<div class="gridHeader" >名称</div><div class="gridBox" ></div>'

            } catch (e) {
                try {
                    editor.execCommand('error');
                } catch ( e ) {
                    mini.alert('控件异常，请联系技术支持');
                }
                return false;
            }
            isCreate=true;
        }


        if(isCreate){
            editor.execCommand('insertHtml',oNode.outerHTML);
        }else{
            delete UE.plugins[thePlugins].editdom;
        }
    };

</script>
</body>
</html>