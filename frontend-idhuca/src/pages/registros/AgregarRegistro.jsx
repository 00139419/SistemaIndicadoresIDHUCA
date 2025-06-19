import React, { useState } from "react";
import { Calendar } from "primereact/calendar";
import { Dropdown } from "primereact/dropdown";
import { InputTextarea } from "primereact/inputtextarea";
import { Button } from "primereact/button";
import { Card } from "primereact/card";
import { InputText } from "primereact/inputtext";
import { InputNumber } from "primereact/inputnumber";
import { MultiSelect } from "primereact/multiselect";
import { TabView, TabPanel } from "primereact/tabview";
import { getCatalogo } from "./../../services/RegstrosService";
import { useEffect } from "react";

import "primereact/resources/themes/lara-light-indigo/theme.css";
import "primereact/resources/primereact.min.css";
import "primeicons/primeicons.css";
import "primeflex/primeflex.css";

const AgregarRegistro = () => {
  const [fechaHecho, setFechaHecho] = useState(null);
  const [fuente, setFuente] = useState(null);
  const [estadoActual, setEstadoActual] = useState(null);
  const [derechoAsociado, setDerechoAsociado] = useState(null);
  const [observaciones, setObservaciones] = useState("");
  const [departamento, setDepartamento] = useState(null);
  const [municipio, setMunicipio] = useState(null);
  const [lugarExacto, setLugarExacto] = useState(null);
  const [personas, setPersonas] = useState([]);
  const [municipiosResidenciaList, setMunicipiosResidenciaList] = useState([]);

  const [departamentos, setDepartamentos] = useState([]);
  const [municipios, setMunicipios] = useState([]);
  const [fuentes, setFuentes] = useState([]);
  const [estados, setEstados] = useState([]);
  const [estadosSalud, setEstadosSalud] = useState([]);
  const [lugaresExactos, setLugaresExactos] = useState([]);
  const [generos, setGeneros] = useState([]);
  const [derechos, setDerechos] = useState([]);
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

  const [loadingCatalogos, setLoadingCatalogos] = useState(true);

  useEffect(() => {
    cargarCatalogos();
  }, []);

  const cargarCatalogos = async () => {
    try {
      const [
        d,
        f,
        e,
        l,
        g,
        sd,
        p,
        ss,
        tp,
        tv,
        ar,
        cv,
        td,
        md,
        me,
        tr,
        tpJud,
        dp,
      ] = await Promise.all([
        getCatalogo({ departamentos: true }),
        getCatalogo({ fuentes: true }),
        getCatalogo({ estadoRegistro: true }),
        getCatalogo({ lugarExacto: true }),
        getCatalogo({ genero: true }),
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

      setTiposPersona(tp);
      setEstadosSalud(ss);
      setDepartamentos(d);
      setFuentes(f);
      setEstados(e);
      setLugaresExactos(l);
      setGeneros(g);
      setDerechos(sd);
      setPaises(p);
      setTiposViolencia(tv);
      setArtefactos(ar);
      setContextosViolencia(cv);
      setTiposDetencion(td);
      setMotivosDetencion(md);
      setMediosExpresion(me);
      setTiposRepresion(tr);
      setTiposProcesoJudicial(tpJud);
      setDuracionesProceso(dp);
    } catch (error) {
      console.error("Error al cargar catálogos", error);
    } finally {
      setLoadingCatalogos(false);
    }
  };

  useEffect(() => {
    if (departamento) {
      setMunicipio(null); // Reiniciar selección
      cargarMunicipios(departamento.codigo);
    } else {
      setMunicipio(null);
      setMunicipios([]);
    }
  }, [departamento]);

  const cargarMunicipios = async (codigoDepartamento) => {
    try {
      const response = await getCatalogo({
        municipios: true,
        parentId: codigoDepartamento,
      });

      setMunicipios(response);
    } catch (error) {
      console.error("Error al cargar municipios", error);
      setMunicipios([]);
    }
  };

  const handleGuardar = () => {
    const json = {
      fechaHecho: fechaHecho?.toISOString().split("T")[0],
      fuente: { codigo: fuente?.codigo },
      estadoActual: { codigo: estadoActual?.codigo },
      derechoAsociado: { codigo: derechoAsociado?.codigo },
      observaciones,
      ubicacion: {
        departamento: { codigo: departamento?.codigo },
        municipio: { codigo: municipio?.codigo },
        lugarExacto: { codigo: lugarExacto?.codigo },
      },
    };

    console.log("JSON final (sin personas aún):", json);
  };

  const handleDepartamentoResidenciaChange = async (index, departamento) => {
    actualizarPersona(index, "departamentoResidencia", departamento);
    actualizarPersona(index, "municipioResidencia", null); // Reiniciar municipio

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
    } catch (err) {
      console.error("Error al cargar municipios residencia", err);
      const nuevos = [...municipiosResidenciaList];
      nuevos[index] = [];
      setMunicipiosResidenciaList(nuevos);
    }
  };

  const handleAgregarPersona = () => {
    setPersonas([...personas, personaVacia()]);
    setMunicipiosResidenciaList([...municipiosResidenciaList, []]);
  };

  const handleEliminarPersona = (index) => {
    const nuevasPersonas = [...personas];
    nuevasPersonas.splice(index, 1);
    setPersonas(nuevasPersonas);

    const nuevosMunicipios = [...municipiosResidenciaList];
    nuevosMunicipios.splice(index, 1);
    setMunicipiosResidenciaList(nuevosMunicipios);
  };

  const handleDuplicarUltima = () => {
    if (personas.length === 0) return;
    const ultima = { ...personas[personas.length - 1] };
    setPersonas([...personas, ultima]);
  };

  const actualizarPersona = (index, campo, valor) => {
    const nuevas = [...personas];
    nuevas[index][campo] = valor;
    setPersonas(nuevas);
  };

  const personaVacia = () => ({
    nombre: "",
    edad: null,
    genero: null,
    nacionalidad: null,
    departamentoResidencia: null,
    municipioResidencia: null,
    tipoPersona: null,
    estadoSalud: null,
    derechosVulnerados: [],
    violencia: {},
    detencionIntegridad: {},
    expresionCensura: {},
    accesoJusticia: {},
  });

  if (loadingCatalogos) {
    return (
      <div className="flex justify-content-center align-items-center min-h-screen">
        <i className="pi pi-spin pi-spinner" style={{ fontSize: "2rem" }} />
        <span className="ml-3 text-xl">Cargando catálogos...</span>
      </div>
    );
  }

  return (
    <div className="p-4 surface-100 min-h-screen">
      <Card title="📝 Registro del Hecho" className="shadow-4 border-round-lg">
        <div className="formgrid grid p-fluid gap-3">
          {/* Fecha del hecho */}
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Fecha del hecho</label>
            <Calendar
              value={fechaHecho}
              onChange={(e) => setFechaHecho(e.value)}
              dateFormat="yy-mm-dd"
              showIcon
              className="w-full"
            />
          </div>

          {/* Fuente */}
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Fuente</label>
            <Dropdown
              value={fuente}
              onChange={(e) => setFuente(e.value)}
              options={fuentes}
              optionLabel="descripcion"
              placeholder="Seleccione una fuente"
              className="w-full"
            />
          </div>

          {/* Estado Actual */}
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Estado Actual</label>
            <Dropdown
              value={estadoActual}
              onChange={(e) => setEstadoActual(e.value)}
              options={estados}
              optionLabel="descripcion"
              placeholder="Seleccione un estado"
              className="w-full"
            />
          </div>

          {/* Departamento */}
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Departamento</label>
            <Dropdown
              value={departamento}
              onChange={(e) => setDepartamento(e.value)}
              options={departamentos}
              optionLabel="descripcion"
              placeholder="Seleccione un departamento"
              className="w-full"
            />
          </div>

          {/* Municipios */}
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Municipio</label>
            <Dropdown
              value={municipio}
              onChange={(e) => setMunicipio(e.value)}
              options={municipios}
              optionLabel="descripcion"
              pl
              placeholder={
                departamento
                  ? "Seleccione un municipio"
                  : "Seleccione un departamento primero"
              }
              className="w-full"
            />
          </div>

          {/* Lugar exacto */}
          <div className="col-12 md:col-5">
            <label className="font-semibold mb-2 block">Lugar Exacto</label>
            <Dropdown
              value={lugarExacto}
              onChange={(e) => setLugarExacto(e.value)}
              options={lugaresExactos}
              optionLabel="descripcion"
              placeholder="Seleccione un lugar"
              className="w-full"
            />
          </div>

          {/* Observaciones */}
          <div className="col-12">
            <label className="font-semibold mb-2 block">Observaciones</label>
            <InputTextarea
              value={observaciones}
              onChange={(e) => setObservaciones(e.target.value)}
              rows={4}
              className="w-full"
              autoResize
            />
          </div>
        </div>
      </Card>
      <Card
        title="👥 Personas Afectadas"
        className="mt-5 shadow-4 border-round-lg"
      >
        <div className="flex justify-content-end mb-3 gap-2">
          <Button
            label="Agregar Persona"
            icon="pi pi-user-plus"
            onClick={handleAgregarPersona}
          />
          {personas.length > 0 && (
            <Button
              label="Duplicar Última"
              icon="pi pi-copy"
              className="p-button-secondary"
              onClick={handleDuplicarUltima}
            />
          )}
        </div>

        {personas.map((persona, index) => (
          <Card key={index} className="mb-4 border-round shadow-2">
            <div className="flex justify-content-between align-items-center mb-3">
              <h5 className="m-0">👤 Persona #{index + 1}</h5>
              <Button
                icon="pi pi-trash"
                className="p-button-danger p-button-text"
                onClick={() => handleEliminarPersona(index)}
                tooltip="Eliminar persona"
              />
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
                      placeholder="Seleccione género"
                    />
                  </div>

                  {/* Departamento de residencia */}
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

                  {/* Municipio de residencia */}
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

                  {/* Otros campos como nacionalidad, residencia, tipoPersona, etc */}
                </div>
              </TabPanel>

              {/* Tab: Derechos vulnerados */}
              <TabPanel header="Derechos Vulnerados">
                <MultiSelect
                  value={persona.derechosVulnerados}
                  onChange={(e) =>
                    actualizarPersona(index, "derechosVulnerados", e.value)
                  }
                  options={derechos}
                  optionLabel="descripcion"
                  placeholder="Seleccione derechos"
                  className="w-full"
                  display="chip"
                  filter
                  filterPlaceholder="Buscar derecho..."
                />
              </TabPanel>

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
      </Card>
      {/* Botón */}
      <div className="flex justify-content-center mt-4">
        <Button
          label="Guardar datos generales"
          icon="pi pi-save"
          className="p-button-primary px-6 py-2 font-bold"
          onClick={handleGuardar}
        />
      </div>
    </div>
  );
};

export default AgregarRegistro;
