/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cliffordc
 */
public class DHISQueryBuilder {

    private HttpRequest req;
    
    private static Map<String, DHISQueryBuilder> instanceMap;
    
    private static DHISQueryBuilder getInstance(String path) {
       if (instanceMap == null) {
           instanceMap = new HashMap<>();
       }
       if (!instanceMap.containsKey(path)) {
           instanceMap.put(path, new DHISQueryBuilder(path));
       }
       return instanceMap.get(path);
       
    }

    private DHISQueryBuilder(String path) {
        req = Unirest.get(path);
    }

    public DHISQueryBuilder addQuery(String name, String value) {
        req = req.queryString(name, value);
        return this;
    } 

}
