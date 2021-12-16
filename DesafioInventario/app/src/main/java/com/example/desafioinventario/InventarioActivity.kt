package com.example.desafioinventario

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import assistant.Auxiliar

class InventarioActivity : AppCompatActivity() {
    var usuario = Auxiliar.usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)
    }
}