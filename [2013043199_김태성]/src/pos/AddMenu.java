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

public class AddMenu implements ActionListener {
	JFrame f = new JFrame();
	JPanel p = new JPanel();
	JLabel l_menu_name = new JLabel("메뉴명");
	JLabel l_price = new JLabel("가격");;
	JTextField f_add_menu_name = new JTextField();
	JTextField f_add_price = new JTextField();
	JButton b_add_menu = new JButton("등록");
	JButton b_cancle = new JButton("취소");
	Connection db;
	String sql;
	PreparedStatement stmt;
	ResultSet rs;
	Viewer v;
	int menu_id = 1000;
	
	public AddMenu(Connection db, Viewer v) {
		this.v = v;
		this.db = db;
		p.setLayout(null);
		l_menu_name.setBounds(20, 20, 100, 30);
		l_price.setBounds(20, 70, 100, 30);
		f_add_menu_name.setBounds(130, 20, 100, 30);
		f_add_price.setBounds(130, 70, 100, 30);
		b_add_menu.setBounds(20, 120, 100, 30);
		b_cancle.setBounds(130, 120, 100, 30);
		b_add_menu.addActionListener(this);
		b_cancle.addActionListener(this);
		p.add(l_menu_name);
		p.add(l_price);
		p.add(f_add_menu_name);
		p.add(f_add_price);
		p.add(b_add_menu);
		p.add(b_cancle);
		f.add(p);
		f.setTitle("메뉴등록");
		f.setBounds(150, 150, 270, 200);
		f.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == b_add_menu) {
			String menu = f_add_menu_name.getText();
			String price = f_add_price.getText();
			// 먼저 저장된 이름들의 목록 구하기
			try {
				boolean already = this.already(menu);
				System.out.println(already);
				if (already) {
					JOptionPane.showMessageDialog(null, "이미 존재하는 이름입니다.");
				}
				else {
					// menu의 id 확장시키기
					sql = "select menu_id from menu";
					stmt = db.prepareStatement(sql);
					rs = stmt.executeQuery();
					while (rs.next()) {
						menu_id = rs.getInt("menu_id");
					}
					// 새로운 menu 저장하기
					sql = "insert into menu values('"
							+ menu + "','" // menu
							+ menu_id + "'," // menu_id
							+ price + ")"; // price
					stmt = db.prepareStatement(sql);
					stmt.executeUpdate();
					v.menuUpdate();
					JOptionPane.showMessageDialog(null, "등록되었습니다.");
					f.dispose();
				}
				stmt.close();
				rs.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == b_cancle) {
			f.dispose();
		}
	}
	
	public boolean already(String menu) throws SQLException {
		boolean checkName = false;
		sql = "select menu from menu";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		while (rs.next()) {
			if (menu.equals(rs.getString("menu"))) {
				checkName = true;
			}
		}
		return checkName;
	}
}
