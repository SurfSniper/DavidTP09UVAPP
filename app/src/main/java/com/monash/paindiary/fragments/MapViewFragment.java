package com.monash.paindiary.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.monash.paindiary.R;
import com.monash.paindiary.activities.AppActivity;
import com.monash.paindiary.databinding.FragmentMapViewBinding;
import com.monash.paindiary.enums.NavigationItem;

import java.io.IOException;
import java.util.List;

public class MapViewFragment extends Fragment {
    private FragmentMapViewBinding binding;
    private SymbolManager symbolManager;
    private Symbol symbol;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private LatLng latLng;
    private final double defaultZoomLevel = 13;

    public MapViewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String token = getString(R.string.mapbox_public_access_token);
        Mapbox.getInstance(getContext(), token);

        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        ((AppActivity) requireActivity()).ManualSelectNavigationItem(NavigationItem.MapView);
        binding.btnLocate.setEnabled(true);

        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::initMap);
        // default location set to Monash, Clayton (On start up)
        latLng = new LatLng(-37.9139, 145.1326);

        binding.btnLocate.setOnClickListener(this::btnLocateOnClicked);
        binding.btnZoomPlus.setOnClickListener(this::btnZoomPlusOnClicked);
        binding.btnZoomMinus.setOnClickListener(this::btnZoomMinusOnClicked);

        return view;
    }

    private void btnZoomPlusOnClicked(View view) {
        double currentZoom = mapboxMap.getCameraPosition().zoom;
        CameraPosition position = new CameraPosition.Builder()
                .zoom(++currentZoom)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 800);
    }

    private void btnZoomMinusOnClicked(View view) {
        double currentZoom = mapboxMap.getCameraPosition().zoom;
        CameraPosition position = new CameraPosition.Builder()
                .zoom(--currentZoom)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 800);
    }

    private void btnLocateOnClicked(View view) {
        hideKeyboard();
        binding.btnLocate.setEnabled(false);
        String given_address = binding.editAddress.getText().toString();
        if (!given_address.isEmpty()) {
            try {
                Geocoder geocoder = new Geocoder(getContext());
                List<Address> addresses = geocoder.getFromLocationName(given_address, 1);
                if (addresses.size() > 0) {
                    latLng.setLatitude(addresses.get(0).getLatitude());
                    latLng.setLongitude(addresses.get(0).getLongitude());
                    CameraPosition position = new CameraPosition.Builder()
                            .target(latLng)
                            .zoom(defaultZoomLevel)
                            .build();
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);
                    showMarker();
                } else {
                    Toast.makeText(getContext(), "Location not found!", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Log.i("EXCEPTION", e.getMessage());
            } finally {
                binding.btnLocate.setEnabled(true);
            }
        } else {
            Toast.makeText(getContext(), "Please provide location address.", Toast.LENGTH_SHORT).show();
            binding.btnLocate.setEnabled(true);
        }
    }

    private void initMap(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setMinZoomPreference(6);
        mapboxMap.setMaxZoomPreference(20);
        mapboxMap.moveCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .zoom(defaultZoomLevel)
                        .target(latLng)
                        .build())
        );
        Drawable drawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_baseline_place_24);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, Color.RED);

        mapboxMap.setStyle(new Style.Builder()
                .fromUri(Style.MAPBOX_STREETS)
                .withImage("POINT_IMAGE", wrappedDrawable), style -> {
            symbolManager = new SymbolManager(mapView, mapboxMap, style);
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);
            showMarker();
        });
    }

    private void showMarker() {
        if (symbol != null)
            symbolManager.delete(symbol);
        SymbolOptions options = new SymbolOptions()
                .withLatLng(latLng)
                .withIconImage("POINT_IMAGE");
        symbol = symbolManager.create(options);
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppActivity)getActivity()).ShowProgress(false);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.mapView.onDestroy();
        binding = null;
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
