<template>
  <div class="timeline-main">
    <div class="content-wrapper">
      <!-- 헤드라인 -->
      <v-responsive class="mx-auto" width="100%">
        <div class="headline-section mb-6">
          <h2 class="text-h4 font-weight-bold">최근 추가된 타임라인</h2>
        </div>
        <!-- 타임라인 카드 섹션 -->
        <v-row justify="center">
          <v-col cols="12">
            <!-- 중앙 정렬을 위한 컬럼 크기 조정 -->
            <v-card
              v-for="timeline in recentTimelines"
              :key="timeline.post.id"
              class="mx-auto mb-4 d-flex flex-column"
              max-width="100%"
              height="350"
              elevation="5"
              :image="timeline.thumbnailUrl ? getImageUrl(timeline.thumbnailUrl) : require('@/assets/noimage.jpg')"
            >
              <v-card-item style="background-color: rgba(0, 0, 0, 0.55)">
                <v-card-title class="text-white text-shadow">{{ timeline.post.title }}</v-card-title>
                <v-card-subtitle class="text-white text-shadow">
                  {{ timeline.post.description }}
                </v-card-subtitle>
                <div class="d-flex flex-wrap gap-1 mt-2">
                  <v-chip
                    v-for="(keyword, index) in timeline.post.keywords.split(',')"
                    :key="index"
                    size="x-small"
                    variant="outlined"
                    class="text-white mr-1"
                    style="background-color: rgba(255, 255, 255, 0.2)"
                  >
                    {{ keyword.trim() }}
                  </v-chip>
                </div>
              </v-card-item>
              <v-spacer></v-spacer>
              <!-- 액션 버튼 -->
              <template v-slot:actions>
                <v-btn
                  append-icon="mdi-chevron-right"
                  text="자세히 보기"
                  variant="tonal"
                  elevation="5"
                  class="text-white text-shadow tonal-btn"
                  @click="viewTimeline(timeline.post.id)"
                ></v-btn>
                <v-spacer></v-spacer>
                <v-btn
                  :prepend-icon="timeline.isBookmarked ? 'mdi-heart' : 'mdi-heart-outline'"
                  :text="timeline.isBookmarked ? '북마크' : '북마크'"
                  variant="tonal"
                  elevation="5"
                  class="text-white text-shadow tonal-btn"
                  @click="toggleBookmark(timeline.post.id)"
                ></v-btn>
              </template>
            </v-card>
          </v-col>
        </v-row>
        <v-divider class="mb-10 mt-9"></v-divider>
        <div class="headline-section mb-6">
          <h2 class="text-h4 font-weight-bold">인기 타임라인</h2>
        </div>
        <!-- 타임라인 카드 섹션 -->
        <v-row justify="center">
          <v-col cols="12">
            <!-- 중앙 정렬을 위한 컬럼 크기 조정 -->
            <v-card
              v-for="timeline in topTimeLines"
              :key="timeline.post.id"
              class="mx-auto mb-4 d-flex flex-column"
              max-width="100%"
              height="350"
              elevation="5"
              :image="timeline.thumbnailUrl ? getImageUrl(timeline.thumbnailUrl) : require('@/assets/noimage.jpg')"
            >
              <v-card-item style="background-color: rgba(0, 0, 0, 0.55)">
                <v-card-title class="text-white text-shadow">{{ timeline.post.title }}</v-card-title>
                <v-card-subtitle class="text-white text-shadow">
                  {{ timeline.post.description }}
                </v-card-subtitle>
                <div class="d-flex flex-wrap gap-1 mt-2">
                  <v-chip
                    v-for="(keyword, index) in timeline.post.keywords.split(',')"
                    :key="index"
                    size="x-small"
                    variant="outlined"
                    class="text-white mr-1"
                    style="background-color: rgba(255, 255, 255, 0.2)"
                  >
                    {{ keyword.trim() }}
                  </v-chip>
                </div>
              </v-card-item>
              <v-spacer></v-spacer>
              <!-- 액션 버튼 -->
              <template v-slot:actions>
                <v-btn
                  append-icon="mdi-chevron-right"
                  text="자세히 보기"
                  variant="tonal"
                  elevation="5"
                  class="text-white text-shadow tonal-btn"
                  @click="viewTimeline(timeline.post.id)"
                ></v-btn>
                <v-spacer></v-spacer>
                <v-btn
                  :prepend-icon="timeline.isBookmarked ? 'mdi-heart' : 'mdi-heart-outline'"
                  :text="timeline.isBookmarked ? '북마크됨' : '북마크'"
                  variant="tonal"
                  elevation="5"
                  class="text-white text-shadow tonal-btn"
                  @click="toggleBookmark(timeline.post.id)"
                ></v-btn>
              </template>
            </v-card>
          </v-col>
        </v-row>
        <v-divider class="mb-10 mt-9"></v-divider>
        <div class="headline-section mb-6">
          <h2 class="text-h4 font-weight-bold">전체 타임라인</h2>
        </div>
        <!-- 타임라인 카드 섹션 -->
        <v-row justify="center">
          <v-col cols="12">
            <!-- 중앙 정렬을 위한 컬럼 크기 조정 -->
            <v-card
              v-for="timeline in allTimelines"
              :key="timeline.post.id"
              class="mx-auto mb-4 d-flex flex-column"
              max-width="100%"
              height="350"
              elevation="5"
              :image="timeline.thumbnailUrl ? getImageUrl(timeline.thumbnailUrl) : require('@/assets/noimage.jpg')"
            >
              <v-card-item style="background-color: rgba(0, 0, 0, 0.55)">
                <v-card-title class="text-white text-shadow">{{ timeline.post.title }}</v-card-title>
                <v-card-subtitle class="text-white text-shadow">
                  {{ timeline.post.description }}
                </v-card-subtitle>
              </v-card-item>
              <v-spacer></v-spacer>
              <!-- 액션 버튼 -->
              <template v-slot:actions>
                <v-btn
                  append-icon="mdi-chevron-right"
                  text="자세히 보기"
                  variant="tonal"
                  elevation="5"
                  class="text-white text-shadow tonal-btn"
                  @click="viewTimeline(timeline.post.id)"
                ></v-btn>
                <v-spacer></v-spacer>
                <v-btn
                  :prepend-icon="timeline.isBookmarked ? 'mdi-heart' : 'mdi-heart-outline'"
                  :text="timeline.isBookmarked ? '북마크됨' : '북마크'"
                  variant="tonal"
                  elevation="5"
                  class="text-white text-shadow tonal-btn"
                  @click="toggleBookmark(timeline.post.id)"
                ></v-btn>
              </template>
            </v-card>
            <!-- 페이지네이션 컨트롤 추가 -->
            <div class="d-flex justify-center mt-6">
              <v-btn
                :disabled="currentPage === 0"
                @click="loadPreviousPage"
                prepend-icon="mdi-chevron-left"
                variant="outlined"
                color="white"
                class="pagination-btn"
              >
                이전
              </v-btn>
              <span class="mx-4 d-flex align-center text-white">페이지 {{ currentPage + 1 }}</span>
              <v-btn
                :disabled="!hasMorePages"
                @click="loadNextPage"
                append-icon="mdi-chevron-right"
                variant="outlined"
                color="white"
                class="pagination-btn"
              >
                다음
              </v-btn>
            </div>
          </v-col>
        </v-row>
      </v-responsive>
    </div>
    <!-- FAB 버튼 -->
    <div class="fab-container">
      <v-btn class="fab-button" icon="mdi-plus" color="primary" elevation="5" @click="createNewTimeline"></v-btn>
    </div>
    <!-- 로그인 필요 모달 -->
    <v-dialog v-model="showNotLoggedInModal" max-width="400">
      <NotLoggedInModal @close="showNotLoggedInModal = false" @open-login-modal="openLoginModal" />
    </v-dialog>

    <v-dialog v-model="showLoginModal" transition="dialog-top-transition" max-width="800px">
      <v-card class="login-modal">
        <LoginModal :isActive="showLoginModal" @close="closeLoginModal" @login-success="onLoginSuccess" />
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import api from "@/api/axios";
import { mapGetters } from "vuex";
import NotLoggedInModal from "./NotLoggedInModal.vue";
import LoginModal from "./LoginModal.vue";

export default {
  name: "TimelineMain",
  components: {
    NotLoggedInModal,
    LoginModal,
  },
  data() {
    return {
      recentTimelines: [],
      bookmarkedPosts: [],
      topTimeLines: [],
      showNotLoggedInModal: false,
      showLoginModal: false,
      allTimelines: [],
      currentPage: 0,
      pageSize: 5,
      hasMorePages: true,
    };
  },
  computed: {
    ...mapGetters("auth", ["isLoggedIn"]),
    ...mapGetters("auth", ["id"]),
  },

  methods: {
    formatDate(date) {
      if (!date) return "";
      const [year, month, day] = date.split("-");
      return `${year}년 ${month}월 ${day}일`;
    },
    getImageUrl(thumbnailUrl) {
      if (!thumbnailUrl) return null;

      if (!thumbnailUrl.startsWith("/")) {
        thumbnailUrl = "/" + thumbnailUrl;
      }
      return `https://api.ssafy.me${thumbnailUrl}`;
    },
    updateBookmarkStatus() {
      this.recentTimelines = this.recentTimelines.map((timeline) => {
        const isBookmarked = this.bookmarkedPosts.includes(timeline.post.id);
        return {
          ...timeline,
          isBookmarked,
        };
      });
      this.topTimeLines = this.topTimeLines.map((timeline) => {
        const isBookmarked = this.bookmarkedPosts.includes(timeline.post.id);
        return {
          ...timeline,
          isBookmarked,
        };
      });
    },
    async viewTimeline(id) {
      try {
        // 타임라인 상세 데이터 확인을 위한 테스트 로그
        const response = await api.get(`/posts/${id}`);
        console.log("Timeline detail data:", response.data);

        // 상세 페이지로 이동
        this.$router.push({
          name: "TimelineDetail",
          params: { id: id },
        });
      } catch (error) {
        console.error("Error loading timeline:", error);
      }
    },

    createNewTimeline() {
      if (this.isLoggedIn) {
        this.$router.push("/timeline/create");
      } else {
        this.showNotLoggedInModal = true;
      }
    },
    openLoginModal() {
      this.showNotLoggedInModal = false;
      this.showLoginModal = true;
    },

    closeLoginModal() {
      this.showLoginModal = false;
    },

    onLoginSuccess() {
      this.closeLoginModal();
      this.$router.push("/timeline/create");
    },
    async toggleBookmark(postId) {
      if (!this.isLoggedIn) {
        this.showNotLoggedInModal = true;
        return;
      }

      try {
        const isCurrentlyBookmarked = this.bookmarkedPosts.includes(postId);

        if (isCurrentlyBookmarked) {
          // 북마크 삭제
          await api.delete("/posts/bookmarks", {
            data: {
              // DELETE 요청의 경우 data 옵션을 사용해야 합니다
              userId: this.id,
              postId: postId,
            },
          });
        } else {
          // 북마크 추가
          await api.post("/posts/bookmarks", {
            userId: this.id,
            postId: postId,
          });
        }

        // 북마크 상태 토글
        const index = this.bookmarkedPosts.indexOf(postId);
        if (index === -1) {
          this.bookmarkedPosts.push(postId);
        } else {
          this.bookmarkedPosts.splice(index, 1);
        }

        // UI 업데이트
        this.updateBookmarkStatus();
      } catch (error) {
        console.error("Failed to toggle bookmark:", error);
      }
    },
    async fetchRecentTimelines() {
      try {
        const response = await api.get("/posts/recent");
        this.recentTimelines = response.data.map((timeline) => ({
          ...timeline,
          isBookmarked: false, // 초기값 설정
        }));

        if (this.isLoggedIn) {
          await this.fetchUserBookmarks();
        }
      } catch (error) {
        console.error("Failed to load recent timelines:", error);
      }
    },
    async fetchTopTimelines() {
      try {
        const response = await api.get("/posts/top");
        this.topTimeLines = response.data.map((timeline) => ({
          ...timeline,
          isBookmarked: false, // 초기값 설정
        }));

        if (this.isLoggedIn) {
          await this.fetchUserBookmarks();
        }
      } catch (error) {
        console.error("Failed to load top timelines:", error);
      }
    },
    async fetchUserBookmarks() {
      try {
        const userId = this.id;
        if (!userId) {
          console.warn("User ID not available");
          return;
        }

        const response = await api.get(`/posts/bookmarks/list/${userId}`);
        this.bookmarkedPosts = response.data.map((bookmark) => bookmark.postId);
        console.log("User bookmarked posts:", this.bookmarkedPosts);

        // 현재 타임라인 목록의 북마크 상태 업데이트
        this.updateBookmarkStatus();
      } catch (error) {
        console.error("Failed to fetch user bookmarks:", error);
      }
    },
    async fetchAllTimelines() {
      try {
        const response = await api.get(`/posts/list/${this.currentPage * this.pageSize}`);
        this.allTimelines = response.data.map((timeline) => ({
          ...timeline,
          isBookmarked: false,
        }));

        // 다음 페이지 존재 여부 확인
        this.hasMorePages = response.data.length === this.pageSize;

        if (this.isLoggedIn) {
          await this.fetchUserBookmarks();
        }
      } catch (error) {
        console.error("Failed to load all timelines:", error);
      }
    },

    async loadNextPage() {
      if (this.hasMorePages) {
        this.currentPage++;
        await this.fetchAllTimelines();
      }
    },

    async loadPreviousPage() {
      if (this.currentPage > 0) {
        this.currentPage--;
        await this.fetchAllTimelines();
      }
    },
  },

  async created() {
    this.fetchRecentTimelines();
    this.fetchTopTimelines();
    await this.fetchAllTimelines();
  },
};
</script>

<style scoped>
.timeline-main {
  position: relative;
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
  width: 100%;
  padding: 20px;
}
.content-wrapper {
  flex: 1;
  position: relative;
}
.v-responsive {
  width: 100%;
}

.headline-section {
  text-align: left;
  padding: 0 16px;
}

.fab-container {
  position: sticky;
  bottom: 97px; /* 하단에서의 거리 */
  margin-left: auto; /* 오른쪽 정렬 */
  margin-right: -20px;
  width: fit-content; /* 컨테이너 크기를 내용에 맞춤 */
  padding-right: 0px; /* 오른쪽 여백 */
  z-index: 100;
}
.fab-button {
  background-color: #2c2c2c !important;
  color: white !important;
}

.fab-button:hover {
  background-color: #505050 !important;
}

.text-shadow {
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.tonal-btn {
  background-color: rgba(0, 0, 0, 0.55) !important; /* 반투명한 검정 배경 */
}
.login-modal {
  max-width: 600px;
  width: 100%;
  margin: auto;
  padding: 20px;
}
.pagination-btn {
  border-color: white !important;
  color: white !important;
}

.pagination-btn:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.pagination-btn:disabled {
  border-color: rgba(255, 255, 255, 0.3) !important;
  color: rgba(255, 255, 255, 0.3) !important;
}
</style>
