<table class="table-detail column-four table-align">
	<caption>[XX]详细信息</caption>
	<#--<thead>
		<tr>
			<td colspan="4" align="center"><h2>[XX]详细信息</h2></td>
		</tr>
	</thead>-->
	<tbody>
	  <#assign index=0>
	  <#list controls as control >
	  <#if index % 2==0>
	  <tr>
	  </#if>
		<td>${control.name}</td>
		<td><${control.tag} plugins="${control.editcontrol}" class="rcx ${control.editcontrol}" name="${control.key}" label="${control.name}"  mwidth="0" wunit="px" mheight="0" hunit="px"></td>
	  <#if index % 2==1>
	  </tr>
	  </#if>
	  <#assign index=index+1>
	  </#list>
  </tbody>
</table>
