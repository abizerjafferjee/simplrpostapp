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

import android.location.Location;
import android.view.View;
import android.view.View.OnClickListener;

import com.codeapex.simplrpostprod.Activity.AddLocation_Activity;
import com.codeapex.simplrpostprod.opencodelocation.code.CodeContract;
import com.codeapex.simplrpostprod.opencodelocation.direction.Direction;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;


public class MapPresenter implements MapContract.ActionsListener {

    private final MyMapView mView;

    private final CodeContract.ActionsListener mCodeActionsListener;

    public MapPresenter(
            MyMapView view,
            CodeContract.ActionsListener codeListener) {
        mView = view;
        mCodeActionsListener = codeListener;

        mView
                .getSatelliteButton()
                .setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int mapType = mView.getMapType();
                                if (mapType == -1) {
                                    // Map is not initialized
                                    return;
                                }
                                if (mapType != GoogleMap.MAP_TYPE_HYBRID) {
                                    requestSatelliteView();
                                } else {
                                    requestRoadView();
                                }
                            }
                        });

        mView
                .getMyLocationButton()
                .setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Location currentLocation =
                                        AddLocation_Activity.getMainPresenter().getCurrentLocation();
                                if (currentLocation == null) {
                                    AddLocation_Activity.getMainPresenter().loadCurrentLocation();
                                } else {
                                    moveMapToLocation(currentLocation);
                                }
                            }
                        });
    }

    @Override
    public void mapChanged(double latitude, double longitude) {
        Direction direction = mCodeActionsListener.codeLocationUpdated(latitude, longitude, false);

        if (direction.getToCode() != null) {
            mView.drawCodeArea(direction.getToCode());
        }

    }

    @Override
    public void requestSatelliteView() {
        mView.showSatelliteView();
    }

    @Override
    public void requestRoadView() {
        mView.showRoadView();
    }

    @Override
    public void moveMapToLocation(Location location) {
        if (location != null) {
            Direction direction =
                    mCodeActionsListener.codeLocationUpdated(
                            location.getLatitude(), location.getLongitude(), true);
            mView.moveMapToLocation(direction.getFromCode());
        }
    }

    @Override
    public CameraPosition getMapCameraPosition() {
        return mView.getCameraPosition();
    }

    @Override
    public void setMapCameraPosition(double latitude, double longitude, float zoom) {
        mView.setCameraPosition(latitude, longitude, zoom);
    }

    @Override
    public void startUpdateCodeOnDrag() {
        mView.startUpdateCodeOnDrag();
    }

    @Override
    public void stopUpdateCodeOnDrag() {
        mView.stopUpdateCodeOnDrag();
    }
}
