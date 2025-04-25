import Navbar from '../Navbar';
import Footer from '../Footer';
import { Outlet } from 'react-router-dom';

const MainLayout = () => {
  return (
    <div className="layout d-flex flex-column min-vh-100">
      <Navbar />
      <main className="flex-grow-1 container mt-4">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};

export default MainLayout;
