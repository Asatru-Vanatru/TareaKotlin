const express = require('express');
const cors = require('cors');
const { MongoClient, ObjectId } = require('mongodb');

const MONGO_URL = process.env.MONGO_URL || 'mongodb://127.0.0.1:27017';
const PORT = process.env.PORT || 3000;
const CAMPOS = ['codigo', 'titulo', 'descripcion', 'categoria', 'servicio', 'evidenciaUri', 'estado', 'fechaCreacion'];
const OBLIGATORIOS = ['codigo', 'titulo', 'descripcion', 'categoria'];

const app = express();
app.use(cors());
app.use(express.json({ limit: '5mb' }));

const out = (d) => (d ? { ...d, _id: d._id.toString() } : d);
const oid = (s) => (ObjectId.isValid(s) && String(new ObjectId(s)) === s ? new ObjectId(s) : null);

function limpiar(body) {
  const o = {};
  for (const k of CAMPOS) if (body[k] !== undefined) o[k] = body[k];
  return o;
}
function faltantes(o) {
  return OBLIGATORIOS.filter((k) => typeof o[k] !== 'string' || !o[k].trim());
}

async function main() {
  const client = new MongoClient(MONGO_URL, { serverSelectionTimeoutMS: 5000 });
  await client.connect();
  const col = client.db('solicitudes_db').collection('solicitudes');

  app.get('/api/solicitudes', async (req, res, next) => {
    try { res.json((await col.find().toArray()).map(out)); } catch (e) { next(e); }
  });

  app.post('/api/solicitudes', async (req, res, next) => {
    try {
      const doc = limpiar(req.body || {});
      const f = faltantes(doc);
      if (f.length) return res.status(400).json({ error: 'Faltan campos obligatorios', campos: f });
      if (doc.evidenciaUri === undefined) doc.evidenciaUri = null;
      if (typeof doc.fechaCreacion !== 'number') doc.fechaCreacion = Date.now();
      const r = await col.insertOne(doc);
      res.status(201).json(out({ _id: r.insertedId, ...doc }));
    } catch (e) { next(e); }
  });

  app.put('/api/solicitudes/:id', async (req, res, next) => {
    try {
      const _id = oid(req.params.id);
      if (!_id) return res.status(404).json({ error: 'No encontrado' });
      const datos = limpiar(req.body || {});
      const f = OBLIGATORIOS.filter((k) => k in datos && (typeof datos[k] !== 'string' || !datos[k].trim()));
      if (f.length) return res.status(400).json({ error: 'Campos invalidos', campos: f });
      const r = Object.keys(datos).length
        ? await col.findOneAndUpdate({ _id }, { $set: datos }, { returnDocument: 'after' })
        : await col.findOne({ _id });
      if (!r) return res.status(404).json({ error: 'No encontrado' });
      res.json(out(r));
    } catch (e) { next(e); }
  });

  app.delete('/api/solicitudes/:id', async (req, res, next) => {
    try {
      const _id = oid(req.params.id);
      if (!_id) return res.status(404).json({ error: 'No encontrado' });
      const r = await col.deleteOne({ _id });
      if (!r.deletedCount) return res.status(404).json({ error: 'No encontrado' });
      res.status(204).end();
    } catch (e) { next(e); }
  });

  app.use((err, req, res, next) => {
    console.error(err);
    res.status(err.status || 500).json({ error: err.message });
  });

  app.listen(PORT, '0.0.0.0', () => console.log(`API lista en http://localhost:${PORT}/api/solicitudes`));
}
main().catch((e) => { console.error('No se pudo conectar a MongoDB:', e.message); process.exit(1); });
