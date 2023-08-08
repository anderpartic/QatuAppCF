package com.anderson.qatuappcf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private GoogleMap googleMap;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private AppCompatImageButton back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapActivity.this, Cliente.class);
                startActivity(i);
                finish();
            }
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Verificar si tenemos permisos para acceder a la ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Obtener la ubicación actual del dispositivo
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();

                    // Mostrar ubicación actual en el mapa
                    LatLng miUbicacion = new LatLng(latitud, longitud);
                    googleMap.addMarker(new MarkerOptions().position(miUbicacion).title("Mi ubicación"));

                    // Mostrar lugar fijo (por ejemplo, un lugar en latitudLugarFijo y longitudLugarFijo)
                    double latitudLugarFijo = 0.040314; // Latitud del lugar fijo
                    double longitudLugarFijo = -78.144704; // Longitud del lugar fijo
                    LatLng lugarFijo = new LatLng(latitudLugarFijo, longitudLugarFijo);
                    googleMap.addMarker(new MarkerOptions().position(lugarFijo).title("Mercado Diario de Cayambe"));

                    // Mostrar trayectoria (polilínea) entre la ubicación actual y el lugar fijo
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .add(miUbicacion)
                            .add(lugarFijo);
                    Polyline polyline = googleMap.addPolyline(polylineOptions);

                    // Ajustar la cámara para que muestre ambos marcadores y la trayectoria
                    LatLng centerLatLng = new LatLng((latitud + latitudLugarFijo) / 2, (longitud + longitudLugarFijo) / 2);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 12.0f));
                }
            });
        } else {
            // Si no tenemos permisos, solicitarlos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el permiso fue concedido, volvemos a cargar el mapa
                mapView.getMapAsync(this);
            } else {
                Log.d("MapActivity", "Permiso de ubicación denegado");
                // Si el permiso fue denegado, aquí puedes mostrar un mensaje o tomar alguna acción en consecuencia.
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
