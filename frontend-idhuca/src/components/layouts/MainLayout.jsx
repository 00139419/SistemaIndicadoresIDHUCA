import React, { useState, useEffect } from 'react';
import Navbar from '../Navbar';
import Footer from '../Footer';
import { Outlet } from 'react-router-dom';
import { useAutoLogout } from '../../hooks/useAutoLogout';
import { useAuth } from '../AuthContext';

const MainLayout = () => {
  const { logout } = useAuth();
  const [showWarning, setShowWarning] = useState(false);

  useAutoLogout(logout, 3 * 60 * 1000, 1 * 60 * 1000);

  useEffect(() => {
    const handleWarning = () => setShowWarning(true);

    window.addEventListener('showLogoutWarning', handleWarning);
    return () => window.removeEventListener('showLogoutWarning', handleWarning);
  }, []);

  const dismissWarning = () => setShowWarning(false);

  return (
    <div className="layout d-flex flex-column min-vh-100">
      <Navbar />
      <main className="flex-grow-1 container mt-4">
        <Outlet />

        {/* Modal */}
        <div className={`modal fade ${showWarning ? 'show d-block' : ''}`} tabIndex="-1" role="dialog" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-dialog-centered" role="document">
            <div className="modal-content border-warning">
              <div className="modal-header bg-warning text-dark">
                <h5 className="modal-title">Sesión Inactiva</h5>
              </div>
              <div className="modal-body">
                <p>Tu sesión se cerrará pronto por inactividad.</p>
              </div>
              <div className="modal-footer">
                <button className="btn btn-secondary" onClick={dismissWarning}>
                  Seguir conectado
                </button>
              </div>
            </div>
          </div>
        </div>
        {/* Fin Modal */}
      </main>
      <Footer />
    </div>
  );
};

export default MainLayout;
