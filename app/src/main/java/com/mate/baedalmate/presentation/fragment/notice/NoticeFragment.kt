package com.mate.baedalmate.presentation.fragment.notice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentNoticeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeFragment : Fragment() {
    private var binding by autoCleared<FragmentNoticeBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        initNoticeDetail()
    }

    private fun setBackClickListener() {
        binding.btnNoticeActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initNoticeDetail() {
        with(binding) {
            // TODO 서버와 통신해 공지사항 내용 initialize
            tvNoticeActionbarTitle.text = ""
            tvNoticeContentsUploadDate.text = ""
            tvNoticeContentsDetail.text = ""
        }
    }
}