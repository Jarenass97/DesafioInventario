from flask import Flask, request, jsonify
from flask_restful import Resource, Api
from Conexion import Conexion

app = Flask(__name__)
api = Api(app)

conexion = Conexion('root', '', 'inventario')

@app.route('/')
def hello():
    return 'Bienvenido a la Api de mi inventario\n"/usuarios" muestra usuarios'
if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
