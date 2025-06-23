import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { MultiSelect } from "primereact/multiselect";
import { Button } from "primereact/button";
import "bootstrap/dist/css/bootstrap.min.css";
import "primereact/resources/themes/lara-light-blue/theme.css";
import "primereact/resources/primereact.min.css";
import { useLocation } from "react-router-dom";
import { Calendar } from "primereact/calendar";
import { InputSwitch } from "primereact/inputswitch";
import { getCatalogo } from "./../../services/RegstrosService";
import { useEffect } from "react";

export default function FiltradoRegistros() {
  const [mostrar, setMostrar] = useState({});
  const navigate = useNavigate();
  const location = useLocation();
  let { derechoId, derechoTitle } = location.state || {};

  const toggleMostrar = (clave) => {
    setMostrar((prev) => ({ ...prev, [clave]: !prev[clave] }));
  };

  // Catalogos
  const [departamentos, setDepartamentos] = useState([]);
  const [municipios, setMunicipios] = useState([]);
  const [departamentosResidencia, setDepartamentosResidencia] = useState([]);
  const [municipiosResidencia, setMunicipiosResidencia] = useState([]);
  const [fuentes, setFuentes] = useState([]);
  const [estados, setEstados] = useState([]);
  const [estadosSaludPersonaAfectada, setEstadosSaludPersonaAfectada] =
    useState([]);
  const [lugaresExactos, setLugaresExactos] = useState([]);
  const [generos, setGeneros] = useState([]);
  const [derechos, setDerechos] = useState([]);
  const [paises, setPaises] = useState([]);
  const [tiposPersona, setTiposPersona] = useState([]);
  const [tiposViolencia, setTiposViolencia] = useState([]);
  const [artefactos, setArtefactos] = useState([]);
  const [contextosViolencia, setContextosViolencia] = useState([]);
  const [actorResponsable, setActorResponsable] = useState([]);
  const [tiposDetencion, setTiposDetencion] = useState([]);
  const [motivosDetencion, setMotivosDetencion] = useState([]);
  const [mediosExpresion, setMediosExpresion] = useState([]);
  const [tiposRepresion, setTiposRepresion] = useState([]);
  const [tiposProcesoJudicial, setTiposProcesoJudicial] = useState([]);
  const [duracionesProceso, setDuracionesProceso] = useState([]);

  const [loadingCatalogos, setLoadingCatalogos] = useState(true);

  const [eventoFiltro, setEventoFiltro] = useState({
    fechaHechoRango: { fechaInicio: null, fechaFin: null },
    fuentes: [],
    estadosActuales: [],
    flagRegimenExcepcion: null,
    departamentos: [],
    municipios: [],
    lugaresExactos: [],
  });

  const [afectadaFiltro, setAfectadaFiltro] = useState({
    nombres: [],
    generos: [],
    nacionalidades: [],
    departamentosResidencia: [],
    municipiosResidencia: [],
    tiposPersona: [],
    estadosSalud: [],
    rangoEdad: { edadInicio: null, edadFin: null },
  });

  const [derechosVulneradosFiltro, setDerechosVulneradosFiltro] = useState({
    derechosVulnerados: [],
  });

  const [violenciaFiltro, setViolenciaFiltro] = useState({
    esAsesinato: null,
    tiposViolencia: [],
    artefactosUtilizados: [],
    contextos: [],
    actoresResponsables: [],
    estadosSaludActorResponsable: [],
    huboProteccion: null,
    investigacionAbierta: null,
  });

  const [detencionFiltro, setDetencionFiltro] = useState({
    tiposDetencion: [],
    ordenJudicial: null,
    autoridadesInvolucradas: [],
    huboTortura: null,
    motivosDetencion: [],
    duracionDiasExactos: [],
    accesoAbogado: null,
    resultados: [],
  });

  const [censuraFiltro, setCensuraFiltro] = useState({
    mediosExpresion: [],
    tiposRepresion: [],
    represaliasLegales: null,
    represaliasFisicas: null,
    actoresCensores: [],
    consecuencias: [],
  });

  const [accesoJusticiaFiltro, setAccesoJusticiaFiltro] = useState({
    tiposProceso: [],
    fechaDenunciaRango: {
      fechaInicio: null,
      fechaFin: null,
    },
    tiposDenunciante: [],
    duracionesProceso: [],
    accesoAbogado: null,
    huboParcialidad: null,
    resultadosProceso: [],
    instancias: [],
  });

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
        dr,
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
        getCatalogo({ departamentos: true }),
      ]);

      setDepartamentosResidencia(dr);
      setTiposPersona(tp);
      setEstadosSaludPersonaAfectada(ss);
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
      setActorResponsable(tp);
    } catch (error) {
      console.error("Error al cargar catálogos", error);
    } finally {
      setLoadingCatalogos(false);
    }
  };

  useEffect(() => {
    const cargarMunicipiosEvento = async () => {
      if (eventoFiltro.departamentos.length === 1) {
        const [depto] = eventoFiltro.departamentos;
        const municipios = await obtenerMunicipiosPorDepartamento(depto.codigo);
        setMunicipios(municipios);
      } else {
        setMunicipios([]);
      }
    };

    cargarMunicipiosEvento();
  }, [eventoFiltro.departamentos]);

  useEffect(() => {
    const cargarMunicipiosResidencia = async () => {
      if (afectadaFiltro.departamentosResidencia.length === 1) {
        const [depto] = afectadaFiltro.departamentosResidencia;
        const municipios = await obtenerMunicipiosPorDepartamento(depto.codigo);
        setMunicipiosResidencia(municipios);
      } else {
        setMunicipiosResidencia([]);
      }
    };

    cargarMunicipiosResidencia();
  }, [afectadaFiltro.departamentosResidencia]);

  const obtenerMunicipiosPorDepartamento = async (codigoDepto) => {
    if (!codigoDepto) return [];

    try {
      const res = await getCatalogo({
        municipios: true,
        parentId: codigoDepto,
      });
      return res;
    } catch (err) {
      console.error("Error obteniendo municipios", err);
      return [];
    }
  };

  const catalogoMock = [
    { codigo: "COD1", descripcion: "Opción 1" },
    { codigo: "COD2", descripcion: "Opción 2" },
    { codigo: "COD3", descripcion: "Opción 3" },
  ];

  const renderBotonFiltro = (clave, label) => (
    <div className="col-md-4 mb-3">
      <Button
        label={label}
        className="p-button-outlined w-100 text-start"
        onClick={() => toggleMostrar(clave)}
      />
      {mostrar[clave] && (
        <div className="mt-2">
          <MultiSelect
            value={[]} // valor temporal
            options={[]} // aquí irán los catálogos reales
            optionLabel="descripcion"
            optionValue="codigo"
            placeholder="Seleccionar..."
            className="w-100"
            disabled
          />
        </div>
      )}
    </div>
  );

  const isEmptyValue = (value) => {
    if (value === null || value === undefined) return true;

    if (Array.isArray(value)) return value.length === 0;

    if (typeof value === "object") {
      // Manejo especial para rangos de fecha o edad
      const allNull = Object.values(value).every(
        (v) => v === null || v === undefined || v === ""
      );
      return allNull;
    }

    return false;
  };

  const cleanFiltro = (filtro) => {
    const limpio = {};
    for (const [key, val] of Object.entries(filtro)) {
      if (!isEmptyValue(val)) {
        limpio[key] = val;
      }
    }
    return Object.keys(limpio).length > 0 ? limpio : null;
  };

  const shouldInclude = (obj) => {
    return Object.values(obj).some((value) => !isEmptyValue(value));
  };

  const aplicarFiltros = () => {
    const filtrosFinales = {};

    const evento = cleanFiltro(eventoFiltro);
    if (evento) filtrosFinales.eventoFiltro = evento;

    const afectada = cleanFiltro(afectadaFiltro);
    if (afectada) filtrosFinales.afectadaFiltro = afectada;

    const derechos = cleanFiltro(derechosVulneradosFiltro);
    if (derechos) filtrosFinales.derechosVulneradosFiltro = derechos;

    const violencia = cleanFiltro(violenciaFiltro);
    if (violencia) filtrosFinales.violenciaFiltro = violencia;

    const detencion = cleanFiltro(detencionFiltro);
    if (detencion) filtrosFinales.detencionFiltro = detencion;

    const censura = cleanFiltro(censuraFiltro);
    if (censura) filtrosFinales.censuraFiltro = censura;

    const justicia = cleanFiltro(accesoJusticiaFiltro);
    if (justicia) filtrosFinales.accesoJusticiaFiltro = justicia;

    console.log(filtrosFinales);

    navigate("/select-register", {
      state: { filtros: filtrosFinales, derechoId },
    });
  };

  if (loadingCatalogos) {
    return (
      <div className="flex justify-content-center align-items-center min-h-screen">
        <i className="pi pi-spin pi-spinner" style={{ fontSize: "2rem" }} />
        <span className="ml-3 text-xl">Cargando catálogos...</span>
      </div>
    );
  }

  return (
    <div className="container py-4">
      <h3 className="mb-4">Filtros del Registro</h3>

      {/* Evento */}
      <div className="border rounded p-3 mb-4">
        <h5>Filtros de Evento</h5>
        <div className="row">
          <div className="container py-4">
            <h3 className="mb-4">Filtros de Evento</h3>

            <div className="border rounded p-3 mb-4">
              <div className="row">
                {/* Fecha del hecho */}
                <div className="col-md-6 mb-3">
                  <label>Fecha de inicio</label>
                  <Calendar
                    value={eventoFiltro.fechaHechoRango.fechaInicio}
                    onChange={(e) =>
                      setEventoFiltro((prev) => ({
                        ...prev,
                        fechaHechoRango: {
                          ...prev.fechaHechoRango,
                          fechaInicio: e.value,
                        },
                      }))
                    }
                    showIcon
                    dateFormat="yy-mm-dd"
                    className="w-100"
                  />
                </div>
                <div className="col-md-6 mb-3">
                  <label>Fecha de fin</label>
                  <Calendar
                    value={eventoFiltro.fechaHechoRango.fechaFin}
                    onChange={(e) =>
                      setEventoFiltro((prev) => ({
                        ...prev,
                        fechaHechoRango: {
                          ...prev.fechaHechoRango,
                          fechaFin: e.value,
                        },
                      }))
                    }
                    showIcon
                    dateFormat="yy-mm-dd"
                    className="w-100"
                  />
                </div>

                {/* Fuentes */}
                <div className="col-md-4 mb-3">
                  <label>Fuentes</label>
                  <MultiSelect
                    value={eventoFiltro.fuentes.map((c) => c.codigo)}
                    options={fuentes}
                    optionLabel="descripcion"
                    optionValue="codigo"
                    onChange={(e) =>
                      setEventoFiltro((prev) => ({
                        ...prev,
                        fuentes: e.value.map((codigo) => ({ codigo })),
                      }))
                    }
                    placeholder="Seleccionar fuentes"
                    className="w-100"
                  />
                </div>

                {/* Estados actuales */}
                <div className="col-md-4 mb-3">
                  <label>Estados actuales</label>
                  <MultiSelect
                    value={eventoFiltro.estadosActuales.map((c) => c.codigo)}
                    options={estados}
                    optionLabel="descripcion"
                    optionValue="codigo"
                    onChange={(e) =>
                      setEventoFiltro((prev) => ({
                        ...prev,
                        estadosActuales: e.value.map((codigo) => ({ codigo })),
                      }))
                    }
                    placeholder="Seleccionar estados"
                    className="w-100"
                  />
                </div>

                {/* Régimen de excepción */}
                <div className="col-md-4 mb-3 d-flex align-items-center">
                  <label className="me-3">Régimen de excepción</label>
                  <InputSwitch
                    checked={eventoFiltro.flagRegimenExcepcion || false}
                    onChange={(e) =>
                      setEventoFiltro((prev) => ({
                        ...prev,
                        flagRegimenExcepcion: e.value,
                      }))
                    }
                  />
                </div>

                {/* Departamentos */}
                <div className="col-md-4 mb-3">
                  <label>Departamentos</label>
                  <MultiSelect
                    value={eventoFiltro.departamentos.map((c) => c.codigo)}
                    options={departamentos}
                    optionLabel="descripcion"
                    optionValue="codigo"
                    onChange={(e) =>
                      setEventoFiltro((prev) => ({
                        ...prev,
                        departamentos: e.value.map((codigo) => ({ codigo })),
                        municipios: [],
                      }))
                    }
                    placeholder="Seleccionar departamentos"
                    className="w-100"
                  />
                </div>

                {/* Municipios */}
                <div className="col-md-4 mb-3">
                  <label>Municipios</label>
                  <MultiSelect
                    value={eventoFiltro.municipios.map((c) => c.codigo)}
                    options={municipios}
                    optionLabel="descripcion"
                    optionValue="codigo"
                    onChange={(e) =>
                      setEventoFiltro((prev) => ({
                        ...prev,
                        municipios: e.value.map((codigo) => ({ codigo })),
                      }))
                    }
                    placeholder="Seleccionar municipios"
                    className="w-100"
                  />
                </div>

                {/* Lugares exactos */}
                <div className="col-md-4 mb-3">
                  <label>Lugares exactos</label>
                  <MultiSelect
                    value={eventoFiltro.lugaresExactos.map((c) => c.codigo)}
                    options={lugaresExactos}
                    optionLabel="descripcion"
                    optionValue="codigo"
                    onChange={(e) =>
                      setEventoFiltro((prev) => ({
                        ...prev,
                        lugaresExactos: e.value.map((codigo) => ({ codigo })),
                      }))
                    }
                    placeholder="Seleccionar lugares"
                    className="w-100"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Persona Afectada */}
      <div className="border rounded p-3 mb-4">
        <h5>Filtros de Persona Afectada</h5>
        <div className="row">
          {/* Nombres */}
          <div className="col-md-6 mb-3">
            <label>Nombres</label>
            <input
              type="text"
              className="form-control"
              placeholder="Escriba nombres separados por coma"
              onChange={(e) =>
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  nombres: e.target.value
                    .split(",")
                    .map((n) => n.trim())
                    .filter((n) => n),
                }))
              }
            />
          </div>

          {/* Géneros */}
          <div className="col-md-6 mb-3">
            <label>Géneros</label>
            <MultiSelect
              value={afectadaFiltro.generos.map((c) => c.codigo)}
              options={generos}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  generos: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar géneros"
              className="w-100"
            />
          </div>

          {/* Nacionalidades */}
          <div className="col-md-6 mb-3">
            <label>Nacionalidades</label>
            <MultiSelect
              value={afectadaFiltro.nacionalidades.map((c) => c.codigo)}
              options={paises}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  nacionalidades: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar nacionalidades"
              className="w-100"
              filter
              filterPlaceholder="Buscar nacionalidad"
              filterBy="descripcion"
            />
          </div>

          {/* Departamentos de residencia */}
          <div className="col-md-6 mb-3">
            <label>Departamentos de residencia</label>
            <MultiSelect
              value={afectadaFiltro.departamentosResidencia.map(
                (c) => c.codigo
              )}
              options={departamentosResidencia}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  departamentosResidencia: e.value.map((codigo) => ({
                    codigo,
                    municipiosResidencia: [],
                  })),
                }))
              }
              placeholder="Seleccionar departamentos"
              className="w-100"
            />
          </div>

          {/* Municipios de residencia */}
          <div className="col-md-6 mb-3">
            <label>Municipios de residencia</label>
            <MultiSelect
              value={afectadaFiltro.municipiosResidencia.map((c) => c.codigo)}
              options={municipiosResidencia}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  municipiosResidencia: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar municipios"
              className="w-100"
            />
          </div>

          {/* Tipos de persona */}
          <div className="col-md-6 mb-3">
            <label>Tipos de persona</label>
            <MultiSelect
              value={afectadaFiltro.tiposPersona.map((c) => c.codigo)}
              options={tiposPersona}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  tiposPersona: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar tipos"
              className="w-100"
            />
          </div>

          {/* Estados de salud */}
          <div className="col-md-6 mb-3">
            <label>Estados de salud</label>
            <MultiSelect
              value={afectadaFiltro.estadosSalud.map((c) => c.codigo)}
              options={estadosSaludPersonaAfectada}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  estadosSalud: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar estados de salud"
              className="w-100"
            />
          </div>

          {/* Rango de edad */}
          <div className="col-md-3 mb-3">
            <label>Edad mínima</label>
            <input
              type="number"
              className="form-control"
              onChange={(e) =>
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  rangoEdad: {
                    ...prev.rangoEdad,
                    edadInicio: e.target.value
                      ? parseInt(e.target.value)
                      : null,
                  },
                }))
              }
            />
          </div>
          <div className="col-md-3 mb-3">
            <label>Edad máxima</label>
            <input
              type="number"
              className="form-control"
              onChange={(e) =>
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  rangoEdad: {
                    ...prev.rangoEdad,
                    edadFin: e.target.value ? parseInt(e.target.value) : null,
                  },
                }))
              }
            />
          </div>
        </div>
      </div>

      {/* Derechos Vulnerados */}
      <div className="border rounded p-3 mb-4">
        <h5>Filtros de Derechos Vulnerados</h5>
        <div className="row">
          <div className="col-md-6 mb-3">
            <label>Derechos vulnerados</label>
            <MultiSelect
              value={derechosVulneradosFiltro.derechosVulnerados.map(
                (c) => c.codigo
              )}
              options={derechos}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setDerechosVulneradosFiltro((prev) => ({
                  ...prev,
                  derechosVulnerados: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar derechos"
              className="w-100"
            />
          </div>
        </div>
      </div>

      {/* Violencia */}
      <div className="border rounded p-3 mb-4">
        <h5>Filtros de Violencia</h5>
        <div className="row">
          {/* Es asesinato */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Es asesinato?</label>
            <InputSwitch
              checked={violenciaFiltro.esAsesinato || false}
              onChange={(e) =>
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  esAsesinato: e.value,
                }))
              }
            />
          </div>

          {/* Tipos de violencia */}
          <div className="col-md-4 mb-3">
            <label>Tipos de violencia</label>
            <MultiSelect
              value={violenciaFiltro.tiposViolencia.map((c) => c.codigo)}
              options={tiposViolencia}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  tiposViolencia: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar tipos"
              className="w-100"
            />
          </div>

          {/* Artefactos utilizados */}
          <div className="col-md-4 mb-3">
            <label>Artefactos utilizados</label>
            <MultiSelect
              value={violenciaFiltro.artefactosUtilizados.map((c) => c.codigo)}
              options={artefactos}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  artefactosUtilizados: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar artefactos"
              className="w-100"
            />
          </div>

          {/* Contextos */}
          <div className="col-md-4 mb-3">
            <label>Contextos</label>
            <MultiSelect
              value={violenciaFiltro.contextos.map((c) => c.codigo)}
              options={contextosViolencia}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  contextos: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar contextos"
              className="w-100"
            />
          </div>

          {/* Actores responsables */}
          <div className="col-md-4 mb-3">
            <label>Actores responsables</label>
            <MultiSelect
              value={violenciaFiltro.actoresResponsables.map((c) => c.codigo)}
              options={actorResponsable}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  actoresResponsables: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar actores"
              className="w-100"
            />
          </div>

          {/* Estados de salud del actor */}
          <div className="col-md-4 mb-3">
            <label>Estado de salud del actor</label>
            <MultiSelect
              value={violenciaFiltro.estadosSaludActorResponsable.map(
                (c) => c.codigo
              )}
              options={estadosSaludPersonaAfectada}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  estadosSaludActorResponsable: e.value.map((codigo) => ({
                    codigo,
                  })),
                }))
              }
              placeholder="Seleccionar estados"
              className="w-100"
            />
          </div>

          {/* Hubo protección */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Hubo protección?</label>
            <InputSwitch
              checked={violenciaFiltro.huboProteccion || false}
              onChange={(e) =>
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  huboProteccion: e.value,
                }))
              }
            />
          </div>

          {/* Investigación abierta */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Investigación abierta?</label>
            <InputSwitch
              checked={violenciaFiltro.investigacionAbierta || false}
              onChange={(e) =>
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  investigacionAbierta: e.value,
                }))
              }
            />
          </div>
        </div>
      </div>

      {/* Detención */}
      <div className="border rounded p-3 mb-4">
        <h5>Filtros de Detención</h5>
        <div className="row">
          {/* Tipos de detención */}
          <div className="col-md-4 mb-3">
            <label>Tipos de detención</label>
            <MultiSelect
              value={detencionFiltro.tiposDetencion.map((c) => c.codigo)}
              options={tiposDetencion}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setDetencionFiltro((prev) => ({
                  ...prev,
                  tiposDetencion: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar tipos"
              className="w-100"
            />
          </div>

          {/* Orden judicial */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Orden judicial?</label>
            <InputSwitch
              checked={detencionFiltro.ordenJudicial || false}
              onChange={(e) =>
                setDetencionFiltro((prev) => ({
                  ...prev,
                  ordenJudicial: e.value,
                }))
              }
            />
          </div>

          {/* Autoridades involucradas */}
          <div className="col-md-4 mb-3">
            <label>Autoridades involucradas</label>
            <MultiSelect
              value={detencionFiltro.autoridadesInvolucradas.map(
                (c) => c.codigo
              )}
              options={tiposPersona}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setDetencionFiltro((prev) => ({
                  ...prev,
                  autoridadesInvolucradas: e.value.map((codigo) => ({
                    codigo,
                  })),
                }))
              }
              placeholder="Seleccionar autoridades"
              className="w-100"
            />
          </div>

          {/* Hubo tortura */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Hubo tortura?</label>
            <InputSwitch
              checked={detencionFiltro.huboTortura || false}
              onChange={(e) =>
                setDetencionFiltro((prev) => ({
                  ...prev,
                  huboTortura: e.value,
                }))
              }
            />
          </div>

          {/* Motivos de detención */}
          <div className="col-md-4 mb-3">
            <label>Motivos de detención</label>
            <MultiSelect
              value={detencionFiltro.motivosDetencion.map((c) => c.codigo)}
              options={motivosDetencion}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setDetencionFiltro((prev) => ({
                  ...prev,
                  motivosDetencion: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar motivos"
              className="w-100"
            />
          </div>

          {/* Duración exacta (días) */}
          <div className="col-md-4 mb-3">
            <label>Duración exacta (días)</label>
            <input
              type="text"
              className="form-control"
              placeholder="Ej: 1, 2, 5, 10"
              onChange={(e) =>
                setDetencionFiltro((prev) => ({
                  ...prev,
                  duracionDiasExactos: e.target.value
                    .split(",")
                    .map((v) => parseInt(v.trim()))
                    .filter((v) => !isNaN(v)),
                }))
              }
            />
          </div>

          {/* Acceso a abogado */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Acceso a abogado?</label>
            <InputSwitch
              checked={detencionFiltro.accesoAbogado || false}
              onChange={(e) =>
                setDetencionFiltro((prev) => ({
                  ...prev,
                  accesoAbogado: e.value,
                }))
              }
            />
          </div>

          {/* Resultados */}
          <div className="col-md-4 mb-3">
            <label>Resultados</label>
            <input
              type="text"
              className="form-control"
              placeholder="Ej: liberado, procesado..."
              onChange={(e) =>
                setDetencionFiltro((prev) => ({
                  ...prev,
                  resultados: e.target.value
                    .split(",")
                    .map((v) => v.trim())
                    .filter((v) => v),
                }))
              }
            />
          </div>
        </div>
      </div>

      {/* Censura */}
      <div className="border rounded p-3 mb-4">
        <h5>Filtros de Censura</h5>
        <div className="row">
          {/* Medios de expresión */}
          <div className="col-md-4 mb-3">
            <label>Medios de expresión</label>
            <MultiSelect
              value={censuraFiltro.mediosExpresion.map((c) => c.codigo)}
              options={mediosExpresion}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setCensuraFiltro((prev) => ({
                  ...prev,
                  mediosExpresion: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar medios"
              className="w-100"
            />
          </div>

          {/* Tipos de represión */}
          <div className="col-md-4 mb-3">
            <label>Tipos de represión</label>
            <MultiSelect
              value={censuraFiltro.tiposRepresion.map((c) => c.codigo)}
              options={tiposRepresion}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setCensuraFiltro((prev) => ({
                  ...prev,
                  tiposRepresion: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar tipos"
              className="w-100"
            />
          </div>

          {/* ¿Represalias legales? */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Represalias legales?</label>
            <InputSwitch
              checked={censuraFiltro.represaliasLegales || false}
              onChange={(e) =>
                setCensuraFiltro((prev) => ({
                  ...prev,
                  represaliasLegales: e.value,
                }))
              }
            />
          </div>

          {/* ¿Represalias físicas? */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Represalias físicas?</label>
            <InputSwitch
              checked={censuraFiltro.represaliasFisicas || false}
              onChange={(e) =>
                setCensuraFiltro((prev) => ({
                  ...prev,
                  represaliasFisicas: e.value,
                }))
              }
            />
          </div>

          {/* Actores censores */}
          <div className="col-md-4 mb-3">
            <label>Actores censores</label>
            <MultiSelect
              value={censuraFiltro.actoresCensores.map((c) => c.codigo)}
              options={tiposPersona}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setCensuraFiltro((prev) => ({
                  ...prev,
                  actoresCensores: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar actores"
              className="w-100"
            />
          </div>

          {/* Consecuencias */}
          <div className="col-md-4 mb-3">
            <label>Consecuencias</label>
            <input
              type="text"
              className="form-control"
              placeholder="Ej: despido, amenazas, etc."
              onChange={(e) =>
                setCensuraFiltro((prev) => ({
                  ...prev,
                  consecuencias: e.target.value
                    .split(",")
                    .map((v) => v.trim())
                    .filter((v) => v),
                }))
              }
            />
          </div>
        </div>
      </div>

      {/* Acceso a la Justicia */}
      <div className="border rounded p-3 mb-4">
        <h5>Filtros de Acceso a la Justicia</h5>
        <div className="row">
          {/* Tipos de proceso */}
          <div className="col-md-4 mb-3">
            <label>Tipos de proceso</label>
            <MultiSelect
              value={accesoJusticiaFiltro.tiposProceso.map((c) => c.codigo)}
              options={tiposProcesoJudicial}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  tiposProceso: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar tipos"
              className="w-100"
            />
          </div>

          {/* Fecha de denuncia - inicio */}
          <div className="col-md-4 mb-3">
            <label>Fecha de inicio de denuncia</label>
            <Calendar
              value={accesoJusticiaFiltro.fechaDenunciaRango.fechaInicio}
              onChange={(e) =>
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  fechaDenunciaRango: {
                    ...prev.fechaDenunciaRango,
                    fechaInicio: e.value,
                  },
                }))
              }
              showIcon
              dateFormat="yy-mm-dd"
              className="w-100"
            />
          </div>

          {/* Fecha de denuncia - fin */}
          <div className="col-md-4 mb-3">
            <label>Fecha de fin de denuncia</label>
            <Calendar
              value={accesoJusticiaFiltro.fechaDenunciaRango.fechaFin}
              onChange={(e) =>
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  fechaDenunciaRango: {
                    ...prev.fechaDenunciaRango,
                    fechaFin: e.value,
                  },
                }))
              }
              showIcon
              dateFormat="yy-mm-dd"
              className="w-100"
            />
          </div>

          {/* Tipos de denunciante */}
          <div className="col-md-4 mb-3">
            <label>Tipos de denunciante</label>
            <MultiSelect
              value={accesoJusticiaFiltro.tiposDenunciante.map((c) => c.codigo)}
              options={tiposPersona}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  tiposDenunciante: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar tipos"
              className="w-100"
            />
          </div>

          {/* Duraciones del proceso */}
          <div className="col-md-4 mb-3">
            <label>Duraciones del proceso</label>
            <MultiSelect
              value={accesoJusticiaFiltro.duracionesProceso.map(
                (c) => c.codigo
              )}
              options={duracionesProceso}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) =>
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  duracionesProceso: e.value.map((codigo) => ({ codigo })),
                }))
              }
              placeholder="Seleccionar duraciones"
              className="w-100"
            />
          </div>

          {/* ¿Acceso a abogado? */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Acceso a abogado?</label>
            <InputSwitch
              checked={accesoJusticiaFiltro.accesoAbogado || false}
              onChange={(e) =>
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  accesoAbogado: e.value,
                }))
              }
            />
          </div>

          {/* ¿Hubo parcialidad? */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Hubo parcialidad?</label>
            <InputSwitch
              checked={accesoJusticiaFiltro.huboParcialidad || false}
              onChange={(e) =>
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  huboParcialidad: e.value,
                }))
              }
            />
          </div>

          {/* Resultados del proceso */}
          <div className="col-md-4 mb-3">
            <label>Resultados del proceso</label>
            <input
              type="text"
              className="form-control"
              placeholder="Ej: sobreseído, absuelto..."
              onChange={(e) =>
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  resultadosProceso: e.target.value
                    .split(",")
                    .map((v) => v.trim())
                    .filter((v) => v),
                }))
              }
            />
          </div>

          {/* Instancias */}
          <div className="col-md-4 mb-3">
            <label>Instancias</label>
            <input
              type="text"
              className="form-control"
              placeholder="Ej: primera instancia, apelación..."
              onChange={(e) =>
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  instancias: e.target.value
                    .split(",")
                    .map((v) => v.trim())
                    .filter((v) => v),
                }))
              }
            />
          </div>
        </div>
      </div>

      {/* Botón aplicar */}
      <div className="text-end">
        <Button
          label="Aplicar filtros"
          icon="pi pi-check"
          className="p-button-primary"
          onClick={aplicarFiltros}
        />
      </div>
    </div>
  );
}
