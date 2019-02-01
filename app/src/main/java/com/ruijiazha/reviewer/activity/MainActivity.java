package com.ruijiazha.reviewer.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ruijiazha.reviewer.database.DBHelper;
import com.ruijiazha.reviewer.data.Marker;
import com.ruijiazha.reviewer.R;
import com.ruijiazha.reviewer.data.Review;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity {

    private static final int IMAGE = 1;
    private static final int SCREENSHOT = 5;

    private byte[] img;
    private String imgPath;
    private String positions;
    private String reviewType;
    private String Q1, Q2, Q3;
    private String username;
    private String screenshotTime = null;
    private int rates = 0;
    private boolean refresh = false;

    File image;
    FileInputStream fis;
    Bundle extras;

    Button save,upload;
    ImageView imageView,issueTypeExpandIcon,questionIcon;
    EditText inputReview, inputAppName;
    RatingBar ratingBar;
    RadioGroup issueTypeGroup;
    RadioButton issueType1,issueType2,issueType3,issueType4;
    RadioGroup Q1Group, Q2Group, Q3Group;
    RadioButton Q1A1,Q1A2;
    RadioButton Q2A1,Q2A2,Q2A3;
    RadioButton Q3A1,Q3A2;
    ImageButton view, marker,explain1,explain2,explain3,explain4;
    RelativeLayout cover;
    RelativeLayout relative;
    LinearLayout issueTypeChoice,issueTypeExpand,question,questionExpand;



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(MainActivity.this, "Review Saved", Toast.LENGTH_LONG).show();
                finish();
                if(MarkerActivity.instance != null){
                    MarkerActivity.instance.finish();
                }
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                i.putExtra("entrance", "saveButton");
                startActivity(i);
            } else if (msg.what == 0) {
                Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_LONG).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        marker = (ImageButton) findViewById(R.id.marker);
        save = (Button) findViewById(R.id.saveBtn);
        upload = (Button)findViewById(R.id.upload);
        inputAppName = (EditText) findViewById(R.id.inputAppName);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        inputReview = (EditText) findViewById(R.id.inputReview);
        view = (ImageButton) findViewById(R.id.view);
        cover = (RelativeLayout)findViewById(R.id.cover);
        issueTypeExpand = (LinearLayout) findViewById(R.id.issueTypeExpand);
        issueTypeChoice = (LinearLayout)findViewById(R.id.issueTypeChoice);
        issueTypeExpandIcon = (ImageView)findViewById(R.id.issueTypeExpandIcon);
        explain1 = (ImageButton)findViewById(R.id.explain1);
        explain2 = (ImageButton)findViewById(R.id.explain2);
        explain3 = (ImageButton)findViewById(R.id.explain3);
        explain4 = (ImageButton)findViewById(R.id.explain4);
        question = (LinearLayout)findViewById(R.id.question);
        questionExpand = (LinearLayout)findViewById(R.id.questionExpand);
        questionIcon = (ImageView)findViewById(R.id.questionIcon);
        relative = new RelativeLayout(MainActivity.this);//this layout is used to draw markers

        issueTypeGroup = (RadioGroup) findViewById(R.id.issueTypeGroup);
        issueType1 = (RadioButton) findViewById(R.id.issueType1);
        issueType2 = (RadioButton) findViewById(R.id.issueType2);
        issueType3 = (RadioButton) findViewById(R.id.issueType3);
        issueType4 = (RadioButton) findViewById(R.id.issueType4);

        issueTypeExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(issueTypeChoice.getVisibility()==View.GONE) {
                    issueTypeChoice.setVisibility(View.VISIBLE);
                    issueTypeExpandIcon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                }else{
                    issueTypeChoice.setVisibility(View.GONE);
                    issueTypeExpandIcon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
                }
            }
        });

        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionExpand.getVisibility()==View.GONE) {
                    questionExpand.setVisibility(View.VISIBLE);
                    questionIcon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                }else{
                    questionExpand.setVisibility(View.GONE);
                    questionIcon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
                }
            }
        });

        explain1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Bugs: functional mistakes", Toast.LENGTH_LONG).show();
            }
        });

        explain2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Performance: functional but not good enough", Toast.LENGTH_LONG).show();
            }
        });

        explain3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "UI/User Experience: unpleasant design", Toast.LENGTH_LONG).show();
            }
        });

        explain4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Service Request/Improvement: need to add more service or improve", Toast.LENGTH_LONG).show();
            }
        });

        issueTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == issueType1.getId()) {
                    reviewType = "Bugs";
                } else if (checkedId == issueType2.getId()) {
                    reviewType = "Performance";
                } else if (checkedId == issueType3.getId()) {
                    reviewType = "UI/User Experience";
                } else if (checkedId == issueType4.getId()) {
                    reviewType = "Service Request/Improvement";
                } else {
                    reviewType = "Others";
                }
            }
        });


        Q1Group = (RadioGroup) findViewById(R.id.Q1Group);
        Q2Group = (RadioGroup) findViewById(R.id.Q2Group);
        Q3Group = (RadioGroup) findViewById(R.id.Q3Group);

        Q1A1 = (RadioButton)findViewById(R.id.Q1A1);
        Q1A2 = (RadioButton)findViewById(R.id.Q1A2);
        Q2A1 = (RadioButton)findViewById(R.id.Q2A1);
        Q2A2 = (RadioButton)findViewById(R.id.Q2A2);
        Q2A3 = (RadioButton)findViewById(R.id.Q2A3);
        Q3A1 = (RadioButton)findViewById(R.id.Q3A1);
        Q3A2 = (RadioButton)findViewById(R.id.Q3A2);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE);
                    } else {
                        gallery();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



        });



        Q1Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == Q1A1.getId()) {
                    Q1 = "1";
                } else if (checkedId == Q1A2.getId()) {
                    Q1 = "2";
                } else {
                    Q1 = null;
                }
            }
        });

        Q2Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == Q2A1.getId()) {
                    Q2 = "1";
                } else if (checkedId == Q2A2.getId()) {
                    Q2 = "2";
                } else if (checkedId == Q2A3.getId()) {
                    Q2 = "3";
                } else {
                    Q2 = null;
                }
            }
        });

        Q3Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == Q3A1.getId()) {
                    Q3 = "1";
                } else if (checkedId == Q3A2.getId()) {
                    Q3 = "2";
                } else {
                    Q3 = null;
                }
            }
        });


        //if no token, need to open loginactivity
        //write username as token
        SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        if (token.isEmpty() || token == null) {
            this.startActivity(new Intent(this.getApplicationContext(), LoginActivity.class));
        }

        marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(img != null) {
                    Intent intent = new Intent(getApplicationContext(), MarkerActivity.class);
                    intent.putExtra("imgPath", imgPath);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "Please upload a screenshot first!", Toast.LENGTH_LONG).show();
                }
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    rates = (int) rating;
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                i.putExtra("entrance", "viewButton");
                startActivity(i);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appName = inputAppName.getText().toString().trim();
                String reviewText = inputReview.getText().toString().trim();
                if (reviewText.equals("")) {
                    reviewText = null;
                }
                if (positions!=null && positions.equals("")) {
                    positions = null;
                }

                username = getSharedPreferences("token", Context.MODE_PRIVATE).getString("token", "");

                final Review review = new Review();
                review.setAppName(appName);
                review.setMarker(positions);
                review.setOverall(reviewText);
                review.setRating(rates);
                review.setScreenshot(img);
                review.setType(reviewType);
                review.setQ1(Q1);
                review.setQ2(Q2);
                review.setQ3(Q3);
                review.setScreenshotTime(screenshotTime);
                review.setUsername(username);

                if (appName.equals("")) {
                    Toast.makeText(MainActivity.this, "Please input application name!", Toast.LENGTH_LONG).show();
                } else if (reviewType == null || Q1 == null || Q2 == null || Q3 == null ) {
                    Toast.makeText(MainActivity.this, "Please complete the questions!", Toast.LENGTH_LONG).show();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            Connection con = DBHelper.setConnection();
                            int res = DBHelper.reviewSave(con, review, fis, image);
                            if (res == 1) {
                                mHandler.sendEmptyMessage(1);
                            } else {
                                mHandler.sendEmptyMessage(0);
                            }
                        }
                    }.start();
                }
            }
        });


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); //add this to get intent in onResume
    }

    @Override
    protected void onResume() {
        super.onResume();

        //get shared pic
        Intent itnIn = getIntent();
        extras = itnIn.getExtras();
        String action = itnIn.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, SCREENSHOT);
                } else {
                    try {
                        screenshotTime = DBHelper.getCurrentTime();
                        Uri uri2 = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                        String path = getRealPathFromURI(MainActivity.this, uri2);
                        imgPath = path;
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        imageView.setImageBitmap(bitmap);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

//                    image = new File(imgPath);
//                    fis = new FileInputStream(image);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        img = baos.toByteArray();

                    } catch (Exception e) {
                        Log.e(this.getClass().getName(), e.toString());
                    }
                }
            }
        }

        //draw the markers on pic

        if(!refresh) {
            positions = getIntent().getStringExtra("positions");
        }else{
            positions = null;
            refresh = false;
        }
        Bitmap screenshot = null;
        float zoom = 0f;
        if(img != null) {
            screenshot = BitmapFactory.decodeStream(new ByteArrayInputStream(img));
            int orgHeight = screenshot.getHeight();
            zoom = (float)imageView.getHeight() / (float)orgHeight;
        }


        if(positions != null && !positions.equals("") && zoom != 0f) {
            List<Marker> markerList = new ArrayList<>();
            for (String each : positions.split(";")) {
                if (!each.trim().equals("")) {
                    String[] parts = each.split(":");
                    String position = parts[0].trim();
                    String[] pos = position.split("\\+");
                    String x = pos[0].trim();
                    String y = pos[1].trim();
//                    String content = parts[1].trim();
                    Marker m = new Marker();
                    m.setX(Integer.parseInt(x));
                    m.setY(Integer.parseInt(y));
//                    m.setContent(content);
                    markerList.add(m);
                }
            }
            DisplayMetrics dm = getResources().getDisplayMetrics();

            relative.removeAllViews();
            cover.removeView(relative);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)(screenshot.getWidth() * zoom), imageView.getHeight());
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            relative.setLayoutParams(params);
            cover.addView(relative);

            int i = 1;
            for (Marker m : markerList) {
                ImageView markerIcon = new ImageView(MainActivity.this);
                switch (i) {
                    case 1:
                        markerIcon.setImageResource(R.drawable.one);
                        break;
                    case 2:
                        markerIcon.setImageResource(R.drawable.two);
                        break;
                    case 3:
                        markerIcon.setImageResource(R.drawable.three);
                        break;
                    case 4:
                        markerIcon.setImageResource(R.drawable.four);
                        break;
                    case 5:
                        markerIcon.setImageResource(R.drawable.five);
                        break;
                    default:
                }


                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams((int) (20 * dm.density), (int) (20 * dm.density));
                p.leftMargin = (int) (m.getX() * zoom - 10 * dm.density);
                p.topMargin = (int) (m.getY() * zoom - 10 * dm.density);
                markerIcon.setLayoutParams(p);
                relative.addView(markerIcon);

                i++;
            }
        }else{
            relative.removeAllViews();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case IMAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gallery();
                } else {
                    Toast.makeText(MainActivity.this, "Gallery Access Failed!", Toast.LENGTH_LONG).show();
                }
                break;
            case SCREENSHOT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        Uri uri2 = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                        String path = getRealPathFromURI(MainActivity.this, uri2);
                        imgPath = path;
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        imageView.setImageBitmap(bitmap);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

//                    image = new File(imgPath);
//                    fis = new FileInputStream(image);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        img = baos.toByteArray();

                    } catch (Exception e) {
                        Log.e(this.getClass().getName(), e.toString());
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Gallery Access Failed!", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE: //result of choosing a photo from gallery
                if (resultCode == RESULT_OK) {
                    try {
                        relative.removeAllViews();
                        refresh = true;
                        if(MarkerActivity.instance != null){
                            MarkerActivity.instance.finish();
                        }
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String path = cursor.getString(columnIndex);
                        imgPath = path;
                        cursor.close();
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        imageView.setImageBitmap(bitmap);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

//                        image = new File(imgPath);
//                        fis = new FileInputStream(image);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        img = baos.toByteArray();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
//            case MARKER:
//                if (resultCode == 5) {
//                    positions = data.getStringExtra("positions");
//                }
//                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Do you want to give up the review?")
                .setPositiveButton("Give up",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setNegativeButton("Cancel", null).create().show();
    }

    public String getRealPathFromURI(Activity act, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = act.managedQuery(contentUri, proj, null, null,null);
        if (cursor == null) {
            String path = contentUri.getPath();
            return path;
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void gallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE);
    }

}
