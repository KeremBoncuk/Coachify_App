import React, { useEffect, useState, useMemo } from 'react';
import { Box, Typography } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import dayjs from 'dayjs';
import { getAssignedStudents } from '../../../api/mentorUsers';

/* ───────────────────────── helpers ───────────────────────── */
const mapStudentsForGrid = (apiStudents) =>
  apiStudents.map((s) => ({
    id: s.id || s._id || crypto.randomUUID(),
    fullName: s.fullName ?? '',
    phoneNumber: s.phoneNumber ?? '',
    email: s.email ?? '',
    paymentStatus: s.paymentStatus ?? '',
    purchaseDate: s.purchaseDate ?? null,
    subscriptionStartDate: s.subscriptionStartDate ?? null,
    nextPaymentDate: s.nextPaymentDate ?? null,
    active: s.active ?? false,
  }));

/* works for ISO-8601 strings */
const formatDate = (value) => {
  if (!value) return '';
  const date = dayjs(value);
  return date.isValid() ? date.format('DD/MM/YYYY') : '';
};

/* ───────────────────────── component ──────────────────────── */
const MentorStudentTable = () => {
  const [rows, setRows]     = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const apiData = await getAssignedStudents();
        setRows(mapStudentsForGrid(apiData));
      } catch (err) {
        console.error('Failed to fetch mentor students', err);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const columns = useMemo(
    () => [
      { field: 'fullName', headerName: 'Full Name', flex: 1 },
      { field: 'phoneNumber', headerName: 'Phone', flex: 1 },
      { field: 'email', headerName: 'Email', flex: 1 },
      { field: 'paymentStatus', headerName: 'Payment', width: 120 },
      {
        field: 'purchaseDate',
        headerName: 'Purchase',
        width: 150,
        renderCell: ({ value }) => formatDate(value),
      },
      {
        field: 'subscriptionStartDate',
        headerName: 'Start',
        width: 150,
        renderCell: ({ value }) => formatDate(value),
      },
      {
        field: 'nextPaymentDate',
        headerName: 'Next Pay',
        width: 150,
        renderCell: ({ value }) => formatDate(value),
      },
      {
        field: 'active',
        headerName: 'Active',
        width: 100,
        type: 'boolean',
        renderCell: ({ value }) => (value ? 'Yes' : 'No'),
      },
    ],
    []
  );

  if (loading) {
    return (
      <Typography variant="body1" align="center" mt={4}>
        Loading students…
      </Typography>
    );
  }

  if (!rows.length) {
    return (
      <Typography variant="body1" align="center" mt={4}>
        No assigned students found.
      </Typography>
    );
  }

  return (
    <Box sx={{ height: 600, width: '100%' }}>
      <DataGrid
        rows={rows}
        columns={columns}
        initialState={{ pagination: { paginationModel: { pageSize: 10 } } }}
        pageSizeOptions={[10, 25, 50]}
        disableRowSelectionOnClick
      />
    </Box>
  );
};

export default MentorStudentTable;
