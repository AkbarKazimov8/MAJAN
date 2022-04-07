/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.asr.ajan.pluginsystem.majanplugin.utils;

import de.dfki.asr.ajan.pluginsystem.majanplugin.exceptions.ConstraintsException;
import de.dfki.asr.ajan.pluginsystem.majanplugin.vocabularies.MAJANVocabulary;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.Models;
import org.slf4j.Logger;
import org.springframework.core.ConstantException;

/**
 *
 * @author akka02
 */
public class Utils {
    public static enum CONSTRAINT_TYPE {
		MUST_LINK,
		CANNOT_LINK
	}
    
    public static void printRDF4JModel(Model model, Logger logger){
        logger.info("Start--Statements--\nSize:"+model.size());
        model.forEach((statement) -> {
            logger.info(statement.toString());
        });
        logger.info("End--Statements--");
    }
    
    public static ArrayList<int[]> getConstraints(Model rdfInput, List<Value> agentNames, CONSTRAINT_TYPE type) throws ConstraintsException{
        ArrayList<int[]> constraintConnections = null;
        switch (type) {
            case MUST_LINK:
                constraintConnections = extractConstraints(rdfInput, agentNames, MAJANVocabulary.HAS_MUST_LINK_CONNECTIONS, MAJANVocabulary.HAS_MUST_CONNECT);
                //System.out.println("Must links:");
                break;
            case CANNOT_LINK:
                constraintConnections = extractConstraints(rdfInput, agentNames, MAJANVocabulary.HAS_CANNOT_LINK_CONNECTIONS, MAJANVocabulary.HAS_CANNOT_CONNECT);
                //System.out.println("Cannot links:");
                break;
            default:
                throw new ConstraintsException("No valid input is given as the type of constraint. Either "+CONSTRAINT_TYPE.MUST_LINK + " or "
                        +CONSTRAINT_TYPE.CANNOT_LINK +" must be specified");
        }
        printConstraints(constraintConnections, agentNames);
        
        return constraintConnections;
    }
    
    public static void printConstraints(ArrayList<int[]> constraintConnections, List<Value> agentNames){
        for (int[] cl : constraintConnections) {
                    System.out.println("pair1:"+cl[0] + ", pair2:"+cl[1]);
                    System.out.println("agent1:"+agentNames.get(cl[0]) + ", agent2:"+agentNames.get(cl[1]));
                }
    }
    private static ArrayList<int[]> extractConstraints(Model rdfInput, List<Value> agentNames, IRI firstIri, IRI secondIri) throws ConstraintsException{
        ArrayList<int[]> constraintConnections = new ArrayList<>();

        Set<Resource> bNodesAsSubject = Models.objectResources(rdfInput.filter(null, firstIri, null));

        for(Resource rsr:bNodesAsSubject){
            Set<Value> agentPair=rdfInput.filter(rsr, secondIri, null).objects();
            
            if(agentPair.size()!=2){
                throw new ConstraintsException("More or Less than 2 (" + agentPair.size()+") number of agents are "
                        + "specified as MustBeLinked in the following subject:"+rsr.toString());
            }
            Iterator<Value> iterator = agentPair.iterator();
            constraintConnections.add(new int[]{agentNames.indexOf(iterator.next()), agentNames.indexOf(iterator.next())});
        }
        
        return constraintConnections;
    }

}
