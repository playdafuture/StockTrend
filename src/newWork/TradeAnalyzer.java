package newWork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Vector;
import static newWork.main.path;

public class TradeAnalyzer {
    private String path;
    private Vector<Trade> tradeTable;
    private double profitLong, profitShort;
    private int winningLong, winningShort, tradesLong, tradesShort;
    private int daysHeld;
    private String index;
    private String model;
    
    public TradeAnalyzer(String p, String indexName, String modelName) {
        path = p;
        tradeTable = new Vector<Trade>();
        profitLong = 0;
        profitShort = 0;
        tradesLong = 0;
        tradesShort = 0;
        winningLong = 0;
        winningShort = 0;
        tradesLong = 0;
        tradesShort = 0;
        daysHeld = 0;
        index = indexName;
        model = modelName;
    }
    
    public void add(Vector<Trade> VT) {
        for (int i = 0; i < VT.size(); i++) {
            //adds each trade from this vector to the total vector
            tradeTable.add(VT.get(i));
        }        
    }
    
    public int getSize() {
        return tradeTable.size();
    }
    
    public void reset() {
        profitLong = 0;
        profitShort = 0;
        tradesLong = 0;
        tradesShort = 0;
        winningLong = 0;
        winningShort = 0;
        tradesLong = 0;
        tradesShort = 0;
        daysHeld = 0;
    }
    
    public Vector<Trade> export() {
        return tradeTable;
    }
    
    /**
     * Calculate the key numbers of the trade list and return the results
     * @param displayDetails true if want detailed to be displayed on screen
     * @param totalStatsPW the print writer to write the results to
     * @return
        resultList[0] = APA;
        resultList[1] = adh;
        resultList[2] = WRL;
        resultList[3] = WRS;
        resultList[4] = WRA;
        resultList[5] = tradeTable.size();
     * @throws FileNotFoundException 
     */
    public double[] calculate(boolean displayDetails,PrintWriter totalStatsPW) throws FileNotFoundException {
        boolean printTrades = false;
        PrintWriter pw = null;
        if (printTrades) {
            File tradePath = new File(path + "Output/(" + main.p.toString() + ")/");
            tradePath.mkdirs();
            pw = new PrintWriter(path + "Output/(" + main.p.toString() + ")/" + model + "/" + index + ".csv");
            pw.println("Name, setupDate, entryDate, closingDate, entryPrice, closingPrice, target, stopLoss, direction, percentPL");
        }
        
        double tradeProfit, totalProfit = 0;
        int tradeLength; //days held for one specifit trade
        for (int i = 0; i < tradeTable.size(); i++) {
            Trade t = tradeTable.get(i);
            tradeProfit = t.percentPL();
            totalProfit += tradeProfit;
            
            tradeLength = t.getHoldingDays();
            daysHeld += tradeLength;
            
            if (t.getDirection() > 0) { //trade is long **implement enum**
                tradesLong++;
                profitLong += tradeProfit;
                if (tradeProfit >= 0) {
                    winningLong++;
                }
            } else if (t.getDirection() < 0) { //trade is short
                tradesShort++;
                profitShort += tradeProfit;
                if (tradeProfit >= 0) {
                    winningShort++;
                }
            }
            if (printTrades) {
                pw.print(tradeTable.get(i).toString() + ",");
                pw.println(tradeTable.get(i).percentPL());
            }            
        }
        if (printTrades) {
            pw.close();
        }        
        
        if (tradeTable.size() != tradesLong + tradesShort) {
            System.out.println("Something is fishy! Total trade number is invalid");
        }

        double adh = (double)(daysHeld)/tradeTable.size();
        double APL = profitLong/tradesLong;
        double APS = profitShort/tradesShort;
        double APA = totalProfit/tradeTable.size();
        double WRL = 100*winningLong/tradesLong;
        double WRS = 100*winningShort/tradesShort;
        double WRA = (double)(winningLong+winningShort)/tradeTable.size()*100;
        
        if (displayDetails) {
            System.out.println("Average Days Held = " + adh);        
            System.out.println("Average Profit in Long Trades = " + APL + "%");        
            System.out.println("Average Profit in Short Trades = " + APS + "%");           
            System.out.print("Winning rate in Long = " + WRL + "%");
            System.out.println("(" + (winningLong) + "/" + tradesLong + ")");        
            System.out.print("Winning rate in Short = " + WRS + "%");
            System.out.println("(" + (winningShort) + "/" + tradesShort + ")");        
            System.out.print("Total Win Rate = " + WRA + "%");
            System.out.println("(" + (winningLong+winningShort) + "/" + tradeTable.size() + ")");
        }
        
        System.out.println("Average Profit Overall = " + APA + "%");
        totalStatsPW.println("Average Days Held = " + adh);        
        totalStatsPW.println("Average Profit in Long Trades = " + APL + "%");        
        totalStatsPW.println("Average Profit in Short Trades = " + APS + "%");           
        totalStatsPW.println("Average Profit Overall = " + APA + "%");        
        totalStatsPW.print("Winning rate in Long = " + WRL + "%");
        totalStatsPW.println("(" + (winningLong) + "/" + tradesLong + ")");        
        totalStatsPW.print("Winning rate in Short = " + WRS + "%");
        totalStatsPW.println("(" + (winningShort) + "/" + tradesShort + ")");        
        totalStatsPW.print("Total Win Rate = " + WRA + "%");
        totalStatsPW.println("(" + (winningLong+winningShort) + "/" + tradeTable.size() + ")");
        
        double[] resultList = new double[6];
        resultList[0] = APA;
        resultList[1] = adh;
        resultList[2] = WRL;
        resultList[3] = WRS;
        resultList[4] = WRA;
        resultList[5] = tradeTable.size();
        return resultList;
    }
}
