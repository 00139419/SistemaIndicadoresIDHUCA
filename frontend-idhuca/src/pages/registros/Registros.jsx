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
  let { filtros, derechoId } = location.state || {};

  derechoId = String(derechoId || "");

  if (!derechoId.startsWith("DER_")) {
    derechoId = "DER_" + derechoId;
  }

  console.log("derechoId start: " + derechoId);

  const [registroSeleccionado, setRegistroSeleccionado] = useState(null);
  const { userRole } = useAuth();
  const navigate = useNavigate();
  const { derechoCodigo } = useParams();
  const [derechos, setDerechos] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState([]);
  const [paginacion, setPaginacion] = useState({
    paginaActual: 0,
    totalPaginas: 0,
    totalRegistros: 0,
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

  const fetchRegistros = async (pagina = 0) => {
    console.log("fetch registros");
    try {
      setIsLoading(true);
      setError(null);

      const derechoSeleccionado = derechos.find((d) => d.codigo === derechoId);

      if (!derechoSeleccionado) {
        throw new Error("Código de derecho no válido");
      }

      console.log("Iniciando fetchRegistros con:", {
        derecho: derechoSeleccionado,
        pagina,
        registrosPorPagina: 10,
      });

      const response = await getRegistrosByDerecho(
        {
          codigo: derechoSeleccionado.codigo,
          descripcion: derechoSeleccionado.descripcion,
        },
        pagina,
        10
      );

      console.log("Respuesta de getRegistrosByDerecho:", response);

      if (response.registros && Array.isArray(response.registros)) {
        const formattedData = response.registros.map((registro) => ({
          id: registro.id,
          fechaHecho: registro.fechaHecho
            ? new Date(registro.fechaHecho).toLocaleDateString()
            : "N/A",
          fuente: registro.fuente?.descripcion || "N/A",
          estadoActual: registro.estadoActual?.descripcion || "N/A",
          flagViolencia: renderCheck(registro.flagViolencia),
          flagDetencion: renderCheck(registro.flagDetencion),
          flagExpresion: renderCheck(registro.flagExpresion),
          flagJusticia: renderCheck(registro.flagJusticia),
          flagCensura: renderCheck(registro.flagCensura),
          flagRegimenExcepcion: renderCheck(registro.flagRegimenExcepcion),
          observaciones: registro.observaciones || "N/A",
        }));

        setData(formattedData);
        setPaginacion(response.paginacion);
      } else {
        setData([]);
        setPaginacion({
          paginaActual: 0,
          totalPaginas: 0,
          totalRegistros: 0,
        });
      }
    } catch (error) {
      console.error("Error en fetchRegistros:", error);
      setError(error.message);
      setData([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handlePageChange = (newPage) => {
    fetchRegistros(newPage - 1);
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
      console.log("Eliminando:", item.id);
      await deleteEvent(item.id);

      setMessageSuccess("Registro eliminado con éxito.");
      setShowSuccess(true);
      setTimeout(() => setShowSuccess(false), 3000);

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
          itemsPerPage={10}
          currentPage={paginacion.paginaActual + 1}
          totalPages={paginacion.totalPaginas}
          onPageChange={handlePageChange}
        />
      )}
    </>
  );
};

export default Registros;
