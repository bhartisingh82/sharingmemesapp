package com.example.memssharing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.memssharing.databinding.ActivityMainBinding;
import com.google.android.material.color.utilities.Variant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.ReferenceQueue;

public class MainActivity extends AppCompatActivity {




    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getmeme();
        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getmeme();
            }

        });

        binding.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharememe();
            }
        });

    }

    private void getmeme() {

        String url = " https://meme-api.com/gimme";

        binding.loader.setVisibility(View.VISIBLE);
        binding.memeImageView.setVisibility(View.GONE);

        RequestQueue que = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String imgUrl = response.getString("url");
                            Glide.with(getApplicationContext())
                                    .load(imgUrl)
                                    .into(binding.memeImageView);

                            binding.loader.setVisibility(View.GONE);
                            binding.memeImageView.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                });
        que.add(jsonObjectRequest);


    }

    private void sharememe() {
        Bitmap image = getBitmapFromView(binding.memeImageView);
        shareImageAndText(image);
    }

    private void shareImageAndText(Bitmap image) {

        Uri uri =getImageToShare(image);
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Share Mems BYlinks");
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent,"Share Mems via"));
    }
    private Uri getImageToShare(Bitmap image) {
        File imageFolder =new File(getCacheDir(),"image");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file =new File(imageFolder,"share.png");
            FileOutputStream outputStream =new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG,90,outputStream);
            outputStream.flush();
            outputStream.close();
            uri =FileProvider.getUriForFile(this,"com.bharti.shareImage.fileProvider",file);

        }catch (Exception e){
            Toast.makeText(this,""+ e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return uri;
    }
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with same height and width
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the background view of layout
        Drawable background = view.getBackground();
        if(background != null){
            background.draw(canvas);
        }else{
            canvas.drawColor(Color.WHITE);
        }
        //draw the view on canvas
        view.draw(canvas);

        return returnedBitmap;

    }

}