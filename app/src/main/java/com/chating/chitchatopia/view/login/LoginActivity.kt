package com.chating.chitchatopia.view.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.R
import com.chating.chitchatopia.databinding.ActivityLoginBinding
import com.chating.chitchatopia.view.home.MainActivity
import com.chating.chitchatopia.view.signup.SignUpActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(),LoginView {


    var email:String?=null
    var password:String?=null
    lateinit var binding:ActivityLoginBinding
    lateinit var loginPresenter:LoginPresenter
    lateinit var user:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_login)

        doInitialization()

    }

    private fun doInitialization() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this,R.color.dark_blue)

        binding.loginBt.setOnClickListener {
            Toast.makeText(this,"clicked", Toast.LENGTH_LONG).show()
            binding.isLoading=true
            binding.loginBt.isEnabled = false
            email=binding.emailLoginETx.text.toString().trim()
            password=binding.passwordLoginETx.text.toString().trim()
            loginCheck(email!!, password!!)
        }
        binding.signUpOfLoginTx.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun loginCheck(email: String, password: String) {

        if (email.isEmpty()){
            binding.emailLoginETx.error = "Please Enter your E-mail"
            binding.emailLoginETx.requestFocus()
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(binding.emailLoginETx)
            hideLoading()
            return
        }

        else if (!isEmailValid(email))
        {
            binding.emailLoginETx.error = "Please Enter Valid E-mail"
            binding.emailLoginETx.requestFocus()
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(binding.emailLoginETx)
            hideLoading()
            return
        }

        else if(password.isEmpty()){
            binding.passwordLoginETx.error="Please Enter Your Password"
            binding.passwordLoginETx.requestFocus()
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(binding.passwordLoginETx)
            hideLoading()
            return
        }
        else{

            loginPresenter=LoginPresenter(this)
            loginPresenter.login(email,password)
        }

    }



    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$")
        return emailRegex.matches(email)
    }

    override fun showLoading() {
        binding.isLoading=true
    }

    override fun hideLoading() {
        binding.isLoading=false
    }

    override fun onErrorLogin(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun onSuccessLogin(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun startActivity(user: User) {
        intent= Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("currentUserDetails",user)
        startActivity(intent)
    }

    @SuppressLint("LogNotTimber")
    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser?.uid!=null){
            Log.d("there is a logging user",FirebaseAuth.getInstance().currentUser?.uid.toString())
            intent= Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

}