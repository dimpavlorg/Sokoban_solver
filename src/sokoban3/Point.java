/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban3;

/**
 *
 * @author Дима
 */
public class Point{
    public Point(){ 
        x = y = 0;
    }    
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }
    //lkoko
    public int x, y;
       
    public Point copy(){
        return new Point(this.x, this.y);
    }
        
 
    
    @Override
    public boolean equals(Object o){
        if ((o instanceof Point)){
            Point p = (Point)o;
            if (p != null)
                return this.x == p.x && this.y == p.y;
            else
                return false;
        }                
        else
            return false;
    }
    
    @Override
    public String toString(){
        return "("+this.x+","+this.y+")";
    }
}
