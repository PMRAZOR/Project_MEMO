<template>
  <div class="nearby-timelines">
    <v-container>
      <!-- 헤더 섹션 -->
      <v-row>
        <v-col>
          <h2 class="text-h4 font-weight-bold mb-6">내 주변 관광지</h2>
        </v-col>
      </v-row>

      <!-- 지도 섹션 -->
      <v-row>
        <v-col cols="12">
          <v-card elevation="2">
            <div id="map" style="width: 100%; height: 500px"></div>
          </v-card>
        </v-col>
      </v-row>

      <!-- 필터 섹션 -->
      <v-row class="mt-4">
        <v-col cols="12" sm="6" md="4">
          <v-select
            v-model="maxDistance"
            variant="outlined"
            :items="distanceOptions"
            label="검색 반경"
            suffix="km"
          ></v-select>
        </v-col>
      </v-row>

      <!-- 장소 리스트 섹션 -->
      <v-row>
        <v-col cols="12">
          <v-card v-if="nearbyPlaces.length">
            <v-list>
              <v-list-item v-for="place in nearbyPlaces" :key="place.id" :value="place" @click="focusPlace(place)">
                <template v-slot:prepend>
                  <v-icon icon="mdi-map-marker"></v-icon>
                </template>

                <v-list-item-title>{{ place.name }}</v-list-item-title>
                <v-list-item-subtitle>{{ place.address }}</v-list-item-subtitle>

                <template v-slot:append>
                  <v-chip size="small"> {{ place.distance.toFixed(1) }}km </v-chip>
                </template>
              </v-list-item>
            </v-list>
          </v-card>
          <v-card v-else>
            <v-card-text class="text-center py-4"> 주변에 관광지가 없습니다. </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import api from "@/api/axios";

export default {
  name: "NearbyTimelines",
  data() {
    return {
      map: null,
      userPosition: null,
      nearbyPlaces: [],
      places: [],
      maxDistance: 5,
      distanceOptions: [
        { title: "1km", value: 1 },
        { title: "5km", value: 5 },
        { title: "10km", value: 10 },
        { title: "20km", value: 20 },
      ],
      markers: [], // 마커 관리를 위한 배열
      infowindows: [], // 인포윈도우 관리를 위한 배열
    };
  },

  watch: {
    maxDistance() {
      this.findNearbyPlaces();
      this.adjustMapBounds();
    },
  },

  methods: {
    initializeKakaoMap() {
      if (window.kakao && window.kakao.maps) {
        const mapContainer = document.getElementById("map");
        const mapOption = {
          center: new kakao.maps.LatLng(37.566826, 126.978656),
          level: 5,
        };
        this.map = new kakao.maps.Map(mapContainer, mapOption);
        this.getCurrentLocation();
      }
    },

    getCurrentLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          async (position) => {
            const lat = position.coords.latitude;
            const lng = position.coords.longitude;
            this.userPosition = { lat, lng };

            const moveLatLng = new kakao.maps.LatLng(lat, lng);
            this.map.setCenter(moveLatLng);

            // 현재 위치 마커 이미지 설정
            const imageSrc = require("@/assets/location.png"), // 마커 이미지 URL
              imageSize = new kakao.maps.Size(50, 50), // 마커 이미지의 크기
              imageOption = { offset: new kakao.maps.Point(27, 69) }; // 마커 이미지의 옵션. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정

            // 마커 이미지를 생성
            const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);

            // 현재 위치 마커 표시 (커스텀 이미지 적용)
            new kakao.maps.Marker({
              position: moveLatLng,
              map: this.map,
              title: "현재 위치",
              image: markerImage, // 커스텀 마커 이미지 설정
            });

            this.findNearbyPlaces();
            this.adjustMapBounds();
          },
          (error) => {
            console.error("Geolocation error:", error);
          }
        );
      }
    },

    calculateDistance(lat1, lon1, lat2, lon2) {
      const R = 6371;
      const dLat = this.deg2rad(lat2 - lat1);
      const dLon = this.deg2rad(lon2 - lon1);
      const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(this.deg2rad(lat1)) * Math.cos(this.deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
      return R * c;
    },

    deg2rad(deg) {
      return deg * (Math.PI / 180);
    },

    clearMarkers() {
      this.markers.forEach((marker) => marker.setMap(null));
      this.markers = [];
      this.infowindows.forEach((infowindow) => infowindow.close());
      this.infowindows = [];
    },
    adjustMapBounds() {
      if (!this.map || !this.userPosition || !this.nearbyPlaces.length) return;

      // 모든 마커의 위치를 포함하는 경계 생성
      const bounds = new kakao.maps.LatLngBounds();

      // 현재 위치 추가
      bounds.extend(new kakao.maps.LatLng(this.userPosition.lat, this.userPosition.lng));

      // 모든 주변 장소들의 위치 추가
      this.nearbyPlaces.forEach((place) => {
        bounds.extend(new kakao.maps.LatLng(place.latitude, place.longitude));
      });

      // 지도 범위 재설정
      this.map.setBounds(bounds, 50); // 50은 패딩 값(px)입니다.

      // 적절한 줌 레벨 설정
      let zoomLevel;
      switch (this.maxDistance) {
        case 1:
          zoomLevel = 4;
          break;
        case 5:
          zoomLevel = 7;
          break;
        case 10:
          zoomLevel = 8;
          break;
        case 20:
          zoomLevel = 15;
          break;
        default:
          zoomLevel = 5;
      }

      // 현재 줌 레벨이 설정한 값보다 더 확대되어 있으면 조정
      if (this.map.getLevel() < zoomLevel) {
        this.map.setLevel(zoomLevel);
      }
    },

    findNearbyPlaces() {
      if (!this.userPosition) return;

      this.clearMarkers();

      this.nearbyPlaces = this.places
        .map((place) => ({
          ...place,
          distance: this.calculateDistance(
            this.userPosition.lat,
            this.userPosition.lng,
            place.latitude,
            place.longitude
          ),
        }))
        .filter((place) => place.distance <= this.maxDistance)
        .sort((a, b) => a.distance - b.distance);

      // 현재 위치 마커 이미지 설정
      const imageSrc = require("@/assets/point.png"), // 마커 이미지 URL
        imageSize = new kakao.maps.Size(30, 30), // 마커 이미지의 크기
        imageOption = { offset: new kakao.maps.Point(15, 30) }; // 마커 이미지의 옵션. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정

      // 마커 이미지를 생성
      const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);
      // 마커 생성 후 adjustMapBounds 호출
      this.nearbyPlaces.forEach((place) => {
        const position = new kakao.maps.LatLng(place.latitude, place.longitude);
        const marker = new kakao.maps.Marker({
          position: position,
          map: this.map,
          image: markerImage,
        });

        const infowindow = new kakao.maps.InfoWindow({
          content: `
          <div style="padding:10px;width:200px;">
            <h3 style="margin:0 0 5px;font-size:14px;color:#000;">${place.name}</h3>
            <p style="margin:0;font-size:12px;color:#666;">${place.distance.toFixed(1)}km</p>
          </div>
        `,
        });

        kakao.maps.event.addListener(marker, "click", () => {
          this.infowindows.forEach((info) => info.close());
          infowindow.open(this.map, marker);
        });

        this.markers.push(marker);
        this.infowindows.push(infowindow);
      });

      // 마커 생성 후 지도 범위 조정
      this.adjustMapBounds();
    },

    focusPlace(place) {
      const position = new kakao.maps.LatLng(place.latitude, place.longitude);
      this.map.setCenter(position);
      this.map.setLevel(3);
    },

    async fetchPlaces() {
      try {
        // axios 인스턴스의 baseURL이 'http://localhost/memo'로 설정되어 있으므로
        // '/places'만 추가하면 됩니다
        const response = await api.get("/places");
        this.places = response.data;
        console.log("Fetched places:", this.places); // 데이터 확인용 로그
      } catch (error) {
        console.error("Failed to load places:", error);
        // 에러 처리를 위한 추가 로직
        if (error.response) {
          // 서버가 응답을 반환한 경우
          console.error("Error response:", error.response.data);
        } else if (error.request) {
          // 요청이 전송되었지만 응답을 받지 못한 경우
          console.error("Error request:", error.request);
        }
      }
    },
  },

  async mounted() {
    // 스크립트가 이미 로드되어 있으므로 바로 초기화
    this.initializeKakaoMap();
    await this.fetchPlaces(); // 데이터 불러오기를 기다림

    // 데이터를 불러온 후 바로 주변 장소 검색 및 지도 범위 조정
    if (this.userPosition) {
      this.findNearbyPlaces();
      this.adjustMapBounds();
    }
  },
};
</script>

<style scoped>
.nearby-timelines {
  padding: 20px 0;
}

#map {
  border-radius: 5px;
}
</style>
