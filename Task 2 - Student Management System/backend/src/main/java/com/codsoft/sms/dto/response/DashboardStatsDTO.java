package com.codsoft.sms.dto.response;

public class DashboardStatsDTO {
    private long totalStudents;
    private long activeStudents;
    private long totalCourses;
    private long newThisMonth;

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getActiveStudents() { return activeStudents; }
    public void setActiveStudents(long activeStudents) { this.activeStudents = activeStudents; }

    public long getTotalCourses() { return totalCourses; }
    public void setTotalCourses(long totalCourses) { this.totalCourses = totalCourses; }

    public long getNewThisMonth() { return newThisMonth; }
    public void setNewThisMonth(long newThisMonth) { this.newThisMonth = newThisMonth; }
}
