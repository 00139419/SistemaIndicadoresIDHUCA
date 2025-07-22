import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
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
import { useNavigate } from "react-router-dom";
import { Dialog } from "primereact/dialog";

import "primereact/resources/themes/lara-light-indigo/theme.css";
import "primereact/resources/primereact.min.css";
import "primeicons/primeicons.css";
import "primeflex/primeflex.css";

const AgregarRegistro = () => {
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const navigate = useNavigate();
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
  const [derechosPrincipales, setDerechosPrincipales] = useState([]);
  const [subDerechos, setSubDerechos] = useState([]);
  const location = useLocation();
  let derechoIdFromState = location.state?.derechoId;

  if (!derechoIdFromState) {
    derechoIdFromState = localStorage.getItem("selectedDerechoId");
  }

  const DERECHO_ID_TO_CODIGO = {
    1: "DER_1", // Derecho a la Libertad Personal e Integridad personal
    2: "DER_2", // Derecho a la Libertad de Expresión
    3: "DER_3", // Derecho de Acceso a la Justicia
    4: "DER_4", // Derecho a la Vida
  };

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
        dr, // derechos principales
        sd, // subderechos
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
        ctx,
      ] = await Promise.all([
        getCatalogo({ departamentos: true, cargarDeafult: true }),
        getCatalogo({ fuentes: true, cargarDeafult: true }),
        getCatalogo({ estadoRegistro: true, cargarDeafult: true }),
        getCatalogo({ lugarExacto: true, cargarDeafult: true }),
        getCatalogo({ genero: true, cargarDeafult: true }),
        getCatalogo({ derechos: true, cargarDeafult: true }), // <-- derechos principales
        getCatalogo({
          subDerechos: true,
          cargarDeafult: true,
          parentId: "DER_1",
        }), // <-- subderechos
        getCatalogo({ paises: true, cargarDeafult: true }),
        getCatalogo({ estadoSalud: true, cargarDeafult: true }),
        getCatalogo({ tipoPersona: true, cargarDeafult: true }),
        getCatalogo({ tipoViolencia: true, cargarDeafult: true }),
        getCatalogo({ tipoArma: true, cargarDeafult: true }),
        getCatalogo({ tipoPersona: true, cargarDeafult: true }),
        getCatalogo({ tipoDetencion: true, cargarDeafult: true }),
        getCatalogo({ motivoDetencion: true, cargarDeafult: true }),
        getCatalogo({ medioExpresion: true, cargarDeafult: true }),
        getCatalogo({ tipoRepresion: true, cargarDeafult: true }),
        getCatalogo({ tipoProcesoJudicial: true, cargarDeafult: true }),
        getCatalogo({ duracionProceso: true, cargarDeafult: true }),
        getCatalogo({ contexto: true, cargarDeafult: true }),
      ]);

      setTiposPersona(tp);
      setEstadosSalud(ss);
      setDepartamentos(d);
      setFuentes(f);
      setEstados(e);
      setLugaresExactos(l);
      setGeneros(g);
      setDerechosPrincipales(dr); // <-- derechos principales
      console.log("derechosPrincipales:", dr);
      setSubDerechos(sd); // <-- subderechos
      setPaises(p);
      setTiposViolencia(tv);
      setArtefactos(ar);
      setContextosViolencia(ctx);
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

  useEffect(() => {
    if (derechoIdFromState && derechosPrincipales.length > 0) {
      const derechoCodigo = DERECHO_ID_TO_CODIGO[Number(derechoIdFromState)];
      const derecho = derechosPrincipales.find(
        (d) => d.codigo === derechoCodigo
      );
      if (derecho) {
        setDerechoAsociado(derecho);
      }
    }
  }, [derechoIdFromState, derechosPrincipales]);

  const handleGuardar = async () => {
    // Primero validar campos obligatorios
    if (!fechaHecho) {
      alert("La fecha del hecho es obligatoria");
      return;
    }

    if (!fuente) {
      alert("La fuente es obligatoria");
      return;
    }

    if (!estadoActual) {
      alert("El estado actual es obligatorio");
      return;
    }

    if (!derechoAsociado) {
      console.error("derechoAsociado es null");
      console.log("derechoIdFromState:", derechoIdFromState);
      console.log("derechos disponibles:", derechos);
      alert("El derecho asociado es obligatorio");
      return;
    }

    console.log("derechoAsociado a enviar:", derechoAsociado);

    // Calcular flags dinámicamente basado en los datos de las personas
    const hayViolencia = personas.some(
      (p) => p.violencia && Object.keys(p.violencia).length > 0
    );
    const hayDetencion = personas.some(
      (p) =>
        p.detencionIntegridad && Object.keys(p.detencionIntegridad).length > 0
    );
    const hayExpresion = personas.some(
      (p) => p.expresionCensura && Object.keys(p.expresionCensura).length > 0
    );
    const hayJusticia = personas.some(
      (p) => p.accesoJusticia && Object.keys(p.accesoJusticia).length > 0
    );
    const hayCensura = personas.some(
      (p) => p.expresionCensura && p.expresionCensura.tipoRepresion
    );

    // Construir el objeto principal
    const registro = {
      fechaHecho: fechaHecho.toISOString().split("T")[0],
      fuente: {
        codigo: fuente.codigo,
        descripcion: fuente.descripcion,
      },
      estadoActual: {
        codigo: estadoActual.codigo,
        descripcion: estadoActual.descripcion,
      },
      derechoAsociado: {
        codigo: derechoAsociado.codigo,
        descripcion: derechoAsociado.descripcion,
      },
      flagViolencia: hayViolencia,
      flagDetencion: hayDetencion,
      flagExpresion: hayExpresion,
      flagJusticia: hayJusticia,
      flagCensura: hayCensura,
      flagRegimenExcepcion: false,
      observaciones: observaciones?.trim() || "", // Si está vacío o solo espacios, envía string vacío
      ubicacion: {
        departamento: departamento
          ? {
              codigo: departamento.codigo,
              descripcion: departamento.descripcion,
            }
          : null,
        municipio: municipio
          ? {
              codigo: municipio.codigo,
              descripcion: municipio.descripcion,
            }
          : null,
        lugarExacto: lugarExacto
          ? {
              codigo: lugarExacto.codigo,
              descripcion: lugarExacto.descripcion,
            }
          : null,
      },
      personasAfectadas: personas.map((p) => {
        const persona = {
          nombre: p.nombre || "",
          ...(p.edad !== null && p.edad !== undefined ? { edad: p.edad } : {}),
          genero: p.genero
            ? {
                codigo: p.genero.codigo,
                descripcion: p.genero.descripcion,
              }
            : null,
          nacionalidad: p.nacionalidad
            ? {
                codigo: p.nacionalidad.codigo,
                descripcion: p.nacionalidad.descripcion,
              }
            : null,
          departamentoResidencia: p.departamentoResidencia
            ? {
                codigo: p.departamentoResidencia.codigo,
                descripcion: p.departamentoResidencia.descripcion,
              }
            : null,
          municipioResidencia: p.municipioResidencia
            ? {
                codigo: p.municipioResidencia.codigo,
                descripcion: p.municipioResidencia.descripcion,
              }
            : null,
          tipoPersona: p.tipoPersona
            ? {
                codigo: p.tipoPersona.codigo,
                descripcion: p.tipoPersona.descripcion,
              }
            : null,
          estadoSalud: p.estadoSalud
            ? {
                codigo: p.estadoSalud.codigo,
                descripcion: p.estadoSalud.descripcion,
              }
            : null,
          derechosVulnerados:
            Array.isArray(p.derechosVulnerados) &&
            p.derechosVulnerados.length > 0
              ? p.derechosVulnerados.map((d) => ({
                  derecho: {
                    codigo: d.codigo,
                    descripcion: d.descripcion,
                  },
                }))
              : [],
        };

        // Solo agregar secciones si tienen datos válidos
        if (p.violencia && Object.keys(p.violencia).length > 0) {
          persona.violencia = {
            esAsesinato: p.violencia.esAsesinato || false,
            tipoViolencia: p.violencia.tipoViolencia
              ? {
                  codigo: p.violencia.tipoViolencia.codigo,
                  descripcion: p.violencia.tipoViolencia.descripcion,
                }
              : null,
            artefactoUtilizado: p.violencia.artefactoUtilizado
              ? {
                  codigo: p.violencia.artefactoUtilizado.codigo,
                  descripcion: p.violencia.artefactoUtilizado.descripcion,
                }
              : null,
            contexto: p.violencia.contexto
              ? {
                  codigo: p.violencia.contexto.codigo,
                  descripcion: p.violencia.contexto.descripcion,
                }
              : null,
            actorResponsable: p.violencia.actorResponsable
              ? {
                  codigo: p.violencia.actorResponsable.codigo,
                  descripcion: p.violencia.actorResponsable.descripcion,
                }
              : null,
            estadoSaludActorResponsable: p.violencia.estadoSaludActorResponsable
              ? {
                  codigo: p.violencia.estadoSaludActorResponsable.codigo,
                  descripcion:
                    p.violencia.estadoSaludActorResponsable.descripcion,
                }
              : null,
            huboProteccion: p.violencia.huboProteccion || false,
            investigacionAbierta: p.violencia.investigacionAbierta || false,
            respuestaEstado: p.violencia.respuestaEstado || "",
          };
        }

        if (
          p.detencionIntegridad &&
          Object.keys(p.detencionIntegridad).length > 0
        ) {
          persona.detencion = {
            tipoDetencion: p.detencionIntegridad.tipoDetencion
              ? {
                  codigo: p.detencionIntegridad.tipoDetencion.codigo,
                  descripcion: p.detencionIntegridad.tipoDetencion.descripcion,
                }
              : null,
            ordenJudicial: p.detencionIntegridad.ordenJudicial || false,
            autoridadInvolucrada: p.detencionIntegridad.autoridadInvolucrada
              ? {
                  codigo: p.detencionIntegridad.autoridadInvolucrada.codigo,
                  descripcion:
                    p.detencionIntegridad.autoridadInvolucrada.descripcion,
                }
              : null,
            huboTortura: p.detencionIntegridad.huboTortura || false,
            duracionDias: p.detencionIntegridad.duracionDias || 0,
            accesoAbogado: p.detencionIntegridad.accesoAbogado || false,
            resultado: p.detencionIntegridad.resultado || "",
            motivoDetencion: p.detencionIntegridad.motivoDetencion
              ? {
                  codigo: p.detencionIntegridad.motivoDetencion.codigo,
                  descripcion:
                    p.detencionIntegridad.motivoDetencion.descripcion,
                }
              : null,
          };
        }

        if (p.expresionCensura && Object.keys(p.expresionCensura).length > 0) {
          persona.expresion = {
            medioExpresion: p.expresionCensura.medioExpresion
              ? {
                  codigo: p.expresionCensura.medioExpresion.codigo,
                  descripcion: p.expresionCensura.medioExpresion.descripcion,
                }
              : null,
            tipoRepresion: p.expresionCensura.tipoRepresion
              ? {
                  codigo: p.expresionCensura.tipoRepresion.codigo,
                  descripcion: p.expresionCensura.tipoRepresion.descripcion,
                }
              : null,
            represaliasLegales: p.expresionCensura.represaliasLegales || false,
            represaliasFisicas: p.expresionCensura.represaliasFisicas || false,
            actorCensor: p.expresionCensura.actorCensor
              ? {
                  codigo: p.expresionCensura.actorCensor.codigo,
                  descripcion: p.expresionCensura.actorCensor.descripcion,
                }
              : null,
            consecuencia: p.expresionCensura.consecuencia || "",
          };
        }

        if (p.accesoJusticia && Object.keys(p.accesoJusticia).length > 0) {
          persona.justicia = {
            tipoProceso: p.accesoJusticia.tipoProceso
              ? {
                  codigo: p.accesoJusticia.tipoProceso.codigo,
                  descripcion: p.accesoJusticia.tipoProceso.descripcion,
                }
              : null,
            fechaDenuncia: p.accesoJusticia.fechaDenuncia
              ? new Date(p.accesoJusticia.fechaDenuncia).toISOString()
              : null,
            tipoDenunciante: p.accesoJusticia.tipoDenunciante
              ? {
                  codigo: p.accesoJusticia.tipoDenunciante.codigo,
                  descripcion: p.accesoJusticia.tipoDenunciante.descripcion,
                }
              : null,
            duracionProceso: p.accesoJusticia.duracionProceso
              ? {
                  codigo: p.accesoJusticia.duracionProceso.codigo,
                  descripcion: p.accesoJusticia.duracionProceso.descripcion,
                }
              : null,
            accesoAbogado: p.accesoJusticia.accesoAbogado || false,
            huboParcialidad: p.accesoJusticia.huboParcialidad || false,
            resultadoProceso: p.accesoJusticia.resultadoProceso || "",
            instancia: p.accesoJusticia.instancia || "",
          };
        }

        return persona;
      }),
    };

    // Validar que hay al menos una persona
    if (registro.personasAfectadas.length === 0) {
      alert("Debe agregar al menos una persona afectada");
      return;
    }

    // Enviar el registro al backend
    try {
      const token = localStorage.getItem("authToken");
      if (!token) {
        alert("No hay token de autenticación");
        return;
      }
      const response = await fetch(
        "http://localhost:8080/idhuca-indicadores/api/srv/registros/evento/add",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(registro),
        }
      );
      if (!response.ok) {
        alert("Error al guardar el registro");
        return;
      }
      const data = await response.json();
      if (data.codigo === 0) {
        setShowSuccessModal(true);
        setTimeout(() => {
          setShowSuccessModal(false);
          navigate("/select-register", {
            state: {
              filtros: {},
              derechoId: derechoIdFromState,
              categoriaEjeX: {},
            },
          });
        }, 2000); // 2 segundos
      } else {
        alert("Error: " + (data.mensaje || "No se pudo guardar"));
      }
    } catch (error) {
      alert("Error de red o del servidor");
      console.error("Error completo:", error);
    }
    console.log("Registro enviado:", registro);
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
    // Cambiar de {} a null para que aparezcan los botones de "¿Desea registrar?"
    violencia: null,
    detencionIntegridad: null,
    expresionCensura: null,
    accesoJusticia: null,
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
      <Dialog
        header="Registro guardado"
        visible={showSuccessModal}
        onHide={() => {
          setShowSuccessModal(false);
          navigate("/select-register", {
            state: {
              filtros: {},
              derechoId: derechoIdFromState,
              categoriaEjeX: "",
            },
          });
        }}
        closable={false}
        style={{ width: "350px" }}
      >
        <div
          className="flex flex-column align-items-center justify-content-center"
          style={{ minHeight: "100px" }}
        >
          <i
            className="pi pi-check-circle"
            style={{ fontSize: "2rem", color: "green" }}
          ></i>
          <p className="mt-3 text-center">
            ¡El evento se guardó correctamente!
          </p>
        </div>
      </Dialog>
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
            <label className="font-semibold mb-2 block">
              Observaciones{" "}
              <span className="text-muted font-normal">(Opcional)</span>
            </label>
            <InputTextarea
              value={observaciones}
              onChange={(e) => setObservaciones(e.target.value)}
              rows={4}
              className="w-full"
              autoResize
              placeholder="Ingrese observaciones adicionales (opcional)"
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
                    <label className="mb-2 d-block">Nombre</label>
                    <InputText
                      value={persona.nombre}
                      onChange={(e) =>
                        actualizarPersona(index, "nombre", e.target.value)
                      }
                      className="w-full"
                    />
                  </div>

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

                  <div className="field col-12 md:col-4">
                    <label className="mb-2 d-block">Tipo de persona</label>
                    <Dropdown
                      value={persona.tipoPersona}
                      onChange={(e) =>
                        actualizarPersona(index, "tipoPersona", e.value)
                      }
                      options={tiposPersona}
                      optionLabel="descripcion"
                      placeholder="Seleccione género"
                      className="w-full"
                    />
                  </div>

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
                      disabled={!(persona.nacionalidad && persona.nacionalidad.codigo === "PAIS_9300")}
                      onClick={() => {
                        console.log(`DepartamentoResidencia habilitado para persona #${index + 1}:`, persona.nacionalidad);
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
                      disabled={!(persona.nacionalidad && persona.nacionalidad.codigo === "PAIS_9300")}
                      onClick={() => {
                        console.log(`MunicipioResidencia habilitado para persona #${index + 1}:`, persona.nacionalidad);
                      }}
                    />
                  </div>

                  {/* Nacionalidad*/}
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
                        console.log(`Nacionalidad seleccionada para persona #${index + 1}:`, nuevaNacionalidad);
                        // Si NO es El Salvador (9300), borrar departamento y municipio
                        if (!nuevaNacionalidad || nuevaNacionalidad.codigo !== "9300") {
                          actualizarPersona(index, "departamentoResidencia", null);
                          actualizarPersona(index, "municipioResidencia", null);
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

              <TabPanel header="Violencia">
                <div className="mb-3">
                  <label className="mb-2 d-block">
                    ¿Desea registrar violencia?
                  </label>
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

                    <div className="field col-12 md:col-4">
                      <label className="mb-2 d-block">
                        Artefacto utilizado
                      </label>
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

                    <div className="field col-12">
                      <label className="mb-2 d-block">
                        Respuesta del Estado
                      </label>
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
                  <label className="mb-2 d-block">
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
                      <label className="mb-2 d-block">
                        Tipo de proceso judicial
                      </label>
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

                    <div className="field col-12 md:col-4">
                      <label className="mb-2 d-block">Fecha de denuncia</label>
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
                      <label className="mb-2 d-block">
                        Tipo de denunciante
                      </label>
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

                    <div className="field col-12 md:col-4">
                      <label className="mb-2 d-block">
                        Duración del proceso
                      </label>
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

                    <div className="field col-12 md:col-4">
                      <label className="mb-2 d-block">
                        ¿Tuvo acceso a abogado?
                      </label>
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

                    <div className="field col-12 md:col-4">
                      <label className="mb-2 d-block">¿Hubo parcialidad?</label>
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

                    <div className="field col-12 md:col-6">
                      <label className="mb-2 d-block">
                        Resultado del proceso
                      </label>
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
                      <label className="mb-2 d-block">Instancia</label>
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
                      <label className="mb-2 d-block">
                        Motivo de detención
                      </label>
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
