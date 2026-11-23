# Backend local (Express + MongoDB)

MongoDB portable en `C:\Users\pc\mongodata` (binarios en `bin`, datos en `db`).

## Arrancar
    powershell -ExecutionPolicy Bypass -File backend\start.ps1
Levanta mongod (127.0.0.1:27017) si no corre y la API en el puerto 3000.
Datos de ejemplo (opcional): `cd backend; npm run seed`.
Parar: Ctrl+C en el servidor; `Stop-Process -Name mongod` para Mongo.

## Ejemplos curl
    curl http://localhost:3000/api/solicitudes
    curl -X POST http://localhost:3000/api/solicitudes -H "Content-Type: application/json" -d '{"codigo":"SOL-9","titulo":"T","descripcion":"D","categoria":"C","servicio":"S","estado":"Pendiente"}'
    curl -X PUT http://localhost:3000/api/solicitudes/<id> -H "Content-Type: application/json" -d '{"estado":"Resuelto"}'
    curl -X DELETE http://localhost:3000/api/solicitudes/<id>

Codigos: POST 201 (400 si faltan codigo/titulo/descripcion/categoria), PUT 200/404, DELETE 204/404.

## Emulador Android
El emulador ve el PC en `http://10.0.2.2:3000/api/` (URL base que ya usa la app).
