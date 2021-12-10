package com.example.desafioinventario.ui.home

import adapters.AulasAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import assistant.Curso
import com.example.desafioinventario.R
import com.example.desafioinventario.databinding.FragmentHomeBinding
import model.Aula

class HomeFragment() : Fragment() {

    lateinit var rvAulas: RecyclerView
    lateinit var aulasAdapter: AulasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_home,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvAulas = view.findViewById(R.id.rvAulas)
        rvAulas.setHasFixedSize(true)
        rvAulas.layoutManager = LinearLayoutManager(this.context)
        val aulas = ArrayList<Aula>(0)
        aulas.add(Aula("209", "", Curso.DAM2))
        aulas.add(Aula("212", "", Curso.DAM1))
        newAulasAdapter(AulasAdapter(this.context as AppCompatActivity, aulas))
    }
    private fun newAulasAdapter(adaptador: AulasAdapter) {
        aulasAdapter = adaptador
        rvAulas.adapter = aulasAdapter
    }
}