package edu.upenn.cis350.karma.WebConnection;

import android.content.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.upenn.cis350.karma.VendorSide.Order;

/**
 * Wrapper for the DatabaseClient class to hide url endpoints and make database calls easier
 */
public class DataProcessor {
    DatabaseClient client;

    public DataProcessor() {
        client = new DatabaseClient();
    }

    /**
     * checks login credentials
     * @param email
     * @param password
     * @param ctx
     * @param listener
     */
    public void checkLoginCredentials(String email, String password, final Context ctx,
                                 final FetchDataListener listener) {
        String endPointUrl = "/checkcustomerlogin";

        // build parameters to send with the request
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        client.postRequest(ctx, listener, params, endPointUrl);
    }

    public void checkVendorLoginCredentials(String email, String password, final Context ctx,
                                      final FetchDataListener listener) {
        String endPointUrl = "/checkvendorlogin";

        // build parameters to send with the request
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        client.postRequest(ctx, listener, params, endPointUrl);
    }

    public void signUpUser(String first, String last, String email, String password, final Context ctx,
                           final FetchDataListener listener) {
        String endPointUrl = "/signupuser";

        // build parameters to send with the request
        Map<String, String> params = new HashMap<>();
        params.put("password", password);
        params.put("email", email);
        params.put("firstName", first);
        params.put("lastName", last);

        client.postRequest(ctx, listener, params, endPointUrl);
    }

    public void getVendorOrders(String email, final Context ctx, final FetchDataListener listener) {
        String endPointUrl = "/vendororderslist";

        // build parameters to send with the request
        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        client.postRequest(ctx, listener, params, endPointUrl);
    }

    public void getVendorInfo(final Context ctx, final FetchDataListener listener) {
        String endPointUrl = "/vendorinfo";
        client.getRequest(ctx, listener, endPointUrl);
    }

    public void getVendorMenu(String email, final Context ctx, final FetchDataListener listener) {
        String endPointUrl = "/getvendormenu";
        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        client.postRequest(ctx, listener, params, endPointUrl);
    }

    public void postNewOrder(Order order, final Context ctx, final FetchDataListener listener) {
        String endPointUrl = "/placeneworder";

        Map<String, String> params = new HashMap<>();
        System.out.println(order.getUser());
        params.put("user", order.getUser());
        params.put("vendor", order.getVendor());
        params.put("total", order.getTotal().toString());
        params.put("code", order.getCode());
        params.put("time", order.getTimestamp());
        params.put("items", order.getItemsJS());
        client.postRequest(ctx, listener, params, endPointUrl);
    }

    public void completeOrder(String timestamp, final Context ctx,
                              final FetchDataListener listener) {
        String endPointUrl = "/completeorder";

        Map<String, String> params = new HashMap<>();
        params.put("timestamp", timestamp);

        client.postRequest(ctx, listener, params, endPointUrl);
    }

    public void getUserOrders(String email, final Context ctx, final FetchDataListener listener) {
        String endPointUrl = "/userorderslist";

        // build parameters to send with the request
        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        client.postRequest(ctx, listener, params, endPointUrl);
    }

    public void getUserFeed(String email, final Context ctx, final FetchDataListener listener) {
        String endPointUrl = "/getuserfeed";

        Map<String, String>  params = new HashMap<>();
        params.put("email", email);

        client.postRequest(ctx, listener, params, endPointUrl);
    }

    public void getUserProfile(String email, final Context ctx, final FetchDataListener listener) {
        String endPointUrl = "/getuserprofile";

        Map<String, String>  params = new HashMap<>();
        params.put("email", email);

        client.postRequest(ctx, listener, params, endPointUrl);
    }

    public void addFriend(String user, String followUser, final Context ctx, final FetchDataListener listener) {
        String endPointUrl = "/addfriend";

        Map<String, String> params = new HashMap<>();
        params.put("user", user);
        params.put("followUser", followUser);
        client.postRequest(ctx, listener, params, endPointUrl);
    }

}
