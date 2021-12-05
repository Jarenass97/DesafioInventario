package com.example.desafioinventario

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import model.Usuario

class InterfazUsuarioActivity : AppCompatActivity() {
    lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_usuario)
        supportActionBar?.hide()

        val bun: Bundle = intent.extras!!
        usuario = bun.getSerializable("user") as Usuario

    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.strTituloCerrarSesion))
            .setMessage(getString(R.string.strMensajeCerrarSesion))
            .setPositiveButton("OK") { view, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                view.dismiss()
            }
            .setNegativeButton("NO") { view, _ ->
                view.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }
}