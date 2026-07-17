import React, { useState, useEffect } from 'react';
import { ArrowRightLeft, ArrowDownUp } from 'lucide-react';
import CustomSelect from '../components/CustomSelect';
import Spinner from '../components/Spinner';
import { useToast } from '../components/Toast';
import { currencyService, conversionService } from '../api/services';

const Converter = () => {
  const { showToast } = useToast();
  const [currencies, setCurrencies] = useState([]);
  const [loadingCurrencies, setLoadingCurrencies] = useState(true);
  
  const [from, setFrom] = useState('USD');
  const [to, setTo] = useState('EUR');
  const [amount, setAmount] = useState('1.00');
  
  const [converting, setConverting] = useState(false);
  const [result, setResult] = useState(null);

  useEffect(() => {
    currencyService.getCurrencies()
      .then(data => {
        setCurrencies(data);
        setLoadingCurrencies(false);
      })
      .catch(err => {
        showToast(err.message, 'error');
        setLoadingCurrencies(false);
      });
  }, [showToast]);

  const handleSwap = () => {
    setFrom(to);
    setTo(from);
    setResult(null);
  };

  const handleConvert = async (e) => {
    e.preventDefault();
    if (!amount || isNaN(amount) || parseFloat(amount) <= 0) {
      showToast('Please enter a valid amount greater than 0', 'error');
      return;
    }
    if (from === to) {
      showToast('Source and target currencies cannot be the same', 'error');
      return;
    }

    setConverting(true);
    setResult(null);
    try {
      const response = await conversionService.convert(from, to, parseFloat(amount));
      setResult(response);
      showToast('Conversion successful!', 'success');
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      setConverting(false);
    }
  };

  return (
    <div className="container animate-fade-in" style={{ maxWidth: '600px' }}>
      <div className="text-center mb-8">
        <h2>Currency Converter</h2>
        <p className="text-muted">Real-time exchange rates at your fingertips.</p>
      </div>

      <div className="glass-panel" style={{ padding: 'var(--spacing-6)' }}>
        {loadingCurrencies ? (
          <Spinner text="Loading supported currencies..." />
        ) : (
          <form onSubmit={handleConvert}>
            <div className="form-group">
              <label className="form-label">Amount</label>
              <input 
                type="number" 
                className="form-control" 
                value={amount} 
                onChange={(e) => setAmount(e.target.value)} 
                min="0.01" 
                step="0.01" 
                required 
                style={{ fontSize: '1.25rem', fontWeight: 'bold' }}
              />
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-4)', marginTop: 'var(--spacing-4)' }}>
              <div style={{ flex: 1 }}>
                <CustomSelect 
                  label="From" 
                  value={from} 
                  onChange={(e) => setFrom(e.target.value)} 
                  options={currencies} 
                />
              </div>
              
              <button 
                type="button" 
                onClick={handleSwap} 
                className="btn-icon" 
                style={{ 
                  marginTop: 'var(--spacing-4)', 
                  backgroundColor: 'var(--color-surface)',
                  border: '1px solid var(--glass-border)',
                  boxShadow: 'var(--glass-shadow)'
                }}
                title="Swap currencies"
              >
                <ArrowDownUp size={20} className="text-primary" />
              </button>
              
              <div style={{ flex: 1 }}>
                <CustomSelect 
                  label="To" 
                  value={to} 
                  onChange={(e) => setTo(e.target.value)} 
                  options={currencies} 
                />
              </div>
            </div>

            <button 
              type="submit" 
              className="btn btn-primary mt-4" 
              style={{ width: '100%', padding: '1rem', fontSize: '1.125rem' }}
              disabled={converting}
            >
              {converting ? (
                <> <Spinner size={18} text="" /> Converting... </>
              ) : (
                <> <ArrowRightLeft size={20} /> Convert </>
              )}
            </button>
          </form>
        )}
      </div>

      {result && (
        <div className="glass-panel animate-fade-in mt-8" style={{ padding: 'var(--spacing-6)', textAlign: 'center' }}>
          <h4 className="text-muted mb-2">Conversion Result</h4>
          <div style={{ fontSize: '2.5rem', fontWeight: 700, color: 'var(--color-primary)', marginBottom: 'var(--spacing-2)' }}>
            {result.result.toFixed(4)} <span style={{ fontSize: '1.5rem', color: 'var(--color-text)' }}>{result.to}</span>
          </div>
          <p className="text-muted" style={{ fontSize: '1.125rem' }}>
            {result.amount} {result.from} = {result.result.toFixed(4)} {result.to}
          </p>
          <div style={{ marginTop: 'var(--spacing-4)', padding: 'var(--spacing-3)', backgroundColor: 'var(--color-surface-hover)', borderRadius: 'var(--radius-md)' }}>
            <p style={{ margin: 0, fontSize: '0.875rem' }}>
              <strong>Exchange Rate:</strong> 1 {result.from} = {result.rate} {result.to}
            </p>
            <p style={{ margin: 0, fontSize: '0.875rem', color: 'var(--color-text-muted)', marginTop: '0.25rem' }}>
              As of {new Date(result.timestamp).toLocaleString()}
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

export default Converter;
