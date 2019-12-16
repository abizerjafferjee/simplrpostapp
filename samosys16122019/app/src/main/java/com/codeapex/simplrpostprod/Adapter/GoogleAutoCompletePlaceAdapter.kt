package com.codeapex.simplrpostprod.Adapter

import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.codeapex.simplrpostprod.Activity.PlaceListner
import com.codeapex.simplrpostprod.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.compat.AutocompleteFilter
import com.google.android.libraries.places.compat.Places
import kotlinx.coroutines.*
import java.util.*

/**
 * Adapter that handles autocomplete requests from the Google Places API.
 * Results are encoded as GoogleAutoCompletePlaceAdapter.PlaceAutoComplete objects
 * that contain both the place ID and the text addressFullText from the autocomplete query.
 */
class GoogleAutoCompletePlaceAdapter
/**
 * Initializes with a resource for text rows and autocomplete query bounds.
 *
 * @param context Context of current activity
 * @param filter  Filter param according to which results arrived
 * @see android.widget.ArrayAdapter.ArrayAdapter
 */
(context: Context,
 /**
  * The autocomplete filter used to restrict queries to a specific set of place types.
  */
 private val mPlaceFilter: AutocompleteFilter) : ArrayAdapter<GoogleAutoCompletePlaceAdapter.PlaceAutoComplete>(context, R.layout.dropdown_view_for_location), Filterable {

    /**
     * Debug logs get traced.
     */
    private val strTAG = this.javaClass.simpleName
    /**
     * Current results returned by this adapter.
     */
    private var mResultList: ArrayList<PlaceAutoComplete>? = null

    /**
     * The Google Play services client that handles autocomplete requests.
     */
    //    private GoogleApiClient mGoogleApiClient;

    /**
     * The bounds used for Google Places API autocomplete requests.
     */
    private val mBounds: LatLngBounds
    val BOUNDS_INDIA = LatLngBounds(LatLng(7.798000, 68.14712),
            LatLng(37.090000, 97.34466))

    init {
        mBounds = BOUNDS_INDIA
    }

    /**
     * Sets the Google Play services client used for autocomplete queries.
     * Autocomplete queries are suspended when the client is set to null.
     * Ensure that the client has successfully connected, contains the [Places.GEO_DATA_API]
     * API and is available for queries, otherwise API access will be disabled when it is set here.
     *
     * @param googleApiClient GoogleApiClient instance
     */
    //    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
    //        if (googleApiClient == null || !googleApiClient.isConnected()) {
    //            mGoogleApiClient = null;
    //        } else {
    //            mGoogleApiClient = googleApiClient;
    //        }
    //    }

    /**
     * Returns the number of results received in the last autocomplete query.
     */
    override fun getCount(): Int {
        return if (mResultList != null && mResultList!!.size > 0) {
            mResultList!!.size
        } else {
            0
        }
    }

    /**
     * Returns an item from the last autocomplete query.
     */
    override fun getItem(position: Int): PlaceAutoComplete? {
        try {
            return if (mResultList != null && position < mResultList!!.size) {
                mResultList!![position]
            } else {
                null
            }
        } catch (e: Exception) {
            return null
        }

    }


    /**
     * Returns the filter for the current set of autocomplete results.
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
                val results = Filter.FilterResults()
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutocomplete(constraint)
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList
                        results.count = mResultList!!.size
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults?) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged()
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    /**
     * Submits an autocomplete query to the Google Places API.
     * Results are returned as GoogleAutoCompletePlaceAdapter.PlaceAutoComplete
     * objects to store the place ID and addressFullText that the API returns.
     * Returns an empty list if no results were found.
     * Returns null if the API client is not available or the query did not complete
     * successfully.
     * This method MUST be called off the main UI thread, as it will block until data is returned
     * from the API, which may include a network request.
     *
     * @param constraint Autocomplete query string.
     * @return Results from the autocomplete API or null if the query was not successful.
     * @see Places.GEO_DATA_API.getAutocomplete
     */
    private fun getAutocomplete(constraint: CharSequence?): ArrayList<PlaceAutoComplete>? = runBlocking {
        //        if (mGoogleApiClient != null) {
        if (constraint == null)
            return@runBlocking null

        // Submit the query to the autocomplete API and retrieve a PendingResult that will
        // contain the results when the query completes.
        /*PendingResult<AutocompletePredictionBuffer> results =
//                    Places.getGeoDataClient(getContext()).getAutocompletePredictions(
//                            mGoogleApiClient, constraint.toString(),
//                            mBounds, mPlaceFilter
//                    );*/
        val resultList = ArrayList<PlaceAutoComplete>()

        CoroutineScope(Dispatchers.IO).launch {
            val results = Places.getGeoDataClient(context).getAutocompletePredictions(
                    constraint.toString(), mBounds, mPlaceFilter)
            suspendCancellableCoroutine<ArrayList<PlaceAutoComplete>> { result ->
                results.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val iterator = task.result!!.iterator()
                        while (iterator.hasNext()) {
                            val prediction = iterator.next()

                            resultList.add(PlaceAutoComplete(prediction
                                    .getFullText(StyleSpan(Typeface.NORMAL)),
                                    prediction.placeId!!))
                        }
                    }
                    result.resumeWith(Result.success(resultList))
                }

            }

        }.join()
        return@runBlocking resultList
        // This method should have been called off the main UI thread. Block and wait for at
        // most 60s for a result from the API.
        //            final int timeInSeconds = 60;
        //            AutocompletePredictionBuffer autocompletePredictions = results
        //                    .await(timeInSeconds, TimeUnit.SECONDS);

        // Confirm that the query completed successfully, otherwise return null.
        //            final Status status = autocompletePredictions.getStatus();
        //            if (!status.isSuccess()) {
        //                Log.e(strTAG, "Error getting autocomplete prediction API call: " + status.toString());
        //                autocompletePredictions.release();
        //                return null;
        //            }

        //            Log.d(strTAG, "Query completed. Received " + autocompletePredictions.getCount()
        //                    + " predictions.");

        // Copy the results into our own data structure, because we can't hold onto the buffer.
        // AutocompletePrediction objects encapsulate the API response
        // (place ID and addressFullText).
        //            Iterator<AutocompletePrediction> iterator = results.iterator();
        //            ArrayList<PlaceAutoComplete> resultList = new ArrayList<>();
        //            while (iterator.hasNext()) {
        //                AutocompletePrediction prediction = iterator.next();
        //                // Get the details of this prediction and copy it into a new PlaceAutoComplete
        //                // object.
        //
        //                resultList.add(new PlaceAutoComplete(prediction
        //                        .getFullText(new StyleSpan(Typeface.NORMAL)),
        //                        prediction.getPlaceId()));
        //            }

        // Release the buffer now that all data has been copied.
        //            autocompletePredictions.release();

//        return null
        //        }
        //        Log.e(strTAG, "Google API client is not connected for autocomplete query.");
        //        return null;
    }

    /**
     * get latitude, longitude for selected place.
     *
     * @param mSelectedPlaceId Place id
     * @param profile          User profile.
     */
    fun getLatLong(mSelectedPlaceId: String, placeListner: PlaceListner) {
        //final LatLng[] latLngForLocation = new LatLng[1];
        val result = Places.getGeoDataClient(context).getPlaceById(mSelectedPlaceId)
        result.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val response = task.result!!
                val myPlace = response.get(0)
                val latLngObj = myPlace.latLng
                Log.v("Latitude is", "" + latLngObj.latitude)
                Log.v("pankaj_next_listener", "" + latLngObj.latitude)
                Log.v("Longitude is", "" + latLngObj.longitude)
                placeListner.latLong(latLngObj)
//                profile.addressLatitude = latLngObj.latitude.toString()
//                profile.addressLongitude = latLngObj.longitude.toString()
            }
        }
//                result.addOnCompleteListener(new ResultCallback<PlaceBuffer>() {
//                    @Override
//                    public void onResult(@NonNull PlaceBuffer places) {
//                        if (places.getStatus().isSuccess()) {
//                            final Place myPlace = places.get(0);
//                            LatLng latLngObj = myPlace.getLatLng();
//                            Log.v("Latitude is", "" + latLngObj.latitude);
//                            Log.v("Longitude is", "" + latLngObj.longitude);
//                            profile.setAddressLatitude(String.valueOf(latLngObj.latitude));
//                            profile.setAddressLongitude(String.valueOf(latLngObj.longitude));
//
//                            Log.d(strTAG, "Captured location successfully.");
//                        }
//                        places.release();
//                    }
//                });
//        return latLngForLocation[0];
    }

    /**
     * get latitude, longitude for selected place.
     *
     * @param mSelectedPlaceId Place id
     * @param me               Me object.
     */
//    fun setLatLngInMe(mSelectedPlaceId: String, me: Me?) {
//        //final LatLng[] latLngForLocation = new LatLng[1];
//
//        if (me == null) {
//            Log.e(strTAG, "Me object is null")
//            return
//        }
//        val result = Places.getGeoDataClient(context).getPlaceById(mSelectedPlaceId)
//        result.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val response = task.result!!
//                val myPlace = response.get(0)
//                val latLngObj = myPlace.latLng
//                Log.v("Latitude is", "" + latLngObj.latitude)
//                Log.v("Longitude is", "" + latLngObj.longitude)
//                me.addressLatitude = latLngObj.latitude.toString()
//                me.addressLongitude = latLngObj.longitude.toString()
//            }
//        }
//        //        PendingResult<PlaceBuffer> result = Places.GeoDataApi.getPlaceById(mGoogleApiClient, mSelectedPlaceId);
//        //        result.setResultCallback(new ResultCallback<PlaceBuffer>() {
//        //            @Override
//        //            public void onResult(@NonNull PlaceBuffer places) {
//        //                if (places.getStatus().isSuccess()) {
//        //                    if (places.getCount() > 0) {
//        //                        final Place myPlace = places.get(0);
//        //                        LatLng latLngObj = myPlace.getLatLng();
//        //                        Log.v("Latitude is", "" + latLngObj.latitude);
//        //                        Log.v("Longitude is", "" + latLngObj.longitude);
//        //                        me.setAddressLatitude(String.valueOf(latLngObj.latitude));
//        //                        me.setAddressLongitude(String.valueOf(latLngObj.longitude));
//        //
//        //                        Log.d(strTAG, "Captured location successfully.");
//        //                    }
//        //                }
//        //                places.release();
//        //            }
//        //        });
//        //return latLngForLocation[0];
//    }

    /**
     * Holder for Google Places API autocomplete results.
     */
    inner class PlaceAutoComplete
    /**
     * Initializing Address.
     *
     * @param addressFullText Address
     * @param placeId         placeId
     */
    internal constructor(
            /**
             * To hold the Description in place Autocomplete result.
             */
            private val addressFullText: CharSequence,
            /**
             * To hold the place id in place Autocomplete result.
             */
            /**
             * Get ID for place.
             *
             * @return String ID of place.
             */
            val placeId: String) {

        override fun toString(): String {
            //Log.d(strTAG, "AutoComplete Place: " + addressFullText.toString());
            return addressFullText.toString()
        }

    }
}
