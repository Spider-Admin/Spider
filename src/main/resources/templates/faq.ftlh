<!DOCTYPE html>
<html lang="en">
	<@include.head/>
	<body>
		<h1><#if !isRelease>!Testing! </#if>${selfName}</h1>

		<@include.menu/>

		<h2>General <abbr title="Frequently Asked Questions">FAQ</abbr></h2>

		<h3>What is this?</h3>
		<p><@faqAbout?interpret /></p>

		<h3 id="hidden-categories">Which freesites are hidden?</h3>
		<p>
			Freesites, which are hidden are not shown in the index, but can still be found in <a href="#database">database</a>.
			Each freesite is manually checked once by me. I add one or more categories to each freesite.
			Freesites with the following categories are hidden:
		</p>
		<ul>
<#list hiddenCategories as hiddenCategory>
			<li><#if hiddenCategory?has_content>${hiddenCategory}<#else>Freesites without a category</#if></li>
<#else>
			<li>Nothing is hidden</li>
</#list>
		</ul>

		<h3 id="warning-categories">What is the meaning of ℹ️ and ⚠️?</h3>
		<p>
			These icons indicate that a comment is available with either information (ℹ️) or a warning (⚠️) about the freesite.
			Hover over the icon to see the comment. In the case of a warning, the comment always explains the reason.
			A warning is shown, if a freesite is categorized under any of the following:
		</p>
		<ul>
<#list warningCategories as warningCategory>
			<li>${warningCategory}</li>
<#else>
			<li>No categories for warnings defined!</li>
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

		<h3>What is CSAM?</h3>
		<p>
			In this index, CSAM (Child Sexual Abuse Material) refers to content
			that is illegal under §§ 184b and 184c of the German Criminal Code
			(Strafgesetzbuch - StGB). It includes all types of visual content,
			including photographs, videos, and realistic animations or drawings
			depicting minors (under 18) in unnatural, sexually suggestive poses
			or sexual activities. This covers so-called "posing images" - sexualized
			photos of minors without explicit acts - as well as fictional content.<br>
			While these are illegal in Germany, they may be legal in some other
			countries. We follow German legal definitions because most developers
			involved in Hyphanet are based in Germany.
		</p><p>
			Reference:
		</p><ul>
			<li><a href="https://www.gesetze-im-internet.de/stgb/__184b.html">§ 184b StGB</a> (<a href="https://www.gesetze-im-internet.de/englisch_stgb/englisch_stgb.html#p1813">translation</a>)</li>
			<li><a href="https://www.gesetze-im-internet.de/stgb/__184c.html">§ 184c StGB</a> (<a href="https://www.gesetze-im-internet.de/englisch_stgb/englisch_stgb.html#p1834">translation</a>)</li>
		</ul>

		<h3>Why have some freesites a yellow background?</h3>
		<p>
			Freesites will be <span class="highlight">highlighted in yellow</span>, if they are new or changed their online-status (from online to offline or vice versa).
		</p>

		<h2><abbr title="Frequently Asked Questions">FAQ</abbr> for (future) freesite-authors</h2>

		<h3>How can I add my freesite to your index?</h3>
		<ul>
			<li>Add a link to your freesite to a freesite, which is already in the index.</li>
			<li>Post a link to your freesite in the <abbr title="Freenet Message System">FMS</abbr> boards <kbd>sites</kbd> or <kbd>site.announce</kbd>.</li>
			<li>Post a link to your freesite in the Frost board <kbd>sites</kbd>.</li>
			<li>Contact me and send me a link to your freesite.</li>
		</ul>

		<h3>How can I create and insert my own freesite?</h3>
		<p>
			<a href="<@format.key freesite=publishFreesite/>"><@format.title freesite=publishFreesite/></a> explains how to create a freesite.
			Once created, you can insert your freesite with <a href="<@format.key freesite=jsiteFreesite/>"><@format.title freesite=jsiteFreesite/></a> into Hyphanet.
		</p>

		<h3>How can I add author, language, description or keywords to my freesite?</h3>
		<p>
			Just use the appropriate <abbr title="Hypertext Markup Language">HTML</abbr>-tags. Here is a example:<br>
			<textarea class="tag-example" readonly="readonly">
&lt;!DOCTYPE html&gt;
&lt;html lang="your-language"&gt;
&#32;&#32;&lt;head&gt;
&#32;&#32;&#32;&#32;&lt;meta http-equiv="content-type" content="text/html; charset=utf-8"&gt;
&#32;&#32;&#32;&#32;&lt;meta name="author" content="your-author"&gt;
&#32;&#32;&#32;&#32;&lt;meta name="description" content="your-description"&gt;
&#32;&#32;&#32;&#32;&lt;meta name="keywords" content="your-keyword1,your-keyword2"&gt;
&#32;&#32;&#32;&#32;&lt;title&gt;your title&lt;/title&gt;
&#32;&#32;&lt;/head&gt;
&#32;&#32;&lt;body&gt;
&#32;&#32;&lt;/body&gt;
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
			<li><label for="in-query">In = Which freesites has links to the freesite with the ID &lt;ID&gt;:</label><br>
				<input type="text" id="in-query" class="query" readonly="readonly" value="SELECT `Key`, `Edition`, `Title` FROM `Freesite` WHERE `ID` IN (SELECT `FreesiteID` FROM `Network` WHERE `TargetFreesiteID` = <ID>)"></li>
			<li><label for="out-query">Out = The freesite with the ID &lt;ID&gt; has links to these freesites:</label><br>
				<input type="text" id="out-query" class="query" readonly="readonly" value="SELECT `Key`, `Edition`, `Title` FROM `Freesite` WHERE `ID` IN (SELECT `TargetFreesiteID` FROM `Network` WHERE `FreesiteID` = <ID>)"></li>
		</ul>

<#if selfDatabaseKey?has_content>
		<h3 id="database">Where can I get the database of your index?</h3>
		<p>
			<#t>Download a <abbr title="Structured Query Language">SQL</abbr>-dump using this key <a href="<@format.key freesite=selfDatabaseFreesite key=selfDatabaseKey/>"><@format.title freesite=selfDatabaseFreesite key=selfDatabaseKey/></a>.

		</p>
</#if>
	</body>
</html>