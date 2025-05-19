import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/layouts/MainLayout';
import LoginForm from './pages/Login';
import Index from './pages/Index';
import VerifyIdentity from './pages/VerifyIdentity';
import ResetPassword from './pages/ResetPassword';
import ResetPasswordSuccess from './pages/ResetPasswordSuccess';
import Form from './pages/Form';
import ProtectedRoute from './routes/ProtectedRoute';
import { AuthProvider } from './components/AuthContext';
import 'primereact/resources/themes/lara-light-blue/theme.css'; // Theme
import 'primereact/resources/primereact.min.css'; // Core CSS
import 'primeicons/primeicons.css'; // Icons
import 'primeflex/primeflex.css'; // Utility classes

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginForm />} />
          <Route element={<MainLayout />}>

          {/* Rutas protegidas */}
          <Route path="/index" element={
            <ProtectedRoute>
              <Index />
            </ProtectedRoute>
          } />
          <Route path="/formulario-derecho" index element={<Form />} />
          </Route>
      
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/verify-identity" element={<VerifyIdentity />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/reset-password-success" element={<ResetPasswordSuccess />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;