import { Link, NavLink } from 'react-router-dom'
import { GraduationCap, LayoutDashboard, Users, Menu, X } from 'lucide-react'
import { useState } from 'react'

const NAV_LINKS = [
  { to: '/',         label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/students', label: 'Students',  icon: Users,           end: false },
]

/**
 * Navbar — persistent top navigation bar.
 * Highlights the active route via React Router's NavLink `isActive` callback.
 * Includes a mobile hamburger menu that collapses below `sm` breakpoint.
 */
function Navbar() {
  const [mobileOpen, setMobileOpen] = useState(false)

  return (
    <nav className="bg-primary text-white shadow-md" role="navigation" aria-label="Main navigation">
      <div className="page-container">
        <div className="flex items-center justify-between h-16">

          {/* ── Logo ─────────────────────────────────────────── */}
          <Link
            to="/"
            className="flex items-center gap-2 font-heading font-semibold text-lg tracking-wide
              hover:opacity-90 transition-opacity duration-hover focus-ring rounded"
          >
            <GraduationCap size={24} aria-hidden="true" />
            <span className="hidden xs:inline">SMS</span>
            <span className="hidden sm:inline text-slate-300 font-body font-light text-sm">
              Student Management System
            </span>
          </Link>

          {/* ── Desktop nav links ────────────────────────────── */}
          <ul className="hidden sm:flex items-center gap-1">
            {NAV_LINKS.map(({ to, label, icon: Icon, end }) => (
              <li key={to}>
                <NavLink
                  to={to}
                  end={end}
                  className={({ isActive }) =>
                    `flex items-center gap-1.5 px-3 py-2 rounded-md text-sm font-medium
                    transition-colors duration-hover focus-ring ${
                      isActive
                        ? 'bg-white/20 text-white'
                        : 'text-slate-200 hover:bg-primary-hover hover:text-white'
                    }`
                  }
                >
                  <Icon size={16} aria-hidden="true" />
                  {label}
                </NavLink>
              </li>
            ))}
          </ul>

          {/* ── Mobile menu button ───────────────────────────── */}
          <button
            className="sm:hidden p-2 rounded-md text-slate-200 hover:bg-primary-hover
              transition-colors duration-hover focus-ring"
            onClick={() => setMobileOpen(prev => !prev)}
            aria-label={mobileOpen ? 'Close menu' : 'Open menu'}
            aria-expanded={mobileOpen}
          >
            {mobileOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>
      </div>

      {/* ── Mobile dropdown ─────────────────────────────────── */}
      {mobileOpen && (
        <div className="sm:hidden border-t border-white/10 bg-primary">
          <ul className="page-container py-2 flex flex-col gap-1">
            {NAV_LINKS.map(({ to, label, icon: Icon, end }) => (
              <li key={to}>
                <NavLink
                  to={to}
                  end={end}
                  onClick={() => setMobileOpen(false)}
                  className={({ isActive }) =>
                    `flex items-center gap-2 px-3 py-2.5 rounded-md text-sm font-medium
                    transition-colors duration-hover ${
                      isActive
                        ? 'bg-white/20 text-white'
                        : 'text-slate-200 hover:bg-primary-hover hover:text-white'
                    }`
                  }
                >
                  <Icon size={16} aria-hidden="true" />
                  {label}
                </NavLink>
              </li>
            ))}
          </ul>
        </div>
      )}
    </nav>
  )
}

export default Navbar
