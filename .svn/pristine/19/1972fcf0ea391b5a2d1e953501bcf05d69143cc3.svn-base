package pl.edu.agh.cs.kraksim.rlcd;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import pl.edu.agh.cs.kraksim.core.Lane;
import pl.edu.agh.cs.kraksim.iface.block.BlockIView;
import pl.edu.agh.cs.kraksim.iface.block.LaneBlockIface;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoCursor;
import pl.edu.agh.cs.kraksim.iface.carinfo.CarInfoIView;
import pl.edu.agh.cs.kraksim.iface.carinfo.LaneCarInfoIface;
import pl.edu.agh.cs.kraksim.iface.eval.LaneEvalIface;

class LaneRLCDExt implements LaneEvalIface{

	 private static final Logger logger     = Logger.getLogger( LaneRLCDExt.class );
	 
	  private static final int    ZONELENGHT = 20;

	  private Lane                lane;

	  private RLCDEView             ev;
	  private RLCDParams            params;

	  private LaneCarInfoIface    laneCarInfoExt;
	  private LaneBlockIface      laneBlockExt;

	  private int                 n;
	  private float[]             V;
	  private float[]             Qblocked;
	  private float[]             Qunblocked;
	  //private CellStat[]          cells;
	  //list modeli
	  private List<Model>	  	  models = new ArrayList<Model>();
	  //model, ktory jest aktualnie w uzyciu 
	  //TODO sprawdzic, czy jak sie aktualizuje model, to jego odpowiednik w models tez sie aktualizuje
	  private Model				  model;
	  private float               evaluation;
	  private int                 queueLength;

	  private String              id;

	  private static final int M = 10;
	  private static final float omega = (float) 0.5;
	  private static final float p = (float) 1;
	  private static final float lambda = (float) 0.1;
	  
	  final private static class Model{
		  private CellStat[] cells;
		  private float error = 0;
		  Model(CellStat[] cells){
			  this.cells = cells;
		  }
	  }
	  
	  final private static class TransitionStat
	  {
	    private Lane destLane;
	    private int  destPos;
	    private int  count;
	    //probability of transition
	    private float t;
	    private float deltaT;
	    private float r;
	    private float deltaR;

	    private TransitionStat(Lane destLane, int destPos, int count) {
	      this.destLane = destLane;
	      this.destPos = destPos;
	      this.count = count;
	      t = deltaT = r = deltaR = 0;
	    }
	    private TransitionStat(Lane destLane, int destPos, int count, 
	    		float t, float r) {
		      this.destLane = destLane;
		      this.destPos = destPos;
		      this.count = count;
		      this.t = t;
		      this.r = r;
		      deltaT = deltaR = 0;
	    }
	    private void updateDeltaT(int nm, int krDelta){
	    	nm = nm < M ? nm : M;
	    	deltaT = (float) ((1.0/((float)nm+1.0))*((float)krDelta - t));
	    }
	    private void updateT(){
	    	t+=deltaT;
	    }
	    private void updateDeltaR(int nm, int rr){
	    	nm = nm < M ? nm : M;
	    	deltaR = (float) ((1.0/((float)nm+1.0))*((float)rr - r));
	    }
	    private void updateR(){
	    	r+=deltaR;
	    }
	  }

	  final private static class CellStat
	  {
	    private int              carOnBlockedCount;
	    private TransitionStat[] transOnBlockedStats;
	    private int              carOnUnblockedCount;
	    private TransitionStat[] transOnUnblockedStats;

	    private void countCarOnBlocked(Lane destLane, int destPos) {
	      carOnBlockedCount++;

	      if ( transOnBlockedStats != null ) {
	        for (int i = 0; i < transOnBlockedStats.length; i++) {
	          TransitionStat ts = transOnBlockedStats[i];
	          if ( ts.destLane == destLane && ts.destPos == destPos ) {
	            ts.count++;
	            return;
	          }
	        }
	      }

	      transOnBlockedStats = extend( transOnBlockedStats, new TransitionStat(
	          destLane, destPos, 1 ) );
	    }

	    private void countCarOnUnblocked(Lane destLane, int destPos) {
	      carOnUnblockedCount++;

	      if ( transOnUnblockedStats != null ) {
	        for (int i = 0; i < transOnUnblockedStats.length; i++) {
	          TransitionStat ts = transOnUnblockedStats[i];
	          if ( ts.destLane == destLane && ts.destPos == destPos ) {
	            ts.count++;
	            return;
	          }
	        }
	      }

	      transOnUnblockedStats = extend( transOnUnblockedStats, new TransitionStat(
	          destLane, destPos, 1 ) );
	    }

	    private void halveCounters() {
	      if ( carOnBlockedCount > 0 ) {
	        carOnBlockedCount = 0;
	        for (int i = 0; i < transOnBlockedStats.length; i++) {
	          transOnBlockedStats[i].count /= 2;
	          carOnBlockedCount += transOnBlockedStats[i].count;
	        }
	      }

	      if ( carOnUnblockedCount > 0 ) {
	        carOnUnblockedCount = 0;
	        for (int i = 0; i < transOnUnblockedStats.length; i++) {
	          transOnUnblockedStats[i].count /= 2;
	          carOnUnblockedCount += transOnUnblockedStats[i].count;
	        }
	      }
	    }

	    private TransitionStat[] extend(TransitionStat[] a, TransitionStat ts) {
	      if ( a == null ) return new TransitionStat[] {
	        ts };
	      TransitionStat[] b = new TransitionStat[a.length + 1];
	      for (int i = 0; i < a.length; i++)
	        b[i] = a[i];
	      b[a.length] = ts;
	      return b;
	    }
	  }

	  LaneRLCDExt(
	      Lane lane,
	      RLCDEView ev,
	      CarInfoIView carInfoView,
	      BlockIView blockView,
	      RLCDParams params)
	  {
	    this.lane = lane;
	    this.ev = ev;
	    this.params = params;

	    Logger.shutdown();
	    
	    n = lane.getLength();
	    this.id = lane.getOwner().getId() + ":" + lane.getAbsoluteNumber() + ":" + n;
	    V = new float[n];
	    /* for lanes going to gateway, we may assume that V[i] = 0.0 for all i */

	    laneCarInfoExt = carInfoView.ext( lane );

	    if ( lane.getOwner().getEnd().isIntersection() ) {
	      laneBlockExt = blockView.ext( lane );

	      Qblocked = new float[n];
	      Qunblocked = new float[n];

	      model = new Model(new CellStat[n]);
	      models.add(model);
	      for (int pos = 0; pos < n; pos++)
	    	  model.cells[pos] = new CellStat();
	      
	    }

	    if ( logger.isTraceEnabled() ) {
	      logger.trace( lane + ", lengt (n)=" + n );
	    }
	  }

	  private void countCar(int pos, Lane destLane, int destPos) {
	    if ( logger.isTraceEnabled() ) {
	      logger.trace( id + ": from=" + pos + ", to lane " + destLane + " newPos" + destPos );
	    }
	    try {
	      if ( laneBlockExt.isBlocked() ) {
	        model.cells[pos].countCarOnBlocked( destLane, destPos );
	      }
	      else {
	        model.cells[pos].countCarOnUnblocked( destLane, destPos );
	      }
	    }
	    catch (Exception e) {
	      // TODO: handle exception
	    }
	  }

	  void halveCounters() {
	    int n = lane.getLength();
	    for (int pos = 0; pos < n; pos++)
	      model.cells[pos].halveCounters();
	  }

	  void updateStatsToGateway() {
	    CarInfoCursor cursor = laneCarInfoExt.carInfoForwardCursor();
	    if ( cursor.isValid() ) {
	      Lane l = cursor.beforeLane();
	      if ( l != null && l != lane ) {
	        ev.ext( l ).countCar( cursor.beforePos(), lane, cursor.currentPos() );
	      }
	    }
	  }

	  void updateStatsToIsect() {
//	    CarInfoCursor cursor = laneCarInfoExt.carInfoForwardCursor();
	    CarInfoCursor cursor = laneCarInfoExt.carInfoBackwardCursor();
	    while ( cursor.isValid() ) {
	      Lane l = cursor.beforeLane();
	      // optimalization of the frequent case: don't use module view when the
	      // car hasn't changed the lane
	      int pos = cursor.currentPos();
	      if ( (n - pos) > ZONELENGHT ) {
	        break;
	      }
	      if ( l == lane ) {
	        countCar( cursor.beforePos(), lane, pos );
	      }
	      else if ( l != null ) {
	        ev.ext( l ).countCar( cursor.beforePos(), lane, pos );
	      }
	      cursor.next();
	    }
	  }

	  void updateValues1() {
		  float error = 0;
	    for (int pos = 0; pos < n; pos++) {
	      CellStat c = model.cells[pos];

	      if ( c.carOnBlockedCount == 0 )
	        Qblocked[pos] = 0.0f;
	      else {
	        float q = 0.0f;
	        for (int i = 0; i < c.transOnBlockedStats.length; i++) {
	          TransitionStat ts = c.transOnBlockedStats[i];
	          float p = (float) ts.count / (float) c.carOnBlockedCount;
	          if(ts.t == 0){
	        	  //ts.t = (float) ts.count / (float) c.carOnBlockedCount;
	        	  //ts.t = (float) ts.count / (float) c.transOnBlockedStats.length;
	        	  ts.t = p;
	          }
	          //float p = ts.t;
	          if(logger.isTraceEnabled())	
	          	logger.trace("percentage: " + p + "ts.t: " + ts.t);
	          float v;
	          // optimalization of the frequent case: don't use module view when the
	          // car hasn't changed the lane
	          v = 1.0f;
	          if ( ts.destLane == lane ) {
	            v = params.discount * V[ts.destPos];
	            if ( ts.destPos == pos ) v += 1.0f;
	          }
	          else {
	            try {
	              v = params.discount * ev.ext( ts.destLane ).V[ts.destPos];
	            }
	            catch (Exception e) {
	              ;//TODO
	            }
	          }
	          // TODO: lepsza metoda karania
	          //logger.trace("mytrace p: " + p + "v: " + v + "ts.r: " + ts.r);
	          //v += ts.r;
	          int krDelta = 0;
	          int rr = 0;
	          if(ts.destLane == lane && ts.destPos == pos){
	        	  krDelta = 1;
	        	  rr = 1;
		       //   logger.trace("mytrace p: " + p + "v: " + v + "ts.r: " + ts.r);
	          }
	          ts.updateDeltaT(c.carOnBlockedCount, krDelta);
	          ts.updateDeltaR(c.carOnBlockedCount, rr);
	          ts.updateT();
	          ts.updateR();
	          
	          float cm = (float)((float)c.carOnBlockedCount/(float)M)*((float)c.carOnBlockedCount/(float)M);
	          error += cm*(omega*ts.deltaR*ts.deltaR + (1-omega)*ts.deltaT);
	          
	          q += p * v;
	        }
	        Qblocked[pos] = q;
	      }

	      if ( c.carOnUnblockedCount == 0 )
	        Qunblocked[pos] = 0.0f;
	      else {
	        float q = 0.0f;
	        for (int i = 0; i < c.transOnUnblockedStats.length; i++) {
	          TransitionStat ts = c.transOnUnblockedStats[i];
	          float p = (float) ts.count / (float) c.carOnUnblockedCount;
	          if(ts.t == 0){
	        	  //ts.t = (float) ts.count / (float) c.carOnUnblockedCount;
	        	  //ts.t = (float) ts.count / (float) c.transOnUnblockedStats.length;
	        	  ts.t = p;
	          }
	          //float p = ts.t;
	          float v;
	          // optimalization of the frequent case: don't use module view when the
	          // car hasn't changed the lane
	          v = 1.0f;
	          if ( ts.destLane == lane ) {
	            v = params.discount * V[ts.destPos];
	            if ( ts.destPos == pos ) q += 1.0f;
	          }
	          else {
	            try {
	              v = params.discount * ev.ext( ts.destLane ).V[ts.destPos];
	            }
	            catch (Exception e) {
	              ;//TODO
	            }
	          }
	          // TODO: lepsza metoda karania
	          //v += ts.r;
	          int krDelta = 0;
	          int rr = 0;
	          if(ts.destLane == lane && ts.destPos == pos){
	        	  krDelta = 1;
	        	  rr = 1;
	          }
	          ts.updateDeltaT(c.carOnUnblockedCount, krDelta);
	          ts.updateDeltaR(c.carOnUnblockedCount, rr);
	          ts.updateT();
	          ts.updateR();
	          
	          float cm = (float)((float)c.carOnUnblockedCount/(float)M)*((float)c.carOnUnblockedCount/(float)M);
	          error += cm*(omega*ts.deltaR*ts.deltaR + (1.0-omega)*ts.deltaT);
	          
	          q += p * v;
	        }
	        Qunblocked[pos] = q;
	      }
	    }
	    for(Model m : models){
	    	m.error = m.error + p*(error - m.error);
	    }
	  }

	  void updateValues2() {
	    for (int pos = 0; pos < n; pos++) {
	      CellStat c = model.cells[pos];

	      int totalCount = c.carOnBlockedCount + c.carOnUnblockedCount;

	      if ( totalCount > 0 ) {
	        float pb = (float) c.carOnBlockedCount / (float) totalCount;
	        V[pos] = pb * Qblocked[pos] + (1 - pb) * Qunblocked[pos];
	      }
	      else V[pos] = 0.0f;
	    }
	  }
	  
	  void changeModel(){
		  if(model.error > lambda){
			  Model bestmodel = model;
			  for(Model m : models){
				  if(m.error < bestmodel.error){
					  bestmodel = m;
				  }
			  }
			  if(bestmodel.error < lambda){
				  model = bestmodel;
				  if(logger.isTraceEnabled())
				  	logger.trace("oldmodel");
			  }
			  else{
				  if(logger.isTraceEnabled())
				  	logger.trace("newmodel");
				  model = new Model(new CellStat[n]);
				  for(int i = 0 ;i < n ; ++i){
					  model.cells[i] = new CellStat();
				  }
				  models.add(model);
			  }
		  }
		  else{
			  for(Model m : models){
				  if(m.error < model.error){
					  model = m;
				  }
			  }
		  }
		  if(logger.isTraceEnabled())
			  logger.trace("models.size(): " + models.size() + "model.error: " + model.error);
	  }

	  void makeEvaluation() {
	    evaluation = 0.0f;

	    int queueEnd = lane.getLength();
	    if ( logger.isTraceEnabled() ) {
	      logger.trace( id + ", blocked=" + laneBlockExt.isBlocked() + ", queueEnd=" + queueEnd );
	    }
	    CarInfoCursor cursor = laneCarInfoExt.carInfoBackwardCursor();

	    while ( cursor.isValid() ) {
	      // TODO: currentPos?
	      //      int pos = cursor.currentPos();

	      int pos = cursor.currentPos();
//	      System.out.println( n + ", " + pos + " = " + (n - pos) );
	      if ( (n - pos) > ZONELENGHT ) {
	        break;
	      }

	      if ( logger.isTraceEnabled() ) {
	        logger.trace( "      position=" + pos );
	      }
	      if ( pos + 1 < queueEnd ) {
	        break;
	      }

	      queueEnd = pos;

	      //      try {
	      evaluation += Qblocked[pos] - Qunblocked[pos];
	      if ( logger.isTraceEnabled() ) {
	        logger.trace( "      queueEnd := position=" + pos );
	        logger.trace( "      evaluation=" + evaluation + ": Qb=" + Qblocked[pos] + " - Qu"
	                      + Qunblocked[pos] );
	      }
	      //        if ( evaluation < 0.0 ) {
	      //          System.out.println( lane + "  " + evaluation );
	      //        }
	      //      }
	      //      catch (Exception e) {
	      //        logger.warn( e.getMessage() );
	      //      }

	      cursor.next();
	    }

	    queueLength = lane.getLength() - queueEnd;
	    if ( logger.isTraceEnabled() ) {
	      logger.trace( "      queueLength=" + queueLength );
	    }
	  }

	  public float getEvaluation() {
	    if ( logger.isTraceEnabled() ) {
	      logger.trace( id + " queue=" + queueLength + ", evaluation=" + evaluation + ", blocked="
	                    + laneBlockExt.isBlocked() );
	    }
	    return evaluation;
	  }

	  public int getMinGreenDuration() {
	    int ret =  (int) ((queueLength - 1) * (float) params.carStartDelay + (queueLength / (float) params.carMaxVelocity));
	    return Math.max( ret, params.minimumGreen );
	  }
}
