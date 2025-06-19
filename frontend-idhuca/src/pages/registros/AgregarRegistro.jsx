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

  const [loadingCatalogos, setLoadingCatalogos] = useState(true);

  useEffect(() => {
    cargarCatalogos();
  }, []);

  const cargarCatalogos = async () => {
    try {
      const [d, f, e, l, g, sd, p, ss, tp] = await Promise.all([
        getCatalogo({ departamentos: true }),
        getCatalogo({ fuentes: true }),
        getCatalogo({ estadoRegistro: true }),
        getCatalogo({ lugarExacto: true }),
        getCatalogo({ genero: true }),
        getCatalogo({ subDerechos: true, parentId: "DER_1" }),
        getCatalogo({ paises: true }),
        getCatalogo({ estadoSalud: true }),
        getCatalogo({ tipoPersona: true }),
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

              {/* Tab: Violencia */}
              <TabPanel header="Violencia">
                {/* Campos como tipo de violencia, artefacto, actor responsable, etc */}
              </TabPanel>

              {/* Tab: Justicia */}
              <TabPanel header="Acceso a Justicia">
                {/* Campos relacionados al proceso judicial */}
              </TabPanel>

              {/* Tab: Detención */}
              <TabPanel header="Detención / Integridad">
                {/* Campos como tipoDetencion, orden judicial, tortura, etc */}
              </TabPanel>

              {/* Tab: Censura y Expresión */}
              <TabPanel header="Expresión / Censura">
                {/* Campos como tipoRepresion, medioExpresion, actorCensor, consecuencia */}
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
