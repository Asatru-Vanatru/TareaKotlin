const { MongoClient } = require('mongodb');
(async () => {
  const c = new MongoClient(process.env.MONGO_URL || 'mongodb://127.0.0.1:27017');
  await c.connect();
  const col = c.db('solicitudes_db').collection('solicitudes');
  const now = Date.now();
  const r = await col.insertMany([
    { codigo: 'SOL-001', titulo: 'Luminaria rota', descripcion: 'Poste sin luz en calle principal', categoria: 'Infraestructura', servicio: 'Alumbrado', evidenciaUri: null, estado: 'Pendiente', fechaCreacion: now },
    { codigo: 'SOL-002', titulo: 'Bache profundo', descripcion: 'Bache en la esquina de la plaza', categoria: 'Vialidad', servicio: 'Pavimentacion', evidenciaUri: null, estado: 'En proceso', fechaCreacion: now },
    { codigo: 'SOL-003', titulo: 'Basura acumulada', descripcion: 'Retiro de basura pendiente', categoria: 'Aseo', servicio: 'Recoleccion', evidenciaUri: null, estado: 'Pendiente', fechaCreacion: now }
  ]);
  console.log('Insertadas', r.insertedCount);
  await c.close();
})();
