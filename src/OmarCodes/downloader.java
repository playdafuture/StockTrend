package OmarCodes;

import java.io.*;

public class downloader {
	private String fileName, mDirectory, mTicker;
	Date startDate, endDate;

	//It requires a file of tickers and a destination directory
	public downloader(String inputFile, String Directory, String startD, String endD) {
		fileName 	= inputFile;
		mDirectory 	= Directory;
		startDate 	= new Date(startD);
		endDate 	= new Date(endD);
		mTicker 	= inputFile;
	}
	public downloader(String inputFile, String Directory, Date startD, Date endD) {
		fileName 	= inputFile;
		mDirectory 	= Directory;
		startDate 	= startD;
		endDate 	= endD;
		mTicker 	= inputFile;
	}
	public Date getStartDate() {return startDate;}
	public Date getEndtDate() {return endDate;}
	public String getFileName() { return this.fileName;}
	
	public boolean loadAll() throws IOException {
		if (!(new File(this.fileName).isFile())) {
			System.out.println ("Input file does not exist");
			return false;
		}
		if (!(new File(mDirectory).exists())) {
			System.out.println("The destination directory does not exist ");
			return false;
		}
		BufferedReader   buff = null;
		try {
			//open the ticker file
			FileReader inFile = new FileReader(this.fileName);
			buff = new BufferedReader(inFile);
			String strLine;
			while((strLine = buff.readLine()) != null){
				//The Symbol is in strLine.
				//Create a YahooData object and get data
				System.out.print("Downloading " + strLine + "... ");
				YahooData MyYahoo = new YahooData(strLine, this.mDirectory, startDate, endDate);
				if (MyYahoo.getData())
					System.out.println("Done!");
				else {
					System.out.println("Failed!");
				}
			}
		}catch(IOException e) {
			System.out.println(e.getMessage());
			buff.close();
			return false;
		}
		buff.close();
		return true;	
	}
	//loads a single ticker to a file and returns a DataArray
	public boolean load() {
		if (!(new File(mDirectory).exists())) {
			System.out.println("The destination directory does not exist ");
			return false;
		}
		YahooData MyYahoo = new YahooData(mTicker, this.mDirectory, startDate, endDate);
		this.fileName = MyYahoo.getFileName();
		return MyYahoo.getData();
	}
}
/*	public static void main (String [] args) {
		String Directory = "C:/Users/Omar/Desktop/Omar/JerryWork/Data/";
		Date startDate = new Date("01/01/2000", "MDY");
		Date endDate = new Date("12/31/2014", "MDY");
		
		downloader Stocks 		= new downloader(Directory + "stocks.txt", Directory, startDate.toString(),endDate.toString());
		downloader Indices		= new downloader(Directory +"indices.txt", Directory, startDate.toString(),endDate.toString());
		downloader Commodities	= new downloader(Directory +"commodities.txt", Directory, startDate.toString(),endDate.toString());
		downloader Forex		= new downloader(Directory +"forex.txt", Directory, startDate.toString(),endDate.toString());
		Stocks.load();
		Indices.load();
		Commodities.load();
		Forex.load();
	}
}*/
