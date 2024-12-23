<template>
  <div class="board-view-container">
    <v-responsive class="mx-auto pt-4" width="100%">
      <!-- Header -->
      <div class="board-header mb-6">
        <h2 class="text-h4 font-weight-bold">게시판</h2>
        <v-btn
          class="create-button"
          color="white"
          variant="elevated"
          @click="navigateToCreate"
          :loading="loading"
          prepend-icon="mdi-plus"
        >
          글쓰기
        </v-btn>
      </div>

      <!-- Board List Component -->
      <BoardList
        :boards="boards"
        :loading="loading"
        :current-page="currentPage"
        :total-pages="totalPages"
        :sort-by="sortBy"
        :sort-direction="sortDirection"
        :search-type="searchType"
        :search-keyword="searchKeyword"
        @view-board="navigateToDetail"
        @page-change="handlePageChange"
        @sort-change="handleSort"
        @search="handleSearch"
      />

      <!-- Loading Overlay -->
      <v-overlay
        :model-value="loading"
        class="align-center justify-center"
        persistent
      >
        <v-progress-circular
          color="primary"
          indeterminate
          size="64"
        ></v-progress-circular>
      </v-overlay>
    </v-responsive>

    <!-- Snackbar for notifications -->
    <v-snackbar
      v-model="showSnackbar"
      :color="snackbarColor"
      :timeout="3000"
      location="top"
    >
      {{ snackbarMessage }}
      <template #actions>
        <v-btn
          color="white"
          variant="text"
          @click="showSnackbar = false"
        >
          닫기
        </v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useStore } from 'vuex';
import BoardList from '@/components/board/BoardList.vue';

export default defineComponent({
  name: 'BoardListView',
  
  components: {
    BoardList
  },

  setup() {
    const router = useRouter();
    const route = useRoute();
    const store = useStore();

    // State
    const boards = ref([]);
    const loading = ref(false);
    const currentPage = ref(1);
    const totalPages = ref(0);
    const sortBy = ref('createdDate');
    const sortDirection = ref('DESC');
    const searchType = ref(null);
    const searchKeyword = ref('');
    const showSnackbar = ref(false);
    const snackbarMessage = ref('');
    const snackbarColor = ref('success');

    // Query params handling
    const initializeFromQuery = () => {
      const query = route.query;
      currentPage.value = Number(query.page) || 1;
      sortBy.value = query.sortBy || 'createdDate';
      sortDirection.value = query.sortDirection || 'DESC';
      searchType.value = query.searchType || null;
      searchKeyword.value = query.searchKeyword || '';
    };

    const updateQueryParams = () => {
      const query = {
        page: currentPage.value,
        sortBy: sortBy.value,
        sortDirection: sortDirection.value
      };

      if (searchType.value) {
        query.searchType = searchType.value;
      }
      if (searchKeyword.value) {
        query.searchKeyword = searchKeyword.value;
      }

      router.replace({ query });
    };

    // Methods
    const showError = (message) => {
      snackbarMessage.value = message;
      snackbarColor.value = 'error';
      showSnackbar.value = true;
    };

    const loadBoards = async () => {
      if (loading.value) return;
      
      try {
        loading.value = true;
        const response = await store.dispatch('board/fetchBoards', {
          page: currentPage.value - 1,
          size: 10,
          searchType: searchType.value,
          searchKeyword: searchKeyword.value,
          sortBy: sortBy.value,
          sortDirection: sortDirection.value,
        });
        
        if (response && response.content) {
          boards.value = response.content;
          totalPages.value = response.totalPages;
          updateQueryParams();
        }
      } catch (error) {
        console.error('Error loading boards:', error);
        showError('게시글 목록을 불러오는데 실패했습니다.');
      } finally {
        loading.value = false;
      }
    };

    const handleSearch = ({ searchType: newSearchType, searchKeyword: newSearchKeyword }) => {
      searchType.value = newSearchType;
      searchKeyword.value = newSearchKeyword;
      currentPage.value = 1;
      loadBoards();
    };

    const handleSort = ({ sortBy: newSortBy, sortDirection: newSortDirection }) => {
      sortBy.value = newSortBy;
      sortDirection.value = newSortDirection;
      loadBoards();
    };

    const handlePageChange = (newPage) => {
      if (currentPage.value !== newPage) {
        currentPage.value = newPage;
        loadBoards();
      }
    };

    const navigateToCreate = () => {
      router.push({ name: 'BoardCreate' });
    };

    const navigateToDetail = (id) => {
      router.push({ name: 'BoardDetail', params: { id: id.toString() } });
    };

    // Lifecycle hooks
    onMounted(() => {
      initializeFromQuery();
      loadBoards();
    });

    // Watch for route changes
    watch(
      () => route.query,
      () => {
        initializeFromQuery();
        loadBoards();
      },
      { deep: true }
    );

    return {
      // State
      boards,
      loading,
      currentPage,
      totalPages,
      sortBy,
      sortDirection,
      searchType,
      searchKeyword,
      showSnackbar,
      snackbarMessage,
      snackbarColor,

      // Methods
      handleSearch,
      handleSort,
      handlePageChange,
      navigateToCreate,
      navigateToDetail,
    };
  }
});
</script>

<style scoped>
.board-view-container {
  position: relative;
  min-height: calc(100vh - 64px);
  display: flex;
  flex-direction: column;
  width: 100%;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  background-color: #1e1e1e;
  border-radius: 5px;
}

.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  color: white;
  padding: 0 16px;
  margin-bottom: 20px;
}

.board-header h2 {
  text-align: left;
}

.create-button {
  min-width: 120px;
  height: 40px;
  font-weight: 500;
  letter-spacing: 0.5px;
  background-color: #2c2c2c !important;
  color: white !important;
}

.create-button:hover {
  background-color: #505050 !important;
}

/* Dark theme overrides */
:deep(.v-btn) {
  text-transform: none;
}

:deep(.v-snackbar__content) {
  font-size: 14px;
}

/* Pagination styles */
:deep(.v-pagination__item) {
  color: white !important;
}

:deep(.v-pagination__item--is-active) {
  background-color: rgba(255, 255, 255, 0.2) !important;
}

/* Table styles */
:deep(.v-data-table) {
  background-color: #2c2c2c !important;
  color: white !important;
}

:deep(.v-data-table__thead) {
  background-color: #1e1e1e !important;
}

:deep(.v-data-table__th) {
  color: white !important;
}

:deep(.v-data-table__tr:hover) {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

@media (max-width: 600px) {
  .board-view-container {
    padding: 16px;
  }

  .board-header {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    margin-bottom: 16px;
  }

  .board-header h2 {
    font-size: 1.5rem;
    text-align: center;
  }

  .create-button {
    width: 100%;
  }
}
</style>