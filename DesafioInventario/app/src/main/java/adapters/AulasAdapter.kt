package adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.desafioinventario.R
import model.Aula
import org.jetbrains.anko.find

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
        return ViewHolder(layoutInflater.inflate(R.layout.aulas_item, parent, false), context)
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

    class ViewHolder(view: View, ventana: AppCompatActivity) : RecyclerView.ViewHolder(view) {
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
            })
        }

        private fun marcarSeleccion(aulasAdapter: AulasAdapter, pos: Int) {
            seleccionado = if (pos == seleccionado) -1
            else pos
            aulasAdapter.notifyDataSetChanged()
        }
    }

}