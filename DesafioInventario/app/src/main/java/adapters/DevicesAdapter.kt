package adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import api.InventarioApi
import api.ServiceBuilder
import assistant.Rol
import com.example.desafioinventario.R
import model.Dispositivo
import model.Usuario
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DevicesAdapter(
    var context: AppCompatActivity,
    var dispositivos: ArrayList<Dispositivo>,
) :
    RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {

    companion object {
        var seleccionado: Int = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.dispositivos_item, parent, false), context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dispositivos[position]
        holder.bind(item, context, position, this)
    }

    override fun getItemCount(): Int {
        return dispositivos.size
    }

    fun getSelected(): Dispositivo {
        return dispositivos[seleccionado]
    }

    fun deseleccionar() {
        if (seleccionado != -1) {
            seleccionado = -1
            notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View, val ventana: AppCompatActivity) :
        RecyclerView.ViewHolder(view) {
        val txtId = view.findViewById<TextView>(R.id.txtIdDispositivo)
        val txtNombre = view.findViewById<TextView>(R.id.txtNombreDispositivo)

        fun bind(
            dispositivo: Dispositivo,
            context: AppCompatActivity,
            pos: Int,
            devicesAdapter: DevicesAdapter
        ) {
            txtId.text = dispositivo.id
            txtNombre.text = dispositivo.nombre
            if (pos == seleccionado) {
                with(itemView) { setBackgroundResource(R.color.onPrimaryDark) }
                with(txtId) { setTextColor(Color.WHITE) }
                with(txtNombre) { setTextColor(Color.WHITE) }
            } else {
                with(itemView) { setBackgroundResource(R.color.white) }
                with(txtId) { setTextColor(Color.BLACK) }
                with(txtNombre) { setTextColor(Color.BLACK) }
            }
            itemView.setOnClickListener(View.OnClickListener {
                marcarSeleccion(devicesAdapter, pos)
            })
            itemView.setOnLongClickListener(View.OnLongClickListener {
                marcarSeleccion(devicesAdapter, pos)
                preguntarBorrado(dispositivo, devicesAdapter)
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

        private fun preguntarBorrado(dispositivo: Dispositivo, devicesAdapter: DevicesAdapter) {
            AlertDialog.Builder(ventana)
                .setTitle(ventana.getString(R.string.strTituloBorrar))
                .setMessage(ventana.getString(R.string.strMensajeBorrar))
                .setPositiveButton("OK") { view, _ ->
                    marcarSeleccion(devicesAdapter, -1)
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

        private fun marcarSeleccion(devicesAdapter: DevicesAdapter, pos: Int) {
            seleccionado = pos
            devicesAdapter.notifyDataSetChanged()
        }
    }

}