package com.ruijiazha.reviewer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.ruijiazha.reviewer.database.DBHelper;
import com.ruijiazha.reviewer.R;
import com.ruijiazha.reviewer.data.ReviewShow;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Activity {

    private String username;
    private String entrance;

    TextView usernameText;
    ImageButton backButton;

    private List<ReviewShow> showList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;

    Handler getReviewListHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1) {
                List<ReviewShow> res = (List<ReviewShow>)msg.obj;
                reviewAdapter = new ReviewAdapter(ProfileActivity.this, R.layout.review_list_item, res);
                ListView listView = (ListView) findViewById(R.id.reviewList);
                listView.setAdapter(reviewAdapter);
            }
        }
    };

    Handler deleteHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1) {
                showList.remove((int)msg.obj);
                reviewAdapter.notifyDataSetChanged();
                Toast.makeText(ProfileActivity.this, "Review Deleted!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(ProfileActivity.this, "Delete failed!", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameText = (TextView) findViewById(R.id.usernameText);
        backButton = (ImageButton) findViewById(R.id.backButton);

        Intent i = getIntent();
        entrance = i.getStringExtra("entrance");

        SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        if (token.isEmpty() || token == null) {
            this.startActivity(new Intent(this.getApplicationContext(), LoginActivity.class));
        } else {
            username = token;
        }
        usernameText.setText(username);

        initList();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entrance.equals("viewButton")) {
                    finish();
                } else {
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        });
    }

    protected void initList() {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Connection con = DBHelper.setConnection();
                showList = DBHelper.getReviewList(con,username);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = showList;
                getReviewListHandler.sendMessage(msg);
            }
        }.start();
    }

    private class ReviewAdapter extends ArrayAdapter<ReviewShow> {

        private int layoutId;

        public ReviewAdapter(@NonNull Context context, int layoutId, @NonNull List<ReviewShow> objects) {
            super(context, layoutId, objects);
            this.layoutId = layoutId;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ReviewShow rs = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
            TextView timeText = (TextView)view.findViewById(R.id.timeText);
            RatingBar ratingShow = (RatingBar)view.findViewById(R.id.ratingShow);
            TextView appNameText = (TextView)view.findViewById(R.id.appNameText);
            ImageButton deleteButton = (ImageButton)view.findViewById(R.id.deleteButton);
            ImageButton detailButton = (ImageButton)view.findViewById(R.id.detailButton);
            timeText.setText(rs.getTime());
            ratingShow.setRating(rs.getRates());
            appNameText.setText(rs.getAppName());

            detailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                    final int reviewid = showList.get(position).getReviewid();
                    i.putExtra("entrance", entrance);
                    i.putExtra("reviewid", reviewid);
                    startActivity(i);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(ProfileActivity.this)
                            .setTitle("Are you sure to delete this review?")
                            .setPositiveButton("Delete",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            final int reviewid = showList.get(position).getReviewid();
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    Looper.prepare();
                                                    Connection con = DBHelper.setConnection();
                                                    int res = DBHelper.deleteReview(con,reviewid);
                                                    Message msg = new Message();
                                                    msg.what = res;
                                                    msg.obj = position;
                                                    deleteHandler.sendMessage(msg);
                                                }
                                            }.start();

                                        }
                                    }).setNegativeButton("Cancel", null).create().show();
                }
            });

            return view;
        }

    }
}
