import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.*;

public class scheduling extends frame implements Runnable {
    private HashMap<String, PCB> process = new HashMap();
    private Object temp = new Object();
    private JList source = new JList();
    public PCB  running;
    private ArrayList pidmng = new ArrayList();
    public LinkedList <memory> partitionlist=new LinkedList();
    public HashMap<String,JLabel> image =new HashMap<>();
    public HashMap<String,memory> map=new HashMap<>();   //occupied
    public HashMap<memory,JLabel> map2=new HashMap<>();   //free
    public int flag=1;
    memory os=new memory(0,100,"occupied");
    memory free=new memory(100,630,"free");
    public scheduling() {
        new Thread(this).start();
        partitionlist.add(os);
        partitionlist.add(free);
        JLabel temp=new JLabel("大小:"+free.size,JLabel.CENTER);
        temp.setBorder(BorderFactory.createLineBorder(Color.gray));
        temp.setBounds(0,free.adrr-100,185,free.size);
        temp.setVisible(true);
        instore.add(temp);
        repaint();
        map2.put(free,temp);
    }

    public void actionPerformed(ActionEvent e) {//信息作为key有问题 key会改变
        if (e.getSource() == add) {
            for (int i = 0; i < 5; i++) {
                PCB pcb = new PCB();
                while (pidmng.contains(pcb.PID)) {
                    pcb.PID = (int) (Math.random() * 100);
                }
                process.put("PID:"+pcb.PID, pcb);
                pidmng.add(pcb.PID);
            }
            fresh();
        } else {
            if (e.getSource() == append) {
                Object[] values = {"随机生成", "自定义参数"};
                Object selectedValue = JOptionPane.showInputDialog(null, null, "PCB创建", JOptionPane.INFORMATION_MESSAGE, null,
                        values, values[0]);
                if (selectedValue == "自定义参数") {
                    String counter  = JOptionPane.showInputDialog("请输入要求进行时间");
                    String priority = JOptionPane.showInputDialog("请输入优先权");
                    String store =JOptionPane.showInputDialog("请输入需要的主存空间(小于630)");
                    PCB pcb = new PCB(Integer.parseInt(counter), Integer.parseInt(priority),Integer.parseInt(store));
                    while (pidmng.contains(pcb.PID)) {
                        pcb.PID = (int) (Math.random() * 100);
                    }
                    pidmng.add(pcb.PID);
                    process.put("PID:"+pcb.PID, pcb);
                }
                if(selectedValue=="随机生成"){
                    PCB pcb = new PCB();
                    while (pidmng.contains(pcb.PID)) {
                        pcb.PID = (int) (Math.random() * 100);
                    }
                    pidmng.add(pcb.PID);
                    process.put("PID:"+pcb.PID, pcb);
                }
                fresh();
            } else {
                if (e.getSource() == suspend) {
                    String []str=((String)temp).split("\n");
                    PCB pcb = process.get(str[0]);
                    if(pcb.state=="running"||pcb.state=="ready") {
                        pcb.state = "blocked";
                        combine(String.valueOf(pcb.PID));
                        fresh();
                    }
                } else {
                    if (e.getSource() == wakeup) {
                        String []str=((String)temp).split("\n");
                        PCB pcb = process.get(str[0]);
                        if (pcb.state == "blocked") {
                            pcb.state="waiting";
                            fresh();
                        }

                    } else {
                        if (e.getSource() == delete) {
                            String []str=((String)temp).split("\n");
                            PCB pcb = process.get(str[0]);
                            if(pcb.state=="running"||pcb.state=="ready"){
                                combine(String.valueOf(pcb.PID));
                            }
                            pcb.state = "zombie";
                            fresh();
                        } else {
                            if (e.getSource() == restart) {
                                restart();
                            }else {
                                if(e.getSource()==compact){
                                    compact();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public void mouseClicked(MouseEvent e) {
        source = (JList) e.getSource();
        temp = source.getSelectedValue();
        reserveline.clearSelection();
        readyline.clearSelection();
        runningline.clearSelection();
        blockedline.clearSelection();
        finishline.clearSelection();
        zombieline.clearSelection();
        source.setSelectedValue(temp, false);
    }
    public void restart(){
        process.clear();
        readylinedlm.clear();
        runninglinedlm.clear();
        blockedlinedlm.clear();
        reservedlm.clear();
        finishlinedlm.clear();
        zombiedlm.clear();
        waitingdlm.clear();
        partitionlist.clear();
        partitionlist.add(os);
        free.size=630;
        free.state="free";
        partitionlist.add(free);
        image.clear();
        instore.removeAll();
        map.clear();
        JLabel temp=new JLabel("大小:"+free.size,JLabel.CENTER);
        temp.setBorder(BorderFactory.createLineBorder(Color.gray));
        temp.setBounds(0,free.adrr-100,185,free.size);
        temp.setVisible(true);
        instore.add(temp);
        repaint();
        map2.put(free,temp);
        repaint();
    }
    public void fresh() {
        if(flag==1) {
            flag=0;
            synchronized (scheduling.class) {
                classify();
                allocation();
                classify();
                if (runninglinedlm.size() == 0 && readylinedlm.size() != 0) {
                    String str = (String) readylinedlm.get(0);
                    String[] str1 = (str).split("\n");
                    PCB pcb = process.get(str1[0]);
                    pcb.state = "running";
                    running = pcb;
                    classify();
                }
            }
            flag=1;
        }
    }
    public void allocation(){ //边循环，边改list 很容易出现问题ConcurrentModificationException   用链表，有序  内存 首次适应  //用于内存分配
        int w=waitingdlm.size();
        for(int j=0;j<w;j++){
            String str[]=((String)waitingdlm.getElementAt(j)).split("\n");
            PCB pcb=process.get(str[0]);
            //ArrayList <memory> arrayList=partitionlist;
            for(int k=0;k<partitionlist.size();k++){
                memory m=partitionlist.get(k);
                if(m.state=="free"&&m.size>=pcb.store){
                    instore.remove(map2.get(m));
                    map2.remove(m);
                    pcb.addr=m.adrr;
                    pcb.state="ready";
                    memory a=new memory(m.adrr+pcb.store,m.size-pcb.store,"free");
                    partitionlist.add(k+1,a);
                    m.state="occupied";
                    m.size=pcb.store;
                    JLabel temp=new JLabel(str[0],JLabel.CENTER);
                    temp.setBackground(Color.pink);
                    temp.setOpaque(true);
                    temp.setBorder(BorderFactory.createLineBorder(Color.gray));
                    temp.setBounds(0,m.adrr-100,185,m.size);
                    temp.setVisible(true);
                    instore.add(temp);
                    //repaint();
                    image.put(String.valueOf(pcb.PID),temp);
                    map.put(String.valueOf(pcb.PID),m);
                    JLabel jlb=new JLabel("大小:"+a.size,JLabel.CENTER);
                    jlb.setBorder(BorderFactory.createLineBorder(Color.gray));
                    jlb.setBounds(0,a.adrr-100,185,a.size);
                    jlb.setVisible(true);
                    instore.add(jlb);
                    repaint();
                    map2.put(a,jlb);
                    break;
                }
            }
        }
        int i=reservedlm.size();
        for(int j=0;j<i;j++){
            String str[]=((String)reservedlm.getElementAt(j)).split("\n");
            PCB pcb=process.get(str[0]);
            //ArrayList <memory> arrayList=partitionlist;
            for(int k=0;k<partitionlist.size();k++){
                memory m=partitionlist.get(k);
                if(m.state=="free"&&m.size>=pcb.store){
                    instore.remove(map2.get(m));
                    map2.remove(m);
                    pcb.addr=m.adrr;
                    pcb.state="ready";
                    memory a=new memory(m.adrr+pcb.store,m.size-pcb.store,"free");
                    partitionlist.add(k+1,a);
                    m.state="occupied";
                    m.size=pcb.store;
                    JLabel temp=new JLabel(str[0],JLabel.CENTER);
                    temp.setBackground(Color.pink);
                    temp.setOpaque(true);
                    temp.setBorder(BorderFactory.createLineBorder(Color.gray));
                    temp.setBounds(0,m.adrr-100,185,m.size);
                    temp.setVisible(true);
                    instore.add(temp);
                    //repaint();
                    image.put(String.valueOf(pcb.PID),temp);
                    map.put(String.valueOf(pcb.PID),m);
                    JLabel jlb=new JLabel("大小:"+a.size,JLabel.CENTER);
                    jlb.setBorder(BorderFactory.createLineBorder(Color.gray));
                    jlb.setBounds(0,a.adrr-100,185,a.size);
                    jlb.setVisible(true);
                    instore.add(jlb);
                    repaint();
                    map2.put(a,jlb);
                    break;
                }
            }
        }
    }
    public void  combine(String PID){   //内存回收
        memory my=map.get(PID);
        JLabel jlb=image.get(PID);
        image.remove(PID);
        instore.remove(jlb);
        int index= partitionlist.indexOf(my);
        memory m1=partitionlist.get(index-1);
        if(partitionlist.getLast()!=my) {
            memory m2 = partitionlist.get(index + 1);
            if((m1.state=="free")&&(m2.state=="free")){ //两边均是空
                m1.size+=my.size+m2.size;
                partitionlist.remove(my);
                partitionlist.remove(m2);
                instore.remove(map2.get(m1));
                instore.remove(map2.get(m2));
                map2.remove(m1);
                map2.remove(m2);
                JLabel jlb1=new JLabel("大小:"+m1.size,JLabel.CENTER);
                jlb1.setBorder(BorderFactory.createLineBorder(Color.gray));
                jlb1.setBounds(0,m1.adrr-100,185,m1.size);
                jlb1.setVisible(true);
                instore.add(jlb1);
                repaint();
                map2.put(m1,jlb1);
            }else{
                if((m1.state=="free")&&(m2.state=="occupied")){
                    m1.size+=my.size;
                    partitionlist.remove(my);
                    instore.remove(map2.get(m1));
                    map2.remove(m1);
                    JLabel jlb1=new JLabel("大小:"+m1.size,JLabel.CENTER);
                    jlb1.setBorder(BorderFactory.createLineBorder(Color.gray));
                    jlb1.setBounds(0,m1.adrr-100,185,m1.size);
                    jlb1.setVisible(true);
                    instore.add(jlb1);
                    repaint();
                    map2.put(m1,jlb1);

                }else{
                    if((m1.state=="occupied")&&(m2.state=="occupied")){
                        my.state="free";

                        JLabel jlb1=new JLabel("大小:"+my.size,JLabel.CENTER);
                        jlb1.setBorder(BorderFactory.createLineBorder(Color.gray));
                        jlb1.setBounds(0,my.adrr-100,185,my.size);
                        jlb1.setVisible(true);
                        instore.add(jlb1);
                        repaint();
                        map2.put(my,jlb1);

                    }else{
                        my.size+=m2.size;
                        my.state="free";
                        partitionlist.remove(m2);

                        instore.remove(map2.get(m2));
                        map2.remove(m2);
                        JLabel jlb1=new JLabel("大小:"+my.size,JLabel.CENTER);
                        jlb1.setBorder(BorderFactory.createLineBorder(Color.gray));
                        jlb1.setBounds(0,my.adrr-100,185,my.size);
                        jlb1.setVisible(true);
                        instore.add(jlb1);
                        repaint();
                        map2.put(my,jlb1);


                    }
                }
            }
        }else{
            if(m1.state=="occupied"){
                my.state="free";

                JLabel jlb1=new JLabel("大小:"+my.size,JLabel.CENTER);
                jlb1.setBorder(BorderFactory.createLineBorder(Color.gray));
                jlb1.setBounds(0,my.adrr-100,185,my.size);
                jlb1.setVisible(true);
                instore.add(jlb1);
                repaint();
                map2.put(my,jlb1);

            }else {
                m1.size+=my.size;
                partitionlist.remove(my);

                instore.remove(map2.get(m1));
                map2.remove(m1);
                JLabel jlb1=new JLabel("大小:"+m1.size,JLabel.CENTER);
                jlb1.setBorder(BorderFactory.createLineBorder(Color.gray));
                jlb1.setBounds(0,m1.adrr-100,185,m1.size);
                jlb1.setVisible(true);
                instore.add(jlb1);
                repaint();
                map2.put(m1,jlb1);
            }
        }

    }


    public void run() { //时间片控制
        while (true) {
                try {
                        if (runninglinedlm.size() != 0) {
                            //String str1 = (String) runninglinedlm.get(0);
                            //PCB pcb1 = process.get(str1);  //pcb1 没有 可能原因 优先权时间减少了
                            Thread.sleep(5000);
                            running.counter--;
                            running.priority--;
                            if (running.counter == 0) {
                                running.state = "finish";
                                combine(String.valueOf(running.PID));
                            } else {
                                running.state = "ready";
                            }
                            fresh();//顺序
                        }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        }
    }
    public  void classify() { //重新对状态 可视化进行匹配
        readylinedlm.clear();
        runninglinedlm.clear();
        blockedlinedlm.clear();
        reservedlm.clear();
        finishlinedlm.clear();
        zombiedlm.clear();  //是否会有东西
        waitingdlm.clear();
        ArrayList<PCB> values = new ArrayList();
        synchronized (process) {
            for (String str : process.keySet()) {//java.util.ConcurrentModificationException   ，使用iterator遍历集合的同时对集合进行修改  //优先度不准
                values.add(process.get(str));
            }
            int i = values.size();
            for (int j = 0; j < i; j++) {//找出优先权最大的
                int s = -100;
                PCB tmp = new PCB();
                for (PCB a : values) {
                    if (a.priority > s) {
                        s = a.priority;
                        tmp = a;
                    }
                }
                match(tmp);
                values.remove(tmp);
                //System.out.println(tmp.PID);
            }
        }
        }
    public void match(PCB pcb) {  //
        String msg = "PID:" + pcb.PID + "\n" + "运行时间:" + pcb.counter + "\n" + "优先权：" + pcb.priority;
        if (pcb.state == "ready") {
            msg=msg+"\n"+"所需主存大小："+pcb.store+"\n"+"主存起始位置："+pcb.addr;
            readylinedlm.addElement(msg);
            readyline.setModel(readylinedlm);
        } else {
            if (pcb.state == "running") {
                msg=msg+"\n"+"所需主存大小："+pcb.store+"\n"+"主存起始位置："+pcb.addr;
                runninglinedlm.addElement(msg);
                runningline.setModel(runninglinedlm);
            } else {
                if (pcb.state == "finish") {
                    finishlinedlm.addElement(msg);
                    finishline.setModel(finishlinedlm);
                } else {
                    if (pcb.state == "zombie") {
                        zombiedlm.addElement(msg);
                        zombieline.setModel(zombiedlm);
                    } else {
                        if (pcb.state == "blocked") {
                            msg=msg+"\n"+"所需主存大小："+pcb.store;
                            blockedlinedlm.addElement(msg);
                            blockedline.setModel(blockedlinedlm);
                        } else {
                            if (pcb.state == "reserve") {
                                msg=msg+"\n"+"所需主存大小："+pcb.store;
                                reservedlm.addElement(msg);
                                reserveline.setModel(reservedlm);
                            }else{
                                if(pcb.state=="waiting"){
                                    msg=msg+"\n"+"所需主存大小："+pcb.store;
                                    waitingdlm.addElement(msg);
                                    waitingline.setModel(waitingdlm);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //public LinkedList <memory> partitionlist=new LinkedList();
    //public HashMap<String,JLabel> image =new HashMap<>();
    //public HashMap<memory,JLabel> map2=new HashMap<>();
    //public HashMap<String,memory> map=new HashMap<>();   //occupied
    public void compact(){//紧凑
        int size=0;
        int adrr=0;
        ArrayList <memory> al=new ArrayList<>();
        al.addAll(partitionlist);
        for(memory m:al){
            if(m.state=="free"){
                size+=m.size;
                partitionlist.remove(m);
                instore.remove(map2.get(m));
                map2.remove(m);
                if(map.containsKey(m)) {
                    map.remove(m);
                }

            }else{
                adrr+=m.size;
                String pid=null;
                for(String key:map.keySet()){
                    if(map.get(key)==m){
                        pid=key;
                    }
                }
                if(process.containsKey("PID:"+pid)){
                    process.get("PID:"+pid).addr-=size;
                }
                if(image.containsKey(pid)){
                    //System.out.println("ok");
                    image.get(pid).setBounds(0,m.adrr-100-size,185,m.size);

                    m.adrr-=size;
                }
            }

        }
        memory free=new memory(adrr,size,"free");
        partitionlist.add(free);
        JLabel jlb=new JLabel("大小:"+free.size,JLabel.CENTER);
        jlb.setBounds(0,free.adrr-100,185,free.size);
        jlb.setBorder(BorderFactory.createLineBorder(Color.gray));
        map2.put(free,jlb);
        jlb.setVisible(true);
        instore.add(jlb);
        repaint();
        allocation();
    }

    public static void main(String args[]) {
        new scheduling();
    }

}
