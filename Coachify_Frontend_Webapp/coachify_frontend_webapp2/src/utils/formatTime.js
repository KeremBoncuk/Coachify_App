/**
 * Tiny helper: returns "HH:MM AM/PM" (no seconds)
 */
export const formatTime = (isoString) =>
  new Date(isoString).toLocaleTimeString(undefined, {
    hour: "2-digit",
    minute: "2-digit",
  });
