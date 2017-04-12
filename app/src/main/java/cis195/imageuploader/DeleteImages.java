package cis195.imageuploader;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by arelin on 4/10/17.
 */

class DeleteImages extends AsyncTask<String, Object, Void> {


    public interface AsyncResponse {
        void processFinish(Boolean output);
    }

    AsyncResponse asyncResponse = null;

    public DeleteImages(AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
    }

    @Override
    protected Void doInBackground(String... urls) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void nothing) {
        super.onPostExecute(nothing);
        asyncResponse.processFinish(true);
    }
}
