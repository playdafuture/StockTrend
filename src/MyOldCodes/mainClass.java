package MyOldCodes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Future
 */
public class mainClass {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        String fileName = "table.csv";
        DataPointArray aapl = new DataPointArray(10000);
        load(fileName, aapl);
        System.out.println("File loaded successfully");
        System.out.println("Total entries = " + aapl.getElements());
        
//        int[] aaplPatternA = PatternAnalyzer.searchPatternA(aapl);
//        System.out.println("A shape pattern recorded. Total records = " + aaplPatternA.length);
//        int[] aaplPatternV = PatternAnalyzer.searchPatternV(aapl);
//        System.out.println("V shape pattern recorded. Total records = " + aaplPatternV.length);
        int[] aaplPatternDV = PatternAnalyzer.searchDeepV(aapl);
        System.out.println("DV shape pattern recorded. Total records = " + aaplPatternDV.length);
        
        double[][] aaplPatterDVSim = PatternAnalyzer.simulateDeepV(aapl, aaplPatternDV);
        PatternAnalyzer.printResult(aaplPatterDVSim);
        
        
    }
    
    public static void load(String fileName, DataPointArray arrayName) throws IOException {
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String fileLine;
            while ((fileLine = br.readLine()) != null) {
                arrayName.insert(fileLine);
            }
        } catch (FileNotFoundException FNFex) {
            System.out.println("ERROR! File not found. Program will exit.");
            System.exit(1);
        }
    }
    
}