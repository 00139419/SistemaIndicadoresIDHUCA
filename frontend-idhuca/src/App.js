import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/layouts/MainLayout';
import LoginForm from './pages/Login';
import Index from './pages/Index';
import VerifyIdentity from './pages/VerifyIdentity';
import ResetPassword from './pages/ResetPassword';
import ResetPasswordSuccess from './pages/ResetPasswordSuccess';
import 'primereact/resources/themes/lara-light-blue/theme.css'; // Theme
import 'primereact/resources/primereact.min.css'; // Core CSS
import 'primeicons/primeicons.css'; // Icons
import 'primeflex/primeflex.css'; // Utility classes

function App() {
  return (
    <Router>
      <Routes>
        <Route element={<MainLayout />}>
          <Route path="/index" element={<Index />} />
        </Route>
        <Route path="/login" element={<LoginForm />} />
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/verify-identity" element={<VerifyIdentity />} />
        <Route path="/reset-password" element={<ResetPassword />} />
        <Route path="/reset-password-success" element={<ResetPasswordSuccess />} />
      </Routes>
    </Router>
  );
}

export default App;