/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.asr.ajan.pluginsystem.majanplugin.utils;

import java.util.List;

/**
 *
 * @author akka02
 */
public class CoalitionGeneratorUtils {
    
    public static boolean mlViolated(int[] coalitionInByte, List<int[]> mls) {
        boolean violated = false;
        
        for(int[] ml:mls) {
            int linked = 0;
            for(int i =0; i<ml.length;i++) {
                for(int j=0;j<coalitionInByte.length;j++) {
                    if(coalitionInByte[j]==(ml[i]+1)) {
                        linked+=1;
                        break;
                    }
                }
            }
            if(linked>0 && linked!=ml.length) {
                return true;
            }
        }
        return violated;
    }
    public static boolean clViolated(int[] coalitionInByte, List<int[]> cls) {
        boolean violated = false;
        for (int[] cl : cls) {
            int linked = 0;
            for (int i = 0; i < cl.length; i++) {
                for (int j = 0; j < coalitionInByte.length; j++) {
                    System.out.println("CLIB:"+j+" - " + coalitionInByte[j]);
                    System.out.println("CL:" + i + " - " + cl[i]);
                    if((cl[i]+1)==coalitionInByte[j]) {
                        linked+=1;
                    }
                }
            }
            if(linked>1) {
                return true;
            }
        }
        return violated;
    }
    public static int[] convertCombinationFromBitToByteFormat( int combinationInBitFormat, int numOfAgents ){
        int combinationSize = getSizeOfCombinationInBitFormat( combinationInBitFormat, numOfAgents);
        return( convertCombinationFromBitToByteFormat(combinationInBitFormat, numOfAgents, combinationSize) );
    }
    public static int[] convertCombinationFromBitToByteFormat( int combinationInBitFormat, int numOfAgents, int combinationSize ){
        int[] combinationInByteFormat = new int[ combinationSize ];
        int j=0;
        for(int i=0; i<numOfAgents; i++){
            if ((combinationInBitFormat & (1<<i)) != 0){
                combinationInByteFormat[j]= (int)(i+1);
                j++;
            }
        }
        return( combinationInByteFormat );
    }
    
    public static int getSizeOfCombinationInBitFormat( int combinationInBitFormat, int numOfAgents ){
        return( Integer.bitCount( combinationInBitFormat ) );
    }	
}
