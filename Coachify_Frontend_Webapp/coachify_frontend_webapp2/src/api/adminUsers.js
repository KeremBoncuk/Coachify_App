import axios from './axiosInstance';

/* ------------------------------------------------------------------ */
/* LIST ENDPOINTS (with optional onlyActive=true/false)                */
/* ------------------------------------------------------------------ */
export const getStudents = (onlyActive) =>
  axios
    .get('/admin/students', { params: { onlyActive } })
    .then((r) => r.data);

export const getMentors = (onlyActive) =>
  axios
    .get('/admin/mentors', { params: { onlyActive } })
    .then((r) => r.data);

/* ------------------------------------------------------------------ */
/* SINGLE USER LOOKUP                                                 */
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
    MENTOR : '/admin/update-mentor',
    ADMIN  : '/admin/update-admin',
  };
  return axios.put(map[role], data).then((r) => r.data);
};

/* ------------------------------------------------------------------ */
/* REGISTER USER (NEW)                                                */
/* ------------------------------------------------------------------ */
export const registerStudent = (student) =>
  axios.post('/admin/register-student', student).then((r) => r.data);

export const registerMentor = (mentor) =>
  axios.post('/admin/register-mentor', mentor).then((r) => r.data);

/* ------------------------------------------------------------------ */
/* ASSIGN MENTOR → STUDENT                                            */
/* ------------------------------------------------------------------ */
export const assignMentorToStudent = (studentId, mentorId) =>
  axios
    .post('/admin/students/assign-mentor', { studentId, mentorId })
    .then((r) => r.data);
