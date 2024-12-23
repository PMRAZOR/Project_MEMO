DROP DATABASE IF EXISTS `pjttest`;
CREATE DATABASE IF NOT EXISTS `pjttest`;
use pjttest;

-- 사용자 테이블
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    profile_image VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    is_deleted BOOLEAN DEFAULT false;
    deleted_at TIMESTAMP DEFAULT NULL;
);

select * from users;

-- 장소 타입 테이블 
CREATE TABLE place_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);


insert into place_types
VALUES (12,'관광지'),(14,'문화시설'),(15,'축제공연행사'),(25,'여행코스'),(28,'레포츠'),(32,'숙박'),(38,'쇼핑'),(39,'음식점');


-- 장소 테이블
CREATE TABLE places (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) unique not null,
    address VARCHAR(200),
    latitude DECIMAL(20,17),
    longitude DECIMAL(20,17),
    place_type INT, -- 카페, 식당, 관광지 등
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (place_type) REFERENCES place_types(id)
);

select * from places;

-- 게시물
CREATE TABLE posts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    gpt_keyword VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE boards (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    user_id INT NOT NULL,
    view_count INT DEFAULT 0,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
select * from boards;




-- 방문지 테이블
CREATE TABLE visits (
    id INT PRIMARY KEY AUTO_INCREMENT,
    post_id INT NOT NULL,
    place_id INT NOT NULL,
    visit_date DATETIME NOT NULL, -- 방문 날짜/시간
    title VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (place_id) REFERENCES places(id)
);

select * from visits;

-- 스팟 로그 사진 테이블
CREATE TABLE visit_photos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    visit_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,    -- 저장된 파일명
    original_name VARCHAR(255) NOT NULL, -- 원본 파일명
    file_path VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (visit_id) REFERENCES visits(id)
);


-- 북마크 테이블
CREATE TABLE post_bookmarks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id)
);

-- 커뮤니티 테이블
CREATE TABLE boards (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    user_id INT NOT NULL,
    view_count INT DEFAULT 0,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);