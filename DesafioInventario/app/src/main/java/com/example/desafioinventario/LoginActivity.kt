package com.example.desafioinventario

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import api.InventarioApi
import api.ServiceBuilder
import assistant.Animacion
import assistant.Auxiliar
import assistant.Rol
import model.Usuario
import okhttp3.ResponseBody
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import java.io.FileNotFoundException
import java.io.InputStream


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
    val contexto = this

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

    fun cambiarFoto(view: View) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.strElegirFoto))
            .setMessage(getString(R.string.strMensajeElegirFoto))
            .setPositiveButton(getString(R.string.strCamara)) { view, _ ->
                hacerFoto()
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.strGaleria)) { view, _ ->
                elegirDeGaleria()
                view.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }

    private fun elegirDeGaleria() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Seleccione una imagen"),
            Auxiliar.CODE_GALLERY
        )
    }

    private fun hacerFoto() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                Auxiliar.CODE_CAMERA
            )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, Auxiliar.CODE_CAMERA)
    }

    fun iniciarSesion(view: View) {
        if (camposLoginVacios())
            mostrarTextError(
                txtMensajeLogin,
                getString(R.string.strCamposVacios)
            )
        else {
            val request = ServiceBuilder.buildService(InventarioApi::class.java)
            val call = request.getUsuario(edUsernameLogin.text.toString())
            call.enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.code() == 200) {
                        val post = response.body()!!
                        val usuario =
                            Usuario(
                                post.username,
                                post.passwd,
                                post.roles,
                                post.email,
                                post.img
                            )
                        if (usuario.passwd == edPasswdLogin.text.toString()) {
                            contexto.abrirInterfaz(usuario)
                            Toast.makeText(
                                contexto,
                                getString(R.string.strLoginCorrecto),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            mostrarTextError(
                                txtMensajeLogin,
                                getString(R.string.strCredencialesIncorrectas)
                            )
                        }
                    }
                    if (response.code() == 400) {
                        mostrarTextError(
                            txtMensajeLogin,
                            getString(R.string.strCredencialesIncorrectas)
                        )
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    Toast.makeText(
                        contexto,
                        getString(R.string.strFalloConexion),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        }
    }

    private fun abrirInterfaz(usuario: Usuario) {
        val intent = Intent(this, InterfazUsuarioActivity::class.java)
        intent.putExtra("user", usuario)
        startActivity(intent)
        finish()
    }

    private fun camposLoginVacios(): Boolean {
        return edUsernameLogin.text.isEmpty() || edPasswdLogin.text.isEmpty()
    }

    private fun mostrarTextError(txt: TextView, error: String) {
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
        if (camposRegVacios()) mostrarTextError(txtMensajeReg, getString(R.string.strCamposVacios))
        else if (!passCoinciden()) mostrarTextError(
            txtMensajeReg,
            getString(R.string.strPassNoCoinciden)
        )
        else {
            val roles = ArrayList<Rol>(0)
            roles.add(getRol())
            val user = Usuario(
                edUsernameReg.text.toString(),
                edPass1Reg.text.toString(),
                roles,
                edEmailReg.text.toString()
            )
            if (photo != null) {
                user.img = Auxiliar.getBytes(photo!!)
            }
            Log.e("jorge", user.toString())
            val request = ServiceBuilder.buildService(InventarioApi::class.java)
            val call = request.addUser(user)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == 200) {
                        Toast.makeText(
                            contexto,
                            getString(R.string.strOperacionExitosa),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else Toast.makeText(contexto, "el usuario ya existe", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        contexto,
                        getString(R.string.strFalloConexion),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Auxiliar.CODE_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    photo = data?.extras?.get("data") as Bitmap
                    imgUser.setImageBitmap(photo)
                }
            }
            Auxiliar.CODE_GALLERY -> {
                if (resultCode === Activity.RESULT_OK) {
                    val selectedImage = data?.data
                    val selectedPath: String? = selectedImage?.path
                    if (selectedPath != null) {
                        var imageStream: InputStream? = null
                        try {
                            imageStream = selectedImage.let {
                                contentResolver.openInputStream(
                                    it
                                )
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                        val bmp = BitmapFactory.decodeStream(imageStream)
                        photo = Bitmap.createScaledBitmap(bmp, 200, 300, true)
                        imgUser.setImageBitmap(photo)
                    }
                }
            }
        }
    }
}