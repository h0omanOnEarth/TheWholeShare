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
import kotlinx.coroutines.launch
import org.json.JSONArray

class HomeUserFragment(
    var username: String,
    var arrRequests : MutableList<Request>,
    var arrNews : MutableList<News>
) : Fragment() {

    lateinit var rv_ListNews : RecyclerView
    lateinit var newsAdapter : NewsAdapter
    lateinit var arrParticipants : MutableList<Participant>
    lateinit var userActive : User

    var onClickButton:((resource:String,news:News)->Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrParticipants = mutableListOf()
        userActive = User(-1,"","","","","","",-1,"")
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

        getUserLoggedIn(username)
        fetchParticipants()
        fetchNews()

        newsAdapter.onClick = object:NewsAdapter.clickListener{
            override fun onEdit(news: News) {
                onClickButton?.invoke("edit",news)
            }
        }

    }

    //to get user who logged in
    fun getUserLoggedIn(uname:String){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listUsers",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
                    val id = o.getInt("id")
                    val username = o.getString("username")
                    val password = o.getString("password")
                    val full_name = o.getString("full_name")
                    val phone = o.getString("phone")
                    val address = o.getString("address")
                    val email = o.getString("email")
                    val role = o.getInt("role")
                    val deleted_at = o.get("deleted_at").toString()
                    val u = User(
                        id,username,password,full_name,phone,address,email,role,deleted_at
                    )
                    if(username==uname) {
                        userActive = u
                        break
                    }
                }
            },
            Response.ErrorListener {
                Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
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
            },
            Response.ErrorListener {
                Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }

    //fetch data participants
    fun fetchParticipants(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listParticipants",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                arrParticipants.clear()
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
                    val id = o.getInt("id")
                    val user_id = o.getInt("user_id")
                    val request_id = o.getInt("request_id")
                    val pickup = o.getString("pickup")
                    val status = o.getInt("status")
                    val created_at = o.get("created_at").toString()
                    val updated_at = o.get("updated_at").toString()

                    val participant = Participant(
                        id,user_id,request_id,pickup,status,created_at,updated_at
                    )

                    if(user_id==userActive.id){
                        arrParticipants.add(participant)
                    }
                }
            },
            Response.ErrorListener {
                Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }

}