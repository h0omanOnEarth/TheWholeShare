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
import com.clarissa.thewholeshare.models.Request
import org.json.JSONArray

class HomeUserFragment : Fragment() {

    lateinit var rv_ListNews : RecyclerView
    lateinit var newsAdapter : NewsAdapter

    lateinit var arrNews : MutableList<News>
    lateinit var arrRequests : MutableList<Request>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrNews = mutableListOf()
        arrRequests = mutableListOf()
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

        //fetch data requests
        fun fetchRequests(){
            val strReq = object: StringRequest(
                Method.GET,
                "${WholeShareApiService.WS_HOST}/listRequest",
                Response.Listener {
                    val obj: JSONArray = JSONArray(it)
                    arrRequests.clear()
                    println(obj.length())
                    for (i in 0 until obj.length()){
                        val o = obj.getJSONObject(i)
                        println(o)
                        val id = o.getInt("id")
                        val location = o.getString("location")
                        val batch = o.getInt("batch")
                        val deadline = o.get("deadline").toString()
                        val note = o.getString("note")
                        val status = o.getInt("status")
                        val created_at = o.get("created_at").toString()
                        val updated_at = o.get("updated_at").toString()
                        val deleted_at = o.get("deleted_at").toString()

                        val req = Request(
                            id,location,batch,deadline,note,status,created_at,updated_at,deleted_at
                        )
                        arrRequests.add(req)
                    }
                    println(arrRequests.size)
                },
                Response.ErrorListener {
                    Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
                }
            ){}
            val queue: RequestQueue = Volley.newRequestQueue(context)
            queue.add(strReq)
        }

        fetchRequests()
        fetchNews()

    }


    //fetch data news
    fun fetchNews(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listNews",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                arrNews.clear()
                println(obj.length())
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
                    println(o)
                    val id = o.getInt("id")
                    val title = o.getString("title")
                    val content = o.getString("content")
                    val request_id = o.getInt("request_id")
                    val created_at = o.get("created_at").toString()
                    val updated_at = o.get("updated_at").toString()
                    val deleted_at = o.get("deleted_at").toString()

                    val news = News(
                        id,title,content,request_id,created_at,updated_at,deleted_at
                    )
                    arrNews.add(news)
                    newsAdapter.notifyDataSetChanged()
                }
                println(arrNews.size)
            },
            Response.ErrorListener {
                Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }




}