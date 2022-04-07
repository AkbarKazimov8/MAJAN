/*
 * Copyright (C) 2021 Pivotal Software, Inc..
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package de.dfki.asr.ajan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author akbar
 */
public class HttpMessage {
    private CloseableHttpClient httpClient = null;
    
    public HttpMessage(){
        this.httpClient = HttpClients.createDefault();
    }

    public void close() throws IOException {
        httpClient.close();
    }

    public String sendGet(String url, HashMap<String, String> headersMap) throws Exception {
        String response = null;
        HttpGet request = new HttpGet(url);

        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
            request.addHeader(entry.getKey(),entry.getValue());
        }
        
        try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {

            // Get HttpResponse Status
            System.out.println(httpResponse.getStatusLine().toString());
            
            HttpEntity entity = httpResponse.getEntity();
            
//            Header headers = response.getContentType();
            
  //          System.out.println(headers);
              response = EntityUtils.toString(entity);
        }
        
        return response; 
    }

    public String sendPost(String url, String content, HashMap<String, String> headersMap) throws Exception {

        HttpPost post = new HttpPost(url);
        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
            post.addHeader(entry.getKey(),entry.getValue());
        }
        post.setEntity(new StringEntity(content));
        
        try (CloseableHttpClient clHttpClient = HttpClients.createDefault();
             CloseableHttpResponse response = clHttpClient.execute(post)) {

            return EntityUtils.toString(response.getEntity());
        }

    }
}
