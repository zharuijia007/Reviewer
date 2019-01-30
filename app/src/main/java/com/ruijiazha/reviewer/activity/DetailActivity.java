package com.ruijiazha.reviewer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruijiazha.reviewer.database.DBHelper;
import com.ruijiazha.reviewer.data.Marker;
import com.ruijiazha.reviewer.R;
import com.ruijiazha.reviewer.data.Review;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends Activity {

    ImageButton backButton;
    TextView appNameText,timeText,overallText;
    RatingBar ratingShow;
    ImageView screenshot;
    RelativeLayout cover, overallCover;
    LinearLayout subReviewCover;

    private Review review = new Review();
    private int reviewid;
    private String entrance;


    Handler getAReviewHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1) {
                Review review = (Review) msg.obj;
                appNameText.setText(review.getAppName());
                timeText.setText(review.getTime());
                ratingShow.setRating(review.getRating());
                overallText.setText(review.getOverall());
                if(review.getOverall()!=null && !review.getOverall().equals("")){
                    overallCover.setVisibility(View.VISIBLE);
                }
                Bitmap decoded = null;
                if(review.getScreenshot()!=null) {
                    cover.setVisibility(View.VISIBLE);
                    decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(review.getScreenshot()));
                    screenshot.setImageBitmap(decoded);
                }

                String marker = review.getMarker();
                if(marker != null && decoded != null) {
                    List<Marker> markerList = new ArrayList<>();
                    for (String each : marker.split(";")) {
                        if (!each.trim().equals("")) {
                            String[] parts = each.split(":");
                            String position = parts[0].trim();
                            String[] pos = position.split("\\+");
                            String x = pos[0].trim();
                            String y = pos[1].trim();
                            String content = null;
                            if(parts.length == 1){
                                content = null;
                            }else {
                                content = parts[1].trim();
                            }
                            Marker m = new Marker();
                            m.setX(Integer.parseInt(x));
                            m.setY(Integer.parseInt(y));
                            m.setContent(content);
                            markerList.add(m);
                        }
                    }

                    int orgHeight = decoded.getHeight();
                    int orgWidth = decoded.getWidth();
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    int realHeight = (int)(dm.density * 400);
                    float zoom = (float)realHeight / (float)orgHeight;
                    int realWidth = (int)(orgWidth * zoom);

                    RelativeLayout relative = new RelativeLayout(DetailActivity.this);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(realWidth, realHeight);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    relative.setLayoutParams(params);
                    cover.addView(relative);

                    int i = 1;
                    for(Marker m : markerList){
                        ImageView imageView = new ImageView(DetailActivity.this);
                        ImageView imageView2 = new ImageView(DetailActivity.this);
                        switch (i){
                            case 1:
                                imageView.setImageResource(R.drawable.one);
                                imageView2.setImageResource(R.drawable.one);
                                break;
                            case 2:
                                imageView.setImageResource(R.drawable.two);
                                imageView2.setImageResource(R.drawable.two);
                                break;
                            case 3:
                                imageView.setImageResource(R.drawable.three);
                                imageView2.setImageResource(R.drawable.three);
                                break;
                            case 4:
                                imageView.setImageResource(R.drawable.four);
                                imageView2.setImageResource(R.drawable.four);
                                break;
                            case 5:
                                imageView.setImageResource(R.drawable.five);
                                imageView2.setImageResource(R.drawable.five);
                                break;
                                default:
                        }


                        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams((int)(20 * dm.density), (int)(20 * dm.density));
                        p.leftMargin = (int)(m.getX() * zoom - 10 * dm.density);
                        p.topMargin = (int)(m.getY() * zoom - 10 * dm.density);
                        imageView.setLayoutParams(p);
                        relative.addView(imageView);

                        RelativeLayout subReview = new RelativeLayout(DetailActivity.this);
                        LinearLayout.LayoutParams subReviewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        subReviewParams.bottomMargin = (int)(10 * dm.density);
                        subReviewParams.leftMargin = (int)(10 * dm.density);
                        subReviewParams.rightMargin = (int)(10 * dm.density);
                        subReview.setPadding(0,(int)(10 * dm.density),0,(int)(10 * dm.density));
                        subReview.setBackgroundColor(0xFF8B658B);
                        subReview.setLayoutParams(subReviewParams);
                        subReviewCover.addView(subReview);

                        RelativeLayout.LayoutParams subReviewIconParams = new RelativeLayout.LayoutParams((int)(40 * dm.density), (int)(40 * dm.density));
                        subReviewIconParams.leftMargin = (int)(10 * dm.density);
                        subReviewIconParams.addRule(RelativeLayout.CENTER_VERTICAL);
                        imageView2.setLayoutParams(subReviewIconParams);
                        subReview.addView(imageView2);

                        TextView textView = new TextView(DetailActivity.this);
                        textView.setText(m.getContent());
                        textView.setTextColor(Color.WHITE);
                        textView.setTextSize(15);

                        RelativeLayout.LayoutParams subReviewContentParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        subReviewContentParams.leftMargin = (int)(60 * dm.density);
                        subReviewContentParams.rightMargin = (int)(10 * dm.density);
                        subReviewContentParams.addRule(RelativeLayout.CENTER_VERTICAL);
                        textView.setLayoutParams(subReviewContentParams);
                        subReview.addView(textView);

                        i++;
                    }
                }









            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        reviewid = i.getIntExtra("reviewid",-1);
        entrance = i.getStringExtra("entrance");

        backButton = (ImageButton)findViewById(R.id.backButton);
        appNameText = (TextView)findViewById(R.id.appNameText);
        timeText = (TextView)findViewById(R.id.timeText);
        overallText = (TextView)findViewById(R.id.overallText);
        ratingShow = (RatingBar)findViewById(R.id.ratingShow);
        screenshot = (ImageView)findViewById(R.id.screenshot);
        cover = (RelativeLayout)findViewById(R.id.cover);
        overallCover = (RelativeLayout)findViewById(R.id.overallCover);
        subReviewCover = (LinearLayout)findViewById(R.id.subReviewCover);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                i.putExtra("entrance", entrance);
                startActivity(i);
            }
        });

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Connection con = DBHelper.setConnection();
                review = DBHelper.getAReview(con, reviewid);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = review;
                getAReviewHandler.sendMessage(msg);
            }
        }.start();
    }
}
