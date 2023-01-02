package com.clarissa.thewholeshare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(
    private val context: Context,
    private val arrLocations:MutableList<Location>,
    private val layout:Int
) : RecyclerView.Adapter<LocationAdapter.CustomViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationAdapter.CustomViewHolder {
        var itemView = LayoutInflater.from(parent.context)
        return CustomViewHolder(itemView.inflate(
            layout, parent ,false
        ))
    }

    override fun onBindViewHolder(holder: LocationAdapter.CustomViewHolder, position: Int) {
        var item = arrLocations[position]

        holder.tvLocationName.text = item.address
        holder.tvNote.text = item.note
    }

    override fun getItemCount(): Int {
        return arrLocations.size
    }

    inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view){
        var tvLocationName: TextView = view.findViewById(R.id.tvLocationName)
        var tvNote: TextView = view.findViewById(R.id.tvNote)
    }

}