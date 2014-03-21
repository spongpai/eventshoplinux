package com.eventshop.eventshoplinux.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eventshop.eventshoplinux.DAO.admin.AdminManagementDAO;
import com.eventshop.eventshoplinux.DAO.datasource.DataSourceManagementDAO;
import com.eventshop.eventshoplinux.DAO.query.QueryListDAO;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.CSVEmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.EmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.STTEmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.SensorMongoDBEmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.VisualEmageIterator;
import com.eventshop.eventshoplinux.domain.datasource.emage.ResolutionMapper.SpatialMapper;
import com.eventshop.eventshoplinux.domain.datasource.emage.ResolutionMapper.TemporalMapper;
import com.eventshop.eventshoplinux.domain.datasource.emage.STMerger;


//import com.eventshop.eventshoplinux.domain.datasource.DataSource;
import com.eventshop.eventshoplinux.domain.datasource.DataSource;
import com.eventshop.eventshoplinux.domain.datasource.DataSourceListElement;
import com.eventshop.eventshoplinux.domain.datasource.DataSource.DataFormat;
import com.eventshop.eventshoplinux.domain.datasource.simulator.DistParameters;
import com.eventshop.eventshoplinux.domain.datasource.simulator.GaussianParameters2D;
import com.eventshop.eventshoplinux.domain.datasource.simulator.Simulator.Kernel;
//import com.eventshop.eventshoplinux.domain.query.QueryRunnable;
import com.eventshop.eventshoplinux.servlets.Query;
import com.eventshop.eventshoplinux.servlets.ResponseJSON;
import com.eventshop.eventshoplinux.domain.query.QueryDTO;
import com.eventshop.eventshoplinux.util.datasourceUtil.DataProcess;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.FlickrWrapper;
import com.eventshop.eventshoplinux.domain.common.FrameParameters;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.SimDataWrapper;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.TwitterWrapper;
import com.eventshop.eventshoplinux.util.datasourceUtil.wrapper.Wrapper;
import com.eventshop.eventshoplinux.util.commonUtil.Config;
import com.eventshop.eventshoplinux.util.commonUtil.SystemUtilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jersey.core.util.Base64;








//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;

public class RegisterServlet extends HttpServlet
{
	 protected Log log=LogFactory.getLog(this.getClass().getName());
	   

	/**
	 * Automatically generated serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	String context = "";
	String tempDir = "";
	int preRegisteredSrcCount = 0;
	int preRegisteredQueryCount = 0;
	
	// All the registered data sources in EventShop
	HashMap<String, DataSource> sources;
	// All the data processes
	HashMap<String, DataProcess> dataProcesses;
	
	// All the registered queries
	HashMap  queries;
	// A QueryJSONParser object
	Query query;
	
	
	public void init()
	{
		//System.out.println(" this init is called");
		context = Config.getProperty("context");
		tempDir = Config.getProperty("tempDir");
		preRegisterDataSourcesQueries();		
	}
		
	
	public void init(final ServletConfig config)
	{
		//System.out.println(" this final init is called");
		//context = config.getServletContext().getRealPath("/");
		context = Config.getProperty("context");
		log.info(context);
		tempDir = Config.getProperty("tempDir");
		preRegisterDataSourcesQueries();	
	}
	
	
	public RegisterServlet()
	{
		//sources = new ArrayList<DataSource>();
		sources = new HashMap<String, DataSource>();
		dataProcesses = new HashMap<String, DataProcess>();		
		queries = new HashMap();
		preRegisterDataSourcesQueries();
		//no json parser required so removed the line
	}
	
	
		
	// This method is most likely for testing and debugging purpose
	public void doGet(HttpServletRequest request,  HttpServletResponse response)
	{
		if(request.getParameter("type") != null && request.getParameter("type").equalsIgnoreCase("show")){
			if(request.getParameter("q").equalsIgnoreCase("runningds")){
				String dsList = "";
				for (Map.Entry<String, DataSource> entry : sources.entrySet()) {
					if(entry.getValue().getControl() == 1)
						dsList += entry.getKey() + " ";
						
				}
				new ResponseJSON(response, ResponseJSON.ResponseStatus.INFO, "Running ds: " + dsList);
			} else if(request.getParameter("q").equalsIgnoreCase("dscontrolflag")){
				String output = "";
				for (Map.Entry<String, DataSource> entry : sources.entrySet()) {
					output += entry.getKey() + "-" + entry.getValue().control + ",";
				}
				new ResponseJSON(response, ResponseJSON.ResponseStatus.INFO, "DS control flag: " + output);
			} else if(request.getParameter("q").equalsIgnoreCase("dsinfo")){
				JsonArray dsArr = new JsonArray();
				for (Map.Entry<String, DataSource> entry : sources.entrySet()) {
					JsonObject jo = new JsonObject();
					jo.addProperty(entry.getKey(), entry.getValue().toString());
					dsArr.add(jo);
				}
				new ResponseJSON(response, ResponseJSON.ResponseStatus.INFO, dsArr.toString());
			} else if(request.getParameter("q").equalsIgnoreCase("dataprocess")){
				String dpList = "";
				for (Map.Entry<String, DataProcess> entry : dataProcesses.entrySet()) {
					//if(entry.getValue().isRunning() == 1)
					//	dpList += entry.getKey() + " ";
					dpList += entry.getKey() + "-" + entry.getValue().isRunning + ", ";
				}
				new ResponseJSON(response, ResponseJSON.ResponseStatus.INFO, "DataProcess list: " + dpList);
			}
		} else {
			doPost(request, response);
		}
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response){
		response.setContentType("application/json");	
		try {
			log.info("in servlet");
			if(request.getParameter("type") == null){
				log.info("invalid action each request require `type` parameter");
				new ResponseJSON(response, ResponseJSON.ResponseStatus.ERROR, "invalid parameters, each request required `type` parameter");
				return;
			}
				
			// start data source
			if(request.getParameter("type").equals("d") || request.getParameter("type").equals("startds")){
				// e.g. type=d&dsID=3 or type=startds&dsID=3
				String dsId = request.getParameter("dsID");
				log.info("startds: " + dsId);
				if(dsId == null || dsId.isEmpty()){
					log.info("invalid dsId " + dsId);
					new ResponseJSON(response, ResponseJSON.ResponseStatus.ERROR, "invalid dsID " + dsId);
				} else{
					DataSourceManagementDAO datasourceDAO = new DataSourceManagementDAO();
					String[] dsIDList = dsId.split(",");
					String output = "";
					for(int i = 0; i < dsIDList.length; i++){
						if(!sources.containsKey(dsIDList[i]) || sources.get(dsIDList[i]).url == null){
							sources.put(dsIDList[i], datasourceDAO.getDataSource(Integer.parseInt(dsIDList[i])));
						}
						DataSource dataSrc =  sources.get(dsIDList[i]);
						if(!dataProcesses.containsKey(dataSrc.srcID)){		// This data source isn't in the dataProcesses list yet
							startDataProcess(dataSrc);						// start data process and add to the list
							dataSrc.setControl(1);
							sources.put(dataSrc.srcID, dataSrc);					
							output += "DataProcess is started. " + dataSrc.srcID + " is running["+dataSrc.getControl()+"]. ";
						}
						else {								// check whether the data process is running or not
							DataSource ds = sources.get(dataSrc.srcID);
							if(!dataProcesses.get(dataSrc.srcID).isRunning){	// ds isn't running
								startDataProcess(dataSrc);
								ds.setControl(1);			// update control flag
								sources.put(ds.srcID, ds);					
								output += "DataProcess is started. " + ds.srcID + " is running["+ds.getControl()+"]. ";
							}
							else {						// the selected ds is already started and running, so do nothing.
								output += dataSrc.srcID + " is already started and running["+ds.getControl()+"], so do nothing. ";
							}	
						}
						new ResponseJSON(response, ResponseJSON.ResponseStatus.SUCCESS, output);
					}
				}
			} 
			// stop data source
			else if(request.getParameter("type").equalsIgnoreCase("stopds")){
				String dsId = request.getParameter("dsID");
				log.info("stopds: " + dsId);
				if(dsId == null || dsId.isEmpty()){
					log.info("invalid dsId " + dsId);
					new ResponseJSON(response, ResponseJSON.ResponseStatus.ERROR, "invalid dsID " + dsId);
				} else{
					DataSourceManagementDAO datasourceDAO = new DataSourceManagementDAO();
					String[] dsIDList = dsId.split(",");
					String output = "";
					for(int i = 0; i < dsIDList.length; i++){	
						if(dataProcesses.containsKey(dsIDList[i])){
							if(dataProcesses.get(dsIDList[i]).isRunning)
								dataProcesses.get(dsIDList[i]).stop();
							sources.get(dsIDList[i]).setControl(0);
							output += dsIDList[i] + " is stopped. ";
						} else{
							output += dsIDList[i] + " isn't running, so do nothing. ";
						}
					}
					new ResponseJSON(response, ResponseJSON.ResponseStatus.SUCCESS, output);
				}
			} 
			// start query
			else if(request.getParameter("type").equalsIgnoreCase("startq")){
				/*
				String qId = request.getParameter("qIDList");
				log.info("in startq: " + qId);
				if(qId == null || qId.isEmpty()){
					log.info("invalid qID " + qId);
					new ResponseJSON(response, ResponseJSON.ResponseStatus.ERROR, "invalid qID " + qId);
				} else{
				
					String[] qIDArr = ((String) request.getParameter("qIDList")).split(",");
					QueryListDAO queryDAO = new QueryListDAO();
					List<Query> queryList = (List)queryDAO.getQueryList(qIDArr);		// get queryId, queryName, queryStatus, bounding_box;
					
					log.info("get query list from db: " + queryList.size());
					
					
					
					Query query = queryParser.parseQuery(queryJSONText);
					new Thread(query).start();
					queries.add(query);

					// TODO: provide output for numeric output
					while (query.outputName == null)
						Thread.sleep(500);

					byte[] output = (query.outputName + ".json").getBytes();
					OutputStream out = response.getOutputStream();
					out.write(output);
					out.close();
					
					
					
					
					// Start every selected process
					String filepath = "";
					List<File> files = new LinkedList<File>();
					QueryResult[] queryResult = new QueryResult[queryList.size()];
					OutputStream outputStream;
					int count = 0;
					//System.out.println("queryList.size()"+queryList.size());
					for(int i = 0; i < queryList.size(); ++i)
					{	
						int index = Integer.parseInt(((Query)queryList.get(i)).queryID);
						parseQuery
						if(!queries.containsKey(index))
							queries.put(index, (Query) queryList.get(i));
						if(!((Query)queries.get(index)).isRunning)
							// Start query process
							new Thread((Query)queries.get(index)).start();	
						
						tempDir ="temp";
						filepath =  context + "temp\\queries\\" + index+".json";// + "_" + src.srcName;
						
						log.info(filepath);
						File file = new File(filepath);
						boolean exists = file.exists();						
						if (!exists) {
							//System.out.println("file does not exist man"+filepath);
						} else {
							//System.out.println("we are taking data now from "+filepath);
							byte[] output = org.apache.commons.io.FileUtils.readFileToByteArray(file);
							QueryResult query = new QueryResult();
							query.queryId = new Integer(index).toString();
							//query.output = new String(Base64.encode(output));
							query.output = new String(output);
							queryResult[count] = query;
							count++;
							//System.out.println(" we are getin"+count);
							//queryResult[i].queryId = new Integer(index).toString();
							//queryResult[i].output = new String(Base64.encode(output));						
						}
					}
				
				/* commenting because of null
				for(int i = 0; i < queryList.size(); ++i)
				{					
					int index = Integer.parseInt(((Query)queryList.get(i)).queryID);
					System.out.println(" this is failing?"+queries.size());
					while(!((Query)queries.get(index)).isRunning)
						Thread.sleep(500);
					System.out.println(" this is fail done");
				}
				
				
				
				for (int i=0;i<queryResult.length;i++) {
					System.out.println(((QueryResult)queryResult[i]).queryId+" i ");
				}
				
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				ObjectMapper mapper = new ObjectMapper();				
				mapper.writeValue(out, queryResult);				
				//out.print();
				out.flush();
				out.close();		
				}
			*/
			} else if(request.getParameter("type").equals("alert"))
			{
				log.info("in alert part");
				// Parse the alert json
				//String alertJSONText = request.getParameter("json");
				//JSONObject alertObject = JSONObject.fromObject(alertJSONText);
				
				String userDS = request.getParameter("userDS").split(":")[0];
				String srcQuery = request.getParameter("sourceQuery").split(":")[0];
				String nearestDS = request.getParameter("nearestDs").split(":")[0];
				
				String[] qRanges = request.getParameter("qRange").split(":");
				double minValProb = Double.valueOf(qRanges[0]);
				double maxValProb = Double.valueOf(qRanges[1]);
				
				String[] aRanges = request.getParameter("aRange").split(":");
				double minValSol = Double.valueOf(aRanges[0]);
				double maxValSol = Double.valueOf(aRanges[1]);
				
				String msg = request.getParameter("msg");
				
				AlertProcessor alertProc = new AlertProcessor(userDS, srcQuery, nearestDS, minValProb,
						maxValProb, minValSol, maxValSol, msg, this);
				alertProc.DoAlerts();

				byte[] output = "Sent tweets".toString().getBytes();
				OutputStream out = response.getOutputStream();
				out.write(output);	        	      
				out.close();
			}

		} catch (Exception p) {
			log.error(p.getMessage());
			new ResponseJSON(response, ResponseJSON.ResponseStatus.ERROR, p.getMessage());
			return ;
		}
	}
	
	public void doPost2(HttpServletRequest request, HttpServletResponse response) 
	{
		response.setContentType("application/json");		
		
		try {
			log.info("in servlet");
			
			// Add data source
			if(request.getParameter("type").equals("d"))
			{
//				log.info("datasource " + request.getParameter("dsID"));
//				DataSourceManagementDAO datasourceDAO = new DataSourceManagementDAO();
//				String[] dsIDList =request.getParameter("dsIDList").split(",");
//				List<DataSource> dataSrcList = datasourceDAO.getDatasourceForSelectedDS(dsIDList);
//	        
//				// if it is to edit the old source, set into the corresponding location
//				// otherwise, add it to the list
//				Iterator iterator = dataSrcList.iterator();
//				while(iterator.hasNext()) {
//					DataSource dataSrc =  (DataSource)iterator.next();
//					sources.put(dataSrc.srcID, dataSrc);					
//					startDataProcess(dataSrc);
//					byte[] output = dataSrc.toString().getBytes();
//					//byte[] output = org.apache.commons.io.FileUtils.readFileToByteArray(file);
//					
//				}				
//				System.out.println(" we are getting the datasource"+dataSrcList.size());
//				response.setContentType("application/json");
//				PrintWriter out = response.getWriter();
//				ObjectMapper mapper = new ObjectMapper();				
//				mapper.writeValue(out, dataSrcList);				
//				//out.print();
//				out.flush();
//				out.close();
//				// why serialize and write this?
				
			}
			// Add a new query
			else if(request.getParameter("type").equals("q"))
			{
//				log.info("in query");
//				//String queryJSONText = request.getParameter("json");
//				Query query = (Query)request.getAttribute("Query");
//				queries.put(query.queryID,query);
//				
//				new Thread(query).start();
//
//				// TODO: provide output for numeric output
//				while(query.outputName == null) Thread.sleep(500);
//				
//				// why serialize and write this?
//				byte[] output = (query.outputName + ".json").getBytes();
//				OutputStream out = response.getOutputStream();
//				//System.out.println(query.outputName + "output is "+out);
//				out.write(output);	        	      
//				out.close();				
				
			} 
			// Stop all running queries
			else if(request.getParameter("type").equals("stop"))
			{
				log.info("in stop");
				
				// Stop data processes
				for(int i = 0; i < dataProcesses.size(); i++)
				{
					dataProcesses.get(i).stop();
					while(dataProcesses.get(i).isRunning)
						Thread.sleep(500);
				}
				// Stop queries
				for(int i = 0; i < queries.size(); i++)
				{
					((Query) queries.get(i)).stop();
					while(((Query)queries.get(i)).isRunning);
						Thread.sleep(500);
				}
				
				// clear arrays
				dataProcesses.clear();
				
				while(queries.size() > preRegisteredQueryCount)
				{
					queries.remove(preRegisteredQueryCount);
				}
				
				while(sources.size() > preRegisteredSrcCount)
				{
					sources.remove(preRegisteredSrcCount);
				}
					
				byte[] output = "All Queries have been Stopped!".getBytes();
				OutputStream out = response.getOutputStream();
				out.write(output);	        	      
				out.close();
			}
			// Start the selected queries
			else if(request.getParameter("type").equals("qidstart"))
			{
				/*
				//System.out.println(" we are in qidstart ");
				log.info("in qidstarttt"+ request.getParameter("qIDList"));
//				String qids = request.getParameter("json");
//				JSONObject qidObject = JSONObject.fromObject(qids);
//				JSONArray qidArray = qidObject.getJSONArray("registeredIDs");
				String[] qIDArr = ((String) request.getParameter("qIDList")).split(",");
				QueryListDAO queryDAO = new QueryListDAO();
				List<Query> queryList = (List)queryDAO.getQueryList(qIDArr);				
				// Start every selected process
				String filepath = "";
				//String[] filepathArr = new String[queryList.size()];
				List<File> files = new LinkedList<File>();
				QueryResult[] queryResult = new QueryResult[queryList.size()];
				OutputStream outputStream;
				int count = 0;
				//System.out.println("queryList.size()"+queryList.size());
				for(int i = 0; i < queryList.size(); ++i)
				{	
					//System.out.println("in loop");
					int index = Integer.parseInt(((Query)queryList.get(i)).queryID);
					new Thread((Query)queries.get(index)).start();
					tempDir ="temp";
					filepath =  context + "temp\\queries\\" + index+".json";// + "_" + src.srcName;
					
					log.info(filepath);
					File file = new File(filepath);
					boolean exists = file.exists();						
					if (!exists) {
						//System.out.println("file does not exist man"+filepath);
					} else {
						//System.out.println("we are taking data now from "+filepath);
						byte[] output = org.apache.commons.io.FileUtils.readFileToByteArray(file);
						QueryResult query = new QueryResult();
						query.queryId = new Integer(index).toString();
						//query.output = new String(Base64.encode(output));
						query.output = new String(output);
						queryResult[count] = query;
						count++;
						//System.out.println(" we are getin"+count);
						//queryResult[i].queryId = new Integer(index).toString();
						//queryResult[i].output = new String(Base64.encode(output));						
					}
				}
				
				/* commenting because of null
				for(int i = 0; i < queryList.size(); ++i)
				{					
					int index = Integer.parseInt(((Query)queryList.get(i)).queryID);
					System.out.println(" this is failing?"+queries.size());
					while(!((Query)queries.get(index)).isRunning)
						Thread.sleep(500);
					System.out.println(" this is fail done");
				}
				/*
				
				
				for (int i=0;i<queryResult.length;i++) {
					System.out.println(((QueryResult)queryResult[i]).queryId+" i ");
				}
				
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				ObjectMapper mapper = new ObjectMapper();				
				mapper.writeValue(out, queryResult);				
				//out.print();
				out.flush();
				out.close();
				*/
			}
			// Stop the selected queries
			else if(request.getParameter("type").equals("qidstop"))
			{
				/*
				log.info("in qidstop");
				String qids = request.getParameter("json");
				log.info(qids);
//				JSONObject qidObject = JSONObject.fromObject(qids);
//				JSONArray qidArray = qidObject.getJSONArray("registeredIDs");

				String[] qIDArr = ((String) request.getParameter("qIDList")).split(",");
				QueryListDAO queryDAO = new QueryListDAO();
				List<Query> queryList = (List)queryDAO.getQueryList(qIDArr);

				// Stop every selected process
				for(int i = 0; i < queryList.size(); ++i)
				{
					log.info("stop query " + i);
					int index = Integer.parseInt(((Query)queryList.get(i)).queryID);
					log.info(index);
					((Query)queries.get(index)).stop();
				}
				
				for(int i = 0; i < queryList.size(); ++i)
				{
					int index = Integer.parseInt(((Query)queryList.get(i)).queryID);
					while(((Query)queries.get(index)).isRunning)
						Thread.sleep(500);
				}
				
				byte[] output = "Selected Queries Stopped!".getBytes();
				OutputStream out = response.getOutputStream();
				out.write(output);	        	      
				out.close();
				*/
			}
			// Check if file for a datasource exist
			else if(request.getParameter("type").equals("datastatus"))
			{
				log.info("in datastatus");
				log.info(sources.size());

				String output = "";
				for (int i=0; i < sources.size(); i++)
				{
					DataSource src = (DataSource)sources.get(i);
					String filepath = tempDir + "/ds" + src.srcID + "_" + src.srcName;
					log.info(filepath);
					File file = new File(filepath);
					boolean exists = file.exists();
					
					if (exists)	
						output += "1,";
					else 
						output += "0,";
				}

				OutputStream out = response.getOutputStream();
				out.write(output.getBytes());	        	      
				out.close();
			}
			// Read the query file
			else if(request.getParameter("type").equals("readquery"))
			{
				String qid = request.getParameter("qid");
				
				OutputStream out = response.getOutputStream();
				BufferedReader reader = new BufferedReader(new FileReader(context+"proc/src/q" + qid + ".cc"));
				String line;
				while((line = reader.readLine()) != null)
				{
					line += "\n";
					out.write(line.getBytes());
				}
				reader.close();        	      
				out.close();
			}
			// To send alerts
			else if(request.getParameter("type").equals("alert"))
			{
				log.info("in alert part");
				// Parse the alert json
				//String alertJSONText = request.getParameter("json");
				//JSONObject alertObject = JSONObject.fromObject(alertJSONText);
				
				String userDS = request.getParameter("userDS").split(":")[0];
				String srcQuery = request.getParameter("sourceQuery").split(":")[0];
				String nearestDS = request.getParameter("nearestDs").split(":")[0];
				
				String[] qRanges = request.getParameter("qRange").split(":");
				double minValProb = Double.valueOf(qRanges[0]);
				double maxValProb = Double.valueOf(qRanges[1]);
				
				String[] aRanges = request.getParameter("aRange").split(":");
				double minValSol = Double.valueOf(aRanges[0]);
				double maxValSol = Double.valueOf(aRanges[1]);
				
				String msg = request.getParameter("msg");
				
				AlertProcessor alertProc = new AlertProcessor(userDS, srcQuery, nearestDS, minValProb,
						maxValProb, minValSol, maxValSol, msg, this);
				alertProc.DoAlerts();

				byte[] output = "Sent tweets".toString().getBytes();
				OutputStream out = response.getOutputStream();
				out.write(output);	        	      
				out.close();
			}
			// reset system
			else if(request.getParameter("type").equals("reset"))
			{
				log.info("reset system");
				SystemUtilities.reset();
				
				byte[] output = "Reset System".toString().getBytes();
				OutputStream out = response.getOutputStream();
				out.write(output);	        	      
				out.close();
			}
			// login
			else if(request.getParameter("type").equals("login"))
			{
				log.info("login");
				String username = request.getParameter("u");
				String password = request.getParameter("p");
				byte[] output = "0".toString().getBytes();
				if(username.equalsIgnoreCase("event") && password.equalsIgnoreCase("es2012")){
					output = "1".toString().getBytes();
				}
				OutputStream out = response.getOutputStream();
				out.write(output);	        	      
				out.close();
			}
			//System.out.println(" getting out 22");
		} catch (Exception p) {
			log.error(p.getMessage());
			return ;
		}
	}

	
	@SuppressWarnings("unchecked")
	public DataSource parseDataSource(DataSource dataSrc)
	{
		Enum format = dataSrc.getSrcFormat();
        if(format.equals("stream"))
        	dataSrc.srcFormat = DataFormat.stream;
        else if(format.equals("visual"))
        	dataSrc.srcFormat = DataFormat.visual;
        else if(format.equals("csv"))
        	dataSrc.srcFormat = DataFormat.csv;
        
    
        
        return dataSrc;
	}

	
	public FrameParameters getFinalParamFromSrc(int index)
	{
		if(index < sources.size())
			return ((DataSource)sources.get(index)).finalParam;
		else
		{
			// Create FrameParameters
			long timeWindow = 1000*10;
			long mSecsOffset = 1000;

			// Group 1
			double latUnit1 = 0.5;
			double longUnit1 = 0.5;
			double swLat1 = 24;
			double swLong1 = -125;
			double neLat1 = 50;
			double neLong1 = -66;

			FrameParameters fp = new FrameParameters(timeWindow, mSecsOffset, latUnit1,longUnit1, 
					swLat1,swLong1 , neLat1, neLong1);
			return fp;
		}
	}
	
	
	public void startDataProcess(DataSource src)
	{
		log.info("data source:" + src.toString());
		DataFormat format = src.srcFormat;
		
		String imgBasePath = context + "results/ds/";
		
		if(format == DataFormat.stream)
		{
			Wrapper wrapper = null;
			if(src.supportedWrapper.equals("Twitter"))
			{
				log.info("before creation");
				wrapper = new TwitterWrapper(src.url, src.srcTheme, src.initParam, true);
				log.info("wrapper created");
				
				String[] words = new String[src.bagOfWords.size()];
				src.bagOfWords.toArray(words);
				((TwitterWrapper) wrapper).setBagOfWords(words);
			}
			else if(src.supportedWrapper.equals("Flickr"))
			{
				wrapper = new FlickrWrapper(src.url, src.srcTheme, src.initParam);
				String[] words = new String[src.bagOfWords.size()];
				src.bagOfWords.toArray(words);
				((FlickrWrapper) wrapper).setBagOfWords(words);
			}
			else if(src.supportedWrapper.equals("sim"))
			{
				// Create Distribution Parameters and Generators
				ArrayList<DistParameters> dParams = new ArrayList<DistParameters>();
				// LA
				GaussianParameters2D gParam = new GaussianParameters2D(34.1, -118.2, 3.0, 3.0, 200);
				dParams.add(gParam);
				// SF
				gParam = new GaussianParameters2D(37.8, -122.4, 2.0, 2.0, 200);
				dParams.add(gParam);
				// Seattle
				gParam = new GaussianParameters2D(47.6, -122.3, 1.0, 1.0, 100);
				dParams.add(gParam);
				// NYC
				gParam = new GaussianParameters2D(40.8, -74.0, 5.0, 5.0, 200);
				dParams.add(gParam);

				
				
				// Wrappers
				//wrapper = new SimDataWrapper(src.url, src.srcTheme, src.initParam, Kernel.gaussian, dParams);//changed to initParam...vks:Aug30,2011
				wrapper = new SimDataWrapper(src.url, src.srcTheme, src.initParam, com.eventshop.eventshoplinux.util.datasourceUtil.simulator.Simulator.Kernel.gaussian, dParams);
			}
							
			// Add EmageIterator
			EmageIterator eit = new STTEmageIterator();
			eit.setSTTPointIterator(wrapper);
			eit.setSrcID(new Long(src.srcID));
			
			// Add output filename

			// change it to be query independent: 08/19/2011 Mingyan
			String filepath = tempDir + "/ds" + src.srcID + "_" + src.srcName;
			
			// Create st merger
			STMerger merger = null;
			/*
			if(!src.finalParam.equals(src.initParam))
			{
				log.info("The initial and final frame parameters are different");
				
				merger = new STMerger(src.finalParam);
				SpatialMapper sp = null;
				TemporalMapper tp = null;
				if(src.initParam.latUnit < src.finalParam.latUnit 
						|| src.initParam.longUnit < src.finalParam.longUnit)
					sp = SpatialMapper.sum;
				else if(src.initParam.latUnit > src.finalParam.latUnit 
						|| src.initParam.longUnit > src.finalParam.longUnit)
					sp = SpatialMapper.repeat;
				
				if(src.initParam.timeWindow < src.finalParam.timeWindow)
					tp = TemporalMapper.sum;
				else if(src.initParam.timeWindow > src.finalParam.timeWindow)
					tp = TemporalMapper.repeat;
				
				merger.addIterator(eit, sp, tp);
				merger.setMergingExpression("mulED(R0,1)");
			}
			*/
			// Add data processors
			DataProcess process = new DataProcess(merger, eit, wrapper, filepath, imgBasePath+"ds"+src.srcID, "ds"+src.srcID);
			dataProcesses.put(src.srcID, process);
			
			// Start the data collecting process
			new Thread(process).start();
		}
		else if(format == DataFormat.visual)
		{
			try {
				BufferedReader reader = new BufferedReader(new FileReader(src.visualParam.tranMatPath));
				String line = reader.readLine();
				String[] dim = line.split(",");
				double[][] tranMat = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
				int row = 0;
				String tranMatStr = "tranMat:\n ";
				while((line = reader.readLine()) != null)
				{
					String[] numbers = line.split(",");
					for(int col = 0; col < numbers.length; ++col)
						tranMat[row][col] = Double.parseDouble(numbers[col].trim());
					row++;
				}
				reader.close();
				
				for(int x = 0; x < tranMat.length; x++){
					for(int y = 0; y < tranMat[x].length; y++)
						tranMatStr += tranMat[x][y] + ", ";
					tranMatStr += "\n";
				}
				log.info(tranMatStr);
				
				
				reader = new BufferedReader(new FileReader(src.visualParam.colorMatPath));
				line = reader.readLine();
				dim = line.split(",");
				double[][] colorMat = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
				row = 0;
				while((line = reader.readLine()) != null)
				{
					String[] numbers = line.split(",");
					for(int col = 0; col < numbers.length; ++col)
						colorMat[row][col] = Double.parseDouble(numbers[col].trim());
					row++;
				}
				reader.close();
				
				String colorMatStr = "colorMatStr:\n ";
				for(int x = 0; x < colorMat.length; x++){
					for(int y = 0; y < colorMat[x].length; y++)
						colorMatStr += colorMat[x][y] + ", ";
					colorMatStr += "\n";
				}
				log.info(colorMatStr);
				
				VisualEmageIterator veit = new VisualEmageIterator(src.initParam, src.srcTheme, src.url, tranMat, 
						colorMat, src.visualParam.maskPath, src.visualParam.ignoreSinceNumber);
				veit.setSrcID(new Long(src.srcID));
				
				// Add output filename
				//Change it to be query independent: 08/19/2011 Mingyan
				String filepath = tempDir + "/ds" + src.srcID + "_" + src.srcName;
				
				// Create st merger
				STMerger merger = null;
				/*
				if(!src.finalParam.equals(src.initParam))
				{
					merger = new STMerger(src.finalParam);
					SpatialMapper sp = null;
					TemporalMapper tp = null;
					if(src.initParam.latUnit < src.finalParam.latUnit 
							|| src.initParam.longUnit < src.finalParam.longUnit)
						sp = SpatialMapper.average;
					else if(src.initParam.latUnit > src.finalParam.latUnit 
							|| src.initParam.longUnit > src.finalParam.longUnit)
						sp = SpatialMapper.repeat;
					
					if(src.initParam.timeWindow < src.finalParam.timeWindow)
						tp = TemporalMapper.sum;
					else if(src.initParam.timeWindow > src.finalParam.timeWindow)
						tp = TemporalMapper.repeat;
					
					merger.addIterator(veit, sp, tp);
					merger.setMergingExpression("mulED(R0,1)");
				}
				*/
								
				// Add data processors
				DataProcess process = new DataProcess(merger, veit, null, filepath, imgBasePath+"ds"+src.srcID, "ds"+src.srcID);
				dataProcesses.put(src.srcID, process);
				
				// Start the data collecting process
				new Thread(process).start();
			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		else if(format == DataFormat.csv)
		{
			String filepath = tempDir + "/ds" + src.srcID + "_" + src.srcName;
			CSVEmageIterator csvEIter = new CSVEmageIterator(src.initParam, src.srcTheme, src.url);
			csvEIter.setSrcID(new Long(src.srcID));
			
			// Create st merger
			
			STMerger merger = null;
			/*
			if(!src.finalParam.equals(src.initParam))
			{
				merger = new STMerger(src.finalParam);
				SpatialMapper sp = null;
				TemporalMapper tp = null;
				if(src.initParam.latUnit < src.finalParam.latUnit 
						|| src.initParam.longUnit < src.finalParam.longUnit)
					sp = SpatialMapper.average;
				else if(src.initParam.latUnit > src.finalParam.latUnit 
						|| src.initParam.longUnit > src.finalParam.longUnit)
					sp = SpatialMapper.repeat;
				
				if(src.initParam.timeWindow < src.finalParam.timeWindow)
					tp = TemporalMapper.sum;
				else if(src.initParam.timeWindow > src.finalParam.timeWindow)
					tp = TemporalMapper.repeat;
				
				merger.addIterator(csvEIter, sp, tp);
				merger.setMergingExpression("mulED(R0,1)");
			}
			*/		
			DataProcess process = new DataProcess(merger, csvEIter, null, filepath, imgBasePath+"ds"+src.srcID, "ds"+src.srcID);
			dataProcesses.put(src.srcID, process);
			
			// Start the data collecting process
			new Thread(process).start();
		}
	}
	
	
	public static void main(String[] args)
	{
		RegisterServlet servlet = new RegisterServlet();
		servlet.init();
		servlet.context = Config.getProperty("context");
		String tempDir = Config.getProperty("tempDir");	
		//servlet.preRegisterDataSourcesQueries(); //not needed?
		//String queryText = "[{\"qID\":16,\"type\":\"filter_ds6\",\"query\":{\"dataSrcID\":\"ds6\",\"maskMethod\":\"map\",\"coords\":[24,-125,50,-66],\"placename\":\"New York City\",\"filePath\":\"/home/sln/ESProjects/es-auge/Temp/q0_filterFile\",\"valRange\":[\"-99999999\",\"99999999\"],\"timeRange\":[\"0\",\"9223372036854775807\"],\"normMode\":true,\"normVals\":[\"0\",\"100\"]}}]";
		//String queryText = args[0];
		//Query q0 = (Query)servlet.query.parseQuery(queryText);
		Query q0 = new Query(null);
		servlet.queries.put(q0.queryID,q0);
		int count = servlet.queries.size();
		new Thread((Query)servlet.queries.get(count -1 )).start();
		
		 
	}
	
	private void preRegisterDataSourcesQueries()
	{
    	DataSourceManagementDAO dataSrcDAO = new DataSourceManagementDAO();
    	List<DataSource> tempArry = (ArrayList)dataSrcDAO.getDataSrcList();
    	for (int i=0;i<tempArry.size();i++) {    		
    		sources.put(((DataSource)tempArry.get(i)).srcID,(DataSource)tempArry.get(i));
    	}
    	preRegisteredSrcCount = sources.size();
    	//this is not the right query object
    	QueryListDAO adminDAO = new QueryListDAO();
    	//ArrayList<QueryDTO> queryList = (ArrayList)adminDAO.getQueryList();    	
    	//queries = convertDTOListToQueryHash(queryList);
    	//preRegisteredQueryCount = queries.size();
    	
    	//do we need to set finalframe for source?
    			
	}
    
    private FrameParameters getDefaultFrameParams() {
    	long timeWindow = 1000*60*5; //*60*24*2;//the last 2 days
		long syncAtMilliSec = 1000;
		double latUnit = 0.1;
		double longUnit = 0.1;
		double swLat = 24;
		double swLong = -125;
		double neLat = 50;
		double neLong = -66;	
		FrameParameters fp = new FrameParameters(timeWindow, syncAtMilliSec,latUnit,longUnit, swLat,swLong , neLat, neLong);
		return fp;
    }
    
    private HashMap convertDTOListToQueryHash(ArrayList<QueryDTO> queryList) {
    	HashMap queryHash = new HashMap();
    	for (int i=0;i<queryList.size();i++)	{
    		QueryDTO queryDTO = queryList.get(i);
    		Query query = new Query(context);
    		query.ccPath = queryDTO.getQueryEsql();
    		query.setQueryID(queryDTO.getqID());
    		//query.setOpName(queryDTO.getQueryName());
    		//query.finalResolution(queryDTO.get)
    		queryHash.put(queryDTO.getqID(), query);
    	}  
    	return queryHash;
    }
    
 
    
}
