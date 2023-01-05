package com.clarissa.thewholeshare.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.models.Location
import com.clarissa.thewholeshare.models.Participant
import com.clarissa.thewholeshare.models.Request

class StatusAdapter(
    private val context: Context,
    private val arrStatus:MutableList<Participant>,
    private val arrRequests:MutableList<Request>,
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

        when (item.status) {
            0 -> { // pending
                holder.imgView_status.setImageResource(R.drawable.time_machine_120px)
                holder.tvStatus.text = "Pending"
            }
            1 -> { // delivering
                holder.imgView_status.setImageResource(R.drawable.on_the_way)
                holder.tvStatus.text = "On The Way"
            }
            2 -> { //finished
                holder.imgView_status.setImageResource(R.drawable.ok_120px)
                holder.tvStatus.text = "Delivered"
            }
            3->{ //canceled
                holder.imgView_status.setImageResource(R.drawable.cancel_120px)
                holder.tvStatus.text = "Canceled"
            }
        }

        var to = ""
        for(i in arrRequests.indices){
            if(arrRequests[i].id==item.request_id){
                to = arrRequests[i].location
                break
            }
        }
        holder.tvTo.text = to
    }

    override fun getItemCount(): Int {
        return arrStatus.size
    }

    inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view){
        var tvID: TextView = view.findViewById(R.id.tvId_status)
        var tvTo: TextView = view.findViewById(R.id.tvTo_status)
        var tvFrom : TextView = view.findViewById(R.id.tvFrom_status)
        var tvStatus : TextView = view.findViewById(R.id.tvStatus_status)
        var imgView_status : ImageView =  view.findViewById(R.id.imgView_status)
    }
}