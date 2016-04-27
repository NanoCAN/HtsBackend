<%@ page import="org.springframework.util.ClassUtils" %>
<html>

<head>
    <meta name="layout" content="main">
    <title><g:if test="${params.query && params.query?.trim() != ''}">${params.query} - </g:if>Search</title>
    <style>

    .searchResult

    ul {
        list-style-type: none !important;
        width: 500px;
    }

    .searchResult h3 {
        font: bold Helvetica, Verdana, sans-serif;
    }

    .searchResult li .colorbox {
        float: left;
        width: 20px;
        height: 20px;
        margin: 5px;
        margin-left: 20px;
        border: 1px solid rgba(0, 0, 0, .2);
    }

    .searchResult li {
        padding: 10px;
        overflow: auto;
    }

    .searchResult li:hover {
        background: #eee;
        cursor: pointer;
    }
    </style>
</head>

  <div class="navbar">
      <div class="navbar-inner">
          <div class="container">
              <ul class="nav">
                  <g:render template="/templates/navmenu"></g:render>
              </ul>
          </div>
      </div>
  </div>

<div id="index-search" class="content scaffold-show" role="main">
<h1>Search</h1>

  <div class="searchResult">
      <ul>
          <g:each var="result" in="${searchResults}">
              <li>
              <g:set var="className" value="${org.springframework.util.ClassUtils.getShortName(result.class)}" />
              <g:set var="link" value="${createLink(controller: className[0].toLowerCase() + className[1..-1], action: 'show', id: result.id)}" />
              <div class="colorbox" style="background:${result.color?:'#fff'};"></div>
                  <a href="${link}">
                    <h3>${className}</h3>

                        id: ${result.id}
                        name: ${result?.encodeAsHTML()}
                  </a>
              </li>
          </g:each>
      </ul>
  </div>
</div>
  </body>
</html>
