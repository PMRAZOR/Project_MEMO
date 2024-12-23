<template>
  <v-dialog :model-value="modelValue" @update:model-value="updateModelValue">
    <v-card class="custom-place-modal">
      <v-toolbar title="사용자 지정 장소 추가" rounded dark></v-toolbar>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols="12">
              <v-text-field
                v-model="place.name"
                hint="장소 이름을 입력해 주세요"
                clearable
                label="장소 이름"
                variant="outlined"
              />
            </v-col>
            <v-col cols="12">
              <v-select
                v-model="place.placeType"
                :items="placeTypes"
                item-title="name"
                item-value="id"
                label="장소 유형"
                hint="장소의 유형을 선택해주세요"
                variant="outlined"
                required
              ></v-select>
            </v-col>
            <v-col cols="12">
              <v-text-field
                v-model="place.address"
                hint="주소를 입력해 주세요"
                clearable
                label="주소"
                variant="outlined"
              />
            </v-col>
            <v-col cols="6">
              <v-text-field
                v-model="place.latitude"
                hint="위도를 입력해 주세요"
                clearable
                label="위도"
                type="number"
                variant="outlined"
              />
            </v-col>
            <v-col cols="6">
              <v-text-field
                v-model="place.longitude"
                hint="경도를 입력해 주세요"
                clearable
                label="경도"
                type="number"
                variant="outlined"
              />
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn-login class="button-text-size" @click="cancel">취소</v-btn-login>
        <v-btn-login class="button-text-size" @click="savePlace">저장</v-btn-login>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from "axios";

export default {
  name: "CustomPlaceModal",
  props: {
    modelValue: Boolean,
  },
  data() {
    return {
      place: {
        name: "",
        address: "",
        latitude: null,
        longitude: null,
        placeType: null,
      },
      placeTypes: [],
    };
  },
  async created() {
    await this.fetchPlaceTypes();
  },
  methods: {
    async fetchPlaceTypes() {
      try {
        const response = await axios.get("https://api.ssafy.me/places/types");
        this.placeTypes = response.data;
      } catch (error) {
        console.error("장소 타입 불러오기 실패:", error);
      }
    },
    updateModelValue(value) {
      this.$emit("update:modelValue", value);
    },
    cancel() {
      this.resetForm();
      this.updateModelValue(false);
    },
    savePlace() {
      if (this.validateForm()) {
        this.$emit("place-selected", this.place);
        this.resetForm();
        this.updateModelValue(false);
      }
    },
    validateForm() {
      if (
        !this.place.name ||
        !this.place.address ||
        !this.place.latitude ||
        !this.place.longitude ||
        !this.place.placeType
      ) {
        alert("모든 필드를 입력해주세요.");
        return false;
      }
      return true;
    },
    resetForm() {
      this.place = {
        name: "",
        address: "",
        latitude: null,
        longitude: null,
        placeType: null,
      };
    },
  },
};
</script>

<style scoped>
.custom-place-modal {
  max-width: 600px;
  width: 100%;
  margin: auto;
  padding: 20px;
}
</style>
