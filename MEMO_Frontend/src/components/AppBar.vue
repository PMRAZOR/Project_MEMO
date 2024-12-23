<template>
  <div>
    <v-app-bar
      app
      color="appBarColor"
      dark
      dense
      max-width="100%"
      class="appbar-padding"
      height="34"
      :elevate-on-scroll="true"
      :scroll-threshold="0"
    >
      <v-img
        src="@/styles/logo.png"
        max-height="40"
        max-width="35"
        contain
        class="mr-3"
        transition="fade-transition"
        @click="goToMain"
        style="cursor: pointer"
      ></v-img>
      <v-spacer></v-spacer>

      <!-- 큰 화면에서 보이는 메뉴 -->
      <v-row class="align-center flex-row d-none d-md-flex" justify="end">
        <v-col cols="auto" class="pa-1">
          <v-btn-app-bar class="button-text-size" @click="goToThisWeekEvent">이번주 주요 행사</v-btn-app-bar>
        </v-col>
        <v-col cols="auto" class="pa-1">
          <v-btn-app-bar class="button-text-size" @click="goToMap">주위 관광지 검색</v-btn-app-bar>
        </v-col>
        <v-col cols="auto" class="pa-1">
          <v-btn-app-bar class="button-text-size" @click="goToTimeline">타임라인</v-btn-app-bar>
        </v-col>
        <v-col cols="auto" class="pa-1 mr-4">
          <v-btn-app-bar class="button-text-size" @click="goToCommunity">커뮤니티</v-btn-app-bar>
        </v-col>

        <v-col cols="auto" class="pa-1" v-if="!isLoggedIn">
          <v-dialog transition="dialog-top-transition" max-width="800px">
            <template v-slot:activator="{ props: activatorProps }">
              <v-btn-login class="button-text-size" v-bind="activatorProps">로그인</v-btn-login>
            </template>
            <template v-slot:default="{ isActive }">
              <v-card class="login-modal">
                <LoginModal :isActive="isActive" @close="isActive.value = false" @login-success="onLoginSuccess" />
              </v-card>
            </template>
          </v-dialog>
        </v-col>
        <v-col cols="auto" class="pa-1" v-else>
          <v-btn-login class="button-text-size" @click="clickLogout">로그아웃</v-btn-login>
        </v-col>

        <v-col cols="auto" class="mr-1">
          <v-dialog v-if="!isLoggedIn" transition="dialog-top-transition" max-width="800px">
            <template v-slot:activator="{ props: activatorProps }">
              <v-btn-regist class="button-text-size" v-bind="activatorProps">회원가입</v-btn-regist>
            </template>
            <template v-slot:default="{ isActive }">
              <v-card class="login-modal">
                <RegistModal :isActive="isActive" @close="isActive.value = false" />
              </v-card>
            </template>
          </v-dialog>
          <v-btn-regist v-else class="button-text-size" @click="goToMyPage">마이페이지</v-btn-regist>
        </v-col>
      </v-row>

      <!-- 작은 화면에서 보이는 햄버거 메뉴 -->
      <v-app-bar-nav-icon @click="toggleDrawer" class="d-md-none" color="white"></v-app-bar-nav-icon>
    </v-app-bar>

    <!-- 햄버거 메뉴의 내용 (탑다운) -->
    <transition name="menu-fade">
      <div v-show="drawer" class="top-down-menu">
        <v-list>
          <v-list-item>
            <v-btn-login block @click="goToAndCloseMenu('thisWeekEvent')">이번주 주요 행사</v-btn-login>
          </v-list-item>
          <v-list-item>
            <v-btn-login block @click="goToAndCloseMenu('map')">주위 관광지 검색</v-btn-login>
          </v-list-item>
          <v-list-item>
            <v-btn-login block @click="goToAndCloseMenu('timeline')">타임라인</v-btn-login>
          </v-list-item>
          <v-list-item>
            <v-btn-login block @click="goToAndCloseMenu('community')">커뮤니티</v-btn-login>
          </v-list-item>
          <v-list-item v-if="!isLoggedIn">
            <v-dialog transition="dialog-top-transition" max-width="800px">
              <template v-slot:activator="{ props: activatorProps }">
                <v-btn-regist block v-bind="activatorProps">로그인</v-btn-regist>
              </template>
              <template v-slot:default="{ isActive }">
                <v-card class="login-modal">
                  <LoginModal :isActive="isActive" @close="isActive.value = false" @login-success="onLoginSuccess" />
                </v-card>
              </template>
            </v-dialog>
          </v-list-item>
          <v-list-item v-else>
            <v-btn-login block @click="clickLogout">로그아웃</v-btn-login>
          </v-list-item>
          <v-list-item>
            <v-dialog v-if="!isLoggedIn" transition="dialog-top-transition" max-width="800px">
              <template v-slot:activator="{ props: activatorProps }">
                <v-btn-regist block v-bind="activatorProps">회원가입</v-btn-regist>
              </template>
              <template v-slot:default="{ isActive }">
                <v-card class="login-modal">
                  <RegistModal :isActive="isActive" @close="isActive.value = false" />
                </v-card>
              </template>
            </v-dialog>
            <v-btn-regist v-else block @click="goToAndCloseMenu('mypage')">마이페이지</v-btn-regist>
          </v-list-item>
        </v-list>
      </div>
    </transition>

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
import { mapGetters, mapActions } from "vuex";
import LoginModal from "./LoginModal.vue";
import RegistModal from "./RegistModal.vue";
import NotLoggedInModal from "./NotLoggedInModal.vue";

export default {
  data() {
    return {
      drawer: false,
      showNotLoggedInModal: false,
      showLoginModal: false,
    };
  },
  computed: {
    ...mapGetters("auth", ["isLoggedIn", "user"]),
  },
  watch: {
    $route(to) {
      if (to.query.requireAuth === "true" && !this.showNotLoggedInModal) {
        this.showNotLoggedInModal = true;
      }
    },
  },
  methods: {
    ...mapActions("auth", ["logout"]),
    closeLoginModal() {
      this.showLoginModal = false;
      const query = { ...this.$route.query };
      delete query.requireAuth;
      this.$router.replace({ path: this.$route.path, query: query });
    },
    closeNotLoggedInModal() {
      this.showNotLoggedInModal = false;
      const query = { ...this.$route.query };
      delete query.requireAuth;
      this.$router.replace({ path: this.$route.path, query: query });
    },
    clickLogout(){
      this.logout();
      this.$router.replace("/");
    },
    onLoginSuccess() {
      this.closeLoginModal();
      const redirectPath = this.$route.query.redirect || "/";
      this.$router.push(redirectPath);
    },
    openLoginModal() {
      this.showLoginModal = true;
    },
    toggleDrawer() {
      this.$nextTick(() => {
        this.drawer = !this.drawer;
      });
    },
    goToAndCloseMenu(route) {
      this.drawer = false;
      switch (route) {
        case "thisWeekEvent":
          this.goToThisWeekEvent();
          break;
        case "map":
          this.goToMap();
          break;
        case "timeline":
          this.goToTimeline();
          break;
        case "community":
          this.goToCommunity();
          break;
        case "mypage":
          this.goToMyPage();
          break;
      }
    },
    goToMain() {
      this.$router.push("/");
    },
    goToThisWeekEvent() {
      this.$router.push("/event");
    },
    goToMap() {
      this.$router.push("/map");
    },
    goToTimeline() {
      this.$router.push("/timeline");
    },
    goToCommunity() {
      this.$router.push("/boards");
    },
    goToMyPage() {
      this.$router.push("/mypage");
    },
  },
  components: {
    LoginModal,
    RegistModal,
    NotLoggedInModal,
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

.top-down-menu {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background-color: #333;
  z-index: 6;
  padding-top: 87px;
}

.menu-fade-enter-active,
.menu-fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.menu-fade-enter-from,
.menu-fade-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

.appbar-padding {
  padding: 26px 24px !important;
}

.button-text-size {
  font-size: 0.9rem !important;
}

.fade-transition {
  transition: opacity 0.2s ease-out;
}

@media (max-width: 600px) {
  .v-card-text {
    padding: 16px 24px;
  }
}

@media (min-width: 960px) {
  .top-down-menu {
    display: none;
  }
}
</style>