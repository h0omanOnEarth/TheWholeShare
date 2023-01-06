package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.News
import com.clarissa.thewholeshare.models.Request
import org.json.JSONArray

class UserDonateReportFragment(
    var current_news : News
) : Fragment() {

    lateinit var tvTitle_detailNews : TextView
    lateinit var tvLocation_detailNews : TextView
    lateinit var tvBatch_detailNews: TextView
    lateinit var tvDeadline_detailNews: TextView
    lateinit var tvContent_detailNews: TextView
    lateinit var btnBack_detailNews: Button

    lateinit var arrRequests : MutableList<Request>

    var onClickButton:((resource:String)->Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrRequests = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_donate_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTitle_detailNews = view.findViewById(R.id.tvTitle_reportDetailDonate)
        tvLocation_detailNews = view.findViewById(R.id.tvLocation_reportDetailDonate)
        tvBatch_detailNews = view.findViewById(R.id.tvBatch_reportDetailDonate)
        tvDeadline_detailNews = view.findViewById(R.id.tvDeadline_reportDetailDonate)
        tvContent_detailNews = view.findViewById(R.id.tvContent_reportDetailDonate)
        btnBack_detailNews = view.findViewById(R.id.btnBack_reportDetailDonate)

        btnBack_detailNews.setOnClickListener {
            onClickButton?.invoke("back")
        }

        refreshList()

        tvTitle_detailNews.text = current_news.title
        tvContent_detailNews.text = current_news.content

        for(i in arrRequests.indices){
            if(arrRequests[i].id==current_news.request_id){
                tvLocation_detailNews.text = arrRequests[i].location
                tvBatch_detailNews.text = arrRequests[i].batch.toString()
                tvDeadline_detailNews.text = arrRequests[i].deadline
                break
            }
        }

    }

    //fetch data requests
    fun refreshList(){
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


}