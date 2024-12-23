<template>
  <v-dialog :model-value="modelValue" @update:model-value="updateModelValue" @show="onDialogShow">
    <v-card class="map-modal">
      <v-toolbar title="지도에서 장소 찾기" rounded dark></v-toolbar>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols="12">
              <v-text-field
                v-model="keyword"
                hint="검색할 장소 이름을 입력해주세요"
                clearable
                label="검색"
                variant="outlined"
                @keyup.enter="searchPlaces"
              >
                <template v-slot:append-inner>
                  <v-btn @click="searchPlaces" variant="text" icon="mdi-magnify"></v-btn>
                </template>
              </v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <div class="map-container">
                <div id="map" ref="mapContainer" style="width: 100%; height: 400px"></div>
                <div id="menu_wrap" class="bg_white">
                  <div class="scroll-results">
                    <ul id="placesList"></ul>
                    <div id="pagination"></div>
                  </div>
                </div>
              </div>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn-login class="button-text-size" @click="cancel">취소</v-btn-login>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from "@/api/axios";
export default {
  name: "PlaceFromMapModal",
  props: {
    modelValue: Boolean,
  },
  emits: ["update:modelValue", "place-selected"],
  data() {
    return {
      axios: axios,
      map: null,
      markers: [],
      ps: null,
      infowindow: null,
      keyword: "",
    };
  },
  watch: {
    modelValue: {
      immediate: true,
      handler(newValue) {
        if (newValue) {
          this.$nextTick(() => {
            setTimeout(() => {
              this.initMap();
            }, 100);
          });
        }
      },
    },
  },
  methods: {
    initMap() {
      if (window.kakao && window.kakao.maps) {
        const container = this.$refs.mapContainer;
        if (!container) return;

        const options = {
          center: new window.kakao.maps.LatLng(37.566826, 126.9786567),
          level: 3,
        };

        this.map = new window.kakao.maps.Map(container, options);
        this.ps = new window.kakao.maps.services.Places();
        this.infowindow = new window.kakao.maps.InfoWindow({ zIndex: 1 });
      }
    },
    updateModelValue(value) {
      this.$emit("update:modelValue", value);
      if (!value) {
        this.map = null;
        this.markers = [];
        this.ps = null;
        this.infowindow = null;
      }
    },
    searchPlaces() {
      if (!this.keyword.trim()) {
        alert("키워드를 입력해주세요!");
        return;
      }

      this.ps.keywordSearch(this.keyword, (data, status, pagination) => {
        if (status === window.kakao.maps.services.Status.OK) {
          // pagination 객체를 저장
          this.pagination = pagination;
          this.displayPlaces(data);
          this.displayPagination(pagination);
        } else if (status === window.kakao.maps.services.Status.ZERO_RESULT) {
          alert("검색 결과가 존재하지 않습니다.");
        } else if (status === window.kakao.maps.services.Status.ERROR) {
          alert("검색 중 오류가 발생했습니다.");
        }
      });
    },
    displayPlaces(places) {
      if (!this.map) return; // map 객체가 없으면 리턴

      const listEl = document.getElementById("placesList");
      const bounds = new window.kakao.maps.LatLngBounds();

      this.removeAllChildNodes(listEl);
      this.removeMarker();

      places.forEach((place, i) => {
        const placePosition = new window.kakao.maps.LatLng(place.y, place.x);
        const marker = this.addMarker(placePosition, i);
        const itemEl = this.getListItem(i, place);

        bounds.extend(placePosition);

        this.addMarkerAndListEvents(marker, itemEl, place);

        listEl.appendChild(itemEl);
      });

      if (places.length > 0) {
        this.map.setBounds(bounds);
      }
    },
    cancel() {
      this.updateModelValue(false);
    },

    addMarkerAndListEvents(marker, itemEl, place) {
      // 마커에 마우스오버 이벤트 추가
      window.kakao.maps.event.addListener(marker, "mouseover", () => {
        this.displayInfowindow(marker, place.place_name);
      });

      // 마커에 마우스아웃 이벤트 추가
      window.kakao.maps.event.addListener(marker, "mouseout", () => {
        if (this.infowindow) {
          this.infowindow.close();
        }
      });

      // 마커 클릭 이벤트 추가
      window.kakao.maps.event.addListener(marker, "click", () => {
        this.selectPlace(place);
      });

      // 목록 항목에 마우스오버 이벤트 추가
      itemEl.onmouseover = () => {
        this.displayInfowindow(marker, place.place_name);
      };

      // 목록 항목에 마우스아웃 이벤트 추가
      itemEl.onmouseout = () => {
        if (this.infowindow) {
          this.infowindow.close();
        }
      };

      // 목록 항목 클릭 이벤트 추가
      itemEl.onclick = () => {
        this.selectPlace(place);
      };
    },

    selectPlace(place) {
      // 부모 컴포넌트로 카카오맵 장소 데이터 전달
      this.$emit("place-selected", place);
      this.updateModelValue(false);
    },

    addMarker(position, idx) {
      const imageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png";
      const imageSize = new window.kakao.maps.Size(36, 37);
      const imgOptions = {
        spriteSize: new window.kakao.maps.Size(36, 691),
        spriteOrigin: new window.kakao.maps.Point(0, idx * 46 + 10),
        offset: new window.kakao.maps.Point(13, 37),
      };

      const markerImage = new window.kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions);
      const marker = new window.kakao.maps.Marker({
        position: position,
        image: markerImage,
      });

      marker.setMap(this.map);
      this.markers.push(marker);

      return marker;
    },

    removeMarker() {
      this.markers.forEach((marker) => {
        marker.setMap(null);
      });
      this.markers = [];
    },

    getListItem(index, place) {
      const el = document.createElement("li");
      el.className = "item";

      const itemStr = `
    <span class="markerbg marker_${index + 1}"></span>
    <div class="info">
      <h5>${place.place_name}</h5>
      ${
        place.road_address_name
          ? `<span>${place.road_address_name}</span>
             <span class="jibun gray">${place.address_name}</span>`
          : `<span>${place.address_name}</span>`
      }
      ${place.phone ? `<span class="tel">${place.phone}</span>` : ""}
    </div>
  `;

      el.innerHTML = itemStr;
      return el;
    },

    displayPagination(pagination) {
      const paginationEl = document.getElementById("pagination");

      this.removeAllChildNodes(paginationEl);

      for (let i = 1; i <= pagination.last; i++) {
        const el = document.createElement("a");
        // href 제거
        el.style.cursor = "pointer"; // 커서 스타일 추가
        el.innerHTML = i;

        if (i === pagination.current) {
          el.className = "on";
        } else {
          el.onclick = (e) => {
            // 이벤트 기본 동작 방지
            e.preventDefault();

            // 페이지 이동 시 새로운 검색 수행
            this.ps.keywordSearch(
              this.keyword,
              (data, status) => {
                if (status === window.kakao.maps.services.Status.OK) {
                  this.displayPlaces(data);
                  // 페이지네이션 업데이트
                  pagination.gotoPage(i);
                }
              },
              { page: i }
            );
          };
        }

        paginationEl.appendChild(el);
      }
    },

    displayInfowindow(marker, title) {
      if (!this.infowindow) return;

      const content = `
      <div style="padding:5px;z-index:1;color:#333;background:white;border-radius:4px;font-size:12px;font-weight:bold;">
        ${title}
      </div>
    `;
      this.infowindow.setContent(content);
      this.infowindow.open(this.map, marker);
    },

    removeAllChildNodes(el) {
      while (el.hasChildNodes()) {
        el.removeChild(el.lastChild);
      }
    },
  },
};
</script>

<style scoped>
.map-modal {
  max-width: 1000px;
  width: 100%;
  margin: auto;
  padding: 20px;
}

.map-container {
  position: relative;
  width: 100%;
  height: 400px;
}

#menu_wrap {
  position: absolute;
  top: 0;
  right: 0;
  width: 250px;
  height: 100%;
  margin: 0;
  padding: 5px;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.9);
  z-index: 1;
  font-size: 12px;
  border-radius: 4px;
  border: 1px solid #ddd; /* 테두리 추가 */
}

.scroll-results {
  height: 100%;
  overflow-y: auto;
}

#placesList {
  padding: 0;
  margin: 0;
  background: white; /* 배경색 추가 */
}

#placesList li {
  list-style: none;
  padding: 10px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  background: white; /* 배경색 추가 */
}

#placesList .item {
  position: relative;
  border-bottom: 1px solid #888;
  overflow: hidden;
  background: white;
  min-height: 65px;
}

#placesList .item .info {
  padding: 10px 0 10px 55px;
}

#placesList .item h5 {
  margin: 0;
  font-size: 14px;
  color: #333; /* 제목 색상 진하게 */
  font-weight: bold; /* 제목 굵게 */
}

#placesList .item .info span {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #666; /* 주소 텍스트 색상 */
}

#placesList .item:hover {
  background: #f5f5f5; /* 호버 효과 추가 */
}

#placesList .info .gray {
  color: #8a8a8a;
}

#placesList .info .tel {
  color: #009900;
}

/* 검색 결과 구분선 추가 */
#placesList li + li {
  border-top: 1px solid #eee;
}

/* 전체 텍스트 색상 강제 지정 */
#menu_wrap * {
  color: #333 !important;
}

/* 인포윈도우 스타일 */
.infowindow {
  background: white !important;
  color: #333 !important;
  padding: 5px 10px;
  border-radius: 4px;
  border: 1px solid #ddd;
}
#pagination {
  margin: 10px auto;
  text-align: center;
}

#pagination a {
  display: inline-block;
  margin: 0 3px;
  padding: 3px 8px;
  border: 1px solid #ccc;
  color: #333;
  text-decoration: none;
  border-radius: 2px;
  cursor: pointer; /* 커서 스타일 추가 */
}

#pagination a:hover {
  background: #f5f5f5;
}

#pagination a.on {
  background: #4285f4;
  color: #fff !important;
  border-color: #4285f4;
  font-weight: bold;
  cursor: default; /* 현재 페이지는 커서 기본값으로 */
}

/* 마커 관련 스타일 */
.markerbg {
  float: left;
  width: 36px;
  height: 37px;
  margin: 10px 0 0 10px;
  background: url(https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png) no-repeat;
}

.marker_1 {
  background-position: 0 -10px;
}
.marker_2 {
  background-position: 0 -56px;
}
.marker_3 {
  background-position: 0 -102px;
}
.marker_4 {
  background-position: 0 -148px;
}
.marker_5 {
  background-position: 0 -194px;
}
.marker_6 {
  background-position: 0 -240px;
}
.marker_7 {
  background-position: 0 -286px;
}
.marker_8 {
  background-position: 0 -332px;
}
.marker_9 {
  background-position: 0 -378px;
}
.marker_10 {
  background-position: 0 -423px;
}
.marker_11 {
  background-position: 0 -470px;
}
.marker_12 {
  background-position: 0 -516px;
}
.marker_13 {
  background-position: 0 -562px;
}
.marker_14 {
  background-position: 0 -608px;
}
.marker_15 {
  background-position: 0 -654px;
}

/* 검색결과 항목 단위 스타일 */

.info h5 {
  margin: 0 0 5px 0;
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.info span {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #666;
}

.info .jibun {
  padding-left: 26px;
  background: url(https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/places_jibun.png) no-repeat;
}

.info .tel {
  color: #009900;
  margin-top: 4px;
}

#placesList li:hover {
  background: #f5f5f5;
}

#map {
  border-radius: 5px;
}
</style>
