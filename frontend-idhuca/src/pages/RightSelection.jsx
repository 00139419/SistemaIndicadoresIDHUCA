import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import ExpresionIcon from "../assets/icons/expresion.png";
import LibertadIcon from "../assets/icons/libertad.png";
import JusticiaIcon from "../assets/icons/justicia.png";
import VidaIcon from "../assets/icons/vida.png";

export default function SelectDerechosPage() {
  const [hoveredCard, setHoveredCard] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();

  // Obtener el 'from' del state pasado desde la navbar
  const from = location.state?.from;

  const derechos = [
    {
      id: 1,
      title: "Derecho a la Libertad Personal e Integridad personal",
      icon: (
        <img
          src={LibertadIcon}
          alt="Libertad Personal e Integridad"
          width="64"
          height="64"
        />
      ),
    },
    {
      id: 2,
      title: "Derecho a la Libertad de Expresión",
      icon: (
        <img
          src={ExpresionIcon}
          alt="Libertad de Expresión"
          width="64"
          height="64"
        />
      ),
    },
    {
      id: 3,
      title: "Derecho de Acceso a la Justicia",
      icon: (
        <img
          src={JusticiaIcon}
          alt="Acceso a la Justicia"
          width="64"
          height="64"
        />
      ),
    },
    {
      id: 4,
      title: "Derecho a la Vida",
      icon: (
        <img src={VidaIcon} alt="Derecho a la Vida" width="64" height="64" />
      ),
    },
  ];

  const handleSelect = (derecho) => {
    // Pasar solo valores primitivos para evitar errores de clonación
    const derechoId = derecho.id;
    const derechoTitle = derecho.title;
    localStorage.setItem("selectedDerechoId", derechoId); // <-- guarda aquí
    localStorage.setItem("selectedDerechoTitle", derechoTitle); // <-- guarda aquí

    // Determinar la ruta basada en el 'from' recibido
    const path = from === "ficha" ? "/ficha-de-derechos" : "/select-register";

    navigate(path, {
      state: {
        derechoId: derechoId,
        derechoTitle: derechoTitle,
      },
    });
  };

  /* --- Estilos de las cartas --- */
  const cardStyle = {
    transition: "all 0.3s ease",
    cursor: "pointer",
    minHeight: "250px",
    border: "1px solid #e9ecef",
  };

  const hoveredStyle = {
    transform: "translateY(-8px) scale(1.03)",
    boxShadow: "0 16px 32px -8px rgba(0,0,0,0.18), 0 4px 12px -2px rgba(0,0,0,0.10)",
    background: "linear-gradient(90deg, #e3f0ff 0%, #f8faff 100%)",
    border: "2px solid #1976d2",
  };

  return (
    <div
      className="d-flex flex-column justify-content-center align-items-center"
      style={{
        position: "absolute",
        top: "50%",
        left: "50%",
        transform: "translate(-50%, -50%)",
        width: "100vw",
        minHeight: "auto",
        margin: 0,
        padding: 0,
        overflow: "hidden"
      }}
    >
      {/* Título */}
      <h1
        className="fw-bold text-primary text-center fs-2"
        style={{ fontFamily: 'Montserrat, Arial, sans-serif', letterSpacing: '1px' }}
      >
        Selecciona un derecho
      </h1>

      {/* Contenedor de las cartas */}
      <div className="container-fluid px-4">
        <div
          className="row justify-content-center" // solo gap vertical
          style={{ maxWidth: "900px", margin: "0 auto" }}
        >
          {derechos.map((derecho, index) => (
            <div key={derecho.id} className="col-6">
              <div
                className="card bg-light p-4 d-flex flex-column align-items-center justify-content-center shadow-sm h-100"
                style={
                  hoveredCard === index
                    ? { ...cardStyle, ...hoveredStyle }
                    : cardStyle
                }
                onMouseEnter={() => setHoveredCard(index)}
                onMouseLeave={() => setHoveredCard(null)}
                onClick={() => handleSelect(derecho)}
              >
                <div className="mb-4" style={{ transform: "scale(1.3)" }}>
                  <span style={{
                    display: "inline-block",
                    filter: hoveredCard === index ? "drop-shadow(0 0 1px #1976d2)" : "none"
                  }}>
                    {derecho.icon}
                  </span>
                </div>
                <p
                  className={`text-center mb-0 fw-medium ${hoveredCard === index ? "text-primary" : "text-secondary"}`}
                  style={{ fontSize: "1.1rem", lineHeight: "1.4" }}
                >
                  {derecho.title}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}