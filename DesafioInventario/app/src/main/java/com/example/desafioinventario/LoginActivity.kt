package com.example.desafioinventario

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import assistant.Animacion
import assistant.Rol
import model.Usuario

class LoginActivity : AppCompatActivity() {
    private lateinit var login_reg: ViewAnimator
    private lateinit var txtMensajeLogin: TextView
    lateinit var edUsernameLogin: EditText
    lateinit var edPasswdLogin: EditText
    lateinit var edEmailReg: EditText
    lateinit var edUsernameReg: EditText
    lateinit var edPass1Reg: EditText
    lateinit var edPass2Reg: EditText
    lateinit var txtMensajeReg: TextView
    lateinit var animaciones: Animacion
    lateinit var imgUser: ImageView
    var isLogin = true
    var photo: Bitmap? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        animaciones = Animacion(this)

        login_reg = findViewById(R.id.vaLogReg)
        txtMensajeLogin = findViewById(R.id.txtMensajeLogin)
        edUsernameLogin = findViewById(R.id.edNombreUsuarioLogin)
        edPasswdLogin = findViewById(R.id.edPasswdLogin)
        imgUser = findViewById(R.id.imgUsuario)
        edEmailReg = findViewById(R.id.edCorreoRegistro)
        edUsernameReg = findViewById(R.id.edUsuarioReg)
        edPass1Reg = findViewById(R.id.edPasswdReg)
        edPass2Reg = findViewById(R.id.edPasswd2Reg)
        txtMensajeReg = findViewById(R.id.txtMensajeErrorReg)
        ocultarMsgs()

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
        if (camposLoginVacios()) mostrarError(txtMensajeLogin, getString(R.string.strCamposVacios))
        //Buscar username en base de datos y comprobar si la contraseña es correcta. en caso afirmativo acceder
        //al menú principal de la app
    }

    private fun camposLoginVacios(): Boolean {
        return edUsernameLogin.text.isEmpty() || edPasswdLogin.text.isEmpty()
    }

    private fun mostrarError(txt: TextView, error: String) {
        txt.text = error
        txt.isVisible = true
        txt.startAnimation(animaciones.aparicion_difuminada)
    }

    override fun onBackPressed() {
        if (isLogin) super.onBackPressed()
        else {
            reiniciar()
        }
    }

    private fun ocultarMsgs() {
        txtMensajeLogin.isVisible = false
        txtMensajeReg.isVisible = false
    }

    fun registrar(view: View) {
        if (camposRegVacios()) mostrarError(txtMensajeReg, getString(R.string.strCamposVacios))
        else if (!passCoinciden()) mostrarError(
            txtMensajeReg,
            getString(R.string.strPassNoCoinciden)
        )
        else {
            val rol = getRol()
            val user = Usuario(
                edUsernameReg.text.toString(),
                edPass1Reg.text.toString(),
                edEmailReg.text.toString(),
                rol,
                photo
            )
            //registrar user
            reiniciar()
        }
    }

    private fun getRol(): Rol {
        var rol = Rol.PROFESOR
        val groupRol: RadioGroup = findViewById(R.id.rgRoles)
        val radioRol: RadioButton = findViewById(groupRol.checkedRadioButtonId)
        when (radioRol.text) {
            getString(R.string.strJefe) -> rol = Rol.JEFE_DEPARTAMENTO
            getString(R.string.strEncargado) -> rol = Rol.ENCARGADO
            getString(R.string.strProfesor) -> rol = Rol.PROFESOR
        }
        return rol
    }

    fun reiniciar() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun passCoinciden(): Boolean {
        return edPass1Reg.text.toString() == edPass2Reg.text.toString()
    }

    private fun camposRegVacios(): Boolean {
        return edEmailReg.text.isEmpty() || edUsernameReg.text.isEmpty() || edPass1Reg.text.isEmpty() || edPass2Reg.text.isEmpty()
    }
}