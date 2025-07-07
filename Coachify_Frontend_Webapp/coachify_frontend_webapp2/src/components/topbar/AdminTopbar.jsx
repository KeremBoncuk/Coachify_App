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
  import Brightness4Icon from '@mui/icons-material/Brightness4';
  import Brightness7Icon from '@mui/icons-material/Brightness7';
  import { useNavigate } from "react-router-dom";
  import { logout } from "../../auth/logout";
  import { useThemeContext } from '../../ThemeContext';

  const AdminTopbar = ({ onMenuClick }) => {
    const theme   = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("md"));
    const navigate = useNavigate();
    const { toggleColorMode } = useThemeContext();

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
            <IconButton sx={{ ml: 1 }} onClick={toggleColorMode} color="inherit">
              {theme.palette.mode === 'dark' ? <Brightness7Icon /> : <Brightness4Icon />}
            </IconButton>
            <Button color="inherit" onClick={handleLogout}>
              Logout
            </Button>
          </Box>
        </Toolbar>
      </AppBar>
    );
  };

  export default AdminTopbar;
