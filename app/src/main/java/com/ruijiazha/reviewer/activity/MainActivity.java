package com.ruijiazha.reviewer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
    private String Q1, Q2, Q3, Q4, Q5, Q6, Q7;
    private String username;
    private int rates = 0;

    File image;
    FileInputStream fis;
    Bundle extras;

    Button save,upload;
    ImageView imageView;
    EditText inputReview, inputAppName;
    RatingBar ratingBar;
    RadioGroup Q1Group, Q2Group, Q3Group, Q4Group, Q5Group, Q6Group, Q7Group;
    RadioButton Q1ButtonYes, Q1ButtonNo, Q2ButtonYes, Q2ButtonNo, Q3ButtonYes, Q3ButtonNo, Q4ButtonYes, Q4ButtonNo, Q5ButtonYes, Q5ButtonNo, Q6ButtonYes, Q6ButtonNo, Q7Button1, Q7Button2, Q7Button3;
    ImageButton view, marker;
    RelativeLayout cover;
    RelativeLayout relative;



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
        relative = new RelativeLayout(MainActivity.this);//this layout is used to draw markers


        Q1Group = (RadioGroup) findViewById(R.id.Q1Group);
        Q2Group = (RadioGroup) findViewById(R.id.Q2Group);
        Q3Group = (RadioGroup) findViewById(R.id.Q3Group);
        Q4Group = (RadioGroup) findViewById(R.id.Q4Group);
        Q5Group = (RadioGroup) findViewById(R.id.Q5Group);
        Q6Group = (RadioGroup) findViewById(R.id.Q6Group);
        Q7Group = (RadioGroup) findViewById(R.id.Q7Group);

        Q1ButtonYes = (RadioButton) findViewById(R.id.Q1ButtonYes);
        Q1ButtonNo = (RadioButton) findViewById(R.id.Q1ButtonNo);
        Q2ButtonYes = (RadioButton) findViewById(R.id.Q2ButtonYes);
        Q2ButtonNo = (RadioButton) findViewById(R.id.Q2ButtonNo);
        Q3ButtonYes = (RadioButton) findViewById(R.id.Q3ButtonYes);
        Q3ButtonNo = (RadioButton) findViewById(R.id.Q3ButtonNo);
        Q4ButtonYes = (RadioButton) findViewById(R.id.Q4ButtonYes);
        Q4ButtonNo = (RadioButton) findViewById(R.id.Q4ButtonNo);
        Q5ButtonYes = (RadioButton) findViewById(R.id.Q5ButtonYes);
        Q5ButtonNo = (RadioButton) findViewById(R.id.Q5ButtonNo);
        Q6ButtonYes = (RadioButton) findViewById(R.id.Q6ButtonYes);
        Q6ButtonNo = (RadioButton) findViewById(R.id.Q6ButtonNo);
        Q7Button1 = (RadioButton) findViewById(R.id.Q7Button1);
        Q7Button2 = (RadioButton) findViewById(R.id.Q7Button2);
        Q7Button3 = (RadioButton) findViewById(R.id.Q7Button3);


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


        final Spinner spinner = (Spinner) findViewById(R.id.typeSpinner);
        spinner.setGravity(Gravity.CENTER);

        Q1Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == Q1ButtonYes.getId()) {
                    Q1 = "yes";
                } else if (checkedId == Q1ButtonNo.getId()) {
                    Q1 = "no";
                } else {
                    Q1 = null;
                }
            }
        });

        Q2Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == Q2ButtonYes.getId()) {
                    Q2 = "yes";
                } else if (checkedId == Q2ButtonNo.getId()) {
                    Q2 = "no";
                } else {
                    Q2 = null;
                }
            }
        });

        Q3Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == Q3ButtonYes.getId()) {
                    Q3 = "yes";
                } else if (checkedId == Q3ButtonNo.getId()) {
                    Q3 = "no";
                } else {
                    Q3 = null;
                }
            }
        });

        Q4Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == Q4ButtonYes.getId()) {
                    Q4 = "yes";
                } else if (checkedId == Q4ButtonNo.getId()) {
                    Q4 = "no";
                } else {
                    Q4 = null;
                }
            }
        });

        Q5Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == Q5ButtonYes.getId()) {
                    Q5 = "yes";
                } else if (checkedId == Q5ButtonNo.getId()) {
                    Q5 = "no";
                } else {
                    Q5 = null;
                }
            }
        });

        Q6Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == Q6ButtonYes.getId()) {
                    Q6 = "yes";
                } else if (checkedId == Q6ButtonNo.getId()) {
                    Q6 = "no";
                } else {
                    Q6 = null;
                }
            }
        });

        Q7Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == Q7Button1.getId()) {
                    Q7 = "Visiting";
                } else if (checkedId == Q7Button2.getId()) {
                    Q7 = "Wandering";
                } else if (checkedId == Q7Button3.getId()) {
                    Q7 = "Traveling";
                } else {
                    Q7 = null;
                }
            }
        });


        String[] plants = new String[]{
                "Select a Review Type",
                "Functional Error",
                "Lagging",
                "Unattractive Interface Design",
                "Uninteresting Content",
                "App Quits Unexpectedly",
                "App Freeze",
                "Lose Data",
                "Feature missing",
                "Feature should be removed",
                "Feature not working as expected",
                "Difficult to use",
                "Not working on particular system version",
                "Not working on particular device",
                "Poor Connection with Wifi",
                "Poor Connection with Mobile Network",
                "Hidden Cost",
                "Too Expensive",
                "Privacy and ethics issues",
                "Cost too much energy or memory",
                "Not Specific"
        };

        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_show_item, plantsList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    reviewType = (String) parent.getItemAtPosition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                review.setQ4(Q4);
                review.setQ5(Q5);
                review.setQ6(Q6);
                review.setQ7(Q7);
                review.setUsername(username);

                if (appName.equals("")) {
                    Toast.makeText(MainActivity.this, "Please input application name!", Toast.LENGTH_LONG).show();
                } else if (reviewType == null || Q1 == null || Q2 == null || Q3 == null || Q4 == null || Q5 == null || Q6 == null || Q7 == null) {
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

        positions = getIntent().getStringExtra("positions");

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
