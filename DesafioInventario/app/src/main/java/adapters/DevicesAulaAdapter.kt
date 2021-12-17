package adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import api.InventarioApi
import api.ServiceBuilder
import assistant.Auxiliar.usuario
import com.example.desafioinventario.R
import model.Aula
import model.Dispositivo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DevicesAulaAdapter(
    var context: AppCompatActivity,
    var dispositivos: ArrayList<Dispositivo>,
    val aula: Aula
) :
    RecyclerView.Adapter<DevicesAulaAdapter.ViewHolder>() {

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
        val txtId = view.findViewById<TextView>(R.id.txtParam1Dispositivo)
        val txtNombre = view.findViewById<TextView>(R.id.txtParam2Dispositivo)

        fun bind(
            dispositivo: Dispositivo,
            context: AppCompatActivity,
            pos: Int,
            devicesAdapter: DevicesAulaAdapter
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
            if (usuario.isEncargado() || usuario.isJefe()) {
                itemView.setOnClickListener(View.OnClickListener {
                    marcarSeleccion(devicesAdapter, pos)
                    dialogDevice(dispositivo, devicesAdapter)
                })
                itemView.setOnLongClickListener(View.OnLongClickListener {
                    marcarSeleccion(devicesAdapter, pos)
                    preguntarBorrado(dispositivo, devicesAdapter)
                    true
                })
            }
        }

        private fun dialogDevice(dispositivo: Dispositivo, devicesAdapter: DevicesAulaAdapter) {
            val id = dispositivo.id
            val dialog = ventana.layoutInflater.inflate(R.layout.dispositivos_creater, null)
            val edIdentificador =
                dialog.findViewById<EditText>(R.id.edIdentificadorDispositivoCreater)
            val edNombre = dialog.findViewById<EditText>(R.id.edNombreDispositivoCreater)
            ocultarCampos(dialog)
            cargarDatos(dispositivo, edIdentificador, edNombre)
            AlertDialog.Builder(ventana)
                .setView(dialog)
                .setTitle(ventana.getString(R.string.strModificarDispositivo))
                .setPositiveButton("OK") { view, _ ->
                    if (!camposVacios(edIdentificador, edNombre)) {
                        dispositivo.id = edIdentificador.text.toString()
                        dispositivo.nombre = edNombre.text.toString()
                        modDevice(id, dispositivo, devicesAdapter)
                    }
                    view.dismiss()
                }
                .setNegativeButton(ventana.getString(R.string.strCancelar)) { view, _ ->
                    view.dismiss()
                }
                .setCancelable(false).create().show()
        }

        private fun ocultarCampos(dialog: View) {
            val txtAula = dialog.findViewById<TextView>(R.id.txtAulaDispositivosCreater)
            val spAulas = dialog.findViewById<Spinner>(R.id.spAulaDispositivosCreater)
            txtAula.isVisible = false
            spAulas.isVisible = false
        }

        private fun cargarDatos(
            dispositivo: Dispositivo,
            edIdentificador: EditText,
            edNombre: EditText
        ) {
            edIdentificador.text.apply { clear();append(dispositivo.id) }
            edNombre.text.apply { clear();append(dispositivo.nombre) }
        }

        private fun camposVacios(edIdentificador: EditText, edNombre: EditText): Boolean {
            return edIdentificador.text.isEmpty() || edNombre.text.isEmpty()
        }

        private fun modDevice(
            identificador: String,
            device: Dispositivo,
            devicesAdapter: DevicesAulaAdapter
        ) {
            val request = ServiceBuilder.buildService(InventarioApi::class.java)
            val call = request.modDispositivo(identificador, device)
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
                        recargarDispositivos(devicesAdapter)
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


        private fun preguntarBorrado(dispositivo: Dispositivo, devicesAdapter: DevicesAulaAdapter) {
            AlertDialog.Builder(ventana)
                .setTitle(ventana.getString(R.string.strTituloBorrar))
                .setMessage(ventana.getString(R.string.strMensajeBorrar))
                .setPositiveButton("OK") { view, _ ->
                    marcarSeleccion(devicesAdapter, -1)
                    borrarDispositivo(dispositivo, devicesAdapter)
                    view.dismiss()
                }
                .setNegativeButton(ventana.getString(R.string.strCancelar)) { view, _ ->
                    view.dismiss()
                }
                .create()
                .show()
        }

        private fun borrarDispositivo(
            dispositivo: Dispositivo,
            devicesAdapter: DevicesAulaAdapter
        ) {
            val request = ServiceBuilder.buildService(InventarioApi::class.java)
            val call = request.deleteDispositivo(dispositivo.id)
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
                        recargarDispositivos(devicesAdapter)
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

        private fun recargarDispositivos(devicesAdapter: DevicesAulaAdapter) {
            val request = ServiceBuilder.buildService(InventarioApi::class.java)
            val call = request.getDispositivosByAula(devicesAdapter.aula.nombre)
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
                        devicesAdapter.dispositivos = dispositivos
                        devicesAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            ventana,
                            "No existen dispositivos que mostrar",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        devicesAdapter.dispositivos = ArrayList<Dispositivo>(0)
                        devicesAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<MutableList<Dispositivo>>, t: Throwable) {
                    Toast.makeText(
                        ventana,
                        ventana.getString(R.string.strFalloConexion),
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
        }

        private fun marcarSeleccion(devicesAdapter: DevicesAulaAdapter, pos: Int) {
            seleccionado = pos
            devicesAdapter.notifyDataSetChanged()
        }
    }

}