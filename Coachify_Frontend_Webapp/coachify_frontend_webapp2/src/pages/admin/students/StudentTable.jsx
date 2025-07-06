import React, { useEffect, useState, useMemo } from 'react';
import {
  Box,
  Typography,
  TextField,
  IconButton,
  MenuItem,
  Switch,
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { updateUser, assignMentorToStudent } from '../../../api/adminUsers';
import { LocalizationProvider, DatePicker } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';
import AssignMentor from './AssignMentor';

const PAYMENT_OPTIONS = ['PAID', 'PENDING', 'FAILED', 'CANCELLED'];

const StudentTable = ({
  students,
  mentors,                   // ← full mentor list from parent
  onReloadStudents = () => {}, // default noop
}) => {
  /* ───────────── state ───────────── */
  const [rows, setRows] = useState(students);
  const [originalRows, setOriginalRows] = useState(students);
  const [editedRows, setEditedRows] = useState({});

  /* Re-sync when parent data changes */
  useEffect(() => {
    setRows(students);
    setOriginalRows(students);
  }, [students]);

  /* Active-mentor list for dropdown */
  const activeMentors = useMemo(
    () => mentors.filter((m) => m.active),
    [mentors]
  );

  /* ────────── helpers ────────── */
  const handleFieldChange = (id, field, value) => {
    setRows((prev) =>
      prev.map((r) => (r.id === id ? { ...r, [field]: value } : r))
    );
    setEditedRows((prev) => ({ ...prev, [id]: true }));
  };

  const handleSave = async (id) => {
    const student = rows.find((r) => r.id === id);
    const original = originalRows.find((r) => r.id === id);
    if (!student) return;

    try {
      await updateUser('STUDENT', { ...student });

      /* mentor changed? → call assignment API */
      if (student.assignedMentor !== original.assignedMentor) {
        await assignMentorToStudent(
          student.id,
          student.assignedMentor || ''      // empty string → unassign
        );
      }

      setEditedRows((p) => {
        const copy = { ...p };
        delete copy[id];
        return copy;
      });
      setOriginalRows((p) =>
        p.map((r) => (r.id === id ? { ...student } : r))
      );
    } catch (err) {
      console.error('Save failed', err);
    }
  };

  const handleActiveToggle = async (id, newVal) => {
    /* optimistic UI */
    setRows((prev) =>
      prev.map((r) => (r.id === id ? { ...r, active: newVal } : r))
    );

    try {
      const student = rows.find((r) => r.id === id);
      if (!student) return;

      await updateUser('STUDENT', { ...student, active: newVal });
      await onReloadStudents();                // refresh list
    } catch (err) {
      console.error('Failed to update active', err);
      setRows((prev) =>
        prev.map((r) => (r.id === id ? { ...r, active: !newVal } : r))
      );
    }
  };

  /* ────────── columns ────────── */
  const columns = [
    {
      field: 'fullName',
      headerName: 'Full Name',
      flex: 1,
      renderCell: (p) => (
        <TextField
          variant="standard"
          fullWidth
          value={p.row.fullName}
          onChange={(e) =>
            handleFieldChange(p.row.id, 'fullName', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
        />
      ),
    },
    {
      field: 'phoneNumber',
      headerName: 'Phone',
      flex: 1,
      renderCell: (p) => (
        <TextField
          variant="standard"
          fullWidth
          value={p.row.phoneNumber}
          onChange={(e) =>
            handleFieldChange(p.row.id, 'phoneNumber', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
        />
      ),
    },
    {
      field: 'email',
      headerName: 'Email',
      flex: 1,
      renderCell: (p) => (
        <TextField
          variant="standard"
          fullWidth
          value={p.row.email}
          onChange={(e) =>
            handleFieldChange(p.row.id, 'email', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
        />
      ),
    },
    {
      field: 'paymentStatus',
      headerName: 'Payment',
      width: 140,
      renderCell: (p) => (
        <TextField
          select
          variant="standard"
          value={p.row.paymentStatus}
          onChange={(e) =>
            handleFieldChange(p.row.id, 'paymentStatus', e.target.value)
          }
        >
          {PAYMENT_OPTIONS.map((opt) => (
            <MenuItem key={opt} value={opt}>
              {opt}
            </MenuItem>
          ))}
        </TextField>
      ),
    },
    {
      field: 'purchaseDate',
      headerName: 'Purchase',
      width: 150,
      renderCell: (p) => (
        <DatePicker
          value={p.row.purchaseDate ? dayjs(p.row.purchaseDate) : null}
          onChange={(val) =>
            handleFieldChange(
              p.row.id,
              'purchaseDate',
              val ? val.format('YYYY-MM-DD') : ''
            )
          }
          format="DD/MM/YYYY"
          slotProps={{
            textField: { variant: 'standard', sx: { minWidth: 110 } },
          }}
        />
      ),
    },
    {
      field: 'subscriptionStartDate',
      headerName: 'Start',
      width: 150,
      renderCell: (p) => (
        <DatePicker
          value={p.row.subscriptionStartDate ? dayjs(p.row.subscriptionStartDate) : null}
          onChange={(val) =>
            handleFieldChange(
              p.row.id,
              'subscriptionStartDate',
              val ? val.format('YYYY-MM-DD') : ''
            )
          }
          format="DD/MM/YYYY"
          slotProps={{
            textField: { variant: 'standard', sx: { minWidth: 110 } },
          }}
        />
      ),
    },
    {
      field: 'nextPaymentDate',
      headerName: 'Next Pay',
      width: 150,
      renderCell: (p) => (
        <DatePicker
          value={p.row.nextPaymentDate ? dayjs(p.row.nextPaymentDate) : null}
          onChange={(val) =>
            handleFieldChange(
              p.row.id,
              'nextPaymentDate',
              val ? val.format('YYYY-MM-DD') : ''
            )
          }
          format="DD/MM/YYYY"
          slotProps={{
            textField: { variant: 'standard', sx: { minWidth: 110 } },
          }}
        />
      ),
    },
    /* ───── Mentor dropdown (active mentors only) ───── */
    {
      field: 'mentorName',
      headerName: 'Mentor',
      width: 200,
      sortable: false,
      renderCell: (p) => (
        <AssignMentor
          value={p.row.assignedMentor}
          mentors={activeMentors}                       // ⬅️ filter
          onChange={(newId) => {
            handleFieldChange(p.row.id, 'assignedMentor', newId);
            const name =
              activeMentors.find((m) => m.id === newId)?.fullName || '';
            handleFieldChange(p.row.id, 'mentorName', name);
          }}
        />
      ),
    },
    {
      field: 'notes',
      headerName: 'Notes',
      flex: 1,
      renderCell: (p) => (
        <TextField
          variant="standard"
          fullWidth
          value={p.row.notes}
          onChange={(e) =>
            handleFieldChange(p.row.id, 'notes', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
        />
      ),
    },
    {
      field: 'active',
      headerName: 'Active',
      width: 90,
      sortable: false,
      renderCell: (p) => (
        <Switch
          checked={p.row.active}
          onChange={(e) => handleActiveToggle(p.row.id, e.target.checked)}
          color="primary"
        />
      ),
    },
    {
      field: 'actions',
      headerName: '',
      width: 70,
      sortable: false,
      disableColumnMenu: true,
      renderCell: (p) =>
        editedRows[p.row.id] && (
          <IconButton
            color="success"
            title="Save"
            onClick={() => handleSave(p.row.id)}
          >
            <CheckCircleIcon />
          </IconButton>
        ),
    },
  ];

  /* ────────── render ────────── */
  return rows.length === 0 ? (
    <Typography variant="body1" align="center" mt={4}>
      No students found.
    </Typography>
  ) : (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Box sx={{ height: 600, width: '100%' }}>
        <DataGrid
          rows={rows}
          columns={columns}
          pageSize={10}
          rowsPerPageOptions={[10, 25, 50]}
          disableSelectionOnClick
        />
      </Box>
    </LocalizationProvider>
  );
};

export default StudentTable;
