package OmarCodes;


import java.io.*;
public class DataFeed {
	//This array is ordered 0 being the most recent data and .getSize -1 is the last.
	private DataPointArray mData;
	private String fileName;
	private String mTicker;
	private String mDirectory;
	private Date startDate, endDate;
	private downloader mDownloader;
	BufferedReader   mbuff;
	
	DataFeed (String Symbol) {
		mTicker 		= Symbol;
		mDirectory 		= "./";
		fileName 		= mDirectory + mTicker + "_Daily.csv";	 
		mDownloader 	= null;
		mData			= new DataPointArray(1000);
	}
	DataFeed (String Symbol, String Directory, Date startD, Date endD) {
		mTicker 		= Symbol;
		mDirectory 		= Directory;
		fileName 		= Directory + mTicker + "_Daily.csv";
		startDate 		= startD;
		endDate			= endD;
		mDownloader		= null;
		mData			= new DataPointArray(1000);
	}
	public DataPointArray getArray() {
		return mData;
	}
	//this method will load data into mData that has been pased to the object.
	//if the file does not exist it will be downloaded from yahoo.
	
	public boolean Download() {
			if (!(new File(this.fileName).isFile())) {
				System.out.println ("Input file does not exist, downloading to " + this.fileName);
				mDownloader = new downloader (mTicker, mDirectory, startDate, endDate);
				if (!mDownloader.load()) {
					System.out.println ("Enable to download data for " + mTicker);
					return false;
				}
				this.fileName = mDownloader.getFileName();
			}
			return true;
	}
	
	public boolean loadToArray() {
		try {
			if (!(new File(this.fileName).isFile())) {
				System.out.println ("Input file still does not exist!" + this.fileName);
				System.out.println("Donwloading data to file");
				if(!Download()) {
					System.out.println("Anable to dowload data for " + mTicker);
					return false;
				}
			}
			File myFile = new File(this.fileName); 
			
			FileReader inFile = new FileReader(myFile);
			BufferedReader   mbuff = new BufferedReader(inFile);
			String strLine;
			//strip out the three first lines
			mbuff.readLine();
			mbuff.readLine();
			mbuff.readLine();
			
			while((strLine = mbuff.readLine()) != null) {
				if (!mData.insert(strLine)) {
					System.out.println ("Error converting " + strLine);
				}
			}
			//reorder the array
			mbuff.close();
			mData.reverse(); //reverse the array so that index 0 points to the oldest data.
			return true;
		}catch (IOException e) {
			System.out.println ("Error reading file: " + e.getMessage());
			return false;
		}
	}
	public DataPoint getNewBar() {
		//get new data to DataPoint array. store in file.
		if (mData.getSize() == 0)
			return null;
		return (mData.remove());
	}
}
