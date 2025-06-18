import React, { useState } from "react";
import { Calendar } from "primereact/calendar";
import { Dropdown } from "primereact/dropdown";
import { InputTextarea } from "primereact/inputtextarea";
import { Button } from "primereact/button";
import { Card } from "primereact/card";

import "primereact/resources/themes/lara-light-indigo/theme.css"; 
import "primereact/resources/primereact.min.css";
import "primeicons/primeicons.css";
import "primeflex/primeflex.css";

const AgregarRegistro = () => {
  const [fechaHecho, setFechaHecho] = useState(null);
  const [fuente, setFuente] = useState(null);
  const [estadoActual, setEstadoActual] = useState(null);
  const [derechoAsociado, setDerechoAsociado] = useState(null);
  const [observaciones, setObservaciones] = useState("");
  const [departamento, setDepartamento] = useState(null);
  const [municipio, setMunicipio] = useState(null);
  const [lugarExacto, setLugarExacto] = useState(null);

  // Catálogos simulados
  const fuentes = [{ codigo: "FUENTE_8", nombre: "Fuente 8" }];
  const estados = [{ codigo: "ESTREG_3", nombre: "Estado 3" }];
  const departamentos = [{ codigo: "DEP_1", nombre: "San Salvador" }];
  const municipios = [{ codigo: "MUN_1_7", nombre: "Mejicanos" }];
  const lugaresExactos = [{ codigo: "LUGEXAC_1", nombre: "Parque Central" }];

  const handleGuardar = () => {
    const json = {
      fechaHecho: fechaHecho?.toISOString().split("T")[0],
      fuente: { codigo: fuente?.codigo },
      estadoActual: { codigo: estadoActual?.codigo },
      derechoAsociado: { codigo: derechoAsociado?.codigo },
      observaciones,
      ubicacion: {
        departamento: { codigo: departamento?.codigo },
        municipio: { codigo: municipio?.codigo },
        lugarExacto: { codigo: lugarExacto?.codigo },
      },
    };

    console.log("JSON final (sin personas aún):", json);
  }; 

  return (
    <div className="p-4 surface-100 min-h-screen">
      <Card title="📝 Registro del Hecho" className="shadow-4 border-round-lg">
        <div className="formgrid grid p-fluid gap-4">
          {/* Fecha del hecho */}
          <div className="col-12 md:col-4">
            <label className="font-semibold mb-2 block">
              Fecha del hecho
            </label>
            <Calendar
              value={fechaHecho}
              onChange={(e) => setFechaHecho(e.value)}
              dateFormat="yy-mm-dd"
              showIcon
              className="w-full"
            />
          </div>

          {/* Fuente */}
          <div className="col-12 md:col-4">
            <label className="font-semibold mb-2 block">Fuente</label>
            <Dropdown
              value={fuente}
              onChange={(e) => setFuente(e.value)}
              options={fuentes}
              optionLabel="nombre"
              placeholder="Seleccione una fuente"
              className="w-full"
            />
          </div>

          {/* Estado Actual */}
          <div className="col-12 md:col-4">
            <label className="font-semibold mb-2 block">Estado Actual</label>
            <Dropdown
              value={estadoActual}
              onChange={(e) => setEstadoActual(e.value)}
              options={estados}
              optionLabel="nombre"
              placeholder="Seleccione un estado"
              className="w-full"
            />
          </div>

          {/* Departamento */}
          <div className="col-12 md:col-4">
            <label className="font-semibold mb-2 block">Departamento</label>
            <Dropdown
              value={departamento}
              onChange={(e) => setDepartamento(e.value)}
              options={departamentos}
              optionLabel="nombre"
              placeholder="Seleccione un departamento"
              className="w-full"
            />
          </div>

          {/* Municipio */}
          <div className="col-12 md:col-4">
            <label className="font-semibold mb-2 block">Municipio</label>
            <Dropdown
              value={municipio}
              onChange={(e) => setMunicipio(e.value)}
              options={municipios}
              optionLabel="nombre"
              placeholder="Seleccione un municipio"
              className="w-full"
            />
          </div>

          {/* Lugar exacto */}
          <div className="col-12 md:col-4">
            <label className="font-semibold mb-2 block">Lugar Exacto</label>
            <Dropdown
              value={lugarExacto}
              onChange={(e) => setLugarExacto(e.value)}
              options={lugaresExactos}
              optionLabel="nombre"
              placeholder="Seleccione un lugar"
              className="w-full"
            />
          </div>

          {/* Observaciones */}
          <div className="col-12">
            <label className="font-semibold mb-2 block">Observaciones</label>
            <InputTextarea
              value={observaciones}
              onChange={(e) => setObservaciones(e.target.value)}
              rows={4}
              className="w-full"
              autoResize
            />
          </div>
        </div>

        {/* Botón */}
        <div className="flex justify-content-center mt-4">
          <Button
            label="Guardar datos generales"
            icon="pi pi-save"
            className="p-button-primary px-6 py-2 font-bold"
            onClick={handleGuardar}
          />
        </div>
      </Card>
    </div>
  );
};

export default AgregarRegistro;
