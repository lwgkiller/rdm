<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/ryydEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }
        .table-detail > tbody > tr > td {
            border: 1px solid #eee;
            text-align: center !important;
            background-color: #fff;
            white-space: normal;
            word-break: break-all;
            color: rgb(85, 85, 85);
            font-weight: normal;
            padding: 4px;
            height: 40px;
            min-height: 40px;
            box-sizing: border-box;
            font-size: 15px;
        }
        .mini-grid-row
        {
            height:40px;
        }

    </style>
</head>
<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBtn" class="mini-button" style="display: none" onclick="saveRyyd()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formRyyd" method="post">
            <input id="zId" name="zId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 15%">对应月份：</td>
                    <td >
                        <input id="month" class="mini-monthpicker"  style="width: auto" name="month"/>
                    </td>
                    <td rowspan="2" colspan="6" align="center" headerAlign="center" style="width: 68%">部门人员变动情况(上月20日至本月12日)</td>
                </tr>
                <tr>
                    <td rowspan="3" colspan="2" align="center" headerAlign="center" style="width: 24%">部门现有人员情况(截止本月12日)</td>
                </tr>
                <tr>
                    <td style="width: 36%" colspan="3">人员增加情况</td>
                    <td style="width: 36%" colspan="3">人员减少情况</td>
                </tr>
                <tr>
                    <td style="width: 12%">      </td>
                    <td align="center" style="width: 12%">考核期内</td>
                    <td align="center" style="width: 12%">本月累计</td>
                    <td style="width: 12%">      </td>
                    <td align="center" style="width: 12%">考核期内</td>
                    <td align="center" style="width: 12%">本月累计</td>
                </tr>
                <tr>
                    <td>部门人员人数</td>
                    <td style="width: 12%">
                        <input allowInput="false" id="totalNum" name="totalNum" class="mini-textbox" style="width: auto" />
                    </td>
                    <td style="width: 12%">增加人数</td>
                    <td>
                        <input allowInput="false" onvaluechanged="sumUpkh()" id="upNumkh" name="upNumkh" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>
                        <input allowInput="false" onvaluechanged="sumUplj()" id="upNumlj" name="upNumlj" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>减少人数</td>
                    <td>
                        <input allowInput="false" onvaluechanged="sumDownkh()" id="downNumkh" name="downNumkh" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>
                        <input allowInput="false" onvaluechanged="sumDownlj()" id="downNumlj" name="downNumlj" class="mini-textbox" style="width: auto"/>
                    </td>
                </tr>
                <tr>
                    <td>其中：在册</td>
                    <td>
                        <input onvaluechanged="sumTotal()" id="totalZc" name="totalZc" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>其中：在册</td>
                    <td>
                        <input onvaluechanged="sumUpkh()" id="upZckh" name="upZckh" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>
                        <input onvaluechanged="sumUplj()" id="upZclj" name="upZclj" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>其中：在册</td>
                    <td>
                        <input onvaluechanged="sumDownkh()" id="downZckh" name="downZckh" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>
                        <input onvaluechanged="sumDownlj()" id="downZclj" name="downZclj" class="mini-textbox" style="width: auto"/>
                    </td>
                </tr>
                <tr>
                    <td>劳务</td>
                    <td>
                        <input onvaluechanged="sumTotal()" id="totalLw" name="totalLw" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>劳务</td>
                    <td>
                        <input onvaluechanged="sumUpkh()" id="upLwkh" name="upLwkh" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>
                        <input onvaluechanged="sumUplj()" id="upLwlj" name="upLwlj" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>劳务</td>
                    <td>
                        <input onvaluechanged="sumDownkh()" id="downLwkh" name="downLwkh" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>
                        <input onvaluechanged="sumDownlj()" id="downLwlj" name="downLwlj" class="mini-textbox" style="width: auto"/>
                    </td>
                </tr>
                <tr>
                    <td>实习生-技校生</td>
                    <td>
                        <input onvaluechanged="sumTotal()" id="totalJx" name="totalJx" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>实习生-技校生</td>
                    <td>
                        <input onvaluechanged="sumUpkh()" id="upJxkh" name="upJxkh" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>
                        <input onvaluechanged="sumUplj()" id="upJxlj" name="upJxlj" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>实习生-技校生</td>
                    <td>
                        <input onvaluechanged="sumDownkh()" id="downJxkh" name="downJxkh" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>
                        <input onvaluechanged="sumDownlj()" id="downJxlj" name="downJxlj" class="mini-textbox" style="width: auto"/>
                    </td>
                </tr>
                <tr>
                    <td>实习生-大学生</td>
                    <td>
                        <input onvaluechanged="sumTotal()" id="totalDx" name="totalDx" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>实习生-大学生</td>
                    <td>
                        <input onvaluechanged="sumUpkh()" id="upDxkh" name="upDxkh" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>
                        <input onvaluechanged="sumUplj()" id="upDxlj" name="upDxlj" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>实习生-大学生</td>
                    <td>
                        <input onvaluechanged="sumDownkh()" id="downDxkh" name="downDxkh" class="mini-textbox" style="width: auto"/>
                    </td>
                    <td>
                        <input onvaluechanged="sumDownlj()" id="downDxlj" name="downDxlj" class="mini-textbox" style="width: auto"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: auto;height: 350px ">人员增加明细：</td>
                    <td colspan="7">
                        <div style="margin-top: 20px;margin-bottom: 2px">
                            <a id="editMsgZj" class="mini-button " style="margin-right: 5px;display: none" plain="true"
                               onclick="addZjDetail()">新增</a>
                            <a id="removeZj" style="margin-right: 5px;display: none" class="mini-button" onclick="removeRyzj()">删除</a>
                        </div>
                        <div id="zjListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             idField="id" autoload="true" virtualScroll="true"
                             url="${ctxPath}/zhgl/core/ryyd/getRyydDetailList.do?gridType=ryzj&belongId=${zId}"
                             autoload="true" allowCellEdit="true" allowCellSelect="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div type="indexcolumn" align="center" width="30">序号</div>
                                <div field="name" width="110" headerAlign="center" align="center">姓名
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="identity" width="80" headerAlign="center" align="center">身份证号
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="post" width="80" headerAlign="center" align="center">岗位
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="ryType" width="80" headerAlign="center" align="center">人员类别
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="ydType" width="80" headerAlign="center" align="center">增加类型
                                    <input property="editor" class="mini-textbox" /></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: auto;height: 350px ">人员减少明细：</td>
                    <td colspan="7">
                        <div style="margin-top: 20px;margin-bottom: 2px">
                            <a id="editMsgJs" class="mini-button " style="margin-right: 5px;display: none" plain="true"
                               onclick="addJsDetail()">新增</a>
                            <a id="removeJs" style="margin-right: 5px;display: none" class="mini-button" onclick="removeRyjs()">删除</a>
                        </div>
                        <div id="jsListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             allowResize="false"
                             idField="id" autoload="true" virtualScroll="true"
                             url="${ctxPath}/zhgl/core/ryyd/getRyydDetailList.do?gridType=ryjs&belongId=${zId}"
                             autoload="true" allowCellEdit="true" allowCellSelect="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div type="indexcolumn" align="center" width="30">序号</div>
                                <div field="name" width="110" headerAlign="center" align="center">姓名
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="identity" width="80" headerAlign="center" align="center">身份证号
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="post" width="80" headerAlign="center" align="center">岗位
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="ryType" width="80" headerAlign="center" align="center">人员类别
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="ydType" width="80" headerAlign="center" align="center">减少类型
                                    <input property="editor" class="mini-textbox" /></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: auto;height: 350px ">考核期内提出离职人员明细：</td>
                    <td colspan="7">
                        <div style="margin-top: 20px;margin-bottom: 2px">
                            <a id="editMsgLz" class="mini-button " style="margin-right: 5px;display: none" plain="true"
                               onclick="addLzDetail()">新增</a>
                            <a id="removeLz" style="margin-right: 5px;display: none" class="mini-button" onclick="removeRylz()">删除</a>
                        </div>
                        <div id="lzListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             allowResize="false"
                             idField="id" autoload="true" virtualScroll="true"
                             url="${ctxPath}/zhgl/core/ryyd/getRyydDetailList.do?gridType=rylz&belongId=${zId}"
                             autoload="true" allowCellEdit="true" allowCellSelect="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div type="indexcolumn" align="center" width="30">序号</div>
                                <div field="name" width="110" headerAlign="center" align="center">姓名
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="identity" width="80" headerAlign="center" align="center">身份证号
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="post" width="80" headerAlign="center" align="center">岗位
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="ryType" width="80" headerAlign="center" align="center">人员类别
                                    <input property="editor" class="mini-textbox" /></div>
                                <div field="ydType" width="80" headerAlign="center" align="center">目前状态
                                    <input property="editor" class="mini-textbox" /></div>
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
    var status = "${status}";
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var zId = "${zId}";
    var formRyyd = new mini.Form("#formRyyd");
    var zjListGrid = mini.get("zjListGrid");
    var jsListGrid = mini.get("jsListGrid");
    var lzListGrid = mini.get("lzListGrid");
    var currentUserMainDepName = "${currentUserMainDepName}";
    var isDepRespMan = ${isDepRespMan};
    var isJxzy = ${isJxzy};
    var isZbsj = ${isZbsj};
    var isFgld = ${isFgld};
    var currentUserId = "${currentUserId}";

    function sumTotal() {
        var totalNum=0;
        var totalZc = mini.get("totalZc").getValue();
        var totalLw = mini.get("totalLw").getValue();
        var totalJx = mini.get("totalJx").getValue();
        var totalDx = mini.get("totalDx").getValue();
        if(totalZc!=""&&totalZc!=null){
            totalNum=totalNum+parseInt(totalZc);
        }
        if(totalLw!=""&&totalLw!=null){
            totalNum=totalNum+parseInt(totalLw);
        }
        if(totalJx!=""&&totalJx!=null){
            totalNum=totalNum+parseInt(totalJx);
        }
        if(totalDx!=""&&totalDx!=null){
            totalNum=totalNum+parseInt(totalDx);
        }
        mini.get("totalNum").setValue(totalNum);
    }
    function sumUpkh() {
        var upNumkh=0;
        var upZckh = mini.get("upZckh").getValue();
        var upLwkh = mini.get("upLwkh").getValue();
        var upJxkh = mini.get("upJxkh").getValue();
        var upDxkh = mini.get("upDxkh").getValue();
        if(upZckh!=""&&upZckh!=null){
            upNumkh=upNumkh+parseInt(upZckh);
        }
        if(upLwkh!=""&&upLwkh!=null){
            upNumkh=upNumkh+parseInt(upLwkh);
        }
        if(upJxkh!=""&&upJxkh!=null){
            upNumkh=upNumkh+parseInt(upJxkh);
        }
        if(upDxkh!=""&&upDxkh!=null){
            upNumkh=upNumkh+parseInt(upDxkh);
        }
        mini.get("upNumkh").setValue(upNumkh);
    }
    function sumUplj() {
        var upNumlj=0;
        var upZclj = mini.get("upZclj").getValue();
        var upLwlj = mini.get("upLwlj").getValue();
        var upJxlj = mini.get("upJxlj").getValue();
        var upDxlj = mini.get("upDxlj").getValue();
        if(upZclj!=""&&upZclj!=null){
            upNumlj=upNumlj+parseInt(upZclj);
        }
        if(upLwlj!=""&&upLwlj!=null){
            upNumlj=upNumlj+parseInt(upLwlj);
        }
        if(upJxlj!=""&&upJxlj!=null){
            upNumlj=upNumlj+parseInt(upJxlj);
        }
        if(upDxlj!=""&&upDxlj!=null){
            upNumlj=upNumlj+parseInt(upDxlj);
        }
        mini.get("upNumlj").setValue(upNumlj);
    }
    function sumDownkh() {
        var downNumkh=0;
        var downZckh = mini.get("downZckh").getValue();
        var downLwkh = mini.get("downLwkh").getValue();
        var downJxkh = mini.get("downJxkh").getValue();
        var downDxkh = mini.get("downDxkh").getValue();
        if(downZckh!=""&&downZckh!=null){
            downNumkh=downNumkh+parseInt(downZckh);
        }
        if(downLwkh!=""&&downLwkh!=null){
            downNumkh=downNumkh+parseInt(downLwkh);
        }
        if(downJxkh!=""&&downJxkh!=null){
            downNumkh=downNumkh+parseInt(downJxkh);
        }
        if(downDxkh!=""&&downDxkh!=null){
            downNumkh=downNumkh+parseInt(downDxkh);
        }
        mini.get("downNumkh").setValue(downNumkh);
    }
    function sumDownlj() {
        var downNumlj=0;
        var downZclj = mini.get("downZclj").getValue();
        var downLwlj = mini.get("downLwlj").getValue();
        var downJxlj = mini.get("downJxlj").getValue();
        var downDxlj = mini.get("downDxlj").getValue();
        if(downZclj!=""&&downZclj!=null){
            downNumlj=downNumlj+parseInt(downZclj);
        }
        if(downLwlj!=""&&downLwlj!=null){
            downNumlj=downNumlj+parseInt(downLwlj);
        }
        if(downJxlj!=""&&downJxlj!=null){
            downNumlj=downNumlj+parseInt(downJxlj);
        }
        if(downDxlj!=""&&downDxlj!=null){
            downNumlj=downNumlj+parseInt(downDxlj);
        }
        mini.get("downNumlj").setValue(downNumlj);
    }

</script>
</body>
</html>