import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class frame extends JFrame implements ActionListener,MouseListener {
    public JList readyline=new JList();
    public JList runningline=new JList();
    public JList blockedline=new JList();
    public JList reserveline=new JList();//后备
    public JList finishline=new JList();
    public JList zombieline =new JList();
    public JList waitingline =new JList();
    public JScrollPane readyjsp=new JScrollPane(readyline);
    public JScrollPane runningjsp=new JScrollPane(runningline);
    public JScrollPane blockedlinejsp=new JScrollPane(blockedline);
    public JScrollPane reservelinejsp=new JScrollPane(reserveline);
    public JScrollPane finishlinejsp=new JScrollPane(finishline);
    public JScrollPane zombiejsp=new JScrollPane(zombieline);
    public JScrollPane waitingjsp=new JScrollPane(waitingline);
    DefaultListModel readylinedlm = new DefaultListModel();
    DefaultListModel runninglinedlm = new DefaultListModel();
    DefaultListModel blockedlinedlm = new DefaultListModel();
    DefaultListModel reservedlm = new DefaultListModel();
    DefaultListModel finishlinedlm = new DefaultListModel();
    DefaultListModel zombiedlm=new DefaultListModel();
    DefaultListModel waitingdlm=new DefaultListModel();
    public JButton  suspend=new JButton("挂起");
    public JButton  wakeup=new JButton("解挂");
    public JButton  append=new JButton("添加进程");
    public JButton  add=new JButton("批量添加进程");
    public JButton  delete =new JButton("删除进程");
    public JButton  restart=new JButton("重启");
    public JButton  compact=new JButton("紧凑");
    public JPanel   instore=new JPanel();
    public JPanel   os=new JPanel();
    public JTextArea   notice=new JTextArea();

    public frame(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        instore.setLayout(null);
        instore.setBorder(BorderFactory.createLineBorder(Color.gray));
        this.add(readyjsp);
        this.add(runningjsp);
        this.add(blockedlinejsp);
        this.add(reservelinejsp);
        this.add(finishlinejsp);
        this.add(zombiejsp);
        this.add(waitingjsp);
        this.add(suspend);
        this.add(wakeup);
        this.add(append);
        this.add(delete);
        this.add(add);
        this.add(restart);
        this.add(compact);
        this.add(instore);
        this.add(os);
        this.add(notice);
        readyline.setBorder(BorderFactory.createTitledBorder("就绪队列"));
        runningline.setBorder(BorderFactory.createTitledBorder("运行"));
        blockedline.setBorder(BorderFactory.createTitledBorder("挂起队列"));
        reserveline.setBorder(BorderFactory.createTitledBorder("后备队列"));
        finishline.setBorder(BorderFactory.createTitledBorder("完成"));
        zombieline.setBorder(BorderFactory.createTitledBorder("僵死队列"));
        waitingline.setBorder(BorderFactory.createTitledBorder("解挂等待队列"));


        readyline.setFixedCellHeight(85);
        runningline.setFixedCellHeight(85);
        blockedline.setFixedCellHeight(70);
        reserveline.setFixedCellHeight(70);
        finishline.setFixedCellHeight(50);
        zombieline.setFixedCellHeight(50);
        waitingline.setFixedCellHeight(70);





        this.setSize(1400,800);
        reservelinejsp.setBounds(0,0,200,750);
        readyjsp.setBounds(200,0,200,750);
        runningjsp.setBounds(400,0,200,750);
        blockedlinejsp.setBounds(600,0,200,400);
        waitingjsp.setBounds(600,401,200,350);
        finishlinejsp.setBounds(800,0,200,750);
        zombiejsp.setBounds(1000,0,200,750);
        add.setBounds(50,755,100,20);
        append.setBounds(260,755,70,20);
        suspend.setBounds(460,755,50,20);
        wakeup.setBounds(670,755,50,20);
        delete.setBounds(860,755,70,20);
        restart.setBounds(1080,755,50,20);
        compact.setBounds(750,755,50,20);
        instore.setBounds(1205,110,185,630);
        os.setBounds(1205,10,185,100);
        notice.setBounds(1205,743,185,30);
        instore.setBackground(Color.lightGray);
        os.setBackground(Color.red);
        notice.setText("操作系统大小：100"+"\n"+"主存总大小：730");
        this.setVisible(true);
        suspend.addActionListener(this);
        add.addActionListener(this);
        append.addActionListener(this);
        wakeup.addActionListener(this);
        delete.addActionListener(this);
        restart.addActionListener(this);
        compact.addActionListener(this);

        runningline.addMouseListener(this);
        reserveline.addMouseListener(this);
        readyline.addMouseListener(this);
        blockedline.addMouseListener(this);
        waitingline.addMouseListener(this);

        readyline.setCellRenderer(new myRenderer());
        runningline.setCellRenderer(new myRenderer());
        blockedline.setCellRenderer(new myRenderer());
        reserveline.setCellRenderer(new myRenderer());
        finishline.setCellRenderer(new myRenderer());
        zombieline.setCellRenderer(new myRenderer());
        waitingline.setCellRenderer(new myRenderer());


    }
    public void actionPerformed(ActionEvent e){}
    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}

    class myRenderer extends JTextArea implements ListCellRenderer {
        public Component getListCellRendererComponent(
                JList list,              // the list
                Object value,            // value to display
                int index,               // cell index
                boolean isSelected,      // is the cell selected
                boolean cellHasFocus)    // does the cell have focus
        {
            this.setLineWrap(true);            //自动换行
            this.setWrapStyleWord(true);        //保持换行不会影响单词的完整性（被中间切开的情况）
            // 加个边框
            setBorder(BorderFactory.createLineBorder(Color.lightGray));
            //setEnabled(list.isEnabled());
            if(isSelected){
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setText((String)value);
            setOpaque(true);
            setEnabled(true);

            return this;
        }
    }
}
//jScrollPane1.setViewportView(myJlist);
/*
 readyline.setFixedCellHeight(40);
        runningline.setFixedCellHeight(40);
        blockedline.setFixedCellHeight(40);
        reserveline.setFixedCellHeight(40);
        finishline.setFixedCellHeight(40);
        zombieline.setFixedCellHeight(40);
         waitingline.setFixedCellHeight(40);
        setLineWrap(true);            //自动换行
        setWrapStyleWord(true);
 */