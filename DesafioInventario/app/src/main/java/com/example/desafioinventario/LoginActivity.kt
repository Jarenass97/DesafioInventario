package com.example.desafioinventario

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.ViewAnimator

class LoginActivity : AppCompatActivity() {
    private lateinit var login_reg: ViewAnimator
    var isLogin = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        login_reg = findViewById(R.id.vaLogReg)
        val txtRegistrar = findViewById<TextView>(R.id.txtRegistrar)
        txtRegistrar.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> view.setBackgroundResource(R.color.pulsacion)
                MotionEvent.ACTION_UP -> {
                    view.setBackgroundColor(Color.TRANSPARENT)
                    login_reg.showNext()
                    isLogin = false
                }
            }
            true
        })
    }

    fun iniciarSesion(view: View) {
        //Buscar username en base de datos y comprobar si la contraseña es correcta. en caso afirmativo acceder
        //al menú principal de la app
    }

    override fun onBackPressed() {
        if (isLogin) super.onBackPressed()
        else {
            login_reg.showPrevious()
            isLogin = true
        }
    }

    fun registrar(view: View) {
        login_reg.showPrevious()
        isLogin = true
    }
}