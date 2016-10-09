package newWork;

import java.util.Vector;
import core.*;

public class patternModelCfs {
    Vector<Trade> trArray;
    trend currentTrend;
    DataPointArray instanceDPA;
    DataPointArray origDPA;
    String name;
    
    public patternModelCfs(DataPointArray originalDPA, String stockName) {
        trArray = new Vector<Trade>(10);
        currentTrend = trend.unknown;
        instanceDPA = new DataPointArray();
        origDPA = originalDPA;
        name = stockName;      
        instanceDPA.fileName = originalDPA.fileName;
    }
    /**
     * Simulate the Trade as if the DataPoints are fed to this class in real time.
     * @param origDPA The DataPointArray to be simulated on
     * @param name The name of the Index/Stock
     * @return A Vector of Trade objects as of result of the simulation
     */
    public Vector<Trade> simulate() {
        DataPoint tempDP = new DataPoint();
        while ((tempDP = origDPA.getBar()) != null) {
            if (instanceDPA.getSize() < 2) {
                //Less then 3 bars added, unable to proceed
                instanceDPA.insert(tempDP);
                instanceDPA.filter(0);
                instanceDPA.computeEMA20();
                instanceDPA.computeEMA40();
                continue;
            }
            manageTrades(tempDP);
            morningCheck(tempDP);
            if (tempDP.getLow() >= instanceDPA.getLast().getLow() && tempDP.getHigh() <= instanceDPA.getLast().getHigh()){
                //inside bar
                continue;
            } else if (!(tempDP.getLow() <= instanceDPA.getLast().getLow() && tempDP.getHigh() >= instanceDPA.getLast().getHigh())){
                //not an outside bar - hence, a "normal" bar
                instanceDPA.insert(tempDP);
                instanceDPA.computeEMA20();
                instanceDPA.computeEMA40();
                instanceDPA.checkLastThree();
                
                if (currentTrend == trend.unknown && instanceDPA.hasThreeRelatives()) {
                    //Unknown trend and 3 relative points exist
                    trendUnknown();
                } else if (currentTrend == trend.up) {
                    trendUp();
                } else if (currentTrend == trend.down) {
                    trendDown();
                }
            } else {
                //outside bar
                if (currentTrend == trend.up) {
                    if (instanceDPA.getLastSL()!=null && tempDP.getLow() < instanceDPA.getLastSL().getLow()) {
                        //strong low is violated, trend will change
                        instanceDPA.insert(tempDP);
                        instanceDPA.filter(instanceDPA.getSize()-1);
                        instanceDPA.update();
                        instanceDPA.computeEMA20();
                        instanceDPA.computeEMA40();
                        currentTrend = trend.unknown;
                        trendUnknown();
                    } else {
                        instanceDPA.insert(tempDP);
                        instanceDPA.filter(instanceDPA.getSize()-1);
                        instanceDPA.update();
                        instanceDPA.computeEMA20();
                        instanceDPA.computeEMA40();
                    }
                } else if (currentTrend == trend.down) {
                    if (instanceDPA.getLastSH()!=null && tempDP.getHigh() > instanceDPA.getLastSH().getHigh()) {
                        //strong low is violated, trend will change
                        instanceDPA.insert(tempDP);
                        instanceDPA.filter(instanceDPA.getSize()-1);
                        instanceDPA.update();
                        instanceDPA.computeEMA20();
                        instanceDPA.computeEMA40();
                        currentTrend = trend.unknown;
                        trendUnknown();
                    } else {
                        instanceDPA.insert(tempDP);
                        instanceDPA.filter(instanceDPA.getSize()-1);
                        instanceDPA.update();
                        instanceDPA.computeEMA20();
                        instanceDPA.computeEMA40();
                    }
                } else { //trend unknown
                    instanceDPA.insert(tempDP);
                    instanceDPA.filter(instanceDPA.getSize()-1);
                    instanceDPA.update();
                    instanceDPA.computeEMA20();
                    instanceDPA.computeEMA40();
                    currentTrend = trend.unknown;
                    trendUnknown();
                }
                //end of outside bar
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
    
    private void updateStops() {
        if (trArray.isEmpty() || !lastTrade().isOpen()) {return;}
        if (lastTrade().direction == Direction.longTrade) { //in a long trade
            DataPoint dp = instanceDPA.getLastSL();
            if (dp != null && lastTrade().stopLoss < dp.getLow()-0.01) {
                lastTrade().stopLoss = dp.getLow() - 0.01;
            }
        } else { //in a short trade
            DataPoint dp = instanceDPA.getLastSH();
            if (dp != null && lastTrade().stopLoss > dp.getHigh()+0.01) {
                lastTrade().stopLoss = dp.getHigh() + 0.01;
            }
        }
    }
    
    private void trendUnknown() {
        if (!instanceDPA.hasThreeRelatives()) {return;}
        //Unknown trend and 3 relative points exist
        if (instanceDPA.getLastSL() != null && instanceDPA.getLast().getLow() < instanceDPA.getLastSL().getLow()) {
            currentTrend = trend.down;
            setupShort();
        } else if (instanceDPA.getLastSH() != null && instanceDPA.getLast().getHigh() > instanceDPA.getLastSH().getHigh()) {
            currentTrend = trend.up;
            setupLong();
        }
    }
    
    private void manageTrades(DataPoint tempDP) {
        if (!instanceDPA.hasThreeRelatives() || trArray.isEmpty()) {return;}
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
                    //price dropped below stoploss: reverse trade immediately
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
    
    private void trendUp() {
        if (!instanceDPA.hasThreeRelatives() || trArray.isEmpty()) {return;}
        if (instanceDPA.getLastSL() != null && instanceDPA.getLast().getLow() < instanceDPA.getLastSL().getLow()) {
            //reversal detected
            currentTrend = trend.down;
            setupShort();
        }
    }
    
    private void trendDown() {
        if (!instanceDPA.hasThreeRelatives() || trArray.isEmpty()) {return;}
        if (instanceDPA.getLastSH() != null && instanceDPA.getLast().getHigh() > instanceDPA.getLastSH().getHigh()) {
            //reversal detected
            currentTrend = trend.up;
            setupLong();
        }
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
        double stopLoss;
        if (instanceDPA.getLastRH().getFlag() == 2) {
            stopLoss = instanceDPA.getLastRH().getHigh();
        } else {
            if (instanceDPA.getSecondRHIndex() != -1) {
                stopLoss = Math.max(instanceDPA.getLastRH().getHigh(), instanceDPA.getSecondRH().getHigh());
            } else {
                stopLoss = instanceDPA.getLastRH().getHigh();
            }
        }
        stopLoss += 0.01;
        t.setUp(instanceDPA.getLast().getDate(), 0, stopLoss, stopLoss, Direction.shortTrade);
        trArray.add(t);
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
        double stopLoss;
        if (instanceDPA.getLastRL().getFlag() == -2) {
            stopLoss = instanceDPA.getLastRL().getLow();
        } else {
            if (instanceDPA.getSecondRLIndex() != -1) {
                stopLoss = Math.min(instanceDPA.getLastRL().getLow(), instanceDPA.getSecondRL().getLow());
            } else {
                stopLoss = instanceDPA.getLastRL().getLow();
            }
        }
        stopLoss -= 0.01;
        t.setUp(instanceDPA.getLast().getDate(), 0, stopLoss, stopLoss, Direction.longTrade);
        trArray.add(t);
    }
    
    private void morningCheck(DataPoint tempDP) {
        if (trArray.isEmpty()) {return;}
        for (int i = 0; i < trArray.size(); i++) {
            if (trArray.get(i).getEntryPrice()==0 && trArray.get(i).isOpen()==false) {
                //check if a new trade needs to be open in the morning
                if (trArray.get(i).direction == Direction.longTrade) { //long trade pending
                    if (tempDP.getOpen() <= trArray.get(i).stopLoss) { //open price is lower than stop loss
                        trArray.remove(i);
                        i--;
                    } else {
                        if (tempDP.getOpen() - trArray.get(i).stopLoss > main.p.maxLossFactor * tempDP.getOpen() || tempDP.getOpen() - trArray.get(i).stopLoss < main.p.minLossFactor * tempDP.getOpen()) {
                            trArray.remove(i);
                            i--;
                        } else {
                            trArray.get(i).target = tempDP.getOpen() + (tempDP.getOpen() - trArray.get(i).stopLoss)*main.p.targetFactor;
                            trArray.get(i).Open(tempDP.getDate(), tempDP.getOpen());
                            if (main.p.fixStopLoss != 0) {
                                trArray.get(i).stopLoss = tempDP.getOpen() - tempDP.getOpen() * main.p.fixStopLoss;
                            }
                        }                    
                    }
                } else { //short trade pending
                    if (tempDP.getOpen() >= trArray.get(i).stopLoss) { //open price is lower than stop loss
                        trArray.remove(i);
                        i--;
                    } else {
                        if (trArray.get(i).stopLoss - tempDP.getOpen() > main.p.maxLossFactor * tempDP.getOpen() || trArray.get(i).stopLoss - tempDP.getOpen() < main.p.minLossFactor * tempDP.getOpen()) {
                            trArray.remove(i);
                            i--;
                        } else {
                            trArray.get(i).target = tempDP.getOpen() - (trArray.get(i).stopLoss - tempDP.getOpen())*main.p.targetFactor;
                            trArray.get(i).Open(tempDP.getDate(), tempDP.getOpen());
                            if (main.p.fixStopLoss != 0) {
                                trArray.get(i).stopLoss = tempDP.getOpen() + tempDP.getOpen() * main.p.fixStopLoss;
                            }
                        }                    
                    }
                }
            }
        }        
    }
    
    private Trade lastTrade() { //inline function for saving space and clear reading
        if (trArray.isEmpty()) {
            return null;
        }
        return trArray.get(trArray.size()-1);
    }
    
}

