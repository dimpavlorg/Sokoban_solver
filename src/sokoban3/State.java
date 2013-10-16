/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban3;
import java.io.*;
import java.util.*;
/**
 *
 * @author Дима
 */
public class State implements Comparable{
    public Point man;
    public LinkedList<Point> boxes;
    public static ArrayList<Point> goals;
    public static ArrayList<Point> obstacles;
    private static int maxX= 0, maxY = 0;
    public State parentState;
    
    
    public State(){
        this.boxes = new LinkedList<Point>();
        this.man = new Point();
        parentState = null;
    }
    
    public boolean isEmptyPoint(int x, int y){
        return isEmptyPoint(new Point(x, y));
    }
    
    public boolean isEmptyPoint(Point cur){
       if (cur.x<1 || cur.x > State.maxX || cur.y < 1 || cur.y > State.maxY) return false; 
       if (this.man.equals(cur)) return false;
       else return !(this.boxes.contains(cur) || State.obstacles.contains(cur));          
    }
    
    public State copyState(){
        State copy = new State();
        
        for (Point iter: this.boxes){
            Point p = new Point(iter.x, iter.y);
            copy.boxes.add(p);
        }    
       
        copy.man = new Point(this.man.x, this.man.y);
        return copy;
    }
    
    
    
   public static boolean isEnd(State cur){  
       return cur.boxes.containsAll(State.goals);
   }
    
    public static State getInitialState() throws IOException{
            State init = new State();
            State.goals = new ArrayList<Point>();
            State.obstacles = new ArrayList<Point>();
            File f = new File("src"+File.separator+"input"+File.separator+"map");                       
            BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()));
            String read;
            int i = 0; 
            while ((read = br.readLine()) != null){
                if (State.maxY<read.length()) State.maxY = read.length();
                for (int j = 0; j<read.length(); j++){
                    switch(read.charAt(j)){
                        case '$':
                            init.boxes.add(new Point(i+1, j+1));
                            break;
                        case '@':
                            init.man = new Point(i+1,j+1);
                            break;
                        case '.': 
                            State.goals.add(new Point(i+1,j+1));                           
                            break;
                        case '#':
                            State.obstacles.add(new Point(i+1,j+1));
                            break;
                        case '*':
                            init.boxes.add(new Point(i+1, j+1));
                            State.goals.add(new Point(i+1,j+1));
                            break;
                        case '+':
                            init.man = new Point(i+1,j+1);
                            State.goals.add(new Point(i+1,j+1));
                            break;
                    }
                }
                i++;
            }  
            State.maxX = i;
            br.close();                          
        return init;
    }
        
    
    public static State getEndState(State init){
        State end = new State();
        end.man = init.man.copy();
        for (Point it: State.goals){
            end.boxes.add(it.copy());            
        }
        return end;
    }
    
    public void print(){
       System.out.print(this);
    }
    
    
    @Override
    public String toString(){
        StringBuffer s = new StringBuffer();
        s.append('\n');
        for (int i = 1; i<=State.maxX; i++){
            for (int j = 1; j<=State.maxY; j++){
                Point p = new Point(i,j);
                if (State.goals.contains(p)){
                    if (this.boxes.contains(p)){
                        s.append('*');
                    }
                    else{ 
                        if (this.man.equals(p)) s.append('+');
                        else s.append('.');
                    }
                    continue;
                }
                if (this.isEmptyPoint(p)) {
                    s.append(' ');
                    continue;
                }
                if (State.obstacles.contains(p)){
                    s.append('#');
                    continue;
                }
                
                if (this.boxes.contains(p)){
                    s.append('$');
                    continue;
                }
                if (this.man.equals(p)) {                    
                        s.append('@');
                    continue;
                }
            }
         s.append('\n');         
        }
        return s.toString();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof State){
            State cur = (State)o;
            if (cur != null){
                return this.man.equals(cur.man) && this.boxes.containsAll(cur.boxes);
            }else return false;
        }
        else return false;
    }
    
    public Point getSizes(){
        return new Point(maxX, maxY);
    }
    
    public boolean isBoxOnGoal(Point p){
        return goals.contains(p);
    }
    
        @Override
    public int compareTo(Object o){
        if (this.equals(o)) return 0;
        if (o != null){
            if (o instanceof State){
                State cur = (State)o;
                int thisbox = 0;
                int curbox = 0;
                for (Point it: this.boxes){
                    if (State.goals.contains(it)) thisbox++;
                }
                for (Point it: cur.boxes){
                    if (State.goals.contains(it)) curbox++;                   
                }
                
                if (thisbox < curbox) return -1;
                else return 1;
            }
            else return 1;
        }
        else
        return 1;
        
    }
}
