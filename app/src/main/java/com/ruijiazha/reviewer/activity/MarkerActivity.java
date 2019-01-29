package com.ruijiazha.reviewer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ruijiazha.reviewer.R;
import com.ruijiazha.reviewer.database.DBHelper;
import com.ruijiazha.reviewer.element.StarImageButton;

import java.sql.Connection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;


public class MarkerActivity extends Activity {

    ImageView imageFull;
    ImageButton save,clear;
    ViewGroup relativeLayout;

    private int markerX,markerY;
    private int i = 0;
    public HashMap<Integer,String> starContentMap = new HashMap<>();//save contents to re-enter popwindow
    public HashMap<Integer,String> starReviewMap = new HashMap<>();//save reviews and transfer to mainactivity orderly, to draw markers on the screenshot

    public static MarkerActivity instance;//for finish this activity in another one

    public void clear(View v){
        new AlertDialog.Builder(MarkerActivity.this)
                .setTitle("Are you sure to delete ALL markers?")
                .setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                relativeLayout.removeAllViews();
                                starContentMap.clear();
                                starReviewMap.clear();
                            }
                        }).setNegativeButton("Cancel", null).create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);

        instance = this;

        Intent intent = getIntent();
        String imgPath = intent.getStringExtra("imgPath");
        imageFull = (ImageView) findViewById(R.id.imageFull);
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        imageFull.setImageBitmap(bitmap);
        imageFull.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        relativeLayout =(ViewGroup) findViewById(R.id.relative);
        save = (ImageButton) findViewById(R.id.save);
        clear = (ImageButton) findViewById(R.id.clear);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                Set<Integer> keySet = starReviewMap.keySet();
                Set<Integer> sortSet = new TreeSet<>(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o1.compareTo(o2);//asc
                    }
                });
                sortSet.addAll(keySet);
                StringBuffer sb = new StringBuffer();
                for(int j : sortSet){
                    sb.append(starReviewMap.get(j));
                }

                intent.putExtra("positions",sb.toString());
//                setResult(5,intent);
//                finish();
                startActivity(intent);
            }
        });

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        final int windowWidth = outMetrics.widthPixels;
        final int windowHeight = outMetrics.heightPixels;

        imageFull.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(starContentMap.size()<5) {
                            markerX = Math.round(event.getX());
                            markerY = Math.round(event.getY());
                            final StarImageButton imageView = new StarImageButton(MarkerActivity.this);
                            imageView.setStarid(i+1);
                            starContentMap.put(i+1,"");
                            imageView.setBackground(getDrawable(R.drawable.star));

                            DisplayMetrics dm = getResources().getDisplayMetrics();
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)(40 * dm.density), (int)(40 * dm.density));
                            params.leftMargin = markerX - (int)(20 * dm.density);
                            params.topMargin = markerY - (int)(20 * dm.density);
                            imageView.setLayoutParams(params);
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            relativeLayout.addView(imageView);
                            i++;

                            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                            View popupView = layoutInflater.inflate(R.layout.popup_layout, null);

                            ImageButton savePop, closePop;
                            final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

                            popupWindow.setFocusable(true);
                            popupWindow.setTouchable(true);
                            popupWindow.setWidth(windowWidth * 3 / 4);

                            popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return false;
                                }
                            });

                            popupWindow.setBackgroundDrawable(new ColorDrawable(0xe0000000));
                            popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
                            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                            int offY = 1;
                            if (markerY > windowHeight * 2 / 5) {
                                offY = -1;
                            }
                            popupWindow.showAtLocation(v, Gravity.CENTER_HORIZONTAL, 0, offY * windowHeight / 4);
                            final EditText reviewPop = (EditText) popupView.findViewById(R.id.reviewPop);

                            closePop = (ImageButton) popupView.findViewById(R.id.closePop);
                            savePop = (ImageButton) popupView.findViewById(R.id.savePop);
                            closePop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindow.dismiss();
                                    starContentMap.remove(imageView.getStarid());
                                    starReviewMap.remove(imageView.getStarid());
                                    relativeLayout.removeView(imageView);
                                }
                            });

                            savePop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String reviewPopText = reviewPop.getText().toString();
                                    starContentMap.put(i,reviewPopText);
                                    //sb.append(String.valueOf(markerX) + "+" + String.valueOf(markerY) + ":" + reviewPopText + ";");
                                    starReviewMap.put(i,String.valueOf(markerX) + "+" + String.valueOf(markerY) + ":" + reviewPopText + ";");
                                    popupWindow.dismiss();
                                    imageView.setEnabled(true);
                                    Toast.makeText(MarkerActivity.this, "Review saved!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //popupwindow
                                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                    View popupView = layoutInflater.inflate(R.layout.popup_layout, null);

                                    ImageButton savePop, closePop;
                                    final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

                                    popupWindow.setFocusable(true);
                                    popupWindow.setTouchable(true);
                                    popupWindow.setWidth(windowWidth * 3 / 4);

                                    popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            return false;
                                        }
                                    });

                                    popupWindow.setBackgroundDrawable(new ColorDrawable(0xe0000000));

                                    popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
                                    popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                                    int offY = 1;
                                    if (markerY > windowHeight * 2 / 5) {
                                        offY = -1;
                                    }
                                    popupWindow.showAtLocation(v, Gravity.CENTER_HORIZONTAL, 0, offY * windowHeight / 4);
                                    final EditText reviewPop = (EditText) popupView.findViewById(R.id.reviewPop);

                                    reviewPop.setText(starContentMap.get(imageView.getStarid()));

                                    closePop = (ImageButton) popupView.findViewById(R.id.closePop);
                                    savePop = (ImageButton) popupView.findViewById(R.id.savePop);
                                    closePop.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popupWindow.dismiss();
                                            starContentMap.remove(imageView.getStarid());
                                            starReviewMap.remove(imageView.getStarid());
                                            relativeLayout.removeView(imageView);

                                        }
                                    });

                                    savePop.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String reviewPopText = reviewPop.getText().toString();
                                            starContentMap.put(imageView.getStarid(),reviewPopText);
                                            //sb.append(String.valueOf(markerX) + "+" + String.valueOf(markerY) + ":" + reviewPopText + ";");
                                            starReviewMap.put(imageView.getStarid(),String.valueOf(markerX) + "+" + String.valueOf(markerY) + ":" + reviewPopText + ";");
                                            popupWindow.dismiss();
                                            imageView.setEnabled(true);
                                            Toast.makeText(MarkerActivity.this, "Review saved!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(MarkerActivity.this, "You can put no more than 5 markers!", Toast.LENGTH_SHORT).show();
                        }
                        break;

//                    case MotionEvent.ACTION_MOVE:
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }



}
