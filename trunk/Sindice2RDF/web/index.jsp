<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Test Page</title>
    </head>
    <body>
        <h1>Send a query and get metadata in RDF/XML Format</h1>
        <h2>Search: </h2>
        <div class="search-types" id="search-type-term">
        <form action="Sindice2RDF" method="get">
          <input type="text" name="q" size="45"  value="" />
          <button type="submit" class="inspectButton">
            SEARCH
          </button>
        </form>
        </div>
    </body>
</html>
