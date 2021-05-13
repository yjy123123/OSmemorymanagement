import java.util.ArrayList;
public class PCB{
    public int PID;
    public int counter;
    public int priority;
    public String  state;
    public int addr;
    public int store;
    public PCB(){
        PID=(int)(Math.random()*100);
         counter=(int)(Math.random()*10)+1;
         priority=(int)(Math.random()*10);
         store=(int)((Math.random()*100)+(Math.random()*100)+1);
         state="reserve";

    }
    public PCB(int counter,int priority,int store) {
        PID = (int) (Math.random() * 100);
        this.counter = counter;
        this.priority = priority;
        this.store=store;
        state = "reserve";
    }

}
