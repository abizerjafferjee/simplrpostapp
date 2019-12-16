package com.codeapex.simplrpostprod.Activity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.model.LatLng

class MapActivity : FragmentActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

}

interface PlaceListner {
    fun latLong(latLng: LatLng)
}