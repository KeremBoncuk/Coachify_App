import React, { useEffect, useState } from 'react';
import { Box, Typography, TextField, IconButton } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { updateUser } from '../../../api/adminUsers';

const StudentTable = ({ students }) => {
  const [rows,       setRows]       = useState(students);
  const [editedRows, setEditedRows] = useState({});

  useEffect(() => setRows(students), [students]);

  /*  ---------- inline fullName editing ------------ */
  const handleFullNameChange = (id, value) => {
    setRows((prev) =>
      prev.map((r) => (r.id === id ? { ...r, fullName: value } : r))
    );
    setEditedRows((prev) => ({ ...prev, [id]: true }));
  };

  const handleSave = async (id) => {
    const student = rows.find((r) => r.id === id);
    if (!student) return;

    try {
      await updateUser('STUDENT', { ...student });
      setEditedRows((prev) => {
        const copy = { ...prev };
        delete copy[id];
        return copy;
      });
    } catch (err) {
      console.error('Save failed', err);
    }
  };

  /*  ---------------- DataGrid columns -------------- */
  const columns = [
    {
      field: 'fullName',
      headerName: 'Full Name',
      flex: 1,
      renderCell: (params) => (
        <TextField
          variant="standard"
          value={params.row.fullName}
          fullWidth
          onChange={(e) =>
            handleFullNameChange(params.row.id, e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()} // allow Space
        />
      ),
    },
    { field: 'phoneNumber', headerName: 'Phone', flex: 1 },
    { field: 'email', headerName: 'Email', flex: 1 },
    { field: 'paymentStatus', headerName: 'Payment', width: 120 },
    { field: 'purchaseDate', headerName: 'Purchase', width: 130 },
    { field: 'subscriptionStartDate', headerName: 'Start', width: 130 },
    { field: 'nextPaymentDate', headerName: 'Next Pay', width: 130 },
    {
      field: 'mentorName',
      headerName: 'Mentor',
      width: 200,
      sortable: false,
    },
    { field: 'notes', headerName: 'Notes', flex: 1 },
    {
      field: 'active',
      headerName: 'Active',
      width: 90,
      type: 'boolean',
    },
    {
      field: 'actions',
      headerName: '',
      width: 70,
      sortable: false,
      disableColumnMenu: true,
      renderCell: (params) =>
        editedRows[params.row.id] && (
          <IconButton
            color="success"
            title="Save"
            onClick={() => handleSave(params.row.id)}
          >
            <CheckCircleIcon />
          </IconButton>
        ),
    },
  ];

  /*  ------------------- render --------------------- */
  return rows.length === 0 ? (
    <Typography variant="body1" align="center" mt={4}>
      No students found.
    </Typography>
  ) : (
    <Box sx={{ height: 600, width: '100%' }}>
      <DataGrid
        rows={rows}
        columns={columns}
        pageSize={10}
        rowsPerPageOptions={[10, 25, 50]}
        disableSelectionOnClick
      />
    </Box>
  );
};

export default StudentTable;
