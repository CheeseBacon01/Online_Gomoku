import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class TTT extends JFrame implements ActionListener {
	Tic_Tac_Toe game;
	JButton[] buttons;
	private JMenuItem resetMenuItem;
	private JMenuItem changeMenuItem;
	public char Symbol1='O';
	public char Symbol2='X';

	class Tic_Tac_Toe{
		char[]board;
		Tic_Tac_Toe(){
			board=new char[10];
			for(int i=1;i<=9;i++){
				board[i]=' ';
			}
		}

		public int set(Character user,int pos){
			if(pos<1||pos>9||board[pos]!=' '){
				return 0;
			}
			if(user=='A'){
				board[pos]=Symbol1;
			}
			else{
				board[pos]=Symbol2;
			}
			return 1;
		}

		public int evaluate(){
			if((board[1]==board[2]&&board[2]==board[3]&&board[1]!=' ')||(board[4]==board[5]&&board[5]==board[6]&&board[4]!=' ')||(board[7]==board[8]&&board[8]==board[9]&&board[7]!=' ')){
				return 1;
			}
			if((board[1]==board[4]&&board[4]==board[7]&&board[1]!=' ')||(board[2]==board[5]&&board[5]==board[8]&&board[2]!=' ')||(board[3]==board[6]&&board[6]==board[9]&&board[3]!=' ')){
				return 1;
			}
			if((board[1]==board[5]&&board[5]==board[9]&&board[1]!=' ')||(board[3]==board[5]&&board[5]==board[7]&&board[3]!=' ')){
				return 1;
			}
			else{
				return 0;
			}
		}
	}

	public TTT(String title){
		super(title);
		game =new Tic_Tac_Toe();
		buttons=new JButton[10];
		this.setLocation(150,250);
		this.setSize(300,200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = this.getContentPane();
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(new GridLayout(3,3));

		MyActionListener actionListener = new MyActionListener();
		
		for(int i=1;i<=9;i++){
			buttons[i]=new JButton(" ");
			buttons[i].addActionListener(actionListener);
			contentPane.add(buttons[i]);
		}
		
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		JMenu funcMenu = new JMenu("FUNC");
		resetMenuItem=new JMenuItem("Reset");
		resetMenuItem.addActionListener(e->resetgame());
		funcMenu.add(resetMenuItem);
		changeMenuItem=new JMenuItem("Change Symbol");
		changeMenuItem.addActionListener(e->changeSymbols());
		funcMenu.add(changeMenuItem);
		menuBar.add(funcMenu);
		
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		this.setVisible(true);
	}

	class MyActionListener implements ActionListener {
		public static char user='A';
		
		public void actionPerformed(ActionEvent event) {
			JButton clickedButton = (JButton) event.getSource();
			
			int index=-1;
			for(int i=1;i<=9;i++){
				if(clickedButton==buttons[i]){
					index=i;
					break;
				}
			}
			if(index!=-1&&clickedButton.getText().equals(" ")){
				int move=game.set(user,index);
				if(move==1){
					if(user=='A'){
						String Symbol3=Character.toString(Symbol1);
						clickedButton.setText(Symbol3);
					}
					else{
						String Symbol4=Character.toString(Symbol2);
						clickedButton.setText(Symbol4);
					}

					if(game.evaluate()==1){
						JOptionPane.showMessageDialog(null,"Player "+user+" wins");
						resetgame();
						return ;
					}
					
					if(tie()){
						JOptionPane.showMessageDialog(null,"Tie");
						resetgame();
						return;
					}
					if(user=='A'){
						user='B';
					}
					else{
						user='A';
					}
				}
			}
		}
	}

	public void resetgame(){
		for(int i=1;i<=9;i++){
			buttons[i].setText(" ");
			game = new TTT.Tic_Tac_Toe();
			MyActionListener.user = 'A';
		}
	}

	public boolean tie(){
		for(int i=1;i<=9;i++){
			if(buttons[i].getText().equals(" ")){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==resetMenuItem){
			resetgame();
		}
		else if(e.getSource()==changeMenuItem){
			changeSymbols();
		}
	}

	public void changeSymbols(){
		String input1=JOptionPane.showInputDialog(this, "Change Player1 Symbol to:");
		String input2=JOptionPane.showInputDialog(this, "Change Player2 Symbol X to:");
		if(input1==null||input2==null||input1.equals(input2)){
			JOptionPane.showMessageDialog(this,"ERROR");
			return;
		}
		Symbol1=input1.charAt(0);
		Symbol2=input2.charAt(0);
		resetgame();
	}

	public static void main(String[] args) {
        TTT frame1 = new TTT("Frame 1");
	}
}