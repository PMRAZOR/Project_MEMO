<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" md="8">
        <v-card class="profile-page">
          <v-toolbar title="프로필 수정" dark></v-toolbar>
          <v-card-text>
            <v-container>
              <v-row justify="center" align="center">
                <v-col cols="12" class="text-center">
                  <v-avatar
                    size="150"
                    class="profile-avatar mb-4"
                    @click="$refs.fileInput.click()"
                  >
                    <v-img
                      v-if="previewImage || user.profileImage"
                      :src="getImageUrl(previewImage || user.profileImage)"
                      cover
                    >
                      <template v-slot:placeholder>
                        <v-row class="fill-height ma-0" align="center" justify="center">
                          <v-progress-circular indeterminate color="grey-lighten-5"></v-progress-circular>
                        </v-row>
                      </template>
                    </v-img>
                    <v-icon
                      v-else
                      size="100"
                      color="grey-darken-1"
                    >mdi-account-circle</v-icon>
                    <div class="avatar-overlay">
                      <v-icon color="white" size="30">mdi-camera</v-icon>
                    </div>
                  </v-avatar>
                  <input
                    ref="fileInput"
                    type="file"
                    accept="image/*"
                    class="d-none"
                    @change="onFileInputChange"
                  />
                </v-col>
                
                <v-col cols="12" sm="8" md="6">
                  <v-text-field
                    v-model="profile.nickname"
                    clearable
                    label="닉네임"
                    :rules="[(v) => !!v || '닉네임은 필수 입력입니다']"
                    variant="outlined"
                    hide-details="auto"
                    class="mb-4"
                  />
                </v-col>
              </v-row>
            </v-container>
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="error" text @click="confirmDelete">회원탈퇴</v-btn>
            <v-btn @click="$router.push('/')">취소</v-btn>
            <v-btn color="primary" @click="saveProfile">저장</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- 탈퇴 확인 다이얼로그 -->
    <v-dialog v-model="showDeleteConfirm" max-width="300">
      <v-card>
        <v-card-title>회원 탈퇴</v-card-title>
        <v-card-text>정말로 탈퇴하시겠습니까?</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="showDeleteConfirm = false">취소</v-btn>
          <v-btn color="error" @click="deleteMemberAndLogout">탈퇴</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 알림 스낵바 -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
    >
      {{ snackbar.message }}
      <template v-slot:actions>
        <v-btn
          variant="text"
          @click="snackbar.show = false"
        >
          닫기
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import authApi from "@/api/authApi";

export default {
  name: "MyPage",
  data() {
    return {
      profile: {
        nickname: '',
      },
      uploadFile: null,
      previewImage: null,
      showDeleteConfirm: false,
      snackbar: {
        show: false,
        message: '',
        color: 'success'
      }
    };
  },
  computed: {
    ...mapGetters('auth', ['user']),
  },
  methods: {
    ...mapActions('auth', ['updateProfile', 'deleteMember']),
    
    onFileInputChange(event) {
      const file = event.target.files[0];
      if (!file) return;

      this.uploadFile = file;
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewImage = e.target.result;
      };
      reader.readAsDataURL(file);
    },

    async fetchUserData() {
      try {
        const response = await authApi.get(`${this.user.id}`);
        const userData = response.data;
        this.profile.nickname = userData.nickname;
        this.previewImage = userData.profileImage;
      } catch (error) {
        this.showSnackbar('사용자 정보를 불러오는데 실패했습니다.', 'error');
      }
    },

    getImageUrl(imagePath) {
      if (!imagePath) return null;
      if (imagePath.startsWith('data:')) return imagePath;
      return `${process.env.VUE_APP_API_BASE_URL}/uploads/${imagePath}`;
    },

    showSnackbar(message, color = 'success') {
      this.snackbar.message = message;
      this.snackbar.color = color;
      this.snackbar.show = true;
    },

    async saveProfile() {
      if (!this.validateForm()) return;

      const formData = new FormData();
      formData.append('nickname', this.profile.nickname);
      if (this.uploadFile) {
        formData.append('profileImage', this.uploadFile);
      }

      const result = await this.updateProfile({
        id: this.user.id,
        formData
      });

      if (result.success) {
        this.showSnackbar('프로필이 성공적으로 수정되었습니다.');
        await this.fetchUserData();
      } else {
        this.showSnackbar(result.error || '프로필 수정에 실패했습니다.', 'error');
      }
    },

    confirmDelete() {
      this.showDeleteConfirm = true;
    },

    async deleteMemberAndLogout() {
      const result = await this.deleteMember(this.user.id);
      if (result.success) {
        this.$router.push('/');
      } else {
        this.showSnackbar('회원 탈퇴에 실패했습니다.', 'error');
      }
    },

    validateForm() {
      if (!this.profile.nickname?.trim()) {
        this.showSnackbar('닉네임을 입력해주세요.', 'error');
        return false;
      }
      return true;
    }
  },
  async created() {
    await this.fetchUserData();
  }
};
</script>

<style scoped>
.profile-page {
  width: 100%;
  margin: auto;
  padding: 20px;
}

.profile-avatar {
  cursor: pointer;
  position: relative;
  border: 3px solid #e0e0e0;
  transition: all 0.3s ease;
}

.profile-avatar:hover {
  border-color: #1976D2;
}

.profile-avatar:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  border-radius: 50%;
}
</style>