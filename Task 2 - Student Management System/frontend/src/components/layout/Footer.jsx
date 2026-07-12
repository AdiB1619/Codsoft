/**
 * Footer — minimal site footer with copyright notice.
 */
function Footer() {
  const year = new Date().getFullYear()
  return (
    <footer className="bg-white border-t border-slate-200 mt-auto">
      <div className="page-container py-4 text-center text-xs text-slate-500">
        &copy; {year} Student Management System &mdash; CodSoft Java Internship Project
      </div>
    </footer>
  )
}

export default Footer
