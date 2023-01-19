package com.mate.baedalmate.presentation.fragment.login

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.LoadingAlertDialog
import com.mate.baedalmate.databinding.FragmentLoginBinding
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import com.mate.baedalmate.presentation.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var binding by autoCleared<FragmentLoginBinding>()
    private val loginViewModel by activityViewModels<MemberViewModel>()
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        checkAccountIsValid()
        initAlertDialog()
        loginSuccess()
        setKakaoLoginButtonClickListener()
        setPolicy()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun checkAccountIsValid() {
        val isValid = requireActivity().intent.getBooleanExtra("isValid", true)
        if (!isValid) {
            with(findNavController()) {
                if (currentDestination?.id == R.id.LoginFragment) {
                    currentDestination?.getAction(R.id.action_loginFragment_to_setAccountMyProfileFragment)
                        ?.let { navigate(R.id.action_loginFragment_to_setAccountMyProfileFragment) }
                }
            }
        }
    }

    private fun initAlertDialog() {
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        loadingAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        showLoadingDialog()
        if (error != null) {
            LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
            Log.e(ContentValues.TAG, "카카오 로그인 실패", error)
        } else if (token != null) {
            Log.i(ContentValues.TAG, "카카오 로그인 성공 ${token.accessToken}")
            loginViewModel.setKakaoAccessToken(token.accessToken)
            loginViewModel.requestLoginKakao(token.accessToken)
        }
    }

    private fun loginSuccess() {
        loginViewModel.loginSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess == true) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                requireActivity().finish()
            } else if (isSuccess == false) {
                Log.e(ContentValues.TAG, "로그인 실패")
            }
        })
    }

    private fun setKakaoLoginButtonClickListener() {
        binding.btnLoginKakao.setOnClickListener {
            showLoadingDialog()
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                UserApiClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
            }
        }
    }

    private fun setPolicy() {
        var span = SpannableString(getString(R.string.login_policy_guide_message))
        setPolicyMessage(span = span, type = "terms", text = getString(R.string.policy_terms))
        setPolicyMessage(span = span, type = "privacy", text = getString(R.string.policy_privacy))
        span.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_main_C4C4C4
                )
            ),
            0,
            span.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvLoginUseGuideMessage.movementMethod = LinkMovementMethod.getInstance()
        binding.tvLoginUseGuideMessage.text = span
    }

    private fun setPolicyMessage(span: SpannableString, type: String, text: String) {
        span.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    with(findNavController()) {
                        if (currentDestination?.id == R.id.LoginFragment) {
                            currentDestination?.getAction(R.id.action_loginFragment_to_policyFragment)
                                ?.let {
                                    navigate(
                                        LoginFragmentDirections.actionLoginFragmentToPolicyFragment(
                                            informationType = type
                                        )
                                    )
                                }
                        }
                    }
                }
            },
            span.indexOf(text), span.indexOf(text) + text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.setSpan(
            StyleSpan(Typeface.BOLD),
            span.indexOf(text),
            span.indexOf(text) + text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.setSpan(
            UnderlineSpan(),
            span.indexOf(text),
            span.indexOf(text) + text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun showLoadingDialog() {
        with(LoadingAlertDialog) {
            showLoadingDialog(loadingAlertDialog)
            resizeDialogFragment(requireContext(), loadingAlertDialog)
        }
    }
}