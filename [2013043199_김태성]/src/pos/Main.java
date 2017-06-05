package pos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Main implements ActionListener {
	private JFrame frame = new JFrame();
	private JPanel panel1 = new JPanel();
	private JLabel id = new JLabel("아이디");
	private JLabel pwd = new JLabel("비밀번호");
	private JTextField putid = new JTextField();
	private JPasswordField putpwd = new JPasswordField();
	private JButton login = new JButton("로그인");
	public String username;
	public String password;
	public ConnectDB connect;
	
	public Main() {
		// 패널 레이아웃 설정
		panel1.setLayout(null);
		// 컴포넌트 위치 지정
		id.setBounds(20, 10, 60, 30);
		pwd.setBounds(20, 50, 60, 30);
		putid.setBounds(100, 10, 80, 30);
		putpwd.setBounds(100, 50, 80, 30);
		login.setBounds(200, 25, 80, 35);
		// 버튼에 액션리스너 추가
		login.addActionListener(this);
		// 패널에 컴포넌트 추가
		panel1.add(id);
		panel1.add(pwd);
		panel1.add(putid);
		panel1.add(putpwd);
		panel1.add(login);
		// 프레임에 패널 추가
		frame.add(panel1);
		// 프레임 설정
		frame.setTitle("데이터베이스 로그인");
		frame.setBounds(150, 150, 320, 130);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		frame.setVisible(true);		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == login) {
			username = putid.getText();
			password = new String(putpwd.getPassword());
			
			new ConnectDB(username, password);
			frame.dispose();
		}
	}
}
