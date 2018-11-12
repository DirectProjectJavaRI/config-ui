<!--<div id="header">
    
    <div class="clear"></div>
</div>-->
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div id="header_main">
<a href="<c:url value="/main"/>" id="bannerLeft"><img src="<c:url value="/resources/images/direct-project-logo2.png" />" alt="Direct Project" border="0" /></a>
</div>
<div id="nav_sub">
 <sec:authorize access="hasRole('ROLE_ADMIN')">
    <ul id="nav_sub_links">
        <li><a href="<c:url value="/main"/>">Manage Domains</a></li>
        <li><a href="<c:url value="/main/search?domainName=&submitType=gotosettings"/>">Agent Settings</a></li>
        <li><a href="<c:url value="/main/search?domainName=&submitType=gotocertificates"/>">Certificates</a></li>
        <li><a href="<c:url value="/main/search?domainName=&submitType=gotodns"/>">DNS Entries</a></li>
        <li><a href="<c:url value="/main/search?domainName=&submitType=ManageTrustBundles"/>">Trust Bundles</a></li>
        <!--  removing policies for now... not quite baked yet 
        <li><a href="<c:url value="/main/search?domainName=&submitType=ManagePolicies"/>">Policies</a></li>
        -->
    </ul>

    <div style="float:right;line-height: 38px;padding-right:10px;">
    You are logged in. <a href="<c:url value="/logout"/>">Log out</a>
</div>
    </sec:authorize>
</div>

<div id="main">