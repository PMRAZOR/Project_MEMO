<template>
  <div class="board-list-container">
    <!-- Search Component -->
    <BoardSearch 
      :search-type="searchType"
      :search-keyword="searchKeyword"
      @search="$emit('search', $event)"
    />

    <!-- Custom Table -->
    <v-card class="board-table-card">
      <div class="table-responsive">
        <table class="board-table">
          <thead>
            <tr>
              <th 
                v-for="header in headers" 
                :key="header.key"
                :style="{ width: header.width }"
              >
                <div class="header-content">
                  {{ header.title }}
                </div>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading" class="loading-row">
              <td :colspan="headers.length">
                <div class="loading-content">
                  <v-progress-circular
                    indeterminate
                    color="primary"
                    size="32"
                  />
                  <span class="loading-text">로딩중...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="!boards.length" class="empty-row">
              <td :colspan="headers.length">
                <div class="empty-content">
                  <v-icon icon="mdi-alert-circle-outline" size="32" color="grey" />
                  <span>게시글이 없습니다.</span>
                </div>
              </td>
            </tr>
            <tr
              v-for="board in boards"
              :key="board.id"
              class="board-row"
              @click="$emit('view-board', board.id)"
            >
              <td class="text-center">{{ board.id }}</td>
              <td>
  <div class="title-cell">
    <span class="title-text">{{ board.title }}</span>
    <span v-if="board.commentCount > 0" class="comment-count">
      [{{ board.commentCount }}]
    </span>
    <span v-if="isNewPost(board.createdDate)" class="new-badge">
      NEW
    </span>
  </div>
</td>
              <td class="text-center">{{ board.nickname }}</td>
              <td class="text-center">{{ formatDate(board.createdDate) }}</td>
              <td class="text-center">{{ formatNumber(board.viewCount) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </v-card>

    <!-- Pagination -->
    <div class="pagination-wrapper">
      <v-pagination
        :model-value="currentPage"
        :length="totalPages"
        :total-visible="7"
        color="dark"
        @update:model-value="$emit('page-change', $event)"
      />
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import BoardSearch from './BoardSearch.vue';

export default defineComponent({
  name: 'BoardList',
  
  components: {
    BoardSearch,
  },

  props: {
    boards: {
      type: Array,
      required: true,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    currentPage: {
      type: Number,
      required: true,
    },
    totalPages: {
      type: Number,
      required: true,
    },
    searchType: {
      type: String,
      default: null,
    },
    searchKeyword: {
      type: String,
      default: '',
    },
  },

  emits: ['view-board', 'page-change', 'search'],

  setup() {
    const headers = [
      { 
        title: '번호',
        key: 'id',
        width: '80px'
      },
      { 
        title: '제목',
        key: 'title',
        width: 'auto'
      },
      { 
        title: '작성자',
        key: 'author',
        width: '120px'
      },
      { 
        title: '작성일',
        key: 'createdDate',
        width: '120px'
      },
      { 
        title: '조회수',
        key: 'viewCount',
        width: '80px'
      }
    ];

    const formatDate = (date) => {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    };

    const formatNumber = (num) => {
      return num.toLocaleString('ko-KR');
    };

    const isNewPost = (date) => {
      const postDate = new Date(date);
      const now = new Date();
      const diffTime = Math.abs(now - postDate);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return diffDays <= 3;
    };

    return {
      headers,
      formatDate,
      formatNumber,
      isNewPost,
    };
  },
});
</script>

<style scoped>
.board-list-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.board-table-card {
  background-color: #2c2c2c;
  border-radius: 8px;
  overflow: hidden;
}

.table-responsive {
  width: 100%;
  overflow-x: auto;
}

.board-table {
  width: 100%;
  border-collapse: collapse;
  color: white;
  table-layout: fixed; /* 추가: 테이블 레이아웃을 고정 */
}

.board-table th {
  background-color: #383838;
  padding: 16px;
  font-weight: 600;
  text-align: left;
  border-bottom: 2px solid #505050;
  white-space: nowrap;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.board-table td {
  padding: 16px;
  border-bottom: 1px solid #424242;
}

.board-row {
  cursor: pointer;
  transition: background-color 0.2s;
}

.board-row:hover {
  background-color: #383838;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  max-width: 100%; /* 최대 너비 설정 */
  overflow: hidden;
}
.title-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.board-table th:nth-child(1),
.board-table td:nth-child(1) {
  width: 8%; /* 번호 열 */
}

.board-table th:nth-child(2),
.board-table td:nth-child(2) {
  width: 50%; /* 제목 열 */
}

.board-table th:nth-child(3),
.board-table td:nth-child(3) {
  width: 15%; /* 작성자 열 */
}

.board-table th:nth-child(4),
.board-table td:nth-child(4) {
  width: 17%; /* 작성일 열 */
}

.board-table th:nth-child(5),
.board-table td:nth-child(5) {
  width: 10%; /* 조회수 열 */
}


.comment-count {
  color: #4CAF50;
  font-size: 0.9em;
  font-weight: 500;
}

.new-badge {
  background-color: #f44336;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.75em;
  font-weight: 600;
}

.loading-content,
.empty-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px 0;
  color: #9e9e9e;
}

.loading-text {
  font-size: 0.9em;
  color: #9e9e9e;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

:deep(.v-pagination) {
  margin-bottom: 20px;
  .v-btn {
    background-color: #383838;
    color: white;
    min-width: 32px;
    height: 32px;
    margin: 0 4px;
    font-size: 0.875rem;

    &--active {
      background-color: #1976D2;
    }

    &:not(.v-btn--active):hover {
      background-color: #424242;
    }
  }
}

.text-center {
  text-align: center;
}

/* Responsive styles */
@media (max-width: 960px) {
  .board-table th,
  .board-table td {
    padding: 12px;
  }

  .title-cell {
    min-width: 200px;
  }
}

@media (max-width: 600px) {
  .board-list-container {
    gap: 16px;
  }

  .pagination-wrapper {
    margin-top: 16px;
  }
}

@media (max-width: 768px) {
  .board-table th:nth-child(4),
  .board-table td:nth-child(4),
  .board-table th:nth-child(5),
  .board-table td:nth-child(5) {
    display: none;
  }

  /* 768px 이하에서의 컬럼 비율 조정 */
  .board-table th:nth-child(1),
  .board-table td:nth-child(1) {
    width: 15%;
  }

  .board-table th:nth-child(2),
  .board-table td:nth-child(2) {
    width: 60%;
  }

  .board-table th:nth-child(3),
  .board-table td:nth-child(3) {
    width: 25%;
  }
}

@media (max-width: 475px) {
  .board-table th:nth-child(1),
  .board-table td:nth-child(1),
  .board-table th:nth-child(3),
  .board-table td:nth-child(3) {
    display: none;
  }

  .board-table th:nth-child(5),
  .board-table td:nth-child(5) {
    display: table-cell;
  }


  .board-table th:nth-child(2),
  .board-table td:nth-child(2) {
    width: 80%;
  }

  .board-table {
    min-width: auto;
  }

  .title-cell {
    min-width: 150px;
  }
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  max-width: 100%;
}

/* 제목 텍스트를 위한 새로운 클래스 추가 */
.title-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}
</style>