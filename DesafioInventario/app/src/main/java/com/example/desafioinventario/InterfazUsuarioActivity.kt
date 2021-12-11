package com.example.desafioinventario

import adapters.AulasAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import api.InventarioApi
import api.ServiceBuilder
import assistant.Auxiliar
import assistant.Curso
import com.example.desafioinventario.databinding.ActivityInterfazUsuarioBinding
import com.example.desafioinventario.ui.home.HomeFragment
import model.Usuario
import retrofit2.*

class InterfazUsuarioActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityInterfazUsuarioBinding
    lateinit var usuario: Usuario
    lateinit var rvAulas: RecyclerView
    lateinit var aulasAdapter: AulasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bun: Bundle = intent.extras!!
        usuario = bun.getSerializable("user") as Usuario
        Log.e("jorge", usuario.toString())

        binding = ActivityInterfazUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarInterfazUsuario.toolbar)

        binding.appBarInterfazUsuario.btnAddAula.setOnClickListener { view ->
            dialogAula()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        cargarUsuario()
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
        AlertDialog.Builder(this)
            .setTitle("Add Aula")
            .setView(aulaView)
            .setPositiveButton("OK") { view, _ ->
                view.dismiss()
            }
            .setNegativeButton("cancel") { view, _ ->
                view.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
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
                        this@InterfazUsuarioActivity,
                        R.layout.cursos_list,
                        R.id.txtCurso,
                        usuarios
                    )
                } else {
                    Log.e("Fernando", "Algo ha fallado en el login.")
                    Toast.makeText(this@InterfazUsuarioActivity, response.message().toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<MutableList<Usuario>>, t: Throwable) {
                Toast.makeText(
                    this@InterfazUsuarioActivity,
                    getString(R.string.strFalloConexion),
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun cargarCursos(curso: Spinner) {
        val cursos = Curso.values()
        curso.adapter = ArrayAdapter(this, R.layout.cursos_list, R.id.txtCurso, cursos)
    }

    private fun cargarUsuario() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val header: View = navigationView.getHeaderView(0)
        val img = header.findViewById<ImageView>(R.id.imgUsuarioNavigation)
        val username = header.findViewById<TextView>(R.id.txtUsernameNavigation)
        val email = header.findViewById<TextView>(R.id.txtEmailNavigation)
        if (usuario.img != null) img.setImageBitmap(
            Auxiliar.getImage(
                usuario.img!!
            )
        )
        username.text = usuario.username
        email.text = usuario.email
    }

    private fun replaceFragment(fragment: HomeFragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.interfaz_usuario, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.strTituloCerrarSesion))
            .setMessage(getString(R.string.strMensajeCerrarSesion))
            .setPositiveButton("OK") { view, _ ->
                super.onBackPressed()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                view.dismiss()
            }
            .setNegativeButton("NO") { view, _ ->
                view.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }
}