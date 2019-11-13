package com.codeapex.simplrpostprod.Adapter

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codeapex.simplrpostprod.R
import kotlinx.android.synthetic.main.lyt_image.view.*


class AdapterServiceImage(val arrayList: ArrayList<String>, val activity: Activity, val picker: ImagePicker) : RecyclerView.Adapter<VHService>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHService {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lyt_image, parent, false)
        return VHService(view)
    }


    override fun getItemCount(): Int {
        if (arrayList.size == 3)
            return arrayList.size
        return arrayList.size + 1
    }

    override fun onBindViewHolder(holder: VHService, position: Int) {
        holder.item.img_cross_one.setOnClickListener {
            arrayList.removeAt(holder.adapterPosition)
            notifyDataSetChanged()
        }

        if (position >= arrayList.size) {
            holder.item.img_service.setImageDrawable(holder.item.context.getDrawable(R.drawable.placeholder))
            holder.item.img_cross_one.visibility = View.GONE
            holder.item.setOnClickListener {
                picker.pickImage()
            }
        } else {
            holder.item.img_cross_one.visibility = View.VISIBLE
            holder.item.setOnClickListener(null)
            Log.d("pankaj_working","file name ${arrayList[position]}")
            Glide.with(activity).load( Uri.parse(arrayList.get(position))).error(Glide.with(activity).load(R.drawable.pdf))
                    .into( holder.item.img_service)

        }
    }

    fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor = activity.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

}

interface ImagePicker {
    fun pickImage()
}

class VHService(val item: View) : RecyclerView.ViewHolder(item) {
}

//private fun uploadFile() {
//    // Map is used to multipart the file using okhttp3.RequestBody
//    val file = File(mediaPath)
//
//    // Parsing any Media type file
//    val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
//    val fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody)
//    val filename = RequestBody.create(MediaType.parse("text/plain"), file.getName())
//
//    val getResponse = AppConfig.getRetrofit().create(ApiConfig::class.java)
//    val call = getResponse.uploadFile(fileToUpload, filename)
//    call.enqueue(object : Callback() {
//        fun onResponse(call: Call, response: Response) {
//            val serverResponse = response.body()
//            if (serverResponse != null) {
//                if (serverResponse!!.getSuccess()) {
//                    Toast.makeText(getApplicationContext<T>(), serverResponse!!.getMessage(), Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(getApplicationContext<T>(), serverResponse!!.getMessage(), Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                assert(serverResponse != null)
//                Log.v("Response", serverResponse!!.toString())
//            }
//            progressDialog.dismiss()
//        }
//
//        fun onFailure(call: Call, t: Throwable) {
//
//        }
//    })
//}
