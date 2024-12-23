<template>
  <v-card class="not-logged-in-modal">
    <v-toolbar title="로그인이 필요한 서비스 입니다" rounded dark></v-toolbar>
    <v-card-text> 이 기능을 사용하려면 로그인이 필요합니다. </v-card-text>
    <v-card-actions class="justify-end">
      <v-btn-login class="button-text-size" @click="openLoginModal">로그인</v-btn-login>
      <v-btn-login class="button-text-size" @click="$emit('close')">닫기</v-btn-login>
    </v-card-actions>
  </v-card>
</template>

<script>
export default {
  name: "NotLoggedInModal",
  methods: {
    openLoginModal() {
      this.$emit("open-login-modal");
      this.$emit("close");
    },
  },
};
</script>

<style scoped>
.not-logged-in-modal {
  max-width: 600px;
  width: 100%;
  margin: auto;
  padding: 20px;
}
</style>
