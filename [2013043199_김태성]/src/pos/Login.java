package pos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Login implements ActionListener {
	Connection db;
	String sql;
	PreparedStatement stmt;
	ResultSet rs;
	String names;
	String ids;
	Viewer v;

	JFrame f = new JFrame();
	JPanel p = new JPanel();
	JLabel l_name = new JLabel("이름");
	JTextField f_name = new JTextField();
	JLabel l_number = new JLabel("사원번호");
	JTextField f_number = new JTextField();
	JButton login = new JButton("로그인");

	public Login(Connection db, Viewer v) {
		this.v = v;
		this.db = db;
		p.setLayout(null);
		l_name.setBounds(15, 15, 100, 30);
		f_name.setBounds(100, 15, 100, 30);
		l_number.setBounds(15, 50, 100, 30);
		f_number.setBounds(100, 50, 100, 30);
		login.setBounds(220, 30, 100, 30);
		login.addActionListener(this);
		p.add(l_name);
		p.add(f_name);
		p.add(l_number);
		p.add(f_number);
		p.add(login);
		f.add(p);
		f.setBounds(100, 100, 350, 130);
		f.setTitle("사원 로그인");
		f.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == login) {
			String name = f_name.getText();
			String id = f_number.getText();
			try {
				if (name.equals("") || id.equals("")) {
					JOptionPane.showMessageDialog(null, "입력하세요.");
				}
				else if (check(name, id)) {
					JOptionPane.showMessageDialog(null, "로그인되었습니다.");
					System.out.println(setGrade(name));
					v.currentStaff = name;
					v.authority = setGrade(name);
					if (setGrade(name).equals("Supervisor")) {
						v.c_date.removeAllItems();
						v.getSalesItem();
					}
					else if (setGrade(name).equals("Staff")) {
						v.c_date.removeAllItems();
					}
					v.frame.setTitle("식당 관리 시스템 (현재 관리자 : " + name + ")");
					f.dispose();
				}
				else {
					JOptionPane.showMessageDialog(null, "로그인에 실패하였습니다.");
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public boolean check(String name, String id) throws SQLException {
		boolean checkName = false;
		sql = "select name from worker";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		while (rs.next()) {
			if (name.equals(rs.getString("name"))) {
				checkName = true;
			}
		}
		if (checkName) {
			sql = "select worker_id from worker where name = '"
					+ name + "'";
			stmt = db.prepareStatement(sql);
			rs = stmt.executeQuery();
			rs.next();
			if (id.equals(rs.getString("worker_id"))) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public String setGrade(String name) throws SQLException {
		sql = "select grade from worker where name = '"
				+ name + "'";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		rs.next();
		return rs.getString("grade");
	}
}
