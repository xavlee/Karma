package edu.upenn.cis350.karma.VendorSide;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import edu.upenn.cis350.karma.FoodItem;

public class Order implements Serializable {
    private static final String PARSE_TAG = "OrderParsing";
    private String vendor;
    private String user;
    private String timestamp;
    private List<FoodItem> items;
    private Long total;
    private String code;

    public Order(String vendor, String user, String timestamp, List<FoodItem> items, Long total,
                 String code) {
        this.vendor = vendor;
        this.user = user;
        this.timestamp = timestamp;
        this.items = items;
        this.total = total;
        this.code = code;
    }

    public Order(String vendor, String user, List<FoodItem> items) {
        this.vendor = vendor;
        this.user = user;
        this.items = items;
    }

    public Order(JSONObject currOrderObj) {
        this.vendor = (String) currOrderObj.get("vendor");
        this.user = (String) currOrderObj.get("user");
        String rawTime = (String) currOrderObj.get("createdAt");

        /*
        rawTime = rawTime.substring(0, rawTime.indexOf('Z'));
        rawTime += "+00.00";
         */

        this.timestamp = rawTime;

        this.total = (Long) currOrderObj.get("total");
        this.code = (String) currOrderObj.get("code");
        this.items = new ArrayList<>();
        JSONArray itemArray = (JSONArray) currOrderObj.get("items");

        Iterator itemIterator = itemArray.iterator();

        while (itemIterator.hasNext()) {
            JSONObject curr = (JSONObject) itemIterator.next();
            String itemName = (String) curr.get("item");
            Long price = (Long) curr.get("price");
            Long quantity = (Long) curr.get("quantity");
            items.add(new FoodItem(itemName, price, quantity));
        }
    }

    public String getVendor() {
        return vendor;
    }

    public String getUser() {
        return user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getFirstItem() {
        if (!items.isEmpty()) {
            return items.get(0).getItem();
        } else {
            return "Empty order";
        }
    }

    public String getItems() {
        String itemList = "";
        for (FoodItem item : items) {
            itemList += item.getItem() + ": " + item.getQuantity() + ", ";
        }
        return itemList.substring(0, itemList.lastIndexOf(','));
    }

    public String getItemsJS() {
        StringBuilder sb = new StringBuilder();

        for (FoodItem item : items) {
            sb.append(item.getItem() + ":" + item.getPrice() + ":" + item.getQuantity());
            sb.append("!");
        }
        return sb.toString();
    }

    public Long getTotal() {
        return total;
    }

    public String getCode() {
        return code;
    }
}
