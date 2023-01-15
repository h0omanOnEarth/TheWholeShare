package com.clarissa.thewholeshare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clarissa.thewholeshare.AdminMainActivity
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.models.Location
import com.clarissa.thewholeshare.models.Participant

class ListPackageAdapter(
    var context: Context,
    var data: ArrayList<Participant>
):RecyclerView.Adapter<ListPackageAdapter.MyHolder>() {
    class MyHolder (it: View) : RecyclerView.ViewHolder(it){
        var tvid: TextView = it.findViewById(R.id.tvidpackage)
        var tvloc: TextView = it.findViewById(R.id.tvjalan)
        var tvnama: TextView = it.findViewById(R.id.tvfullname)
        var linear: LinearLayout = it.findViewById(R.id.linear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        var convertview = LayoutInflater.from(context).inflate(R.layout.item_package, parent, false)
        return ListPackageAdapter.MyHolder(convertview)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = data[position]
        holder.tvid.setText("#" + item.id)
        holder.tvloc.setText(item.pickup)
        holder.tvnama.setText(item.fullname)
        holder.linear.setOnClickListener(View.OnClickListener {
            (context as AdminMainActivity).switchFragVerif(item.id)
        })
    }

    override fun getItemCount(): Int {
        return data.size
    }
}