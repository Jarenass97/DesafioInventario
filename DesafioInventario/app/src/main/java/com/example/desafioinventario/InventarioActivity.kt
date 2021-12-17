package com.example.desafioinventario

import adapters.DevicesAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.InventarioApi
import api.ServiceBuilder
import model.Aula
import model.Dispositivo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InventarioActivity : AppCompatActivity() {
    lateinit var rvDispositivos: RecyclerView
    lateinit var aula: Aula
    lateinit var devicesAdapter: DevicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)
        val bun = intent.extras!!
        aula = bun.getSerializable("aula") as Aula
        rvDispositivos = findViewById(R.id.rvDispositivos)
        rvDispositivos.setHasFixedSize(true)
        rvDispositivos.layoutManager = LinearLayoutManager(this)
        cargarDispositivos()
    }

    fun addDispositivo(view: View) {
        val dialog = layoutInflater.inflate(R.layout.dispositivos_creater, null)
        val edIdentificador = dialog.findViewById<EditText>(R.id.edIdentificadorDispositivoCreater)
        val edNombre = dialog.findViewById<EditText>(R.id.edNombreDispositivoCreater)
        AlertDialog.Builder(this)
            .setView(dialog)
            .setTitle(getString(R.string.strCrearDispositivo))
            .setPositiveButton("OK") { view, _ ->
                if (!camposVacios(edIdentificador, edNombre)) {
                    insertDevice(
                        Dispositivo(
                            edIdentificador.text.toString(),
                            edNombre.text.toString(),
                            aula.nombre
                        )
                    )
                }
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.strCancelar)) { view, _ ->
                view.dismiss()
            }
            .setCancelable(false).create().show()
    }

    private fun insertDevice(dispositivo: Dispositivo) {
        val req = ServiceBuilder.buildService(InventarioApi::class.java)
        val call = req.addDevice(dispositivo)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == 200) {
                    Toast.makeText(
                        this@InventarioActivity,
                        getString(R.string.strOperacionExitosa),
                        Toast.LENGTH_SHORT
                    ).show()
                    cargarDispositivos()
                } else Toast.makeText(
                    this@InventarioActivity,
                    response.message(),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    this@InventarioActivity,
                    getString(R.string.strFalloConexion),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun cargarDispositivos() {
        val request = ServiceBuilder.buildService(InventarioApi::class.java)
        val call = request.getDispositivos()
        call.enqueue(object : Callback<MutableList<Dispositivo>> {
            override fun onResponse(
                call: Call<MutableList<Dispositivo>>,
                response: Response<MutableList<Dispositivo>>
            ) {
                if (response.code() == 200) {
                    var dispositivos = ArrayList<Dispositivo>(0)
                    for (device in response.body()!!) {
                        dispositivos.add(device)
                    }
                    newDevicesAdapter(DevicesAdapter(this@InventarioActivity, dispositivos))
                } else {
                    Toast.makeText(
                        this@InventarioActivity,
                        response.message(),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

            override fun onFailure(call: Call<MutableList<Dispositivo>>, t: Throwable) {
                Toast.makeText(
                    this@InventarioActivity,
                    getString(R.string.strFalloConexion),
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun newDevicesAdapter(adaptador: DevicesAdapter) {
        devicesAdapter = adaptador
        rvDispositivos.adapter = devicesAdapter
    }

    private fun camposVacios(edIdentificador: EditText, edNombre: EditText): Boolean {
        return edIdentificador.text.isEmpty() || edNombre.text.isEmpty()
    }
}