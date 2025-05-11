import { createContext, useContext, useState } from 'react';

// Creamos el contexto
const UserContext = createContext();

// Hook personalizado para usar el contexto fácilmente
export const useUser = () => useContext(UserContext);

// Componente proveedor
export const UserProvider = ({ children }) => {
  // Esto puede venir de un login o de una API
  const [user, setUser] = useState({
    name: 'Juan',
    role: 'admin', // o 'user'
  });

  return (
    <UserContext.Provider value={{ user, setUser }}>
      {children}
    </UserContext.Provider>
  );
};
