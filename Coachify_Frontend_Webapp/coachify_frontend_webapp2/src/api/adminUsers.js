import axios from './axiosInstance';

/* ------------------------------------------------------------------ */
/* LIST ENDPOINTS                                                     */
/* ------------------------------------------------------------------ */
export const getStudents = () =>
  axios.get('/admin/students').then((r) => r.data);

export const getMentors = () =>
  axios.get('/admin/mentors').then((r) => r.data);

/* ------------------------------------------------------------------ */
/* SINGLE USER LOOKUP (Mentor, Student, Admin)                         */
/* ------------------------------------------------------------------ */
export const getMentorById = (mentorId) =>
  axios.get(`/admin/get-mentor?mentorId=${mentorId}`).then((r) => r.data);

export const getStudentById = (studentId) =>
  axios.get(`/admin/get-student?studentId=${studentId}`).then((r) => r.data);

export const getAdminById = (adminId) =>
  axios.get(`/admin/get-admin?adminId=${adminId}`).then((r) => r.data);

/* ------------------------------------------------------------------ */
/* CREATE / UPDATE                                                    */
/* ------------------------------------------------------------------ */
export const updateUser = (role, data) => {
  const map = {
    STUDENT: '/admin/update-student',
    MENTOR: '/admin/update-mentor',
    ADMIN: '/admin/update-admin',
  };
  return axios.put(map[role], data).then((r) => r.data);
};
