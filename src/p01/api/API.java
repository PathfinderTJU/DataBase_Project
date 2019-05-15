package p01.api;

import java.sql.*;
import java.util.*;

public class API {
	
	public static final String URL = "jdbc:mysql://localhost:3306/course?serverTimezone=GMT";
	public static final String USER = "root";
	public static final String PASS = "";
	public static Connection conn = null;
	
	public API() {
		
	}
	
	public void connect() {
		try {
			System.out.println("Connectioning to database");
		
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection(URL, USER, PASS);
			
			System.out.println("Connect successfully.");
		}catch(Exception e) {
			System.out.println("Connect failed!");
			e.printStackTrace();
		}
	}
	
	//插入
	//插入学生
	public boolean insert_student(String[] fields) throws Exception{
		
		boolean flag = false;
		
		if (fields.length != 6) {
			System.out.println("The number of arguments is illegal.");
			return flag;
		}
		
		if (fields[0].charAt(0) == '0') {
			System.out.println("The sid first number can't be 0");
			return flag;
		}
		
		if (fields[0].length() != 10) {
			System.out.println("Student's sid is illegal.");
			return flag;
		}
		
		if (!fields[2].equals("man") && !fields[2].equals("women")) {
			System.out.println("Student's gender is illegal.");
			return flag;
		}
		
		if (Integer.valueOf(fields[3]) < 10 || Integer.valueOf(fields[3]) > 50) {
			System.out.println("Student's age is illegal.");
			return flag;
		}
		
		String SQL = "INSERT INTO Student VALUES(?, ?, ?, ?, ?, ?)";
		
		PreparedStatement ptmt = conn.prepareStatement(SQL);
		
		ptmt.setString(1, fields[0]);
		ptmt.setString(2, fields[1]);
		ptmt.setString(3, fields[2]);
		ptmt.setInt(4, Integer.valueOf(fields[3]));
		ptmt.setInt(5, Integer.valueOf(fields[4]));
		ptmt.setInt(6, Integer.valueOf(fields[5]));
		
		ptmt.execute();
		
		flag = true;
		System.out.println("Insert successfully.");
		return flag;
	}
	
	//插入课程
	public boolean insert_course(String[] fields) throws Exception{
		
		boolean flag = false;
		
		if (fields.length != 6) {
			System.out.println("The number of arguments is illegal.");
			return flag;
		}
		
		if (fields[0].charAt(0) == '0') {
			System.out.println("The cid first number can't be 0");
			return flag;
		}
		
		if (fields[0].length() != 7) {
			System.out.println("Course's cid is illegal.");
			return flag;
		}
		
		String SQL = "INSERT INTO Course VALUES(?, ?, ?, ?, ?, ?)";
		
		PreparedStatement ptmt = conn.prepareStatement(SQL);
		
		ptmt.setInt(1, Integer.valueOf(fields[0]));
		ptmt.setString(2, fields[1]);
		ptmt.setString(3, fields[2]);
		ptmt.setInt(4, Integer.valueOf(fields[3]));
		ptmt.setInt(5, Integer.valueOf(fields[4]));
		ptmt.setInt(6, Integer.valueOf(fields[5]));
		
		ptmt.execute();
		
		flag = true;
		System.out.println("Insert successfully.");
		return flag;
	}
	
	//插入选课记录
	public boolean insert_record(String[] fields) throws Exception{
		
		boolean flag = false;
		
		if (fields.length != 4) {
			System.out.println("The number of arguments is illegal.");
			return flag;
		}
		
		if (fields[0].length() != 10) {
			System.out.println("Record's sid is illegal.");
			return flag;
		}
		
		if (fields[1].length() != 7) {
			System.out.println("Record's cid is illegal.");
			return flag;
		}
		
		String sid = fields[0];
		String cid = fields[1];
		int time = Integer.valueOf(fields[2]); 
		
		String[] student = search_student_basic("sid", sid).get(0);
		String[] course = search_course_basic("cid",cid).get(0);
		
		int student_grade = Integer.valueOf(student[4]);
		int course_suit_grade = Integer.valueOf(course[4]);
		int course_cancel_year = Integer.valueOf(course[5]);
		
		if (student_grade < course_suit_grade) {
			System.out.println("Student's grade is lower than course's suitable grade.");
			throw new IndexOutOfBoundsException();
		}
		
		if (time >= course_cancel_year && course_cancel_year != -1) {
			System.out.println("Course has been canceled since " + course_cancel_year + ".");
			throw new IndexOutOfBoundsException();
		}
		
		String SQL = "INSERT INTO Record VALUES(?, ?, ?, ?)";
		
		PreparedStatement ptmt = conn.prepareStatement(SQL);
			
		ptmt.setString(1, fields[0]);
		ptmt.setString(2, fields[1]);
		ptmt.setInt(3, Integer.valueOf(fields[2]));
		ptmt.setInt(4, Integer.valueOf(fields[3]));
			
		ptmt.execute();
			
		flag = true;
		System.out.println("Insert successfully.");
		return flag;
		
	}
	
	//删除
	//删除学生
	public boolean delete_student(String sid) throws Exception{
		
		boolean flag = false;
		
		List<String[]> exits = search_student_basic("sid", sid);
		
		if (exits.isEmpty()) {
			System.out.println("sid is not exists");
			throw new IllegalArgumentException();
		}
		
		String SQL = "DELETE FROM Student WHERE sid = ?";
		
		PreparedStatement ptmt = conn.prepareStatement(SQL);
		
		ptmt.setString(1, sid);
		
		ptmt.execute();
		
		flag = true;
		System.out.println("Delete successfully.");
		return flag;
	}
	
	//删除课程
	public boolean delete_course(String cid) throws Exception{
		
		boolean flag = false;
		
		List<String[]> exits = search_course_basic("cid", cid);
		
		if (exits.isEmpty()) {
			System.out.println("sid is not exists");
			throw new IllegalArgumentException();
		}
		
		String SQL = "DELETE FROM Course WHERE cid = ?";
		
		PreparedStatement ptmt = conn.prepareStatement(SQL);
		
		ptmt.setString(1, cid);
		
		ptmt.execute();
		flag = true;
		System.out.println("Delete successfully.");
		return flag;
	}
	
	//删除选课记录
	public boolean delete_record(String sid, String cid, String time) throws Exception{
		
		boolean flag = false;
		
		List<String[]> exits = search_student_course_grade("sid", sid, "cid", cid);
		
		if (exits.isEmpty()) {
			System.out.println("sid is not exists");
			throw new IllegalArgumentException();
		}
		
		String SQL = "DELETE FROM Record WHERE sid = ? AND cid = ? AND time = ?";
		
		PreparedStatement ptmt = conn.prepareStatement(SQL);
		
		ptmt.setString(1, sid);
		ptmt.setString(2, cid);
		ptmt.setInt(3, Integer.valueOf(time));
		
		ptmt.execute();
		
		flag = true;
		System.out.println("Delete successfully.");
		return flag;
	}
	
	//更新
	//更新学生的某一个非主键属性
	public boolean update_student(String sid, String fieldName, String value) throws Exception{
		
		boolean flag = false;
		
		if (fieldName.equals("sid")){
			System.out.println("You can't update main key.");
			return flag;
		}
		
		List<String[]> exits = search_student_basic("sid", sid);
		
		if (exits.isEmpty()) {
			System.out.println("sid is not exists");
			throw new IllegalArgumentException();
		}
		
		String SQL = "UPDATE Student SET " + fieldName + " = ? WHERE sid = ?";
		
		PreparedStatement stmt = conn.prepareStatement(SQL);
		
		stmt.setString(1, value);
		stmt.setString(2, sid);
		
		stmt.execute();
		
		flag = true;
		System.out.println("Update successfully.");
		return flag;
	}
	
	//更新课程的某一个非主键属性
	public boolean update_course(String cid, String fieldName, String value) throws Exception{
		
		boolean flag = false;
		
		if (fieldName.equals("cid")){
			System.out.println("You can't update main key.");
			return flag;
		}
		
		List<String[]> exits = search_course_basic("cid", cid);
		
		if (exits.isEmpty()) {
			System.out.println("sid is not exists");
			throw new IllegalArgumentException();
		}
		
		String SQL = "UPDATE Course SET " + fieldName + " = ? WHERE cid = ?";

		PreparedStatement stmt = conn.prepareStatement(SQL);
		
		stmt.setString(1, value);
		stmt.setString(2, cid);
		
		stmt.execute();
		
		flag = true;
		System.out.println("Update successfully.");
		return flag;
	}
	
	//更新选课记录的某一个非主键属性
	public boolean update_record(String sid, String cid, String time, String value) throws Exception{
		
		boolean flag = false;
		
		List<String[]> exits = search_student_course_grade("sid", sid, "cid", cid);
		
		if (exits.isEmpty()) {
			System.out.println("sid is not exists");
			throw new IllegalArgumentException();
		}
		
		String SQL = "UPDATE Record SET grade = ? WHERE sid = ? AND cid = ? AND time = ?";
		
		PreparedStatement stmt = conn.prepareStatement(SQL);
		
		stmt.setString(1, value);
		stmt.setString(2, sid);
		stmt.setString(3, cid);
		stmt.setInt(4, Integer.valueOf(time));
		
		stmt.execute();
		
		flag = true;
		System.out.println("Update successfully.");
		return flag;
	}
	
	//学生信息查询
	//根据学生sid或sname查询学生的基本信息，包括学生的7个属性
	public List<String[]> search_student_basic(String fieldName, String value){
		String SQL;
		
		if (fieldName.equals("sid")) {
			SQL = "SELECT * FROM Student WHERE sid = \"" + value + "\"";
		}else if (fieldName.equals("sname")) {
			SQL = "SELECT * FROM Student WHERE sname = \"" + value + "\"";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_sid = result.getString("sid");
				String result_sname = result.getString("sname");
				String result_gender = result.getString("gender");
				Integer result_age = result.getInt("age");
				Integer result_grade = result.getInt("grade");
				Integer result_class = result.getInt("class");
				String[] temp = {result_sid, result_sname, result_gender, 
								 result_age.toString(), result_grade.toString(), 
								 result_class.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//根据学生sid或sname查询学生的选课记录，包括学生的7个属性+课程cid、cname、teacher、point4个属性+选课时间time
	public List<String[]> search_student_chosen(String fieldName, String value){
		String SQL;
		
		if (fieldName.equals("sid")) {
			SQL = "SELECT S.*, C.cid, C.cname, C.teacher, C.point, R.time FROM Student S, Record R, Course C WHERE S.sid = \"" + value + "\" AND R.sid = S.sid AND R.cid = C.cid";
		}else if (fieldName.equals("sname")) {
			SQL = "SELECT S.*, C.cid, C.cname, C.teacher, C.point, R.time FROM Student S, Record R, Course C WHERE S.sname = \"" + value + "\" AND R.sid = S.sid AND R.cid = C.cid";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_sid = result.getString("sid");
				String result_sname = result.getString("sname");
				Integer result_grade = result.getInt("grade");
				Integer result_class = result.getInt("class");
				String result_cid = result.getString("cid");
				String result_cname = result.getString("cname");
				String result_teacher = result.getString("teacher");
				Integer result_point = result.getInt("point");
				Integer result_time = result.getInt("time");
				String[] temp = {result_sid, result_sname,
								result_grade.toString(), 
								result_class.toString(), result_cid, result_cname, 
								result_teacher, result_point.toString(), result_time.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//查询学生的全部信息，包括课程的6个属性
	public List<String[]> search_all_student() {
		String SQL = "SELECT * FROM Student";
	
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_sid = result.getString("sid");
				String result_sname = result.getString("sname");
				String result_gender = result.getString("gender");
				Integer result_age = result.getInt("age");
				Integer result_grade = result.getInt("grade");
				Integer result_class = result.getInt("class");
				String[] temp = {result_sid, result_sname, result_gender, 
								 result_age.toString(), result_grade.toString(), 
								 result_class.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}

	//学生成绩查询
	//根据学生sid或sname以及课程cid或cname和查询学生的课程成绩，包括学生sid，sname，grade，class4个属性+课程cid、cname、teacher、point3各属性+选课时间time+成绩grade
	public List<String[]> search_student_course_grade(String studentFieldName, String studentValue, String courseFieldName, String courseValue){
		String SQL = "SELECT S.sid, S.sname, S.grade, S.class, C.cid, C.cname, C.teacher, C.point, R.time, R.grade AS score FROM Student S, Record R, Course C WHERE ";
		
		if (studentFieldName.equals("sid")) {
			SQL = SQL + "S.sid = \"" + studentValue + "\" AND ";
		}else if (studentFieldName.equals("sname")) {
			SQL = SQL + "sname = \"" + studentValue + "\" AND ";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		if (courseFieldName.equals("cid")) {
			SQL = SQL + "C.cid = \"" + courseValue + "\" AND ";
		}else if (courseFieldName.equals("cname")) {
			SQL = SQL + "cname = \"" + courseValue + "\" AND ";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		SQL = SQL + "S.sid = R.sid AND R.cid = C.cid";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_sid = result.getString("sid");
				String result_sname = result.getString("sname");
				String result_cid = result.getString("cid");
				String result_cname = result.getString("cname");
				String result_teacher = result.getString("teacher");
				Integer result_point = result.getInt("point");
				Integer result_time = result.getInt("time");
				Integer result_score = result.getInt("score");
				String[] temp = {result_sid, result_sname, 
								 result_cid, result_cname, result_teacher,
								 result_point.toString(), result_time.toString(), result_score.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//根据学生sid或sname查询学生的全部课程成绩，包括学生sid，sname，grade，class4个属性+课程cid、cname、teacher、point3各属性+选课时间time+成绩grade
	public List<String[]> search_student_all_grade(String fieldName, String value){
		String SQL = "SELECT S.sid, S.sname, S.grade, S.class, C.*, R.time, R.grade AS score FROM Student S, Record R, Course C WHERE ";
		
		if (fieldName.equals("sid")) {
			SQL = SQL + "S.sid = \"" + value + "\" AND ";
		}else if (fieldName.equals("sname")) {
			SQL = SQL + "sname = \"" + value + "\" AND ";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		SQL = SQL + "S.sid = R.sid AND R.cid = C.cid";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_sid = result.getString("sid");
				String result_sname = result.getString("sname");
				Integer result_grade = result.getInt("grade");
				Integer result_class = result.getInt("class");
				String result_cid = result.getString("cid");
				String result_cname = result.getString("cname");
				String result_teacher = result.getString("teacher");
				Integer result_point = result.getInt("point");
				Integer result_time = result.getInt("time");
				String result_suit_year = result.getString("suit_grade");
				String result_cancel_year = result.getString("cancel_year");
				Integer result_score = result.getInt("score");
				String[] temp = {result_sid, result_sname, result_grade.toString(), 
								 result_class.toString(), result_cid, result_cname, result_teacher,
								 result_point.toString(), result_time.toString(), 
								 result_suit_year, result_cancel_year, result_score.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//根据学生sid或sname查询学生所有科目的平均成绩,包括学生sid,sname,grade,class4个属性+平均成绩avg
	public List<String[]> search_student_average_grade(String fieldName, String value){
		String SQL = "SELECT S.sid, S.sname, S.grade, S.class, AVG(R.grade) AS average FROM Student S, Record R WHERE ";
		
		if (fieldName.equals("sid")) {
			SQL += "S.sid = \"" + value + "\" AND ";
		}else if (fieldName.endsWith("sname")) {
			SQL += "S.sname = \"" + value + "\" AND ";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		SQL += "S.sid = R.sid AND R.grade >= 0 GROUP BY S.sid";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			System.out.println(SQL);
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_sid = result.getString("S.sid");
				String result_sname = result.getString("sname");
				Integer result_grade = result.getInt("grade");
				Integer result_class = result.getInt("class");;
				Integer result_average = result.getInt("average");
				String[] temp = {result_sid, result_sname, result_grade.toString(), 
								result_class.toString(), result_average.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//班级成绩查询
	//根据年级grade、班级class和课程cid或cname查询讯班级课程的平均成绩，包括班级grade+class+课程cid、cname、teacher、point3个属性+选课时间+平均成绩avg
	public List<String[]> search_class_course_average(String gradeValue, String classValue, String fieldName, String value){
		String SQL = "SELECT S.grade, S.class, C.cid, C.cname, C.teacher, C.point, R.time, AVG(R.grade) AS average "
			       + "FROM Student S, Course C, Record R "
			       + "WHERE S.class = \"" + classValue +"\" AND S.grade = \"" + gradeValue + "\" AND ";
		
		if (fieldName.equals("cid")) {
			SQL += "C.cid = \"" + value + "\" AND ";
		}else if (fieldName.equals("cname")) {
			SQL += "C.cname = \"" + value + "\" AND ";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		SQL += "S.sid = R.sid AND R.cid = C.cid GROUP BY S.class";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);

			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				Integer result_grade = result.getInt("grade");
				Integer result_class = result.getInt("class");
				String result_cid = result.getString("cid");
				String result_cname = result.getString("cname");
				String result_teacher = result.getString("teacher");
				Integer result_point = result.getInt("point");
				Integer result_time = result.getInt("time");
				Integer result_average = result.getInt("average");
				String[] temp = {result_grade.toString(), result_class.toString(), result_cid, 
							     result_cname, result_teacher, result_point.toString(), 
								 result_time.toString(), result_average.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//根据年级grade、班级class和课程cid或cname查询班级课程的全部成绩，包括sid、sname、grade、class、cid、cname、teacher、point、time、grade
	public List<String[]> search_class_course_all(String gradeValue, String classValue, String fieldName, String value){
		String SQL = "SELECT S.sid, S.sname, S.grade, S.class, "
				   		  + "C.cid, C.cname, C.teacher, C.point, R.time, R.grade AS score "
				   + "FROM Student S, Course C, Record R "
				   + "WHERE S.class = \"" + classValue +"\" AND S.grade = \"" + gradeValue + "\" AND ";
		
		if (fieldName.equals("cid")) {
			SQL += "C.cid = \"" + value + "\" AND ";
		}else if (fieldName.equals("cname")) {
			SQL += "C.cname = \"" + value + "\" AND ";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		SQL += "S.sid = R.sid AND R.cid = C.cid";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);

			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_sid = result.getString("sid");
				String result_sname = result.getString("sname");
				Integer result_grade = result.getInt("grade");
				Integer result_class = result.getInt("class");
				String result_cid = result.getString("cid");
				String result_cname = result.getString("cname");
				String result_teacher = result.getString("teacher");
				Integer result_point = result.getInt("point");
				Integer result_time = result.getInt("time");
				Integer result_score = result.getInt("score");
				String[] temp = {result_sid, result_sname, result_grade.toString(),
								 result_class.toString(), result_cid, result_cname, 
								 result_teacher, result_point.toString(), 
								 result_time.toString(), result_score.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//根据年级grade、班级class查询班级内同学的全部平均成绩，包括sid、sname、average
	public List<String[]> search_class_average_all(String gradeValue, String classValue){
		String SQL = "SELECT S.sid, S.sname, AVG(R.grade) AS average "
		           + "FROM Student S, Course C, Record R "
		           + "WHERE S.class = \"" + classValue +"\" AND S.grade = \"" + gradeValue + "\" AND ";
		
		SQL += "S.sid = R.sid AND R.cid = C.cid GROUP BY S.sid";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
		
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			System.out.println(result.toString());
			int i = 0;
			while(result.next()) {
				String result_sid = result.getString("sid");
				String result_sname = result.getString("sname");
				Integer result_average = result.getInt("average");
				String[] temp = {result_sid, result_sname, result_average.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//课程信息查询
	//根据课程cid或cname查询课程的基本信息，包括课程的6个属性
	public List<String[]> search_course_basic(String fieldName, String value) {
		String SQL;
		
		if (fieldName.equals("cid")) {
			SQL = "SELECT * FROM Course WHERE cid = \"" + value + "\"";
		}else if (fieldName.equals("cname")) {
			SQL = "SELECT * FROM Course WHERE cname = \"" + value + "\"";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_cid = result.getString("cid");
				String result_cname = result.getString("cname");
				String result_teacher = result.getString("teacher");
				Integer result_point = result.getInt("point");
				Integer result_suit_grade = result.getInt("suit_grade");
				Integer result_cancel_year = result.getInt("cancel_year");
				String[] temp = {result_cid, result_cname, result_teacher, 
								 result_point.toString(), result_suit_grade.toString(), 
								 result_cancel_year.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//根据课程cid或cname查询课程的被选记录，包括课程的6个属性+选课时间time+学生的sid、sname、grade、class
	public List<String[]> search_course_chosen(String fieldName, String value) {
		String SQL;
		
		if (fieldName.equals("cid")) {
			SQL = "SELECT C.*, R.time, S.sid, S.sname, S.grade, S.class FROM Student S, Record R, Course C WHERE C.cid = \"" + value + "\" AND R.cid = C.cid AND R.sid = S.sid";
		}else if (fieldName.equals("cname")) {
			SQL = "SELECT C.*, R.time, S.sid, S.sname, S.grade, S.class FROM Student S, Record R, Course C WHERE C.cname = \"" + value + "\" AND R.cid = C.cid AND R.sid = S.sid";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_cid = result.getString("cid");
				String result_cname = result.getString("cname");
				String result_teacher = result.getString("teacher");
				Integer result_point = result.getInt("point");
				Integer result_suit_grade = result.getInt("suit_grade");
				Integer result_cancel_year = result.getInt("cancel_year");
				Integer result_time = result.getInt("time");
				String result_sid = result.getString("sid");
				String result_sname = result.getString("sname");
				Integer result_grade = result.getInt("grade");
				Integer result_class = result.getInt("class");
				String[] temp = {result_cid, result_cname, result_teacher,
								 result_point.toString(), result_suit_grade.toString(),
								 result_cancel_year.toString(), result_time.toString(),
								 result_sid, result_sname, result_grade.toString(), result_class.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//查询全部课程的基本信息，包括课程的6个属性
	public List<String[]> search_all_course() {
		String SQL = "SELECT * FROM Course";
	
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_cid = result.getString("cid");
				String result_cname = result.getString("cname");
				String result_teacher = result.getString("teacher");
				Integer result_point = result.getInt("point");
				Integer result_suit_grade = result.getInt("suit_grade");
				Integer result_cancel_year = result.getInt("cancel_year");
				String[] temp = {result_cid, result_cname, result_teacher, 
								 result_point.toString(), result_suit_grade.toString(), 
								 result_cancel_year.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//课程成绩查询
	//根据课程cid或cname查询课程的成绩表，包括课程的cid、cname、teacher、point+选课时间time+学生sid、sname、grade、class + 成绩grade
	public List<String[]> search_course_grade(String fieldName, String value){
		String SQL = "SELECT C.cid, C.cname, C.teacher, C.point, R.time, S.sid, S.sname, S.grade, S.class, R.grade AS score "
				   + "FROM Student S, Course C, Record R "
				   + "WHERE ";
		
		if (fieldName.equals("cid")) {
			SQL += "C.cid = \"" + value + "\" AND ";
		}else if (fieldName.equals("cname")) {
			SQL += "cname = \"" + value + "\" AND ";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		SQL += "S.sid = R.sid AND R.cid = C.cid";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_cid = result.getString("cid");
				String result_cname = result.getString("cname");
				String result_teacher = result.getString("teacher");
				Integer result_point = result.getInt("point");
				Integer result_time = result.getInt("time");
				String result_sid = result.getString("sid");
				String result_sname = result.getString("sname");
				Integer result_score = result.getInt("score");
				Integer result_grade = result.getInt("grade");
				Integer result_class = result.getInt("class");
				String[] temp = {result_cid, result_cname, result_teacher,
								 result_point.toString(), result_time.toString(),
								 result_sid, result_sname, result_grade.toString(),
								 result_score.toString(), result_class.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//根据课程cid或cname和成绩上限（含）、下限（含）查询在范围内的人数，包括课程的cid、cname、teacher、point+选课时间time+人数number
	public List<String[]> search_course_range(String fieldName, String value, String low, String high){
		
		if (Integer.valueOf(high) < Integer.valueOf(low)) {
			System.out.println("Query Failed. The range is illegal");
			return null;
		}
		
		String SQL = "SELECT C.cid, C.cname, C.teacher, C.point, R.time, COUNT(*) AS count "
				   + "FROM Student S, Course C, Record R "
				   + "WHERE ";
		
		if (fieldName.equals("cid")) {
			SQL += "C.cid = \"" + value + "\" AND ";
		}else if (fieldName.equals("cname")) {
			SQL += "cname = \"" + value + "\" AND ";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		SQL += "S.sid = R.sid AND R.cid = C.cid AND R.grade < " + high + " AND R.grade >= " + low
			 + " GROUP BY C.cid"; 

		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_cid = result.getString("cid");
				String result_cname = result.getString("cname");
				String result_teacher = result.getString("teacher");
				Integer result_point = result.getInt("point");
				Integer result_time = result.getInt("time");
				Integer result_count = result.getInt("count");
				String[] temp = {result_cid, result_cname, result_teacher,
								 result_point.toString(), result_time.toString(),
								 result_count.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//根据class、grade课程cid或cname和成绩上限（含）、下限（含）查询在范围内的人数，包括课程的cid、cname、teacher、point+选课时间time+人数number
	public List<String[]> search_class_range(String grade, String className, String fieldName, String value, String low, String high){
		
		if (Integer.valueOf(high) < Integer.valueOf(low)) {
			System.out.println("Query Failed. The range is illegal");
			return null;
		}
		
		String SQL = "SELECT C.cid, C.cname, C.teacher, C.point, R.time, COUNT(*) AS count "
				   + "FROM Student S, Course C, Record R "
				   + "WHERE ";
		
		if (fieldName.equals("cid")) {
			SQL += "C.cid = \"" + value + "\" AND ";
		}else if (fieldName.equals("cname")) {
			SQL += "cname = \"" + value + "\" AND ";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		SQL += "S.sid = R.sid AND R.cid = C.cid AND S.class = \"" + className + "\" AND S.grade = \"" + grade + "\" AND R.grade < " + high + " AND R.grade >= " + low
			 + " GROUP BY C.cid"; 

		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_cid = result.getString("cid");
				String result_cname = result.getString("cname");
				String result_teacher = result.getString("teacher");
				Integer result_point = result.getInt("point");
				Integer result_time = result.getInt("time");
				Integer result_count = result.getInt("count");
				String[] temp = {result_cid, result_cname, result_teacher,
								 result_point.toString(), result_time.toString(),
								 result_count.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
	
	//根据课程cid或cname查询课程的平均成绩，包括课程的cid、cname、teacher、point+选课时间time+平均成绩avg
	public List<String[]> search_course_average(String fieldName, String value){
		String SQL = "SELECT C.cid, C.cname, C.teacher, C.point, R.time, AVG(R.grade) AS average "
				   + "FROM Student S, Course C, Record R "
				   + "WHERE ";
		
		if (fieldName.equals("cid")) {
			SQL += "C.cid = \"" + value + "\" AND ";
		}else if (fieldName.equals("cname")) {
			SQL += "cname = \"" + value + "\" AND ";
		}else {
			System.out.println("Query Failed. Illegal query method.");
			return null;
		}
		
		SQL += "S.sid = R.sid AND R.cid = C.cid GROUP BY S.sid";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			List<String[]> result_list = new ArrayList<>();
			
			int i = 0;
			while(result.next()) {
				String result_cid = result.getString("cid");
				String result_cname = result.getString("cname");
				String result_teacher = result.getString("teacher");
				Integer result_point = result.getInt("point");
				Integer result_time = result.getInt("time");
				Integer result_average = result.getInt("average");
				String[] temp = {result_cid, result_cname, result_teacher,
								 result_point.toString(), result_time.toString(),
								 result_average.toString()};
				
				result_list.add(temp);
				i++;
			}
			
			if (i != 0) {
				System.out.println("Query successfully.");
			}else {
				System.out.println("Query failed. Result is null.");
			}
			
			return result_list;
		}catch(SQLException e) {
			
			e.printStackTrace();
			return null;
			
		}
	}
		
	//账户
	//注册学生账户
	public boolean regist_student(String sid, String password, String repeatPass) throws Exception{
		
		boolean flag = false;
		
		if (sid.length() != 10 || password.length() > 15 || password.length() < 6 || !password.equals(repeatPass)) {
			System.out.println("Regist failed. Illegal sid or password.");
			return flag;
		}
		
		List<String[]> temp = search_student_basic("sid", sid);
		
		if (temp.isEmpty()) {
			System.out.println("Regist failed. Student don't exist.");
			throw new IllegalArgumentException();
		}
		
		
		String SQL = "INSERT INTO Student_account VALUES(?, ?)";
		
		PreparedStatement ptmt = conn.prepareStatement(SQL);
		
		ptmt.setString(1, sid);
		ptmt.setString(2, password);
	
		ptmt.execute();
		
		flag = true;
		System.out.println("Regist successfully.");
		return flag;
	}
	
	//学生登录验证
	public boolean login_student(String sid, String password){
		
		if (sid.length() != 10 || password.length() > 15 || password.length() < 6) {
			System.out.println("Login failed. Sid or password error.");
			return false;
		}
		
		String SQL = "SELECT * "
				   + "FROM Student_account S "
				   + "WHERE S.sid = \"" + sid + "\" AND S.pass = \"" + password + "\"";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			int i = 0;
			while(result.next()) {
				i++;
			}
			
			if (i != 0) {
				System.out.println("Login successfully.");
				return true;
			}else {
				System.out.println("Login failed. Sid or password error.");
				return false;
			}
			
		}catch(SQLException e) {
			
			e.printStackTrace();
			System.out.println("Login error.");
			return false;
			
		}
}
	
	//修改学生密码
	public boolean update_password(String sid, String oldPass, String newPass) throws Exception{
		
		if (sid.length() != 10 || oldPass.length() > 15 || oldPass.length() < 6 || newPass.length() > 15 || newPass.length() < 6) {
			System.out.println("Update failed. Illegal sid or password.");
			return false;
		}
		
		boolean temp = login_student(sid, oldPass);
		
		if (!temp) {
			System.out.println("Update failed. Password error.");
			throw new IllegalArgumentException();
		}
		
		String SQL = "UPDATE Student_account SET pass = ? WHERE sid = ?";
		
		PreparedStatement ptmt = conn.prepareStatement(SQL);
		
		ptmt.setString(1, newPass);
		ptmt.setString(2, sid);
	
		ptmt.execute();
		
		System.out.println("Update successfully.");
		return true;
	}
	
	//忘记密码
	public boolean forget_password(String sid, String newPass) throws Exception{
		if (sid.length() != 10 || newPass.length() > 15 || newPass.length() < 6) {
			System.out.println("Update failed. Illegal sid or password.");
			return false;
		}
		
		List<String[]> exits = search_student_basic("sid", sid);
		
		if (exits.isEmpty()) {
			System.out.println("sid is not exists");
			throw new IllegalArgumentException();
		}
		String SQL = "UPDATE Student_account SET pass = ? WHERE sid = ?";
		
		PreparedStatement ptmt = conn.prepareStatement(SQL);
		
		ptmt.setString(1, newPass);
		ptmt.setString(2, sid);
	
		ptmt.execute();
		
		System.out.println("Update successfully.");
		return true;
	}
	
	//管理员登录验证
	public boolean login_admin(String adminID, String password){
		
		String SQL = "SELECT * "
				   + "FROM Admin_account A "
				   + "WHERE A.id = \"" + adminID + "\" AND A.pass = \"" + password + "\"";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			
			ResultSet result = stmt.executeQuery(SQL);
			
			int i = 0;
			while(result.next()) {
				i++;
			}
			
			if (i != 0) {
				System.out.println("Login successfully.");
				return true;
			}else {
				System.out.println("Login failed. adminID or password error.");
				return false;
			}
			
		}catch(SQLException e) {
			
			e.printStackTrace();
			System.out.println("Login error.");
			return false;
			
		}
}

}
