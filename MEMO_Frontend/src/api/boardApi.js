import instance from './axios';  // 설정된 axios 인스턴스 import

const boardApi = {
  // 게시글 목록 조회
  getBoards({ page, size, searchType, searchKeyword, sortField = 'createdDate', sortDirection = 'DESC' }) {
    return instance.get('/boards', {
      params: {
        page,
        size,
        searchType,
        searchKeyword,
        sortField,
        sortDirection
      }
    });
  },

  // 게시글 상세 조회
  getBoard(id) {
    return instance.get(`/boards/${id}`);
  },

  // 게시글 생성
  createBoard(boardData) {
    return instance.post('/boards', boardData);
  },

  // 게시글 수정
  updateBoard(id, boardData) {
    return instance.put(`/boards/${id}`, boardData);
  },

  // 게시글 삭제
  deleteBoard(id) {
    return instance.delete(`/boards/${id}`);
  }
};

export default boardApi;