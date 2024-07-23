<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/3/31
  Time: 16:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>创建新技术</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/xcmgjssjk/xcmgjssjkEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto;width: 80%;">
        <form id="formJssjk" method="post" >
            <input id="jssjkId" name="jssjkId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="splx" name="splx" class="mini-hidden"/>
            <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    技术数据库申请
                </caption>
                <tr id="ifDisplay" style="display: none">
                    <td style="width: 14%">删除原因：<span style="color: #ff0000">*</span></td>
                    <td style="width: 30%">
                        <input id="delReason" name="delReason"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">技术名称：</td>
                    <td style="width: 30%">
                        <input id="jsName" name="jsName"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 14%">技术编号:(审批完成后自动生成)</td>
                    <td style="width: 30%;">
                        <input id="jsNum" name="jsNum"  class="mini-textbox" readonly style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">技术负责人：</td>
                    <td style="width: 30%">
                        <input id="jsfzrId" name="jsfzrId" textname="jsfzrName" enabled="false" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="可见范围" length="1000" maxlength="1000"  mainfield="no"  single="false" />
                    </td>
                    <td style="width: 14%">联系方式：</td>
                    <td style="width: 30%;">
                        <input id="lxfs" name="lxfs"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">依托项目：</td>
                    <td style="width: 30%">
                        <input id="ytxm" name="ytxm"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 14%">项目编号：</td>
                    <td style="width: 30%">
                        <input id="xmNum" name="xmNum"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">鉴定日期：</td>
                    <td style="width: 30%">
                        <input id="jdTime" name="jdTime"  class="mini-datepicker" format="yyyy-MM-dd"
                                style="width:98%;" />
                    </td>
                    <td style="width: 14%">技术类别：</td>
                    <td style="width: 30%;">
                        <input id="jslb" name="jslb" class="mini-combobox" style="width:480px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'XTJS','value' : '系统技术'},{'key' : 'LBJJS','value' : '零部件技术'},
                                   {'key' : 'ZCJS','value' : '支撑技术'},{'key' : 'QT','value' : '其他'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">专业方向：</td>
                    <td style="width: 30%;">
                        <input id="jsfx" name="jsfx" class="mini-combobox" style="width:480px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'YY','value' : '液压'},{'key' : 'JG','value' : '结构'},{'key' : 'DL','value' : '动力'}
                                   ,{'key' : 'KZ','value' : '控制'},{'key' : 'DQ','value' : '电气'},{'key' : 'FZ','value' : '仿真'}
                                   ,{'key' : 'CS','value' : '测试'},{'key' : 'QT','value' : '其他'}]"
                        />
                    </td>
                    <td style="width: 14%">研发方向：</td>
                    <td style="width: 30%;">
                        <input id="yffx" name="yffx" class="mini-combobox" style="width:480px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'JNJS','value' : '节能技术'},{'key' : 'KKXJS','value' : '可靠性技术'},{'key' : 'LBJYF','value' : '零部件研发'}
                                   ,{'key' : 'QLH','value' : '轻量化'},{'key' : 'YYJS','value' : '预研技术'},{'key' : 'RXSS','value' : '人性化舒适化'}
                                   ,{'key' : 'ZNHJS','value' : '智能化技术'},{'key' : 'QT','value' : '其他'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">是否是已鉴定技术：</td>
                    <td style="width: 30%;">
                        <input id="ifjdjs" name="ifjdjs" class="mini-combobox" style="width:480px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'yes','value' : '是'},{'key' : 'no','value' : '否'}]"
                        />
                    </td>
                    <td style="width: 14%">技术来源：</td>
                    <td style="width: 30%;">
                        <input id="jsly" name="jsly" class="mini-combobox" style="width:480px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'WJ2','value' : '产学研技术'},{'key' : 'WJ1','value' : '自主开发'},{'key' : 'WJ3','value' : '联合开发'}
                                   ,{'key' : 'WJ4','value' : '其他'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">技术描述：</td>
                    <td style="width: 30%;" colspan="4">
						<textarea id="jsms" name="jsms" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
                                  emptytext="" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">团队花名册(内部人员)：</td>
                    <%--<td style="width: 3%;" colspan="4">--%>
						<%--<textarea id="tdhmc" name="tdhmc" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"--%>
                                  <%--emptytext="" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>--%>
                    <%--</td>--%>
                    <td  colspan="4">
                        <div class="mini-toolbar" id="tdmcButtons" >
                            <a class="mini-button" id="addJssjktdmc"  plain="true" onclick="addJssjk_tdmcInfoRow">添加</a>
                            <a class="mini-button" id="removeJssjktdmc" plain="true" onclick="removeJssjk_tdmcInfoRow">删除</a>
                        </div>
                        <div id="grid_jssjk_tdmcInfo" class="mini-datagrid" style="width: 100%; height: 220px" allowResize="false"
                             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="true" showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <%--<div field="wbName"  align="center" headerAlign="center" width="50" renderer="render">外部单位人员
                                    <input property="editor" class="mini-textarea"/>
                                </div>--%>
                                <div field="nbIds" align="center" displayField="nbNames" headerAlign="center" width="50">公司内部人员
                                    <input property="editor" class="mini-user rxc" plugins="mini-user"  style="width:90%;height:34px;" allowinput="false" length="500" maxlength="500"  mainfield="no"  single="true" />
                                </div>
                                <div field="gznr"  align="center" headerAlign="center" width="50" renderer="render">工作内容
                                    <input property="editor" class="mini-textarea" />
                                </div>
                                <div field="lxfs"  align="center" headerAlign="center" width="30" renderer="render">联系方式
                                    <input property="editor" class="mini-textarea"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">团队花名册(外部人员)：</td>
                    <td  colspan="4">
                        <div class="mini-toolbar" id="tdmcwbButtons" >
                            <a class="mini-button" id="addJssjktdmcwb"  plain="true" onclick="addJssjk_tdmcwbInfoRow">添加</a>
                            <a class="mini-button" id="removeJssjktdmcwb" plain="true" onclick="removeJssjk_tdmcwbInfoRow">删除</a>
                        </div>
                        <div id="grid_jssjk_tdmcwbInfo" class="mini-datagrid" style="width: 100%; height: 220px" allowResize="false"
                             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="true" showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div field="wbdw"  align="center" headerAlign="center" width="50">外部单位
                                    <input property="editor" class="mini-textarea"/>
                                </div>
                                <div field="wbName" align="center" headerAlign="center" width="50">外部单位人员
                                    <input property="editor" class="mini-textarea" />
                                </div>
                                <div field="gznr"  align="center" headerAlign="center" width="50" renderer="render">工作内容
                                    <input property="editor" class="mini-textarea" />
                                </div>
                                <div field="lxfs"  align="center" headerAlign="center" width="30" renderer="render">联系方式
                                    <input property="editor" class="mini-textarea"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%;height: 300px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button"  onclick="addJssjkFile()">添加附件</a>
                            <span style="color: red">注：添加附件前，请先进行草稿的保存</span>
                            <span>成果交付（填写交付成果类别，并提交相应附件）
（成果类别：软件代码、控制算法代码、软件说明、设计图纸、计算方法、算法图形、试验方法、作业规范、性能指标说明文档、技术合作厂家、试验报告、应用范围机型说明、专利、论文、安装程序、技术标准、汇报文档、鉴定证书等。)</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" url="${ctxPath}/jssj/core/config/getJssjkFileList.do?jssjkId=${jssjkId}" autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true" >
                            <div property="columns">
                                <div type="indexcolumn" align="center"  width="20">序号</div>
                                <div field="fileName"  width="140" headerAlign="center" align="center" >附件名称</div>
                                <div field="fjlx"  width="80" headerAlign="center" align="center" renderer="onFjlxRenderer">附件类别</div>
                                <div field="fileSize"  width="80" headerAlign="center" align="center" >附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var status="${status}";
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var fileListGrid=mini.get("fileListGrid");
    var formJssjk = new mini.Form("#formJssjk");
    var jssjkId="${jssjkId}";
    var nodeVarsStr='${nodeVars}';
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var currentUserId="${currentUserId}";
    var currentUserRoles=${currentUserRoles};
    var sptype="${sptype}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var grid_jssjk_tdmcInfo = mini.get("#grid_jssjk_tdmcInfo");
    var grid_jssjk_tdmcwbInfo = mini.get("#grid_jssjk_tdmcwbInfo");

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        if(action=='edit' || action=='task'){
            cellHtml=returnJssjkPreviewSpan(record.fileName,record.id,record.jssjkId,coverContent);
        }else if(action=='detail'){
            if(currentUserName =='管理员' ||currentUserId ==record.CREATE_BY_ || whetherIfFgld(currentUserRoles)){
                cellHtml=returnJssjkPreviewSpan(record.fileName,record.id,record.jssjkId,coverContent);
            }else {
                cellHtml='<span  title="预览" style="color: silver" >预览</span>';
            }
        }
        if(action=='edit' || action=='task'){
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downJssjLoadFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
        }else if(action=='detail'){
            if(currentUserName =='管理员' || currentUserId ==record.CREATE_BY_ || whetherIfFgld(currentUserRoles)){
                cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="downJssjLoadFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
            }else {
                cellHtml+='&nbsp;&nbsp;&nbsp;<span  title="下载" style="color: silver" >下载</span>';
            }
        }
        //增加删除按钮
        if(action=='edit' || (action=='task' && isBianzhi == 'yes')) {
            var deleteUrl="/jssj/core/config/delJssjkFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.id+'\',\''+record.jssjkId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }
    function onFjlxRenderer(e) {
        var record = e.record;
        var fjlx = record.fjlx;
        var arr = [ {'key' : 'RJDM','value' : '软件代码'},{'key' : 'SFDM','value' : '控制算法代码'},{'key' : 'RJSM','value' : '软件说明'}
            ,{'key' : 'SJTZ','value' : '设计图纸'},{'key' : 'JSFF','value' : '计算方法'},{'key' : 'SFTX','value' : '算法图形'}
            ,{'key' : 'SYFF','value' : '试验方法'},{'key' : 'ZYGF','value' : '作业规范'},{'key' : 'XNZB','value' : '性能指标说明文档'}
            ,{'key' : 'JSHZ','value' : '技术合作厂家'},{'key' : 'SYBG','value' : '试验报告'},{'key' : 'JXSM','value' : '应用范围机型说明'}
            ,{'key' : 'ZL','value' : '专利'},{'key' : 'LW','value' : '论文'},{'key' : 'QT','value' : '其他'},{'key' : 'AZCX','value' : '安装程序'}
            ,{'key' : 'JSBZ','value' : '技术标准'},{'key' : 'HBWD','value' : '汇报文档'},{'key' : 'JDZS','value' : '鉴定证书'}
        ];
        return $.formatItemValue(arr,fjlx);
    }

</script>
</body>
</html>
