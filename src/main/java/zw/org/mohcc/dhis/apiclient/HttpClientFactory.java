/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.org.mohcc.dhis.apiclient;

import java.util.Map;

/**
 *
 * @author cliffordc
 */
public interface HttpClientFactory {

    public HttpClient getInstance(String username, String password, Map<String, String> headers, String toURLString);

}
