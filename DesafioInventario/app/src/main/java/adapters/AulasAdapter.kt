package adapters

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import api.InventarioApi
import api.ServiceBuilder
import assistant.Auxiliar.usuario
import assistant.Curso
import com.example.desafioinventario.InventarioActivity
import com.example.desafioinventario.R
import model.Aula
import model.Usuario
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AulasAdapter(
    var context: AppCompatActivity,
    var aulas: ArrayList<Aula>
) :
    RecyclerView.Adapter<AulasAdapter.ViewHolder>() {

    companion object {
        var seleccionado: Int = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.aulas_item, parent, false),
            context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = aulas[position]
        holder.bind(item, context, position, this)
    }

    override fun getItemCount(): Int {
        return aulas.size
    }

    fun getSelected(): Aula {
        return aulas[seleccionado]
    }

    fun deseleccionar() {
        if (seleccionado != -1) {
            seleccionado = -1
            notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View, val ventana: AppCompatActivity) :
        RecyclerView.ViewHolder(view) {
        val txtNombre = view.findViewById<TextView>(R.id.txtNombreAulaItem)
        val txtCurso = view.findViewById<TextView>(R.id.txtCursoAulaItem)
        val icono = view.findViewById<ImageView>(R.id.icAulaItem)

        fun bind(aula: Aula, context: AppCompatActivity, pos: Int, aulasAdapter: AulasAdapter) {
            txtNombre.text = "${context.getString(R.string.strAula)}: ${aula.nombre}"
            txtCurso.text = "${context.getString(R.string.strCurso)}: ${aula.curso}"
            if (pos == seleccionado) {
                with(itemView) { setBackgroundResource(R.color.onPrimaryDark) }
                with(icono) { setColorFilter(Color.WHITE) }
                with(txtNombre) { setTextColor(Color.WHITE) }
                with(txtCurso) { setTextColor(Color.WHITE) }
            } else {
                with(itemView) { setBackgroundResource(R.color.white) }
                with(icono) { setColorFilter(Color.BLACK) }
                with(txtNombre) { setTextColor(Color.BLACK) }
                with(txtCurso) { setTextColor(Color.BLACK) }
            }
            itemView.setOnClickListener(View.OnClickListener {
                marcarSeleccion(aulasAdapter, pos)
                if (usuario.isJefe()) {
                    preguntarOpcion(aula, aulasAdapter)
                } else abrirInventario(aula)
            })
            itemView.setOnLongClickListener(View.OnLongClickListener {
                marcarSeleccion(aulasAdapter, pos)
                if (usuario.isJefe()) {
                    preguntarBorrado(aula, aulasAdapter)
                }
                true
            })
        }

        private fun preguntarOpcion(aula: Aula, aulasAdapter: AulasAdapter) {
            AlertDialog.Builder(ventana)
                .setTitle(ventana.getString(R.string.strEligeOpcion))
                .setMessage(ventana.getString(R.string.strMensajeOpcionAula))
                .setPositiveButton(ventana.getString(R.string.strEditar)) { view, _ ->
                    dialogAula(aula, aulasAdapter)
                    view.dismiss()
                }
                .setNegativeButton(ventana.getString(R.string.strVerInventario)) { view, _ ->
                    abrirInventario(aula)
                    view.dismiss()
                }
                .setCancelable(true).create().show()
        }

        private fun abrirInventario(aula: Aula) {
            val intent = Intent(ventana, InventarioActivity::class.java)
            intent.putExtra("aula", aula)
            ventana.startActivity(intent)
        }

        private fun deleteAula(aula: Aula, aulasAdapter: AulasAdapter) {
            val request = ServiceBuilder.buildService(InventarioApi::class.java)
            val call = request.deleteAula(aula.nombre)
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
                        recargarAulas(aulasAdapter)
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

        private fun preguntarBorrado(aula: Aula, aulasAdapter: AulasAdapter) {
            AlertDialog.Builder(ventana)
                .setTitle(ventana.getString(R.string.strTituloBorrar))
                .setMessage(ventana.getString(R.string.strMensajeBorrar))
                .setPositiveButton("OK") { view, _ ->
                    deleteAula(aula, aulasAdapter)
                    view.dismiss()
                }
                .setNegativeButton(ventana.getString(R.string.strCancelar)) { view, _ ->
                    view.dismiss()
                }
                .create()
                .show()
        }

        private fun dialogAula(aula: Aula, aulasAdapter: AulasAdapter) {
            val identificador = aula.nombre
            val aulaView = ventana.layoutInflater.inflate(R.layout.aulas_creater, null)
            val nombre = aulaView.findViewById<EditText>(R.id.edNombreAula)
            val descripcion = aulaView.findViewById<EditText>(R.id.edDescripcionAula)
            val curso = aulaView.findViewById<Spinner>(R.id.spCursoAula)
            val encargado = aulaView.findViewById<Spinner>(R.id.spEncargadoAula)
            val alumnos = aulaView.findViewById<EditText>(R.id.edAlumnosAula)
            nombre.append(aula.nombre)
            descripcion.append(aula.descripcion)
            cargarCursos(curso, aula.curso)
            cargarEncargados(encargado, aula.encargado)
            alumnos.append(aula.numAlumnos.toString())
            AlertDialog.Builder(ventana)
                .setIcon(R.drawable.ic_class)
                .setTitle(ventana.getString(R.string.strTituloModAula))
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
                        modAula(identificador, aula, aulasAdapter)
                    } else {
                        Toast.makeText(
                            ventana,
                            ventana.getString(R.string.strCamposVacios),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    view.dismiss()
                }
                .setNegativeButton(ventana.getString(R.string.strCancelar)) { view, _ ->
                    view.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()
        }

        private fun modAula(nombre: String, aula: Aula, aulasAdapter: AulasAdapter) {
            val request = ServiceBuilder.buildService(InventarioApi::class.java)
            val call = request.modAula(nombre, aula)
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
                        recargarAulas(aulasAdapter)
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

        private fun recargarAulas(aulasAdapter: AulasAdapter) {
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
                        aulasAdapter.aulas = aulas
                        aulasAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            ventana,
                            response.message().toString(),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }

                override fun onFailure(call: Call<MutableList<Aula>>, t: Throwable) {
                    Toast.makeText(
                        ventana,
                        ventana.getString(R.string.strFalloConexion),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        private fun camposVacios(
            nombre: EditText,
            descripcion: EditText,
            alumnos: EditText
        ): Boolean {
            return nombre.text.isEmpty() || descripcion.text.isEmpty() || alumnos.text.isEmpty()
        }

        private fun cargarEncargados(encargado: Spinner, userEncargado: String?) {
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
                            if (post.isEncargado()) usuarios.add(post.username)
                        }
                        encargado.adapter = ArrayAdapter(
                            ventana,
                            R.layout.encargados_list,
                            R.id.txtEncargadoAulaItem,
                            usuarios
                        )
                        if (userEncargado != null) encargado.setSelection(
                            usuarios.indexOf(
                                userEncargado
                            )
                        )
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

        private fun cargarCursos(curso: Spinner, cursoAula: Curso) {
            val cursos = Curso.values()
            curso.adapter =
                ArrayAdapter(ventana, R.layout.cursos_list, R.id.txtCurso, cursos)
            curso.setSelection(cursos.indexOf(cursoAula))
        }

        private fun marcarSeleccion(aulasAdapter: AulasAdapter, pos: Int) {
            seleccionado = pos
            aulasAdapter.notifyDataSetChanged()
        }
    }

}