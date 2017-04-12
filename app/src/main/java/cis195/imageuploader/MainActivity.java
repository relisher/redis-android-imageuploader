package cis195.imageuploader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 0;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL = 1;
    @BindView(R.id.rv) RecyclerView cardViewer;
    LinearLayoutManager llm = new LinearLayoutManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent cameraView = new Intent(this, CameraPreview.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fab);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(cameraView);
            }
        });
        ButterKnife.bind(this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE_EXTERNAL);
        }

        new RetrieveImages(new RetrieveImages.AsyncResponse() {
            @Override
            public void processFinish(Boolean output, List<ListItem> items) {
                RecyclerViewAdapter rVA = new RecyclerViewAdapter(items);
                cardViewer.setAdapter(rVA);
                cardViewer.setLayoutManager(llm);
            }
        }).execute("http://ec2-34-197-228-35.compute-1.amazonaws.com/7bfabef848a997c6bcfd43595e43600d/KEYS/*");
    }







}
