import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ToastProvider } from './components/Toast';
import Navbar from './components/Navbar';
import Footer from './components/Footer';

// Pages
import Dashboard from './pages/Dashboard';
import Converter from './pages/Converter';
import Currencies from './pages/Currencies';
import History from './pages/History';
import Favorites from './pages/Favorites';

function App() {
  return (
    <ToastProvider>
      <BrowserRouter>
        <div className="app-layout">
          <Navbar />
          <main className="main-content">
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/convert" element={<Converter />} />
              <Route path="/currencies" element={<Currencies />} />
              <Route path="/history" element={<History />} />
              <Route path="/favorites" element={<Favorites />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </BrowserRouter>
    </ToastProvider>
  );
}

export default App;
