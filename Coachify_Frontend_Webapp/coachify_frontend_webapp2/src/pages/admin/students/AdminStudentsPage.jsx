import React, { useEffect, useMemo, useState } from 'react';
import {
  Box,
  Typography,
  CircularProgress,
  Button,
  Stack,
} from '@mui/material';
import StudentFilters from './StudentFilters';
import StudentTable from './StudentTable';
import AdminStudentCreation from './AdminStudentCreation';
import { getStudents, getMentors, getMentorById } from '../../../api/adminUsers';

const AdminStudentsPage = () => {
  /* ---------------- state ---------------- */
  const [students, setStudents] = useState([]);
  const [mentors, setMentors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isCreateOpen, setIsCreateOpen] = useState(false);

  const [search, setSearch] = useState('');
  const [paymentFilter, setPaymentFilter] = useState('');
  const [activeFilter, setActiveFilter] = useState('');

  /* ------------- fetch mentors ----------- */
  const fetchMentors = async () => {
    try {
      const data = await getMentors();
      setMentors(data);
    } catch (err) {
      console.error('Failed to fetch mentors', err);
      setMentors([]);
    }
  };

  /* ------------- fetch + enrich students ---------- */
  const fetchStudents = async () => {
    try {
      setLoading(true);
      const raw = await getStudents();

      const baseRows = raw.map((s, idx) => ({
        id: s.id || s._id || idx,
        fullName: s.fullName ?? '',
        email: s.email ?? '',
        phoneNumber: s.phoneNumber ?? '',
        paymentStatus: s.paymentStatus ?? '',
        purchaseDate: s.purchaseDate ?? '',
        subscriptionStartDate: s.subscriptionStartDate ?? '',
        nextPaymentDate: s.nextPaymentDate ?? '',
        assignedMentor: s.assignedMentor ?? null,
        notes: s.notes ?? '',
        active: s.active,
      }));

      // Build mentor name map
      const mentorIds = [
        ...new Set(baseRows.map((r) => r.assignedMentor).filter(Boolean)),
      ];

      const mentorMap = {};
      for (const id of mentorIds) {
        try {
          const mentor = await getMentorById(id);
          mentorMap[id] = mentor.fullName;
        } catch (e) {
          console.error(`Mentor ${id} lookup failed; using ID fallback`);
          mentorMap[id] = id;
        }
      }

      const rowsWithNames = baseRows.map((r) => ({
        ...r,
        mentorName: mentorMap[r.assignedMentor] || '',
      }));

      setStudents(rowsWithNames);
    } catch (err) {
      console.error('Student fetch failed', err);
      setStudents([]);
    } finally {
      setLoading(false);
    }
  };

  /* ------------- initial load ---------- */
  useEffect(() => {
    fetchMentors();
    fetchStudents();
  }, []);

  /* ------------- filtering memo ---------- */
  const filtered = useMemo(() => {
    const q = search.toLowerCase();

    return students.filter((s) => {
      const matchSearch = s.fullName.toLowerCase().includes(q);
      const matchPay =
        !paymentFilter || s.paymentStatus === paymentFilter;
      const matchActive =
        !activeFilter ||
        (activeFilter === 'true' && s.active) ||
        (activeFilter === 'false' && !s.active);
      return matchSearch && matchPay && matchActive;
    });
  }, [students, search, paymentFilter, activeFilter]);

  /* --------------- render --------------- */
  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box p={2}>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        justifyContent="space-between"
        alignItems="center"
        spacing={2}
        mb={2}
      >
        <Typography variant="h5">Manage Students</Typography>
        <Button
          variant="contained"
          color="primary"
          onClick={() => setIsCreateOpen(true)}
        >
          Create Student
        </Button>
      </Stack>

      <StudentFilters
        search={search}
        setSearch={setSearch}
        paymentFilter={paymentFilter}
        setPaymentFilter={setPaymentFilter}
        activeFilter={activeFilter}
        setActiveFilter={setActiveFilter}
      />

      {/* ✅ Pass fetchStudents so the grid can self-refresh */}
      <StudentTable
        students={filtered}
        mentors={mentors}
        onReloadStudents={fetchStudents}
      />

      <AdminStudentCreation
        open={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        onSuccess={() => {
          fetchStudents();
          setIsCreateOpen(false);
        }}
      />
    </Box>
  );
};

export default AdminStudentsPage;
