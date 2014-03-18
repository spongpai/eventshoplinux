function getDataFormatInfo(value)
{
	$('#simplemodal-container').css('height', 'auto');
	//$('#simplemodal-container').css('width', '450px');
	//alert(value);
	
	if(value=="stream")
	{
		//$("#dataFormatData").html($("#data").html());
		$("#visual").css("display", "none");
		$("#csv").css("display", "none");
		$("#data").css("display", "block");
		
		
	}
	else if(value=="visual")
	{
		
		var visualimageArr = [];
		$("#data").css("display", "none");
		$("#csv").css("display", "none");
		$("#visual").css("display", "block");
		$("#addTMPData,#addCMPData,#addMPData").css("display","inline");
		$("#addTMPData,#addCMPData,#addMPData").css("float","left");
		$("#viewTMPData").css("display","block");
		//$("#dataFormatData").html($("#visual").html());
	}
	else if(value=="csv")
	{
		$("#data").css("display", "none");
		$("#visual").css("display", "none");
		$("#csv").css("display", "block");
		
		//$("#dataFormatData").html($("#csv").html());
	}
	else
	{
		$("#data").css("display", "none");
		$("#visual").css("display", "none");
		$("#csv").css("display", "none");
	}
}



/*function ViewDataFormatInfo(value)
{
	$('#simplemodal-container').css('height', 'auto');
	//$('#simplemodal-container').css('width', '450px');
	//alert(value);
	
	if(value=="stream")
	{
		$("#viewDSDataFormat").html($("#viewData").html());
	}
	else if(value=="visual")
	{
		$("#viewDSDataFormat").html($("#viewVisual").html());
	}
	else if(value=="csv")
	{
		$("#viewDSDataFormat").html($("#viewCsv").html());
	}
	else
	{
		$("#viewDSDataFormat").html("");	
	}
}*/


$(document).ready(function () {
	
	/*$("#imgaddDS").click(function(){		
		
		$('#basic-modal-content').modal('show');		
		$('#basic-modal-content').css('height', 'auto');
		$('#basic-modal-content').css('width', 'auto');
		$("#DSTabs").tabs();
		//$('#basic-modal-content').css("display","block");
			
	});*/
	
	$("#viewDSimg").click(function(){
		$('#basic-modal-content').css('height', 'auto');
		//$('#basic-modal-content').css("display","block");
		$( "#DSTabs" ).tabs();	
	});
	$(document).on("click", "#datasourceTable tr ", function(event){
		// console.log(this.id);
		$('#basic-modal-content').css('height', 'auto');
		//$('#basic-modal-content').css("display","block");
		$( "#DSTabs" ).tabs();		
	});
	//$("#viewTMP").click(function(){
	
	$(document).on("click", "#viewTMP", function(event){		
		$('#tmpData').css("display","inline");
	}); 
	
});

	
jQuery(function ($) {
	
	// Close dialog on click
	$('#Ok').click(function (e) {
		$.modal.close();

		return false;
	});
});

jQuery(function ($) {
	

	// Close dialog on click
	$('#Ok').click(function (e) {
		$.modal.close();

		return false;
	});
});

jQuery(function ($) {
	
	// Close dialog on click
	$('#add').click(function (e) {
		$.modal.close();

		return false;
	});
});

jQuery(function ($) {
	
	// Close dialog on click
	$('#cancel').click(function (e) {
		$.modal.close();

		return false;
	});
});


jQuery(function ($) {
// Highlight row 
$('#dataTable tr').click(function () {
    $(this).find('td input:radio').prop('checked', true);
    $('#dataTable tr').removeClass("active");
    $(this).addClass("active");
});

});
 jQuery(function ($) {
 $("input[name$='one']").click(function() {
      var test = $(this).val();
	  $("#displayText").val(test);
    });
});

jQuery(function ($) {
// Highlight row 
$('#queryTable tr').click(function () {
    $(this).find('td input:radio').prop('checked', true);
    $('#queryTable tr').removeClass("active");
    $(this).addClass("active");
});

});

jQuery(function ($) {
// Add row on click
$('#add').click(function () {
    var original = $('#firstRow');
	original.clone().appendTo('#datasourceTable');
     $('#datasourceTable tr').removeClass("active");
    $(this).addClass("active");
});
});

/*jQuery(function ($) {
 $("#pagination").pagination({
        items: 100,
        itemsOnPage: 10,
        cssStyle: 'light-theme'
    });
});
*/

jQuery(function ($) {
 $('#maphide').change(function(){
 
       if ($(this).is(":checked")) {
	       $('#googleMap').hide();
       } else {
           $('#googleMap').show();
       }
   });
});


jQuery(function ($) {
 $('#chksptbounds').change(function(){
 
       if ($(this).is(":checked")) {
	       $('#spatBounds').show();
       } else {
           $('#spatBounds').hide();
       }
   });
});


 




jQuery(function ($) {
var data = [3, 6, 2, 7, 5, 2, 0, 3, 8, 9, 2, 5, 9, 3, 6, 3, 6, 2, 7, 5, 2, 1, 3, 8, 9, 2, 5, 9, 2, 7];
 var treeData = {"name" : "R", "info" : "tst", "children" : [
            {"name" : "Filter","children" :[{"name" :"D1"}]},
            {"name" : "D3" },
            {"name" : "Filter","children":[{
			"name":"D2"},
			 {"name" : "A3", "children": [
                  {"name" : "A31", "children" :[
            {"name" : "A311" },
            {"name" : "A312" }
    ]}] }
			]}
      ]};
	 
renderLineChart(data);
//drawOwn();
renderTreeChart(treeData);

});



function getMaxObjectValue(this_array, element) {
	var values = [];
	for (var i = 0; i < this_array.length; i++) {
			values.push(Math.ceil(parseFloat(this_array[i][""+element])));
	}
	values.sort(function(a,b){return a-b});
	return values[values.length-1];
}

function getMinObjectValue(this_array, element) {
	var values = [];
	for (var i = 0; i < this_array.length; i++) {
			values.push(Math.floor(parseFloat(this_array[i][""+element])));
	}
	values.sort(function(a,b){return a-b});
	return values[0];
}

jQuery(function ($) {

$('#chartList').change(function () {
var chartType=$('#chartList').val();
    if(chartType=='bar'){
	var data = [[1,1.5],[2,2],[3,2.5],[4,3],[5,3.5],[6,4],[7,4.5],[8,5],[1,5],[7,5]];
	chartonClick(data);
	}else if(chartType=='line'){
	var dataOne=[];
	chartonClick(dataOne);
	}else if(chartType=='pie'){
	var pieData = [{"value":20}, 
            {"value":10}, 
            {"value":30},
			{"value":7},
			{"value":9}];
	pieChart(pieData);
	}
	 
});
});


function filter (term, _id, cellNr){
	var suche = term.value.toLowerCase();
	var table = document.getElementById(_id);
	var ele;
	for (var r = 1; r < table.rows.length; r++){
		ele = table.rows[r].cells[cellNr].innerHTML.replace(/<[^>]+>/g,"");
		if (ele.toLowerCase().indexOf(suche)>=0 )
			table.rows[r].style.display = '';
		else table.rows[r].style.display = 'none';
	}
}

function validation (){

var userId=document.getElementById('userId');
var mailId=document.getElementById('userMailId');
var userPWD=document.getElementById('userPWD');
var fullName=document.getElementById('fullName');
mailVal=mailId.value;
atpos = mailVal.indexOf("@");
dotpos = mailVal.lastIndexOf(".");
if(userId.value==''){
	 alert( "Please provide user name!" );
     return false;
}

if(mailVal=='' ||atpos < 1 || ( dotpos - atpos < 2 )){
 alert( "Please provide valid mail id!" );
     return false;
	 
}

if(userPWD.value==''){
 alert( "Please provide the password!" );
     return false;
}

if(fullName.value==''){
 alert( "Please provide the fullname!" );
     return false;
}

if( userId.value!=='' && mailVal!=='' && userPWD.value!=='' && fullName.value!=='' ){
alert('User created successfully');
window.location.href="login.html";
return true;
}

}
function setCookie(c_name,value,exdays)
{
var exdate=new Date();
exdate.setDate(exdate.getDate() + exdays);
var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
document.cookie=c_name + "=" + c_value;
}
function getCookie(c_name)
{
var c_value = document.cookie;
var c_start = c_value.indexOf(" " + c_name + "=");
if (c_start == -1)
{
c_start = c_value.indexOf(c_name + "=");
}
if (c_start == -1)
{
c_value = null;
}
else
{
c_start = c_value.indexOf("=", c_start) + 1;
var c_end = c_value.indexOf(";", c_start);
if (c_end == -1)
{
c_end = c_value.length;
}
c_value = unescape(c_value.substring(c_start,c_end));
}
return c_value;
}

function logout(){
	var checkAdmin=getCookie("checkAdmin");
	setCookie("userName",'',-1);
	setCookie("id",'',-1);
	setCookie("roleId",'',-1);
	
	setCookie("checkAdmin",'',-1);
	if(checkAdmin==1){
		window.location.href="http://localhost:8080/eventshoplinux/#admin";
		location.reload(); 
		
	}else{
	  window.location.href="http://localhost:8080/eventshoplinux/";
	}
}

function loginValidation(){
var loginId=document.getElementById('loginId');
var loginPWD=document.getElementById('loginPWD');
var loginVal=loginId.value;
atpos = loginVal.indexOf("@");
dotpos = loginVal.lastIndexOf(".");


if(loginVal=='' ||atpos < 1 || ( dotpos - atpos < 2 )){
 alert( "Please provide valid mail id!" );
     return false;
	 
}

if(loginPWD.value==''){
 alert( "Please provide the password!" );
     return false;
}

if(loginVal!=='' && loginPWD.value!==''){
window.location.href="index.html";
return true;
}
}



/*jQuery(function ($) {
$( ".column" ).sortable({
      connectWith: ".column"
    });
 
    $( ".portlet" ).addClass( "ui-widget ui-widget-content ui-helper-clearfix ui-corner-all" )
      .find( ".portlet-header" )
        .addClass( "ui-widget-header ui-corner-all" )
        .prepend( "<span class='ui-icon ui-icon-minusthick'></span>")
        .end()
      .find( ".portlet-content" );
 
    $( ".portlet-header .ui-icon" ).click(function() {
      $( this ).toggleClass( "ui-icon-minusthick" ).toggleClass( "ui-icon-plusthick" );
      $( this ).parents( ".portlet:first" ).find( ".portlet-content" ).toggle();
    });
 
    $( ".column" ).disableSelection();
});



 jQuery(function ($) {
    $('.portlet-container').holseePortlets();
  });*/
  
  
  function renderLineChart(data){
		// define dimensions of graph
		var m = [80, 80, 80, 80]; // margins
		var w = 900 - m[1] - m[3]; // width
		var h = 400 - m[0] - m[2]; // height
		
		// create a simple data array that we'll plot with a line (this array represents only the Y values, X will just be the index location)
		//var data = [3, 6, 2, 7, 5, 2, 0, 3, 8, 9, 2, 5, 9];
		//var data = [3, 6, 2, 7, 5, 2, 0, 3, 8, 9, 2, 5, 9, 3, 6, 3, 6, 2, 7, 5, 2, 1, 3, 8, 9, 2, 5, 9, 2, 7];


		// X scale will fit all values from data[] within pixels 0-w
		var x = d3.scale.linear().domain([0, data.length]).range([0, w]);
		// Y scale will fit values from 0-10 within pixels h-0 (Note the inverted domain for the y-scale: bigger is up!)
		var y = d3.scale.linear().domain([0, 10]).range([h, 0]);
			// automatically determining max range can work something like this
			// var y = d3.scale.linear().domain([0, d3.max(data)]).range([h, 0]);

		// create a line function that can convert data[] into x and y points
		var line = d3.svg.line()
			// assign the X function to plot our line as we wish
			.x(function(d,i) { 
				// verbose logging to show what's actually being done
				//console.log('Plotting X value for data point: ' + d + ' using index: ' + i + ' to be at: ' + x(i) + ' using our xScale.');
				// return the X coordinate where we want to plot this datapoint
				return x(i); 
			})
			.y(function(d) { 
				// verbose logging to show what's actually being done
				//console.log('Plotting Y value for data point: ' + d + ' to be at: ' + y(d) + " using our yScale.");
				// return the Y coordinate where we want to plot this datapoint
				return y(d); 
			})

			// Add an SVG element with the desired dimensions and margin.
             // d3.select('svg').remove();
			 d3.select("#graph").html('');
      		var graph = d3.select("#graph").append("svg:svg")
			      .attr("width", w + m[1] + m[3])
			      .attr("height", h + m[0] + m[2])
			    .append("svg:g")
			      .attr("transform", "translate(" + m[3] + "," + m[0] + ")");

			// create yAxis
			var xAxis = d3.svg.axis().scale(x).tickSize(-h).tickSubdivide(true).tickFormat(function(d) { return d3.time.format("%b-%Y")(new Date(d)); });;
			//var xAxis = d3.svg.axis().scale(x).tickSize(-h).tickValues([1, 2, 3, 5, 8, 13,1, 2, 3, 5, 8, 13,2, 3, 5, 8, 13]);
			//var xAxis = d3.svg.axis().scale(x).tickSize(-h).tickValues(1,2,3,4,5,6,7);

        
			// Add the x-axis.
			graph.append("svg:g")
			      .attr("class", "x axis")
			      .attr("transform", "translate(0," + h + ")")
			      .call(xAxis);

	/*	graph.append("text")
	    .attr("class", "axis-label")
	    .attr("text-anchor", "end")
	    .attr("x", 20)
	    //.attr("y", height + 34)
	    .text('Date'); */


			// create left yAxis
			var yAxisLeft = d3.svg.axis().scale(y).ticks(4).orient("left");
			// Add the y-axis to the left
			graph.append("svg:g")
			      .attr("class", "y axis")
			      .attr("transform", "translate(-25,0)")
			      .call(yAxisLeft);
			
  			// Add the line by appending an svg:path element with the data line we created above
			// do this AFTER the axes above so that the line is above the tick-lines
			graph.append("svg:path").attr("d", line(data));
			// d3.select('svg').remove();
}
function renderTreeChart(){

}
function changeTreeValue(e){
var index = document.getElementById("sSource").options;
//var indexVal = index.options[index.selectedIndex].value;
var str=[];
var treeData;
for(var i=0;i<index.length;i++){
if(index[i].selected){
str =index[i].value;

}

/*if((str=='d1') && (str=='d2')){
alert('test');
 var treeData = {"name" : "R", "info" : "tst", "children" : [
                {"name" : "D1" },
				{"name" : "D2" }
            
      ]};
}*/
/*if(str=='d3' || str=='d4'){
 var treeData = {"name" : "R", "info" : "tst", "children" : [
                {"name" : "D3" },
				{"name" : "D4" }
            
      ]};

} */


}

//renderTreeChart(treeData);
} 


/************* QUery Graph Codes Starts Here ****************/
 // Variable Declaration
 var datasourceList = new Array();
 var resultArray = new Array();
 var selectedDSArray = new Array();
 var tmpGrpValue="";
 var tmpAggValue="";


//Common Functions for Generating Query Graph
//This function is used to whether the element is available in a array 
Array.prototype.contains = function(obj) {
    var i = this.length;
    while (i--) {
        if (this[i] == obj) {
            return true;
        }
    }
    return false;
}

//This function is used to whether the element is available in a array for IE8
if(!Array.prototype.indexOf) {
    Array.prototype.indexOf = function(needle) {
        for(var i = 0; i < this.length; i++) {
            if(this[i] === needle) {
                return i;
            }
        }
        return -1;
    };
}

//This function is used to Find and Remove json array object the element 
function removeDataSource(array, property, value) {
   $.each(array, function(index, result) {
      if(result[property] == value) {
          //Remove from array
          array.splice(index, 1);
      }    
   });
}

function RemoveResultData(resArray,data)
{
	var deleteIndex = resArray.indexOf(data);
	if(deleteIndex != -1)
	{
		resArray.splice(deleteIndex, 1);	
	}
	return resArray
}

//This function is used to rendering the Query Graph based on DataSource
 function renderQueryGraph(dataSourceInfo,resultData){
 	
 	var width = 650;
    var height = 175;
    var cx=0;var cy=0;
 	var tmpGY=0,tmpFY=0;
    //Testing Purpose
    Graph.Layout.Fixed = function(graph) {
        this.graph = graph;
        this.layout();
        };
        Graph.Layout.Fixed.prototype = {
        layout: function() {
        this.layoutPrepare();
        this.layoutCalcBounds();
        },

        layoutPrepare: function() {
        for (i in this.graph.nodes) {
        var node = this.graph.nodes[i];
        if (node.x) {
        node.layoutPosX = node.x;
        } else {
        node.layoutPosX = 0;
        }
        if (node.y) {
        node.layoutPosY = node.y;
        } else {
        node.layoutPosY = 0;
        }
        }
        },

        layoutCalcBounds: function() {
        var minx = Infinity, maxx = -Infinity, miny = Infinity, maxy = -Infinity;

        for (i in this.graph.nodes) {
        var x = this.graph.nodes[i].layoutPosX;
        var y = this.graph.nodes[i].layoutPosY;
    
        if(x > maxx) maxx = x;
        if(y > maxy) maxy = y;
        if(y < miny) miny = y;
        if(x < minx) minx = x;
        }
       //minx=20;
        this.graph.layoutMinX = minx;
        this.graph.layoutMaxX = maxx;

        this.graph.layoutMinY = miny;
        this.graph.layoutMaxY = maxy;
        }
        };
    //Testing Purpose	
    var g = new Graph();
    g.edgeFactory.template.style.directed = true;

    var render = function(r, n) {
    var label = r.text(0, 30, n.label).attr({opacity:0});
    var yourSet=r.set();
    //the Raphael set is obligatory, containing all you want to display 
    var set = r.set().push(
        r.rect(-30, -13, 52, 26)
          .attr({"fill": "#fa8",
                "stroke-width": 2
                , r : 5}))
      .push(r.text(-5, 0, n.label).attr({"fill": "#000000"}));
      return set;
  		};

  	$.each(datasourceList , function() {
		// Label Name to Make "START"
		if(this.label=="DS0")
		{
			labelName="START";
			cx= 0;cy=250;
		}
		else
		{
			if(this.type=="Filter")
			{
				cx=50;
				tmpFY=tmpFY+75;
				cy=tmpFY;
			}
			else
			{
				cx=100;
				tmpGY=tmpGY+75;
				cy=tmpGY;
			}
			labelName=this.label;	
		}
		// Adding the Node
		g.addNode(this.label,{x:cx, y:cy, label : labelName, render : render});
		if (this.edge.length > 0) {
			for (j=0;j<this.edge.length;j++) {	
				//To make a connection between two nodes
				g.addEdge(this.edge[j][0],this.edge[j][1],{stroke : this.color});
			}
		}
    });
	// to make a connection between Result and other Nodes
	if(resultData.length>0)
	{
		g.addNode("RESULT",{x:170,y:250,label:"RESULT",directed : false, render : render})
		for(var k=0; k< resultData.length;k++)
		{
			g.addEdge(resultData[k],"RESULT",{stroke:"black"});	
		}
	}
	//Empty the Query Graph Area	
    $("#canvas").html(" ");
    //Generating a Query Graph
    //var layouter = new Graph.Layout.Ordered(g, topological_sort(g));
    //Testing Purpose
    var layouter = new Graph.Layout.Fixed(g, topological_sort(g));
    var renderer = new Graph.Renderer.Raphael('canvas', g, width, height);
} 

function addDataSourceList(value,rootNode,connection,color,type)
{
 	var datasource = new Object();
	datasource.label =value;
	datasource.type =type;
	datasource.edge = [];
	datasource.shape = "dot";
	datasource.color = color;
	if(rootNode != "NULL")
	{
		datasource.edge.push([rootNode,value]);
	}
	if(connection != "NULL")
	{
		for(var k=0;k<connection.length;k++)
		{
			datasource.edge.push([connection[k],value]);
		}
	}
	
	datasourceList.push(datasource);
}

function nodeDynamic(){
 
	datasourceList = [];
	var datasource = new Object();
	datasource.label ="DS0";
	datasource.edge = [];
	datasource.shape = "dot";
	datasource.color = "green";
	datasourceList.push(datasource);		

	$('#sSource option:selected').each(function(){
	       	var selectedValues =$(this).val();
	        selectedDSArray.push(selectedValues);
			resultArray.push(selectedValues);
			addDataSourceList(selectedValues,"DS0","NULL","green","Filter");
	});
	renderQueryGraph(datasourceList,resultArray);
}

function addGroupby(){
	var grpSelectedArray= new Array();
	if(tmpGrpValue != "")
	{
		removeDataSource(datasourceList, 'label', tmpGrpValue);
		if(selectedDSArray.contains(tmpGrpValue) == true)
		{
			delete(selectedDSArray[tmpGrpValue]);
		}
		//deleting elements from the Result Array because to made a connection between datasources and result
		RemoveResultData(resultArray,tmpGrpValue);

	}	
	var index = document.getElementById("groupBy");
	var indexVal = index.options[index.selectedIndex].value;
	tmpGrpValue=indexVal;
	$('#gSource option:selected').each(function(){
		var GrpselectedValues =$(this).val()
	    if(selectedDSArray.contains(GrpselectedValues)==false){
	    	addDataSourceList(GrpselectedValues,"DS0","NULL","red","Filter"); 
			selectedDSArray.push(GrpselectedValues);
			resultArray.push(GrpselectedValues); 
		}
		else if(selectedDSArray.indexOf(GrpselectedValues) == -1)
		{
			addDataSourceList(GrpselectedValues,"DS0","NULL","red","Filter"); 
			resultArray.push(GrpselectedValues);
	    	selectedDSArray.push(GrpselectedValues); 
		}
		//deleting elements from the Result Array because to made a connection between datasources and result
		RemoveResultData(resultArray,GrpselectedValues);
		grpSelectedArray.push(GrpselectedValues);
	});
	addDataSourceList(indexVal,"NULL",grpSelectedArray,"red","Group");
	resultArray.push(indexVal);
	renderQueryGraph(datasourceList,resultArray);
}

function addAggregation(){
	var aggSelectedArray= new Array();
	if(tmpAggValue != "")
	{
		removeDataSource(datasourceList, 'label', tmpAggValue);
		if(selectedDSArray.contains(tmpAggValue) == true)
		{
			delete(selectedDSArray[tmpAggValue]);
		}
		RemoveResultData(resultArray,tmpAggValue);
		
	}
	var Aggindex = document.getElementById("aggBy");
	var AggindexVal = Aggindex.options[Aggindex.selectedIndex].value;
	tmpAggValue=AggindexVal;
	$('#aSource option:selected').each(function(){
		var aggSelectedValues =$(this).val()
	    if(selectedDSArray.contains(aggSelectedValues)==false){
	    	addDataSourceList(aggSelectedValues,"DS0","NULL","blue","Filter"); 
	    	selectedDSArray.push(aggSelectedValues);
	    	resultArray.push(aggSelectedValues); 
		}
		//IE8  
		else if(selectedDSArray.indexOf(aggSelectedValues) == -1)
		{
			addDataSourceList(aggSelectedValues,"DS0","NULL","blue","Filter"); 
	    	selectedDSArray.push(aggSelectedValues);
	    	resultArray.push(aggSelectedValues); 
		}
		aggSelectedArray.push(aggSelectedValues);
		RemoveResultData(resultArray,aggSelectedValues);
		
	});
	addDataSourceList(AggindexVal,"NULL",aggSelectedArray,"blue","Aggregation");
	resultArray.push(AggindexVal);
	
	renderQueryGraph(datasourceList,resultArray);
}



/************* QUery Graph Codes Ends Here ****************/
