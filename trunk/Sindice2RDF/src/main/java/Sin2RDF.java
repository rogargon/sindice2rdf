/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author antlop
 */
public class Sin2RDF extends HttpServlet {

    private static final String JSON_FORMAT = "json";
    private static final String RDFN3_FORMAT = "rdfn3";
    private static final String RDFXML_FORMAT = "rdfxml";
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        int numberOfResources = Integer.parseInt(request.getParameter("num"));
        String key =request.getParameter("key");
        String format = request.getParameter("format");

        if(format.equals(RDFN3_FORMAT) || format.equals(RDFXML_FORMAT)){
            PrintWriter out = response.getWriter();
            if( format.equals(RDFN3_FORMAT) ){
                response.setContentType("text/n3;charset=UTF-8");
            }else if ( format.equals(RDFXML_FORMAT)){
                response.setContentType("application/rdf+xml;charset=UTF-8");
            }
            //ArrayList<Resource> listResources = new ArrayList<Resource>();
            ArrayList<String> uris = getEntries(numberOfResources,key);
            Model model = ModelFactory.createMemModelMaker().createDefaultModel();
            try {
                Iterator it = uris.listIterator();
                while( it.hasNext()) {
                    String uri2 = it.next().toString();
                    JSON2Model(this.getJSONObject(uri2),model);
                }
                if( format.equals(RDFN3_FORMAT) ){
                    model.write(out, "N3");
                }else if ( format.equals(RDFXML_FORMAT)){
                    model.write(out, "RDF/XML-ABBREV");
                }
            }finally {
                out.close();
            }
        }

        if(format.equals(JSON_FORMAT)){
            PrintWriter out = response.getWriter();
            response.setContentType("application/json;charset=UTF-8");
            ArrayList<String> uris = getEntries(numberOfResources,key);
            JSONObject j = new JSONObject();
             try {
                Iterator it = uris.listIterator();
                while( it.hasNext()) {
                    String uri2 = it.next().toString();
                    JSONObject a = this.getJSONObject(uri2);
                    j.accumulate(uri2, a);
                }
                out.println(j);
             } finally {
                out.close();
            }
        }
    }

    protected JSONObject getJSONObject(String uri){
        JSONObject arrayJSON = null;
        try {
            String uriSindice = this.getServletConfig().getInitParameter("url_api_sindice");
            SindiceCacheClient sindiceCacheclient = new SindiceCacheClient(uriSindice);
            JSONObject jsonResponse = sindiceCacheclient.getJSONResponse(uri);
            arrayJSON = jsonResponse.getJSONObject(uri);
        } catch (IOException ex) {
            Logger.getLogger(Sin2RDF.class.getName()).log(Level.SEVERE, null, ex);
        }
        return arrayJSON;
    }

    protected ArrayList<String> getEntries(int numberOfResources, String key){
        int maxPage=1;
        if(numberOfResources>10){
            maxPage = numberOfResources/10;
        }
        int currentResources = 0;
        ArrayList<String> uris = new ArrayList<String>();
        
        for(int p=1;p<=maxPage;p++){
            try {
                String urlkey = this.getServletConfig().getInitParameter("url_api_sindice_search");
                SindiceSearchClient sindiceSearchClient = new SindiceSearchClient(urlkey);
                JSONObject jsonResponse2 = sindiceSearchClient.getJSONResponse(key, p);
                JSONArray arrayJSON2 = jsonResponse2.getJSONArray("entries");
                Iterator it = arrayJSON2.listIterator();
                while (it.hasNext() && currentResources < numberOfResources) {
                    JSONObject job = (JSONObject) it.next();
                    uris.add(job.getString("link"));
                    currentResources++;
                }
            } catch (IOException ex) {
                Logger.getLogger(Sin2RDF.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return uris;
    }

    protected Model Resources2Model(ArrayList<Resource> resources){
        Model model = ModelFactory.createMemModelMaker().createDefaultModel();
        Iterator it = resources.listIterator();
        while( it.hasNext()) {
            Resource r = (Resource) it.next();
            model.add(r.getModel());
        }
        return model;
    }

    protected void JSON2Model(JSONObject jsonObjec, Model model){

        if(jsonObjec.containsKey("explicit_content")){
            Iterator it = jsonObjec.getJSONArray("explicit_content").listIterator();
            while( it.hasNext() ) {
                String[] atriple = it.next().toString().split(" ");
                model.add(model.createResource(scape(StringEscapeUtils.unescapeXml(atriple[0]))), model.createProperty(scape(atriple[1])),scape(atriple[2]));
            }
        }
        //Add implicit content
        if(jsonObjec.containsKey("implicit_content")){
            Iterator it2 = jsonObjec.getJSONArray("implicit_content").listIterator();
            while( it2.hasNext() ) {
                String[] atriple = it2.next().toString().split(" ");
                model.add(model.createResource(scape(StringEscapeUtils.unescapeXml(atriple[0]))), model.createProperty(scape(atriple[1])),scape(atriple[2]));
            }
        }
    }

    public String scape(String label){
        return label.replace("<", "").replace(">", "");
      }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}