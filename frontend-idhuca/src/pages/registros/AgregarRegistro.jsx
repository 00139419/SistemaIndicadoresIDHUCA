import React, { useState } from 'react';
import { Form, Button, Row, Col, Card } from 'react-bootstrap';

const AgregarRegistro = () => {
  const [formData, setFormData] = useState({
    fechaHecho: '',
    fuente: '',
    estadoActual: '',
    derechoAsociado: '',
    observaciones: '',
    departamento: '',
    municipio: '',
    lugarExacto: '',
    personasAfectadas: [],
  });

  const [catalogos, setCatalogos] = useState({
    fuentes: [],
    estados: [],
    derechos: [],
    departamentos: [],
    municipios: [],
    lugaresExactos: [],
    // Agregar más catálogos si es necesario
  });

  // Métodos para obtener los catálogos
  const obtenerCatalogos = () => {
    // Lógica futura para cargar los catálogos desde el backend
    setCatalogos({
      fuentes: [
        { codigo: 'FUENTE_8', descripcion: 'Fuente 8' },
        { codigo: 'FUENTE_9', descripcion: 'Fuente 9' },
      ],
      estados: [
        { codigo: 'ESTREG_3', descripcion: 'Estado 3' },
        { codigo: 'ESTREG_4', descripcion: 'Estado 4' },
      ],
      derechos: [
        { codigo: 'DER_4', descripcion: 'Derecho 4' },
        { codigo: 'DER_5', descripcion: 'Derecho 5' },
      ],
      departamentos: [
        { codigo: 'DEP_1', descripcion: 'San Salvador' },
        { codigo: 'DEP_6', descripcion: 'La Libertad' },
      ],
      municipios: [
        { codigo: 'MUN_1_7', descripcion: 'Mejicanos' },
        { codigo: 'MUN_6_7', descripcion: 'Santa Tecla' },
      ],
      lugaresExactos: [
        { codigo: 'LUGEXAC_1', descripcion: 'Parque Central' },
        { codigo: 'LUGEXAC_2', descripcion: 'Zona Universitaria' },
      ],
    });
  };

  // Llamar a los catálogos al cargar
  React.useEffect(() => {
    obtenerCatalogos();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = () => {
    // Aquí generas el JSON final
    const jsonFinal = {
      fechaHecho: formData.fechaHecho,
      fuente: { codigo: formData.fuente },
      estadoActual: { codigo: formData.estadoActual },
      derechoAsociado: { codigo: formData.derechoAsociado },
      flagViolencia: false,
      flagDetencion: false,
      flagExpresion: false,
      flagJusticia: false,
      flagCensura: false,
      flagRegimenExcepcion: false,
      observaciones: formData.observaciones,
      ubicacion: {
        departamento: { codigo: formData.departamento },
        municipio: { codigo: formData.municipio },
        lugarExacto: { codigo: formData.lugarExacto },
      },
      personasAfectadas: [], // Se agregará por separado
    };
    console.log(jsonFinal);
  };

  return (
    <div className="container mt-4">
      <Card className="p-4 shadow">
        <h3>Registro de Hecho</h3>
        <Form>
          <Row>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Fecha del Hecho</Form.Label>
                <Form.Control type="date" name="fechaHecho" value={formData.fechaHecho} onChange={handleInputChange} />
              </Form.Group>
            </Col>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Fuente</Form.Label>
                <Form.Select name="fuente" value={formData.fuente} onChange={handleInputChange}>
                  <option value="">Seleccione</option>
                  {catalogos.fuentes.map(f => (
                    <option key={f.codigo} value={f.codigo}>{f.descripcion}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Estado Actual</Form.Label>
                <Form.Select name="estadoActual" value={formData.estadoActual} onChange={handleInputChange}>
                  <option value="">Seleccione</option>
                  {catalogos.estados.map(e => (
                    <option key={e.codigo} value={e.codigo}>{e.descripcion}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
          </Row>

          <Row className="mt-3">
            <Col md={6}>
              <Form.Group>
                <Form.Label>Derecho Asociado</Form.Label>
                <Form.Select name="derechoAsociado" value={formData.derechoAsociado} onChange={handleInputChange}>
                  <option value="">Seleccione</option>
                  {catalogos.derechos.map(d => (
                    <option key={d.codigo} value={d.codigo}>{d.descripcion}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={6}>
              <Form.Group>
                <Form.Label>Observaciones</Form.Label>
                <Form.Control as="textarea" rows={2} name="observaciones" value={formData.observaciones} onChange={handleInputChange} />
              </Form.Group>
            </Col>
          </Row>

          <Row className="mt-3">
            <Col md={4}>
              <Form.Group>
                <Form.Label>Departamento</Form.Label>
                <Form.Select name="departamento" value={formData.departamento} onChange={handleInputChange}>
                  <option value="">Seleccione</option>
                  {catalogos.departamentos.map(dep => (
                    <option key={dep.codigo} value={dep.codigo}>{dep.descripcion}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Municipio</Form.Label>
                <Form.Select name="municipio" value={formData.municipio} onChange={handleInputChange}>
                  <option value="">Seleccione</option>
                  {catalogos.municipios.map(mun => (
                    <option key={mun.codigo} value={mun.codigo}>{mun.descripcion}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Lugar Exacto</Form.Label>
                <Form.Select name="lugarExacto" value={formData.lugarExacto} onChange={handleInputChange}>
                  <option value="">Seleccione</option>
                  {catalogos.lugaresExactos.map(lug => (
                    <option key={lug.codigo} value={lug.codigo}>{lug.descripcion}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
          </Row>

          <div className="mt-4 text-end">
            <Button variant="primary" onClick={handleSubmit}>Guardar Hecho</Button>
          </div>
        </Form>
      </Card>
    </div>
  );
};

export default AgregarRegistro;
