import { useState } from 'react';
import { useAuth } from '../../components/AuthContext';
import { useNavigate } from 'react-router-dom';
import VistaRegistrosDinamica from '../../components/VistaRegistrosDinamica';

const Registros = () => {
  const { userRole } = useAuth();
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  const sampleColumns = [
    { key: 'Text1', title: 'Text' },
    { key: 'Text2', title: 'Text' },
    { key: 'Text3', title: 'Text' }
  ];

  const sampleData = [
    { Text1: 'Text', Text2: 'Text', Text3: 'Text' },
    { Text1: 'Text', Text2: 'Text', Text3: 'Text' },
    { Text1: 'Text', Text2: 'Text', Text3: 'Text' }
  ];

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
    // Implementar lógica de filtrado
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
      columns={sampleColumns}
      data={sampleData}
      isLoading={isLoading}
      onView={showView ? handleView : null}
      onEdit={showEdit ? handleEdit : null}
      onDelete={showDelete ? handleDelete : null}
      onCreate={showCreate ? handleCreate : null}
      onGenerateChart={showGenerateChart ? handleGenerateChart : null}
      onFilter={showFilter ? handleFilter : null}
    />
  );
};

export default Registros;