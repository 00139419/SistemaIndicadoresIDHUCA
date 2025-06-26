import '../custom.css';
const Footer = () => {
  const currentYear = new Date().getFullYear();
  
  return (
    <footer className="footer mt-auto py-3 bg-dark text-white">
      <div className="container text-center">
        <p className="mb-0">
          UCA {currentYear} © Derechos reservados | Instituto de Derechos Humanos de la UCA
        </p>
      </div>
    </footer>
  );
};

export default Footer;