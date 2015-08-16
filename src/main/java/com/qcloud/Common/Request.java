package com.qcloud.Common;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.qcloud.Utilities.MD5;

/**
 * @brief 请求调用类
 * @author robinslsun
 */
public class Request {
	protected static String requestUrl = "";
	protected static String rawResponse = "";
	protected static String version = "SDK_JAVA_1.2";

	public static String getRequestUrl() {
		return requestUrl;
	}

	public static String getRawResponse() {
		return rawResponse;
	}

	public static String generateUrl(TreeMap<String, Object> params,
			String secretId, String secretKey, String requestMethod,
			String requestHost, String requestPath) {
		if (!params.containsKey("SecretId"))
			params.put("SecretId", secretId);

		if (!params.containsKey("Nonce"))
			params.put("Nonce",
					new Random().nextInt(Integer.MAX_VALUE));

		if (!params.containsKey("Timestamp"))
			params.put("Timestamp", System.currentTimeMillis() / 1000);

		params.put("RequestClient", version);

		String plainText = Sign.makeSignPlainText(params, requestMethod,
				requestHost, requestPath);

		try {
			params.put("Signature", Sign.sign(plainText, secretKey));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (params.get("Action").toString().equals("MultipartUploadVodFile")) {
			String url = "http://" + requestHost + requestPath;
			url += Sign.buildParamStr(params);
			return url;
		}

		String url = "https://" + requestHost + requestPath;
		if (requestMethod.equals("GET")) {
			url += Sign.buildParamStr(params);
		}

		return url;
	}

	public static String send(TreeMap<String, Object> params, String secretId,
			String secretKey, String requestMethod, String requestHost,
			String requestPath, String fileName) {
		if (!params.containsKey("SecretId"))
			params.put("SecretId", secretId);

		if (!params.containsKey("Nonce"))
			params.put("Nonce",
					new Random().nextInt(Integer.MAX_VALUE));

		if (!params.containsKey("Timestamp"))
			params.put("Timestamp", System.currentTimeMillis() / 1000);

		params.put("RequestClient", version);
		String plainText = Sign.makeSignPlainText(params, requestMethod,
				requestHost, requestPath);
		try {
			params.put("Signature", Sign.sign(plainText, secretKey));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (params.get("Action").toString().equals("MultipartUploadVodFile")) {
			String url = "http://" + requestHost + requestPath;
			return sendMultipartUploadVodFileRequest(url, params,
					requestMethod, fileName);
		}

		String url = "https://" + requestHost + requestPath;

		return sendRequest(url, params, requestMethod, fileName);
	}

	@SuppressWarnings("deprecation")
	public static String sendRequest(String url,
			Map<String, Object> requestParams, String requestMethod,
			String fileName) {
		String result = "";
		BufferedReader in = null;
		String paramStr = "";

		for (String key : requestParams.keySet()) {
			if (!paramStr.isEmpty()) {
				paramStr += '&';
			}
			paramStr += key + '='
					+ URLEncoder.encode(requestParams.get(key).toString());
		}

		try {

			if (requestMethod.equals("GET")) {
				if (url.indexOf('?') > 0) {
					url += '&' + paramStr;
				} else {
					url += '?' + paramStr;
				}
			}
			requestUrl = url;
			String BOUNDARY = "---------------------------"
					+ MD5.stringToMD5(
							String.valueOf(System.currentTimeMillis()))
							.substring(0, 15);
			URL realUrl = new URL(url);
			URLConnection connection = null;
			if (url.toLowerCase().startsWith("https")) {
				HttpsURLConnection httpsConn = (HttpsURLConnection) realUrl
						.openConnection();

				httpsConn.setHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
				connection = httpsConn;
			} else {
				connection = realUrl.openConnection();
			}

			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

			if (requestMethod.equals("POST")) {
				((HttpURLConnection) connection).setRequestMethod("POST");
				// 发送POST请求必须设置如下两行
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setRequestProperty("Content-Type",
						"multipart/form-data; boundary=" + BOUNDARY);
				OutputStream out = new DataOutputStream(
						connection.getOutputStream());
				StringBuffer strBuf = new StringBuffer();
				for (String key : requestParams.keySet()) {
					strBuf.append("\r\n").append("--").append(BOUNDARY)
							.append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\""
							+ key + "\"\r\n\r\n");
					strBuf.append(requestParams.get(key));
				}
				out.write(strBuf.toString().getBytes());
				if (fileName != null) {
					File file = new File(fileName);
					String filename = file.getName();
					String contentType = URLConnection.getFileNameMap()
							.getContentTypeFor(fileName);

					strBuf = new StringBuffer();
					strBuf.append("\r\n").append("--").append(BOUNDARY)
							.append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"entityFile\"; filename=\""
							+ filename + "\"\r\n");
					strBuf.append("Content-Type:" + contentType + "\r\n\r\n");

					out.write(strBuf.toString().getBytes());

					DataInputStream ins = new DataInputStream(
							new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = ins.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
					ins.close();
				}
				byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
				out.write(endData);
				out.flush();
				out.close();
			}

			// 建立实际的连接
			connection.connect();

			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}

		} catch (Exception e) {
			result = "{\"code\":-2700,\"location\":\"com.qcloud.Common.Request:225\",\"message\":\"api sdk throw exception! "
					+ e.toString() + "\"}";
		} finally {
			// 使用finally块来关闭输入流
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				result = "{\"code\":-2800,\"location\":\"com.qcloud.Common.Request:234\",\"message\":\"api sdk throw exception! "
						+ e2.toString() + "\"}";
			}
		}
		rawResponse = result;
		return result;
	}

	@SuppressWarnings("deprecation")
	public static String sendMultipartUploadVodFileRequest(String url,
			Map<String, Object> requestParams, String requestMethod,
			String fileName) {
		String result = "";
		BufferedReader in = null;
		String paramStr = "";

		for (String key : requestParams.keySet()) {
			if (!paramStr.isEmpty()) {
				paramStr += '&';
			}
			paramStr += key + '='
					+ URLEncoder.encode(requestParams.get(key).toString());
		}

		try {

			if (url.indexOf('?') > 0) {
				url += '&' + paramStr;
			} else {
				url += '?' + paramStr;
			}

			System.out.println(url);

			requestUrl = url;
			// String BOUNDARY = "---------------------------" +
			// MD5.stringToMD5(String.valueOf(System.currentTimeMillis())).substring(0,15);
			URL realUrl = new URL(url);
			URLConnection connection = null;
			if (url.toLowerCase().startsWith("https")) {
				HttpsURLConnection httpsConn = (HttpsURLConnection) realUrl
						.openConnection();

				httpsConn.setHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
				connection = httpsConn;
			} else {
				connection = realUrl.openConnection();
			}

			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setDoOutput(true);
			connection.setDoInput(true);

			File file = new File(fileName);
			long file_length = (Long) requestParams.get("fileSize");
			OutputStream out = new DataOutputStream(
					connection.getOutputStream());
			DataInputStream ins = new DataInputStream(new FileInputStream(file));
			int offset = ((Integer) requestParams.get("offset")).intValue();
			int dataSize = ((Integer) requestParams.get("dataSize")).intValue();
			if (offset >= file_length) {
				return "{\"code\":-3001,\"location\":\"com.qcloud.Common.Request:303\",\"message\":\"api sdk throw exception! offset larger than the size of file\"}";
			}
			int skipBytes = ins.skipBytes(offset);
			int page = dataSize / 1024;
			int remainder = dataSize % 1024;
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			byte[] bufferOut2 = new byte[remainder];
			while (page != 0) {
				if ((bytes = ins.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				page = page - 1;
			}
			if ((bytes = ins.read(bufferOut2)) != -1) {
				out.write(bufferOut2, 0, bytes);
			}
			ins.close();
			out.flush();
			out.close();

			// 建立实际的连接
			connection.connect();
			try {
				// 定义 BufferedReader输入流来读取URL的响应
				in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
			} catch (Exception e) {
				result = "{\"code\":-3002,\"location\":\"com.qcloud.Common.Request:331\",\"message\":\"api sdk throw exception! protocol doesn't support input or the character Encoding is not supported."
						+ "details: " + e.toString() + "\"}";
				if (in != null) {
					in.close();
				}
				rawResponse = result;
				return result;
			}
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}

		} catch (Exception e) {
			result = "{\"code\":-3000,\"location\":\"com.qcloud.Common.Request:345\",\"message\":\"api sdk throw exception! "
					+ e.toString() + "\"}";
		} finally {
			// 使用finally块来关闭输入流
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				result = "{\"code\":-3003,\"location\":\"com.qcloud.Common.Request:354\",\"message\":\"api sdk throw exception! "
						+ e2.toString() + "\"}";
			}
		}
		rawResponse = result;
		return result;
	}
}
