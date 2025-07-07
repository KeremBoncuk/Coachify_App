import axiosInstance from './axiosInstance';

/**
 * Mentor Student API Helper
 * ------------------------------------------------
 * Uses the shared Axios instance (JWT auto-attached).
 * All endpoints align with MentorStudentController backend.
 */

// GET /mentor/students
export const getAssignedStudents = () =>
  axiosInstance.get('/mentor/students').then((res) => res.data);
