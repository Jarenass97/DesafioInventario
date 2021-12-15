from flask import Flask, request, jsonify
from flask_restful import Resource, Api
from Conexion import Conexion
from assistant import Constantes
from assistant.Constantes import *

app = Flask(__name__)
api = Api(app)

conexion = Conexion('root', '', 'inventario')


@app.route('/')
def hello():
    return 'Bienvenido a la Api de mi inventario' \
           '\n"/usuarios" muestra usuarios' \
           '\n"/usuario/<username>" muestra usuario por nombre de usuario'


@app.route('/usuarios', methods=['GET'])
def getUsers():
    lista = conexion.getUsuarios()
    if len(lista) != 0:
        resp = jsonify(lista)
        resp.status_code = 200
    else:
        respuesta = {'message': 'No existen usuarios.'}
        resp = jsonify(respuesta)
        resp.status_code = 400
    return resp


@app.route('/addUser', methods=["POST"])
def addUsuario():
    data = request.json
    if (conexion.insertarUsuario(data[Constantes.USERNAME__USUARIOS], data[Constantes.PASSWD__USUARIOS],
                                 data[Constantes.EMAIL__USUARIOS], data[Constantes.IMAGE__USUARIOS],
                                 data[Constantes.ARRAY_ROLES]) == 0):
        respuesta = {'message': 'OK.'}
        resp = jsonify(respuesta)
        resp.status_code = 200
    else:
        respuesta = {'message': 'El usuario ya existe.'}
        resp = jsonify(respuesta)
        resp.status_code = 400
    return resp


@app.route('/addAula', methods=["POST"])
def addAula():
    data = request.json
    if (conexion.insertarAula(data[NOMBRE__AULAS], data[DESCRIPCION__AULAS], data[CURSO_IMPARTIDO__AULAS],
                              data[ENCARGADO__AULAS], data[NUM_ALUMNOS__AULAS]) == 0):
        respuesta = {'message': 'OK.'}
        resp = jsonify(respuesta)
        resp.status_code = 200
    else:
        respuesta = {'message': 'El aula ya existe.'}
        resp = jsonify(respuesta)
        resp.status_code = 400
    return resp


@app.route('/aulas', methods=['GET'])
def getAulas():
    lista = conexion.getAulas()
    if len(lista) != 0:
        resp = jsonify(lista)
        resp.status_code = 200
    else:
        respuesta = {'message': 'No existen aulas.'}
        resp = jsonify(respuesta)
        resp.status_code = 400
    return resp


@app.route('/aulas/<encargado>', methods=['GET'])
def getAulasEncargado(encargado):
    lista = conexion.getAulasEncargado(encargado)
    if len(lista) != 0:
        resp = jsonify(lista)
        resp.status_code = 200
    else:
        respuesta = {'message': 'No existen aulas.'}
        resp = jsonify(respuesta)
        resp.status_code = 400
    return resp


@app.route("/usuario/<username>", methods=['GET'])
def getUsuario(username):
    usuarios = conexion.getUsuario(username)
    if len(usuarios) != 0:
        resp = jsonify(usuarios)
        resp.status_code = 200
    else:
        respuesta = {'message': 'No existe el usuario'}
        resp = jsonify(respuesta)
        resp.status_code = 400
    return resp


@app.route("/modUser/<username>", methods=["PUT"])
def modUsuario(username):
    data = request.json
    if (conexion.modificarUsuario(username, data[USERNAME__USUARIOS], data[PASSWD__USUARIOS], data[EMAIL__USUARIOS],
                                  data[IMAGE__USUARIOS]) > 0):
        respuesta = {'message': 'Ok.'}
        resp = jsonify(respuesta)
        resp.status_code = 200
    else:
        respuesta = {'message': 'Error al modificar.'}
        resp = jsonify(respuesta)
        resp.status_code = 400
    return resp


@app.route("/modAula/<nombre>", methods=["PUT"])
def modAula(nombre):
    data = request.json
    if (conexion.modificarAula(nombre, data[NOMBRE__AULAS], data[DESCRIPCION__AULAS], data[CURSO_IMPARTIDO__AULAS],
                               data[ENCARGADO__AULAS], data[NUM_ALUMNOS__AULAS]) > 0):
        respuesta = {'message': 'Ok.'}
        resp = jsonify(respuesta)
        resp.status_code = 200
    else:
        respuesta = {'message': 'Error al modificar.'}
        resp = jsonify(respuesta)
        resp.status_code = 400
    return resp


@app.route("/delAula/<nombre>", methods=["DELETE"])
def delPersona(nombre):
    if conexion.borrarAula(nombre) > 0:
        respuesta = {'message': 'Ok.'}
        resp = jsonify(respuesta)
        resp.status_code = 200
    else:
        respuesta = {'message': 'Aula' + str(nombre) + ' no encontrado.'}
        resp = jsonify(respuesta)
        resp.status_code = 400
    return resp


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
