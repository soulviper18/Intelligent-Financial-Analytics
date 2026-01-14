const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const API_ENDPOINTS = {
  health: `${API_BASE_URL}/health`,
  auth: `${API_BASE_URL}/auth`,
  users: `${API_BASE_URL}/users`,
};

export default API_BASE_URL;
