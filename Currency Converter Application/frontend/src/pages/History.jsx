import React, { useState, useEffect } from 'react';
import { Trash2 } from 'lucide-react';
import { historyService } from '../api/services';
import Spinner from '../components/Spinner';
import { useToast } from '../components/Toast';

const History = () => {
  const { showToast } = useToast();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchHistory = () => {
    setLoading(true);
    historyService.getHistory()
      .then(data => {
        setHistory(data);
        setLoading(false);
      })
      .catch(err => {
        showToast(err.message, 'error');
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this conversion record?")) return;
    
    try {
      await historyService.deleteHistoryItem(id);
      showToast('Record deleted successfully', 'success');
      setHistory(history.filter(item => item.id !== id));
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  return (
    <div className="container animate-fade-in">
      <div className="text-center mb-8">
        <h2>Conversion History</h2>
        <p className="text-muted">Track your recent currency conversions.</p>
      </div>

      <div className="glass-panel" style={{ padding: 'var(--spacing-6)' }}>
        {loading ? (
          <Spinner text="Loading history..." />
        ) : history.length === 0 ? (
          <div className="text-center text-muted" style={{ padding: 'var(--spacing-8)' }}>
            No conversion history found. 
            <br/><br/>
            Go to the Converter to make your first conversion!
          </div>
        ) : (
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th>Date & Time</th>
                  <th>Source</th>
                  <th>Target</th>
                  <th>Amount</th>
                  <th>Result</th>
                  <th>Rate</th>
                  <th style={{ width: '50px' }}></th>
                </tr>
              </thead>
              <tbody>
                {history.map(item => (
                  <tr key={item.id}>
                    <td className="text-muted">{new Date(item.timestamp).toLocaleString()}</td>
                    <td style={{ fontWeight: 600 }}>{item.from}</td>
                    <td style={{ fontWeight: 600 }}>{item.to}</td>
                    <td>{item.amount.toFixed(2)}</td>
                    <td style={{ color: 'var(--color-primary)', fontWeight: 'bold' }}>{item.result.toFixed(4)}</td>
                    <td className="text-muted">{item.rate.toFixed(4)}</td>
                    <td>
                      <button 
                        className="btn-ghost" 
                        onClick={() => handleDelete(item.id)}
                        title="Delete record"
                      >
                        <Trash2 size={16} className="text-error" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default History;
