package com.mate.baedalmate.presentation.fragment.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentMyProfileChangeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileChangeFragment : Fragment() {
    private var binding by autoCleared<FragmentMyProfileChangeBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileChangeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        setImageChangeClickListener()
        setChangeSubmitClickListener()
    }

    private fun setBackClickListener() {
        binding.btnMyProfileChangeActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setImageChangeClickListener() {
        binding.layoutMyProfileChangePhoto.setOnDebounceClickListener {
            // TODO 갤러리 연동 및 서버에 저장
        }
    }

    private fun setChangeSubmitClickListener() {
        binding.btnMyProfileChangeSubmit.setOnDebounceClickListener {
            // TODO 닉네임 서버에 저장 동작
        }
    }
}