drop table if exists `member_flupet`;
drop table if exists `flupet`;
CREATE TABLE `flupet` (
                          `id`	int unsigned auto_increment
                              primary key,
                          `name`	varchar(10)	NOT NULL,
                          `img_url`	MEDIUMTEXT	NOT NULL, -- 터지면 MEDIUMTEXT로 설정
                          `stage`	int	NOT NULL
);

# drop table if exists `member_flupet`;
CREATE TABLE `member_flupet` (
                                 `id`	int unsigned auto_increment
                                     primary key,
                                 `flupet_id`	int unsigned	NOT NULL,
                                 `member_id`	varchar(255)	NOT NULL,
                                 `name`	varchar(10)    NOT NULL,
                                 `exp`	int	NOT NULL	DEFAULT 0,
                                 `steps`	int unsigned	NOT NULL	DEFAULT 0,
                                 `is_dead`	boolean	NOT NULL	DEFAULT false,
                                 `create_time`	datetime(6)	NOT NULL,
                                 `end_time`	datetime(6)	NULL,
                                 `fullness`	int	NOT NULL	DEFAULT 100,
                                 `health`	int	NOT NULL	DEFAULT 100,
                                 `pat_cnt`	int	NOT NULL	DEFAULT 5,
                                 `acha_time`	datetime(6)	NULL,
                                 `fullness_update_time`	datetime(6)	NULL,
                                 `health_update_time`	datetime(6)	NULL,
                                 CONSTRAINT `fk_member_flupet_flupet_id` FOREIGN KEY (`flupet_id`) REFERENCES `flupet` (`id`),
                                 INDEX `idx_member_id` (`member_id`),
                                 INDEX `idx_member_id_is_dead` (`member_id`, `is_dead`) -- 복합 인덱스 추가(이게 성능상 더 좋은지 판단 필요)
);

drop table if exists `food_type`;
CREATE TABLE `food_type` (
                             `id`	int unsigned auto_increment
                                 primary key,
                             `name`	varchar(10)	NOT NULL,
                             `img_url`	varchar(255)	NOT NULL,
                             `fullness_effect`	int	NOT NULL,
                             `health_effect`	int	NOT NULL,
                             `price`	int	NOT NULL,
                             `stock`	int	NOT NULL    DEFAULT 30,
                             `description`	varchar(255)	NOT NULL
);

INSERT INTO flupet (`name`, `img_url`, `stage`) VALUES
                                                    ('알', 'https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/egg.gif', 1),
                                                    ('흰토끼', 'https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/rabbit_white1.png,https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/rabbit_white2.gif,https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/rabbit_white_sleep.gif,https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/rabbit_white_happy.gif', 2),
                                                    ('네로', 'https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/cat_gray.png,https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/gray_cat_head_banging.gif,https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/cat_gray_sleep.gif,https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/cat_gray_happy.gif', 2),
                                                    ('코기', 'https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/dog_corgi.png,https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/dog_corgi2.gif,https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/dog_corgi_sleep.gif,https://my-fluffit-app-service-bucket.s3.ap-northeast-2.amazonaws.com/dog_corgi_happy.gif', 2);

-- ALTER TABLE `members` ADD CONSTRAINT `PK_MEMBERS` PRIMARY KEY (
-- 	`id`
-- );
--
-- ALTER TABLE `memberFlupets` ADD CONSTRAINT `PK_MEMBERFLUPETS` PRIMARY KEY (
-- 	`id`
-- );
--
-- ALTER TABLE `steps` ADD CONSTRAINT `PK_STEPS` PRIMARY KEY (
-- 	`id`
-- );
--
-- ALTER TABLE `foodTypes` ADD CONSTRAINT `PK_FOODTYPES` PRIMARY KEY (
-- 	`id`
-- );
--
-- ALTER TABLE `runnings` ADD CONSTRAINT `PK_RUNNINGS` PRIMARY KEY (
-- 	`id`
-- );
--
-- ALTER TABLE `flupets` ADD CONSTRAINT `PK_FLUPETS` PRIMARY KEY (
-- 	`id`
-- );
--
-- ALTER TABLE `battleRankings` ADD CONSTRAINT `PK_BATTLERANKINGS` PRIMARY KEY (
-- 	`Key`
-- );
--
-- ALTER TABLE `battle` ADD CONSTRAINT `PK_BATTLE` PRIMARY KEY (
-- 	`id`
-- );
--
-- ALTER TABLE `AppVersion` ADD CONSTRAINT `PK_APPVERSION` PRIMARY KEY (
-- 	`id`
-- );
--
-- ALTER TABLE `PetRangkings` ADD CONSTRAINT `PK_PETRANGKINGS` PRIMARY KEY (
-- 	`Key`
-- );
--
-- ALTER TABLE `battleTypes` ADD CONSTRAINT `PK_BATTLETYPES` PRIMARY KEY (
-- 	`id`
-- );

