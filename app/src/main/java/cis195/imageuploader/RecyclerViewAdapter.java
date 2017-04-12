package cis195.imageuploader;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by arelin on 4/7/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ImageInformation> {



    private List<ListItem> _images;

    RecyclerViewAdapter(List<ListItem> images){
        _images = images;
    }


    public static class ImageInformation extends RecyclerView.ViewHolder implements View.OnLongClickListener  {
        @BindView(R.id.cv) CardView cv;
        @BindView(R.id.image) ImageView image;
        @BindView(R.id.description) TextView description;
        @BindView(R.id.date) TextView date;

        ImageInformation(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            cv.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle ("Delete")
                    .setMessage ("Would you like to delete?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String imageName = date.getText().toString() + "?" +
                                    description.getText().toString();
                            byte[] data = new byte[0];
                            try {
                                data = imageName.getBytes("UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            final String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                            new DeleteImages(new DeleteImages.AsyncResponse() {
                                public void processFinish(Boolean output) {
                                    Intent main = new Intent(builder.getContext(), MainActivity.class);
                                    builder.getContext().startActivity(main);
                                }
                            }).execute("http://ec2-34-197-228-35.compute-1.amazonaws.com/" +
                                    "7bfabef848a997c6bcfd43595e43600d/DEL/" + base64 );
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                     }});

            builder.create().show();
            return true;
        }
    }

    @Override
    public int getItemCount() {
        return _images.size();
    }

    @Override
    public ImageInformation onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        ImageInformation pvh = new ImageInformation(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ImageInformation imageInformationHolder, int i) {
        System.out.println(Integer.toString(i) + " times");
        imageInformationHolder.date.setText(_images.get(i)._postingTime);
        imageInformationHolder.description.setText(_images.get(i)._text);
        Picasso.with(imageInformationHolder.cv.getContext()).load(_images.get(i)._link).into(
                imageInformationHolder.image);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

