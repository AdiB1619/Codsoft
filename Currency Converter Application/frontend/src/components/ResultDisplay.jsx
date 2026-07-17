import React from 'react';

const ResultDisplay = ({ from, to, amount, result, rate }) => {
  if (result === null) return null;

  return (
    <div className="mt-6 p-6 bg-blue-50 rounded-lg border border-blue-100 text-center shadow-sm">
      <div className="text-gray-500 text-sm mb-2">
        {amount} {from} =
      </div>
      <div className="text-3xl font-bold text-gray-900 mb-2">
        {result.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 })} {to}
      </div>
      <div className="text-gray-500 text-xs">
        1 {from} = {rate} {to}
      </div>
    </div>
  );
};

export default ResultDisplay;
