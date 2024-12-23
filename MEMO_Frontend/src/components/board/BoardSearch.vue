<template>
  <v-card class="search-card mb-4" elevation="2">
    <v-card-text>
      <v-row align="center" no-gutters>
        <v-col cols="12" sm="3" class="pr-sm-2">
          <v-select
            v-model="searchTypeValue"
            :items="searchTypes"
            label="검색 유형"
            variant="outlined"
            density="comfortable"
            hide-details
            menu-icon="mdi-chevron-down"
          />
        </v-col>
        <v-col cols="12" sm="8" class="py-2 py-sm-0 px-sm-2">
          <v-text-field
            v-model="searchKeywordValue"
            label="검색어를 입력하세요"
            variant="outlined"
            density="comfortable"
            hide-details
            clearable
            @keyup.enter="handleSearch"
            prepend-inner-icon="mdi-magnify"
          />
        </v-col>
        <v-col cols="12" sm="1" class="pl-sm-2">
          <v-btn 
            block 
            color="white"
            height="48"
            @click="handleSearch"
            :disabled="!searchKeywordValue && searchTypeValue"
          >
            검색
          </v-btn>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script>
import { defineComponent, ref, watch } from 'vue';

export default defineComponent({
  name: 'BoardSearch',

  props: {
    searchType: {
      type: String,
      default: null
    },
    searchKeyword: {
      type: String,
      default: ''
    }
  },

  emits: ['search'],

  setup(props, { emit }) {
    const searchTypes = [
      { title: '전체', value: null },
      { title: '제목', value: 'TITLE' },
      { title: '내용', value: 'CONTENT' },
      { title: '작성자', value: 'AUTHOR' },
    ];

    const searchTypeValue = ref(props.searchType);
    const searchKeywordValue = ref(props.searchKeyword);

    watch(() => props.searchType, (newValue) => {
      searchTypeValue.value = newValue;
    });

    watch(() => props.searchKeyword, (newValue) => {
      searchKeywordValue.value = newValue;
    });

    const handleSearch = () => {
      emit('search', {
        searchType: searchTypeValue.value,
        searchKeyword: searchKeywordValue.value
      });
    };

    return {
      searchTypes,
      searchTypeValue,
      searchKeywordValue,
      handleSearch
    };
  }
});
</script>

<style scoped>
.search-card {
  background-color: #2c2c2c !important;
  border: 1px solid #424242;
}

:deep(.v-field) {
  border-color: #424242 !important;
  background-color: #383838 !important;
}

:deep(.v-field__input) {
  color: white !important;
}

:deep(.v-label) {
  color: rgba(255, 255, 255, 0.7) !important;
}

:deep(.v-select__selection) {
  color: white !important;
}

@media (max-width: 600px) {
  .search-card {
    padding: 8px;
  }
  
  :deep(.v-row) {
    row-gap: 12px;
  }
}
</style>