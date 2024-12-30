import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class Server{
    private static int portNumber=5555;
    private Gomoku game;
    private char current;
    private PrintWriter out1,out2;

    public static void main(String[] args){
        new Server().Connect();
    }

    public Server(){
        game=new Gomoku();
        current='B';
    }

    public void Connect(){
        try(ServerSocket serverSocket=new ServerSocket(portNumber)){
            System.out.println("Waiting for client connection......");
            Socket socket1=serverSocket.accept();
            System.out.println("Client1 connected");
            Socket socket2=serverSocket.accept();
            System.out.println("Client2 connected");

            InputStream input1=socket1.getInputStream();
            OutputStream output1=socket1.getOutputStream();
            BufferedReader reader1=new BufferedReader(new InputStreamReader(input1));
            PrintWriter writer1=new PrintWriter(output1,true);
            InputStream input2=socket2.getInputStream();
            OutputStream output2=socket2.getOutputStream();
            BufferedReader reader2=new BufferedReader(new InputStreamReader(input2));
            PrintWriter writer2=new PrintWriter(output2,true);
            
            writer1.println("YOUR_TURN");
            writer1.flush();
            new MyThread(reader1,writer1,writer2,'B').start();
            new MyThread(reader2,writer2,writer1,'W').start();
        }catch(IOException e){
            System.out.println(e);
        }
    }

    class MyThread extends Thread{
        private BufferedReader in;
        private PrintWriter out,out2;
        private char player;

        public MyThread(BufferedReader in,PrintWriter out1,PrintWriter out2,char player){
            this.out=out1;
            this.in=in;
            this.out2=out2;
            this.player=player;
        }

        public void run(){
            try{
                while(true){
                    String str=in.readLine();
                    if(str!=null){
                        String[] buffer=str.split(",");
                        int x=Integer.parseInt(buffer[0]);
                        int y=Integer.parseInt(buffer[1]);

                        if(game.board[x][y]==' '&&current==player){
                            game.board[x][y]=current;
                            out.println(x+","+y+","+current);
                            out.flush();
                            out2.println(x+","+y+","+current);
                            out2.flush();

                            if(game.evaluate(x,y,current)==1){
                                out.println("WIN "+current);
                                out2.println("WIN "+current);
                                game.reset();
                                current='B';
                                out.println("RESET B");
                                out2.println("RESET W");
                                if(player=='B'){
                                    out.println("YOUR_TURN");
                                }
                                else{
                                    out2.println("YOUR_TURN");
                                    out.println("NOT_YOUR_TURN");
                                }
                            }
                            else if (game.Tie()){
                                out.println("TIE");
                                out2.println("TIE");
                                game.reset();
                                current='B';
                                out.println("RESET B");
                                out2.println("RESET W");
                                if(player=='B'){
                                    out.println("YOUR_TURN");
                                }
                                else{
                                    out2.println("YOUR_TURN");
                                    out.println("NOT_YOUR_TURN");
                                }
                            }
                            else{
                                if(current=='B'){
                                    current='W';
                                }
                                else{
                                    current='B';
                                }
                                out2.println("YOUR_TURN");
                            }
                        }
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    class Gomoku{
        char[][] board;

        Gomoku(){
            board=new char[16][16];
            reset();
        }

        void reset(){
            for(int i=1;i<=15;i++){
                for(int j=1;j<=15;j++){
                    board[i][j]=' ';
                }
            }
        }

        public boolean Tie(){
            for(int i=1;i<=15;i++){
                for(int j=1;j<=15;j++){
                    if(board[i][j]==' '){
                        return false;
                    }
                }
            }
            return true;
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
                if(y+i>=1&&y+i<=15&&board[x][y+i]==current){
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
                if(y+i>=1&&y+i<=15&&x+i>=1&&x+i<=15&&board[x+i][y+i]==current){
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
}