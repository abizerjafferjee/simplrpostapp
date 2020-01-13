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

package com.codeapex.simplrpostprod.opencodelocation.search;

import android.location.Location;

import com.codeapex.simplrpostprod.opencodelocation.code.OpenLocationCodeUtil;
import com.codeapex.simplrpostprod.opencodelocation.localities.Locality;
import com.codeapex.simplrpostprod.Activity.AddLocation_Activity;
import com.codeapex.simplrpostprod.opencodelocation.map.MyMapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.openlocationcode.OpenLocationCode;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements SearchContract.ActionsListener {

    private final SearchContract.SourceView mSourceView;

    private final List<SearchContract.TargetView> mTargetViews;

    public SearchPresenter(
            SearchContract.SourceView sourceView, List<SearchContract.TargetView> targetViews) {
        mSourceView = sourceView;
        mTargetViews = targetViews;
    }

    @Override
    public boolean searchCode(String codeStr) {
        if (codeStr.trim().isEmpty()) {
            mSourceView.showEmptyCode();
        } else {
            // We need to convert the search string into an OLC code. To do this, we might need
            // a location. We prefer the center of the map, or the current location if the map
            // cannot be loaded.
            OpenLocationCode code;
            CameraPosition position = AddLocation_Activity.getMainPresenter().getMapCameraPosition();
            Location location = AddLocation_Activity.getMainPresenter().getCurrentLocation();
            if (position != null) {
                code =
                        OpenLocationCodeUtil.getCodeForSearchString(
                                codeStr.trim(),
                                position.target.latitude,
                                position.target.longitude);

            } else if (location != null) {
                code =
                        OpenLocationCodeUtil.getCodeForSearchString(
                                codeStr.trim(), location.getLatitude(), location.getLongitude());
            } else {
                // We don't have a map or a location for the user. Use the map default location.
                code =
                        OpenLocationCodeUtil.getCodeForSearchString(
                                codeStr.trim(),
                                MyMapView.INITIAL_MAP_LATITUDE,
                                MyMapView.INITIAL_MAP_LONGITUDE);
            }

            if (code != null) {
                for (SearchContract.TargetView view : mTargetViews) {
                    view.showSearchCode(code);
                }
                // If we are showing a result, stop updating the code etc when the map is dragged.
                AddLocation_Activity.getMainPresenter().getMapActionsListener().stopUpdateCodeOnDrag();
                return true;
            }
        }
        mSourceView.showInvalidCode();
        return false;
    }

    @Override
    public void getSuggestions(String code) {
        OpenLocationCode currentLocationCode = null;
        if (AddLocation_Activity.getMainPresenter().getCurrentLocation() != null) {
            currentLocationCode =
                    OpenLocationCodeUtil.createOpenLocationCode(
                            AddLocation_Activity.getMainPresenter().getCurrentLocation().getLatitude(),
                            AddLocation_Activity.getMainPresenter().getCurrentLocation().getLongitude());
        }
        OpenLocationCode mapLocationCode = null;
        if (AddLocation_Activity.getMainPresenter().getMapCameraPosition() != null) {
            CameraPosition position = AddLocation_Activity.getMainPresenter().getMapCameraPosition();
            mapLocationCode =
                    OpenLocationCodeUtil.createOpenLocationCode(
                            position.target.latitude, position.target.longitude);
        }
        List<String> localities =
                Locality.getAllLocalitiesForSearchDisplay(currentLocationCode, mapLocationCode);
        List<String> suggestions = new ArrayList<>();
        for (String locality : localities) {
            suggestions.add(code + " " + locality);
        }
        mSourceView.showSuggestions(suggestions);
    }

    @Override
    public void setSearchText(String text) {
        mSourceView.setText(text);
    }
}
