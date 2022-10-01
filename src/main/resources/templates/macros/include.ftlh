<#macro head>
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8">
	<meta name="author" content="${metaAuthor}">
	<meta name="description" content="<@metaDescription?interpret />">
	<meta name="keywords" content="${metaKeywords}">
	<title>${selfName}<#if title != ""> - </#if>${title}</title>
	<link rel="stylesheet" href="style.css">
</head>
</#macro>

<#macro menu>
<ul id="menu">
	<li<#if outputType == "ONLINE"> class="active"</#if>><a href="index.htm">Online</a></li>
	<li<#if outputType == "ONLINE_IMG"> class="active"</#if>><a href="index-img.htm">Online (Gallery)</a></li>
	<li<#if outputType == "FMS"> class="active"</#if>><a href="fms.htm"><abbr title="Freenet Message System">FMS</abbr></a></li>
	<li<#if outputType == "FMS_IMG"> class="active"</#if>><a href="fms-img.htm"><abbr title="Freenet Message System">FMS</abbr> (Gallery)</a></li>
	<li<#if outputType == "SONE"> class="active"</#if>><a href="sone.htm">Sone</a></li>
	<li<#if outputType == "OFFLINE"> class="active"</#if>><a href="offline.htm">Offline</a></li>
	<li<#if outputType == "FAQ"> class="active"</#if>><a href="faq.htm">About / <abbr title="Frequently Asked Questions">FAQ</abbr></a></li>
</ul>
</#macro>
