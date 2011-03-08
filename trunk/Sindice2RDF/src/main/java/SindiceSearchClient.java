
import java.io.IOException;
import java.io.InputStream;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;


 /**
 *
 * @author antlop
 */
public class SindiceSearchClient {
    private HttpClient httpClient;
    private String sindiceCacheUrl;

    public SindiceSearchClient(String sindiceCacheUrl){
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        //params.setConnectionTimeout(1000);
        params.setSoTimeout(5000);
        params.setParameter("http.protocol.content-charset","UTF-8");
        connectionManager.setParams(params);
        httpClient = new HttpClient(connectionManager);
        this.sindiceCacheUrl = sindiceCacheUrl;
    }

    public JSONObject getJSONResponse(String key, int page) throws IOException{
        GetMethod method = new GetMethod(sindiceCacheUrl);
        try{
          String query ="&page="+page+"&q="+key;
          method.setQueryString(query);
          final int status = httpClient.executeMethod(method);
          boolean success = status == HttpStatus.SC_OK;
          if(success){
            String in = method.getResponseBodyAsString();
            return (JSONObject) JSONSerializer.toJSON(in);
          }else{
            throw new IOException("http request failed, response was http status "+status+": "+method.getResponseBodyAsString());
          }
        }finally{
          method.releaseConnection();
        }
    }

    public InputStream getInputStreamRespone(String ... urls) throws IOException{
        GetMethod method = new GetMethod(sindiceCacheUrl);
        try{
          NameValuePair[] params = new NameValuePair[urls.length];
          for(int i=0;i<urls.length;i++){
            params[i] = new NameValuePair("url",urls[i]);
          }
          method.setQueryString(params);
          final int status = httpClient.executeMethod(method);
          boolean success = status == HttpStatus.SC_OK;
          if(success){
            InputStream in = method.getResponseBodyAsStream();
            return in;
          }else{
            throw new IOException("http request failed, response was http status "+status+": "+method.getResponseBodyAsString());
          }
        }finally{
          method.releaseConnection();
        }

    }
}


