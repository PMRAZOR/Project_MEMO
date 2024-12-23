<template>


      <div class="content-area">
        <BoardForm
          v-if="board"
          :initial-data="board"
          :loading="loading"
          @submit="handleSubmit"
          @cancel="handleCancel"
        />
      </div>


    <v-snackbar 
      v-model="showSnackbar" 
      :color="snackbarColor" 
      location="top"
      timeout="3000"
    >
      {{ snackbarMessage }}
      <template #actions>
        <v-btn variant="text" @click="showSnackbar = false">닫기</v-btn>
      </template>
    </v-snackbar>

</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useStore } from 'vuex';
import BoardForm from '@/components/board/BoardForm.vue';

export default {
  name: 'BoardEditView',
  
  components: {
    BoardForm
  },

  setup() {
    const route = useRoute();
    const router = useRouter();
    const store = useStore();
    
    const board = ref(null);
    const loading = ref(false);
    const showSnackbar = ref(false);
    const snackbarMessage = ref('');
    const snackbarColor = ref('success');

    const loadBoard = async () => {
      try {
        loading.value = true;
        const response = await store.dispatch('board/fetchBoard', route.params.id);
        
        // 작성자 확인 - userId가 일치하지 않을 때 접근 제한
        if (store.state.auth.user?.id !== response.userId) {
          showSnackbar.value = true;
          snackbarColor.value = 'error';
          snackbarMessage.value = '게시글 수정 권한이 없습니다.';
          setTimeout(() => {
            router.push({ 
              name: 'BoardDetail', 
              params: { id: route.params.id } 
            });
          }, 1000);
          return;
        }
        
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

    const handleSubmit = async (formData) => {
      try {
        loading.value = true;
        await store.dispatch('board/updateBoard', {
          id: route.params.id,
          ...formData
        });
        showSnackbar.value = true;
        snackbarColor.value = 'success';
        snackbarMessage.value = '게시글이 성공적으로 수정되었습니다.';
        setTimeout(() => {
          router.push({ 
            name: 'BoardDetail', 
            params: { id: route.params.id } 
          });
        }, 1000);
      } catch (error) {
        showSnackbar.value = true;
        snackbarColor.value = 'error';
        snackbarMessage.value = '게시글 수정에 실패했습니다.';
      } finally {
        loading.value = false;
      }
    };

    const handleCancel = () => {
      router.push({ 
        name: 'BoardDetail', 
        params: { id: route.params.id } 
      });
    };

    return {
      board,
      loading,
      showSnackbar,
      snackbarMessage,
      snackbarColor,
      handleSubmit,
      handleCancel
    };
  }
};
</script>

<style scoped>
.board-edit-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  min-height: calc(100vh - 64px);
}

.board-edit-card {
  background-color: #2c2c2c;
  border-radius: 8px;
  overflow: hidden;
}

.edit-header {
  padding: 24px 32px;
  background-color: #383838;
  border-bottom: 1px solid #424242;
}

.edit-title {
  color: white;
  font-size: 1.5rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
}

.content-area {
  padding: 32px;
  min-height: 300px;
  color: #e0e0e0;
}

/* Form specific styles */
:deep(.form-group) {
  margin-bottom: 24px;
}

:deep(.form-label) {
  color: #e0e0e0;
  font-size: 0.9rem;
  margin-bottom: 8px;
  display: block;
}

/* Dark theme input overrides */
:deep(.v-text-field) {
  background-color: #2c2c2c;
}

:deep(.v-text-field .v-field__input) {
  color: #e0e0e0 !important;
}

:deep(.v-text-field .v-field__outline) {
  border-color: #424242;
}

:deep(.v-text-field--focused .v-field__outline) {
  border-color: var(--v-primary-base);
}

/* Button styles */
:deep(.v-btn) {
  text-transform: none;
  font-weight: 500;
}

/* Snackbar styles */
.v-snackbar {
  margin-top: 64px;
}

/* Responsive styles */
@media (max-width: 960px) {
  .board-edit-container {
    padding: 16px;
  }

  .edit-header {
    padding: 20px 24px;
  }

  .edit-title {
    font-size: 1.25rem;
  }

  .content-area {
    padding: 24px;
  }
}

@media (max-width: 600px) {
  .board-edit-container {
    padding: 12px;
  }

  .edit-header {
    padding: 16px 20px;
  }

  .content-area {
    padding: 20px;
  }

  :deep(.button-group) {
    flex-direction: column;
  }

  :deep(.v-btn) {
    width: 100%;
    margin-bottom: 8px;
  }
}

/* Dark mode specific adjustments */
:deep(.v-field) {
  border-radius: 8px;
  background-color: #2c2c2c !important;
}

:deep(.v-field__input) {
  color: white !important;
  font-size: 1rem;
  padding: 12px 16px;
}

:deep(.v-label) {
  color: #9e9e9e;
}

:deep(.v-textarea .v-field__input) {
  line-height: 1.6;
  padding: 16px;
}
</style>