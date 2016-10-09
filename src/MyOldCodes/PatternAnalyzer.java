/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyOldCodes;

/**
 *
 * @author Future
 */
public class PatternAnalyzer {
    public static int[] searchPatternA(DataPointArray dpa) {
        int[] tempList = new int[dpa.getElements()];
        int counter = 0;
        DataPoint left, mid, right;
        
        for (int i = 0; i < dpa.getElements() - 2; i++) {
            left = dpa.show(i);
            mid = dpa.show(i + 1);
            right = dpa.show(i + 2);            
            if(mid.getLow() > left.getLow() && mid.getHigh() > left.getHigh()) {
                if (right.getHigh() < mid.getHigh() && right.getLow() < mid.getLow()) {
                    tempList[counter++] = i + 1;
                }
            }            
        }
        int[] AList = new int[counter + 1];
        for (int i = 0; i < counter; i++) {
            AList[i] = tempList[i];
        }
        return AList;
    }
    
    public static int[] searchPatternV(DataPointArray dpa) {
        int[] tempList = new int[dpa.getElements()];
        int counter = 0;
        DataPoint left, mid, right;
        
        for (int i = 0; i < dpa.getElements() - 2; i++) {
            left = dpa.show(i);
            mid = dpa.show(i + 1);
            right = dpa.show(i + 2);            
            if(mid.getLow() < left.getLow() && mid.getHigh() < left.getHigh()) {
                if (right.getHigh() > mid.getHigh() && right.getLow() > mid.getLow()) {
                    tempList[counter++] = i + 1;
                }
            }            
        }
        int[] VList = new int[counter + 1];
        for (int i = 0; i < counter; i++) {
            VList[i] = tempList[i];
        }
        return VList;
    }
    
    public static int[] searchDeepV(DataPointArray dpa) {
        int[] tempList = new int[dpa.getElements()];
        int counter = 0;
        DataPoint LL, L, M, R, RR;
        
        for (int i = 0; i < dpa.getElements() - 4; i++) {
            LL = dpa.show(i);
            L = dpa.show(i+1);
            M = dpa.show(i+2);
            R = dpa.show(i+3);
            RR = dpa.show(i+4);
            if (goingDown(LL,L) && goingDown(L,M) && goingUp(M,R) && goingUp(R,RR)) {
                tempList[counter++] = i;
            }
        }
        int[] DVList = new int[counter + 1];
        for (int i = 0; i < counter; i++) {
            DVList[i] = tempList[i];
        }
        return DVList;
    }
    
    private static boolean goingDown(DataPoint a, DataPoint b) {
        if (a.getOpen() > b.getOpen() && a.getClose() > b.getClose()) {
            return true;
        }
        return false;
    }
    
    private static boolean goingUp(DataPoint a, DataPoint b) {
        if (a.getOpen() < b.getOpen() && a.getClose() < b.getClose()) {
            return true;
        }
        return false;
    }
    
    public static double[][] simulateDeepV(DataPointArray dpa, int[] markedLocations) {
        /**
         * In each simulation instance, the records we care is
         * Buying price and Selling price
         * result[i][0] is buying price for that instance
         * result[i][1] is selling price for that instance
         */
        double[][] result = new double[markedLocations.length][2];
        for (int i = 0; i < markedLocations.length - 1; i++) {
            result[i][0] = dpa.show(markedLocations[i]+5).getOpen();
            int hold = 0;
            try {
                while ((markedLocations[i]+5+hold+1 < dpa.getElements())
                    && goingUp(dpa.show(markedLocations[i]+5+hold), dpa.show(markedLocations[i]+5+hold+1))) {
                    hold++;
                }
                result[i][1] = dpa.show(markedLocations[i]+5+hold+1).getOpen();
            } catch (ArrayIndexOutOfBoundsException oob) {
                System.out.println("End of data reached.");
            }
        }
        return result;
    }
    
    public static void printResult(double[][] instanceResult) {
        double[][] result = instanceResult;
        int length = result.length;
        int profit = 0;
        int winCount = 0;
        double bp, sp;
        double bpsum = 0;
        for (int i = 0; i < length - 1; i++) {
            bp = result[i][0];
            bpsum += bp;
            sp = result[i][1];
            System.out.print("Instance " + i + ": ");
            if (sp > bp) {
                System.out.println("WIN");
                winCount++;
            } else {
                System.out.println("LOSE");
            }
            System.out.println("Instance profit = " + (sp - bp));
            profit += sp-bp;
            System.out.print("Buying price = " + bp + "; ");
            System.out.println("Selling price = " + sp);
        }
        System.out.println("Total win rate = " + winCount*100/length + "%");
        System.out.println("Total Profit = " + profit);
        System.out.println("Profit percentage = " + profit*100/bpsum + "%");
        System.out.print("End result = ");
        if (profit > 0) {
            System.out.println("WIN");
        } else {
            System.out.println("LOSE");
        }
    }
}
