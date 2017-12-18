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
import lombok.Builder;

/**
 *
 * @author cliffordc
 */

/*
final HttpResponse<String> result = Unirest.get("https://zim.dhis2.org/develop/api/{type}/{id}")
        .routeParam("type", "categories")
        .routeParam("id", "ru80fGU70hD")
        .queryString("fields", "categoryOptions[name,code,id]")
        .basicAuth(prop.getProperty("username"), prop.getProperty("password"))
        .asString();
return result.getBody();
*/

//public class DHISQueryBuilder {
//
//    private DHISQuery query;
//    
//    private static DHISQueryBuilder instance;
//    
//    public static DHISQueryBuilder getInstance(String path) {
//       if (instanceMap == null) {
//           instanceMap = new HashMap<>();
//       }
//       if (!instanceMap.containsKey(path)) {
//           instanceMap.put(path, new DHISQueryBuilder(path));
//       }
//       return instanceMap.get(path);
//       
//    }
//
//    private DHISQueryBuilder(String path) {
//        req = Unirest.get(path);
//    }
//
//    public DHISQueryBuilder addQuery(String name, String value) {
//        req = req.queryString(name, value);
//        return this;
//    } 
//        
//    public DHISQueryBuilder addField(String name) {
//        return this;
//    } 
//}
