import React, { useState, useEffect } from 'react';
import { currencyService, conversionService } from '../api/services';
import { useToast } from '../components/Toast';
import CurrencySelector from '../components/CurrencySelector';
import ResultDisplay from '../components/ResultDisplay';
import LoadingSpinner from '../components/LoadingSpinner';
import { ArrowLeftRight, RefreshCw } from 'lucide-react';

const Converter = () => {
  const [currencies, setCurrencies] = useState([]);
  const [loadingInitial, setLoadingInitial] = useState(true);
  
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [amount, setAmount] = useState('');
  
  const [isConverting, setIsConverting] = useState(false);
  const [result, setResult] = useState(null);
  
  const addToast = useToast();

  useEffect(() => {
    const fetchCurrencies = async () => {
      try {
        const data = await currencyService.getSupportedCurrencies();
        setCurrencies(data);
        if (data.length >= 2) {
          setFrom(data[0].code);
          setTo(data[1].code);
        }
      } catch (error) {
        addToast(error.message || 'Failed to load currencies', 'error');
      } finally {
        setLoadingInitial(false);
      }
    };
    
    fetchCurrencies();
  }, [addToast]);

  const handleSwap = () => {
    setFrom(to);
    setTo(from);
    setResult(null); // Clear previous result on swap
  };

  const handleClear = () => {
    setAmount('');
    setResult(null);
  };

  const handleConvert = async (e) => {
    e.preventDefault();
    if (!amount || isNaN(amount) || amount <= 0) {
      addToast('Please enter a valid amount greater than 0', 'error');
      return;
    }
    if (from === to) {
      addToast('Please select different currencies to convert', 'error');
      return;
    }

    setIsConverting(true);
    try {
      const response = await conversionService.convertCurrency({
        from,
        to,
        amount: parseFloat(amount)
      });
      setResult(response);
      addToast('Conversion successful', 'success', 2000);
    } catch (error) {
      addToast(error.message || 'Failed to convert currency', 'error');
    } finally {
      setIsConverting(false);
    }
  };

  const isFormValid = from && to && amount && !isNaN(amount) && amount > 0 && from !== to;

  if (loadingInitial) {
    return (
      <div className="flex justify-center items-center h-full min-h-[60vh]">
        <LoadingSpinner size="lg" text="Loading currencies..." />
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden">
        <div className="px-6 py-4 bg-blue-600 border-b border-blue-700">
          <h2 className="text-xl font-bold text-white flex items-center">
            <RefreshCw className="mr-2" size={20} />
            Currency Converter
          </h2>
        </div>
        
        <div className="p-6">
          <form onSubmit={handleConvert}>
            <div className="mb-6">
              <label htmlFor="amount" className="block text-sm font-medium text-gray-700 mb-1">
                Amount
              </label>
              <div className="relative rounded-md shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <span className="text-gray-500 sm:text-sm">$</span>
                </div>
                <input
                  type="number"
                  name="amount"
                  id="amount"
                  className="focus:ring-blue-500 focus:border-blue-500 block w-full pl-7 pr-12 sm:text-sm border-gray-300 rounded-md py-3 bg-gray-50 outline-none border transition-colors"
                  placeholder="0.00"
                  step="0.01"
                  min="0.01"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  disabled={isConverting}
                />
              </div>
            </div>

            <div className="flex flex-col md:flex-row items-center justify-between space-y-4 md:space-y-0 md:space-x-4 mb-6">
              <div className="w-full md:w-5/12">
                <CurrencySelector 
                  id="from-currency"
                  label="From"
                  options={currencies}
                  value={from}
                  onChange={setFrom}
                  disabled={isConverting}
                />
              </div>

              <div className="flex items-center justify-center w-full md:w-2/12 pt-6">
                <button
                  type="button"
                  onClick={handleSwap}
                  disabled={isConverting}
                  className="p-3 bg-gray-100 rounded-full text-blue-600 hover:bg-blue-100 hover:text-blue-700 focus:outline-none transition-colors disabled:opacity-50"
                  title="Swap currencies"
                >
                  <ArrowLeftRight size={20} />
                </button>
              </div>

              <div className="w-full md:w-5/12">
                <CurrencySelector 
                  id="to-currency"
                  label="To"
                  options={currencies}
                  value={to}
                  onChange={setTo}
                  disabled={isConverting}
                />
              </div>
            </div>

            <div className="flex space-x-4 mt-8">
              <button
                type="button"
                onClick={handleClear}
                disabled={isConverting || (!amount && !result)}
                className="flex-1 py-3 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none disabled:opacity-50 transition-colors"
              >
                Clear
              </button>
              <button
                type="submit"
                disabled={isConverting || !isFormValid}
                className="flex-1 flex justify-center items-center py-3 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none disabled:bg-blue-400 transition-colors"
              >
                {isConverting ? (
                  <>
                    <LoadingSpinner size="sm" text="" />
                    <span className="ml-2">Converting...</span>
                  </>
                ) : (
                  'Convert'
                )}
              </button>
            </div>
          </form>

          {result && !isConverting && (
            <ResultDisplay 
              from={result.from} 
              to={result.to} 
              amount={result.amount} 
              result={result.result} 
              rate={result.rate} 
            />
          )}
        </div>
      </div>
    </div>
  );
};

export default Converter;
