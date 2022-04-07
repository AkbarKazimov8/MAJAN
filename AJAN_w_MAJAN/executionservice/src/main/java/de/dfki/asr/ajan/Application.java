/*
 * Copyright (C) 2020 see AJAN-service/AUTHORS.txt (German Research Center for Artificial Intelligence, DFKI).
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

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings("PMD.UseUtilityClass")
public class Application {
    protected static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(final String... args) {
        SpringApplication.run(Application.class, args);
        
        sendNotification();        
    }
    
    private static void sendNotification(){
        String url = System.getProperty("notificationUrl");
        String[] headerKeys = System.getProperty("notificationHeaderKeys").split(",");
        String[] headerValues = System.getProperty("notificationHeaderValues").split(",");
        System.out.println("url:"+url);
            
        int n = headerKeys.length > headerValues.length ? headerValues.length : headerKeys.length ;
        HashMap<String, String> headers = new HashMap<>();
        for (int i = 0; i < n; i++) {
            headers.put(headerKeys[i], headerValues[i]);
            System.out.println("key:"+headerKeys[i]);
            System.out.println("value:"+headerValues[i]);
        }
        
        HttpMessage hm = new HttpMessage();
        String resp;
        try {
            //resp = hm.sendGet(url, headers);
            resp = hm.sendPost(url, "just a body", headers);
            System.out.println(resp);
            hm.close();
        } catch (Exception ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
