<template>
  <div class="timeline-main">
    <!-- 헤더 섹션 -->
    <div class="headline-section mb-6">
      <h2 class="text-h4 font-weight-bold">{{ timelineData.post?.title }}</h2>
      <div class="d-flex flex-wrap gap-1 mt-2">
        <v-chip
          v-for="(keyword, index) in timelineData.post?.keywords?.split(',')"
          :key="index"
          size="x-small"
          variant="outlined"
          class="text-white mr-1"
          style="background-color: rgba(255, 255, 255, 0.2)"
        >
          {{ keyword.trim() }}
        </v-chip>
      </div>
    </div>

    <div class="timeline-container">
      <v-timeline side="end" align="start" class="timeline-set">
        <!-- 타임라인 설명 아이템 -->
        <v-timeline-item size="large">
          <template v-slot:opposite>
            <div class="text-caption-for-name mobile-hide">{{ writerNickname }}</div>
          </template>
          <template v-slot:icon>
            <v-avatar :image="writerProfileImage"></v-avatar>
          </template>
          <div class="timeline-item-container description-container">
            <v-card class="elevation-5">
              <v-card-text>{{ timelineData.post?.description }}</v-card-text>
            </v-card>
          </div>
        </v-timeline-item>

        <!-- 방문 기록 아이템들 -->
        <v-timeline-item v-for="visit in timelineData.visits" :key="visit.id" :dot-color="'grey'" size="small">
          <template v-slot:opposite>
            <div class="text-caption mobile-hide">{{ formatDate(visit.visitDate) }}</div>
          </template>

          <v-card class="elevation-5 timeline-card">
            <!-- 모바일에서만 보이는 날짜 표시 -->
            <div class="mobile-date">{{ formatDate(visit.visitDate) }}</div>
            <v-carousel
              v-if="visit.photos && visit.photos.length > 0"
              height="300"
              width="100%"
              hide-delimiter-background
              show-arrows="hover"
            >
              <template v-slot:prev="{ props }">
                <v-btn
                  variant="text"
                  color="white"
                  icon="mdi-chevron-left"
                  v-bind="props"
                  class="carousel-nav-btn"
                ></v-btn>
              </template>

              <template v-slot:next="{ props }">
                <v-btn
                  variant="text"
                  color="white"
                  icon="mdi-chevron-right"
                  v-bind="props"
                  class="carousel-nav-btn"
                ></v-btn>
              </template>
              <template v-slot:dots="{ items }">
                <div class="v-carousel__controls">
                  <button
                    v-for="(item, index) in items"
                    :key="index"
                    class="v-carousel__controls__item"
                    :class="{ 'v-btn--active': item.isSelected }"
                    @click="item.select"
                  >
                    <div class="custom-dot"></div>
                  </button>
                </div>
              </template>
              <v-carousel-item v-for="photo in visit.photos" :key="photo.id" :src="getImageUrl(photo.fileName)" cover>
                <template v-slot:placeholder>
                  <div class="d-flex align-center justify-center fill-height">
                    <v-progress-circular indeterminate color="grey-lighten-4"></v-progress-circular>
                  </div>
                </template>
              </v-carousel-item>
            </v-carousel>

            <v-card-title>{{ visit.title }}</v-card-title>
            <v-card-text>
              <p>{{ visit.description }}</p>
              <div v-if="visit.place" class="location-info">
                <v-icon>mdi-map-marker</v-icon>
                <span>{{ visit.place.name }}</span>
                <span class="text-caption-address">{{ visit.place.address }}</span>
              </div>
            </v-card-text>
          </v-card>
        </v-timeline-item>
      </v-timeline>
    </div>
  </div>
</template>
<script>
import api, { BASE_URL } from "@/api/axios";

export default {
  data() {
    return {
      timelineData: {
        post: {
          id: null,
          title: "",
          description: "",
          startDate: "",
          endDate: "",
          keywords: "",
        },
        visits: [],
      },
    };
  },
  computed: {
    writerNickname() {
      if (this.timelineData.writer) {
        return this.timelineData.writer.nickname;
      }
      return "작성자";
    },
    writerProfileImage() {
      if (this.timelineData.writer?.profileImage) {
        return this.getProfileImageUrl(this.timelineData.writer.profileImage);
      }
      return "https://i.pravatar.cc/64";
    },
  },
  methods: {
    formatDate(dateString) {
      if (!dateString) return "";
      const date = new Date(dateString.split(" ")[0]);
      return new Intl.DateTimeFormat("ko-KR", {
        month: "long",
        day: "numeric",
      }).format(date);
    },

    getImageUrl(fileName) {
      if (!fileName) return null;
      return `${BASE_URL}/posts/uploads/${fileName}`;
    },
    getProfileImageUrl(imagePath) {
      if (!imagePath) return null;
      if (imagePath.startsWith("data:")) return imagePath;
      return `${process.env.VUE_APP_API_BASE_URL}/uploads/${imagePath}`;
    },

    async fetchTimelineData() {
      try {
        const id = this.$route.params.id;
        const response = await api.get(`/posts/${id}`);
        console.log("Timeline detail data:", response.data);
        this.timelineData = response.data;
        if (this.timelineData.post?.userId) {
          const userResponse = await api.get(`/members/${this.timelineData.post.userId}`);
          this.timelineData.writer = userResponse.data;
          console.log("Writer data:", this.timelineData.writer);
        }
      } catch (error) {
        console.error("타임라인 데이터 로딩 실패:", error);
      }
    },
  },

  created() {
    this.fetchTimelineData();
  },
};
</script>

<style scoped>
.mobile-date {
  display: none;
  padding: 8px 16px 8px 16px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.875rem;
}
.description-container {
  display: flex;
  justify-content: flex-start;
  width: 100%;
}

.description-card {
  display: inline-block;
  max-width: fit-content;
  min-width: 200px;
}
:deep(.description-card .v-card-text) {
  white-space: pre-wrap;
  word-break: break-word;
}
.custom-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  transition: background-color 0.2s;
}

.v-btn--active .custom-dot {
  background-color: white;
}
.carousel-nav-btn {
  background: rgba(0, 0, 0, 0.5);
  opacity: 0.7;
}

.carousel-nav-btn:hover {
  background: rgba(0, 0, 0, 0.5);
  opacity: 1;
}
.timeline-set {
  justify-content: start !important;
}
.timeline-main {
  position: relative;
  min-height: calc(100vh - 200px);
  width: 100%;
  padding: 20px;
}

.timeline-container {
  width: 100%;
  max-width: 720px;
  margin: 0 auto;
}

.headline-section {
  text-align: left;
  padding: 0 16px;
  margin-bottom: 40px;
}

:deep(.v-timeline-item__body) {
  padding: 0 16px;
  width: 100%; /* 추가 */
  max-width: 518px; /* 추가 - 타임라인 아이템의 최대 너비 제한 */
}
:deep(.v-timeline) {
  width: 100%;
  max-width: 720px; /* 전체 타임라인의 최대 너비 설정 */
  margin: 0 auto;
}
:deep(.v-timeline-item__opposite) {
  flex: none !important;
  width: 100px !important;
  min-width: 100px !important;
  margin-right: 16px;
  text-align: right;
}
/* 타임라인 라인 위치 조정 */
:deep(.v-timeline-divider) {
  margin-right: -8px; /* 추가 */
}
.timeline-card {
  width: 100% !important;
  margin-left: 0; /* 수정 */
  margin-right: 0; /* 추가 */
}

.text-caption {
  font-size: 0.875rem !important;
  margin-top: 4px;
}
.text-caption-for-name {
  font-size: 0.875rem !important;
  margin-top: 12px;
}
.text-caption-address {
  font-size: 0.875rem !important;
}

.location-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  padding-top: 10px;
  color: #666;
  border-top: 1px solid #9e9e9e65;
}

@media (max-width: 600px) {
  .timeline-main {
    padding: 12px; /* 모바일에서 패딩 축소 */
  }

  .headline-section {
    padding: 0 8px;
    margin-bottom: 24px;
  }

  .mobile-hide {
    display: none;
  }

  .mobile-date {
    display: block;
  }

  .timeline-container {
    padding: 0;
    max-width: 100%;
  }

  :deep(.v-timeline) {
    padding: 0 !important;
    margin: 0 !important;
    width: 100%;
  }

  :deep(.v-timeline-item) {
    grid-template-columns: 0 auto !important; /* 타임라인 라인 제거 */
    padding: 0 8px !important; /* 좌우 패딩 추가 */
    margin-bottom: 16px !important;
    width: 100%;
  }

  :deep(.v-timeline-item__body) {
    padding: 0 !important;
    width: 100% !important;
    max-width: 100% !important;
  }

  :deep(.v-timeline-divider) {
    display: none;
  }

  :deep(.v-timeline-item__opposite) {
    display: none !important;
  }

  .timeline-card {
    width: 100% !important;
    max-width: none !important;
    margin: 0 !important;
  }

  /* 캐러셀 높이 조정 */
  :deep(.v-carousel) {
    height: 250px !important;
  }

  /* 위치 정보 텍스트 조정 */
  .location-info {
    flex-wrap: wrap;
    font-size: 0.875rem;
  }

  .text-caption-address {
    width: 100%;
    margin-left: 28px; /* icon width + gap */
  }
}

/* 추가: 작은 모바일 화면을 위한 미디어 쿼리 */
@media (max-width: 360px) {
  .timeline-main {
    padding: 8px;
  }

  :deep(.v-carousel) {
    height: 200px !important;
  }
}
.image-error {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  color: #666;
}
</style>
