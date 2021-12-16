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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import assistant.Auxiliar
import assistant.Auxiliar.usuario
import com.example.desafioinventario.databinding.ActivityInterfazUsuarioBinding
import model.Usuario
import retrofit2.*


class InterfazUsuarioActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityInterfazUsuarioBinding
    lateinit var rvAulas: RecyclerView
    lateinit var aulasAdapter: AulasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterfazUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarInterfazUsuario.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_aulas,
                R.id.nav_usuarios,
                R.id.nav_perfil
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        cargarUsuario()
    }

    private fun cargarUsuario() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val header: View = navigationView.getHeaderView(0)
        val img = header.findViewById<ImageView>(R.id.imgUsuarioNavigation)
        val username = header.findViewById<TextView>(R.id.txtUsernameNavigation)
        val email = header.findViewById<TextView>(R.id.txtEmailNavigation)
        if (!usuario.sinImagen()) img.setImageBitmap(
            Auxiliar.getImage(
                usuario.img!!
            )
        )
        username.text = usuario.username
        email.text = usuario.email
        if (!usuario.isJefe()) {
            val nav_Menu: Menu = navigationView.menu
            nav_Menu.findItem(R.id.nav_usuarios).isVisible = false
        }
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