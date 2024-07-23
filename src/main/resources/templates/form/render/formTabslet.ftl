<#if displayType=='first'>
	<#list list as tabs>
		<#if tabs_index lt 1>
			${tabs.val}
		</#if>
	</#list>
	<br/>
	<div class="mini-tabs tabs-pages" id="tab_${bpmFormView.boDefId}"  >
		<#list list as tabs>
			<#if tabs_index gt 0>
				<div title="${tabs.key}">
					${tabs.val}
				</div>
			</#if>
		</#list>
	</div>
</#if>


<#if displayType!='first'>
	<div class="mini-tabs tabs-pages" id="tab_${bpmFormView.boDefId}"  >
		<#list list as tabs>
			<div title="${tabs.key}">
				${tabs.val}
			</div>
		</#list>
	</div>
</#if>


	
	