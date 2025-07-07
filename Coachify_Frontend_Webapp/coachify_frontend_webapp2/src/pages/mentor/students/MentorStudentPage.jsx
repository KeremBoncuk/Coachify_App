import React from "react";
import MentorStudentTable from "./MentorStudentTable";
import { Box, Typography } from "@mui/material";

const MentorStudentPage = () => (
  <Box sx={{ p: 2 }}>
    <Typography variant="h4" gutterBottom>
      My Students
    </Typography>

    {/* Read-only grid */}
    <MentorStudentTable />
  </Box>
);

export default MentorStudentPage;
