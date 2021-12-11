import array
import ast
import re

import pymysql
from assistant import Constantes
from assistant.Constantes import *


class Conexion:
    def __init__(self, usuario, passwd, bd):
        self._usuario = usuario
        self._passwd = passwd
        self._bd = bd
        try:
            self._conexion = pymysql.connect(host='localhost', user=self._usuario, password=self._passwd, db=self._bd)
            self._conexion.close()
            print('Datos de conexion correctos')
        except(pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            print('Ocurrió un error con los datos de conexión: ', e)

    def getUsuarios(self):
        try:
            usuarios = []
            self.conectar()
            with self._conexion.cursor() as cursor:
                cursor.execute(f"SELECT * FROM {TABLA_USUARIOS}")
                r = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in
                     cursor.fetchall()]
                for u in r:
                    usuario = u
                    if usuario[Constantes.IMAGE__USUARIOS]:
                        image = usuario[Constantes.IMAGE__USUARIOS].decode('utf-8')
                        image = image.replace("[", "").replace("]", "").replace(" ", "").split(",")
                        usuario[Constantes.IMAGE__USUARIOS] = image
                    del cursor
                    cursor = self._conexion.cursor()
                    consulta = f"SELECT {Constantes.ID_ROL__USER_ROLES} FROM {Constantes.TABLA__USER_ROLES} WHERE {Constantes.ID_USER__USER_ROLES} in (SELECT {Constantes.USERNAME__USUARIOS} FROM {Constantes.TABLA_USUARIOS} WHERE {Constantes.USERNAME__USUARIOS} = %s)"
                    cursor.execute(consulta, usuario[USERNAME__USUARIOS])
                    r = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in
                         cursor.fetchall()]
                    if r:
                        roles = []
                        for rol in r:
                            roles.append(self.nombreRol(rol[Constantes.ID_ROL__USER_ROLES]))
                        usuario[Constantes.ARRAY_ROLES] = roles
                    usuarios.append(usuario)
                self.cerrarConexion()
                return usuarios
        except(pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            return []

    def conectar(self):
        try:
            self._conexion = pymysql.connect(host='localhost', user=self._usuario, password=self._passwd, db=self._bd)
        except(pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            return -1

    def cerrarConexion(self):
        self._conexion.close()

    def insertarUsuario(self, username, passwd, email, image, roles):
        try:
            self.conectar()
            cursor = self._conexion.cursor()
            if not image:
                consulta = f"INSERT INTO {TABLA_USUARIOS}({USERNAME__USUARIOS}," \
                           f"{PASSWD__USUARIOS},{EMAIL__USUARIOS}) " \
                           f"values ('{username}','{passwd}','{email}')"
            else:
                consulta = f"INSERT INTO {TABLA_USUARIOS}({USERNAME__USUARIOS}," \
                           f"{PASSWD__USUARIOS},{EMAIL__USUARIOS},{IMAGE__USUARIOS}) " \
                           f"values ('{username}','{passwd}','{email}','{image}')"
            cursor.execute(consulta)
            for rol in roles:
                id_rol = 0
                if rol == JEFE_DEPARTAMENTO:
                    id_rol = 1
                elif rol == ENCARGADO:
                    id_rol = 2
                elif rol == PROFESOR:
                    id_rol = 3
                consulta = f"INSERT INTO {TABLA__USER_ROLES}({ID_USER__USER_ROLES}," \
                           f"{ID_ROL__USER_ROLES}) " \
                           f"values ('{username}',{id_rol}) "
                cursor.execute(consulta)
            self._conexion.commit()
            self.cerrarConexion()
            return 0
        except pymysql.err.IntegrityError as e:
            return -1

    def insertarAula(self, nombre, descripcion, curso, encargado, num_alumnos):
        try:
            self.conectar()
            cursor = self._conexion.cursor()
            consulta = f"INSERT INTO {TABLA__AULAS}({NOMBRE__AULAS},{DESCRIPCION__AULAS}," \
                       f"{CURSO_IMPARTIDO__AULAS},{ENCARGADO__AULAS},{NUM_ALUMNOS__AULAS}) " \
                       f"values ('{nombre}','{descripcion}','{curso}','{encargado}',{num_alumnos})"
            cursor.execute(consulta)
            self._conexion.commit()
            self.cerrarConexion()
            return 0
        except pymysql.err.IntegrityError as e:
            return -1

    def getUsuario(self, username):
        try:
            self.conectar()
            cursor = self._conexion.cursor()
            cursor.execute(
                f"SELECT * FROM {Constantes.TABLA_USUARIOS} WHERE {Constantes.USERNAME__USUARIOS} = %s",
                username)
            r = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in cursor.fetchall()]
            if r:
                usuario = r[0]
                if usuario[Constantes.IMAGE__USUARIOS]:
                    image = usuario[Constantes.IMAGE__USUARIOS].decode('utf-8')
                    image = image.replace("[", "").replace("]", "").replace(" ", "").split(",")
                    usuario[Constantes.IMAGE__USUARIOS] = image
                del cursor
                cursor = self._conexion.cursor()
                consulta = f"SELECT {Constantes.ID_ROL__USER_ROLES} FROM {Constantes.TABLA__USER_ROLES} WHERE {Constantes.ID_USER__USER_ROLES} in (SELECT {Constantes.USERNAME__USUARIOS} FROM {Constantes.TABLA_USUARIOS} WHERE {Constantes.USERNAME__USUARIOS} = %s)"
                cursor.execute(consulta, username)
                r = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in
                     cursor.fetchall()]
                if r:
                    roles = []
                    for rol in r:
                        roles.append(self.nombreRol(rol[Constantes.ID_ROL__USER_ROLES]))
                    usuario[Constantes.ARRAY_ROLES] = roles
                self.cerrarConexion()
                return usuario
            else:
                self.cerrarConexion()
                return []
        except (pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            return []

    def nombreRol(self, id_rol):
        rol = ""
        if id_rol == 1:
            rol = Constantes.JEFE_DEPARTAMENTO
        elif id_rol == 2:
            rol = Constantes.ENCARGADO
        elif id_rol == 3:
            rol = Constantes.PROFESOR
        return rol
