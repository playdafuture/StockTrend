package legacyModels;

import java.util.Vector;
import newWork.DataPoint;
import newWork.DataPointArray;
import core.*;

public class targetModelB {
    Vector<Trade> trArray;
    trend currentTrend;
    DataPointArray instanceDPA;
    DataPointArray origDPA;
    String name;
    double riskFactor;
    
    public targetModelB(DataPointArray originalDPA, String stockName, double rf) {
        trArray = new Vector<Trade>(10);
        currentTrend = trend.unknown;
        instanceDPA = new DataPointArray();
        origDPA = originalDPA;
        name = stockName;      
        riskFactor = rf;
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
            morningCheck(tempDP);
            if (instanceDPA.getSize() < 2) {
                //Less then 3 bars added, unable to proceed
                instanceDPA.insert(tempDP);
                instanceDPA.filter(0);
                instanceDPA.computeEMA20();
                instanceDPA.computeEMA40();
                continue;
            }
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
                instanceDPA.insert(tempDP);
                if (trArray.isEmpty() || !lastTrade().isOpen()) { //no trade or trade is not open
                    instanceDPA.filter(instanceDPA.getSize()-1);
                    instanceDPA.update();
                    instanceDPA.computeEMA20();
                    instanceDPA.computeEMA40();
                    if (currentTrend == trend.up) {
                        trendUp();
                    } else if (currentTrend == trend.down) {
                        trendDown();
                    } else {
                        trendUnknown();
                    }
                    continue;
                } else if (currentTrend == trend.up) {
                    //check if close condition is met
                    if (tempDP.getLow() <= lastTrade().stopLoss) {
                        //close immediately but open at the end of the day
                        lastTrade().Close(tempDP.getDate(), lastTrade().stopLoss);
                        currentTrend = trend.unknown;
                    } else if (instanceDPA.getLastSL() != null && instanceDPA.getLast().getLow() < instanceDPA.getLastSL().getLow()) { 
                        //strong low is broken
                        currentTrend = trend.unknown;
                    }
                    //end of up trend
                } else if (currentTrend == trend.down) {
                    //check if close condition is met
                    if (tempDP.getHigh() >= lastTrade().stopLoss) {
                        //close immediately but open at the end of the day
                        lastTrade().Close(tempDP.getDate(), lastTrade().stopLoss);
                        currentTrend = trend.unknown;
                    } else if (instanceDPA.getLastSH() != null && instanceDPA.getLast().getHigh() > instanceDPA.getLastSH().getHigh()) { 
                        //strong high is broken
                        currentTrend = trend.unknown;
                    }
                    //end of down trend
                }
                instanceDPA.filter(instanceDPA.getSize()-1);
                instanceDPA.update();
                instanceDPA.computeEMA20();
                instanceDPA.computeEMA40();
                if (currentTrend == trend.unknown) {
                    trendUnknown();
                }
                //end of outside bar
            }
        } //end of getting bars
        
        //Check to force close last trade
        if (lastTrade() != null && lastTrade().isOpen()) {
            lastTrade().Close(instanceDPA.getLast().getDate(), instanceDPA.getLast().getClose());
        } else if (lastTrade() != null) { //trade is only setup, remove from list
            trArray.remove(trArray.size()-1);
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
        if (!trArray.isEmpty() && lastTrade().isOpen()) { //we're in a trade
            manageTrades();
            if (instanceDPA.getLastSL() != null && instanceDPA.getLast().getLow() < instanceDPA.getLastSL().getLow()) {
                currentTrend = trend.down;
            } else if (instanceDPA.getLastSH() != null && instanceDPA.getLast().getHigh() > instanceDPA.getLastSH().getHigh()) {
                currentTrend = trend.up;
            }
        } else {
            if (instanceDPA.getLastSL() != null && instanceDPA.getLast().getLow() < instanceDPA.getLastSL().getLow()) {
                currentTrend = trend.down;
                setupShort();
            } else if (instanceDPA.getLastSH() != null && instanceDPA.getLast().getHigh() > instanceDPA.getLastSH().getHigh()) {
                currentTrend = trend.up;
                setupLong();
            }
        }
    }
    
    private void manageTrades() {
        if (!instanceDPA.hasThreeRelatives()) {return;}
        if (!trArray.isEmpty() && lastTrade().isOpen()) { //in a trade already
            //trade direction checker
            if (lastTrade().direction == Direction.longTrade) { //in a long trade
                //target reached, close trade and wait for reversal
                if (instanceDPA.getLast().getHigh() >= lastTrade().target) {
                    if (instanceDPA.getLast().getOpen() >= lastTrade().target) {
                        //open price already exceeded target, close at open price
                        lastTrade().Close(instanceDPA.getLast().getDate(), instanceDPA.getLast().getOpen());
                    } else {
                        //close at target
                        lastTrade().Close(instanceDPA.getLast().getDate(), lastTrade().target);
                    }
                }
                //price dropped below stoploss: reverse trade immediately
                if (instanceDPA.getLast().getLow() <= lastTrade().stopLoss) {
                    if (instanceDPA.getLast().getOpen() <= lastTrade().stopLoss) {
                        //open price is lower than stoploss, close at open
                        lastTrade().Close(instanceDPA.getLast().getDate(), instanceDPA.getLast().getOpen());
                    } else {
                        //close at stop loss
                        lastTrade().Close(instanceDPA.getLast().getDate(), lastTrade().stopLoss);
                    }
                }
            } else { //in a short trade
                if (instanceDPA.getLast().getLow() <= lastTrade().target) {
                    if (instanceDPA.getLast().getOpen() <= lastTrade().target) {
                        //open price already exceeded target, close at open price
                        lastTrade().Close(instanceDPA.getLast().getDate(), instanceDPA.getLast().getOpen());
                    } else {
                        //close at target
                        lastTrade().Close(instanceDPA.getLast().getDate(), lastTrade().target);
                    }
                }
                //price poped above stoploss: reverse trade
                if (instanceDPA.getLast().getHigh() >= lastTrade().stopLoss) {
                    if (instanceDPA.getLast().getOpen() >= lastTrade().stopLoss) {
                        //open price is higher than stoploss, close at open
                        lastTrade().Close(instanceDPA.getLast().getDate(), instanceDPA.getLast().getOpen());
                    } else {
                        //close at stop loss
                        lastTrade().Close(instanceDPA.getLast().getDate(), lastTrade().stopLoss);
                    }                        
                    currentTrend = trend.up;
                    setupLong();
                }
            }
        }
    }
    
    private void trendUp() {
        if (!instanceDPA.hasThreeRelatives()) {return;}
        if (!trArray.isEmpty() && lastTrade().isOpen()) { //in a trade already
            manageTrades();
            if (instanceDPA.getLastSL() != null && instanceDPA.getLast().getLow() < instanceDPA.getLastSL().getLow()) {
                //reversal detected
                currentTrend = trend.down;
            }
        } else { //not yet in a trade
            if (instanceDPA.getLastSL() != null && instanceDPA.getLast().getLow() < instanceDPA.getLastSL().getLow()) {
                //reversal detected
                currentTrend = trend.down;
                setupShort();
            }
        }        
    }
    
    private void trendDown() {
        if (!instanceDPA.hasThreeRelatives()) {return;}
        //target reached, close trade and wait for reversal
        if (!trArray.isEmpty() && lastTrade().isOpen()) { //in a trade already
            manageTrades();
            if (instanceDPA.getLastSH() != null && instanceDPA.getLast().getHigh() > instanceDPA.getLastSH().getHigh()) {
                //reversal detected
                currentTrend = trend.up;
            }
        } else { //not yet in a trade
            if (instanceDPA.getLastSH() != null && instanceDPA.getLast().getHigh() > instanceDPA.getLastSH().getHigh()) {
                //reversal detected
                currentTrend = trend.up;
                setupLong();
            }
        }
    }
    
    private void setupShort() {
        if (instanceDPA.getDP(instanceDPA.getSize() - 1).EMA40 < instanceDPA.getDP(instanceDPA.getSize() - 2).EMA40) {
            return;
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
        if (instanceDPA.getDP(instanceDPA.getSize() - 1).EMA40 > instanceDPA.getDP(instanceDPA.getSize() - 2).EMA40) {
            return;
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
        double maxLoss = 0.1;
        if (!trArray.isEmpty() && lastTrade().getEntryPrice()==0 && lastTrade().isOpen()==false) {
            //check if a new trade needs to be open in the morning
            if (lastTrade().direction == Direction.longTrade) { //long trade pending
                if (tempDP.getOpen() <= lastTrade().stopLoss) { //open price is lower than stop loss
                    trArray.remove(trArray.size()-1);
                    currentTrend = trend.unknown;
                } else {
                    if (tempDP.getOpen() - lastTrade().stopLoss > maxLoss * tempDP.getOpen()) {
                        trArray.remove(trArray.size()-1);
                        currentTrend = trend.unknown;
                    } else {
                        lastTrade().target = tempDP.getOpen() + (tempDP.getOpen() - lastTrade().stopLoss)*riskFactor;
                        lastTrade().Open(tempDP.getDate(), tempDP.getOpen());
                    }                    
                }
            } else { //short trade pending
                if (tempDP.getOpen() >= lastTrade().stopLoss) { //open price is lower than stop loss
                    trArray.remove(trArray.size()-1);
                    currentTrend = trend.unknown;
                } else {
                    if (lastTrade().stopLoss - tempDP.getOpen() > maxLoss * tempDP.getOpen()) {
                        trArray.remove(trArray.size()-1);
                        currentTrend = trend.unknown;
                    } else {
                        lastTrade().target = tempDP.getOpen() - (lastTrade().stopLoss - tempDP.getOpen())*riskFactor;
                        lastTrade().Open(tempDP.getDate(), tempDP.getOpen());
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

