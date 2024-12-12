// middleware/uploadMiddleware.js
const multer = require('multer');
const path = require('path');

// Konfigurasi multer untuk mengunggah file
const storage = multer.memoryStorage();

const upload = multer({
  storage: storage,
  limits: { fileSize: 10 * 1024 * 1024 }, // Maksimal 10 MB
  fileFilter: (req, file, cb) => {
    const ext = path.extname(file.originalname);
    if (ext !== '.jpg' && ext !== '.png') {
      return cb(new Error('Hanya file JPG dan PNG yang diperbolehkan'));
    }
    cb(null, true);
  }
});

// Middleware untuk mengunggah foto profil
const uploadPicture = upload.single('images');

module.exports = uploadPicture;