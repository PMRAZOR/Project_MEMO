<template>
  <v-toolbar title="로그인" rounded dark></v-toolbar>
  <v-card-text>
    <v-text-field v-model="userId" :rules="rules" hide-details="auto" label="아이디" class="mb-4"></v-text-field>
    <v-text-field v-model="password" type="password" label="비밀번호" class="mb-4"></v-text-field>
    <v-alert v-if="loginError" type="error" dense text class="mb-4">
      {{ loginError }}
    </v-alert>
  </v-card-text>
  <v-card-actions class="justify-end">
    <v-btn-login class="button-text-size" @click="closeModal">닫기</v-btn-login>
    <v-btn-login class="button-text-size" @click="handleLogin">로그인</v-btn-login>
  </v-card-actions>
</template>

<script>
import { mapActions } from "vuex";

export default {
  props: {
    isActive: {
      type: Boolean,
      required: true,
    },
  },
  data: () => ({
    userId: "",
    password: "",
    loginError: "",
    rules: [
      (value) => !!value || "필수 입력 항목입니다.",
      (value) => (value && value.length >= 3) || "최소 3자 이상 입력해주세요.",
    ],
  }),
  methods: {
    ...mapActions("auth", ["login"]),
    closeModal() {
      this.loginError = "";
      this.$emit("close");
    },
    async handleLogin() {
  try {
    const loginData = {
      id: this.userId,
      password: this.password,
    };

    const result = await this.login(loginData);

    if (result.success) {
      this.loginError = "";
      this.$emit("login-success");
      this.$emit("close");

      // 리다이렉트 처리
      const redirectPath = this.$route.query.redirect || "/";
      this.$router.push(redirectPath);
    } else {
      // 로그인 실패 시 에러 메시지 설정
      this.loginError = result.error || "로그인에 실패했습니다.";
    }
  } catch (error) {
    // 예외 발생 시 에러 메시지 설정
    console.error("Login error:", error);
    this.loginError = "로그인 중 오류가 발생했습니다.";
  }
},
  },
};
</script>

<style scoped>
.v-alert {
  margin-top: 16px;
}
.v-card-text {
  padding: 24px 32px;
}

.v-text-field {
  margin-bottom: 20px;
}

.button-text-size {
  font-size: 0.9rem !important;
}
</style>
