package com.clarissa.thewholeshare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.models.Location
import com.clarissa.thewholeshare.models.Request

class MasterLocationAdapter(
    var context: Context,
    var data: ArrayList<Location>
):RecyclerView.Adapter<MasterLocationAdapter.MyHolderLocation>(){

    inner class MyHolderLocation(it: View) : RecyclerView.ViewHolder(it) {
        var tvLocationName: TextView = it.findViewById(R.id.tvLocationName)
        var tvNote: TextView = it.findViewById(R.id.tvNote)
        var tvpending: TextView = it.findViewById(R.id.tvPending)
        var tvverified: TextView = it.findViewById(R.id.tvVerified)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolderLocation {
        var convertview = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false)
        return MyHolderLocation(convertview)
    }

    override fun onBindViewHolder(holder: MyHolderLocation, position: Int) {
        var item = data[position]
        holder.tvLocationName.setText(item.address)
        holder.tvNote.setText(item.note)
        var jumlahpending=0
        var jumlahverified=0
    }

    override fun getItemCount(): Int {
        return data.size
    }

}