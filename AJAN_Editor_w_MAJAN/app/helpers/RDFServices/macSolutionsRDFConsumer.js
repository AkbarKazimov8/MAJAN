/*
 * Created on Tue Nov 10 2020
 *
 * The MIT License (MIT)
 * Copyright (c) 2020 André Antakli, Xueting Li (German Research Center for Artificial Intelligence, DFKI).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import {RDF, RDFS, SPIN, AGENTS, MAJAN} from "ajan-editor/helpers/RDFServices/vocabulary";
import JsonLdParser from "npm:rdf-parser-jsonld";
import rdf from "npm:rdf-ext";
import stringToStream from "npm:string-to-stream";
import util from "ajan-editor/helpers/RDFServices/utility";

let parser = new JsonLdParser();

export default {
  //getGoalsGraph: getGoalsGraph
  getMacSolutionsGraph: getMacSolutionsGraph
};

function getMacSolutionsGraph(data) {
  console.log("get mac solution definition:",data);
	const quadStream = parser.import(stringToStream(JSON.stringify(data)));
//  console.log("--1");
	let obj = rdf
		.dataset()
		.import(quadStream)
		.then(function(dataset) {
      //console.log("--2");
			// let goals = new Array();
      let macSolutions = new Array();
      // getGoals(dataset, goals);
      getMacSolutions(dataset, macSolutions);
      //console.log("--3");
      // return [goals, dataset];
      return [macSolutions, dataset]
		});
	return Promise.resolve(obj);
}

/*
function getGoalsGraph(data) {
  console.log("get goal definition:",data);
	const quadStream = parser.import(stringToStream(JSON.stringify(data)));
	let obj = rdf
		.dataset()
		.import(quadStream)
		.then(function(dataset) {
			let goals = new Array();
      getGoals(dataset, goals);
      return [goals, dataset];
		});
	return Promise.resolve(obj);
}*/

function getMacSolutions(graph, macSolutions) {
//  console.log("--4");
	Promise.resolve(graph).then(function(graph) {
		graph.forEach(quad => {
			if (
				quad.predicate.value === RDF.type
				&& quad.object.value === MAJAN.macProblemInstance
			) {
        //console.log("--5");
        // goals.push(getGoalsDefinitions(graph, quad.subject));
        macSolutions.push(getMacSolutionsDefinitions(graph, quad.subject));
			}
		});
	});
}

/*function getGoals(graph, goals) {
	Promise.resolve(graph).then(function(graph) {
		graph.forEach(quad => {
			if (
				quad.predicate.value === RDF.type
				&& quad.object.value === AGENTS.Goal
			) {
        goals.push(getGoalsDefinitions(graph, quad.subject));
			}
		});
	});
}*/

function getMacSolutionsDefinitions(graph, resource) {
  //console.log("--6");
  //console.log("getMacSolutionsDefinitions1:",resource);
  //console.log("getMacSolutionsDefinitions2:",graph);
  // let goal = {};
  let macSolution = {};
  // macSolution.id
  // macSolution.useCaseName
  // macSolution.solverName
  // macSolution.runtime
  // macSolution.numberOfAgents
  // macSolution.numberOfExchangedMessages
  // macSolution.solutions : array


  macSolution.id = util.generateUUID();
  let groupingSolutions = new Array();
  graph.forEach(function (quad) {
  //  console.log("--7");
    if (quad.subject.equals(resource)) {
      if (quad.predicate.value === MAJAN.hasId) {
        macSolution.id = quad.object.value;
      }
      if (quad.predicate.value === MAJAN.useCase) {
        macSolution.useCase = quad.object.value;
      }
      if (quad.predicate.value === MAJAN.hasStatus) {
        macSolution.status = quad.object.value;
      }
      if (quad.predicate.value === MAJAN.solver) {
        macSolution.solver = quad.object.value;
      }
      if (quad.predicate.value === MAJAN.hasRuntime) {
        macSolution.runtime = quad.object.value;
      }
      if (quad.predicate.value === MAJAN.hasNumberOfAgents) {
        macSolution.numberOfAgents = quad.object.value;
      }
      if (quad.predicate.value === MAJAN.hasNumberOfExchangedMessages) {
        macSolution.numberOfExchangedMessages = quad.object.value;
      }
      if (quad.predicate.value === MAJAN.hasSolution) {
        groupingSolutions.push(getGroupingSolution(graph, quad.object));
        //macSolution.numberOfExchangedMessages = quad.object.value;
      }
    }
  });
  macSolution.groupingSolutions = groupingSolutions;
  //console.log("getMacSolutionsDefinitions3:",macSolution.type);
  return macSolution;
}

function getGroupingSolution(graph, resource){
  //console.log("getGroupingSolution--"+resource);
  let groupingSolution = {};
  let groups = new Array();
  graph.forEach(function(quad) {
		if(quad.subject.equals(resource)) {
    //  console.log("--8");
			if(quad.predicate.value === MAJAN.hasValue) {
        groupingSolution.value = quad.object.value;
			}
      if(quad.predicate.value === MAJAN.hasRank) {
        groupingSolution.rank = quad.object.value;
			}
      if(quad.predicate.value === MAJAN.hasMembers) {
        groups.push(getGroup(graph, quad.object))
			}
		}
	});
  groupingSolution.groups = groups;
  return groupingSolution;
}

function getGroup(graph, resource) {
  //console.log("getGroup--"+resource);
  let group = new Array();
  graph.forEach(function(quad) {
    if(quad.subject.equals(resource)) {
      if(quad.predicate.value === MAJAN.hasMembers) {
        group.push(quad.object.value);
      }
    }
  });
  return group;
}
/*function getGoalsDefinitions(graph, resource) {
  console.log("getGoalsDefinitions1:",resource);
  console.log("getGoalsDefinitions2:",graph);
  let goal = {};
  goal.id = util.generateUUID();
  goal.uri = resource.value;
  goal.name = "Goal";
  graph.forEach(function (quad) {
    if (quad.subject.equals(resource)) {
      if (quad.predicate.value === RDFS.label) {
        goal.label = quad.object.value;
      }
      if (quad.predicate.value === RDF.type) {
        goal.type = quad.object.value;
			}
      if (quad.predicate.value === AGENTS.variables) {
				let variables = new Array();
				setVariables(variables, graph, quad.object)
        goal.variables = variables;
			}
      if (quad.predicate.value === AGENTS.condition) {
        goal.condition = quad.object.value;
			}
    }
  });
  console.log("getGoalsDefinitions:",goal.label);
  return goal;
}*/
