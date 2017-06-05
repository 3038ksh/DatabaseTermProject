package pos;

import java.awt.BorderLayout;
import java.util.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Viewer implements ActionListener {
	// 오늘의 날짜
	Calendar cal = Calendar.getInstance();
	String today = cal.get(Calendar.YEAR) + "/"
			+ (cal.get(Calendar.MONTH)+1) + "/"
			+ cal.get(Calendar.DATE);
	
	// 데이터베이스
	Connection db;
	String sql;
	PreparedStatement stmt;
	ResultSet rs;
	int customer_id = 1000;
	int menu_id = 1000;
	
	int dataLoaded = 0;
	
	String authority; // 현재 로그인 권한
	String currentStaff; // 현재 로그인된 사원
	
	JFrame frame = new JFrame();
	
	// 타이틀
	JLabel title = new JLabel("식당 주문관리");
	
	// 0. 메뉴바
	JMenuBar bar = new JMenuBar();
	// 0-1. 1차메뉴
	JMenu menu = new JMenu("Menu");
	// 0-2. 2차메뉴
	JMenuItem open = new JMenuItem("Open");
	JMenuItem login = new JMenuItem("Log in");
	// 0-3. 메뉴바의 아이템
	String filePath;
	
	// 패널들
	JPanel main_panel = new JPanel();
	JPanel title_panel = new JPanel();
	JPanel grid_panel = new JPanel();
	JPanel table_panel = new JPanel();
	JPanel order_panel = new JPanel();
	JPanel menu_panel = new JPanel();
	JPanel sign_panel = new JPanel();
	
	// 1. 테이블현황 패널
	JButton[] tables = new JButton[20];
	
	// 2. 주문내역 패널
	JTextArea t_order = new JTextArea();
	String t_order_string = "<추가주문>\n";
	JLabel l_customer_name = new JLabel("고객명");
	JTextField f_customer_name = new JTextField();
	JLabel l_table_name = new JLabel("테이블명");
	JComboBox<String> c_table_name = new JComboBox<String>();
	JButton b_order = new JButton("주문");
	JButton b_cancle = new JButton("취소");
	JButton b_pay = new JButton("결제");

	// 3. 메뉴 패널
	JButton[] menus = new JButton[20];
	
	// 4. 등록/조회 패널
	JTabbedPane tp = new JTabbedPane();
	JPanel tab_customer = new JPanel();
	// 4-1. 고객 패널
	JLabel l_customer_name4 = new JLabel("고객명");
	JTextField f_customer_name4 = new JTextField();
	JButton b_sign = new JButton("가입");
	JButton b_find = new JButton("조회");
	JTextArea t_customer = new JTextArea();
	// 4-2. 매출 패널
	JPanel tab_sales = new JPanel();
	JLabel l_period = new JLabel("기간");
	JComboBox<String> c_date = new JComboBox<String>();
	JTextArea t_sales_area = new JTextArea(); 
	// 4-3. 직원 패널
	JPanel tab_staff = new JPanel();
	JLabel l_staff_name = new JLabel("직원명");
	JTextField f_staff_name = new JTextField();
	JButton b_add_staff = new JButton("직원등록");
	JButton b_find_staff = new JButton("조회");
	JTextArea t_staff_area = new JTextArea();
	// 4-4. 메뉴 패널
	JPanel tab_menu = new JPanel();
	JLabel l_menu_name = new JLabel("메뉴명");
	JTextField f_menu_name = new JTextField();
	JButton b_menu_add = new JButton("메뉴등록");
	JButton b_find_m = new JButton("조회");
	JTextArea t_menu_area = new JTextArea();
	
	// 생성자
	public Viewer(Connection db) {
		this.db = db;
		// JMenuBar
		open.setMnemonic('O');
		login.setMnemonic('L');
		open.addActionListener(this);
		login.addActionListener(this);
		menu.add(open); // JMenu에 Item 부착
		menu.add(login);
		bar.add(menu); // JMenuBar에 JMenu 부착
		
		// title_panel
		title.setFont(new Font("필기체", 1, 40));
		title_panel.add(title);
		title_panel.setBackground(Color.WHITE);
		title_panel.setBorder(new LineBorder(Color.BLACK, 3));

		// grid_panel
		grid_panel.setLayout(new GridLayout(2, 2));
		
		// 1. 테이블 현황 패널
		// table_panel
		table_panel.setBorder(new TitledBorder("테이블 현황"));
		table_panel.setLayout(new GridLayout(4, 5));
		// 테이블 버튼 생성
		for (int i = 0; i < 20; i++) {
			tables[i] = new JButton((i+1)+"");
			tables[i].setBackground(Color.WHITE);
			tables[i].setFont(new Font("필기체", 1, 20));
			tables[i].addActionListener(this);
			table_panel.add(tables[i]);
		}
		
		// 2. 주문내역 패널
		// order_panel
		order_panel.setBorder(new TitledBorder("주문 내역"));
		order_panel.setLayout(null);
		t_order.setBorder(new LineBorder(Color.gray, 2));
		t_order.setEditable(false);
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(t_order);
		for (int i = 0; i < 20; i++) {
			c_table_name.addItem((i + 1) +"");
		}
		c_table_name.addActionListener(this);
		b_order.addActionListener(this);
		b_order.setBackground(Color.WHITE);
		b_cancle.addActionListener(this);
		b_cancle.setBackground(Color.WHITE);
		b_pay.addActionListener(this);
		b_pay.setBackground(Color.WHITE);
		// 바운드
		scroll.setBounds(15, 18, 200, 330);
		l_customer_name.setBounds(230, 15, 100, 30);
		f_customer_name.setBounds(230, 50, 100, 30);
		l_table_name.setBounds(230, 85, 100, 30);
		c_table_name.setBounds(230, 110, 100, 30);
		b_order.setBounds(230, 170, 100, 30);
		b_cancle.setBounds(230, 220, 100, 30);
		b_pay.setBounds(230, 270, 100, 30);
		// order_panel에 컴포넌트들 부착
		order_panel.add(scroll);
		order_panel.add(l_customer_name);
		order_panel.add(f_customer_name);
		order_panel.add(l_table_name);
		order_panel.add(c_table_name);
		order_panel.add(b_order);
		order_panel.add(b_cancle);
		order_panel.add(b_pay);
		
		// 3. 메뉴 패널
		// menu_panel
		menu_panel.setBorder(new TitledBorder("메뉴"));
		menu_panel.setLayout(new GridLayout(10, 2));
		// 메뉴 버튼 생성
		for (int i = 0; i < 20; i++) {
			menus[i] = new JButton();
			menus[i].addActionListener(this);
			menus[i].setBackground(Color.WHITE);
			menu_panel.add(menus[i]);
		}
		
		// 4. 등록/조회 패널
		// sign_panel
		sign_panel.setBorder(new TitledBorder("등록/조회"));
		sign_panel.setLayout(new BorderLayout());
		// 4-1. 고객 패널
		tab_customer.setLayout(null);
		l_customer_name4.setBounds(15, 15, 100, 30);
		f_customer_name4.setBounds(15, 50, 100, 30);
		b_sign.setBounds(180, 50, 60, 30);
		b_find.setBounds(250, 50, 60, 30);
		t_customer.setBounds(15, 90, 300, 200);
		t_customer.setBorder(new LineBorder(Color.gray, 2));
		b_sign.addActionListener(this);
		b_find.addActionListener(this);
		b_sign.setBackground(Color.WHITE);
		b_find.setBackground(Color.WHITE);
		tab_customer.add(l_customer_name4);
		tab_customer.add(f_customer_name4);
		tab_customer.add(b_sign);
		tab_customer.add(b_find);
		tab_customer.add(t_customer);
		// 4-2. 매출 패널
		tab_sales.setLayout(null);
		l_period.setBounds(15, 15, 100, 30);
		c_date.setBounds(150, 15, 100, 30);
		t_sales_area.setBounds(15, 50, 300, 240);
		t_sales_area.setBorder(new LineBorder(Color.gray, 2));
		tab_sales.add(l_period);
		tab_sales.add(c_date);
		tab_sales.add(t_sales_area);
		// 4-3. 직원 패널
		tab_staff.setLayout(null);
		l_staff_name.setBounds(15, 15, 100, 30);
		f_staff_name.setBounds(15, 50, 100, 30);
		b_add_staff.setBounds(150, 50, 90, 30);
		b_find_staff.setBounds(250, 50, 60, 30);
		t_staff_area.setBounds(15, 90, 300, 200);
		t_staff_area.setBorder(new LineBorder(Color.gray, 2));
		b_add_staff.addActionListener(this);
		b_find_staff.addActionListener(this);
		b_add_staff.setBackground(Color.WHITE);
		b_find_staff.setBackground(Color.WHITE);
		tab_staff.add(l_staff_name);
		tab_staff.add(f_staff_name);
		tab_staff.add(b_add_staff);
		tab_staff.add(b_find_staff);
		tab_staff.add(t_staff_area);
		// 4-4. 메뉴 패널
		tab_menu.setLayout(null);
		l_menu_name.setBounds(15, 15, 100, 30);
		f_menu_name.setBounds(15, 50, 120, 30);
		b_menu_add.setBounds(150, 50, 90, 30);
		b_find_m.setBounds(250, 50, 60, 30);
		t_menu_area.setBounds(15, 90, 300, 200);
		t_menu_area.setBorder(new LineBorder(Color.gray, 2));
		b_menu_add.addActionListener(this);
		b_find_m.addActionListener(this);
		b_menu_add.setBackground(Color.WHITE);
		b_find_m.setBackground(Color.WHITE);
		tab_menu.add(l_menu_name);
		tab_menu.add(f_menu_name);
		tab_menu.add(b_menu_add);
		tab_menu.add(b_find_m);
		tab_menu.add(t_menu_area);
		// tab에 여러가지 패널 각각 부착
		tp.addTab("고객", tab_customer);
		tp.addTab("매출", tab_sales);
		tp.addTab("직원", tab_staff);
		tp.addTab("메뉴", tab_menu);
		sign_panel.add(tp, BorderLayout.CENTER);
		
		// grid_panel에 panel 부착
		grid_panel.add(table_panel);
		grid_panel.add(order_panel);
		grid_panel.add(menu_panel);
		grid_panel.add(sign_panel);
		
		// main_panel
		main_panel.setLayout(new BorderLayout());
		main_panel.add(title_panel, BorderLayout.NORTH);
		main_panel.add(grid_panel, BorderLayout.CENTER);
		
		// 프레임에 부착
		frame.setJMenuBar(bar); // JMenuBar
		frame.add(main_panel);
		
		// 프레임 설정
		frame.setTitle("식당 관리 시스템 (현재 관리자 : 없음)");
		frame.setBounds(0, 0, 700, 850);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		try {
			this.menuUpdate();
			this.checkDataIsOpen();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("최초 접속 : 파일 요구");
			JOptionPane.showMessageDialog(null, "최초 접속이므로 Schema를 불러와야 합니다.");
		}
	}

	
	
	
	// actionPerformed
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// 메뉴바
		if (e.getSource() == open) {
			if (dataLoaded == 1) {
				System.out.println("이미 data.txt를 불러왔습니다. (중복금지)");
			}
			
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("텍스트파일(.txt) 또는 SQL Schema(.sql)", "txt", "sql");
			chooser.setFileFilter(filter);
			int check = chooser.showOpenDialog(null);
			if (check != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "파일이 선택되지 않았습니다.", "경고", JOptionPane.WARNING_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(null, "파일이 선택되었습니다.");
				filePath = chooser.getSelectedFile().getPath();
				try {
					// 스키마 연결
					if (filePath.contains("pos_schema.sql")) {
						this.openSchema();
						JOptionPane.showMessageDialog(null,"Schema를 불러왔으므로 데이터를 불러와야 합니다.");
					}
					// 데이터 연결
					else {
						if (dataLoaded == 0) {
							this.openTXTtoSQL();
						}
						else {
							JOptionPane.showMessageDialog(null, "여러 번 불러올 수 없습니다.");
						}
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		else if (e.getSource() == login) {
			if (dataLoaded == 0) {
				JOptionPane.showMessageDialog(null, "데이터를 불러와야 로그인할 수 있습니다.");
			}
			else {
			new Login(db, this);
			f_customer_name.setText("");
			f_customer_name4.setText("");
			t_customer.setText("");
			t_sales_area.setText("");
			f_staff_name.setText("");
			t_staff_area.setText("");
			f_menu_name.setText("");
			t_menu_area.setText("");
			}
		}
		
		// 1. 테이블 현황 버튼
		else if (e.getSource() == tables[0]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("1");
		}
		else if (e.getSource() == tables[1]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("2");
		}
		else if (e.getSource() == tables[2]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("3");
		}
		else if (e.getSource() == tables[3]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("4");
		}
		else if (e.getSource() == tables[4]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("5");
		}
		else if (e.getSource() == tables[5]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("6");
		}
		else if (e.getSource() == tables[6]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("7");
		}
		else if (e.getSource() == tables[7]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("8");
		}
		else if (e.getSource() == tables[8]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("9");
		}
		else if (e.getSource() == tables[9]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("10");
		}
		else if (e.getSource() == tables[10]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("11");
		}
		else if (e.getSource() == tables[11]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("12");
		}
		else if (e.getSource() == tables[12]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("13");
		}
		else if (e.getSource() == tables[13]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("14");
		}
		else if (e.getSource() == tables[14]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("15");
		}
		else if (e.getSource() == tables[15]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("16");
		}
		else if (e.getSource() == tables[16]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("17");
		}
		else if (e.getSource() == tables[17]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("18");
		}
		else if (e.getSource() == tables[18]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("19");
		}
		else if (e.getSource() == tables[19]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("20");
		}
		
		
		
		// 2. 주문 내역 패널
		else if (e.getSource() == c_table_name) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == b_order) {
			if (t_order.getText().contains("<추가주문>")) {
			String table_num = (String) c_table_name.getSelectedItem();
			StringTokenizer st = new StringTokenizer(t_order.getText(), "\n");
			st.nextToken();
			while (st.hasMoreTokens()) {
				StringTokenizer st2 = new StringTokenizer(st.nextToken(), "\t");
				sql = "insert into orders values('"
						+ st2.nextToken() + "','" // 메뉴이름
						+ today + "'," // 날짜
						+ st2.nextToken() + "," // 가격
						+ table_num + "," // 테이블 번호
						+ "null," // 구매자 : 아직 입력 필요 없음
						+ "1)"; // 현재 테이블의 상태; 1:있음, 0:없음
				System.out.println(sql);
				try {
					stmt = db.prepareStatement(sql);
					stmt.executeUpdate();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					String showing = showTableOrder();
					t_order.setText(showing);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			int num = new Integer(table_num).intValue();
			tables[num-1].setBackground(Color.YELLOW);
			t_order_string = "<추가주문>\n";
			JOptionPane.showMessageDialog(null, "주문되었습니다.");
			}
		}
		else if (e.getSource() == b_cancle) {
			t_order_string = "<추가주문>\n";
			t_order.setText("");
			String table_num = (String)c_table_name.getSelectedItem();
			sql = "delete from orders where dates = '"
				+ today + "' and table_num = "
				+ table_num + " and status = 1";
			try {
				stmt = db.prepareStatement(sql);
				stmt.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int num = new Integer(table_num).intValue();
			tables[num-1].setBackground(Color.WHITE);
			JOptionPane.showMessageDialog(null, "모든 내역이 취소되었습니다.");
		}
		// 결제
		else if (e.getSource() == b_pay) {
			// 로그인 되어 있지 않을 경우 결제할 수 없도록 함
			if (currentStaff == null) {
				JOptionPane.showMessageDialog(null, "현재 사원 또는 관리자가 로그인 되어 있지 않아 결제할 수 없습니다.");
			}
			// 테이블에 주문내역이 없을 경우 결제 불가
			else if (!t_order.getText().contains("총 합계")) {
				JOptionPane.showMessageDialog(null, "현 테이블에 내역이 존재하지 않아 결제할 수 없습니다.");
			}
			else {
			String table_num = (String) c_table_name.getSelectedItem();
			String buyer = f_customer_name.getText();
			int sum_price = 0;
			String grade = "";
			if (buyer.equals("")) {
				buyer = "비회원";
			}
			
			// 결제하려는사람의고객명이이상한지체크해보기
			// customer의 이름들을 구해서 미리 확인
			sql = "select name from customer";
			boolean checkName = false;
			try {
				stmt = db.prepareStatement(sql);
				rs = stmt.executeQuery();
				while (rs.next()) {
					if (buyer.equals(rs.getString("name"))) {
						checkName = true;
					}
				}
			} catch (SQLException e3) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null,"목록을 불러올 수 없습니다.");
			}
			if (!checkName) {
				JOptionPane.showMessageDialog(null, "존재하지 않는 고객명입니다.\n가입하여 주시기 바랍니다.");
			}
			else {
			// 결제될 금액 구하기
			sql = "select sum(price) from orders where dates = '"
					+ today + "' and status = 1 and table_num = "
					+ table_num;
			try {
				stmt = db.prepareStatement(sql);
				rs = stmt.executeQuery();
				rs.next();
				sum_price = rs.getInt("sum(price)");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// 결제하려는 고객님의 등급을 체크
			if (!buyer.equals("비회원")) {
				sql = "select grade from customer where name = '"
					+ buyer + "'";
				String buyer_grade = "";
				try {
					stmt = db.prepareStatement(sql);
					rs = stmt.executeQuery();
					rs.next();
					buyer_grade = rs.getString("grade");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (buyer_grade.equals("Gold")) {
					sum_price = sum_price / 100 * 70;
				}
				else if (buyer_grade.equals("Silver")) {
					sum_price = sum_price / 100 * 80;
				}
				else if (buyer_grade.equals("Bronze")) {
					sum_price = sum_price / 100 * 90;
				}
				System.out.println("할인액" + buyer_grade);
			}
			
			// 현재 로그인 사원의 실적 추가하기
			sql = "update worker set record = record + "
					+ sum_price + "where name = '"
					+ currentStaff + "'";
			try {
				stmt = db.prepareStatement(sql);
				stmt.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// 고객님의 이름에 구매액 추가하기
			// 먼저, 고객님의 amount를 구해와서 등급 변경을 체크
			sql = "select amount from customer where name = '"
					+ buyer + "'";
			try {
				stmt = db.prepareStatement(sql);
				rs = stmt.executeQuery();
				rs.next();
				int amount = rs.getInt("amount");
				amount += sum_price;
				if (amount >= 1000000) {
					grade = "Gold";
				}
				else if (amount >= 500000) {
					grade = "Silver";
				}
				else if (amount >= 300000) {
					grade = "Bronze";
				}
				else {
					grade = "Normal";
				}
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			sql = "update customer set amount = amount + "
					+ sum_price + ", grade = '"
							+ grade + "' where name = '"
									+ buyer + "'";
			try {
				stmt = db.prepareStatement(sql);
				stmt.executeUpdate();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			// 테이블 상태 0으로 돌리기
			sql = "update orders set status = 0, buyer = '"
					+ buyer + "' where dates = '"
					+ today + "' and status = 1 and table_num = "
					+ table_num;
			try {
				stmt = db.prepareStatement(sql);
				stmt.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "결제되었습니다.");
			t_order.setText("결제되었습니다.");
			f_customer_name.setText("");
			int num = new Integer(table_num).intValue();
			tables[num-1].setBackground(Color.WHITE);
			}
			}
		}
		
		// 3. 메뉴 패널
		else if (e.getSource() == menus[0]) {
			menuAddOrder(menus[0]);
		}
		else if (e.getSource() == menus[1]) {
			menuAddOrder(menus[1]);
		}
		else if (e.getSource() == menus[2]) {
			menuAddOrder(menus[2]);
		}
		else if (e.getSource() == menus[3]) {
			menuAddOrder(menus[3]);
		}
		else if (e.getSource() == menus[4]) {
			menuAddOrder(menus[4]);
		}
		else if (e.getSource() == menus[5]) {
			menuAddOrder(menus[5]);
		}
		else if (e.getSource() == menus[5]) {
			menuAddOrder(menus[5]);
		}
		else if (e.getSource() == menus[6]) {
			menuAddOrder(menus[6]);
		}
		else if (e.getSource() == menus[7]) {
			menuAddOrder(menus[7]);
		}
		else if (e.getSource() == menus[8]) {
			menuAddOrder(menus[8]);
		}
		else if (e.getSource() == menus[9]) {
			menuAddOrder(menus[9]);
		}
		else if (e.getSource() == menus[10]) {
			menuAddOrder(menus[10]);
		}
		else if (e.getSource() == menus[11]) {
			menuAddOrder(menus[11]);
		}
		else if (e.getSource() == menus[12]) {
			menuAddOrder(menus[12]);
		}
		else if (e.getSource() == menus[13]) {
			menuAddOrder(menus[13]);
		}
		else if (e.getSource() == menus[14]) {
			menuAddOrder(menus[14]);
		}
		else if (e.getSource() == menus[15]) {
			menuAddOrder(menus[15]);
		}
		else if (e.getSource() == menus[16]) {
			menuAddOrder(menus[16]);
		}
		else if (e.getSource() == menus[17]) {
			menuAddOrder(menus[17]);
		}
		else if (e.getSource() == menus[18]) {
			menuAddOrder(menus[18]);
		}
		else if (e.getSource() == menus[19]) {
			menuAddOrder(menus[19]);
		}
		
		// 4. 등록/조회 패널
		// 4-1. 고객패널
		else if (e.getSource() == b_sign) {
			if (authority == null) {
				JOptionPane.showMessageDialog(null, "먼저 로그인 하여 주시기 바랍니다.");
			}
			else if (authority.equals("Supervisor")) {
				new AddCustomer(db);
			}
			else if (authority.equals("Staff")) {
				JOptionPane.showMessageDialog(null, "Staff는 등록 권한이 없습니다.");
			}
		}
		else if (e.getSource() == b_find) {
			try {
				this.findCustomer();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// 4-2. 매출패널
		else if (e.getSource() == c_date) {
			if (authority.equals("Supervisor")) {
				String result = "";
				String selectedDate = (String) c_date.getSelectedItem();
				// SQL - 일 매출 구하기
				sql = "select sum(price) from orders where dates = '"
						+ selectedDate + "' and status = 0";
				System.out.println(sql);
				if (selectedDate != null) {
				try {
					stmt = db.prepareStatement(sql);
					rs = stmt.executeQuery();
					rs.next();
					result += "일 매출 : " + rs.getString("sum(price)");
					result += "\n------------------------------\n";
					
					// SQL - 가장 많이 팔린 메뉴 구하기
					sql = "with count_menu(menu, value) as "
							+ "(select menu, count(menu) "
							+ "from orders "
							+ "where status = 0 "
							+ "group by menu) "
							+ "select menu from count_menu where value = "
							+ "(select max(value) from count_menu)";
					stmt = db.prepareStatement(sql);
					rs = stmt.executeQuery();
					rs.next();
					result += "가장 많이 팔린 메뉴\n: " + rs.getString("menu") + "\n\n";
					System.out.println(sql);
					
					// SQL - 가장 적게 팔린 메뉴 구하기
					sql = "with count_menu(menu, value) as "
							+ "(select menu, count(menu) "
							+ "from orders "
							+ "where status = 0 "
							+ "group by menu) "
							+ "select menu from count_menu where value = "
							+ "(select min(value) from count_menu)";
					stmt = db.prepareStatement(sql);
					rs = stmt.executeQuery();
					rs.next();
					result += "가장 적게 팔린 메뉴\n: " + rs.getString("menu") + "\n";
					System.out.println(sql);
					
					result += "------------------------------\n";
					// SQL - 선택 날짜 전까지의 누적 매출
					sql = "select sum(price) from orders where dates <= '"
							+ selectedDate + "'";
					stmt = db.prepareStatement(sql);
					rs = stmt.executeQuery();
					rs.next();
					result += "누적 매출 : " + rs.getString("sum(price)");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 매출 탭 부분 보이게 설정하기
				t_sales_area.setText(result);
				}
			}
			else if (authority.equals("Staff")) {
				JOptionPane.showMessageDialog(null, "Staff는 매출 내역을 볼 수 없습니다.");
			}
		}
		
		// 4-3. 직원패널
		else if (e.getSource() == b_add_staff) {
			if (authority == null) {
				JOptionPane.showMessageDialog(null, "먼저 로그인 하여 주시기 바랍니다.");
			}
			else if (authority.equals("Supervisor")) {
				new AddWorker(db);
			}
			else if (authority.equals("Staff")) {
				JOptionPane.showMessageDialog(null, "Staff는 등록 권한이 없습니다.");
			}
		}
		else if (e.getSource() == b_find_staff) {
			try {
				this.findStaff();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// 4-4. 메뉴패널
		else if (e.getSource() == b_menu_add) {
			sql = "select count(menu) from menu";
			try {
				stmt = db.prepareStatement(sql);
				rs = stmt.executeQuery();
				rs.next();
				int num = rs.getInt("count(menu)");
				if (num == 20) {
					JOptionPane.showMessageDialog(null, "이미 메뉴가 20개 등록되어 있습니다.");
				}
				else {
					if (authority == null) {
						JOptionPane.showMessageDialog(null, "먼저 로그인 하여 주시기 바랍니다.");
					}
					else if (authority.equals("Supervisor")) {
						new AddMenu(db, this);
					}
					else if (authority.equals("Staff")) {
						JOptionPane.showMessageDialog(null, "Staff는 등록 권한이 없습니다.");
					}	
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
		}
		else if (e.getSource() == b_find_m) {
			try {
				this.findMenu();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
	
	
	// 메소드들
	
	// 스키마 불러오기
	public void openSchema() throws SQLException, FileNotFoundException {
		File file = new File(filePath);
		FileReader fr = new FileReader(file);
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(fr);
		String s = new String();
		try {
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			br.close();
			
			String[] inst = sb.toString().split(";");
			
			Statement st = db.createStatement();
			
			for (int i = 0; i < inst.length; i++)
			{
				if (!inst[i].trim().equals(""))
				{
					st.executeUpdate(inst[i]);
					System.out.println(">>"+inst[i]);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 불러온 데이터를 SQL에 저장한다
	public void openTXTtoSQL() throws SQLException {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filePath));
			// 고객 데이터 읽어오기
			String read = br.readLine();
			int line_num = new Integer(read).intValue();
			String tok = "";
			for (int i = 0; i < line_num; i++) {
				read = br.readLine();
				tok = tok + read +"\t"; // tab으로 토큰가능
			}
			StringTokenizer st = new StringTokenizer(tok, "\t");
			for (int i = 0; i < line_num; i++) {
				// customer의 id 확장시키기
				sql = "select customer_id from customer";
				stmt = db.prepareStatement(sql);
				rs = stmt.executeQuery();
				while (rs.next()) {
					customer_id = rs.getInt("customer_id");
				}
				customer_id++;
				String name = st.nextToken();
				String born = st.nextToken();
				String phone = st.nextToken();
				String grade = st.nextToken();
				String amount = "0";
				// customer의 amount 정하기
				if (grade.equals("Gold")) {
					amount = "1000000";
				}
				else if (grade.equals("Silver")) {
					amount = "500000";
				}
				else if (grade.equals("Bronze")) {
					amount = "300000";
				}
				// 저장하기
				sql = "insert into customer values('"
						+ name + "'," // name
						+ customer_id + ",'" // customer_id
						+ born + "'," // born
						+ phone + ",'" // phone
						+ grade + "'," // grade
						+ amount + ")"; // amount
				stmt = db.prepareStatement(sql);
				stmt.executeUpdate();
			}
			// 직원 데이터 읽어오기
			read = br.readLine();
			line_num = new Integer(read).intValue();
			tok = "";
			int worker_id = 1001;
			for (int i = 0; i < line_num; i++) {
				read = br.readLine();
				tok = tok + read + '\t';
			}
			st = new StringTokenizer(tok, "\t");
			for (int i = 0; i < line_num; i++) {
				sql = "insert into worker values('"
						+ st.nextToken() + "',"
						+ worker_id + ",'"
						+ st.nextToken() + "',"
						+ "0)";
				stmt = db.prepareStatement(sql);
				stmt.executeUpdate();
				worker_id++;
			}
			// 메뉴 데이터 읽어오기
			read = br.readLine();
			line_num = new Integer(read).intValue();
			tok = "";
			for (int i = 0; i < line_num; i++) {
				read = br.readLine();
				tok = tok + read + '\t';
			}
			st = new StringTokenizer(tok, "\t");
			for (int i = 0; i < line_num; i++) {
				// menu의 id 확장시키기
				sql = "select menu_id from menu";
				stmt = db.prepareStatement(sql);
				rs = stmt.executeQuery();
				while (rs.next()) {
					menu_id = rs.getInt("menu_id");
				}
				menu_id++;
				sql = "insert into menu values('"
						+ st.nextToken() + "','" // 메뉴
						+ menu_id + "'," // 메뉴id
						+ st.nextToken() + ")"; // 가격
				stmt = db.prepareStatement(sql);
				stmt.executeUpdate();
			}
			// 완료
			JOptionPane.showMessageDialog(null, "모두 저장되었습니다.");
			this.menuUpdate();
			this.dataLoaded = 1; // 데이터체크
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	// 메뉴버튼 업데이트
	public void menuUpdate() throws SQLException {
		sql = "select menu from menu";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		for (int i = 0; rs.next(); i++) {
			menus[i].setText(rs.getString("menu"));
		}
	}
	
	// 고객조회
	public void findCustomer() throws SQLException {
		boolean checkName = false;		
		// customer의 이름들을 구해서 미리 확인
		sql = "select name from customer";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		String name = f_customer_name4.getText().replaceAll("\\s","");
		while (rs.next()) {
			if (name.equals(rs.getString("name"))) {
				checkName = true;
			}
		}
		if (f_customer_name4.getText().equals("")) {
			t_customer.setText("아무것도 입력하지 않았습니다.");
		}
		else if (checkName) {
			sql = "select * from customer where name = '"
					+ f_customer_name4.getText() + "'";
			stmt = db.prepareStatement(sql);
			rs = stmt.executeQuery();
			System.out.println(sql);
			rs.next();
			String result = "";
			result += "고객명 : " + rs.getString("name") + "\n";
			result += "생년월일 : " + rs.getString("born") + "\n";
			result += "전화번호 : " + rs.getString("phone") + "\n";
			result += "고객등급 : " + rs.getString("grade") + "\n";
			result += "총 구매금액 : " + rs.getString("amount") + "\n";
			t_customer.setText(result);
		}
		else {
			t_customer.setText("검색 결과 없음");
		}
	}
	
	// 메뉴 조회
	public void findMenu() throws SQLException {
		boolean checkName = false;
		// 메뉴이름 미리 구하기
		sql = "select menu from menu";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		String menu = f_menu_name.getText().replaceAll("\\s","");
		while (rs.next()) {
			if (menu.equals(rs.getString("name"))) {
				checkName = true;
			}
		}
		if (f_menu_name.getText().equals("")) {
			t_menu_area.setText("아무것도 입력하지 않았습니다.");
		}
		else if (checkName) {
			sql = "select * from menu where menu = '"
					+ f_menu_name.getText() + "'";
			stmt = db.prepareStatement(sql);
			rs = stmt.executeQuery();
			System.out.println(sql);
			rs.next();
			String result = "";
			result += "메뉴명 : " + rs.getString("menu") + "\n";
			result += "가격 : " + rs.getString("price") + "\n";
			t_menu_area.setText(result);
		}
		else {
			t_menu_area.setText("검색 결과 없음");
		}
	}
	
	// 직원이름찾기
	public void findStaff() throws SQLException {
		boolean checkName = false;
		// 직원이름 미리 구하기
		sql = "select name from worker";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		String name = f_staff_name.getText().replaceAll("\\s","");
		while (rs.next()) {
			if (name.equals(rs.getString("name"))) {
				checkName = true;
			}
		}
		if (f_staff_name.getText().equals("")) {
			t_staff_area.setText("아무것도 입력하지 않았습니다.");
		}
		else if (checkName) {
			sql = "select * from worker where name = '"
					+ f_staff_name.getText() + "'";
			stmt = db.prepareStatement(sql);
			rs = stmt.executeQuery();
			System.out.println(sql);
			rs.next();
			String result = "";
			result += "직원명 : " + rs.getString("name") + "\n";
			result += "직급 : " + rs.getString("grade") + "\n";
			result += "총실적 : " + rs.getString("record") + "\n";
			t_staff_area.setText(result);
		}
		else {
			t_staff_area.setText("검색 결과 없음");
		}
	}
	
	// 매출 아이템 선정하기
	public void getSalesItem() throws SQLException {
		sql = "select distinct dates from orders where status = 0 order by dates";
		System.out.println(sql);
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		while (rs.next()) {
			c_date.addItem(rs.getString("dates").substring(0, 10));
		}
		c_date.addActionListener(this);
	}
	 
	// 메뉴 버튼 눌러서 내역 추가하기
	public void menuAddOrder(JButton menu_button) {
		if (!menu_button.getText().equals("")) {
		try {
			sql = "select menu, price from menu where menu = '"
					+ menu_button.getText() + "'";
			System.out.println(sql);
			stmt = db.prepareStatement(sql);
			rs = stmt.executeQuery();
			rs.next();
			t_order_string += rs.getString("menu") + '\t' + rs.getString("price") + "\n";
			t_order.setText(t_order_string);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
	}
	
	// 테이블 선택 시에 보여주기
	public String showTableOrder() throws SQLException {
		String result = "";
		String table_num = (String)c_table_name.getSelectedItem();
		System.out.println(table_num);
		sql = "select menu, price from orders where dates = '"
				+ today + "' and table_num = " + table_num
				+ " and status = 1";
		System.out.println(sql);
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		while (rs.next()) {
			result += rs.getString("menu") + "\t" 
					+ rs.getString("price") + "\n";
		}
		result += "\n------------------------------\n";
		sql = "select sum(price) from orders where dates = '"
				+ today + "' and table_num = " + table_num
				+ " and status = 1";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		rs.next();
		result += "총 합계\t" + rs.getString("sum(price)");
		if (rs.getString("sum(price)") == null) {
			result = "내역이 존재하지 않습니다.";
		}
		return result;
	}
	
	public void checkDataIsOpen() {
		try {
			sql = "select * from menu";
			stmt = db.prepareStatement(sql);
			rs = stmt.executeQuery();
			rs.next();
			String check = rs.getString("menu");
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "데이터를 불러온 적이 없습니다.");
			e.printStackTrace();
		}
	}
}
