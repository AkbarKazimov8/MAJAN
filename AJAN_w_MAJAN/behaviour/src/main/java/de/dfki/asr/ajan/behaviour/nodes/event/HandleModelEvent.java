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
import de.dfki.asr.ajan.behaviour.AgentTaskInformation;
import de.dfki.asr.ajan.behaviour.events.ModelEventInformation;
import de.dfki.asr.ajan.behaviour.exception.ConditionEvaluationException;
import de.dfki.asr.ajan.behaviour.nodes.BTRoot;
//import de.dfki.asr.ajan.behaviour.nodes.common.AbstractTDBLeafTask;
//import de.dfki.asr.ajan.behaviour.nodes.common.BTUtil;
import de.dfki.asr.ajan.behaviour.nodes.query.BehaviorConstructQuery;
import de.dfki.asr.ajan.common.AJANVocabulary;
//import de.dfki.asr.ajan.behaviour.nodes.common.BTVocabulary;
//import de.dfki.asr.ajan.behaviour.nodes.common.EvaluationResult;
import de.dfki.asr.ajan.behaviour.nodes.common.*;
import de.dfki.asr.ajan.behaviour.nodes.common.EvaluationResult.Result;
//import de.dfki.asr.ajan.behaviour.nodes.common.LeafStatus;
import de.dfki.asr.ajan.common.AgentUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

@RDFBean("bt:HandleEvent")
public class HandleModelEvent extends AbstractTDBLeafTask {
        @Getter @Setter
        @RDFSubject
	private String url;

	@RDF("rdfs:label")
	@Getter @Setter
	private String label;

	@RDF("ajan:event")
	@Getter @Setter
	private URI event;

	/*@RDF("ajan:goal")
	@Getter @Setter
	private URI goal;*/

	@RDF("bt:validate")
	@Getter @Setter
	private BehaviorConstructQuery query;

	protected BehaviorConstructQuery constructQuery;
	protected static final Logger LOG = LoggerFactory.getLogger(HandleModelEvent.class);

	@Override
	public Resource getType() {
		return BTVocabulary.HANDLE_EVENT;
	}

	@Override
	public void reset() {
		if (constructQuery != null) {
			constructQuery.setReset(true);
		}
		super.reset();
	}

	@Override
	public LeafStatus executeLeaf() {
		try {
			constructQuery = query;
			if (handleEvent()) {
				LOG.info(toString() + " SUCCEEDED");
				return new LeafStatus(Status.SUCCEEDED, toString() + " SUCCEEDED");
			} else {
				LOG.info(toString() + " FAILED");
				return new LeafStatus(Status.FAILED, toString() + " FAILED");
			}
		} catch (ConditionEvaluationException ex) {
			LOG.info(toString() + " FAILED due to query evaluation error", ex);
			return new LeafStatus(Status.FAILED, toString() + " FAILED");
		}
	}
        // I have been using these so far
	/*protected boolean handleEvent() throws ConditionEvaluationException {
            AgentTaskInformation ati=this.getObject();
		if (checkEventGoalMatching(ati)) {
			try {
				Model model = getEventModel(this.getObject());
				if (!model.isEmpty()) {
					if (constructQuery.getTargetBase().equals(new URI(AJANVocabulary.EXECUTION_KNOWLEDGE.toString()))) {
						this.getObject().getExecutionBeliefs().update(model);
					} else if (constructQuery.getTargetBase().equals(new URI(AJANVocabulary.AGENT_KNOWLEDGE.toString()))) {
						this.getObject().getAgentBeliefs().update(model);
					} else if (constructQuery.getTargetBase().equals(new URI(AJANVocabulary.LOCAL_SERVICES_KNOWLEDGE.toString()))) {
						this.getObject().getLocalServicesBeliefs().update(model);
					} else if (constructQuery.getTargetBase().equals(new URI(AJANVocabulary.LOCAL_AGENTS_KNOWLEDGE.toString()))) {
						this.getObject().getLocalAgentsBeliefs().update(model);
					}
					return true;
				}
			} catch (QueryEvaluationException | URISyntaxException ex) {
				throw new ConditionEvaluationException(ex);
			}
		}
		return false;
	}
	protected boolean checkEventGoalMatching(AgentTaskInformation ati) {
		if (ati.getEventInformation() instanceof ModelEventInformation) {
			ModelEventInformation info = (ModelEventInformation)ati.getEventInformation();
			boolean bothNull = event == null && goal == null;
			boolean eventMatching = event != null && event.toString().equals(((ModelEventInformation) info).getEvent());
			boolean goalMatching = goal != null && goal.toString().equals(((ModelEventInformation) info).getEvent());
			return bothNull || eventMatching || goalMatching;
		}
		return false;
	}

	protected Model getEventModel(AgentTaskInformation ati) {
		Object info = ati.getEventInformation();
		Model model = new LinkedHashModel();
		if (info instanceof ModelEventInformation) {
			ModelEventInformation eventInfo = (ModelEventInformation) info;
			model = eventInfo.getModel();
			if (constructQuery == null || constructQuery.getSparql().isEmpty()) {
				return model;
			} else {
				return constructQuery.getResult(model);
			}
		}
		return model;
	}*/

        // the ones in github-ajan-service
        protected boolean handleEvent() throws ConditionEvaluationException {
		if (checkEventGoalMatching()) {
			try {
				Model model = getEventModel();
				if (!model.isEmpty()) {
					if (constructQuery.getTargetBase().equals(new URI(AJANVocabulary.EXECUTION_KNOWLEDGE.toString()))) {
						this.getObject().getExecutionBeliefs().update(model);
					} else if (constructQuery.getTargetBase().equals(new URI(AJANVocabulary.AGENT_KNOWLEDGE.toString()))) {
						this.getObject().getAgentBeliefs().update(model);
					}
					return true;
				}
			} catch (QueryEvaluationException | URISyntaxException ex) {
				throw new ConditionEvaluationException(ex);
			}
		}
		return false;
	}

	protected boolean checkEventGoalMatching() {
		if (this.getObject().getEventInformation() instanceof ModelEventInformation) {
			ModelEventInformation info = (ModelEventInformation)this.getObject().getEventInformation();
			boolean eventMatching = event != null && event.toString().equals(((ModelEventInformation) info).getEvent());
			boolean allEvents = event != null && event.toString().equals(AJANVocabulary.ALL.toString());
			return event == null || eventMatching || allEvents;
		}
		return false;
	}

	protected Model getEventModel() {
		Object info = this.getObject().getEventInformation();
		Model model = new LinkedHashModel();
		if (info instanceof ModelEventInformation) {
			ModelEventInformation eventInfo = (ModelEventInformation) info;
			model = eventInfo.getModel();
                        System.out.println("handle model 1 ----");
                        for(Statement stm: model){
                            System.out.println(stm);
                        }
			if (constructQuery == null || constructQuery.getSparql().isEmpty()) {
				return model;
			} else {
                            Model model2 = constructQuery.getResult(model);
                            System.out.println("handle model 2 ----");
                            for(Statement stm: model2){
                                System.out.println(stm);
                            }
                            return model2;
			}
		}
		return model;
	}
        
	@Override
	public void end() {
		LOG.info("Status (" + getStatus() + ")");
	}

	@Override
	public String toString() {
		return "HandleEvent (" + label + ")";
	}

	@Override
	public Model getModel(final Model model, final BTRoot root, final BTUtil.ModelMode mode) {
		if (mode.equals(BTUtil.ModelMode.DETAIL) && constructQuery != null) {
			constructQuery.setResultModel(getInstance(root.getInstance()), BTVocabulary.VALIDATE_RESULT, model);
		}
		return super.getModel(model, root, mode);
	}

	@Override
	public Result simulateNodeLogic(final EvaluationResult result, final Resource root) {
		return Result.UNCLEAR;
	}
}