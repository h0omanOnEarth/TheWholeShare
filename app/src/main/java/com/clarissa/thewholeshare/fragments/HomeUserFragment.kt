package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.adapters.NewsAdapter
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.News
import com.clarissa.thewholeshare.models.Participant
import com.clarissa.thewholeshare.models.Request
import com.clarissa.thewholeshare.models.User
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.json.JSONArray

class HomeUserFragment(
    var arrRequests : MutableList<Request>,
    var arrNews : MutableList<News>
) : Fragment() {

    lateinit var rv_ListNews : RecyclerView
    lateinit var newsAdapter : NewsAdapter
    lateinit var arrParticipants : MutableList<Participant>

    var onClickButton:((resource:String,news:News)->Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrParticipants = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_ListNews = view.findViewById(R.id.rv_ListNews)
        rv_ListNews.layoutManager = GridLayoutManager(view.context, 2)

        newsAdapter =  NewsAdapter(view.context,arrNews,arrRequests,R.layout.item_news)
        rv_ListNews.adapter = newsAdapter

        fetchNews()

        newsAdapter.onClick = object:NewsAdapter.clickListener{
            override fun onEdit(news: News) {
                onClickButton?.invoke("edit",news)
            }
        }

    }
    //fetch data news
    fun fetchNews(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listNews",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                arrNews.clear()
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
//                    val id = o.getInt("id")
//                    val title = o.getString("title")
//                    val content = o.getString("content")
//                    val request_id = o.getInt("request_id")
//                    val created_at = o.get("created_at").toString()
//                    val updated_at = o.get("updated_at").toString()
//                    val deleted_at = o.get("deleted_at").toString()
//
//                    val news = News(
//                        id,title,content,request_id,created_at,updated_at,deleted_at
//                    )
                    val news = Gson().fromJson(o.toString(), News::class.java)
                    arrNews.add(news)
                    newsAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener {
                Toast.makeText(requireContext(),"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }

}