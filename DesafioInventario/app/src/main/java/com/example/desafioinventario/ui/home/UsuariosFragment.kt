package com.example.desafioinventario.ui.home

import adapters.UsuariosAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.InventarioApi
import api.ServiceBuilder
import assistant.Auxiliar
import assistant.Auxiliar.usuario
import assistant.Rol
import com.example.desafioinventario.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import model.Usuario
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsuariosFragment : Fragment() {
    lateinit var rvUsuarios: RecyclerView
    lateinit var usuariosAdapter: UsuariosAdapter
    lateinit var btnAddUser: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_usuarios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvUsuarios = view.findViewById(R.id.rvUsuarios)
        rvUsuarios.setHasFixedSize(true)
        rvUsuarios.layoutManager = LinearLayoutManager(context)
        getUsuarios()

        btnAddUser = view.findViewById(R.id.btnAddUser)
        btnAddUser.setOnClickListener(View.OnClickListener { dialogUser() })
    }

    private fun dialogUser() {
        val dialog = layoutInflater.inflate(R.layout.usuarios_modifier, null)
        val edUsername = dialog.findViewById<EditText>(R.id.edUsernameModifier)
        val edPass = dialog.findViewById<EditText>(R.id.edPassModifier)
        val edEmail = dialog.findViewById<EditText>(R.id.edEmailModifier)
        val ckbJefe = dialog.findViewById<CheckBox>(R.id.ckbJefeModifier)
        val ckbEncargado = dialog.findViewById<CheckBox>(R.id.ckbEncargadoModifier)
        val ckbProfesor = dialog.findViewById<CheckBox>(R.id.ckbProfesorModifier)
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.strTituloModUser))
            .setView(dialog)
            .setPositiveButton("OK") { view, _ ->
                if (!camposVacios(
                        edUsername,
                        edPass,
                        edEmail,
                        ckbJefe,
                        ckbEncargado,
                        ckbProfesor
                    )
                ) {
                    val roles = ArrayList<Rol>(0)
                    if (ckbJefe.isChecked) roles.add(Rol.JEFE_DEPARTAMENTO)
                    if (ckbEncargado.isChecked) roles.add(Rol.ENCARGADO)
                    if (ckbProfesor.isChecked) roles.add(Rol.PROFESOR)
                    val usuario = Usuario(
                        edUsername.text.toString(),
                        edPass.text.toString(),
                        roles,
                        edEmail.text.toString()
                    )
                    addUser(usuario)
                } else Toast.makeText(
                    requireContext(),
                    getString(R.string.strCamposVacios),
                    Toast.LENGTH_SHORT
                ).show()
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.strCancelar)) { view, _ ->
                view.dismiss()
            }
            .setCancelable(false).create().show()
    }

    private fun addUser(usuario: Usuario) {
        val request = ServiceBuilder.buildService(InventarioApi::class.java)
        val call = request.addUser(usuario)
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
                    getUsuarios()
                } else Toast.makeText(context, "el usuario ya existe", Toast.LENGTH_SHORT)
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

    private fun camposVacios(
        edUsername: EditText,
        edPass: EditText,
        edEmail: EditText,
        ckbJefe: CheckBox,
        ckbEncargado: CheckBox,
        ckbProfesor: CheckBox
    ): Boolean {
        return edUsername.text.isEmpty() ||
                edPass.text.isEmpty() ||
                edEmail.text.isEmpty() ||
                (!ckbJefe.isChecked && !ckbEncargado.isChecked && !ckbProfesor.isChecked)
    }

    private fun newUsuariosAdapter(adaptador: UsuariosAdapter) {
        usuariosAdapter = adaptador
        rvUsuarios.adapter = usuariosAdapter
    }

    private fun getUsuarios(): ArrayList<Usuario> {
        val request = ServiceBuilder.buildService(InventarioApi::class.java)
        val call = request.getUsuarios()
        val usuarios = ArrayList<Usuario>(0)
        call.enqueue(object : Callback<MutableList<Usuario>> {
            override fun onResponse(
                call: Call<MutableList<Usuario>>,
                response: Response<MutableList<Usuario>>
            ) {
                if (response.code() == 200) {
                    for (usuario in response.body()!!) {
                        if (usuario.img == null) usuario.img = byteArrayOf()
                        usuarios.add(usuario)
                    }
                    newUsuariosAdapter(
                        UsuariosAdapter(
                            context as AppCompatActivity,
                            usuarios
                        )
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
        return usuarios
    }
}