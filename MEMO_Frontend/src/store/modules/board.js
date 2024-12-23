import boardApi from '@/api/boardApi';

const state = {
  boards: [],
  totalPages: 0,
  currentBoard: null,
  loading: false,
  error: null
};

const getters = {
  getBoards: state => state.boards,
  getTotalPages: state => state.totalPages,
  getCurrentBoard: state => state.currentBoard,
  isLoading: state => state.loading,
  getError: state => state.error
};

const mutations = {
  SET_BOARDS(state, boards) {
    state.boards = boards;
  },
  SET_TOTAL_PAGES(state, totalPages) {
    state.totalPages = totalPages;
  },
  SET_CURRENT_BOARD(state, board) {
    state.currentBoard = board;
  },
  SET_LOADING(state, loading) {
    state.loading = loading;
  },
  SET_ERROR(state, error) {
    state.error = error;
  },
  CLEAR_ERROR(state) {
    state.error = null;
  }
};

const actions = {
  // 게시글 목록 조회
  async fetchBoards({ commit }, params) {
    try {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      
      const response = await boardApi.getBoards(params);
      commit('SET_BOARDS', response.data.content);
      commit('SET_TOTAL_PAGES', response.data.totalPages);
      
      return response.data;
    } catch (error) {
      commit('SET_ERROR', error.response?.data?.message || '게시글 목록을 불러오는데 실패했습니다.');
      throw error;
    } finally {
      commit('SET_LOADING', false);
    }
  },

  // 게시글 상세 조회
  async fetchBoard({ commit }, id) {
    try {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      
      const response = await boardApi.getBoard(id);
      commit('SET_CURRENT_BOARD', response.data);
      
      return response.data;
    } catch (error) {
      commit('SET_ERROR', error.response?.data?.message || '게시글을 불러오는데 실패했습니다.');
      throw error;
    } finally {
      commit('SET_LOADING', false);
    }
  },

  // 게시글 생성
  async createBoard({ commit }, boardData) {
    try {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      
      const response = await boardApi.createBoard(boardData);
      return response.data;
    } catch (error) {
      commit('SET_ERROR', error.response?.data?.message || '게시글 작성에 실패했습니다.');
      throw error;
    } finally {
      commit('SET_LOADING', false);
    }
  },

  // 게시글 수정
  async updateBoard({ commit }, { id, ...boardData }) {
    try {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      
      const response = await boardApi.updateBoard(id, boardData);
      commit('SET_CURRENT_BOARD', response.data);
      
      return response.data;
    } catch (error) {
      commit('SET_ERROR', error.response?.data?.message || '게시글 수정에 실패했습니다.');
      throw error;
    } finally {
      commit('SET_LOADING', false);
    }
  },

  // 게시글 삭제
  async deleteBoard({ commit }, id) {
    try {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      
      await boardApi.deleteBoard(id);
    } catch (error) {
      commit('SET_ERROR', error.response?.data?.message || '게시글 삭제에 실패했습니다.');
      throw error;
    } finally {
      commit('SET_LOADING', false);
    }
  }
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
};