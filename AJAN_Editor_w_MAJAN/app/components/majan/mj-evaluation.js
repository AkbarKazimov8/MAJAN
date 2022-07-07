/*
 * Created on Tue Nov 10 2020
 *
 * The MIT License (MIT)
 * Copyright (c) 2020 Akbar Kazimov (German Research Center for Artificial Intelligence, DFKI).
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
import macSolutionsAjax from "ajan-editor/helpers/majan/evaluation/macSolutionsAjax";
import globals from "ajan-editor/helpers/global-parameters";
import { typeOf } from '@ember/utils';

import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";


import nmi from 'npm:normalized-mutual-information';

// import nmi from "normalized-mutual-information";
am4core.useTheme(am4themes_animated);

let that;
let $ = Ember.$;
const parserN3 = new ParserN3();
let ajax = null; // ajax
// const nmi = require('normalized-mutual-information');
export default Ember.Component.extend({
  ajax: Ember.inject.service(),
  store: Ember.inject.service(),
  mac_repo_tobesaved: "",
  selected_mac_repo: "",
  macReposList: [],
  macProblemInstances: undefined,
  selectedMacInstanceUseCase: "N/A",
  selectedMacInstanceStatus: "N/A",
  selectedMacInstanceSolver: "N/A",
  selectedMacInstanceRuntime: "N/A" ,
  selectedMacInstanceAgentsCount: "N/A",
  selectedMacInstanceMessagesCount: "N/A",
  selectedMacInstanceSolutions: undefined,
  tooltipInputStructure: "",
  tooltipDescriptions: "",
  tooltipExampleInput: "",
  centralAlgorithmList: [],
  centralInputsList: [],
  centralInputBody: "",
  centralInputTitleToBeSaved: "",
  selectedInputTitle: "",
  selectedCentralAlgorithm: {},
  centralAlgorithmSolutionsList: [],
  selectedSolutionsForComparison: [],
  chart: null,
  chartData:[{
     "solution": "N/A",
      "runtime": 0.000
  }, {
     "solution": "N/A",
      "runtime": 0.000
  }],
  agentIdMappingTitleValue: "",
  agentIdMappingInputValue: "",
  nmiScore: "-",
  comparisonAgentIdMappingsList: [],
  selectedAgentIdMappingTitle: "",
  selectedAgentIdMappingIndex: "",

  didInsertElement() {
    this._super(...arguments);
    that = this;
    // TO CLEAR FROM LOCAL STORAGE
     localStorage.centralAlgorithms = [];
     localStorage.centralInputs = [];
     localStorage.macRepos = [];
     localStorage.agentIdMappings = [];
    resetSelectedSolutionsInComparisonSection();
    setTriplestoreField();
    initializeGlobals(this);
    setNoSolutionsSelectedYet();
    loadStoredMacRepos();
    loadStoredCentralInputs();
    loadStoredAgentIdMappings();
    displayStoredCentralAlgorithms();
    console.log("000-"+that.selected_mac_repo);

    // chartFunc();
    // barChartF();
    // runtimeBarChart();
    // barChartRace();
    initAmCharts();
    updateChartData(that.chartData);
  },

  willDestroyElement() {
    this._super(...arguments);
    if (that.chart) {
      that.chart.dispose();
    }
  },


  actions: {
    reloadNmiScore(){
      setNmiScore("-");
      if(that.comparisonAgentIdMappingsList.length > 0){
        if(that.selectedAgentIdMappingIndex != "" && that.selectedAgentIdMappingIndex != undefined){
      displayNMIscore(that.comparisonAgentIdMappingsList.filter(function(item) {return item.title == that.selectedAgentIdMappingIndex})[0].body);
      }else {
        displayNMIscore(that.comparisonAgentIdMappingsList[0].body);
      }
    }else {
      alert("There is no saved mapping to be reloaded!");
    }
    },
    saveAgentIdMappingInput(){
      if (that.agentIdMappingInputValue == ""){
        alert("Input is empty. Enter something!");
      }else if (that.agentIdMappingTitleValue == "") {
        alert("Input Title is empty. Enter something!");
      }else {
        if(that.comparisonAgentIdMappingsList.filter(function(item) {return item.title == that.agentIdMappingTitleValue}).length==0){
          that.comparisonAgentIdMappingsList.pushObject({
            title: that.agentIdMappingTitleValue,
            body: that.agentIdMappingInputValue,
          });
          localStorage.agentIdMappings = JSON.stringify(that.comparisonAgentIdMappingsList);
          if(that.selectedAgentIdMappingTitle == undefined || that.selectedAgentIdMappingTitle == ""){
            // displaySelectedInput(that.centralInputsList[0]);
            displaySelectedAgentIdMapping(that.comparisonAgentIdMappingsList[0]);
            displayNMIscore(that.comparisonAgentIdMappingsList[0].body);
          }else {
            displaySelectedAgentIdMapping(that.comparisonAgentIdMappingsList.filter(function(item) {return item.title == that.selectedAgentIdMappingTitle})[0]);
            // displaySelectedInput(that.centralInputsList.filter(function(item) {return item.title == that.selectedInputTitle})[0]);
            displayNMIscore(that.comparisonAgentIdMappingsList.filter(function(item) {return item.title == that.selectedAgentIdMappingTitle})[0].body);
           }
          //
          // that.set('centralInputBody', "");
           that.set('agentIdMappingTitleValue', "");
          // that.set('selectedInputTitle', that.centralInputsList[0].title);
        }else {
          // notify that input title exists
          alert("Agent-ID Mapping Title already exists. Write something else!");
        }
      }

    },
    save_repo(){
      if(that.mac_repo_tobesaved != ""){
        that.macReposList.pushObject({
          name: that.mac_repo_tobesaved,
        });
        localStorage.macRepos = JSON.stringify(that.macReposList);
        that.set('mac_repo_tobesaved', "");
        sendQueryToFetchMacProblems();

      }else {
        alert("No input is provided!");
      }
    },

    save_input(){
      if (that.centralInputBody != "" & that.centralInputTitleToBeSaved != "") {
        if(that.centralInputsList.filter(function(item) {return item.title == that.centralInputTitleToBeSaved}).length==0){
          that.centralInputsList.pushObject({
            title: that.centralInputTitleToBeSaved,
            body: that.centralInputBody,
          });
          localStorage.centralInputs = JSON.stringify(that.centralInputsList);
          if(that.selectedInputTitle == undefined || that.selectedInputTitle == ""){
            displaySelectedInput(that.centralInputsList[0]);
          }else {
            displaySelectedInput(that.centralInputsList.filter(function(item) {return item.title == that.selectedInputTitle})[0]);
           }
          //
           that.set('centralInputTitleToBeSaved', "");
        }else {
          alert("Input Title already exists. Write something else!");
        }
      }else {
        alert("Input is empty. Enter something!");
      }
    },

    handleAgentIdMappingSelection: function(selected){
      displaySelectedAgentIdMapping(that.get('comparisonAgentIdMappingsList').filter(
          function(item) {return item.title == selected})[0]);
      displayNMIscore(that.get('comparisonAgentIdMappingsList').filter(
          function(item) {return item.title == selected})[0].body);
      that.set('selectedAgentIdMappingIndex', selected);
    },

    macRepoSelection: function(selected){
      that.set('selected_mac_repo', selected)
      resetMacSolutionPanel();
      sendQueryToFetchMacProblems();
    },

    centralAlgoSelection:function(selected){
      displaySelectedAlgorithm(that.get('centralAlgorithmList').filter(function(item) {return item.name == selected})[0].config);

    },

    setCentralInputSelection:function(selected){
      displaySelectedInput(that.get('centralInputsList').filter(
        function(item) {return item.title == selected})[0]);

    },

    problemInstanceRadioClick: function(selected){
      that.set('selectedSolutionsForComparison.mac', "Problem Instance: "+(parseInt(selected)+1));
      that.set('selectedSolutionsForComparison.macProblemIndex', selected);
      displayMacProblemInfo(that.get('macProblemInstances')[selected]);
      displayMacSolutions(that.get('macProblemInstances')[selected]);
  },

  handleMacSolutionChange: function(selected){
    var selectionTitle = "MAC Solution: " + (parseInt(selected)+1) + " of Problem Instance: " + (parseInt(that.selectedSolutionsForComparison.macProblemIndex)+1);
    that.set('selectedSolutionsForComparison.mac', selectionTitle);
    that.set('selectedSolutionsForComparison.macId', selected);

    that.chartData.splice(1,1,{
      solution: selectionTitle,
      runtime: that.get('macProblemToBeDisplayed.runtime')
    });
    updateChartData(that.chartData);
    updateNMIscore();
  },

  handleCentralSolutionChange: function(selected){
    var selectionTitle = "Central Solution: " + (parseInt(selected)+1);
    that.set('selectedSolutionsForComparison.central', selectionTitle);
    that.set('selectedSolutionsForComparison.centralId', selected);

    that.chartData.splice(0,1,{
      solution: selectionTitle,
      runtime: that.get('selectedCentralAlgorithm.runtime')
    });
    updateChartData(that.chartData);
    updateNMIscore();
  },

  reload_mac(){
    sendQueryToFetchMacProblems();
  },

  runAlgorithm(){
    cleanCentralSolutions();
      let runJarPayload = {
        jarPath: that.selectedCentralAlgorithm.jarPath,
        timeout: that.selectedCentralAlgorithm.timeout,
        body: that.centralInputBody
      };
      sendRunJarRequest(runJarPayload);
  },

  upload_file: function(event){
    const reader = new FileReader();
    const file = event.target.files[0];
    let algoConfigData;

    reader.onload = () => {
      algoConfigData = reader.result;
      saveNewAlgorithm(algoConfigData);
    };

    if (file) {
      reader.readAsText(file);
    }
  }
  }
})

function chartFunc(){
  let innerChart = am4core.create("chartdiv", am4charts.XYChart);
    innerChart.paddingRight = 20;

    let data = [];
    let visits = 10;
    for (let i = 1; i < 366; i++) {
      visits += Math.round((Math.random() < 0.5 ? 1 : -1) * Math.random() * 10);
      data.push({ date: new Date(2018, 0, i), name: "name" + i, value: visits });
    }

    innerChart.data = data;

    let dateAxis = innerChart.xAxes.push(new am4charts.DateAxis());
    dateAxis.renderer.grid.template.location = 0;

    let valueAxis = innerChart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.tooltip.disabled = true;
    valueAxis.renderer.minWidth = 35;

    let series = innerChart.series.push(new am4charts.LineSeries());
    series.dataFields.dateX = "date";
    series.dataFields.valueY = "value";

    series.tooltipText = "{valueY.value}";
    innerChart.cursor = new am4charts.XYCursor();

    let scrollbarX = new am4charts.XYChartScrollbar();
    scrollbarX.series.push(series);
    innerChart.scrollbarX = scrollbarX;

    that.chart = innerChart;
}

function barChartF(){
  var innerChart = am4core.create("chartdiv", am4charts.XYChart);


innerChart.colors.saturation = 0.4;

innerChart.data = [{
	"country": "USA",
	"visits": 3025
}, {
	"country": "China",
	"visits": 1882
}, {
	"country": "Japan",
	"visits": 1809
}, {
	"country": "Germany",
	"visits": 1322
}, {
	"country": "UK",
	"visits": 1122
}, {
	"country": "France",
	"visits": 1114
}, {
	"country": "India",
	"visits": 984
}, {
	"country": "Spain",
	"visits": 711
}, {
	"country": "Netherlands",
	"visits": 665
}, {
	"country": "Russia",
	"visits": 580
}, {
	"country": "South Korea",
	"visits": 443
}, {
	"country": "Canada",
	"visits": 441
}];


var categoryAxis = innerChart.yAxes.push(new am4charts.CategoryAxis());
categoryAxis.renderer.grid.template.location = 0;
categoryAxis.dataFields.category = "country";
categoryAxis.renderer.minGridDistance = 20;

var valueAxis = innerChart.xAxes.push(new am4charts.ValueAxis());
valueAxis.renderer.maxLabelPosition = 0.98;

var series = innerChart.series.push(new am4charts.ColumnSeries());
series.dataFields.categoryY = "country";
series.dataFields.valueX = "visits";
series.tooltipText = "{valueX.value}";
series.sequencedInterpolation = true;
series.defaultState.transitionDuration = 1000;
series.sequencedInterpolationDelay = 100;
series.columns.template.strokeOpacity = 0;

innerChart.cursor = new am4charts.XYCursor();
innerChart.cursor.behavior = "panY";
that.chart = innerChart;
}

function initAmCharts(){
  var innerChart = am4core.create("chartdiv", am4charts.XYChart);
  innerChart.colors.saturation = 0.8;
  var chrtTitle = innerChart.titles.create();
  chrtTitle.text = "Computation time of solutions";
  chrtTitle.fontSize = 25;
  chrtTitle.marginBottom = 25;

var categoryAxis = innerChart.yAxes.push(new am4charts.CategoryAxis());
categoryAxis.renderer.grid.template.location = 0;
categoryAxis.dataFields.category = "solution";
categoryAxis.renderer.minGridDistance = 30;
// categoryAxis.title.text = "Solutions";
categoryAxis.title.rotation = 0;
categoryAxis.title.align = "center";
categoryAxis.title.valign = "top";
categoryAxis.title.dy = -10;
categoryAxis.title.fontWeight = 600;
// categoryAxis.paddingLeft = 10;
categoryAxis.paddingRight = 10;
categoryAxis.layout = "absolute";

var valueAxis = innerChart.xAxes.push(new am4charts.ValueAxis());
valueAxis.renderer.maxLabelPosition = 0.98;
valueAxis.title.text = "Runtime (seconds)";
valueAxis.title.align = "center";
valueAxis.title.fontWeight = 600;

var series = innerChart.series.push(new am4charts.ColumnSeries());
series.dataFields.categoryY = "solution";
series.dataFields.valueX = "runtime";
series.tooltipText = "{valueX.value}";
// series.columns.template.showTooltipOn = "always";
series.dataFields.valueY = "value";
series.sequencedInterpolation = true;
series.defaultState.transitionDuration = 1000;
series.sequencedInterpolationDelay = 100;
series.columns.template.strokeOpacity = 0;
series.columns.template.fill = am4core.color("#ff0000");
// series.columns.template.maxWidth = 50;
series.columns.template.maxHeight = 50;
// series.minWidth = '300px';
// series.columns.template.minWidth = 500;
var bullet = series.bullets.push(new am4charts.LabelBullet);
bullet.label.text = "{valueX.value}";
bullet.label.rotation = 0;
bullet.label.truncate = false;
bullet.label.hideOversized = false;
bullet.label.horizontalCenter = "left";

innerChart.cursor = new am4charts.XYCursor();
innerChart.cursor.behavior = "panY";

// innerChart.legend = new am4charts.Legend();
// innerChart.legend.itemContainers.template.cursorOverStyle = am4core.MouseCursorStyle.pointer;

innerChart.background.fill = '#F2F2F2';
// innerChart.background.opacity = 0.3;

innerChart.legend = new am4charts.Legend();
// innerChart.legend.markers.template.disabled = true;

var cellSize = 50;
innerChart.events.on("datavalidated", function(ev) {

  // Get objects of interest
  var chart = ev.target;
  var categoryAxis = chart.yAxes.getIndex(0);

  // Calculate how we need to adjust chart height
  var adjustHeight = chart.data.length * cellSize - categoryAxis.pixelHeight;

  // get current chart height
  var targetHeight = chart.pixelHeight + adjustHeight;

  // Set it on chart's container
  chart.svgContainer.htmlElement.style.height = targetHeight + "px";
});

// innerChart.paddinTop = 0;
// innerChart.paddinBottom = 0;
// innerChart.paddinLeft = 0;
// innerChart.paddinRight = 0;

that.chart = innerChart;
}

function updateChartData(data){
  console.log("girdiii");
  that.chart.data = data;
}

function runtimeBarChart(){
  var innerChart = am4core.create("chartdiv", am4charts.XYChart);
  innerChart.colors.saturation = 0.8;
  innerChart.data = [{
	   "solution": "MAC_1_1",
	    "runtime": 124
  }, {
	   "solution": "Central_4",
	    "runtime": 23
  }];


var categoryAxis = innerChart.yAxes.push(new am4charts.CategoryAxis());
categoryAxis.renderer.grid.template.location = 0;
categoryAxis.dataFields.category = "solution";
categoryAxis.renderer.minGridDistance = 20;
categoryAxis.title.text = "Solutions";
categoryAxis.title.rotation = 0;
categoryAxis.title.align = "center";
categoryAxis.title.valign = "top";
categoryAxis.title.dy = -10;
categoryAxis.title.fontWeight = 600;
// categoryAxis.paddingLeft = 10;
categoryAxis.paddingRight = 10;
categoryAxis.layout = "absolute";

var valueAxis = innerChart.xAxes.push(new am4charts.ValueAxis());
valueAxis.renderer.maxLabelPosition = 0.98;
valueAxis.title.text = "Runtime";
valueAxis.title.align = "center";
valueAxis.title.fontWeight = 600;

var series = innerChart.series.push(new am4charts.ColumnSeries());
series.dataFields.categoryY = "solution";
series.dataFields.valueX = "runtime";
series.tooltipText = "{valueX.value}";
series.sequencedInterpolation = true;
series.defaultState.transitionDuration = 1000;
series.sequencedInterpolationDelay = 100;
series.columns.template.strokeOpacity = 0;
series.columns.template.fill = am4core.color("#ff0000");
// series.columns.template.maxWidth = 50;
series.columns.template.maxHeight = 50;




innerChart.cursor = new am4charts.XYCursor();
innerChart.cursor.behavior = "panY";

// innerChart.legend = new am4charts.Legend();
// innerChart.legend.itemContainers.template.cursorOverStyle = am4core.MouseCursorStyle.pointer;

innerChart.background.fill = '#F2F2F2';
// innerChart.background.opacity = 0.3;

innerChart.legend = new am4charts.Legend();
// innerChart.legend.markers.template.disabled = true;

var cellSize = 50;
innerChart.events.on("datavalidated", function(ev) {

  // Get objects of interest
  var chart = ev.target;
  var categoryAxis = chart.yAxes.getIndex(0);

  // Calculate how we need to adjust chart height
  var adjustHeight = chart.data.length * cellSize - categoryAxis.pixelHeight;

  // get current chart height
  var targetHeight = chart.pixelHeight + adjustHeight;

  // Set it on chart's container
  chart.svgContainer.htmlElement.style.height = targetHeight + "px";
});


that.chart = innerChart;
}

function barChartRace(){
  var chart = am4core.create("chartdiv", am4charts.XYChart);
chart.padding(40, 40, 40, 40);

chart.numberFormatter.bigNumberPrefixes = [
  { "number": 1e+3, "suffix": "K" },
  { "number": 1e+6, "suffix": "M" },
  { "number": 1e+9, "suffix": "B" }
];

var label = chart.plotContainer.createChild(am4core.Label);
label.x = am4core.percent(97);
label.y = am4core.percent(95);
label.horizontalCenter = "right";
label.verticalCenter = "middle";
label.dx = -15;
label.fontSize = 50;

var playButton = chart.plotContainer.createChild(am4core.PlayButton);
playButton.x = am4core.percent(97);
playButton.y = am4core.percent(95);
playButton.verticalCenter = "middle";
playButton.events.on("toggled", function(event) {
  if (event.target.isActive) {
    play();
  }
  else {
    stop();
  }
})

var stepDuration = 4000;

var categoryAxis = chart.yAxes.push(new am4charts.CategoryAxis());
categoryAxis.renderer.grid.template.location = 0;
categoryAxis.dataFields.category = "network";
categoryAxis.renderer.minGridDistance = 1;
categoryAxis.renderer.inversed = true;
categoryAxis.renderer.grid.template.disabled = true;

var valueAxis = chart.xAxes.push(new am4charts.ValueAxis());
valueAxis.min = 0;
valueAxis.rangeChangeEasing = am4core.ease.linear;
valueAxis.rangeChangeDuration = stepDuration;
valueAxis.extraMax = 0.1;

var series = chart.series.push(new am4charts.ColumnSeries());
series.dataFields.categoryY = "network";
series.dataFields.valueX = "MAU";
series.tooltipText = "{valueX.value}"
series.columns.template.strokeOpacity = 0;
series.columns.template.column.cornerRadiusBottomRight = 5;
series.columns.template.column.cornerRadiusTopRight = 5;
series.interpolationDuration = stepDuration;
series.interpolationEasing = am4core.ease.linear;

var labelBullet = series.bullets.push(new am4charts.LabelBullet())
labelBullet.label.horizontalCenter = "right";
labelBullet.label.text = "{values.valueX.workingValue.formatNumber('#.0as')}";
labelBullet.label.textAlign = "end";
labelBullet.label.dx = -10;

chart.zoomOutButton.disabled = true;
that.chart = chart;
}

// function handleMacSolutionChange(src){
//   console.log("mac solution checked: "+src);
//   console.log("mac solution checked: "+src.value);
// }
//
// function handleCentralSolutionChange(src){
//   console.log("central solution checked: "+src);
//   console.log("central solution checked: "+src.value);
// }

// TODO use alert() function
function sendRunJarRequest(payload) {
  // TODO: IF BASE URI OF AJAN CHANGES, PLEASE CHANGE IT BELOW AS WELL
    return $.ajax({
      url: "http://localhost:8060//welcome/integration/coordination/ajan/majanService/runJar",
      type: "POST",
      contentType: "text/plain",
      data: payload.body,
      headers: {
        // Content-Type: "text/plain; charset=utf-8",
        // Accept: "text/plain; charset=utf-8",
        jarPath: payload.jarPath,
        timeout: payload.timeout
      },
    }).then(function (data) {
      let groupingProblem = buildCentralGroupingProblem(data);
      let centralSolutions = getSolutionsOfGroupingProblem(groupingProblem);
      displayCentralSolutions(centralSolutions, groupingProblem);

    }).catch (function (error) {
      alert("JAR file couldn't be executed. Error message is below:\n" + JSON.stringify(error));
    });
  }

function displayCentralSolutions(centralSolutions, groupingProblem){
  that.set('selectedCentralAlgorithm.runtime', groupingProblem.runtime);
  that.set('selectedCentralAlgorithm.agentCount', groupingProblem.agentCount);
  that.set('centralAlgorithmSolutionsList', centralSolutions);
}

function cleanCentralSolutions(){
  that.set('selectedCentralAlgorithm.runtime', "");
  that.set('selectedCentralAlgorithm.agentCount', "");
  that.set('centralAlgorithmSolutionsList', []);
}

function saveNewAlgorithm(jsonConfigInput){
  let jsonObj = JSON.parse(jsonConfigInput);
  let algorithm = jsonObj.algorithm;

  if(that.centralAlgorithmList.filter(function(item) {return item.name == algorithm}).length!=0){
    alert("An algorithm with the name, \"" + algorithm + "\", already exists. Please specify unique names for algorithms!");
  }else {
    // Save algorithm to local storage
    that.centralAlgorithmList.pushObject({
      name: algorithm,
      config: jsonConfigInput
    });
    that.set('centralAlgorithmList', that.centralAlgorithmList);
    localStorage.centralAlgorithms = JSON.stringify(that.centralAlgorithmList);
    if(that.selectedCentralAlgorithm.name == undefined || that.selectedCentralAlgorithm.name == ""){
      displaySelectedAlgorithm(that.centralAlgorithmList[0].config);
    }else {
      displaySelectedAlgorithm(that.centralAlgorithmList.filter(function(item) {return item.name == that.selectedCentralAlgorithm.name})[0].config);    }
  }
}

function displaySelectedAlgorithm(jsonConfigInput){
  let jsonObj = JSON.parse(jsonConfigInput);
  let algorithm = jsonObj.algorithm;
  let pathToJarFile = jsonObj.pathToJarFile;
  let timeout = jsonObj.timeout;
  let algoInfo = {
    name: algorithm,
    jarPath: pathToJarFile,
    timeout: timeout
  };
  that.set('selectedCentralAlgorithm', algoInfo);

  that.set('tooltipInputStructure',"<b>Name:</b> "+algorithm + "<br><b>JAR file path:</b> "+ pathToJarFile
    + "<br><b>Maximum amount of waiting time: </b>" + timeout + " seconds"
    + "<br><b>Input Structure:</b><br>");
  that.set('tooltipDescriptions', "<br><br><b>Descriptions:</b><br>");
  that.set('tooltipExampleInput', "<br><br><b>Example Input:</b><br>");
  setTooltipRepresentationOfAlgorithmConfig(jsonObj);
}

function displaySelectedInput(selectedInput){
  that.set('centralInputBody', selectedInput.body);
  that.set('selectedInputTitle', selectedInput.title);
}

function displaySelectedAgentIdMapping(selectedAgentIdMapping){
  that.set('agentIdMappingInputValue', selectedAgentIdMapping.body);
  that.set('selectedAgentIdMappingTitle', selectedAgentIdMapping.title);
}

function updateNMIscore(){
  if(that.comparisonAgentIdMappingsList.length > 0){
    if(that.selectedAgentIdMappingIndex != "" && that.selectedAgentIdMappingIndex != undefined){
        displayNMIscore(that.comparisonAgentIdMappingsList.filter(function(item) {return item.title == that.selectedAgentIdMappingIndex})[0].body);
    }else {
      displayNMIscore(that.comparisonAgentIdMappingsList[0].body);
    }
  }
}

function displayNMIscore(selectedAgentIdMappingBody){
  if(that.selectedMacInstanceSolutions == null || that.selectedMacInstanceSolutions == undefined || that.selectedMacInstanceSolutions.length == 0 || that.selectedSolutionsForComparison.macId == undefined){
    setNmiScore("-");
    alert("No mac solution selected to compute NMI score!");
  }else if(that.selectedSolutionsForComparison.centralId == undefined){
    setNmiScore("-");
    alert("No central solution selected to compute NMI score!");
  } else {

let selectedMACSoltuion = that.selectedMacInstanceSolutions[that.selectedSolutionsForComparison.macId];
let macGroups = selectedMACSoltuion.groups; // this is an array of objects: {agentId: "<agentName>", groupId: <groupId>}
var agentNameLines = selectedAgentIdMappingBody.trim().split(/\r?\n/);
let macArrayForNMI = Array.apply(null, {length: agentNameLines.length}).map(Number.call, Number); // this array is created to pass to NMI() to compute comparison of MAC vs Central solutions

for (var i = 0; i < agentNameLines.length; i++) {
  let agentNameLine = agentNameLines[i];
  if(!agentNameLine.includes("=")){
    setNmiScore("-");
    alert("Incorrect Mapping Structure to compute NMI score: MAC and Central agent names are not separated with = in the selected mapping file");
    return;
  }
  let index = parseInt(agentNameLine.trim().split('=')[1].trim());
  if(Number.isInteger(index)){
    let agentName = agentNameLine.trim().split('=')[0].trim();
    let groupIdOfAgent = macGroups.filter(function(item) {return item.agentId == agentName})[0].groupId;
    macArrayForNMI.splice((index-1), 1, (groupIdOfAgent)); // deduct 1 since array starts from 0 and the indexes of agents in mapping start from 1
  }else {
    setNmiScore("-");
    alert("Incorrect Mapping Structure to compute NMI score: Index values of Mapping should be integer but instead found " + index);
    return;
  }}

  let groupsArrayForNmi = that.centralAlgorithmSolutionsList[that.selectedSolutionsForComparison.centralId].groupsArrayForNmi;
  if(groupsArrayForNmi.length != agentNameLines.length){
    setNmiScore("-");
    alert("Amount of agents in mapping input (MAC_Agent_Name = Central_Agent_Name) should be same as selected solutions to compute NMI score!");
  }else if(groupsArrayForNmi.length == macArrayForNMI.length){
    let nmiscore = nmi.jNMI(groupsArrayForNmi, macArrayForNMI);
    nmiscore = (nmiscore).toFixed(3);
    setNmiScore(nmiscore);
  }else {
    setNmiScore("-");
    alert("Selected Solutions should have the same amount of agents to compute NMI score!");
  }
}

function setNmiScore(score){
    that.set('nmiScore', score);
}

}

function setTooltipRepresentationOfAlgorithmConfig(jsonObj){
  if (jsonObj.keyword != undefined) {
    strRepresentation = addKeyword(jsonObj.keyword);
  }else if (jsonObj.keywords != undefined) {
    jsonObj.keywords.forEach(function(keywordObj){
       addKeyword(keywordObj.keyword);
    });
  }
}

function addKeyword(keyword){
  let type = keyword.type;
  that.set('tooltipInputStructure', that.get('tooltipInputStructure') + "<b>" + type + " -></b>");
  that.set('tooltipExampleInput', that.get('tooltipExampleInput') + "<b>" + type + " -></b>");
  let description = keyword.description;
  that.set('tooltipDescriptions', that.get('tooltipDescriptions') + "<i>&#9679; " + type + ":</i> " + description + "<br>");

  if (keyword.element != undefined) {
    that.set('tooltipInputStructure', that.get('tooltipInputStructure') + " {");
    that.set('tooltipExampleInput', that.get('tooltipExampleInput') + " {");
    addElement(keyword.element);
    that.set('tooltipInputStructure', that.get('tooltipInputStructure') + "}");
    that.set('tooltipExampleInput', that.get('tooltipExampleInput') + "}");
  }else if (keyword.elements != undefined) {
    keyword.elements.forEach(function(elementObj){
      that.set('tooltipInputStructure', that.get('tooltipInputStructure') + " {");
      that.set('tooltipExampleInput', that.get('tooltipExampleInput') + " {");
      addElement(elementObj.element);
      that.set('tooltipInputStructure', that.get('tooltipInputStructure') + "} |");
      that.set('tooltipExampleInput', that.get('tooltipExampleInput') + "} |");
    });
    that.set('tooltipInputStructure', that.get('tooltipInputStructure').substring(0, that.get('tooltipInputStructure').length-1));
    that.set('tooltipExampleInput', that.get('tooltipExampleInput').substring(0, that.get('tooltipExampleInput').length-1));
  }
  that.set('tooltipInputStructure', that.get('tooltipInputStructure') + "<br>");
  that.set('tooltipExampleInput', that.get('tooltipExampleInput') + "<br>");

}

function addElement(element){
  if (element.item != undefined) {
    that.set('tooltipInputStructure', that.get('tooltipInputStructure') + "&lt;");
    addItem(element.item);
    that.set('tooltipInputStructure', that.get('tooltipInputStructure') + "&gt;");
  }else if(element.items != undefined){
    element.items.forEach(function(itemObj){
      that.set('tooltipInputStructure', that.get('tooltipInputStructure') + "&lt;");
      addItem(itemObj.item);
      that.set('tooltipInputStructure', that.get('tooltipInputStructure') + "&gt;, ");
      that.set('tooltipExampleInput', that.get('tooltipExampleInput') + ", ");
    });
    that.set('tooltipInputStructure', that.get('tooltipInputStructure').substring(0, that.get('tooltipInputStructure').length-2));
    that.set('tooltipExampleInput', that.get('tooltipExampleInput').substring(0, that.get('tooltipExampleInput').length-2));
  }
}

function addItem(item){
  let name = item.name;
  that.set('tooltipInputStructure', that.get('tooltipInputStructure') + name);
  let example_value = item.example_value;
  that.set('tooltipExampleInput', that.get('tooltipExampleInput') + example_value);
  let datatype = item.datatype;
  let description =item.description;
  that.set('tooltipDescriptions', that.get('tooltipDescriptions') + "<i>&#9679; " + name + ":</i> " + description + " (" + datatype + ")<br>");
}

function sendQueryToFetchMacProblems(){
  let repo = (localStorage.currentStore || "http://localhost:8090/rdf4j/repositories/");
  if(that.get('selected_mac_repo') == null || that.get('selected_mac_repo') == ""){
    if(that.get('macReposList') != null && that.get('macReposList').length > 0){
      repo = repo + that.get('macReposList')[0].name;
    }else{
      alert("No MAC repo is selected to retrieve MAC solutions!");
    }
  }else{
    repo = repo + that.get('selected_mac_repo');
  }
  macSolutionsAjax.getFromServer(ajax, repo).then(macSolutionsFetched);
}

function macSolutionsFetched(rdfData) {
that.set('macProblemInstances', macSolutionsAjax.getMacSolutions());
if(that.get('macProblemInstances').length > 0){
  displayMacProblemInfo(that.get('macProblemInstances')[0]);
  displayMacSolutions(that.get('macProblemInstances')[0]);
}else {
  alert("There is no MAC problem instance in the specified repository!");
}

let macProblemInstances2 = macSolutionsAjax.getMacSolutions();

}
function resetMacSolutionPanel(){
  that.set('macProblemInstances', null);
  that.set('selectedMacInstanceSolutions',null);

  that.set('macProblemToBeDisplayed.useCase', "N/A");
  that.set('macProblemToBeDisplayed.status', "N/A");
  that.set('macProblemToBeDisplayed.solver', "N/A");
  that.set('macProblemToBeDisplayed.runtime', "N/A");
  that.set('macProblemToBeDisplayed.numberOfAgents', "N/A");
}



function displayMacSolutions(macProblemToBeDisplayed){
  if (macProblemToBeDisplayed.groupingSolutions.length>0) {
    let macSolutionValue = "N/A";
    let macSolutionRank = "";
    let selectedMacInstanceSolutions = new Array();
    macProblemToBeDisplayed.groupingSolutions.forEach(function(groupingSolution){
      let groups = new Array();
      let groupId = 1;
      let macSolutionContent = "[";
      // macSolutionValue = Double.valueOf(String.valueOf(groupingSolution.value)) ;
      macSolutionRank = Math.floor(groupingSolution.rank) ;
      macSolutionValue = parseFloat(groupingSolution.value).toFixed(3) ;
      groupingSolution.groups.forEach(function(group){
    macSolutionContent = macSolutionContent + "[";
    group.forEach(function(member){
      // add each agent and its group to array
      groups.pushObject(
        {
          agentId: member,
          groupId: groupId
        }
    );
      macSolutionContent = macSolutionContent + member + ", ";
   })
   macSolutionContent = macSolutionContent.trim();
   macSolutionContent = macSolutionContent.slice(0,-1);
   macSolutionContent = macSolutionContent + "], ";
   groupId = groupId + 1;
})
macSolutionContent = macSolutionContent.trim();
macSolutionContent = macSolutionContent.slice(0,-1);
macSolutionContent = macSolutionContent + "]";
let macSlObj = {macSolutionContent, macSolutionValue, macSolutionRank, groups};

selectedMacInstanceSolutions.pushObject(macSlObj);
})
selectedMacInstanceSolutions = selectedMacInstanceSolutions.sortBy('macSolutionRank');
that.set('selectedMacInstanceSolutions', selectedMacInstanceSolutions);
}else {
  alert("There is no solution for the selected MAC Problem instance!");
  that.set('selectedMacInstanceSolutions',null);
}
}

function displayMacProblemInfo(macProblemToBeDisplayed){
          that.set("macProblemToBeDisplayed", macProblemToBeDisplayed);
}

function parseRdfTest(rdfInput) {
  let parser = new N3({ factory: rdf });
  let quadStream = parser.import(stringToStream(JSON.stringify(rdfInput)));
  /*let logData = {
    agentId: "",
    useCase: "",
    solver: "",
    activity: "",
    activityStatus: ""
  };*/
  rdf.dataset().import(quadStream).then((dataset) => {
    dataset.forEach((quad) => {
      /*switch (quad.predicate.value) {
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
        }*/
    });
    addLogToList(logData);
  });
}
function setNoSolutionsSelectedYet(){
  that.set('selectedSolutionsForComparison.mac', "Nothing selected in <MAC Solution>");
  that.set('selectedSolutionsForComparison.central', "Nothing selected in <Central Solution>");
}

function loadStoredMacRepos(){
  if(localStorage.macRepos != null & localStorage.macRepos != [] ){
    that.set('macReposList', JSON.parse(localStorage.macRepos));
  }
}
function loadStoredCentralInputs(){
  if(localStorage.centralInputs != null & localStorage.centralInputs != [] ){
    that.set('centralInputsList', JSON.parse(localStorage.centralInputs));
    displaySelectedInput(that.get('centralInputsList')[0]);
  }
}

function loadStoredAgentIdMappings(){
  if(localStorage.agentIdMappings != null & localStorage.agentIdMappings != [] ){
    that.set('comparisonAgentIdMappingsList', JSON.parse(localStorage.agentIdMappings));
    displaySelectedAgentIdMapping(that.get('comparisonAgentIdMappingsList')[0]);
  }
}

function displayStoredCentralAlgorithms(){
  if(localStorage.centralAlgorithms != null & localStorage.centralAlgorithms != [] ){
      that.set('centralAlgorithmList', JSON.parse(localStorage.centralAlgorithms));
      displaySelectedAlgorithm(that.get('centralAlgorithmList')[0].config);
    }
}

function setTriplestoreField() {
$(".store-url").text(localStorage.currentStore);
}

function initializeGlobals(currentComponent) {
	setCurrentComponent(currentComponent);
	initializeAjax();
}

function setCurrentComponent(currentComponent) {
	globals.currentComponent = currentComponent;
}

function initializeAjax() {
	ajax = globals.currentComponent.get("ajax");
	globals.ajax = ajax;
}

function getSolutionsOfGroupingProblem(groupingProblem){
      let groupingValue = "N/A";
      let groupingRank = "";
      let groupingSolutionsList = new Array();

      groupingProblem.groupingSolutions.forEach(function(groupingSolution){
        let groupsArrayForNmi = Array.apply(null, {length: parseInt(groupingProblem.agentCount)}).map(Number.call, Number); // this array is created to pass to NMI() to compute comparison of MAC vs Central solutions
        let groupId = 1;
        let groupingSolutionContent = "[";
        groupingRank = Math.floor(groupingSolution.rank) ;
        groupingValue = parseFloat(groupingSolution.value).toFixed(3);
        groupingSolution.groups.forEach(function(group){
          groupingSolutionContent = groupingSolutionContent + "[";
          group.forEach(function(member){
            groupsArrayForNmi.splice(parseInt(member)-1, 1, groupId);
            groupingSolutionContent = groupingSolutionContent + member + ", ";
          })
        groupingSolutionContent = groupingSolutionContent.trim().slice(0,-1) + "], ";
        groupId = groupId + 1;

        })
        groupingSolutionContent = groupingSolutionContent.trim().slice(0,-1) + "]";
        let groupingSolutionObj = {groupingSolutionContent, groupingValue, groupingRank, groupsArrayForNmi};
        groupingSolutionsList.pushObject(groupingSolutionObj);
      })
      groupingSolutionsList = groupingSolutionsList.sortBy('groupingRank');
      return groupingSolutionsList;
}

function buildCentralGroupingProblem(jarExecutionResponse){
      // !!!! PARSING THE RESULT OF EXECUTION OF JAR FILE WHICH WAS RECEIVED FROM THE .../runJar ENDPOINT
      let jarResponseJsonObj = JSON.parse(JSON.stringify(jarExecutionResponse));
      let groupingProblem = {};

      // FETCH NUMBER OF AGENTS FROM JSON RESULT
      if (jarResponseJsonObj.numberOfAgents != undefined || jarResponseJsonObj.numberOfAgents != "") {
        groupingProblem.agentCount = jarResponseJsonObj.numberOfAgents;
      }else {
        groupingProblem.agentCount = "N/A";
      }
      // FETCH RUNTIME OF EXECUTION FROM JSON RESULT
      if (jarResponseJsonObj.runtime != undefined || jarResponseJsonObj.runtime != "") {
          groupingProblem.runtime = jarResponseJsonObj.runtime;
      }else {
          groupingProblem.runtime = "N/A";
      }
      // FETCH GROUPINGS FROM THE JSON RESULT IF THEY EXIST
      if (jarResponseJsonObj.groupings != undefined) {
          groupingProblem.groupingSolutions = new Array();
          jarResponseJsonObj.groupings.forEach(function(groupingObj){
            groupingProblem.groupingSolutions.pushObject(fetchGroupingFromJarResponse(groupingObj));
          });
      }else {
          // NO SOLUTION FOUND BY JAR AND RETURNED
          alert("Execution of the specified algorithm (JAR) didn't find any solutions");
      }
      return groupingProblem;
}

function fetchGroupingFromJarResponse(groupingJsonObj){
      let grouping = {};
      // let groupingRank = "";
      // let groupingValue = "N/A";
      grouping.rank = groupingJsonObj.rank;
      grouping.value = groupingJsonObj.value;
      grouping.groups = new Array();
      groupingJsonObj.groups.forEach(function(groupObj){
        grouping.groups.pushObject(fetchGroupFromJarResponse(groupObj));
      });
      return grouping;
}

function fetchGroupFromJarResponse(groupObj){
      let group = new Array();
      groupObj.ids.forEach(function(id){
          group.pushObject(id);
      });
      return group;
}

function displayNumberOfAgentsFromSpecifiedInput(){
  // TODO
}

function displaySolutions(jarExecutionResponse){
  // !!!! PARSING THE RESULT OF EXECUTION OF JAR FILE WHICH WAS RECEIVED FROM THE .../runJar ENDPOINT
  let jsonObj = JSON.parse(JSON.stringify(jarExecutionResponse));
  let jarExecResultObj = {};
  // FETCH RUNTIME OF EXECUTION FROM JSON RESULT
  if (jsonObj.runtime != undefined || jsonObj.runtime != "") {
    jarExecResultObj.runtime = jsonObj.runtime;
  }else {
    jarExecResultObj.runtime = "N/A";
  }

  // FETCH GROUPINGS FROM THE JSON RESULT IF THEY EXIST
  if (jsonObj.groupings != undefined) {
    jsonObj.groupings.forEach(function(groupingObj){
       jarExecResultObj = fetchGrouping(groupingObj, jarExecResultObj);
    });
  }else {
  // NO SOLUTION FOUND BY JAR AND RETURNED
    alert("Execution of the specified algorithm (JAR) didn't find any solutions");
  }
}
function fetchGrouping(groupingObj, jarResultObj){

}

function resetSelectedSolutionsInComparisonSection(){
  that.set('selectedSolutionsForComparison.mac', "");
  that.set('selectedSolutionsForComparison.macId', "");
  that.set('selectedSolutionsForComparison.central', "");
  that.set('selectedSolutionsForComparison.centralId', "");
}

function retrieveMacReposAndPushToUi(){
    that.get('store').findAll('macrepo').then((dataList) => {
      //deal with dataList like an array
      dataList.forEach((item) => {
        //the item is the exact model instance and
        //you can access to attributes like belo
        let name = item.get('name');
        that.macReposList.pushObject(name);
      });
    })
  }
