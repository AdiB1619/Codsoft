import React, { useState, useEffect } from 'react';
import { currencyService } from '../api/services';
import Spinner from '../components/Spinner';
import { useToast } from '../components/Toast';

const Currencies = () => {
  const { showToast } = useToast();
  const [currencies, setCurrencies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    currencyService.getCurrencies()
      .then(data => {
        setCurrencies(data);
        setLoading(false);
      })
      .catch(err => {
        showToast(err.message, 'error');
        setLoading(false);
      });
  }, [showToast]);

  const filteredCurrencies = currencies.filter(c => 
    c.code.toLowerCase().includes(searchTerm.toLowerCase()) || 
    c.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="container animate-fade-in">
      <div className="text-center mb-8">
        <h2>Supported Currencies</h2>
        <p className="text-muted">A complete list of our {currencies.length} supported global currencies.</p>
      </div>

      <div className="glass-panel" style={{ padding: 'var(--spacing-6)' }}>
        <div className="form-group mb-4" style={{ maxWidth: '400px' }}>
          <input 
            type="text" 
            className="form-control" 
            placeholder="Search by code or name..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        {loading ? (
          <Spinner text="Loading currencies..." />
        ) : (
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Name</th>
                  <th>Symbol</th>
                </tr>
              </thead>
              <tbody>
                {filteredCurrencies.length > 0 ? (
                  filteredCurrencies.map(currency => (
                    <tr key={currency.code}>
                      <td style={{ fontWeight: 'bold', color: 'var(--color-primary)' }}>{currency.code}</td>
                      <td>{currency.name}</td>
                      <td>{currency.symbol || '-'}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="3" className="text-center text-muted" style={{ padding: 'var(--spacing-6)' }}>
                      No currencies found matching "{searchTerm}"
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default Currencies;
