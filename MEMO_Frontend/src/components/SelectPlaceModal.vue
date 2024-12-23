<template>
  <v-dialog :model-value="modelValue" @update:model-value="updateModelValue">
    <v-card class="select-place-modal">
      <v-toolbar title="장소 선택하기" rounded dark></v-toolbar>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols="12" class="pb-1">
              <v-text-field
                v-model="searchQuery"
                hint="검색 할 장소 이름을 적어주세요"
                clearable
                label="이름"
                variant="outlined"
                @keyup.enter="searchPlace"
              >
                <template v-slot:append-inner>
                  <v-btn-login class="button-text-size" @click="searchPlace" variant="text" icon="mdi-magnify">
                    검색
                  </v-btn-login>
                </template>
              </v-text-field>
            </v-col>
          </v-row>

          <!-- 검색 결과 목록 -->
          <v-row v-if="searchResults && searchResults.length > 0">
            <v-col cols="12">
              <v-list>
                <v-list-item v-for="place in displayedResults" :key="place.id" @click="selectListItem(place)">
                  <template v-slot:default>
                    <v-list-item-title>{{ place.name }}</v-list-item-title>
                    <v-list-item-subtitle>{{ place.address }}</v-list-item-subtitle>
                  </template>
                </v-list-item>
              </v-list>
            </v-col>
          </v-row>

          <!-- 페이지네이션 -->
          <v-row v-if="totalPages > 1">
            <v-col cols="12" class="d-flex justify-center">
              <v-pagination
                v-model="currentPage"
                :length="totalPages"
                :total-visible="5"
                @update:model-value="handlePageChange"
              ></v-pagination>
            </v-col>
          </v-row>
          <v-card-actions>
            <v-row justify="center">
              <v-col cols="12" sm="4" class="d-flex justify-center pa-1">
                <v-btn-login class="button-text-size" @click="openSearchOfficialPlaceModal" block>
                  국가 공인 장소 추가하기
                </v-btn-login>
              </v-col>
              <v-col cols="12" sm="4" class="d-flex justify-center pa-1">
                <v-btn-login class="button-text-size" @click="addPlaceFromMap" block> 지도에서 추가하기 </v-btn-login>
              </v-col>
              <v-col cols="12" sm="4" class="d-flex justify-center pa-1">
                <v-btn-login class="button-text-size" @click="addCustomPlace" block>
                  사용자 지정 장소 추가하기
                </v-btn-login>
              </v-col>
            </v-row>
          </v-card-actions>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn-login class="button-text-size" @click="cancel">취소</v-btn-login>
        <v-btn-login class="button-text-size" @click="selectPlace">선택</v-btn-login>
      </v-card-actions>
    </v-card>
  </v-dialog>
  <SearchOfficialPlaceModal v-model="showSearchOfficialPlaceModal" @place-selected="onOfficialPlaceSelected" />
  <CustomPlaceModal v-model="showCustomPlaceModal" @place-selected="onCustomPlaceSelected" />
  <PlaceFromMapModal v-model="showPlaceFromMapModal" @place-selected="onMapPlaceSelected" />
  <v-snackbar v-model="showSnackbar" :color="snackbarColor" timeout="3000">
    {{ snackbarMessage }}

    <template v-slot:actions>
      <v-btn-login variant="text" @click="showSnackbar = false"> 닫기 </v-btn-login>
    </template>
  </v-snackbar>
</template>

<script>
import SearchOfficialPlaceModal from "./SearchOfficialPlaceModal.vue";
import CustomPlaceModal from "./CustomPlaceModal.vue";
import PlaceFromMapModal from "./PlaceFromMapModal.vue";
import axios from "axios";

export default {
  name: "SelectPlaceModal",
  components: {
    SearchOfficialPlaceModal,
    CustomPlaceModal,
    PlaceFromMapModal,
  },
  props: {
    modelValue: Boolean,
  },
  data() {
    return {
      searchQuery: "",
      description: "",
      placeId: null,
      showSearchOfficialPlaceModal: false,
      showCustomPlaceModal: false,
      showPlaceFromMapModal: false,
      searchResults: [],
      currentPage: 1,
      itemsPerPage: 5,
      showSnackbar: false,
      snackbarMessage: "",
      snackbarColor: "success",
    };
  },

  computed: {
    totalPages() {
      return Math.ceil(this.searchResults.length / this.itemsPerPage);
    },
    displayedResults() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.searchResults.slice(start, end);
    },
  },

  methods: {
    updateModelValue(value) {
      this.$emit("update:modelValue", value);
    },
    cancel() {
      this.updateModelValue(false);
    },
    addOfficialPlace() {
      console.log("Adding official place");
    },
    addPlaceFromMap() {
      this.showPlaceFromMapModal = true;
    },
    addCustomPlace() {
      this.showCustomPlaceModal = true;
    },
    async onCustomPlaceSelected(place) {
      try {
        await axios.post("https://api.ssafy.me/places", place);
        this.snackbarColor = "success";
        this.snackbarMessage = "사용자 지정 장소가 성공적으로 등록되었습니다.";
        this.showSnackbar = true;
        this.showCustomPlaceModal = false;
        await this.searchPlace(); // 목록 새로고침
      } catch (error) {
        console.error("사용자 지정 장소 저장 중 오류 발생:", error);
        this.snackbarColor = "error";
        this.snackbarMessage = "장소 저장 중 오류가 발생했습니다.";
        this.showSnackbar = true;
      }
    },
    selectPlace() {
      // 선택된 장소 정보를 부모 컴포넌트로 전달
      this.$emit("place-selected", {
        id: this.placeId,
        description: this.description,
      });
      this.updateModelValue(false);
    },
    openSearchOfficialPlaceModal() {
      this.showSearchOfficialPlaceModal = true;
    },
    async searchPlace() {
      try {
        const response = await axios.get(`https://api.ssafy.me/places?keyword=${encodeURIComponent(this.searchQuery)}`);
        this.searchResults = response.data || [];
        this.currentPage = 1; // 검색 시 첫 페이지로 초기화
        console.log("Search Results:", this.searchResults);
      } catch (error) {
        console.error("Error searching places:", error);
        this.searchResults = [];
      }
    },

    handlePageChange(page) {
      this.currentPage = page;
    },

    async onOfficialPlaceSelected(place) {
      try {
        // 유효성 검사
        if (!place || !place.title || !place.addr1 || !place.mapy || !place.mapx || !place.contenttypeid) {
          console.warn("유효하지 않은 장소 데이터:", place);
          return;
        }

        const placeData = {
          name: place.title,
          address: place.addr1,
          latitude: parseFloat(place.mapy),
          longitude: parseFloat(place.mapx),
          placeType: parseInt(place.contenttypeid),
        };

        // DB에 저장
        await axios.post("https://api.ssafy.me/places", placeData);

        // 성공 메시지 표시
        this.snackbarColor = "success";
        this.snackbarMessage = "장소가 성공적으로 등록되었습니다.";
        this.showSnackbar = true;

        this.showSearchOfficialPlaceModal = false;
        await this.searchPlace();
      } catch (error) {
        console.error("장소 저장 중 오류 발생:", error);

        // 에러 메시지 표시
        this.snackbarColor = "error";
        if (error.response && error.response.status === 500) {
          this.snackbarMessage = "이미 등록된 장소입니다.";
        } else {
          this.snackbarMessage = "장소 저장 중 오류가 발생했습니다.";
        }
        this.showSnackbar = true;
      }
    },
    async onMapPlaceSelected(place) {
      try {
        const placeData = {
          name: place.place_name,
          address: place.road_address_name || place.address_name,
          latitude: parseFloat(place.y),
          longitude: parseFloat(place.x),
          placeType: 12,
        };

        // DB에 저장
        await axios.post("https://api.ssafy.me/places", placeData);

        // 성공 메시지 표시
        this.snackbarColor = "success";
        this.snackbarMessage = "지도에서 선택한 장소가 성공적으로 등록되었습니다.";
        this.showSnackbar = true;

        this.showPlaceFromMapModal = false;
        await this.searchPlace(); // 목록 새로고침
      } catch (error) {
        console.error("장소 저장 중 오류 발생:", error);

        // 에러 메시지 표시
        this.snackbarColor = "error";
        if (error.response && error.response.status === 500) {
          this.snackbarMessage = "이미 등록된 장소입니다.";
        } else {
          this.snackbarMessage = "장소 저장 중 오류가 발생했습니다.";
        }
        this.showSnackbar = true;
      }
    },

    selectListItem(place) {
      this.placeId = place.id;
      this.description = place.address;
      this.$emit("place-selected", {
        id: place.id,
        name: place.name,
        address: place.address,
        latitude: place.latitude,
        longitude: place.longitude,
        placeType: place.placeType,
      });
      this.updateModelValue(false);
    },
  },
};
</script>

<style scoped>
.select-place-modal {
  max-width: 600px;
  width: 100%;
  margin: auto;
  padding: 20px;
}
</style>
