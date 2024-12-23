<template>
  <v-dialog :model-value="modelValue" @update:model-value="updateModelValue">
    <v-card class="visit-modal">
      <v-toolbar title="방문 정보 입력" rounded dark></v-toolbar>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols="12" class="pb-0">
              <v-text-field
                v-model="visit.title"
                clearable
                label="제목"
                :rules="[(v) => !!v || '제목은 필수 입력입니다']"
                variant="outlined"
              />
            </v-col>
            <v-col cols="12" class="pb-0">
              <v-menu
                v-model="dateMenu"
                :close-on-content-click="false"
                transition="scale-transition"
                offset-y
                min-width="auto"
              >
                <template v-slot:activator="{ props }">
                  <v-text-field
                    :model-value="formattedVisitDate"
                    label="방문 날짜"
                    readonly
                    v-bind="props"
                    :rules="[(v) => !!visit.visitDate || '방문 날짜는 필수 입력입니다']"
                    variant="outlined"
                  ></v-text-field>
                </template>
                <v-date-picker
                  v-model="visit.visitDate"
                  @update:model-value="dateMenu = false"
                  elevation="10"
                  show-adjacent-months
                  color="grey-darken-1"
                  :header="'방문 날짜'"
                ></v-date-picker>
              </v-menu>
            </v-col>
            <v-col cols="12" class="pb-0">
              <v-textarea
                v-model="visit.description"
                label="방문 내용"
                :rules="[(v) => !!v || '방문 내용은 필수 입력입니다']"
                variant="outlined"
              ></v-textarea>
            </v-col>
            <v-col cols="12" class="pt-1 pb-4">
              <div v-if="selectedPlace" class="selected-place-info">
                <v-card variant="outlined" class="pa-4" :style="{ borderColor: 'GrayText' }">
                  <div class="text-subtitle-1">{{ selectedPlace.name }}</div>
                  <div class="text-body-2">{{ selectedPlace.address }}</div>
                </v-card>
              </div>
              <div class="d-flex justify-center">
                <v-btn-login class="button-text-size" @click="openSelectPlaceModal">
                  {{ selectedPlace ? "장소 변경하기" : "장소 선택하기" }}
                </v-btn-login>
              </div>
            </v-col>
          </v-row>
          <v-col cols="12">
            <div class="scroll-container" ref="scrollContainer" @wheel.prevent="handleWheel">
              <div class="d-flex gap-4">
                <!-- 이미지 미리보기 카드들 -->
                <v-card
                  v-for="(preview, index) in previewImages"
                  :key="index"
                  width="120"
                  height="120"
                  class="image-card"
                >
                  <v-img :src="preview" cover height="100%" width="100%">
                    <template v-slot:placeholder>
                      <v-row class="fill-height ma-0" align="center" justify="center">
                        <v-progress-circular indeterminate color="grey-lighten-5"></v-progress-circular>
                      </v-row>
                    </template>
                  </v-img>
                  <v-btn
                    icon="mdi-close"
                    size="x-small"
                    variant="tonal"
                    class="remove-image-btn"
                    @click="removeImage(index)"
                  >
                    <v-icon>mdi-close</v-icon>
                  </v-btn>
                </v-card>

                <!-- 추가 버튼 카드 -->
                <v-card
                  width="120"
                  height="120"
                  class="add-image-card"
                  variant="outlined"
                  @click="$refs.fileInput.click()"
                >
                  <v-container class="fill-height">
                    <v-row align="center" justify="center">
                      <v-col class="text-center">
                        <v-icon size="40" color="grey-darken-1"> mdi-plus </v-icon>
                        <div class="text-caption text-grey-darken-1 mt-2">사진 추가</div>
                      </v-col>
                    </v-row>
                  </v-container>
                </v-card>
              </div>
            </div>

            <!-- 숨겨진 파일 입력 -->
            <input ref="fileInput" type="file" multiple accept="image/*" class="d-none" @change="onFileInputChange" />
          </v-col>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn-login class="button-text-size" @click="cancel">취소</v-btn-login>
        <v-btn-login class="button-text-size" @click="saveVisit">저장</v-btn-login>
      </v-card-actions>
    </v-card>
  </v-dialog>
  <SelectPlaceModal v-model="showSelectPlaceModal" @place-selected="onPlaceSelected" />
</template>

<script>
import SelectPlaceModal from "./SelectPlaceModal.vue";

export default {
  name: "VisitModal",
  components: {
    SelectPlaceModal,
  },
  props: {
    modelValue: Boolean,
  },
  emits: ["update:modelValue", "save-visit", "update:snackbar"],
  data() {
    return {
      dateMenu: false,
      showSelectPlaceModal: false,
      uploadFiles: [],
      previewImages: [], // imagePreviewUrls를 previewImages로 변경
      selectedPlace: null,
      showSnackbar: false,
      snackbarMessage: "",
      visit: {
        title: "",
        description: "",
        visitDate: null,
        placeId: null,
        photos: [],
      },
    };
  },
  computed: {
    formattedVisitDate() {
      return this.formatDate(this.visit.visitDate);
    },
  },
  methods: {
    clearImages() {
      this.previewImages = [];
      this.uploadFiles = [];
      if (this.imagePreviewUrls) {
        this.imagePreviewUrls = [];
      }
    },
    onFileInputChange(event) {
      const files = event.target.files;
      if (!files.length) return;

      // 새로운 파일들에 대해서만 처리
      Array.from(files).forEach((file) => {
        if (file && file instanceof File) {
          const reader = new FileReader();

          reader.onload = (e) => {
            this.previewImages.push(e.target.result);

            // FileList 업데이트
            const dataTransfer = new DataTransfer();
            if (this.uploadFiles) {
              Array.from(this.uploadFiles).forEach((existingFile) => {
                dataTransfer.items.add(existingFile);
              });
            }
            dataTransfer.items.add(file);
            this.uploadFiles = dataTransfer.files;
          };

          reader.readAsDataURL(file);
        }
      });

      // 입력 초기화
      event.target.value = "";
    },

    removeImage(index) {
      // FileList를 배열로 변환
      const filesArray = Array.from(this.uploadFiles);
      filesArray.splice(index, 1);

      // 새로운 FileList 생성
      const dataTransfer = new DataTransfer();
      filesArray.forEach((file) => {
        if (file instanceof File) {
          dataTransfer.items.add(file);
        }
      });

      // 상태 업데이트
      this.uploadFiles = dataTransfer.files;
      this.previewImages.splice(index, 1);
    },

    updateModelValue(value) {
      this.$emit("update:modelValue", value);
    },
    cancel() {
      this.resetForm();
      this.updateModelValue(false);
    },
    async saveVisit() {
      if (!this.validateForm()) {
        return;
      }

      try {
        const formData = new FormData();

        // 날짜 처리
        let visitDate = null;
        if (this.visit.visitDate) {
          const date = new Date(this.visit.visitDate);
          date.setHours(9);
          visitDate = date.toISOString();
        }

        const visitData = {
          title: this.visit.title,
          description: this.visit.description,
          visitDate: visitDate,
          placeId: this.visit.placeId,
          placeName: this.selectedPlace.name,
          placeAddress: this.selectedPlace.address,
          placeLatitude: this.selectedPlace.latitude, // 필요한 경우 추가
          placeLongitude: this.selectedPlace.longitude, // 필요한 경우 추가
          placeType: this.selectedPlace.placeType, // 필요한 경우 추가
        };
        const placeData = {
          id: this.selectedPlace?.id,
          name: this.selectedPlace?.name,
          address: this.selectedPlace?.address,
          latitude: this.selectedPlace?.latitude,
          longitude: this.selectedPlace?.longitude,
          placeType: this.selectedPlace?.placeType,
        };

        formData.append("place", JSON.stringify(placeData));
        formData.append("visitData", JSON.stringify(visitData));

        // 파일 추가 전 로깅
        console.log("=== Adding files to FormData ===");
        if (this.uploadFiles && this.uploadFiles.length > 0) {
          Array.from(this.uploadFiles).forEach((file, index) => {
            formData.append("photos", file);
            console.log(`Adding file ${index}:`, {
              name: file.name,
              size: file.size,
              type: file.type,
            });
          });
        }

        this.$emit("save-visit", formData);
        this.resetForm();
        this.updateModelValue(false);
      } catch (error) {
        console.error("Error saving visit:", error);
        this.$emit("update:snackbar", {
          show: true,
          message: "저장 중 오류가 발생했습니다",
          color: "error",
        });
      }
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
        return "Invalid Date";
      }
    },
    openSelectPlaceModal() {
      this.showSelectPlaceModal = true;
    },
    onPlaceSelected(place) {
      this.visit.placeId = place.id;
      // this.visit.description = place.address;
      this.selectedPlace = {
        name: place.name,
        address: place.address,
      };
      this.showSelectPlaceModal = false;
    },
    resetForm() {
      this.clearImages();
      this.visit = {
        title: "",
        description: "",
        visitDate: null,
        placeId: null,
        photos: [],
      };
      this.selectedPlace = null;
    },
    handleWheel(event) {
      const container = this.$refs.scrollContainer;
      if (container) {
        // shift 키를 누른 상태에서의 휠 이벤트나 일반 휠 이벤트 모두 처리
        const delta = event.shiftKey ? event.deltaX : event.deltaY;
        container.scrollLeft += delta;
      }
    },
    validateForm() {
      if (!this.visit.title?.trim()) {
        this.$emit("update:snackbar", {
          show: true,
          message: "제목을 입력해주세요",
          color: "error",
        });
        return false;
      }
      if (!this.visit.visitDate) {
        this.$emit("update:snackbar", {
          show: true,
          message: "방문 날짜를 선택해주세요",
          color: "error",
        });
        return false;
      }
      if (!this.visit.description?.trim()) {
        this.$emit("update:snackbar", {
          show: true,
          message: "방문 내용을 입력해주세요",
          color: "error",
        });
        return false;
      }
      if (!this.selectedPlace) {
        this.$emit("update:snackbar", {
          show: true,
          message: "장소를 선택해주세요",
          color: "error",
        });
        return false;
      }
      return true;
    },
    beforeUnmount() {
      this.clearImages();
    },
  },
};
</script>

<style scoped>
.visit-modal {
  max-width: 600px;
  width: 100%;
  margin: auto;
  padding: 20px;
}
.selected-place-info {
  margin-bottom: 16px;
}

.scroll-container {
  width: 100%;
  overflow-x: auto;
  white-space: nowrap;
  padding-bottom: 8px; /* 스크롤바 여유 공간 */
}

.scroll-container > div {
  display: inline-flex;
  min-width: min-content;
}

.image-card {
  position: relative;
  flex-shrink: 0;
}

.add-image-card {
  flex-shrink: 0;
  border: 2px dashed #e0e0e0;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
}

.add-image-card:hover {
  border-color: #9e9e9e;
  background-color: #f5f5f5;
}

.remove-image-btn {
  position: absolute !important;
  top: 8px;
  right: 8px;
  z-index: 1;
  background-color: rgba(255, 255, 255, 0.5) !important;
}

.gap-4 {
  gap: 16px;
}

.scroll-container::-webkit-scrollbar {
  height: 6px;
}

.scroll-container::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}

.scroll-container::-webkit-scrollbar-thumb {
  background: rgba(80, 80, 80, 0.822);
  border-radius: 3px;
}

.scroll-container::-webkit-scrollbar-thumb:hover {
  border-radius: 3px;
  background: rgba(170, 170, 170, 0.61);
}
</style>
