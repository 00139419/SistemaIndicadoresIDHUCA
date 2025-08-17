import { useState, useEffect } from "react";
import { useAuth } from "../../components/AuthContext";
import { useNavigate, useParams } from "react-router-dom";
import VistaRegistrosDinamica from "../../components/VistaRegistrosDinamica";
import DetalleRegistro from "../registros/DetalleRegistro";
import {
  getRegistrosByDerecho,
  renderCheck,
  deleteEvent,
  getDerechosCatalog,
  detailEvent,
} from "../../services/RegstrosService";
import { useLocation } from "react-router-dom";

const Registros = () => {
  const location = useLocation();
  let { filtros, derechoId, categoriaEjeX } = location.state || {};
  // Asegurar que filtros siempre sea un objeto
  if (!filtros) filtros = {};

  derechoId = String(derechoId || "");

  if (!derechoId.startsWith("DER_")) {
    derechoId = "DER_" + derechoId;
  }

  const [registroSeleccionado, setRegistroSeleccionado] = useState(null);
  const { userRole } = useAuth();
  const navigate = useNavigate();
  const { derechoCodigo } = useParams();
  const [derechos, setDerechos] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState([]);

  const [paginacion, setPaginacion] = useState({
    paginaActual: 1, 
    totalPaginas: 0,
    totalRegistros: 0,
    registrosPorPagina: 5,
  });

  // Para mini modales
  const [showSuccess, setShowSuccess] = useState(false);
  const [messageSuccess, setMessageSuccess] = useState("");

  const columns = [
    { key: "fechaHecho", title: "Fecha del Hecho" },
    { key: "fuente", title: "Fuente" },
    { key: "estadoActual", title: "Estado Actual" },
    { key: "flagViolencia", title: "Violencia" },
    { key: "flagDetencion", title: "Detención" },
    { key: "flagExpresion", title: "Expresión" },
    { key: "flagJusticia", title: "Justicia" },
    { key: "flagCensura", title: "Censura" },
    { key: "flagRegimenExcepcion", title: "Régimen de Excepción" },
    { key: "observaciones", title: "Observaciones" },
  ];

  const fetchDerechos = async () => {
    try {
      const items = await getDerechosCatalog();
      setDerechos(items);
    } catch (error) {
      setError(error.message);
    }
  };

  useEffect(() => {
    fetchDerechos();
  }, []);

  useEffect(() => {
    if (derechoId && derechos.length > 0) {
      const derechoSeleccionado = derechos.find((d) => d.codigo === derechoId);
      console.log("derechoSeleccionado:", derechoSeleccionado);

      if (derechoSeleccionado) {
        fetchRegistros();
      } else {
        console.error("Derecho no encontrado en el catálogo");
        setError("Derecho no encontrado en el catálogo");
      }
    }
  }, [derechoCodigo, derechos]);

  const fetchRegistros = async (pagina = paginacion.paginaActual) => {
    try {
      setIsLoading(true);
      setError(null);

      const derechoSeleccionado = derechos.find((d) => d.codigo === derechoId);
      if (!derechoSeleccionado) throw new Error("Código de derecho no válido");


      // Siempre clonar filtros para no mutar el original
      const filtrosUsados = { ...filtros };
      filtrosUsados.paginacion = {
        paginaActual: pagina - 1,
        registrosPorPagina: paginacion.registrosPorPagina || 5,
      };

      const resp = await getRegistrosByDerecho(
        {
          codigo: derechoSeleccionado.codigo,
          descripcion: derechoSeleccionado.descripcion,
        },
        filtrosUsados
      );

      const formatted = (resp.registros ?? []).map((r) => ({
        id: r.id,
        fechaHecho: r.fechaHecho
          ? new Date(r.fechaHecho).toLocaleDateString()
          : "N/A",
        fuente: r.fuente?.descripcion || "N/A",
        estadoActual: r.estadoActual?.descripcion || "N/A",
        flagViolencia: renderCheck(r.flagViolencia),
        flagDetencion: renderCheck(r.flagDetencion),
        flagExpresion: renderCheck(r.flagExpresion),
        flagJusticia: renderCheck(r.flagJusticia),
        flagCensura: renderCheck(r.flagCensura),
        flagRegimenExcepcion: renderCheck(r.flagRegimenExcepcion),
        observaciones: r.observaciones || "N/A",
      }));
      setData(formatted); // ←  esto es lo que alimenta la tabla

      setPaginacion((prev) => ({
        ...prev,
        paginaActual: resp.paginacion.paginaActual + 1, // lo volvemos 1‑based
        totalPaginas: resp.paginacion.totalPaginas,
        totalRegistros: resp.paginacion.totalRegistros,
      }));
    } catch (e) {
      setError(e.message);
      setData([]); // para que la UI lo refleje
    } finally {
      setIsLoading(false);
    }
  };

  const handleView = (item) => {
    navigate(`/registros/detalle/${item.id}`);
  };

  const handleEdit = (item) => {
    console.log("Editar:", item);
  };

  const handleDelete = async (item) => {
    const confirmDelete = window.confirm(
      "¿Estás seguro de que deseas eliminar este registro?"
    );

    if (!confirmDelete) return;

    try {
      // Elimina el registro del estado local antes de hacer fetch
      setData((prevData) => prevData.filter((reg) => reg.id !== item.id));

      await deleteEvent(item.id);

      setMessageSuccess("Registro eliminado con éxito.");
      setShowSuccess(true);
      setTimeout(() => setShowSuccess(false), 3000);

      // Vuelve a cargar los registros para mantener la paginación y datos correctos
      fetchRegistros();
    } catch (error) {
      console.error(error);
      alert("Error al eliminar.");
    }
  };

  const handleCreate = () => {
    console.log("Crear nuevo registro");
  };

  const handleGenerateChart = () => {
    navigate("/graphs");
  };

  const handleFilter = () => {
    navigate("/filter");
    console.log("Aplicando filtros...");
  };

  // Determinar qué acciones mostrar según el rol
  const showEdit = ["ROL_1", "ROL_2"].includes(userRole);
  const showView = true; // Todos los roles pueden ver
  const showDelete = ["ROL_1", "ROL_2"].includes(userRole);
  const showCreate = ["ROL_1", "ROL_2"].includes(userRole);
  const showFilter = true; // Todos los roles pueden filtrar
  const showGenerateChart = true; // Todos los roles pueden generar gráficos

  const handlePageChange = (nuevaPagina) => {
    if (nuevaPagina < 1 || nuevaPagina > paginacion.totalPaginas) return;
    setPaginacion((prev) => ({ ...prev, paginaActual: nuevaPagina }));
  };

  const handleItemsPerPageChange = (e) => {
    const perPage = Number(e.target.value);
    setPaginacion((prev) => ({
      ...prev,
      registrosPorPagina: perPage,
      paginaActual: 1, // volvemos a la primera
    }));
  };

  useEffect(() => {
    if (derechos.length > 0) {
      fetchRegistros(paginacion.paginaActual);
    }
  }, [paginacion.paginaActual, paginacion.registrosPorPagina, derechos]);

  return (
    <>
      {showSuccess && (
        <div
          className="alert alert-success alert-dismissible fade show mx-3 mt-3"
          role="alert"
        >
          {messageSuccess}
          <button
            type="button"
            className="btn-close"
            onClick={() => setShowSuccess(false)}
            aria-label="Close"
          ></button>
        </div>
      )}

      {registroSeleccionado ? (
        <DetalleRegistro
          registro={registroSeleccionado}
          onBack={() => setRegistroSeleccionado(null)} // si agregas un botón para volver
        />
      ) : (
        <VistaRegistrosDinamica
          categoriaEjeX={categoriaEjeX}
          filtros={filtros}
          derechoId={derechoId}
          title="Registros"
          columns={columns}
          data={data}
          isLoading={isLoading}
          error={error}
          onView={handleView}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onCreate={handleCreate}
          onGenerateChart={handleGenerateChart}
          onFilter={handleFilter}
          itemsPerPage={paginacion.registrosPorPagina}
          currentPage={paginacion.paginaActual}
          totalPages={paginacion.totalPaginas}
          onPageChange={handlePageChange}
        />
      )}

      {!isLoading && !error && (
        <div className="px-4 py-3 border-top bg-#ffffff">
          <div className="d-flex justify-content-between align-items-center">
            <div>
              Mostrar
              <select
                className="form-select form-select-sm d-inline-block mx-2"
                style={{ width: "80px" }}
                value={paginacion.registrosPorPagina}
                onChange={handleItemsPerPageChange}
              >
                {[5, 10, 15, 20, 25, 50].map((n) => (
                  <option key={n} value={n}>
                    {n}
                  </option>
                ))}
              </select>
              registros por página
            </div>

            {/* 🆕 total de registros */}
            <span className="ms-3 text-muted" style={{ fontSize: "0.85rem" }}>
              Registros totales: <strong>{paginacion.totalRegistros}</strong>
            </span>

            <div>
              Página {paginacion.paginaActual} de {paginacion.totalPaginas}
              <div className="btn-group ms-2">
                <button
                  className="btn btn-sm btn-outline-secondary"
                  onClick={() => handlePageChange(1)}
                  disabled={paginacion.paginaActual === 1}
                  title="Primera página"
                >
                  &laquo;
                </button>
                <button
                  className="btn btn-sm btn-outline-secondary"
                  onClick={() => handlePageChange(paginacion.paginaActual - 1)}
                  disabled={paginacion.paginaActual === 1}
                  title="Página anterior"
                >
                  &lt;
                </button>
                <button
                  className="btn btn-sm btn-outline-secondary"
                  onClick={() => handlePageChange(paginacion.paginaActual + 1)}
                  disabled={
                    paginacion.paginaActual === paginacion.totalPaginas ||
                    paginacion.totalPaginas === 0
                  }
                  title="Página siguiente"
                >
                  &gt;
                </button>
                <button
                  className="btn btn-sm btn-outline-secondary"
                  onClick={() => handlePageChange(paginacion.totalPaginas)}
                  disabled={
                    paginacion.paginaActual === paginacion.totalPaginas ||
                    paginacion.totalPaginas === 0
                  }
                  title="Última página"
                >
                  &raquo;
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default Registros;
