import React from "react";
import {
  Box,
  Button,
  TextField,
  MenuItem,
  Typography,
  Container,
  Paper,
} from "@mui/material";
import { useFormik } from "formik";
import * as Yup from "yup";
import { useNavigate } from "react-router-dom";
import { login } from "../api/auth";
import { jwtDecode } from "jwt-decode";

const LoginPage = () => {
  const navigate = useNavigate();

  const formik = useFormik({
    initialValues: { fullName: "", password: "", role: "" },
    validationSchema: Yup.object({
      fullName: Yup.string().required("Full name is required"),
      password: Yup.string().min(6).required("Password is required"),
      role: Yup.string().oneOf(["ADMIN", "MENTOR"]).required("Role is required"),
    }),
    onSubmit: async (values, helpers) => {
      try {
        const { data } = await login(values); // { token }
        const { token } = data;

        localStorage.setItem("token", token);
        const { role } = jwtDecode(token);
        
        role === "ADMIN"
          ? navigate("/admin/dashboard", { replace: true })
          : navigate("/mentor/dashboard", { replace: true });
      } catch (err) {
        helpers.setSubmitting(false);
        helpers.setErrors({
          password: "Login failed. Check credentials and role.",
        });
      }
    },
  });

  return (
    <Container maxWidth="sm">
      <Paper elevation={3} sx={{ p: 4, mt: 8 }}>
        <Typography variant="h4" gutterBottom>
          Coachify Login
        </Typography>

        <form onSubmit={formik.handleSubmit} noValidate>
          <TextField
            fullWidth
            margin="normal"
            label="Full Name"
            {...formik.getFieldProps("fullName")}
            error={formik.touched.fullName && Boolean(formik.errors.fullName)}
            helperText={formik.touched.fullName && formik.errors.fullName}
          />

          <TextField
            fullWidth
            margin="normal"
            label="Password"
            type="password"
            {...formik.getFieldProps("password")}
            error={formik.touched.password && Boolean(formik.errors.password)}
            helperText={formik.touched.password && formik.errors.password}
          />

          <TextField
            select
            fullWidth
            margin="normal"
            label="Select Role"
            {...formik.getFieldProps("role")}
            error={formik.touched.role && Boolean(formik.errors.role)}
            helperText={formik.touched.role && formik.errors.role}
          >
            <MenuItem value="ADMIN">Admin</MenuItem>
            <MenuItem value="MENTOR">Mentor</MenuItem>
          </TextField>

          <Box mt={2}>
            <Button
              fullWidth
              variant="contained"
              type="submit"
              disabled={formik.isSubmitting}
            >
              Log&nbsp;In
            </Button>
          </Box>
        </form>
      </Paper>
    </Container>
  );
};

export default LoginPage;
