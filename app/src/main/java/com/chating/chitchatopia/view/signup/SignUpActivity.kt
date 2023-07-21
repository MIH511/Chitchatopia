package com.chating.chitchatopia.view.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.R
import com.chating.chitchatopia.databinding.ActivitySignUpBinding
import com.chating.chitchatopia.utilities.Constants
import com.chating.chitchatopia.view.profile_setting.ProfileSettingActivity


class SignUpActivity : AppCompatActivity(), SignUpView {
    var email:String?=null
    var password:String?=null
    var name:String?=null
    var signUpPresenter=SignUpPresenter(this)
    var newUser: User?=null
    lateinit var bindingSignUpBinding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSignUpBinding=DataBindingUtil.setContentView(this,R.layout.activity_sign_up)

        doInitialization()
    }

    private fun doInitialization() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this,R.color.dark_blue)


        bindingSignUpBinding.signUpBt.setOnClickListener {
            showLoading()

            email=bindingSignUpBinding.emailSignUpETx.text.toString().trim()
            password=bindingSignUpBinding.passwordETx.text.toString().trim()
            name= bindingSignUpBinding.nameSignUpETx.text.toString().trim()
            loginCheck(email!!, password!!,name!!)
        }

        bindingSignUpBinding.backImageViewSignUp.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loginCheck(email: String, password: String, name: String) {
        if (email.isEmpty()){
            bindingSignUpBinding.emailSignUpETx.error = "Please Enter your E-mail"
            bindingSignUpBinding.emailSignUpETx.requestFocus()
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(bindingSignUpBinding.emailSignUpETx)
            hideLoading()
            return
        }

        else if (!isEmailValid(email))
        {
            bindingSignUpBinding.emailSignUpETx.error = "Please Enter Valid E-mail"
            bindingSignUpBinding.emailSignUpETx.requestFocus()
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(bindingSignUpBinding.emailSignUpETx)
            hideLoading()
            return
        }

        else if(name.isEmpty()){
            bindingSignUpBinding.nameSignUpETx.error="Please enter your name"
            bindingSignUpBinding.nameSignUpETx.requestFocus()
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(bindingSignUpBinding.nameSignUpETx)
            hideLoading()
            return
        }

        else if(password.isEmpty()){
            bindingSignUpBinding.passwordETx.error="Please Enter Your Password"
            bindingSignUpBinding.passwordETx.requestFocus()
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(bindingSignUpBinding.passwordETx)
            hideLoading()
            return
        }

        else if (password.length<8){
            bindingSignUpBinding.passwordETx.error="Password should be more than 7 characters"
            bindingSignUpBinding.passwordETx.requestFocus()
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(bindingSignUpBinding.passwordETx)
            hideLoading()
            return
        }
        else if (!getUnusedPatterns(password)){
            return
        }
        else{
            newUser=User(email,password, name,"","","")
            signUpPresenter.signUpProcess(newUser!!)
        }
    }


    private fun getUnusedPatterns(password: String):Boolean{
        var isValid=true
        if (!password.matches(Regex(".*[a-z].*"))) {
            bindingSignUpBinding.isSmallLetterError=true
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(bindingSignUpBinding.passwordETx)
            isValid=false
        }
        if (!password.matches(Regex(".*[A-Z].*"))) {
            bindingSignUpBinding.isCapitalLetterError=true
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(bindingSignUpBinding.passwordETx)
            isValid=false
        }
        if (!password.matches(Regex(".*\\d.*"))) {
            bindingSignUpBinding.isDigitError=true
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(bindingSignUpBinding.passwordETx)
            isValid=false
        }
        if (!password.matches(Regex(".*[@\$!%*?&].*"))) {
            bindingSignUpBinding.isSpecialCharError=true
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(bindingSignUpBinding.passwordETx)
            isValid=false
        }

        return isValid

    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")
        return emailRegex.matches(email)
    }

    override fun showLoading() {
        bindingSignUpBinding.isLoading=true
        bindingSignUpBinding.isSmallLetterError=false
        bindingSignUpBinding.isCapitalLetterError=false
        bindingSignUpBinding.isDigitError=false
        bindingSignUpBinding.isSpecialCharError=false
    }

    override fun hideLoading() {
        bindingSignUpBinding.isLoading=false
    }

    override fun onErrorSignup(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()

    }

    override fun onSuccessSignup(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun startActivity(currentUserUid: String, newUser: User) {
        intent= Intent(this, ProfileSettingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.KEY_CURRENT_USER_DETAILS, newUser)
        startActivity(intent)
    }
}

