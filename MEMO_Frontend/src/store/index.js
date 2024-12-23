// store/index.js
import { createStore } from "vuex";
import VuexPersistence from "vuex-persist";
import board from './modules/board';
import auth from './modules/auth';

const vuexLocal = new VuexPersistence({
  storage: window.localStorage,
  modules: ['auth']  // auth 모듈만 로컬 스토리지에 저장
});

export default createStore({
  modules: {
    board,
    auth
  },
  plugins: [vuexLocal.plugin],
});