import pymysql
from assistant import Constantes


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
            self.conectar()
            with self._conexion.cursor() as cursor:
                cursor.execute(f"SELECT * FROM {Constantes.TABLA_USUARIOS}")
                r = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in
                     cursor.fetchall()]
                self.cerrarConexion()
                return r
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
            consulta = f"INSERT INTO {Constantes.TABLA_USUARIOS}({Constantes.USERNAME__USUARIOS}," \
                       f"{Constantes.PASSWD__USUARIOS},{Constantes.EMAIL__USUARIOS},{Constantes.IMAGE__USUARIOS}) " \
                       f"values (%s,%s,%s,%s) "
            cursor.execute(consulta, (username, passwd, email, image))
            for rol in roles:
                consulta = f"INSERT INTO {Constantes.TABLA__USER_ROLES}({Constantes.ID_USER__USER_ROLES}," \
                           f"{Constantes.ID_ROL__USER_ROLES}) " \
                           f"values (%s,%s) "
                cursor.execute(consulta, (username, rol))
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
                del cursor
                cursor = self._conexion.cursor()
                consulta = f"SELECT {Constantes.ID_ROL__USER_ROLES} FROM {Constantes.TABLA__USER_ROLES} WHERE {Constantes.ID_USER__USER_ROLES} in (SELECT {Constantes.USERNAME__USUARIOS} FROM {Constantes.TABLA_USUARIOS} WHERE {Constantes.USERNAME__USUARIOS} = %s)"
                cursor.execute(consulta, username)
                r = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in
                     cursor.fetchall()]
                if r:
                    roles = []
                    for rol in r:
                        roles.append(rol[Constantes.ID_ROL__USER_ROLES])
                    usuario[Constantes.ARRAY_ROLES] = roles
                self.cerrarConexion()
                return usuario
            else:
                self.cerrarConexion()
                return []
        except (pymysql.err.OperationalError, pymysql.err.InternalError) as e:
            return []
