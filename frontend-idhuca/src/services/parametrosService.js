import { authFetch } from "./../authFetch";

export async function getTiempoVidaSesion() {
  const res = await authFetch("parametros/sistema/getOne", {
    method: "POST",
    body: JSON.stringify({ clave: "tiempo_de_vida_de_sesion" }),
  });

console.log("respuesta: " + JSON.stringify(res.entity))

  const valor = Number(res.entity?.valor);
  return isFinite(valor) && valor > 0 ? valor : 15;
}

