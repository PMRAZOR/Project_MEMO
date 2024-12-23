<template>
  <v-container class="pa-0 container" fluid>
    <v-img height="405px" class="rounded elevation-5" cover>
      <video autoplay loop muted playsinline class="fill-height w-100 video-background">
        <source :src="bgVideo" type="video/webm" />
      </video>
      <v-row class="fill-height content-overlay" align="center" justify="center">
        <v-col cols="18" md="10" class="text-center">
          <h1 class="title-shadow font-weight-bold white--text mb-6">당신의 경험을 공유하세요</h1>
          <v-btn-login class="elevation-5" size="large" @click="handleClick"> 시작하기 </v-btn-login>
        </v-col>
      </v-row>
    </v-img>

    <!-- 최근 타임라인 섹션 -->
    <div class="mt-6 pt-8">
      <h2 class="text-h4 font-weight-bold mb-4">최근 추가된 타임라인</h2>
      <v-row justify="center">
        <v-expand-transition group>
          <v-col v-for="timeline in recentTimelines" :key="timeline.post.id" cols="12" sm="6" md="4">
            <v-card
              class="mx-auto mb-4 d-flex flex-column"
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
              <template v-slot:actions>
                <v-btn
                  append-icon="mdi-chevron-right"
                  text="자세히 보기"
                  variant="tonal"
                  elevation="5"
                  class="text-white text-shadow tonal-btn w-100"
                  @click="viewTimeline(timeline.post.id)"
                ></v-btn>
              </template>
            </v-card>
          </v-col>
        </v-expand-transition>
      </v-row>
    </div>
    <!-- 북마크 탑 섹션 -->
    <div class="mt-6 pt-8">
      <h2 class="text-h4 font-weight-bold mb-4">인기 많은 타임라인</h2>
      <v-row justify="center">
        <v-expand-transition group>
          <v-col v-for="timeline in topTimelines" :key="timeline.post.id" cols="12" sm="6" md="4">
            <v-card
              class="mx-auto mb-4 d-flex flex-column"
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
              <template v-slot:actions>
                <v-btn
                  append-icon="mdi-chevron-right"
                  text="자세히 보기"
                  variant="tonal"
                  elevation="5"
                  class="text-white text-shadow tonal-btn w-100"
                  @click="viewTimeline(timeline.post.id)"
                ></v-btn>
              </template>
            </v-card>
          </v-col>
        </v-expand-transition>
      </v-row>
    </div>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import api from "@/api/axios";
import bgVideo from "@/assets/bg.webm";

const store = useStore();
const router = useRouter();
const recentTimelines = ref([]);
const topTimelines = ref([]);
const bookmarkedPosts = ref([]);

const isRecentVisible = ref(false);
const isTopVisible = ref(false);
const recentSection = ref(null);
const topSection = ref(null);

const isLoggedIn = computed(() => store.getters["auth/isLoggedIn"]);
const userId = computed(() => store.getters["auth/id"]);

const handleClick = () => {
  router.push("/timeline");
};

const getImageUrl = (thumbnailUrl) => {
  if (!thumbnailUrl) return null;

  if (!thumbnailUrl.startsWith("/")) {
    thumbnailUrl = "/" + thumbnailUrl;
  }

  return `https://api.ssafy.me${thumbnailUrl}`;
};

const viewTimeline = async (id) => {
  try {
    const response = await api.get(`/posts/${id}`);
    console.log("Timeline detail data:", response.data);
    // 상세 페이지로 이동
    router.push({
      name: "TimelineDetail", // 라우터에 정의된 경로 이름
      params: { id: id },
    });
  } catch (error) {
    console.error("Error loading timeline:", error);
  }
};
const updateBookmarkStatus = () => {
  recentTimelines.value = recentTimelines.value.map((timeline) => ({
    ...timeline,
    isBookmarked: bookmarkedPosts.value.includes(timeline.post.id),
  }));
  topTimelines.value = topTimelines.value.map((timeline) => ({
    ...timeline,
    isBookmarked: bookmarkedPosts.value.includes(timeline.post.id),
  }));
};

const fetchRecentTimelines = async () => {
  try {
    const response = await api.get("/posts/recent");
    recentTimelines.value = response.data.slice(0, 3).map((timeline) => ({
      ...timeline,
      isBookmarked: false,
    }));
    if (isLoggedIn.value) {
      await fetchUserBookmarks();
    }
  } catch (error) {
    console.error("Failed to load recent timelines:", error);
  }
};
const fetchTopTimelines = async () => {
  try {
    const response = await api.get("/posts/top");
    topTimelines.value = response.data.slice(0, 3).map((timeline) => ({
      ...timeline,
      isBookmarked: false,
    }));
    if (isLoggedIn.value) {
      await fetchUserBookmarks();
    }
  } catch (error) {
    console.error("Failed to load top timelines:", error);
  }
};

const fetchUserBookmarks = async () => {
  try {
    if (!userId.value) {
      console.warn("User ID not available");
      return;
    }
    const response = await api.get(`/posts/bookmarks/list/${userId.value}`);
    bookmarkedPosts.value = response.data.map((bookmark) => bookmark.postId);
    updateBookmarkStatus();
  } catch (error) {
    console.error("Failed to fetch user bookmarks:", error);
  }
};

onMounted(() => {
  fetchRecentTimelines();
  fetchTopTimelines();
  const observerRecent = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          isRecentVisible.value = true;
          observerRecent.disconnect();
        }
      });
    },
    {
      threshold: 0.1,
    }
  );

  const observerTop = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          isTopVisible.value = true;
          observerTop.disconnect();
        }
      });
    },
    {
      threshold: 0.1,
    }
  );

  // 최근 타임라인 섹션 관찰 시작
  if (recentSection.value) {
    observerRecent.observe(recentSection.value);
  }

  // 인기 타임라인 섹션 관찰 시작
  if (topSection.value) {
    observerTop.observe(topSection.value);
  }
});
</script>

<style scoped>
.v-expand-transition-enter-active,
.v-expand-transition-leave-active {
  transition: all 0.3s ease-out;
}

.v-expand-transition-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.v-expand-transition-leave-to {
  opacity: 0;
}
.video-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
}

.content-overlay {
  position: relative;
  z-index: 2;
}

.container {
  padding: 20px;
}
.title-shadow {
  font-size: 52px;
  text-shadow: 2px 3px 3px rgba(22, 22, 22, 0.349);
}
.text-shadow {
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}
.tonal-btn {
  background-color: rgba(0, 0, 0, 0.55) !important;
}
</style>
