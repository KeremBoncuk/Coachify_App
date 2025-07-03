import React from 'react';
import { TextField, MenuItem, Stack } from '@mui/material';

const StudentFilters = ({
  search, setSearch,
  paymentFilter, setPaymentFilter,
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
      label="Payment Status"
      value={paymentFilter}
      onChange={(e) => setPaymentFilter(e.target.value)}
      sx={{ minWidth: 180 }}
    >
      <MenuItem value="">All</MenuItem>
      <MenuItem value="PAID">Paid</MenuItem>
      <MenuItem value="UNPAID">Unpaid</MenuItem>
    </TextField>

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

export default StudentFilters;
