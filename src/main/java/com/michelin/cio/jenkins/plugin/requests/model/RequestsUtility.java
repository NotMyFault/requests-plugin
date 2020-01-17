/*
 * The MIT License
 *
 * Copyright 2019 Lexmark
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.michelin.cio.jenkins.plugin.requests.model;

import com.michelin.cio.jenkins.plugin.requests.action.RequestMailSender.DescriptorEmailImpl;

import hudson.util.Secret;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

// @author John Flynn <john.trixmot.flynn@gmail.com>

public class RequestsUtility {

	private static final Logger LOGGER = Logger
			.getLogger(RequestsUtility.class.getName());

	public String runPostMethod(String jenkinsURL, String urlString)
			throws ClientProtocolException, IOException {
		LOGGER.info("[INFO] urlString: " + urlString);
		String returnStatus;
		DescriptorEmailImpl descriptorEmailImpl = new DescriptorEmailImpl();
		String username = descriptorEmailImpl.getUnlockuser();
		// The password must be a Jenkins User Token:
		Secret password = descriptorEmailImpl.getUnlockpassword();
		URI uri = URI.create(urlString);
		HttpHost host = new HttpHost(uri.getHost(), uri.getPort(),
				uri.getScheme());
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
				new AuthScope(uri.getHost(), uri.getPort()),
				new UsernamePasswordCredentials(username,
						password.getPlainText()));
		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(host, basicAuth);
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultCredentialsProvider(credsProvider).build();
		HttpPost httpPost = new HttpPost(uri);

		// Get the jenkins crumb for the Post call:
		// URL crumbURL = new URL(jenkinsURL +
		// "crumbIssuer/api/xml?xpath=concat(//crumbRequestField,%22:%22,//crumb)");
		// String[] crumbArray = getCrumb(crumbURL);

		// if (crumbArray[2].equals("true")) {
		// httpPost.addHeader(crumbArray[0], crumbArray[1]);
		// LOGGER.info("[INFO] Crumb value set: ");
		// } else {
		// Jenkins system without a crumb:
		// LOGGER.info("[INFO] No Crumb value set: ");
		// }

		// Add AuthCache to the execution context
		HttpClientContext localContext = HttpClientContext.create();
		localContext.setAuthCache(authCache);

		HttpResponse response = httpClient.execute(host, httpPost,
				localContext);
		int responseCode = response.getStatusLine().getStatusCode();
		LOGGER.info("[INFO] responseCode: " + responseCode);

		if ((responseCode > 199) && (responseCode < 400)) {
			returnStatus = "success";
		} else {
			LOGGER.info("[ERROR] httpClient getReasonPhrase: "
					+ response.getStatusLine().getReasonPhrase());
			returnStatus = response.getStatusLine().getReasonPhrase();
		}
		return returnStatus;
	}
	
	public String constructFolderJobName(String inputName) {
		String[] projectNameList = null;
		StringBuffer stringBuffer = new StringBuffer();
		projectNameList = inputName.split("/");
		
		// Need to add '/job/' in between all names:
		int nameCount = projectNameList.length;
		stringBuffer.append(projectNameList[0]);
		for (int i = 1; i < nameCount; i++) {
			stringBuffer.append("/job/");
			stringBuffer.append(projectNameList[i]);
		}

		//LOGGER.info("[INFO] FULL FOLDER PROJECT NAME: " + stringBuffer.toString());
		
		return stringBuffer.toString();
	}
	
	public String[] constructFolderJobNameAndFull(String inputName) {
		String[] projectNameList = null;
		String[] returnNameList = new String[2];
		StringBuffer stringBuffer = new StringBuffer();
		String projectName = "";
		projectNameList = inputName.split(" » ");
		int nameCount = projectNameList.length;
		
		stringBuffer.append(projectNameList[0]);
		for (int i = 1; i < nameCount; i++) {
			if (i + 1 == nameCount) {						
				stringBuffer.append("/job/");
				stringBuffer.append(projectNameList[i].split(" ")[0]);
				projectName = projectNameList[i].split(" ")[0];
			} else {
				stringBuffer.append("/job/");
				stringBuffer.append(projectNameList[i]);
			}					
		}
		
		returnNameList[0] = projectName;
		returnNameList[1] = stringBuffer.toString();
		
		return returnNameList;
	}

	public String[] getCrumb(URL url) {
		String[] crumbArray = new String[2];
		String[] returnValues = new String[3];
		String crumbAvailable = "false";

		BufferedReader reader = null;

		try {
			DescriptorEmailImpl descriptorEmailImpl = new DescriptorEmailImpl();
			String authStr = descriptorEmailImpl.getUnlockuser() + ":"
					+ descriptorEmailImpl.getUnlockpassword();
			String basicAuth = "Basic " + DatatypeConverter
					.printBase64Binary(authStr.getBytes("utf-8"));
			HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url
					.openConnection();
			httpsURLConnection.setRequestMethod("GET");
			httpsURLConnection.setUseCaches(false);
			httpsURLConnection.setDoInput(true);
			httpsURLConnection.setDoOutput(true);
			httpsURLConnection.setRequestProperty("Authorization", basicAuth);

			InputStream inputStream = httpsURLConnection.getInputStream();
			reader = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));
			StringBuilder out = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				out.append(line);
			}

			String crumbValue = out.toString();
			crumbArray = crumbValue.split(":");
			inputStream.close();
			crumbAvailable = "true";

		} catch (IOException ioe) {
			crumbArray[0] = "";
			crumbArray[1] = "";
			crumbAvailable = "false";
			LOGGER.warning("No crumb available: " + ioe.getMessage());

		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				LOGGER.warning(
						"[ERROR]: Unable to close reader" + e.getMessage());
			}
		}

		returnValues[0] = crumbArray[0];
		returnValues[1] = crumbArray[1];
		returnValues[2] = crumbAvailable;

		return returnValues;
	}

}
