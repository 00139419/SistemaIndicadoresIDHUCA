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
import { Dialog } from "primereact/dialog";
import {
  getCatalogo,
  updateEvento,
  detailEvent,
  updatePersonaAfectada,
  deletePersonaAfectada,
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

  // Estados para modales
  const [showModal, setShowModal] = useState(false);
  const [modalData, setModalData] = useState({
    type: "success",
    title: "",
    message: "",
    onAccept: null,
  });
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [confirmData, setConfirmData] = useState({
    message: "",
    onConfirm: null,
    onCancel: null,
  });

  // Función para mostrar modal de respuesta
  const showResponseModal = (type, title, message, onAccept = null) => {
    setModalData({ type, title, message, onAccept });
    setShowModal(true);
  };

  // Función para mostrar modal de confirmación
  const showConfirmDialog = (message, onConfirm, onCancel = null) => {
    setConfirmData({ message, onConfirm, onCancel });
    setShowConfirmModal(true);
  };

  // Cargar catálogos y datos del evento
  useEffect(() => {
  const cargarTodo = async () => {
    try {
      setLoading(true);
      const [
        d, f, e, l, g, dr, sd, p, ss, tp, tv, ar, cv, td, md, me, tr, tpJud, dp,
      ] = await Promise.all([
        getCatalogo({ departamentos: true, cargarDeafult: true }),
        getCatalogo({ fuentes: true, cargarDeafult: true }),
        getCatalogo({ estadoRegistro: true, cargarDeafult: true }),
        getCatalogo({ lugarExacto: true, cargarDeafult: true }),
        getCatalogo({ genero: true, cargarDeafult: true }),
        getCatalogo({ derechos: true, cargarDeafult: true }),
        getCatalogo({ subDerechos: true, cargarDeafult: true, parentId: "DER_1" }),
        getCatalogo({ paises: true, cargarDeafult: true }),
        getCatalogo({ estadoSalud: true, cargarDeafult: true }),
        getCatalogo({ tipoPersona: true, cargarDeafult: true }),
        getCatalogo({ tipoViolencia: true, cargarDeafult: true }),
        getCatalogo({ tipoArma: true, cargarDeafult: true }),
        getCatalogo({ contexto: true, cargarDeafult: true }), // CONTEXTO PARA VIOLENCIA
        getCatalogo({ tipoDetencion: true, cargarDeafult: true }),
        getCatalogo({ motivoDetencion: true, cargarDeafult: true }),
        getCatalogo({ medioExpresion: true, cargarDeafult: true }),
        getCatalogo({ tipoRepresion: true, cargarDeafult: true }), // TIPOS REPRESION
        getCatalogo({ tipoProcesoJudicial: true, cargarDeafult: true }),
        getCatalogo({ duracionProceso: true, cargarDeafult: true }),
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


        console.log("EditarRegistro - mediosExpresion:", me);
      console.log("EditarRegistro - tiposRepresion:", tr);
      

        // Cargar datos del evento
        const eventoData = await detailEvent(id);
        const eventoEntity = eventoData.entity;
        const eventoEditado = transformarEventoParaEdicion(eventoEntity);

        // Precargar municipios de residencia para cada persona
        const municipiosPorPersona = await Promise.all(
          (eventoEditado.personasAfectadas || []).map(async (p) => {
            if (p.departamentoResidencia && p.departamentoResidencia.codigo) {
              try {
                const municipios = await getCatalogo({
                  municipios: true,
                  parentId: p.departamentoResidencia.codigo,
                });
                return municipios;
              } catch {
                return [];
              }
            }
            return [];
          })
        );

        setEvento(eventoEditado);
        setMunicipiosResidenciaList(municipiosPorPersona);

        console.log(evento);
      } catch (err) {
        showResponseModal(
          "error",
          "Error",
          `Error al cargar datos: ${err.message}`
        );
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
        derechosVulnerados: (p.derechosVulnerados || []).map(
          (dv) => dv.derecho
        ),
        // Mejorar la verificación para expresionCensura
        expresionCensura:
          p.expresionCensura &&
          (p.expresionCensura.medioExpresion ||
            p.expresionCensura.tipoRepresion ||
            p.expresionCensura.actorCensor ||
            p.expresionCensura.represaliasLegales !== undefined ||
            p.expresionCensura.represaliasFisicas !== undefined ||
            p.expresionCensura.consecuencia)
            ? {
                medioExpresion: p.expresionCensura.medioExpresion || null,
                tipoRepresion: p.expresionCensura.tipoRepresion || null,
                represaliasLegales:
                  p.expresionCensura.represaliasLegales || false,
                represaliasFisicas:
                  p.expresionCensura.represaliasFisicas || false,
                actorCensor: p.expresionCensura.actorCensor || null,
                consecuencia: p.expresionCensura.consecuencia || "",
              }
            : null,
        // Aplicar la misma lógica a las demás secciones
        violencia:
          p.violencia &&
          (p.violencia.tipoViolencia ||
            p.violencia.artefactoUtilizado ||
            p.violencia.contexto ||
            p.violencia.actorResponsable ||
            p.violencia.estadoSaludActorResponsable ||
            p.violencia.esAsesinato !== undefined ||
            p.violencia.huboProteccion !== undefined ||
            p.violencia.investigacionAbierta !== undefined ||
            p.violencia.respuestaEstado)
            ? p.violencia
            : null,
        detencionIntegridad:
          p.detencionIntegridad &&
          (p.detencionIntegridad.tipoDetencion ||
            p.detencionIntegridad.autoridadInvolucrada ||
            p.detencionIntegridad.motivoDetencion ||
            p.detencionIntegridad.ordenJudicial !== undefined ||
            p.detencionIntegridad.huboTortura !== undefined ||
            p.detencionIntegridad.accesoAbogado !== undefined ||
            p.detencionIntegridad.duracionDias ||
            p.detencionIntegridad.resultado)
            ? p.detencionIntegridad
            : null,
        accesoJusticia:
          p.accesoJusticia &&
          (p.accesoJusticia.tipoProceso ||
            p.accesoJusticia.fechaDenuncia ||
            p.accesoJusticia.tipoDenunciante ||
            p.accesoJusticia.duracionProceso ||
            p.accesoJusticia.accesoAbogado !== undefined ||
            p.accesoJusticia.huboParcialidad !== undefined ||
            p.accesoJusticia.resultadoProceso ||
            p.accesoJusticia.instancia)
            ? p.accesoJusticia
            : null,
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
    if (
      !evento.fechaHecho ||
      !evento.fuente ||
      !evento.estadoActual ||
      !evento.derechoAsociado
    ) {
      showResponseModal(
        "error",
        "Campos Requeridos",
        "Todos los campos principales son obligatorios"
      );
      return;
    }
    const payload = {
      id: evento.id,
      fechaHecho: evento.fechaHecho
        ? new Date(evento.fechaHecho).toISOString().split("T")[0]
        : null,
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
      console.log(payload);

      await updateEvento(payload);
      showResponseModal(
        "success",
        "¡Éxito!",
        "Evento actualizado correctamente",
        () => {
          navigate("/registros");
        }
      );
    } catch (error) {
      showResponseModal(
        "error",
        "Error",
        `Error al actualizar: ${error.message}`
      );
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
      accesoJusticia: persona.accesoJusticia
        ? {
            ...persona.accesoJusticia,
            fechaDenuncia: persona.accesoJusticia.fechaDenuncia
              ? new Date(persona.accesoJusticia.fechaDenuncia)
                  .toISOString()
                  .split("T")[0]
              : null,
          }
        : null,
      fechaHecho: evento.fechaHecho
        ? new Date(evento.fechaHecho).toISOString().split("T")[0]
        : null,
    };
    try {
      console.log("Actualizando persona: ", JSON.stringify(payload));
      await updatePersonaAfectada(payload);
      showResponseModal(
        "success",
        "¡Éxito!",
        "Persona actualizada correctamente"
      );
    } catch (error) {
      showResponseModal(
        "error",
        "Error",
        `Error al actualizar persona: ${error.message}`
      );
    }
  };

  const handleEliminarPersona = async (personaId) => {
    showConfirmDialog(
      "¿Seguro que deseas eliminar esta persona afectada?",
      async () => {
        try {
          await deletePersonaAfectada(evento.id, personaId);
          setEvento((prev) => ({
            ...prev,
            personasAfectadas: prev.personasAfectadas.filter(
              (p) => p.id !== personaId
            ),
          }));
          showResponseModal(
            "success",
            "¡Éxito!",
            "Persona afectada eliminada correctamente"
          );
        } catch (error) {
          showResponseModal(
            "error",
            "Error",
            `Error al eliminar persona: ${error.message}`
          );
        }
      }
    );
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
      {/* Modal de Respuesta */}
      <Dialog
        header={
          <div className="flex align-items-center">
            <i
              className={`fas ${
                modalData.type === "success"
                  ? "fa-check-circle text-green-500"
                  : "fa-exclamation-triangle text-red-500"
              } mr-2`}
            ></i>
            {modalData.title}
          </div>
        }
        visible={showModal}
        onHide={() => setShowModal(false)}
        style={{ width: "350px" }}
        closable={false}
      >
        <div
          className="flex flex-column align-items-center justify-content-center"
          style={{ minHeight: "100px" }}
        >
          <i
            className={`pi ${
              modalData.type === "success"
                ? "pi-check-circle"
                : "pi-times-circle"
            }`}
            style={{
              fontSize: "2rem",
              color: modalData.type === "success" ? "green" : "red",
            }}
          ></i>
          <p className="mt-3 text-center">{modalData.message}</p>
          <Button
            label="Aceptar"
            icon="pi pi-check"
            className={`mt-3 ${
              modalData.type === "success"
                ? "p-button-success"
                : "p-button-danger"
            }`}
            onClick={() => {
              setShowModal(false);
              if (modalData.onAccept) {
                modalData.onAccept();
              }
            }}
          />
        </div>
      </Dialog>

      {/* Modal de Confirmación */}
      <Dialog
        header={
          <div className="flex align-items-center">
            <i className="pi pi-question-circle text-orange-500 mr-2"></i>
            Confirmación
          </div>
        }
        visible={showConfirmModal}
        onHide={() => setShowConfirmModal(false)}
        style={{ width: "400px" }}
        closable={false}
      >
        <div
          className="flex flex-column align-items-center justify-content-center"
          style={{ minHeight: "100px" }}
        >
          <i
            className="pi pi-question-circle"
            style={{ fontSize: "2rem", color: "orange" }}
          ></i>
          <p className="mt-3 text-center">{confirmData.message}</p>
          <div className="flex gap-2 mt-3">
            <Button
              label="Cancelar"
              icon="pi pi-times"
              className="p-button-secondary"
              onClick={() => {
                setShowConfirmModal(false);
                if (confirmData.onCancel) {
                  confirmData.onCancel();
                }
              }}
            />
            <Button
              label="Confirmar"
              icon="pi pi-check"
              className="p-button-danger"
              onClick={() => {
                setShowConfirmModal(false);
                if (confirmData.onConfirm) {
                  confirmData.onConfirm();
                }
              }}
            />
          </div>
        </div>
      </Dialog>
      <Card
        title="✏️ Editar Registro del Hecho"
        className="shadow-4 border-round-lg"
      >
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
            label="Actualizar datos generales del evento"
            icon="pi pi-save"
            className="p-button-primary px-6 py-2 font-bold"
            onClick={handleActualizarEvento}
          />
        </div>
      </Card>

      {evento.personasAfectadas.map((persona, index) => (
        <Card
          key={persona.id || index}
          className="mt-6 mb-4 border-round shadow-2"
        >
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
                {/* Nombre */}
                <div className="field col-12 md:col-4">
                  <label className="mb-2 d-block">Nombre</label>
                  <InputText
                    value={persona.nombre}
                    onChange={(e) =>
                      actualizarPersona(index, "nombre", e.target.value)
                    }
                    className="w-full"
                  />
                </div>
                {/* Edad */}
                <div className="field col-12 md:col-4">
                  <label className="mb-2 d-block">Edad</label>
                  <InputNumber
                    value={persona.edad}
                    onValueChange={(e) =>
                      actualizarPersona(index, "edad", e.value)
                    }
                    showButtons
                    min={0}
                    className="w-full"
                  />
                </div>
                {/* Género */}
                <div className="field col-12 md:col-4">
                  <label className="mb-2 d-block">Género</label>
                  <Dropdown
                    value={persona.genero}
                    onChange={(e) =>
                      actualizarPersona(index, "genero", e.value)
                    }
                    options={generos}
                    optionLabel="descripcion"
                    placeholder="Seleccione género"
                    className="w-full"
                  />
                </div>
                {/* Tipo de persona */}
                <div className="field col-12 md:col-4">
                  <label className="mb-2 d-block">Tipo de persona</label>
                  <Dropdown
                    value={persona.tipoPersona}
                    onChange={(e) =>
                      actualizarPersona(index, "tipoPersona", e.value)
                    }
                    options={tiposPersona}
                    optionLabel="descripcion"
                    placeholder="Seleccione tipo"
                    className="w-full"
                  />
                </div>
                // En la sección de Datos Generales, modifica los campos de
                departamento y municipio de residencia:
                {/* Departamento de residencia */}
                <div className="field col-12 md:col-5">
                  <label className="mb-2 d-block font-semibold">
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
                    disabled={
                      !(
                        persona.nacionalidad &&
                        persona.nacionalidad.codigo === "PAIS_9300"
                      )
                    }
                    onClick={() => {
                      console.log(
                        `DepartamentoResidencia habilitado para persona #${
                          index + 1
                        }:`,
                        persona.nacionalidad
                      );
                    }}
                  />
                </div>
                {/* Municipio de residencia */}
                <div className="field col-12 md:col-5">
                  <label className="mb-2 d-block font-semibold">
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
                    disabled={
                      !(
                        persona.nacionalidad &&
                        persona.nacionalidad.codigo === "PAIS_9300"
                      )
                    }
                    onClick={() => {
                      console.log(
                        `MunicipioResidencia habilitado para persona #${
                          index + 1
                        }:`,
                        persona.nacionalidad
                      );
                    }}
                  />
                </div>
                {/* Nacionalidad - también necesita lógica para limpiar campos */}
                <div className="field col-12 md:col-4">
                  <label className="mb-2 d-block">Nacionalidad</label>
                  <Dropdown
                    value={persona.nacionalidad}
                    onChange={(e) => {
                      const nuevaNacionalidad = e.value;
                      actualizarPersona(
                        index,
                        "nacionalidad",
                        nuevaNacionalidad
                      );
                      console.log(
                        `Nacionalidad seleccionada para persona #${index + 1}:`,
                        nuevaNacionalidad
                      );

                      // Si NO es El Salvador (PAIS_9300), borrar departamento y municipio
                      if (
                        !nuevaNacionalidad ||
                        nuevaNacionalidad.codigo !== "PAIS_9300"
                      ) {
                        actualizarPersona(
                          index,
                          "departamentoResidencia",
                          null
                        );
                        actualizarPersona(index, "municipioResidencia", null);
                        // También limpiar la lista de municipios para esta persona
                        const nuevos = [...municipiosResidenciaList];
                        nuevos[index] = [];
                        setMunicipiosResidenciaList(nuevos);
                      }
                    }}
                    options={paises}
                    optionLabel="descripcion"
                    placeholder="Seleccione país"
                    filter
                    filterPlaceholder="Buscar país..."
                    className="w-full"
                    resetFilterOnHide
                    showClear
                  />
                </div>
                {/* Estado de salud */}
                <div className="field col-12 md:col-4">
                  <label className="mb-2 d-block">Estado de salud</label>
                  <Dropdown
                    value={persona.estadoSalud}
                    onChange={(e) =>
                      actualizarPersona(index, "estadoSalud", e.value)
                    }
                    options={estadosSalud}
                    optionLabel="descripcion"
                    placeholder="Seleccione el estado de la victima"
                    className="w-full"
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
                    persona.violencia ? "Quitar violencia" : "Agregar violencia"
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
                  {/* ¿Hubo asesinato? */}
                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">¿Hubo asesinato?</label>
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
                      className="w-full"
                    />
                  </div>

                  {/* Tipo de violencia */}
                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">Tipo de violencia</label>
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
                      className="w-full"
                    />
                  </div>

                  {/* Artefacto utilizado */}
                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">Artefacto utilizado</label>
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
                      className="w-full"
                    />
                  </div>

                  {/* Contexto */}
                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">Contexto</label>
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
                      className="w-full"
                    />
                  </div>

                  {/* Actor responsable */}
                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">Actor responsable</label>
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
                      className="w-full"
                    />
                  </div>

                  {/* Estado salud actor */}
                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">Estado salud actor</label>
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
                      className="w-full"
                    />
                  </div>

                  {/* ¿Hubo protección? */}
                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">¿Hubo protección?</label>
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
                      className="w-full"
                    />
                  </div>

                  {/* ¿Investigación abierta? */}
                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">
                      ¿Investigación abierta?
                    </label>
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
                      className="w-full"
                    />
                  </div>

                  {/* Respuesta del Estado */}
                  <div className="field col-12">
                    <label className="mb-2 d-block">Respuesta del Estado</label>
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
                  {/* Tipo de proceso judicial */}
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
                      className="w-full"
                    />
                  </div>

                  {/* Fecha de denuncia */}
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

                  {/* Tipo de denunciante */}
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
                      className="w-full"
                    />
                  </div>

                  {/* Duración del proceso */}
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
                      className="w-full"
                    />
                  </div>

                  {/* Acceso a abogado */}
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
                      className="w-full"
                    />
                  </div>

                  {/* Hubo parcialidad */}
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
                      className="w-full"
                    />
                  </div>

                  {/* Resultado del proceso */}
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

                  {/* Instancia */}
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
                <label className="mb-2 d-block">
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
                    <label className="mb-2 d-block">Tipo de detención</label>
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
                      className="w-full"
                    />
                  </div>

                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">
                      ¿Existió orden judicial?
                    </label>
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
                      className="w-full"
                    />
                  </div>

                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">
                      Autoridad involucrada
                    </label>
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
                      className="w-full"
                    />
                  </div>

                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">¿Hubo tortura?</label>
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
                      className="w-full"
                    />
                  </div>

                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">Días de duración</label>
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
                      className="w-full"
                    />
                  </div>

                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">
                      ¿Tuvo acceso a abogado?
                    </label>
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
                      className="w-full"
                    />
                  </div>

                  <div className="field col-12">
                    <label className="mb-2 d-block">Resultado</label>
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
                    <label className="mb-2 d-block">Motivo de detención</label>
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
                      className="w-full"
                    />
                  </div>
                </div>
              )}
            </TabPanel>

            {/* Tab: Expresión / Censura */}
            <TabPanel header="Expresión / Censura">
              <div className="mb-3">
                <label className="mb-2 d-block">
                  ¿Desea registrar censura/represión?
                </label>
                <Button
                  label={
                    persona.expresionCensura
                      ? "Quitar sección"
                      : "Agregar sección"
                  }
                  icon={persona.expresionCensura ? "pi pi-times" : "pi pi-plus"}
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
                    <label className="mb-2 d-block">Medio de expresión</label>
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
                      className="w-full"
                    />
                  </div>

                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">Tipo de represión</label>
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
                      className="w-full"
                    />
                  </div>

                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">Actor censor</label>
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
                      className="w-full"
                    />
                  </div>

                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">
                      ¿Represalias legales?
                    </label>
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
                      className="w-full"
                    />
                  </div>

                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">
                      ¿Represalias físicas?
                    </label>
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
                      className="w-full"
                    />
                  </div>

                  <div className="field col-12">
                    <label className="mb-2 d-block">Consecuencia</label>
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
