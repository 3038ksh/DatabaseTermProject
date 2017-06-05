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

public class AddCustomer implements ActionListener {
	JFrame f = new JFrame();
	JPanel p = new JPanel();
	JLabel l_customer_name = new JLabel("고객명");
	JLabel l_born = new JLabel("생일(4자리)");
	JLabel l_phone = new JLabel("연락처");
	JTextField f_add_customer_name = new JTextField();
	JTextField f_add_customer_born = new JTextField();
	JTextField f_add_customer_phone = new JTextField();
	JButton b_add_customer = new JButton("가입신청");
	JButton b_add_customer_cancle = new JButton("취소");
	Connection db;
	String sql;
	PreparedStatement stmt;
	ResultSet rs;
	int customer_id = 1000;
	
	public AddCustomer(Connection db) {
		this.db = db;
		p.setLayout(null);
		l_customer_name.setBounds(20, 20, 100, 30);
		l_born.setBounds(20, 70, 100, 30);
		l_phone.setBounds(20, 120, 100, 30);
		f_add_customer_name.setBounds(130, 20, 100, 30);
		f_add_customer_born.setBounds(130, 70, 100, 30);
		f_add_customer_phone.setBounds(130, 120, 100, 30);
		b_add_customer.setBounds(20, 170, 100, 30);
		b_add_customer_cancle.setBounds(130, 170, 100, 30);
		b_add_customer.addActionListener(this);
		b_add_customer_cancle.addActionListener(this);
		p.add(l_customer_name);
		p.add(l_born);
		p.add(l_phone);
		p.add(f_add_customer_name);
		p.add(f_add_customer_born);
		p.add(f_add_customer_phone);
		p.add(b_add_customer);
		p.add(b_add_customer_cancle);
		f.add(p);
		f.setTitle("회원등록");
		f.setBounds(150, 150, 270, 250);
		f.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == b_add_customer) {
			String name = f_add_customer_name.getText();
			String born = f_add_customer_born.getText();
			String phone = f_add_customer_phone.getText();
			// 먼저 저장된 이름들의 목록 구하기
			try {
				boolean already = this.already(name);
				System.out.println(already);
				if (already) {
					JOptionPane.showMessageDialog(null, "이미 존재하는 이름입니다.");
				}
				else if (born.length()!=4) {
					JOptionPane.showMessageDialog(null, "생일은 (MMDD)형태로 입력하시기 바랍니다.");
				}
				else {
					// customer의 id 확장시키기
					sql = "select customer_id from customer";
					stmt = db.prepareStatement(sql);
					rs = stmt.executeQuery();
					while (rs.next()) {
						customer_id = rs.getInt("customer_id");
						System.out.println(customer_id);
					}
					customer_id++;
					// 새로운 cusotmer 저장하기
					sql = "insert into customer values('"
							+ name + "'," // name
							+ customer_id + ",'" // customer_id
							+ born + "'," // born
							+ phone + "," // phone
							+ "'Normal'," // grade
							+ "0)"; // amount
					stmt = db.prepareStatement(sql);
					stmt.executeUpdate();
					JOptionPane.showMessageDialog(null, "가입되었습니다.");
					f.dispose();
				}
				stmt.close();
				rs.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == b_add_customer_cancle) {
			f.dispose();
		}
	}
	
	public boolean already(String name) throws SQLException {
		boolean checkName = false;
		sql = "select name from customer";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		while (rs.next()) {
			if (name.equals(rs.getString("name"))) {
				checkName = true;
			}
		}
		return checkName;
	}	
}
