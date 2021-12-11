package com.example.desafioinventario.ui.home

import adapters.AulasAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.InventarioApi
import api.ServiceBuilder
import assistant.Curso
import com.example.desafioinventario.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import model.Aula
import model.Usuario
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment() : Fragment() {

    lateinit var rvAulas: RecyclerView
    lateinit var aulasAdapter: AulasAdapter
    lateinit var btnAddAula: FloatingActionButton
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvAulas = view.findViewById(R.id.rvAulas)
        rvAulas.setHasFixedSize(true)
        rvAulas.layoutManager = LinearLayoutManager(context)
        btnAddAula = view.findViewById(R.id.btnAddAula)
        btnAddAula.setOnClickListener(View.OnClickListener {
            dialogAula()
        })
        cargarAulas()
    }


    private fun cargarAulas() {
        val request = ServiceBuilder.buildService(InventarioApi::class.java)
        val call = request.getAulas()
        call.enqueue(object : Callback<MutableList<Aula>> {
            override fun onResponse(
                call: Call<MutableList<Aula>>,
                response: Response<MutableList<Aula>>
            ) {
                if (response.code() == 200) {
                    val aulas = ArrayList<Aula>(0)
                    for (aula in response.body()!!) {
                        aulas.add(aula)
                    }
                    newAulasAdapter(AulasAdapter(context as AppCompatActivity, aulas))
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

    private fun newAulasAdapter(adaptador: AulasAdapter) {
        aulasAdapter = adaptador
        rvAulas.adapter = aulasAdapter
    }

    private fun dialogAula() {
        val aulaView = layoutInflater.inflate(R.layout.aulas_creater, null)
        val nombre = aulaView.findViewById<EditText>(R.id.edNombreAula)
        val descripcion = aulaView.findViewById<EditText>(R.id.edDescripcionAula)
        val curso = aulaView.findViewById<Spinner>(R.id.spCursoAula)
        val encargado = aulaView.findViewById<Spinner>(R.id.spEncargadoAula)
        val alumnos = aulaView.findViewById<EditText>(R.id.edAlumnosAula)
        cargarCursos(curso)
        cargarEncargados(encargado)
        Thread.sleep(500)
        AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.ic_class)
            .setTitle(getString(R.string.strTituloAddAula))
            .setView(aulaView)
            .setPositiveButton("OK") { view, _ ->
                if (!camposVacios(nombre, descripcion, alumnos)) {
                    val aula = Aula(
                        nombre.text.toString(),
                        descripcion.text.toString(),
                        Curso.valueOf(curso.selectedItem.toString()),
                        encargado.selectedItem.toString(),
                        alumnos.text.toString().toInt()
                    )
                    crearAula(aula)
                } else {
                    Toast.makeText(context, getString(R.string.strCamposVacios), Toast.LENGTH_SHORT)
                        .show()
                }
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.strCancelar)) { view, _ ->
                view.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun camposVacios(nombre: EditText, descripcion: EditText, alumnos: EditText): Boolean {
        return nombre.text.isEmpty() || descripcion.text.isEmpty() || alumnos.text.isEmpty()
    }

    private fun crearAula(aula: Aula) {
        val request = ServiceBuilder.buildService(InventarioApi::class.java)
        val call = request.addAula(aula)
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
                    cargarAulas()
                } else Toast.makeText(
                    context,
                    "el aula ya existe",
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

    private fun cargarEncargados(encargado: Spinner) {
        val request = ServiceBuilder.buildService(InventarioApi::class.java)
        val call = request.getUsuarios()
        call.enqueue(object : Callback<MutableList<Usuario>> {
            override fun onResponse(
                call: Call<MutableList<Usuario>>,
                response: Response<MutableList<Usuario>>
            ) {
                if (response.code() == 200) {
                    var usuarios = ArrayList<String>(0)
                    for (post in response.body()!!) {
                        usuarios.add(post.username.toString())
                    }
                    encargado.adapter = ArrayAdapter(
                        context!!,
                        R.layout.encargados_list,
                        R.id.txtEncargadoAulaItem,
                        usuarios
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

            override fun onFailure(call: Call<MutableList<Usuario>>, t: Throwable) {
                Toast.makeText(
                    context,
                    getString(R.string.strFalloConexion),
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun cargarCursos(curso: Spinner) {
        val cursos = Curso.values()
        curso.adapter = ArrayAdapter(requireContext(), R.layout.cursos_list, R.id.txtCurso, cursos)
    }
}