import { useState, useEffect } from 'react';
import { useAuth } from '../../components/AuthContext';
import { useNavigate, useParams } from 'react-router-dom';
import VistaRegistrosDinamica from '../../components/VistaRegistrosDinamica';
import { getRegistrosByDerecho, fetchCatalog } from '../../services/RegstrosService';

const Registros = () => {
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
    totalRegistros: 0
  });

  const columns = [
    { key: 'fechaHecho', title: 'Fecha del Hecho' },
    { key: 'fuente', title: 'Fuente' },
    { key: 'estadoActual', title: 'Estado Actual' },
    { key: 'flagViolencia', title: 'Violencia' },
    { key: 'flagDetencion', title: 'Detención' },
    { key: 'flagExpresion', title: 'Expresión' },
    { key: 'flagJusticia', title: 'Justicia' },
    { key: 'flagCensura', title: 'Censura' },
    { key: 'flagRegimenExcepcion', title: 'Régimen de Excepción' },
    { key: 'observaciones', title: 'Observaciones' }
  ];

  const sampleData = [
    { Text1: 'Text', Text2: 'Text', Text3: 'Text' },
    { Text1: 'Text', Text2: 'Text', Text3: 'Text' },
    { Text1: 'Text', Text2: 'Text', Text3: 'Text' }
  ];

  const fetchDerechos = async () => {
    try {
      const params = {
        derechos: true,
        tipoProcesoJudicial: false,
        tipoDenunciante: false,
        duracionProceso: false,
        tipoRepresion: false,
        medioExpresion: false,
        motivoDetencion: false,
        tipoArma: false,
        tipoDetencion: false,
        tipoViolencia: false,
        estadoSalud: false,
        tipoPersona: false,
        genero: false,
        lugarExacto: false,
        estadoRegistro: false,
        fuentes: false,
        paises: false,
        subDerechos: false,
        roles: false,
        departamentos: false,
        municipios: false,
        securityQuestions: false,
        parentId: "DER_1",
        filtros: {
          paginacion: {
            paginaActual: 0,
            registrosPorPagina: 10
          }
        }
      };

      const response = await fetchCatalog(params);
      setDerechos(response.items); 
    } catch (error) {
      setError(error.message);
    }
  };

  useEffect(() => {
    fetchDerechos();
  }, []);

  useEffect(() => {
    console.log('derechoCodigo:', derechoCodigo);
    console.log('derechos:', derechos);
    
    if (derechoCodigo && derechos.length > 0) {
      const derechoSeleccionado = derechos.find(d => d.codigo === derechoCodigo);
      console.log('derechoSeleccionado:', derechoSeleccionado);
      
      if (derechoSeleccionado) {
        fetchRegistros();
      } else {
        console.error('Derecho no encontrado en el catálogo');
        setError('Derecho no encontrado en el catálogo');
      }
    }
  }, [derechoCodigo, derechos]);

  const fetchRegistros = async (pagina = 0) => {
    try {
      setIsLoading(true);
      setError(null);
      
      const derechoSeleccionado = derechos.find(d => d.codigo === derechoCodigo);
      
      if (!derechoSeleccionado) {
        throw new Error('Código de derecho no válido');
      }

      console.log('Iniciando fetchRegistros con:', {
        derecho: derechoSeleccionado,
        pagina,
        registrosPorPagina: 10
      });

      const response = await getRegistrosByDerecho({
        codigo: derechoSeleccionado.codigo,
        descripcion: derechoSeleccionado.descripcion
      }, pagina, 10);

      console.log('Respuesta de getRegistrosByDerecho:', response);

      if (response.registros && Array.isArray(response.registros)) {
        const formattedData = response.registros.map(registro => ({
          id: registro.id,
          fechaHecho: registro.fechaHecho ? new Date(registro.fechaHecho).toLocaleDateString() : 'N/A',
          fuente: registro.fuente?.descripcion || 'N/A',
          estadoActual: registro.estadoActual?.descripcion || 'N/A',
          flagViolencia: registro.flagViolencia ? '✓' : '✗',
          flagDetencion: registro.flagDetencion ? '✓' : '✗',
          flagExpresion: registro.flagExpresion ? '✓' : '✗',
          flagJusticia: registro.flagJusticia ? '✓' : '✗',
          flagCensura: registro.flagCensura ? '✓' : '✗',
          flagRegimenExcepcion: registro.flagRegimenExcepcion ? '✓' : '✗',
          observaciones: registro.observaciones || 'N/A'
        }));

        setData(formattedData);
        setPaginacion(response.paginacion);
      } else {
        setData([]);
        setPaginacion({
          paginaActual: 0,
          totalPaginas: 0,
          totalRegistros: 0
        });
      }
    } catch (error) {
      console.error('Error en fetchRegistros:', error);
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
    console.log('Ver:', item);
  };

  const handleEdit = (item) => {
    console.log('Editar:', item);
  };

  const handleDelete = (item) => {
    console.log('Eliminar:', item);
  };

  const handleCreate = () => {
    console.log('Crear nuevo registro');
  };

  const handleGenerateChart = () => {
    navigate('/graphs');
  };
  

  const handleFilter = () => {
    navigate('/filter');
    console.log('Aplicando filtros...');
  };

  // Determinar qué acciones mostrar según el rol
  const showEdit = ['ROL_1', 'ROL_2'].includes(userRole);
  const showView = true; // Todos los roles pueden ver
  const showDelete = ['ROL_1', 'ROL_2'].includes(userRole);
  const showCreate = ['ROL_1', 'ROL_2'].includes(userRole);
  const showFilter = true; // Todos los roles pueden filtrar
  const showGenerateChart = true; // Todos los roles pueden generar gráficos

   return (
    <VistaRegistrosDinamica
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
  );
};

export default Registros;