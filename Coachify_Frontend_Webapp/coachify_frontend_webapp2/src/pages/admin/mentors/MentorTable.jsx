import React, { useEffect, useState } from 'react';
import {
  Box,
  Typography,
  TextField,
  IconButton,
  Switch,
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { updateUser } from '../../../api/adminUsers';
import { LocalizationProvider, DatePicker } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';

const MentorTable = ({
  mentors,
  onReloadMentors = () => {},
}) => {
  const [rows, setRows] = useState(mentors);
  const [originalRows, setOriginalRows] = useState(mentors);
  const [editedRows, setEditedRows] = useState({});

  useEffect(() => {
    setRows(mentors);
    setOriginalRows(mentors);
  }, [mentors]);

  /* ---------------- helpers ------------- */
  const handleFieldChange = (id, field, value) => {
    setRows((prev) =>
      prev.map((r) => (r.id === id ? { ...r, [field]: value } : r))
    );
    setEditedRows((prev) => ({ ...prev, [id]: true }));
  };

  const handleSave = async (id) => {
    const mentor = rows.find((r) => r.id === id);
    if (!mentor) return;

    try {
      await updateUser('MENTOR', { ...mentor });
      setEditedRows((prev) => {
        const copy = { ...prev };
        delete copy[id];
        return copy;
      });
      setOriginalRows((prev) =>
        prev.map((r) => (r.id === id ? { ...mentor } : r))
      );
    } catch (err) {
      console.error('Save failed', err);
    }
  };

  const handleActiveToggle = async (id, newValue) => {
    // optimistic UI update
    setRows((prev) =>
      prev.map((r) => (r.id === id ? { ...r, active: newValue } : r))
    );

    try {
      const mentor = rows.find((r) => r.id === id);
      if (!mentor) return;

      await updateUser('MENTOR', { ...mentor, active: newValue });
      await onReloadMentors();
    } catch (err) {
      console.error('Failed to update active status', err);
      setRows((prev) =>
        prev.map((r) => (r.id === id ? { ...r, active: !newValue } : r))
      );
    }
  };

  /* ---------------- columns ------------- */
  const columns = [
    { field: 'fullName', headerName: 'Full Name', flex: 1,
      renderCell: (params) => (
        <TextField
          variant="standard"
          fullWidth
          value={params.row.fullName}
          onChange={(e) =>
            handleFieldChange(params.row.id, 'fullName', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
        />
      ),
    },
    { field: 'phoneNumber', headerName: 'Phone', flex: 1,
      renderCell: (params) => (
        <TextField
          variant="standard"
          fullWidth
          value={params.row.phoneNumber}
          onChange={(e) =>
            handleFieldChange(params.row.id, 'phoneNumber', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
        />
      ),
    },
    { field: 'email', headerName: 'Email', flex: 1,
      renderCell: (params) => (
        <TextField
          variant="standard"
          fullWidth
          value={params.row.email}
          onChange={(e) =>
            handleFieldChange(params.row.id, 'email', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
        />
      ),
    },
    { field: 'school', headerName: 'School', flex: 1,
      renderCell: (params) => (
        <TextField
          variant="standard"
          fullWidth
          value={params.row.school}
          onChange={(e) =>
            handleFieldChange(params.row.id, 'school', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
        />
      ),
    },
    { field: 'department', headerName: 'Dept.', flex: 1,
      renderCell: (params) => (
        <TextField
          variant="standard"
          fullWidth
          value={params.row.department}
          onChange={(e) =>
            handleFieldChange(params.row.id, 'department', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
        />
      ),
    },
    { field: 'placement', headerName: 'Place', width: 100,
      renderCell: (params) => (
        <TextField
          variant="standard"
          type="number"
          value={params.row.placement}
          onChange={(e) =>
            handleFieldChange(params.row.id, 'placement', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
          sx={{ maxWidth: 90 }}
        />
      ),
    },
    { field: 'area', headerName: 'Area', width: 100,
      renderCell: (params) => (
        <TextField
          variant="standard"
          value={params.row.area}
          onChange={(e) =>
            handleFieldChange(params.row.id, 'area', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
          sx={{ maxWidth: 90 }}
        />
      ),
    },
    { field: 'birthDate', headerName: 'Birth', width: 140,
      renderCell: (params) => (
        <DatePicker
          value={params.row.birthDate ? dayjs(params.row.birthDate) : null}
          onChange={(newVal) => {
            const iso = newVal ? newVal.format('YYYY-MM-DD') : '';
            handleFieldChange(params.row.id, 'birthDate', iso);
          }}
          format="DD/MM/YYYY"
          slotProps={{
            textField: { variant: 'standard', sx: { minWidth: 110 } },
          }}
        />
      ),
    },
    { field: 'iban', headerName: 'IBAN', flex: 1.2,
      renderCell: (params) => (
        <TextField
          variant="standard"
          fullWidth
          value={params.row.iban}
          onChange={(e) =>
            handleFieldChange(params.row.id, 'iban', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
        />
      ),
    },
    { field: 'notes', headerName: 'Notes', flex: 1.5,
      renderCell: (params) => (
        <TextField
          variant="standard"
          fullWidth
          value={params.row.notes}
          onChange={(e) =>
            handleFieldChange(params.row.id, 'notes', e.target.value)
          }
          onKeyDown={(e) => e.stopPropagation()}
        />
      ),
    },
    { field: 'active', headerName: 'Active', width: 90, sortable: false,
      renderCell: (params) => (
        <Switch
          checked={params.row.active}
          onChange={(e) =>
            handleActiveToggle(params.row.id, e.target.checked)
          }
          color="primary"
        />
      ),
    },
    { field: 'actions', headerName: '', width: 70, sortable: false, disableColumnMenu: true,
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

  /* ---------------- render ------------- */
  return rows.length === 0 ? (
    <Typography variant="body1" align="center" mt={4}>
      No mentors found.
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

export default MentorTable;
