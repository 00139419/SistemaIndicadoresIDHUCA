import { useState } from "react";
import { InputText } from "primereact/inputtext";
import { Password } from "primereact/password";
import { Button } from "primereact/button";
import logoUCA from "../assets/idhuca-logo-blue.png";

export default function LoginForm() {
  const [credentials, setCredentials] = useState({
    username: "",
    password: "",
  });
  

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials({
      ...credentials,
      [name]: value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Credenciales enviadas:", credentials);
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
    <div
      className="p-card p-shadow-3"
      style={{
        maxWidth: "500px", // Increased width for the white card
        width: "100%",
        padding: "4rem", // Increased padding for more spacing inside the card
        borderRadius: "12px", // Slightly more rounded corners
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)", // Optional: Add a subtle shadow
      }}
    >
      <div className="p-text-center p-mb-4">
        <h1
          className="p-text-bold"
          style={{
            fontSize: "2rem", // Increased font size for the title
            marginBottom: "1rem",
            textAlign: "center",
          }}
        >
          Iniciar sesión
        </h1>
        <p
          className="p-text-secondary"
          style={{
            fontSize: "1.125rem", // Slightly larger font size for the subtitle
            marginBottom: "2rem",
            textAlign: "center",
          }}
        >
          Inicie sesión para continuar
        </p>
      </div>
  
      <form onSubmit={handleSubmit}>
        <div className="p-field p-mb-3">
          <label htmlFor="username" className="p-d-block" style={{ fontSize: "1rem" }}>
            Usuario
          </label>
          <InputText
            id="username"
            name="username"
            value={credentials.username}
            onChange={handleChange}
            placeholder="testsidhuea@uca.edu.sv"
            className="p-inputtext-lg p-d-block" // Larger input size
            style={{ width: "100%", marginBottom: "1.5rem" }}
          />
        </div>
  
        <div className="p-field p-mb-3">
          <label htmlFor="password" className="p-d-block" style={{ fontSize: "1rem" }}>
            Contraseña
          </label>
          <Password
            id="password"
            name="password"
            value={credentials.password}
            onChange={handleChange}
            toggleMask
            feedback={false}
            className="p-inputtext-lg p-d-block" // Larger input size
            style={{ width: "100%", marginBottom: "1.5rem" }}
          />
        </div>
  
        <div
          className="p-d-flex p-jc-center p-ai-center"
          style={{ height: "100%", textAlign: "center" }}
        >
          <a
            href="#"
            className="p-text-secondary"
            style={{ fontSize: "1rem", marginBottom: "1.5rem" }} // Slightly larger font size
          >
            ¿Olvidaste tu contraseña?
          </a>
        </div>
  
        <div
          className="p-d-flex p-jc-center p-ai-center"
          style={{ height: "100%", textAlign: "center" }}
        >
          <Button
            type="submit"
            label="Iniciar sesión"
            className="p-button-lg p-button-primary" // Larger button size
            style={{
              fontSize: "1.125rem", // Slightly larger font size for the button
              padding: "1rem",
              marginTop: "1rem",
              width: "100%",
            }}
          />
        </div>
      </form>
  
      <div
        className="p-d-flex p-jc-center p-ai-center"
        style={{ marginTop: "2rem", textAlign: "center" }} // Increased margin for spacing
      >
        <img
          src={logoUCA}
          alt="Instituto de Derechos Humanos de la UCA"
          style={{ height: "50px", width: "auto" }} // Slightly larger logo
        />
      </div>
    </div>
  </div>
  );
}
