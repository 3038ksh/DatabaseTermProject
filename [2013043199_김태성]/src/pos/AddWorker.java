package pos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddWorker implements ActionListener {
	JFrame f = new JFrame();
	JPanel p = new JPanel();
	JLabel l_staff_name = new JLabel("직원명");
	JLabel l_grade = new JLabel("직급");;
	JTextField f_staff_name = new JTextField();
	JComboBox<String> c_grade = new JComboBox<String>();
	JButton b_add_staff = new JButton("등록");
	JButton b_cancle = new JButton("취소");
	Connection db;
	String sql;
	PreparedStatement stmt;
	ResultSet rs;
	Viewer v;
	int worker_id = 1000;
	
	public AddWorker(Connection db) {
		this.db = db;
		p.setLayout(null);
		l_staff_name.setBounds(20, 20, 100, 30);
		l_grade.setBounds(20, 70, 100, 30);
		f_staff_name.setBounds(130, 20, 100, 30);
		c_grade.setBounds(130, 70, 100, 30);
		b_add_staff.setBounds(20, 120, 100, 30);
		b_cancle.setBounds(130, 120, 100, 30);
		b_add_staff.addActionListener(this);
		b_cancle.addActionListener(this);
		c_grade.addItem("Supervisor");
		c_grade.addItem("Staff");
		p.add(l_staff_name);
		p.add(l_grade);
		p.add(f_staff_name);
		p.add(c_grade);
		p.add(b_add_staff);
		p.add(b_cancle);
		f.add(p);
		f.setTitle("직원등록");
		f.setBounds(150, 150, 270, 200);
		f.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == b_add_staff) {
			String staff_name = f_staff_name.getText();
			String grade = (String)c_grade.getSelectedItem();
			// 먼저 저장된 이름들의 목록 구하기
			try {
				boolean already = this.already(staff_name);
				System.out.println(already);
				if (already) {
					JOptionPane.showMessageDialog(null, "이미 존재하는 이름입니다.");
				}
				else {
					// worker의 id 확장시키기
					sql = "select worker_id from worker";
					stmt = db.prepareStatement(sql);
					rs = stmt.executeQuery();
					String workers_id = "";
					while (rs.next()) {
						workers_id += rs.getString("worker_id") + " ";
					}
					for (int i = worker_id; i < 9999; i++) {
						if (!workers_id.contains(i+"")) {
							worker_id = i;
							break;
						}
					}
					// 새로운 worker 저장하기
					sql = "insert into worker values('"
							+ staff_name + "'," // name
							+ worker_id + ",'" // worker_id
							+ grade + "',"
							+ "0)"; // grade
					stmt = db.prepareStatement(sql);
					stmt.executeUpdate();
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
	
	public boolean already(String staff_name) throws SQLException {
		boolean checkName = false;
		sql = "select name from worker";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		while (rs.next()) {
			if (staff_name.equals(rs.getString("name"))) {
				checkName = true;
			}
		}
		return checkName;
	}
}
