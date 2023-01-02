package com.mate.baedalmate.presentation.fragment.block

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentBlockUserListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlockUserListFragment : Fragment() {
    private var binding by autoCleared<FragmentBlockUserListBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBlockUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
    }

    private fun setBackClickListener() {
        binding.btnBlockUserListActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }
}