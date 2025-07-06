import React from 'react';
import { TextField, MenuItem } from '@mui/material';

/**
 * AssignMentor Component
 *
 * Props:
 * - value: current assigned mentorId (string or null)
 * - mentors: array of mentor objects [{ id, fullName }]
 * - onChange: function(mentorId) — called when selection changes
 */
const AssignMentor = ({ value, mentors, onChange }) => {
  return (
    <TextField
      select
      variant="standard"
      value={value || ''}
      onChange={(e) => onChange(e.target.value)}
      fullWidth
    >
      <MenuItem value="">-- Unassigned --</MenuItem>
      {mentors.map((mentor) => (
        <MenuItem key={mentor.id} value={mentor.id}>
          {mentor.fullName}
        </MenuItem>
      ))}
    </TextField>
  );
};

export default AssignMentor;
