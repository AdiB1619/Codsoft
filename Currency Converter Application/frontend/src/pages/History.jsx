import React, { useState, useEffect } from 'react';
import { historyService } from '../api/services';
import { useToast } from '../components/Toast';
import HistoryTable from '../components/HistoryTable';
import LoadingSpinner from '../components/LoadingSpinner';
import { Clock } from 'lucide-react';

const History = () => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const addToast = useToast();

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      setLoading(true);
      const data = await historyService.getHistory();
      setHistory(data);
    } catch (error) {
      addToast(error.message || 'Failed to load conversion history', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this record?')) return;
    
    try {
      await historyService.deleteHistory(id);
      addToast('Record deleted successfully', 'success');
      setHistory(prev => prev.filter(item => item.id !== id));
    } catch (error) {
      addToast(error.message || 'Failed to delete record', 'error');
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-full min-h-[60vh]">
        <LoadingSpinner size="lg" text="Loading history..." />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-4 sm:space-y-0">
        <h1 className="text-2xl font-bold text-gray-900 flex items-center">
          <Clock className="mr-2 text-blue-600" size={24} />
          Conversion History
        </h1>
        <button
          onClick={fetchHistory}
          className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none transition-colors"
        >
          Refresh Data
        </button>
      </div>

      <HistoryTable history={history} onDelete={handleDelete} />
    </div>
  );
};

export default History;
