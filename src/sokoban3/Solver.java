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
        private  LinkedList<State> generator;
        private  LinkedList<State> statesAll;
       // private State endState;
        private boolean flag;
        private LinkedList<State> path;
        private DeadLock deadLock;
        private long runCounter = 0;
        
        public Solver(State init){
            generator = new LinkedList<State>();
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
                
           if (cur.isEmptyPoint(cur.man.x+1, cur.man.y)){
                State c = cur.copyState();
                c.man.x = c.man.x+1;
                c.parentState = cur;
                if (!(generator.contains(c) || statesAll.contains(c)))                      
                            generator.add(c);                        
            }
            
            if (cur.isEmptyPoint(cur.man.x-1, cur.man.y)){
                State c = cur.copyState();
                c.man.x = c.man.x-1;
                c.parentState = cur;
                    if (!(generator.contains(c) || statesAll.contains(c)))                  
                            generator.add(c);
                        
            }
            
            if (cur.isEmptyPoint(cur.man.x, cur.man.y+1)){
                State c = cur.copyState();
                c.man.y = c.man.y+1;
                c.parentState = cur;
                   if (!(generator.contains(c) || statesAll.contains(c)))                    
                            generator.add(c);                        
            }
            
            if (cur.isEmptyPoint(cur.man.x, cur.man.y-1)){
                State c = cur.copyState();
                c.man.y = c.man.y-1;
                c.parentState = cur;
                    if (!(generator.contains(c) || statesAll.contains(c)))                  
                            generator.add(c);
                        
            }
                                    
            if (cur.boxes.contains(new Point(cur.man.x+1, cur.man.y))){
                if (cur.isEmptyPoint(cur.man.x+2, cur.man.y) && !deadLock.isDeadLock(cur.man.x+2, cur.man.y)){
                    State c = cur.copyState();
                    c.man.x = c.man.x+1;
                    c.boxes.removeFirstOccurrence(new Point(cur.man.x+1, cur.man.y));
                    c.boxes.add(new Point(cur.man.x+2, cur.man.y));   
                    c.parentState = cur;
                    if (!deadLock.isFrosenDeadLock(new Point(cur.man.x+2, cur.man.y), c)){
                        if (!(generator.contains(c) || statesAll.contains(c)))                       
                                generator.add(c);
                    }
                }                
            }
            
            if (cur.boxes.contains(new Point(cur.man.x-1, cur.man.y))){
                if (cur.isEmptyPoint(cur.man.x-2, cur.man.y) && !deadLock.isDeadLock(cur.man.x-2, cur.man.y)){
                    State c = cur.copyState();
                    c.man.x = c.man.x-1;
                    c.boxes.removeFirstOccurrence(new Point(cur.man.x-1, cur.man.y));
                    c.boxes.add(new Point(cur.man.x-2, cur.man.y));
                    c.parentState = cur;
                    if (!deadLock.isFrosenDeadLock(new Point(cur.man.x-2, cur.man.y), c)){
                       if (!(generator.contains(c) || statesAll.contains(c)))
                            generator.add(c);                    
                    }                
                }
            }
            
            if (cur.boxes.contains(new Point(cur.man.x, cur.man.y+1))){
                if (cur.isEmptyPoint(cur.man.x, cur.man.y+2) && !deadLock.isDeadLock(cur.man.x, cur.man.y+2)){
                    State c = cur.copyState();
                    c.man.y = c.man.y+1;
                    c.boxes.removeFirstOccurrence(new Point(cur.man.x, cur.man.y+1));
                    c.boxes.add(new Point(cur.man.x, cur.man.y+2));
                    c.parentState = cur;
                    if (!deadLock.isFrosenDeadLock(new Point(cur.man.x, cur.man.y+2), c)){
                        if (!(generator.contains(c) || statesAll.contains(c)))
                            generator.add(c);
                    }
                    
                }                
            }
            
            if (cur.boxes.contains(new Point(cur.man.x, cur.man.y-1))){
                if (cur.isEmptyPoint(cur.man.x, cur.man.y-2) && !deadLock.isDeadLock(cur.man.x, cur.man.y-2)){
                    State c = cur.copyState();
                    c.man.y = c.man.y-1;
                    c.boxes.removeFirstOccurrence(new Point(cur.man.x, cur.man.y-1));
                    c.boxes.add(new Point(cur.man.x, cur.man.y-2));
                    c.parentState = cur;
                    if (!deadLock.isFrosenDeadLock(new Point(cur.man.x, cur.man.y-2), c)){
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
                    State par = cur.parentState;
                        if (par != null){
                        if ((par.man.x-cur.man.x) == 1) res.append('u');
                        if ((par.man.x-cur.man.x) == -1) res.append('d');
                        if ((par.man.y-cur.man.y) == 1) res.append('l');
                        if ((par.man.y-cur.man.y) == -1) res.append('r');
                    }
                    cur = cur.parentState;
                }
                
                res = res.reverse();
                Collections.reverse(path);
                
                                
                path.get(0).print();
                for (int i = 1; i<path.size(); i++){
                    path.get(i).print();
                    System.out.println(res.charAt(i-1));                                                            
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