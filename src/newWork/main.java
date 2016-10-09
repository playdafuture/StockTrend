package newWork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import core.parameters;
import core.trend;
import java.io.FileOutputStream;

public class main {
    public static String path = "C:/SummerProject/";
    public static String[] models = new String[] {"AOm"};
    public static String currentModelName;
    public static Vector<DataPointArray> DPAA;
    /**
     * Prints a csv file in the output folder for all parameters of each model
     */
    public static PrintWriter modelPrinter;
    /**
     * Prints a txt file inside each parameter folder for each model
     */
    public static PrintWriter parameterPrinter;
    public static parameters p;
    
    public static void main(String[] args) throws IOException{
        loadFiles();
        System.out.println("Files successfully loaded");
        initializeSavingFiles();
        
        
        p = new parameters();
        int bestProfitModel = 0, bestWinrateModel = 0;
        double bp = 0, bwr = 0;
        double[] resultList;
        PrintWriter bestResults;
        
        p.targetFactor = 0.2;
        p.minLossFactor = 0.15;
        p.maxLossFactor = 1;
        p.fixStopLoss = 0.20;
        p.emaReverseFactor = false; //0 is false and 1 is true
        System.out.println("Parameters = " + p.toString());
        File pPath = new File((path + "Output/(" + p.toString() + ")/"));
        pPath.mkdirs();
        bestResults = new PrintWriter(pPath + "/BestResults.txt");                        
        for (int m = 0; m <= models.length-1; m++) {
            currentModelName = models[m];
            resultList = runCurrentModel();
            if (m == 0) {
                bp = resultList[0];
                bwr = resultList[4];
            } else {
                if (resultList[0] > bp) {
                    bp = resultList[0];
                    bestProfitModel = m;
                }
                if (resultList[4] > bwr) {
                    bwr = resultList[4];
                    bestWinrateModel = m;
                }
            }
        }
        bestResults.println("Best Profit Model = model" + models[bestProfitModel]);
        bestResults.println("... with an average profit of " + bp);
        bestResults.println("Best WinRate Model = model" + models[bestWinrateModel]);
        bestResults.println("... with an overall win rate of " + bwr);
        bestResults.close();
        System.out.println("End of parameters");
        
//        for (int tar = 0; tar <= 2; tar++) {
//            for (int min = 0; min <= 1; min++) {
//                for (int max = 0; max <= 2; max++) {
//                    for (int ftl = 4; ftl >= 0; ftl--) {
//                        for (int er = 0; er <= 1; er++) {
//                            p.targetFactor = 0.5 * (tar + 1);
//                            p.minLossFactor = 0.03 + min * 0.02;
//                            p.maxLossFactor = 0.1 + max * 0.05;
//                            p.fixStopLoss = (double)ftl*5/100;
//                            p.emaReverseFactor = (er%2 != 0); //0 is false and 1 is true
//
//                            System.out.println("Parameters = " + p.toString());
//                            File pPath = new File((path + "Output/(" + p.toString() + ")/"));
//                            pPath.mkdirs();
//                            bestResults = new PrintWriter(pPath + "/BestResults.txt");                        
//                            for (int m = 0; m <= models.length-1; m++) {
//                                currentModelName = models[m];
//                                resultList = runCurrentModel();
//                                if (m == 0) {
//                                    bp = resultList[0];
//                                    bwr = resultList[4];
//                                } else {
//                                    if (resultList[0] > bp) {
//                                        bp = resultList[0];
//                                        bestProfitModel = m;
//                                    }
//                                    if (resultList[4] > bwr) {
//                                        bwr = resultList[4];
//                                        bestWinrateModel = m;
//                                    }
//                                }
//                            }
//                            bestResults.println("Best Profit Model = model" + models[bestProfitModel]);
//                            bestResults.println("... with an average profit of " + bp);
//                            bestResults.println("Best WinRate Model = model" + models[bestWinrateModel]);
//                            bestResults.println("... with an overall win rate of " + bwr);
//                            bestResults.close();
//                            System.out.println("End of parameters");
//                        }
//                    }                    
//                }
//            }
//        }
    }
    
    /**
     * Loads all files into DPAA based on the index files
     * @throws IOException 
     */
    public static void loadFiles() throws IOException {
        DPAA = new Vector<DataPointArray>();
        //load index files
        Vector<String> fileNames = new Vector<String>();
        FileReader fr;
        
        try {
            fr = new FileReader(path + "files/indexStocks.txt");
            BufferedReader br = new BufferedReader(fr);
            String fileLine;
            while ((fileLine = br.readLine()) != null) {
                fileLine = "S/" + fileLine;
                fileNames.add(fileLine);
            }
        } catch (FileNotFoundException FNFex) {
            System.out.println("ERROR! indexStocks file not found. Program will exit.");
            System.exit(1);
        }
        
        try {
            fr = new FileReader(path + "files/indexCommodities.txt");
            BufferedReader br = new BufferedReader(fr);
            String fileLine;
            while ((fileLine = br.readLine()) != null) {
                fileLine = "C/" + fileLine;
                fileNames.add(fileLine);
            }
        } catch (FileNotFoundException FNFex) {
            System.out.println("ERROR! indexCommodities.txt file not found. Program will exit.");
            System.exit(1);
        }
        
        try {
            fr = new FileReader(path + "files/indexIndices.txt");
            BufferedReader br = new BufferedReader(fr);
            String fileLine;
            while ((fileLine = br.readLine()) != null) {
                fileLine = "I/" + fileLine;
                fileNames.add(fileLine);
            }
        } catch (FileNotFoundException FNFex) {
            System.out.println("ERROR! indexIndices.txt file not found. Program will exit.");
            System.exit(1);
        }
        
        for (int i = 0; i < fileNames.size(); i++) {
            String fileName = fileNames.get(i);
            DataPointArray tempDPA = new DataPointArray();
            try {
                fr = new FileReader(path + "files/"+fileName.substring(2)+"_Daily.csv");
                BufferedReader br = new BufferedReader(fr);
                String fileLine;
                while ((fileLine = br.readLine()) != null) {
                    tempDPA.insert(fileLine);
                }
            } catch (FileNotFoundException FNFex) {
                System.out.println("File not found for " + fileName);
                continue;
            }
            //check if date is in reverse order
            if (tempDPA.getDP(0).getDate().isLarger(tempDPA.getDP(1).getDate())) {
                tempDPA.reverse();
            }
            tempDPA.fileName = fileName.substring(2);
            tempDPA.marketName = fileName.substring(0, 1);
            DPAA.add(tempDPA);
        }
    }
    
    public static void initializeSavingFiles() throws IOException {
        for (int i = 0; i < models.length; i++) {
            modelPrinter = new PrintWriter(path + "Output/model" + models[i] + "Overview.csv");
            modelPrinter.println("Target Profit, Minimum Loss, Maximum Loss, EMA Reversed, StopLoss Updated, Total Profit Gain/Loss, Average Days Held, Winning Rate in Long, Winning Rate in Short, Overall Winning Rate, Trades Completed \n");
            modelPrinter.close();
        }
    }
    
    public static double[] runCurrentModel() throws IOException {
        System.out.println("Running model " + currentModelName);
        File pPath = new File((path + "Output/(" + p.toString() + ")/"));
        pPath.mkdirs();
        modelPrinter = new PrintWriter(new FileOutputStream(new File(path + "Output/model" + currentModelName + "Overview.csv"),true));
        parameterPrinter = new PrintWriter(path + "Output/(" + p.toString() + ")/model" + currentModelName + ".txt");
        
        TradeAnalyzer totalTA = new TradeAnalyzer(path, "Total", currentModelName);
        TradeAnalyzer stockTA = new TradeAnalyzer(path, "Stocks", currentModelName);
        TradeAnalyzer indicTA = new TradeAnalyzer(path, "Indicies", currentModelName);
        TradeAnalyzer commoTA = new TradeAnalyzer(path, "Commodities", currentModelName);
        
        totalTA.reset();
        stockTA.reset();
        indicTA.reset();
        commoTA.reset();
        
        System.out.println("Stocks results");
        parameterPrinter.println("Stocks results");
        runMarket("Stocks", stockTA);
        System.out.println("Commodities results");
        parameterPrinter.println("Commodities results");
        runMarket("Commodities", commoTA);
        System.out.println("Indicies results");
        parameterPrinter.println("Indicies results");
        runMarket("Indicies", indicTA);
        totalTA.add(stockTA.export());
        totalTA.add(indicTA.export());
        totalTA.add(commoTA.export());
        System.out.println("Total results");
        parameterPrinter.println("Total results");
        
        if (totalTA.getSize() == 0) {
            System.out.println("No trades at all in this model");
            double[] resultList = new double[5];
            for (int i = 0; i < 5; i++) {
                resultList[i] = 0;
            }
            parameterPrinter.close();
            modelPrinter.close();
            return resultList;
        } else {
            double[] resultList = totalTA.calculate(true, parameterPrinter);
            parameterPrinter.close();
            modelPrinter.append(p.toString() + "," + resultString(resultList) + "\n");
            modelPrinter.close();
            return resultList;
        }
    }
    
    public static void runMarket(String marketName, TradeAnalyzer TA) throws IOException {        
        for (int i = 0; i < DPAA.size(); i++) {
            if (!(marketName.startsWith(DPAA.get(i).marketName))) {
                continue;
            }
            Vector<Trade> tempTR = null;
            DPAA.get(i).resetIndexReturned();
            tempTR = chooseModelJavaFile(DPAA.get(i).fileName, DPAA.get(i));
            
            if (tempTR == null) {
                return;
            } else if (tempTR.size() > 0) {
                TA.add(tempTR);
            } else {
                System.out.println(DPAA.get(i).fileName + " did not trigger any Trade");
            }
        }
        
        if (TA.getSize() > 0) {
            TA.calculate(false, parameterPrinter);
        } else {
            System.out.println("Trade Table is empty.");
        }
    }

    /**
     * Based on given fileName and DataPointArray, check global static variables, and simulate.
     * @param fileName the index name for record keeping
     * @param testDPA the specific DataPointArray to be tested on
     * @return Vector of trades completed in this simulation
     */
    private static Vector<Trade> chooseModelJavaFile(String fileName, DataPointArray testDPA) {
        Vector<Trade> tempTR = null;
        if (currentModelName.equals("A")) {
            patternModelA myModel = new patternModelA(testDPA, fileName);
            tempTR = myModel.simulate();
        } else if (currentModelName.equals("A1")) {
            patternModelA1 myModel = new patternModelA1(testDPA, fileName);
            tempTR = myModel.simulate();
        } else if (currentModelName.equals("A2")) {
            patternModelA2 myModel = new patternModelA2(testDPA, fileName);
            tempTR = myModel.simulate();
        } else if (currentModelName.equals("Af")) {
            patternModelAfs myModel = new patternModelAfs(testDPA, fileName);
            tempTR = myModel.simulate();
        } else if (currentModelName.equals("AOm")) {
            patternModelAOmar myModel = new patternModelAOmar(testDPA, fileName,0.2,2);
            tempTR = myModel.simulate();
        } else if (currentModelName.equals("B")) {
            patternModelB myModel = new patternModelB(testDPA, fileName);
            tempTR = myModel.simulate();
        } else if (currentModelName.equals("Bf")) {
            patternModelBfs myModel = new patternModelBfs(testDPA, fileName);
            tempTR = myModel.simulate();
        } else if (currentModelName.equals("C")) {
            patternModelC myModel = new patternModelC(testDPA, fileName);
            tempTR = myModel.simulate();
        } else if (currentModelName.equals("Cf")) {
            patternModelCfs myModel = new patternModelCfs(testDPA, fileName);
            tempTR = myModel.simulate();
        } else if (currentModelName.equals("D1")) {
            patternModelD1 myModel = new patternModelD1(testDPA, fileName);
            tempTR = myModel.simulate();
        } else if (currentModelName.equals("D1f")) {
            patternModelD1fs myModel = new patternModelD1fs(testDPA, fileName);
            tempTR = myModel.simulate();
        } else if (currentModelName.equals("D2")) {
            patternModelD2 myModel = new patternModelD2(testDPA, fileName);
            tempTR = myModel.simulate();
        } else if (currentModelName.equals("D2f")) {
            patternModelD2fs myModel = new patternModelD2fs(testDPA, fileName);
            tempTR = myModel.simulate();
        } else {
            System.out.println("Unexpected Model name:" + currentModelName);
            return null;
        }
        return tempTR;
    }
    
    /**
     * In-line function for rounding double into x decimals
     * @param value The double to be rounded
     * @param places Number of decimals desired
     * @return rounded double
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    /**
     * In-line function for converting all the values from the result list to a String for file saving.
     * @param resultList {APA,adh,WRL,WRS,WRA,size}
     * @return String "APA,adh,WRL,WRS,WRA,size\n"
     */
    private static String resultString(double[] resultList) {
        String rs = "";
        for (int i = 0; i <= 4; i++) {
            rs = rs + Double.toString(resultList[i]) + ",";
        }
        rs = rs + Double.toString(resultList[5]);
        return rs;
    }
}
