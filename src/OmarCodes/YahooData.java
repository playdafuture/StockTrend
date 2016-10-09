package OmarCodes;


import java.io.*;
import java.net.*;

public class YahooData {
	private String Symbol, Directory, outputFile, strURL;
	private Date startDate, endDate;
	
	public YahooData (String Symbol, String startD, String endD) {
		this.Directory 	= "./";
		this.Symbol 	= Symbol;
		this.startDate 	= new Date(startD);
		this.endDate 	= new Date(endD); 
		this.outputFile = this.Directory + Symbol + "_ily.csv";
		this.strURL 	= this.buildURL();
	}
	public YahooData (String Symbol, Date startD, Date endD) {
		this.Directory 	= "./";
		this.Symbol 	= Symbol;
		this.startDate 	= startD;
		this.endDate 	= endD; 
		this.outputFile = this.Directory + Symbol + "_ily.csv";
		this.strURL 	= this.buildURL();
	}
	public YahooData (String Symbol, String Directory, String startD, String endD) {
		this.Directory 	= Directory;
		this.Symbol 	= Symbol;
		this.startDate 	= new Date(startD);
		//System.out.println(startD.toString());
		this.endDate 	= new Date(endD); 
		//System.out.println(endD.toString());
		this.outputFile = this.Directory + Symbol + "_Daily.csv";
		this.strURL 	= this.buildURL();
	}
	public YahooData (String Symbol, String Directory, Date startD, Date endD) {
		this.Directory 	= Directory;
		this.Symbol 	= Symbol;
		this.startDate 	= startD;
		//System.out.println(startD.toString());
		this.endDate 	= endD; 
		//System.out.println(endD.toString());
		this.outputFile = this.Directory + Symbol + "_Daily.csv";
		this.strURL 	= this.buildURL();
	}
	
	public YahooData (String Symbol, String Directory, String fileName, Date startD, Date endD) {
		this.Directory 	= Directory;
		this.Symbol 	= Symbol;
		this.startDate 	= startD;
		this.endDate 	= endD; 
		this.outputFile = this.Directory + fileName;
		this.strURL 	= this.buildURL();
	}
	
	public String getFileName() {
		return this.outputFile;
	}
	
    public String buildURL(){
        String MyURL = "http://real-chart.finance.yahoo.com/table.csv?s=";
        //YahooFinance URL Reconstructed
        MyURL = (MyURL + this.Symbol+"&a="+Integer.toString(this.startDate.getMonth()-1)+"&b="
        		+Integer.toString(this.startDate.getDay())+"&c=" + Integer.toString(this.startDate.getYear()) + 
        		"&d="+Integer.toString(this.endDate.getMonth()-1)+"&e="
        		+Integer.toString(this.endDate.getDay())+"&f=" + Integer.toString(this.endDate.getYear()) +"&g=d&ignore=.csv");
        //System.out.println(MyURL);
        return MyURL;
    }
	public boolean getData() {
		//Record the first and last date in the file.  
	    try {
	    	//Open Connection the Yahoo Finance URL
	        URL url  = new URL(strURL);
	        URLConnection urlConn = url.openConnection();
	        InputStreamReader  inStream = null;
	        //Start Reading
	        urlConn = url.openConnection();
	        inStream = new InputStreamReader(urlConn.getInputStream());
	        BufferedReader buff= new BufferedReader(inStream);
	        String stringLine;	
	        
	        //insert beginning date and end date at the beginning of the file.
	        FileWriter fstreamStat = new FileWriter(this.outputFile);
    	  	BufferedWriter outst = new BufferedWriter(fstreamStat);
	        outst.write(this.startDate.toString() +  "\n " + this.endDate.toString() +"\n" );
	      
	        while((stringLine = buff.readLine()) != null) //While not in the header
	        {
	        	outst.write(stringLine + "\n");
	        }
	        //System.out.println(this.outputFile + "Here 1\n");
	        outst.close();
	        return true;
	    }catch (MalformedURLException e) {
	    	System.out.println(e.getMessage());
	    	return false;
	    }catch(IOException e){
		    System.out.println(e.getMessage());
		    return false;
	    }
	}

	public DataPointArray getDataArray() {
		//Record the first and last date in the file.  
	    try {
	    	//Open Connection the Yahoo Finance URL
	        URL url  = new URL(strURL);
	        URLConnection urlConn = url.openConnection();
	        InputStreamReader  inStream = null;
	        //Start Reading
	        urlConn = url.openConnection();
	        inStream = new InputStreamReader(urlConn.getInputStream());
	        BufferedReader buff= new BufferedReader(inStream);
	        String stringLine;	
	        
	        //create a DatsaPointArray
	        DataPointArray  DataArray	= new DataPointArray();
	        //remove first line	        
	        stringLine = buff.readLine();
	        
	        while((stringLine = buff.readLine()) != null) //While not in the header
	        {
	        	if (!DataArray.insert(stringLine)) {
					System.out.println ("Error converting " + stringLine);
				}
	        }
	        //System.out.println(this.outputFile + "Here 1\n");
	        return DataArray;
	    }catch (MalformedURLException e) {
	    	System.out.println(e.getMessage());
	    	return null;
	    }catch(IOException e){
		    System.out.println(e.getMessage());
		    return null;
	    }
	}

}

       