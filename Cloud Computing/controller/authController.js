const { auth, db } = require('../config/firebase-config'); 
const jwt = require('jsonwebtoken');
const { v4: uuidv4 } = require('uuid');

// Regex untuk validasi email
const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

// Registrasi pengguna dengan Firebase Auth
exports.register = async (req, res) => {
  const { nama, email, password } = req.body;

  // Validasi input
  if (!nama || !email || !password) {
    return res.status(400).send('Semua kolom wajib diisi');
  }

  if (!emailRegex.test(email)) {
    return res.status(400).send('Email tidak valid');
  }

  if (password.length < 6) {
  return res.status(400).send('Password minimal 6 karakter');
}

  try {
    // Mendaftarkan pengguna di Firebase Authentication
    const userRecord = await auth.createUser({  
      email,
      password,
      displayName: nama, // menyimpan nama pengguna
    });

    // Menyimpan data pengguna tambahan di Firestore
    await db.collection('users').doc(userRecord.uid).set({
      uid: userRecord.uid,
      nama: userRecord.displayName,
      email: userRecord.email,
      fotoProfil: null, // Atau bisa diatur jika ada foto profil
    });

    res.status(201).send('Registrasi Berhasil');
  } catch (error) {
    console.error('Error saat registrasi:', error);
    res.status(500).send('Error registering user');
  }
};

// Login pengguna menggunakan Firebase Auth
exports.login = async (req, res) => {
  const { token } = req.body; // Terima token dari frontend (Firebase ID Token)

  if (!token) {
    return res.status(400).send('Token tidak tersedia');
  }

  try {
    console.log('Token diterima:', token);

    // Verifikasi Firebase ID Token
    const decodedToken = await auth.verifyIdToken(token);
    const userUid = decodedToken.uid;
    console.log('Token valid, UID:', userUid);

    // Cari pengguna berdasarkan UID
    const users = await db.collection('users').where('uid', '==', userUid).get();
    if (users.empty) {
      console.log('Pengguna tidak ditemukan di Firestore dengan UID:', userUid);
      return res.status(404).send('Pengguna tidak ditemukan');
    }

    const userData = users.docs[0].data();
    console.log('Data pengguna ditemukan:', userData);

    // Buat JWT Token untuk session di backend
    const secretKey = process.env.JWT_SECRET || 'your_jwt_secret';
    const jwtToken = jwt.sign({ email: userData.email, uid: userUid }, secretKey, { expiresIn: '1h' });

    res.json({ jwtToken });
  } catch (error) {
    console.error('Error saat memverifikasi token atau mencari pengguna:', error.message);
    res.status(401).send('Token tidak valid');
  }
};

// Login sebagai Guest
exports.guest = (req, res) => {
  try {
    // Data anonim untuk guest
    const guestData = {
      id: uuidv4(), // Generate ID unik menggunakan UUID
      role: "guest",
    };

    const secretKey = process.env.JWT_SECRET || 'your_jwt_secret';

    // Buat token JWT
    const token = jwt.sign(guestData, secretKey, { expiresIn: "1h" });

    // Kirim response dengan token
    res.status(200).json({
      message: "Login sebagai guest berhasil",
      token,
      user: guestData,
    });
  } catch (error) {
    console.error("Error saat login sebagai guest:", error);
    res.status(500).json({ message: "Terjadi kesalahan", error: error.message });
  }
};
