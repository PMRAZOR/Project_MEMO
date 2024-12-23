<template>
  <v-app>
    <!-- 상단 바 -->
    <Appbar />

    <!-- 메인 콘텐츠 영역 -->
    <v-main class="d-flex flex-column main-content">
      <v-container max-width="800" class="px-10 flex-grow-1 d-flex flex-column">
        <router-view></router-view>
      </v-container>
    </v-main>

    <!-- 하단 네브바 구역 -->
    <BottomNav />
  </v-app>
</template>

<script>
import Appbar from "./components/AppBar.vue";
import BottomNav from "./components/BottomNav.vue";

export default {
  name: "App", // 또는 페이지 컴포넌트 이름
  components: {
    Appbar,
    BottomNav,
  },
};
</script>

<style>
.main-content {
  padding-bottom: 56px; /* 하단 네비게이션 바의 높이만큼 패딩 추가 */
}

.v-main {
  padding-top: 87px; /* 앱바의 높이만큼 상단 패딩 추가 */
  min-height: 100vh;
}

.v-container {
  padding-top: 16px;
  padding-bottom: 16px;
}

/* 전역 스크롤바 스타일 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: rgba(80, 80, 80, 0.822);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  border-radius: 3px;
  background: rgba(170, 170, 170, 0.61);
}

/* Firefox 스크롤바 */
/* 
* {
  scrollbar-width: thin;
  scrollbar-color: rgba(255, 255, 255, 0.7) rgba(0, 0, 0, 0.1);
}
  */
</style>
