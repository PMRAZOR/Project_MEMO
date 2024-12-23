<template>
  <div class="board-detail-container">
    <v-card class="board-detail-card">
      <!-- 헤더 영역 -->
      <div class="board-header">
        <h2 class="board-title">{{ board?.title }}</h2>
        <div class="board-meta">
          <div class="meta-left">
            <span class="author">
              <v-icon icon="mdi-account" size="small" />
              {{ board?.nickname }}
            </span>
            <span class="date">
              <v-icon icon="mdi-calendar" size="small" />
              {{ formatDate(board?.createdDate) }}
            </span>
            <span class="views">
              <v-icon icon="mdi-eye" size="small" />
              {{ board?.viewCount }}
            </span>
          </div>
          <div class="action-buttons" v-if="isAuthor">
            <v-btn
              color="primary"
              variant="text"
              @click="handleEdit"
              prepend-icon="mdi-pencil"
            >
              수정
            </v-btn>
            <v-btn
              color="error"
              variant="text"
              @click="confirmDelete"
              prepend-icon="mdi-delete"
            >
              삭제
            </v-btn>
          </div>
        </div>
      </div>

      <v-divider class="divider" />

      <!-- 본문 영역 -->
      <div class="content-area">
        {{ board?.content }}
      </div>

      <v-divider class="divider" />

      <!-- 하단 버튼 영역 -->
      <div class="board-footer">
        <v-btn
          color="white"
          variant="outlined"
          @click="$router.push({ name: 'BoardList' })"
          prepend-icon="mdi-arrow-left"
        >
          목록으로
        </v-btn>
      </div>
    </v-card>

    <!-- 삭제 확인 다이얼로그 -->
    <v-dialog v-model="showDeleteDialog" max-width="400">
      <v-card class="dialog-card">
        <v-card-title class="dialog-title">
          <v-icon icon="mdi-alert-circle" color="error" class="mr-2" />
          게시글 삭제
        </v-card-title>
        <v-card-text class="dialog-content">
          정말로 이 게시글을 삭제하시겠습니까?
        </v-card-text>
        <v-card-actions class="dialog-actions">
          <v-spacer />
          <v-btn
            color="grey-darken-3"
            variant="outlined"
            @click="showDeleteDialog = false"
          >
            취소
          </v-btn>
          <v-btn
            color="error"
            variant="elevated"
            @click="handleDelete"
          >
            삭제
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar -->
    <v-snackbar
      v-model="showSnackbar"
      :color="snackbarColor"
      location="top"
      timeout="3000"
    >
      {{ snackbarMessage }}
      <template #actions>
        <v-btn variant="text" @click="showSnackbar = false">
          닫기
        </v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useStore } from 'vuex';

export default {
  name: 'BoardDetailView',

  setup() {
    const route = useRoute();
    const router = useRouter();
    const store = useStore();
    
    const board = ref(null);
    const loading = ref(false);
    const showDeleteDialog = ref(false);
    const showSnackbar = ref(false);
    const snackbarMessage = ref('');
    const snackbarColor = ref('success');

    const isAuthor = computed(() => {

      const userId = store.state.auth.user?.id;
      const boardUserId = board.value?.userId;

      // 둘 다 존재할 때만 비교
      return userId && boardUserId && userId === boardUserId;
    });

    const loadBoard = async () => {
      try {
        loading.value = true;
        const response = await store.dispatch('board/fetchBoard', route.params.id);
        board.value = response;
      } catch (error) {
        showSnackbar.value = true;
        snackbarColor.value = 'error';
        snackbarMessage.value = '게시글을 불러오는데 실패했습니다.';
        router.push({ name: 'BoardList' });
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      loadBoard();
    });

    const formatDate = (date) => {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    };

    const handleEdit = () => {
      router.push({ 
        name: 'BoardEdit', 
        params: { id: board.value.id } 
      });
    };

    const confirmDelete = () => {
      showDeleteDialog.value = true;
    };

    const handleDelete = async () => {
      try {
        await store.dispatch('board/deleteBoard', board.value.id);
        showSnackbar.value = true;
        snackbarColor.value = 'success';
        snackbarMessage.value = '게시글이 성공적으로 삭제되었습니다.';
        setTimeout(() => {
          router.push({ name: 'BoardList' });
        }, 1000);
      } catch (error) {
        showSnackbar.value = true;
        snackbarColor.value = 'error';
        snackbarMessage.value = '게시글 삭제에 실패했습니다.';
      } finally {
        showDeleteDialog.value = false;
      }
    };

    return {
      board,
      loading,
      showDeleteDialog,
      showSnackbar,
      snackbarMessage,
      snackbarColor,
      isAuthor,
      formatDate,
      handleEdit,
      confirmDelete,
      handleDelete
    };
  }
};
</script>

<style scoped>
.board-detail-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  min-height: calc(100vh - 64px);
}

.board-detail-card {
  background-color: #2c2c2c;
  border-radius: 8px;
  overflow: hidden;
}

.board-header {
  padding: 24px 32px;
  background-color: #383838;
}

.board-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: white;
  margin-bottom: 16px;
  line-height: 1.4;
}

.board-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #9e9e9e;
  font-size: 0.9rem;
}

.meta-left {
  display: flex;
  gap: 24px;
  align-items: center;
}

.meta-left span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.divider {
  border-color: #424242;
}

.content-area {
  padding: 32px;
  min-height: 300px;
  line-height: 1.8;
  color: #e0e0e0;
  white-space: pre-wrap;
  word-break: break-word;
}

.board-footer {
  padding: 24px 32px;
  display: flex;
  justify-content: flex-start;
}

/* Dialog styles */
.dialog-card {
  background-color: #2c2c2c;
}

.dialog-title {
  padding: 20px 24px;
  color: white;
  font-size: 1.25rem;
  border-bottom: 1px solid #424242;
}

.dialog-content {
  padding: 24px;
  color: #e0e0e0;
}

.dialog-actions {
  padding: 16px 24px;
  gap: 12px;
}

/* Responsive styles */
@media (max-width: 960px) {
  .board-detail-container {
    padding: 16px;
  }

  .board-header {
    padding: 20px 24px;
  }

  .board-title {
    font-size: 1.25rem;
  }

  .content-area {
    padding: 24px;
  }
}

@media (max-width: 600px) {
  .board-detail-container {
    padding: 12px;
  }

  .board-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .meta-left {
    flex-wrap: wrap;
    gap: 16px;
  }

  .action-buttons {
    width: 100%;
    justify-content: flex-end;
  }

  .board-footer {
    padding: 16px 20px;
  }
}
</style>