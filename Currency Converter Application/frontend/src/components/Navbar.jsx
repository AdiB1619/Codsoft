import React from 'react';
import { NavLink } from 'react-router-dom';
import { Activity, History, Star, Settings2 } from 'lucide-react';

const Navbar = () => {
  return (
    <nav className="glass-panel" style={{ margin: 'var(--spacing-4)', padding: 'var(--spacing-4)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-2)' }}>
        <div style={{ backgroundColor: 'var(--color-primary)', padding: '0.5rem', borderRadius: 'var(--radius-md)', color: 'white' }}>
          <Activity size={24} />
        </div>
        <h2 style={{ margin: 0, fontWeight: 700, fontSize: '1.25rem' }}>NovaConvert</h2>
      </div>
      
      <div style={{ display: 'flex', gap: 'var(--spacing-4)' }}>
        <NavLink to="/" className={({isActive}) => isActive ? "text-primary" : "text-muted"} style={{ fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
          Dashboard
        </NavLink>
        <NavLink to="/convert" className={({isActive}) => isActive ? "text-primary" : "text-muted"} style={{ fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
          Converter
        </NavLink>
        <NavLink to="/currencies" className={({isActive}) => isActive ? "text-primary" : "text-muted"} style={{ fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
          <Settings2 size={18} /> Currencies
        </NavLink>
        <NavLink to="/history" className={({isActive}) => isActive ? "text-primary" : "text-muted"} style={{ fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
          <History size={18} /> History
        </NavLink>
        <NavLink to="/favorites" className={({isActive}) => isActive ? "text-primary" : "text-muted"} style={{ fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
          <Star size={18} /> Favorites
        </NavLink>
      </div>
    </nav>
  );
};

export default Navbar;
