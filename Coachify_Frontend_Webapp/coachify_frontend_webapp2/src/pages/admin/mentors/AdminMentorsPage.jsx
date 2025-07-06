import React, { useEffect, useMemo, useState } from 'react';
import {
  Box,
  Typography,
  CircularProgress,
  Button,
  Stack,
} from '@mui/material';
import MentorFilters from './MentorFilters';
import MentorTable from './MentorTable';
import AdminMentorCreation from './AdminMentorCreation';
import { getMentors } from '../../../api/adminUsers';

const AdminMentorsPage = () => {
  /* ---------------- state ---------------- */
  const [mentors, setMentors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isCreateOpen, setIsCreateOpen] = useState(false);

  const [search, setSearch] = useState('');
  const [activeFilter, setActiveFilter] = useState('');

  /* ------------- fetch mentors ----------- */
  const fetchMentors = async () => {
    try {
      setLoading(true);
      const raw = await getMentors();

      const rows = raw.map((m, idx) => ({
        id: m.id || m._id || idx,
        fullName: m.fullName ?? '',
        email: m.email ?? '',
        phoneNumber: m.phoneNumber ?? '',
        school: m.school ?? '',
        department: m.department ?? '',
        placement: m.placement ?? '',
        area: m.area ?? '',
        birthDate: m.birthDate ?? '',
        iban: m.iban ?? '',
        notes: m.notes ?? '',
        active: m.active,
      }));

      setMentors(rows);
    } catch (err) {
      console.error('Mentor fetch failed', err);
      setMentors([]);
    } finally {
      setLoading(false);
    }
  };

  /* ------------- initial load ---------- */
  useEffect(() => {
    fetchMentors();
  }, []);

  /* ------------- filtering memo ---------- */
  const filtered = useMemo(() => {
    const q = search.toLowerCase();
    return mentors.filter((m) => {
      const matchSearch = m.fullName.toLowerCase().includes(q);
      const matchActive =
        !activeFilter ||
        (activeFilter === 'true' && m.active) ||
        (activeFilter === 'false' && !m.active);
      return matchSearch && matchActive;
    });
  }, [mentors, search, activeFilter]);

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
        <Typography variant="h5">Manage Mentors</Typography>
        <Button
          variant="contained"
          color="primary"
          onClick={() => setIsCreateOpen(true)}
        >
          Create Mentor
        </Button>
      </Stack>

      <MentorFilters
        search={search}
        setSearch={setSearch}
        activeFilter={activeFilter}
        setActiveFilter={setActiveFilter}
      />

      <MentorTable
        mentors={filtered}
        onReloadMentors={fetchMentors}
      />

      <AdminMentorCreation
        open={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        onSuccess={() => {
          fetchMentors();
          setIsCreateOpen(false);
        }}
      />
    </Box>
  );
};

export default AdminMentorsPage;
