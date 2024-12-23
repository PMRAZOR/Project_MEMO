<template>
  <div class="timeline-main">
    <div class="content-wrapper">
      <v-responsive class="mx-auto" width="100%">
        <!-- 타임라인 카드 섹션 -->
        <v-row justify="center">
          <v-col cols="12">
            <!-- 중앙 정렬을 위한 컬럼 크기 조정 -->
            <v-card
              v-for="timeline in bookmarks"
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
                  prepend-icon="mdi-heart"
                  text="북마크"
                  variant="tonal"
                  elevation="5"
                  class="text-white text-shadow tonal-btn"
                  @click="toggleBookmark(timeline.post.id)"
                ></v-btn>
              </template>
            </v-card>
          </v-col>
        </v-row>
      </v-responsive>
    </div>
  </div>
</template>

<script>
import api from "@/api/axios";
import { mapGetters } from "vuex";
export default {
  name: "BookmarkPage",
  components: {},
  data() {
    return {
      recentTimelines: [],
      bookmarks: [],
      showNotLoggedInModal: false,
      showLoginModal: false,
    };
  },
  computed: {
    ...mapGetters("auth", ["isLoggedIn", "id"]),
    userId() {
      // id가 없으면 콘솔에 기록
      if (!this.id) {
        console.warn("User ID is not available:", this.id);
      }
      return this.id;
    },
  },

  methods: {
    formatDate(date) {
      if (!date) return "";
      const [year, month, day] = date.split("-");
      return `${year}년 ${month}월 ${day}일`;
    },
    getImageUrl(thumbnailUrl) {
      if (!thumbnailUrl) return null;
      return `https://api.ssafy.me${thumbnailUrl}`;
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

    async toggleBookmark(postId) {
      if (!this.isLoggedIn) {
        this.showNotLoggedInModal = true;
        return;
      }

      try {
        const isCurrentlyBookmarked = true; // 북마크 페이지이므로 항상 true

        if (isCurrentlyBookmarked) {
          // 북마크 삭제
          await api.delete("/posts/bookmarks", {
            data: {
              userId: this.id,
              postId: postId,
            },
          });

          // 북마크 목록에서 해당 항목 제거
          this.bookmarks = this.bookmarks.filter((bookmark) => bookmark.post.id !== postId);
        }
      } catch (error) {
        console.error("Failed to remove bookmark:", error);
      }
    },

    async fetchUserBookmarks() {
      try {
        if (!this.id) {
          console.warn("User ID not available");
          return;
        }

        const response = await api.get(`/posts/bookmarks/${this.id}`);
        this.bookmarks = response.data.map((timeline) => ({
          ...timeline,
          isBookmarked: true, // 북마크 페이지이므로 모두 true로 설정
        }));
        console.log("User bookmarks:", this.bookmarks);
      } catch (error) {
        console.error("Failed to fetch user bookmarks:", error);
      }
    },
  },

  async created() {
    // 컴포넌트가 생성될 때 북마크 정보를 가져옵니다
    await this.fetchUserBookmarks();
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
</style>
