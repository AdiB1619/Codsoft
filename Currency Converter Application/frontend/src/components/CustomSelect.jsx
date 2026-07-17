import React from 'react';

const CustomSelect = ({ label, value, onChange, options, disabled, placeholder }) => {
  return (
    <div className="form-group">
      {label && <label className="form-label">{label}</label>}
      <select 
        className="form-control" 
        value={value} 
        onChange={onChange} 
        disabled={disabled}
        style={{ cursor: disabled ? 'not-allowed' : 'pointer' }}
      >
        <option value="" disabled>{placeholder || 'Select an option'}</option>
        {options.map(opt => (
          <option key={opt.code} value={opt.code}>
            {opt.code} - {opt.name}
          </option>
        ))}
      </select>
    </div>
  );
};

export default CustomSelect;
