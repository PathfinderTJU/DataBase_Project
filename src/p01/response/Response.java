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
			serverSocket = new ServerSocket(PORT, 1, InetAddress.getByName("127.0.0.1"));//���������������׽���
		}catch(Exception e) {
			System.out.println("Failed to create socket.");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Server socket create successfully.");
		
		//ѭ���ȴ�����
		while(true) {
			try {
				//ʹ��accept������ȡ���ӵ��������ͻ��׽���
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client socket get.");
						
				//��ȡ�ͻ������������
				InputStream input = clientSocket.getInputStream();
				OutputStream output = clientSocket.getOutputStream();
				
				//����URL
				reader = new BufferedReader(new InputStreamReader(input));
				
				//ֻ���ȡ���б��ļ��ɻ�ȡ��Ҫ��Ϣ
				String headline = reader.readLine();
				
				//���и�ʽ�������� URI HTTP1.1����ȡ��Ӧ��Ϣ
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
						//�޲���
						if (methodName.length() + 5 == uri.length()) {
							Method method = clazz.getMethod(methodName);
							Object result = method.invoke(obj);
							
							PrintStream writer = new PrintStream(output);
							//��Ӧͷ��
							writer.println("HTTP/1.0 200 OK");
							writer.println("Content-type: application/json; charset=utf-8");
							writer.println("Access-Control-Allow-Origin: *");
							writer.println("Content-length: "+ result.toString().getBytes().length);

							writer.println();
							writer.println(result.toString());
						}else {//�в���
							String argument = uri.substring(methodName.length() + 6);
							int count = argument.length();
							JSONObject json = new JSONObject();
							while(true) {//����json����
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
							//��Ӧͷ��
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
	 * ѧ��ע��
	 * @position����¼ҳ��
	 * @input��JSON����˳��Ϊsid��password��repeatPass
	 * @output��JSON����ע��״̬status
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
	 * ѧ����¼��֤
	 * @position����¼ҳ��
	 * @input��JSON����˳��Ϊsid��password
	 * @output��JSON���󣬵�¼״̬status, �û�����type
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
	 * ѧ����������
	 * @position����������ҳ��
	 * @input��JSON���󣬰���sid��newPass
	 * @output��JSON���󣬰����޸�״̬status
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
	 * ����Ա��¼
	 * @position����¼ҳ��
	 * @input��JSON���󣬰���adminID��password
	 * @output��JSON���󣬵�¼״̬status����¼����type
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
	 * ��ȡѧ��������Ϣ
	 * @position��ѧ���ˣ���������
	 * @input��JSON����fieldName��value
	 * @output��JSON����sid��sname��gender��age��grade��class
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
	 * ��ȡѧ��������Ϣ
	 * @position������ˣ�ѧ������
	 * @input��JSON����fieldName��value
	 * @output��JSON����sid��sname��gender��age��grade��class
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
	 * ��ȡȫ��ѧ��������Ϣ
	 * @position������Ա�ˣ�ѧ������
	 * @input��null
	 * @output��JSON�������飬ÿ��JSON�������sid��sname��gender��age��grade��class
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
	 * ѧ���޸�����
	 * @position��ѧ���ˣ���������
	 * @input��JSON���󣬰���sid��oldPass��newPass
	 * @output��JSON�����޸�״̬status
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
	 * ��ȡѧ��ĳ�Ƶĳɼ�
	 * @position��ѧ���ˣ�����Ա�ˣ�ѧ���ɼ���ѯ��
	 * @input��JSON����studentFieldName��studentValue��courseFieldName��courseValue
	 * @outpu: JSON����sid��sname��grade��class��cid��cname��point��time��score
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
	 * ��ȡѧ����ȫ���ɼ�
	 * @position��ѧ���ˡ�����Ա�ˣ�ѧ���ɼ���ѯ
	 * @input��JSON����fieldName��value
	 * @output��JSON�������飬�������JSON����ÿ��JSON�������sid��sname��grade��class��cid��cname��teacher��point��time
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
	 * ��ȡѧ����ƽ���ɼ�
	 * @position��ѧ���ˣ�����Ա�ˣ��ɼ���ѯ
	 * @input��JSON����fieldName��value
	 * @output��JSON����sid��sname��grade��class��average
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
	 * ��ȡ�γ̵Ļ�����Ϣ
	 * @position��ѧ���ˣ��γ̲�ѯ
	 * @input��JSON����fieldName��value
	 * @output��JSON����cid��cname��teacher��point��suit_year��cancel_year
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
	 * ��ȡȫ���γ̵Ļ�����Ϣ
	 * @position��ѧ���ˣ��γ̲�ѯ
	 * @input��null
	 * @output��JSON�������飬ÿ��JSON�������cid��cname��teacher��point��suit_year��cancel_year
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
	 * ѧ��ѡ��(Ĭ�����óɼ�Ϊ-1)
	 * @position��ѧ���ˣ�ѡ��
	 * @input��JSON����sid��cid��ѡ��ʱ��time
	 * @output��JSON����ѡ��״̬status
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
	 * ѧ���˿�
	 * @position��ѧ���ˣ��˿�
	 * @input��JSON����sid��cid��time
	 * @output��JSON����ѡ��״̬status
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
	 * ѧ����ѧ
	 * @position������Ա�ˣ�ѧ������
	 * @input��JSON����sid��sname��gender��age��grade��class
	 * @output��JSON����status����״̬
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
	 * ѧ����ѧ
	 * @position������Ա�ˣ�ѧ������
	 * @input��JSON����sid
	 * @output��JSON����status����״̬
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
	 * �޸�ѧ����ĳ�����ԣ������޸�sid��
	 * @position������Ա�ˣ�ѧ������
	 * @input��JSON����sid��fielName��valule
	 * @output��JSON���󣬸��½��status
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
	 * �ǳɼ�
	 * @position������Ա�ˣ��ɼ�����
	 * @input��JSON����sid��cid��time��score
	 * @output��JSON���󣬲����״̬status
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
	 * �ĳɼ���ɾ�ɼ���
	 * @position������Ա�ˣ��ɼ�����
	 * @input��JSON����sid��cid��time��score
	 * @output��JSON�����޸ĵ�״̬status
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
	 * ��ȡ�༶��ĳ�γ�ƽ���ɼ�
	 * @position������Ա�ˣ��ɼ�����
	 * @input��fieldName��value��class��grade
	 * @output��cid��cname��teacher��point��time��class��average
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
	 * ��ȡ�༶��ĳ�γ�ȫ���ɼ�
	 * @position������Ա�ˣ��ɼ�����
	 * @input��fieldName��value��class��grade
	 * @output��JSON�������飬sid��sname��grade��class��cid��cname��teacher��point��time��grade
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
	 * ��ȡ�༶��ȫ��ƽ���ɼ�
	 * @position������Ա�ˣ��ɼ�����
	 * @input��grade��class
	 * @output��sid��sname��average
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
	 * ��ȡĳ�γ̵�ȫ���ɼ�
	 * @position������Ա�ˣ��ɼ�����
	 * @input��JSON����fieldName��value
	 * @output��sid��sname��grade��class��cid��cname��teacher��point��time��score
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
	 * ��ȡĳ�γ̵�ƽ���ɼ�
	 * @position������Ա�ˣ��ɼ�����
	 * @input��JSON����fieldName��value
	 * @output��sid��sname��grade��class��cid��cname��teacher��point��time��score
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
	 * ��ȡĳ�γ̵ĳɼ��ֲ�
	 * @position������Ա�ˣ��ɼ�����
	 * @input��JSON����fieldName��value��low��high
	 * @output��JSON����cid��cname��teacher��point��time��count
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
	 * ��ȡĳ�༶ĳ�γ̵ĳɼ��ֲ�
	 * @position������Ա�ˣ��ɼ�����
	 * @input��JSON����class, fieldName��value��low��high
	 * @output��JSON����cid��cname��teacher��point��time��count
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
	 * ���ӿγ�
	 * @position������Ա�ˣ��γ̹���
	 * @input��JSON����cid��cname��teacher��point��suit_year��cancel_year(Ĭ��-1)
	 * @output��JSON���󣬲���״̬status
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
	 * ɾ���γ�
	 * @position������Ա�ˣ��γ̹���
	 * @input��cid
	 * @output��status
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
	 * �޸Ŀγ̵�ĳ�����ԣ�����Ϊcid��
	 * @position������Ա�ˣ��γ̹���
	 * @input��cid��fieldName��value
	 * @output��status
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
	 * ����Աѡ��
	 * @position������Ա�ˣ�ѡ�ι���
	 * @input��sid��cid��time
	 * @output��status
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
	 * ����Ա�˿�
	 * @position������Ա�ˣ�ѡ�ι���
	 * @input��sid��cid��time
	 * @output��status
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
