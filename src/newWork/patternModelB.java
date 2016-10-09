package newWork;

import java.util.Vector;
import core.*;

public class patternModelB {
    Vector<Trade> trArray;
    trend currentTrend;
    DataPointArray instanceDPA;
    DataPointArray origDPA;
    String name;
    DataPoint latestLow, latestHigh;
    
    public patternModelB(DataPointArray originalDPA, String stockName) {
        trArray = new Vector<Trade>(10);
        currentTrend = trend.unknown;
        instanceDPA = new DataPointArray();
        origDPA = originalDPA;
        name = stockName;      
        latestLow = null;
        latestHigh = null;
        instanceDPA.fileName = originalDPA.fileName;
    }
    
    /**
     * Simulate the Trade as if the DataPoints are fed to this class in real time.
     * @param origDPA The DataPointArray to be simulated on
     * @param name The name of the Index/Stock
     * @return A Vector of Trade objects as of result of the simulation
     */
    public Vector<Trade> simulate() {
        DataPoint todaysBar = new DataPoint();
        while ((todaysBar = origDPA.getBar()) != null) {
            if (instanceDPA.getSize() < 3) {
                //Less then 3 bars added, unable to proceed
                instanceDPA.insert(todaysBar);
                instanceDPA.filter(0);
                continue;
            }           
            if (todaysBar.getLow() >= instanceDPA.getLast().getLow() && 
                    todaysBar.getHigh() <= instanceDPA.getLast().getHigh()){
                //inside bar
                continue;
            }
            manageTrades(todaysBar);
            morningCheck(todaysBar);
            if (!(todaysBar.getLow() <= instanceDPA.getLast().getLow() &&
                    todaysBar.getHigh() >= instanceDPA.getLast().getHigh())) {
                //not an outside bar
                instanceDPA.insert(todaysBar);
                instanceDPA.checkLastThree();
                instanceDPA.computeEMA20();
                instanceDPA.computeEMA40();
                detectPattern();
            } else {
                //outside bar
                instanceDPA.insert(todaysBar);
                instanceDPA.filter(instanceDPA.getSize()-1);
                instanceDPA.update();
                instanceDPA.computeEMA20();
                instanceDPA.computeEMA40();
                detectPattern();
            }
        } //end of getting bars
        
        //Check to force close last trade
        for (int i = 0; i < trArray.size(); i++) {
            if (!trArray.get(i).isOpen() && trArray.get(i).entryPrice == 0) {
                trArray.remove(i--);
            } else if (trArray.get(i).isOpen()){
                trArray.get(i).Close(instanceDPA.getLast().getDate(), instanceDPA.getLast().getClose());
            }
        }
        return trArray; //for other use
    }
    
    private void detectPattern() {
        if (instanceDPA.hasXRelatives(4)) {
            if (instanceDPA.getLastRelative().getFlag() > 0) {
                //Last flag is high, test for LHLH pattern to set up short
                if (instanceDPA.getXthRelative(2).getHigh() < instanceDPA.getXthRelative(0).getHigh() && 
                        instanceDPA.getXthRelative(3).getLow() < instanceDPA.getXthRelative(1).getLow() &&   
                        instanceDPA.getLast().getLow() < instanceDPA.getXthRelative(1).getLow()) {
                    setupShort();
                }
            } else {
                //Last flag is short, test for HLHL pattern to set up long
                if (instanceDPA.getXthRelative(2).getLow() > instanceDPA.getXthRelative(0).getLow() && 
                        instanceDPA.getXthRelative(3).getHigh() > instanceDPA.getXthRelative(1).getHigh() && 
                        instanceDPA.getLast().getHigh() < instanceDPA.getXthRelative(1).getHigh()) {
                    setupLong();
                }
            }
        }
    }
    
    private void manageTrades(DataPoint tempDP) {
        if (!instanceDPA.hasXRelatives(5) || trArray.isEmpty()) {return;}
        for (int i = 0; i < trArray.size(); i++) {
            if (trArray.get(i).isOpen()) { //in a trade already
                //trade direction checker
                if (trArray.get(i).direction == Direction.longTrade) { //in a long trade
                    //target reached, close trade and wait for reversal
                    if (tempDP.getHigh() >= trArray.get(i).target) {
                        if (tempDP.getOpen() >= trArray.get(i).target) {
                            //open price already exceeded target, close at open price
                            trArray.get(i).Close(tempDP.getDate(), tempDP.getOpen());
                        } else {
                            //close at target
                            trArray.get(i).Close(tempDP.getDate(), trArray.get(i).target);
                        }
                    }
                    //price dropped below stoploss, close trade
                    if (tempDP.getLow() <= trArray.get(i).stopLoss) {
                        if (tempDP.getOpen() <= trArray.get(i).stopLoss) {
                            //open price is lower than stoploss, close at open
                            trArray.get(i).Close(tempDP.getDate(), tempDP.getOpen());
                        } else {
                            //close at stop loss
                            trArray.get(i).Close(tempDP.getDate(), trArray.get(i).stopLoss);
                        }
                    }
                } else { //in a short trade
                    if (tempDP.getLow() <= trArray.get(i).target) {
                        if (tempDP.getOpen() <= trArray.get(i).target) {
                            //open price already exceeded target, close at open price
                            trArray.get(i).Close(tempDP.getDate(), tempDP.getOpen());
                        } else {
                            //close at target
                            trArray.get(i).Close(tempDP.getDate(), trArray.get(i).target);
                        }
                    }
                    //price poped above stoploss: reverse trade
                    if (tempDP.getHigh() >= trArray.get(i).stopLoss) {
                        if (tempDP.getOpen() >= trArray.get(i).stopLoss) {
                            //open price is higher than stoploss, close at open
                            trArray.get(i).Close(tempDP.getDate(), tempDP.getOpen());
                        } else {
                            //close at stop loss
                            trArray.get(i).Close(tempDP.getDate(), trArray.get(i).stopLoss);
                        }
                    }
                }
            }
        }
    }
    
    private void setupLong() {
        if (!main.p.emaReverseFactor) {
            if (instanceDPA.getDP(instanceDPA.getSize() - 1).EMA20 < instanceDPA.getDP(instanceDPA.getSize() - 1).EMA40) {
                return;
            }
        } else {
            if (instanceDPA.getDP(instanceDPA.getSize() - 1).EMA20 >= instanceDPA.getDP(instanceDPA.getSize() - 1).EMA40) {
                return;
            }
        }
        Trade t = new Trade(name);
        double stopLoss = instanceDPA.getXthRelative(0).getLow() - 0.01;
        t.setUp(instanceDPA.getLast().getDate(), 0, stopLoss, stopLoss, Direction.longTrade);
        t.triggerDate = instanceDPA.getLastRelative().getDate();
        for (int i = trArray.size()-1; i >= 0; i--) {
            if (instanceDPA.getLastRelative().getDate().isEqual(trArray.get(i).triggerDate)) {
                //the triggerDate for this trade conflicts with one in the trade array already
                return;
            }
        }
        trArray.add(t);
    }
    
    private void setupShort() {
        if (!main.p.emaReverseFactor) {
            if (instanceDPA.getDP(instanceDPA.getSize() - 1).EMA20 > instanceDPA.getDP(instanceDPA.getSize() - 1).EMA40) {
                return;
            }
        } else {
            if (instanceDPA.getDP(instanceDPA.getSize() - 1).EMA20 <= instanceDPA.getDP(instanceDPA.getSize() - 1).EMA40) {
                return;
            }
        }
        Trade t = new Trade(name);
        double stopLoss = instanceDPA.getXthRelative(0).getHigh() + 0.01;
        t.setUp(instanceDPA.getLast().getDate(), 0, stopLoss, stopLoss, Direction.shortTrade);
        t.triggerDate = instanceDPA.getLastRelative().getDate();
        for (int i = trArray.size()-1; i >= 0; i--) {
            if (instanceDPA.getLastRelative().getDate().isEqual(trArray.get(i).triggerDate)) {
                //the triggerDate for this trade conflicts with one in the trade array already
                return;
            }
        }
        trArray.add(t);
    }
    
    private void morningCheck(DataPoint tempDP) {
        if (trArray.isEmpty()) {return;}
        for (int i = 0; i < trArray.size(); i++) {
            if (trArray.get(i).getEntryPrice()==0 && trArray.get(i).isOpen()==false) {
                //check if a new trade needs to be open in the morning
                if (trArray.get(i).direction == Direction.longTrade) { //long trade pending
                    if (tempDP.getOpen() <= trArray.get(i).stopLoss) {
                        //open price is lower than stop loss
                        trArray.remove(i);
                        i--;
                    } else {
                        if (tempDP.getOpen() - trArray.get(i).stopLoss > main.p.maxLossFactor * tempDP.getOpen() || tempDP.getOpen() - trArray.get(i).stopLoss < main.p.minLossFactor * tempDP.getOpen()) {
                            //doesn't fall in risk range
                            trArray.remove(i);
                            i--;
                        } else {
                            trArray.get(i).target = tempDP.getOpen() + (tempDP.getOpen() - trArray.get(i).stopLoss)*main.p.targetFactor;
                            trArray.get(i).Open(tempDP.getDate(), tempDP.getOpen());
                        }                    
                    }
                } else { //short trade pending
                    if (tempDP.getOpen() >= trArray.get(i).stopLoss) { 
                        //open price is lower than stop loss
                        trArray.remove(i);
                        i--;
                    } else {
                        if (trArray.get(i).stopLoss - tempDP.getOpen() > main.p.maxLossFactor * tempDP.getOpen() || trArray.get(i).stopLoss - tempDP.getOpen() < main.p.minLossFactor * tempDP.getOpen()) {
                            //doesn't fall in risk range
                            trArray.remove(i);
                            i--;
                        } else {
                            trArray.get(i).target = tempDP.getOpen() - (trArray.get(i).stopLoss - tempDP.getOpen())*main.p.targetFactor;
                            trArray.get(i).Open(tempDP.getDate(), tempDP.getOpen());
                        }                    
                    }
                }
            }
        }        
    }    
}

