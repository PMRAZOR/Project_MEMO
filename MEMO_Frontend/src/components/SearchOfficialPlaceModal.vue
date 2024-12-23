<template>
  <v-dialog :model-value="modelValue" @update:model-value="updateModelValue" max-width="550px">
    <v-card class="select-place-modal">
      <v-toolbar title="국가 공인 장소 검색하기" rounded dark></v-toolbar>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols="12">
              <v-text-field
                v-model="searchQuery"
                hint="검색 할 장소 이름을 적어주세요"
                clearable
                label="이름"
                variant="outlined"
                @keyup.enter="searchPlace"
              >
                <template v-slot:append-inner>
                  <v-btn-login class="button-text-size" @click="searchPlace" variant="text" icon="mdi-magnify">
                    검색
                  </v-btn-login>
                </template>
              </v-text-field>
            </v-col>
          </v-row>
          <v-row v-if="searchResults.length > 0">
            <v-col cols="12">
              <v-list>
                <v-list-item v-for="place in searchResults" :key="place.contentid" @click="selectPlace(place)">
                  <v-list-item-content>
                    <v-list-item-title>{{ place.title }}</v-list-item-title>
                    <v-list-item-subtitle>{{ place.addr1 }} {{ place.addr2 }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-col>
          </v-row>

          <!-- 페이지네이션 추가 -->
          <v-row v-if="totalPages > 1">
            <v-col cols="12" class="d-flex justify-center">
              <v-pagination
                v-model="currentPage"
                :length="totalPages"
                :total-visible="6"
                show-first-last
                prev-icon="mdi-chevron-left"
                next-icon="mdi-chevron-right"
                first-icon="mdi-chevron-double-left"
                last-icon="mdi-chevron-double-right"
                @update:model-value="handlePageChange"
              ></v-pagination>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn-login class="button-text-size" @click="cancel">취소</v-btn-login>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from "axios";

export default {
  name: "SearchOfficialPlaceModal",
  props: {
    modelValue: Boolean,
  },
  emits: ["update:modelValue", "place-selected"],
  data() {
    return {
      searchQuery: "",
      searchResults: [],
      currentPage: 1,
      totalResults: 0,
      itemsPerPage: 5,
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.totalResults / this.itemsPerPage);
    },
  },
  methods: {
    updateModelValue(value) {
      this.$emit("update:modelValue", value);
    },
    cancel() {
      this.updateModelValue(false);
    },
    async searchPlace() {
      console.log("Searching for:", this.searchQuery);
      // 새로운 검색 시 페이지 초기화
      this.currentPage = 1;

      try {
        const encodedKeyword = encodeURIComponent(this.searchQuery);
        const url = `https://apis.data.go.kr/B551011/KorService1/searchKeyword1?serviceKey=ziktH%2FGPfHcpskjxJEvc%2BuMqR%2B2zfqVDCSj1MJQJZa0mGQ%2BxIFpLHfvMvwlTh9V4DiSmdWxx%2FUVUVXTCwxFPow%3D%3D&MobileApp=AppTest&MobileOS=ETC&pageNo=1&numOfRows=${this.itemsPerPage}&_type=json&keyword=${encodedKeyword}&arrange=A`;

        console.log("Request URL:", url);

        const response = await axios.get(url);
        console.log("API Response:", response.data);

        if (
          response.data &&
          response.data.response &&
          response.data.response.body &&
          response.data.response.body.items
        ) {
          this.searchResults = response.data.response.body.items.item || [];
          this.totalResults = response.data.response.body.totalCount || 0;

          if (!Array.isArray(this.searchResults) && this.searchResults) {
            this.searchResults = [this.searchResults];
          }

          console.log("Search Results:", this.searchResults);
        } else {
          this.searchResults = [];
          this.totalResults = 0;
        }
      } catch (error) {
        console.error("Error searching places:", error);
        this.searchResults = [];
        this.totalResults = 0;
      }
    },

    async handlePageChange(page) {
      this.currentPage = page;
      const encodedKeyword = encodeURIComponent(this.searchQuery);
      const url = `https://apis.data.go.kr/B551011/KorService1/searchKeyword1?serviceKey=ziktH%2FGPfHcpskjxJEvc%2BuMqR%2B2zfqVDCSj1MJQJZa0mGQ%2BxIFpLHfvMvwlTh9V4DiSmdWxx%2FUVUVXTCwxFPow%3D%3D&MobileApp=AppTest&MobileOS=ETC&pageNo=${page}&numOfRows=${this.itemsPerPage}&_type=json&keyword=${encodedKeyword}&arrange=A`;

      try {
        const response = await axios.get(url);
        if (response.data?.response?.body?.items) {
          this.searchResults = response.data.response.body.items.item || [];
          if (!Array.isArray(this.searchResults) && this.searchResults) {
            this.searchResults = [this.searchResults];
          }
        }
      } catch (error) {
        console.error("Error changing page:", error);
      }
    },

    selectPlace(place) {
      // 유효성 검사
      if (!place || !place.title || !place.addr1 || !place.mapy || !place.mapx || !place.contenttypeid) {
        console.warn("유효하지 않은 장소 데이터:", place);
        return;
      }

      // DB 저장은 하지 않고 부모 컴포넌트로 데이터만 전달
      this.$emit("place-selected", place);
      this.updateModelValue(false);
    },
    viewDetails(place) {
      console.log("View details for:", place);
    },
  },
};
</script>

<style scoped>
.select-place-modal {
  width: 100%;
  margin: auto;
  padding: 20px;
}
</style>
