var bodySelection = d3.select("body");

var dataset = [];
var limit = 90;

// random integers from 0 to 99. Reasonable for our case?
for(var i = 0; i <= limit; i++) {
 dataset.push(Math.floor(Math.random()*200)) 
}

var margin = {top: 20, right: 20, bottom: 20, left: 25},
    width = 500
    height = 200;

var padding = 1;
var scale = 200/Math.max.apply(null, dataset); // 4x scale

// create svg element
var svg = d3.select("#div-chart")
  .append("svg")
  .attr("width", width + margin.left + margin.right)
  .attr("height", height + margin.top + margin.bottom)
  .append('g')
  .attr("transform", "translate(" + margin.left + "," + margin.top + ")");;

svg.selectAll("rect")
  .data(dataset)
  .enter()
  .append("rect")
  .attr("x", function(d,i){
    return i* (width/dataset.length);
  })
  .attr("y", function(d){ return height - d*scale; })
  .attr("width", width/dataset.length - padding)
  .attr("height", function(d){return d*scale;})
  .attr("fill", function(d){
    var r = 0;
    var g = d*5;
    var b = d*5;
    return "rgb(" +r+ "," +g+ "," +b+ ")";
  });

var xScale = d3.scale.linear()
  .domain([0, dataset.length - 1])
  .range([0, width])
var yScale = d3.scale.linear()
  .domain([Math.max.apply(null, dataset), 0])
  .range([0, height])

// Axis
var xAxis = d3.svg.axis()
              .scale(xScale)
              .orient("bottom")
              .ticks(10); // rough number of ticks
var yAxis = d3.svg.axis()
              .scale(yScale)
              .orient("left")
              .ticks(10);

var xAxisGroup = svg.append("g")
  .attr("class", "axis") // assign "axis" class
  .attr("transform", "translate(0, " + (height - padding) + ")")
  .call(xAxis);

var yAxisGroup = svg.append("g")
  .attr("class", "axis")
  .call(yAxis);