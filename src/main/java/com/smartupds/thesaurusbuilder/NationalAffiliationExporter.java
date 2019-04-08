package com.smartupds.thesaurusbuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.log4j.Log4j;

/**
 * @author Yannis Marketakis (SmartUp Data Solutions)
 * @author Nikos Minadakis  (SmartUp Data Solutions)
 */
@Log4j
public class NationalAffiliationExporter {
    private static String endpointURL;
    private static final String TYPE_IRI="http://vocab.getty.edu/ontology#GuideTerm";
    private static final String N3_FILENAME="nationalAffiliation.n3";
    
    private static Map<String,String> fetchResources(String endpoint, String startingResourceIRI) throws MalformedURLException, IOException, URISyntaxException, InterruptedException{
        endpointURL=endpoint;
        Map<String,String> retMap=new HashMap<>();
        Set<String> irisToInspect=new HashSet<>();
        irisToInspect.add(startingResourceIRI);
        while(!irisToInspect.isEmpty()){
            String resource=irisToInspect.iterator().next();
            Map<String,String> intermediateResults=query(resource);
            retMap.putAll(intermediateResults);
            log.debug("Map with intermediate results (so far): "+retMap);
            irisToInspect.remove(resource);
            irisToInspect.addAll(intermediateResults.keySet());
            log.debug("Resource IRIs to inspect: "+irisToInspect.size());
            log.debug("Sleep for 2 seconds");
            Thread.sleep(2000);
        }
        System.out.println(retMap);
        return retMap;
    }
    
    private static Map<String,String> query(String resourceIRI) throws IOException, URISyntaxException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        URIBuilder builder=new URIBuilder(endpointURL);
        
        builder.setParameter("query", generateSparqlQuery(resourceIRI));
        HttpGet httpGet=new HttpGet(builder.build());
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        log.debug("Executing request " + httpGet.getRequestLine());
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
        String responseBody = httpclient.execute(httpGet, responseHandler);
        return parseResults(responseBody);
    }
    
    private static Map<String,String> parseResults(String jsonResponse){
        Map<String,String> retMap=new HashMap<>();
        JsonObject results=new JsonObject();
        JsonArray actualResults=new JsonParser().parse(jsonResponse).getAsJsonObject().get("results").getAsJsonObject().get("bindings").getAsJsonArray();
        log.debug("Found "+actualResults.size()+" results");
        for(JsonElement resultElem : actualResults){
            retMap.put(resultElem.getAsJsonObject().getAsJsonObject("s").get("value").getAsString(),
                       resultElem.getAsJsonObject().getAsJsonObject("label").get("value").getAsString());
        }
        return retMap;
    }
    
    private static String generateSparqlQuery(String originalResource){
        return "SELECT ?s ?label " 
              +"WHERE{?s <http://vocab.getty.edu/ontology#broader> <"+originalResource+">. "
              +"     ?s <http://www.w3.org/2008/05/skos-xl#prefLabel> ?pref_label_iri. "
              +"     ?pref_label_iri <http://vocab.getty.edu/ontology#term> ?label. "
              +"     FILTER(lang(?label)='en') "
              +"}";        
    }
    
    private static void outputNtriples(Map<String,String> resources) throws IOException{
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(N3_FILENAME)))) {
            for(String resourceIRI : resources.keySet()){
                out.write("<");
                out.write(resourceIRI);
                out.write("> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <");
                out.write(TYPE_IRI);
                out.write(">.\n");
                out.write("<");
                out.write(resourceIRI);
                out.write("> <http://www.w3.org/2000/01/rdf-schema#label> \"");
                out.write(resources.get(resourceIRI));
                out.write("\".\n");
            }
            out.flush();
        }
    }
    
    public static void main(String[] args) throws IOException, MalformedURLException, URISyntaxException, InterruptedException{
        Map<String,String> resources=fetchResources("http://vocab.getty.edu/sparql.json","http://vocab.getty.edu/aat/300111079");
        outputNtriples(resources);
    }
}