package com.mate.baedalmate.presentation.fragment.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.imageview.ShapeableImageView
import com.mate.baedalmate.R
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.databinding.FragmentWriteCategoryBinding
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WriteCategoryFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteCategoryBinding>()
    private val args by navArgs<WriteCategoryFragmentArgs>()
    private val writeViewModel by activityViewModels<WriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        initCategory()
        setCategoryClickListener()
    }

    private fun setBackClickListener() {
        binding.btnWriteCategoryActionbarBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initCategory() {
        val deviceWidth = GetDeviceSize.getDeviceWidthSize(requireContext())
        val categorySize = (deviceWidth * 0.33).toInt()
        val marginSize = (categorySize / 3)
        binding.imgWriteCategoryKorean.updateLayoutParams<ConstraintLayout.LayoutParams> {
            marginEnd = marginSize
        }
        for (i in 0 until binding.layoutWriteCategoryContents.childCount) {
            if (binding.layoutWriteCategoryContents.getChildAt(i) is ShapeableImageView) {
                val shapeableImageView =
                    binding.layoutWriteCategoryContents.getChildAt(i) as ShapeableImageView
                shapeableImageView.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    width = categorySize
                    height = categorySize
                }
            }
        }
    }

    private fun setCategoryClickListener() {
        for (i in 0 until binding.layoutWriteCategoryContents.childCount) {
            val childView = binding.layoutWriteCategoryContents.getChildAt(i)
            if (childView is ShapeableImageView) {
                childView.setOnClickListener {
                    writeViewModel.categoryId = (i / 3) + 1
                    findNavController().navigate(
                        WriteCategoryFragmentDirections.actionWriteCategoryFragmentToWriteFirstFragment(
                            recruitDetailForModify = args.recruitDetailForModify
                        )
                    )
                }
            }
        }
    }
}