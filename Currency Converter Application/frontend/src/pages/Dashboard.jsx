import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ArrowRightLeft, History, Star, TrendingUp } from 'lucide-react';
import { currencyService } from '../api/services';

const Dashboard = () => {
  const [currencyCount, setCurrencyCount] = useState(0);

  useEffect(() => {
    currencyService.getCurrencies()
      .then(data => setCurrencyCount(data.length))
      .catch(err => console.error("Could not load currencies", err));
  }, []);

  return (
    <div className="container animate-fade-in">
      <div className="text-center mb-8">
        <h1 className="mb-2">Welcome to <span className="text-primary">NovaConvert</span></h1>
        <p className="text-muted" style={{ fontSize: '1.125rem' }}>
          Fast, reliable, and beautiful currency conversion.
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: 'var(--spacing-6)' }}>
        
        <div className="glass-panel" style={{ padding: 'var(--spacing-6)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-4)', marginBottom: 'var(--spacing-4)' }}>
            <div className="btn-icon" style={{ backgroundColor: 'rgba(59, 130, 246, 0.1)', color: 'var(--color-primary)' }}>
              <ArrowRightLeft size={24} />
            </div>
            <h3 style={{ margin: 0 }}>Convert</h3>
          </div>
          <p className="text-muted mb-4">Instantly convert between {currencyCount > 0 ? currencyCount : 'multiple'} currencies with real-time rates.</p>
          <Link to="/convert" className="btn btn-primary" style={{ width: '100%' }}>Go to Converter</Link>
        </div>

        <div className="glass-panel" style={{ padding: 'var(--spacing-6)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-4)', marginBottom: 'var(--spacing-4)' }}>
            <div className="btn-icon" style={{ backgroundColor: 'rgba(16, 185, 129, 0.1)', color: 'var(--color-accent)' }}>
              <History size={24} />
            </div>
            <h3 style={{ margin: 0 }}>History</h3>
          </div>
          <p className="text-muted mb-4">View your past conversions and track your financial activities easily.</p>
          <Link to="/history" className="btn btn-secondary" style={{ width: '100%' }}>View History</Link>
        </div>

        <div className="glass-panel" style={{ padding: 'var(--spacing-6)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-4)', marginBottom: 'var(--spacing-4)' }}>
            <div className="btn-icon" style={{ backgroundColor: 'rgba(245, 158, 11, 0.1)', color: '#F59E0B' }}>
              <Star size={24} />
            </div>
            <h3 style={{ margin: 0 }}>Favorites</h3>
          </div>
          <p className="text-muted mb-4">Save your frequently used currencies for quick access.</p>
          <Link to="/favorites" className="btn btn-secondary" style={{ width: '100%' }}>Manage Favorites</Link>
        </div>

        <div className="glass-panel" style={{ padding: 'var(--spacing-6)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-4)', marginBottom: 'var(--spacing-4)' }}>
            <div className="btn-icon" style={{ backgroundColor: 'rgba(139, 92, 246, 0.1)', color: '#8B5CF6' }}>
              <TrendingUp size={24} />
            </div>
            <h3 style={{ margin: 0 }}>Currencies</h3>
          </div>
          <p className="text-muted mb-4">Explore our comprehensive list of supported global currencies.</p>
          <Link to="/currencies" className="btn btn-secondary" style={{ width: '100%' }}>View Currencies</Link>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;
