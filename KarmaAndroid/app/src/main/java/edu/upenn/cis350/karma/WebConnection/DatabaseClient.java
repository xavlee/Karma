package edu.upenn.cis350.karma.WebConnection;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

/**
 * client for creating HTTP requests to the Node server
 */
public class DatabaseClient {

    private static final String BASE_URL = "http://10.0.2.2:3000";
    private final String TAG = "DatabaseClient";

    /**
     * returns JSON objects back from the Node server (without parameters)
     *
     * @param ctx -- context from the calling activity
     * @param listener -- callback to the calling activity
     * @param endPointUrl -- the specific endpoint that you need the data from on the node side
     */
    public void getRequest(final Context ctx, final FetchDataListener listener,
                           final String endPointUrl) {
        if (listener != null) {
            listener.onFetchStart();
        }

        String url = BASE_URL + endPointUrl;

        StringRequest stringRequest = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        JSONParser parser = new JSONParser();
                        try {
                            JSONObject resp = (JSONObject) parser.parse(response);

                            if (resp.get("error") == null) {
                                listener.onFetchComplete(resp);
                            } else {
                                listener.onFetchFailure((String) resp.get("error"));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            listener.onFetchFailure("Network Connectivity Problem");

                        } else if (error.networkResponse != null &&
                                error.networkResponse.data != null) {

                            VolleyError volley_error = new VolleyError(new String(error
                                    .networkResponse.data));
                            String err = volley_error.getMessage();

                            if (listener != null) {
                                listener.onFetchFailure(err);
                            }

                        } else {
                            listener.onFetchFailure("Something went wrong. " +
                                    "Please try again later");
                        }
                    }
                });

        RequestQueueSingleton.getInstance(ctx.getApplicationContext())
                .addToRequestQueue(stringRequest);
    }

    /**
     * creates a post request to the node server (includes parameters as a json object)
     *
     * @param ctx -- calling activity context
     * @param listener -- callback listener for the calling activity
     * @param params -- params for the request
     * @param endPointUrl -- the endpoint needed for the node server
     */
    public void postRequest(final Context ctx, final FetchDataListener listener,
                            final Map<String, String> params, final String endPointUrl) {
        if (listener != null) {
            listener.onFetchStart();
        }

        String url = BASE_URL + endPointUrl;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (listener != null) {
                                JSONParser parser = new JSONParser();
                                JSONObject resp = (JSONObject) parser.parse(response);

                                if (resp.get("error") == null) {
                                    listener.onFetchComplete(resp);
                                } else {
                                    listener.onFetchFailure((String) resp.get("error"));
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    listener.onFetchFailure("Network Connectivity Problem");

                } else if (error.networkResponse != null &&
                        error.networkResponse.data != null) {

                    VolleyError volley_error = new VolleyError(new String(error
                            .networkResponse.data));
                    String err = volley_error.getMessage();

                    if (listener != null) {
                        listener.onFetchFailure(err);
                    }

                } else {
                    error.printStackTrace();
                    listener.onFetchFailure("Something went wrong. " +
                            "Please try again later");
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

        };

        RequestQueueSingleton.getInstance(ctx.getApplicationContext())
                .addToRequestQueue(postRequest.setShouldCache(false));
    }
}

