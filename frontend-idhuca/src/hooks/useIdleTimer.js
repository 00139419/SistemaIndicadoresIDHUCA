import { useEffect, useRef } from "react";

export const useIdleTimer = (onIdle, idleTimeMs) => {
  const timerRef = useRef(null);

  useEffect(() => {

    // ① Si está deshabilitado, salir
    if (idleTimeMs == null) return;

    // ② Función local para reiniciar (estable)
    const resetTimer = () => {
      if (timerRef.current) clearTimeout(timerRef.current);
      timerRef.current = setTimeout(onIdle, idleTimeMs);
    };

    const events = ["mousemove", "keydown", "scroll", "touchstart", "click"];
    events.forEach(e => window.addEventListener(e, resetTimer));

    resetTimer(); // arranca

    return () => {
      events.forEach(e => window.removeEventListener(e, resetTimer));
      if (timerRef.current) clearTimeout(timerRef.current);
    };
  }, [idleTimeMs, onIdle]);
};
