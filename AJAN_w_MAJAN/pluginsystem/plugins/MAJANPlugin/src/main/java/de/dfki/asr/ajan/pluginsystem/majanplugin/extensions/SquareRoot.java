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
import de.dfki.asr.ajan.pluginsystem.majanplugin.exceptions.AjanMathInputException;
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
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
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
@RDFBean("bt:SquareRoot")
public class SquareRoot extends AbstractTDBLeafTask implements NodeExtension, TreeNode{
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SquareRoot.class);
    
    @Getter
    @Setter
    @RDFSubject
    private String url;
    
    @RDF("rdfs:label")
    @Getter @Setter
    private String label;
   
    @RDF("bt:query")
    @Getter @Setter
    private BehaviorConstructQuery query;
    
    @Override
    public Resource getType() {
        return MAJANVocabulary.SQUARE_ROOT_TYPE;
    }
           
    @Override
    public String toString() {
        return "SquareRoot (" + label + ")";
    }
    
    @Override
    public void end() {
        LOG.info("SquareRoot (" + getStatus() + ")");
    }
    
    @Override
    public EvaluationResult.Result simulateNodeLogic(final EvaluationResult result, final Resource root) {
        return EvaluationResult.Result.UNCLEAR;
    }
    
    @Override
    public LeafStatus executeLeaf() {
        try {
            if (compute()) {
                String report = toString() + " SUCCEEDED";
		LOG.info(report);
		return new LeafStatus(Status.SUCCEEDED, report);
            } else {
                String report = toString() + " FAILED";
		LOG.info(report);
		return new LeafStatus(Status.FAILED, report);
            }
        } catch (URISyntaxException | AjanMathInputException ex) {
            String report = toString() + " FAILED due to \""+ex.getMessage()+"\"";
            LOG.info(report);
            return new LeafStatus(Status.FAILED, report);
        }
    }
    
    private boolean compute() throws URISyntaxException, AjanMathInputException{
        Set<Resource> inputSubjects = null; 
        IRI resultPredicate = null;
        double value = 0.0;
        
        boolean respFlag = false;
        Repository repo = BTUtil.getInitializedRepository(this.getObject(), query.getOriginBase());
        Model modelResult = query.getResult(repo);
        
        //Utils.printRDF4JModel(modelResult, LOG);
        // Extract the Subjects  
        inputSubjects = modelResult.filter(null, org.eclipse.rdf4j.model.vocabulary.RDF.TYPE, 
                MAJANVocabulary.MATH_SUBJECT).subjects();
        if(inputSubjects.isEmpty()){
            throw new AjanMathInputException("No subject is specified (i.e. no subject exists for type "+
                    MAJANVocabulary.MATH_SUBJECT+")");
        } // end

        
        ModelBuilder builder=new ModelBuilder();

        for (Resource inputSubject : inputSubjects) {
            // Extract the Value whose the square root to be computed
            Set<Value> valueSet = modelResult.filter(inputSubject, MAJANVocabulary.MATH_HAS_VALUE, null).objects();
            if(!valueSet.isEmpty()){
                value=Double.valueOf(valueSet.iterator().next().stringValue());
            }else{
                LOG.warn("No value is given (i.e. no value exists for predicate "+
                        MAJANVocabulary.MATH_HAS_VALUE+"). Trying to proceed anyway");
               // throw new AjanMathInputException("No value is given (i.e. no value exists for predicate "+
               //         MAJANVocabulary.math_HAS_VALUE+")");
            } // end
            
            // Extract the Predicate to be given to the Result
            valueSet = modelResult.filter(inputSubject, MAJANVocabulary.MATH_HAS_RESULT_PREDICATE, null).objects();
            if(!valueSet.isEmpty() || !(valueSet.iterator().next() instanceof IRI)){
                resultPredicate = (IRI) valueSet.iterator().next();
            }else{
                LOG.warn("No Result Predicate is given (i.e. no value exists for predicate " +MAJANVocabulary.MATH_HAS_RESULT_PREDICATE+")."
                        + " Trying to proceed anyway." );
                //throw new AjanMathInputException("No Result Predicate is given (i.e. no value exists for predicate "+
            //       MAJANVocabulary.math_HAS_RESULT_PREDICATE+")");

            } // end
            
            if(resultPredicate == null){
                resultPredicate = MAJANVocabulary.FACTORY.createIRI(MAJANVocabulary.AJAN_NAMESPACE.toString(), "hasSquareRootValue");
            }
            double squareRoot = Math.sqrt(value);
            builder.subject(inputSubject)
                    .add(resultPredicate, squareRoot);
        }
        
        
        Model responseModel=builder.build();
        //Utils.printRDF4JModel(responseModel, LOG);
        if(query.getTargetBase().toString().equals(AJANVocabulary.EXECUTION_KNOWLEDGE.toString())){
            this.getObject().getExecutionBeliefs().update(responseModel);
        }else if(query.getTargetBase().toString().equals(AJANVocabulary.AGENT_KNOWLEDGE.toString())){
            this.getObject().getAgentBeliefs().update(responseModel);
        }else if(query.getTargetBase().toString().equals(AJANVocabulary.LOCAL_AGENTS_KNOWLEDGE.toString())){
            this.getObject().getLocalAgentsBeliefs().update(responseModel);
        }else if(query.getTargetBase().toString().equals(AJANVocabulary.LOCAL_SERVICES_KNOWLEDGE.toString())){
            this.getObject().getLocalServicesBeliefs().update(responseModel);
        }
        respFlag=true;
        
        return respFlag;
    }
}
