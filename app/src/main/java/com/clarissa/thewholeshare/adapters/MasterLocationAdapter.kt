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
import com.clarissa.thewholeshare.models.Request

class MasterLocationAdapter(
    var context: Context,
    var data: ArrayList<Location>
):RecyclerView.Adapter<MasterLocationAdapter.MyHolderLocation>(){

    class MyHolderLocation(it: View) : RecyclerView.ViewHolder(it) {
        var tvLocationName: TextView = it.findViewById(R.id.tvLocationName)
        var tvNote: TextView = it.findViewById(R.id.tvNote)
        var tvpending: TextView = it.findViewById(R.id.tvPending)
        var tvverified: TextView = it.findViewById(R.id.tvVerified)
        var llocate: LinearLayout = it.findViewById(R.id.linearlocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolderLocation {
        var convertview = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false)
        return MyHolderLocation(convertview)
    }

    override fun onBindViewHolder(holder: MyHolderLocation, position: Int) {
        var item = data[position]
        println("test adapter alamat : " + item.address)
        holder.tvLocationName.setText(item.address)
        holder.tvNote.setText(item.note)
        var jumlahpending=0
        var jumlahverified=0
        holder.llocate.setOnClickListener {
            val popup = PopupMenu(context,it)
            popup.menuInflater.inflate(R.menu.pop_up_menu_request,popup.menu)
            popup.setOnMenuItemClickListener {
                return@setOnMenuItemClickListener when(it.itemId){
                    R.id.item_update->{
                        (context as AdminMainActivity).switchFragUpdate()
                        true
                    }
                    R.id.item_detail->{
                        (context as AdminMainActivity).switchFragDetail()
                        true
                    }else->{
                        false
                    }
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}