define([
  'jquery',
  'underscore',
  'backbone',
  'text!templates/user/query/addFinalFrmParamTpl.html',  
 ], function($, _, Backbone,addFinalFrmParamTpl){
	var self;
	var addFinalFrmParamView = Backbone.View.extend({
    el: $("#Finalconfigexecute"),

	initialize: function(opts){
		  self = this;
		  self.app_router = opts.router;
		  this.generatedQueriesArr = opts.generatedQueriesArr;
		  this.executeQueryModel = opts.executeQueryModel;		 
	  },
	  
    render: function(){
    	this.$el.html(addFinalFrmParamTpl);
			},		   
	 });

  return addFinalFrmParamView;
  
});
