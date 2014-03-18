define([
  'jquery',
  'underscore',
  'backbone',
  'jqueryui',  
  'dalgorithms',
  'ddalgorithms',
  'dgraffle',
  'dgraph',
  'modal',
  'paginate',
  'sortable',  
  'queryGraph',
  'd3',
  'topojson',
  'basic',
  'bootMin',
  'datetimepicker',
  'text!templates/user/userHome.html',
  'mvc/user/models/session',
  'mvc/user/views/query/filterOp',
  'mvc/user/views/query/groupingOp',
  'mvc/user/views/query/aggregationOp',
  'mvc/user/views/query/spatialPatternOp',
  'mvc/user/views/query/temporalPatternOp',
  'mvc/user/views/query/spatialCharOp',
  'mvc/user/views/query/temporalCharOp',
  'mvc/user/views/datasource/registerDatasource',
  'mvc/user/views/datasource/addDatasource',
  'mvc/user/models/datasource',
  'mvc/user/collections/datasources',
  'mvc/user/views/query/registerQuery',
  'mvc/user/models/query',
  'mvc/user/views/query/queryGraph',
  'mvc/user/collections/queries',
 
], function($, _, Backbone,jqueryui,dalgorithms,ddalgorithms,dgraffle,dgraph,modal,paginate,sortable,queryGraph,d3,topojson,basic,bootMin,datetimepicker,
  homePageTemplate,sessionModel,filterOpView,groupingOpView,aggregationOpView,spatialPatternOpView,temporalPatternOpView,spatialCharOpView,
  temporalCharOpView,registerDatasource,addDatasourceView,dataSourceModel,dataSourceCollection,registerQueryView,queryModel,queryGraphView,queryListCollection){
  
  var homePageView = Backbone.View.extend({
  
    el: $("body"),
    template: homePageTemplate,
      
     initialize: function(opts){
      
     this.app_router = opts.router;          
       
     this.queryModel = new queryModel();
     this.queryListCollection = new queryListCollection();
     
      
     this.datasourceModel = new dataSourceModel();
     this.sessionModel = new sessionModel();    
     var loc = this.sessionModel.checkSession();

     if(loc == "login") {        
         window.location.hash = "";
         this.app_router.navigate("*actions",true);
         window.location.reload();
      }
       
     
      this.generatedQueriesArr = [];
      this.queryCount = 1;
      this.dataSourceCollection = new dataSourceCollection();
      
      
    },
    events :{       
      "click #addDataSourceBtn":"validateAddDataSource",
      "click #q_play":"runQuery",
      "click #playDS img":"runDs",
      "click #q_stop":"stopQuery",
      "click #imgaddDS":"showViewDSModal", 
      "click #datasourceTable td span":"showViewDSModal", 
      "click #exeQuery":"queryExecute" ,
     /* "click #resetQueryValues": "resetQuery",*/
      
     },

    render: function(){     
    
     $("#page").css("display","none");     
     this.$el.html(this.template); 
     
     this.datasourceUpdate();
     this.queryListUpdate();
     this.subRender();
    
     this.queryGraphView = new queryGraphView({router:this, generatedQueriesArr:this.generatedQueriesArr, queryCount:this.queryCount});
     this.queryGraphView.setElement('#flowGraph').render();
     
     this.registerDatasource = new registerDatasource({router:this, sessionModel:this.sessionModel});
     this.registerDatasource.setElement('#dsTableContainer').render();
          
     this.registerQueryView = new registerQueryView({router:this, sessionModel:this.sessionModel});
     this.registerQueryView.setElement('#queryTableContainer').render();
     
     return this;
    },
    
    datasourceUpdate : function() {
      var dsOptions = '';
       this.dataSourceCollection.fetch({
         data: $.param({"userId":this.sessionModel.id}),
         success: function(dataSourceCollection){        
            var q_temp = JSON.parse(JSON.stringify(dataSourceCollection));
            $.each(q_temp, function(i, obj) {
            dsOptions += '<option value="ds'+obj['srcID']+'">DS'+obj['srcID']+':'+obj['srcName']+'</option>';   
              });
            
            $('#sSource').html(dsOptions);
            $('#sSource').multiSelect('refresh');
          
           
            $('#gSource').html(dsOptions);
            $('#gSource').multiSelect('refresh');
            $('#aSource').html(dsOptions);
            $('#aSource').multiSelect('refresh');
            $('#pSource').html(dsOptions);
            $('#pSource').multiSelect('refresh');
            $('#tSource').html(dsOptions);
            $('#tSource').multiSelect('refresh');
            $('#spaceCharSource').html(dsOptions);
            $('#spaceCharSource').multiSelect('refresh');
            $('#tempCharSource').html(dsOptions);
            $('#tempCharSource').multiSelect('refresh');
            
            
            
         }
         
       });
      
    },
    
    queryListUpdate : function() {
    	var queryOptions = '';
    	 this.queryListCollection.fetch({
  		   data: $.param({"userId":this.sessionModel.id}),
  		   success: function(queryListCollection){
  			 var self = this;
  			    var q_temp = JSON.parse(JSON.stringify(queryListCollection));
  			    $.each(q_temp, function(i, obj) {
  			    	queryOptions += '<option value="ds'+obj['qID']+'">DS'+obj['qID']+':'+obj['queryName']+'</option>'; 	
  		        });
  			    }
  		   
  	   });
    	
    },
    subRender: function() {
      
       this.filterOpView = new filterOpView({router:this, generatedQueriesArr:this.generatedQueriesArr, queryCount:this.queryCount,dsOptions:this.dsOptions});
         this.filterOpView.setElement('#tabs-1').render();
         
         this.groupingOpView = new groupingOpView({router:this, generatedQueriesArr:this.generatedQueriesArr, queryCount:this.queryCount});
         this.groupingOpView.setElement('#tabs-2').render();
         
         this.aggregationOpView = new aggregationOpView({router:this, generatedQueriesArr:this.generatedQueriesArr, queryCount:this.queryCount});
         this.aggregationOpView.setElement('#tabs-3').render();
         
         this.spatialPatternOpView = new spatialPatternOpView({router:this, generatedQueriesArr:this.generatedQueriesArr, queryCount:this.queryCount});
         this.spatialPatternOpView.setElement('#tabs-4').render();
         
         this.temporalPatternOpView = new temporalPatternOpView({router:this, generatedQueriesArr:this.generatedQueriesArr, queryCount:this.queryCount});
         this.temporalPatternOpView.setElement('#tabs-5').render();
         
         this.spatialCharOpView = new spatialCharOpView({router:this, generatedQueriesArr:this.generatedQueriesArr, queryCount:this.queryCount});
         this.spatialCharOpView.setElement('#tabs-6').render();
         
         this.temporalCharOpView = new temporalCharOpView({router:this, generatedQueriesArr:this.generatedQueriesArr, queryCount:this.queryCount});
         this.temporalCharOpView.setElement('#tabs-7').render();
         
    },
  
    queryExecute: function(){
      var timeWindow = $('#dsFTimeP').val();
      var dsFSyncP = $('#dsFSyncP').val();
      var latitudeUnit = $('#dsFUnitLatP').val();
      var longitudeUnit = $('#dsFUnitLongP').val();
      
      var dsFSouthLatP = $('#dsFSouthLatP').val();
      var dsFSouthLongP = $('#dsFSouthLongP').val();
      var dsFNorthLatP = $('#dsFNorthLatP').val();
      var dsFNorthLongP = $('#dsFNorthLongP').val();
      
var finaltabparms1={};      
        finaltabparms1.dataSrcID=  $("#sSource").val();
        finaltabparms1.SelectedDsaSource=  $("#aSource").val();
        finaltabparms1.SelectedDsgSource=  $("#gSource").val();
        finaltabparms1.SelectedDsspaceCharSource=  $("#spaceCharSource").val();
        finaltabparms1.SelectedDspSource=  $("#pSource").val();
        finaltabparms1.SelectedDstempCharSource=  $("#tempCharSource").val();
        finaltabparms1.SelectedDstSource=  $("#tSource").val();
        finaltabparms1.valRange=[];
        finaltabparms1.valRange[0]= $('#valRangeMin').val();
        finaltabparms1.valRange[1]=$('#valRangeMax').val();
        finaltabparms1.normVals=[];
	    finaltabparms1.normVals[0] = $('#normValsMin').val();
	    finaltabparms1.normVals[1] = $('#normValsMax').val();
        finaltabparms1.FtemporalBoundsInMethod = $('#temporalBoundsInMethod').val();
        finaltabparms1.timeRange = $('#timeRangeSecs').val();
        finaltabparms1.Fdatepicker = $('#datepicker').val();
        finaltabparms1.Fdatepicker1 = $('#datepicker1').val();
        finaltabparms1.maskMethod = $('#maskMethodVal').val();
        finaltabparms1.method = $('#groupBy').val();
        finaltabparms1.numGroup = $('#num_of_groups').val();
        finaltabparms1.split = $('#seperate_group_images').val();
        finaltabparms1.doColoring = $('#group_in_colors').val();
        finaltabparms1.colorCodes=[];
        finaltabparms1.colorCodes[0] = $('#group_colors').val();
      
      
      finaltabparms1.thresholds=[];
      finaltabparms1.thresholds[0] = $('#threshol_vals').val();
      
      finaltabparms1.values=[];
      
      finaltabparms1.values[0] = $('#aggNormMin').val();
      finaltabparms1.values[1] = $('#aggNormMax').val();
      finaltabparms1.aggOperator = $('#aggBy').val();
      finaltabparms1.sizeNorm = $('#sp_size_norm').val();
      finaltabparms1.SPsp_value_norm = $('#sp_value_norm').val();
      finaltabparms1.patternSrc = $('#sp_pattern_type').val();
      finaltabparms1.TPtp_norm_pattern = $('#tp_norm_pattern').val();
        finaltabparms1.TPtp_norm_value = $('#tp_norm_value').val();
        finaltabparms1.TPtp_time_window = $('#tp_time_window').val();
        finaltabparms1.TPtp_pattern_type = $('#tp_pattern_type').val();
        finaltabparms1.spCharoperator = $('#sc_spatial_char_operator').val();
        finaltabparms1.timeWindow = $('#date_time_window').val();
        finaltabparms1.tmplCharOperator = $('#tc_char_operator').val();
      finaltabparms1.placeName = $('#placeName').val();
    finaltabparms1.numRows = $('#sp_num_rows').val();
    finaltabparms1.numCols = $('#sp_num_cols').val();
    finaltabparms1.gaussianCenter = $('#gs_center').val();
    finaltabparms1.gaussianDeviation = $('#gs_sd').val();
    finaltabparms1.gaussianAmplitude = $('#gs_amp').val();
    finaltabparms1.linearStartPosition = $('#ln_start_position').val();
    finaltabparms1.linearStartValue = $('#ln_start_value').val();
    finaltabparms1.linearDirection = $('#ln_direct_gradient').val();
    finaltabparms1.patternSamplingRate = $('#tp_samp_rate').val();
    finaltabparms1.patternDuration = $('#tp_samp_dur').val();
    finaltabparms1.patternSamplingRate = $('#tp_samp_rate').val();
  //  finaltabparms1.templinearParam.slope = $('#ln_slope').val();
    //finaltabparms1.templinearParam.yIntercept = $('#ln_intercept').val();
    //finaltabparms1.expParam.base = $('#exp_base_value').val();
    //finaltabparms1.expParam.scale = $('#exp_scale_factor').val();
    //finaltabparms1.periodicParam.frequency = $('#pr_frq').val();
    //finaltabparms1.periodicParam.amplitude = $('#pr_amplitude').val();
    //finaltabparms1.periodicParam. phaseDelay = $('#pr_phase_delay').val();
    //finaltabparms1.filePath = $('#filterfilef').val();
    finaltabparms1.filePath = $('#file_data_st').html();
    finaltabparms1.filePathSpatial = $('#file_data1_pattern').html();
    finaltabparms1.QueryName1 = $('#QueryName1').val();
    
   
     //finaltabparms1.coordrs=[];
  
   finaltabparms1.coordrs = $('#latlong2').val();
   finaltabparms1.mapcords = $('#map_data').val();
   
   if($("#QueryName1").val()==""){
	   alert("Please enter the Query name");
	   return false;
   }
   
    
     console.log(finaltabparms1);
    
      var queryStatus = "S";      
      
      var qryCreatorId = this.sessionModel.id;
      var boundingBox = dsFSouthLongP+','+dsFNorthLatP+','+dsFSouthLatP+','+dsFNorthLongP;
      
      var finalObjArr = [];
      $.each(this.generatedQueriesArr, function(i, obj) {    
               
          obj['queryName'] =   finaltabparms1.QueryName1;       
          obj['timeWindow'] = timeWindow;
          obj['latitudeUnit'] = latitudeUnit;
          obj['longitudeUnit'] = longitudeUnit;
          obj['queryStatus'] = queryStatus;
          obj['qryCreatorId'] = qryCreatorId;           
          obj['boundingBox'] = boundingBox;
          obj['timeRange'] = [1,1]; // hardcoding for now as the field doesnt exist in UI -- sanjukta
          
          finalObjArr.push(obj);
            
        });
      
          
      var finalStr = finalObjArr.join();      
      
      this.queryModel.set({query:finalObjArr});
      this.queryModel.save(null,
            {   
    	    //url: "/eventshoplinux/webresources/queryservice/createQuery",
    	    type:"POST",
    	    
            success: function (queryModel,responseText) {                  
              alert("Success Text: "+responseText); // It will be invoked if you pass only json from server
              $("#toPopup2").fadeOut("normal");  
              $("#backgroundPopup2").fadeOut("normal");
            },
            error: function(queryModel,responseText) {
              
              $("#toPopup2").fadeOut("normal");  
              $("#backgroundPopup2").fadeOut("normal");
            }
                
            });
         this.queryListUpdate();
		 this.registerQueryView.fetchquery();
      
    },
    /*resetQuery: function(){
    	//$("#sSource option:selected").removeAttr("selected");
         $('#sSource').multiSelect('refresh');
       
        
       
         $('#gSource').multiSelect('refresh');
     
         $('#aSource').multiSelect('refresh');
        
         $('#pSource').multiSelect('refresh');
       
         $('#tSource').multiSelect('refresh');
         
         $('#spaceCharSource').multiSelect('refresh');
       
         $('#tempCharSource').multiSelect('refresh');
    	$("#filterValues input").val(null);
    	 $("#temporalBoundsInMethod")[0].selectedIndex=0;
    	$("#groupingOpValues input").val(null);
    	$("#aggregationOpValue input").val(null);
    	$("#spatialPatternOpValues input").val(null);
    	$("#temporalPatternValue input").val(null);
    	$("#spatialCharOpValues input").val(null);
    	$("#temporalCharOpValue input").val(null);
      // $('.filterValues').reset();
       alert("hello");
      
     //console.log(finaltabparms1);
    },*/
    runDs: function(e){

      var dsID = [];
      
      $("input[name='cboxDS[]']:checked").each(function(i){       
           dsID[i] = $(this).val();
        });
      
      $.ajax({
        type: "POST",
        url: "http://localhost:8080/eventshoplinux/register",
        data: { "type": "d", "dsID": dsID.join(',') },
        success : function(data){
          //disable the run if its already running now
           console.log("successss");
       
         }
        });
      e.preventDefault();
     
    },

     runQuery: function(e){
      var qID = [];
        
      $("input[name='cboxQR[]']:checked").each(function(i){       
        qID[i] = $(this).val();
    });
        
      console.log("arr "+qID.join(','));  
      
      $.ajax({
        type: "POST",
        url: "http://localhost:8080/eventshoplinux/register",
        data: { "type": "qidstart", "qIDList": qID.join(',') },
        success : function(output){
          
        mapDataLoader(output);
          
         },
         error : function(output){
           var data="";          
          // type: "qidstop",
         /* $("#q_stop"+qID).attr('src','');
          $("#q_stop"+qID).attr('class','img_class_stop'); 
          $("#q_stop"+qID).attr('id','stop_'+qID);*/                
         }
        
        });
      e.preventDefault();
      
    
    },    
     stopQuery: function(e){
      var qID = [];
        
      $("input[name='cboxQR[]']:checked").each(function(i){       
        qID[i] = $(this).val();
    });
        
      
      $.ajax({
        type: "GET",
        url: "http://localhost:8080/eventshoplinux/register",
        data: { "type": "qidstop", "qIDList": qID.join(',') },
        sucess : function(output){
          
          if(output.length=="")
            {
            
            var data ="";
            }
          
          else
            
           data = output; // variable for the servlet use
         },
         error : function(output){
           
           var data="";
           
           //type: "qidstop",
       /* $("#q_stop"+qID).attr('src','');
        $("#q_stop"+qID).attr('class','img_class_stop'); 
        $("#q_stop"+qID).attr('id','stop_'+qID);  */
          }
        
        });
      e.preventDefault();
    },
    
    
    
    
    
    
    
    showMap: function(){
      
      var getDsJson = new getMapDsModel({id:21});
      getDsJson.toJSON();
      getDsJson.fetch({success:function(getDsJson){
        
    }});
      
    },
    showAddDSModal:function(){
      
    },
    showViewDSModal:function(ev){
      
      if ($(ev.target).attr('id') == 'imgaddDS') {
        
        var viewDSList = new dataSourceModel({id:DSID});  
        var viewDSAttributes = {};
        $('input').val('');
        $('#addDataSourceBtn').val("Add");
        
      } else {
        
      var DSID = $(ev.target).attr('id');
      $('#addDataSourceBtn').val("Save");     
      
      $('#dataSourceId').val(DSID);
      var viewDSList = new dataSourceModel({id:DSID});            
      viewDSList.toJSON();      
      viewDSList.fetch({success:function(){         
        var viewDSAttributes = viewDSList.attributes;
        $('#dsTheme').val(viewDSAttributes.srcTheme);
        $('#dsName').val(viewDSAttributes.srcName);
        $('#dsPath').val(viewDSAttributes.url);
        $('#dsTime').val(viewDSAttributes.initParam.timeWindow);
        $('#dsSync').val(viewDSAttributes.initParam.syncAtMilSec);
  
        $('#dsUnitLat').val(viewDSAttributes.initParam.latUnit);
        $('#dsUnitLong').val(viewDSAttributes.initParam.longUnit);
        $('#dsSouthLat').val(viewDSAttributes.initParam.swLat);
        $('#dsSouthLong').val(viewDSAttributes.initParam.swLong);
        $('#dsNorthLat').val(viewDSAttributes.initParam.neLat);
        $('#dsNorthLong').val(viewDSAttributes.initParam.neLong);
        $('#dsFTime').val(viewDSAttributes.finalParam.timeWindow);
        $('#dsFSync').val(viewDSAttributes.finalParam.syncAtMilSec);
        $('#dsFUnitLat').val(viewDSAttributes.finalParam.latUnit);
        $('#dsFUnitLong').val(viewDSAttributes.finalParam.longUnit);
        $('#dsFSouthLat').val(viewDSAttributes.finalParam.swLat);
        $('#dsFSouthLong').val(viewDSAttributes.finalParam.swLong);
        $('#dsFNorthLat').val(viewDSAttributes.finalParam.neLat);
        $('#dsFNorthLong').val(viewDSAttributes.finalParam.neLong);
          if(viewDSAttributes.srcFormat=="stream")
        {
          $('#dataFormat').val(viewDSAttributes.srcFormat);
          //$("#dataFormatData").html($("#data").html());
          document.getElementById('dfTransPath').addEventListener('change', readSingleFile, false);
          $('#dfSupport').val(viewDSAttributes.supportedWrapper);
          $('textarea#dfTextArea').attr("value", viewDSAttributes.bagOfWords.join(","));
        }
        else if(viewDSAttributes.srcFormat=="visual")
        {
          
          var visual=viewDSAttributes.visualParam;
          var visualTransTable="<table>";
          if (visual.translationMatrix != null) {
            for(var i=0;i< visual.translationMatrix.row;i++)
            {
              visualTransTable +='<tr>';
              for(var k=0;k<visual.translationMatrix.column;k++)
              {
                visualTransTable +="<td>"+visual.translationMatrix.matrix[i][k]+"</td>";
              }
              visualTransTable +='</tr>';
            }
                } else {
                    visualTransTable +='<tr><td></td></tr>';
                }

          visualTransTable +="</table>";
          
          $("#tmpData").html(visualTransTable);
          
        }
      }});               
      
      } // if not imgaddDS
      //Start Code to display popup         
      $("div.loader").show();     
      //var popupStatus = 0; // set value
      //if(popupStatus == 0) { // if value is 0, show popup
        $("div.loader").fadeOut('normal'); // fadeout loading
        $("#toPopup").fadeIn(0500); // fadein popup div
        $("#backgroundPopup").css("opacity", "0.7"); // css opacity, supports IE7, IE8
        $("#backgroundPopup").fadeIn(0001); 
       
      //End code to display popup
      
      
      
      $("div#backgroundPopup, div.close").click(function() {
        
          $("#toPopup").fadeOut("normal");  
          $("#backgroundPopup").fadeOut("normal");  
          
      });
        
    },    
    validateAddDataSource:function(){
      var self = this;
      if($('#dsTheme').val()=="")
      {
        alert("Please Enter The Theme");
        return false;
      }
      else if($('#dsName').val()=="")
      {
        alert("Please Enter The Name");
        return false;
      }
      else if($('#dsPath').val()=="")
      {
        
        alert("Please Enter The URL");
        return false;
      }
      else if(!(/^(http|ftp|https):\/\/(www\.)[A-Za-z0-9_-]+\.+[A-Za-z0-9.\/%&=\?_:;-]+$/.test($('#dsPath').val()))){
        
        alert("Please Enter Valid URL");
        return false;
      }
      else if($('#dsTime').val()=="" || !(/^[0-9]+$/.test($('#dsTime').val())))
      {
        alert("Please Enter The Time Window in Sec");
        return false;
      }
      else if($('#dsSync').val()=="" || !(/^[0-9]+$/.test($('#dsSync').val())))
      {
        alert("Please Enter The Synchronize at nth Sec");
        return false;
      }
      else if($('#dsUnitLat').val()=="" || !(/^[0-9]+$/.test($('#dsUnitLat').val())))
      {
        alert("Please Enter The Unit of Latitude");
        return false;
      }
      else if($('#dsUnitLong').val()=="" || !(/^[0-9]+$/.test($('#dsUnitLong').val())))
      {
        alert("Please Enter The Unit of Longitude");
        return false;
      }
      else if($('#dsSouthLat').val()=="" || !(/^[0-9]+$/.test($('#dsSouthLat').val())))
      {
        alert("Please Enter The Southwest of Latitude");
        return false;
      }
      else if($('#dsSouthLong').val()=="" || !(/^[0-9]+$/.test($('#dsSouthLong').val())))
      {
        alert("Please Enter The Southwest of Longitude");
        return false;
      }
      else if($('#dsNorthLat').val()=="" || !(/^[0-9]+$/.test($('#dsNorthLat').val())))
      {
        alert("Please Enter The Northeast of Latitude");
        return false;
      }
      else if($('#dsNorthLong').val()=="" || !(/^[0-9]+$/.test($('#dsNorthLong').val())))
      {
        alert("Please Enter The Northeast of Longitude");
        return false;
      }
      
      //got to referesh the list of datasources in the parent once name is changed -- sanjukta
      
      var dataObj={};     
      
      dataObj.srcID= (($('#addDataSourceBtn').val() == "Add")?"":$('#dataSourceId').val()); // datasource id is empty for new
      dataObj.userId= this.sessionModel.id;
      dataObj.srcTheme=$('#dsTheme').val();
      dataObj.srcName=$('#dsName').val();
      dataObj.url=$('#dsPath').val();
      dataObj.srcFormat=$('#dataFormat').val();
      dataObj.srcVarName = "";  // dont know wt is this for?
      dataObj.bagofwords= [];
   
      
      if(dataObj.srcFormat=="stream")
      {
        dataObj.supportedwrapper=$('#dfSupport').val();
        var tempArray = $("#dataFormatData").find('#dfTextArea').val().split(",");
        dataObj.bagofwords=tempArray;
        
      }
      
      else if(dataObj.srcFormat=="visual")
      {
        
        dataObj.supportedwrapper="VisualEmageWrapper";
      
      }
      
      var vdfIgnoreColor = ($('#vdfIgnoreColor').val() == undefined?0:$('#vdfIgnoreColor').val());
      //alert($('#hidden_id_File').html());
      dataObj.wrapper = {   
          wrprName :  '',
          wrprName :  $('#dsTheme').val(),
          //wrprType : '',
          wrprType : $('#Data_foramt_type').val(),
          wrprKeyValue : $('#key-value_pair').val(), //key value field missing in UI            
          wrprBagOfWords : $('#dfSupport').val(),
          //wrprVisualMaskMat : '',
          wrprVisualMaskMat : $('#hidden_id_mask').html(),
          wrprVisualIgnore : vdfIgnoreColor,
          wrprArchStartTime : '',           
          wrprArchEndTime : '',           
          wrprArchGenRate : '',
          //wrprVisualColorMat : '', 
          wrprVisualColorMat : $('#hidden_id_color').html(),
          //wrprVisualTransMat : '',
          wrprVisualTransMat : $('#hidden_id_trans').html(),
          //wrprCSVFile : $('#hidden_id_File').html()
          wrprCSVFileURL : $('#CSV_URL').val(),
          
      };
      
      
      
      
      
      
      
      dataObj.Visualparam = {
          
          
          "tranMatPath" : $('#dfTransPath').val(),//$('#vdfIgnoreColor').val(), //document.getElementById('dfTransPath').addEventListener('change', readSingleFile, false), //$('#byte_content').val(),   document.getElementById('dfTransPath').addEventListener('change', readSingleFile, false),
            
          
          "colorMatPath" :  $('#dfColorPath').val(),//$('#vdfIgnoreColor').val(), //document.getElementById('dfColorPath').addEventListener('change', readSingleFile, false), //$('#dfColorPath').val(),  //document.getElementById('dfColorPath').addEventListener('change', readSingleFile, false),
          
          
          "maskPath" : $('#dfMaskPath').val(), // $('#vdfIgnoreColor').val(), //document.getElementById('dfMaskPath').addEventListener('change', readSingleFile, false), // $('#dfMaskPath').val(),     //document.getElementById('dfMaskPath').addEventListener('change', readSingleFile, false),
          
         
      };  
      
      dataObj.initParam={
        "timeWindow":$('#dsTime').val(),
        "syncAtMilSec":$('#dsSync').val(),
        "latUnit":$('#dsUnitLat').val(),
        "longUnit":$('#dsUnitLong').val(),
        "swLat":$('#dsSouthLat').val(),
        "swLong":$('#dsSouthLong').val(),
        "neLat":$('#dsNorthLat').val(),
        "neLong":$('#dsNorthLong').val()
        };
      dataObj.finalParam={
        "timeWindow":$('#dsFTime').val(),
        "syncAtMilSec":$('#dsFSync').val(),
        "latUnit":$('#dsFUnitLat').val(),
        "longUnit":$('#dsFUnitLong').val(),
        "swLat":$('#dsFSouthLat').val(),
        "swLong":$('#dsFSouthLong').val(),
        "neLat":$('#dsFNorthLat').val(),
        "neLong":$('#dsFNorthLong').val()
        };
      
      
      this.datasourceModel.set(dataObj);
      
      this.datasourceModel.save(null,
                {
                  type:"PUT",
                  success: function (datasourceModel,responseText) {
                      alert("Data saved successfully");                     
                      $("#toPopup").fadeOut("normal");  
                      $("#backgroundPopup").fadeOut("normal");  
                      return true;
                    },
                    error: function(datasourceModel,responseText) {
                      alert("Data saved successfully"); // need to pass json or accept string otherwise goes to error -- sanjukta
                      $("#toPopup").fadeOut("normal");  
		              $("#backgroundPopup").fadeOut("normal");  
		              self.datasourceUpdate();
		              self.registerDatasource.fetchDatasource();
              
            }

                });     
    }


  });
  return homePageView;
});
