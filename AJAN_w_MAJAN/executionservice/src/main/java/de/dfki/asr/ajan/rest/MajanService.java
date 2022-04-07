/*
 * Copyright (C) 2022 Pivotal Software, Inc..
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
package de.dfki.asr.ajan.rest;

import de.dfki.asr.ajan.executionservice.ExecutionServiceApplication;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



/**
 *
 * @author Akbar Kazimov
 */
@RestController
public class MajanService {
    
    @RequestMapping(
            method = RequestMethod.POST,
            value = ExecutionServiceApplication.BASE_PATH + "/majanService/runJar",
            consumes = "text/plain",
            produces = "application/json"
    )
    public String runJarEndpoint(@RequestBody String body,
                               @RequestHeader(value = "jarPath") String jarPath,
                               @RequestHeader(value = "timeout") int timeout) {
        
        System.out.println("jar:" + jarPath);
        String inputFilePath = System.getProperty("user.dir") + 
                File.separator + "majanService" + 
                File.separator + "JarInput.txt";
        String outputFilePath = System.getProperty("user.dir") + 
                File.separator + "majanService" +
                File.separator + "JarOutput.txt";
        
        File inFile = null, outFile = null;
        
        try {
            inFile = createFileIfNotExists(inputFilePath);
            outFile = createFileIfNotExists(outputFilePath);
            writeToFile(inputFilePath, body);
        } catch (IOException ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR.toString() + "\nException Message: " + ex.getLocalizedMessage();
        }
        String jarResult = "";
        
        try {
            jarResult = runJar(jarPath, inputFilePath, outputFilePath, timeout);
            if(jarResult.toLowerCase().contains("exception in thread")){
                return "Internal JAR Error. \n Output of JAR is: " + jarResult;
            }
        } catch (InterruptedException | IOException ex) {
            return "Internal JAR Error. \n Exception Message: " + ex.getLocalizedMessage();
        }finally{
            removeFile(inFile);
        }
        String output = "";
        try {
            output = readFromFile(outputFilePath).trim();
        } catch (IOException ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR.toString() + "\nException Message: " + ex.getLocalizedMessage();
        }finally{
            removeFile(outFile);
        }
        if("".equals(output)){
            return jarResult;
        }
        return output;
    }
    
    private static File createFileIfNotExists(String path) throws IOException{
        File file = new File(path);
        //if(!file.isFile()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        //}
        return file;
    }
    private static void removeFile(File file){
        if(file != null)
            file.delete();
    }
    private static void writeToFile(String filePath, String input) throws IOException{
        FileWriter fileWriter = new FileWriter(new File(filePath));
        PrintWriter printWriter=new PrintWriter(fileWriter);
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Iterator<String> linesIter = reader.lines().iterator();
        while(linesIter.hasNext()) {
            printWriter.append(linesIter.next()).println();
        }
        printWriter.close();
    }
    
    private static String readFromFile(String filePath) throws FileNotFoundException, IOException {
        String result = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Iterator<String> linesIter = reader.lines().iterator();
            while(linesIter.hasNext()) {
                result += linesIter.next() + System.lineSeparator();
            }
        }
        return result;
    }
    
    private static String runJar(String jarPath, String inputPath, String outputPath, int timeout) throws InterruptedException, IOException{
        //String command = "java -jar \"" + jarPath + "\" \"" + inputPath + "\" \"" + outputPath + "\"";
       
        //String command = "java -jar " + jarPath + " " + inputPath + " " + outputPath;
        String result = "";
        Process pr = new ProcessBuilder("java", "-jar", jarPath, inputPath, outputPath).start();

//        Process pr = Runtime.getRuntime().exec(command);
        if(pr.waitFor(timeout, TimeUnit.SECONDS)){
            InputStream in = pr.getInputStream();
            StringBuilder output = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    output.append((char) c);
                }
            }
            result = output.toString();
            
            InputStream errorStream = pr.getErrorStream();
            StringBuilder error = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader(errorStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    error.append((char) c);
                }
            }
            result += error.toString();
        }else{
            result = "Algorithm didn't finish execution before timeout!";
        }
        return result;
    }
}
