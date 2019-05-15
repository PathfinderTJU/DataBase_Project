package p01.response;

import p01.api.API;
import org.json.*;

import java.io.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Response {
	
	public static API api = null;
	public static final int PORT = 80;
	public static BufferedReader reader = null;
	
	public static void main(String[] args) {
		api = new API();
		api.connect();
		
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT, 1, InetAddress.getByName("127.0.0.1"));//创建到服务器的套接字
		}catch(Exception e) {
			System.out.println("Failed to create socket.");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Server socket create successfully.");
		
		//循环等待连接
		while(true) {
			try {
				//使用accept方法获取连接到服务器客户套接字
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client socket get.");
						
				//获取客户端输入输出流
				InputStream input = clientSocket.getInputStream();
				OutputStream output = clientSocket.getOutputStream();
				
				//解析URL
				reader = new BufferedReader(new InputStreamReader(input));
				
				//只需读取首行报文即可获取必要信息
				String headline = reader.readLine();
				
				//首行格式：请求码 URI HTTP1.1，获取相应信息
				String words[] = headline.split(" ");
				String requestType = words[0];
				String uri = words[1];

				if (uri.substring(0, 4).equals("/api")) {
					Class<?> clazz = Response.class;
					Object obj = clazz.newInstance();
					String methodName = null;
					if (uri.contains("?")){
						int index = uri.indexOf("?");
						methodName = uri.substring(5, index);
					}else {
						methodName = uri.substring(5);
					}
					if (requestType.equals("GET") || requestType.equals("OPTIONS")) {
						//无参数
						if (methodName.length() + 5 == uri.length()) {
							Method method = clazz.getMethod(methodName);
							Object result = method.invoke(obj);
							
							PrintStream writer = new PrintStream(output);
							//响应头部
							writer.println("HTTP/1.0 200 OK");
							writer.println("Content-type: application/json; charset=utf-8");
							writer.println("Access-Control-Allow-Origin: *");
							writer.println("Content-length: "+ result.toString().getBytes().length);

							writer.println();
							writer.println(result.toString());
						}else {//有参数
							String argument = uri.substring(methodName.length() + 6);
							int count = argument.length();
							JSONObject json = new JSONObject();
							while(true) {//构造json参数
								int i = 0;
								String key = "";
								String value = "";
								while(argument.charAt(i) != '=') {
									key += argument.charAt(i);
									i++;
								}
								i++;
								while(argument.charAt(i) != '&') {
									value += argument.charAt(i);
									i++;
									if (i == count) {
										break;
									}
								}
								json.put(key, value);
								if (i != count) {
									i++;
									argument = argument.substring(i);
									count = argument.length();
								}else {
									break;
								}
							}
							Method method = clazz.getMethod(methodName, JSONObject.class);
							Object result = method.invoke(obj, json);
							PrintStream writer = new PrintStream(output);
							//响应头部
							writer.println("HTTP/1.0 200 OK");
							writer.println("Content-type: text/plain; charset=utf-8");
							writer.println("Access-Control-Allow-Origin: *");
							writer.println("Content-length: "+ result.toString().getBytes().length);
							writer.println();
							writer.println(result.toString());
							clientSocket.close();
						}					
					}
				}else {
					PrintStream writer = new PrintStream(output);
					writer.println("HTTP/1.0 404 File Not Found");
					writer.println("Content-type:text/html");
					writer.println("Content-length: 23");
					writer.println();
					writer.println("<h1>404 Not Found</h1>");
				}
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	/*
	 * 学生注册
	 * @position：登录页面
	 * @input：JSON对象，顺序为sid，password，repeatPass
	 * @output：JSON对象，注册状态status
	 */
	public JSONObject regist_student(JSONObject json) {		
		String sid = json.getString("sid");
		String password = json.getString("password");
		String repeatPass = json.getString("repeatPass");
		
		try {
			boolean status = api.regist_student(sid, password, repeatPass);
			
			JSONObject result = new JSONObject();
			result.put("status", status);
			result.put("code", "200");
			
			System.out.println("Add successfully.");
			return result;
			
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}
	
	/*
	 * 学生登录验证
	 * @position：登录页面
	 * @input：JSON对象，顺序为sid，password
	 * @output：JSON对象，登录状态status, 用户类型type
	 */
	public JSONObject login_student(JSONObject json) {
		
		String sid = json.getString("sid");
		String password = json.getString("password");
		
		boolean status = api.login_student(sid, password);
		
		JSONObject result = new JSONObject();
		
		result.put("status", status);
		result.put("type", "student");
		result.put("code", "200");
		
		System.out.println(">Student login successfully.");
		return result;
	}
	
	/*
	 * 学生忘记密码
	 * @position：忘记密码页面
	 * @input：JSON对象，包括sid，newPass
	 * @output：JSON对象，包括修改状态status
	 */
	public JSONObject forget_password(JSONObject json) {
		
		String sid = json.getString("sid");
		String newPass = json.getString("newPass");
		
		boolean status = true;
		try {
			status = api.forget_password(sid, newPass);
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
		
		JSONObject result = new JSONObject();
		
		result.put("status", status);
		result.put("code", "200");
		
		System.out.println(">Update password successfully.");
		return result;
	}
	
	/*
	 * 管理员登录
	 * @position：登录页面
	 * @input：JSON对象，包括adminID，password
	 * @output：JSON对象，登录状态status、登录类型type
	 */
	public JSONObject login_admin(JSONObject json) {
		
		String adminID = json.getString("adminID");
		String password = json.getString("password");
		
		boolean status = api.login_admin(adminID, password);
		
		JSONObject result = new JSONObject();
		
		result.put("status", status);
		result.put("type", "admin");
		result.put("code", "200");
		
		System.out.println(">Admin login successfully.");
		return result;
		
	}
	
	/*
	 * 获取学生基本信息
	 * @position：学生端，个人中心
	 * @input：JSON对象，fieldName、value
	 * @output：JSON对象，sid、sname、gender、age、grade、class
	 */
	public JSONObject get_student_basic(JSONObject json) {
		
		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		
		List<String[]> temp = api.search_student_basic(fieldName, value);
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
		
		String[] temp2 = temp.get(0);
		
		JSONObject result = new JSONObject();
		result.put("sid", temp2[0]);
		result.put("sname", temp2[1]);
		result.put("gender", temp2[2]);
		result.put("age", temp2[3]);
		result.put("grade", temp2[4]);
		result.put("class", temp2[5]);
		result.put("code", "200");
		
		System.out.println(">Query successfully.");
		return result;
	}
	
	/*
	 * 获取学生基本信息
	 * @position：管理端，学生管理
	 * @input：JSON对象，fieldName、value
	 * @output：JSON对象，sid、sname、gender、age、grade、class
	 */
	public JSONArray admin_get_student_basic(JSONObject json) {
		
		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		
		List<String[]> temp = api.search_student_basic(fieldName, value);
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			JSONArray resultArray = new JSONArray();
			result.put("code", "500");
			resultArray.put(result);
			return resultArray;
		}
		JSONArray result = new JSONArray();
		Iterator<String[]> it = temp.iterator();
		while(it.hasNext()) {
			String[] temp2 = it.next();
			
			JSONObject tempJson = new JSONObject();
			tempJson.put("sid", temp2[0]);
			tempJson.put("sname", temp2[1]);
			tempJson.put("gender", temp2[2]);
			tempJson.put("age", temp2[3]);
			tempJson.put("grade", temp2[4]);
			tempJson.put("class", temp2[5]);
			tempJson.put("code", "200");
			result.put(tempJson);
		}
		
		System.out.println(">Query successfully.");
		return result;
	}
	
	/*
	 * 获取全部学生基本信息
	 * @position：管理员端，学生管理
	 * @input：null
	 * @output：JSON对象数组，每个JSON对象包括sid、sname、gender、age、grade、class
	 */
	public JSONArray get_all_student() {
		List<String[]> temp = api.search_all_student();
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			JSONArray resultArray = new JSONArray();
			result.put("code", "500");
			resultArray.put(result);
			return resultArray;
		}
		
		JSONArray result = new JSONArray();
		
		Iterator<String[]> it = temp.iterator();
		while(it.hasNext()) {
			String[] temp2 = it.next();
			
			JSONObject tempJson = new JSONObject();
			tempJson.put("sid", temp2[0]);
			tempJson.put("sname", temp2[1]);
			tempJson.put("gender", temp2[2]);
			tempJson.put("age", temp2[3]);
			tempJson.put("grade", temp2[4]);
			tempJson.put("class", temp2[5]);
			tempJson.put("code", "200");
			result.put(tempJson);
		}
			
		System.out.println(">Query successfully.");
		return result;
	}
	
	/*
	 * 学生修改密码
	 * @position：学生端，个人中心
	 * @input：JSON对象，包括sid、oldPass、newPass
	 * @output：JSON对象，修改状态status
	 */
	public JSONObject update_password(JSONObject json) {
		
		String sid = json.getString("sid");
		String oldPass = json.getString("oldPass");
		String newPass = json.getString("newPass");
		
		boolean status = false;
		
		try {
			status = api.update_password(sid, oldPass, newPass);
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
		
		JSONObject result = new JSONObject();
		result.put("status", status);
		result.put("code", "200");
		
		System.out.println(">Update password successfully.");
		return result;
	}

	/*
	 * 获取学生某科的成绩
	 * @position：学生端，管理员端，学生成绩查询。
	 * @input：JSON对象，studentFieldName、studentValue、courseFieldName、courseValue
	 * @outpu: JSON对象，sid、sname、grade、class、cid、cname、point、time、score
	 */
	public JSONArray get_student_grade(JSONObject json) {
		
		String studentFieldName = json.getString("studentFieldName");
		String studentValue = json.getString("studentValue");
		String courseFieldName = json.getString("courseFieldName");
		String courseValue = json.getString("courseValue");
		
		List<String[]> temp = api.search_student_course_grade(studentFieldName, studentValue, courseFieldName, courseValue);
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			JSONArray resultArray = new JSONArray();
			result.put("code", "500");
			resultArray.put(result);
			return resultArray;
		}
		
		JSONArray result = new JSONArray();
		
		Iterator<String[]> it = temp.iterator();
		while(it.hasNext()) {
			String[] temp2 = it.next();
			
			JSONObject tempJson = new JSONObject();
			tempJson.put("sid", temp2[0]);
			tempJson.put("sname", temp2[1]);
			tempJson.put("cid", temp2[2]);
			tempJson.put("cname", temp2[3]);
			tempJson.put("teacher", temp2[4]);
			tempJson.put("point", temp2[5]);
			tempJson.put("time", temp2[6]);
			tempJson.put("score", temp2[7]);
			tempJson.put("code", "200");
			
			result.put(tempJson);
		}
		
		System.out.println(">Query successfully.");
		return result;
	}
	
	/*
	 * 获取学生的全部成绩
	 * @position：学生端、管理员端，学生成绩查询
	 * @input：JSON对象，fieldName，value
	 * @output：JSON对象数组，包括多个JSON对象，每个JSON对象包括sid、sname、grade、class、cid、cname、teacher、point、time
	 */
	public JSONArray get_student_all_grade(JSONObject json) {
		
		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		
		List<String[]> temp = api.search_student_all_grade(fieldName, value);
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			JSONArray resultArray = new JSONArray();
			result.put("code", "500");
			resultArray.put(result);
			return resultArray;
		}
		
		JSONArray result = new JSONArray();
		
		Iterator<String[]> it = temp.iterator();
		while(it.hasNext()) {
			String[] temp2 = it.next();
			
			JSONObject tempJson = new JSONObject();
			tempJson.put("sid", temp2[0]);
			tempJson.put("sname", temp2[1]);
			tempJson.put("cid", temp2[4]);
			tempJson.put("cname", temp2[5]);
			tempJson.put("teacher", temp2[6]);
			tempJson.put("point", temp2[7]);
			tempJson.put("time", temp2[8]);
			tempJson.put("suit_year", temp2[9]);
			tempJson.put("cancel_year", temp2[10]);
			tempJson.put("score", temp2[11]);
			tempJson.put("code", "200");
			result.put(tempJson);
		}
			
		System.out.println(">Query successfully.");
		return result;
	}
	
	/*
	 * 获取学生的平均成绩
	 * @position：学生端，管理员端，成绩查询
	 * @input：JSON对象，fieldName、value
	 * @output：JSON对象，sid、sname、grade、class、average
	 */
	public JSONObject get_student_average(JSONObject json) {
		
		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		
		List<String[]> temp = api.search_student_average_grade(fieldName, value);
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
		
		String[] temp2 = temp.get(0);
		
		JSONObject result = new JSONObject();
		
		result.put("sid", temp2[0]);
		result.put("sname", temp2[1]);
		result.put("grade", temp2[2]);
		result.put("class", temp2[3]);
		result.put("average", temp2[4]);
		result.put("code", "200");
		
		System.out.println(">Query successfully.");
		return result;
	}

	/*
	 * 获取课程的基本信息
	 * @position：学生端，课程查询
	 * @input：JSON对象，fieldName、value
	 * @output：JSON对象，cid、cname、teacher、point、suit_year、cancel_year
	 */
	public JSONArray get_course_basic(JSONObject json) {
		
		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		
		List<String[]> temp = api.search_course_basic(fieldName, value);
		
		if (temp.isEmpty()) {
			JSONObject result = new JSONObject();
			JSONArray resultArray = new JSONArray();
			result.put("code", "500");
			resultArray.put(result);
			return resultArray;
		}
		
		JSONArray result = new JSONArray();
		
		Iterator<String[]> it = temp.iterator();
		while(it.hasNext()) {
			String[] temp2 = it.next();
		
			JSONObject tempJson = new JSONObject();
			
			tempJson.put("cid", temp2[0]);
			tempJson.put("cname", temp2[1]);
			tempJson.put("teacher", temp2[2]);
			tempJson.put("point", temp2[3]);
			tempJson.put("suit_year", temp2[4]);
			tempJson.put("cancel_year", temp2[5]);
			tempJson.put("code", "200");
			
			result.put(tempJson);
		}
		System.out.println(">Query successfully.");
		return result;
	}
	
	/*
	 * 获取全部课程的基本信息
	 * @position：学生端，课程查询
	 * @input：null
	 * @output：JSON对象数组，每个JSON对象包括cid、cname、teacher、point、suit_year、cancel_year
	 */
	public JSONArray get_all_course() {
		List<String[]> temp = api.search_all_course();
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			JSONArray resultArray = new JSONArray();
			result.put("code", "500");
			resultArray.put(result);
			return resultArray;
		}
		
		JSONArray result = new JSONArray();
		
		Iterator<String[]> it = temp.iterator();
		while(it.hasNext()) {
			String[] temp2 = it.next();
			
			JSONObject tempJson = new JSONObject();
			tempJson.put("cid", temp2[0]);
			tempJson.put("cname", temp2[1]);
			tempJson.put("teacher", temp2[2]);
			tempJson.put("point", temp2[3]);
			tempJson.put("suit_year", temp2[4]);
			tempJson.put("cancel_year", temp2[5]);
			tempJson.put("code", "200");
			result.put(tempJson);
		}
			
		System.out.println(">Query successfully.");
		return result;
	}
	
	/*
	 * 学生选课(默认设置成绩为-1)
	 * @position：学生端，选课
	 * @input：JSON对象，sid、cid、选课时间time
	 * @output：JSON对象，选课状态status
	 */
	public JSONObject student_select_course(JSONObject json) {
		String sid = json.getString("sid");
		String cid = json.getString("cid");
		String time = json.getString("time");
		String[] args = {sid, cid, time, "-1"};
		
		try {
			boolean status = api.insert_record(args);
			
			JSONObject result = new JSONObject();
			result.put("code", "200");
			result.put("status", status);
			
			
			System.out.println(">Select successfully.");
			return result;
			
		}catch(IndexOutOfBoundsException e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "400");
			return result;
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}
	
	/*
	 * 学生退课
	 * @position：学生端，退课
	 * @input：JSON对象，sid、cid、time
	 * @output：JSON对象，选课状态status
	 */
	public JSONObject student_delete_course(JSONObject json) {
		
		String sid = json.getString("sid");
		String cid = json.getString("cid");
		String time = json.getString("time");
		
		try {
			boolean status = api.delete_record(sid, cid, time);
			
			JSONObject result = new JSONObject();
			result.put("status", status);
			result.put("code", "200");
			
			System.out.println("Delete successfully.");
			return result;
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}
	
	/*
	 * 学生入学
	 * @position：管理员端，学生管理
	 * @input：JSON对象，sid、sname、gender、age、grade、class
	 * @output：JSON对象，status插入状态
	 */
	public JSONObject add_student(JSONObject json) {
		
		String sid = json.getString("sid");
		String sname = json.getString("sname");
		String gender = json.getString("gender");
		String age = json.getString("age");
		String grade = json.getString("grade");
		String className = json.getString("class");
		String[] args = {sid, sname, gender, age, grade, className};
		
		try {
			boolean status = api.insert_student(args);
			
			JSONObject result = new JSONObject();
			result.put("status", status);
			result.put("code", "200");
			
			System.out.println("Add successfully.");
			return result;
			
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}
	
	/*
	 * 学生退学
	 * @position：管理员端，学生管理
	 * @input：JSON对象，sid
	 * @output：JSON对象，status插入状态
	 */
	public JSONObject delete_student(JSONObject json) {
		
		String sid = json.getString("sid");
		
		try {
			boolean status = api.delete_student(sid);
			
			JSONObject result = new JSONObject();
			result.put("status", status);
			result.put("code", "200");
			
			System.out.println("Delete successfully.");
			return result;
			
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}
	
	/*
	 * 修改学生的某个属性（不能修改sid）
	 * @position：管理员端，学生管理
	 * @input：JSON对象，sid，fielName，valule
	 * @output：JSON对象，更新结果status
	 */
	public JSONObject update_student(JSONObject json) {
		
		String sid = json.getString("sid");
		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		
		try {
			boolean status = api.update_student(sid, fieldName, value);
			
			JSONObject result = new JSONObject();
			result.put("status", status);
			result.put("code", "200");
			
			System.out.println(">Update successfully.");
			return result;
			
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}
	
	/*
	 * 登成绩
	 * @position：管理员端，成绩管理
	 * @input：JSON对象，sid、cid、time、score
	 * @output：JSON对象，插入的状态status
	 */
	public JSONObject add_score(JSONObject json) {
		
		String sid = json.getString("sid");
		String sname = json.getString("cid");
		String time = json.getString("time");
		String score = json.getString("score");	
		String[] args = {sid, sname, time, score};
		
		try {
			boolean status = api.insert_record(args);
			
			JSONObject result = new JSONObject();
			result.put("status", status);
			result.put("code", "200");
			
			System.out.println("Add successfully.");
			return result;
			
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}
	
	/*
	 * 改成绩（删成绩）
	 * @position：管理员端，成绩管理
	 * @input：JSON对象，sid、cid、time、score
	 * @output：JSON对象，修改的状态status
	 */
	public JSONObject update_score(JSONObject json) {
		
		String sid = json.getString("sid");
		String cid = json.getString("cid");
		String time = json.getString("time");
		String score = json.getString("score");	
		
		try {
			boolean status = api.update_record(sid, cid, time, score);
			
			JSONObject result = new JSONObject();
			result.put("status", status);
			result.put("code", "200");
			
			System.out.println("UPdate/Delete successfully.");
			return result;
			
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}
	
	/*
	 * 获取班级的某课程平均成绩
	 * @position：管理员端，成绩管理
	 * @input：fieldName、value、class、grade
	 * @output：cid、cname、teacher、point、time、class、average
	 */
	public JSONObject get_class_course_grade(JSONObject json) {
		
		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		String className = json.getString("class");
		String grade = json.getString("grade");
		
		List<String[]> temp = api.search_class_course_average(grade, className, fieldName, value);
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
		
		String[] temp2 = temp.get(0);
		
		JSONObject result = new JSONObject();
		
		result.put("grade", temp2[0]);
		result.put("class", temp2[1]);
		result.put("cid", temp2[2]);
		result.put("cname", temp2[3]);
		result.put("teacher", temp2[4]);
		result.put("point", temp2[5]);
		result.put("time", temp2[6]);
		result.put("average", temp2[7]);
		result.put("code", "200");
		
		System.out.println(">Query successfully.");
		return result;
	}
	
	/*
	 * 获取班级的某课程全部成绩
	 * @position：管理员端，成绩管理
	 * @input：fieldName、value、class、grade
	 * @output：JSON对象数组，sid、sname、grade、class、cid、cname、teacher、point、time、grade
	 */
	public JSONArray get_class_course_all(JSONObject json) {
		
		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		String classValue = json.getString("class");
		String gradeValue = json.getString("grade");
		
		List<String[]> temp = api.search_class_course_all(gradeValue, classValue, fieldName, value);
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			JSONArray resultArray = new JSONArray();
			result.put("code", "500");
			resultArray.put(result);
			return resultArray;
		}
		
		JSONArray result = new JSONArray();
		
		Iterator<String[]> it = temp.iterator();
		while(it.hasNext()) {
			String[] temp2 = it.next();

			System.out.println();
			JSONObject tempJson = new JSONObject();
			tempJson.put("sid", temp2[0]);
			tempJson.put("sname", temp2[1]);
			tempJson.put("grade", temp2[2]);
			tempJson.put("class", temp2[3]);
			tempJson.put("cid", temp2[4]);
			tempJson.put("cname", temp2[5]);
			tempJson.put("teacher", temp2[6]);
			tempJson.put("point", temp2[7]);
			tempJson.put("time", temp2[8]);
			tempJson.put("score", temp2[9]);
			tempJson.put("code", "200");
			result.put(tempJson);
		}
			
		System.out.println(">Query successfully.");
		return result;
	}
	
	/*
	 * 获取班级的全部平均成绩
	 * @position：管理员端，成绩管理
	 * @input：grade、class
	 * @output：sid、sname、average
	 */
	public JSONArray get_class_average_all(JSONObject json) {
		
		String grade = json.getString("grade");
		String className = json.getString("class");
		
		List<String[]> temp = api.search_class_average_all(grade, className);
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			JSONArray resultArray = new JSONArray();
			result.put("code", "500");
			resultArray.put(result);
			return resultArray;
		}
		
		JSONArray result = new JSONArray();
		
		Iterator<String[]> it = temp.iterator();
		while(it.hasNext()) {
			String[] temp2 = it.next();
			
			JSONObject tempJson = new JSONObject();
			tempJson.put("sid", temp2[0]);
			tempJson.put("same", temp2[1]);
			tempJson.put("average", temp2[2]);
			tempJson.put("code", "200");
			result.put(tempJson);
		}
			
		System.out.println(">Query successfully.");
		return result;
		
	}
	
	/*
	 * 获取某课程的全部成绩
	 * @position：管理员端，成绩管理
	 * @input：JSON对象，fieldName、value
	 * @output：sid、sname、grade、class、cid、cname、teacher、point、time、score
	 */
	public JSONArray get_course_grade_all(JSONObject json) {
		
		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		
		List<String[]> temp = api.search_course_grade(fieldName, value);
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			JSONArray resultArray = new JSONArray();
			result.put("code", "500");
			resultArray.put(result);
			return resultArray;
		}
		
		JSONArray result = new JSONArray();
		
		Iterator<String[]> it = temp.iterator();
		while(it.hasNext()) {
			String[] temp2 = it.next();
			
			JSONObject tempJson = new JSONObject();
			tempJson.put("cid", temp2[0]);
			tempJson.put("cname", temp2[1]);
			tempJson.put("teacher", temp2[2]);
			tempJson.put("point", temp2[3]);
			tempJson.put("time", temp2[4]);
			tempJson.put("sid", temp2[5]);
			tempJson.put("sname", temp2[6]);
			tempJson.put("grade", temp2[7]);
			tempJson.put("score", temp2[8]);
			tempJson.put("class", temp2[9]);
			tempJson.put("code", "200");
			result.put(tempJson);
		}
			
		System.out.println(">Query successfully.");
		return result;
	}

	/*
	 * 获取某课程的平均成绩
	 * @position：管理员端，成绩管理
	 * @input：JSON对象，fieldName、value
	 * @output：sid、sname、grade、class、cid、cname、teacher、point、time、score
	 */
	public JSONObject get_course_average(JSONObject json) {
	
		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		
		List<String[]> temp = api.search_course_average(fieldName, value);
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
		
		String[] temp2 = temp.get(0);
		
		JSONObject result = new JSONObject();
		
		result.put("cid", temp2[0]);
		result.put("cname", temp2[1]);
		result.put("teacher", temp2[2]);
		result.put("point", temp2[3]);
		result.put("time", temp2[4]);
		result.put("average", temp2[5]);
		
		System.out.println(">Query successfully.");
		return result;
	}
	
	/*
	 * 获取某课程的成绩分布
	 * @position：管理员端，成绩管理
	 * @input：JSON对象，fieldName、value、low、high
	 * @output：JSON对象，cid、cname、teacher、point、time、count
	 */
	public JSONObject get_course_range(JSONObject json) {

		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		String low = json.getString("low");
		String high = json.getString("high");
		
		List<String[]> temp = api.search_course_range(fieldName, value, low, high);
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
		
		String[] temp2 = temp.get(0);
		
		JSONObject result = new JSONObject();
		
		result.put("cid", temp2[0]);
		result.put("cname", temp2[1]);
		result.put("teacher", temp2[2]);
		result.put("point", temp2[3]);
		result.put("time", temp2[4]);
		result.put("count", temp2[5]);
		result.put("code", "200");
		
		System.out.println(">Query successfully.");
		return result;
	}
	
	/*
	 * 获取某班级某课程的成绩分布
	 * @position：管理员端，成绩管理
	 * @input：JSON对象，class, fieldName、value、low、high
	 * @output：JSON对象，cid、cname、teacher、point、time、count
	 */
	public JSONObject get_class_range(JSONObject json) {
		
		String grade = json.getString("grade");
		String className = json.getString("class");
		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		String low = json.getString("low");
		String high = json.getString("high");
		
		List<String[]> temp = api.search_class_range(grade, className, fieldName, value, low, high);
		
		if (temp.isEmpty()) {
			System.out.println(">Result is null");
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
		
		String[] temp2 = temp.get(0);
		
		JSONObject result = new JSONObject();
		
		result.put("cid", temp2[0]);
		result.put("cname", temp2[1]);
		result.put("teacher", temp2[2]);
		result.put("point", temp2[3]);
		result.put("time", temp2[4]);
		result.put("count", temp2[5]);
		result.put("code", "200");
		
		System.out.println(">Query successfully.");
		return result;
	}
	
	/*
	 * 增加课程
	 * @position：管理员端，课程管理
	 * @input：JSON对象cid、cname、teacher、point、suit_year、cancel_year(默认-1)
	 * @output：JSON对象，插入状态status
	 */
	public JSONObject add_course(JSONObject json) {
		
		String cid = json.getString("cid");
		String cname = json.getString("cname");
		String teacher = json.getString("teacher");
		String point = json.getString("point");
		String suit_year = json.getString("suit_year");
		String cancel_year = json.getString("cancel_year");
		String args[] = {cid, cname, teacher, point, suit_year, cancel_year}; 
		
		try {
			boolean status = api.insert_course(args);
			
			JSONObject result = new JSONObject();
			result.put("status", status);
			result.put("code", "200");
			
			System.out.println("Add successfully.");
			return result;
			
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}
	
	/*
	 * 删除课程
	 * @position：管理员端，课程管理
	 * @input：cid
	 * @output：status
	 */
	public JSONObject delete_course(JSONObject json) {
		String cid = json.getString("cid");
		
		try {
			boolean status = api.delete_course(cid);
			
			JSONObject result = new JSONObject();
			result.put("status", status);
			result.put("code", "200");
			
			System.out.println("Delete successfully.");
			return result;
			
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}
	
	/*
	 * 修改课程的某个属性（不能为cid）
	 * @position：管理员端，课程管理
	 * @input：cid、fieldName、value
	 * @output：status
	 */
	public JSONObject update_course(JSONObject json) {
		String cid = json.getString("cid");
		String fieldName = json.getString("fieldName");
		String value = json.getString("value");
		
		try {
			boolean status = api.update_course(cid, fieldName, value);
			
			JSONObject result = new JSONObject();
			result.put("status", status);
			result.put("code", "200");
			
			System.out.println(">Update successfully.");
			return result;
			
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}
	
	/*
	 * 管理员选课
	 * @position：管理员端，选课管理
	 * @input：sid、cid、time
	 * @output：status
	 */
	public JSONObject add_record(JSONObject json) {
		
		String sid = json.getString("sid");
		String cid = json.getString("cid");
		String time = json.getString("time");
		String args[] = {sid, cid, time, "-1"}; 
		
		try {
			boolean status = api.insert_record(args);
			
			JSONObject result = new JSONObject();
			result.put("status", status);
			result.put("code", "200");
			
			System.out.println("Add successfully.");
			return result;
			
		}catch(IndexOutOfBoundsException e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "400");
			return result;
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}

	/*
	 * 管理员退课
	 * @position：管理员端，选课管理
	 * @input：sid、cid、time
	 * @output：status
	 */
	public JSONObject delete_record(JSONObject json) {
		
		String sid = json.getString("sid");
		String cid = json.getString("cid");
		String time = json.getString("time");
		
		try {
			boolean status = api.delete_record(sid, cid, time);
			
			JSONObject result = new JSONObject();
			result.put("status", status);
			result.put("code", "200");
			
			System.out.println("Add successfully.");
			return result;
			
		}catch(Exception e) {
			e.printStackTrace();
			JSONObject result = new JSONObject();
			result.put("code", "500");
			return result;
		}
	}
	
}
