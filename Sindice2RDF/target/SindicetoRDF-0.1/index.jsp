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
        <div id="search">
            <form action="Sin2RDF" method="get">
                <div>
                    <label>keyword</label>
                    <input type="text" name="key" size="45"  value="" />
                </div>
                <div>
                    <label>Number of results</label>
                    <input type="text" name="num" id="num" size="45"  value="20"/>
                </div>
                <div>
                    <label>Response's format</label>
                    <select name="format">
                        <option value="json">JSON</option>
                        <option value="rdfn3">RDF/N3</option>
                        <option value="rdfxml">RDF/XML</option>
                    </select>
                </div>
                <div>
                    <button type="submit">
                        SEARCH
                    </button>
                </div>
            </form>
        </div>
    </body>
</html>
