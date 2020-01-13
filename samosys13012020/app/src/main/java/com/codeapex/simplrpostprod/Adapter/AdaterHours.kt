package com.codeapex.simplrpostprod.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.codeapex.simplrpostprod.ModelClass.WorkingHour
import com.codeapex.simplrpostprod.R
import kotlinx.android.synthetic.main.businesstiming_card.view.*

class AdaterHours(internal var applicationContext: Context, val myListData: ArrayList<WorkingHour>) : RecyclerView.Adapter<AdaterHours.Holderclass>() {
    private val selectedPosition: Int = 0
    val hourlist = arrayListOf("12 AM", "01 AM", "02 AM", "03 AM", "04 AM", "05 AM", "06 AM", "07 AM", "08 AM", "09 AM", "10 AM", "11 AM",
            "12 PM", "01 PM", "02 PM", "03 PM", "04 PM", "05 PM", "06 PM", "07 PM", "08 PM", "09 PM", "10 PM", "11 PM")

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): AdaterHours.Holderclass {

        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.businesstiming_card, viewGroup, false)


        return Holderclass(view)
    }

    override fun onBindViewHolder(viewHolder: AdaterHours.Holderclass, position: Int) {
        viewHolder.text.text = myListData[position].dayName
        viewHolder.itemView.cb_isopen.isChecked = if (myListData[position].isOpen.equals("1")) true else false
        if (myListData[position].isOpen.equals("1"))
            viewHolder.itemView.lyt_disable.visibility = View.GONE
        else
            viewHolder.itemView.lyt_disable.visibility = View.VISIBLE

        if (viewHolder.sp_start.adapter == null) {
            viewHolder.sp_start.adapter = ArrayAdapter<String>(viewHolder.itemView.context, R.layout.time_layout, hourlist)
            viewHolder.sp_start.setSelection(hourlist.binarySearch(myListData[position].openTime))
            viewHolder.sp_start.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    myListData[position].openTime = viewHolder.sp_start.adapter.getItem(pos).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
            viewHolder.sp_close.adapter = ArrayAdapter<String>(viewHolder.itemView.context, R.layout.time_layout, hourlist)
            viewHolder.sp_close.setSelection(hourlist.indexOf(myListData[position].closeTime))
            viewHolder.sp_close.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    myListData[position].closeTime = viewHolder.sp_close.adapter.getItem(pos).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }

        viewHolder.itemView.cb_isopen.setOnCheckedChangeListener { buttonView, isChecked ->
            myListData[position].isOpen = if (isChecked) "1" else "0"
            viewHolder.itemView.cb_isopen.setOnCheckedChangeListener(null)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return myListData.size
    }

    inner class Holderclass(val item: View) : RecyclerView.ViewHolder(item) {
        internal var text: TextView
        //        internal var txt_start: TextView
//        internal var txt_close: TextView
        internal var sp_start: Spinner
        internal var sp_close: Spinner
        internal var imageView: ImageView? = null

        init {
            text = itemView.findViewById<View>(R.id.textview) as TextView
//            txt_start = itemView.findViewById<View>(R.id.txt_start) as TextView
//            txt_close = itemView.findViewById<View>(R.id.txt_close) as TextView
            sp_start = itemView.findViewById<View>(R.id.sp_start) as Spinner
            sp_close = itemView.findViewById<View>(R.id.sp_close) as Spinner

        }

    }
}



