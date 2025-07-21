import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import { deleteEvent } from "../../services/RegstrosService";
import { useLocation } from "react-router-dom";

const EliminarRegistro = () => {
  const location = useLocation();
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [visible, setVisible] = useState(true);

  const handleEliminar = async () => {
    let { filtros, derechoId, categoriaEjeX } = location.state || {};

    setLoading(true);
    try {
      await deleteEvent(id);
      setVisible(false);
      navigate("/select-register", {
        state: { filtros, derechoId, categoriaEjeX },
      });
    } catch (error) {
      alert("Error al eliminar el registro: " + error.message);
      setLoading(false);
    }
  };

  const footer = (
    <div>
      <Button
        label="Cancelar"
        className="p-button-secondary mr-2"
        onClick={() => {
          setVisible(false);
          navigate("/registros");
        }}
        disabled={loading}
      />
      <Button
        label="Eliminar"
        icon="pi pi-trash"
        className="p-button-danger"
        onClick={handleEliminar}
        loading={loading}
      />
    </div>
  );

  return (
    <Dialog
      header="Eliminar Registro"
      visible={visible}
      style={{ width: "30vw" }}
      modal
      closable={false}
      footer={footer}
    >
      <p>
        ¿Seguro que deseas eliminar este registro? Esta acción no se puede
        deshacer.
      </p>
    </Dialog>
  );
};

export default EliminarRegistro;
