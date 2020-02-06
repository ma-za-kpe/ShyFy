package com.maku.shyfy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_listview.view.*

class MyAdapter(private val myDataset: ArrayList<String>, val context: Context) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val name = view.label
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_listview, parent, false) as TextView)
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        myDataset.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        holder?.name?.text = myDataset.get(position)
    }


}