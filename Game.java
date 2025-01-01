import java.net.Socket;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

class Game extends JFrame implements ActionListener,MouseListener{
    @Override
    public void actionPerformed(ActionEvent event){
        if(event.getSource()==inputLine){
			String input=inputLine.getText();
			if(!input.isEmpty()){
				textArea.append("Self: "+input+"\n");
				writer.println("CHAT"+input);
				inputLine.setText("");
			}
		}
    }
    JButton[] buttons;
    private static final String ADDRESS="127.0.0.1";
    private static final int PORT=5555;
    private Gomoku game;
    private JTextField inputLine;
    private JTextArea  textArea;
    private char user1='B';
    private char current=user1;
    private InputStream in;
    private OutputStream out;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean myTurn=false;

    @Override
    public void mouseClicked(MouseEvent e){
        if(!myTurn) return;
        int x=(e.getX()-25)/50+1;
        int y=(e.getY()-25)/50+1;
        if(x>=1&&x<=9&&y>=1&&y<=9&&game.board[x][y]==' '){
            game.board[x][y]=current;
            writer.println(x+","+y);
            repaint();
            myTurn=false;
        }
    }

    public Game(String title) {
        super(title);
        game=new Gomoku();
        this.setLocation(300,250);
        this.setSize(800, 500);
        this.setBackground(Color.WHITE);
        textArea=new JTextArea();
        textArea.setBounds(500,20,200,300);
		JScrollPane scrollText=new JScrollPane(textArea);
		scrollText.setBounds(500,20,200,300);
        Container contentPane=getContentPane();
        contentPane.setLayout(null);
        contentPane.add(scrollText);

        inputLine = new JTextField();
        inputLine.setPreferredSize(new Dimension(200,30));
        inputLine.setBounds(500,350,200,30); 
        contentPane.add(inputLine);
        inputLine.addActionListener(this);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addMouseListener(this);
        this.setVisible(true);
        Socket client=null;

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
        for(int i=50;i<=450;i+=50){ 
            g.drawLine(i,50,i,450);
            g.drawLine(50,i,450,i);
        }
        draw(g);
    }

    public void draw(Graphics2D g){
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
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
            board=new char[10][10];
            for(int i=1;i<=9;i++){
                for(int j=1;j<=9;j++){
                    board[i][j]=' ';
                }
            }
        }
        public int evaluate(int x,int y,char current){
            int count=0;
            for(int i=-4;i<=4;i++){  //Horizon
                if(x+i>=1&&x+i<=9&&board[x+i][y]==current){
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
                if(y+i>=1&&y+i<=9&&board[x][y+i]==current){
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
                if(y+i>=1&&y+i<=9&&x+i>=1&&x+i<=9&&board[x+i][y+i]==current){
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
                if(y-i>=1&&y-i<=9&&x+i>=1&&x+i<=9&&board[x+i][y-i]==current){
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

    public void resetgame(Character player){
        game=new Gomoku();
        current=user1;
        myTurn=(player==current);
        repaint();
    }

    public boolean tie(){
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
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
					if(message.equals("NOT_YOUR_TURN")){
                        myTurn=false;
                        continue;
                    }
                    if(message.startsWith("RESET")){
                        char player=message.charAt(6);
                        resetgame(player);
                        continue;
                    }
					if(message.startsWith("CHAT")){
						String msg=message.substring(4);
						textArea.append("Opponent: "+msg+"\n");
						continue;
					}
                    if(message.startsWith("WIN")){
                        char winner=message.charAt(4);
                        JOptionPane.showMessageDialog(Game.this,"Player "+winner+" wins!");
                        resetgame('B');
                        continue;
                    }
                    if(message.equals("TIE")){
                        JOptionPane.showMessageDialog(Game.this,"TIE");
                        resetgame('B');
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
        new Game("Gomoku");
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