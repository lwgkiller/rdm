<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>一次性采购物料价格审批</title>
    <%@include file="/commons/edit.jsp"%>
    <script src="${ctxPath}/scripts/matPriceReview/matPriceReviewEditDisposable.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/matPriceReview/matPriceReviewUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css" />
</head>


<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveMatPriceReview" class="mini-button" onclick="saveOrCommitMatPriceReview('save')">暂存</a>
        <a id="commitMatPriceReview" class="mini-button" onclick="saveOrCommitMatPriceReview('commit')">提交到OA</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto">
        <form id="materielApplyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="reviewCategory" name="reviewCategory" class="mini-hidden"/>
            <p style="font-size: 16px;font-weight: bold">基本信息</p><hr />
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 200px">
                <tr>
                    <td >申请单号：</td>
                    <td >
                        <input id="applyNo" name="applyNo"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td >申请人部门：</td>
                    <td >
                        <input id="applyDeptId" name="applyDeptId" textname="applyDeptName" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px" showclose="false"
                               allowinput="false" length="500" maxlength="500" minlen="0" single="true" initlogindep="false"/>
                    </td>
                    <td >申请人：</td>
                    <td >
                        <input id="applyUserId" name="applyUserId" textname="applyUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                               mainfield="no"  single="true" />
                        <input name="applyUserNo"  class="mini-hidden" style="width:98%;" />
                    </td>
                    <td>申请人电话：<span style="color:red">*</span></td>
                    <td>
                        <input id="applyUserMobile" name="applyUserMobile"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td >申报类别：<span style="color:red">*</span></td>
                    <td >
                        <input id="applyCategory" name="applyCategory" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
                               data="[ {'key' : '技术、质量改进','value' : '技术、质量改进'},{'key' : '新品试制','value' : '新品试制'},{'key' : '生产、服务急需','value' : '生产、服务急需'}]"
                        />
                    </td>
                    <td >供应商编码：<span style="color:red">*</span></td>
                    <td >
                        <input name="applierCode"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td >供应商名称（全称）：<span style="color:red">*</span></td>
                    <td >
                        <input name="applierName"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 7%">地址：<span style="color:red">*</span></td>
                    <td style="width: 18%">
                        <input id="address" name="address" class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">配套产品：<span style="color:red">*</span></td>
                    <td style="width: 18%">
                        <input id="ptProduct" name="ptProduct" class="mini-textbox" style="width:98%;" />
                    </td>
                    <td >质量特性：<span style="color:red">*</span></td>
                    <td >
                        <input id="zlFeature" name="zlFeature" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
                               data="[ {'key' : 'A类件','value' : 'A类件'},{'key' : 'B类件','value' : 'B类件'},{'key' : 'C类件','value' : 'C类件'},{'key' : '辅料','value' : '辅料'}]"
                        />
                    </td>
                    <td >物料类别：<span style="color:red">*</span></td>
                    <td >
                        <input id="matCategory" name="matCategory" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
                               data="[ {'key' : '外协','value' : '外协'},{'key' : '外购','value' : '外购'}]"
                        />
                    </td>
                    <td >币种：<span style="color:red">*</span></td>
                    <td >
                        <input id="moneyCategory" name="moneyCategory" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
                               data="[ {'key' : '人民币','value' : '人民币'},{'key' : '外币','value' : '外币'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">执行税率：<span style="color:red">*</span></td>
                    <td style="width: 18%">
                        <input name="zxRate"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td >是否附图：<span style="color:red">*</span></td>
                    <td >
                        <input id="sfft" name="sfft" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
                               data="[ {'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                        />
                    </td>
                    <td style="width: 7%">技术协议编号：</td>
                    <td style="width: 18%">
                        <input id="jsxyNumber" name="jsxyNumber" class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 7%">采购申请号：</td>
                    <td style="width: 18%">
                        <input id="cgApplyNo" name="cgApplyNo" class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">采购订单号：</td>
                    <td style="width: 18%">
                        <input id="cgOrderNo" name="cgOrderNo" class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="width: 7%">要求到货时间：</td>
                    <td style="width: 18%">
                        <input name="requireArrivalTime"  id="requireArrivalTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:46%;" />
                    </td>
                    <td style="width: 7%">采购人员：</td>
                    <td style="width: 18%">
                        <input id="cgUser" name="cgUser" class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td >申报价格说明：<br>（不超过1000字）</td>
                    <td colspan="3">
                        <input id="priceDesc" name="priceDesc" class="mini-textarea" style="width:98%;height:110px"
                               emptytext="具体填写规范：1、采购原因。2、采购价格对比，同类商品对比或不同供应商价格对比。3、其他说明。"/>
                    </td>
                    <td >附件：</td>
                    <td colspan="3">
                        <a id="uploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;" onclick="addMatPriceReviewFile">附件列表</a><br>
                        <span>附件上传：1、供应商报价单。2、《徐工挖机价格审批同类产品对比表》或《徐工挖机价格审批供应商比价表》。3、价格谈判会议纪要。4、其他必要资料。</span>
                    </td>

                </tr>
            </table>
        </form>
        <p style="font-size: 16px;font-weight: bold;margin-top: 20px">物料信息</p><hr />
        <div class="mini-toolbar" id="matDetailButtons" style="margin-bottom: 5px">
            <a class="mini-button"   plain="true" id="addOneMateriel" onclick="editOrViewMateriel(null,'edit')">添加</a>
            <a class="mini-button btn-red"  plain="true" id="delMateriel" onclick="deleteMateriel()">删除</a>
        </div>
        <div id="materielGrid" class="mini-datagrid"  allowResize="false" url="${ctxPath}/matPriceReview/core/getMatDetailList.do?reviewId=${matPriceReviewObjId}"
             idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false" autoload="true"
             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" style="height: 350px"
             allowCellWrap="true" showVGridLines="true">
            <div property="columns">
                <div type="checkcolumn" width="30px"></div>
                <div renderer="matDetailRenderer" align="center" width="120px" cellCls="actionIcons" headerAlign="center" >操作</div>
                <div field="matCode" width="110px" headerAlign="center" align="center" >物料号</div>
                <div field="matName" width="130px" headerAlign="center" align="center" >物料名称</div>
                <div field="jclx" width="130px" headerAlign="center" align="center" renderer="jclxRenderer" >集采类型</div>
                <div field="tuhao"  headerAlign="center" align="center" width="100px">图号(规格)</div>
                <div field="zjxh" width="100px" headerAlign="center" align="center" >整机型号</div>
                <div field="jldw" width="60px" headerAlign="center" align="center" >计量单位</div>
                <div field="cgsl" width="50px" headerAlign="center" align="center" >采购数量</div>
                <div field="jghs"  width="70px" headerAlign="center" align="center" >价格(含税)</div>
                <div field="jgbhs" width="70px"  headerAlign="center" align="center" >价格(不含税)</div>
                <div field="yfcdf" width="85px"  headerAlign="center" align="center" >运费承担方</div>
                <div field="remark" width="90px" headerAlign="center" align="center" >备注</div>
            </div>
        </div>

        <p style="font-size: 16px;font-weight: bold;margin-top: 20px">非集采信息记录</p><hr />
        <div class="mini-toolbar" id="fjcRecordButtons" style="margin-bottom: 5px">
            <a class="mini-button"   plain="true" id="addOneFjcRecord" onclick="editOrViewRecord('','edit','fjc')">添加</a>
            <a class="mini-button btn-red"  plain="true" id="delOneFjcRecord" onclick="deleteRecord('','fjc')">删除</a>
            <%--            <div style="display: inline-block" class="separator"></div>
                        <a class="mini-button"   plain="true" id="generateFjcRecord" onclick="generateRecord('fjc')">根据物料自动添加</a>--%>
        </div>
        <div id="fjcRecordGrid" class="mini-datagrid"  allowResize="false" url="${ctxPath}/matPriceReview/core/getRecordList.do?reviewId=${matPriceReviewObjId}&jclx=fjc"
             idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false" autoload="true"
             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" style="height: 250px"
             allowCellWrap="true" showVGridLines="true">
            <div property="columns">
                <div type="checkcolumn" width="30px"></div>
                <div renderer="matRecordRenderer" align="center" width="140px" cellCls="actionIcons" headerAlign="center" >操作</div>
                <div field="applierCode" width="110px" headerAlign="center" align="center" >供应商代码(SAP)</div>
                <div field="applierName" width="250px" headerAlign="center" align="center" >供应商名称</div>
                <div field="matCode" width="100px" headerAlign="center" align="center" >物料号</div>
                <div field="matName"  headerAlign="center" align="center" width="250px">物料描述</div>
                <div field="mrpkzz" width="80px" headerAlign="center" align="center" >MRP控制者</div>
                <div field="cgzz" width="70px" headerAlign="center" align="center" >采购组织</div>
                <div field="gc" width="70px" headerAlign="center" align="center" >工厂代码</div>
                <div field="recordType" width="120px" headerAlign="center" align="center" >采购信息记录类型</div>
                <div field="planDeliveryTime"  width="90px" headerAlign="center" align="center" >计划交货时间</div>
                <div field="cgz" width="70px"  headerAlign="center" align="center" >采购组</div>
                <div field="jingjia" width="90px"  headerAlign="center" align="center" >净价</div>
                <div field="bizhong" width="65px"  headerAlign="center" align="center" >币种</div>
                <div field="jgdw" width="70px" headerAlign="center" align="center" >价格单位</div>
                <div field="jldw" width="70px" headerAlign="center" align="center" >单位</div>
                <div field="shuima" width="70px" headerAlign="center" align="center" >税码</div>
                <div field="sfGjPrice" width="90px" headerAlign="center" align="center" >是否估计价格</div>
            </div>
        </div>
        <p style="font-size: 16px;font-weight: bold;margin-top: 20px">集采信息记录</p><hr />
        <div class="mini-toolbar" id="jcRecordButtons" style="margin-bottom: 5px">
            <a class="mini-button"   plain="true" id="addOneJcRecord" onclick="editOrViewRecord('','edit','gyjc')">添加</a>
            <a class="mini-button btn-red"  plain="true" id="delOneJcRecord" onclick="deleteRecord('','gyjc')">删除</a>
            <%--<div style="display: inline-block" class="separator"></div>
            <a class="mini-button"   plain="true" id="generateJcRecord" onclick="generateRecord('gyjc')">根据物料自动添加</a>--%>
        </div>
        <div id="jcRecordGrid" class="mini-datagrid"  allowResize="false" url="${ctxPath}/matPriceReview/core/getRecordList.do?reviewId=${matPriceReviewObjId}&jclx=jc"
             idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false" autoload="true"
             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" style="height: 250px"
             allowCellWrap="true" showVGridLines="true">
            <div property="columns">
                <div type="checkcolumn" width="30px"></div>
                <div renderer="matRecordRenderer" align="center" width="140px" cellCls="actionIcons" headerAlign="center" >操作</div>
                <div field="cgzz" width="80px" headerAlign="center" align="center" >采购组织</div>
                <div field="cggs" width="80px" headerAlign="center" align="center" >采购公司</div>
                <div field="gc"  headerAlign="center" align="center" width="80px">工厂</div>
                <div field="applierName" width="250px" headerAlign="center" align="center" >供应商名称</div>
                <div field="applierCode" width="100px" headerAlign="center" align="center" >供应商编码</div>
                <div field="matName" width="250px" headerAlign="center" align="center" >物料描述</div>
                <div field="matCode" width="100px" headerAlign="center" align="center" >物料编码</div>
                <div field="jldw"  width="70px" headerAlign="center" align="center" >采购单位</div>
                <div field="jgdw" width="70px"  headerAlign="center" align="center" >价格单位</div>
                <div field="jgNumber" width="70px"  headerAlign="center" align="center" >价格数量</div>
                <div field="jbdw" width="70px"  headerAlign="center" align="center" >基本单位</div>
                <div field="bizhong" width="70px" headerAlign="center" align="center" >币种</div>
                <div field="wsdj" width="90px" headerAlign="center" align="center" >未税单价</div>
                <div field="shuima" width="65px" headerAlign="center" align="center" >税码</div>
                <div field="shuilv" width="65px" headerAlign="center" align="center" >税率</div>
                <div field="priceValidStart" width="95px" headerAlign="center" align="center">价格有效期从</div>
                <div field="priceValidEnd" width="95px" headerAlign="center" align="center" >价格有效期至</div>
                <div field="fktj" width="70px" headerAlign="center" align="center" >付款条件</div>
                <div field="jsfs" width="70px" headerAlign="center" align="center" >结算方式</div>
                <div field="zgPrice" width="70px" headerAlign="center" align="center" >暂估价</div>
                <div field="jgld" width="70px" headerAlign="center" align="center" >价格联动</div>
                <div field="jgsy" width="70px" headerAlign="center" align="center" >价格术语</div>
                <div field="cpf" width="70px" headerAlign="center" align="center" >出票方</div>
                <div field="remark" width="80px" headerAlign="center" align="center" >行备注</div>
            </div>
        </div>

    </div>
</div>

<div id="matDetailWindow" title="物料详情" class="mini-window" style="width:800px;height:400px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="topToolBar" style="text-align:right;">
        <a class="mini-button" id="saveMatBtn" onclick="saveMatDetail()">保存</a>
        <a class="mini-button btn-red" onclick="closeMatDetail()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;">
        <form id="matDetailForm" method="post">
            <input name="id" class="mini-hidden"/>
            <input name="reviewId" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-six grey" style="width: 99%">
                <tr>
                    <td align="center" style="width: 10%">物料号：<span style="color:red">*</span></td>
                    <td align="center" style="width: 40%">
                        <input id="matCode" name="matCode" class="mini-textbox" style="width:98%;height:34px;"/>
                    </td>
                    <td align="center" style="width: 10%">物料名称：<span style="color:red">*</span></td>
                    <td align="center" style="width: 40%">
                        <input id="matName" name="matName" class="mini-textbox" style="width:85%;height:34px;"/>
                        <image id="syncMatName"  src="${ctxPath}/styles/images/同步.png" style="cursor: pointer;vertical-align: middle" title="从物料扩充获取信息" onclick="syncMatInfo('matName')"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="width: 10%">集采类型：<span style="color:red">*</span></td>
                    <td align="center" style="width: 40%">
                        <input id="jclx" name="jclx" class="mini-combobox" style="width:85%;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
                               data="[{'key' : '非集采','value' : 'fjc'},{'key' : '保税集采','value' : 'bsjc'},{'key' : '供应集采','value' : 'gyjc'}]"
                        />
                        <image id="syncJclx"  src="${ctxPath}/styles/images/同步.png" style="cursor: pointer;vertical-align: middle" title="从物料扩充获取信息" onclick="syncMatInfo('jclx')"/>
                    </td>
                    <td align="center" style="width: 10%">图号(规格)：<span style="color:red">*</span></td>
                    <td align="center" style="width: 40%">
                        <input id="tuhao" name="tuhao" class="mini-textbox" style="width:98%;height:34px;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="width: 10%">整机型号：<span style="color:red">*</span></td>
                    <td align="center" style="width: 40%">
                        <input id="zjxh" name="zjxh" class="mini-textbox" style="width:98%;height:34px;"/>
                    </td>
                    <td align="center" style="width: 10%">计量单位：<span style="color:red">*</span></td>
                    <td align="center" style="width: 40%">
                        <input id="jldw" name="jldw" class="mini-textbox" style="width:98%;height:34px;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="width: 10%">采购数量：<span style="color:red">*</span></td>
                    <td align="center" style="width: 40%">
                        <input id="cgsl" name="cgsl" class="mini-textbox" style="width:98%;height:34px;"/>
                    </td>
                    <td align="center" style="width: 10%">价格(含税)：<span style="color:red">*</span></td>
                    <td align="center" style="width: 40%">
                        <input id="jghs" name="jghs" class="mini-textbox" style="width:98%;height:34px;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="width: 10%">价格(不含税)：<span style="color:red">*</span></td>
                    <td align="center" style="width: 40%">
                        <input id="jgbhs" name="jgbhs" class="mini-textbox" style="width:98%;height:34px;"/>
                    </td>
                    <td align="center" style="width: 10%">运费承担方：<span style="color:red">*</span></td>
                    <td align="center" style="width: 40%">
                        <input id="yfcdf" name="yfcdf" class="mini-textbox" style="width:98%;height:34px;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="width: 10%">备注：</td>
                    <td align="center" style="width: 40%">
                        <input id="remark" name="remark" class="mini-textbox" style="width:98%;height:34px;"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var matPriceReviewObj=${matPriceReviewObj};
    var materielApplyForm = new mini.Form("#materielApplyForm");
    var materielGrid = mini.get("#materielGrid");
    var jcRecordGrid = mini.get("#jcRecordGrid");
    var fjcRecordGrid = mini.get("#fjcRecordGrid");
    var matDetailWindow = mini.get("matDetailWindow");
    var matDetailForm = new mini.Form("#matDetailForm");

    //操作栏
    materielGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    jcRecordGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    fjcRecordGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });

    function matDetailRenderer(e) {
        var record=e.record;
        var s = '<span title="查看" onclick="editOrViewMateriel(' + JSON.stringify(record).replace(/"/g, '&quot;') +',\'view\')">查看</span>';
        if(action!='view') {
            s+='<span title="编辑" onclick="editOrViewMateriel(' + JSON.stringify(record).replace(/"/g, '&quot;') +',\'edit\')">编辑</span>';
            s+='<span  title="删除" onclick="deleteMateriel(\''+record.id+'\')">删除</span>';
        }
        return s;
    }
    function matRecordRenderer(e) {
        var record=e.record;
        var jclx = record.jclx;
        var s = '<span title="查看" onclick="editOrViewRecord(\'' + record.id +'\',\'view\',\''+jclx+'\')">查看</span>';
        if(action!='view') {
            s+='<span title="编辑" onclick="editOrViewRecord(\'' + record.id +'\',\'edit\',\''+jclx+'\')">编辑</span>';
            s+='<span  title="删除" onclick="deleteRecord(\''+record.id+'\',\''+jclx+'\')">删除</span>';
        }

        return s;
    }

    function jclxRenderer(e) {
        var record=e.record;
        var jclx = record.jclx;
        if(jclx=='fjc') {
            return "非集采";
        } else if(jclx=='bsjc') {
            return "保税集采";
        } else if(jclx=='gyjc') {
            return "供应集采";
        }
        return jclx;
    }

</script>
</body>
</html>