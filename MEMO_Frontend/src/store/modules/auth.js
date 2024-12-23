// store/modules/auth.js
import authApi from "@/api/authApi";
import { jwtDecode } from "jwt-decode";

export default {
  namespaced: true,
  state: () => ({
    isLoggedIn: false,
    user: {
      id: null,
      userId: null,
      nickname: null,
      email: null,
    },
    token: null,
  }),

  mutations: {
    SET_LOGGED_IN(state, value) {
      state.isLoggedIn = value;
    },
    SET_USER(state, decodedUser) {

      state.user = decodedUser
        ? {
            id: decodedUser.id,
            userId: decodedUser.userId,
            nickname: decodedUser.name,
            email: decodedUser.email,
          }
        : {
            id: null,
            userId: null,
            nickname: null,
            email: null,
          };
    },
    SET_TOKEN(state, token) {
      state.token = token;
    },
  },

  actions: {
    async login({ commit }, credentials) {
      try {
        const response = await authApi.post("/login", credentials);
        const token = response.data;
        const decodedUser = jwtDecode(token);
        
        commit("SET_TOKEN", token);
        commit("SET_USER", decodedUser);
        commit("SET_LOGGED_IN", true);
        return { success: true };
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.message || "로그인에 실패했습니다.",
        };
      }
    },

    async register({ commit }, registData) {
      try {
        const response = await authApi.post("/join", registData);
        const token = response.data;
        const decodedUser = jwtDecode(token);

        commit("SET_TOKEN", token);
        commit("SET_USER", decodedUser);
        commit("SET_LOGGED_IN", true);
        return { success: true };
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.message || "회원가입에 실패했습니다.",
        };
      }
    },

    logout({ commit }) {
      commit("SET_TOKEN", null);
      commit("SET_USER", null);
      commit("SET_LOGGED_IN", false);
    },

    async updateProfile({ commit }, { id, formData }) {
      try {
        await authApi.patch(`/${id}/profile`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        
        const response = await authApi.get(`/${id}`);
        commit("SET_USER", response.data);
        return { success: true };
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.message || "프로필 수정에 실패했습니다."
        };
      }
    },

    async deleteMember({ commit }, id) {
      try {
        await authApi.delete(`/${id}`);
        commit("SET_TOKEN", null);
        commit("SET_USER", null);
        commit("SET_LOGGED_IN", false);
        return { success: true };
      } catch (error) {
        return {
          success: false,
          error: error.response?.data?.message || "회원 탈퇴에 실패했습니다."
        };
      }
    },
  },

  getters: {
    isLoggedIn: (state) => state.isLoggedIn,
    user: (state) => state.user,
    token: (state) => state.token,
    id: (state) => state.user?.id || null,
  },
};