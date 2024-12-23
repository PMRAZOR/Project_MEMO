<template>
  <v-bottom-navigation v-model="activeTab" :elevation="8" :bg-color="color" mode="shift" class="bottom-nav">
    <v-btn @click="goToMain" :value="'/'">
      <v-icon>mdi-home-variant</v-icon>
      <span>메인</span>
    </v-btn>
    <v-btn @click="goToRecent" :value="'/timeline'">
      <v-icon>mdi-timeline-check</v-icon>
      <span>타임라인</span>
    </v-btn>
    <v-btn @click="goToBookmark" :value="'/bookmark'">
      <v-icon>mdi-heart</v-icon>
      <span>북마크</span>
    </v-btn>
    <v-btn @click="goToMap" :value="'/map'">
      <v-icon>mdi-map-marker</v-icon>
      <span>지도</span>
    </v-btn>
  </v-bottom-navigation>
  <v-dialog v-model="showNotLoggedInModal" max-width="400">
    <NotLoggedInModal @close="showNotLoggedInModal = false" @open-login-modal="openLoginModal" />
  </v-dialog>
  <v-dialog v-model="showLoginModal" transition="dialog-top-transition" max-width="800px">
    <v-card class="login-modal">
      <LoginModal :isActive="showLoginModal" @close="closeLoginModal" @login-success="onLoginSuccess" />
    </v-card>
  </v-dialog>
</template>

<script>
import { mapGetters } from "vuex";
import NotLoggedInModal from "./NotLoggedInModal.vue";
import LoginModal from "./LoginModal.vue";
export default {
  components: {
    NotLoggedInModal,
    LoginModal,
  },
  data() {
    return {
      color: undefined, // 필요한 경우 색상 지정
      activeTab: "/",
      showNotLoggedInModal: false,
      showLoginModal: false,
    };
  },
  computed: {
    ...mapGetters("auth", ["isLoggedIn"]),
  },
  methods: {
    goToMain() {
      this.$router.push("/");
    },
    goToRecent() {
      this.$router.push("/timeline");
    },
    goToBookmark() {
      if (this.isLoggedIn) {
        this.$router.push("/bookmark");
      } else {
        this.showNotLoggedInModal = true;
      }
    },
    openLoginModal() {
      this.showNotLoggedInModal = false;
      this.showLoginModal = true;
    },
    onLoginSuccess() {
      console.log("onLoginSuccess called in AppBar");
      this.closeLoginModal();
      // 리다이렉트 처리 추가
      const redirectPath = this.$route.query.redirect || "/";
      this.$router.push(redirectPath);
    },
    goToMap() {
      this.$router.push("/map");
    },
    updateActiveTab() {
      this.activeTab = this.$route.path;
    },
    checkAuth() {
      const token = localStorage.getItem("token");
      return !!token;
    },
    closeLoginModal() {
      this.showLoginModal = false;

      const query = { ...this.$route.query };
      delete query.requireAuth;

      this.$router.replace({
        path: this.$route.path,
        query: query,
      });
    },
    closeNotLoggedInModal() {
      this.showNotLoggedInModal = false;

      // 현재 경로의 쿼리 파라미터를 복사
      const query = { ...this.$route.query };
      delete query.requireAuth;

      // 현재 경로 유지하면서 쿼리만 업데이트
      this.$router.replace({
        path: this.$route.path,
        query: query,
      });
    },
  },
  watch: {
    $route: {
      handler: "updateActiveTab",
      immediate: true,
    },
  },
  mounted() {
    this.updateActiveTab();
  },
};
</script>
<style scoped>
.login-modal {
  max-width: 600px;
  width: 100%;
  margin: auto;
  padding: 20px;
}
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
}
</style>
