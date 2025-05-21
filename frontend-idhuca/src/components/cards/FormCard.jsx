import { useState } from 'react';

// Reusable Card component for settings
const Card = ({ icon, title, subtitle, expanded, onToggleExpand, children }) => {
  return (
    <div className="card mb-3 shadow-sm">
      <div 
        className="card-header bg-light d-flex justify-content-between align-items-center py-3 cursor-pointer"
        onClick={onToggleExpand}
        style={{ cursor: 'pointer' }}
      >
        <div className="d-flex align-items-center">
          <i className={`bi ${icon} me-3 fs-5`}></i>
          <div>
            <h5 className="mb-0">{title}</h5>
            <small className="text-muted">{subtitle}</small>
          </div>
        </div>
        <i className={`bi ${expanded ? 'bi-chevron-down' : 'bi-chevron-right'}`}></i>
      </div>
      
      {expanded && (
        <div className="card-body">
          {children}
        </div>
      )}
    </div>
  );
};

export default Card;