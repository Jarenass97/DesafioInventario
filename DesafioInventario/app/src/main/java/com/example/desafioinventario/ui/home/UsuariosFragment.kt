package com.example.desafioinventario.ui.home

import adapters.UsuariosAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.InventarioApi
import api.ServiceBuilder
import com.example.desafioinventario.R
import model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsuariosFragment : Fragment() {
    lateinit var rvUsuarios: RecyclerView
    lateinit var usuariosAdapter: UsuariosAdapter
    lateinit var usuario: Usuario

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
        val bun = requireActivity().intent.extras!!
        usuario = bun.getSerializable("user") as Usuario
        rvUsuarios = view.findViewById(R.id.rvUsuarios)
        rvUsuarios.setHasFixedSize(true)
        rvUsuarios.layoutManager = LinearLayoutManager(context)
        getUsuarios()
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
                        usuarios.add(usuario)
                    }
                    newUsuariosAdapter(UsuariosAdapter(context as AppCompatActivity, usuarios,usuario))
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