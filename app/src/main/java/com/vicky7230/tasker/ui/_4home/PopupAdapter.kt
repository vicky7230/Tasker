package com.vicky7230.tasker.ui._4home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.vicky7230.tasker.R

class PopupAdapter(private val list: ArrayList<String>) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val view = layoutInflater.inflate(R.layout.popup_list_item, parent, false)
        if (list[position] == "List")
            view.findViewById<AppCompatImageView>(R.id.image).setImageResource(R.drawable.ic_list)
        else
            view.findViewById<AppCompatImageView>(R.id.image).setImageResource(R.drawable.ic_task)
        view.findViewById<AppCompatTextView>(R.id.text).text = list[position]

        if (position == 1)
            view.findViewById<View>(R.id.divider).visibility = View.GONE

        return view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}