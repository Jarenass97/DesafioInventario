package com.example.desafioinventario.ui.home

import adapters.DevicesAdapter
import adapters.DevicesAulaAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.InventarioApi
import api.ServiceBuilder
import com.example.desafioinventario.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import model.Aula
import model.Dispositivo
import okhttp3.ResponseBody
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DispositivosFragment : Fragment() {
    lateinit var rvDispositivos: RecyclerView
    lateinit var devicesAdapter: DevicesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dispositivos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAddDevice = view.findViewById<FloatingActionButton>(R.id.btnAddDispositivoGeneral)
        btnAddDevice.setOnClickListener(View.OnClickListener {
            addDispositivo()
        })
        rvDispositivos = view.findViewById(R.id.rvDispositivosGeneral)
        rvDispositivos.setHasFixedSize(true)
        rvDispositivos.layoutManager = LinearLayoutManager(context)
        cargarDispositivos()
    }

    fun addDispositivo() {
        val dialog = layoutInflater.inflate(R.layout.dispositivos_creater, null)
        val edIdentificador = dialog.findViewById<EditText>(R.id.edIdentificadorDispositivoCreater)
        val edNombre = dialog.findViewById<EditText>(R.id.edNombreDispositivoCreater)
        val spAulas = dialog.findViewById<Spinner>(R.id.spAulaDispositivosCreater)
        cargarAulas(spAulas)
        AlertDialog.Builder(requireContext())
            .setView(dialog)
            .setTitle(getString(R.string.strCrearDispositivo))
            .setPositiveButton("OK") { view, _ ->
                if (!camposVacios(edIdentificador, edNombre)) {
                    insertDevice(
                        Dispositivo(
                            edIdentificador.text.toString(),
                            edNombre.text.toString(),
                            spAulas.selectedItem.toString()
                        )
                    )
                }else Toast.makeText(context, getString(R.string.strCamposVacios), Toast.LENGTH_SHORT).show()
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.strCancelar)) { view, _ ->
                view.dismiss()
            }
            .setCancelable(false).create().show()
    }

    private fun cargarAulas(spAulas: Spinner) {
        val request = ServiceBuilder.buildService(InventarioApi::class.java)
        val call = request.getAulas()
        call.enqueue(object : Callback<MutableList<Aula>> {
            override fun onResponse(
                call: Call<MutableList<Aula>>,
                response: Response<MutableList<Aula>>
            ) {
                if (response.code() == 200) {
                    var aulas = ArrayList<String>(0)
                    for (post in response.body()!!) {
                        aulas.add(post.nombre)
                    }
                    spAulas.adapter = ArrayAdapter(
                        context!!,
                        R.layout.cursos_list,
                        R.id.txtCurso,
                        aulas
                    )
                } else {
                    Toast.makeText(
                        context,
                        response.message().toString(),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

            override fun onFailure(call: Call<MutableList<Aula>>, t: Throwable) {
                Toast.makeText(
                    context,
                    getString(R.string.strFalloConexion),
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun camposVacios(edIdentificador: EditText, edNombre: EditText): Boolean {
        return edIdentificador.text.isEmpty() || edNombre.text.isEmpty()
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
                        context,
                        getString(R.string.strOperacionExitosa),
                        Toast.LENGTH_SHORT
                    ).show()
                    cargarDispositivos()
                } else Toast.makeText(
                    context,
                    response.message(),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    context,
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
                    val listaDispositivos = dispositivos.sortedBy { it.aula }
                    newDevicesAdapter(
                        DevicesAdapter(
                            context as AppCompatActivity,
                            listaDispositivos
                        )
                    )
                } else {
                    Toast.makeText(
                        context,
                        "No existen dispositivos que mostrar",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

            override fun onFailure(call: Call<MutableList<Dispositivo>>, t: Throwable) {
                Toast.makeText(
                    context,
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
}