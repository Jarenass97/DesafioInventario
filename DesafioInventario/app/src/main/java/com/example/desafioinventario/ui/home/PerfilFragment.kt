package com.example.desafioinventario.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import api.InventarioApi
import api.ServiceBuilder
import assistant.Auxiliar
import com.example.desafioinventario.R
import com.google.android.material.navigation.NavigationView
import model.Usuario
import okhttp3.ResponseBody
import org.jetbrains.anko.internals.AnkoInternals.createAnkoContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileNotFoundException
import java.io.InputStream

class PerfilFragment : Fragment() {

    lateinit var btnGuardar: ImageButton
    lateinit var usuario: Usuario
    lateinit var img: ImageView
    lateinit var edUsername: EditText
    lateinit var email: EditText
    var photo: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bun = requireActivity().intent.extras!!
        usuario = bun.getSerializable("user") as Usuario
        img = view.findViewById<ImageView>(R.id.imgUsuarioPerfil)
        edUsername = view.findViewById<EditText>(R.id.edUsernamePerfil)
        email = view.findViewById<EditText>(R.id.edEmailPerfil)
        btnGuardar = view.findViewById(R.id.btnGuardarPerfil)
        img.setOnClickListener(View.OnClickListener {
            cambiarFoto()
        })
        btnGuardar.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> btnGuardar.setBackgroundResource(R.color.pulsacion)
                MotionEvent.ACTION_UP -> {
                    btnGuardar.setBackgroundColor(Color.TRANSPARENT)
                    guardar()
                }
            }
            true
        })
        val btnCambiarPass = view.findViewById<Button>(R.id.btnCambiarPassword)
        btnCambiarPass.setOnClickListener(View.OnClickListener {
            cambiarContraseña()
        })
        cargarUsuario()
    }

    fun cambiarContraseña() {
        val dialog = layoutInflater.inflate(R.layout.password_changer, null)
        val pass1 = dialog.findViewById<EditText>(R.id.edPassChanger)
        val pass2 = dialog.findViewById<EditText>(R.id.edPass2Changer)
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.strCambiarPassword))
            .setView(dialog)
            .setPositiveButton("OK") { view, _ ->
                val p1 = pass1.text.toString()
                val p2 = pass2.text.toString()
                if (p1 == p2) {
                    usuario.passwd = p1
                    Toast.makeText(
                        context,
                        getString(R.string.strOperacionExitosa),
                        Toast.LENGTH_SHORT
                    ).show()
                } else Toast.makeText(
                    context,
                    getString(R.string.strPassNoCoinciden),
                    Toast.LENGTH_SHORT
                ).show()
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.strCancelar)) { view, _ ->
                view.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun cargarUsuario() {
        if (!usuario.sinImagen()) img.setImageBitmap(Auxiliar.getImage(usuario.img!!))
        edUsername.apply {
            text.clear()
            append(usuario.username)
        }
        email.apply {
            text.clear()
            append(usuario.email)
        }
    }

    private fun guardar() {
        val username = usuario.username
        usuario.username = edUsername.text.toString()
        usuario.email = email.text.toString()
        if (photo != null) usuario.img = Auxiliar.getBytes(photo!!)
        val request = ServiceBuilder.buildService(InventarioApi::class.java)
        val call = request.modUsuario(username, usuario)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == 200) {
                    Toast.makeText(
                        context,
                        getString(R.string.strOperacionExitosa),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    if (photo != null) {
                        val navigationView: NavigationView =
                            (context as AppCompatActivity).findViewById(R.id.nav_view)
                        val header: View = navigationView.getHeaderView(0)
                        val img = header.findViewById<ImageView>(R.id.imgUsuarioNavigation)
                        img.setImageBitmap(photo)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, getString(R.string.strFalloConexion), Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    fun cambiarFoto() {
        AlertDialog.Builder(requireContext())
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
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        )
            ActivityCompat.requestPermissions(
                context as AppCompatActivity,
                arrayOf(Manifest.permission.CAMERA),
                Auxiliar.CODE_CAMERA
            )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, Auxiliar.CODE_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Auxiliar.CODE_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    photo = data?.extras?.get("data") as Bitmap
                    img.setImageBitmap(photo)
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
                                requireContext().contentResolver.openInputStream(
                                    it
                                )
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                        val bmp = BitmapFactory.decodeStream(imageStream)
                        photo = Bitmap.createScaledBitmap(bmp, 200, 300, true)
                        img.setImageBitmap(photo)
                    }
                }
            }
        }
    }

}