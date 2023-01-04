package com.clarissa.thewholeshare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.models.Location
import com.clarissa.thewholeshare.models.Participant

class StatusAdapter(
    private val context: Context,
    private val arrStatus:MutableList<Participant>,
    private val layout:Int
): RecyclerView.Adapter<StatusAdapter.CustomViewHolder>()  {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        var itemView = LayoutInflater.from(parent.context)
        return CustomViewHolder(itemView.inflate(
            layout, parent ,false
        ))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var item = arrStatus[position]

        holder.tvID.text = "#"+item.id.toString()
        holder.tvFrom.text = item.pickup
    }

    override fun getItemCount(): Int {
        return arrStatus.size
    }

    inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view){
        var tvID: TextView = view.findViewById(R.id.tvId_status)
        var tvTo: TextView = view.findViewById(R.id.tvTo_status)
        var tvFrom : TextView = view.findViewById(R.id.tvFrom_status)
        var tvStatus : TextView = view.findViewById(R.id.tvStatus_status)
    }
}