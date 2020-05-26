package edu.upenn.cis350.karma.WebConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * singleton request queue for the HTTP requests
 */
public class RequestQueueSingleton {
        private static RequestQueueSingleton instance;
        private RequestQueue requestQueue;
        private ImageLoader imageLoader;
        private static Context ctx;

        private RequestQueueSingleton(Context context) {
            ctx = context;
            requestQueue = getRequestQueue();

            imageLoader = new ImageLoader(requestQueue,
                    new ImageLoader.ImageCache() {

                        // for image loading -- might use later
                        private final LruCache<String, Bitmap>
                                cache = new LruCache<String, Bitmap>(20);

                        @Override
                        public Bitmap getBitmap(String url) {
                            return cache.get(url);
                        }

                        @Override
                        public void putBitmap(String url, Bitmap bitmap) {
                            cache.put(url, bitmap);
                        }
                    });
        }

        public static synchronized RequestQueueSingleton getInstance(Context context) {
            if (instance == null) {
                instance = new RequestQueueSingleton(context);
            }
            return instance;
        }

        public RequestQueue getRequestQueue() {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
            }
            return requestQueue;
        }

        // use this to add a request to the queue, so it can handle multiple requests in a row
        public <T> void addToRequestQueue(Request<T> req) {
            getRequestQueue().add(req);
        }

        // for images later
        public ImageLoader getImageLoader() {
            return imageLoader;
        }
    }