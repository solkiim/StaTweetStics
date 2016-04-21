var bodySelection = d3.select("body");

var svgContainer = bodySelection.append("svg")
  .attr("width", 100)
  .attr("height", 500);

var dataset = [];
var limit = 90;

// random integers from 0 to 99. Reasonable for our case?
for(var i = 0; i <= limit; i++) {
 dataset.push(Math.floor(Math.random()*100)) 
}

var w = 500;
var h = 100;
var padding = 1;
var scale = 1; // 4x scale

// create svg element
var svg = d3.select("#div-chart")
  .append("svg")
  .attr("width", w + 40)
  .attr("height", h + 40);

svg.selectAll("rect")
  .data(dataset)
  .enter()
  .append("rect")
  .attr("x", function(d,i){
    return i* (w/dataset.length);
  })
  .attr("y", function(d){ return h - d*scale; })
  .attr("width", w/dataset.length - padding)
  .attr("height", function(d){return d*scale;})
  .attr("fill", function(d){
    var r = 0;
    var g = d*5;
    var b = d*5;
    return "rgb(" +r+ "," +g+ "," +b+ ")";
  });


var xScale = d3.scale.linear()
  .domain([0, 90])
  .range([0, 500])
var yScale = d3.scale.linear()
  .domain([0, 100])
  .range([10, 90])

// Axis
var xAxis = d3.svg.axis()
              .scale(xScale)
              .orient("bottom")
              .ticks(10); // rough number of ticks
var yAxis = d3.svg.axis()
              .scale(yScale)
              .orient("left")
              .ticks(5);

var xAxisGroup = svg.append("g")
  .attr("class", "axis") // assign "axis" class
 .attr("transform", "translate(0, " + (h - padding) + ")")
  .call(xAxis);

var yAxisGroup = svg.append("g")
  .attr("class", "axis")
  .call(yAxis);