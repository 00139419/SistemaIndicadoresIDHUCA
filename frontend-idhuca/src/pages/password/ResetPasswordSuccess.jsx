import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import logoUCA from "../../assets/idhuca-logo-blue.png";

function PasswordResetSuccess() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = () => {
    setLoading(true);
    setTimeout(() => {
      navigate("/login");
    }, 800);
  };

  return (
    <div
      className="p-d-flex p-jc-center p-ai-center"
      style={{
        backgroundColor: "#003C71",
        minHeight: "100vh", // Ensures the container spans the full viewport height
        display: "flex", // Explicitly set flexbox display
        justifyContent: "center", // Centers horizontally
        alignItems: "center", // Centers vertically
      }}
    >
      <div className="container">
        <div className="row justify-content-center">
          <div className="col-md-6 col-lg-5 col-xl-4">
            <div
              className="card shadow border-0"
              style={{
                borderRadius: "20px",
                padding: "2rem",
                textAlign: "center",
              }}
            >
              <div className="card-body">
                <div
                  style={{
                    backgroundColor: "#41C16F",
                    width: "80px",
                    height: "80px",
                    borderRadius: "50%",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    margin: "0 auto 1.5rem",
                  }}
                >
                  <i
                    className="bi bi-check-lg"
                    style={{ color: "white", fontSize: "40px" }}
                  ></i>
                </div>

                <h1 className="fw-bold mb-2">
                  Contraseña restablecida con exito
                </h1>

                <p className="text-muted">¡Ya puedes acceder a tu cuenta!</p>

                <button
                  className="btn w-100 mt-4 mb-4"
                  style={{ backgroundColor: "#333333", color: "white" }}
                  onClick={handleLogin}
                  disabled={loading}
                >
                  {loading ? "Cargando..." : "iniciar sesion"}
                </button>

                <div className="d-flex justify-content-center mt-4">
                  <img
                    src={logoUCA}
                    alt="Instituto de Derechos Humanos de la UCA"
                    style={{ height: "50px", width: "auto" }} // Slightly larger logo
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default PasswordResetSuccess;
