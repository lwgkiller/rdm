<div class="div-form" v-if="hasRight('${ent.name}')">
	<div class="subTable_Title" >${ent.comment}</div>
	<div class="table-container">
		<table class="subTable">
			<tr>
				<th  v-if="getMgrRight('${ent.name}')">管理</th>
				<#list ent.sysBoAttrs as field>
				<th>${field.comment}</th>
				</#list>
			</tr>
			<tr v-for="(item, index) in data.SUB_${ent.name}">
				<td v-if="getMgrRight('${ent.name}')">
					<div class="subTable_delRow" v-if="getDelRight('${ent.name}')"  v-on:click="remove('${ent.name}',index)">删除</div>
					<div class="subTable_editRow" v-if="getEditRight('${ent.name}')"   v-on:click="editDialog('${ent.name}',index)">编辑</div>
				</td>
				<#list ent.sysBoAttrs as field>
					<td ><@fieldCtrl field=field type="subRow" entName=ent.name/></td>
				</#list>
			</tr>
		</table>
	</div>
	<div class="toolbar" v-if="getAddRight('${ent.name}')">
		<div class="subTable_addRow" v-on:click="addDialog('${ent.name}')">添加数据</div>
	</div>
	<rx-dialog title="${ent.comment}" ref="dialog_${ent.name}" v-on:handok="handOk">

		
	<div class="div-form-main">
		<div class="caption" >${ent.comment}</div>
		<div class="form-container form-containerrow" >
			<#assign rowTwoCol = ["mini-textbox","mini-checkboxlist","mini-combobox","mini-checkbox","mini-radiobuttonlist","mini-time","mini-month","mini-datepicker","mini-spinner"]>
			<#assign rowCol = ["mini-buttonedit","upload-panel","mini-user","mini-group","mini-dep","mini-textarea","mini-ueditor"]>
			<#list ent.sysBoAttrs as field>
	
				<#if  rowTwoCol?seq_contains(field.control)>
					<div class="form">
						<div class="row2col-title">
							${field.comment}<#if field.required><span class="required">*</span></#if>
						</div>
						<div class="row2col-input">
							<@fieldCtrl field=field type="curRow" entName=ent.name/>
						</div>
					</div>
				<#elseif rowCol?seq_contains(field.control)>
					<div class="form-rowcol">
						<div class="rowcol-title">
							${field.comment}<#if field.required><span class="required">*</span></#if>
						</div>
						<div class="rowcol-input">
							<@fieldCtrl field=field type="curRow" entName=ent.name/>
						</div>
					</div>
				</#if>
			</#list>
		</div>
	</div>
		
	</rx-dialog>
	
	<div class="form-container" v-if="data.SUB_${ent.name}.length==0&&getAddRight('${ent.name}')=='w'">
		<div class="emptyData">子表数据为空!</div>
	</div>
</div>
