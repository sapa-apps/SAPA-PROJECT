const express = require('express');
const router = express.Router(); 
const authController = require('../controller/authController');
const profileController = require('../controller/profileController');
const contentController = require('../controller/contentController')
const historyController = require('../controller/historyController')
const authMiddleware = require('../middleware/authMiddleware');
const uploadPicture = require('../middleware/uploadMiddleware');


// Routes for Registration
router.post('/register', authController.register);
// Routes for Login
router.post('/login', authController.login);
// Routes for Guest
router.post('/guest', authController.guest);

// Routes for Profile
router.get('/profile', authMiddleware, profileController.getProfile);
router.put('/profile/update', authMiddleware, uploadPicture, profileController.updateProfile);

// Routes for Content
router.post('/contents', uploadPicture, contentController.addContent);
router.get('/contents', contentController.getContents);
router.get('/contents/:id', contentController.getContentById);
router.put('/contents/:id', uploadPicture, contentController.updateContent);
router.delete('/contents/:id',  contentController.deleteContent);

// Routes for History
router.post('/history', authMiddleware, historyController.historyTranslation);


module.exports = router; 
