package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.adapters.NewsAdapter
import com.clarissa.thewholeshare.adapters.StatusAdapter
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.News
import com.clarissa.thewholeshare.models.Participant
import com.clarissa.thewholeshare.models.Request
import com.clarissa.thewholeshare.models.User
import com.google.gson.Gson
import org.json.JSONArray

class UserListStatusFragment(
    var username:String,
    var arrParticipants: MutableList<Participant>,
    var arrRequests:MutableList<Request>,
    var arrExpiredRequests : MutableList<Request>,
    var arrUsers : MutableList<User>
) : Fragment() {

    lateinit var userActive : User
    lateinit var rvListStatus_User:RecyclerView

    lateinit var statusAdapter: StatusAdapter

    var onClickButton:((resource:String,status:Participant)->Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userActive = User(-1,"","","","","","",-1,"")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_list_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvListStatus_User = view.findViewById(R.id.rvListStatus_User)
        rvListStatus_User.layoutManager = LinearLayoutManager(view.context.applicationContext)

        //to get user who logged in
        fun getUserLoggedIn(){
            for(i in arrUsers.indices){
                if(arrUsers[i].username==username){
                    userActive = arrUsers[i]
                    break
                }
            }
        }

        statusAdapter =  StatusAdapter(view.context,arrParticipants,arrRequests,R.layout.item_user_status)
        rvListStatus_User.adapter = statusAdapter

        getUserLoggedIn()
        fetchParticipants()
        fetchRequests()
        editStatusParticipants()

        statusAdapter.onClick = object:StatusAdapter.clickListener{
            override fun onClick(status: Participant) {
                onClickButton?.invoke("detail",status)
            }
        }

    }


    //fetch data requests
    fun fetchRequests(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listRequest",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                arrRequests.clear()
                statusAdapter.notifyDataSetChanged()
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
                    val id = o.getInt("id")
                    val location = o.getString("location")
                    val batch = o.getInt("batch")
                    val deadline = o.get("deadline").toString()
                    val note = o.getString("note")
                    val status = o.getString("status")
                    val created_at = o.get("created_at").toString()
                    val updated_at = o.get("updated_at").toString()
                    val deleted_at = o.get("deleted_at").toString()

                    val req = Request(
                        id,location,batch,deadline,note,status,created_at,updated_at,deleted_at
                    )
                    arrRequests.add(req)
                    statusAdapter.notifyDataSetChanged()
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
                statusAdapter.notifyDataSetChanged()
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
//                    val id = o.getInt("id")
//                    val user_id = o.getInt("user_id")
//                    val request_id = o.getInt("request_id")
//                    val courier_id = o.getInt("courier_id")
//                    val pickup = o.getString("pickup")
//                    val note = o.getString("note")
//                    val status = o.getInt("status")
//                    val created_at = o.get("created_at").toString()
//                    val updated_at = o.get("updated_at").toString()
//
//                    val participant = Participant(
//                        id,user_id,request_id, courier_id,pickup,note,status,created_at,updated_at
//                    )

                    val participant = Gson().fromJson(o.toString(), Participant::class.java)

                    if(participant.user_id == userActive.id){
                        arrParticipants.add(participant)
                        statusAdapter.notifyDataSetChanged()
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

    fun doUpdateStatusParticipant(id:Int,status:Int){
        val strReq = object : StringRequest(
            Method.POST,
            "${WholeShareApiService.WS_HOST}/updateStatusParticipants",
            Response.Listener {
                fetchParticipants()
                fetchRequests()
            },
            Response.ErrorListener {
                println(it.message)
                Toast.makeText(context,it.message, Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String,String>()
                params["id"] = id.toString()
                params["status"] = status.toString()
                return params
            }
        }
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }

    //fungsi untuk mengubah status participants yang pending menjadi canceled karena expired
    fun editStatusParticipants(){
        for(i in arrParticipants.indices){
            for( j in arrExpiredRequests.indices){
                if(arrParticipants[i].request_id== arrExpiredRequests[j].id){
                    if(arrParticipants[i].status==0) {
                        //jika pending maka ubah menjadi canceled (3)
                        doUpdateStatusParticipant(arrParticipants[i].id,3)
                    }
                }
            }
        }
    }

}