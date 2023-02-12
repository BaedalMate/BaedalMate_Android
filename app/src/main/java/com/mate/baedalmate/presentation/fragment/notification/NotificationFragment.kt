package com.mate.baedalmate.presentation.fragment.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentNotificationBinding
import com.mate.baedalmate.presentation.adapter.notification.NotificationAdapter
import com.mate.baedalmate.presentation.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var binding by autoCleared<FragmentNotificationBinding>()
    private val notificationViewModel by activityViewModels<NotificationViewModel>()
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
        setBackClickListener()
        getNotification()
        initListAdapter()
    }

    private fun setBackClickListener() {
        binding.btnNotificationActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getNotification() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                showLoadingView()
                notificationViewModel.getNotifications()
            }
        }
    }

    private fun observeNotifications() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                notificationViewModel.notifications.observe(viewLifecycleOwner) { notifications ->
                    hideLoadingView()
                    notificationAdapter.submitList(notifications.notifications.toMutableList())
                }
            }
        }
    }

    private fun initListAdapter() {
        notificationAdapter = NotificationAdapter(requestManager = glideRequestManager)
        with(binding.rvNotificationResultList) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapter
        }
        setListAdapterItemClickListener()
        observeNotifications()
    }

    private fun setListAdapterItemClickListener() {
        notificationAdapter.setOnItemClickListener(object : NotificationAdapter.OnItemClickListener {
            override fun notificationClick(roomId: Int, pos: Int) {
                findNavController().navigate(NotificationFragmentDirections.actionNotificationFragmentToChatFragment(
                    roomId = roomId
                ))
            }
        })
    }

    private fun showLoadingView() {
        with(binding) {
            rvNotificationResultList.visibility = View.INVISIBLE
            lottieNotificationLoading.visibility = View.VISIBLE
        }
    }

    private fun hideLoadingView() {
        with(binding) {
            rvNotificationResultList.visibility = View.VISIBLE
            lottieNotificationLoading.visibility = View.GONE
        }
    }
}