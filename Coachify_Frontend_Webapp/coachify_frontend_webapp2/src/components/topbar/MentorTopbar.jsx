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
import { useContext } from "react";
import { ThemeContext } from "../../theme/ThemeContext";
import { Brightness4, Brightness7 } from "@mui/icons-material";

const MentorTopbar = ({ onMenuClick }) => {
  const theme    = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("md"));
  const navigate = useNavigate();
  const { toggleTheme, themeMode } = useContext(ThemeContext);


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
          Mentor Panel – Coachify
        </Typography>
        <Box>
          <IconButton onClick={toggleTheme} color="inherit">
            {themeMode === 'light' ? <Brightness4 /> : <Brightness7 />}
          </IconButton>
          <Button color="inherit" onClick={handleLogout}>
            Logout
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default MentorTopbar;
