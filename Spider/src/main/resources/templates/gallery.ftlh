<!DOCTYPE html>
<html lang="en">
	<@include.head/>
	<body>
		<h1><#if !isRelease>!Testing! </#if>${selfName}</h1>
		<@include.menu/>
		<p>${freesiteList?size} keys</p>
		<#list freesiteList as freesite>
			<#t><#if freesite.isObsolete()><del></#if>
			<#t><a href="<@format.key freesite=freesite/>"><@format.activelink freesite=freesite/></a>
			<#t><#if freesite.isObsolete()></del></#if>

		</#list>
	</body>
</html>