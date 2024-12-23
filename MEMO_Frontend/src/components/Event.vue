<template>
  <div class="container">
    <div class="headline-section mb-2">
      <h2 class="text-h4 font-weight-bold mb-4">이번주 주요 지역 행사</h2>
      <p class="mt-1">현재 사용자 위치</p>
      <p class="mt-1">지번 주소 : {{ jibunAddress }}</p>
      <p>도로명 주소 : {{ roadAddress }}</p>
    </div>
    <v-infinite-scroll class="custom-scroll flex-grow-1" :items="festivalItems" :onLoad="loadMoreFestivals">
      <template v-for="item in festivalItems" :key="item.contentid">
        <div class="image-container">
          <v-img
            :src="item.firstimage || '/noimage.jpg'"
            :alt="item.title"
            height="300"
            cover
            class="image-hover"
            :lazy-src="'/noimage.jpg'"
          >
            <template v-slot:placeholder>
              <v-row class="fill-height ma-0" align="center" justify="center">
                <v-img src="/noimage.jpg" height="300" cover></v-img>
              </v-row>
            </template>
          </v-img>
          <div class="overlay">
            <div class="title">{{ item.title }}</div>
          </div>
        </div>
      </template>
      <template v-slot:empty>
        <v-alert type="success">모든 축제를 불러왔어요!</v-alert>
      </template>
    </v-infinite-scroll>
  </div>
</template>
<script>
import axios from "axios";
import { ref, onMounted, onUnmounted } from "vue";

const scrollHeight = ref(0);

const updateScrollHeight = () => {
  scrollHeight.value = window.innerHeight; // 64는 앱 바의 높이입니다. 필요에 따라 조정하세요.
};

onMounted(() => {
  updateScrollHeight();
  window.addEventListener("resize", updateScrollHeight);
});

onUnmounted(() => {
  window.removeEventListener("resize", updateScrollHeight);
});

const areaCodeMap = {
  서울: 1,
  인천: 2,
  대전: 3,
  대구: 4,
  광주: 5,
  부산: 6,
  울산: 7,
  세종특별자치시: 8,
  경기도: 31,
  강원도: 32,
  충청북도: 33,
  충청남도: 34,
  경상북도: 35,
  경상남도: 36,
  전라북도: 37,
  전라남도: 38,
  제주도: 39,
};

export default {
  name: "WeekEvent",
  data() {
    return {
      location: "위치를 가져오는 중...",
      roadAddress: "",
      jibunAddress: "",
      currentDate: new Date(),
      festivalItems: [],
      currentPage: 1,
      isAddressLoaded: false,
      apiKey: "ziktH/GPfHcpskjxJEvc+uMqR+2zfqVDCSj1MJQJZa0mGQ+xIFpLHfvMvwlTh9V4DiSmdWxx/UVUVXTCwxFPow==",
    };
  },
  watch: {
    // jibunAddress가 변경될 때마다 실행되는 watcher 추가
    jibunAddress(newValue) {
      if (newValue && !this.isAddressLoaded) {
        this.isAddressLoaded = true;
        this.loadMoreFestivals({ done: () => {} });
      }
    },
  },
  computed: {
    formattedStartDate() {
      return this.formatDate(this.currentDate);
    },

    formattedEndDate() {
      const endDate = new Date(this.currentDate);
      endDate.setDate(endDate.getDate() + 14);
      return this.formatDate(endDate);
    },
    currentCity() {
      if (this.jibunAddress) {
        return this.jibunAddress.split(" ")[0];
      }
      return "";
    },
    areaCode() {
      return areaCodeMap[this.currentCity] || 39; // 기본값으로 39(제주) 사용
    },
  },
  mounted() {
    this.getLocation();
    this.loadMoreFestivals({ done: () => {} });
  },
  methods: {
    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      return `${year}${month}${day}`;
    },
    getLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            const { latitude, longitude } = position.coords;
            this.location = `위도: ${latitude}, 경도: ${longitude}`;
            this.getAddress(longitude, latitude);
          },
          (error) => {
            this.location = "위치 정보를 가져올 수 없습니다.";
            console.error("위치 정보 오류:", error);
          }
        );
      } else {
        this.location = "브라우저가 위치 정보를 지원하지 않습니다.";
      }
    },

    getAddress(lng, lat) {
      if (window.kakao && window.kakao.maps && window.kakao.maps.services) {
        const geocoder = new window.kakao.maps.services.Geocoder();

        geocoder.coord2Address(lng, lat, (result, status) => {
          if (status === window.kakao.maps.services.Status.OK) {
            const roadAddress = result[0].road_address;
            const jibunAddress = result[0].address;

            this.roadAddress = roadAddress ? roadAddress.address_name : "도로명 주소 없음";
            this.jibunAddress = jibunAddress ? jibunAddress.address_name : "지번 주소 없음";

            console.log("Updated jibunAddress:", this.jibunAddress);
          } else {
            this.roadAddress = "주소를 가져오는데 실패했습니다.";
            this.jibunAddress = "주소를 가져오는데 실패했습니다.";
          }
        });
      } else {
        console.error("Kakao maps SDK is not loaded");
      }
    },
    async fetchFestivalData() {
      try {
        const response = await axios.get("https://apis.data.go.kr/B551011/KorService1/searchFestival1", {
          params: {
            numOfRows: 100,
            pageNo: 1,
            MobileOS: "ETC",
            MobileApp: "MEMO",
            _type: "json",
            eventStartDate: this.formattedDate,
            areaCode: this.areaCode,
            serviceKey: this.apiKey,
          },
        });
        console.log("Festival data:", response.data);
      } catch (error) {
        console.error("Error fetching festival data:", error);
      }
    },
    async loadMoreFestivals({ done }) {
      if (!this.isAddressLoaded) {
        console.log("Address not loaded yet. Skipping API call.");
        done("error");
        return;
      }

      try {
        console.log("Current jibunAddress:", this.jibunAddress);
        console.log("Start date:", this.formattedStartDate);
        console.log("End date:", this.formattedEndDate);

        const params = {
          numOfRows: 10,
          pageNo: this.currentPage,
          MobileOS: "ETC",
          MobileApp: "MEMO",
          _type: "json",
          eventStartDate: this.formattedStartDate,
          eventEndDate: this.formattedEndDate,
          areaCode: this.areaCode,
          serviceKey: this.apiKey,
        };

        console.log("API request params:", params);

        const response = await axios.get("https://apis.data.go.kr/B551011/KorService1/searchFestival1", { params });

        console.log("Full API Response:", JSON.stringify(response.data, null, 2));

        const newItems = response.data.response.body.items.item || [];
        this.festivalItems.push(...newItems);
        this.currentPage++;

        done(newItems.length > 0 ? "ok" : "empty");
      } catch (error) {
        console.error("Error fetching festival data:", error);
        done("error");
      }
    },
  },
};
</script>

<style scoped>
.container {
  padding: 20px;
}
.headline-section {
  text-align: left;
  padding: 0 16px;
}
.v-img {
  background-color: #f0f0f0;
}

.custom-scroll {
  overflow-y: auto;
}

/* Webkit browsers like Chrome, Safari */
.custom-scroll::-webkit-scrollbar {
  width: 0;
  background: transparent;
}

/* Firefox */
.custom-scroll {
  scrollbar-width: none;
  -ms-overflow-style: none;
}
.image-container {
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
  border-radius: 5px;
}
.image-hover {
  transition: filter 0.3s ease;
}

.image-container:hover .image-hover {
  filter: blur(3px) brightness(70%);
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-container:hover .overlay {
  opacity: 1;
}

.title {
  color: white;
  font-size: 1.2em;
  font-weight: bold;
  text-align: center;
  padding: 10px;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 5px;
}
</style>
