import { Outlet } from 'react-router-dom'
import Navbar from './Navbar'
import Footer from './Footer'

/**
 * AppLayout wraps every authenticated page with the shared Navbar and Footer.
 * The <Outlet /> renders the matched child route.
 */
function AppLayout() {
  return (
    <div className="flex flex-col min-h-screen bg-slate-50">
      <Navbar />
      <main className="flex-1 page-container py-6">
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}

export default AppLayout
