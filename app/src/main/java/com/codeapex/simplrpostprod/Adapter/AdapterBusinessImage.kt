package com.codeapex.simplrpostprod.Adapter

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codeapex.simplrpostprod.R
import kotlinx.android.synthetic.main.lyt_image.view.*



class AdapterBusinessImage(val arrayList: ArrayList<String>, val activity: Activity) : RecyclerView.Adapter<VHService>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHService {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lyt_image, parent, false)
        return VHService(view)
    }



    override fun getItemCount(): Int {

        return arrayList.size
    }

    override fun onBindViewHolder(holder: VHService, position: Int) {
        holder.item.img_cross_one.setOnClickListener {
            arrayList.removeAt(position)
            notifyDataSetChanged()
        }
        Glide.with(activity).load( Uri.parse(arrayList.get(position))).into( holder.item.img_service);

    }

}

class VHBusiness(val item: View) : RecyclerView.ViewHolder(item) {
}