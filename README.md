### Features

- En la carpeta DB se encuentra un docker para ejecutar facilmente un pgamin + postgress: docker compose up -d
- En la carpeta login se encuentra el backend y frontend para el login con JWT
- En la carpeta Spring se encuentran los 2 ultimos ejercicios
- En el apartado de este readme de bases de datos se encuentra los ejercicios asociados a las bases de datos

# Readme


Inicializar e instalar django
=============
Asegúrate de tener Python y Django instalados en tu sistema. Si no los 
tienes, puedes descargar Python desde https://www.python.org/downloads/ y 
luego instalar Django usando pip en la línea de comandos con el comando: 
pip install Django.

    django-admin startproject <nombre_del_proyecto>

Reemplaza <nombre_del_proyecto> con el nombre que deseas darle a tu 
proyecto.

Deberá instalar djangorestframework_simplejwt

    pip install djangorestframework_simplejwt

Y agregar al siguiente configuración el settings.py del proyecto, esto 
para que el proyecto use JWT como metodo de autenticación

    REST_FRAMEWORK = {
        'DEFAULT_AUTHENTICATION_CLASSES': [
            'rest_framework_simplejwt.authentication.JWTAuthentication',
        ],
    }


se deberán definir las rutas a usar, en este caso se usara api/token y 
api/token/refresh



    
	from rest_framework_simplejwt import views as jwt_views
	
    urlpatterns = [
        path('api/token/', jwt_views.TokenObtainPairView.as_view(), 
name='token_obtain_pair'),
        path('api/token/refresh/', jwt_views.TokenRefreshView.as_view(), 
name='token_refresh'),
    ]

api/token cumplirá la función de retornarnos un token valido con x 
duración de tiempo que se definirá más adelante.

api/token/refresh será nuestro endpoint para actualizar el token siempre 
que este termine su tiempo util.

Agregar la siguiente configuración la settings.py para que Django empiece 
a usar djangorestframework_simplejwt y le definamos el tiempo de duración 
que queremos para cada token 

    INSTALLED_APPS = [
        #otras aplicaciones
        'rest_framework_simplejwt',
    ]
    
    SIMPLE_JWT = {
        'ACCESS_TOKEN_LIFETIME': timedelta(minutes=15),
        'REFRESH_TOKEN_LIFETIME': timedelta(days=1),
    }

Finalmente se deberá ver algo asi

    REST_FRAMEWORK = {
        'DEFAULT_AUTHENTICATION_CLASSES': [
            'rest_framework_simplejwt.authentication.JWTAuthentication',
        ],
    }
    
    INSTALLED_APPS = [
        'django.contrib.admin',
        'django.contrib.auth',
        'django.contrib.contenttypes',
        'django.contrib.sessions',
        'django.contrib.messages',
        'django.contrib.staticfiles',
        'rest_framework',
        'rest_framework_simplejwt',
    ]
    
    SIMPLE_JWT = {
        'ACCESS_TOKEN_LIFETIME': timedelta(minutes=15),
        'REFRESH_TOKEN_LIFETIME': timedelta(days=1),
    }

para efectos del ejercicio usaremos la base de datos sqlite que nos 
proporciona por defecto la configuración de django, por lo que solamente 
ejecutaremos las migraciones.

    #asegurese de apagar primero el servidor (CTRL + C)
	python manage.py makemigrations
    python manage.py migrate

finalmente crearemos un superusuario y probaremos el servicio

    python manage.py createsuperuser

Se ejecutan los pasos mencionados por el servicio de creacion de usuario.

una vez finalizado ejecutamos nuevamente el proyecto y desde postman o 
alguna herramienta para lanzar peticiones HTTP ejecutamos la siguiente:

    # URL: localhost:8000/api/token
    #METHOD POST
    #RAW DATA (JSON)
    
    {
        "username": "tu usuario",
        "password": "tu contraseña"
    }

Deberá regresar un status 200 con nuestro token de la sigueinte forma: 

    {
        "refresh": 
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoicmVmcmVzaCIsImV4cCI6MTY4Mjg2OTA5NywiaWF0IjoxNjgyNzgyNjk3LCJqdGkiOiJlODMxNDc1NWQ4ZmE0MjZkOTU5MGFhMmEwMDNkZTM5NyIsInVzZXJfaWQiOjF9.Mejyn0mkttO5NrdMrBeAlYaRR_Fl4GJ6BNBpC7QiN28",
        "access": 
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjgyNzgzNTk3LCJpYXQiOjE2ODI3ODI2OTcsImp0aSI6ImQxZGFlZjIyYjAzYTRhNWRiYTIzYjI0MjJiZTIzM2VkIiwidXNlcl9pZCI6MX0.zbQLYFeeaknLHCN1hhl6gTS9ipR38ItllQej2Cc98d4"
    }

access va a ser el token con el que podemos realizar peticiones durante la 
aplicación, esté tendrá la carga util para el uso de la aplicación, en él 
se puede incrustrar información como el nombre de usuario o datos 
importantes que requieran ser protegidos. 

Dado que nuestro token contienel ID lo usaremos para traer nuestro nombre 
de usuario con un servicio get, creamos una clase views.py donde estara 
nuestra logica del usuario, heredamos de las clases viewsets.Viewset y 
escribimos nuestro metodo list, la herencia de esta clase usa los metodos 
http mas y los traduce al funcionamiento de lso metodos: ejemplo list para 
GET, create para Post, update para PATCH

y utilizamosa el decordar permmission_classes para especificar que este 
metodo requiere autenticación.

    
    from backend.serializer import UserSerializer
    from django.contrib.auth.models import User
    from django.shortcuts import get_object_or_404
    from rest_framework import viewsets
    from rest_framework.response import Response
    from rest_framework.permissions import IsAuthenticated
    from rest_framework.decorators import api_view, permission_classes
    
    
    class UserViewSet(viewsets.ViewSet):
        """
        A simple ViewSet for listing or retrieving users.
        """
        @permission_classes((IsAuthenticated, ))
        def list(self, request):
            uid = request.user.id
            queryset = User.objects.filter(pk=uid)
          
            serializer_context = {
                'request': request,
            }
            serializer = UserSerializer(queryset, 
context=serializer_context, many=True)
            return Response(serializer.data)

finalmente probamos en postman el resultado y nos trae solo el usuario al 
que corresponde este token activo.

# Inicializar proyecto REACT

Para crear un proyecto REACT puedes seguir los siguientes pasos:

1. Asegúrate de tener Node.js y npm instalados en tu sistema. Puedes 
descargar Node.js desde https://nodejs.org y npm se instala 
automáticamente junto con Node.js
2. Abre una línea de comandos y navega hasta el directorio donde deseas 
crear tu proyecto, este caso se llamara frontend
3. Instala el paquete vue-cli de forma global utilizando el siguiente 
comando:

    npm i react

4. Crea un nuevo proyecto Vue.js utilizando el comando vue create 
<nombre_del_proyecto>. Reemplaza <nombre_del_proyecto> con el nombre que 
deseas darle a tu proyectom, en este caso login con REACT

5. finalmente accedenemos a la carpeta del proyecto y ejecutamos 

    npm run serve

Nuestro sitio base estará corriendo en localhost:3000

1. Crea un nuevo archivo llamado Login.vue en la carpeta src/views de tu 
proyecto REACT.js.

2. En el archivo LoginPage.vue, agrega el siguiente código

    import React, { useState } from 'react';
    import { withRouter } from 'react-router-dom';
    
    import '../../App.css';
    
    
    function SignInPage(props) { // Aquí se pasa props como parámetro
      const [email, setEmail] = useState('');
      const [password, setPassword] = useState('');
    
      const handleSubmit = async (e) => {
        e.preventDefault();
    
        const response = await fetch('http://localhost:8000/api/token/', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ username: email, password: password }),
        });
        const data = await response.json();
        console.log("data: ", data)
    
        if (response.ok) {
          localStorage.setItem('token', data.access);
          props.history.push("/home")
        } else {
          console.log('Login failed');
          alert(data.detail)
        }
      };
	   return (
	   ### Features

- En la carpeta DB se encuentra un docker para ejecutar facilmente un pgamin + postgress: docker compose up -d
- En la carpeta login se encuentra el backend y frontend para el login con JWT
- En la carpeta Spring se encuentran los 2 ultimos ejercicios
- En el apartado de este readme de bases de datos se encuentra los ejercicios asociados a las bases de datos

# Readme


Inicializar e instalar django
=============
Asegúrate de tener Python y Django instalados en tu sistema. Si no los 
tienes, puedes descargar Python desde https://www.python.org/downloads/ y 
luego instalar Django usando pip en la línea de comandos con el comando: 
pip install Django.

    django-admin startproject <nombre_del_proyecto>

Reemplaza <nombre_del_proyecto> con el nombre que deseas darle a tu 
proyecto.

Deberá instalar djangorestframework_simplejwt

    pip install djangorestframework_simplejwt

Y agregar al siguiente configuración el settings.py del proyecto, esto 
para que el proyecto use JWT como metodo de autenticación

    REST_FRAMEWORK = {
        'DEFAULT_AUTHENTICATION_CLASSES': [
            'rest_framework_simplejwt.authentication.JWTAuthentication',
        ],
    }


se deberán definir las rutas a usar, en este caso se usara api/token y 
api/token/refresh



    
	from rest_framework_simplejwt import views as jwt_views
	
    urlpatterns = [
        path('api/token/', jwt_views.TokenObtainPairView.as_view(), 
name='token_obtain_pair'),
        path('api/token/refresh/', jwt_views.TokenRefreshView.as_view(), 
name='token_refresh'),
    ]

api/token cumplirá la función de retornarnos un token valido con x 
duración de tiempo que se definirá más adelante.

api/token/refresh será nuestro endpoint para actualizar el token siempre 
que este termine su tiempo util.

Agregar la siguiente configuración la settings.py para que Django empiece 
a usar djangorestframework_simplejwt y le definamos el tiempo de duración 
que queremos para cada token 

    INSTALLED_APPS = [
        #otras aplicaciones
        'rest_framework_simplejwt',
    ]
    
    SIMPLE_JWT = {
        'ACCESS_TOKEN_LIFETIME': timedelta(minutes=15),
        'REFRESH_TOKEN_LIFETIME': timedelta(days=1),
    }

Finalmente se deberá ver algo asi

    REST_FRAMEWORK = {
        'DEFAULT_AUTHENTICATION_CLASSES': [
            'rest_framework_simplejwt.authentication.JWTAuthentication',
        ],
    }
    
    INSTALLED_APPS = [
        'django.contrib.admin',
        'django.contrib.auth',
        'django.contrib.contenttypes',
        'django.contrib.sessions',
        'django.contrib.messages',
        'django.contrib.staticfiles',
        'rest_framework',
        'rest_framework_simplejwt',
    ]
    
    SIMPLE_JWT = {
        'ACCESS_TOKEN_LIFETIME': timedelta(minutes=15),
        'REFRESH_TOKEN_LIFETIME': timedelta(days=1),
    }

para efectos del ejercicio usaremos la base de datos sqlite que nos 
proporciona por defecto la configuración de django, por lo que solamente 
ejecutaremos las migraciones.

    #asegurese de apagar primero el servidor (CTRL + C)
	python manage.py makemigrations
    python manage.py migrate

finalmente crearemos un superusuario y probaremos el servicio

    python manage.py createsuperuser

Se ejecutan los pasos mencionados por el servicio de creacion de usuario.

una vez finalizado ejecutamos nuevamente el proyecto y desde postman o 
alguna herramienta para lanzar peticiones HTTP ejecutamos la siguiente:

    # URL: localhost:8000/api/token
    #METHOD POST
    #RAW DATA (JSON)
    
    {
        "username": "tu usuario",
        "password": "tu contraseña"
    }

Deberá regresar un status 200 con nuestro token de la sigueinte forma: 

    {
        "refresh": 
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoicmVmcmVzaCIsImV4cCI6MTY4Mjg2OTA5NywiaWF0IjoxNjgyNzgyNjk3LCJqdGkiOiJlODMxNDc1NWQ4ZmE0MjZkOTU5MGFhMmEwMDNkZTM5NyIsInVzZXJfaWQiOjF9.Mejyn0mkttO5NrdMrBeAlYaRR_Fl4GJ6BNBpC7QiN28",
        "access": 
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjgyNzgzNTk3LCJpYXQiOjE2ODI3ODI2OTcsImp0aSI6ImQxZGFlZjIyYjAzYTRhNWRiYTIzYjI0MjJiZTIzM2VkIiwidXNlcl9pZCI6MX0.zbQLYFeeaknLHCN1hhl6gTS9ipR38ItllQej2Cc98d4"
    }

access va a ser el token con el que podemos realizar peticiones durante la 
aplicación, esté tendrá la carga util para el uso de la aplicación, en él 
se puede incrustrar información como el nombre de usuario o datos 
importantes que requieran ser protegidos. 

Dado que nuestro token contienel ID lo usaremos para traer nuestro nombre 
de usuario con un servicio get, creamos una clase views.py donde estara 
nuestra logica del usuario, heredamos de las clases viewsets.Viewset y 
escribimos nuestro metodo list, la herencia de esta clase usa los metodos 
http mas y los traduce al funcionamiento de lso metodos: ejemplo list para 
GET, create para Post, update para PATCH

y utilizamosa el decordar permmission_classes para especificar que este 
metodo requiere autenticación.

    
    from backend.serializer import UserSerializer
    from django.contrib.auth.models import User
    from django.shortcuts import get_object_or_404
    from rest_framework import viewsets
    from rest_framework.response import Response
    from rest_framework.permissions import IsAuthenticated
    from rest_framework.decorators import api_view, permission_classes
    
    
    class UserViewSet(viewsets.ViewSet):
        """
        A simple ViewSet for listing or retrieving users.
        """
        @permission_classes((IsAuthenticated, ))
        def list(self, request):
            uid = request.user.id
            queryset = User.objects.filter(pk=uid)
          
            serializer_context = {
                'request': request,
            }
            serializer = UserSerializer(queryset, 
context=serializer_context, many=True)
            return Response(serializer.data)

finalmente probamos en postman el resultado y nos trae solo el usuario al 
que corresponde este token activo.

# Inicializar proyecto REACT

Para crear un proyecto REACT puedes seguir los siguientes pasos:

1. Asegúrate de tener Node.js y npm instalados en tu sistema. Puedes 
descargar Node.js desde https://nodejs.org y npm se instala 
automáticamente junto con Node.js
2. Abre una línea de comandos y navega hasta el directorio donde deseas 
crear tu proyecto, este caso se llamara frontend
3. Instala el paquete vue-cli de forma global utilizando el siguiente 
comando:

    npm i react

4. Crea un nuevo proyecto Vue.js utilizando el comando vue create 
<nombre_del_proyecto>. Reemplaza <nombre_del_proyecto> con el nombre que 
deseas darle a tu proyectom, en este caso login con REACT

5. finalmente accedenemos a la carpeta del proyecto y ejecutamos 

    npm run serve

Nuestro sitio base estará corriendo en localhost:3000

1. Crea un nuevo archivo llamado Login.vue en la carpeta src/views de tu 
proyecto REACT.js.

2. En el archivo LoginPage.vue, agrega el siguiente código

    import React, { useState } from 'react';
    import { withRouter } from 'react-router-dom';
    
    import '../../App.css';
    
    
    function SignInPage(props) { // Aquí se pasa props como parámetro
      const [email, setEmail] = useState('');
      const [password, setPassword] = useState('');
    
      const handleSubmit = async (e) => {
        e.preventDefault();
    
        const response = await fetch('http://localhost:8000/api/token/', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ username: email, password: password }),
        });
        const data = await response.json();
        console.log("data: ", data)
    
        if (response.ok) {
          localStorage.setItem('token', data.access);
          props.history.push("/home")
        } else {
          console.log('Login failed');
          alert(data.detail)
        }
      };
	   return (
        <div className="text-center m-5-auto">
          <h2>Sign in to us</h2>
          <form onSubmit={handleSubmit}>
            <p>
              <label>Username or email address</label><br />
              <input type="text" name="email" value={email} onChange={(e) 
=> setEmail(e.target.value)} required />
            </p>
            <p>
              <label>Password</label>
              <br />
              <input type="password" name="password" value={password} 
onChange={(e) => setPassword(e.target.value)} required />
            </p>
            <p>
              <button id="sub_btn" type="submit">Login</button>
            </p>
          </form>
        </div>
      );
    }
    export default withRouter(SignInPage);
	  
	  
	  


La logica principal ocurre en la promesa asincrona donde se le solicta el 
token al backend, este lo guarda en el local storage, de esta forma cada 
petición que se realice validará con el token del lado del servidor si 
tiene permiso o no de usar lo servicios.

# Base de datos y scripts

Se crea el siguiente script para efectos del ejercicio:

    CREATE TABLE provincias (
      idprovincia INT PRIMARY KEY,
      descripcion VARCHAR(50) NOT NULL
    );
    
    -- Insertar datos en la tabla provincias
    INSERT INTO provincias (idprovincia, descripcion) VALUES
      (1, 'Zaragoza'),
      (2, 'Huesca'),
      (3, 'Teruel');
    
    -- Crear tabla productos
    CREATE TABLE productos (
      idproducto CHAR(1) PRIMARY KEY,
      descripcion VARCHAR(50) NOT NULL,
      precio DECIMAL(5,2) NOT NULL
    );
    
    -- Insertar datos en la tabla productos
    INSERT INTO productos (idproducto, descripcion, precio) VALUES
      ('A', 'Playmobil', 5.00),
      ('B', 'Puzzle', 10.25),
      ('C', 'Peonza', 3.65);
      
      
     -- Crear tabla clientes
    CREATE TABLE clientes (
      idcliente INT PRIMARY KEY,
      nombre VARCHAR(50) NOT NULL,
      idprovincia INT NOT NULL,
      FOREIGN KEY (idprovincia) REFERENCES provincias(idprovincia)
    );
    
    -- Insertar datos en la tabla clientes
    INSERT INTO clientes (idcliente, nombre, idprovincia) VALUES
      (1, 'Juan Palomo', 1),
      (2, 'Armando Ruido', 2),
      (3, 'Carmelo Cotón', 1),
      (4, 'Dolores Fuertes', 3),
      (5, 'Alberto Mate', 3);
    
    
    -- Crear tabla compras
    CREATE TABLE compras (
      idcompra INT PRIMARY KEY,
      idcliente INT NOT NULL,
      idproducto CHAR(1) NOT NULL,
      fecha DATE NOT NULL,
      FOREIGN KEY (idcliente) REFERENCES clientes(idcliente),
      FOREIGN KEY (idproducto) REFERENCES productos(idproducto)
    );
    
    -- Insertar datos en la tabla compras
    INSERT INTO compras (idcompra, idcliente, idproducto, fecha) VALUES
      (1, 1, 'C', '2022-01-01'),
      (2, 2, 'B', '2022-01-15'),
      (3, 2, 'C', '2022-01-22'),
      (4, 4, 'A', '2022-02-03'),
      (5, 3, 'A', '2022-02-05'),
      (6, 1, 'B', '2022-02-16'),
      (7, 1, 'B', '2022-02-21'),
      (8, 4, 'A', '2022-02-21'),
      (9, 5, 'C', '2022-03-01'),
      (10, 3, 'A', '2022-03-01'),
      (11, 3, 'C', '2022-03-05'),
      (12, 2, 'B', '2022-03-07'),
      (13, 2, 'B', '2022-03-11'),
      (14, 1, 'A', '2022-03-18'),
      (15, 1, 'C', '2022-03-29'),
      (16, 5, 'B', '2022-04-08'),
      (17, 5, 'C', '2022-04-09'),
      (18, 4, 'C', '2022-04-09'),
      (19, 1, 'A', '2022-04-12'),
      (20, 2, 'A', '2022-04-19');

y las consultas son las siguientes

    -- Todas las compras detalladas con los datos del cliente, de los 
productos y de cada una de las
    -- ventas (código de cliente, nombre de cliente, nombre de provincia, 
producto, importe, fecha de la venta)
    
    select cl.idcliente, cl.nombre, pr.descripcion, 
productos.descripcion,productos.precio,compras.fecha  
    from provincias pr
    inner join clientes cl on pr.idprovincia = cl.idprovincia
    inner join compras ON compras.idcliente = cl.idcliente
    inner join provincias ON provincias.idprovincia = cl.idprovincia
    inner join productos ON productos.idproducto = compras.idproducto
    order by cl.nombre;
    
    -- Las compras detalladas de los clientes de Teruel.
    
    select cl.idcliente, cl.nombre, pr.descripcion, 
productos.descripcion,productos.precio,compras.fecha  from provincias pr
    inner join clientes cl on pr.idprovincia = cl.idprovincia
    inner join compras ON compras.idcliente = cl.idcliente
    inner join provincias ON provincias.idprovincia = cl.idprovincia
    inner join productos ON productos.idproducto = compras.idproducto
    where pr.descripcion like 'Teruel';
    
    --Las compras detalladas de los clientes de Huesca y Zaragoza en el 
primer trimestre de 2015
    
    select cl.idcliente, cl.nombre, pr.descripcion, 
productos.descripcion,productos.precio,compras.fecha  from provincias pr
    inner join clientes cl on pr.idprovincia = cl.idprovincia
    inner join compras  ON compras.idcliente = cl.idcliente
    inner join provincias ON provincias.idprovincia = cl.idprovincia
    inner join productos ON productos.idproducto = compras.idproducto
    where pr.descripcion in ('Huesca', 'Zaragoza')
    and compras.fecha BETWEEN '2022-01-01' and '2022-03-31'
    order by compras.fecha
    
    
    --Las compras agrupadas por producto de todos los clientes mostrando 
el número de compras y el
    --importe total para cada producto por cada uno de los clientes 
(código de cliente, provincia,
    --producto, número de ventas, importe total)
    
    select cl.idcliente, provincias.descripcion, productos.descripcion, 
count(cl.idcliente) as numero_ventas, sum(productos.precio) as 
importe_total from provincias pr
    inner join clientes cl on pr.idprovincia = cl.idprovincia
    inner join compras  ON compras.idcliente = cl.idcliente
    inner join provincias ON provincias.idprovincia = cl.idprovincia
    inner join productos ON productos.idproducto = compras.idproducto
    group by (cl.idcliente, provincias.descripcion, productos.descripcion, 
productos.precio)
    
    
    --Número de peonzas totales que se han comprado en el mes de marzo por 
los clientes de
    --Zaragoza (número de peonzas e importe total)
    
    select count(cl.idprovincia) as numero_de_peonzas, 
sum(productos.precio) as importe_total from provincias pr
    inner join clientes cl on pr.idprovincia = cl.idprovincia
    inner join compras  ON compras.idcliente = cl.idcliente
    inner join provincias ON provincias.idprovincia = cl.idprovincia
    inner join productos ON productos.idproducto = compras.idproducto
    where pr.descripcion in ('Zaragoza') and productos.descripcion in 
('Peonza')
    and compras.fecha BETWEEN '2022-03-01' and '2022-03-31'
    group by (cl.idcliente, productos.precio)
    
    --Las compras realizadas por todos los clientes agrupadas por mes 
    --(código de cliente, nombre,provincia, mes, número de compras, 
importe total)
    
    select total_ventas.idcliente, clientes.nombre, 
provincias2.descripcion, total_ventas.cantidad_ventas from
    (select ventas.idcliente, count(ventas.idcliente) as cantidad_ventas 
from 
    (select compras.idcliente, DATE_TRUNC('month',compras.fecha) as mes , 
sum(pr.precio) as importe from compras 
     inner join productos pr ON pr.idproducto = compras.idproducto
    where DATE_TRUNC('month',compras.fecha) 
    in 
    (select DATE_TRUNC('month',compras.fecha) as dd from compras group by 
dd)
    group by (compras.idcliente, pr.precio, compras.fecha)) as ventas
    group by (ventas.idcliente)) total_ventas
    inner join clientes ON clientes.idcliente = total_ventas.idcliente
    inner join provincias provincias2 ON provincias2.idprovincia = 
clientes.idprovincia
    

# BASE DE DATOS DON PEPE MUELAS,

SCRIPT GENERADO PARA LA BASE DE DATOS SOLICTADA POR DON PEPE MUELAS

Entidades:

Grabación (Título, Categoría musical, Número de temas, Descripción)
Formato (Nombre, Estado de conservación)
Intérprete (Nombre, Descripción)
Compañía discográfica (Identificador de compañía, Nombre, Dirección)
Relaciones:

La entidad Grabación se relaciona con la entidad Formato mediante la 
relación Grabación-Formato, la cual es de tipo 1:N, es decir, una 
grabación puede tener varios formatos, pero un formato sólo puede 
pertenecer a una grabación.
La entidad Grabación se relaciona con la entidad Intérprete mediante la 
relación Grabación-Intérprete, la cual es de tipo N:M, es decir, una 
grabación puede tener varios intérpretes, y un intérprete puede participar 
en varias grabaciones. Esta relación se descompone en dos relaciones: 
Grabación-Intérprete-Participación y Intérprete-Participación-Grabación, 
ambas de tipo 1:N.
La entidad Grabación se relaciona con la entidad Compañía discográfica 
mediante la relación Grabación-Compañía discográfica, la cual es de tipo 
N:1, es decir, varias grabaciones pueden ser editadas por la misma 
compañía discográfica, pero una grabación sólo puede ser editada por una 
compañía discográfica.

este modelo se creo para postgresql

    CREATE TABLE Grabacion (
      id SERIAL PRIMARY KEY,
      Titulo VARCHAR(255),
      CategoriaMusical VARCHAR(255),
      NumTemas INT,
      Descripcion TEXT
    );
    
    CREATE TABLE Formato (
      id SERIAL PRIMARY KEY,
      Nombre VARCHAR(255),
      EstadoConservacion VARCHAR(255)
    );
    
    
    CREATE TABLE Interprete (
      id SERIAL PRIMARY KEY,
      Nombre VARCHAR(255),
      Descripcion TEXT
    );
    
    
    CREATE TABLE CompaniaDiscografica (
      id INT PRIMARY KEY,
      Nombre VARCHAR(255),
      Direccion TEXT
    );
    
    CREATE TABLE GrabacionFormato (
      id_grabacion INT,
      id_formato INT,
      FOREIGN KEY (id_grabacion) REFERENCES Grabacion(id),
      FOREIGN KEY (id_formato) REFERENCES Formato(id)
    );
    
    CREATE TABLE InterpreteParticipacion (
      id_interprete INT,
      id_grabacion INT,
      FechaParticipacion DATE,
      FOREIGN KEY (id_interprete) REFERENCES Interprete(id),
      FOREIGN KEY (id_grabacion) REFERENCES Grabacion(id)
    );
    
    CREATE TABLE GrabacionCompaniaDiscografica (
      id_grabacion INT,
      id_compania INT,
      FOREIGN KEY (id_grabacion) REFERENCES Grabacion(id),
      FOREIGN KEY (id_compania) REFERENCES CompaniaDiscografica(id)
    );

    

# Realizar la implementación de una clase Controller de un api Rest

Realizar la implementación de una clase Controller de un api Rest que 
incluya un endpoint
“/api/usuarios” tipo “get” para listar los usuarios de una tabla USUARIOS 
de una bd, incluyendo la capa
de servicio con el acceso a la bd para obtener el select de usuarios 
mediante jdbctemplate.

Se crea un proyecto springboot con las siguientes dependencias 

    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
https://maven.apache.org/xsd/maven-4.0.0.xsd">
    	<modelVersion>4.0.0</modelVersion>
    	<parent>
    		<groupId>org.springframework.boot</groupId>
    		<artifactId>spring-boot-starter-parent</artifactId>
    		<version>2.5.4</version>
    		<relativePath/> <!-- lookup parent from repository -->
    	</parent>
    	<groupId>com.bezkoder</groupId>
    	<artifactId>spring-boot-jdbctemplate-crud-example</artifactId>
    	<version>0.0.1-SNAPSHOT</version>
    	<name>spring-boot-jdbctemplate-crud-example</name>
    	<description>Spring Boot JDBCTemplate CRUD example with H2 
Database - Rest API</description>
    	<properties>
    		<java.version>1.8</java.version>
    	</properties>
    	<dependencies>
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			
<artifactId>spring-boot-starter-data-jdbc</artifactId>
    		</dependency>
    		
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-web</artifactId>
    		</dependency>
    
    		<dependency>
    			<groupId>com.h2database</groupId>
    			<artifactId>h2</artifactId>
    			<scope>runtime</scope>
    		</dependency>
    		
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-test</artifactId>
    			<scope>test</scope>
    		</dependency>
    		<dependency>
    			<groupId>mysql</groupId>
    			<artifactId>mysql-connector-java</artifactId>
    			<version>8.0.14</version>
    		</dependency>
    		<dependency>
    			<groupId>org.postgresql</groupId>
    			<artifactId>postgresql</artifactId>
    			<version>42.2.10</version>
    		</dependency>
    	</dependencies>
    
    	<build>
    		<plugins>
    			<plugin>
    				
<groupId>org.springframework.boot</groupId>
    				
<artifactId>spring-boot-maven-plugin</artifactId>
    			</plugin>
    		</plugins>
    	</build>
    
    </project>
    

estos proyectos se pueden pre configurar de una forma un poco sencilla 
desde la web https://start.spring.io/

Se crea también una base de datos en mysql con las siguientes tablas

    CREATE DATABASE  IF NOT EXISTS `users` /*!40100 DEFAULT CHARACTER SET 
utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
    USE `users`;
    -- MySQL dump 10.13  Distrib 8.0.33, for macos13 (arm64)
    --
    -- Host: localhost    Database: users
    -- ------------------------------------------------------
    -- Server version	8.0.33
    
    /*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
    /*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
    /*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
    /*!50503 SET NAMES utf8 */;
    /*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
    /*!40103 SET TIME_ZONE='+00:00' */;
    /*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
    /*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, 
FOREIGN_KEY_CHECKS=0 */;
    /*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, 
SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
    /*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
    
    --
    -- Table structure for table `clients`
    --
    
    DROP TABLE IF EXISTS `clients`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!50503 SET character_set_client = utf8mb4 */;
    CREATE TABLE `clients` (
      `id` int NOT NULL AUTO_INCREMENT,
      `name` varchar(45) DEFAULT NULL,
      `balance` bigint DEFAULT NULL,
      `active` tinyint DEFAULT NULL,
      PRIMARY KEY (`id`),
      CONSTRAINT `ck_balance_positive` CHECK ((`balance` >= 0))
    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_0900_ai_ci;
    /*!40101 SET character_set_client = @saved_cs_client */;
    
    --
    -- Dumping data for table `clients`
    --
    
    LOCK TABLES `clients` WRITE;
    /*!40000 ALTER TABLE `clients` DISABLE KEYS */;
    INSERT INTO `clients` VALUES (1,'NICOLAS CARABALLO 
ROJAS',777,1),(2,'ANDRES HERNANDEZ',7123,1);
    /*!40000 ALTER TABLE `clients` ENABLE KEYS */;
    UNLOCK TABLES;
    /*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
    
    /*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
    /*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
    /*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
    /*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
    /*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
    /*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
    /*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
    
    -- Dump completed on 2023-04-29 22:50:30
    

CREATE DATABASE  IF NOT EXISTS `users` /*!40100 DEFAULT CHARACTER SET 
utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `users`;
-- MySQL dump 10.13  Distrib 8.0.33, for macos13 (arm64)
--
-- Host: localhost    Database: users
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, 
FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' 
*/;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `lastname` varchar(45) DEFAULT NULL,
  `active` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES 
(1,'NICOLAS','CARABALLO',1),(2,'CARABALLO','NICOLAS',0),(3,'Ortiz','Javier',0),(4,'Ortiz','Javier',0),(5,'Ortiz','Javier',0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-04-29 22:50:30


se crean los objetos pertinentes, archivo de conexion JDBC para cada uno y 
sus respectivos querys y finalmente los controladores. 

# MODELOS
    package com.bezkoder.spring.jdbc.model;
    
    public class User {
    
      private long id;
      private String lastname;
      private String name;
      private boolean active;
    
      public User() {
    
      }
      
      public User(long id, String lastname, String name, boolean active) {
        this.id = id;
        this.lastname = lastname;
        this.name = name;
        this.active = active;
      }
    
      public User(String lastname, String name, boolean active) {
        this.lastname = lastname;
        this.name = name;
        this.active = active;
      }
      
      public void setId(long id) {
        this.id = id;
      }
      
      public long getId() {
        return id;
      }
    
      public String getBalance() {
        return lastname;
      }
    
      public void setLastname(String lastname) {
        this.lastname = lastname;
      }
    
      public String getName() {
        return name;
      }
    
      public void setName(String name) {
        this.name = name;
      }
    
      public boolean isActive() {
        return active;
      }
    
      public void setActive(boolean isActive) {
        this.active = isActive;
      }
    
      @Override
      public String toString() {
        return "usuario [id=" + id + ", lastname=" + lastname + ", name=" 
+ name + ", active=" + active + "]";
      }
    
    }
    

Se crea el modelo junto con sus atributos y sus respectivos metodos de 
encapsulamiento.

## REPOSITORIOS DE FUNCIONES

    package com.bezkoder.spring.jdbc.repository;
    
    import java.util.List;
    
    import com.bezkoder.spring.jdbc.model.User;
    
    public interface UserRepository {
      int save(User name);
    
      int update(User name);
    
      User findById(Long id);
    
      int deleteById(Long id);
    
      List<User> findAll();
    
      List<User> findByActive(boolean active);
    
      List<User> findByNameContaining(String title);
    
      int deleteAll();
    }
    

Se crean las funciones principales que usara nuestra aplicación spring 
donde cadaw uno recibe un tipo de dato respecto a su funcion. 

## JDBC REPOSITORY

    package com.bezkoder.spring.jdbc.repository;
    
    import java.util.List;
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.dao.IncorrectResultSizeDataAccessException;
    import org.springframework.jdbc.core.BeanPropertyRowMapper;
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.stereotype.Repository;
    
    import com.bezkoder.spring.jdbc.model.User;
    
    @Repository
    public class JdbcUserRepository implements UserRepository {
    
      @Autowired
      private JdbcTemplate jdbcTemplate;
    
      @Override
      public int save(User user) {
        return jdbcTemplate.update("INSERT INTO users (name, lastname, 
active) VALUES(?,?,?)",
            new Object[] { user.getBalance(), user.getName(), 
user.isActive() });
      }
    
      @Override
      public int update(User user) {
        return jdbcTemplate.update("UPDATE users SET name=?, lastname=?, 
active=? WHERE id=?",
            new Object[] { user.getBalance(), user.getName(), 
user.isActive(), user.getId() });
      }
    
      @Override
      public User findById(Long id) {
        try {
          User user = jdbcTemplate.queryForObject("SELECT * FROM users 
WHERE id=?",
              BeanPropertyRowMapper.newInstance(User.class), id);
    
          return user;
        } catch (IncorrectResultSizeDataAccessException e) {
          return null;
        }
      }
    
      @Override
      public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id);
      }
    
      @Override
      public List<User> findAll() {
        return jdbcTemplate.query("SELECT * from users", 
BeanPropertyRowMapper.newInstance(User.class));
      }
    
      @Override
      public List<User> findByActive(boolean active) {
        return jdbcTemplate.query("SELECT * from users WHERE active=?",
            BeanPropertyRowMapper.newInstance(User.class), active);
      }
    
      @Override
      public List<User> findByNameContaining(String name) {
        String q = "SELECT * from users WHERE name ILIKE '%" + name + 
"%'";
    
        return jdbcTemplate.query(q, 
BeanPropertyRowMapper.newInstance(User.class));
      }
    
      @Override
      public int deleteAll() {
        return jdbcTemplate.update("DELETE from users");
      }
    }
    

Creamos una clase que va heredar de nuestro repositorio en la cuál vamos a 
hacer la sobrecarga de los metodos y sus atributos que definimos 
anteriomente.

FINALMENTE TENEMOS LA CLASE CONTROLER QUE ES LA CUAL DEFINIMOS CON AYUDA 
DE LAS ANOTACIONES LOS METODOS HTTP DISPONIBLES, EN EL CUAL LLAMAREMOS LAS 
FUNCIONES DE NUESTRO JDBC

    package com.bezkoder.spring.jdbc.controller;
    
    import java.util.ArrayList;
    import java.util.List;
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.CrossOrigin;
    import org.springframework.web.bind.annotation.DeleteMapping;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.PutMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;
    
    import com.bezkoder.spring.jdbc.model.User;
    import com.bezkoder.spring.jdbc.repository.UserRepository;
    
    @CrossOrigin(origins = "http://localhost:8081")
    @RestController
    @RequestMapping("/api")
    public class UserController {
    
      @Autowired
      UserRepository userRepository;
    
      @GetMapping("/users")
      public ResponseEntity<List<User>> getAllusers(@RequestParam(required 
= false) String title) {
        try {
          List<User> users = new ArrayList<User>();
    
          if (title == null)
            userRepository.findAll().forEach(users::add);
          else
            
userRepository.findByNameContaining(title).forEach(users::add);
    
          if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
          }
    
          return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
          return new ResponseEntity<>(null, 
HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    
      @GetMapping("/users/{id}")
      public ResponseEntity<User> getUserById(@PathVariable("id") long id) 
{
        User user = userRepository.findById(id);
    
        if (user != null) {
          return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
      }
    
      @PostMapping("/users")
      public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
          userRepository.save(new User(user.getBalance(), user.getName(), 
false));
          return new ResponseEntity<>("User was created successfully.", 
HttpStatus.CREATED);
        } catch (Exception e) {
          return new ResponseEntity<>(null, 
HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    
      @PutMapping("/users/{id}")
      public ResponseEntity<String> updateUser(@PathVariable("id") long 
id, @RequestBody User user) {
        User _user = userRepository.findById(id);
    
        if (_user != null) {
          _user.setId(id);
          _user.setLastname(user.getBalance());
          _user.setName(user.getName());
          _user.setActive(user.isActive());
    
          userRepository.update(_user);
          return new ResponseEntity<>("User was updated successfully.", 
HttpStatus.OK);
        } else {
          return new ResponseEntity<>("Cannot find User with id=" + id, 
HttpStatus.NOT_FOUND);
        }
      }
    
      @DeleteMapping("/users/{id}")
      public ResponseEntity<String> deleteUser(@PathVariable("id") long 
id) {
        try {
          int result = userRepository.deleteById(id);
          if (result == 0) {
            return new ResponseEntity<>("Cannot find User with id=" + id, 
HttpStatus.OK);
          }
          return new ResponseEntity<>("User was deleted successfully.", 
HttpStatus.OK);
        } catch (Exception e) {
          return new ResponseEntity<>("Cannot delete user.", 
HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    
      @DeleteMapping("/users")
      public ResponseEntity<String> deleteAllusers() {
        try {
          int numRows = userRepository.deleteAll();
          return new ResponseEntity<>("Deleted " + numRows + " User(s) 
successfully.", HttpStatus.OK);
        } catch (Exception e) {
          return new ResponseEntity<>("Cannot delete users.", 
HttpStatus.INTERNAL_SERVER_ERROR);
        }
    
      }
    
      @GetMapping("/users/published")
      public ResponseEntity<List<User>> findByPublished() {
        try {
          List<User> users = userRepository.findByActive(true);
    
          if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
          }
          return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    
    }
    

Para ver el funcionamiento desde postman mandamos la siguiente petición 
HTTP 

    #method GET
    # endopint: localhost:xxxx/api/clients/ O 
localhost:8080/api/clients/{id}


# CLIENTE Y SALDO EN UNA FUCNION 

Dada una tabla CLIENTES con los campos CLIENTE y SALDO, se solicita 
implementar un método que
mediante dos updates reste un importe del campo saldo del cliente1 y lo 
sume al saldo del cliente en
una sola transacción, de forma que en caso de excepción el saldo de los 
dos clientes vuelva a su valor
anterior.

usando el proyecto anterior, creamos un objeto transacción con lo 
siguiente

	    package com.bezkoder.spring.jdbc.model;
    
    public class Transaction {
    
        private long balance;
        private Client cliente1;
        private Client cliente2;
    
        public Transaction(long balance, Client client1, Client client2){
            this.balance=balance;
            this.cliente1=client1;
            this.cliente2=client2;
        }
    
        public long getBalance() {
            return balance;
        }
    
        public void setBalance(long balance) {
            this.balance = balance;
        }
    
        public Client getCliente1() {
            return cliente1;
        }
    
        public void setCliente1(Client cliente1) {
            this.cliente1 = cliente1;
        }
    
        public Client getCliente2() {
            return cliente2;
        }
    
        public void setCliente2(Client cliente2) {
            this.cliente2 = cliente2;
        }
    }
    
	

esto para realizar una transacción entre 2 personas.

para el clientController se agrega lo siguiente que es muy similar a lo 
anterior visto, pero en este caso enviamos un objeto tipo transaccion para 
definir los usuarios a usar

    @PostMapping("/clients/transactionBalance")
      public ResponseEntity<String> transactionBalance(@RequestBody 
Transaction transaction) {
        System.out.println("esta aqui");
        try {
          clientRepository.transactionBalance(new 
Transaction(transaction.getBalance(),
                  new Client(transaction.getCliente1().getId(), 
transaction.getCliente1().getBalance(), 
transaction.getCliente1().getName(), false),
                  new Client(transaction.getCliente2().getId(), 
transaction.getCliente2().getBalance(), 
transaction.getCliente2().getName(), false)));
          return new ResponseEntity<>("Balance applied successfully.", 
HttpStatus.CREATED);
        } catch (Exception e) {
          return new ResponseEntity<>("Balance cant less zero", 
HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
	  


finalmente, en el cliente repository agregamos el siguiente codigo 

    @Override
      public void transactionBalance(Transaction transaction) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() 
{
          protected void doInTransactionWithoutResult(TransactionStatus 
status) {
            try {
    
              // Sumar el importe al saldo del cliente2
              jdbcTemplate.update("UPDATE clients SET balance = balance + 
? WHERE id = ?", transaction.getBalance(), 
transaction.getCliente2().getId());
    
              // Restar el importe al saldo del cliente1
              jdbcTemplate.update("UPDATE clients SET balance = balance - 
? WHERE id = ?", transaction.getBalance(), 
transaction.getCliente1().getId());
    
    
            } catch (Exception e) {
              status.setRollbackOnly();
              throw e;
            }
          }
        });
      }
	  
	  
las clases transactionTemplate, TransactionCallbackWithoutResult, 
doInTransactionWithoutResult, TransactionStatus nos permiten hacer un 
manejo transaccional desde codigo. 

en mi opinion hacerlo desde codigo no siempre es suficiente y para efectos 
practicos aqui se pudo haber realizado transacciones desde el motor de 
base de datos. 


# PUNTO FINAL HILOS
En Java se puede utilizar la sincronización para lograr que un hilo se 
bloquee mientras otro está accediendo a una variable compartida. Para 
hacerlo, se puede utilizar la palabra clave synchronized en el método o 
bloque de código que necesita acceder a la variable compartida.

se podría ver de la siguiente forma: 

    public class DatosCompartidos {
        private int valor;
    
        public synchronized int obtenerValor() {
            return valor;
        }
    
        public synchronized void modificarValor(int nuevoValor) {
            valor = nuevoValor;
        }
    }
   

Al hacer esto, cada vez que un hilo intente llamar a cualquiera de estos 
dos métodos, primero tendrá que adquirir un bloqueo en el objeto de la 
clase DatosCompartidos. Si otro hilo ya tiene el bloqueo, el hilo actual 
se quedará bloqueado hasta que el otro hilo libere el bloqueo al finalizar 
su acceso a la variable compartida.

    }
    export default withRouter(SignInPage);
	  
	  
	  


La logica principal ocurre en la promesa asincrona donde se le solicta el 
token al backend, este lo guarda en el local storage, de esta forma cada 
petición que se realice validará con el token del lado del servidor si 
tiene permiso o no de usar lo servicios.

# Base de datos y scripts

Se crea el siguiente script para efectos del ejercicio:

    CREATE TABLE provincias (
      idprovincia INT PRIMARY KEY,
      descripcion VARCHAR(50) NOT NULL
    );
    
    -- Insertar datos en la tabla provincias
    INSERT INTO provincias (idprovincia, descripcion) VALUES
      (1, 'Zaragoza'),
      (2, 'Huesca'),
      (3, 'Teruel');
    
    -- Crear tabla productos
    CREATE TABLE productos (
      idproducto CHAR(1) PRIMARY KEY,
      descripcion VARCHAR(50) NOT NULL,
      precio DECIMAL(5,2) NOT NULL
    );
    
    -- Insertar datos en la tabla productos
    INSERT INTO productos (idproducto, descripcion, precio) VALUES
      ('A', 'Playmobil', 5.00),
      ('B', 'Puzzle', 10.25),
      ('C', 'Peonza', 3.65);
      
      
     -- Crear tabla clientes
    CREATE TABLE clientes (
      idcliente INT PRIMARY KEY,
      nombre VARCHAR(50) NOT NULL,
      idprovincia INT NOT NULL,
      FOREIGN KEY (idprovincia) REFERENCES provincias(idprovincia)
    );
    
    -- Insertar datos en la tabla clientes
    INSERT INTO clientes (idcliente, nombre, idprovincia) VALUES
      (1, 'Juan Palomo', 1),
      (2, 'Armando Ruido', 2),
      (3, 'Carmelo Cotón', 1),
      (4, 'Dolores Fuertes', 3),
      (5, 'Alberto Mate', 3);
    
    
    -- Crear tabla compras
    CREATE TABLE compras (
      idcompra INT PRIMARY KEY,
      idcliente INT NOT NULL,
      idproducto CHAR(1) NOT NULL,
      fecha DATE NOT NULL,
      FOREIGN KEY (idcliente) REFERENCES clientes(idcliente),
      FOREIGN KEY (idproducto) REFERENCES productos(idproducto)
    );
    
    -- Insertar datos en la tabla compras
    INSERT INTO compras (idcompra, idcliente, idproducto, fecha) VALUES
      (1, 1, 'C', '2022-01-01'),
      (2, 2, 'B', '2022-01-15'),
      (3, 2, 'C', '2022-01-22'),
      (4, 4, 'A', '2022-02-03'),
      (5, 3, 'A', '2022-02-05'),
      (6, 1, 'B', '2022-02-16'),
      (7, 1, 'B', '2022-02-21'),
      (8, 4, 'A', '2022-02-21'),
      (9, 5, 'C', '2022-03-01'),
      (10, 3, 'A', '2022-03-01'),
      (11, 3, 'C', '2022-03-05'),
      (12, 2, 'B', '2022-03-07'),
      (13, 2, 'B', '2022-03-11'),
      (14, 1, 'A', '2022-03-18'),
      (15, 1, 'C', '2022-03-29'),
      (16, 5, 'B', '2022-04-08'),
      (17, 5, 'C', '2022-04-09'),
      (18, 4, 'C', '2022-04-09'),
      (19, 1, 'A', '2022-04-12'),
      (20, 2, 'A', '2022-04-19');

y las consultas son las siguientes

    -- Todas las compras detalladas con los datos del cliente, de los 
productos y de cada una de las
    -- ventas (código de cliente, nombre de cliente, nombre de provincia, 
producto, importe, fecha de la venta)
    
    select cl.idcliente, cl.nombre, pr.descripcion, 
productos.descripcion,productos.precio,compras.fecha  
    from provincias pr
    inner join clientes cl on pr.idprovincia = cl.idprovincia
    inner join compras ON compras.idcliente = cl.idcliente
    inner join provincias ON provincias.idprovincia = cl.idprovincia
    inner join productos ON productos.idproducto = compras.idproducto
    order by cl.nombre;
    
    -- Las compras detalladas de los clientes de Teruel.
    
    select cl.idcliente, cl.nombre, pr.descripcion, 
productos.descripcion,productos.precio,compras.fecha  from provincias pr
    inner join clientes cl on pr.idprovincia = cl.idprovincia
    inner join compras ON compras.idcliente = cl.idcliente
    inner join provincias ON provincias.idprovincia = cl.idprovincia
    inner join productos ON productos.idproducto = compras.idproducto
    where pr.descripcion like 'Teruel';
    
    --Las compras detalladas de los clientes de Huesca y Zaragoza en el 
primer trimestre de 2015
    
    select cl.idcliente, cl.nombre, pr.descripcion, 
productos.descripcion,productos.precio,compras.fecha  from provincias pr
    inner join clientes cl on pr.idprovincia = cl.idprovincia
    inner join compras  ON compras.idcliente = cl.idcliente
    inner join provincias ON provincias.idprovincia = cl.idprovincia
    inner join productos ON productos.idproducto = compras.idproducto
    where pr.descripcion in ('Huesca', 'Zaragoza')
    and compras.fecha BETWEEN '2022-01-01' and '2022-03-31'
    order by compras.fecha
    
    
    --Las compras agrupadas por producto de todos los clientes mostrando 
el número de compras y el
    --importe total para cada producto por cada uno de los clientes 
(código de cliente, provincia,
    --producto, número de ventas, importe total)
    
    select cl.idcliente, provincias.descripcion, productos.descripcion, 
count(cl.idcliente) as numero_ventas, sum(productos.precio) as 
importe_total from provincias pr
    inner join clientes cl on pr.idprovincia = cl.idprovincia
    inner join compras  ON compras.idcliente = cl.idcliente
    inner join provincias ON provincias.idprovincia = cl.idprovincia
    inner join productos ON productos.idproducto = compras.idproducto
    group by (cl.idcliente, provincias.descripcion, productos.descripcion, 
productos.precio)
    
    
    --Número de peonzas totales que se han comprado en el mes de marzo por 
los clientes de
    --Zaragoza (número de peonzas e importe total)
    
    select count(cl.idprovincia) as numero_de_peonzas, 
sum(productos.precio) as importe_total from provincias pr
    inner join clientes cl on pr.idprovincia = cl.idprovincia
    inner join compras  ON compras.idcliente = cl.idcliente
    inner join provincias ON provincias.idprovincia = cl.idprovincia
    inner join productos ON productos.idproducto = compras.idproducto
    where pr.descripcion in ('Zaragoza') and productos.descripcion in 
('Peonza')
    and compras.fecha BETWEEN '2022-03-01' and '2022-03-31'
    group by (cl.idcliente, productos.precio)
    
    --Las compras realizadas por todos los clientes agrupadas por mes 
    --(código de cliente, nombre,provincia, mes, número de compras, 
importe total)
    
    select total_ventas.idcliente, clientes.nombre, 
provincias2.descripcion, total_ventas.cantidad_ventas from
    (select ventas.idcliente, count(ventas.idcliente) as cantidad_ventas 
from 
    (select compras.idcliente, DATE_TRUNC('month',compras.fecha) as mes , 
sum(pr.precio) as importe from compras 
     inner join productos pr ON pr.idproducto = compras.idproducto
    where DATE_TRUNC('month',compras.fecha) 
    in 
    (select DATE_TRUNC('month',compras.fecha) as dd from compras group by 
dd)
    group by (compras.idcliente, pr.precio, compras.fecha)) as ventas
    group by (ventas.idcliente)) total_ventas
    inner join clientes ON clientes.idcliente = total_ventas.idcliente
    inner join provincias provincias2 ON provincias2.idprovincia = 
clientes.idprovincia
    

# BASE DE DATOS DON PEPE MUELAS,

SCRIPT GENERADO PARA LA BASE DE DATOS SOLICTADA POR DON PEPE MUELAS

Entidades:

Grabación (Título, Categoría musical, Número de temas, Descripción)
Formato (Nombre, Estado de conservación)
Intérprete (Nombre, Descripción)
Compañía discográfica (Identificador de compañía, Nombre, Dirección)
Relaciones:

La entidad Grabación se relaciona con la entidad Formato mediante la 
relación Grabación-Formato, la cual es de tipo 1:N, es decir, una 
grabación puede tener varios formatos, pero un formato sólo puede 
pertenecer a una grabación.
La entidad Grabación se relaciona con la entidad Intérprete mediante la 
relación Grabación-Intérprete, la cual es de tipo N:M, es decir, una 
grabación puede tener varios intérpretes, y un intérprete puede participar 
en varias grabaciones. Esta relación se descompone en dos relaciones: 
Grabación-Intérprete-Participación y Intérprete-Participación-Grabación, 
ambas de tipo 1:N.
La entidad Grabación se relaciona con la entidad Compañía discográfica 
mediante la relación Grabación-Compañía discográfica, la cual es de tipo 
N:1, es decir, varias grabaciones pueden ser editadas por la misma 
compañía discográfica, pero una grabación sólo puede ser editada por una 
compañía discográfica.

este modelo se creo para postgresql

    CREATE TABLE Grabacion (
      id SERIAL PRIMARY KEY,
      Titulo VARCHAR(255),
      CategoriaMusical VARCHAR(255),
      NumTemas INT,
      Descripcion TEXT
    );
    
    CREATE TABLE Formato (
      id SERIAL PRIMARY KEY,
      Nombre VARCHAR(255),
      EstadoConservacion VARCHAR(255)
    );
    
    
    CREATE TABLE Interprete (
      id SERIAL PRIMARY KEY,
      Nombre VARCHAR(255),
      Descripcion TEXT
    );
    
    
    CREATE TABLE CompaniaDiscografica (
      id INT PRIMARY KEY,
      Nombre VARCHAR(255),
      Direccion TEXT
    );
    
    CREATE TABLE GrabacionFormato (
      id_grabacion INT,
      id_formato INT,
      FOREIGN KEY (id_grabacion) REFERENCES Grabacion(id),
      FOREIGN KEY (id_formato) REFERENCES Formato(id)
    );
    
    CREATE TABLE InterpreteParticipacion (
      id_interprete INT,
      id_grabacion INT,
      FechaParticipacion DATE,
      FOREIGN KEY (id_interprete) REFERENCES Interprete(id),
      FOREIGN KEY (id_grabacion) REFERENCES Grabacion(id)
    );
    
    CREATE TABLE GrabacionCompaniaDiscografica (
      id_grabacion INT,
      id_compania INT,
      FOREIGN KEY (id_grabacion) REFERENCES Grabacion(id),
      FOREIGN KEY (id_compania) REFERENCES CompaniaDiscografica(id)
    );

    

# Realizar la implementación de una clase Controller de un api Rest

Realizar la implementación de una clase Controller de un api Rest que 
incluya un endpoint
“/api/usuarios” tipo “get” para listar los usuarios de una tabla USUARIOS 
de una bd, incluyendo la capa
de servicio con el acceso a la bd para obtener el select de usuarios 
mediante jdbctemplate.

Se crea un proyecto springboot con las siguientes dependencias 

    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
https://maven.apache.org/xsd/maven-4.0.0.xsd">
    	<modelVersion>4.0.0</modelVersion>
    	<parent>
    		<groupId>org.springframework.boot</groupId>
    		<artifactId>spring-boot-starter-parent</artifactId>
    		<version>2.5.4</version>
    		<relativePath/> <!-- lookup parent from repository -->
    	</parent>
    	<groupId>com.bezkoder</groupId>
    	<artifactId>spring-boot-jdbctemplate-crud-example</artifactId>
    	<version>0.0.1-SNAPSHOT</version>
    	<name>spring-boot-jdbctemplate-crud-example</name>
    	<description>Spring Boot JDBCTemplate CRUD example with H2 
Database - Rest API</description>
    	<properties>
    		<java.version>1.8</java.version>
    	</properties>
    	<dependencies>
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			
<artifactId>spring-boot-starter-data-jdbc</artifactId>
    		</dependency>
    		
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-web</artifactId>
    		</dependency>
    
    		<dependency>
    			<groupId>com.h2database</groupId>
    			<artifactId>h2</artifactId>
    			<scope>runtime</scope>
    		</dependency>
    		
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-test</artifactId>
    			<scope>test</scope>
    		</dependency>
    		<dependency>
    			<groupId>mysql</groupId>
    			<artifactId>mysql-connector-java</artifactId>
    			<version>8.0.14</version>
    		</dependency>
    		<dependency>
    			<groupId>org.postgresql</groupId>
    			<artifactId>postgresql</artifactId>
    			<version>42.2.10</version>
    		</dependency>
    	</dependencies>
    
    	<build>
    		<plugins>
    			<plugin>
    				
<groupId>org.springframework.boot</groupId>
    				
<artifactId>spring-boot-maven-plugin</artifactId>
    			</plugin>
    		</plugins>
    	</build>
    
    </project>
    

estos proyectos se pueden pre configurar de una forma un poco sencilla 
desde la web https://start.spring.io/

Se crea también una base de datos en mysql con las siguientes tablas

    CREATE DATABASE  IF NOT EXISTS `users` /*!40100 DEFAULT CHARACTER SET 
utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
    USE `users`;
    -- MySQL dump 10.13  Distrib 8.0.33, for macos13 (arm64)
    --
    -- Host: localhost    Database: users
    -- ------------------------------------------------------
    -- Server version	8.0.33
    
    /*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
    /*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
    /*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
    /*!50503 SET NAMES utf8 */;
    /*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
    /*!40103 SET TIME_ZONE='+00:00' */;
    /*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
    /*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, 
FOREIGN_KEY_CHECKS=0 */;
    /*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, 
SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
    /*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
    
    --
    -- Table structure for table `clients`
    --
    
    DROP TABLE IF EXISTS `clients`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!50503 SET character_set_client = utf8mb4 */;
    CREATE TABLE `clients` (
      `id` int NOT NULL AUTO_INCREMENT,
      `name` varchar(45) DEFAULT NULL,
      `balance` bigint DEFAULT NULL,
      `active` tinyint DEFAULT NULL,
      PRIMARY KEY (`id`),
      CONSTRAINT `ck_balance_positive` CHECK ((`balance` >= 0))
    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_0900_ai_ci;
    /*!40101 SET character_set_client = @saved_cs_client */;
    
    --
    -- Dumping data for table `clients`
    --
    
    LOCK TABLES `clients` WRITE;
    /*!40000 ALTER TABLE `clients` DISABLE KEYS */;
    INSERT INTO `clients` VALUES (1,'NICOLAS CARABALLO 
ROJAS',777,1),(2,'ANDRES HERNANDEZ',7123,1);
    /*!40000 ALTER TABLE `clients` ENABLE KEYS */;
    UNLOCK TABLES;
    /*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
    
    /*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
    /*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
    /*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
    /*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
    /*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
    /*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
    /*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
    
    -- Dump completed on 2023-04-29 22:50:30
    

CREATE DATABASE  IF NOT EXISTS `users` /*!40100 DEFAULT CHARACTER SET 
utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `users`;
-- MySQL dump 10.13  Distrib 8.0.33, for macos13 (arm64)
--
-- Host: localhost    Database: users
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, 
FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' 
*/;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `lastname` varchar(45) DEFAULT NULL,
  `active` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES 
(1,'NICOLAS','CARABALLO',1),(2,'CARABALLO','NICOLAS',0),(3,'Ortiz','Javier',0),(4,'Ortiz','Javier',0),(5,'Ortiz','Javier',0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-04-29 22:50:30


se crean los objetos pertinentes, archivo de conexion JDBC para cada uno y 
sus respectivos querys y finalmente los controladores. 

# MODELOS
    package com.bezkoder.spring.jdbc.model;
    
    public class User {
    
      private long id;
      private String lastname;
      private String name;
      private boolean active;
    
      public User() {
    
      }
      
      public User(long id, String lastname, String name, boolean active) {
        this.id = id;
        this.lastname = lastname;
        this.name = name;
        this.active = active;
      }
    
      public User(String lastname, String name, boolean active) {
        this.lastname = lastname;
        this.name = name;
        this.active = active;
      }
      
      public void setId(long id) {
        this.id = id;
      }
      
      public long getId() {
        return id;
      }
    
      public String getBalance() {
        return lastname;
      }
    
      public void setLastname(String lastname) {
        this.lastname = lastname;
      }
    
      public String getName() {
        return name;
      }
    
      public void setName(String name) {
        this.name = name;
      }
    
      public boolean isActive() {
        return active;
      }
    
      public void setActive(boolean isActive) {
        this.active = isActive;
      }
    
      @Override
      public String toString() {
        return "usuario [id=" + id + ", lastname=" + lastname + ", name=" 
+ name + ", active=" + active + "]";
      }
    
    }
    

Se crea el modelo junto con sus atributos y sus respectivos metodos de 
encapsulamiento.

## REPOSITORIOS DE FUNCIONES

    package com.bezkoder.spring.jdbc.repository;
    
    import java.util.List;
    
    import com.bezkoder.spring.jdbc.model.User;
    
    public interface UserRepository {
      int save(User name);
    
      int update(User name);
    
      User findById(Long id);
    
      int deleteById(Long id);
    
      List<User> findAll();
    
      List<User> findByActive(boolean active);
    
      List<User> findByNameContaining(String title);
    
      int deleteAll();
    }
    

Se crean las funciones principales que usara nuestra aplicación spring 
donde cadaw uno recibe un tipo de dato respecto a su funcion. 

## JDBC REPOSITORY

    package com.bezkoder.spring.jdbc.repository;
    
    import java.util.List;
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.dao.IncorrectResultSizeDataAccessException;
    import org.springframework.jdbc.core.BeanPropertyRowMapper;
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.stereotype.Repository;
    
    import com.bezkoder.spring.jdbc.model.User;
    
    @Repository
    public class JdbcUserRepository implements UserRepository {
    
      @Autowired
      private JdbcTemplate jdbcTemplate;
    
      @Override
      public int save(User user) {
        return jdbcTemplate.update("INSERT INTO users (name, lastname, 
active) VALUES(?,?,?)",
            new Object[] { user.getBalance(), user.getName(), 
user.isActive() });
      }
    
      @Override
      public int update(User user) {
        return jdbcTemplate.update("UPDATE users SET name=?, lastname=?, 
active=? WHERE id=?",
            new Object[] { user.getBalance(), user.getName(), 
user.isActive(), user.getId() });
      }
    
      @Override
      public User findById(Long id) {
        try {
          User user = jdbcTemplate.queryForObject("SELECT * FROM users 
WHERE id=?",
              BeanPropertyRowMapper.newInstance(User.class), id);
    
          return user;
        } catch (IncorrectResultSizeDataAccessException e) {
          return null;
        }
      }
    
      @Override
      public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id);
      }
    
      @Override
      public List<User> findAll() {
        return jdbcTemplate.query("SELECT * from users", 
BeanPropertyRowMapper.newInstance(User.class));
      }
    
      @Override
      public List<User> findByActive(boolean active) {
        return jdbcTemplate.query("SELECT * from users WHERE active=?",
            BeanPropertyRowMapper.newInstance(User.class), active);
      }
    
      @Override
      public List<User> findByNameContaining(String name) {
        String q = "SELECT * from users WHERE name ILIKE '%" + name + 
"%'";
    
        return jdbcTemplate.query(q, 
BeanPropertyRowMapper.newInstance(User.class));
      }
    
      @Override
      public int deleteAll() {
        return jdbcTemplate.update("DELETE from users");
      }
    }
    

Creamos una clase que va heredar de nuestro repositorio en la cuál vamos a 
hacer la sobrecarga de los metodos y sus atributos que definimos 
anteriomente.

FINALMENTE TENEMOS LA CLASE CONTROLER QUE ES LA CUAL DEFINIMOS CON AYUDA 
DE LAS ANOTACIONES LOS METODOS HTTP DISPONIBLES, EN EL CUAL LLAMAREMOS LAS 
FUNCIONES DE NUESTRO JDBC

    package com.bezkoder.spring.jdbc.controller;
    
    import java.util.ArrayList;
    import java.util.List;
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.CrossOrigin;
    import org.springframework.web.bind.annotation.DeleteMapping;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.PutMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;
    
    import com.bezkoder.spring.jdbc.model.User;
    import com.bezkoder.spring.jdbc.repository.UserRepository;
    
    @CrossOrigin(origins = "http://localhost:8081")
    @RestController
    @RequestMapping("/api")
    public class UserController {
    
      @Autowired
      UserRepository userRepository;
    
      @GetMapping("/users")
      public ResponseEntity<List<User>> getAllusers(@RequestParam(required 
= false) String title) {
        try {
          List<User> users = new ArrayList<User>();
    
          if (title == null)
            userRepository.findAll().forEach(users::add);
          else
            
userRepository.findByNameContaining(title).forEach(users::add);
    
          if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
          }
    
          return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
          return new ResponseEntity<>(null, 
HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    
      @GetMapping("/users/{id}")
      public ResponseEntity<User> getUserById(@PathVariable("id") long id) 
{
        User user = userRepository.findById(id);
    
        if (user != null) {
          return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
      }
    
      @PostMapping("/users")
      public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
          userRepository.save(new User(user.getBalance(), user.getName(), 
false));
          return new ResponseEntity<>("User was created successfully.", 
HttpStatus.CREATED);
        } catch (Exception e) {
          return new ResponseEntity<>(null, 
HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    
      @PutMapping("/users/{id}")
      public ResponseEntity<String> updateUser(@PathVariable("id") long 
id, @RequestBody User user) {
        User _user = userRepository.findById(id);
    
        if (_user != null) {
          _user.setId(id);
          _user.setLastname(user.getBalance());
          _user.setName(user.getName());
          _user.setActive(user.isActive());
    
          userRepository.update(_user);
          return new ResponseEntity<>("User was updated successfully.", 
HttpStatus.OK);
        } else {
          return new ResponseEntity<>("Cannot find User with id=" + id, 
HttpStatus.NOT_FOUND);
        }
      }
    
      @DeleteMapping("/users/{id}")
      public ResponseEntity<String> deleteUser(@PathVariable("id") long 
id) {
        try {
          int result = userRepository.deleteById(id);
          if (result == 0) {
            return new ResponseEntity<>("Cannot find User with id=" + id, 
HttpStatus.OK);
          }
          return new ResponseEntity<>("User was deleted successfully.", 
HttpStatus.OK);
        } catch (Exception e) {
          return new ResponseEntity<>("Cannot delete user.", 
HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    
      @DeleteMapping("/users")
      public ResponseEntity<String> deleteAllusers() {
        try {
          int numRows = userRepository.deleteAll();
          return new ResponseEntity<>("Deleted " + numRows + " User(s) 
successfully.", HttpStatus.OK);
        } catch (Exception e) {
          return new ResponseEntity<>("Cannot delete users.", 
HttpStatus.INTERNAL_SERVER_ERROR);
        }
    
      }
    
      @GetMapping("/users/published")
      public ResponseEntity<List<User>> findByPublished() {
        try {
          List<User> users = userRepository.findByActive(true);
    
          if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
          }
          return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    
    }
    

Para ver el funcionamiento desde postman mandamos la siguiente petición 
HTTP 

    #method GET
    # endopint: localhost:xxxx/api/clients/ O 
localhost:8080/api/clients/{id}


# CLIENTE Y SALDO EN UNA FUCNION 

Dada una tabla CLIENTES con los campos CLIENTE y SALDO, se solicita 
implementar un método que
mediante dos updates reste un importe del campo saldo del cliente1 y lo 
sume al saldo del cliente en
una sola transacción, de forma que en caso de excepción el saldo de los 
dos clientes vuelva a su valor
anterior.

usando el proyecto anterior, creamos un objeto transacción con lo 
siguiente

	    package com.bezkoder.spring.jdbc.model;
    
    public class Transaction {
    
        private long balance;
        private Client cliente1;
        private Client cliente2;
    
        public Transaction(long balance, Client client1, Client client2){
            this.balance=balance;
            this.cliente1=client1;
            this.cliente2=client2;
        }
    
        public long getBalance() {
            return balance;
        }
    
        public void setBalance(long balance) {
            this.balance = balance;
        }
    
        public Client getCliente1() {
            return cliente1;
        }
    
        public void setCliente1(Client cliente1) {
            this.cliente1 = cliente1;
        }
    
        public Client getCliente2() {
            return cliente2;
        }
    
        public void setCliente2(Client cliente2) {
            this.cliente2 = cliente2;
        }
    }
    
	

esto para realizar una transacción entre 2 personas.

para el clientController se agrega lo siguiente que es muy similar a lo 
anterior visto, pero en este caso enviamos un objeto tipo transaccion para 
definir los usuarios a usar

    @PostMapping("/clients/transactionBalance")
      public ResponseEntity<String> transactionBalance(@RequestBody 
Transaction transaction) {
        System.out.println("esta aqui");
        try {
          clientRepository.transactionBalance(new 
Transaction(transaction.getBalance(),
                  new Client(transaction.getCliente1().getId(), 
transaction.getCliente1().getBalance(), 
transaction.getCliente1().getName(), false),
                  new Client(transaction.getCliente2().getId(), 
transaction.getCliente2().getBalance(), 
transaction.getCliente2().getName(), false)));
          return new ResponseEntity<>("Balance applied successfully.", 
HttpStatus.CREATED);
        } catch (Exception e) {
          return new ResponseEntity<>("Balance cant less zero", 
HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
	  


finalmente, en el cliente repository agregamos el siguiente codigo 

    @Override
      public void transactionBalance(Transaction transaction) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() 
{
          protected void doInTransactionWithoutResult(TransactionStatus 
status) {
            try {
    
              // Sumar el importe al saldo del cliente2
              jdbcTemplate.update("UPDATE clients SET balance = balance + 
? WHERE id = ?", transaction.getBalance(), 
transaction.getCliente2().getId());
    
              // Restar el importe al saldo del cliente1
              jdbcTemplate.update("UPDATE clients SET balance = balance - 
? WHERE id = ?", transaction.getBalance(), 
transaction.getCliente1().getId());
    
    
            } catch (Exception e) {
              status.setRollbackOnly();
              throw e;
            }
          }
        });
      }
	  
	  
las clases transactionTemplate, TransactionCallbackWithoutResult, 
doInTransactionWithoutResult, TransactionStatus nos permiten hacer un 
manejo transaccional desde codigo. 

en mi opinion hacerlo desde codigo no siempre es suficiente y para efectos 
practicos aqui se pudo haber realizado transacciones desde el motor de 
base de datos. 


# PUNTO FINAL HILOS
En Java se puede utilizar la sincronización para lograr que un hilo se 
bloquee mientras otro está accediendo a una variable compartida. Para 
hacerlo, se puede utilizar la palabra clave synchronized en el método o 
bloque de código que necesita acceder a la variable compartida.

se podría ver de la siguiente forma: 

    public class DatosCompartidos {
        private int valor;
    
        public synchronized int obtenerValor() {
            return valor;
        }
    
        public synchronized void modificarValor(int nuevoValor) {
            valor = nuevoValor;
        }
    }
   

Al hacer esto, cada vez que un hilo intente llamar a cualquiera de estos 
dos métodos, primero tendrá que adquirir un bloqueo en el objeto de la 
clase DatosCompartidos. Si otro hilo ya tiene el bloqueo, el hilo actual 
se quedará bloqueado hasta que el otro hilo libere el bloqueo al finalizar 
su acceso a la variable compartida.
