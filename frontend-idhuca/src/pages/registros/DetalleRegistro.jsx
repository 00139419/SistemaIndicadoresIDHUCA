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
            Persona #{idx + 1}: {p.nombre ? p.nombre || p.length == 0 : 'Persona no identificada'}
          </div>
          <div className="card-body">
            <div className="row">
              <div className="col-md-6">
                <ul className="list-group mb-3">
                  <li className="list-group-item"><strong>Edad:</strong> {p.edad ? p.edad : 'Desconocido'}</li>
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
                          <li><strong>¿Fue un asesinato?:</strong> {p.violencia.esAsesinato === true ? 'Sí' : p.violencia.esAsesinato === false ? 'No' : 'No especificado'}</li>
                          <li><strong>Tipo de violencia:</strong> {p.violencia.tipoViolencia.descripcion}</li>
                          <li><strong>Artefacto utilizado:</strong> {p.violencia.artefactoUtilizado.descripcion}</li>
                          <li><strong>Contexto de la violencia:</strong> {p.violencia.contexto.descripcion}</li>
                          <li><strong>Actor responsable:</strong> {p.violencia.actorResponsable.descripcion}</li>
                          <li><strong>Estado de salud del actor responsable:</strong> {p.violencia.estadoSaludActorResponsable.descripcion}</li>
                          <li><strong>Hubo protección:</strong> {p.violencia.huboProteccion === true ? 'Sí' : p.violencia.huboProteccion === false ? 'No' : 'No especificado'}</li>
                          <li><strong>Investigación abierta:</strong> {p.violencia.investigacionAbierta === true ? 'Sí' : p.violencia.investigacionAbierta === false ? 'No' : 'No especificado'}</li>
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
                          <li><strong>Tipo de detención:</strong> {p.detencionIntegridad.tipoDetencion.descripcion}</li>
                          <li><strong>Autoridad involucrada:</strong> {p.detencionIntegridad.autoridadInvolucrada.descripcion}</li>
                          <li><strong>¿Hubo tortura?:</strong> {p.detencionIntegridad.huboTortura === true ? 'Sí' : p.detencionIntegridad.huboTortura === false ? 'No' : 'No especificado'}</li>
                          <li><strong>Duración de la detención (días):</strong> {p.detencionIntegridad.duracionDias}</li>
                          <li><strong>¿Existió acceso a un abogado?:</strong> {p.detencionIntegridad.accesoAbogado === true ? 'Sí' : p.detencionIntegridad.accesoAbogado === false ? 'No' : 'No especificado'}</li>
                          <li><strong>Resultado del proceso:</strong> {p.detencionIntegridad.resultado}</li>
                          <li><strong>Motivo de la detención:</strong> {p.detencionIntegridad.motivoDetencion.descripcion}</li>
                          <li><strong>¿Existió orden judicial?:</strong> {p.detencionIntegridad.ordenJudicial === true ? 'Sí' : p.detencionIntegridad.ordenJudicial === false ? 'No' : 'No especificado'}</li>
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
                          <li><strong>Medio de expresión:</strong> {p.expresionCensura.medioExpresion.descripcion}</li>
                          <li><strong>Tipo de represión:</strong> {p.expresionCensura.tipoRepresion.descripcion}</li>
                          <li><strong>¿Hubo represalias legales?:</strong> {p.expresionCensura.represaliasLegales === true ? 'Sí' : p.expresionCensura.represaliasLegales === false ? 'No' : 'No especificado'}</li>
                          <li><strong>¿Hubo represalias físicas?:</strong> {p.expresionCensura.represaliasFisicas === true ? 'Sí' : p.expresionCensura.represaliasFisicas === false ? 'No' : 'No especificado'}</li>
                          <li><strong>Actor censor:</strong> {p.expresionCensura.actorCensor.descripcion}</li>
                          <li><strong>Consecuencias:</strong> {p.expresionCensura.consecuencia}</li>
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
                          <li><strong>Tipo de proceso judicial:</strong> {p.accesoJusticia.tipoProceso.descripcion}</li>
                          <li><strong>Fecha de presentación de denuncia:</strong> {p.accesoJusticia.fechaDenuncia}</li>
                          <li><strong>Tipo de denunciante:</strong> {p.accesoJusticia.tipoDenunciante.descripcion}</li>
                          <li><strong>Duración del proceso:</strong> {p.accesoJusticia.duracionProceso.descripcion}</li>
                          <li><strong>¿Tuvo acceso a abogado?:</strong> {p.accesoJusticia.accesoAbogado === true ? 'Sí' : p.accesoJusticia.accesoAbogado === false ? 'No' : 'No especificado'}</li>
                          <li><strong>¿Hubo parcialidad en el proceso?:</strong> {p.accesoJusticia.huboParcialidad === true ? 'Sí' : p.accesoJusticia.huboParcialidad === false ? 'No' : 'No especificado'}</li>
                          <li><strong>Resultado del proceso:</strong> {p.accesoJusticia.resultadoProceso}</li>
                          <li><strong>Instancia judicial:</strong> {p.accesoJusticia.instancia}</li>
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
