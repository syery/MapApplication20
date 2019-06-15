package com.example.myapplication20;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private LocationManager locationManager;
    private GoogleMap mMap;
    private Location mLocation;
    private Handler handler1;
    private Timer timer1;
    private Location[] mLocations;  // 3点を入れる配列の宣言

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);

        handler1 = new Handler();
        timer1 = new Timer();
        mLocations = new Location[3];   // 3点を入れる配列の初期化

        //タイマーに直接スケジュール(1秒後に1秒間隔の処理を開始)を追加して実行
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                         //マーカーを立てる(デバッグ用)
                        if (mLocations[0] != null) {
                             mMap.addMarker(new MarkerOptions().position(new LatLng(mLocations[0].getLatitude(), mLocations[0].getLongitude())));
                         }

                        // 三角形を作る
                        if (mLocations[0] != null && mLocations[1] != null && mLocations[2] != null) {
                            PolygonOptions options = new PolygonOptions();
                            options.add(new LatLng(mLocations[0].getLatitude(), mLocations[0].getLongitude()));
                            options.add(new LatLng(mLocations[1].getLatitude(), mLocations[1].getLongitude()));
                            options.add(new LatLng(mLocations[2].getLatitude(), mLocations[2].getLongitude()));
                            options.strokeColor(Color.CYAN);
                            options.strokeWidth(5);
                            options.fillColor(Color.CYAN);
                            mMap.addPolygon(options);
                        }

                    }
                });
            }
        }, 1000, 1000);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onLocationChanged(Location location) {
        // 座標コピー[1] -> [2]
        if (mLocations[1] != null) {
            if (mLocations[2] == null) {
                mLocations[2] = new Location(mLocations[1]);
            } else {
                mLocations[2].setLatitude(mLocations[1].getLatitude());
                mLocations[2].setLongitude(mLocations[1].getLongitude());
            }
        }

        // 座標コピー[0] -> [1]
        if (mLocations[0] != null) {
            if (mLocations[1] == null) {
                mLocations[1] = new Location(mLocations[0]);
            } else {
                mLocations[1].setLatitude(mLocations[0].getLatitude());
                mLocations[1].setLongitude(mLocations[0].getLongitude());
            }
        }

        // 座標作成[0]
        mLocations[0] = new Location(location);
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng aomori = new LatLng(40.783084, 140.781492);
        //   mMap.addMarker(new MarkerOptions().position(aomori).title("Marker in 青森大学"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aomori, 17));
    }
}