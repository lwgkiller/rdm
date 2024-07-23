<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	 <title>上传单个图片示例</title>
<%@include file="/commons/list.jsp"%>
</head>
<body>
    <h1>Form 表单</h1>      
       

    <form id="form1" >
        <input name="id" class="mini-hidden" />
        <table>
            
            <tr>
                <td>
                    <label for="textbox1$text">TextBox：</label>
                </td>
                <td>
                    <input id="textbox1"  name="username" class="mini-textbox" required="true" />
                </td>
            </tr>
            <tr>
                <td>
                    <label for="pwd1$text">Password：</label>
                </td>
                <td>
                    <input id="pwd1" name="Pwd" class="mini-password" required="true"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="textarea1$text">TextArea：</label>
                </td>
                <td>
                    <input id="textarea1" name="Area" class="mini-textarea" required="true"/>
                </td>
            </tr>
            <tr>
                <td>                   
                    <label for="date1$text">DatePicker：</label>
                </td>
                <td>
                    <input id="date1" name="BirthDay" class="mini-datepicker" value="2010-10-12" required="true" />
                </td>
            </tr>
            <tr>
                <td>
                    Spinner：
                </td>
                <td>
                    <input name="Age" class="mini-spinner" value="22" minValue="10" maxValue="50" />
                </td>
            </tr>
             <tr>
                <td>
                    TimeSpinner：
                </td>
                <td>
                    <input name="Time" class="mini-timespinner" format="H:mm" />
                </td>
            </tr>
            <tr>
                <td>
                    CheckBox：
                </td>
                <td>
                    <input name="Married" class="mini-checkbox" text="婚否" value="Y" trueValue="Y" falseValue="N" />
                </td>
            </tr>
            <tr>
                <td>
                    ComboBox：
                </td>
                <td>
                    <input name="Country" showNullItem="true" class="mini-combobox" url="${ctxPath}/demo/countrys.txt" value="cn" textField="text" valueField="id" />
                    <br />
                    <span id="combobox_error" style="color:Red;"></span>
                </td>
            </tr>
            <tr>
                <td>
                    TreeSelect：
                </td>
                <td>                    
                    <input name="TreeSelect"  class="mini-treeselect" url="${ctxPath}/demo/listTree.txt" multiSelect="true" 
                            textField="text" valueField="id" parentField="pid" checkRecursive="true"
                            value="ajax,button"
                        />
                </td>
            </tr>
            <tr>
                <td>
                    CheckBoxList：
                </td>
                <td>
                    <div name="countrys" class="mini-checkboxlist" repeatItems="3" repeatLayout="flow" url="${ctxPath}/demo/countrys.txt" value="cn,de,usa" textField="text" valueField="id" ></div>
                </td>
            </tr>
            <tr>
                <td>
                    RadioButtonList：
                </td>
                <td>
                    <div name="countrys2" class="mini-radiobuttonlist" value="en" repeatItems="4" repeatDirection="vertical" repeatLayout="table" url="${ctxPath}/demo/countrys.txt" textField="text" valueField="id" ></div>
                </td>
            </tr>
            <tr>
                <td>
                    ListBox：
                </td>
                <td>
                    <div name="countrys3" showNullItem="true" class="mini-listbox" url="${ctxPath}/demo/countrys.txt" value="fr" textField="text" valueField="id" style="height:120px;"></div>
                </td>
            </tr>
            <tr>
                <td>
                    
                </td>
                <td>
                	<input value="SubmitFrom" type="button" onclick="juqeryGetForm()" />
                    <!-- input value="SubmitFrom" type="button" onclick="submitForm()" />
                    <input value="LoadForm" type="button" onclick="loadForm()" />

                    <input value="ResetForm" type="button" onclick="resetForm()"/>
                    <input value="ClearForm" type="button" onclick="clearForm()"/>
                    <input value="SetForm" type="button" onclick="setForm()" />
                    <input value="GetForm" type="button" onclick="getForm()" />
                    --> 
                </td>
            </tr>
        </table>
    </form>
    <script type="text/javascript">
        mini.parse();
        
        //获得表单的值
        function juqeryGetForm(){
        	var data=$("#form1").serializeArray();
        	alert(mini.encode(data));
        }
        
        function getForm() {
            var form = new mini.Form("#form1");
            var data = form.getData(true, false);
            var s = mini.encode(data);
            alert(s);
            //form.setIsValid(false);
        }
        function setForm() {
            var obj = {
                String: "abc",
                Date: "2020-11-12",
                Boolean: 'Y',
                TreeSelect: "ajax",
                countrys: "cn",
                //countrys2: "de",
                countrys3: "usa"
            };
            var form = new mini.Form("#form1");
            form.setData(obj, false);
        }

        function resetForm() {
            var form = new mini.Form("#form1");
            form.reset();
        }
        function clearForm() {
            var form = new mini.Form("#form1");
            form.clear();
        }

        function submitForm() {
            

            //提交表单数据
            var form = new mini.Form("#form1");            
            var data = form.getData();      //获取表单多个控件的数据
            var json = mini.encode(data);   //序列化成JSON
            $.ajax({
                url: "${ctxPath}/demo/FormService.jsp?method=SaveData",
                type: "post",
                data: { submitData: json },
                success: function (text) {
                    alert("提交成功，返回结果:" + text);
                }
            });

        }
        function loadForm() {
            //加载表单数据
            var form = new mini.Form("#form1");            
            $.ajax({
                url: "${ctxPath}/demo/FormService.jsp?method=LoadData",
                type: "post",
                success: function (text) {
                    var data = mini.decode(text);   //反序列化成对象
                    form.setData(data);             //设置多个控件数据
                }
            });
        }
    </script>
    
    

    <div class="description">
        <h3>Description</h3>
        <p>MiniUI Form能批量对多个控件进行赋值、取值、重置、验证、获取错误信息等。
        </p>
        <p>使用ajax进行数据的加载和提交操作。</p>
        <p>当调用"setData"/"getData"方法时，将根据表单控件的name属性进行值的设置和获取操作。</p>
    </div>
</body>
</html>