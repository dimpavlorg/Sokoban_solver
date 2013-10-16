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
public class Sokoban3 {

    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args){
        // TODO code application logic here
        try{ 
            //State init = State.getInitialState();
         //   DeadLock d = new DeadLock(init); 
            //System.out.println(d.toString());
          //  System.out.println(d.isFrosenDeadLock(new Point(2,7), init));
            //System.out.println(d.toString());
           // System.out.println(d.isDeadLock(6, 2));
           
           System.out.println("Started at "+new Date());
           Solver s = new Solver(State.getInitialState());            
           s.solve();            
           System.out.println(s.generatePath());  
           System.out.println("Ended at "+ new Date());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}