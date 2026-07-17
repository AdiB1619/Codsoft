import React from 'react';
import { Loader2 } from 'lucide-react';

const Spinner = ({ size = 24, text = 'Loading...' }) => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 'var(--spacing-2)', padding: 'var(--spacing-4)' }}>
      <Loader2 size={size} className="animate-spin text-primary" />
      {text && <span className="text-muted" style={{ fontSize: '0.875rem' }}>{text}</span>}
    </div>
  );
};

export default Spinner;
