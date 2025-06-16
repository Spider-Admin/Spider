<!DOCTYPE html>
<html lang="en">
	<@include.head/>
	<body>
		<h1><#if !isRelease>!Testing! </#if>${selfName}</h1>
		<@include.menu/>
		<p>${freesiteList?size} keys</p>
		<table class="<#if outputType != "OFFLINE">online<#else>offline</#if>">
		<thead>
			<#t><tr>
				<#t><th>Title (Language)<br>Author</th>
				<#t><#if outputType != "OFFLINE"><th>Description<br>Keywords</th></#if>
				<#t><th>Crawled<br>Added</th>
				<#t><#if outputType != "OFFLINE"><th>Pages / Online %<br>Freesites: In / Out</th><#else><th>Freesites: In</th></#if>
			<#lt></tr>
		</thead>
		<tbody>
		<#list freesiteList as freesite>
			<#t><tr<@format.css freesite=freesite/>>
				<#t><td>
					<#t><#if !isRelease>${freesite.ID}<#if freesite.isHighlight()>, Highlight</#if>: </#if><#if freesite.isKeyClickable()><a href="<@format.key freesite=freesite/>"><@format.title freesite=freesite/></a><#else>${freesite.key}/${freesite.sitePath}/${freesite.editionWithHint}/</#if>
					<#t><#if freesite.language?has_content> (${freesite.language})</#if>
					<#t><#if freesite.commentFormated?has_content> <dfn title="${freesite.commentFormated}">${freesite.commentIcon}</dfn></#if>
					<#t><#if freesite.author?has_content><br>${freesite.author}</#if>
				<#t></td>
				<#t><#if outputType != "OFFLINE"><td>
					<#t><#if freesite.description?has_content>${freesite.description}</#if>
					<#t><#if freesite.keywordsFormated?has_content><#if freesite.description?has_content><br></#if>Keywords: ${freesite.keywordsFormated}</#if>
				</td></#if><#t>
				<#t><td>
					<#t>${freesite.crawled?date}<br>
					<#t>${freesite.added?date}
				<#t></td>
				<#t><td>
					<#t><#if outputType != "OFFLINE">${freesite.pathList?size} / <@format.percent value=freesite.pathOnlinePercent/><br></#if>
					<#t>${freesite.inNetwork?size}<#if outputType != "OFFLINE"> / ${freesite.outNetwork?size}</#if>
				<#t></td>
			<#lt></tr>
		</#list>
		</tbody>
		</table>
	</body>
</html>