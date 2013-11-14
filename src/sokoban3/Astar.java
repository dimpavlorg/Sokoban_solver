package sokoban3;
import java.util.*;


public class Astar {
	private static class StateComp implements Comparable<StateComp>, Comparator<StateComp>{
		public StateComp(State o, Point res){
			stat = o.copyState();
			rasst = (int)Math.abs(res.x - stat.man.x) + (int)Math.abs(res.y-stat.man.y); 
		}
		private int rasst;
		public State stat;
		public StateComp parentState;
		
		public int compareTo(StateComp cur){											
					if (cur.rasst > this.rasst) return -1;
					else if (cur.rasst == this.rasst) return 0;
						 else return 1;
								
		}
		public int compare(StateComp o1, StateComp o2){
			if (o1.rasst > o2.rasst) return 1;
			else if (o1.rasst == o2.rasst) return 0;
			else return -1;
			
		}
		
		public boolean equals(Object o){
			if (o instanceof StateComp){				
				StateComp c = (StateComp)o;
				if (c.stat.man.equals(this.stat.man)) return true;
				else return false;
			}
			else return false;
			
		}
		
		public boolean isEnd(Point res){
			return this.stat.man.equals(res);
		}
	}

    
    
    public static String pathTo(State start, Point res){
    	StringBuffer path = new StringBuffer();
    	PriorityQueue<StateComp> generator = new PriorityQueue<StateComp>();
        LinkedList<StateComp> statesAll = new LinkedList<StateComp>();    	
    	StateComp sd = new StateComp(start, res);
    	generator.add(sd);    	    	    
    	StateComp cur = null; 
    	boolean flag;
    	if (start.man.equals(res)) return "on";
        do{                         
           cur = generator.poll();
           flag = false;
           if (cur!=null){                 
            if (cur.stat.isEmptyPoint(cur.stat.man.x+1, cur.stat.man.y)){
                 State c = cur.stat.copyState();
                 c.man.x = c.man.x + 1;
                 StateComp s = new StateComp(c, res);
                 s.parentState = cur;
                 if (!(generator.contains(s) || statesAll.contains(s)))                      
                             generator.add(s);                        
             }
             
             if (cur.stat.isEmptyPoint(cur.stat.man.x-1, cur.stat.man.y)){
                 State c = cur.stat.copyState();
                 c.man.x = c.man.x - 1;
                 StateComp s = new StateComp(c, res);
                 s.parentState = cur;
                     if (!(generator.contains(s) || statesAll.contains(s)))                  
                             generator.add(s);                        
             }
             
             if (cur.stat.isEmptyPoint(cur.stat.man.x, cur.stat.man.y+1)){
                 State c = cur.stat.copyState();
                 c.man.y = c.man.y + 1;
                 StateComp s = new StateComp(c, res);
                 s.parentState = cur;
                    if (!(generator.contains(s) || statesAll.contains(s)))                    
                             generator.add(s);                        
             }
             
             if (cur.stat.isEmptyPoint(cur.stat.man.x, cur.stat.man.y-1)){
                 State c = cur.stat.copyState();
                 c.man.y = c.man.y-1;
                 StateComp s = new StateComp(c, res);
                 s.parentState = cur;
                     if (!(generator.contains(s) || statesAll.contains(s)))                  
                             generator.add(s);
                         
             }                                                 
             
             if (!statesAll.contains(cur)) statesAll.add(cur);
                 for (StateComp it: statesAll){
                     flag = flag | it.isEnd(res);
                 }             
             }           
        }while(!(generator.isEmpty() || flag));
    	
    	if (flag){
    		cur = null;    		
            for (StateComp it: statesAll){
                if (it.isEnd(res)){
                    cur = it;
                    break;
                }                    
            }
            
            while (cur != null){                
                StateComp par = cur.parentState;
                    if (par != null){
                    if ((par.stat.man.x-cur.stat.man.x) == 1) path.append('u');
                    if ((par.stat.man.x-cur.stat.man.x) == -1) path.append('d');
                    if ((par.stat.man.y-cur.stat.man.y) == 1) path.append('l');
                    if ((par.stat.man.y-cur.stat.man.y) == -1) path.append('r');
                }
                cur = cur.parentState;
            }           
            path = path.reverse();                                  
        }
    	
    	return path.toString();
    }
}