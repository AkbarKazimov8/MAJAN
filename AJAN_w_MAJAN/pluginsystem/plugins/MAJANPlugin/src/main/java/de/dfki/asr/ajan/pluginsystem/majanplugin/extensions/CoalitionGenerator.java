/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.asr.ajan.pluginsystem.majanplugin.extensions;

import com.google.common.collect.BiMap;
import de.dfki.asr.ajan.behaviour.nodes.common.AbstractTDBLeafTask;
import de.dfki.asr.ajan.behaviour.nodes.common.BTUtil;
import de.dfki.asr.ajan.behaviour.nodes.common.EvaluationResult;
import de.dfki.asr.ajan.behaviour.nodes.common.LeafStatus;
import de.dfki.asr.ajan.behaviour.nodes.query.BehaviorConstructQuery;
import de.dfki.asr.ajan.common.AJANVocabulary;
import de.dfki.asr.ajan.pluginsystem.extensionpoints.NodeExtension;
import de.dfki.asr.ajan.pluginsystem.majanplugin.exceptions.CoalitionGenerationInputException;
import de.dfki.asr.ajan.pluginsystem.majanplugin.exceptions.ConstraintsException;
import de.dfki.asr.ajan.pluginsystem.majanplugin.utils.CoalitionGeneratorUtils;
import de.dfki.asr.ajan.pluginsystem.majanplugin.utils.Utils;
import de.dfki.asr.ajan.pluginsystem.majanplugin.vocabularies.MAJANVocabulary;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.Extension;

/**
 *
 * @author Akbar
 */
@Extension
@RDFBean("bt:CoalitionGenerator")
public class CoalitionGenerator extends AbstractTDBLeafTask implements NodeExtension{
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(CoalitionGenerator.class);
    
    @Getter
    @Setter
    @RDFSubject
    private String url;
    
    @RDF("rdfs:label")
    @Getter @Setter
    private String label;
   
    @RDF("bt:query")
    @Getter @Setter
    private BehaviorConstructQuery coalitionGeneratorInputQuery;
    
    @Override
    public Resource getType() {
        return MAJANVocabulary.COALITION_GENERATOR_TYPE;
    }
           
    @Override
    public String toString() {
        return "CoalitionGenerator (" + label + ")";
    }
    
    @Override
    public void end() {
        LOG.info("CoalitionGenerator (" + getStatus() + ")");
    }
    
    @Override
    public EvaluationResult.Result simulateNodeLogic(final EvaluationResult result, final Resource root) {
        return EvaluationResult.Result.UNCLEAR;
    }
    
    @Override
    public LeafStatus executeLeaf() {
        try {
            if (generateCoalitions()) {
                String report = toString() + " SUCCEEDED";
		LOG.info(report);
		return new LeafStatus(Status.SUCCEEDED, report);
            } else {
                String report = toString() + " FAILED";
		LOG.info(report);
		return new LeafStatus(Status.FAILED, report);
            }
        } catch (URISyntaxException | ConstraintsException | CoalitionGenerationInputException ex) {
            String report = toString() + " FAILED due to \""+ex.getMessage()+"\"";
            LOG.info(report);
            return new LeafStatus(Status.FAILED, report);
        }
    }
    
    private boolean generateCoalitions() throws URISyntaxException, ConstraintsException, CoalitionGenerationInputException {
        boolean responseFlag = false;
        int numOfAgents=0, minCoalitionSize=0, maxCoalitionSize=0;
        String lccId = "";
        List<int[]> mlList = null, clList = null;
        List<Value> agentNames=new ArrayList<>();
        
        Repository repo = BTUtil.getInitializedRepository(this.getObject(), coalitionGeneratorInputQuery.getOriginBase());
        Model rdfInputModel = coalitionGeneratorInputQuery.getResult(repo);
        
       // Utils.printRDF4JModel(modelResult, LOG);
      /*  Iterator<Statement> itmodelResult = modelResult.iterator();
        while(itmodelResult.hasNext()){
            LOG.info("Statement: " + itmodelResult.next().toString());
        }*/

        // Extract the LCC Problem Instance subject bnode. There should be only 1 problem instance because 
        // the algorithm cannot run multiple configurations at the same time. 
        Set<Resource> subjects = rdfInputModel.filter(null, org.eclipse.rdf4j.model.vocabulary.RDF.TYPE, 
                MAJANVocabulary.MAC_PROBLEM_INSTANCE).subjects();
        Resource macUseCaseSubject=null;
        if(!subjects.isEmpty()){
            macUseCaseSubject=subjects.iterator().next();
        }else{
            throw new CoalitionGenerationInputException("No problem instance is specified (i.e. no subject exists for type "+
                    MAJANVocabulary.MAC_PROBLEM_INSTANCE+")");
        } // end


        // Extract id of Mac Problem Instance from Model
        Set<Value> valueSet = rdfInputModel.filter(null, MAJANVocabulary.HAS_ID, null).objects();
        if(!valueSet.isEmpty()){
            lccId = valueSet.iterator().next().stringValue();
        }else{
            throw new CoalitionGenerationInputException("Mac Use Case Id is not given (i.e. no value exists for predicate "+
                    MAJANVocabulary.HAS_ID+")");
        } // end

        // Extract NumOfAgents from Model
        valueSet = rdfInputModel.filter(null, MAJANVocabulary.HAS_NUMBER_OF_AGENTS, null).objects();
        if(!valueSet.isEmpty()){
            numOfAgents=Integer.valueOf(valueSet.iterator().next().stringValue());
        }else{
            throw new CoalitionGenerationInputException("Number of agents is not given (i.e. no value exists for predicate "+
                    MAJANVocabulary.HAS_NUMBER_OF_AGENTS+")");
        } // end

        // Extract Agent Names from Model
        valueSet=rdfInputModel.filter(null, MAJANVocabulary.HAS_PARTICIPANTS, null).objects();
        if(valueSet.size()!=numOfAgents){
            throw new CoalitionGenerationInputException("Amount of participating agents ("+valueSet.size()+") is different "
                    + "than the given \"numberOfAgents\" ("+numOfAgents+") value.");
        }
        Iterator<Value> valueIterator = valueSet.iterator();
        while(valueIterator.hasNext()){
            agentNames.add(valueIterator.next());
        } // end
        
        // Extract Minimum and Maximum Coalition Size from Model
        valueSet = rdfInputModel.filter(null, MAJANVocabulary.HAS_MIN_COALITION_SIZE, null).objects();
        if(!valueSet.isEmpty()){
            minCoalitionSize=Integer.valueOf(valueSet.iterator().next().stringValue());
        }else{
            throw new CoalitionGenerationInputException("Minimum Coalition Size is not given (i.e. no value exists for predicate "+
                    MAJANVocabulary.HAS_MIN_COALITION_SIZE+")");
        }
        valueSet = rdfInputModel.filter(null, MAJANVocabulary.HAS_MAX_COALITION_SIZE, null).objects();
        if(!valueSet.isEmpty()){
            maxCoalitionSize=Integer.valueOf(valueSet.iterator().next().stringValue());
        }else{
            throw new CoalitionGenerationInputException("Maximum Coalition Size is not given (i.e. no value exists for predicate "+
                    MAJANVocabulary.HAS_MAX_COALITION_SIZE+")");
        } // end

        // Extract Must Link Connections
        mlList = Utils.getConstraints(rdfInputModel, agentNames, Utils.CONSTRAINT_TYPE.MUST_LINK); 
            
        // Extract Cannot Link Connections
        clList = Utils.getConstraints(rdfInputModel, agentNames, Utils.CONSTRAINT_TYPE.CANNOT_LINK);
                
        
        
        // Print the result
       /* System.out.println("Results:\nnum:"+numOfAgents);
        System.out.println("min:"+minCoalitionSize);
        System.out.println("max:"+maxCoalitionSize);
        

        for(int i=0;i<agentNames.size();i++){
            System.out.println("Agent "+(i+1)+" is "+agentNames.get(i));
        }
        
        for (int[] is : mlList) {
            System.out.println("ML1:"+is[0]);
            System.out.println("ML2:"+is[1]);
        }
        
        for (int[] is : clList) {
            System.out.println("CL1:"+is[0]);
            System.out.println("CL2:"+is[1]);
        }*/
        

        
        List<int[]> feasibleCoalitions=generateCoalitions(numOfAgents, mlList, clList, minCoalitionSize, maxCoalitionSize);
       
       // System.out.println("Feasible Coalitions Amount is "+feasibleCoalitions.size());
        ModelBuilder builder=new ModelBuilder();
        builder.setNamespace("welcome", MAJANVocabulary.WELCOME_NAMESPACE.toString());
        
        // Adding Coalitions to the Problem Instance Subject
        for(int i=0;i<feasibleCoalitions.size();i++){
            //BNode coalitionBnode = MAJANVocabulary.FACTORY.createBNode();
            Resource coalitionRsr = MAJANVocabulary.FACTORY.createIRI(MAJANVocabulary.WELCOME_NAMESPACE.toString() +lccId +"coalition"+i); 
            builder.subject(macUseCaseSubject)
                    .add(MAJANVocabulary.HAS_FEASIBLE_COALITIONS, coalitionRsr)
                    .subject(coalitionRsr)
                    .add(org.eclipse.rdf4j.model.vocabulary.RDF.TYPE, MAJANVocabulary.CSGP_COALITION);
                    //.add(MAJANVocabulary.CsgpValuePre, ThreadLocalRandom.current().nextDouble(-5,5));
        
            
            // Adding Agents to Coalitions
            for(int j=0;j<feasibleCoalitions.get(i).length;j++){
               // builder.add(MAJANVocabulary.MembersPre, 
                    //    MAJANVocabulary.FACTORY.createIRI(agentNames.get(feasibleCoalitions.get(i)[j]-1)));
                builder.add(MAJANVocabulary.HAS_MEMBERS, agentNames.get(feasibleCoalitions.get(i)[j]-1));
            }
            // Adding size of coalition
            builder.add(MAJANVocabulary.HAS_SIZE, feasibleCoalitions.get(i).length);
            
            // TODO: demeli bele. birinci agentlarin irilarinnan list yarat. sonra hemin listi bir bNoda elave ele. 
            // sonra hemin bNodu :coalition1 :members :bNode kimi modele at. 
            // sonra coalition irilarini liste at. hemin listden RDF list yarat ve onu :LCC1 :feasibleCoalitions bNode kimi modele at.
            // even if I do this, I will still need to iterate a lot to find out the correct coalittion Rsr. That is why, just do it
            
        }
        
        Model responseModel=builder.build();
       // Utils.printRDF4JModel(responseModel, LOG);
        if(coalitionGeneratorInputQuery.getTargetBase().toString().equals(AJANVocabulary.EXECUTION_KNOWLEDGE.toString())){
            this.getObject().getExecutionBeliefs().update(responseModel);
        }else if(coalitionGeneratorInputQuery.getTargetBase().toString().equals(AJANVocabulary.AGENT_KNOWLEDGE.toString())){
            this.getObject().getAgentBeliefs().update(responseModel);
        }else if(coalitionGeneratorInputQuery.getTargetBase().toString().equals(AJANVocabulary.LOCAL_AGENTS_KNOWLEDGE.toString())){
            this.getObject().getLocalAgentsBeliefs().update(responseModel);
        }else if(coalitionGeneratorInputQuery.getTargetBase().toString().equals(AJANVocabulary.LOCAL_SERVICES_KNOWLEDGE.toString())){
            this.getObject().getLocalServicesBeliefs().update(responseModel);
        }

        responseFlag=true;
        return responseFlag;
    }
    
    public List<int[]> generateCoalitions(int numOfAgents, List<int[]> mlList, List<int[]> clList, int minSize, int maxSize) {
        List<int[]> coalitions=new ArrayList<>();
        
        for(int i=1;i <Math.pow(2, numOfAgents);i++) {
            int[] coalitionInByte = CoalitionGeneratorUtils.convertCombinationFromBitToByteFormat(i, numOfAgents);
            
            if(coalitionInByte.length<minSize ||
                    coalitionInByte.length>maxSize ||
                    CoalitionGeneratorUtils.clViolated(coalitionInByte, clList) ||
                    CoalitionGeneratorUtils.mlViolated(coalitionInByte, mlList)) {
                continue;
            }
            coalitions.add(coalitionInByte);
        }
        return coalitions;
    }
}
