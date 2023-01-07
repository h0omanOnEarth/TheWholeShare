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
import org.json.JSONArray

class UserListStatusFragment(
    var arrParticipants: MutableList<Participant>,
    var arrRequests:MutableList<Request>,
) : Fragment() {

    lateinit var rvListStatus_User:RecyclerView

    lateinit var statusAdapter: StatusAdapter

    var onClickButton:((resource:String,status:Participant)->Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        statusAdapter =  StatusAdapter(view.context,arrParticipants,arrRequests,R.layout.item_user_status)
        rvListStatus_User.adapter = statusAdapter

        statusAdapter.onClick = object:StatusAdapter.clickListener{
            override fun onClick(status: Participant) {
                onClickButton?.invoke("detail",status)
            }
        }

    }




}