import java.net.Socket;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

class Game extends JFrame implements ActionListener,MouseListener{
    @Override
    public void actionPerformed(ActionEvent e){
        
    }
    JButton[] buttons;
    private static final String ADDRESS="127.0.0.1";
    private static final int PORT=5555;
    private Gomoku game;
    private JTextField inputLine;
    private JTextArea  textArea;
    private char user1='B';
    private char user2='W';
    private char current=user1;
    private InputStream in;
    private OutputStream out;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean myTurn=false;

    @Override
    public void mouseClicked(MouseEvent e){
        if(!myTurn) return;
        int x=(e.getX()-50+25)/50+1;
        int y=(e.getY()-50+25)/50+1;
        if(x>=1&&x<=15&&y>=1&&y<=15&&game.board[x][y]==' '){
            game.board[x][y]=current;
            writer.println(x+","+y);
            repaint();
            myTurn=false;
            if(game.evaluate(x,y,current)==1){
                JOptionPane.showMessageDialog(this,"Player "+current+" wins!");
                resetgame();
                return;
            }
            else{
                if(current==user1){
                    current=user2;
                }
                else{
                    current=user1;
                }
            }
            if(tie()){
                JOptionPane.showMessageDialog(this,"TIE");
                resetgame();
                return;
            }
        }
    }

    public Game(String title) {
        super(title);
        game=new Gomoku();
        buttons=new JButton[10];
        this.setLocation(150,250);
        this.setSize(1000, 850);
        this.setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textArea=new JTextArea();
        textArea.setBounds(750,70,200,500); 
        Container contentPane=getContentPane();
        contentPane.setLayout(null);
        contentPane.add(textArea);
        
        inputLine = new JTextField();
        inputLine.setPreferredSize(new Dimension(200,30));
        inputLine.setBounds(750, 600, 200, 30); 
        contentPane.add(inputLine);
        inputLine.addActionListener(this);
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        addMouseListener(this);
        this.setVisible(true);
        Socket client = null;

        try{
            client=new Socket(ADDRESS,PORT);
            out=client.getOutputStream();
            in=client.getInputStream();
            reader=new BufferedReader(new InputStreamReader(in));
            writer=new PrintWriter(out,true);
            new IncomingReader().start();
        }catch(IOException e){
            System.out.println("ERROR");
        }
    }

    public void paint(Graphics _g){
        super.paint(_g);
        Graphics2D g=(Graphics2D)_g;
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.BLACK);
        for(int i=50;i<=750;i+=50){ 
            g.drawLine(i,50,i,750);
            g.drawLine(50,i,750,i);
        }
        draw(g);
    }

    public void draw(Graphics2D g){
        for(int i=1;i<=15;i++){
            for(int j=1;j<=15;j++){
                if(game.board[i][j]!=' '){
                    if(game.board[i][j]==user1){
                        g.setColor(Color.BLACK);
                        g.fillOval(50+(i-1)*50-15,50+(j-1)*50-15,30,30);
                    }
                    else{
                        g.setColor(Color.WHITE);
                        g.fillOval(50+(i-1)*50-15,50+(j-1)*50-15,30,30);
                    }
                }
            }
        }
    }

    class Gomoku{
        char[][] board;
        Gomoku(){
            board=new char[16][16];
            for(int i=1;i<=15;i++){
                for(int j=1;j<=15;j++){
                    board[i][j]=' ';
                }
            }
        }
        public int evaluate(int x,int y,char current){
            int count=0;
            for(int i=-4;i<=4;i++){  //Horizon
                if(x+i>=1&&x+i<=15&&board[x+i][y]==current){
                    count++;
                    if(count>=5){
                        return 1;
                    }
                }
                else{
                    count=0;
                }
            }
            count=0;
            for(int i=-4;i<=4;i++){  //Vertical
                if(y+i>=1&&y<=15&&board[x][y+i]==current){
                    count++;
                    if(count>=5){
                        return 1;
                    }
                }
                else{
                    count=0;
                }
            }
            for(int i=-4;i<=4;i++){  //Diagonal
                if(y+i>=1&&y<=15&&x+i>=1&&x+i<=15&&board[x+i][y+i]==current){
                    count++;
                    if(count>=5){
                        return 1;
                    }
                }
                else{
                    count=0;
                }
            }
            for(int i=-4;i<=4;i++){  //Diagonal
                if(y-i>=1&&y-i<=15&&x+i>=1&&x+i<=15&&board[x+i][y-i]==current){
                    count++;
                    if(count>=5){
                        return 1;
                    }
                }
                else{
                    count=0;
                }
            }
            return 0;
        }
    }

    public void resetgame(){
        game=new Gomoku();
        current=user1;
        myTurn = (current == user1);
        repaint();
    }

    public boolean tie(){
        for(int i=1;i<=15;i++){
            for(int j=1;j<=15;j++){
                if(game.board[i][j]==' '){
                    return false;
                }
            }
        }
        return true;
    }

    class IncomingReader extends Thread {
        public void run(){
            try{
                String message;
                while((message=reader.readLine())!=null){
                    if(message.equals("YOUR_TURN")){
                        myTurn=true;
                        continue;
                    }
                    if(message.equals("RESET")){
                        resetgame();
                        continue;
                    }
                    if(message.startsWith("WIN")){
                        char winner=message.charAt(4);
                        JOptionPane.showMessageDialog(Game.this,"Player "+winner+" wins!");
                        resetgame();
                        continue;
                    }
                    String[] parts=message.split(",");
                    int x=Integer.parseInt(parts[0]);
                    int y=Integer.parseInt(parts[1]);
                    char player=parts[2].charAt(0);
                    game.board[x][y]=player;
                    repaint();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        Game frame1=new Game("Gomoku");
    }

    @Override
    public void mousePressed(MouseEvent e){}

    @Override
    public void mouseReleased(MouseEvent e){}

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){}
}