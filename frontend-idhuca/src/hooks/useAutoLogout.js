import { useEffect } from 'react';

export const useAutoLogout = (logout, timeout = 3 * 60 * 1000, warningTime = 60 * 1000) => {
  useEffect(() => {
    let logoutTimer;
    let warningTimer;

    const resetTimers = () => {
      clearTimeout(logoutTimer);
      clearTimeout(warningTimer);

      warningTimer = setTimeout(() => {
        window.dispatchEvent(new Event('showLogoutWarning'));
      }, timeout - warningTime);

      logoutTimer = setTimeout(() => {
        logout();
      }, timeout);
    };

    const events = ['mousemove', 'keydown', 'click', 'scroll'];
    events.forEach(e => window.addEventListener(e, resetTimers));

    resetTimers(); // inicia los timers

    return () => {
      clearTimeout(logoutTimer);
      clearTimeout(warningTimer);
      events.forEach(e => window.removeEventListener(e, resetTimers));
    };
  }, [logout, timeout, warningTime]);
};
