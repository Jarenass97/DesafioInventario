from flask import Flask, request, jsonify
from flask_restful import Resource, Api
from Conexion import Conexion
from assistant import Constantes

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
    print(lista)
    if (len(lista) != 0):
        resp = jsonify(lista)
        resp.status_code = 200
    else:
        respuesta = {'message': 'No existen usuarios.'}
        resp = jsonify(respuesta)
        resp.status_code = 400
    print(resp)
    return resp


@app.route('/addUser', methods=["POST"])
def addUsuario():
    data = request.json
    if (conexion.insertarUsuario(data[Constantes.USERNAME__USUARIOS], data[Constantes.PASSWD__USUARIOS],
                                 data[Constantes.EMAIL__USUARIOS], data[Constantes.IMAGE__USUARIOS], data[Constantes.ARRAY_ROLES]) == 0):
        respuesta = {'message': 'OK.'}
        resp = jsonify(respuesta)
        resp.status_code = 200
    else:
        respuesta = {'message': 'El usuario ya existe.'}
        resp = jsonify(respuesta)
        resp.status_code = 400
    return resp


@app.route("/usuario/<username>", methods=['GET'])
def getUsuario(username):
    lista = conexion.getUsuario(username)
    if len(lista) != 0:
        resp = jsonify(lista)
        resp.status_code = 200
    else:
        respuesta = {'message': 'No existe el usuario'}
        resp = jsonify(respuesta)
        resp.status_code = 400
    return resp


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
