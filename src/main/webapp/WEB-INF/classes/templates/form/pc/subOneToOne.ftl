<#setting number_format="#">
<div class="form" id="form_${ent.name}" relationtype="onetoone" tablename="${ent.name}">
    <table cellspacing="1" cellpadding="0" class="table-detail column-two table-align ${color}">
        <caption>
            ${ent.comment}
        </caption>
        <tbody>
        	<#list ent.sysBoAttrs as field>
            <tr >
                <td>
                	${field.comment}<#if field.required><span class="required">*</span></#if>
                </td>
                <td>
                	<@fieldCtrl field=field inTable=false />
                	
                </td>
            </tr>
            </#list>
        </tbody>
    </table>
</div>