/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.asr.ajan.pluginsystem.majanplugin.vocabularies;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 *
 * @author root
 */
public class MAJANVocabulary {
        public final static ValueFactory FACTORY = SimpleValueFactory.getInstance();
        

        public final static IRI MAC_PROBLEM_INSTANCE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#MACProblemInstance");
        public final static IRI HAS_MAC_PROBLEM_ID = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasMacProblemId");
        public final static IRI HAS_NUMBER_OF_AGENTS = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasNumberOfAgents");
        public final static IRI HAS_NON_EXISTENT_COALITION_VALUE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasNonExistentCoalitionValue");
        public final static IRI HAS_MUST_LINK_CONNECTIONS = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasMustLinkConnections");
        public final static IRI HAS_MUST_CONNECT = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasMustConnect"); 
        public final static IRI HAS_CANNOT_LINK_CONNECTIONS = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasCannotLinkConnections");
        public final static IRI HAS_CANNOT_CONNECT = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasCannotConnect");        
        public final static IRI HAS_PARTICIPANTS = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasParticipants");        
        public final static IRI WELCOME_NAMESPACE = FACTORY.createIRI("https://raw.githubusercontent.com/gtzionis/WelcomeOntology/main/welcome.ttl#");
        public final static IRI AJAN_NAMESPACE = FACTORY.createIRI("http://www.ajan.de/ajan-ns#");
        public final static IRI MAC_NAMESPACE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#");
        public final static IRI HAS_MEMBERS = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasMembers");        
        public final static IRI HAS_SIZE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasSize");
        public final static IRI IS_MEMBER_OF = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#isMemberOf");
        public final static IRI BOSS_TYPE = FACTORY.createIRI("http://www.ajan.de/ajan-ns#CSGP_SOLVER-BOSS");
        public final static IRI CSGP_SOLVER_TYPE = FACTORY.createIRI("http://www.ajan.de/ajan-ns#CSGP_SOLVER");
        public final static IRI COALITION_GENERATOR_TYPE = FACTORY.createIRI("http://www.ajan.de/ajan-ns#CSGP-CoalitionGenerator");
        public final static IRI HAS_MIN_COALITION_SIZE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasMinCoalitionSize");
        public final static IRI HAS_MAX_COALITION_SIZE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasMaxCoalitionSize");        
        public final static IRI HAS_FEASIBLE_COALITIONS = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasFeasibleCoalitions"); 
        public final static IRI CSGP_COALITION = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#CSGP-Coalition");  
        public final static IRI CSGP_COALITION_STRUCTURE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#CSGP-CoalitionStructure");    
        
        public final static IRI HAS_VALUE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasValue");        
        public final static IRI HAS_SOLUTION = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasSolution"); 
        public final static IRI HAS_MIN_CS_VALUE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasMinCsValue");
        public final static IRI HAS_RANK = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasRank");  
        public final static IRI HAS_SOLUTION_OF = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasSolutionOf");        

        
        // HDBSCAN Vocabulary
        public final static IRI HDBSCAN_TYPE = FACTORY.createIRI("http://www.ajan.de/ajan-ns#Clustering_Solver-HDBSCAN");
        public final static IRI HAS_PERFECT_MATCH_SCORE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasPerfectMatchScore"); 
        
        public final static IRI IS_COMPUTED_BY = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#isComputedBy");
        public final static IRI IS_COMPUTED_AGAINST = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#isComputedAgainst");
        
        public final static IRI HAS_SIMILARITY_SCORE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasSimilarityScore");
        
        public final static IRI CLUSTERING = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#Clustering");
        public final static IRI CLUSTER = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#Cluster");
        public final static IRI IS_CLUSTER_OF = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#isClusterOf");
        public final static IRI HAS_MIN_POINTS_PARAM = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasMinPoints");
        public final static IRI HAS_MIN_CLUSTER_SIZE_PARAM = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasMinClusterSize");
        

        // Broadcast
        public final static IRI BROADCAST = FACTORY.createIRI("http://www.ajan.de/behavior/bt-ns#Broadcast");
        
        public final static IRI HAS_ID = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasId");
        public final static IRI IS_COMPUTED_FOR = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#isComputedFor");
        public final static IRI DISTANCE_SCORE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#DistanceScore");
        public final static IRI HAS_DISTANCE_SCORE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_mac_ontology#hasDistanceScore");
        
        // Math vocabulary
        public final static IRI SQUARE_ROOT_TYPE = FACTORY.createIRI("http://www.ajan.de/behavior/bt-ns#SquareRoot");
        public final static IRI MATH_SUBJECT = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_math_ontology#AJAN_MathSubject");
        public final static IRI MATH_HAS_VALUE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_math_ontology#hasValue");
        public final static IRI MATH_HAS_RESULT_PREDICATE = FACTORY.createIRI("http://localhost:8090/rdf4j/repositories/ajan_math_ontology#hasResultPredicate");
        

}
