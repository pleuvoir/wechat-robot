package io.github.pleuvoir.utils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtils {
	
	static{
		System.setProperty("jsse.enableSNIExtension", "false");
		System.setProperty("https.protocols", "TLSv1");
	}
	
    private final static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    public static final String CHARSET = "UTF-8";
    private static ThreadLocal<Map<String, String>> httpHeader = new ThreadLocal<Map<String, String>>();
    private static ThreadLocal<Map<String, Object>> httpClientConfig = new ThreadLocal<Map<String, Object>>();
    private static CookieStore cookieStore;
    private static ThreadLocal<List<Cookie>> httpCookile = new ThreadLocal<>();

    //连接超时时间
    public static final String CONNECT_TIMEOUT = "connect_timeout";
    //socket超时时间
    public static final String SOCKET_TIMEOUT = "socket_timeout";
	public static final Integer DEFAULT_CONNECT_TIMEOUT = 2 * 60 * 1000; //2分钟
    public static final Integer DEFAULT_SOCKET_TIMEOUT = 2 * 60 * 1000;

    public static CloseableHttpClient buildHttpClient() {
        Map<String, Object> configSetting = new HashMap<String, Object>();
        if (httpClientConfig != null && null != httpClientConfig.get()) {
            configSetting = httpClientConfig.get();
        }
        RequestConfig.Builder builder = RequestConfig.custom();
        Integer connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        if (configSetting.get(CONNECT_TIMEOUT) != null) {
            try {
                connectTimeout = Integer.valueOf(configSetting.get(CONNECT_TIMEOUT).toString());
            } catch (Exception e) {
                logger.warn("class covert error!", e);
                connectTimeout = DEFAULT_CONNECT_TIMEOUT;
            }
        }
        builder.setConnectTimeout(connectTimeout);

        Integer socketTimeout = DEFAULT_SOCKET_TIMEOUT;
        if (configSetting.get(SOCKET_TIMEOUT) != null) {
            try {
                socketTimeout = Integer.valueOf(configSetting.get(SOCKET_TIMEOUT).toString());
            } catch (Exception e) {
                logger.warn("class covert error!", e);
                socketTimeout = DEFAULT_SOCKET_TIMEOUT;
            }
        }
        builder.setSocketTimeout(socketTimeout);
        RequestConfig config = builder.build();
        cookieStore = new BasicCookieStore();
        return HttpClientBuilder.create().setDefaultRequestConfig(config).setDefaultCookieStore(cookieStore).build();
    }

    public static String doGet(String url, Map<String, String> params) throws IOException {
        return doGet(url, params, CHARSET);
    }

    public static String doPost(String url, Map<String, String> params) throws IOException {
        return doPost(url, params, CHARSET);
    }

    /**
     * HTTP Get 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doGet(String url, Map<String, String> params, String charset) throws IOException {
        if (TextUtils.isBlank(url)) {
            return null;
        }
        HttpGet httpGet = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            httpGet = new HttpGet(url);
            handlerHeader(httpGet);

            httpClient = buildHttpClient();
            response = httpClient.execute(httpGet);

            List<Cookie> cookies = cookieStore.getCookies();
            if (null != cookies) {
                httpCookile.set(cookies);
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
        	httpGet.abort();
            if (response != null) {
                response.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }

    private static void handlerHeader(HttpRequestBase requestBase) {
        if (httpHeader != null && httpHeader.get() != null) {
            Map<String, String> map = httpHeader.get();
            for (String key : map.keySet()) {
                requestBase.addHeader(key, map.get(key));
            }
        }
    }

    public static String getCookie(String name) {
        if (null==name || name.length() <= 0) {
            return null;
        }

        if (null != httpCookile && null != httpCookile.get()) {
            List<Cookie> cookies = httpCookile.get();
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static String getCookie() {
        if (null != httpCookile && null != httpCookile.get()) {
            List<Cookie> cookieList = httpCookile.get();
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<cookieList.size(); i++) {
                if (i == cookieList.size()-1) {
                    sb.append(cookieList.get(i).getName() + "=" + cookieList.get(i).getValue());
                } else  {
                    sb.append(cookieList.get(i).getName() + "=" + cookieList.get(i).getValue() + "; ");
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doPost(String url, Map<String, String> params, String charset) throws IOException {
        if (TextUtils.isBlank(url)) {
            return null;
        }
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            HttpPost httpPost = new HttpPost(url);
            handlerHeader(httpPost);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
            }
            httpClient = buildHttpClient();
            response = httpClient.execute(httpPost);

            List<Cookie> cookies = cookieStore.getCookies();
            if (null != cookies) {
                httpCookile.set(cookies);
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
            }
            EntityUtils.consume(entity);
            return result;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url       请求的url地址
     * @param jsonParam 请求的JSON参数
     * @return
     */
    public static String doPost(String url, String jsonParam) throws IOException {
        if (TextUtils.isBlank(url)) {
            return null;
        }
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            handlerHeader(httpPost);
            if (!TextUtils.isBlank(jsonParam)) {
                StringEntity entity = new StringEntity(jsonParam, CHARSET);
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json; charset=utf-8");
                httpPost.setHeader("Accept", "application/json");
                httpPost.setEntity(entity);
            }
            httpClient = buildHttpClient();
            response = httpClient.execute(httpPost);

            List<Cookie> cookies = cookieStore.getCookies();
            if (null != cookies) {
                httpCookile.set(cookies);
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }
    
    public static byte[] getAsBytes(String url) {
		CloseableHttpResponse response = null;
		try {
			URIBuilder urlBuilder = new URIBuilder();
			urlBuilder.setPath(url);
			URI uri = urlBuilder.build();
			HttpGet httpGet = new HttpGet(uri);
			response = buildHttpClient().execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toByteArray(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


    public static void setHeader(Map<String, String> header) {
        if (header != null) {
            httpHeader.set(header);
        }
    }

    public static void setConfig(Map<String, Object> config) {
        if (config != null) {
            httpClientConfig.set(config);
        }
    }
}