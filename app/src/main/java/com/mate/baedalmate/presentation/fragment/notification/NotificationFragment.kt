package com.mate.baedalmate.presentation.fragment.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.databinding.FragmentNotificationBinding
import com.mate.baedalmate.presentation.adapter.notification.NotificationAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var binding by autoCleared<FragmentNotificationBinding>()
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var glideRequestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getNotification()
        initListAdapter()
    }

    private fun getNotification() {
        // TODO
    }

    private fun initListAdapter() {
        notificationAdapter = NotificationAdapter(requestManager = glideRequestManager)
        binding.rvNotificationResultList.layoutManager = LinearLayoutManager(requireContext())
        with(binding) {
            rvNotificationResultList.adapter = notificationAdapter
        }
    }
}