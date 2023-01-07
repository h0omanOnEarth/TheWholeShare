package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.News
import com.clarissa.thewholeshare.models.Participant
import com.clarissa.thewholeshare.models.Request
import org.json.JSONArray

class UserDonateDetailFragment(
    var status : Participant,
    var arrRequests: MutableList<Request>,
    var arrNews : MutableList<News>
) : Fragment() {

    var onClickButton:((resource:String)->Unit)? = null
    lateinit var btnBack : Button
    lateinit var tvTo : TextView
    lateinit var tvFrom : TextView
    lateinit var tvStatus : TextView
    lateinit var tvDate : TextView
    lateinit var imgView : ImageView
    lateinit var tvId : TextView
    lateinit var btnToReport : Button

    var onClickButtonReport:((resource:String,news: News)->Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_donate_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack = view.findViewById(R.id.btnBack_detailUserDonate)
        tvTo = view.findViewById(R.id.tvTo_detailDonateUser)
        tvFrom = view.findViewById(R.id.tvFrom_detailUserDonate)
        tvStatus = view.findViewById(R.id.tvStatus_detailUserDonate)
        tvDate = view.findViewById(R.id.tvDate_detailUserDonate)
        imgView = view.findViewById(R.id.imgView_detailUserDonate)
        tvId = view.findViewById(R.id.tvId_detailDonateUser)
        btnToReport = view.findViewById(R.id.btnToReport_detailUserDonate)

        val statusParticipant = status.status

        if(statusParticipant==0){
            tvStatus.text = "Pending"
            btnToReport.isEnabled = false
        }else if(statusParticipant==1){
            tvStatus.text = "On The Way"
            btnToReport.isEnabled = false
        }else if(statusParticipant==2){
            tvStatus.text = "Delivered"
            btnToReport.isEnabled = true
        }else if(statusParticipant==3){
            tvStatus.text = "Canceled"
            btnToReport.isEnabled = false
        }

        tvId.text = "#" + status.id

        tvFrom.text = status.pickup
        tvDate.text = status.created_at.toString().substring(0,10)

        refreshListRequests()

        for(i in arrRequests.indices){
            if(arrRequests[i].id == status.request_id){
                tvTo.text = arrRequests[i].location
                break
            }
        }

        btnBack.setOnClickListener {
            onClickButton?.invoke("back")
        }

        fetchNews()

        btnToReport.setOnClickListener {

            var news : News = News(-1,"","",-1)

            for(i in arrNews.indices){
                if(arrNews[i].request_id==status.request_id){
                    news = arrNews[i]
                    break
                }
            }

            onClickButtonReport?.invoke("report",news)
        }

    }

    //fetch data Requests
    fun refreshListRequests(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listRequest",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                arrRequests.clear()

                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
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