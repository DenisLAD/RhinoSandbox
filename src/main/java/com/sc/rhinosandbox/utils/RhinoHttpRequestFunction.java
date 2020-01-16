/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox.utils;

import com.sc.rhinosandbox.annotations.RhinoFunction;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.UniqueTag;

/**
 *
 * @author lucifer
 */
@RhinoFunction("httpRequest")
public class RhinoHttpRequestFunction extends BaseFunction {

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (args.length < 1) {
            throw new RuntimeException("Function require at lest one parameter");
        }
        if (!(args[0] instanceof ScriptableObject)) {
            throw new RuntimeException("Function require argument type of object");
        }
        ScriptableObject arg = (ScriptableObject) args[0];

        String method = getMethod(arg).toUpperCase();
        String url = getString("url", arg);
        String body = getString("body", arg);
        Map<String, String> cookies = getCookies(arg);
        Map<String, String> headers = getHeaders(arg);

        CloseableHttpClient client = HttpClients.createDefault();

        try {
            HttpRequestBase request = null;

            switch (method) {
                case "POST":
                    HttpPost post;
                    request = post = new HttpPost(url);
                    post.setEntity(new StringEntity(body));
                    break;
                case "GET":
                    request = new HttpGet(url);
                    break;
                case "OPTIONS":
                    request = new HttpOptions(url);
                    break;
                case "DELETE":
                    request = new HttpDelete(url);
                    break;
                case "PUT":
                    HttpPut put;
                    request = put = new HttpPut(url);
                    put.setEntity(new StringEntity(body));
                    break;
                case "HEAD":
                    request = new HttpHead(url);
                    break;
                case "PATCH":
                    HttpPatch patch;
                    request = patch = new HttpPatch(url);
                    patch.setEntity(new StringEntity(body));
                    break;
                case "TRACE":
                    request = new HttpTrace(url);
                    break;
                default:
                    throw new RuntimeException("Method " + method + " not supported");
            }

            setHeaders(request, headers);
            setCookies(request, cookies);

            CloseableHttpResponse response = client.execute(request);

            ScriptableObject res = cx.initStandardObjects();

            parseHeaders(res, response);
            parseCookies(res, response);

            String entity = EntityUtils.toString(response.getEntity());

            res.put("body", res, entity);
            res.put("statusCode", res, response.getStatusLine().getStatusCode());
            res.put("statusStr", res, response.getStatusLine().getReasonPhrase());

            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void parseCookies(ScriptableObject res, CloseableHttpResponse response) {
        ScriptableObject so = Context.getCurrentContext().initStandardObjects();
        res.put("cookies", res, so);

        if (response.getHeaders("Set-Cookie").length < 1) {
            return;
        }

        Header[] elements = response.getHeaders("Set-Cookie");
        for (Header header : elements) {
            String parts[] = header.getValue().split("=", 2);
            so.put(parts[0], so, parts[1].split(";", 2)[0]);
        }
    }

    private void parseHeaders(ScriptableObject res, CloseableHttpResponse response) {
        ScriptableObject so = Context.getCurrentContext().initStandardObjects();
        res.put("headers", res, so);
        if (response.getAllHeaders().length == 0) {
            return;
        }

        for (Header header : response.getAllHeaders()) {
            Object val = so.get(header.getName(), so);
            if (val == null || val == UniqueTag.NOT_FOUND || val == UniqueTag.NULL_VALUE) {
                val = header.getValue();
            } else {
                val = val.toString() + "; " + header.getValue();
            }
            so.put(header.getName(), so, val);
        }
    }

    private void setCookies(HttpRequestBase request, Map<String, String> cookies) {
        if (cookies.isEmpty()) {
            return;
        }
        String cookieString = "";

        for (String key : cookies.keySet()) {
            cookieString += key + "=" + cookies.get(key) + "; ";
        }

        request.setHeader("Cookie", cookieString);
    }

    private void setHeaders(HttpRequestBase request, Map<String, String> headers) {
        for (String key : headers.keySet()) {
            request.setHeader(key, headers.get(key));
        }
    }

    private Map<String, String> getHeaders(ScriptableObject arg) {
        Map<String, String> headers = new HashMap<>();

        Object tmp = arg.get("headers", arg);

        if (tmp == null || tmp == UniqueTag.NOT_FOUND || tmp == UniqueTag.NULL_VALUE) {
            return headers;
        }

        if (!(tmp instanceof ScriptableObject)) {
            throw new RuntimeException("HEADERS should be an Object");
        }

        ScriptableObject so = (ScriptableObject) tmp;
        for (Object idt : so.getIds()) {
            String id = idt.toString();
            Object value = so.get(id, so);
            if (value == null || value == UniqueTag.NOT_FOUND || value == UniqueTag.NULL_VALUE) {
                continue;
            }
            headers.put(id, value.toString());
        }

        return headers;
    }

    private Map<String, String> getCookies(ScriptableObject arg) {
        Map<String, String> cookies = new HashMap<>();

        Object tmp = arg.get("cookies", arg);

        if (tmp == null || tmp == UniqueTag.NOT_FOUND || tmp == UniqueTag.NULL_VALUE) {
            return cookies;
        }

        if (!(tmp instanceof ScriptableObject)) {
            throw new RuntimeException("COOKIES should be an Object");
        }

        ScriptableObject so = (ScriptableObject) tmp;
        for (Object idt : so.getIds()) {
            String id = idt.toString();
            Object value = so.get(id, so);
            if (value == null || value == UniqueTag.NOT_FOUND || value == UniqueTag.NULL_VALUE) {
                continue;
            }
            cookies.put(id, value.toString());
        }

        return cookies;
    }

    private String getMethod(ScriptableObject arg) {
        return getString("method", arg);
    }

    private String getString(String name, ScriptableObject arg) {
        Object method = arg.get(name, arg);
        if (method == UniqueTag.NOT_FOUND || method == null || method == UniqueTag.NOT_FOUND) {
            method = "";
        }
        if (!(method instanceof String)) {
            method = method.toString();
        }

        return (String) method;
    }

}
