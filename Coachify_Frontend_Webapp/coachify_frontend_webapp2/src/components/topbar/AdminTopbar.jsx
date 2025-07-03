  import {
    AppBar,
    Toolbar,
    Typography,
    Button,
    IconButton,
    Box,
    useMediaQuery,
    useTheme,
  } from "@mui/material";
  import MenuIcon from "@mui/icons-material/Menu";
  import { useNavigate } from "react-router-dom";
  import { logout } from "../../auth/logout";

  const AdminTopbar = ({ onMenuClick }) => {
    const theme   = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("md"));
    const navigate = useNavigate();

    const handleLogout = async () => {
      await logout(navigate);
    };

    return (
      <AppBar position="fixed" color="primary">
        <Toolbar>
          {isMobile && (
            <IconButton edge="start" color="inherit" onClick={onMenuClick} sx={{ mr: 2 }}>
              <MenuIcon />
            </IconButton>
          )}
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Admin Panel – Coachify
          </Typography>
          <Box>
            <Button color="inherit" onClick={handleLogout}>
              Logout
            </Button>
          </Box>
        </Toolbar>
      </AppBar>
    );
  };

  export default AdminTopbar;
