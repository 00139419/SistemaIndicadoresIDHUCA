import 'bootstrap/dist/css/bootstrap.min.css';
import { FaUser, FaMapMarkerAlt, FaInfoCircle, FaListUl } from 'react-icons/fa';
import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { detailEvent } from "../../services/RegstrosService";

const DetalleRegistro = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [registro, setRegistro] = useState(null);

  useEffect(() => {
    const fetchDetalle = async () => {
      try {
        const res = await detailEvent(id);
        setRegistro(res.entity);
      } catch (err) {
        console.error("Error al obtener detalle:", err);
      }
    };

    fetchDetalle();
  }, [id]);

  if (!registro) return <p>Cargando...</p>;

  return (
    <div className="container mt-5">
      <div className="text-center mb-4">
        <h2>Detalle del Registro #{registro.id}</h2>
      </div>

      {/* Información general */}
      <div className="card mb-4 shadow-sm">
        <div className="card-header bg-secondary text-white">
          <FaInfoCircle className="me-2" />
          Información general
        </div>
        <ul className="list-group list-group-flush">
          <li className="list-group-item"><strong>Fecha de Registro:</strong> {registro.fechaRegistro}</li>
          <li className="list-group-item"><strong>Fecha del Hecho:</strong> {registro.fechaHecho}</li>
          <li className="list-group-item"><strong>Fuente:</strong> {registro.fuente.descripcion}</li>
          <li className="list-group-item"><strong>Estado Actual:</strong> {registro.estadoActual.descripcion}</li>
          <li className="list-group-item"><strong>Derecho Asociado:</strong> {registro.derechoAsociado.descripcion}</li>
          <li className="list-group-item"><strong>Fue en régimen de excepción:</strong> {registro.flagRegimenExcepcion ? 'Si' : 'No'}</li>
          <li className="list-group-item"><strong>Observaciones:</strong> {registro.observaciones}</li>
        </ul>
      </div>

      {/* Ubicación */}
      <div className="card mb-4 shadow-sm">
        <div className="card-header bg-secondary text-white">
          <FaMapMarkerAlt className="me-2" />
          Ubicación del Hecho
        </div>
        <ul className="list-group list-group-flush">
          <li className="list-group-item"><strong>Departamento:</strong> {registro.ubicacion.departamento.descripcion}</li>
          <li className="list-group-item"><strong>Municipio:</strong> {registro.ubicacion.municipio.descripcion}</li>
          <li className="list-group-item"><strong>Lugar Exacto:</strong> {registro.ubicacion.lugarExacto.descripcion}</li>
        </ul>
      </div>

      

{registro.personasAfectadas.map((p, idx) => (
  <div className="card mb-4 shadow-sm" key={p.id}>
    <div className="card-header bg-info text-white">
      Persona #{idx + 1}: {p.nombre}
    </div>
    <div className="card-body">
      <div className="row">
        <div className="col-md-6">
          <ul className="list-group mb-3">
            <li className="list-group-item"><strong>Edad:</strong> {p.edad}</li>
            <li className="list-group-item"><strong>Género:</strong> {p.genero.descripcion}</li>
            <li className="list-group-item"><strong>Nacionalidad:</strong> {p.nacionalidad.descripcion}</li>
            <li className="list-group-item"><strong>Residencia:</strong> {p.municipioResidencia.descripcion}, {p.departamentoResidencia.descripcion}</li>
            <li className="list-group-item"><strong>Tipo de persona:</strong> {p.tipoPersona.descripcion}</li>
            <li className="list-group-item"><strong>Estado de salud:</strong> {p.estadoSalud.descripcion}</li>
          </ul>
        </div>

        <div className="col-md-6">
          {/* Derechos vulnerados */}
          {p.derechosVulnerados.length > 0 && (
            <div className="card mb-3">
              <div className="card-header bg-secondary text-white" data-bs-toggle="collapse" data-bs-target={`#dv-${idx}`} style={{ cursor: 'pointer' }}>
                Derechos vulnerados específicamente
              </div>
              <div className="collapse show" id={`dv-${idx}`}>
                <div className="card-body">
                  <ul>
                    {p.derechosVulnerados.map(dv => (
                      <li key={dv.id}>{dv.derecho.descripcion}</li>
                    ))}
                  </ul>
                </div>
              </div>
            </div>
          )}

          {/* Violencia */}
          {p.violencia && (
            <div className="card mb-3">
              <div className="card-header bg-secondary text-white" data-bs-toggle="collapse" data-bs-target={`#violencia-${idx}`} style={{ cursor: 'pointer' }}>
                Violencia
              </div>
              <div className="collapse show" id={`violencia-${idx}`}>
                <div className="card-body">
                  <ul>
                    <li><strong>Tipo:</strong> {p.violencia.tipoViolencia.descripcion}</li>
                    <li><strong>Artefacto:</strong> {p.violencia.artefactoUtilizado.descripcion}</li>
                    <li><strong>Contexto:</strong> {p.violencia.contexto.descripcion}</li>
                    <li><strong>Actor responsable:</strong> {p.violencia.actorResponsable.descripcion}</li>
                    <li><strong>Salud del actor:</strong> {p.violencia.estadoSaludActorResponsable.descripcion}</li>
                    <li><strong>Hubo protección:</strong> {p.violencia.huboProteccion ? 'Sí' : 'No'}</li>
                    <li><strong>Investigación abierta:</strong> {p.violencia.investigacionAbierta ? 'Sí' : 'No'}</li>
                    <li><strong>Respuesta del Estado:</strong> {p.violencia.respuestaEstado}</li>
                  </ul>
                </div>
              </div>
            </div>
          )}

          {/* Detención e Integridad */}
          {p.detencionIntegridad && (
            <div className="card mb-3">
              <div className="card-header bg-secondary text-white" data-bs-toggle="collapse" data-bs-target={`#detencion-${idx}`} style={{ cursor: 'pointer' }}>
                Detención e Integridad
              </div>
              <div className="collapse show" id={`detencion-${idx}`}>
                <div className="card-body">
                  <ul>
                    <li><strong>Tipo:</strong> {p.detencionIntegridad.tipoDetencion.descripcion}</li>
                    <li><strong>Autoridad:</strong> {p.detencionIntegridad.autoridadInvolucrada.descripcion}</li>
                    <li><strong>Tortura:</strong> {p.detencionIntegridad.huboTortura ? 'Sí' : 'No'}</li>
                    <li><strong>Duración (días):</strong> {p.detencionIntegridad.duracionDias}</li>
                    <li><strong>Acceso a abogado:</strong> {p.detencionIntegridad.accesoAbogado ? 'Sí' : 'No'}</li>
                    <li><strong>Resultado:</strong> {p.detencionIntegridad.resultado}</li>
                    <li><strong>Motivo:</strong> {p.detencionIntegridad.motivoDetencion.descripcion}</li>
                  </ul>
                </div>
              </div>
            </div>
          )}

          {/* Expresión y Censura */}
          {p.expresionCensura && (
            <div className="card mb-3">
              <div className="card-header bg-secondary text-white" data-bs-toggle="collapse" data-bs-target={`#censura-${idx}`} style={{ cursor: 'pointer' }}>
                Expresión y Censura
              </div>
              <div className="collapse show" id={`censura-${idx}`}>
                <div className="card-body">
                  <ul>
                    <li><strong>Medio:</strong> {p.expresionCensura.medioExpresion.descripcion}</li>
                    <li><strong>Tipo represión:</strong> {p.expresionCensura.tipoRepresion.descripcion}</li>
                    <li><strong>Represalias legales:</strong> {p.expresionCensura.represaliasLegales ? 'Sí' : 'No'}</li>
                    <li><strong>Represalias físicas:</strong> {p.expresionCensura.represaliasFisicas ? 'Sí' : 'No'}</li>
                    <li><strong>Actor censor:</strong> {p.expresionCensura.actorCensor.descripcion}</li>
                    <li><strong>Consecuencia:</strong> {p.expresionCensura.consecuencia}</li>
                  </ul>
                </div>
              </div>
            </div>
          )}

          {/* Acceso a Justicia */}
          {p.accesoJusticia && (
            <div className="card mb-3">
              <div className="card-header bg-secondary text-white" data-bs-toggle="collapse" data-bs-target={`#justicia-${idx}`} style={{ cursor: 'pointer' }}>
                Acceso a la Justicia
              </div>
              <div className="collapse show" id={`justicia-${idx}`}>
                <div className="card-body">
                  <ul>
                    <li><strong>Tipo de proceso:</strong> {p.accesoJusticia.tipoProceso.descripcion}</li>
                    <li><strong>Fecha de denuncia:</strong> {p.accesoJusticia.fechaDenuncia}</li>
                    <li><strong>Tipo de denunciante:</strong> {p.accesoJusticia.tipoDenunciante.descripcion}</li>
                    <li><strong>Duración:</strong> {p.accesoJusticia.duracionProceso.descripcion}</li>
                    <li><strong>Acceso a abogado:</strong> {p.accesoJusticia.accesoAbogado ? 'Sí' : 'No'}</li>
                    <li><strong>Hubo parcialidad:</strong> {p.accesoJusticia.huboParcialidad ? 'Sí' : 'No'}</li>
                    <li><strong>Resultado:</strong> {p.accesoJusticia.resultadoProceso}</li>
                    <li><strong>Instancia:</strong> {p.accesoJusticia.instancia}</li>
                  </ul>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  </div>
))}








    </div>
  );
};

export default DetalleRegistro;
