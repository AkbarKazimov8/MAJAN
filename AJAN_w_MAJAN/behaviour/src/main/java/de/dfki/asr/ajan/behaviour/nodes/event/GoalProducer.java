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

package de.dfki.asr.ajan.behaviour.nodes.event;

import de.dfki.asr.ajan.behaviour.events.*;
import de.dfki.asr.ajan.behaviour.exception.AJANBindingsException;
import de.dfki.asr.ajan.behaviour.exception.ConditionEvaluationException;
import de.dfki.asr.ajan.behaviour.exception.EventEvaluationException;
import de.dfki.asr.ajan.behaviour.nodes.BTRoot;
import de.dfki.asr.ajan.behaviour.nodes.common.*;
import de.dfki.asr.ajan.behaviour.nodes.common.EvaluationResult.Result;
import de.dfki.asr.ajan.behaviour.nodes.query.BehaviorConstructQuery;
import de.dfki.asr.ajan.common.SPARQLUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RDFBean("bt:GoalProducer")
public class GoalProducer extends AbstractTDBLeafTask implements Producer {
	@Getter @Setter
	@RDFSubject
	private String url;

	@RDF("rdfs:label")
	@Getter @Setter
	private String label;

	@RDF("ajan:goal")
	@Getter @Setter
	private URI goalURI;

	@RDF("bt:content")
	@Getter @Setter
	private BehaviorConstructQuery query;

	private AJANGoal goal;
	private Status goalStatus = Status.FRESH;
	private static final Logger LOG = LoggerFactory.getLogger(GoalProducer.class);

	@Override
	public Resource getType() {
		return BTVocabulary.GOAL_PRODUCER;
	}

	@Override
	@SuppressWarnings("PMD.ConfusingTernary")
	public LeafStatus executeLeaf() {
            System.out.println("Girdiiiiiiiiii");
		Status exStatus;
		if (goalStatus.equals(Status.FRESH)) {
			goalStatus = Status.RUNNING;
			exStatus = Status.RUNNING;
			try {
				produceGoal();
			} catch (EventEvaluationException | AJANBindingsException | URISyntaxException | ConditionEvaluationException ex) {
				LOG.info(toString(), ex);
				return new LeafStatus(Status.FAILED, toString() + " FAILED");
			}
		} else if (!goalStatus.equals(Status.RUNNING)) {
                    exStatus = goalStatus;
                    goalStatus = Status.FRESH;
		} else {
			exStatus = Status.RUNNING;
     
                }
		printStatus(exStatus);
		return new LeafStatus(exStatus, toString() + " " + exStatus);
	}

	private void printStatus(final Status status) {
		switch (status) {
			case RUNNING:
				LOG.info(toString() + " RUNNING");
				break;
			case SUCCEEDED:
				LOG.info(toString() + " SUCCEEDED");
				break;
			default:
				LOG.info(toString() + " FAILED");
				break;
		}
	}

	private void produceGoal() throws EventEvaluationException, AJANBindingsException, URISyntaxException, ConditionEvaluationException {
            System.out.println("1111111111111111111111");
            Object info = this.getObject().getEventInformation();
            if (info instanceof ModelEventInformation) {
                ModelEventInformation eventInfo = (ModelEventInformation) info;
                Model model = eventInfo.getModel();
                System.out.println("goal producer model 1 ----");
                for(Statement stm: model){
                    System.out.println(stm);
                }
            }
		            System.out.println("222222222222222222222");

            Map<URI,Event> events = this.getObject().getEvents();
		if (events.containsKey(goalURI)) {
			if (events.get(goalURI) instanceof AJANGoal) {
                                        System.out.println("33333333333333333333333");

				goal = (AJANGoal)events.get(goalURI);
                                            System.out.println("44444444444444444444");

				createGoalEvent(goal);
                                            System.out.println("5555555555555555555");

			} else {
				throw new EventEvaluationException("Event is no AJANGoal");
			}
		} else {
			throw new EventEvaluationException("No such AJANGoal defined");
		}
	}

	private void createGoalEvent(final AJANGoal goal) throws AJANBindingsException, URISyntaxException, ConditionEvaluationException {
                Model model = getModel();
                LOG.info("Statements:--------------------");
                for(Statement stm: model){
                    LOG.info(stm.toString());
                }
                            System.out.println("66666666666666666666");

                if(checkPrecondition(model)){
                                System.out.println("7777777777777777777777");

// i have been using
//                         goal.setEventInformation(this, model);
                    goal.setEventInformation(this, new GoalInformation(model));
                                System.out.println("88888888888888888888888");

		} else {
			throw new AJANBindingsException("Input model failed to conform to Goal precondition");
		}
	}
        
	private Model getModel() throws ConditionEvaluationException {
		try {
			Repository repo = BTUtil.getInitializedRepository(getObject(), query.getOriginBase());
                        
                        Model model = query.getResult(repo);
                        return model;
		} catch (URISyntaxException | QueryEvaluationException ex) {
			throw new ConditionEvaluationException(ex);
		}
	}
        
        private boolean checkPrecondition(final Model model) throws ConditionEvaluationException {
            String precondition = goal.getPrecondition();
                        System.out.println("99999999999999999999999");

            if (!SPARQLUtil.askModel(model, precondition)) {
                            System.out.println("1010101010101010");

			LOG.error("Input model failed to conform to Goal precondition.");
			return false;
		}
            return true;
        }
        
	public boolean checkPostCondition() throws AJANBindingsException, URISyntaxException {
		String condition = goal.getPostcondition(); 
		Repository repo = this.getObject().getAgentBeliefs().getInitializedRepository();
		try (RepositoryConnection conn = repo.getConnection()) {
			BooleanQuery query = conn.prepareBooleanQuery(condition);
			return query.evaluate();
                }
        }

	@Override
	public void reportGoalStatus(final Status result) {
            //System.out.println("Goal status:---" + result.toString());
		goalStatus = result;
		if (goalStatus.equals(Status.RUNNING)) {
			return;
		}
		try {
                    // !!! ADDED "&& goalStatus.equals(Status.SUCCEEDED)" WHICH REQUIRES THAT BOTH OF 
                    // POSTCONDITION AND THE BT WHICH WAS TRIGGERED BY THIS GOAL SHOULD SUCCEED !!!
                    // WITHOUT THIS ADDITION, THE GOAL NODE SUCCEEDED EVEN THOUGH ITS BT FAILED. 
			if (checkPostCondition() && goalStatus.equals(Status.SUCCEEDED)) {
				goalStatus = Status.SUCCEEDED;
			} else {
				goalStatus = Status.FAILED;
			}
			ModelEvent event = createEvent();
			event.setEventInformation(new LinkedHashModel());
		} catch (AJANBindingsException | URISyntaxException ex) {
			LOG.error("Error while creating an Event: " + ex);
			goalStatus = Status.FAILED;
		}
	}

	@Override
	public boolean goalIsRunning() {
		return goalStatus.equals(Status.RUNNING);
	}

	private ModelEvent createEvent() throws URISyntaxException {
		ModelEvent event = new ModelEvent();
		String eventName = "e_" + label;
		event.setName(eventName);
		event.setUrl(url + "#" + eventName);
		event.register(this.getObject().getBt());
		this.getObject().getEvents().put(new URI(url), event);
		return event;
	}

	@Override
	public void end() {
		LOG.info("Status (" + getStatus() + ")");
	}

	@Override
	public String toString() {
		return "GoalProducer (" + label + ")";
	}

	@Override
	public Model getModel(final Model model, final BTRoot root, final BTUtil.ModelMode mode) {
		return super.getModel(model, root, mode);
	}

	@Override
	public Result simulateNodeLogic(final EvaluationResult result, final Resource root) {
		return Result.UNCLEAR;
	}
}

