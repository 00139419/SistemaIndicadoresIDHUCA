import { useState, useEffect } from "react";
import { useMemo } from "react";
import { useAuth } from "../../components/AuthContext";
import graphImage from "../../assets/image.png";
import { useLocation } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import React, { useRef } from "react";
import axios from "axios";

const Graficos = () => {
  const imgRef = useRef(null);
  const { userRole } = useAuth();
  const navigate = useNavigate();

  const API_URL = "http://localhost:8080/idhuca-indicadores/api/srv";

  const location = useLocation();
  let { derechoId, filtros, categoriaEjeX } = location.state || {};

  const hasPermission = (action) => {
    switch (action) {
      case "generate":
      case "modify":
      case "export":
        return ["ROL_1", "ROL_2", "ROL_3"].includes(userRole);
      default:
        return false;
    }
  };

  const nombresCamposEjeX = {
    "eventoFiltro.fechaHechoRango": "Fecha del hecho",
    "eventoFiltro.fuentes": "Fuente del hecho",
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

  // Detectar clave del objeto anidado (ej: "eventoFiltro.fuentes")
  const campoEjeXSeleccionado = useMemo(() => {
    if (!categoriaEjeX) return;

    const padre = Object.keys(categoriaEjeX)[0];
    if (!padre) return null;

    const hijo = Object.keys(categoriaEjeX[padre] || {})[0];
    if (!hijo) return null;

    return `${padre}.${hijo}`;
  }, [categoriaEjeX]);

  const nombreCampo = nombresCamposEjeX[campoEjeXSeleccionado] || null;

  const [imagenRenderizada, setImagenRenderizada] = useState(null);

  const [chartConfig, setChartConfig] = useState({
    tipoGrafico: "Pastel",
    dimension: "2D",
    titulo: "",
    subtitulo: "",
  });

  const tiposGrafico = ["Pastel", "Barras", "Líneas", "Área"];

  const handleConfigChange = (field, value) => {
    setChartConfig((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const generarGrafico = async () => {
    try {

    if(!filtros){
      filtros = {};
    }

      const request = {
        derecho: { codigo: derechoId },
        filtros,
        categoriaEjeX,
        graphicsSettings: {
          chartType: "PIE",
        },
      };

      const token = localStorage.getItem("authToken");
      if (!token) {
        throw new Error("No hay token de autenticación");
      }

      console.log("request " + JSON.stringify(request))

      const response = await axios.post(
        `${API_URL}/graphics/generate`,
        request,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      const base64 = response.data.entity.base64;
      console.log(response.data);
      const imagenConPrefijo = `data:image/png;base64,${base64}`;
      setImagenRenderizada(imagenConPrefijo);
    } catch (error) {
      console.error("Error al generar el gráfico:", error);
    }
  };

  useEffect(() => {
    if (nombreCampo && chartConfig.tipoGrafico) {
      generarGrafico();
    }
  }, [
    categoriaEjeX,
    chartConfig.tipoGrafico,
    chartConfig.dimension,
    chartConfig.titulo,
    chartConfig.subtitulo,
  ]);

  const copyToClipboard = async () => {};

  const downloadAsPNG = () => {
    // En la implementación real, aquí generar y descargar el PNG del gráfico
    alert("Descargando gráfico como PNG...");
  };

  const handleClick = () => {
    navigate("/selectEjeX", {
      state: { derechoId, filtros, categoriaEjeX },
    });
  };

  // Función para descargar la imagen base64 como archivo PNG
  const descargarImagen = () => {
    const link = document.createElement("a");
    link.href = graphImage;
    link.download = "grafico.png";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // Función para copiar la imagen al portapapeles como blob
  const copiarAlPortapapeles = async () => {
    try {
      if (!imagenRenderizada) return;

      const response = await fetch(imagenRenderizada);
      const blob = await response.blob();

      await navigator.clipboard.write([
        new ClipboardItem({ [blob.type]: blob }),
      ]);

      alert("Imagen copiada al portapapeles");
    } catch (err) {
      alert("Error al copiar la imagen al portapapeles: " + err.message);
      console.error(err);
    }
  };

  return (
    <div
      className="container-fluid px-0"
      style={{
        minHeight: "100vh",
        width: "100%",
        maxWidth: "100%",
      }}
    >
      <div className="container-fluid px-4 py-5" style={{ minHeight: "100%" }}>
        {/* Título mejorado */}
        <div className="text-center mb-5">
          <h1
            className="display-4 fw-bold"
            style={{
              fontSize: "2.3rem",
              color: "#000000",
            }}
          >
            Gráficos
          </h1>
        </div>

        <div className="row g-4" style={{ minHeight: "80vh" }}>
          {/* Panel izquierdo - Configuración - Reducido */}
          {hasPermission("modify") && (
            <div className="col-md-4 col-lg-3">
              <div
                className="bg-white p-3 rounded-lg shadow-sm h-100"
                style={{
                  borderRadius: "15px",
                  border: "1px solid #e0e0e0",
                  maxHeight: "80vh",
                  overflowY: "auto",
                }}
              >
                {/* Sección Dimensiones */}
                <div className="mb-4">
                  <h6
                    className="fw-bold mb-3"
                    style={{ color: "#1a237e", fontSize: "1rem" }}
                  >
                    <i className="bi bi-sliders me-2"></i>Dimensiones
                  </h6>

                  <div className="mb-3">
                    <label
                      className="form-label fw-semibold"
                      style={{ fontSize: "0.9rem" }}
                    >
                      Eje X
                    </label>
                    <button
                      type="button"
                      className="btn btn-outline-secondary text-start"
                      style={{
                        backgroundColor: "#f8f9fa",
                        border: "1px solid #ced4da",
                        borderRadius: "6px",
                        fontSize: "0.85rem",
                        height: "38px",
                        width: "100%",
                        paddingLeft: "0.75rem",
                      }}
                      onClick={handleClick}
                    >
                      {nombreCampo
                        ? `Eje X: ${nombreCampo}`
                        : "Seleccionar eje X..."}
                    </button>
                  </div>
                </div>

                {/* Sección Propiedades */}
                <div className="mb-4">
                  <h6
                    className="fw-bold mb-3"
                    style={{ color: "#1a237e", fontSize: "1rem" }}
                  >
                    <i className="bi bi-gear me-2"></i>Propiedades
                  </h6>

                  <div className="mb-3">
                    <label
                      className="form-label fw-semibold"
                      style={{ fontSize: "0.9rem" }}
                    >
                      Tipo de gráfico
                    </label>
                    <select
                      className="form-select form-select-sm"
                      style={{
                        backgroundColor: "#f8f9fa",
                        border: "1px solid #ced4da",
                        borderRadius: "6px",
                        fontSize: "0.85rem",
                      }}
                      value={chartConfig.tipoGrafico}
                      onChange={(e) =>
                        handleConfigChange("tipoGrafico", e.target.value)
                      }
                    >
                      {tiposGrafico.map((tipo, index) => (
                        <option key={index} value={tipo}>
                          {tipo}
                        </option>
                      ))}
                    </select>
                  </div>

                  {/* Botones 2D/3D */}
                  <div className="mb-3">
                    <label
                      className="form-label fw-semibold"
                      style={{ fontSize: "0.9rem" }}
                    >
                      Dimensión
                    </label>
                    <div className="btn-group w-100" role="group">
                      <button
                        type="button"
                        className={`btn btn-sm ${
                          chartConfig.dimension === "2D"
                            ? "btn-primary"
                            : "btn-outline-primary"
                        }`}
                        onClick={() => handleConfigChange("dimension", "2D")}
                        style={{
                          fontSize: "0.8rem",
                          backgroundColor:
                            chartConfig.dimension === "2D"
                              ? "#1a237e"
                              : "transparent",
                          borderColor: "#1a237e",
                        }}
                      >
                        2D
                      </button>
                      <button
                        type="button"
                        className={`btn btn-sm ${
                          chartConfig.dimension === "3D"
                            ? "btn-primary"
                            : "btn-outline-primary"
                        }`}
                        onClick={() => handleConfigChange("dimension", "3D")}
                        style={{
                          fontSize: "0.8rem",
                          backgroundColor:
                            chartConfig.dimension === "3D"
                              ? "#1a237e"
                              : "transparent",
                          borderColor: "#1a237e",
                        }}
                      >
                        3D
                      </button>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label
                      className="form-label fw-semibold"
                      style={{ fontSize: "0.9rem" }}
                    >
                      Título
                    </label>
                    <input
                      type="text"
                      className="form-control form-control-sm"
                      style={{
                        backgroundColor: "#f8f9fa",
                        border: "1px solid #ced4da",
                        borderRadius: "6px",
                        fontSize: "0.85rem",
                      }}
                      value={chartConfig.titulo}
                      onChange={(e) =>
                        handleConfigChange("titulo", e.target.value)
                      }
                      placeholder="Ingrese el título del gráfico"
                    />
                  </div>

                  <div className="mb-3">
                    <label
                      className="form-label fw-semibold"
                      style={{ fontSize: "0.9rem" }}
                    >
                      Subtítulo
                    </label>
                    <input
                      type="text"
                      className="form-control form-control-sm"
                      style={{
                        backgroundColor: "#f8f9fa",
                        border: "1px solid #ced4da",
                        borderRadius: "6px",
                        fontSize: "0.85rem",
                      }}
                      value={chartConfig.subtitulo}
                      onChange={(e) =>
                        handleConfigChange("subtitulo", e.target.value)
                      }
                      placeholder="Ingrese el subtítulo del gráfico"
                    />
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Panel derecho - Visualización del gráfico - Expandido */}
          <div
            className={hasPermission("modify") ? "col-md-8 col-lg-9" : "col-12"}
          >
            <div
              className="bg-white p-4 rounded-lg shadow-sm h-100 d-flex flex-column"
              style={{
                borderRadius: "15px",
                border: "1px solid #e0e0e0",
                minHeight: "80vh",
              }}
            >
              <div
                className="flex-grow-1 d-flex align-items-center justify-content-center"
                style={{ minHeight: "500px" }}
              >
                <div className="position-relative w-100 h-100 d-flex align-items-center justify-content-center flex-column">
                  {imagenRenderizada ? (
                    <>
                      <img
                        ref={imgRef}
                        src={imagenRenderizada}
                        alt="Gráfico de ejemplo"
                        className="img-fluid"
                        style={{
                          maxWidth: "95%",
                          maxHeight: "95%",
                          minHeight: "400px",
                          objectFit: "contain",
                          borderRadius: "8px",
                          boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
                        }}
                      />
                      <div className="mt-3 d-flex gap-2">
                        <button
                          className="btn btn-primary"
                          onClick={descargarImagen}
                        >
                          Descargar imagen
                        </button>
                        <button
                          className="btn btn-secondary"
                          onClick={copiarAlPortapapeles}
                        >
                          Copiar al portapapeles
                        </button>
                      </div>
                    </>
                  ) : (
                    <div className="text-muted fs-6 text-center">
                      Selecciona todas las opciones para generar el gráfico.
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Graficos;
