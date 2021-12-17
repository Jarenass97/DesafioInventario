package model

import assistant.Curso
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Aula(
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("descripcion")
    var descripcion: String,
    @SerializedName("curso_impartido")
    var curso: Curso,
    @SerializedName("username_encargado")
    var encargado: String?,
    @SerializedName("num_alumnos")
    var numAlumnos: Int
) : Serializable
