import { useState } from 'react';
import Card from '../components/cards/FormCard'; 

// Main form component
const Form = () => {
  const [formData, setFormData] = useState({
    issueDate: '',
    eventDate: '',
    department: '',
    detentions: { enabled: false, count: '' },
  });

  // State to manage the expanded state of each card
  const [expandedCards, setExpandedCards] = useState({});

  const toggleCard = (cardId) => {
    setExpandedCards((prevState) => ({
      ...prevState,
      [cardId]: !prevState[cardId], // Toggle the expanded state for the specific card
    }));
  };

  const cardsConfig = [
    {
      id: 'issueDate',
      icon: 'bi-calendar',
      title: 'Fecha de registro',
      subtitle: 'Fecha en la que se registró el evento',
      content: (
        <div className="mb-3">
          <label className="form-label">Ingrese la fecha</label>
          <input
            type="text"
            className="form-control"
            value={formData.issueDate}
            onChange={(e) =>
              setFormData({ ...formData, issueDate: e.target.value })
            }
            placeholder="8/04/2025"
          />
        </div>
      ),
    },
    {
      id: 'eventDate',
      icon: 'bi-calendar',
      title: 'Fecha de hecho',
      subtitle: 'Fecha en la que ocurrió el evento',
      content: (
        <div className="mb-3">
          <label className="form-label">Ingrese la fecha</label>
          <input
            type="text"
            className="form-control"
            value={formData.eventDate}
            onChange={(e) =>
              setFormData({ ...formData, eventDate: e.target.value })
            }
            placeholder="8/04/2025"
          />
        </div>
      ),
    },
    {
      id: 'department',
      icon: 'bi-geo-alt',
      title: 'Departamento',
      subtitle: 'Seleccione el departamento del suceso',
      content: (
        <div className="mb-3">
          <label className="form-label">Departamento</label>
          <select
            className="form-select"
            value={formData.department}
            onChange={(e) =>
              setFormData({ ...formData, department: e.target.value })
            }
          >
            <option value="">Seleccione el departamento</option>
            <option value="Ahuachapan">Ahuachapan</option>
            <option value="San Miguel">San Miguel</option>
            <option value="Santa Ana">Santa Ana</option>
          </select>
        </div>
      ),
    },
    {
      id: 'detentions',
      icon: 'bi-person-fill-lock',
      title: 'Detenciones',
      subtitle: 'Registro de detenciones si las hubo',
      content: (
        <>
          <div className="mb-3 d-flex justify-content-between align-items-center">
            <label className="form-label mb-0">Hubo personas detenidas</label>
            <div className="form-check form-switch">
              <input
                className="form-check-input"
                type="checkbox"
                checked={formData.detentions.enabled}
                onChange={() =>
                  setFormData({
                    ...formData,
                    detentions: {
                      ...formData.detentions,
                      enabled: !formData.detentions.enabled,
                    },
                  })
                }
              />
            </div>
          </div>
          {formData.detentions.enabled && (
            <div className="mb-3">
              <label className="form-label">
                Ingrese el número de personas detenidas
              </label>
              <input
                type="number"
                className="form-control"
                value={formData.detentions.count}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    detentions: {
                      ...formData.detentions,
                      count: e.target.value,
                    },
                  })
                }
                placeholder="0"
                min="0"
              />
            </div>
          )}
        </>
      ),
    },
  ];

  return (
    <div className="row">
      <div className="col-12">
        <h2 className="mb-4">Formulario de Registro</h2>
        {cardsConfig.map((card) => (
          <Card
            key={card.id}
            icon={card.icon}
            title={card.title}
            subtitle={card.subtitle}
            expanded={!!expandedCards[card.id]} // Check if the card is expanded
            onToggleExpand={() => toggleCard(card.id)} // Toggle the specific card
          >
            {card.content}
          </Card>
        ))}
      </div>
    </div>
  );
};

export default Form;