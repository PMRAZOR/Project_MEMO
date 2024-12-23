// axiosAuth.js
import axios from "axios";

const authApi = axios.create({
  baseURL: "https://api.ssafy.me/members", // auth 전용 baseURL
  // timeout: 3000, // 인증 요청은 일반 요청보다 짧은 타임아웃 설정
  // headers: {
  //   "Content-Type": "application/json",
  // },
});

export default authApi;
