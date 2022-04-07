/*
 * Created on Tue Nov 10 2020
 *
 * The MIT License (MIT)
 * Copyright (c) 2020 André Antakli (German Research Center for Artificial Intelligence, DFKI).
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
import Ember from "ember";
import ParserN3 from "@rdfjs/parser-n3" ;
import stringToStream from "npm:string-to-stream";
import { AGENTS, XSD, RDF, RDFS, MAJAN } from "ajan-editor/helpers/RDFServices/vocabulary";
import rdf from "npm:rdf-ext";
import N3 from "npm:rdf-parser-n3";
import { filterBy } from '@ember/object/computed';
import globalMjnVariable from "ajan-editor/helpers/majan/globalVariables";
//import store from 'ember-data/store';

let that;
let $ = Ember.$;
const parserN3 = new ParserN3();

export default Ember.Component.extend({

  store: Ember.inject.service(),
  websockets: Ember.inject.service(),
  wssConnection: false,
  socketRef: null,
  wssMessage: {},
  availableLogs: [],
  allMacLogs: [],
  filteredMacLogs: [],
  agentIdInput: "",
  useCaseInput: "",
  solverInput: "",

  didInsertElement() {
    this._super(...arguments);
    that = this;
    setTriplestoreField();

    getResponseMessage();
    //if (that.socketRef === null) {
      //that.set('wssConnection', true);
    //  that.actions.connect();
  //  }
/*  console.log(11111);
  console.log(globalMjnVariable.getMajanServiceConnection());
  globalMjnVariable.setMajanServiceConnection(true);
  console.log(22222);
  console.log(globalMjnVariable.getMajanServiceConnection());
*/
  if (globalMjnVariable.getMajanServiceConnection() == false || globalMjnVariable.getMajanServiceConnection() == true) {
    that.set('wssConnection', globalMjnVariable.getMajanServiceConnection());
  }else{
    that.set('wssConnection', false);
    globalMjnVariable.setMajanServiceConnection(false);
  }

  /*
  that.get('store').createRecord('macrepo',{
    name: 'macrepoexampledata-in-monitoring'
  });
*/

  },

  willDestroyElement() {
    this._super(...arguments);
    //this.actions.disconnect();
  },

  actions: {
    executeFilter(){
      that.set('filteredMacLogs', that.allMacLogs);
      if (that.agentIdInput !== "") {
        that.set('filteredMacLogs', that.allMacLogs.filterBy('agentId', that.agentIdInput));
      }
      if (that.useCaseInput !== "") {
        if (that.filteredMacLogs.length !== that.allMacLogs.length) {
          that.set('filteredMacLogs', that.filteredMacLogs.filterBy('usecase', that.useCaseInput));
        }else {
          that.set('filteredMacLogs', that.allMacLogs.filterBy('usecase', that.useCaseInput));
        }
      }
      if (that.solverInput !== "") {
        if (that.filteredMacLogs.length !== that.allMacLogs.length) {
          that.set('filteredMacLogs', that.filteredMacLogs.filterBy('solver', that.solverInput));
        }else {
          that.set('filteredMacLogs', that.allMacLogs.filterBy('solver', that.solverInput));
        }
      }

    //  var filtered = that.availableLogs.filterBy('agentId', that.agentIdInput);
        //.filterBy('useCase', that.useCaseInput)
        //.filterBy('solver', that.solverInput);

      console.log(that.availableLogs.length);

      //filtered.forEach((item, i) => {
      //  console.log(item.agentId);
    //  });
      that.set('availableLogs', that.filteredMacLogs);
      console.log(that.agentIdInput);
    },

    cleanFilter(){
      that.set('agentIdInput',"");
      that.set('useCaseInput', "");
      that.set('solverInput',"");
      that.actions.executeFilter();
    },

    cleanLogs(){
      while(this.availableLogs.length > 0){
        this.availableLogs.popObject();
      }
      while(this.allMacLogs.length > 0){
        this.allMacLogs.popObject();
      }
      while(this.filteredMacLogs.length > 0){
        this.filteredMacLogs.popObject();
      }
      that.set('availableLogs', []);
      that.set('allMacLogs', []);
      that.set('filteredMacLogs', []);

    },

    connect() {
      var socket = that.get('websockets').socketFor('ws://localhost:4202');
      socket.on('open', myOpenHandler, that);
      socket.on('message', myMessageHandler, that);
      socket.on('close', myCloseHandler, that);
      that.set('socketRef', socket);
      globalMjnVariable.setMajanServiceConnection(true);
    },

    disconnect() {
      const socket = that.get('socketRef');
      if (socket != null) {
        socket.off('open', myOpenHandler);
        socket.off('message', myMessageHandler);
        socket.off('close', myCloseHandler);
      }
      that.set("wssConnection", false);
      that.get('websockets').closeSocketFor('ws://localhost:4202');
      that.set('socketRef', null);
      globalMjnVariable.setMajanServiceConnection(false);
    },

    clean() {
      //that.set("wssMessage", "");
    },

    setResponse(content) {
      Promise.resolve(content)
        .then(x => {
          Promise.resolve(sendResponseMessage(content))
            .then(function () {
              $("#send-message").trigger("showToast");
              that.set("messageError", "");
            });
        })
        .catch(function (error) {
          that.set("messageError", uri);
        });
    }
  }
  })

  function parseLog(content) {
    let parser = new N3({ factory: rdf });
    let quadStream = parser.import(stringToStream(content));
    let logData = {
      agentId: "",
      useCase: "",
      solver: "",
      activity: "",
      activityStatus: ""
    };
    rdf.dataset().import(quadStream).then((dataset) => {
      dataset.forEach((quad) => {
        switch (quad.predicate.value) {
          case MAJAN.agentId:
            logData.agentId = quad.object.value ;
            break;
          case MAJAN.useCase:
            logData.useCase = quad.object.value ;
            break;
          case MAJAN.solver:
            logData.solver = quad.object.value ;
            console.log("5555555");
            break;
          case MAJAN.activity:
            logData.activity = quad.object.value ;
            console.log("6666666");
            break;
          case MAJAN.activityStatus:
            logData.activityStatus = quad.object.value ;
            console.log("7777777");
            break;
          default:
            console.log("switch default 00000");
          }
      });
      addLogToList(logData);
    });
  }

function addLogToList(logData){
  let date = new Date();
  that.allMacLogs.pushObject({
    time: date.toLocaleTimeString(),
    agentId: logData.agentId,
    usecase: logData.useCase,
    solver: logData.solver,
    activity: logData.activity,
    status: logData.activityStatus,
  });

  that.availableLogs.pushObject({
    time: date.toLocaleTimeString(),
    agentId: logData.agentId,
    usecase: logData.useCase,
    solver: logData.solver,
    activity: logData.activity,
    status: logData.activityStatus,
  });
}

  function myOpenHandler(event) {
  console.log(`On open event has been called: ${event}`);
  that.set("wssConnection", true);
  }

  function myMessageHandler(event) {
  console.log(`Message: ${event.data}`);
  console.log('mesaj set eliyir');
/*  that.set("wssMessage", JSON.parse(event.data));*/
  let wssMessage = JSON.parse(event.data);
  //addLog(wssMessage.body);
  parseLog(wssMessage.body);
/*  this.availableLogs.pushObject({
    time: date.toLocaleTimeString(),
    activity: wssMessage.body
  });
  this.availableLogs.pushObject({
    time: "14:22",
    agentId: "agent3",
    usecase: "LCC",
    solver: "BOSS",
    activity: "Broadcasted Feasible Coalitions",
    status: "Failure"
  });*/
  }

  function myCloseHandler(event) {
  console.log(`On close event has been called: ${event}`);
  that.get('websockets').closeSocketFor('ws://localhost:4202');
  that.set("wssConnection", false);
  }

  function setTriplestoreField() {
  $(".store-url").text(localStorage.currentStore);
  }

  function getResponseMessage() {
  return $.ajax({
    url: "http://localhost:4202/getResponse",
    type: "GET",
    headers: { Accept: "text/plain" }
  }).then(function (data) {
    console.log(data);
    that.set("response", data);
  }).catch(function (error) {
    alert("No TestServiceAction Service is running on http://localhost/4202");
  });
  }


  function sendResponseMessage(content) {
  return $.ajax({
    url: "http://localhost:4202/response",
    type: "POST",
    contentType: "text/plain",
    data: content,
  }).then(function (data) {
    $("#send-message").trigger("showToast");
    getResponseMessage();
  }).catch (function (error) {
    console.log(error);
  });
}

/*
  init() {
    this._super(...arguments);
    //that = this;

    this.set('availableLogs',[{
      time: "14:21",
      agentId: "agent1",
      usecase: "LCC",
      solver: "BOSS",
      activity: "Generated Feasible Coalitions",
      status: "Success"
    },
    {
      time: "14:21",
      agentId: "agent2",
      usecase: "LCC",
      solver: "BOSS",
      activity: "Returned Personal Info",
      status: "Success"
    },
    {
      time: "14:22",
      agentId: "agent1",
      usecase: "LCC",
      solver: "BOSS",
      activity: "Broadcasted Feasible Coalitions",
      status: "Success"
    }
  ])
  this.availableLogs.pushObject({
    time: "14:22",
    agentId: "agent3",
    usecase: "LCC",
    solver: "BOSS",
    activity: "Broadcasted Feasible Coalitions",
    status: "Failure"
  })
  //const textStream = require('streamify-string')(`
//  <http://ex.org/s> <http://ex.org/p> <http://ex.org/o1>, <http://ex.org/o2>.
//  `);

  //rdfParser.parse(textStream, { contentType: 'text/turtle', baseIRI: 'http://example.org' })
  //    .on('data', (quad) => console.log(quad))
  //    .on('error', (error) => console.error(error))
  //    .on('end', () => console.log('All done!'));
} }}*/

//});
