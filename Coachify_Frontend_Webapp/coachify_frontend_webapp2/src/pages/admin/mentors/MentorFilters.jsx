import React from 'react';
import { TextField, MenuItem, Stack } from '@mui/material';

const MentorFilters = ({
  search, setSearch,
  activeFilter, setActiveFilter,
}) => (
  <Stack
    direction={{ xs: 'column', sm: 'row' }}
    spacing={2}
    alignItems="center"
    flexWrap="wrap"
    mb={2}
  >
    <TextField
      label="Search by name"
      value={search}
      onChange={(e) => setSearch(e.target.value)}
      sx={{ width: 250 }}
    />

    <TextField
      select
      label="Active"
      value={activeFilter}
      onChange={(e) => setActiveFilter(e.target.value)}
      sx={{ minWidth: 150 }}
    >
      <MenuItem value="">All</MenuItem>
      <MenuItem value="true">Active</MenuItem>
      <MenuItem value="false">Inactive</MenuItem>
    </TextField>
  </Stack>
);

export default MentorFilters;
