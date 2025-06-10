import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/layouts/MainLayout';
import LoginForm from './pages/Login';
import Index from './pages/index/Index';
import MaintenancePage from './pages/MaintenancePage';
import RightSelection from './pages/RightSelection';
import VerifyIdentity from './pages/VerifyIdentity';
import ResetPassword from './pages/ResetPassword';
import SetNewPassword from './pages/SetNewPassword';
import ResetPasswordSuccess from './pages/ResetPasswordSuccess';
import Form from './pages/Form';
import ProtectedRoute from './routes/ProtectedRoute';
import { AuthProvider } from './components/AuthContext';
import AuditoriaPage from './pages/AuditoriaPage';
import FichaPage from './pages/FichaPage';
import Registros from './pages/Registros';
// Estilos de PrimeReact
import 'primereact/resources/themes/lara-light-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';

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
						{/* FLUJO */}
						<Route path="/seleccion-derecho" element={
							<ProtectedRoute>
								<RightSelection />
							</ProtectedRoute>
						} />
						/*
						<Route path='/registros' element={
							<ProtectedRoute>
								<Registros />
							</ProtectedRoute>
						} />
						*/
						<Route path="/ficha-de-derechos" element={
							<ProtectedRoute>
								<FichaPage />
							</ProtectedRoute>
						} />
						{/* FLUJO */}
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

						<Route path="/formulario-derecho" index element={<Form />} />
					</Route>

					<Route path="/" element={<Navigate to="/login" />} />
					<Route path="/reset-password" element={<ResetPassword />} />
					<Route path="/verify-identity" element={<VerifyIdentity />} />
					<Route path="/set-new-password" element={<SetNewPassword />} />
					<Route path="/reset-password-success" element={<ResetPasswordSuccess />} />
				</Routes>
			</AuthProvider>
		</Router>
	);
}

export default App;
