package edu.upenn.cis350.karma.WebConnection;

import org.json.simple.JSONObject;

/**
 * Callback interface for fetching data in the database client
 */
public interface FetchDataListener {
    // call when the request is completed
    void onFetchComplete(JSONObject data);

    // call when the request fails
    void onFetchFailure(String msg);

    // called when the fetch is starting (for showing progress bar if needed)
    void onFetchStart();
}
