/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codeapex.simplrpostprod.opencodelocation.main;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.opencodelocation.code.CodeContract;
import com.codeapex.simplrpostprod.opencodelocation.code.CodePresenter;
import com.codeapex.simplrpostprod.opencodelocation.code.CodeView;
import com.codeapex.simplrpostprod.opencodelocation.current.DaggerLocationProviderFactoryComponent;
import com.codeapex.simplrpostprod.opencodelocation.current.GoogleApiModule;
import com.codeapex.simplrpostprod.opencodelocation.current.LocationProvider;
import com.codeapex.simplrpostprod.opencodelocation.current.LocationProviderFactoryComponent;
import com.codeapex.simplrpostprod.opencodelocation.direction.Direction;
import com.codeapex.simplrpostprod.opencodelocation.direction.DirectionContract;
import com.codeapex.simplrpostprod.opencodelocation.direction.DirectionPresenter;
import com.codeapex.simplrpostprod.opencodelocation.direction.DirectionUtil;
import com.codeapex.simplrpostprod.opencodelocation.direction.DirectionView;
import com.codeapex.simplrpostprod.opencodelocation.map.MapContract;
import com.codeapex.simplrpostprod.opencodelocation.map.MapPresenter;
import com.codeapex.simplrpostprod.opencodelocation.map.MyMapView;
import com.codeapex.simplrpostprod.opencodelocation.search.SearchContract;
import com.codeapex.simplrpostprod.opencodelocation.search.SearchPresenter;
import com.codeapex.simplrpostprod.opencodelocation.search.SearchView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.openlocationcode.OpenLocationCode;
import java.util.ArrayList;
import java.util.List;


/**
 * This Presenter takes care of obtaining the user current location, as well as synchronising data
 * between all the features: search, code, direction, and map.
 */
public class MainPresenter implements LocationProvider.LocationCallback {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private final CodeContract.ActionsListener mCodeActionsListener;

    private final MapContract.ActionsListener mMapActionsListener;

    private final Context mContext;

    private final LocationProvider mLocationProvider;

    private Location mCurrentLocation;

    public MainPresenter(
            Context context,
            CodeView codeView,
            MyMapView mapView) {
        List<SearchContract.TargetView> searchTargetViews = new ArrayList<>();
        searchTargetViews.add(codeView);
        searchTargetViews.add(mapView);
        mContext = context;
        mCodeActionsListener = new CodePresenter(codeView);
        mMapActionsListener =
                new MapPresenter(mapView, mCodeActionsListener);
        mapView.setListener(mMapActionsListener);

        LocationProviderFactoryComponent locationProviderFactoryComponent =
                DaggerLocationProviderFactoryComponent.builder()
                        .googleApiModule(new GoogleApiModule(context))
                        .build();
        mLocationProvider =
                locationProviderFactoryComponent.locationProviderFactory().create(context, this);
    }

    public void loadCurrentLocation() {
        /*Toast.makeText(
                mContext, mContext.getResources().getString(R.string.current_location_loading),
                Toast.LENGTH_LONG).show();*/
        mLocationProvider.connect();
    }

    public void currentLocationUpdated(Location location) {
        if (location.hasBearing() && getCurrentOpenLocationCode() != null) {
            Direction direction =
                    DirectionUtil.getDirection(location, getCurrentOpenLocationCode());
        }
        if (mCurrentLocation == null) {
            // This is the first location received, so we can move the map to this position.
            mMapActionsListener.setMapCameraPosition(
                    location.getLatitude(), location.getLongitude(), MyMapView.INITIAL_MAP_ZOOM);
        }
        mCurrentLocation = location;
    }

    public void stopListeningForLocation() {
        mLocationProvider.disconnect();
    }

    @Override
    public void handleNewLocation(Location location) {
        Log.i(TAG, "Received new location from LocationProvider: " + location);
        currentLocationUpdated(location);
    }

    @Override
    public void handleNewBearing(float bearing) {
        Log.i(TAG, "Received new bearing from LocationProvider: " + bearing);
        if (mCurrentLocation != null) {
            mCurrentLocation.setBearing(bearing);
            currentLocationUpdated(mCurrentLocation);
        }
    }

    @Override
    public void handleLocationNotAvailable() {
        Toast.makeText(
                mContext,
                mContext.getResources().getString(R.string.current_location_error),
                Toast.LENGTH_LONG)
                .show();
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    private OpenLocationCode getCurrentOpenLocationCode() {
        return mCodeActionsListener.getCurrentOpenLocationCode();
    }

    public CameraPosition getMapCameraPosition() {
        return mMapActionsListener.getMapCameraPosition();
    }


    public void setMapCameraPosition(double latitude, double longitude, float zoom) {
        mMapActionsListener.setMapCameraPosition(latitude, longitude, zoom);
    }

    public MapContract.ActionsListener getMapActionsListener() {
        return mMapActionsListener;
    }
}
