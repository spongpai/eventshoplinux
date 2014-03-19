package com.eventshop.eventshoplinux.util.datasourceUtil.wrapper;

import static com.eventshop.eventshoplinux.constant.Constant.DB_URL;
import static com.eventshop.eventshoplinux.constant.Constant.DRIVER_NAME;
import static com.eventshop.eventshoplinux.constant.Constant.PASSWORD;
import static com.eventshop.eventshoplinux.constant.Constant.USR_NAME;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

import com.eventshop.eventshoplinux.domain.common.FrameParameters;
import com.eventshop.eventshoplinux.domain.datasource.emage.STMerger;
import com.eventshop.eventshoplinux.domain.datasource.emage.STTPoint;
import com.eventshop.eventshoplinux.domain.datasource.emage.ResolutionMapper.SpatialMapper;
import com.eventshop.eventshoplinux.domain.datasource.emage.ResolutionMapper.TemporalMapper;
import com.eventshop.eventshoplinux.domain.datasource.emageiterator.STTEmageIterator;
import com.eventshop.eventshoplinux.service.MongoDB;
import com.eventshop.eventshoplinux.util.commonUtil.Config;
import com.eventshop.eventshoplinux.util.datasourceUtil.DataProcess;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class SensorMongoDBWrapper extends Wrapper {
	protected static Log log=LogFactory.getLog(TwitterWrapper.class);       
	private LinkedBlockingQueue<STTPoint> ls;
	private boolean isRunning = false;

	private MongoDB mongo;
	private long startTimeStream, endTimeStream;
	private long startTimeEmage, endTimeEmage;
	private String startDateStr, endDateStr;
	private double radious;
	
	// For storing raw data into MySQL
	private boolean saveRawData;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Connection conn;
	private String tableName;
	private String theme;
	
	public SensorMongoDBWrapper(String url, String theme, FrameParameters params, MongoDB mongo, 
									long startTimeStream, long endTimeStream, boolean endTimeUnbound, double radious, boolean saveRawData){
		super(url, theme, params);
		this.mongo = mongo;		
		
		
		// e.g., url=http://vanilla.ics.uci.edu:8081/eventshoplinux/webresources/mongodb/{dbName}/{collectionName}/find
		this.saveRawData = saveRawData;
		if(this.saveRawData){
			this.tableName = "tbl_" + theme + "_mongo";
			connection();
			createRawSensorTable(theme);
		}
		this.startTimeStream = startTimeStream;
		if(!endTimeUnbound)
			this.endTimeStream = endTimeStream;
		else
			this.endTimeStream = System.currentTimeMillis();
		
		this.radious = radious;
//		startDateStr = formatter.format(new Date(startTime));
//		if(endTime == null)
//			endDateStr = "";		// keep generating Emage: unbound end-time
//		else
//			endDateStr = formatter.format(new Date(endTime));
	}
	
	public int insertSensor(double lat, double lng, String date, double value, double p_lat, double p_lon, String otherInfo){
		
		String sql = "INSERT INTO " + tableName + " VALUES (NULL, " + lat + ", " + lng + ", '" + 
			date + "', '" + value + "',"+p_lat+","+p_lon+", '"+otherInfo+"')";
		try {
			if(conn == null)
				connection();
			Statement statement = conn.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			log.error(sql);
			log.error(e.getMessage());
		}
		return 0;
	}
	
	public Connection connection(){
	  try {
		  Class.forName(DRIVER_NAME);
		  String url = Config.getProperty(DB_URL);
		  String userName = Config.getProperty(USR_NAME);
		  String pwd = Config.getProperty(PASSWORD);
		  conn = DriverManager.getConnection(url,userName,pwd);
	    
	  } catch(Exception e) {
		  log.error(e.getMessage());
	  }
	  return conn;
	}
	
	void createRawSensorTable(String theme){
		String query1="CREATE TABLE IF NOT EXISTS "+tableName+" ( " +
				"id INT AUTO_INCREMENT," +
				"latitude DOUBLE NOT NULL,"+
				"longitude DOUBLE NOT NULL,"+
				"date DATETIME NOT NULL,"+
				"value DOUBLE NOT NULL,"+
			    "p_latitude DOUBLE NOT NULL,"+
			    "p_longitude DOUBLE NOT NULL,"+
			    "info TEXT NOT NULL,"+
			    "PRIMARY KEY(id));\n\n" ;
			String query2 = "CREATE INDEX idx_"+theme+"_Latitude ON "+ tableName+"(latitude);\n";
			String query3 = "CREATE INDEX idx_"+theme+"_Longitude ON "+ tableName+"(longitude);\n" ;
			String query4 = "CREATE INDEX idx_"+theme+"_Date ON "+tableName+"(date);";


		try {
			log.info(query1);
			Statement statement = conn.createStatement();
			statement.execute(query1);
			statement.execute(query2);
			statement.execute(query3);
			statement.execute(query4);
		} catch (SQLException e) {
			connection();
		}
	}
	
	
	public void run() 
	{
		isRunning = true;
		
		int numofRows = params.getNumOfRows();
		int numOfColumns = params.getNumOfColumns();
		
		
		// Set end time (for each emage)
		long endTime = (long)Math.ceil(System.currentTimeMillis() / params.timeWindow) * params.timeWindow + params.syncAtMilSec;

		int numTimeWindow = 0;
		while(isRunning)
		{
			/*
			STTPoint point;
			long start = startTimeStream + (numTimeWindow * params.timeWindow);
			long end = start = params.timeWindow;

			// Loop for Latitude Longitude blocks 
			for(double i = params.swLat; i < params.neLat; i = i+params.latUnit)
			{
				for(double j = params.swLong; j < params.neLong; j = j+params.longUnit)
				{
					if(!isRunning) break;

					// call url to get sensors near by lat/long
					// center of the each cell in the grid
					double lat = i+0.5*params.latUnit;
					double lng = j+0.5*params.longUnit;
					double r = radious/3959.0;
					
					
					String qstr = "{$and:[{date:{$gt:'"+formatter.format(new Date(start))+"',$lt:'"+formatter.format(new Date(end))+"'}},"
							+ "{'loc':{$geoWithin:{$centerSphere:[["+lng+","+lat+"],"+r+"]}}}]}";
					System.out.println(qstr);
					String fields = "loc,date,value";
					if(mongo.collection == null){
						log.error("invalid mongo db configulation: collection not found");
						this.isRunning = false;
						break;
					}
					List<DBObject> sensorList = mongo.find(qstr, fields);
					for(DBObject sensor: sensorList){
						BasicDBList loc = ((BasicDBList)((DBObject)sensor.get("loc")).get("coordinates"));
						Double value = Double.parseDouble((String) sensor.get("value"));
						Double sensorLat =  Double.parseDouble((String)loc.get(1));
						Double sensroLng =  Double.parseDouble((String)loc.get(0));
						
						System.out.println(sensor.get("date") + "," + sensor.get("value") + ",long/lat" + loc.get(0)+","+loc.get(1));
						point = new STTPoint(sensor.get("value"), start, new Date(endTime), params.latUnit, params.longUnit, i, j, theme);
						
					}
					if(fields != null && fields != "")
						return myDb.find(qstr, fields);
					else
						return myDb.find(qstr);		
					int y = (int)Math.ceil(Math.abs((BigDecimal.valueOf(j)).subtract(BigDecimal.valueOf(params.swLong)).divide(BigDecimal.valueOf(params.longUnit)).doubleValue()));
					int x = (int)Math.ceil(Math.abs((BigDecimal.valueOf(i)).subtract(BigDecimal.valueOf(params.swLat)).divide(BigDecimal.valueOf(params.latUnit)).doubleValue()));

					int ret = 0;
					if (isPopulated[12-x][y])
					{
						ret = doCollection(i+0.5*params.latUnit, j+0.5*params.longUnit, x, y, params.latUnit, start, queryStr);
						numQueries++;
					}			
					ls.add(point);
					}
			}
			log.info("TwitterWrapper NumQueries made:"+numQueries);

			// Sleeping when window is not up yet
			endTime += params.timeWindow;
			while(System.currentTimeMillis() < endTime) 
			{
				try {
					Thread.sleep(endTime - System.currentTimeMillis());
				} catch (InterruptedException e) {
					log.error(e.getMessage());
				}
			}
			*/
		}
	}
/*
	public int doCollection(double lat, double lng, int x, int y, double latUnit, Date date, String queryStr)
	{		
		// Create the query and initialize the parameters
		Query query = new Query();
		query.setCount(100);
		query.setGeoCode(new GeoLocation(lat,lng), 60.0*latUnit, Query.KILOMETERS);
		query.setQuery(queryStr);
		query.setSinceId(sinceID[x][y]);
		query.setSince(new SimpleDateFormat("yyyy-MM-dd").format(date));
		
		boolean firstOne = true;
		int count = 0;
		QueryResult result;
		try{
	       do {
	            result = twitter.search(query);
	            List<Status> tweets = result.getTweets();
	            
	            for (Status tweet : tweets) {
	            	//log.info("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
	                
	            	// Update sinceID of this block
					if(firstOne){
						sinceID[x][y] = tweet.getId();
						firstOne = false;
					}
	                if(this.saveTweets){
						GeoLocation tw_loc = tweet.getGeoLocation();
						double p_lat = 0;
						double p_lon = 0;
						if (tw_loc != null){
							p_lat = tw_loc.getLatitude();
							p_lon = tw_loc.getLongitude();				
						}
						insertTweet(lat, lng, x, y, tweet, p_lat, p_lon);
					}
	                count++;
	            }
	        } while ((query = result.nextQuery()) != null && count < 100);
	       //log.info("#Tweets @ (lat,long) " + count + " (" + lat + ", " + lng + ")");
			
		} catch (TwitterException e) {
			log.error("theme:" + theme + ", error: " + e.getMessage());
			try {
				if(e.getMessage().contains("500"))
					return 0;
				if(e.getMessage().contains("420") || e.getMessage().contains("429") || e.getMessage().contains("limit")) {
					Thread.sleep(1000*60*15);
					log.info("thread sleep for 15 minutes");
				}
			} catch (InterruptedException e2) {
				log.error(e2.getMessage());
			}
			return -1;
		}
        return count;
    }
*/	
	public boolean stop() {
		isRunning = false;
		Thread.currentThread().interrupt();
		return true;
	}

	public STTPoint next(){
		try {
			return ls.take();
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public boolean hasNext(){
		return (ls.peek() != null);
	}

	public void remove(){ 	
		ls.remove();
	}
	
	
	public static void main(String[] args)
	{
		String imgBasePath = Config.getProperty("context") + "results/ds/";
		String tempDir = Config.getProperty("tempDir");
		String url = Config.getProperty("twtrURL");
		FrameParameters fp = new FrameParameters(1*60*60*1000, 0, 2, 2, 24, -125, 50, -66);//CHANGED TO 6 MINUTES for testing...change back !!!!*****
		FrameParameters fpFinal= new FrameParameters(5*60*1000, 0, 0.1, 0.1, 24, -125, 50, -66);
	
		// 1. Twitter-Obama
		TwitterWrapper wrapper = new TwitterWrapper(url, "test", fp, true);
		wrapper.setBagOfWords(new String[]{"test","test2","test3"});
		STTEmageIterator EIterObama = new STTEmageIterator();
		EIterObama.setSTTPointIterator(wrapper);
		
		STMerger mergerObama = new STMerger(fpFinal);
		SpatialMapper sp = SpatialMapper.repeat;
		TemporalMapper tp = TemporalMapper.repeat;		
		mergerObama.addIterator(EIterObama, sp, tp);
		mergerObama.setMergingExpression("mulED(R0,1)");
		DataProcess process = new DataProcess(mergerObama, EIterObama, wrapper, tempDir + "ds0_Twitter-test", imgBasePath + "ds0", "ds0");
		new Thread(process).start();
		/*
		try {

			long timeWindow = 1000*60*60; //*60*24*2;//the last 2 days
			long syncAtMilliSec = 1000;

			double latUnit = 2;
			double longUnit = 2;
			double swLat = 24;
			double swLong = -125;
			double neLat = 50;
			double neLong = -66;

			FrameParameters params = new FrameParameters(timeWindow, syncAtMilliSec, latUnit,longUnit, swLat,swLong , neLat, neLong);
			String url = Config.getProperty("twtrURL");    

			//ds1
			TwitterWrapper wrapper = new TwitterWrapper(url, "Flu", params, true);       	
			//To get the population
			wrapper.getPopulation();

			wrapper.setBagOfWords(new String[]{"obama","president","barack"});
			wrapper.run();

			while (wrapper.hasNext())
			{
				STTPoint a = wrapper.next();
				wrapper.log.info(a.latitude + " " + a.longitude + " " + a.value + " , ");
			}

		} catch(Exception e) {
			log.error(e.getMessage());
		}
		*/
		System.exit(0);
	}
}
