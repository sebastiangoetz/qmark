ALTER TABLE binarycontent MODIFY COLUMN content LONGBLOB;

INSERT INTO `type` (typeID, description, name, typeTypeID) VALUES (1, 'dummy', 'dummy', null), (2, 'Super type of all user types.', 'userType', null), (3, 'A user that is registered and activated.', 'active', 2), (4, 'A user that is registered but not yet activated.', 'registered', 2), (5, 'Super type of all gender types.', 'genderType', null), (6, 'Female gender.', 'female', 5), (7, 'Male gender.', 'male', 5), (8, 'Unknown gender.', 'unknown', 5), (9, 'Types app versions can have.', 'versionType', null), (10, 'Released', 'versionReleased', 9), (11, 'Non-released', 'versionPrivate', 9);

INSERT INTO paymentplan (paymentPlanID, typeID) VALUES (1, 1);

INSERT INTO usecasemodel (useCaseModelID, description) VALUES (1, "<p>To evaluate the energy consumption of live wallpapers, four different use cases exist:</p>\n<ol>\n<li>Desktop Time</li>\n<li>Application Usage</li>\n<li>Locked</li>\n<li>Lock Screen</li>\n</ol>\n\n<h2>Desktop Time</h2>\n<p>The <i>Desktop Time</i> use case represents the state of the wallpaper when the mobile device is active (display enabled) and the desktop is visible. This is the major use case for Live Wallpapers as now the wallpaper is visible and therefore should be running at the desktop's background.</p>\n<p>Thus, live wallpapers are expected to consume their maximum energy in this use case.</p>\n\n<h2>Application Usage</h2>\n<p>The <i>Application Usage</i> use case represents the time the user uses other applications whereby the live wallpapers is set as the current desktop background. Allthough wallpapers should not be active if other applications are in the foreground, some wallpapers showed to be running in the background although not even visible. Therefore, live wallpapers are benchmark in this state as well. The other application used in this use case is the general settings menu of provided by the Android OS.</p>\n\n<h2>Locked</h2>\n<p>The <i>Locked</i> use case represents the time, where the device is locked and the display disabled. As the device is in its standby mode it may happen that applications are running in the background, including live wallpapers. Therefore, this use case is evaluated as well. Nevertheless, live wallpapers are expected to cause no additional energy consumption is this state and the differences between individual wallpapers should be marginally.</p>\n\n<h2>Lock Screen</h2>\n<p>The <i>Lock Screen</i> use case represents the state where the device's display is enabled, but the device is locked. In this state, the live wallpapers may be running and visible behind the lock screen of the device. Therefore, this use case belongs to the live wallpaper benchmark.</p>");

INSERT INTO transition (transitionID, description, name) VALUES (1, 'The time spent on the desktop with the wallpaper active in the background.', 'Desktop Time'), (2, 'The time using other applications.', 'Application Usage'), (3, 'The time having the device locked and the display disabled.', 'Locked'), (4, 'The time spent on the screen to unlock the device with the display enabled.', 'Lock Screen');

INSERT INTO usecasemodeltotransition (useCaseModelID, transitionID) VALUES (1, 1), (1, 2), (1, 3), (1, 4);

INSERT INTO genre (genreID, description, name, useCaseModelID) VALUES (1, 'Live wallpapers are applications that can be configured as animated backgrounds for Android devices, providing additional services (e.g., weather information) or just nice animations.', 'Live Wallpapers', 1);

INSERT INTO vendor (vendorID, description, name, paymentPlanID) VALUES (1, 'http://jokointeractive.blogspot.de/', 'Joko Interactive', 1), (2, '', 'Zantetsuken', 1), (3, '', 'Yougli', 1), (4, 'http://www.kittehface.com/', 'Kittehface Software', 1);

INSERT INTO app (appID, description, logo, name, packageID, rating, ratingDate, genreID, paymentPlanID, vendorID) VALUES (1, 'A fantastic paper cut-out landscape scrolls across your screen! Choose a fixed time of day, or let the sun and moon rise and set according to real world around you.    Purchase the Pro version to experience live local weather in each of your scenes!', 'figures/logos/paperland.png', 'Paperland Live Wallpaper', 'com.joko.paperland', 4.47507, '2013-05-23 14:41:23', 1, 1, 1), (2, 'This is the free version of Mystic Halo Live Wallpaper! Paid version adds touch events, multiple fade schemes, rotation schemes and special effects!    Now works with Galaxy Nexus and Rezound! See "HD Phone" in settings to enable.', 'figures/logos/mysticHalo.jpg', 'Mystic Halo Live Wallpaper fr', 'com.dc.mhf', 4.46235, '2013-05-23 14:41:23', 1, 1, 2), (3, 'Live wallpaper using realistic physics engine and g-sensors to display Androids falling down your screen. Androids react to gravity, touching, shaking your phone and even light and sound now.', 'figures/logos/shakeThemAll.png', 'Shake Them All! Live Wallpaper', 'net.yougli.shakethemall', 4.4197, '2013-05-23 14:41:23', 1, 1, 3), (4, 'A spectacular backdrop of storm clouds, lightning, and rain! Not a movie, with full support for landscape mode and home-screen switching!', 'figures/logos/thunderstorm.png', 'Thunderstorm Free Wallpaper', 'fishnoodle.storm_free', 4.14207, '2013-05-23 14:41:23', 1, 1, 4);

SET foreign_key_checks = 0;

INSERT INTO device (devideID, configuration, description, name, energyModelID, batteryCapacity) VALUES (1, 'Android version 4.1.2', 'Samsung Galaxy Nexus', 'Samsung Galaxy Nexus', 1, 6480);

INSERT INTO energymodel (energyModelID, useCaseModelID, profilingDate, deviceID) VALUES (1, 1, "2013-05-19 11:22:35", 1), (2, 1, "2013-05-19 11:22:35", 1), (3, 1, "2013-05-19 11:22:35", 1), (4, 1, "2013-05-19 11:22:35", 1), (5, 1, "2013-05-19 11:22:35", 1);

SET foreign_key_checks = 1;

INSERT INTO powerrate (powerRateID, `value`, transitionID) VALUES (1, null, 1), (2, null, 2), (3, null, 3), (4, null, 4);
#INSERT INTO powerrate (powerRateID, `value`, transitionID) VALUES (1, 2674, 1), (2, 1330, 2), (3, 684, 3), (4, 2339, 4);
INSERT INTO powerrate (powerRateID, `value`, transitionID) VALUES (5, null, 1), (6, null, 2), (7, null, 3), (8, null, 4);
#INSERT INTO powerrate (powerRateID, `value`, transitionID) VALUES (5, 1562, 1), (6, 1316, 2), (7, 586, 3), (8, 1471, 4);
INSERT INTO powerrate (powerRateID, `value`, transitionID) VALUES (9, 2208, 1), (10, 1374, 2), (11, 590, 3), (12, 1981, 4), (13, 1886, 1), (14, 1333, 2), (15, 589, 3), (16, 1726, 4);

INSERT INTO energymodeltopowerrate (energyModelID, powerRateID) VALUES (2, 1), (2, 2), (2, 3), (2, 4), (3, 5), (3, 6), (3, 7), (3, 8), (4, 9), (4, 10), (4, 11), (4, 12), (5, 13), (5, 14), (5, 15), (5, 16);

INSERT INTO version (versionID, vendorVersionID, name, appID, energyModelID, visibilityTypeID) VALUES (1, "1.8", null, 1, 2, 10), (2, "7.0", null, 2, 3, 10), (3, "1.92", null, 3, 4, 10), (4, "2.1", null, 4, 5, 10);


INSERT INTO usermetadata (userMetaDataID, firstname, genderTypeID, surname, title) VALUES (1, 'Chuck', 7, 'The Robot', 'Dr.'), (2, 'Tiffany', 6, 'The Robot', null);

INSERT INTO `type` (typeID, description, name, typeTypeID) VALUES (12, 'Super type for company member types.', 'companyMemberType', null), (13, 'Company Owner', 'companyOwner', 12), (14, 'Company Member', 'companyMember', 12), (15, 'Invited for Company Membership', 'invitedCompanyMember', 12);

INSERT INTO companyMember (companyMemberID, userMetaDataID, vendorID, typeID) VALUES (1, 1, 1, 13);

INSERT INTO `user` (userID, login, password, typeID, userMetaDataID, emailaddress) VALUES (1 , 'chuck', 'naonao', 3, 1, 'chuck.nao@gmail.com'), (2 , 'tiffany', 'barby', 3, 2, 'tiffany@gmail.com');

INSERT INTO `type` (typeID, description, name, typeTypeID) VALUES (16, 'Super type for app member types.', 'appMemberType', null), (17, 'App Owner', 'appOwner', 16), (18, 'App Member', 'appMember', 16), (19, 'Invited for App Membership', 'invitedAppMember', 16);

INSERT INTO member (memberID, appID, typeID, userID) VALUES (1, 1, 17, 1);
INSERT INTO member (memberID, appID, typeID, userID) VALUES (2, 2, 17, 1);


INSERT INTO testsuite (testSuiteID, name, packageID, testScript, apkBinaryID, versionID) VALUES (1, 'Paperland Test', null, 'TestRun ExampleWallpaper\napplicationUnderTest : paperland com.joko.paperland\nhardwareProfiling : on\nrun : onServer\n\nTestSuite BackgroundTests {\n\n	SetUp {\n		DISPLAY ON\n		UNLOCK\n		START ACTIVITY "com.android.wallpaper.livepicker" "LiveWallpaperActivity"\n		CURSOR down\n		CURSOR down\n		CURSOR down\n		CURSOR down\n		ENTER\n		CURSOR right\n		CURSOR right\n		ENTER\n		HOME BUTTON\n		DISPLAY OFF\n	}\n\n	TestCase WallPaperBackgroundTest {\n\n		Test DeviceLocked\n\n		WAIT FOR 5 // 600\n\n		Test DisplayOn\n\n		DISPLAY ON\n\n		Test LockScreen\n		WAIT FOR 5 // 600\n\n		Test Unlock\n\n		UNLOCK\n\n		Test Desktop\n		WAIT FOR 5 // 600\n\n		Test OpenSettings\n		OPEN SETTINGS\n\n		Test OtherAppActive\n\n		WAIT FOR 5 // 600\n\n		Test CloseSettings\n		HOME BUTTON\n\n		Test LockDevice\n\n		DISPLAY OFF\n	}\n}', null, '1');


INSERT INTO `type` (typeID, description, name, typeTypeID) VALUES (20, 'Super type for test run types.', 'testRunType', null), (21, 'Scheduled', 'scheduledTestRun', 20), (22, 'Running', 'runningTestRun', 20), (23, 'Finished', 'finishedTestRun', 20), (24, 'Failed', 'failedTestRun', 20);

INSERT INTO testrun (testRunID, console, idleTime, typeID, hwProfiling, numberOfRuns, deviceID, testSuiteID) VALUES ('1', 'This is a dummy console.\nWith two lines.', 0, 23, 1, 1, 1, 1);



INSERT INTO vendor (vendorID, description, name, paymentPlanID) VALUES (5, 'http://www.nbcuni.com/', 'NBC Universal, Inc.', 1);
INSERT INTO app (appID, description, logo, name, packageID, rating, ratingDate, genreID, paymentPlanID, vendorID) VALUES (5, 'Bring your phone or tablet to life with the Official Despicable Me 2 Live Wallpaper!', 'figures/logos/despicableMe2.png', 'Despicable Me 2', 'com.nbcuni.universal.despicableme2lwp', 4.3, '2013-07-02 14:41:23', 1, 1, 5);
INSERT INTO version (versionID, vendorVersionID, name, appID, energyModelID, visibilityTypeID) VALUES (5, "1.3", null, 5, null, 10);
INSERT INTO member (memberID, appID, typeID, userID) VALUES (3, 5, 17, 1);

INSERT INTO vendor (vendorID, description, name, paymentPlanID) VALUES (6, null, 'Wasabi', 1);
INSERT INTO app (appID, description, logo, name, packageID, rating, ratingDate, genreID, paymentPlanID, vendorID) VALUES (6, 'Download the free Galaxy S3/S4 Dandelion live wallpaper featuring flying seeds. ', 'figures/logos/galaxyS3S4.png', 'Galaxy S3/S4 Live Wallpaper', 'com.androidwasabi.livewallpaper.dandelion', 4.6, '2013-07-02 14:41:23', 1, 1, 6);
INSERT INTO version (versionID, vendorVersionID, name, appID, energyModelID, visibilityTypeID) VALUES (6, "1.3.7", null, 6, null, 10);
INSERT INTO member (memberID, appID, typeID, userID) VALUES (4, 6, 17, 1);

INSERT INTO app (appID, description, logo, name, packageID, rating, ratingDate, genreID, paymentPlanID, vendorID) VALUES (7, 'Xperia Z Live Wallpaper, inspired by new Sony Xperia Z device background with floating particles.', 'figures/logos/xperia.png', 'Xperia Z Live Wallpaper', 'com.androidwasabi.livewallpaper.xperiaz', 4.6, '2013-07-02 14:41:23', 1, 1, 5);
INSERT INTO version (versionID, vendorVersionID, name, appID, energyModelID, visibilityTypeID) VALUES (7, "1.0.5", null, 7, null, 10);
INSERT INTO member (memberID, appID, typeID, userID) VALUES (5, 7, 17, 1);

INSERT INTO vendor (vendorID, description, name, paymentPlanID) VALUES (7, null, 'Ady Hub', 1);
INSERT INTO app (appID, description, logo, name, packageID, rating, ratingDate, genreID, paymentPlanID, vendorID) VALUES (8, '3D Shark Tank Live Wallpaper! Watch beautiful 3D Sharks and schools of fish swim around in your Live Wallpaper. Very realistic, 3D generated graphics.', 'figures/logos/3dShark.png', '3D Shark Live Wallpaper', 'com.custom.lwp.SharkTank3D', 4.0, '2013-07-02 14:41:23', 1, 1, 7);
INSERT INTO version (versionID, vendorVersionID, name, appID, energyModelID, visibilityTypeID) VALUES (8, '1.0.7', null, 8, null, 10);
INSERT INTO member (memberID, appID, typeID, userID) VALUES (6, 8, 17, 1);

INSERT INTO app (appID, description, logo, name, packageID, rating, ratingDate, genreID, paymentPlanID, vendorID) VALUES (9, 'Water Drop live wallpaper, simulates water ripple effect. Touch or tap the screen to add water drops on your home screen!', 'figures/logos/waterdrop.png', 'Water Drop Live Wallpaper', 'com.androidwasabi.livewallpaper.waterdrop', 4.4, '2013-07-02 14:41:23', 1, 1, 6);
INSERT INTO version (versionID, vendorVersionID, name, appID, energyModelID, visibilityTypeID) VALUES (9, '1.2.5', null, 9, null, 10);
INSERT INTO member (memberID, appID, typeID, userID) VALUES (7, 9, 17, 1);

INSERT INTO vendor (vendorID, description, name, paymentPlanID) VALUES (8, null, 'Xllusion', 1);
INSERT INTO app (appID, description, logo, name, packageID, rating, ratingDate, genreID, paymentPlanID, vendorID) VALUES (10, 'A fun bubble live wallpaper where you can tap bubbles to burst. Now you can customize bubble image from your gallery, choose your lucky color theme and poke your friends to improve affection:)', 'figures/logos/bubble.png', 'Bubble Live Wallpaper', 'com.xllusion.livewallpaper.bubble', 4.4, '2013-07-02 14:41:23', 1, 1, 8);
INSERT INTO version (versionID, vendorVersionID, name, appID, energyModelID, visibilityTypeID) VALUES (10, '2.1.8', null, 10, null, 10);
INSERT INTO member (memberID, appID, typeID, userID) VALUES (8, 10, 17, 1);