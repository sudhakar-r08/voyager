package com.osi.voyagerdriver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.osi.voyagerdriver.AsyncTask.CallAPI;
import com.osi.voyagerdriver.Dataset.HistoryData;
import com.osi.voyagerdriver.Util.Session;
import com.osi.voyagerdriver.Util.Util;
import com.osi.voyagerdriver.dialogs.ProgressDialogView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class HistoryDetailsActivity extends ParentActivity implements View.OnClickListener {
    TextView headername;
    ImageView ic_back;
    HistoryData historyData;
    TextView booking, toll, base, minute, promo, miles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_details);
        headername = (TextView) findViewById(R.id.headername);
        ic_back = (ImageView) findViewById(R.id.ic_back);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        SetOnclicklistener();
    }

    @Override
    public void SetOnclicklistener() {
        super.SetOnclicklistener();
        ic_back.setOnClickListener(this);
        try {
            JSONObject jsonObject_main = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            jsonObject_main = getCommontHeaderParams();
            jsonObject.put("driverId", Session.getUserID(HistoryDetailsActivity.this));
            jsonObject.put("bookingId", HistoryData.bookingId);
            jsonObject_main.put("body", jsonObject);
            CallAPI(jsonObject_main);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void CallAPI(JSONObject params) {
        if (Util.isNetworkConnected(HistoryDetailsActivity.this)) {
            try {
                new CallAPI(GETHISTORYDETAILS, "GETHISTORYDETAILS", params, HistoryDetailsActivity.this, GetDetails_Handler, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            progressdialog.dismissanimation(ProgressDialogView.ERROR);
            Util.ShowToast(HistoryDetailsActivity.this, getString(R.string.nointernetmessage));
        }
    }

    private URL url;
    private Bitmap image,image1;
    Handler GetDetails_Handler = new Handler() {
        public void handleMessage(Message msg) {

            PrintMessage("Handler " + msg.getData().toString());
            if (msg.getData().getBoolean("flag")) {
                if (msg.getData().getInt("code") == SUCCESS) {
                    // Session.setAllInfo(HistoryDetailsActivity.this,msg.getData().getString("responce"));
                    //Intent intent = new Intent(HistoryDetailsActivity.this, LandingPageActivity.class);
                    try {
                        LinearLayout map = (LinearLayout) findViewById(R.id.map);
                        LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
                        linear.setVisibility(View.VISIBLE);
                        booking = (TextView) findViewById(R.id.booking);
                        base = (TextView) findViewById(R.id.base);
                        toll = (TextView) findViewById(R.id.toll);
                        miles = (TextView) findViewById(R.id.miles);
                        minute = (TextView) findViewById(R.id.minutes);
                        promo = (TextView) findViewById(R.id.code);
                        TextView miles1 = (TextView) findViewById(R.id.miles1);
                        TextView time1 = (TextView) findViewById(R.id.time1);
                        TextView total = (TextView) findViewById(R.id.total);
                        TextView customerName = (TextView) findViewById(R.id.name);
                        TextView rating = (TextView) findViewById(R.id.rating);
                        TextView car = (TextView) findViewById(R.id.car);
                        TextView type = (TextView) findViewById(R.id.type);
                        ImageView map1= (ImageView) findViewById(R.id.map1);
                        JSONObject jsonObject = new JSONObject(msg.getData().getString("responce"));
                        Log.i("asasdddddd", "  " + jsonObject);
                        booking.setText(jsonObject.getString("bookingFee"));
                        customerName.setText(jsonObject.getString("customerName"));
                        rating.setText("YOUR RATING | "+jsonObject.getString("driverRating")+" *");
                        base.setText(jsonObject.getString("baseFare"));
                        miles.setText(jsonObject.getString("kmCharges"));
                        minute.setText(jsonObject.getString("waitCharges"));
                        promo.setText(jsonObject.getString("promoCode") ==null ? jsonObject.getString("promoCode"):"0");
                        toll.setText(jsonObject.getString("tollCharges"));
                        miles1.setText(jsonObject.getString("kmTraveled"));
                        time1.setText(jsonObject.getString("waitTime"));
                        total.setText(jsonObject.getString("rideTotalAmt"));
                        ImageView fab1 = (ImageView) findViewById(R.id.fab1);
                        try {
                            url = new URL(GETPIC + "/" + jsonObject.getString("customerId"));
                            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            fab1.setImageBitmap(getCircleBitmap(image));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (jsonObject.getString("accountType").equals("Corporate"))
                        {
                            type.setText("Business");
                        }
                        else{
                            type.setText("Personal");
                        }
                        try {
                            url = new URL(GETMAP + "/" + getIntent().getExtras().getString("bookingId"));
                            image1 = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            Bitmap newbitMap = Bitmap.createScaledBitmap(image1, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("aaaaaaaaaaaaaaaaaaaaa", "aaaaaaaaaaaaaaaaaaaaa" + image1);
                        if(image1 == null)
                        {
                            map.setVisibility(View.GONE);
                        }else
                        {
                            map.setVisibility(View.VISIBLE);
                            map1.setImageBitmap(image1);
                        }
                        car.setText(jsonObject.getString("carNumber")+" "+jsonObject.getString("make")+"-"+jsonObject.getString("model"));
//
//                        intent.putExtra("mobile", jsonObject.getString("mobile"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //startActivity(intent);
                    //    finish();
                } else if (msg.getData().getInt("code") == FROMGENERATETOKEN) {
                    ParseSessionDetails(msg.getData().getString("responce"));
                    try {
                        CallAPI(new JSONObject(msg.getData()
                                .getString("mExtraParam")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.getData().getInt("code") == SESSIONEXPIRE) {
                    if (Util.isNetworkConnected(HistoryDetailsActivity.this)) {
                        CallSessionID(GetDetails_Handler, msg.getData()
                                .getString("mExtraParam"));
                    } else {
                        Util.ShowToast(HistoryDetailsActivity.this, getString(R.string.nointernetmessage));
                    }
                } else {
                    Util.ShowToast(HistoryDetailsActivity.this, msg.getData().getString("msg"));
                }
            } else {
                Util.ShowToast(HistoryDetailsActivity.this, msg.getData().getString("msg"));
            }

        }

    };

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);


        return output;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_back:
                finish();
                break;
            case R.id.help:
                Intent intent = new Intent(HistoryDetailsActivity.this,HelpActivity.class);
                startActivity(intent);
        }
    }
}


