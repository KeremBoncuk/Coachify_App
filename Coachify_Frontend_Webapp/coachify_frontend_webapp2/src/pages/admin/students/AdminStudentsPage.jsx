import React, { useEffect, useMemo, useState } from 'react';
import { Box, Typography, CircularProgress } from '@mui/material';
import StudentFilters from './StudentFilters';
import StudentTable   from './StudentTable';
import { getStudents, getMentorById } from '../../../api/adminUsers';

const AdminStudentsPage = () => {
  /* ---------------- state ---------------- */
  const [students, setStudents] = useState([]);
  const [loading,  setLoading]  = useState(true);

  const [search,        setSearch]        = useState('');
  const [paymentFilter, setPaymentFilter] = useState('');
  const [activeFilter,  setActiveFilter]  = useState('');

  /* ------------- fetch + enrich ---------- */
  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);

        /* 1. students ------------------------------------------------ */
        const raw = await getStudents();

        const baseRows = raw.map((s, idx) => ({
          id: s.id || s._id || idx,
          fullName:             s.fullName            ?? '',
          email:                s.email               ?? '',
          phoneNumber:          s.phoneNumber         ?? '',
          paymentStatus:        s.paymentStatus       ?? '',
          purchaseDate:         s.purchaseDate        ?? '',
          subscriptionStartDate:s.subscriptionStartDate ?? '',
          nextPaymentDate:      s.nextPaymentDate     ?? '',
          assignedMentor:       s.assignedMentor      ?? null,
          notes:                s.notes               ?? '',
          active:               s.active,
        }));

        /* 2. mentor look-ups (unique ids) --------------------------- */
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

        /* 3. merge back --------------------------------------------- */
        const rowsWithNames = baseRows.map((r) => ({
          ...r,
          mentorName: mentorMap[r.assignedMentor] || '',
        }));

        setStudents(rowsWithNames);
      } catch (err) {
        console.error('Student fetch failed', err);
        setStudents([]);           // keep UI consistent
      } finally {
        setLoading(false);
      }
    };

    load();
  }, []);

  /* ------------- filtering memo ---------- */
  const filtered = useMemo(() => {
    const q = search.toLowerCase();

    return students.filter((s) => {
      const matchSearch  = s.fullName.toLowerCase().includes(q);
      const matchPay     = !paymentFilter || s.paymentStatus === paymentFilter;
      const matchActive  =
        !activeFilter ||
        (activeFilter === 'true'  && s.active) ||
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
      <Typography variant="h5" gutterBottom>
        Manage Students
      </Typography>

      <StudentFilters
        search={search}               setSearch={setSearch}
        paymentFilter={paymentFilter} setPaymentFilter={setPaymentFilter}
        activeFilter={activeFilter}   setActiveFilter={setActiveFilter}
      />

      <StudentTable students={filtered} />
    </Box>
  );
};

export default AdminStudentsPage;
