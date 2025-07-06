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
import { registerStudent } from '../../../api/adminUsers';

const AdminStudentCreation = ({ open, onClose, onSuccess }) => {
  const [submitError, setSubmitError] = useState('');

  const formik = useFormik({
    initialValues: {
      fullName: '',
      email: '',
      phoneNumber: '',
      password: '',
      purchaseDate: '',
      subscriptionStartDate: '',
      nextPaymentDate: '',
      paymentStatus: 'PAID',
      notes: '',
    },
    validationSchema: Yup.object({
      fullName: Yup.string().required('Full name is required'),
      email: Yup.string().email('Invalid email').required('Email is required'),
      phoneNumber: Yup.string().required('Phone number is required'),
      password: Yup.string().min(6, 'Minimum 6 characters').required('Password is required'),
      purchaseDate: Yup.date().required('Purchase date is required'),
      subscriptionStartDate: Yup.date().required('Subscription start date is required'),
      nextPaymentDate: Yup.date().required('Next payment date is required'),
      paymentStatus: Yup.string().oneOf(['PAID', 'UNPAID']).required('Payment status is required'),
      notes: Yup.string(),
    }),
    onSubmit: async (values, { setSubmitting, resetForm }) => {
      try {
        await registerStudent(values);
        setSubmitting(false);
        resetForm();
        onSuccess(); // notify parent to refresh students
      } catch (error) {
        setSubmitError(error?.response?.data?.message || 'Failed to create student');
        setSubmitting(false);
      }
    },
  });

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        Create New Student
        <IconButton
          aria-label="close"
          onClick={onClose}
          sx={{
            position: 'absolute',
            right: 8,
            top: 8,
          }}
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
              label="Purchase Date"
              name="purchaseDate"
              type="date"
              value={formik.values.purchaseDate}
              onChange={formik.handleChange}
              error={formik.touched.purchaseDate && Boolean(formik.errors.purchaseDate)}
              helperText={formik.touched.purchaseDate && formik.errors.purchaseDate}
              InputLabelProps={{ shrink: true }}
              fullWidth
            />
            <TextField
              label="Subscription Start Date"
              name="subscriptionStartDate"
              type="date"
              value={formik.values.subscriptionStartDate}
              onChange={formik.handleChange}
              error={formik.touched.subscriptionStartDate && Boolean(formik.errors.subscriptionStartDate)}
              helperText={formik.touched.subscriptionStartDate && formik.errors.subscriptionStartDate}
              InputLabelProps={{ shrink: true }}
              fullWidth
            />
            <TextField
              label="Next Payment Date"
              name="nextPaymentDate"
              type="date"
              value={formik.values.nextPaymentDate}
              onChange={formik.handleChange}
              error={formik.touched.nextPaymentDate && Boolean(formik.errors.nextPaymentDate)}
              helperText={formik.touched.nextPaymentDate && formik.errors.nextPaymentDate}
              InputLabelProps={{ shrink: true }}
              fullWidth
            />
            <TextField
              select
              label="Payment Status"
              name="paymentStatus"
              value={formik.values.paymentStatus}
              onChange={formik.handleChange}
              error={formik.touched.paymentStatus && Boolean(formik.errors.paymentStatus)}
              helperText={formik.touched.paymentStatus && formik.errors.paymentStatus}
              fullWidth
            >
              <option value="PAID">Paid</option>
              <option value="UNPAID">Unpaid</option>
            </TextField>
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

export default AdminStudentCreation;
