package com.maku.shyfy

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_listview.view.*

class MyAdapter(private val myDataset: MutableCollection<WifiP2pDevice>, val context: Context) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val name: TextView = view.label
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_listview, parent, false) )
    }

    override fun getItemCount(): Int {
      return  myDataset.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name =  myDataset
        holder.name.text = name .toString()
    }


}