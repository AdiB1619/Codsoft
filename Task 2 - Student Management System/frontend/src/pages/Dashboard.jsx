import React, { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { UserPlus, Users, GraduationCap, CheckCircle, UserCheck, Download } from 'lucide-react'
import studentApi from '../api/studentApi'
import StatCard from '../components/common/StatCard'
import useToast from '../hooks/useToast'
import Loader from '../components/common/Loader'
import Button from '../components/common/Button'
import { triggerDownload } from '../utils/download'

/**
 * Dashboard — shows summary statistics and quick actions.
 */
function Dashboard() {
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [exporting, setExporting] = useState(false)
  const showToast = useToast()

  useEffect(() => {
    studentApi.getStats()
      .then(data => setStats(data))
      .catch(err => showToast(err.message ?? 'Failed to load stats.', 'error'))
      .finally(() => setLoading(false))
  }, [showToast])

  const handleExport = useCallback(async () => {
    setExporting(true)
    try {
      const response = await studentApi.exportCsv({})
      triggerDownload(response, `students-export-${new Date().toISOString().slice(0, 10)}.csv`)
      showToast('CSV exported successfully.', 'success')
    } catch (err) {
      showToast(err.message ?? 'Export failed.', 'error')
    } finally {
      setExporting(false)
    }
  }, [showToast])

  if (loading) {
    return <Loader />
  }

  return (
    <div>
      <div className="mb-6">
        <h1 className="font-heading text-2xl font-semibold text-primary">Dashboard</h1>
        <p className="text-sm text-slate-500 mt-1">Welcome to the Student Management System</p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard
          title="Total Students"
          value={stats?.totalStudents ?? 0}
          icon={Users}
          iconColor="text-blue-600"
          iconBg="bg-blue-50"
        />
        <StatCard
          title="Active Students"
          value={stats?.activeStudents ?? 0}
          icon={CheckCircle}
          iconColor="text-emerald-600"
          iconBg="bg-emerald-50"
        />
        <StatCard
          title="Total Courses"
          value={stats?.totalCourses ?? 0}
          icon={GraduationCap}
          iconColor="text-indigo-600"
          iconBg="bg-indigo-50"
        />
        <StatCard
          title="New This Month"
          value={stats?.newThisMonth ?? 0}
          icon={UserCheck}
          iconColor="text-amber-600"
          iconBg="bg-amber-50"
        />
      </div>

      {/* Quick actions */}
      <div className="flex flex-wrap gap-3">
        <Link
          to="/students"
          className="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-white border border-slate-200
            text-slate-700 text-sm font-medium hover:bg-slate-50 transition-colors duration-hover focus-ring"
        >
          <Users size={16} aria-hidden="true" />
          View All Students
        </Link>
        <Link
          to="/students/new"
          className="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-primary text-white
            text-sm font-medium hover:bg-primary-hover transition-colors duration-hover focus-ring"
        >
          <UserPlus size={16} aria-hidden="true" />
          Add New Student
        </Link>
        <Button
          variant="outline"
          onClick={handleExport}
          loading={exporting}
          aria-label="Export all students as CSV"
        >
          <Download size={16} aria-hidden="true" className={exporting ? 'hidden' : ''} />
          Export All (CSV)
        </Button>
      </div>
    </div>
  )
}

export default Dashboard
