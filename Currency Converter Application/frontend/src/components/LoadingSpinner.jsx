import React from 'react';

const LoadingSpinner = ({ size = 'md', text = 'Loading...' }) => {
  const sizeClasses = {
    sm: 'w-4 h-4',
    md: 'w-8 h-8',
    lg: 'w-12 h-12'
  };

  return (
    <div className="flex flex-col items-center justify-center space-y-2 p-4">
      <div 
        className={`${sizeClasses[size]} border-4 border-gray-200 border-t-blue-600 rounded-full animate-spin`}
        role="status"
        aria-label={text}
      >
        <span className="sr-only">{text}</span>
      </div>
      {text && <span className="text-gray-500 text-sm font-medium">{text}</span>}
    </div>
  );
};

export default LoadingSpinner;
