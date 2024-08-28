package com.gokuldev.hostellock

// CustomAdapter.kt
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CustomAdapter(private val context: Context, private val dataList: List<Any>) : BaseAdapter() {

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.card, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val data = dataList[position]

        if (data is VisitorDataModel) {
            // Handle VisitorDataModel
            holder.textName.text = "${data.name}"
            holder.textNumber.text = "${data.registerNumber}"
            holder.textDate.text = "${data.date}"
            holder.textTime.text = "${data.time}"
        } else if (data is PurposeDataModel) {
            // Handle PurposeDataModel
            holder.textName.text = "${data.name}"
            holder.textNumber.text = "${data.registerNumber}"
            holder.textDate.text = "${data.date}"
            holder.textTime.text = "${data.time}"
        }

        return view
    }

    private class ViewHolder(view: View) {
        val textName: TextView = view.findViewById(R.id.textViewName)
        val textNumber: TextView = view.findViewById(R.id.textViewNumber)
        val textDate: TextView = view.findViewById(R.id.textViewDate)
        val textTime: TextView = view.findViewById(R.id.textViewTime)
    }
}