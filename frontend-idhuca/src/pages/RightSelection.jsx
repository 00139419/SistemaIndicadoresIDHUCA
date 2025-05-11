import { useState } from 'react';

export default function SelectDerechosPage() {
  const [hoveredCard, setHoveredCard] = useState(null);

  const derechos = [
    {
      id: 1,
      title: 'Derecho a la Libertad Personal e Integridad personal',
      icon: (
        <svg width="64" height="64" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M24 12C26.2091 12 28 10.2091 28 8C28 5.79086 26.2091 4 24 4C21.7909 4 20 5.79086 20 8C20 10.2091 21.7909 12 24 12Z" fill="#4A5568"/>
          <path d="M30 20C30 18.9391 29.5786 17.9217 28.8284 17.1716C28.0783 16.4214 27.0609 16 26 16H22C20.9391 16 19.9217 16.4214 19.1716 17.1716C18.4214 17.9217 18 18.9391 18 20V28H30V20Z" fill="#4A5568"/>
          <path d="M36 28C38.2091 28 40 26.2091 40 24C40 21.7909 38.2091 20 36 20C33.7909 20 32 21.7909 32 24C32 26.2091 33.7909 28 36 28Z" fill="#4A5568"/>
          <path d="M12 28C14.2091 28 16 26.2091 16 24C16 21.7909 14.2091 20 12 20C9.79086 20 8 21.7909 8 24C8 26.2091 9.79086 28 12 28Z" fill="#4A5568"/>
          <path d="M12 30C9.79 30 6 31.105 6 33.5V38H18V33.5C18 31.105 14.21 30 12 30Z" fill="#4A5568"/>
          <path d="M36 30C33.79 30 30 31.105 30 33.5V38H42V33.5C42 31.105 38.21 30 36 30Z" fill="#4A5568"/>
          <path d="M24 30C21.79 30 18 31.105 18 33.5V42H30V33.5C30 31.105 26.21 30 24 30Z" fill="#4A5568"/>
        </svg>
      )
    },
    {
      id: 2,
      title: 'Derecho a la Libertad de Expresión',
      icon: (
        <svg width="64" height="64" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M40 8H8C5.79086 8 4 9.79086 4 12V32C4 34.2091 5.79086 36 8 36H16L24 44L32 36H40C42.2091 36 44 34.2091 44 32V12C44 9.79086 42.2091 8 40 8Z" fill="#4A5568"/>
          <path d="M12 16H36M12 24H28" stroke="white" strokeWidth="3" strokeLinecap="round"/>
        </svg>
      )
    },
    {
      id: 3,
      title: 'Derecho de Acceso a la Justicia',
      icon: (
        <svg width="64" height="64" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M24 4L4 12L24 20L44 12L24 4Z" fill="#4A5568"/>
          <rect x="22" y="20" width="4" height="20" fill="#4A5568"/>
          <rect x="8" y="40" width="32" height="4" fill="#4A5568"/>
          <rect x="4" y="14" width="4" height="24" fill="#4A5568"/>
          <rect x="40" y="14" width="4" height="24" fill="#4A5568"/>
        </svg>
      )
    },
    {
      id: 4,
      title: 'Derecho a la Vida',
      icon: (
        <svg width="64" height="64" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M34 4C29.582 4 26 7.582 26 12C26 22 36 30 36 30C36 30 46 22 46 12C46 7.582 42.418 4 38 4C35.536 4 33.332 5.294 32 7.294C30.668 5.294 28.464 4 26 4H34Z" fill="#4A5568"/>
          <path d="M18 4C13.582 4 10 7.582 10 12C10 22 20 30 20 30C20 30 30 22 30 12C30 7.582 26.418 4 22 4C19.536 4 17.332 5.294 16 7.294C14.668 5.294 12.464 4 10 4H18Z" fill="#4A5568"/>
          <path d="M7 30C3.7 30 1 32.7 1 36V42H41V36C41 32.7 38.3 30 35 30H29.74C27.8 31.84 26.88 32.5 26.88 32.5C24.7 34.24 22.78 35 21 35C19.22 35 17.3 34.24 15.12 32.5C15.12 32.5 14.2 31.84 12.26 30H7Z" fill="#4A5568"/>
        </svg>
      )
    }
  ];

  const cardStyle = {
    transition: 'all 0.3s ease',
    cursor: 'pointer',
    height: '100%'
  };

  const hoveredStyle = {
    transform: 'scale(1.05)',
    boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)'
  };

  return (
    <div className="container py-5">
      <h1 className="text-center mb-5 fw-bold">Selecciona un derecho</h1>
      
      <div className="row justify-content-center">
        {/* Primera fila - 2 tarjetas */}
        <div className="col-md-6 col-lg-5 mb-4">
          <div 
            className="card bg-light p-4 d-flex flex-column align-items-center justify-content-center"
            style={hoveredCard === 1 ? {...cardStyle, ...hoveredStyle} : cardStyle}
            onMouseEnter={() => setHoveredCard(1)}
            onMouseLeave={() => setHoveredCard(null)}
          >
            <div className="mb-3">
              {derechos[0].icon}
            </div>
            <p className="text-center text-secondary">
              {derechos[0].title}
            </p>
          </div>
        </div>
        
        <div className="col-md-6 col-lg-5 mb-4">
          <div 
            className="card bg-light p-4 d-flex flex-column align-items-center justify-content-center"
            style={hoveredCard === 2 ? {...cardStyle, ...hoveredStyle} : cardStyle}
            onMouseEnter={() => setHoveredCard(2)}
            onMouseLeave={() => setHoveredCard(null)}
          >
            <div className="mb-3">
              {derechos[1].icon}
            </div>
            <p className="text-center text-secondary">
              {derechos[1].title}
            </p>
          </div>
        </div>
      </div>
      
      <div className="row justify-content-center">
        {/* Segunda fila - 2 tarjetas */}
        <div className="col-md-6 col-lg-5 mb-4">
          <div 
            className="card bg-light p-4 d-flex flex-column align-items-center justify-content-center"
            style={hoveredCard === 3 ? {...cardStyle, ...hoveredStyle} : cardStyle}
            onMouseEnter={() => setHoveredCard(3)}
            onMouseLeave={() => setHoveredCard(null)}
          >
            <div className="mb-3">
              {derechos[2].icon}
            </div>
            <p className="text-center text-secondary">
              {derechos[2].title}
            </p>
          </div>
        </div>
        
        <div className="col-md-6 col-lg-5 mb-4">
          <div 
            className="card bg-light p-4 d-flex flex-column align-items-center justify-content-center"
            style={hoveredCard === 4 ? {...cardStyle, ...hoveredStyle} : cardStyle}
            onMouseEnter={() => setHoveredCard(4)}
            onMouseLeave={() => setHoveredCard(null)}
          >
            <div className="mb-3">
              {derechos[3].icon}
            </div>
            <p className="text-center text-secondary">
              {derechos[3].title}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}