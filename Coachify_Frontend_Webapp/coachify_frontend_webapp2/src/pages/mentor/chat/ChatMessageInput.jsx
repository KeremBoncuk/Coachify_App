import { IconButton, TextField, Paper } from "@mui/material";
import { Send } from "@mui/icons-material";
import { useState } from "react";

/**
 * Props:
 *  • disabled         boolean
 *  • onSend(text)     Promise
 */
const ChatMessageInput = ({ onSend, disabled }) => {
  const [value, setValue] = useState("");

  const handleSend = async () => {
    if (!value.trim()) return;
    await onSend(value.trim());
    setValue("");
  };

  const onKeyPress = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <Paper
      component="form"
      onSubmit={(e) => {
        e.preventDefault();
        handleSend();
      }}
      sx={{ display: "flex", p: 1 }}
    >
      <TextField
        fullWidth
        size="small"
        placeholder="Type a message…"
        multiline
        maxRows={4}
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyPress={onKeyPress}
        disabled={disabled}
        variant="outlined"
        sx={{ mr: 1 }}
      />
      <IconButton
        color="primary"
        disabled={disabled || !value.trim()}
        onClick={handleSend}
      >
        <Send />
      </IconButton>
    </Paper>
  );
};

export default ChatMessageInput;
