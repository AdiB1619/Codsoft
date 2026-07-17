import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { RefreshCcw, Home, Repeat, List, Clock, Star, Menu, X } from 'lucide-react';

const Navbar = () => {
  const [isOpen, setIsOpen] = useState(false);

  const links = [
    { to: '/', icon: <Home size={18} />, text: 'Dashboard' },
    { to: '/convert', icon: <Repeat size={18} />, text: 'Convert' },
    { to: '/currencies', icon: <List size={18} />, text: 'Currencies' },
    { to: '/history', icon: <Clock size={18} />, text: 'History' },
    { to: '/favorites', icon: <Star size={18} />, text: 'Favorites' }
  ];

  const activeClassName = "bg-blue-700 text-white px-3 py-2 rounded-md text-sm font-medium flex items-center space-x-2";
  const inactiveClassName = "text-blue-100 hover:bg-blue-600 hover:text-white px-3 py-2 rounded-md text-sm font-medium flex items-center space-x-2 transition-colors";
  
  const mobileActiveClassName = "bg-blue-700 text-white block px-3 py-2 rounded-md text-base font-medium flex items-center space-x-2";
  const mobileInactiveClassName = "text-blue-100 hover:bg-blue-600 hover:text-white block px-3 py-2 rounded-md text-base font-medium flex items-center space-x-2";

  return (
    <nav className="bg-blue-600 shadow-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center">
            <div className="flex-shrink-0 flex items-center space-x-2">
              <div className="bg-white p-1 rounded-full text-blue-600">
                <RefreshCcw size={24} />
              </div>
              <span className="font-bold text-white text-xl tracking-tight">Convertify</span>
            </div>
            <div className="hidden md:block ml-10">
              <div className="flex items-baseline space-x-4">
                {links.map(link => (
                  <NavLink 
                    key={link.to} 
                    to={link.to}
                    className={({isActive}) => isActive ? activeClassName : inactiveClassName}
                  >
                    {link.icon}
                    <span>{link.text}</span>
                  </NavLink>
                ))}
              </div>
            </div>
          </div>
          <div className="-mr-2 flex md:hidden">
            <button
              onClick={() => setIsOpen(!isOpen)}
              type="button"
              className="bg-blue-700 inline-flex items-center justify-center p-2 rounded-md text-blue-200 hover:text-white hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-blue-800 focus:ring-white"
              aria-controls="mobile-menu"
              aria-expanded="false"
            >
              <span className="sr-only">Open main menu</span>
              {isOpen ? <X size={24} /> : <Menu size={24} />}
            </button>
          </div>
        </div>
      </div>

      {isOpen && (
        <div className="md:hidden" id="mobile-menu">
          <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3">
            {links.map(link => (
              <NavLink 
                key={link.to} 
                to={link.to}
                onClick={() => setIsOpen(false)}
                className={({isActive}) => isActive ? mobileActiveClassName : mobileInactiveClassName}
              >
                {link.icon}
                <span>{link.text}</span>
              </NavLink>
            ))}
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
