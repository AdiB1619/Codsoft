import React from 'react';

const Footer = () => {
  return (
    <footer style={{ textAlign: 'center', padding: 'var(--spacing-8)', color: 'var(--color-text-muted)', fontSize: '0.875rem' }}>
      <p>© {new Date().getFullYear()} NovaConvert. All rights reserved.</p>
    </footer>
  );
};

export default Footer;
