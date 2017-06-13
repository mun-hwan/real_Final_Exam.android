package com.example.user.final_exam;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import org.json.JSONArray;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener,GoogleMap.OnMyLocationChangeListener {

    public GoogleMap mGoogleMap;
    Context mContext;

    Button btnShowLocation;
    Button btnclearLocation;
    Button btnstopLocation;
    Button button;
    EditText editText;

    private PolylineOptions Options;

    private ArrayList<LatLng> arrayPoints;
    private ArrayList<LatLng> arrayString;

    private static int j=1;

    //------근접 센서 변수
    private SensorManager mSensorManager;
    private Sensor mProximity;

    private WindowManager.LayoutParams params;
    private float brightness;

    //-----버튼 클릭시 값
    boolean switching = false;

//----db생성
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//------------
        myDb = new DatabaseHelper(this);


//센서 선언
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

//
        params = getWindow().getAttributes();



        editText = (EditText) findViewById(R.id.editText);
        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnclearLocation = (Button) findViewById(R.id.btnclearLocation);
        btnstopLocation = (Button) findViewById(R.id.btnstopLocation);
        button=(Button) findViewById(R.id.button);

        // 맵 마커 연결 하는 줄 및 배열 셋팅
        Options = new PolylineOptions();
        Options.color(Color.RED);
        Options.width(7);
        arrayPoints = new ArrayList<>();
        arrayString = new ArrayList<>();




        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                switching=true;
            }
        });

        btnclearLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                switching=false;
                arrayPoints.clear();
            }
        });

        btnstopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                switching=false;

                mGoogleMap.clear();
                arrayPoints.clear();
                j=1;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                switching=false;
                double d1,d2;

            Cursor res = myDb.getAllDate();
            if(res.getCount()==0){
                return;
            }
                j=1;
                while(res.moveToNext())
                {
                    d1 = res.getDouble(1);
                    d2 = res.getDouble(2);
                    LatLng MyLocation = new LatLng(d1, d2);
                    arrayString.add(MyLocation);

                    mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_1))
                            .position(MyLocation).title(Integer.toString(j) + "번째 "+res.getString(3)));
                    j++;

                    Options.addAll(arrayString);
                    mGoogleMap.addPolyline(Options);
                    arrayString.clear();

                }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        LatLng Seoul = new LatLng(37.490210, 127.082026);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(Seoul));

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);
        mGoogleMap.animateCamera(zoom);





        // 현재 위치를 받을수 있는 코드
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setOnMyLocationChangeListener(this);
        } else {
        }

    }


    public String getTimeStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM/dd HH:mm:ss");
        return sdfNow.format(date);
    }


//-----------센서 값 바뀔때 실행
     @Override
    public void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];
        // Do something with this sensor data.


            if(distance==0) {
                // 화면 밝기 설정
                params.screenBrightness = 0f;
                // 밝기 설정 적용
                getWindow().setAttributes(params);
            }
        else{
                // 기존 밝기로 변경
                params.screenBrightness = brightness;
                getWindow().setAttributes(params);
            }

    }
    @Override
    protected void onResume() {
        super.onResume();
        // 기존 밝기 저장
        brightness = params.screenBrightness;
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();

        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onMyLocationChange(Location location) {//현재 위치 변경시 실행
        double d1=location.getLatitude();
        double d2=location.getLongitude();
        LatLng MyLocation = new LatLng(d1, d2);
        Log.e("onMyLocationChange", d1 + "," + d2);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d1, d2),18));

        if(switching) {
            mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_1))
                    .position(MyLocation).title(Integer.toString(j) + "번째 "+getTimeStr()));
            j++;

            //맵 라인 그리기
            arrayPoints.add(MyLocation);
            Options.addAll(arrayPoints);
            mGoogleMap.addPolyline(Options);
            arrayPoints.clear();

            boolean isInserted = myDb.insertData(d1,d2,getTimeStr());
            /*if(isInserted=true)
            {
                Toast.makeText(MapsActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(MapsActivity.this,"Data not Inserted",Toast.LENGTH_LONG).show();*/
        }
    }
}
