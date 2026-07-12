import { Link } from 'react-router-dom'
import { Home } from 'lucide-react'

/**
 * NotFoundPage — shown for any unmatched route.
 */
function NotFoundPage() {
  return (
    <div className="flex flex-col items-center justify-center py-32 gap-6 text-center">
      <p className="font-mono text-7xl font-semibold text-primary/20">404</p>
      <h1 className="font-heading text-2xl font-semibold text-primary">Page Not Found</h1>
      <p className="text-slate-500 text-sm max-w-sm">
        The page you are looking for does not exist or has been moved.
      </p>
      <Link
        to="/"
        className="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-primary text-white text-sm
          font-medium hover:bg-primary-hover transition-colors duration-hover focus-ring"
      >
        <Home size={16} aria-hidden="true" />
        Back to Dashboard
      </Link>
    </div>
  )
}

export default NotFoundPage
