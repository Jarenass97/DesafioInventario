package api

import model.*
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.*

interface InventarioApi {

    @GET("/usuario/{username}")
    fun getUsuario(@Path("username") username: String): Call<Usuario>

    @GET("/usuarios")
    fun getUsuarios(): Call<MutableList<Usuario>>

    @Headers("Content-Type:application/json")
    @POST("/addUser")
    fun addUser(@Body info: Usuario): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("/addAula")
    fun addAula(@Body info: Aula): Call<ResponseBody>

    @GET("/aulas")
    fun getAulas(): Call<MutableList<Aula>>
}