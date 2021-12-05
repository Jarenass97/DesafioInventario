package assistant

import android.content.Context
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.desafioinventario.LoginActivity
import com.example.desafioinventario.R

class Animacion(context: AppCompatActivity) {
    val aparicion_difuminada=AnimationUtils.loadAnimation(context, R.anim.aparicion_difuminada)
}