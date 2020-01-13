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

package com.codeapex.simplrpostprod.opencodelocation.map;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.opencodelocation.search.SearchContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.openlocationcode.OpenLocationCode;

import java.net.MalformedURLException;
import java.net.URL;


public class MyMapView extends LinearLayout
        implements MapContract.View, OnMapReadyCallback, SearchContract.TargetView {

    private static final String TAG = MyMapView.class.getSimpleName();
    // The zoom level needs to be close enough in to make the red box visible.
    public static final float INITIAL_MAP_ZOOM = 19.0f;
    // Initial map position is Cape Verde's National Assembly. This will be the center of the map
    // view if no known last location is available.
    public static final double INITIAL_MAP_LATITUDE = 14.905818;
    public static final double INITIAL_MAP_LONGITUDE = -23.514907;

    private static final float CODE_AREA_STROKE_WIDTH = 5.0f;

    private final MapView mMapView;

    public static GoogleMap mMap;

    private final ImageButton mSatelliteButton;

    private final ImageButton mMyLocationButton;

    private Polygon mCodePolygon;

    private boolean mRetryShowingCurrentLocationOnMap;

    private MapContract.ActionsListener mListener;

    private OpenLocationCode mLastDisplayedCode;

    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.my_map, this, true);

        mMapView = findViewById(R.id.map);

        mSatelliteButton = findViewById(R.id.satelliteButton);

        mMyLocationButton = findViewById(R.id.myLocationButton);

        mRetryShowingCurrentLocationOnMap = true;

        mMapView.getMapAsync(this);

    }

    /*
     * Check that the tile server supports the requested x, y and zoom.
     * Complete this stub according to the tile range you support.
     * If you support a limited range of tiles at different zoom levels, then you
     * need to define the supported x, y range at each zoom level.
     */
    private boolean checkTileExists(int x, int y, int zoom) {
        int minZoom = 12;
        int maxZoom = 16;

        if ((zoom < minZoom || zoom > maxZoom)) {
            return false;
        }

        return true;
    }

    public MapView getMapView() {
        return mMapView;
    }

    public ImageButton getSatelliteButton() {
        return mSatelliteButton;
    }

    public ImageButton getMyLocationButton() {
        return mMyLocationButton;
    }

    public int getMapType() {
        if (mMap == null) {
            return -1;
        }
        return mMap.getMapType();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //setCameraPosition(INITIAL_MAP_LATITUDE, INITIAL_MAP_LONGITUDE, INITIAL_MAP_ZOOM);
        // Enable the location indicator, but disable the button (so we can implement it where we
        // want).

        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Disable tilt and rotate gestures. Can move the button with setPadding() but that also
        // moves the center of the map, and we can't get the dimensions of searchView yet.
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        startUpdateCodeOnDrag();

        /*final String TILE_URL = "http://tile0.maps.2gis.com/tiles?x=%d&y=%d&z=%d&v=1";
        final int MIN_ZOOM = 2;
        final int MAX_ZOOM = 18;
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                String s = String.format(TILE_URL, x, y, zoom);

                if (zoom < MIN_ZOOM || zoom > MAX_ZOOM) {
                    return null;
                }

                try {
                    return new URL(s);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
            }
        };

        map.setMapType(GoogleMap.MAP_TYPE_NONE);
        map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));*/
    }

    @Override
    public void startUpdateCodeOnDrag() {
        Log.i(TAG, "Starting camera drag listener");
        mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mListener.mapChanged(
                        cameraPosition.target.latitude, cameraPosition.target.longitude);
            }
        });
        // And poke the map.
        CameraPosition cameraPosition = getCameraPosition();
        mListener.mapChanged(cameraPosition.target.latitude, cameraPosition.target.longitude);
    }

    @Override
    public void stopUpdateCodeOnDrag() {
        Log.i(TAG, "Stopping camera drag listener");
        mMap.setOnCameraChangeListener(null);
    }

    @Override
    public void drawCodeArea(final OpenLocationCode code) {
        if (code.equals(mLastDisplayedCode)) {
            // Skip if we're already displaying this location.
            return;
        }
        mLastDisplayedCode = code;
        if (mMap != null) {
            OpenLocationCode.CodeArea area = code.decode();
            LatLng southWest = new LatLng(area.getSouthLatitude(), area.getWestLongitude());
            LatLng northWest = new LatLng(area.getNorthLatitude(), area.getWestLongitude());
            LatLng southEast = new LatLng(area.getSouthLatitude(), area.getEastLongitude());
            LatLng northEast = new LatLng(area.getNorthLatitude(), area.getEastLongitude());

            if (mCodePolygon != null) {
                mCodePolygon.remove();
            }

            mCodePolygon = mMap.addPolygon(
                    new PolygonOptions().add(southWest, northWest, northEast, southEast)
                            .strokeColor(ContextCompat.getColor(this.getContext(), R.color.code_box_stroke))
                            .strokeWidth(CODE_AREA_STROKE_WIDTH)
                            .fillColor(ContextCompat.getColor(this.getContext(), R.color.code_box_fill)));
        }
    }

    @Override
    public void moveMapToLocation(final OpenLocationCode code) {
        if (mMap != null) {
            OpenLocationCode.CodeArea area = code.decode();

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(area.getCenterLatitude(), area.getCenterLongitude()),
                    INITIAL_MAP_ZOOM));

            drawCodeArea(code);
        } else {
            // In case the map isn't ready yet
            Handler h = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    moveMapToLocation(code);
                }
            };
            if (mRetryShowingCurrentLocationOnMap) {
                h.postDelayed(r, 2000);
                mRetryShowingCurrentLocationOnMap = false;
            }
        }
    }

    @Override
    public void showSatelliteView() {
        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mSatelliteButton.setSelected(true);
        }
    }

    @Override
    public void showRoadView() {
        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mSatelliteButton.setSelected(false);
        }
    }

    @Override
    public void setListener(MapContract.ActionsListener listener) {
        mListener = listener;
    }

    @Override
    public void showSearchCode(final OpenLocationCode code) {
        moveMapToLocation(code);
    }

    @Override
    public CameraPosition getCameraPosition() {
        if (mMap == null) {
            return null;
        }
        return mMap.getCameraPosition();
    }

    @Override
    public void setCameraPosition(double latitude, double longitude, float zoom) {
        if (mMap == null) {
            Log.w(TAG, "Couldn't set camera position because the map is null");
        } else {
            mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        }
    }

    /*private void drawPolyline(LatLng latLng, LatLng latLng2) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(latLng, latLng2);
        polylineOptions.color(Color.argb(50, 0, 0, 100));
        polylineOptions.width(3.5f);
        polylineOptions.visible(true);
        polylineOptions.geodesic(true);
        Polyline polyline = mMap.addPolyline(polylineOptions);

        this.polylines.add(polyline);
    }*/
}
