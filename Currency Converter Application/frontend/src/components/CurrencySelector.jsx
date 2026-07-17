import React from 'react';

const CurrencySelector = ({ label, value, onChange, options, disabled, id }) => {
  return (
    <div className="flex flex-col mb-4">
      <label htmlFor={id} className="mb-1 text-sm font-medium text-gray-700">
        {label}
      </label>
      <select
        id={id}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled}
        className="w-full px-4 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100 disabled:text-gray-500 transition-colors"
      >
        <option value="" disabled>Select currency</option>
        {options.map((opt) => (
          <option key={opt.code} value={opt.code}>
            {opt.code} - {opt.name}
          </option>
        ))}
      </select>
    </div>
  );
};

export default CurrencySelector;
