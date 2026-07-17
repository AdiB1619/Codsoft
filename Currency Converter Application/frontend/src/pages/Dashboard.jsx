import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { currencyService } from '../api/services';
import { ArrowRight, Activity, DollarSign, Globe, Star } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalCurrencies: 0,
    activeCurrencies: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const currencies = await currencyService.getSupportedCurrencies();
        setStats({
          totalCurrencies: currencies.length,
          activeCurrencies: currencies.length
        });
      } catch (error) {
        console.error("Failed to load dashboard stats", error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchStats();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-full min-h-[60vh]">
        <LoadingSpinner size="lg" text="Loading dashboard..." />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-lg shadow-sm p-6 border border-gray-200">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Welcome to Convertify</h1>
        <p className="text-gray-500">Your personal currency conversion and tracking assistant.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white rounded-lg shadow-sm p-6 border border-gray-200 flex items-center space-x-4">
          <div className="p-3 bg-blue-100 rounded-full text-blue-600">
            <Globe size={24} />
          </div>
          <div>
            <p className="text-sm text-gray-500 font-medium">Supported Currencies</p>
            <p className="text-2xl font-bold text-gray-900">{stats.totalCurrencies}</p>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm p-6 border border-gray-200 flex items-center space-x-4">
          <div className="p-3 bg-green-100 rounded-full text-green-600">
            <Activity size={24} />
          </div>
          <div>
            <p className="text-sm text-gray-500 font-medium">System Status</p>
            <p className="text-2xl font-bold text-gray-900">Online</p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden hover:shadow-md transition-shadow">
          <div className="p-6">
            <div className="flex items-center space-x-2 mb-4">
              <DollarSign className="text-blue-600" size={24} />
              <h2 className="text-xl font-bold text-gray-900">Quick Convert</h2>
            </div>
            <p className="text-gray-500 mb-6">Instantly convert between {stats.totalCurrencies} different global currencies with real-time exchange rates.</p>
            <Link 
              to="/convert" 
              className="inline-flex items-center justify-center w-full sm:w-auto px-6 py-2 border border-transparent text-base font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 transition-colors"
            >
              Start Converting
              <ArrowRight className="ml-2" size={18} />
            </Link>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden hover:shadow-md transition-shadow">
          <div className="p-6">
            <div className="flex items-center space-x-2 mb-4">
              <Star className="text-yellow-500" size={24} />
              <h2 className="text-xl font-bold text-gray-900">Manage Favorites</h2>
            </div>
            <p className="text-gray-500 mb-6">Save your most frequently used currencies for quick access during conversions.</p>
            <Link 
              to="/favorites" 
              className="inline-flex items-center justify-center w-full sm:w-auto px-6 py-2 border border-gray-300 shadow-sm text-base font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 transition-colors"
            >
              View Favorites
              <ArrowRight className="ml-2" size={18} />
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
