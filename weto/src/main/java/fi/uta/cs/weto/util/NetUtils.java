package fi.uta.cs.weto.util;

import com.google.common.net.HttpHeaders;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import org.apache.http.Header;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

public class NetUtils
{
  public static final String HDR_USER_AGENT
                                     = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/75.0.3770.90 Chrome/75.0.3770.90 Safari/537.36";
  public static final String HDR_ACCEPT
                                     = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3";
  public static final String HDR_ACCEPT_LANG = "fi-FI,fi;q=0.9,en;q=0.8";
  public static final String HDR_FORM_CONTENT
                                     = "application/x-www-form-urlencoded";

  private static final int CONN_TIMEOUT = 5000;
  private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
          .setConnectionRequestTimeout(CONN_TIMEOUT).setConnectTimeout(
          CONN_TIMEOUT).setSocketTimeout(CONN_TIMEOUT).setCookieSpec(
          CookieSpecs.DEFAULT).build();

  public static class LocationAndResult
  {
    public String location;
    public String result;

    public LocationAndResult(String loc, String res)
    {
      location = loc;
      result = res;
    }

  }

  public static BasicCookieStore buildCookieStore(String cookiesJsonStr)
  {
    BasicCookieStore cookies = new BasicCookieStore();
    if(cookiesJsonStr != null)
    {
      JsonArray cookiesArray = new JsonParser().parse(cookiesJsonStr)
              .getAsJsonArray();
      for(JsonElement cookieElem : cookiesArray)
      {
        JsonObject cookieObj = cookieElem.getAsJsonObject();
        String name = cookieObj.get("name").getAsString();
        String value = cookieObj.get("value").getAsString();
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(cookieObj.get("domain").getAsString());
        cookie.setPath(cookieObj.get("path").getAsString());
        if(cookieObj.has("expiryDate"))
        {
          String expiryDateStr = cookieObj.get("expiryDate").getAsString();
          cookie.setExpiryDate(DateUtils.parseDate(expiryDateStr));
        }
        cookies.addCookie(cookie);
      }
    }
    return cookies;
  }

  public static LocationAndResult httpGet(CloseableHttpClient httpClient,
          HttpClientContext httpContext, String url, String[][] extraHeaders)
          throws Exception
  {
    LocationAndResult locRes = new LocationAndResult(url, "");
    boolean redirect = true;
    while(redirect)
    {
      redirect = false;
      HttpGet request = new HttpGet(url);
      request.setConfig(REQUEST_CONFIG);
      request.addHeader(HttpHeaders.USER_AGENT, HDR_USER_AGENT);
      if(extraHeaders != null)
      {
        for(String[] header : extraHeaders)
        {
          request.addHeader(header[0], header[1]);
        }
      }
      try( CloseableHttpResponse response = httpClient.execute(request,
              httpContext))
      {
        if((300 <= response.getStatusLine().getStatusCode()) && (response
                .getStatusLine().getStatusCode() < 400))
        {
          Header loc = response.getFirstHeader(HttpHeaders.LOCATION);
          url = loc.getValue();
          redirect = true;
        }
        else
        {
          locRes.location = url;
          if(response.getEntity() != null)
          {
            locRes.result = EntityUtils.toString(response.getEntity(), "UTF-8");
          }
        }
      }
    }
    return locRes;
  }

  public static LocationAndResult httpGet(CloseableHttpClient httpClient,
          HttpClientContext httpContext, String url)
          throws Exception
  {
    return NetUtils.httpGet(httpClient, httpContext, url, null);
  }

  public static LocationAndResult httpGet(String url, String[][] extraHeaders,
          String cookiesJsonStr)
          throws Exception
  {
    LocationAndResult locRes = new LocationAndResult("", "");
    try( CloseableHttpClient httpClient = HttpClientBuilder.create()
            .disableRedirectHandling().build())
    {
      HttpClientContext httpContext = HttpClientContext.create();
      httpContext.setCookieStore(buildCookieStore(cookiesJsonStr));
      locRes = NetUtils.httpGet(httpClient, httpContext, url, extraHeaders);
    }
    return locRes;
  }

  public static LocationAndResult httpGet(String url, String cookiesJsonStr)
          throws Exception
  {
    return NetUtils.httpGet(url, null, cookiesJsonStr);
  }

  public static LocationAndResult httpGet(String url)
          throws Exception
  {
    return NetUtils.httpGet(url, null, null);
  }

  public static LocationAndResult httpPost(CloseableHttpClient httpClient,
          HttpClientContext httpContext, String url, final String parString)
          throws Exception
  {
    LocationAndResult locRes = new LocationAndResult(url, "");
    HttpPost request = new HttpPost(url);
    request.setConfig(REQUEST_CONFIG);
    request.addHeader(HttpHeaders.USER_AGENT, HDR_USER_AGENT);
    request.addHeader(HttpHeaders.ACCEPT, HDR_ACCEPT);
    request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, HDR_ACCEPT_LANG);
    URI uri = new URI(url);
    request.addHeader(HttpHeaders.HOST, uri.getHost());
    request.addHeader(HttpHeaders.ORIGIN, uri.getScheme() + "://" + uri
            .getHost());
    request.addHeader(HttpHeaders.REFERER, url);
    request.addHeader(HttpHeaders.CONTENT_TYPE, HDR_FORM_CONTENT);
    request.setEntity(new StringEntity(parString, "UTF-8"));
    try( CloseableHttpResponse response = httpClient
            .execute(request, httpContext))
    {
      if((300 <= response.getStatusLine().getStatusCode()) && (response
              .getStatusLine().getStatusCode() < 400))
      {
        Header loc = response.getFirstHeader(HttpHeaders.LOCATION);
        url = loc.getValue();
        locRes = NetUtils.httpGet(httpClient, httpContext, url, null);
      }
      else if(response.getEntity() != null)
      {
        locRes.result = EntityUtils.toString(response.getEntity(), "UTF-8");
      }
    }
    return locRes;
  }

  public static LocationAndResult httpPut(String url, String[][] extraHeaders)
          throws Exception
  {
    LocationAndResult locRes = new LocationAndResult("", "");
    try( CloseableHttpClient httpClient = HttpClientBuilder.create()
            .disableRedirectHandling().build())
    {
      HttpClientContext httpContext = HttpClientContext.create();
      httpContext.setCookieStore(new BasicCookieStore());
      HttpPut request = new HttpPut(url);
      request.setConfig(REQUEST_CONFIG);
      request.addHeader(HttpHeaders.USER_AGENT, HDR_USER_AGENT);
      if(extraHeaders != null)
      {
        for(String[] header : extraHeaders)
        {
          request.addHeader(header[0], header[1]);
        }
      }
      try( CloseableHttpResponse response = httpClient.execute(request,
              httpContext))
      {
        if((300 <= response.getStatusLine().getStatusCode()) && (response
                .getStatusLine().getStatusCode() < 400))
        {
          Header loc = response.getFirstHeader(HttpHeaders.LOCATION);
          url = loc.getValue();
          locRes = NetUtils.httpGet(httpClient, httpContext, url, null);
        }
        else if(response.getEntity() != null)
        {
          locRes.result = EntityUtils.toString(response.getEntity(), "UTF-8");
        }
      }
    }
    return locRes;
  }

  public static LocationAndResult httpPut(String url, String[][] extraHeaders,
          String data)
          throws Exception
  {
    LocationAndResult locRes = new LocationAndResult("", "");
    try( CloseableHttpClient httpClient = HttpClientBuilder.create()
            .disableRedirectHandling().build())
    {
      HttpClientContext httpContext = HttpClientContext.create();
      httpContext.setCookieStore(new BasicCookieStore());
      HttpPut request = new HttpPut(url);
      request.setConfig(REQUEST_CONFIG);
      request.addHeader(HttpHeaders.USER_AGENT, HDR_USER_AGENT);
      if(extraHeaders != null)
      {
        for(String[] header : extraHeaders)
        {
          request.addHeader(header[0], header[1]);
        }
      }
      request.setEntity(new StringEntity(data, "UTF-8"));
      try( CloseableHttpResponse response = httpClient.execute(request,
              httpContext))
      {
        if((300 <= response.getStatusLine().getStatusCode()) && (response
                .getStatusLine().getStatusCode() < 400))
        {
          Header loc = response.getFirstHeader(HttpHeaders.LOCATION);
          url = loc.getValue();
          locRes = NetUtils.httpGet(httpClient, httpContext, url, null);
        }
        else if(response.getEntity() != null)
        {
          locRes.result = EntityUtils.toString(response.getEntity(), "UTF-8");
        }
      }
    }
    return locRes;
  }

  public static LocationAndResult httpDelete(String url, String[][] extraHeaders)
          throws Exception
  {
    LocationAndResult locRes = new LocationAndResult("", "");
    try( CloseableHttpClient httpClient = HttpClientBuilder.create()
            .disableRedirectHandling().build())
    {
      HttpClientContext httpContext = HttpClientContext.create();
      httpContext.setCookieStore(new BasicCookieStore());
      HttpDelete request = new HttpDelete(url);
      request.setConfig(REQUEST_CONFIG);
      request.addHeader(HttpHeaders.USER_AGENT, HDR_USER_AGENT);
      if(extraHeaders != null)
      {
        for(String[] header : extraHeaders)
        {
          request.addHeader(header[0], header[1]);
        }
      }
      try( CloseableHttpResponse response = httpClient.execute(request,
              httpContext))
      {
        if((300 <= response.getStatusLine().getStatusCode()) && (response
                .getStatusLine().getStatusCode() < 400))
        {
          Header loc = response.getFirstHeader(HttpHeaders.LOCATION);
          url = loc.getValue();
          locRes = NetUtils.httpGet(httpClient, httpContext, url, null);
        }
        else if(response.getEntity() != null)
        {
          locRes.result = EntityUtils.toString(response.getEntity(), "UTF-8");
        }
      }
    }
    return locRes;
  }

}
