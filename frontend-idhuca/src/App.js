import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/layouts/MainLayout';
import LoginForm from './pages/Login';
import Index from './pages/index/Index';
import MaintenancePage from './pages/MaintenancePage';
import RightSelection from './pages/RightSelection';
import { UserProvider } from './Contexts/UserContext';

// Estilos de PrimeReact
import 'primereact/resources/themes/lara-light-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';

function App() {
  return (
    <UserProvider> {/* all app inside context */}
      <Router>
        <Routes>
          <Route element={<MainLayout />}>
            <Route path="/index" element={<Index />} />
            <Route path="/mantenimiento" element={<MaintenancePage />} />
            <Route path='/registros' element={<RightSelection />} />
          </Route>
          <Route path="/login" element={<LoginForm />} />
          <Route path="/" element={<Navigate to="/login" />} />
        </Routes>
      </Router>
    </UserProvider>
  );
}

export default App;
