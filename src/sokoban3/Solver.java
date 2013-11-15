/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban3;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;

/**
 *
 * @author Дима
 */
public class Solver {
        private  PriorityQueue<State> generator;
        private  LinkedList<State> statesAll;
       // private State endState;
        private boolean flag;
        private LinkedList<State> path;
        private DeadLock deadLock;
        private long runCounter = 0;
        
        public Solver(State init){
            generator = new PriorityQueue<State>();
            statesAll = new LinkedList<State>();
            generator.add(init);            
           // endState = State.getEndState(init);
            path = new LinkedList<State>();
            flag = false;
            
            deadLock = new DeadLock(init);
        } 
        
        
        public void solve(){        
        	State cur = null;            
        do{              
          infoFunction();  
          cur = generator.poll();
          flag = false;
          
          if (cur!=null){
        	  
            for (Point pBox: cur.boxes){            	
            	if (cur.isEmptyPoint(pBox.x-1, pBox.y) && !deadLock.isDeadLock(pBox.x-1, pBox.y))
            		if (cur.isEmptyPoint(pBox.x+1, pBox.y)){            			
            			String pat = Astar.pathTo(cur, new Point(pBox.x+1, pBox.y));
            			if (!pat.equals("")){
            				State c = cur.copyState();
            				if (pat.equals("on")) pat =""; 
            				c.path = pat+"U";
            				c.man = new Point(pBox.x, pBox.y);
            				c.boxes.removeFirstOccurrence(new Point(pBox.x, pBox.y));
            				c.boxes.add(new Point(pBox.x-1, pBox.y));
            				c.parentState = cur;
            				//if(!deadLock.isFrosenDeadLock(new Point(pBox.x-1, pBox.y), c))
            						if (!(generator.contains(c) || statesAll.contains(c)))                       
            							generator.add(c);
            			}            				            			            			
            		} 
            		
            		if (cur.isEmptyPoint(pBox.x+1, pBox.y) && !deadLock.isDeadLock(pBox.x+1, pBox.y))
            			if (cur.isEmptyPoint(pBox.x-1, pBox.y)){
            				String pat = Astar.pathTo(cur, new Point(pBox.x-1, pBox.y));
            				if (!pat.equals("")){            					
            					State c = cur.copyState();
            					if (pat.equals("on")) pat =""; 
            					c.path = pat + "D";
            					c.man = new Point(pBox.x, pBox.y);
            					c.boxes.removeFirstOccurrence(new Point(pBox.x, pBox.y));
                				c.boxes.add(new Point(pBox.x+1, pBox.y));
                				c.parentState = cur;
                				//if(!deadLock.isFrosenDeadLock(new Point(pBox.x+1, pBox.y), c))
                					if (!(generator.contains(c) || statesAll.contains(c)))                       
                						generator.add(c);
            				}
            		}
            		
            		if (cur.isEmptyPoint(pBox.x, pBox.y+1) && !deadLock.isDeadLock(pBox.x, pBox.y+1))
            			if (cur.isEmptyPoint(pBox.x, pBox.y-1)){
            				String pat = Astar.pathTo(cur, new Point(pBox.x, pBox.y-1));
            				if (!pat.equals("")){            					
            					State c = cur.copyState();
            					if (pat.equals("on")) pat =""; 
            					c.path = pat + "R";
            					c.man = new Point(pBox.x, pBox.y);
            					c.boxes.removeFirstOccurrence(new Point(pBox.x, pBox.y));
                				c.boxes.add(new Point(pBox.x, pBox.y+1));
                				c.parentState =cur;
                				//if(!deadLock.isFrosenDeadLock(new Point(pBox.x, pBox.y+1), c))
                					if (!(generator.contains(c) || statesAll.contains(c)))                       
                						generator.add(c);
            				}
            		}
            		
            		if (cur.isEmptyPoint(pBox.x, pBox.y-1) && !deadLock.isDeadLock(pBox.x, pBox.y-1))
            			if (cur.isEmptyPoint(pBox.x, pBox.y+1)){
            				String pat = Astar.pathTo(cur, new Point(pBox.x, pBox.y+1));
            				if (!pat.equals("")){            					
            					State c = cur.copyState();
            					if (pat.equals("on")) pat =""; 
            					c.path = pat + "L";
            					c.man = new Point(pBox.x, pBox.y);
            					c.boxes.removeFirstOccurrence(new Point(pBox.x, pBox.y));
                				c.boxes.add(new Point(pBox.x, pBox.y-1));
                				c.parentState = cur;
                				//if(!deadLock.isFrosenDeadLock(new Point(pBox.x, pBox.y-1), c))
                					if (!(generator.contains(c) || statesAll.contains(c)))                       
                						generator.add(c);
            				}
            		}
            	
            }        	          	
            if (!statesAll.contains(cur)) statesAll.add(cur);
            for (State it: statesAll){
                 flag = flag | it.isEnd(it);
            }             
          }           
        }while(!(generator.isEmpty() || flag));
        generator.clear();
    }
        
    public String generatePath(){
            StringBuffer res = new StringBuffer();
            if (flag){   
                State cur = null;
                for (State it: statesAll){
                    if (it.isEnd(it)){
                        cur = it;
                        break;
                    }                    
                }                
                while (cur != null){
                    path.add(cur);                                            
                    if (cur.path != null)                    
                    	res.insert(0, cur.path);                    
                    cur = cur.parentState;
                }
                               
                Collections.reverse(path);
                
                                
               // path.get(0).print();
                for (int i = 0; i<path.size(); i++){
                    path.get(i).print();
                    System.out.println(path.get(i).path);                                                            
                }
                
                return res.toString();
            }else return "path does not exist";
        }
        
       public void infoFunction()
    {
        runCounter += 1;
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        MemoryUsage memuse;
        if (runCounter % 1000 == 0)
        {
            Runtime.getRuntime().gc();
            memuse = mem.getHeapMemoryUsage();
            System.out.format("Runs so far: %d \tFringe size (StatesAll list): %d \tMemory used so far: %d MB \tMemory available: %d MB%n", runCounter, this.statesAll.size(), memuse.getUsed() / (1024 * 1024), memuse.getMax() / (1024 * 1024));
        }

    }
}