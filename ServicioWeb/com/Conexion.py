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
                    consulta = f"SELECT {Constantes.ID_ROL__USER_ROLES} FROM {Constantes.TABLA__USER_ROLES} " \
                               f"WHERE {Constantes.ID_USER__USER_ROLES} = '{usuario[USERNAME__USUARIOS]}'"
                    cursor.execute(consulta)
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
                f"SELECT * FROM {Constantes.TABLA_USUARIOS} WHERE {Constantes.USERNAME__USUARIOS} = '{username}'")
            r = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in cursor.fetchall()]
            if r:
                usuario = r[0]
                if usuario[Constantes.IMAGE__USUARIOS]:
                    image = usuario[Constantes.IMAGE__USUARIOS].decode('utf-8')
                    image = image.replace("[", "").replace("]", "").replace(" ", "").split(",")
                    usuario[Constantes.IMAGE__USUARIOS] = image
                del cursor
                cursor = self._conexion.cursor()
                consulta = f"SELECT {Constantes.ID_ROL__USER_ROLES} FROM {Constantes.TABLA__USER_ROLES} " \
                           f"WHERE {Constantes.ID_USER__USER_ROLES} = '{username}'"
                cursor.execute(consulta)
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

    def getAulas(self):
        try:
            self.conectar()
            with self._conexion.cursor() as cursor:
                cursor.execute(f"SELECT * FROM {TABLA__AULAS}")
                r = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in
                     cursor.fetchall()]
                self.cerrarConexion()
                return r
        except(pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            self.cerrarConexion()
            return []

    def getAulasEncargado(self, encargado):
        try:
            self.conectar()
            with self._conexion.cursor() as cursor:
                consulta = f"SELECT * FROM {TABLA__AULAS} where {ENCARGADO__AULAS} = '{encargado}'"
                cursor.execute(consulta)
                r = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in
                     cursor.fetchall()]
                self.cerrarConexion()
                return r
        except(pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            return []

    def modificarUsuario(self, username, newUsername, passwd, email, roles, img):
        try:
            self.conectar()
            cursor = self._conexion.cursor()
            if not img:
                consulta = f"UPDATE {TABLA_USUARIOS} SET {USERNAME__USUARIOS} = '{newUsername}', " \
                           f"{EMAIL__USUARIOS} = '{email}', {PASSWD__USUARIOS} = '{passwd}' " \
                           f"WHERE {USERNAME__USUARIOS} = '{username}'"
            else:
                consulta = f"UPDATE {TABLA_USUARIOS} SET {USERNAME__USUARIOS} = '{newUsername}', " \
                           f"{EMAIL__USUARIOS} = '{email}', {PASSWD__USUARIOS} = '{passwd}', " \
                           f"{IMAGE__USUARIOS} = '{img}'  WHERE {USERNAME__USUARIOS} = '{username}'"
            cursor.execute(consulta)
            consulta = f"DELETE FROM {TABLA__USER_ROLES} WHERE {ID_USER__USER_ROLES} = '{newUsername}'"
            cursor.execute(consulta)
            for rol in roles:
                id_rol = 0
                if rol == JEFE_DEPARTAMENTO:
                    id_rol = 1
                elif rol == ENCARGADO:
                    id_rol = 2
                elif rol == PROFESOR:
                    id_rol = 3
                consulta = f"INSERT INTO {TABLA__USER_ROLES}({ID_USER__USER_ROLES}, {ID_ROL__USER_ROLES}) " \
                           f"values ('{newUsername}',{id_rol}) "
                cursor.execute(consulta)
            self._conexion.commit()
            self.cerrarConexion()
            return cursor.rowcount
        except (pymysql.err.IntegrityError) as e:
            return -1

    def modificarAula(self, nombre, new_nombre, desc, curso, encargado, num_alumnos):
        try:
            self.conectar()
            cursor = self._conexion.cursor()
            consulta = f"UPDATE {TABLA__AULAS} SET {NOMBRE__AULAS} = '{new_nombre}', " \
                       f"{DESCRIPCION__AULAS} = '{desc}', {CURSO_IMPARTIDO__AULAS} = '{curso}', " \
                       f"{ENCARGADO__AULAS} = '{encargado}', {NUM_ALUMNOS__AULAS} = {num_alumnos} " \
                       f"WHERE {NOMBRE__AULAS} = '{nombre}'"
            cursor.execute(consulta)
            self._conexion.commit()
            self.cerrarConexion()
            return cursor.rowcount
        except (pymysql.err.IntegrityError) as e:
            return -1

    def borrarAula(self, nombre):
        try:
            self.conectar()
            with self._conexion.cursor() as cursor:
                consulta = f"DELETE FROM {TABLA__AULAS} WHERE {NOMBRE__AULAS} = '{nombre}'"
                cursor.execute(consulta)
                self._conexion.commit()
                self.cerrarConexion()
                return cursor.rowcount
        except (pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            return -1

    def borrarUsuario(self, username):
        try:
            self.conectar()
            with self._conexion.cursor() as cursor:
                consulta = f"DELETE FROM {TABLA_USUARIOS} WHERE {USERNAME__USUARIOS} = '{username}'"
                cursor.execute(consulta)
                self._conexion.commit()
                self.cerrarConexion()
                return cursor.rowcount
        except (pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            return -1

    def insertarDispositivo(self, id, nombre, aula):
        try:
            self.conectar()
            cursor = self._conexion.cursor()
            consulta = f"INSERT INTO {TABLA__DISPOSITIVOS}({ID__DISPOSITIVOS},{NOMBRE__DISPOSITIVOS}," \
                       f"{AULA__DISPOSITIVOS}) values ('{id}','{nombre}','{aula}')"
            cursor.execute(consulta)
            self._conexion.commit()
            self.cerrarConexion()
            return 0
        except pymysql.err.IntegrityError as e:
            return -1

    def getDevices(self):
        try:
            self.conectar()
            with self._conexion.cursor() as cursor:
                consulta = f"SELECT * FROM {TABLA__DISPOSITIVOS}"
                cursor.execute(consulta)
                r = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in
                     cursor.fetchall()]
                self.cerrarConexion()
                return r
        except(pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            return []

    def getDevicesByAula(self, aula):
        try:
            self.conectar()
            with self._conexion.cursor() as cursor:
                consulta = f"SELECT * FROM {TABLA__DISPOSITIVOS} WHERE {AULA__DISPOSITIVOS} = '{aula}'"
                cursor.execute(consulta)
                r = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in
                     cursor.fetchall()]
                self.cerrarConexion()
                return r
        except(pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            return []

    def modificarDispositivo(self, id, new_id, nombre, aula):
        try:
            self.conectar()
            cursor = self._conexion.cursor()
            consulta = f"UPDATE {TABLA__DISPOSITIVOS} SET {ID__DISPOSITIVOS} = '{new_id}', " \
                       f"{NOMBRE__DISPOSITIVOS} = '{nombre}', {AULA__DISPOSITIVOS} = '{aula}' " \
                       f"WHERE {ID__DISPOSITIVOS} = '{id}'"
            cursor.execute(consulta)
            self._conexion.commit()
            self.cerrarConexion()
            return cursor.rowcount
        except (pymysql.err.IntegrityError) as e:
            return -1

    def borrarDispositivo(self, id):
        try:
            self.conectar()
            with self._conexion.cursor() as cursor:
                consulta = f"DELETE FROM {TABLA__DISPOSITIVOS} WHERE {ID__DISPOSITIVOS} = '{id}'"
                cursor.execute(consulta)
                self._conexion.commit()
                self.cerrarConexion()
                return cursor.rowcount
        except (pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            return -1

    def getEncargados(self):
        try:
            self.conectar()
            with self._conexion.cursor() as cursor:
                consulta = f"SELECT {ID_USER__USER_ROLES} FROM {TABLA__USER_ROLES} WHERE {ID_ROL__USER_ROLES} = {ID_ENCARGADO}"
                cursor.execute(consulta)
                r = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in
                     cursor.fetchall()]
                self.cerrarConexion()
                return r
        except(pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            return []
