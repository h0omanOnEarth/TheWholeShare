package com.clarissa.thewholeshare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.models.News
import org.w3c.dom.Text


class NewsAdapter(
    private val context: Context,
    private val arrNews:MutableList<News>,
    private val layout:Int
): RecyclerView.Adapter<NewsAdapter.CustomViewHolder>() {

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
        var item = arrNews[position]

        holder.tvTitle.text = item.title
        holder.tvContent.text = item.content
    }

    override fun getItemCount(): Int {
        return arrNews.size
    }

    inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view){
        var tvTitle: TextView = view.findViewById(R.id.tvTitle_news)
        var tvContent: TextView = view.findViewById(R.id.tvContent_news)
        var tvLocation : TextView = view.findViewById(R.id.tvLocation_News)
        var tvBatch : TextView = view.findViewById(R.id.tvBatch_news)
        var tvDeadline : TextView = view.findViewById(R.id.tvDeadline_news)
    }

}