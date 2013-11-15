/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban3;
import java.util.*;
/**
 *
 * @author Р”РёРјР°
 */
public class DeadLock {    
    private LinkedList<Point> simpleDeadLock;
    private State init;
   // private LinkedList<BoxState> boxesAsWall = new LinkedList<BoxState>();
    private LinkedList<BoxState> blockedBoxes = new LinkedList<BoxState>();
    private LinkedList<BoxState> openlist = new LinkedList<BoxState>();
    public DeadLock(State init){
        this.init = init.copyState();
        simpleDeadLock = new LinkedList<Point>();
        this.init.boxes.clear();        
        calculateSimpleDeadLock();
    }
    
    private class BoxState{        
        public Point pBox;
        public boolean vertBlock = false;
        public boolean horizBlock = false;
        
        public BoxState(Point p){
            this.pBox = p.copy();
        }
        
        @Override
        public boolean equals(Object o){
            if (o instanceof BoxState){
                BoxState cur = (BoxState)o;
                return cur.pBox.equals(this.pBox);
            }else return false;
        }
    }
    
    
    private void calculateSimpleDeadLock(){       
        int maxX = init.getSizes().x;
        int maxY = init.getSizes().y;
        
        LinkedList<State> generator = new LinkedList<State>();
        LinkedList<State> statesAll = new LinkedList<State>();
        for (int i = 1; i<=maxX; i++){
            for(int j = 1; j<=maxY; j++){
              Point p = new Point(i,j);
              
              if (init.isEmptyPoint(p)){
                if (!simpleDeadLock.contains(p)){                                        
                     
                    if (p.equals(init.man)){
                        State c = init.copyState();
                        int ii = 1; int jj= 1;
                        for (ii=1; ii<=maxX; ii++)
                        	for (jj=1; jj<=maxY; jj++)
                        		if (init.isEmptyPoint(new Point(ii,jj)) && !(new Point(ii,jj).equals(init.man)) && !Astar.pathTo(init, new Point(ii,jj)).equals(""))
                        				c.man = new Point(ii,jj);
                        c.boxes.add(p);
                        generator.add(c);
                    }
                    else{
                    		init.boxes.add(p);
                        	generator.add(init.copyState());
                        }                
                    boolean flag = false;
                    State cur = null;            
                    do{                                       
                        cur = generator.poll();
                        flag = false;
                        
                        if (cur!=null){
                      	  
                          for (Point pBox: cur.boxes){            	
                          	if (cur.isEmptyPoint(pBox.x-1, pBox.y)){            			
                          			String pat = Astar.pathTo(cur, new Point(pBox.x+1, pBox.y));
                          			if (!pat.equals("")){
                          				State c = cur.copyState();
                          				if (pat.equals("on")) pat =""; 
                          				c.path = pat+"U";
                          				c.man = new Point(pBox.x, pBox.y);
                          				c.boxes.removeFirstOccurrence(new Point(pBox.x, pBox.y));
                          				c.boxes.add(new Point(pBox.x-1, pBox.y));
                          				c.parentState = cur;                  
                          				if (!(generator.contains(c) || statesAll.contains(c)))                       
                          					generator.add(c);
                          			}            				            			            			
                          		} 
                          		
                          		if (cur.isEmptyPoint(pBox.x+1, pBox.y)){
                          				String pat = Astar.pathTo(cur, new Point(pBox.x-1, pBox.y));
                          				if (!pat.equals("")){            					
                          					State c = cur.copyState();
                          					if (pat.equals("on")) pat =""; 
                          					c.path = pat + "D";
                          					c.man = new Point(pBox.x, pBox.y);
                          					c.boxes.removeFirstOccurrence(new Point(pBox.x, pBox.y));
                              				c.boxes.add(new Point(pBox.x+1, pBox.y));
                              				c.parentState = cur;                              		
                              				if (!(generator.contains(c) || statesAll.contains(c)))                       
                              					generator.add(c);
                          				}
                          		}
                          		
                          		if (cur.isEmptyPoint(pBox.x, pBox.y+1)){
                          				String pat = Astar.pathTo(cur, new Point(pBox.x, pBox.y-1));
                          				if (!pat.equals("")){            					
                          					State c = cur.copyState();
                          					if (pat.equals("on")) pat =""; 
                          					c.path = pat + "R";
                          					c.man = new Point(pBox.x, pBox.y);
                          					c.boxes.removeFirstOccurrence(new Point(pBox.x, pBox.y));
                              				c.boxes.add(new Point(pBox.x, pBox.y+1));
                              				c.parentState =cur;                              				
                              				if (!(generator.contains(c) || statesAll.contains(c)))                       
                              					generator.add(c);
                          				}
                          		}
                          		
                          		if (cur.isEmptyPoint(pBox.x, pBox.y-1)){
                          				String pat = Astar.pathTo(cur, new Point(pBox.x, pBox.y+1));
                          				if (!pat.equals("")){            					
                          					State c = cur.copyState();
                          					if (pat.equals("on")) pat =""; 
                          					c.path = pat + "L";
                          					c.man = new Point(pBox.x, pBox.y);
                          					c.boxes.removeFirstOccurrence(new Point(pBox.x, pBox.y));
                              				c.boxes.add(new Point(pBox.x, pBox.y-1));
                              				c.parentState = cur;                              				
                              				if (!(generator.contains(c) || statesAll.contains(c)))                       
                              					generator.add(c);
                          				}
                          		}                          	
                          }        	          	
                          if (!statesAll.contains(cur)) statesAll.add(cur);
                          for (State it: statesAll){
                               flag = flag | it.isBoxOnGoal(it.boxes.get(0));
                          }             
                        }           
                      }while(!(generator.isEmpty() || flag));
                    
                    if (!flag){
                        if (!(simpleDeadLock.contains(p))) simpleDeadLock.add(p);                        
                    }
                    statesAll.clear();
                    generator.clear();
                    init.boxes.clear();
                }
              }
            }
        }
    }
    
    public boolean isDeadLock(Point pBox){        
        if (simpleDeadLock.contains(pBox)) return true;        
        return false;
    }
    
    public boolean isFrosenDeadLock(Point pBox, State cur){
       // boxesAsWall.clear();
        blockedBoxes.clear();
        openlist.clear();
        if(isBlockedBox(pBox, cur)) {
           //System.out.println("Моя хорошо сосать");
            boolean flag = false;          
            for (BoxState it : blockedBoxes){
                {
                	//System.out.println("Моя хорошо сосать");
                    flag = flag | !init.goals.contains(it.pBox);
                    //System.out.println(flag);
                }
            }                       
            return flag;
        }else 
            return false;            
    }
    
    private boolean isBlockedBox(Point pBox, State cur){
        BoxState p = new BoxState(pBox); 
        
        if (init.obstacles.contains(new Point(pBox.x, pBox.y-1))
                || init.obstacles.contains(new Point(pBox.x, pBox.y+1)) )                               
            p.horizBlock = true;  
       
        
        
       
        if (init.obstacles.contains(new Point(pBox.x-1, pBox.y)) 
                || init.obstacles.contains(new Point(pBox.x+1, pBox.y)))              
            p.vertBlock = true;                                   
       
                 
        if (isDeadLock(new Point(pBox.x, pBox.y-1)) && isDeadLock(new Point(pBox.x, pBox.y+1)))
            p.horizBlock = true;
       
                
        if (isDeadLock(new Point(pBox.x-1, pBox.y)) && isDeadLock(new Point(pBox.x+1, pBox.y)))
            p.vertBlock = true;
        
        if (((openlist.contains(new BoxState(new Point(pBox.x+1, pBox.y)))) && (openlist.contains(new BoxState(new Point(pBox.x, pBox.y+1))))) 
         || ((openlist.contains(new BoxState(new Point(pBox.x+1, pBox.y)))) && (openlist.contains(new BoxState(new Point(pBox.x, pBox.y-1))))) 
         || ((openlist.contains(new BoxState(new Point(pBox.x-1, pBox.y)))) && (openlist.contains(new BoxState(new Point(pBox.x, pBox.y+1)))))
         || ((openlist.contains(new BoxState(new Point(pBox.x-1, pBox.y)))) && (openlist.contains(new BoxState(new Point(pBox.x, pBox.y-1)))))){        
            blockedBoxes.add(p);
            p.horizBlock = true;
            p.vertBlock = true;          
        }        
        else {                        
        	
        	//System.out.println("Не куб");
        	
        	if (cur.boxes.contains(new Point(pBox.x, pBox.y-1))){
        		//System.out.println("идим коробку слева");
        		if (!blockedBoxes.contains(p)){
        			if (!openlist.contains(p)) openlist.add(p);            
        			if (!openlist.contains(new BoxState(new Point(pBox.x, pBox.y-1)))){             
        				if (!blockedBoxes.contains(new BoxState(new Point(pBox.x, pBox.y-1)))){
        				
        					p.horizBlock = isBlockedBox(new Point(pBox.x, pBox.y-1),cur);
        				}            
        				else{                     
        					if (blockedBoxes.contains(new BoxState(new Point(pBox.x+1, pBox.y))) 
                        		|| blockedBoxes.contains(new BoxState(new Point(pBox.x-1, pBox.y)))){                        		
                        			blockedBoxes.add(p);
                        			p.horizBlock = true;
                        			p.vertBlock = true;
                        		}
        					else{
        						p.horizBlock = true;
        					}                           
        				}                   
        			}
        			else
                	{
                		p.horizBlock = true;
                		
                	}
        		}           
        	}
        
        
        
         
        
        	if (cur.boxes.contains(new Point(pBox.x, pBox.y+1))){
        		//System.out.println("Видим коробку справа");
        		if (!blockedBoxes.contains(p)){
        			if (!openlist.contains(p)) openlist.add(p);
        			if (!openlist.contains(new BoxState(new Point(pBox.x, pBox.y+1)))){               
        				if (!blockedBoxes.contains(new BoxState(new Point(pBox.x, pBox.y+1)))){
        				
        					p.horizBlock = isBlockedBox(new Point(pBox.x, pBox.y+1),cur);
        				}            
        				else{                 
        					if (blockedBoxes.contains(new BoxState(new Point(pBox.x+1, pBox.y)))
        							|| blockedBoxes.contains(new BoxState(new Point(pBox.x-1, pBox.y)))){
        						blockedBoxes.add(p);
        						p.horizBlock = true;
        						p.vertBlock = true;
        					}
        					else{
        						p.horizBlock = true;
        					}            
        				}
        				
        			}
        			else
                	{
                		p.horizBlock = true;
                		
                	}
        		}            
        	}
     
        
                
        	if (cur.boxes.contains(new Point(pBox.x+1, pBox.y)))  {      		
        		//System.out.println("Видим коробку снизу");
        		if (!blockedBoxes.contains(p)){
        			//System.out.println("Не блокирована");
        			if (!openlist.contains(p)) openlist.add(p);
        			if (!openlist.contains(new BoxState(new Point(pBox.x+1, pBox.y)))){               
        				if (!blockedBoxes.contains(new BoxState(new Point(pBox.x+1, pBox.y)))){  
        					//System.out.println("Проверяем куб снизу");
        					p.vertBlock = isBlockedBox(new Point(pBox.x+1, pBox.y),cur);
        				}            
        				else{
             
        					if (blockedBoxes.contains(new BoxState(new Point(pBox.x, pBox.y+1)))
        							|| blockedBoxes.contains(new BoxState(new Point(pBox.x, pBox.y-1)))){
        						blockedBoxes.add(p);
        						p.horizBlock = true;
        						p.vertBlock = true;
        					}
        					else{
        						p.vertBlock = true;
        					}            
        				}            
        			} 
        			else
                	{
                		p.vertBlock = true;
                		
                	}
        		}            
        	}

        
         
                 
        	if (cur.boxes.contains(new Point(pBox.x-1, pBox.y))){ 
        		//System.out.println("Видим коробку сверху");
        		if (!blockedBoxes.contains(p)){
        	
                if (!openlist.contains(p)) openlist.add(p);
                	if (!openlist.contains(new BoxState(new Point(pBox.x-1, pBox.y)))){                
                		if (!blockedBoxes.contains(new BoxState(new Point(pBox.x-1, pBox.y)))){   
                      			p.vertBlock = isBlockedBox(new Point(pBox.x-1, pBox.y),cur);
                		}            
                		else{                 
                			if (blockedBoxes.contains(new BoxState(new Point(pBox.x, pBox.y+1)))
                					|| blockedBoxes.contains(new BoxState(new Point(pBox.x, pBox.y-1)))){                  
                				blockedBoxes.add(p);
                				p.horizBlock = true;
                				p.vertBlock = true;
                			}
                			else{
                				p.vertBlock = true;
                			}            
                		}                    
                	}
                	else
                	{
                		//System.out.println("Коробка сверху в открытомлисте так что блокируем коробки");
                		p.vertBlock = true;
                		
                	}
        	}   
        	}
        
        
        if (openlist.contains(new BoxState(new Point(pBox.x, pBox.y)))){
        	if (blockedBoxes.contains(new BoxState(new Point(pBox.x, pBox.y+1))) 
        			|| blockedBoxes.contains(new BoxState(new Point(pBox.x, pBox.y-1))) 
        			|| init.obstacles.contains(new Point(pBox.x, pBox.y-1)) 
        			|| init.obstacles.contains(new Point(pBox.x, pBox.y+1))){
        		if (openlist.contains(new BoxState(new Point(pBox.x+1, pBox.y))) 
        				|| openlist.contains(new BoxState(new Point(pBox.x-1, pBox.y)))){
        			blockedBoxes.add(p);
        			p.horizBlock = true;
        			p.vertBlock = true;              
        		}
        		else{
        			p.horizBlock = true;                                     
        		}
        	}
        }
        if (openlist.contains(new BoxState(new Point(pBox.x, pBox.y)))){
        	if (blockedBoxes.contains(new BoxState(new Point(pBox.x+1, pBox.y))) 
        			|| blockedBoxes.contains(new BoxState(new Point(pBox.x-1, pBox.y)))
        			|| init.obstacles.contains(new Point(pBox.x-1, pBox.y)) 
        			|| init.obstacles.contains(new Point(pBox.x+1, pBox.y))){
        		if (openlist.contains(new BoxState(new Point(pBox.x, pBox.y+1))) 
        				|| openlist.contains(new BoxState(new Point(pBox.x, pBox.y-1)))){
        			blockedBoxes.add(p);
        			p.horizBlock = true;
        			p.vertBlock = true;              
        		}
        		else{                  
        			p.vertBlock = true;              
        		}
        	}
        }
        }
     //   System.out.println(p.horizBlock +" "+ p.vertBlock);
        return p.horizBlock & p.vertBlock;       
    }
   
 

        
    
    public boolean isDeadLock(int x, int y){
        return isDeadLock(new Point(x, y));
    }
    
    @Override
    public String toString(){
        StringBuffer s = new StringBuffer();
        s.append('\n');
        int maxX = init.getSizes().x;
        int maxY = init.getSizes().y;
        for (int i = 1; i<=maxX; i++){
            for (int j = 1; j<=maxY; j++){
                Point p = new Point(i,j);
                if (init.goals.contains(p)){
                    if (init.boxes.contains(p)){
                        s.append('$');
                    }
                    else s.append('.');
                    continue;
                }
                else if (init.isEmptyPoint(p) || init.man.equals(p)) {
                    if (this.simpleDeadLock.contains(p)) s.append('d');
                    else s.append(' ');
                    continue;
                }
                if (init.obstacles.contains(p)){
                    s.append('#');
                    continue;
                }
                
                if (init.boxes.contains(p)){
                    s.append('$');
                    continue;
                }
                
            }
         s.append('\n');
        }
        return s.toString();
    }        
}
