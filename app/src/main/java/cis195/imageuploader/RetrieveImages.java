package cis195.imageuploader;

import android.os.AsyncTask;
import android.util.Base64;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by arelin on 4/10/17.
 */

class RetrieveImages extends AsyncTask<String, Void, List<ListItem> > {

    private Exception exception;

    public interface AsyncResponse {
        void processFinish(Boolean output, List<ListItem> items);
    }

    AsyncResponse asyncResponse = null;

    public RetrieveImages(AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
    }

    protected List<ListItem> doInBackground(String... urls) {
        List<ListItem> redisList = new ArrayList<>();
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();

            okhttp3.Response response = null;
            JsonObject jo = null;
            response = client.newCall(request).execute();
            JsonParser jsonParser = new JsonParser();
            jo = (JsonObject)jsonParser.parse(response.body().string());

            for(JsonElement s : jo.getAsJsonArray("KEYS")) {
                String base64Key = s.toString();
                String url = "http://ec2-34-197-228-35.compute-1.amazonaws.com/7bfabef848a997c6bcfd43595e43600d/GET/"
                        + base64Key + ".jpg";
                url = url.replace("\"", "");
                byte[] data = Base64.decode(base64Key, Base64.DEFAULT);
                String text = new String(data, "UTF-8");

                String[] split = text.split("\\?");
                redisList.add(new ListItem(split[1], split[0], url));
                System.out.println(split[1] + " " + split[0] + " " + url);
            }
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
        return redisList;
    }

    protected void onPostExecute(List<ListItem> items) {
        if(exception != null) {
            exception.printStackTrace();
        }

        super.onPostExecute(items);
        asyncResponse.processFinish(true, items);
    }
}
