import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/layouts/MainLayout';
import LoginForm from './pages/Login';
import Index from './pages/index/Index';
import MaintenancePage from './pages/MaintenancePage';
import RightSelection from './pages/RightSelection';
import VerifyIdentity from './pages/VerifyIdentity';
import ResetPassword from './pages/password/ResetPassword';
import SetNewPassword from './pages/password/SetNewPassword';
import ResetPasswordSuccess from './pages/password/ResetPasswordSuccess.jsx';
import AgregarRegistro from './pages/registros/AgregarRegistro.jsx';
import DetalleRegistro from './pages/registros/DetalleRegistro';
import Form from './pages/Form';
import ProtectedRoute from './routes/ProtectedRoute';
import { AuthProvider } from './components/AuthContext';
import Parameter from "./pages/Parameters";
import Users from "./pages/users/Users";
import AuditoriaPage from './pages/auditoria/AuditoriaPage';
import FichaPage from './pages/FichaPage';
import Registros from './pages/registros/Registros';
import Graficos from './pages/registros/Graficos';
import Filtros from './pages/registros/FiltradoRegistros';
import RegistrosUpdate from './pages/registros/EditarRegistro.jsx';
import EliminarRegistro from './pages/registros/EliminarRegistro.jsx';
import SelectEjeX from './pages/registros/SelectEjeX.jsx';
import { UserProvider } from './Contexts/UserContext';

// Estilos de PrimeReact
import "primereact/resources/themes/lara-light-blue/theme.css";
import "primereact/resources/primereact.min.css";
import "primeicons/primeicons.css";
import "primeflex/primeflex.css";

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginForm />} />

          {/* MainLayout wrapper */}
          <Route element={<MainLayout />}>
            {/* Protected routes */}
            <Route path="/index" element={
              <ProtectedRoute>
                <Index />
              </ProtectedRoute>
            } />
            <Route path="/users" element={
              <ProtectedRoute>
                <Users />
              </ProtectedRoute>
            } />
            <Route path="/seleccion-derecho" element={
              <ProtectedRoute>
                <RightSelection />
              </ProtectedRoute>
            } />
            <Route path="/ficha-de-derechos" element={
              <ProtectedRoute>
                <FichaPage />
              </ProtectedRoute>
            } />
            <Route path="/auditoria" element={
              <ProtectedRoute>
                <AuditoriaPage />
              </ProtectedRoute>
            } />
            <Route path="/mantenimiento" element={
              <ProtectedRoute>
                <MaintenancePage />
              </ProtectedRoute>
            } />
            <Route path="/registros" element={
              <ProtectedRoute>
                <RightSelection />
              </ProtectedRoute>
            } />
            <Route path="/formulario-derecho" element={
              <ProtectedRoute>
                <Form />
              </ProtectedRoute>
            } />
            <Route path="/parametros" element={
              <ProtectedRoute>
                <Parameter />
              </ProtectedRoute>
            } />
            <Route path="/select-register" element={
              <ProtectedRoute>
                <Registros />
              </ProtectedRoute>
            } />
            <Route
              path="/registros/detalle/:id"
              element={
                <ProtectedRoute>
                  <DetalleRegistro />
                </ProtectedRoute>
              }
            />
            <Route
              path="/registros/add"
              element={
                <ProtectedRoute>
                  <AgregarRegistro />
                </ProtectedRoute>
              }
            />
            <Route
              path="/registros/update/:id"
              element={
                <ProtectedRoute>
                  <RegistrosUpdate />
                </ProtectedRoute>
              }
            />
            <Route
              path="/registros/delete/:id"
              element={
                <ProtectedRoute>
                  <EliminarRegistro />
                </ProtectedRoute>
              }
            />
            
            <Route path="/graphs" element={
              <ProtectedRoute>
                <Graficos />
              </ProtectedRoute>
            } />
            <Route path="/selectEjeX" element={
              <ProtectedRoute>
                <SelectEjeX />
              </ProtectedRoute>
            } />
            <Route path="/filter" element={
              <ProtectedRoute>
                <Filtros />
              </ProtectedRoute>
            } />
          </Route>

          {/* Public routes */}
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/verify-identity" element={<VerifyIdentity />} />
          <Route path="/set-new-password" element={<SetNewPassword />} />
          <Route path="/reset-password-success" element={<ResetPasswordSuccess />} />


          {/* Catch-all route */}
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
