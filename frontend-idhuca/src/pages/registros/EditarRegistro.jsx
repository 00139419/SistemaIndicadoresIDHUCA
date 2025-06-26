import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Calendar } from "primereact/calendar";
import { Dropdown } from "primereact/dropdown";
import { InputTextarea } from "primereact/inputtextarea";
import { Button } from "primereact/button";
import { Card } from "primereact/card";
import { InputText } from "primereact/inputtext";
import { InputNumber } from "primereact/inputnumber";
import { MultiSelect } from "primereact/multiselect";
import { TabView, TabPanel } from "primereact/tabview";
import {
  getCatalogo,
  updateEvento,
  detailEvent,
  updatePersonaAfectada,
  deletePersonaAfectada
} from "../../services/RegstrosService";

import "primereact/resources/themes/lara-light-indigo/theme.css";
import "primereact/resources/primereact.min.css";
import "primeicons/primeicons.css";
import "primeflex/primeflex.css";

const EditarRegistro = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  // Estados para los catálogos
  const [departamentos, setDepartamentos] = useState([]);
  const [municipios, setMunicipios] = useState([]);
  const [fuentes, setFuentes] = useState([]);
  const [estados, setEstados] = useState([]);
  const [estadosSalud, setEstadosSalud] = useState([]);
  const [lugaresExactos, setLugaresExactos] = useState([]);
  const [generos, setGeneros] = useState([]);
  const [derechosPrincipales, setDerechosPrincipales] = useState([]);
  const [subDerechos, setSubDerechos] = useState([]);
  const [paises, setPaises] = useState([]);
  const [tiposPersona, setTiposPersona] = useState([]);
  const [tiposViolencia, setTiposViolencia] = useState([]);
  const [artefactos, setArtefactos] = useState([]);
  const [contextosViolencia, setContextosViolencia] = useState([]);
  const [tiposDetencion, setTiposDetencion] = useState([]);
  const [motivosDetencion, setMotivosDetencion] = useState([]);
  const [mediosExpresion, setMediosExpresion] = useState([]);
  const [tiposRepresion, setTiposRepresion] = useState([]);
  const [tiposProcesoJudicial, setTiposProcesoJudicial] = useState([]);
  const [duracionesProceso, setDuracionesProceso] = useState([]);

  // Estados para el evento
  const [evento, setEvento] = useState(null);
  const [municipiosResidenciaList, setMunicipiosResidenciaList] = useState([]);
  const [loading, setLoading] = useState(true);

  // Cargar catálogos y datos del evento
  useEffect(() => {
    const cargarTodo = async () => {
      try {
        setLoading(true);
        const [
          d, f, e, l, g, dr, sd, p, ss, tp, tv, ar, cv, td, md, me, tr, tpJud, dp,
        ] = await Promise.all([
          getCatalogo({ departamentos: true }),
          getCatalogo({ fuentes: true }),
          getCatalogo({ estadoRegistro: true }),
          getCatalogo({ lugarExacto: true }),
          getCatalogo({ genero: true }),
          getCatalogo({ derechos: true }),
          getCatalogo({ subDerechos: true, parentId: "DER_1" }),
          getCatalogo({ paises: true }),
          getCatalogo({ estadoSalud: true }),
          getCatalogo({ tipoPersona: true }),
          getCatalogo({ tipoViolencia: true }),
          getCatalogo({ tipoArma: true }),
          getCatalogo({ tipoPersona: true }),
          getCatalogo({ tipoDetencion: true }),
          getCatalogo({ motivoDetencion: true }),
          getCatalogo({ medioExpresion: true }),
          getCatalogo({ tipoRepresion: true }),
          getCatalogo({ tipoProcesoJudicial: true }),
          getCatalogo({ duracionProceso: true }),
        ]);
        setDepartamentos(d);
        setFuentes(f);
        setEstados(e);
        setLugaresExactos(l);
        setGeneros(g);
        setDerechosPrincipales(dr);
        setSubDerechos(sd);
        setPaises(p);
        setEstadosSalud(ss);
        setTiposPersona(tp);
        setTiposViolencia(tv);
        setArtefactos(ar);
        setContextosViolencia(cv);
        setTiposDetencion(td);
        setMotivosDetencion(md);
        setMediosExpresion(me);
        setTiposRepresion(tr);
        setTiposProcesoJudicial(tpJud);
        setDuracionesProceso(dp);

        // Cargar datos del evento
        const eventoData = await detailEvent(id);
        const eventoEntity = eventoData.entity; // El evento real está en .entity
        setEvento(transformarEventoParaEdicion(eventoEntity));
        setMunicipiosResidenciaList(
          (eventoEntity.personasAfectadas || []).map(() => [])
        );
      } catch (err) {
        alert("Error al cargar datos: " + err.message);
      } finally {
        setLoading(false);
      }
    };
    cargarTodo();
  }, [id]);

  // Cargar municipios cuando cambia el departamento
  useEffect(() => {
    if (evento && evento.departamento) {
      cargarMunicipios(evento.departamento.codigo);
    }
  }, [evento && evento.departamento]);

  const cargarMunicipios = async (codigoDepartamento) => {
    try {
      const response = await getCatalogo({
        municipios: true,
        parentId: codigoDepartamento,
      });
      setMunicipios(response);
    } catch (error) {
      setMunicipios([]);
    }
  };

  // Utilidad para transformar el evento recibido del backend al formato del formulario
  function transformarEventoParaEdicion(data) {
    return {
      id: data.id,
      fechaHecho: data.fechaHecho ? new Date(data.fechaHecho) : null,
      fuente: data.fuente || null,
      estadoActual: data.estadoActual || null,
      derechoAsociado: data.derechoAsociado || null,
      observaciones: data.observaciones || "",
      departamento: data.ubicacion?.departamento || null,
      municipio: data.ubicacion?.municipio || null,
      lugarExacto: data.ubicacion?.lugarExacto || null,
      flagRegimenExcepcion: data.flagRegimenExcepcion || false,
      personasAfectadas: (data.personasAfectadas || []).map((p) => ({
        ...p,
        derechosVulnerados: (p.derechosVulnerados || []).map((dv) => dv.derecho),
        violencia: p.violencia || {},
        detencionIntegridad: p.detencion || {},
        expresionCensura: p.expresion || {},
        accesoJusticia: p.justicia || {},
      })),
    };
  }

  // Handlers de campos principales
  const handleChange = (campo, valor) => {
    setEvento((prev) => ({ ...prev, [campo]: valor }));
  };

  // Handlers de personas
  const actualizarPersona = (index, campo, valor) => {
    const nuevas = [...evento.personasAfectadas];
    nuevas[index][campo] = valor;
    setEvento((prev) => ({
      ...prev,
      personasAfectadas: nuevas,
    }));
  };

  const handleDepartamentoResidenciaChange = async (index, departamento) => {
    actualizarPersona(index, "departamentoResidencia", departamento);
    actualizarPersona(index, "municipioResidencia", null);

    if (!departamento) {
      const nuevos = [...municipiosResidenciaList];
      nuevos[index] = [];
      setMunicipiosResidenciaList(nuevos);
      return;
    }
    try {
      const municipios = await getCatalogo({
        municipios: true,
        parentId: departamento.codigo,
      });
      const nuevos = [...municipiosResidenciaList];
      nuevos[index] = municipios;
      setMunicipiosResidenciaList(nuevos);
    } catch {
      const nuevos = [...municipiosResidenciaList];
      nuevos[index] = [];
      setMunicipiosResidenciaList(nuevos);
    }
  };

  // Guardar cambios
  const handleActualizarEvento = async () => {
    if (!evento.fechaHecho || !evento.fuente || !evento.estadoActual || !evento.derechoAsociado) {
      alert("Todos los campos principales son obligatorios");
      return;
    }
    const payload = {
      id: evento.id,
      fechaHecho: evento.fechaHecho.toISOString().split("T")[0],
      fuente: evento.fuente,
      estadoActual: evento.estadoActual,
      derechoAsociado: evento.derechoAsociado,
      flagRegimenExcepcion: evento.flagRegimenExcepcion,
      observaciones: evento.observaciones,
      ubicacion: {
        id: evento.ubicacionId,
        departamento: evento.departamento,
        municipio: evento.municipio,
        lugarExacto: evento.lugarExacto,
      },
    };
    try {
      await updateEvento(payload);
      alert("Evento actualizado correctamente");
      navigate("/registros");
    } catch (error) {
      alert("Error al actualizar: " + error.message);
    }
  };

  // Función para transformar los derechos vulnerados al formato del backend
  const mapDerechosVulnerados = (derechos) =>
    (derechos || []).map((dv) => ({
      id: dv.id, // Si tienes el id, inclúyelo, si no, omítelo
      derecho: dv.derecho || dv, // Puede venir como objeto o solo el derecho
    }));

  // Función para actualizar una persona afectada
  const handleActualizarPersona = async (persona) => {
    const payload = {
      id: persona.id,
      nombre: persona.nombre,
      genero: persona.genero,
      edad: persona.edad,
      nacionalidad: persona.nacionalidad,
      departamentoResidencia: persona.departamentoResidencia,
      municipioResidencia: persona.municipioResidencia,
      tipoPersona: persona.tipoPersona,
      estadoSalud: persona.estadoSalud,
      derechosVulnerados: mapDerechosVulnerados(persona.derechosVulnerados),
      violencia: persona.violencia || null,
      detencionIntegridad: persona.detencionIntegridad || null,
      expresionCensura: persona.expresionCensura || null,
      accesoJusticia: persona.accesoJusticia || null,
    };
    try {
      await updatePersonaAfectada(payload);
      alert("Persona actualizada correctamente");
    } catch (error) {
      alert("Error al actualizar persona: " + error.message);
    }
  };

  const handleEliminarPersona = async (personaId) => {
    if (!window.confirm("¿Seguro que deseas eliminar esta persona afectada?")) return;
    try {
      await deletePersonaAfectada(evento.id, personaId);
      setEvento((prev) => ({
        ...prev,
        personasAfectadas: prev.personasAfectadas.filter((p) => p.id !== personaId),
      }));
      alert("Persona afectada eliminada correctamente");
    } catch (error) {
      alert("Error al eliminar persona: " + error.message);
    }
  };

  if (loading || !evento) {
    return (
      <div className="flex justify-content-center align-items-center min-h-screen">
        <i className="pi pi-spin pi-spinner" style={{ fontSize: "2rem" }} />
        <span className="ml-3 text-xl">Cargando datos...</span>
      </div>
    );
  }

  return (
    <div className="p-4 surface-100 min-h-screen">
      <Card title="✏️ Editar Registro del Hecho" className="shadow-4 border-round-lg">
        <div className="formgrid grid p-fluid gap-3">
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Fecha del hecho</label>
            <Calendar
              value={evento.fechaHecho}
              onChange={(e) => handleChange("fechaHecho", e.value)}
              dateFormat="yy-mm-dd"
              showIcon
              className="w-full"
            />
          </div>
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Fuente</label>
            <Dropdown
              value={evento.fuente}
              onChange={(e) => handleChange("fuente", e.value)}
              options={fuentes}
              optionLabel="descripcion"
              placeholder="Seleccione una fuente"
              className="w-full"
            />
          </div>
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Estado Actual</label>
            <Dropdown
              value={evento.estadoActual}
              onChange={(e) => handleChange("estadoActual", e.value)}
              options={estados}
              optionLabel="descripcion"
              placeholder="Seleccione un estado"
              className="w-full"
            />
          </div>
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Derecho Asociado</label>
            <Dropdown
              value={evento.derechoAsociado}
              onChange={(e) => handleChange("derechoAsociado", e.value)}
              options={derechosPrincipales}
              optionLabel="descripcion"
              placeholder="Seleccione un derecho"
              className="w-full"
            />
          </div>
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Departamento</label>
            <Dropdown
              value={evento.departamento}
              onChange={(e) => {
                handleChange("departamento", e.value);
                cargarMunicipios(e.value?.codigo);
              }}
              options={departamentos}
              optionLabel="descripcion"
              placeholder="Seleccione un departamento"
              className="w-full"
            />
          </div>
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Municipio</label>
            <Dropdown
              value={evento.municipio}
              onChange={(e) => handleChange("municipio", e.value)}
              options={municipios}
              optionLabel="descripcion"
              placeholder={
                evento.departamento
                  ? "Seleccione un municipio"
                  : "Seleccione un departamento primero"
              }
              className="w-full"
            />
          </div>
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Lugar Exacto</label>
            <Dropdown
              value={evento.lugarExacto}
              onChange={(e) => handleChange("lugarExacto", e.value)}
              options={lugaresExactos}
              optionLabel="descripcion"
              placeholder="Seleccione un lugar"
              className="w-full"
            />
          </div>
          <div className="col-12">
            <label className="font-semibold mb-2 block">Observaciones</label>
            <InputTextarea
              value={evento.observaciones}
              onChange={(e) => handleChange("observaciones", e.target.value)}
              rows={4}
              className="w-full"
              autoResize
            />
          </div>
        </div>
        <div className="flex justify-content-center mt-4">
          <Button
            label="Actualizar evento"
            icon="pi pi-save"
            className="p-button-primary px-6 py-2 font-bold"
            onClick={handleActualizarEvento}
          />
        </div>
      </Card>
      
      {evento.personasAfectadas.map((persona, index) => (
        <Card key={persona.id || index} className="mt-6 mb-4 border-round shadow-2">
          <div className="flex justify-content-between align-items-center mb-3">
            <h5 className="m-0">👤 Persona #{index + 1}</h5>
            <div>
              <Button
                icon="pi pi-save"
                className="p-button-success p-button-text"
                label="Actualizar persona"
                onClick={() => handleActualizarPersona(persona)}
                tooltip="Actualizar persona"
              />
              <Button
                icon="pi pi-trash"
                className="p-button-danger p-button-text ml-2"
                label="Eliminar persona"
                onClick={() => handleEliminarPersona(persona.id)}
                tooltip="Eliminar persona"
              />
            </div>
          </div>
          <TabView>
            {/* Tab: Datos Generales */}
            <TabPanel header="Datos Generales">
              <div className="formgrid grid">
                <div className="field col-12 md:col-4">
                  <label>Nombre</label>
                  <InputText
                    value={persona.nombre}
                    onChange={(e) =>
                      actualizarPersona(index, "nombre", e.target.value)
                    }
                  />
                </div>
                <div className="field col-12 md:col-4">
                  <label>Edad</label>
                  <InputNumber
                    value={persona.edad}
                    onValueChange={(e) =>
                      actualizarPersona(index, "edad", e.value)
                    }
                    showButtons
                    min={0}
                  />
                </div>
                <div className="field col-12 md:col-4">
                  <label>Género</label>
                  <Dropdown
                    value={persona.genero}
                    onChange={(e) =>
                      actualizarPersona(index, "genero", e.value)
                    }
                    options={generos}
                    optionLabel="descripcion"
                    placeholder="Seleccione género"
                  />
                </div>
                <div className="field col-12 md:col-4">
                  <label>Tipo de persona</label>
                  <Dropdown
                    value={persona.tipoPersona}
                    onChange={(e) =>
                      actualizarPersona(index, "tipoPersona", e.value)
                    }
                    options={tiposPersona}
                    optionLabel="descripcion"
                    placeholder="Seleccione tipo"
                  />
                </div>
                <div className="col-12 md:col-5">
                  <label className="font-semibold mb-2 block">
                    Departamento de residencia
                  </label>
                  <Dropdown
                    value={persona.departamentoResidencia}
                    onChange={(e) =>
                      handleDepartamentoResidenciaChange(index, e.value)
                    }
                    options={departamentos}
                    optionLabel="descripcion"
                    placeholder="Seleccione un departamento"
                    className="w-full"
                  />
                </div>
                <div className="col-12 md:col-5">
                  <label className="font-semibold mb-2 block">
                    Municipio de residencia
                  </label>
                  <Dropdown
                    value={persona.municipioResidencia}
                    onChange={(e) =>
                      actualizarPersona(index, "municipioResidencia", e.value)
                    }
                    options={municipiosResidenciaList[index] || []}
                    optionLabel="descripcion"
                    placeholder={
                      persona.departamentoResidencia
                        ? "Seleccione un municipio"
                        : "Seleccione un departamento primero"
                    }
                    className="w-full"
                  />
                </div>
                <div className="field col-12 md:col-4">
                  <label>Nacionalidad</label>
                  <Dropdown
                    value={persona.nacionalidad}
                    onChange={(e) =>
                      actualizarPersona(index, "nacionalidad", e.value)
                    }
                    options={paises}
                    optionLabel="descripcion"
                    placeholder="Seleccione país"
                    filter
                    filterPlaceholder="Buscar país..."
                    className="w-full"
                  />
                </div>
                <div className="field col-12 md:col-4">
                  <label>Estado de salud</label>
                  <Dropdown
                    value={persona.estadoSalud}
                    onChange={(e) =>
                      actualizarPersona(index, "estadoSalud", e.value)
                    }
                    options={estadosSalud}
                    optionLabel="descripcion"
                    placeholder="Seleccione el estado de la victima"
                  />
                </div>
              </div>
            </TabPanel>

            {/* Tab: Derechos vulnerados */}
            <TabPanel header="Derechos Vulnerados">
              <MultiSelect
                value={persona.derechosVulnerados}
                onChange={(e) =>
                  actualizarPersona(index, "derechosVulnerados", e.value)
                }
                options={subDerechos}
                optionLabel="descripcion"
                placeholder="Seleccione derechos"
                className="w-full"
                display="chip"
                filter
                filterPlaceholder="Buscar derecho..."
              />
            </TabPanel>

            {/* Tab: Violencia */}
            <TabPanel header="Violencia">
              <div className="mb-3">
                <label className="mr-2">¿Desea registrar violencia?</label>
                <Button
                  label={
                    persona.violencia
                      ? "Quitar violencia"
                      : "Agregar violencia"
                  }
                  icon={persona.violencia ? "pi pi-times" : "pi pi-plus"}
                  className={`p-button-${
                    persona.violencia ? "danger" : "success"
                  }`}
                  onClick={() =>
                    actualizarPersona(
                      index,
                      "violencia",
                      persona.violencia
                        ? null
                        : {
                            esAsesinato: false,
                            tipoViolencia: null,
                            artefactoUtilizado: null,
                            contexto: null,
                            actorResponsable: null,
                            estadoSaludActorResponsable: null,
                            huboProteccion: false,
                            investigacionAbierta: false,
                            respuestaEstado: "",
                          }
                    )
                  }
                />
              </div>
              {persona.violencia && (
                <div className="formgrid grid">
                  <div className="field col-12 md:col-4">
                    <label>¿Hubo asesinato?</label>
                    <Dropdown
                      value={persona.violencia.esAsesinato}
                      options={[
                        { label: "Sí", value: true },
                        { label: "No", value: false },
                      ]}
                      onChange={(e) =>
                        actualizarPersona(index, "violencia", {
                          ...persona.violencia,
                          esAsesinato: e.value,
                        })
                      }
                      placeholder="Seleccione una opción"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>Tipo de violencia</label>
                    <Dropdown
                      value={persona.violencia.tipoViolencia}
                      options={tiposViolencia}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "violencia", {
                          ...persona.violencia,
                          tipoViolencia: e.value,
                        })
                      }
                      placeholder="Seleccione tipo"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>Artefacto utilizado</label>
                    <Dropdown
                      value={persona.violencia.artefactoUtilizado}
                      options={artefactos}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "violencia", {
                          ...persona.violencia,
                          artefactoUtilizado: e.value,
                        })
                      }
                      placeholder="Seleccione artefacto"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>Contexto</label>
                    <Dropdown
                      value={persona.violencia.contexto}
                      options={contextosViolencia}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "violencia", {
                          ...persona.violencia,
                          contexto: e.value,
                        })
                      }
                      placeholder="Seleccione contexto"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>Actor responsable</label>
                    <Dropdown
                      value={persona.violencia.actorResponsable}
                      options={tiposPersona}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "violencia", {
                          ...persona.violencia,
                          actorResponsable: e.value,
                        })
                      }
                      placeholder="Seleccione actor"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>Estado salud actor</label>
                    <Dropdown
                      value={persona.violencia.estadoSaludActorResponsable}
                      options={estadosSalud}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "violencia", {
                          ...persona.violencia,
                          estadoSaludActorResponsable: e.value,
                        })
                      }
                      placeholder="Seleccione estado"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>¿Hubo protección?</label>
                    <Dropdown
                      value={persona.violencia.huboProteccion}
                      options={[
                        { label: "Sí", value: true },
                        { label: "No", value: false },
                      ]}
                      onChange={(e) =>
                        actualizarPersona(index, "violencia", {
                          ...persona.violencia,
                          huboProteccion: e.value,
                        })
                      }
                      placeholder="Seleccione una opción"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>¿Investigación abierta?</label>
                    <Dropdown
                      value={persona.violencia.investigacionAbierta}
                      options={[
                        { label: "Sí", value: true },
                        { label: "No", value: false },
                      ]}
                      onChange={(e) =>
                        actualizarPersona(index, "violencia", {
                          ...persona.violencia,
                          investigacionAbierta: e.value,
                        })
                      }
                      placeholder="Seleccione una opción"
                    />
                  </div>
                  <div className="field col-12">
                    <label>Respuesta del Estado</label>
                    <InputTextarea
                      value={persona.violencia.respuestaEstado}
                      onChange={(e) =>
                        actualizarPersona(index, "violencia", {
                          ...persona.violencia,
                          respuestaEstado: e.target.value,
                        })
                      }
                      rows={3}
                      autoResize
                      className="w-full"
                    />
                  </div>
                </div>
              )}
            </TabPanel>

            {/* Tab: Acceso a Justicia */}
            <TabPanel header="Acceso a Justicia">
              <div className="mb-3">
                <label className="mr-2">
                  ¿Desea registrar acceso a justicia?
                </label>
                <Button
                  label={
                    persona.accesoJusticia
                      ? "Quitar sección"
                      : "Agregar sección"
                  }
                  icon={persona.accesoJusticia ? "pi pi-times" : "pi pi-plus"}
                  className={`p-button-${
                    persona.accesoJusticia ? "danger" : "success"
                  }`}
                  onClick={() =>
                    actualizarPersona(
                      index,
                      "accesoJusticia",
                      persona.accesoJusticia
                        ? null
                        : {
                            tipoProceso: null,
                            fechaDenuncia: null,
                            tipoDenunciante: null,
                            duracionProceso: null,
                            accesoAbogado: false,
                            huboParcialidad: false,
                            resultadoProceso: "",
                            instancia: "",
                          }
                    )
                  }
                />
              </div>
              {persona.accesoJusticia && (
                <div className="formgrid grid">
                  <div className="field col-12 md:col-4">
                    <label>Tipo de proceso judicial</label>
                    <Dropdown
                      value={persona.accesoJusticia.tipoProceso}
                      options={tiposProcesoJudicial}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "accesoJusticia", {
                          ...persona.accesoJusticia,
                          tipoProceso: e.value,
                        })
                      }
                      placeholder="Seleccione tipo"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>Fecha de denuncia</label>
                    <Calendar
                      value={
                        persona.accesoJusticia.fechaDenuncia
                          ? new Date(persona.accesoJusticia.fechaDenuncia)
                          : null
                      }
                      onChange={(e) =>
                        actualizarPersona(index, "accesoJusticia", {
                          ...persona.accesoJusticia,
                          fechaDenuncia: e.value,
                        })
                      }
                      dateFormat="yy-mm-dd"
                      showIcon
                      className="w-full"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>Tipo de denunciante</label>
                    <Dropdown
                      value={persona.accesoJusticia.tipoDenunciante}
                      options={tiposPersona}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "accesoJusticia", {
                          ...persona.accesoJusticia,
                          tipoDenunciante: e.value,
                        })
                      }
                      placeholder="Seleccione"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>Duración del proceso</label>
                    <Dropdown
                      value={persona.accesoJusticia.duracionProceso}
                      options={duracionesProceso}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "accesoJusticia", {
                          ...persona.accesoJusticia,
                          duracionProceso: e.value,
                        })
                      }
                      placeholder="Seleccione duración"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>¿Tuvo acceso a abogado?</label>
                    <Dropdown
                      value={persona.accesoJusticia.accesoAbogado}
                      options={[
                        { label: "Sí", value: true },
                        { label: "No", value: false },
                      ]}
                      onChange={(e) =>
                        actualizarPersona(index, "accesoJusticia", {
                          ...persona.accesoJusticia,
                          accesoAbogado: e.value,
                        })
                      }
                      placeholder="Seleccione"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>¿Hubo parcialidad?</label>
                    <Dropdown
                      value={persona.accesoJusticia.huboParcialidad}
                      options={[
                        { label: "Sí", value: true },
                        { label: "No", value: false },
                      ]}
                      onChange={(e) =>
                        actualizarPersona(index, "accesoJusticia", {
                          ...persona.accesoJusticia,
                          huboParcialidad: e.value,
                        })
                      }
                      placeholder="Seleccione"
                    />
                  </div>
                  <div className="field col-12 md:col-6">
                    <label>Resultado del proceso</label>
                    <InputTextarea
                      value={persona.accesoJusticia.resultadoProceso}
                      onChange={(e) =>
                        actualizarPersona(index, "accesoJusticia", {
                          ...persona.accesoJusticia,
                          resultadoProceso: e.target.value,
                        })
                      }
                      rows={2}
                      className="w-full"
                    />
                  </div>
                  <div className="field col-12 md:col-6">
                    <label>Instancia</label>
                    <InputText
                      value={persona.accesoJusticia.instancia}
                      onChange={(e) =>
                        actualizarPersona(index, "accesoJusticia", {
                          ...persona.accesoJusticia,
                          instancia: e.target.value,
                        })
                      }
                      className="w-full"
                    />
                  </div>
                </div>
              )}
            </TabPanel>

            {/* Tab: Detención / Integridad */}
            <TabPanel header="Detención / Integridad">
              <div className="mb-3">
                <label className="mr-2">
                  ¿Desea registrar información de detención?
                </label>
                <Button
                  label={
                    persona.detencionIntegridad
                      ? "Quitar sección"
                      : "Agregar sección"
                  }
                  icon={
                    persona.detencionIntegridad ? "pi pi-times" : "pi pi-plus"
                  }
                  className={`p-button-${
                    persona.detencionIntegridad ? "danger" : "success"
                  }`}
                  onClick={() =>
                    actualizarPersona(
                      index,
                      "detencionIntegridad",
                      persona.detencionIntegridad
                        ? null
                        : {
                            tipoDetencion: null,
                            ordenJudicial: false,
                            autoridadInvolucrada: null,
                            huboTortura: false,
                            duracionDias: null,
                            accesoAbogado: false,
                            resultado: "",
                            motivoDetencion: null,
                          }
                    )
                  }
                />
              </div>
              {persona.detencionIntegridad && (
                <div className="formgrid grid">
                  <div className="field col-12 md:col-4">
                    <label>Tipo de detención</label>
                    <Dropdown
                      value={persona.detencionIntegridad.tipoDetencion}
                      options={tiposDetencion}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "detencionIntegridad", {
                          ...persona.detencionIntegridad,
                          tipoDetencion: e.value,
                        })
                      }
                      placeholder="Seleccione tipo"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>¿Existió orden judicial?</label>
                    <Dropdown
                      value={persona.detencionIntegridad.ordenJudicial}
                      options={[
                        { label: "Sí", value: true },
                        { label: "No", value: false },
                      ]}
                      onChange={(e) =>
                        actualizarPersona(index, "detencionIntegridad", {
                          ...persona.detencionIntegridad,
                          ordenJudicial: e.value,
                        })
                      }
                      placeholder="Seleccione una opción"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>Autoridad involucrada</label>
                    <Dropdown
                      value={persona.detencionIntegridad.autoridadInvolucrada}
                      options={tiposPersona}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "detencionIntegridad", {
                          ...persona.detencionIntegridad,
                          autoridadInvolucrada: e.value,
                        })
                      }
                      placeholder="Seleccione autoridad"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>¿Hubo tortura?</label>
                    <Dropdown
                      value={persona.detencionIntegridad.huboTortura}
                      options={[
                        { label: "Sí", value: true },
                        { label: "No", value: false },
                      ]}
                      onChange={(e) =>
                        actualizarPersona(index, "detencionIntegridad", {
                          ...persona.detencionIntegridad,
                          huboTortura: e.value,
                        })
                      }
                      placeholder="Seleccione una opción"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>Días de duración</label>
                    <InputNumber
                      value={persona.detencionIntegridad.duracionDias}
                      onValueChange={(e) =>
                        actualizarPersona(index, "detencionIntegridad", {
                          ...persona.detencionIntegridad,
                          duracionDias: e.value,
                        })
                      }
                      showButtons
                      min={0}
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>¿Tuvo acceso a abogado?</label>
                    <Dropdown
                      value={persona.detencionIntegridad.accesoAbogado}
                      options={[
                        { label: "Sí", value: true },
                        { label: "No", value: false },
                      ]}
                      onChange={(e) =>
                        actualizarPersona(index, "detencionIntegridad", {
                          ...persona.detencionIntegridad,
                          accesoAbogado: e.value,
                        })
                      }
                      placeholder="Seleccione una opción"
                    />
                  </div>
                  <div className="field col-12">
                    <label>Resultado</label>
                    <InputTextarea
                      value={persona.detencionIntegridad.resultado}
                      onChange={(e) =>
                        actualizarPersona(index, "detencionIntegridad", {
                          ...persona.detencionIntegridad,
                          resultado: e.target.value,
                        })
                      }
                      rows={2}
                      className="w-full"
                    />
                  </div>
                  <div className="field col-12 md:col-6">
                    <label>Motivo de detención</label>
                    <Dropdown
                      value={persona.detencionIntegridad.motivoDetencion}
                      options={motivosDetencion}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "detencionIntegridad", {
                          ...persona.detencionIntegridad,
                          motivoDetencion: e.value,
                        })
                      }
                      placeholder="Seleccione motivo"
                    />
                  </div>
                </div>
              )}
            </TabPanel>

            {/* Tab: Expresión / Censura */}
            <TabPanel header="Expresión / Censura">
              <div className="mb-3">
                <label className="mr-2">
                  ¿Desea registrar censura/represión?
                </label>
                <Button
                  label={
                    persona.expresionCensura
                      ? "Quitar sección"
                      : "Agregar sección"
                  }
                  icon={
                    persona.expresionCensura ? "pi pi-times" : "pi pi-plus"
                  }
                  className={`p-button-${
                    persona.expresionCensura ? "danger" : "success"
                  }`}
                  onClick={() =>
                    actualizarPersona(
                      index,
                      "expresionCensura",
                      persona.expresionCensura
                        ? null
                        : {
                            medioExpresion: null,
                            tipoRepresion: null,
                            represaliasLegales: false,
                            represaliasFisicas: false,
                            actorCensor: null,
                            consecuencia: "",
                          }
                    )
                  }
                />
              </div>
              {persona.expresionCensura && (
                <div className="formgrid grid">
                  <div className="field col-12 md:col-4">
                    <label>Medio de expresión</label>
                    <Dropdown
                      value={persona.expresionCensura.medioExpresion}
                      options={mediosExpresion}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "expresionCensura", {
                          ...persona.expresionCensura,
                          medioExpresion: e.value,
                        })
                      }
                      placeholder="Seleccione medio"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>Tipo de represión</label>
                    <Dropdown
                      value={persona.expresionCensura.tipoRepresion}
                      options={tiposRepresion}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "expresionCensura", {
                          ...persona.expresionCensura,
                          tipoRepresion: e.value,
                        })
                      }
                      placeholder="Seleccione tipo"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>Actor censor</label>
                    <Dropdown
                      value={persona.expresionCensura.actorCensor}
                      options={tiposPersona}
                      optionLabel="descripcion"
                      onChange={(e) =>
                        actualizarPersona(index, "expresionCensura", {
                          ...persona.expresionCensura,
                          actorCensor: e.value,
                        })
                      }
                      placeholder="Seleccione actor"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>¿Represalias legales?</label>
                    <Dropdown
                      value={persona.expresionCensura.represaliasLegales}
                      options={[
                        { label: "Sí", value: true },
                        { label: "No", value: false },
                      ]}
                      onChange={(e) =>
                        actualizarPersona(index, "expresionCensura", {
                          ...persona.expresionCensura,
                          represaliasLegales: e.value,
                        })
                      }
                      placeholder="Seleccione"
                    />
                  </div>
                  <div className="field col-12 md:col-4">
                    <label>¿Represalias físicas?</label>
                    <Dropdown
                      value={persona.expresionCensura.represaliasFisicas}
                      options={[
                        { label: "Sí", value: true },
                        { label: "No", value: false },
                      ]}
                      onChange={(e) =>
                        actualizarPersona(index, "expresionCensura", {
                          ...persona.expresionCensura,
                          represaliasFisicas: e.value,
                        })
                      }
                      placeholder="Seleccione"
                    />
                  </div>
                  <div className="field col-12">
                    <label>Consecuencia</label>
                    <InputTextarea
                      value={persona.expresionCensura.consecuencia}
                      onChange={(e) =>
                        actualizarPersona(index, "expresionCensura", {
                          ...persona.expresionCensura,
                          consecuencia: e.target.value,
                        })
                      }
                      rows={2}
                      className="w-full"
                    />
                  </div>
                </div>
              )}
            </TabPanel>
          </TabView>
        </Card>
      ))}
{/*
      <div className="flex justify-content-end mb-3 gap-2">
        <Button
          label="Actualizar todas las personas"
          icon="pi pi-save"
          className="p-button-success"
          onClick={async () => {
            try {
              for (const persona of evento.personasAfectadas) {
                await handleActualizarPersona(persona);
              }
              alert("Todas las personas actualizadas correctamente");
            } catch (error) {
              alert("Error al actualizar personas: " + error.message);
            }
          }}
        />
      </div>
      */}
    </div>
  );
};

export default EditarRegistro;