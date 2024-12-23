<#macro title freesite="" key="">
	<#if freesite != "">
		<#if freesite.title?has_content>
			<#t>${freesite.title}
		<#else>
			<#t>${freesite.sitePath}/${freesite.editionWithHint}
		</#if>
	<#else><#t>
		<#t>${key}
	</#if>
</#macro>

<#macro key freesite="" key="">
	<#if freesite != "">
		<#t>${keyPrefix}/${freesite.key?replace(" ", "%20")?replace("\t", "%09")}/${freesite.sitePath?url}/${freesite.editionWithHint}/
	<#else><#t>
		<#t>${keyPrefix}/${key}
	</#if>
</#macro>

<#macro activelink freesite>
	<#t><img src="<@key freesite=freesite/>activelink.png" alt="<@title freesite=freesite/>" width="108" height="36">
</#macro>

<#macro css freesite>
	<#t><#-- Save space by format obsolete freesites with CSS rather than use the del-tag for each cell -->
	<#t><#if freesite.isObsolete() && !freesite.isHighlight()> class="del"</#if>
	<#t><#if !freesite.isObsolete() && freesite.isHighlight()> class="highlight"</#if>
	<#t><#if freesite.isObsolete() && freesite.isHighlight()> class="highlight del"</#if>
</#macro>

<#macro percent value>
	<#t>${value?string["0.#; roundingMode=halfUp"]}
</#macro>
