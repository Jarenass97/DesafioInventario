package adapters

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import api.InventarioApi
import api.ServiceBuilder
import assistant.Auxiliar
import assistant.Auxiliar.usuario
import assistant.Rol
import com.example.desafioinventario.R
import com.google.android.material.navigation.NavigationView
import model.Aula
import model.Usuario
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsuariosAdapter(
    var context: AppCompatActivity,
    var usuarios: ArrayList<Usuario>,
) :
    RecyclerView.Adapter<UsuariosAdapter.ViewHolder>() {

    companion object {
        var seleccionado: Int = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.usuarios_item, parent, false), context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = usuarios[position]
        holder.bind(item, context, position, this)
    }

    override fun getItemCount(): Int {
        return usuarios.size
    }

    fun getSelected(): Usuario {
        return usuarios[seleccionado]
    }

    fun deseleccionar() {
        if (seleccionado != -1) {
            seleccionado = -1
            notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View, val ventana: AppCompatActivity) :
        RecyclerView.ViewHolder(view) {
        val imgUsuario = view.findViewById<ImageView>(R.id.imgUsuarioItem)
        val txtUsername = view.findViewById<TextView>(R.id.txtUsernameItem)
        val lblRoles = view.findViewById<TextView>(R.id.lblRolesUsuarioItem)
        val txtRoles = view.findViewById<TextView>(R.id.txtRolesItem)

        fun bind(
            usuario: Usuario,
            context: AppCompatActivity,
            pos: Int,
            usuariosAdapter: UsuariosAdapter
        ) {
            if (!usuario.sinImagen()) imgUsuario.setImageBitmap(Auxiliar.getImage(usuario.img!!))
            txtUsername.text =
                if (usuario.username == Auxiliar.usuario.username) "${usuario.username}(t??)"
                else usuario.username
            txtRoles.text = rolesToString(usuario)
            if (pos == seleccionado) {
                with(itemView) { setBackgroundResource(R.color.onPrimaryDark) }
                with(txtUsername) { setTextColor(Color.WHITE) }
                with(lblRoles) { setTextColor(Color.WHITE) }
                with(txtRoles) { setTextColor(Color.WHITE) }
            } else {
                with(itemView) { setBackgroundResource(R.color.white) }
                with(txtUsername) { setTextColor(Color.BLACK) }
                with(lblRoles) { setTextColor(Color.BLACK) }
                with(txtRoles) { setTextColor(Color.BLACK) }
            }
            itemView.setOnClickListener(View.OnClickListener {
                marcarSeleccion(usuariosAdapter, pos)
                dialogUser(usuario, usuariosAdapter)
            })
            itemView.setOnLongClickListener(View.OnLongClickListener {
                marcarSeleccion(usuariosAdapter, pos)
                preguntarBorrado(usuario, usuariosAdapter)
                true
            })
        }

        private fun dialogUser(usuario: Usuario, usuariosAdapter: UsuariosAdapter) {
            val username = usuario.username
            val dialog = ventana.layoutInflater.inflate(R.layout.usuarios_modifier, null)
            val edUsername = dialog.findViewById<EditText>(R.id.edUsernameModifier)
            val edPass = dialog.findViewById<EditText>(R.id.edPassModifier)
            val edEmail = dialog.findViewById<EditText>(R.id.edEmailModifier)
            val ckbJefe = dialog.findViewById<CheckBox>(R.id.ckbJefeModifier)
            val ckbEncargado = dialog.findViewById<CheckBox>(R.id.ckbEncargadoModifier)
            val ckbProfesor = dialog.findViewById<CheckBox>(R.id.ckbProfesorModifier)
            cargarDatos(usuario, edUsername, edPass, edEmail, ckbJefe, ckbEncargado, ckbProfesor)
            AlertDialog.Builder(ventana)
                .setTitle(ventana.getString(R.string.strTituloModUser))
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
                        usuario.username = edUsername.text.toString()
                        usuario.passwd = edPass.text.toString()
                        usuario.email = edEmail.text.toString()
                        val roles = ArrayList<Rol>(0)
                        if (ckbJefe.isChecked) roles.add(Rol.JEFE_DEPARTAMENTO)
                        if (ckbEncargado.isChecked) roles.add(Rol.ENCARGADO)
                        if (ckbProfesor.isChecked) roles.add(Rol.PROFESOR)
                        usuario.roles = roles
                        modUser(username, usuario, usuariosAdapter)
                    } else Toast.makeText(
                        ventana,
                        ventana.getString(R.string.strCamposVacios),
                        Toast.LENGTH_SHORT
                    ).show()
                    view.dismiss()
                }
                .setNegativeButton(ventana.getString(R.string.strCancelar)) { view, _ ->
                    view.dismiss()
                }
                .setCancelable(false).create().show()
        }

        private fun cargarDatos(
            usuario: Usuario,
            edUsername: EditText,
            edPass: EditText,
            edEmail: EditText,
            ckbJefe: CheckBox,
            ckbEncargado: CheckBox,
            ckbProfesor: CheckBox
        ) {
            edUsername.text.apply { clear();append(usuario.username) }
            edPass.text.apply { clear();append(usuario.passwd) }
            edEmail.text.apply { clear();append(usuario.email) }
            if (usuario.isJefe()) ckbJefe.isChecked = true
            if (usuario.isEncargado()) ckbEncargado.isChecked = true
            if (usuario.isProfesor()) ckbProfesor.isChecked = true
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

        private fun modUser(username: String, usuario: Usuario, usuariosAdapter: UsuariosAdapter) {
            val request = ServiceBuilder.buildService(InventarioApi::class.java)
            val call = request.modUsuario(username, usuario)
            call.enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == 200) {
                        Toast.makeText(
                            ventana,
                            ventana.getString(R.string.strOperacionExitosa),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        recargarUsuarios(usuariosAdapter)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        ventana,
                        ventana.getString(R.string.strFalloConexion),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            })
        }

        private fun rolesToString(usuario: Usuario): String {
            var cad = ""
            for (r in usuario.roles) {
                cad += "- $r\n"
            }
            cad = cad.removeRange(cad.length - 1 until cad.length)
            return cad
        }

        private fun preguntarBorrado(usuario: Usuario, usuariosAdapter: UsuariosAdapter) {
            AlertDialog.Builder(ventana)
                .setTitle(ventana.getString(R.string.strTituloBorrar))
                .setMessage(ventana.getString(R.string.strMensajeBorrar))
                .setPositiveButton("OK") { view, _ ->
                    borrarUsuario(usuario, usuariosAdapter)
                    marcarSeleccion(usuariosAdapter, -1)
                    view.dismiss()
                }
                .setNegativeButton(ventana.getString(R.string.strCancelar)) { view, _ ->
                    view.dismiss()
                }
                .create()
                .show()
        }

        private fun borrarUsuario(usuario: Usuario, usuariosAdapter: UsuariosAdapter) {
            val request = ServiceBuilder.buildService(InventarioApi::class.java)
            val call = request.deleteUsuario(usuario.username)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == 200) {
                        Toast.makeText(
                            ventana,
                            ventana.getString(R.string.strOperacionExitosa),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        recargarUsuarios(usuariosAdapter)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        ventana,
                        ventana.getString(R.string.strFalloConexion),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            })
        }

        private fun recargarUsuarios(usuariosAdapter: UsuariosAdapter) {
            val request = ServiceBuilder.buildService(InventarioApi::class.java)
            val call = request.getUsuarios()
            call.enqueue(object : Callback<MutableList<Usuario>> {
                override fun onResponse(
                    call: Call<MutableList<Usuario>>,
                    response: Response<MutableList<Usuario>>
                ) {
                    if (response.code() == 200) {
                        val usuarios = ArrayList<Usuario>(0)
                        for (user in response.body()!!) {
                            if (user.img == null) user.img = byteArrayOf()
                            usuarios.add(user)
                        }
                        usuariosAdapter.usuarios = usuarios
                        usuariosAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            ventana,
                            response.message().toString(),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }

                override fun onFailure(call: Call<MutableList<Usuario>>, t: Throwable) {
                    Toast.makeText(
                        ventana,
                        ventana.getString(R.string.strFalloConexion),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        private fun marcarSeleccion(usuariosAdapter: UsuariosAdapter, pos: Int) {
            seleccionado = pos
            usuariosAdapter.notifyDataSetChanged()
        }
    }

}