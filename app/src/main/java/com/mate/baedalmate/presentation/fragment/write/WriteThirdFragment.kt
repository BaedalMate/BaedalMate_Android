package com.mate.baedalmate.presentation.fragment.write

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dp
import com.mate.baedalmate.data.datasource.remote.recruit.TagDto
import com.mate.baedalmate.databinding.FragmentWriteThirdBinding
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WriteThirdFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteThirdBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        setNextClickListener()
        initSubjectInput()
        initDescriptionInput()
        initTagList()
    }

    private fun setBackClickListener() {
        binding.btnWriteThirdActionbarBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnWriteThirdNext.setOnClickListener {
            writeViewModel.postTitle = binding.etWriteThirdSubjectInput.text.toString().trim()
            writeViewModel.postDetail = binding.etWriteThirdDescriptionInput.text.toString().trim()
            val tagList = mutableListOf<TagDto>()
            for(tagText in binding.chipgroupWriteThirdTagListSaved.getAllChipsTagText()) {
                tagList.add(TagDto(tagname = tagText))
            }
            writeViewModel.postTagList = tagList
            findNavController().navigate(R.id.action_writeThirdFragment_to_writeFourthFragment)
        }
    }

    private fun initSubjectInput() {
        binding.etWriteThirdSubjectInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().isNullOrBlank()) { // 제목이 빈 경우
                    binding.tvWriteThirdSubjectError.visibility = View.VISIBLE
                    binding.etWriteThirdSubjectInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10_stroke_red)
                    deactivationButtonNext()
                } else {
                    binding.tvWriteThirdSubjectError.visibility = View.INVISIBLE
                    binding.etWriteThirdSubjectInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10)

                    if(binding.etWriteThirdDescriptionInput.text.toString().trim().isNullOrBlank() ||
                            s.toString().length > 20 || binding.etWriteThirdDescriptionInput.text.length > 200) {
                        // 내용이 비었거나, 제목 또는 내용이 제한 글자수를 넘은 경우
                        deactivationButtonNext()
                    } else {
                        activationButtonNext()
                    }
                }
                binding.tvWriteThirdSubjectTextCountCurrent.text = binding.etWriteThirdSubjectInput.text.length.toString()
            }
        })
    }

    private fun initDescriptionInput() {
        binding.etWriteThirdDescriptionInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().isNullOrBlank()) { // 제목이 빈 경우
                    binding.tvWriteThirdDescriptionError.visibility = View.VISIBLE
                    binding.etWriteThirdDescriptionInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10_stroke_red)
                    deactivationButtonNext()
                } else {
                    binding.tvWriteThirdDescriptionError.visibility = View.INVISIBLE
                    binding.etWriteThirdDescriptionInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10)

                    if(binding.etWriteThirdSubjectInput.text.toString().trim().isNullOrBlank() ||
                        s.toString().length > 200 || binding.etWriteThirdSubjectInput.text.length > 20) {
                        // 제목이 비었거나, 제목 또는 내용이 제한 글자수를 넘은 경우
                        deactivationButtonNext()
                    } else {
                        activationButtonNext()
                    }
                }
                binding.tvWriteThirdDescriptionTextCountCurrent.text = binding.etWriteThirdDescriptionInput.text.length.toString()
            }
        })
    }

    private fun initTagList() {
        setEditTextChangeListener()
        setAddTagClickListener()
        setEditTextAddTagKeyClickListener()
    }

    private fun setEditTextChangeListener() {
        binding.etWriteThirdTagInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().trim().isNullOrBlank()) {
                    binding.btnWriteThirdTagAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_main_C4C4C4))
                    binding.btnWriteThirdTagAdd.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10_stroke_gray_line)
                    binding.btnWriteThirdTagAdd.backgroundTintList = null
                } else {
                    binding.btnWriteThirdTagAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_FFFFFF))
                    binding.btnWriteThirdTagAdd.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_white_radius_10)
                    binding.btnWriteThirdTagAdd.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.line_orange_FFA077)
                }
            }
        })
    }

    private fun setAddTagClickListener() {
        binding.btnWriteThirdTagAdd.setOnClickListener {
            setAddTag()
        }
    }

    private fun setEditTextAddTagKeyClickListener() {
        binding.etWriteThirdTagInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    setAddTag()
                    true
                }
                else -> false
            }
        }
    }

    private fun setTagCountLimit() {
        with(binding) {
            val currentTagCount = chipgroupWriteThirdTagListSaved.size
            if (currentTagCount >= 8) {
                Toast.makeText(
                    requireContext(),
                    "태그는 8개까지 등록 가능합니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setAddTag() {
        val originalTag = binding.etWriteThirdTagInput.text.toString().trim()
//                    val removeBlankTag = ReplaceUnicode.replaceBlankText(originalTag)

        if (originalTag.isEmpty()
            || binding.chipgroupWriteThirdTagListSaved.getAllChipsTagText()
                .contains(originalTag)
        ) {
            binding.etWriteThirdTagInput.setText("")
        } else {
            val chip = LayoutInflater.from(context)
                .inflate(R.layout.item_write_tag_chip, null) as Chip
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
            )
            with(chip) {
                setTextAppearance(R.style.style_body2_kor)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.main_FB5F1C))
                setOnCloseIconClickListener {
                    binding.chipgroupWriteThirdTagListSaved.removeView(chip as View)
                    setTagCountLimit()
                }
                ensureAccessibleTouchTarget(42.dp)
                text = "#$originalTag"
            }
            if (binding.chipgroupWriteThirdTagListSaved.size < 8) {
                binding.chipgroupWriteThirdTagListSaved.addView(chip, layoutParams)
            }
            binding.etWriteThirdTagInput.setText("")
        }
        setTagCountLimit()
    }

    private fun ChipGroup.getAllChipsTagText(): List<String> {
        return (0 until childCount).mapNotNull { index ->
            val currentChip = getChildAt(index) as? Chip
            currentChip?.text.toString().split("#")[1]
        }
    }

    private fun ChipGroup.clearChips() {
        val chipViews = (0 until childCount).mapNotNull { index ->
            val view = getChildAt(index)
            if (view is Chip) view else null
        }
        chipViews.forEach { removeView(it) }
    }

    private fun activationButtonNext() {
        binding.btnWriteThirdNext.isEnabled = true
    }

    private fun deactivationButtonNext() {
        binding.btnWriteThirdNext.isEnabled = false
    }
}