package com.kuiren.common.httppost;

import java.util.ArrayList;

public class PostRequestEmulator {
	public static void main(String[] args) throws Exception {
		// 设定服务地址
		String serverUrl = "http://127.0.0.1:8080/test/upload";

		// 设定要上传的普通Form Field及其对应的value
		// 类FormFieldKeyValuePair的定义见后面的代码
		ArrayList<FormFieldKeyValuePair> ffkvp = new ArrayList<FormFieldKeyValuePair>();
		ffkvp.add(new FormFieldKeyValuePair("username", "Patrick"));
		ffkvp.add(new FormFieldKeyValuePair("password", "HELLOPATRICK"));
		ffkvp.add(new FormFieldKeyValuePair("hobby", "Computer programming"));

		// 设定要上传的文件。UploadFileItem见后面的代码
		ArrayList<UploadFileItem> ufi = new ArrayList<UploadFileItem>();
		ufi.add(new UploadFileItem("upload1", "E:\\Asturias.mp3"));
		ufi.add(new UploadFileItem("upload2", "E:\\full.jpg"));
		ufi.add(new UploadFileItem("upload3", "E:\\dyz.txt"));

		// 类HttpPostEmulator的定义，见后面的代码
		HttpPostEmulator hpe = new HttpPostEmulator();
		String response = hpe.sendHttpPostRequest(serverUrl, ffkvp, ufi);
		System.out.println("Responsefrom server is: " + response);
	}
}