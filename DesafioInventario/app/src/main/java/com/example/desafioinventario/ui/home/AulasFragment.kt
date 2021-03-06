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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.InventarioApi
import api.ServiceBuilder
import assistant.Auxiliar.usuario
import assistant.Curso
import com.example.desafioinventario.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import model.Aula
import model.Encargado
import model.Usuario
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AulasFragment() : Fragment() {

    lateinit var rvAulas: RecyclerView
    lateinit var aulasAdapter: AulasAdapter
    lateinit var btnAddAula: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_aulas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvAulas = view.findViewById(R.id.rvAulas)
        rvAulas.setHasFixedSize(true)
        rvAulas.layoutManager = LinearLayoutManager(context)
        btnAddAula = view.findViewById(R.id.btnAddAula)
        btnAddAula.setOnClickListener(View.OnClickListener {
            compruebaEncargados()
        })
        cargarDatos()
        cargarAulas()
    }

    private fun cargarDatos() {
        if (usuario.isJefe()) {
            btnAddAula.isVisible = true
        }
    }

    private fun compruebaEncargados() {
        val request = ServiceBuilder.buildService(InventarioApi::class.java)
        val call = request.getEncargados()
        call.enqueue(object : Callback<MutableList<Encargado>> {
            override fun onResponse(
                call: Call<MutableList<Encargado>>,
                response: Response<MutableList<Encargado>>
            ) {
                if (response.code() == 200) {
                    val encargados = ArrayList<String>(0)
                    for (post in response.body()!!) {
                        encargados.add(post.nombre)
                    }
                    if (encargados.isNotEmpty()) dialogAula(encargados)
                    else Toast.makeText(
                        context,
                        "No existen usuarios encargados que asignar al aula",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        context,
                        response.message().toString(),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

            override fun onFailure(call: Call<MutableList<Encargado>>, t: Throwable) {
                Toast.makeText(
                    context,
                    getString(R.string.strFalloConexion),
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun cargarAulas() {
        val request = ServiceBuilder.buildService(InventarioApi::class.java)
        val call = if (!usuario.isJefe() && !usuario.isProfesor() && usuario.isEncargado())
            request.getAulasEncargado(usuario.username)
        else request.getAulas()
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
                } else if (response.code() == 400) {
                    Toast.makeText(
                        context,
                        "No existen aulas que mostrar",
                        Toast.LENGTH_LONG
                    )
                        .show()
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

    private fun dialogAula(encargados: ArrayList<String>) {
        val aulaView = layoutInflater.inflate(R.layout.aulas_creater, null)
        val nombre = aulaView.findViewById<EditText>(R.id.edNombreAula)
        val descripcion = aulaView.findViewById<EditText>(R.id.edDescripcionAula)
        val curso = aulaView.findViewById<Spinner>(R.id.spCursoAula)
        val spEncargados = aulaView.findViewById<Spinner>(R.id.spEncargadoAula)
        val alumnos = aulaView.findViewById<EditText>(R.id.edAlumnosAula)
        cargarCursos(curso)
        cargarEncargados(spEncargados, encargados)
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
                        spEncargados.selectedItem.toString(),
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

    private fun cargarEncargados(encargado: Spinner, encargados: ArrayList<String>) {
        encargado.adapter = ArrayAdapter(
            requireContext(),
            R.layout.encargados_list,
            R.id.txtEncargadoAulaItem,
            encargados
        )
    }

    private fun cargarCursos(curso: Spinner) {
        val cursos = Curso.values()
        curso.adapter = ArrayAdapter(requireContext(), R.layout.cursos_list, R.id.txtCurso, cursos)
    }
}