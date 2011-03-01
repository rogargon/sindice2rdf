/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

/**
 *
 * @author antlop
 */
public class Sindice2RDF extends HttpServlet {
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String uri = (String) request.getParameter("q");
        String uriSindice = this.getServletConfig().getInitParameter("url_api_sindice");
        try {
            SindiceCacheClient sindiceCacheclient = new SindiceCacheClient(uriSindice);
            JSONObject jsonResponse = sindiceCacheclient.getJSONResponse(uri);
            JSONObject arrayJSON = jsonResponse.getJSONObject(uri);
            Model model = JSON2Model(arrayJSON);
            model.write(out, "RDF/XML-ABBREV");
        }finally {
            out.close();
        }
    }

    protected Model JSON2Model(JSONObject jsonObject){
        Model model = ModelFactory.createMemModelMaker().createDefaultModel();
        //Add explicit content
        Iterator it = jsonObject.getJSONArray("explicit_content").listIterator();
        while( it.hasNext() ) {
            String[] atriple = it.next().toString().split(" ");
            model.add(model.createResource(scape(atriple[0])), model.createProperty(scape(atriple[1])),scape(atriple[2]));
        }
        //Add implicit content
        Iterator it2 = jsonObject.getJSONArray("implicit_content").listIterator();
        while( it2.hasNext() ) {
            String[] atriple = it2.next().toString().split(" ");
            model.add(model.createResource(scape(atriple[0])), model.createProperty(scape(atriple[1])),scape(atriple[2]));
        }
       return model;
    }

    public String scape(String str){
        return str.replace("<", "").replace(">", "");
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
