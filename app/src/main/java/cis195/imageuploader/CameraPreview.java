package cis195.imageuploader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cis195.imageuploader.R.id.imageView;

public class CameraPreview extends AppCompatActivity {


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    @BindView(R.id.editText) EditText description;
    @BindView(imageView) ImageView preview;
    private Activity activityContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        ButterKnife.bind(this);
    }

    public void camera(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            preview.setImageBitmap(imageBitmap);
        }
    }

    public void cancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void submit(View view) {
        if (description.getText().toString().isEmpty()) {
            Toast.makeText(this, "Image needs a description!", Toast.LENGTH_LONG).show();
        } else if (preview.getDrawable() == null) {
            Toast.makeText(this, "Take an image first!", Toast.LENGTH_LONG).show();
        }
        else if(description.getText().toString().contains("?")) {
            Toast.makeText(this, "please avoid questionmarks in the description!", Toast.LENGTH_LONG).show();
        } else {
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            String stringDate = dateFormat.format(date).toString();
            final String imageName = stringDate + "?" + description.getText().toString();
            byte[] data = new byte[0];
            try {
                data = imageName.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            final String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            Bitmap bitmap = ((BitmapDrawable)preview.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), byteArray);
            RedisService.getService().postImage(base64, reqFile).enqueue(new Callback<RedisService.SetResponse>() {
                @Override
                public void onResponse(Call<RedisService.SetResponse> call, Response<RedisService.SetResponse> response) {
                    // Adding .jpg to the end informs Webdis of the correct content encoding
                    String url = "http://ec2-34-197-228-35.compute-1.amazonaws.com/7bfabef848a997c6bcfd43595e43600d/GET/" + base64 + ".jpg";
                    Toast.makeText(activityContext, "Success!", Toast.LENGTH_LONG).show();
                    Intent main = new Intent(activityContext, MainActivity.class);
                    startActivity(main);
                }

                @Override
                public void onFailure(Call<RedisService.SetResponse> call, Throwable t) {
                    Toast.makeText(activityContext, t.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
