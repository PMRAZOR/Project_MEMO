<template>
  <v-toolbar title="회원가입" rounded dark></v-toolbar>
  <v-card-text>
    <v-text-field v-model="userId" :rules="rules" hide-details="auto" label="아이디" class="mb-4"></v-text-field>
    <v-text-field
      v-model="password"
      :rules="passwordRules"
      hide-details="auto"
      type="password"
      label="비밀번호"
      class="mb-4"
    ></v-text-field>
    <v-text-field v-model="email" :rules="emailRules" hide-details="auto" label="이메일" class="mb-4"></v-text-field>
    <v-text-field v-model="nickname" :rules="rules" hide-details="auto" label="닉네임" class="mb-4"></v-text-field>
    <v-alert v-if="registError" type="error" dense text class="mb-4">
      {{ registError }}
    </v-alert>
  </v-card-text>
  <v-card-actions class="justify-end">
    <v-btn-login class="button-text-size" @click="closeModal">닫기</v-btn-login>
    <v-btn-login class="button-text-size" @click="handleRegist">회원가입</v-btn-login>
  </v-card-actions>
</template>

<script>
import { mapActions } from "vuex";

export default {
  props: {
    isActive: {
      type: Object,
      required: true,
    },
  },
  data: () => ({
    userId: "",
    password: "",
    email: "",
    nickname: "",
    registError: "",
    rules: [
      (value) => !!value || "필수 입력 항목입니다.",
      (value) => (value && value.length >= 3) || "최소 3자 이상 입력해주세요.",
    ],
    passwordRules: [
      (value) => !!value || "필수 입력 항목입니다.",
      (value) => {
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
        return passwordRegex.test(value) || "최소 8자 이상, 영문자와 숫자를 포함해야 합니다.";
      },
    ],
    emailRules: [
      (value) => !!value || "필수 입력 항목입니다.",
      (value) => (value || "").length <= 30 || "최대 30자 입니다",
      (value) => {
        const pattern =
          /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return pattern.test(value) || "E-Mail 형식을 지켜주세요.";
      },
    ],
  }),
  methods: {
    ...mapActions("auth",["register"]), // vuex action 매핑
    closeModal() {
      this.registError = "";
      this.$emit("close");
    },
    async handleRegist() {
      try {
        const registData = {
          id: this.userId,
          password: this.password,
          email: this.email,
          nickname: this.nickname,
        };

        console.log("Sending registration data:", registData);

        const result = await this.register(registData);

        if (result.success) {
          console.log("회원가입 성공");
          this.registError = "";
          this.$emit("regist-success");
          this.$emit("close");
        } else {
          this.registError = result.error;
        }
      } catch (error) {
        console.error("회원가입 처리 중 오류 발생:", error);
        this.registError = "회원가입 처리 중 오류가 발생했습니다.";
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
