<template>
  <div class="timeline-container">
    <v-responsive class="mx-auto pt-2" width="100%">
      <v-text-field
        v-model="title"
        class="mb-2"
        hint="타임라인 제목을 입력해 주세요"
        clearable
        label="제목"
        :rules="[(v) => !!v || '제목은 필수 입력입니다']"
        variant="outlined"
      />
      <v-text-field
        v-model="description"
        class="mb-2"
        hint="타임라인 설명을 입력해 주세요"
        clearable
        label="설명"
        :rules="[(v) => !!v || '설명은 필수 입력입니다']"
        variant="outlined"
      />
      <v-row>
        <v-col cols="6">
          <v-menu
            v-model="startMenu"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            min-width="auto"
          >
            <template v-slot:activator="{ props }">
              <v-text-field
                :model-value="formattedStartDate"
                label="시작 날짜"
                prepend-icon="mdi-calendar"
                readonly
                v-bind="props"
                :rules="[(v) => !!startDate || '시작 날짜는 필수 입력입니다']"
              ></v-text-field>
            </template>
            <v-date-picker
              v-model="startDate"
              @update:model-value="startMenu = false"
              elevation="10"
              show-adjacent-months
              color="grey-darken-1"
              :header="'시작 날짜'"
            ></v-date-picker>
          </v-menu>
        </v-col>
        <v-col cols="6">
          <v-menu
            v-model="endMenu"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            min-width="auto"
          >
            <template v-slot:activator="{ props }">
              <v-text-field
                :model-value="formattedEndDate"
                label="종료 날짜"
                prepend-icon="mdi-calendar"
                readonly
                v-bind="props"
                :rules="[(v) => !!endDate || '종료 날짜는 필수 입력입니다']"
              ></v-text-field>
            </template>
            <v-date-picker
              v-model="endDate"
              @update:model-value="endMenu = false"
              elevation="10"
              show-adjacent-months
              color="grey-darken-1"
              :header="'종료 날짜'"
            ></v-date-picker>
          </v-menu>
        </v-col>
      </v-row>
      <div v-if="visits.length > 0" class="visits-preview mt-4">
        <v-subheader>방문 목록</v-subheader>
        <div class="visits-cards">
          <v-card v-for="visit in visits" :key="visit.placeId" class="visit-card mb-4 elevation-5">
            <v-carousel
              v-if="visit.photos && visit.photos.length > 0"
              height="200"
              width="100%"
              hide-delimiter-background
              show-arrows="hover"
            >
              <template v-slot:prev="{ props }">
                <v-btn
                  variant="text"
                  color="white"
                  icon="mdi-chevron-left"
                  v-bind="props"
                  class="carousel-nav-btn"
                ></v-btn>
              </template>

              <template v-slot:next="{ props }">
                <v-btn
                  variant="text"
                  color="white"
                  icon="mdi-chevron-right"
                  v-bind="props"
                  class="carousel-nav-btn"
                ></v-btn>
              </template>

              <v-carousel-item v-for="(photo, index) in visit.photos" :key="index" cover>
                <v-img :src="getPhotoPreview(photo)" height="200" cover>
                  <template v-slot:placeholder>
                    <div class="d-flex align-center justify-center fill-height">
                      <v-progress-circular indeterminate color="grey-lighten-4"></v-progress-circular>
                    </div>
                  </template>
                </v-img>
              </v-carousel-item>
            </v-carousel>

            <v-card-title>{{ visit.title }}</v-card-title>
            <v-card-text>
              <div class="visit-metadata mb-4">
                <div class="metadata-item">
                  <v-icon small color="grey">mdi-calendar</v-icon>
                  <span class="ml-2">{{ formatDate(visit.visitDate) }}</span>
                </div>
                <div class="metadata-item" v-if="visit.place?.name || visit.placeName">
                  <v-icon size="small" color="grey">mdi-map-marker</v-icon>
                  <span class="ml-2">{{ visit.place?.name || visit.placeName }}</span>
                </div>
              </div>
              <p>{{ visit.description }}</p>
              <div v-if="visit.place?.address || visit.placeAddress" class="address-info mt-2">
                <v-icon size="small" color="grey">mdi-map-marker-outline</v-icon>
                <span class="text-caption-address ml-2">{{ visit.place?.address || visit.placeAddress }}</span>
              </div>
            </v-card-text>
          </v-card>
        </div>
      </div>
    </v-responsive>
    <div class="speed-dial-container">
      <v-speed-dial v-model="fab" direction="top" transition="slide-y-reverse-transition" elevation="5">
        <template v-slot:activator="{ props }">
          <v-btn v-bind="props" class="fab-button" icon="mdi-plus" />
        </template>
        <v-btn key="1" class="fab-button" icon="mdi-pencil" size="small" @click="openModal" />
        <v-btn key="2" class="fab-button" icon="mdi-check" size="small" @click="saveTimeline" />
        <v-btn key="3" class="fab-button" icon="mdi-close" size="small" />
      </v-speed-dial>
    </div>
    <!-- <v-btn class="fab-button" icon="mdi-plus" variant="tonal" elevation="5" @click="openModal"></v-btn> -->
    <VisitModal v-model="showModal" @save-visit="addVisit" @update:snackbar="updateSnackbar" />
    <v-snackbar v-model="showSnackbar" :color="snackbarColor" timeout="3000">
      {{ snackbarMessage }}
      <template v-slot:actions>
        <v-btn-login variant="text" @click="showSnackbar = false">닫기</v-btn-login>
      </template>
    </v-snackbar>
  </div>
</template>

<script>
import VisitModal from "./VisitModal.vue";
import api from "@/api/axios";
import { mapGetters } from "vuex";

export default {
  name: "TimelineView",
  components: {
    VisitModal,
  },
  data() {
    return {
      startDate: null,
      endDate: null,
      startMenu: false,
      endMenu: false,
      showModal: false,
      visits: [],
      fab: false,
      title: "", // 타임라인 제목을 저장할 변수 추가
      description: "", // 타임라인 설명을 저장할 변수 추가
      saveError: "",
      showSnackbar: false,
      snackbarMessage: "",
      snackbarColor: "success",
    };
  },
  computed: {
    formattedStartDate() {
      return this.formatDate(this.startDate);
    },
    formattedEndDate() {
      return this.formatDate(this.endDate);
    },
    ...mapGetters("auth",["id"]),
    userId() {
      // id가 없으면 콘솔에 기록
      if (!this.id) {
        console.warn("User ID is not available:", this.id);
      }
      return this.id;
    },
  },
  methods: {
    updateSnackbar({ show, message, color }) {
      this.showSnackbar = show;
      this.snackbarMessage = message;
      this.snackbarColor = color;
    },
    openModal() {
      this.showModal = true;
    },
    formatDate(date) {
      if (!date) return "";
      try {
        const targetDate = new Date(date);
        const year = targetDate.getFullYear();
        const month = String(targetDate.getMonth() + 1).padStart(2, "0");
        const day = String(targetDate.getDate()).padStart(2, "0");
        return `${year}년 ${month}월 ${day}일`;
      } catch (error) {
        console.error("Date formatting error:", error);
        return "";
      }
    },
    addVisit(visitData) {
      if (visitData instanceof FormData) {
        const visitDataJson = JSON.parse(visitData.get("visitData"));
        const photos = Array.from(visitData.getAll("photos"));
        const place = JSON.parse(visitData.get("place") || "{}");

        const newVisit = {
          ...visitDataJson,
          photos: photos,
          placeId: place.id || visitDataJson.placeId,
          place: {
            id: place.id,
            name: place.name,
            address: place.address,
            latitude: place.latitude,
            longitude: place.longitude,
            placeType: place.placeType,
          },
          placeName: place.name,
          placeAddress: place.address,
          placeLatitude: place.latitude,
          placeLongitude: place.longitude,
          placeType: place.placeType,
        };

        console.log("Place info:", place);
        console.log("Added visit:", newVisit);

        this.visits.push(newVisit);
      } else {
        const newVisit = {
          ...visitData,
          place: {
            id: visitData.placeId,
            name: visitData.placeName,
            address: visitData.placeAddress,
            latitude: visitData.placeLatitude,
            longitude: visitData.placeLongitude,
            placeType: visitData.placeType,
          },
        };

        console.log("Added visit from direct data:", newVisit);
        this.visits.push(newVisit);
      }
    },
    resetForm() {
      this.title = "";
      this.description = "";
      this.startDate = null;
      this.endDate = null;
      this.visits = [];
    },
    formatDateForDB(date) {
      if (!date) return null;
      if (typeof date === "string") {
        // 이미 YYYY-MM-DD 형식이면 그대로 반환
        if (/^\d{4}-\d{2}-\d{2}$/.test(date)) {
          return date;
        }
        // ISO 문자열이면 YYYY-MM-DD 형식으로 변환
        return date.split("T")[0];
      }
      // Date 객체면 YYYY-MM-DD 형식으로 변환
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
    },
    async saveTimeline() {
      if (!this.validateTimelineForm()) {
        return;
      }
      if (!this.userId) {
        console.error("User ID is not available");
        this.showSnackbar = true;
        this.snackbarColor = "error";
        this.snackbarMessage = "사용자 정보를 불러올 수 없습니다. 다시 로그인해주세요.";
        return;
      }
      try {
        // 저장 전 visits 데이터 확인
        console.log("=== Pre-save Visits Check ===");
        console.log("Current User ID:", this.id);
        this.visits.forEach((visit, index) => {
          console.log(`Visit ${index}:`, {
            title: visit.title,
            placeId: visit.placeId,
            photos: visit.photos
              ? {
                  count: visit.photos.length,
                  details: visit.photos.map((photo) => ({
                    isFile: photo instanceof File,
                    name: photo.name,
                    size: photo.size,
                    type: photo.type,
                  })),
                }
              : "No photos",
          });
        });

        const formData = new FormData();

        const timelineData = {
          post: {
            id: null,
            userId: this.id,
            title: this.title.trim() || "제목 없음",
            description: this.description.trim() || "설명 없음",
            startDate: this.formatDateForDB(this.startDate),
            endDate: this.formatDateForDB(this.endDate),
            createdAt: null,
          },
          visits: this.visits.map((visit) => ({
            title: visit.title.trim() || "방문 제목 없음",
            description: visit.description.trim() || "방문 설명 없음",
            visitDate: this.formatDateForDB(visit.visitDate),
            place: {
              id: visit.placeId,
              name: visit.place?.name || visit.placeName || "",
              address: visit.place?.address || visit.placeAddress || "",
              latitude: visit.place?.latitude || visit.placeLatitude || 0,
              longitude: visit.place?.longitude || visit.placeLongitude || 0,
              placeType: (visit.place?.placeType || visit.placeType || 0).toString(),
            },
            photos: [],
          })),
        };

        console.log("Saving timeline with userId:", timelineData.post.userId);
        formData.append("timelineData", JSON.stringify(timelineData));

        // 사진 파일들 추가 및 로깅
        console.log("=== Adding Photos to FormData ===");
        this.visits.forEach((visit, visitIndex) => {
          if (visit.photos && visit.photos.length > 0) {
            visit.photos.forEach((photo, photoIndex) => {
              if (photo instanceof File) {
                const photoKey = `photo_${visitIndex}_${visit.placeId}_${photoIndex}`;
                formData.append(photoKey, photo);
                console.log(`Adding photo ${photoKey}:`, {
                  fileName: photo.name,
                  fileSize: `${(photo.size / 1024).toFixed(2)}KB`,
                  fileType: photo.type,
                });
              } else {
                console.log(`Invalid photo at visit ${visitIndex}, index ${photoIndex}:`, photo);
              }
            });
          } else {
            console.log(`No photos for visit ${visitIndex}`);
          }
        });

        // 최종 FormData 확인
        console.log("=== Final FormData Content ===");
        for (let [key, value] of formData.entries()) {
          if (key === "timelineData") {
            console.log("Timeline Data:", JSON.parse(value));
          } else {
            console.log(
              `${key}:`,
              value instanceof File
                ? {
                    name: value.name,
                    size: value.size,
                    type: value.type,
                  }
                : value
            );
          }
        }

        const response = await api.post("/posts", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });

        console.log("Timeline saved successfully:", response);
        this.showSnackbar = true;
        this.snackbarColor = "success";
        this.snackbarMessage = "타임라인이 성공적으로 저장되었습니다.";
        const timelineId = response.data.id;
        this.$router.push({
          name: "TimelineDetail",
          params: { id: timelineId },
        });
      } catch (error) {
        console.error("Error saving timeline:", error);
        this.showSnackbar = true;
        this.snackbarColor = "error";
        this.snackbarMessage = "타임라인 저장 중 오류가 발생했습니다.";
      }
    },
    validateTimelineForm() {
      if (!this.title?.trim()) {
        this.showSnackbar = true;
        this.snackbarColor = "error";
        this.snackbarMessage = "타임라인 제목을 입력해주세요";
        return false;
      }
      if (!this.description?.trim()) {
        this.showSnackbar = true;
        this.snackbarColor = "error";
        this.snackbarMessage = "타임라인 설명을 입력해주세요";
        return false;
      }
      if (!this.startDate) {
        this.showSnackbar = true;
        this.snackbarColor = "error";
        this.snackbarMessage = "시작 날짜를 선택해주세요";
        return false;
      }
      if (!this.endDate) {
        this.showSnackbar = true;
        this.snackbarColor = "error";
        this.snackbarMessage = "종료 날짜를 선택해주세요";
        return false;
      }
      // 시작 날짜가 종료 날짜보다 늦은 경우 체크
      if (new Date(this.startDate) > new Date(this.endDate)) {
        this.showSnackbar = true;
        this.snackbarColor = "error";
        this.snackbarMessage = "시작 날짜는 종료 날짜보다 늦을 수 없습니다";
        return false;
      }
      if (this.visits.length === 0) {
        this.showSnackbar = true;
        this.snackbarColor = "error";
        this.snackbarMessage = "최소 하나 이상의 방문 정보를 추가해주세요";
        return false;
      }
      return true;
    },
    getPhotoPreview(photo) {
      if (photo instanceof File) {
        return URL.createObjectURL(photo);
      }
      return photo;
    },
  },
  // created() {
  //   console.log("Timeline component created - Current ID:", this.id);
  //   console.log("Full user state:", this.$store.state.user);
  //   console.log("Is logged in:", this.$store.state.isLoggedIn);
  // },
};
</script>

<style scoped>
.timeline-container {
  position: relative;
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
  width: 100%;
}

.v-responsive {
  width: 100%;
}

.speed-dial-container {
  position: absolute;
  bottom: 0px;
  right: 0px;
  z-index: 100;
}

.fab-button {
  background-color: #2c2c2c !important;
  color: white !important;
}

.fab-button:hover {
  background-color: #505050 !important;
}

:deep(.v-date-picker-controls .v-btn) {
  color: white !important;
}
.visits-preview {
  padding: 0 16px;
}

.visits-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.visit-card {
  width: 100%;
}

.carousel-nav-btn {
  background: rgba(0, 0, 0, 0.5);
  opacity: 0.7;
}

.carousel-nav-btn:hover {
  background: rgba(0, 0, 0, 0.5);
  opacity: 1;
}

.location-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  color: #666;
}

.text-caption-address {
  font-size: 0.875rem;
}

.visit-date {
  display: flex;
  align-items: center;
  color: #666;
  font-size: 0.875rem;
}
.visit-metadata {
  display: flex;
  gap: 16px;
  color: #666;
  font-size: 0.875rem;
  border-bottom: 1px solid #9e9e9e65;
  padding-bottom: 12px;
}

.metadata-item {
  display: flex;
  align-items: center;
}

.address-info {
  display: flex;
  align-items: center;
  color: #666;
  font-size: 0.875rem;
}

.text-caption-address {
  color: #666;
  font-size: 0.875rem;
}
</style>
