/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.asr.ajan.pluginsystem.majanplugin.extensions;
import de.dfki.asr.ajan.behaviour.nodes.common.AbstractTDBLeafTask;
import de.dfki.asr.ajan.behaviour.nodes.common.BTUtil;
import de.dfki.asr.ajan.behaviour.nodes.common.EvaluationResult;
import de.dfki.asr.ajan.behaviour.nodes.common.LeafStatus;
import de.dfki.asr.ajan.behaviour.nodes.common.TreeNode;
import de.dfki.asr.ajan.behaviour.nodes.query.BehaviorAskQuery;
import de.dfki.asr.ajan.behaviour.nodes.query.BehaviorConstructQuery;
import de.dfki.asr.ajan.common.AJANVocabulary;
import de.dfki.asr.ajan.pluginsystem.extensionpoints.NodeExtension;
import de.dfki.asr.ajan.pluginsystem.majanplugin.exceptions.CSGPSolverInputException;
import de.dfki.asr.ajan.pluginsystem.majanplugin.exceptions.CoalitionGenerationInputException;
import de.dfki.asr.ajan.pluginsystem.majanplugin.exceptions.ConstraintsException;
import de.dfki.asr.ajan.pluginsystem.majanplugin.utils.Utils;
import de.dfki.asr.ajan.pluginsystem.majanplugin.vocabularies.MAJANVocabulary;
import general.Combinations;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.sparqlbuilder.core.Prefix;
import org.eclipse.rdf4j.sparqlbuilder.core.SparqlBuilder;
import org.eclipse.rdf4j.sparqlbuilder.core.Variable;
import org.eclipse.rdf4j.sparqlbuilder.core.query.ConstructQuery;
import org.eclipse.rdf4j.sparqlbuilder.core.query.Queries;
import org.eclipse.rdf4j.sparqlbuilder.core.query.SelectQuery;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.TriplePattern;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.Extension;

/**
 *
 * @author Akbar
 */
@Extension
@RDFBean("bt:BOSS")
public class BOSS extends CSGPSolver implements NodeExtension, TreeNode{
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BOSS.class);
    
    @Getter
    @Setter
    @RDFSubject
    public String url;
    
    @RDF("rdfs:label")
    @Getter @Setter
    private String label;
   
    @RDF("bt:query")
    @Getter @Setter
    private BehaviorConstructQuery csgpInputQuery;
    
    @Override
    public Resource getType() {
        return MAJANVocabulary.BOSS_TYPE;
    }
           
    @Override
    public String toString() {
        return "BOSS (" + label + ")";
    }
    
    @Override
    public void end() {
        LOG.info("BOSS (" + getStatus() + ")");
    }
    
    @Override
    public EvaluationResult.Result simulateNodeLogic(final EvaluationResult result, final Resource root) {
        return EvaluationResult.Result.UNCLEAR;
    }
    
    @Override
    public LeafStatus executeLeaf() {
        try {
            if (solve()) {
                String report = toString() + " SUCCEEDED";
		LOG.info(report);
		return new LeafStatus(Status.SUCCEEDED, report);
            } else {
                String report = toString() + " FAILED";
		LOG.info(report);
		return new LeafStatus(Status.FAILED, report);
            }
        } catch (URISyntaxException | CSGPSolverInputException ex) {
            String report = toString() + " FAILED due to \""+ex.getMessage()+"\"";
            LOG.info(report);
            return new LeafStatus(Status.FAILED, report);
        }
    }
    @Override
    public Map<int[][], Double[]> runSolver(int numOfAgents, Map<int[], Double> coalitionsData, double infeasibleCoalitionValue){
        LOG.info("BOSS started!");
        csgpSolver.BOSS boss = new csgpSolver.BOSS();
        Map<int[][], Double[]> solutions = boss.execute(numOfAgents, coalitionsData, infeasibleCoalitionValue);
        LOG.info("BOSS finished!");
        return solutions;
    }
    
    @Override
    public boolean solve() throws URISyntaxException, CSGPSolverInputException {
        // double nonexistentCoalitionValue = ?
        int numOfAgents;
        List<Value> agentNames = new ArrayList<>();
        // coalition in byte as Key and coalition value as Value of the map
        Map<int[], Double> coalitionsData = new HashMap<>();
        double infeasibleCoalitionValue = -1000000;
        boolean respFlag = false;
        Repository repo = BTUtil.getInitializedRepository(this.getObject(), csgpInputQuery.getOriginBase());
        Model modelResult = csgpInputQuery.getResult(repo);

        //Utils.printRDF4JModel(modelResult, LOG);
        
        // Extract the Problem Instance. There should be only 1 problem instance because 
        // the algorithm cannot run multiple configurations at the same time. 
        Set<Resource> subjects = modelResult.filter(null, org.eclipse.rdf4j.model.vocabulary.RDF.TYPE, 
                MAJANVocabulary.MAC_PROBLEM_INSTANCE).subjects();
        Resource problemInstance_subject=null;
        if(!subjects.isEmpty()){
            problemInstance_subject=subjects.iterator().next();
        }else{
            throw new CSGPSolverInputException("No problem instance is specified (i.e. no subject exists for type "+
                    MAJANVocabulary.MAC_PROBLEM_INSTANCE+")");
        } // end
       // LOG.info("lcc_subject:"+problemInstance_subject);
        
        // Extract id of Mac Problem Instance from Model
        Set<Value> valueSet = modelResult.filter(problemInstance_subject, MAJANVocabulary.HAS_ID, null).objects();
        String macProblemId=null;
        if(!valueSet.isEmpty()){
            macProblemId=valueSet.iterator().next().stringValue();
        }else{
            throw new CSGPSolverInputException("Mac Problem Id is not given (i.e. no value exists for predicate "+
                    MAJANVocabulary.HAS_ID+")");
        } // end

        // Extract value for infeasible coalitions from Model
        valueSet = modelResult.filter(problemInstance_subject, MAJANVocabulary.HAS_NON_EXISTENT_COALITION_VALUE, null).objects();
        if(!valueSet.isEmpty()){
            infeasibleCoalitionValue=Double.valueOf(valueSet.iterator().next().stringValue());
        }else{
            throw new CSGPSolverInputException("Default value for infeasible coalitions is not given (i.e. no value exists for predicate "+
                    MAJANVocabulary.HAS_NON_EXISTENT_COALITION_VALUE+")");
        } // end
       // LOG.info("id:"+macProblemId);
        
        // Extract NumOfAgents from Model
        valueSet = modelResult.filter(problemInstance_subject, MAJANVocabulary.HAS_NUMBER_OF_AGENTS, null).objects();
        if(!valueSet.isEmpty()){
            numOfAgents=Integer.valueOf(valueSet.iterator().next().stringValue());
        }else{
            throw new CSGPSolverInputException("Number of agents is not given (i.e. no value exists for predicate "+
                    MAJANVocabulary.HAS_NUMBER_OF_AGENTS+")");
        } // end
       // LOG.info("numOfAgents:"+numOfAgents);
        
        // Extract Agent Names from Model
        valueSet=modelResult.filter(problemInstance_subject, MAJANVocabulary.HAS_PARTICIPANTS, null).objects();
        if(valueSet.size()!=numOfAgents){
            throw new CSGPSolverInputException("Amount of participating agents is different "
                    + "from the given \"numberOfAgents\" information.");
        }
        Iterator<Value> valueIterator = valueSet.iterator();
        while(valueIterator.hasNext()){
            agentNames.add(valueIterator.next());
        } // end
        
       // for(Value agentId:agentNames){
      //      LOG.info("agentName:"+agentId);
      //  }
      //  LOG.info("pref:" + modelResult.filter(problemInstance_subject, MAJANVocabulary.FeasibleCoalitionsPre, null).size());
        // Extract Coalition Values from Model
        Set<Resource> coalitionsAsResource = Models.objectResources(modelResult.filter(problemInstance_subject, 
                MAJANVocabulary.HAS_FEASIBLE_COALITIONS, null));
      //  LOG.info("coalitions size:"+coalitionsAsResource.size());
        Resource[] coalitionRsrs = new Resource[(int)Math.pow(2,numOfAgents)];
        for(Resource coalitionRsr:coalitionsAsResource){
            Set<Value> memberAgents=modelResult.filter(coalitionRsr, MAJANVocabulary.HAS_MEMBERS, null).objects();
         //   LOG.info("members:"+memberAgents.size());
            int[] coalitionInByte = new int[memberAgents.size()];
            
            // Extract Coalition value from Model
            valueSet = modelResult.filter(coalitionRsr, MAJANVocabulary.HAS_VALUE, null).objects();
            double coalitionValue=0;
            if(valueSet.isEmpty()){
                continue;
            }else{
                coalitionValue=Double.valueOf(valueSet.iterator().next().stringValue());
            } // end
            
          //  LOG.info("Coalition:"+coalitionRsr);
         //   LOG.info("Its Value:"+coalitionValue);
            // Create Coalitions in Byte Format
            Iterator<Value> itMA = memberAgents.iterator();
            for (int i = 0; i < coalitionInByte.length; i++) {
                coalitionInByte[i] = agentNames.indexOf(itMA.next())+1;
            } // end
            
            // coalition in byte as Key and coalition value as Value of the map
            coalitionsData.put(coalitionInByte, coalitionValue);
            coalitionRsrs[Combinations.convertCombinationFromByteToBitFormat(coalitionInByte)]=coalitionRsr;

        } // end

        Map<int[][], Double[]> solutions = runSolver(numOfAgents, coalitionsData, infeasibleCoalitionValue);
        
       /* LOG.info("Solution size: " + solutions.size());
        for (Map.Entry<int[][], Double[]> solution : solutions.entrySet()) {
            LOG.info("Rank: " + solution.getValue()[1] + " CS: " + general.General.convertArrayToString(solution.getKey()) + 
                    " Value: " + solution.getValue()[0]);
        }*/
        
        // System.out.println("Feasible Coalitions Amount is "+feasibleCoalitions.size());
        ModelBuilder builder=new ModelBuilder();
        builder.setNamespace("welcome", MAJANVocabulary.WELCOME_NAMESPACE.toString())
                .setNamespace("mac", MAJANVocabulary.MAC_NAMESPACE.toString());

        double minimumCsValue = 5.0;
        // Adding Coalition Structures to the Problem Instance Subject as Solution
        for (Map.Entry<int[][], Double[]> solution : solutions.entrySet()) {
            if(solution.getValue()[0]<=((infeasibleCoalitionValue*3)/4)){
                continue;
            }
            // Storing only top 3 solutions
            if(solution.getValue()[1]>3){
               continue;
            }
            if(solution.getValue()[0] < minimumCsValue){
                minimumCsValue = solution.getValue()[0];
            }
           // System.out.println("Rank: " + solution.getValue()[1] + " CS: " + general.General.convertArrayToString(solution.getKey()) + 
           //         " Value: " + solution.getValue()[0]);
            int rank = solution.getValue()[1].intValue();
            String solutionStr = "mac:" + macProblemId + "_CS" + rank;  
            builder.subject(problemInstance_subject)
                    .add(MAJANVocabulary.HAS_SOLUTION, solutionStr)
                    .subject(solutionStr)
                    .add(org.eclipse.rdf4j.model.vocabulary.RDF.TYPE, MAJANVocabulary.CSGP_COALITION_STRUCTURE)
                    .add(MAJANVocabulary.HAS_VALUE, solution.getValue()[0])
                    .add(MAJANVocabulary.HAS_RANK, solution.getValue()[1])
                    .add(MAJANVocabulary.HAS_SOLUTION_OF, problemInstance_subject);

            // Adding Coalitions to CSs
            for(int[] coalition:solution.getKey()){
                int coalitionInBit = Combinations.convertCombinationFromByteToBitFormat(coalition);
                // The solution of BOSS will only consist of the coalitions that it is given as "feasible". 
                // Therefore, this if condition should always be true. However, if boss returns a coalition that is not feasible 
                // because of the hard constraints, then algorithm will stop adding this particular solution to model which means,
                // some coalitions of this particular CS can be added to model but the CS will not be complete. 
                if(coalitionRsrs[coalitionInBit]!=null){
                    builder.add(MAJANVocabulary.HAS_MEMBERS,
                            coalitionRsrs[coalitionInBit]);       
                }else{
                    break;
                }
            } // end
        }
        builder.subject(problemInstance_subject)
                .add(MAJANVocabulary.HAS_MIN_CS_VALUE, minimumCsValue);
        Model responseModel=builder.build();
       // Utils.printRDF4JModel(responseModel, LOG);
        if(csgpInputQuery.getTargetBase().toString().equals(AJANVocabulary.EXECUTION_KNOWLEDGE.toString())){
            this.getObject().getExecutionBeliefs().update(responseModel);
        }else if(csgpInputQuery.getTargetBase().toString().equals(AJANVocabulary.AGENT_KNOWLEDGE.toString())){
            this.getObject().getAgentBeliefs().update(responseModel);
        }else if(csgpInputQuery.getTargetBase().toString().equals(AJANVocabulary.LOCAL_AGENTS_KNOWLEDGE.toString())){
            this.getObject().getLocalAgentsBeliefs().update(responseModel);
        }else if(csgpInputQuery.getTargetBase().toString().equals(AJANVocabulary.LOCAL_SERVICES_KNOWLEDGE.toString())){
            this.getObject().getLocalServicesBeliefs().update(responseModel);
        }
        respFlag=true;
        return respFlag;
    }
}
