<!DOCTYPE html>
<html lang="en">
	<@include.head/>
	<body>
		<h1><#if !isRelease>!Testing! </#if>${selfName}</h1>

		<@include.menu/>

		<h2>General <abbr title="Frequently Asked Questions">FAQ</abbr></h2>

		<h3>What is this?</h3>
		<p>
			This is an uncensored, but filtered index. Uncensored, because 
			<a href="#hidden-categories">almost every</a> crawled freesite appears in the index. Filtered, because the
			<a href="https://github.com/freenet/wiki/wiki/Content-Filtering">content-filter</a>
			was enabled while crawling the freesites. Dangerous pages can still be
			found in the "Offline"-section.
		</p>
		
		<h3 id="hidden-categories">Which freesites are hidden?</h3>
		<p>
			Freesites, which are hidden are not shown in the index, but can still be found in <a href="#database">database</a>.
			Each freesite is manually checked once by me. I add a one or more categories to each freesite.
			Freesites with the following categories are hidden:
		</p>
		<ul>
<#list hiddenCategories as hiddenCategory>
			<li><#if hiddenCategory?has_content>${hiddenCategory}<#else>Freesites without a category</#if></li>
<#else>
			<li>Nothing is hidden</li>			
</#list>			
		</ul>

		<h3>How can I contact you?</h3>
		<dl>
			<dt>Author:</dt>
			<dd>${author}</dd>
<#if freemail?has_content>
			<dt>Freemail:</dt>
			<dd>${freemail}</dd>
</#if>
<#if frost?has_content>
			<dt>Frost:</dt>
			<dd>${frost}</dd>
</#if>
<#if fmsPublicKey?has_content>
			<dt><abbr title="Freenet Message System">FMS</abbr>:</dt>
	<#if fmsFreesite??>
			<dd><a href="<@format.key freesite=fmsFreesite/>" title="Public Key: ${fmsPublicKey}">${author}</a></dd>
	<#else>
			<dd><span title="Public Key: ${fmsPublicKey}">${author}</span></dd>	
	</#if>
</#if>
<#if sone?has_content>
			<dt>Sone:</dt>
			<dd><a href="${keyPrefix}/Sone/viewSone.html?sone=${sone}">${author}</a></dd>
</#if>
		</dl>

		<h3>How can I bookmark this index?</h3>
		<p>
			You can bookmark this index using the following link: <a href="${keyPrefix}/?newbookmark=${selfFreesite.key}/${selfFreesite.sitePath?url_path}/${selfFreesite.editionWithHint}/&amp;desc=${selfFreesite.title?url_path}&amp;hasAnActivelink=true">Bookmark this index</a>
		</p>

		<h3>Where can I find the latest edition of this index?</h3>
		<p>
			If you bookmarked this index, your node will always find the latest edition for you. Everyone else can manually <a href="${keyPrefix}/${selfFreesite.key}/${selfFreesite.sitePath?url_path}/-1/">check for newer versions of this index</a>.
		</p>

		<h3>What is a "Fake-Key"?</h3>
		<p>
			A "Fake-Key" is a key, which looks correct, but did not work, because
			someone (mostly Anonymous from Frost) changed some characters of the
			key. My crawler calculates the
			<a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein
			distance</a> for all offline keys. Keys which have a distance greater
			then a given threshold are flagged as "Fake-Key". These keys are no
			longer shown in the index, but are still available in the database.
		</p>

		<h3>Why have some freesites a yellow background?</h3>
		<p>
			Freesites will be <span class="highlight">highlighted in yellow</span>, if they are new or changed their online-status (from online to offline or vice versa).
		</p>

		<h2><abbr title="Frequently Asked Questions">FAQ</abbr> for (future) freesite-authors</h2>

		<h3>How can I add my freesite to your index?</h3>
		<ul>
			<li>Add a link to your freesite to a freesite, which is already in the index. <a href="<@format.key freesite=tpiFreesite/>"><@format.title freesite=tpiFreesite/></a> is a index, which everybody can edit.</li>
			<li>Post a link to your freesite in the <abbr title="Freenet Message System">FMS</abbr> boards <kbd>sites</kbd> or <kbd>site.announce</kbd>.</li>
			<li>Post a link to your freesite in the Frost board <kbd>sites</kbd>.</li>
			<li>Contact me and send me a link to your freesite.</li>
		</ul>

		<h3>How can I create and insert my own freesite?</h3>
		<p>
			<a href="<@format.key freesite=publishFreesite/>"><@format.title freesite=publishFreesite/></a> explains how to create a freesite.
			Once created, you can insert your freesite with <a href="<@format.key freesite=jsiteFreesite/>"><@format.title freesite=jsiteFreesite/></a> into freenet.
		</p>

		<h3>How can I add author, language, description or keywords to my freesite?</h3>
		<p>
			Just use the appropriate <abbr title="Hypertext Markup Language">HTML</abbr>-tags. Here is a example:<br/>
			<textarea class="tag-example" readonly="readonly">
&lt;!DOCTYPE html&gt;
&lt;html lang="your-language"&gt;
&nbsp;&nbsp;&lt;head&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;meta http-equiv="content-type" content="text/html; charset=utf-8" /&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;meta name="author" content="your-author" /&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;meta name="description" content="your-description" /&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;meta name="keywords" content="your-keyword1,your-keyword2" /&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;title&gt;your title&lt;/title&gt;
&nbsp;&nbsp;&lt;/head&gt;
&nbsp;&nbsp;&lt;body&gt;
&nbsp;&nbsp;&lt;/body&gt;
&lt;/html&gt;
			</textarea>
		</p>

		<h3>My author, language, description and/or keywords are broken in your index</h3>
		<p>
			Validate the source of your freesite. For the <abbr title="Hypertext Markup Language">HTML</abbr> use 
			<a href="https://validator.w3.org/"><abbr title="World Wide Web Consortium">W3C</abbr> Markup Validation Service</a>.
			For <abbr title="Cascading Style Sheets">CSS</abbr> use <a href="https://jigsaw.w3.org/css-validator/"><abbr
			title="World Wide Web Consortium">W3C</abbr> <abbr
			title="Cascading Style Sheets">CSS</abbr> Validation Service</a>. Use <a
			href="https://www.torproject.org/download/">Tor Browser</a> for
			validating sensitive freesites.
		</p>

		<h2><abbr title="Frequently Asked Questions">FAQ</abbr> for technical users</h2>

<#if selfSourceFreesite??>
		<h3>Where can I get the source code?</h3>
		<p>
			Download the source code from <a href="<@format.key freesite=selfSourceFreesite/>"><@format.title freesite=selfSourceFreesite/></a>.
		</p>
</#if>

		<h3>Are there any details about the in/out-columns available?</h3>
		<p>
			At the moment only due a manual <abbr title="Structured Query Language">SQL</abbr>-query. Download the <abbr title="Structured Query Language">SQL</abbr>-dump, find the ID of the freesite (e.g. this freesite has the ID ${selfFreesite.ID}) and use the ID in the following queries:
		</p>
		<ul>
			<li><label for="in-query">In = Which freesites has links to the freesite with the ID &lt;ID&gt;:</label><br/>
				<input type="text" id="in-query" class="query" readonly="readonly" value="SELECT `Key`, `Edition`, `Title` FROM `Freesite` WHERE `ID` IN (SELECT `FreesiteID` FROM `Network` WHERE `TargetFreesiteID` = <ID>)"/></li>
			<li><label for="out-query">Out = The freesite with the ID &lt;ID&gt; has links to these freesites:</label><br/>
				<input type="text" id="out-query" class="query" readonly="readonly" value="SELECT `Key`, `Edition`, `Title` FROM `Freesite` WHERE `ID` IN (SELECT `TargetFreesiteID` FROM `Network` WHERE `FreesiteID` = <ID>)" /></li>
		</ul>

<#if selfDatabaseKey?has_content>
		<h3 id="database">Where can I get the database of your index?</h3>
		<p>
			Download a <abbr title="Structured Query Language">SQL</abbr>-dump using this key <a href="${keyPrefix}/${selfDatabaseKey}">${selfDatabaseKey}</a>.
		</p>
</#if>
	</body>
</html>