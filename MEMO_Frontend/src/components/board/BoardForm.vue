<template>
  <div class="board-form-container">
    <v-card class="board-form">
      <!-- 헤더 -->
      <div class="form-header">
        <h2 class="form-title">
          <v-icon icon="mdi-pencil" class="mr-2" />
          {{ initialData?.id ? '게시글 수정' : '새 게시글 작성' }}
        </h2>
      </div>

      <!-- 폼 -->
      <v-form ref="form" @submit.prevent="handleSubmit" class="form-content">
        <div class="input-group">
          <label class="input-label">제목</label>
          <v-text-field
            v-model="formData.title"
            placeholder="제목을 입력하세요"
            :rules="titleRules"
            required
            variant="outlined"
            bg-color="#2c2c2c"
            color="white"
            density="comfortable"
            hide-details="auto"
            class="dark-input"
          />
        </div>

        <div class="input-group">
          <label class="input-label">내용</label>
          <v-textarea
            v-model="formData.content"
            placeholder="내용을 입력하세요"
            :rules="contentRules"
            required
            variant="outlined"
            :rows="15"
            auto-grow
            bg-color="#2c2c2c"
            color="white"
            density="comfortable"
            hide-details="auto"
            class="dark-input content-textarea"
          />
        </div>

        <!-- 버튼 그룹 -->
        <div class="button-group">
          <v-btn
            color="grey-darken-3"
            variant="outlined"
            size="large"
            :disabled="loading"
            @click="handleCancel"
            prepend-icon="mdi-close"
            class="action-button"
          >
            취소
          </v-btn>
          <v-btn
            color="primary"
            variant="elevated"
            size="large"
            :loading="loading"
            @click="handleSubmit"
            prepend-icon="mdi-check"
            class="action-button"
          >
            {{ submitButtonText }}
          </v-btn>
        </div>
      </v-form>
    </v-card>
  </div>
</template>

<script>
// script 부분은 이전과 동일
import { ref, computed, onMounted } from 'vue';

export default {
  name: 'BoardForm',

  props: {
    initialData: {
      type: Object,
      default: () => ({
        title: '',
        content: ''
      })
    },
    loading: {
      type: Boolean,
      default: false
    }
  },

  emits: ['submit', 'cancel'],

  setup(props, { emit }) {
    const form = ref(null);
    
    const formData = ref({
      title: '',
      content: ''
    });

    const titleRules = [
      v => !!v || '제목을 입력해주세요.',
      v => v.length <= 100 || '제목은 100자를 초과할 수 없습니다.'
    ];

    const contentRules = [
      v => !!v || '내용을 입력해주세요.',
      v => v.length <= 2000 || '내용은 2000자를 초과할 수 없습니다.'
    ];

    const submitButtonText = computed(() => {
      return props.initialData?.id ? '수정' : '작성';
    });

    onMounted(() => {
      if (props.initialData) {
        formData.value = { ...props.initialData };
      }
    });

    const handleSubmit = async () => {
      const { valid } = await form.value.validate();
      
      if (valid) {
        emit('submit', {
          title: formData.value.title.trim(),
          content: formData.value.content.trim()
        });
      }
    };

    const handleCancel = () => {
      emit('cancel');
    };

    return {
      form,
      formData,
      titleRules,
      contentRules,
      submitButtonText,
      handleSubmit,
      handleCancel
    };
  }
};
</script>

<style scoped>
.board-form-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  min-height: calc(100vh - 64px);
}

.board-form {
  background-color: #2c2c2c;
  border-radius: 8px;
  overflow: hidden;
}

.form-header {
  padding: 24px 32px;
  background-color: #383838;
  border-bottom: 1px solid #424242;
}

.form-title {
  color: white;
  font-size: 1.5rem;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.form-content {
  padding: 32px;
}

.input-group {
  margin-bottom: 24px;
}

.input-label {
  display: block;
  color: #e0e0e0;
  font-size: 1rem;
  font-weight: 500;
  margin-bottom: 8px;
}

.dark-input {
  :deep(.v-field) {
    border-radius: 8px;
    background-color: #2c2c2c !important;
    border: 1px solid #424242;
  }

  :deep(.v-field__input) {
    color: white !important;
    font-size: 1rem;
    padding: 12px 16px;
  }

  :deep(.v-field__outline) {
    display: none;
  }

  :deep(.v-label) {
    color: #9e9e9e;
  }
}

.content-textarea {
  :deep(.v-field__input) {
    line-height: 1.6;
    padding: 16px;
  }
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
}

.action-button {
  min-width: 120px;
  height: 48px;
  font-size: 1rem;
  font-weight: 500;
}

/* Responsive styles */
@media (max-width: 960px) {
  .board-form-container {
    padding: 16px;
  }

  .form-header {
    padding: 20px 24px;
  }

  .form-title {
    font-size: 1.25rem;
  }

  .form-content {
    padding: 24px;
  }
}

@media (max-width: 600px) {
  .board-form-container {
    padding: 12px;
  }

  .form-header {
    padding: 16px 20px;
  }

  .form-content {
    padding: 20px;
  }

  .button-group {
    flex-direction: column;
    gap: 8px;
  }

  .action-button {
    width: 100%;
    height: 44px;
  }
}

/* Dark theme specific overrides */
:deep(.v-btn) {
  text-transform: none;
}

:deep(.v-field--focused) {
  .v-field__outline {
    color: var(--v-primary-base) !important;
  }
}
</style>