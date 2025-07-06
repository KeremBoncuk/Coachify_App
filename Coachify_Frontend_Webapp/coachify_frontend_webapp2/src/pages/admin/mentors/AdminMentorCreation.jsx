import React, { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  TextField,
  Button,
  Stack,
  Typography,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { registerMentor } from '../../../api/adminUsers';

const AdminMentorCreation = ({ open, onClose, onSuccess }) => {
  const [submitError, setSubmitError] = useState('');

  const formik = useFormik({
    initialValues: {
      fullName: '',
      email: '',
      phoneNumber: '',
      password: '',
      school: '',
      department: '',
      placement: '',
      area: '',
      birthDate: '',
      iban: '',
      notes: '',
    },
    validationSchema: Yup.object({
      fullName: Yup.string().required('Full name is required'),
      email: Yup.string().email('Invalid email').required('Email is required'),
      phoneNumber: Yup.string().required('Phone number is required'),
      password: Yup.string().min(6, 'Minimum 6 characters').required('Password is required'),
      school: Yup.string().required('School is required'),
      department: Yup.string().required('Department is required'),
      placement: Yup.number().typeError('Must be a number').required('Placement is required'),
      area: Yup.string().required('Area is required'),
      birthDate: Yup.date().required('Birth date is required'),
      iban: Yup.string().required('IBAN is required'),
      notes: Yup.string(),
    }),
    onSubmit: async (values, { setSubmitting, resetForm }) => {
      try {
        await registerMentor(values);
        setSubmitting(false);
        resetForm();
        onSuccess();            // refresh mentor list in parent
      } catch (error) {
        setSubmitError(error?.response?.data?.message || 'Failed to create mentor');
        setSubmitting(false);
      }
    },
  });

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        Create New Mentor
        <IconButton
          aria-label="close"
          onClick={onClose}
          sx={{ position: 'absolute', right: 8, top: 8 }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent dividers>
        <form onSubmit={formik.handleSubmit}>
          <Stack spacing={2}>
            <TextField
              label="Full Name"
              name="fullName"
              value={formik.values.fullName}
              onChange={formik.handleChange}
              error={formik.touched.fullName && Boolean(formik.errors.fullName)}
              helperText={formik.touched.fullName && formik.errors.fullName}
              fullWidth
            />
            <TextField
              label="Email"
              name="email"
              value={formik.values.email}
              onChange={formik.handleChange}
              error={formik.touched.email && Boolean(formik.errors.email)}
              helperText={formik.touched.email && formik.errors.email}
              fullWidth
            />
            <TextField
              label="Phone Number"
              name="phoneNumber"
              value={formik.values.phoneNumber}
              onChange={formik.handleChange}
              error={formik.touched.phoneNumber && Boolean(formik.errors.phoneNumber)}
              helperText={formik.touched.phoneNumber && formik.errors.phoneNumber}
              fullWidth
            />
            <TextField
              label="Password"
              name="password"
              type="password"
              value={formik.values.password}
              onChange={formik.handleChange}
              error={formik.touched.password && Boolean(formik.errors.password)}
              helperText={formik.touched.password && formik.errors.password}
              fullWidth
            />

            <TextField
              label="School"
              name="school"
              value={formik.values.school}
              onChange={formik.handleChange}
              error={formik.touched.school && Boolean(formik.errors.school)}
              helperText={formik.touched.school && formik.errors.school}
              fullWidth
            />
            <TextField
              label="Department"
              name="department"
              value={formik.values.department}
              onChange={formik.handleChange}
              error={formik.touched.department && Boolean(formik.errors.department)}
              helperText={formik.touched.department && formik.errors.department}
              fullWidth
            />
            <TextField
              label="Placement"
              name="placement"
              type="number"
              value={formik.values.placement}
              onChange={formik.handleChange}
              error={formik.touched.placement && Boolean(formik.errors.placement)}
              helperText={formik.touched.placement && formik.errors.placement}
              fullWidth
            />
            <TextField
              label="Area (e.g. SAY)"
              name="area"
              value={formik.values.area}
              onChange={formik.handleChange}
              error={formik.touched.area && Boolean(formik.errors.area)}
              helperText={formik.touched.area && formik.errors.area}
              fullWidth
            />
            <TextField
              label="Birth Date"
              name="birthDate"
              type="date"
              value={formik.values.birthDate}
              onChange={formik.handleChange}
              error={formik.touched.birthDate && Boolean(formik.errors.birthDate)}
              helperText={formik.touched.birthDate && formik.errors.birthDate}
              InputLabelProps={{ shrink: true }}
              fullWidth
            />
            <TextField
              label="IBAN"
              name="iban"
              value={formik.values.iban}
              onChange={formik.handleChange}
              error={formik.touched.iban && Boolean(formik.errors.iban)}
              helperText={formik.touched.iban && formik.errors.iban}
              fullWidth
            />
            <TextField
              label="Notes"
              name="notes"
              value={formik.values.notes}
              onChange={formik.handleChange}
              error={formik.touched.notes && Boolean(formik.errors.notes)}
              helperText={formik.touched.notes && formik.errors.notes}
              fullWidth
            />
          </Stack>
          {submitError && (
            <Typography color="error" mt={2}>
              {submitError}
            </Typography>
          )}
        </form>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose} color="secondary">
          Cancel
        </Button>
        <Button
          onClick={formik.submitForm}
          color="primary"
          variant="contained"
          disabled={formik.isSubmitting}
        >
          Create
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AdminMentorCreation;
