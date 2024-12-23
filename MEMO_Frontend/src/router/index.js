import { createRouter, createWebHistory } from "vue-router";
import Event from "../components/Event.vue";
import TimelineCreate from "../components/Timeline.vue";
import Timeline from "../components/TimelineMain.vue";
import TimelineDetail from "@/components/TimelineDetail.vue";
import NearByMap from "@/components/NearByMap.vue";
import BookmarkPage from "@/components/BookmarkPage.vue";
import MainPage from "@/components/MainPage.vue";
import MyPage from "@/views/MyPage.vue";
import store from "../store";

import boardRoutes from "./board";

const routes = [
  {
    path: "/",
    name: "MainPage",
    component: MainPage,
    props: true,
  },
  {
    path: "/event",
    name: "Event",
    component: Event,
  },
  {
    path: "/timeline",
    name: "Timeline",
    component: Timeline,
  },
  {
    path: "/timeline/create",
    name: "TimelineCreate",
    component: TimelineCreate,
  },
  {
    path: "/timeline/:id",
    name: "TimelineDetail",
    component: TimelineDetail,
    props: true,
  },
  {
    path: "/map",
    name: "NearByMap",
    component: NearByMap,
    props: true,
  },
  {
    path: "/bookmark",
    name: "BookmarkPage",
    component: BookmarkPage,
    props: true,
  },
  {
    path: "/mypage",
    name: "MyPage",
    component: MyPage,
  },
  ...boardRoutes,
  // 다른 라우트들을 여기에 추가할 수 있습니다.
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

// Navigation Guard 추가
router.beforeEach((to, from, next) => {
  const authRequired = ["TimelineCreate", "BookmarkPage"].includes(to.name);
  const isLoggedIn = store.getters["auth/isLoggedIn"]; // 수정된 부분

  if (authRequired && !isLoggedIn) {
    next({
      path: "/",
      query: {
        redirect: to.fullPath,
        requireAuth: "true",
      },
    });
  } else {
    next();
  }
});
export default router;
