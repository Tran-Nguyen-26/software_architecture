import axios from "axios";

// axios instance for making requests
const axiosInstance = axios.create({
  baseURL: "http://localhost:8088",
});

// request interceptor for adding token
axiosInstance.interceptors.request.use((config) => {
  // add token to request headers
  config.headers["Authorization"] = localStorage.getItem("token");
  return config;

  // const token = localStorage.getItem("token")
  // if (token) {
  //   config.headers["Authorization"] = `Bearer ${token}`; // thêm prefix Bearer
  // }
  // return config;
});

export default axiosInstance;
