<#setting number_format="#">
<#assign gridId=grid.gridId>
<#assign dataoptions=grid.dataoptions>
<#assign edittype=grid.edittype>
<#assign oncellendedit=grid.oncellendedit>
<#assign fields=grid.fields>
<#assign formkey=grid.formkey>
<#assign treegrid=grid.treegrid>
<#assign treecolumn=grid.treecolumn>
<#assign buttonHtml=grid.buttonHtml>
	<#--子表的名字-->
<#assign label=grid.label>

<#assign endEidt=''>
<#if oncellendedit??>
	<#assign endEidt>oncellendedit="${oncellendedit}"</#assign>
</#if>

<#noparse><#assign tbright_</#noparse>${gridId}<#noparse>=permission_.sub.</#noparse>${gridId}<#noparse>.tbright ></#noparse>
<#--开始 隐藏-->
<#noparse><#if (!tbright_</#noparse>${gridId}<#noparse>.hidden )></#noparse>

<#noparse><#assign fieldsRight_</#noparse>${gridId}<#noparse>=permission_.sub.</#noparse>${gridId}<#noparse>.fields ></#noparse>
<#noparse><#if (!tbright_</#noparse>${gridId}<#noparse>.add && !tbright_</#noparse>${gridId}<#noparse>.edit && !tbright_</#noparse>${gridId}<#noparse>.del) || (edittype=='inline' && !tbright_</#noparse>${gridId}<#noparse>.edit) ><#assign readOnly_</#noparse>${gridId}<#noparse>=true></#if></#noparse>

	<div class="_initdata" grid="${gridId}"></div>
			<div class="mini-toolbar sonFormBtn" id="tl${gridId}">
                <div class="headerButtonLeft">${label}</div>
                <div class="headerButtonRight" style="float: right">
                <#noparse><#if </#noparse>!readOnly_${gridId}  >
				<#noparse><#if (tbright_</#noparse>${gridId}<#noparse>.add) > </#noparse>
		    	<a class="mini-button leftRadius" name="addbutton" iconCls="icon-jia" onclick="_addGridRow('${gridId}','${edittype}',false)" title="添加"></a>
		    	<#noparse></#if></#noparse>
		    	<#if (treegrid=="true")>
		    		<#noparse><#if (tbright_</#noparse>${gridId}<#noparse>.add) ></#noparse>
		    		<a class="mini-button" name="addbutton"  onclick="_addGridRow('${gridId}','${edittype}',true)">添加下级</a>
		    		<#noparse></#if></#noparse>
		    	</#if>

		    	<#if (edittype=='openwindow' || edittype=='editform')>
		    		<#noparse><#if (tbright_</#noparse>${gridId}<#noparse>.edit) ></#noparse>
		    		<a class="mini-button" name="editbutton"  onclick="_editGridRow('${gridId}','${edittype}')">编辑</a>
		    		<#noparse></#if></#noparse>
		    		<a class="mini-button" name="editbutton"  onclick="_detailGridRow('${gridId}','${edittype}')">明细</a>
		    	</#if>
		    	<#noparse><#if (tbright_</#noparse>${gridId}<#noparse>.del) ></#noparse>
				<a class="mini-button btnDel rightRadius" name="delbutton" iconCls="icon-trash"  onclick="_delGridRow('${gridId}')" title="删除"></a>
				<#noparse></#if></#noparse>
				<#noparse>
					</#if>
				</#noparse>
				<span class="btnGroup" <#if buttonHtml = "">style="margin: 0 5px;" </#if>>
					${buttonHtml}
				</span>
				<a class="mini-button leftRadius"  name="upbutton" plain="true" iconCls="icon-shang1" onclick="_upGridRow('grid_${gridId}','${edittype}')" title="上移"></a>
				<a class="mini-button rightRadius"  name="downbutton" plain="true" iconCls="icon-xia2" onclick="_downGridRow('grid_${gridId}','${edittype}')" title="下移"></a>
                </div>
			</div>
	<#noparse><#assign dataoption>${util.setRequired(tbright_</#noparse>${gridId}<#noparse>,</#noparse>'{${dataoptions}}'<#noparse>)}</#assign></#noparse>

	<div name="grid_${gridId}" id="grid_${gridId}" ${endEidt}   class="<#if (treegrid=="true")>mini-treegrid<#else>mini-datagrid</#if>"
					style="width:100%" height="auto" multiSelect="true" allowAlternating="flase"
	        		allowCellValid="true" oncellvalidation="onCellValidation" onheadercellclick="onHeaderCellClick"
	        		showPager="false"  allowCellWrap="true" allowCellSelect="true"
	        		data-options="<#noparse>${dataoption}</#noparse>"
	        <#noparse><#if readOnly_</#noparse>${gridId}<#noparse> > </#noparse>
				allowCellEdit="false"
			<#noparse><#else> </#noparse>
				allowCellEdit="true"
			<#noparse>
				</#if>
			</#noparse>

			<#if (treegrid=="true")>
				showTreeIcon="true" resultAsTree="false"  expandOnLoad="true"
    			treeColumn="${treecolumn}" idField="ID_" parentField="PARENT_ID_"
			</#if>

	     >
	    <div property="columns">
	    	<div type="indexcolumn" width="20" >序号</div>
	    	<#noparse><#if </#noparse>tableRight=="w" >
				<div type="checkcolumn" width="20"></div>
			<#noparse>
				</#if>
			</#noparse>

			<#list fields as field>
			<#noparse><#assign fieldRight_</#noparse>${field.name}<#noparse>=fieldsRight_</#noparse>${gridId}<#noparse>.</#noparse>${field.name} />

			<#noparse><#if (fieldRight_</#noparse>${field.name}<#noparse>.right!='none') ></#noparse>
			<#noparse>
			<#assign require=false>
			<#if ( fieldRight_</#noparse>${field.name}<#noparse>.require) ><#assign require=true></#if>
			</#noparse>

			<div  name="${field.name}"  field="${field.name}" header="${field.comment}"
				<#if field.width?? &&  field.width!=""> width="${field.width}" </#if>
				<#if (field.format?? &&  field.format!="")   && field.datatype?? >
					<#if (field.datatype=="date")>
						dateFormat="${field.format}"
					<#elseif (field.datatype=="number")>
						numberFormat="${field.format}"
					</#if>
				</#if>
				<#if (field.renderer??)> renderer="${field.renderer}" </#if>
				<#if (field.visible=="false")> visible="${field.visible}" </#if>
				<#if field.dataoptions?? &&  field.dataoptions!=""> data-options="${field.dataoptions}" </#if>
				<#if field.displayfield?? &&  field.displayfield!="">displayfield="${field.displayfield}"</#if>
				vtype="<#noparse>${util.setFieldRequired(</#noparse>'${field.vtype}',<#noparse>require</#noparse>)}"
				<#if field.control?? >
					 <#if field.control=="upload-panel">renderer="onFileRender" </#if>
					 <#if field.control=="mini-img">renderer="onImgRender" </#if>
				</#if>
			>
			<#noparse><#if (fieldRight_</#noparse>${field.name}<#noparse>.right=='w') ></#noparse>
			<#if field.editor??  >${field.editor}</#if>
			<#noparse></#if></#noparse>
			</div>
			<#noparse></#if></#noparse>

			</#list>
	    </div>
	</div>
<#--结束隐藏-->
<#noparse></#if></#noparse>