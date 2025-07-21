import logo from "../../assets/LogoMain.png";
import { useState, useEffect, useRef } from "react";
import { authFetch } from "./../../authFetch";

const Index = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [info, setInfo] = useState(null);

  useEffect(() => {
    loadInformatoin();
  }, []);

  async function loadInformatoin() {
    setLoading(true);
    setError(null);
    try {
      const data = await obtenerParametro();
      setInfo(data);
    } catch (err) {
      setError("No se pudieron cargar la informacion");
    } finally {
      setLoading(false);
    }
  }

  async function obtenerParametro() {
    const info = {};

    try {
      const data = await authFetch("parametros/sistema/getOne", {
        method: "POST",
        body: JSON.stringify({ clave: "acerca_del_proyecto" }),
      });

      info.descripcion = data.entity.valor;
    } catch (err) {
      console.error("Error al consultar parámetro:", err);
    }
    return info;
  }

  return (
    <div className="container-fluid px-4 py-5">
      {/* Título principal */}
      <div className="row mb-5">
        <div className="col-12 text-center">
          <h1
            className="fw-bold"
            style={{
              fontSize: "2.2rem", // Más pequeño que display-4
              letterSpacing: "0.5px", // Espaciado de medio pixel
              lineHeight: "1.2",
            }}
          >
            Bienvenido al Sistema de Registros de Vulneraciones
            <br />
            de Derechos Humanos del IDHUCA
          </h1>
        </div>
      </div>

      {/* Contenido principal: Logo y descripción */}
      <div className="row align-items-center">
        {/* Columna del logo */}
        <div className="col-md-5 text-center mb-4 mb-md-0">
          <img
            src={logo}
            alt="Logo OUDH - Observatorio Universitario de Derechos Humanos"
            className="img-fluid"
            style={{ maxWidth: "350px" }}
          />
        </div>

        {/* Columna de texto */}
        <div className="col-md-7">
          <h2 className="fw-bold mb-3">Acerca del Proyecto</h2>
          {loading ? (
            <p>Cargando descripción...</p>
          ) : error ? (
            <p className="text-danger">{error}</p>
          ) : (
            <p style={{ textAlign: "justify" }}>{info.descripcion}</p>
          )}
          <p className="mb-3"></p>
        </div>
      </div>
    </div>
  );
};

export default Index;
