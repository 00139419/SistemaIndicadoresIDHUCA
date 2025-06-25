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
  const [municipiosEnabled, setMunicipiosEnabled] = useState(false);
  const [departamentosDisabled, setDepartamentosDisabled] = useState(false);

  const [municipiosResidenciaEnabled, setMunicipiosResidenciaEnabled] =
    useState(false);
  const [departamentosResidenciaDisabled, setDepartamentosResidenciaDisabled] =
    useState(false);

  const [filtroActivo, setFiltroActivo] = useState(false);
  const [campoSeleccionado, setCampoSeleccionado] = useState(null);
  const [mostrar, setMostrar] = useState({});
  const navigate = useNavigate();
  const location = useLocation();
  let { derechoId, filtros, categoriaEjeX } = location.state || {};

  useEffect(() => {
    if (categoriaEjeX) {
      const campo = detectarCampoSeleccionado(categoriaEjeX);
      setCampoSeleccionado(campo);
    }
  }, [categoriaEjeX]);

  function detectarCampoSeleccionado(obj) {
    if (!obj || typeof obj !== "object") return null;

    for (const padre in obj) {
      if (typeof obj[padre] === "object" && obj[padre] !== null) {
        for (const hijo in obj[padre]) {
          const valor = obj[padre][hijo];
          const esValido =
            (Array.isArray(valor) && valor.length > 0) ||
            (typeof valor === "object" &&
              valor !== null &&
              Object.keys(valor).length > 0) ||
            typeof valor === "boolean" ||
            typeof valor === "number" ||
            (typeof valor === "string" && valor.trim() !== "");

          if (esValido) {
            return `${padre}.${hijo}`;
          }
        }
      }
    }

    return null;
  }

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

  const filtroBaseEvento = {
    fechaHechoRango: { fechaInicio: null, fechaFin: null },
    fuentes: [],
    estadosActuales: [],
    flagRegimenExcepcion: null,
    departamentos: [],
    municipios: [],
    lugaresExactos: [],
  };

  const filtroBaseAfectada = {
    nombres: [],
    generos: [],
    nacionalidades: [],
    departamentosResidencia: [],
    municipiosResidencia: [],
    tiposPersona: [],
    estadosSalud: [],
    rangoEdad: { edadInicio: null, edadFin: null },
  };

  const filtroBaseDerechos = {
    derechosVulnerados: [],
  };

  const filtroBaseViolencia = {
    esAsesinato: null,
    tiposViolencia: [],
    artefactosUtilizados: [],
    contextos: [],
    actoresResponsables: [],
    estadosSaludActorResponsable: [],
    huboProteccion: null,
    investigacionAbierta: null,
  };

  const filtroBaseDetencion = {
    tiposDetencion: [],
    ordenJudicial: null,
    autoridadesInvolucradas: [],
    huboTortura: null,
    motivosDetencion: [],
    duracionDiasExactos: [],
    accesoAbogado: null,
    resultados: [],
  };

  const filtroBaseCensura = {
    mediosExpresion: [],
    tiposRepresion: [],
    represaliasLegales: null,
    represaliasFisicas: null,
    actoresCensores: [],
    consecuencias: [],
  };

  const filtroBaseAccesoJusticia = {
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
  };

  useEffect(() => {
    if (categoriaEjeX) {
      if (categoriaEjeX.eventoFiltro)
        setEventoFiltro({ ...filtroBaseEvento, ...categoriaEjeX.eventoFiltro });

      if (categoriaEjeX.afectadaFiltro)
        setAfectadaFiltro({
          ...filtroBaseAfectada,
          ...categoriaEjeX.afectadaFiltro,
        });

      if (categoriaEjeX.derechosVulneradosFiltro)
        setDerechosVulneradosFiltro({
          ...filtroBaseDerechos,
          ...categoriaEjeX.derechosVulneradosFiltro,
        });

      if (categoriaEjeX.violenciaFiltro)
        setViolenciaFiltro({
          ...filtroBaseViolencia,
          ...categoriaEjeX.violenciaFiltro,
        });

      if (categoriaEjeX.detencionFiltro)
        setDetencionFiltro({
          ...filtroBaseDetencion,
          ...categoriaEjeX.detencionFiltro,
        });

      if (categoriaEjeX.censuraFiltro)
        setCensuraFiltro({
          ...filtroBaseCensura,
          ...categoriaEjeX.censuraFiltro,
        });

      if (categoriaEjeX.accesoJusticiaFiltro)
        setAccesoJusticiaFiltro({
          ...filtroBaseAccesoJusticia,
          ...categoriaEjeX.accesoJusticiaFiltro,
        });
    }
  }, [categoriaEjeX]);

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

    if(value === false) return true;

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

  useEffect(() => {
    if (eventoFiltro.municipios.length > 0) {
      setDepartamentosDisabled(true);
    } else {
      setDepartamentosDisabled(false);
    }

    if (eventoFiltro.departamentos.length === 1) {
      setMunicipiosEnabled(true);
    } else {
      setMunicipiosEnabled(false);
    }
  }, [eventoFiltro.departamentos, eventoFiltro.municipios]);

  useEffect(() => {
    if (afectadaFiltro.municipiosResidencia.length > 0) {
      setDepartamentosResidenciaDisabled(true);
    } else {
      setDepartamentosResidenciaDisabled(false);
    }

    if (afectadaFiltro.departamentosResidencia.length === 1) {
      setMunicipiosResidenciaEnabled(true);
    } else {
      setMunicipiosResidenciaEnabled(false);
    }
  }, [
    afectadaFiltro.departamentosResidencia,
    afectadaFiltro.municipiosResidencia,
  ]);

  const aplicarFiltros = () => {
    categoriaEjeX = {};

    const evento = cleanFiltro(eventoFiltro);
    if (evento) categoriaEjeX.eventoFiltro = evento;

    const afectada = cleanFiltro(afectadaFiltro);
    if (afectada) categoriaEjeX.afectadaFiltro = afectada;

    const derechos = cleanFiltro(derechosVulneradosFiltro);
    if (derechos) categoriaEjeX.derechosVulneradosFiltro = derechos;

    const violencia = cleanFiltro(violenciaFiltro);
    if (violencia) categoriaEjeX.violenciaFiltro = violencia;

    const detencion = cleanFiltro(detencionFiltro);
    if (detencion) categoriaEjeX.detencionFiltro = detencion;

    const censura = cleanFiltro(censuraFiltro);
    if (censura) categoriaEjeX.censuraFiltro = censura;

    const justicia = cleanFiltro(accesoJusticiaFiltro);
    if (justicia) categoriaEjeX.accesoJusticiaFiltro = justicia;

    console.log("categoriaEjeX " + JSON.stringify(categoriaEjeX)) 

    navigate("/graphs", {
      state: { derechoId, filtros, categoriaEjeX },
    });
  };

  const limpiarFiltros = () => {
    setCampoSeleccionado(null);
    setEventoFiltro({
      fechaHechoRango: { fechaInicio: null, fechaFin: null },
      fuentes: [],
      estadosActuales: [],
      flagRegimenExcepcion: null,
      departamentos: [],
      municipios: [],
      lugaresExactos: [],
    });

    setAfectadaFiltro({
      nombres: [],
      generos: [],
      nacionalidades: [],
      departamentosResidencia: [],
      municipiosResidencia: [],
      tiposPersona: [],
      estadosSalud: [],
      rangoEdad: { edadInicio: null, edadFin: null },
    });

    setDerechosVulneradosFiltro({ derechosVulnerados: [] });

    setViolenciaFiltro({
      esAsesinato: null,
      tiposViolencia: [],
      artefactosUtilizados: [],
      contextos: [],
      actoresResponsables: [],
      estadosSaludActorResponsable: [],
      huboProteccion: null,
      investigacionAbierta: null,
    });

    setDetencionFiltro({
      tiposDetencion: [],
      ordenJudicial: null,
      autoridadesInvolucradas: [],
      huboTortura: null,
      motivosDetencion: [],
      duracionDiasExactos: [],
      accesoAbogado: null,
      resultados: [],
    });

    setCensuraFiltro({
      mediosExpresion: [],
      tiposRepresion: [],
      represaliasLegales: null,
      represaliasFisicas: null,
      actoresCensores: [],
      consecuencias: [],
    });

    setAccesoJusticiaFiltro({
      tiposProceso: [],
      fechaDenunciaRango: { fechaInicio: null, fechaFin: null },
      tiposDenunciante: [],
      duracionesProceso: [],
      accesoAbogado: null,
      huboParcialidad: null,
      resultadosProceso: [],
      instancias: [],
    });
  };

  const handleFiltroChange = (grupo, clave, valor) => {
    // Actualiza el grupo correspondiente
    switch (grupo) {
      case "eventoFiltro":
        setEventoFiltro((prev) => ({ ...prev, [clave]: valor }));
        break;
      case "afectadaFiltro":
        setAfectadaFiltro((prev) => ({ ...prev, [clave]: valor }));
        break;
      case "derechosVulneradosFiltro":
        setDerechosVulneradosFiltro((prev) => ({ ...prev, [clave]: valor }));
        break;
      case "violenciaFiltro":
        setViolenciaFiltro((prev) => ({ ...prev, [clave]: valor }));
        break;
      case "detencionFiltro":
        setDetencionFiltro((prev) => ({ ...prev, [clave]: valor }));
        break;
      case "censuraFiltro":
        setCensuraFiltro((prev) => ({ ...prev, [clave]: valor }));
        break;
      case "accesoJusticiaFiltro":
        setAccesoJusticiaFiltro((prev) => ({ ...prev, [clave]: valor }));
        break;
      default:
        break;
    }

    // Activa el estado de filtro si aún no estaba activado
    if (!filtroActivo && !isEmptyValue(valor)) {
      setFiltroActivo(true);
    }

    // Si el valor se vacía, verificar si aún queda alguno activo
    if (filtroActivo && isEmptyValue(valor)) {
      const hayActivo =
        !isEmptyValue(eventoFiltro) ||
        !isEmptyValue(afectadaFiltro) ||
        !isEmptyValue(derechosVulneradosFiltro) ||
        !isEmptyValue(violenciaFiltro) ||
        !isEmptyValue(detencionFiltro) ||
        !isEmptyValue(censuraFiltro) ||
        !isEmptyValue(accesoJusticiaFiltro);
      setFiltroActivo(hayActivo);
    }
  };

  if (loadingCatalogos) {
    return (
      <div className="flex justify-content-center align-items-center min-h-screen">
        <i className="pi pi-spin pi-spinner" style={{ fontSize: "2rem" }} />
        <span className="ml-3 text-xl">Cargando catálogos...</span>
      </div>
    );
  }

  const esCampoActivo = (id) => {
    return !campoSeleccionado || campoSeleccionado === id;
  };

  const nombresCamposEjeX = {
    "eventoFiltro.fechaHechoRango": "Fecha del hecho",
    "eventoFiltro.fuentes": "Fuente",
    "eventoFiltro.estadosActuales": "Estado actual del caso",
    "eventoFiltro.flagRegimenExcepcion": "Regimen de excepción",
    "eventoFiltro.departamentos": "Departamento del hecho",
    "eventoFiltro.municipios": "Municipio del hecho",
    "eventoFiltro.lugaresExactos": "Lugar exacto del hecho",

    "afectadaFiltro.nombres": "Nombres afectados/as",
    "afectadaFiltro.generos": "Género",
    "afectadaFiltro.nacionalidades": "Nacionalidad",
    "afectadaFiltro.departamentosResidencia": "Departamento de residencia",
    "afectadaFiltro.municipiosResidencia": "Municipio de residencia",
    "afectadaFiltro.tiposPersona": "Tipo de persona",
    "afectadaFiltro.estadosSalud": "Estado de salud",
    "afectadaFiltro.rangoEdad": "Rango de edad",

    "derechosVulneradosFiltro.derechosVulnerados": "Derecho vulnerado",

    "violenciaFiltro.esAsesinato": "¿Hubo asesinato?",
    "violenciaFiltro.tiposViolencia": "Tipo de violencia",
    "violenciaFiltro.artefactosUtilizados": "Artefacto utilizado",
    "violenciaFiltro.contextos": "Contexto del hecho",
    "violenciaFiltro.actoresResponsables": "Actor responsable",
    "violenciaFiltro.estadosSaludActorResponsable": "Estado de salud del actor",
    "violenciaFiltro.huboProteccion": "¿Hubo protección?",
    "violenciaFiltro.investigacionAbierta": "¿Investigación abierta?",

    "detencionFiltro.tiposDetencion": "Tipo de detención",
    "detencionFiltro.ordenJudicial": "¿Orden judicial?",
    "detencionFiltro.autoridadesInvolucradas": "Autoridad involucrada",
    "detencionFiltro.huboTortura": "¿Hubo tortura?",
    "detencionFiltro.motivosDetencion": "Motivo de la detención",
    "detencionFiltro.duracionDiasExactos": "Días de detención",
    "detencionFiltro.accesoAbogado": "¿Acceso a abogado?",
    "detencionFiltro.resultados": "Resultado de la detención",

    "censuraFiltro.mediosExpresion": "Medio de expresión",
    "censuraFiltro.tiposRepresion": "Tipo de represión",
    "censuraFiltro.represaliasLegales": "¿Represalia legal?",
    "censuraFiltro.represaliasFisicas": "¿Represalia física?",
    "censuraFiltro.actoresCensores": "Actor censor",
    "censuraFiltro.consecuencias": "Consecuencia de la censura",

    "accesoJusticiaFiltro.tiposProceso": "Tipo de proceso judicial",
    "accesoJusticiaFiltro.fechaDenunciaRango": "Fecha de denuncia",
    "accesoJusticiaFiltro.tiposDenunciante": "Tipo de denunciante",
    "accesoJusticiaFiltro.duracionesProceso": "Duración del proceso",
    "accesoJusticiaFiltro.accesoAbogado": "¿Acceso a abogado?",
    "accesoJusticiaFiltro.huboParcialidad": "¿Hubo parcialidad?",
    "accesoJusticiaFiltro.resultadosProceso": "Resultado del proceso",
    "accesoJusticiaFiltro.instancias": "Instancia del proceso",
  };

  return (
    <div className="border rounded p-3 mb-4">
      <h3 className="mb-4">Filtros del Registro</h3>

      {/* Leyenda de campo seleeccionado */}
      {campoSeleccionado && (
        <div
          className="alert alert-danger text-center fw-semibold"
          role="alert"
          style={{ fontSize: "0.9rem" }}
        >
          El campo seleccionado para el eje X es:{" "}
          <span className="fw-bold">
            {nombresCamposEjeX[campoSeleccionado] || campoSeleccionado}
          </span>
        </div>
      )}

      {/* Botones */}
      <div className="border rounded p-3 mb-4">
        {/* Botones Borrar Filtros y Aplicar Filtros */}
        <div className="d-flex justify-content-end gap-2 mt-4">
          <Button
            label="Borrar todos los filtros"
            icon="pi pi-times"
            className="p-button-secondary"
            onClick={limpiarFiltros}
          />
          <Button
            label="Aplicar filtros"
            icon="pi pi-check"
            className="p-button-primary"
            onClick={aplicarFiltros}
          />
        </div>

        <div className="row">
          <div className="container py-4">
            <h5 className="mb-4">Filtros de Evento</h5>

            <div className="border rounded p-3 mb-4">
              <div className="row">
                {/* Fecha del hecho */}
                <div className="col-md-6 mb-3">
                  <label>Fecha de inicio</label>
                  <Calendar
                    value={eventoFiltro.fechaHechoRango.fechaInicio}
                    onChange={(e) => {
                      const nuevoValor = e.value;
                      setEventoFiltro((prev) => ({
                        ...prev,
                        fechaHechoRango: {
                          ...prev.fechaHechoRango,
                          fechaInicio: nuevoValor,
                        },
                      }));

                      const esVacio =
                        !nuevoValor && !eventoFiltro.fechaHechoRango.fechaFin;
                      setCampoSeleccionado(
                        esVacio ? null : "eventoFiltro.fechaHechoRango"
                      );
                    }}
                    showIcon
                    dateFormat="yy-mm-dd"
                    className="w-100"
                    disabled={!esCampoActivo("eventoFiltro.fechaHechoRango")}
                  />
                </div>
                <div className="col-md-6 mb-3">
                  <label>Fecha de fin</label>
                  <Calendar
                    value={eventoFiltro.fechaHechoRango.fechaFin}
                    onChange={(e) => {
                      const nuevoValor = e.value;
                      setEventoFiltro((prev) => ({
                        ...prev,
                        fechaHechoRango: {
                          ...prev.fechaHechoRango,
                          fechaFin: nuevoValor,
                        },
                      }));

                      const esVacio =
                        !eventoFiltro.fechaHechoRango.fechaInicio &&
                        !nuevoValor;
                      setCampoSeleccionado(
                        esVacio ? null : "eventoFiltro.fechaHechoRango"
                      );
                    }}
                    showIcon
                    dateFormat="yy-mm-dd"
                    className="w-100"
                    disabled={!esCampoActivo("eventoFiltro.fechaHechoRango")}
                  />
                </div>

                {/*Fuentes*/}
                <div className="col-md-4 mb-3">
                  <label>Fuentes</label>
                  <MultiSelect
                    value={eventoFiltro.fuentes.map((c) => c.codigo)}
                    options={fuentes}
                    optionLabel="descripcion"
                    optionValue="codigo"
                    onChange={(e) => {
                      const seleccion = e.value.map((codigo) => ({ codigo }));
                      setEventoFiltro((prev) => ({
                        ...prev,
                        fuentes: seleccion,
                      }));
                      setCampoSeleccionado(
                        seleccion.length === 0 ? null : "eventoFiltro.fuentes"
                      );
                    }}
                    placeholder="Seleccionar fuentes"
                    className="w-100"
                    disabled={!esCampoActivo("eventoFiltro.fuentes")}
                  />
                </div>

                {/*Estados actuales */}
                <div className="col-md-4 mb-3">
                  <label>Estados actuales</label>
                  <MultiSelect
                    value={eventoFiltro.estadosActuales.map((c) => c.codigo)}
                    options={estados}
                    optionLabel="descripcion"
                    optionValue="codigo"
                    onChange={(e) => {
                      const seleccion = e.value.map((codigo) => ({ codigo }));
                      setEventoFiltro((prev) => ({
                        ...prev,
                        estadosActuales: seleccion,
                      }));
                      setCampoSeleccionado(
                        seleccion.length === 0
                          ? null
                          : "eventoFiltro.estadosActuales"
                      );
                    }}
                    placeholder="Seleccionar estados"
                    className="w-100"
                    disabled={!esCampoActivo("eventoFiltro.estadosActuales")}
                  />
                </div>

                {/*Régimen de excepción */}
                <div className="col-md-4 mb-3 d-flex align-items-center">
                  <label className="me-3">Régimen de excepción</label>
                  <InputSwitch
                    checked={eventoFiltro.flagRegimenExcepcion || false}
                    onChange={(e) => {
                      const valor = e.value;
                      setEventoFiltro((prev) => ({
                        ...prev,
                        flagRegimenExcepcion: valor,
                      }));
                      setCampoSeleccionado(
                        valor === null || valor === false
                          ? null
                          : "eventoFiltro.flagRegimenExcepcion"
                      );
                    }}
                    disabled={
                      !esCampoActivo("eventoFiltro.flagRegimenExcepcion")
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
                    onChange={(e) => {
                      const seleccion = e.value.map((codigo) => ({ codigo }));
                      setEventoFiltro((prev) => ({
                        ...prev,
                        departamentos: seleccion,
                        municipios: [],
                      }));
                      setCampoSeleccionado(
                        seleccion.length === 0
                          ? null
                          : "eventoFiltro.departamentos"
                      );
                    }}
                    placeholder="Seleccionar departamentos"
                    className="w-100"
                    disabled={
                      !esCampoActivo("eventoFiltro.departamentos") ||
                      departamentosDisabled
                    }
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
                    onChange={(e) => {
                      const seleccion = e.value.map((codigo) => ({ codigo }));
                      setEventoFiltro((prev) => ({
                        ...prev,
                        municipios: seleccion,
                      }));
                      setCampoSeleccionado(
                        seleccion.length === 0
                          ? "eventoFiltro.departamentos"
                          : "eventoFiltro.municipios"
                      );
                    }}
                    placeholder="Seleccionar municipios"
                    className="w-100"
                    disabled={
                      !(
                        esCampoActivo("eventoFiltro.municipios") ||
                        campoSeleccionado === "eventoFiltro.departamentos"
                      ) || !municipiosEnabled
                    }
                  />
                </div>

                {/*Lugares exactos */}
                <div className="col-md-4 mb-3">
                  <label>Lugares exactos</label>
                  <MultiSelect
                    value={eventoFiltro.lugaresExactos.map((c) => c.codigo)}
                    options={lugaresExactos}
                    optionLabel="descripcion"
                    optionValue="codigo"
                    onChange={(e) => {
                      const seleccion = e.value.map((codigo) => ({ codigo }));
                      setEventoFiltro((prev) => ({
                        ...prev,
                        lugaresExactos: seleccion,
                      }));
                      setCampoSeleccionado(
                        seleccion.length === 0
                          ? null
                          : "eventoFiltro.lugaresExactos"
                      );
                    }}
                    placeholder="Seleccionar lugares"
                    className="w-100"
                    disabled={!esCampoActivo("eventoFiltro.lugaresExactos")}
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
              onChange={(e) => {
                const nombres = e.target.value
                  .split(",")
                  .map((n) => n.trim())
                  .filter((n) => n);
                setAfectadaFiltro((prev) => ({ ...prev, nombres }));
                setCampoSeleccionado(
                  nombres.length === 0 ? null : "afectadaFiltro.nombres"
                );
              }}
              disabled={!esCampoActivo("afectadaFiltro.nombres")}
            />
          </div>

          {/*Generos*/}
          <div className="col-md-6 mb-3">
            <label>Géneros</label>
            <MultiSelect
              value={afectadaFiltro.generos.map((c) => c.codigo)}
              options={generos}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) => {
                const seleccion = e.value.map((codigo) => ({ codigo }));
                setAfectadaFiltro((prev) => ({ ...prev, generos: seleccion }));
                setCampoSeleccionado(
                  seleccion.length === 0 ? null : "afectadaFiltro.generos"
                );
              }}
              placeholder="Seleccionar géneros"
              className="w-100"
              disabled={!esCampoActivo("afectadaFiltro.generos")}
            />
          </div>

          {/*Nacionalidades*/}
          <div className="col-md-6 mb-3">
            <label>Nacionalidades</label>
            <MultiSelect
              value={afectadaFiltro.nacionalidades.map((c) => c.codigo)}
              options={paises}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) => {
                const seleccion = e.value.map((codigo) => ({ codigo }));
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  nacionalidades: seleccion,
                }));
                setCampoSeleccionado(
                  seleccion.length === 0
                    ? null
                    : "afectadaFiltro.nacionalidades"
                );
              }}
              placeholder="Seleccionar nacionalidades"
              className="w-100"
              filter
              filterPlaceholder="Buscar nacionalidad"
              filterBy="descripcion"
              disabled={!esCampoActivo("afectadaFiltro.nacionalidades")}
            />
          </div>

          {/*Departamentos de residencia*/}
          <div className="col-md-6 mb-3">
            <label>Departamentos de residencia</label>
            <MultiSelect
              value={afectadaFiltro.departamentosResidencia.map(
                (c) => c.codigo
              )}
              options={departamentosResidencia}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) => {
                const seleccion = e.value.map((codigo) => ({ codigo }));
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  departamentosResidencia: seleccion,
                  municipiosResidencia: [],
                }));
                setCampoSeleccionado(
                  seleccion.length === 0
                    ? null
                    : "afectadaFiltro.departamentosResidencia"
                );
              }}
              placeholder="Seleccionar departamentos"
              className="w-100"
              disabled={
                !esCampoActivo("afectadaFiltro.departamentosResidencia") ||
                departamentosResidenciaDisabled
              }
            />
          </div>

          {/*Municipios de residencia*/}
          <div className="col-md-6 mb-3">
            <label>Municipios de residencia</label>
            <MultiSelect
              value={afectadaFiltro.municipiosResidencia.map((c) => c.codigo)}
              options={municipiosResidencia}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) => {
                const seleccion = e.value.map((codigo) => ({ codigo }));
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  municipiosResidencia: seleccion,
                }));
                setCampoSeleccionado(
                  seleccion.length === 0
                    ? null
                    : "afectadaFiltro.municipiosResidencia"
                );
              }}
              placeholder="Seleccionar municipios"
              className="w-100"
              disabled={
                !(
                  esCampoActivo("afectadaFiltro.municipiosResidencia") ||
                  campoSeleccionado === "afectadaFiltro.departamentosResidencia"
                ) || !municipiosResidenciaEnabled
              }
            />
          </div>

          {/*Tipos de persona*/}
          <div className="col-md-6 mb-3">
            <label>Tipos de persona</label>
            <MultiSelect
              value={afectadaFiltro.tiposPersona.map((c) => c.codigo)}
              options={tiposPersona}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) => {
                const seleccion = e.value.map((codigo) => ({ codigo }));
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  tiposPersona: seleccion,
                }));
                setCampoSeleccionado(
                  seleccion.length === 0 ? null : "afectadaFiltro.tiposPersona"
                );
              }}
              placeholder="Seleccionar tipos"
              className="w-100"
              disabled={!esCampoActivo("afectadaFiltro.tiposPersona")}
            />
          </div>

          {/*Estados de salud*/}
          <div className="col-md-6 mb-3">
            <label>Estados de salud</label>
            <MultiSelect
              value={afectadaFiltro.estadosSalud.map((c) => c.codigo)}
              options={estadosSaludPersonaAfectada}
              optionLabel="descripcion"
              optionValue="codigo"
              onChange={(e) => {
                const seleccion = e.value.map((codigo) => ({ codigo }));
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  estadosSalud: seleccion,
                }));
                setCampoSeleccionado(
                  seleccion.length === 0 ? null : "afectadaFiltro.estadosSalud"
                );
              }}
              placeholder="Seleccionar estados de salud"
              className="w-100"
              disabled={!esCampoActivo("afectadaFiltro.estadosSalud")}
            />
          </div>

          {/*Edad mínima y máxima*/}
          <div className="col-md-3 mb-3">
            <label>Edad mínima</label>
            <input
              type="number"
              className="form-control"
              value={afectadaFiltro.rangoEdad.edadInicio ?? ""}
              onChange={(e) => {
                const valor = e.target.value ? parseInt(e.target.value) : null;
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  rangoEdad: { ...prev.rangoEdad, edadInicio: valor },
                }));
                setCampoSeleccionado(
                  valor === null && !afectadaFiltro.rangoEdad.edadFin
                    ? null
                    : "afectadaFiltro.rangoEdad"
                );
              }}
              disabled={!esCampoActivo("afectadaFiltro.rangoEdad")}
            />
          </div>
          <div className="col-md-3 mb-3">
            <label>Edad máxima</label>
            <input
              type="number"
              className="form-control"
              value={afectadaFiltro.rangoEdad.edadFin ?? ""}
              onChange={(e) => {
                const valor = e.target.value ? parseInt(e.target.value) : null;
                setAfectadaFiltro((prev) => ({
                  ...prev,
                  rangoEdad: { ...prev.rangoEdad, edadFin: valor },
                }));
                setCampoSeleccionado(
                  !afectadaFiltro.rangoEdad.edadInicio && valor === null
                    ? null
                    : "afectadaFiltro.rangoEdad"
                );
              }}
              disabled={!esCampoActivo("afectadaFiltro.rangoEdad")}
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
              onChange={(e) => {
                const seleccion = e.value.map((codigo) => ({ codigo }));
                setDerechosVulneradosFiltro((prev) => ({
                  ...prev,
                  derechosVulnerados: seleccion,
                }));
                setCampoSeleccionado(
                  seleccion.length === 0
                    ? null
                    : "derechosVulneradosFiltro.derechosVulnerados"
                );
              }}
              placeholder="Seleccionar derechos"
              className="w-100"
              disabled={
                !esCampoActivo("derechosVulneradosFiltro.derechosVulnerados")
              }
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
              onChange={(e) => {
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  esAsesinato: e.value,
                }));
                setCampoSeleccionado(
                  e.value ? "violenciaFiltro.esAsesinato" : null
                );
              }}
              disabled={!esCampoActivo("violenciaFiltro.esAsesinato")}
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
              onChange={(e) => {
                const seleccion = e.value.map((codigo) => ({ codigo }));
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  tiposViolencia: seleccion,
                }));
                setCampoSeleccionado(
                  seleccion.length === 0
                    ? null
                    : "violenciaFiltro.tiposViolencia"
                );
              }}
              placeholder="Seleccionar tipos"
              className="w-100"
              disabled={!esCampoActivo("violenciaFiltro.tiposViolencia")}
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
              onChange={(e) => {
                const seleccion = e.value.map((codigo) => ({ codigo }));
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  artefactosUtilizados: seleccion,
                }));
                setCampoSeleccionado(
                  seleccion.length === 0
                    ? null
                    : "violenciaFiltro.artefactosUtilizados"
                );
              }}
              placeholder="Seleccionar artefactos"
              className="w-100"
              disabled={!esCampoActivo("violenciaFiltro.artefactosUtilizados")}
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
              onChange={(e) => {
                const seleccion = e.value.map((codigo) => ({ codigo }));
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  contextos: seleccion,
                }));
                setCampoSeleccionado(
                  seleccion.length === 0 ? null : "violenciaFiltro.contextos"
                );
              }}
              placeholder="Seleccionar contextos"
              className="w-100"
              disabled={!esCampoActivo("violenciaFiltro.contextos")}
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
              onChange={(e) => {
                const seleccion = e.value.map((codigo) => ({ codigo }));
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  actoresResponsables: seleccion,
                }));
                setCampoSeleccionado(
                  seleccion.length === 0
                    ? null
                    : "violenciaFiltro.actoresResponsables"
                );
              }}
              placeholder="Seleccionar actores"
              className="w-100"
              disabled={!esCampoActivo("violenciaFiltro.actoresResponsables")}
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
              onChange={(e) => {
                const seleccion = e.value.map((codigo) => ({ codigo }));
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  estadosSaludActorResponsable: seleccion,
                }));
                setCampoSeleccionado(
                  seleccion.length === 0
                    ? null
                    : "violenciaFiltro.estadosSaludActorResponsable"
                );
              }}
              placeholder="Seleccionar estados"
              className="w-100"
              disabled={
                !esCampoActivo("violenciaFiltro.estadosSaludActorResponsable")
              }
            />
          </div>

          {/* Hubo protección */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Hubo protección?</label>
            <InputSwitch
              checked={violenciaFiltro.huboProteccion || false}
              onChange={(e) => {
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  huboProteccion: e.value,
                }));
                setCampoSeleccionado(
                  e.value ? "violenciaFiltro.huboProteccion" : null
                );
              }}
              disabled={!esCampoActivo("violenciaFiltro.huboProteccion")}
            />
          </div>

          {/* Investigación abierta */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Investigación abierta?</label>
            <InputSwitch
              checked={violenciaFiltro.investigacionAbierta || false}
              onChange={(e) => {
                setViolenciaFiltro((prev) => ({
                  ...prev,
                  investigacionAbierta: e.value,
                }));
                setCampoSeleccionado(
                  e.value ? "violenciaFiltro.investigacionAbierta" : null
                );
              }}
              disabled={!esCampoActivo("violenciaFiltro.investigacionAbierta")}
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
              onChange={(e) => {
                setDetencionFiltro((prev) => ({
                  ...prev,
                  tiposDetencion: e.value.map((codigo) => ({ codigo })),
                }));
                setCampoSeleccionado(
                  e.value.length > 0 ? "detencionFiltro.tiposDetencion" : null
                );
              }}
              placeholder="Seleccionar tipos"
              className="w-100"
              disabled={!esCampoActivo("detencionFiltro.tiposDetencion")}
            />
          </div>

          {/* Orden judicial */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Orden judicial?</label>
            <InputSwitch
              checked={detencionFiltro.ordenJudicial || false}
              onChange={(e) => {
                setDetencionFiltro((prev) => ({
                  ...prev,
                  ordenJudicial: e.value,
                }));
                setCampoSeleccionado(
                  e.value ? "detencionFiltro.ordenJudicial" : null
                );
              }}
              disabled={!esCampoActivo("detencionFiltro.ordenJudicial")}
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
              onChange={(e) => {
                setDetencionFiltro((prev) => ({
                  ...prev,
                  autoridadesInvolucradas: e.value.map((codigo) => ({
                    codigo,
                  })),
                }));
                setCampoSeleccionado(
                  e.value.length > 0
                    ? "detencionFiltro.autoridadesInvolucradas"
                    : null
                );
              }}
              placeholder="Seleccionar autoridades"
              className="w-100"
              disabled={
                !esCampoActivo("detencionFiltro.autoridadesInvolucradas")
              }
            />
          </div>

          {/* Hubo tortura */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Hubo tortura?</label>
            <InputSwitch
              checked={detencionFiltro.huboTortura || false}
              onChange={(e) => {
                setDetencionFiltro((prev) => ({
                  ...prev,
                  huboTortura: e.value,
                }));
                setCampoSeleccionado(
                  e.value ? "detencionFiltro.huboTortura" : null
                );
              }}
              disabled={!esCampoActivo("detencionFiltro.huboTortura")}
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
              onChange={(e) => {
                setDetencionFiltro((prev) => ({
                  ...prev,
                  motivosDetencion: e.value.map((codigo) => ({ codigo })),
                }));
                setCampoSeleccionado(
                  e.value.length > 0 ? "detencionFiltro.motivosDetencion" : null
                );
              }}
              placeholder="Seleccionar motivos"
              className="w-100"
              disabled={!esCampoActivo("detencionFiltro.motivosDetencion")}
            />
          </div>

          {/* Duración exacta (días) */}
          <div className="col-md-4 mb-3">
            <label>Duración exacta (días)</label>
            <input
              type="text"
              className="form-control"
              placeholder="Ej: 1, 2, 5, 10"
              onChange={(e) => {
                const valores = e.target.value
                  .split(",")
                  .map((v) => parseInt(v.trim()))
                  .filter((v) => !isNaN(v));
                setDetencionFiltro((prev) => ({
                  ...prev,
                  duracionDiasExactos: valores,
                }));
                setCampoSeleccionado(
                  valores.length > 0
                    ? "detencionFiltro.duracionDiasExactos"
                    : null
                );
              }}
              disabled={!esCampoActivo("detencionFiltro.duracionDiasExactos")}
            />
          </div>

          {/* Acceso a abogado */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Acceso a abogado?</label>
            <InputSwitch
              checked={detencionFiltro.accesoAbogado || false}
              onChange={(e) => {
                setDetencionFiltro((prev) => ({
                  ...prev,
                  accesoAbogado: e.value,
                }));
                setCampoSeleccionado(
                  e.value ? "detencionFiltro.accesoAbogado" : null
                );
              }}
              disabled={!esCampoActivo("detencionFiltro.accesoAbogado")}
            />
          </div>

          {/* Resultados */}
          <div className="col-md-4 mb-3">
            <label>Resultados</label>
            <input
              type="text"
              className="form-control"
              placeholder="Ej: liberado, procesado..."
              onChange={(e) => {
                const valores = e.target.value
                  .split(",")
                  .map((v) => v.trim())
                  .filter((v) => v);
                setDetencionFiltro((prev) => ({
                  ...prev,
                  resultados: valores,
                }));
                setCampoSeleccionado(
                  valores.length > 0 ? "detencionFiltro.resultados" : null
                );
              }}
              disabled={!esCampoActivo("detencionFiltro.resultados")}
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
              onChange={(e) => {
                setCensuraFiltro((prev) => ({
                  ...prev,
                  mediosExpresion: e.value.map((codigo) => ({ codigo })),
                }));
                setCampoSeleccionado(
                  e.value.length > 0 ? "censuraFiltro.mediosExpresion" : null
                );
              }}
              placeholder="Seleccionar medios"
              className="w-100"
              disabled={!esCampoActivo("censuraFiltro.mediosExpresion")}
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
              onChange={(e) => {
                setCensuraFiltro((prev) => ({
                  ...prev,
                  tiposRepresion: e.value.map((codigo) => ({ codigo })),
                }));
                setCampoSeleccionado(
                  e.value.length > 0 ? "censuraFiltro.tiposRepresion" : null
                );
              }}
              placeholder="Seleccionar tipos"
              className="w-100"
              disabled={!esCampoActivo("censuraFiltro.tiposRepresion")}
            />
          </div>

          {/* ¿Represalias legales? */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Represalias legales?</label>
            <InputSwitch
              checked={censuraFiltro.represaliasLegales || false}
              onChange={(e) => {
                setCensuraFiltro((prev) => ({
                  ...prev,
                  represaliasLegales: e.value,
                }));
                setCampoSeleccionado(
                  e.value ? "censuraFiltro.represaliasLegales" : null
                );
              }}
              disabled={!esCampoActivo("censuraFiltro.represaliasLegales")}
            />
          </div>

          {/* ¿Represalias físicas? */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Represalias físicas?</label>
            <InputSwitch
              checked={censuraFiltro.represaliasFisicas || false}
              onChange={(e) => {
                setCensuraFiltro((prev) => ({
                  ...prev,
                  represaliasFisicas: e.value,
                }));
                setCampoSeleccionado(
                  e.value ? "censuraFiltro.represaliasFisicas" : null
                );
              }}
              disabled={!esCampoActivo("censuraFiltro.represaliasFisicas")}
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
              onChange={(e) => {
                setCensuraFiltro((prev) => ({
                  ...prev,
                  actoresCensores: e.value.map((codigo) => ({ codigo })),
                }));
                setCampoSeleccionado(
                  e.value.length > 0 ? "censuraFiltro.actoresCensores" : null
                );
              }}
              placeholder="Seleccionar actores"
              className="w-100"
              disabled={!esCampoActivo("censuraFiltro.actoresCensores")}
            />
          </div>

          {/* Consecuencias */}
          <div className="col-md-4 mb-3">
            <label>Consecuencias</label>
            <input
              type="text"
              className="form-control"
              placeholder="Ej: despido, amenazas, etc."
              onChange={(e) => {
                const consecuencias = e.target.value
                  .split(",")
                  .map((v) => v.trim())
                  .filter((v) => v);
                setCensuraFiltro((prev) => ({
                  ...prev,
                  consecuencias,
                }));
                setCampoSeleccionado(
                  consecuencias.length > 0
                    ? "censuraFiltro.consecuencias"
                    : null
                );
              }}
              disabled={!esCampoActivo("censuraFiltro.consecuencias")}
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
              onChange={(e) => {
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  tiposProceso: e.value.map((codigo) => ({ codigo })),
                }));
                setCampoSeleccionado(
                  e.value.length > 0
                    ? "accesoJusticiaFiltro.tiposProceso"
                    : null
                );
              }}
              placeholder="Seleccionar tipos"
              className="w-100"
              disabled={!esCampoActivo("accesoJusticiaFiltro.tiposProceso")}
            />
          </div>

          {/* Fecha de inicio de denuncia */}
          <div className="col-md-4 mb-3">
            <label>Fecha de inicio de denuncia</label>
            <Calendar
              value={accesoJusticiaFiltro.fechaDenunciaRango.fechaInicio}
              onChange={(e) => {
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  fechaDenunciaRango: {
                    ...prev.fechaDenunciaRango,
                    fechaInicio: e.value,
                  },
                }));
                setCampoSeleccionado(
                  e.value ? "accesoJusticiaFiltro.fechaDenunciaRango" : null
                );
              }}
              showIcon
              dateFormat="yy-mm-dd"
              className="w-100"
              disabled={
                !esCampoActivo("accesoJusticiaFiltro.fechaDenunciaRango")
              }
            />
          </div>

          {/* Fecha de fin de denuncia */}
          <div className="col-md-4 mb-3">
            <label>Fecha de fin de denuncia</label>
            <Calendar
              value={accesoJusticiaFiltro.fechaDenunciaRango.fechaFin}
              onChange={(e) => {
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  fechaDenunciaRango: {
                    ...prev.fechaDenunciaRango,
                    fechaFin: e.value,
                  },
                }));
                setCampoSeleccionado(
                  e.value ? "accesoJusticiaFiltro.fechaDenunciaRango" : null
                );
              }}
              showIcon
              dateFormat="yy-mm-dd"
              className="w-100"
              disabled={
                !esCampoActivo("accesoJusticiaFiltro.fechaDenunciaRango")
              }
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
              onChange={(e) => {
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  tiposDenunciante: e.value.map((codigo) => ({ codigo })),
                }));
                setCampoSeleccionado(
                  e.value.length > 0
                    ? "accesoJusticiaFiltro.tiposDenunciante"
                    : null
                );
              }}
              placeholder="Seleccionar tipos"
              className="w-100"
              disabled={!esCampoActivo("accesoJusticiaFiltro.tiposDenunciante")}
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
              onChange={(e) => {
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  duracionesProceso: e.value.map((codigo) => ({ codigo })),
                }));
                setCampoSeleccionado(
                  e.value.length > 0
                    ? "accesoJusticiaFiltro.duracionesProceso"
                    : null
                );
              }}
              placeholder="Seleccionar duraciones"
              className="w-100"
              disabled={
                !esCampoActivo("accesoJusticiaFiltro.duracionesProceso")
              }
            />
          </div>

          {/* ¿Acceso a abogado? */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Acceso a abogado?</label>
            <InputSwitch
              checked={accesoJusticiaFiltro.accesoAbogado || false}
              onChange={(e) => {
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  accesoAbogado: e.value,
                }));
                setCampoSeleccionado(
                  e.value ? "accesoJusticiaFiltro.accesoAbogado" : null
                );
              }}
              disabled={!esCampoActivo("accesoJusticiaFiltro.accesoAbogado")}
            />
          </div>

          {/* ¿Hubo parcialidad? */}
          <div className="col-md-4 mb-3 d-flex align-items-center">
            <label className="me-3">¿Hubo parcialidad?</label>
            <InputSwitch
              checked={accesoJusticiaFiltro.huboParcialidad || false}
              onChange={(e) => {
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  huboParcialidad: e.value,
                }));
                setCampoSeleccionado(
                  e.value ? "accesoJusticiaFiltro.huboParcialidad" : null
                );
              }}
              disabled={!esCampoActivo("accesoJusticiaFiltro.huboParcialidad")}
            />
          </div>

          {/* Resultados del proceso */}
          <div className="col-md-4 mb-3">
            <label>Resultados del proceso</label>
            <input
              type="text"
              className="form-control"
              placeholder="Ej: sobreseído, absuelto..."
              onChange={(e) => {
                const resultados = e.target.value
                  .split(",")
                  .map((v) => v.trim())
                  .filter((v) => v);
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  resultadosProceso: resultados,
                }));
                setCampoSeleccionado(
                  resultados.length > 0
                    ? "accesoJusticiaFiltro.resultadosProceso"
                    : null
                );
              }}
              disabled={
                !esCampoActivo("accesoJusticiaFiltro.resultadosProceso")
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
              onChange={(e) => {
                const instancias = e.target.value
                  .split(",")
                  .map((v) => v.trim())
                  .filter((v) => v);
                setAccesoJusticiaFiltro((prev) => ({
                  ...prev,
                  instancias,
                }));
                setCampoSeleccionado(
                  instancias.length > 0
                    ? "accesoJusticiaFiltro.instancias"
                    : null
                );
              }}
              disabled={!esCampoActivo("accesoJusticiaFiltro.instancias")}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
