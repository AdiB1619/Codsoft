import { Routes, Route } from 'react-router-dom'
import AppLayout from './components/layout/AppLayout'
import Dashboard from './pages/Dashboard'
import StudentListPage from './pages/StudentListPage'
import AddStudentPage from './pages/AddStudentPage'
import EditStudentPage from './pages/EditStudentPage'
import StudentDetailsPage from './pages/StudentDetailsPage'
import NotFoundPage from './pages/NotFoundPage'

/**
 * Root router — declares all application routes.
 * Every route renders inside AppLayout which provides the Navbar and Footer.
 */
function App() {
  return (
    <Routes>
      <Route path="/" element={<AppLayout />}>
        <Route index element={<Dashboard />} />
        <Route path="students" element={<StudentListPage />} />
        <Route path="students/new" element={<AddStudentPage />} />
        <Route path="students/:id" element={<StudentDetailsPage />} />
        <Route path="students/:id/edit" element={<EditStudentPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}

export default App
