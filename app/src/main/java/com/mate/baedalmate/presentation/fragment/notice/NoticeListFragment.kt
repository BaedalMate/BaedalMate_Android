package com.mate.baedalmate.presentation.fragment.notice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentNoticeListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeListFragment : Fragment() {
    private var binding by autoCleared<FragmentNoticeListBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoticeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
    }

    private fun setBackClickListener() {
        binding.btnNoticeListActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }
}